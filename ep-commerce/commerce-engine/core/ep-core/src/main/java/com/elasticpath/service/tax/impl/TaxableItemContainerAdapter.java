/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.plugin.tax.builder.TaxAddressBuilder;
import com.elasticpath.plugin.tax.common.TaxContextIdNames;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.TaxableItemImpl;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCodeRetriever;

/**
 * Adapter for a {@link com.elasticpath.plugin.tax.domain.TaxableItemContainer}.
 */
public class TaxableItemContainerAdapter {

	private static final String SHIPPING_COST = "Shipping Cost";

	private BeanFactory beanFactory;
	private ProductSkuLookup productSkuLookup;
	private TaxCodeRetriever taxCodeRetriever;

	/**
	 * Adapts contextual data values to a {@link com.elasticpath.plugin.tax.domain.TaxableItemContainer}.
	 *
	 * @param shoppingItemPricingSnapshotMap a map of shopping items to their corresponding pricing snapshots
	 * @param shippingCost shipping cost
	 * @param discounts discounts
	 * @param activeTaxCodes active tax codes
	 * @param storeCode the store code
	 * @param destinationAddress the destination address
	 * @param originAddress the origin address
	 * @param taxOperationContext the tax operation context
	 * @return an instance of {@link com.elasticpath.plugin.tax.domain.TaxableItemContainer}
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	public TaxableItemContainer adapt(final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap,
									  final BigDecimal shippingCost,
									  final Map<String, BigDecimal> discounts,
									  final Set<String> activeTaxCodes,
									  final String storeCode,
									  final TaxAddress destinationAddress,
									  final TaxAddress originAddress,
									  final TaxOperationContext taxOperationContext) {
		StoreService storeService = beanFactory.getBean(ContextIdNames.STORE_SERVICE);
		Locale locale = storeService.findStoreWithCode(storeCode).getDefaultLocale();

		Currency currency = taxOperationContext.getCurrency();

		MutableTaxableItemContainer container = getBeanFactory().getBean(TaxContextIdNames.MUTABLE_TAXABLE_ITEM_CONTAINER);
		container.setDestinationAddress(destinationAddress);
		container.setOriginAddress(originAddress);
		container.setCurrency(currency);
		container.setItems(buildTaxableItems(shoppingItemPricingSnapshotMap, shippingCost, discounts, activeTaxCodes, currency, locale));
		container.setStoreCode(storeCode);
		container.setTaxOperationContext(taxOperationContext);

		return container;
	}

	/**
	 * Adapts contextual data values to a list of {@link com.elasticpath.plugin.tax.domain.TaxableItem}.
	 *
	 * @param shoppingItemPricingSnapshotMap a map of shopping items to their corresponding pricing snapshots
	 * @param shippingCost   the shipping cost
	 * @param discounts      the given map of the apportioned discount amount of shopping item
	 * @param activeTaxCodes the active tax codes
	 * @param currency       the currency
	 * @param locale         the locale
	 * @return a list of taxable items
	 */
	protected List<TaxableItem> buildTaxableItems(final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> shoppingItemPricingSnapshotMap,
													final BigDecimal shippingCost,
													final Map<String, BigDecimal> discounts,
													final Set<String> activeTaxCodes,
													final Currency currency,
													final Locale locale) {
		List<TaxableItem> result = new ArrayList<>();

		for (Map.Entry<? extends ShoppingItem, ShoppingItemPricingSnapshot> entry : shoppingItemPricingSnapshotMap.entrySet()) {
			final ShoppingItem shoppingItem = entry.getKey();
			final ShoppingItemPricingSnapshot itemPricingSnapshot = entry.getValue();

			ProductSku itemSku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());

			String taxCode = getShoppingItemTaxCode(shoppingItem, itemSku);
			String skuCode = getShoppingItemSkuCode(shoppingItem, itemSku);
			String itemGuid = shoppingItem.getGuid();
			int quantity = shoppingItem.getQuantity();

			BigDecimal price = itemPricingSnapshot.getPriceCalc().withCartDiscounts().getAmount();

			TaxableItem item = buildTaxableItem(itemGuid,
												activeTaxCodes,
												taxCode,
												currency,
												price,
												discounts.get(itemGuid),
												skuCode,
												quantity,
												itemSku.getProduct().getDisplayName(locale));
			result.add(item);
		}

		if (shippingCost != null && isAnyItemPhysical(shoppingItemPricingSnapshotMap.keySet())) {
			TaxableItem item = buildTaxableItem(TaxCode.TAX_CODE_SHIPPING, activeTaxCodes, TaxCode.TAX_CODE_SHIPPING,
			currency, shippingCost, null, TaxCode.TAX_CODE_SHIPPING, 1, SHIPPING_COST);
			result.add(item);
		}
		return result;
	}

	private boolean isAnyItemPhysical(final Collection<? extends ShoppingItem> shoppingItems) {
		for (ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.isShippable(getProductSkuLookup())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Builds a new taxable item.
	 *
	 * @param itemGuid the item Guid
	 * @param activeTaxCodes all active tax codes in the system
	 * @param taxCode the tax code of the item
	 * @param currency the currency
	 * @param price the price of the taxable item
	 * @param referenceId the SKU code or general reference ID of the related item
	 * @param discount the apportioned discount amount of shopping item
	 * @param quantity the quantity
	 * @param itemDescription item description
	 *
	 * @return an instance of {@link com.elasticpath.plugin.tax.domain.TaxableItem}
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	protected TaxableItem buildTaxableItem(final String itemGuid, final Set<String> activeTaxCodes, final String taxCode,
				final Currency currency, final BigDecimal price, final BigDecimal discount,
				final String referenceId, final int quantity, final String itemDescription) {
		TaxableItemImpl item = getBeanFactory().getBean(TaxContextIdNames.TAXABLE_ITEM);

		item.setCurrency(currency);
		item.setItemGuid(itemGuid);
		item.setPrice(price);
		item.setItemCode(referenceId);
		item.setQuantity(quantity);
		item.setItemDescription(itemDescription);
		item.setTaxCode(taxCode);
		item.setTaxCodeActive(activeTaxCodes.contains(taxCode));

		if (discount != null) {
			item.applyDiscount(discount);
		}
		return item;
	}

	private String getShoppingItemTaxCode(final ShoppingItem shoppingItem, final ProductSku itemSku) {

		if (shoppingItem instanceof OrderSku) {
			return ((OrderSku) shoppingItem).getTaxCode();
		}
		if (itemSku == null) {
			return null;
		}
		return getTaxCodeRetriever().getEffectiveTaxCode(itemSku).getCode();
	}

	private String getShoppingItemSkuCode(final ShoppingItem shoppingItem, final ProductSku itemSku) {

		if (shoppingItem instanceof OrderSku) {
			return ((OrderSku) shoppingItem).getSkuCode();
		}
		if (itemSku == null) {
			return null;
		}
		return itemSku.getSkuCode();
	}

	/**
	 * Adapts to a {@link com.elasticpath.plugin.tax.domain.TaxAddress}.
	 *
	 * @param address the address to adapt
	 * @return the tax address instance
	 */
	public TaxAddress toTaxAddress(final Address address) {
		if (address == null) {
			return null;
		}

		return TaxAddressBuilder.newBuilder()
								.withStreet1(address.getStreet1())
								.withStreet2(address.getStreet2())
								.withCity(address.getCity())
								.withSubCountry(address.getSubCountry())
								.withCountry(address.getCountry())
								.withZipOrPostalCode(address.getZipOrPostalCode())
								.build();
	}

	/**
	 * Adapts ta {@link com.elasticpath.plugin.tax.domain.TaxAddress} to a domain {@link com.elasticpath.domain.customer.Address}.
	 *
	 * @param address the tax address
	 * @return the populated address instance
	 */
	public Address toDomainAddress(final TaxAddress address) {
		if (address == null) {
			return null;
		}
		Address domainAddress = getBeanFactory().getBean(ContextIdNames.ORDER_ADDRESS);

		domainAddress.setCity(address.getCity());
		domainAddress.setCountry(address.getCountry());
		domainAddress.setStreet1(address.getStreet1());
		domainAddress.setStreet2(address.getStreet2());
		domainAddress.setSubCountry(address.getSubCountry());
		domainAddress.setZipOrPostalCode(address.getZipOrPostalCode());

		return domainAddress;
	}


	public BeanFactory getBeanFactory() {
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

	protected TaxCodeRetriever getTaxCodeRetriever() {
		return taxCodeRetriever;
	}

	public void setTaxCodeRetriever(final TaxCodeRetriever taxCodeRetriever) {
		this.taxCodeRetriever = taxCodeRetriever;
	}

}
