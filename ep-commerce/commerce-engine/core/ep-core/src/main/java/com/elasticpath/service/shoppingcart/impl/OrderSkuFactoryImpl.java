/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 *
 */
package com.elasticpath.service.shoppingcart.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shoppingcart.BundleApportioningCalculator;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.service.tax.ApportioningCalculator;
import com.elasticpath.service.tax.TaxCodeRetriever;

/**
 * Creates new {@link OrderSku} objects from ordinary {@link ShoppingItem}s, doing nothing with any associated data.
 */
@SuppressWarnings("PMD.GodClass")
public class OrderSkuFactoryImpl implements OrderSkuFactory {
	private BeanFactory beanFactory;
	private ItemPricingSplitter pricingSplitter;
	private BundleApportioningCalculator bundleApportioner;
	private ApportioningCalculator discountApportioner;
	private ProductSkuLookup productSkuLookup;
	private TaxCodeRetriever taxCodeRetriever;
	private TimeService timeService;

	@Override
	public Collection<OrderSku> createOrderSkus(final Collection<ShoppingItem> rootItems,
												final ShoppingCartTaxSnapshot taxSnapshot,
												final Locale locale) {
		Map<String, ItemPricing> rootPricingMap = extractRootPricing(rootItems, taxSnapshot);

		Map<String, Map<String, ItemPricing>> bundleApportionedPriceMap = getBundleApportionedPriceMap(rootItems, rootPricingMap, taxSnapshot);

		Map<String, Map<String, List<ItemPricing>>> bundleQuantityApportionedPriceMap = splitByQuantity(bundleApportionedPriceMap);

		applyApportionedDiscount(bundleQuantityApportionedPriceMap, extractDiscount(rootPricingMap));

		return createOrderSkusWithApportionedPrices(rootItems,
													extractAllLeavesItemPricings(bundleQuantityApportionedPriceMap),
			taxSnapshot,
													locale);
	}

	@Override
	public OrderSku createOrderSku(final ProductSku sku, final Price price, final int quantity, final int ordering,
									final Map<String, String> itemFields) {
		final OrderSku orderSku = createSimpleOrderSku();

		if (sku != null) {
			orderSku.setSkuCode(sku.getSkuCode());
			orderSku.setSkuGuid(sku.getGuid());
			orderSku.setTaxCode(getTaxCode(sku));
		}

		orderSku.setPrice(quantity, price);

		if (price != null) {
			orderSku.setUnitPrice(price.getLowestPrice(quantity).getAmount());
		}

		orderSku.setOrdering(ordering);
		orderSku.mergeFieldValues(itemFields);

		return orderSku;
	}

	private Map<String, Map<String, ItemPricing>> getBundleApportionedPriceMap(final Collection<ShoppingItem> rootItems,
																				final Map<String, ItemPricing> rootPricingMap,
																				final ShoppingCartTaxSnapshot taxSnapshot) {
		// To make apportioning consistent for the cases of 1 bundle and multiple bundles,
		// we apportion price for a single bundle and later multiply the result
		// by the root bundle quantity.
		// see call to multiplyConstituentPricesByRootQuantities below.
		divideRootPricesByRootQuantities(rootPricingMap);

		Map<String, Map<String, ItemPricing>> constituentPricingMap = extractConstituentPricingInOrder(rootItems, taxSnapshot);

		Map<String, Map<String, ItemPricing>> pricingMap = getApportionedPriceMap(rootPricingMap, constituentPricingMap);

		// see above the comment to divideRootPricesByRootQuantities
		multiplyConstituentPricesByRootQuantities(pricingMap, rootPricingMap);
		return pricingMap;
	}

	private Map<String, List<ItemPricing>> extractAllLeavesItemPricings(final Map<String, Map<String, List<ItemPricing>>> splitPricingMap) {
		Map<String, List<ItemPricing>> result = new HashMap<>();
		for (Map<String, List<ItemPricing>> leavesItemPricings : splitPricingMap.values()) {
			result.putAll(leavesItemPricings);
		}

		return result;
	}

	/**
	 * Applies discount on the price map.
	 *
	 * @param pricingMap the price map that needs to be applied discount to
	 * @param discountMap the discount map.
	 */
	protected void applyApportionedDiscount(final Map<String, Map<String, List<ItemPricing>>> pricingMap, final Map<String, BigDecimal> discountMap) {
		for (final Entry<String, Map<String, List<ItemPricing>>> rootPricingEntry : pricingMap.entrySet()) {
			Map<String, List<ItemPricing>> splitChildPricing = rootPricingEntry.getValue();

			Map<String, BigDecimal> allSplitPricing = getAllSplitPricing(splitChildPricing);
			allSplitPricing = sortByAmount(allSplitPricing);
			BigDecimal rootDiscount = discountMap.get(rootPricingEntry.getKey());

			Map<String, BigDecimal> allSplitDiscount = getDiscountApportioner().calculateApportionedAmounts(rootDiscount, allSplitPricing);
			setApportionedDiscount(splitChildPricing, allSplitDiscount);
		}
	}

	/**
	 * Sorts the price map by amount.
	 *
	 * @param allSplitPricing the split pricing
	 * @return a map of sorted price map.
	 */
	protected Map<String, BigDecimal> sortByAmount(final Map<String, BigDecimal> allSplitPricing) {
		List<Entry<String, BigDecimal>> sortedPairs = new ArrayList<>();
		for (Entry<String, BigDecimal> entry : allSplitPricing.entrySet()) {
			sortedPairs.add(entry);
		}
		Collections.sort(sortedPairs, new Comparator<Entry<String, BigDecimal>>() {
			@Override
			public int compare(final Entry<String, BigDecimal> entry1, final Entry<String, BigDecimal> entry2) {
				return entry2.getValue().compareTo(entry1.getValue());
			}
		});

		Map<String, BigDecimal> sortedMap = new LinkedHashMap<>();
		for (Entry<String, BigDecimal> entry : sortedPairs) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	/**
	 * Sets the apportioned discount on the item pricing.
	 *
	 * @param itemPricings item pricing map.
	 * @param apportionedDiscountMap apportioned discount map.
	 */
	protected void setApportionedDiscount(final Map<String, List<ItemPricing>> itemPricings, final Map<String, BigDecimal> apportionedDiscountMap) {
		for (Entry<String, List<ItemPricing>> pricingEntry : itemPricings.entrySet()) {
			String pricingKey = pricingEntry.getKey();
			List<ItemPricing> leavesPricing = pricingEntry.getValue();
			for (Entry<String, BigDecimal> discountEntry : apportionedDiscountMap.entrySet()) {
				String discountKey = discountEntry.getKey();
				if (discountKey.startsWith(pricingKey)) {
					int itemPricingIndex = Integer.parseInt(discountKey.substring(pricingKey.length(), discountKey.length()));
					ItemPricing itemPricing = leavesPricing.get(itemPricingIndex);
					pricingEntry.getValue().set(itemPricingIndex,
							new ItemPricing(itemPricing.getPrice(), discountEntry.getValue(), itemPricing.getQuantity()));
				}
			}
		}
	}

	private Map<String, BigDecimal> getAllSplitPricing(final Map<String, List<ItemPricing>> splitChildPricing) {
		Map<String, BigDecimal> pricings = new HashMap<>();
		for (Entry<String, List<ItemPricing>> entry : splitChildPricing.entrySet()) {
			int childIndex = 0;
			for (ItemPricing pricing : entry.getValue()) {
				pricings.put(entry.getKey() + childIndex, pricing.getPrice().multiply(BigDecimal.valueOf(pricing.getQuantity())));
				childIndex++;
			}
		}

		return pricings;
	}

	private void multiplyConstituentPricesByRootQuantities(final Map<String, Map<String, ItemPricing>> pricingMap,
			final Map<String, ItemPricing> rootPricingMap) {
		for (final Entry<String, Map<String, ItemPricing>> rootPricingEntry : pricingMap.entrySet()) {
			BigDecimal quantity = BigDecimal.valueOf(rootPricingMap.get(rootPricingEntry.getKey()).getQuantity());

			Map<String, ItemPricing> childPricingMap = rootPricingEntry.getValue();
			for (Entry<String, ItemPricing> entry : childPricingMap.entrySet()) {
				BigDecimal singleBundlePrice = entry.getValue().getPrice();
				BigDecimal price = singleBundlePrice.multiply(quantity);
				entry.setValue(new ItemPricing(price, entry.getValue().getDiscount(), entry.getValue().getQuantity()));
			}
		}

	}

	private void divideRootPricesByRootQuantities(final Map<String, ItemPricing> rootPricingMap) {
		for (Entry<String, ItemPricing> pricing : rootPricingMap.entrySet()) {
			BigDecimal discount = pricing.getValue().getDiscount();
			final int quantity = pricing.getValue().getQuantity();
			final int calcScale = 10;
			BigDecimal decimalQuantity = BigDecimal.valueOf(quantity);
			BigDecimal multipleBundlePrice = pricing.getValue().getPrice();
			BigDecimal price = multipleBundlePrice.divide(decimalQuantity, calcScale, RoundingMode.HALF_UP);
			pricing.setValue(new ItemPricing(price, discount, quantity));
		}
	}

	/**
	 * @param cartItem the cart item from which the data should be copied
	 * @param orderSku the order sku into which the data should be copied
	 */
	protected void copyData(final ShoppingItem cartItem, final OrderSku orderSku) {
		Map<String, String> itemData = cartItem.getFields();
		for (final Entry<String, String> entry : itemData.entrySet()) {
			orderSku.setFieldValue(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @param shoppingItem {@code ShoppingItem}
	 * @param orderSku {@code OrderSku}
	 * @param locale {@code Locale}
	 */
	protected void copyFields(final ShoppingItem shoppingItem, final OrderSku orderSku, final Locale locale) {
		final ProductSku productSku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
		final Product product = productSku.getProduct();

		orderSku.setCreatedDate(getTimeService().getCurrentTime());
		orderSku.setSkuGuid(productSku.getGuid());
		orderSku.setSkuCode(productSku.getSkuCode());
		orderSku.setDigitalAsset(productSku.getDigitalAsset());
		orderSku.setTaxCode(getTaxCode(productSku));
		orderSku.setDisplaySkuOptions(getSkuOptionsDisplayString(productSku, locale));
		orderSku.setDisplayName(product.getDisplayName(locale));
		orderSku.setBundleConstituent(shoppingItem.isBundleConstituent());
		if (productSku.getImage() != null) {
			orderSku.setImage(productSku.getImage());
		}

		orderSku.setOrdering(shoppingItem.getOrdering());
	}

	/**
	 * @param orderSku {@link OrderSku}.
	 * @param pricing {@link ItemPricing}.
	 */
	protected void setItemPricing(final OrderSku orderSku, final ItemPricing pricing) {
		orderSku.setQuantity(pricing.getQuantity());
		orderSku.setUnitPrice(pricing.getPrice());
		orderSku.setDiscountBigDecimal(pricing.getDiscount());
	}

	/**
	 * @param item {@link ShoppingItem}.
	 * @param orderSku {@link OrderSku}.
	 * @param taxSnapshot the pricing snapshot corresponding to the shopping item
	 */
	protected void copyPrices(final ShoppingItem item, final OrderSku orderSku, final ShoppingItemTaxSnapshot taxSnapshot) {
		// need null checks below, as nested bundles need not be priced to purchase the root bundle
		orderSku.setPrice(item.getQuantity(), taxSnapshot.getPricingSnapshot().getPrice());
		final Money unitPrice = taxSnapshot.getPricingSnapshot().getPriceCalc().forUnitPrice().getMoney();
		if (unitPrice != null) {
			orderSku.setUnitPrice(unitPrice.getRawAmount());
		}

		orderSku.setTaxAmount(taxSnapshot.getTaxAmount());

		if (taxSnapshot.getPricingSnapshot().getDiscount() != null) {
			orderSku.setDiscountBigDecimal(taxSnapshot.getPricingSnapshot().getDiscount().getRawAmount());
		}
	}

	private void createDependants(final ShoppingItem parentItem,
									final OrderSku parentSku,
									final Map<String, List<ItemPricing>> leavesItemPricings,
									final ShoppingCartTaxSnapshot taxSnapshot,
									final Locale locale) {
		Collection<OrderSku> childrenSku = createOrderSkusWithApportionedPrices(parentItem.getBundleItems(getProductSkuLookup()),
																				leavesItemPricings,
																				taxSnapshot,
																				locale);

		for (OrderSku childSku : childrenSku) {
			parentSku.addChildItem(childSku);
		}
	}

	private OrderSku createOrderSkuWithApportionedPrices(final ShoppingItem item, final ItemPricing apportionedPricing,
															final ShoppingItemTaxSnapshot shoppingItemTaxSnapshot, final Locale locale) {
		final OrderSku orderSku = createSimpleOrderSku();
		copyFields(item, orderSku, locale);
		copyData(item, orderSku);
		copyPrices(item, orderSku, shoppingItemTaxSnapshot);

		if (apportionedPricing != null) {
			setItemPricing(orderSku, apportionedPricing);
		}

		return orderSku;
	}

	/**
	 * Creates order skus and set the unit price/discount/tax according to the {@link ItemPricing} in the price map. If the {@link ItemPricing}
	 * doesn't exist in the price map, use the {@link ItemPricing} from the {@link ShoppingItem}.
	 *
	 * @param shoppingItems a collection of {@link ShoppingItem}.
	 * @param leavesPricingsMap a price map.
	 * @param cartTaxSnapshot the tax aware pricing snapshot for the cart containing the shopping items
	 * @param locale {@code Locale} for determining correct language for product sku description.
	 * @return a collection of {@link OrderSku}.
	 */
	protected Collection<OrderSku> createOrderSkusWithApportionedPrices(final Collection<ShoppingItem> shoppingItems,
																		final Map<String, List<ItemPricing>> leavesPricingsMap,
																		final ShoppingCartTaxSnapshot cartTaxSnapshot,
																		final Locale locale) {
		List<OrderSku> orderSkus = new ArrayList<>();
		for (ShoppingItem shoppingItem : shoppingItems) {
			OrderSku parentOrderSku = null;

			List<ItemPricing> leavesPricings = leavesPricingsMap.get(shoppingItem.getGuid());
			final ShoppingItemTaxSnapshot shoppingItemTaxSnapshot = cartTaxSnapshot.getShoppingItemTaxSnapshot(shoppingItem);
			if (leavesPricings == null) {
				parentOrderSku = createOrderSkuWithApportionedPrices(shoppingItem, null, shoppingItemTaxSnapshot, locale);
				orderSkus.add(parentOrderSku);
			} else {
				for (ItemPricing leafPricing : leavesPricings) {
					orderSkus.add(createOrderSkuWithApportionedPrices(shoppingItem, leafPricing, shoppingItemTaxSnapshot, locale));
				}
			}

			if (parentOrderSku != null) {
				createDependants(shoppingItem, parentOrderSku, leavesPricingsMap, cartTaxSnapshot, locale);
			}
		}

		return orderSkus;
	}

	/**
	 * Creates an {@link OrderSku}.
	 *
	 * @return an {@link OrderSku}.
	 */
	protected OrderSku createSimpleOrderSku() {
		return beanFactory.getBean(ContextIdNames.ORDER_SKU);
	}

	private List<ShoppingItem> createSortedConstituentShoppingItems(final ShoppingItem root, final ShoppingCartTaxSnapshot cartTaxSnapshot) {
		List<ShoppingItem> sortedItems = new ArrayList<>();
		if (root.isBundle(getProductSkuLookup())) {
			populateConstituents(sortedItems, root.getBundleItems(getProductSkuLookup()));
		}
		Collections.sort(sortedItems, new Comparator<ShoppingItem>() {
			@Override
			public int compare(final ShoppingItem item1, final ShoppingItem item2) {
				final ShoppingItemTaxSnapshot itemTaxSnapshot1 = cartTaxSnapshot.getShoppingItemTaxSnapshot(item1);
				final ShoppingItemTaxSnapshot itemTaxSnapshot2 = cartTaxSnapshot.getShoppingItemTaxSnapshot(item2);

				final BigDecimal item1Price = itemTaxSnapshot1.getPricingSnapshot().getLinePricing().getPrice();
				final BigDecimal item2Price = itemTaxSnapshot2.getPricingSnapshot().getLinePricing().getPrice();

				int result = item1Price.compareTo(item2Price);
				if (result == 0) {
					result = item1.getSkuGuid().compareTo(item2.getSkuGuid());
				}

				return -result;
			}
		});
		return sortedItems;
	}

	/**
	 * Extracts the pricing of the constituent/leaf {@link ShoppingItem} from a collection of root {@link ShoppingItem}s. The pricing is sorted by
	 * price and sku guid.
	 *
	 * @param rootItems a collection of root {@link ShoppingItem}.
	 * @param taxSnapshot the tax-aware pricing snapshot for the cart containing the shopping items
	 * @return a map of pricing with root {@link ShoppingItem} guid as key and pricing as value.
	 */
	protected Map<String, Map<String, ItemPricing>> extractConstituentPricingInOrder(final Collection<ShoppingItem> rootItems,
																						final ShoppingCartTaxSnapshot taxSnapshot) {
		Map<String, Map<String, ItemPricing>> pricingMap = new HashMap<>();
		for (final ShoppingItem root : rootItems) {
			List<ShoppingItem> sortedItems = createSortedConstituentShoppingItems(root, taxSnapshot);

			Map<String, ItemPricing> childPricing = new LinkedHashMap<>();
			pricingMap.put(root.getGuid(), childPricing);
			populateItemPricing(childPricing, sortedItems, taxSnapshot);
		}

		return pricingMap;
	}

	/**
	 * Extracts the pricing of a collection of {@link ShoppingItem}.
	 *
	 * @param shoppingItems a collection of {@link ShoppingItem}.
	 * @param cartTaxSnapshot the tax-aware pricing snapshot for the cart containing the shopping items
	 * @return the price map.
	 */
	protected Map<String, ItemPricing> extractRootPricing(final Collection<ShoppingItem> shoppingItems,
															final ShoppingCartTaxSnapshot cartTaxSnapshot) {
		Map<String, ItemPricing> pricing = new HashMap<>();
		for (ShoppingItem item : shoppingItems) {
			final ShoppingItemTaxSnapshot shoppingItemTaxSnapshot = cartTaxSnapshot.getShoppingItemTaxSnapshot(item);
			pricing.put(item.getGuid(), shoppingItemTaxSnapshot.getPricingSnapshot().getLinePricing());
		}
		return pricing;
	}

	protected BundleApportioningCalculator getBundleApportioner() {
		return bundleApportioner;
	}

	public void setBundleApportioner(final BundleApportioningCalculator bundleApportioner) {
		this.bundleApportioner = bundleApportioner;
	}

	protected ApportioningCalculator getDiscountApportioner() {
		return discountApportioner;
	}

	public void setDiscountApportioner(final ApportioningCalculator discountApportioner) {
		this.discountApportioner = discountApportioner;
	}

	private Map<String, Map<String, ItemPricing>> getApportionedPriceMap(final Map<String, ItemPricing> rootPricingMap,
			final Map<String, Map<String, ItemPricing>> constituentPricingMap) {
		Map<String, Map<String, ItemPricing>> result = new HashMap<>();
		for (final Entry<String, ItemPricing> rootPricingEntry : rootPricingMap.entrySet()) {
			ItemPricing pricingToApportion = rootPricingEntry.getValue();
			Map<String, ItemPricing> constituents = constituentPricingMap.get(rootPricingEntry.getKey());
			result.put(rootPricingEntry.getKey(), getBundleApportioner().apportion(pricingToApportion, constituents));
		}

		return result;
	}

	/**
	 * Gets the {@link ItemPricingSplitter} singleton.
	 *
	 * @return {@link ItemPricingSplitter}.
	 */
	protected ItemPricingSplitter getPricingSplitter() {
		if (pricingSplitter == null) {
			pricingSplitter = new ItemPricingSplitter();
		}

		return pricingSplitter;
	}

	/**
	 * Generates the string representation of the sku option values on a cart item.
	 *
	 * @param productSku the sku sold by the cart item
	 * @return the generated string
	 */
	private String getSkuOptionsDisplayString(final ProductSku productSku, final Locale locale) {
		final StringBuilder skuOptionValues = new StringBuilder();
		Collection<SkuOptionValue> optionValues = productSku.getOptionValues();
		if (!optionValues.isEmpty()) {

			for (final Iterator<SkuOptionValue> optionValueIter = optionValues.iterator(); optionValueIter.hasNext();) {
				final SkuOptionValue currOptionValue = optionValueIter.next();
				skuOptionValues.append(currOptionValue.getDisplayName(locale, true));
				if (optionValueIter.hasNext()) {
					skuOptionValues.append(", ");
				}
			}
		}
		return skuOptionValues.toString();
	}

	/**
	 * Populates constituents (the leaves).
	 *
	 * @param container the result container.
	 * @param items a collection of {@link ShoppingItem}.
	 */
	protected void populateConstituents(final List<ShoppingItem> container, final Collection<ShoppingItem> items) {
		for (ShoppingItem item : items) {
			if (item.isBundle(getProductSkuLookup())) {
				populateConstituents(container, item.getBundleItems(getProductSkuLookup()));
			} else {
				container.add(item);
			}
		}
	}

	private void populateItemPricing(final Map<String, ItemPricing> childPricing, final Collection<ShoppingItem> items,
										final ShoppingCartTaxSnapshot cartTaxSnapshot) {
		for (ShoppingItem item : items) {
			final ShoppingItemTaxSnapshot shoppingItemPricingSnapshot = cartTaxSnapshot.getShoppingItemTaxSnapshot(item);
			childPricing.put(item.getGuid(), shoppingItemPricingSnapshot.getPricingSnapshot().getLinePricing());
		}
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Splits the prices by quantity using the {@link ItemPricingSplitter}.
	 *
	 * @param pricingMap the pricing map.
	 * @return a split pricing map.
	 */
	protected Map<String, Map<String, List<ItemPricing>>> splitByQuantity(final Map<String, Map<String, ItemPricing>> pricingMap) {
		Map<String, Map<String, List<ItemPricing>>> result = new HashMap<>();

		for (final Entry<String, Map<String, ItemPricing>> rootPricingEntry : pricingMap.entrySet()) {
			Map<String, List<ItemPricing>> splitChildPricingMap = new HashMap<>();
			Map<String, ItemPricing> childPricingMap = rootPricingEntry.getValue();
			for (Entry<String, ItemPricing> entry : childPricingMap.entrySet()) {
				Collection<ItemPricing> splitPricings = getPricingSplitter().split(entry.getValue());
				splitChildPricingMap.put(entry.getKey(), new ArrayList<>(splitPricings));
			}

			result.put(rootPricingEntry.getKey(), splitChildPricingMap);
		}

		return result;
	}

	/**
	 * Extracts discount {@link BigDecimal} from a price map.
	 *
	 * @param pricingMap a price map.
	 * @return the discount {@link BigDecimal} map.
	 */
	protected Map<String, BigDecimal> extractDiscount(final Map<String, ItemPricing> pricingMap) {
		Map<String, BigDecimal> discountMap = new HashMap<>();
		for (Entry<String, ItemPricing> entry : pricingMap.entrySet()) {
			discountMap.put(entry.getKey(), entry.getValue().getDiscount());
		}

		return discountMap;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	public TaxCodeRetriever getTaxCodeRetriever() {
		return taxCodeRetriever;
	}

	public void setTaxCodeRetriever(final TaxCodeRetriever taxCodeRetriever) {
		this.taxCodeRetriever = taxCodeRetriever;
	}

	/**
	 * Gets the effective tax code for a product sku.
	 * 
	 * @param productSku sku for which to find tax code
	 * @return a tax code
	 */
	protected String getTaxCode(final ProductSku productSku) {
		TaxCode effectiveTaxCode = getTaxCodeRetriever().getEffectiveTaxCode(productSku);
		if (effectiveTaxCode == null) {
			return null;
		} else {
			return effectiveTaxCode.getCode();
		}
	}

	/**
	 * Set the time service.
	 *
	 * @param timeService the <code>TimeService</code> instance.
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	protected TimeService getTimeService() {
		return timeService;
	}
}
