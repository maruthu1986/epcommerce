/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.builder.TaxOperationContextBuilder;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.service.tax.DiscountApportioningCalculator;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;

/**
 * Methods for getting a tax snapshot.
 */
public class TaxSnapshotServiceImpl implements TaxSnapshotService {

	private DiscountApportioningCalculator discountApportioningCalculator;
	private TaxCalculationService taxCalculationService;
	private TaxAddressAdapter taxAddressAdapter;
	private BeanFactory beanFactory;
	private ProductSkuLookup productSkuLookup;

	@Override
	public ShoppingCartTaxSnapshot getTaxSnapshotForCart(final ShoppingCart shoppingCart, final ShoppingCartPricingSnapshot cartPricingSnapshot) {
		if (shoppingCart == null) {
			throw new IllegalArgumentException("ShoppingCart parameter must not be null.");
		}

		if (cartPricingSnapshot == null) {
			throw new IllegalArgumentException("Cart pricing Snapshot parameter must not be null.");
		}

		if (shoppingCart instanceof ShoppingCartImpl) {
			updateTaxCalculationResult((ShoppingCartImpl) shoppingCart, cartPricingSnapshot);
			return (ShoppingCartTaxSnapshot) shoppingCart;
		}

		throw new IllegalArgumentException(
				"This implementation expects ShoppingCart to be a ShoppingCartImpl.  "
						+ "Unexpected ShoppingCart type: [" + shoppingCart.getClass() + "]");
	}

	@Override
	public ShoppingItemTaxSnapshot getTaxSnapshotForOrderSku(final OrderSku orderSku, final ShoppingCartPricingSnapshot cartPricingSnapshot) {
		return (ShoppingItemTaxSnapshot) orderSku;
	}

	@Override
	public ShoppingItemTaxSnapshot getTaxSnapshotForOrderSku(final OrderSku orderSku, final ShoppingItemPricingSnapshot itemPricingSnapshot) {
		return (ShoppingItemTaxSnapshot) orderSku;
	}

	/**
	 * Calculates the tax calculation result by triggering a call to the tax calculation service with the data of the shopping cart, including
	 * cart item and shipping cost.
	 *
	 * @param shoppingCart the shopping cart
	 * @param cartPricingSnapshot the cart pricing snapshot
	 */
	protected void updateTaxCalculationResult(final ShoppingCartImpl shoppingCart, final ShoppingCartPricingSnapshot cartPricingSnapshot) {
		TaxCalculationResult taxCalculationResult = getNewTaxCalculationResult(shoppingCart.getCustomerSession().getCurrency());

		final List<OrderSku> physicalCartItems = new ArrayList<>();
		final List<OrderSku> electronicCartItems = new ArrayList<>();

		// splits cart items depending on their shippable state into electronic and physical
		final Collection<ShoppingItem> apportionedLeafItems = shoppingCart.getApportionedLeafItems();
		final Collection<OrderSku> orderSkus = Collections2.transform(apportionedLeafItems, new Function<ShoppingItem, OrderSku>() {
			@Override
			public OrderSku apply(final ShoppingItem input) {
				return (OrderSku) input;
			}
		});
		splitCartItems(orderSkus, physicalCartItems, electronicCartItems);

		boolean isSplitShipment = !physicalCartItems.isEmpty() && !electronicCartItems.isEmpty();

		Map<String, BigDecimal> discountByShoppingItem = null;
		if (isSplitShipment) {
			final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap =
				Maps.toMap(orderSkus, new Function<OrderSku, ShoppingItemPricingSnapshot>() {
					@Override
					public ShoppingItemPricingSnapshot apply(final OrderSku orderSku) {
						return cartPricingSnapshot.getShoppingItemPricingSnapshot(orderSku);
					}
				});

			discountByShoppingItem = getDiscountApportioningCalculator()
				.apportionDiscountToShoppingItems(shoppingCart.getSubtotalDiscountMoney(), shoppingItemPricingSnapshotMap);
		}

		//Calculate for physical items
		taxCalculationResult = calculateTaxesForItems(shoppingCart, taxCalculationResult,
			isSplitShipment,
			discountByShoppingItem,
			physicalCartItems,
			getTaxAddressAdapter().toTaxAddress(shoppingCart.getShippingAddress()),
			shoppingCart.getShippingCost());

		//Calculate for electronic items
		taxCalculationResult = calculateTaxesForItems(shoppingCart, taxCalculationResult,
			isSplitShipment,
			discountByShoppingItem,
			electronicCartItems,
			getTaxAddressAdapter().toTaxAddress(shoppingCart.getElectronicTaxAddress()),
			Money.valueOf(BigDecimal.ZERO, shoppingCart.getCustomerSession().getCurrency()));

		shoppingCart.setTaxCalculationResult(taxCalculationResult);
	}

	/**
	 * Calculate the discount and taxes for the specified set of items, apply the results to the
	 * items and return the updated tax calculation result.
	 *
	 * @param shoppingCart the shopping cart
	 * @param taxCalculationResult the current tax calculation result which will be updated
	 * @param splitShipmentMode whether this is a split shipment
	 * @param discountByShoppingItem map of discounts per shopping item
	 * @param shoppingItems the shopping items
	 * @param taxAddress address of customer for tax calculation purposes
	 * @param shippingCost the shipping cost
	 * @return new TaxCalculationResult which included results for supplied items, and existing results from taxCalculationResult parameter
	 */
	protected TaxCalculationResult calculateTaxesForItems(final ShoppingCartImpl shoppingCart, final TaxCalculationResult taxCalculationResult,
															final boolean splitShipmentMode,
															final Map<String, BigDecimal> discountByShoppingItem, final List<OrderSku> shoppingItems,
															final TaxAddress taxAddress,
															final Money shippingCost) {
		if (shoppingItems.isEmpty()) {
			return taxCalculationResult;
		}

		Warehouse defaultWarehouse = shoppingCart.getStore().getWarehouse();
		TaxAddress originAddress = null;
		if (defaultWarehouse != null) {
			originAddress = getTaxAddressAdapter().toTaxAddress(defaultWarehouse.getAddress());
		}

		final Money discountForShipment = getDiscountForShipment(
			discountByShoppingItem,
			shoppingItems,
			splitShipmentMode,
			shoppingCart.getSubtotalDiscountMoney());

		final TaxOperationContext taxOperationContext = TaxOperationContextBuilder
			.newBuilder()
			.withCurrency(shoppingCart.getCustomerSession().getCurrency())
			.withTaxDocumentId(StringTaxDocumentId.fromString(shoppingCart.getGuid()))
			.withCustomerCode(shoppingCart.getGuid())
			.withTaxJournalType(TaxJournalType.PURCHASE)
			.withTaxExemption(shoppingCart.getTaxExemption())
			.withCustomerBusinessNumber(shoppingCart.getCustomerBusinessNumber())
			.build();

		final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap =
			Maps.toMap(shoppingItems, new Function<OrderSku, ShoppingItemPricingSnapshot>() {
				@Override
				public ShoppingItemPricingSnapshot apply(final OrderSku input) {
					return (ShoppingItemPricingSnapshot) input;
				}
			});

		final TaxCalculationResult newTaxCalculationResult = getTaxCalculationService().calculateTaxesAndAddToResult(
			taxCalculationResult,
			shoppingCart.getStore().getCode(),
			taxAddress,
			originAddress,
			shippingCost,
			shoppingItemPricingSnapshotMap,
			discountForShipment,
			taxOperationContext);
		newTaxCalculationResult.applyTaxes(shoppingItems);
		return newTaxCalculationResult;
	}

	/**
	 * Calculate the discount for shipment total.
	 *
	 * @param discountByShoppingItem map of discounts per shopping item
	 * @param shoppingItems the shopping items
	 * @param splitShipmentMode whether this is a split shipment
	 * @param cartSubtotalDiscount the cart subtotal discount
	 * @return the discount for shipment total
	 */
	protected Money getDiscountForShipment(final Map<String, BigDecimal> discountByShoppingItem, final List<OrderSku> shoppingItems,
											final boolean splitShipmentMode, final Money cartSubtotalDiscount) {
		if (!splitShipmentMode) {
			return cartSubtotalDiscount;
		}
		BigDecimal discount = BigDecimal.ZERO;
		for (OrderSku sku : shoppingItems) {
			if (discountByShoppingItem.containsKey(sku.getGuid())) {
				discount = discount.add(discountByShoppingItem.get(sku.getGuid()));
			}
		}
		return Money.valueOf(discount, cartSubtotalDiscount.getCurrency());
	}

	private TaxCalculationResult getNewTaxCalculationResult(final Currency currency) {
		final TaxCalculationResult taxCalculationResult = getBeanFactory().getBean(ContextIdNames.TAX_CALCULATION_RESULT);
		taxCalculationResult.initialize(currency);
		return taxCalculationResult;
	}

	private void splitCartItems(final Collection<OrderSku> leafItems,
								final List<OrderSku> physicalSkus,
								final List<OrderSku> electronicSkus) {
		for (final OrderSku orderSku : leafItems) {
			if (orderSku.isBundle(getProductSkuLookup())) {
				throw new IllegalArgumentException("Split cart items expects only leaf items.");
			} else {
				ProductSku sku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
				if (sku.isShippable()) {
					physicalSkus.add(orderSku);
				} else {
					electronicSkus.add(orderSku);
				}
			}
		}
	}

	public void setDiscountApportioningCalculator(final DiscountApportioningCalculator discountApportioningCalculator) {
		this.discountApportioningCalculator = discountApportioningCalculator;
	}

	protected DiscountApportioningCalculator getDiscountApportioningCalculator() {
		return discountApportioningCalculator;
	}

	public void setTaxCalculationService(final TaxCalculationService taxCalculationService) {
		this.taxCalculationService = taxCalculationService;
	}

	public TaxCalculationService getTaxCalculationService() {
		return taxCalculationService;
	}

	public void setTaxAddressAdapter(final TaxAddressAdapter taxAddressAdapter) {
		this.taxAddressAdapter = taxAddressAdapter;
	}

	protected TaxAddressAdapter getTaxAddressAdapter() {
		return taxAddressAdapter;
	}


	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

}
