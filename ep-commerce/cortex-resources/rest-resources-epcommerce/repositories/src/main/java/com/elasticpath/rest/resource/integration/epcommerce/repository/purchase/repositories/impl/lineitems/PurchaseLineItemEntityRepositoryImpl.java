/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.lineitems;

import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Lists;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.money.Money;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.Builder;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemConfigurationEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Purchase Line Item Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class PurchaseLineItemEntityRepositoryImpl<E extends PurchaseLineItemEntity, I extends PurchaseLineItemIdentifier>
		implements Repository<PurchaseLineItemEntity, PurchaseLineItemIdentifier> {

	private OrderRepository orderRepository;
	private MoneyTransformer moneyTransformer;
	private ResourceOperationContext resourceOperationContext;
	private ProductSkuRepository productSkuRepository;
	private PricingSnapshotRepository pricingSnapshotRepository;
	private CartItemModifiersRepository cartItemModifiersRepository;
	private ReactiveAdapter reactiveAdapter;

	@Override
	public Single<PurchaseLineItemEntity> findOne(final PurchaseLineItemIdentifier identifier) {
		PurchaseIdentifier purchaseIdentifier = identifier.getPurchaseLineItems().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		List<String> guidPathFromRootItem = identifier.getLineItemId().getValue();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		return orderRepository.findOrderSku(scope, purchaseId, guidPathFromRootItem)
				.flatMap(orderSku -> buildLineItemEntity(orderSku, locale));
	}

	/**
	 * Converter from order sku to the purchase line item.
	 *
	 * @param orderSku order sku
	 * @param locale   locale
	 * @return purchase line item entity
	 */
	protected Single<PurchaseLineItemEntity> buildLineItemEntity(final OrderSku orderSku, final Locale locale) {
		return Single.just(PurchaseLineItemEntity.builder().withName(orderSku.getDisplayName()))
				.flatMap(lineItemDto -> createConfiguration(orderSku)
						.map(lineItemDto::withConfiguration)
						.flatMap(builder -> getBuilderWithConfiguredLineExtensions(orderSku, builder, locale)))
				.map(Builder::build);
	}

	/**
	 * Configure quantity, amount, tax, total for the purchase.
	 *
	 * @param orderSku order sku
	 * @param builder  builder which to configure
	 * @param locale   locale
	 * @return updated builder
	 */
	protected Single<PurchaseLineItemEntity.Builder> getBuilderWithConfiguredLineExtensions(
			final OrderSku orderSku, final PurchaseLineItemEntity.Builder builder, final Locale locale) {

		if (isBundleComponent(orderSku)) {
			int parentQuantity = orderSku.getParent().getQuantity();
			builder.withQuantity(orderSku.getQuantity() / parentQuantity);
			return Single.just(builder);
		}

		return createAmount(orderSku, locale)
				.flatMap(amount -> createTax(orderSku, locale)
						.flatMap(tax -> createTotal(orderSku, locale)
								.map(total -> builder.withQuantity(orderSku.getQuantity())
										.withLineExtensionAmount(amount)
										.withLineExtensionTax(tax)
										.withLineExtensionTotal(total)
								)));
	}

	private Single<PurchaseLineItemConfigurationEntity> createConfiguration(final OrderSku orderSku) {
		Map<String, String> fields = orderSku.getFields();
		if (fields != null) {
			return retrieveCartItemModifierFields(orderSku.getSkuGuid())
					.map(cartItemModifierFields -> {
						PurchaseLineItemConfigurationEntity.Builder builder = PurchaseLineItemConfigurationEntity.builder();

						cartItemModifierFields.forEach(field -> builder.addingProperty(field.getCode(), fields.get(field.getCode())));

						return builder.build();
					});
		}
		return Single.just(PurchaseLineItemConfigurationEntity.builder().build());
	}

	private boolean isBundleComponent(final OrderSku orderSku) {
		return orderSku.getParent() != null;
	}

	private Single<Collection<CostEntity>> createAmount(final OrderSku orderSku, final Locale locale) {
		return pricingSnapshotRepository.getPricingSnapshotForOrderSku(orderSku)
				.map(shoppingItemPricingSnapshot -> shoppingItemPricingSnapshot.getPriceCalc().withCartDiscounts().getMoney())
				.map(amount -> Collections.singleton(moneyTransformer.transformToEntity(amount, locale)));
	}

	private Single<Collection<CostEntity>> createTax(final OrderSku orderSku, final Locale locale) {
		return getTax(orderSku, orderSku.getCurrency())
				.map(tax -> Collections.singleton(moneyTransformer.transformToEntity(tax, locale)));
	}

	private Single<Collection<CostEntity>> createTotal(final OrderSku orderSku, final Locale locale) {
		return pricingSnapshotRepository.getPricingSnapshotForOrderSku(orderSku)
				.map(pricingSnapshot -> pricingSnapshot.getPriceCalc().withCartDiscounts().getMoney())
				.flatMap(subTotal -> {
					Single<Money> total;
					//We only ship to one address, so we will not have a case where one shipment is tax inclusive and another is exclusive.
					//TODO: CE should be making the correct calculations for inclusive tax so that we don't have to.
					if (findShipment(orderSku).isInclusiveTax()) {
						total = Single.just(subTotal);
					} else {
						total = getTax(orderSku, orderSku.getCurrency()).map(subTotal::add);
					}

					return total.map(money -> Collections.singleton(moneyTransformer.transformToEntity(money, locale)));
				});
	}

	private OrderShipment findShipment(final OrderSku orderSku) {
		OrderShipment shipment = orderSku.getShipment();
		if (shipment == null) {
			for (ShoppingItem shoppingItem : orderSku.getChildren()) {
				OrderSku childSku = (OrderSku) shoppingItem;
				shipment = findShipment(childSku);
				if (shipment != null) {
					break;
				}
			}
		}
		return shipment;
	}

	/**
	 * Recursively Gets the tax for the shopping item. <br>
	 * Note: bundles currently do not have tax, and must be calculated by summing up the tax of their constituents.
	 *
	 * @param item         the shopping item.
	 * @param rootCurrency the currency of the root item.
	 * @return the calculated tax.
	 */
	protected Single<Money> getTax(final OrderSku item, final Currency rootCurrency) {
		return reactiveAdapter.fromRepositoryAsSingle(() -> productSkuRepository.isProductBundle(item.getSkuGuid()))
				.flatMap(isProductBundle -> {
					Single<Money> tax = pricingSnapshotRepository.getTaxSnapshotForOrderSku(item)
							.map(taxSnapshot -> Money.valueOf(taxSnapshot.getTaxAmount(), rootCurrency));

					if (isProductBundle) {
						final List<OrderSku> bundleItems = toOrderSkus(item.getChildren());

						for (final OrderSku bundleItem : bundleItems) {
							//This is a recursive call that will calculate total tax of the bundle tree
							tax = tax.flatMap(money -> getTax(bundleItem, rootCurrency).map(money::add));
						}
					}

					return tax;
				});
	}

	/**
	 * Gets the list of cart item modifier fields for the given skuGuid.
	 *
	 * @param skuGuid the sku guid
	 * @return the list of fields, can be empty
	 */
	private Single<List<CartItemModifierField>> retrieveCartItemModifierFields(final String skuGuid) {
		return productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(skuGuid)
				.map(ProductSku::getProduct)
				.map(product -> cartItemModifiersRepository.findCartItemModifiersByProduct(product));
	}

	/**
	 * Converts ShoppingItem instances to OrderSku instances, by casting.
	 *
	 * @param shoppingItems the shopping items
	 * @return a collection of order skus
	 */
	private List<OrderSku> toOrderSkus(final List<ShoppingItem> shoppingItems) {
		return Lists.transform(shoppingItems, input -> (OrderSku) input);
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}

	@Reference
	public void setMoneyTransformer(final MoneyTransformer moneyTransformer) {
		this.moneyTransformer = moneyTransformer;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setProductSkuRepository(final ProductSkuRepository productSkuRepository) {
		this.productSkuRepository = productSkuRepository;
	}

	@Reference
	public void setPricingSnapshotRepository(final PricingSnapshotRepository pricingSnapshotRepository) {
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Reference
	public void setCartItemModifiersRepository(final CartItemModifiersRepository cartItemModifiersRepository) {
		this.cartItemModifiersRepository = cartItemModifiersRepository;
	}

}
