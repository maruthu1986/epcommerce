/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.tax.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.CatalogLocaleImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.money.Money;
import com.elasticpath.money.StandardMoneyFormatter;
import com.elasticpath.plugin.tax.builder.TaxAddressBuilder;
import com.elasticpath.plugin.tax.builder.TaxOperationContextBuilder;
import com.elasticpath.plugin.tax.common.TaxContextIdNames;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;
import com.elasticpath.plugin.tax.domain.TaxedItem;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxRecord;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxableItemContainer;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItem;
import com.elasticpath.plugin.tax.domain.impl.MutableTaxedItemContainer;
import com.elasticpath.plugin.tax.domain.impl.StringTaxDocumentId;
import com.elasticpath.plugin.tax.domain.impl.TaxableItemImpl;
import com.elasticpath.plugin.tax.manager.TaxManager;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Replacement class for TaxCalculationServiceTest which uses JUnit 4 and JMock 2 and removes dependency on ElasticPathTestCase.
 */
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.TooManyMethods", "PMD.ExcessiveImports" })
public class TaxCalculationServiceImplTest {

	private static final String TEST_SKU_CODE = "00MYCODE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final BigDecimal SHIPPING_TAXES = new BigDecimal("1.04");

	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

	private static final BigDecimal TEN = BigDecimal.TEN.setScale(2);

	private static final BigDecimal SHIPPING_COST = new BigDecimal("8.00");

	private static final String PST_TAX_CODE = "PST";

	private static final String GST_TAX_CODE = "GST";

	private static final String VAT_TAX_CODE = "VAT";

	private static final String CAD = "CAD";

	private static final String SALES_TAX_CODE_GOODS = "GOODS";

	private static final Currency CA_CURRENCY = Currency.getInstance(CAD);

	private static final Currency GB_CURRENCY = Currency.getInstance("GBP");

	private TaxCalculationServiceImpl taxCalculationService;

	private TaxCalculationResult inclusiveTaxCalculationResult;

	private TaxCalculationResult exclusiveTaxCalculationResult;

	private ProductSkuLookup productSkuLookup;

	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory expectationsFactory;

	private TaxableItemContainerAdapter taxableItemContainerAdapter;

	private TaxManager taxManager;

	private TaxDocument taxDocument;

	private StoreService storeService;

	@Mock
	private ShoppingItemPricingSnapshot pricingSnapshot;

	/**
	 * Initialise commonly used variables.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		productSkuLookup = context.mock(ProductSkuLookup.class);

		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CATALOG_LOCALE, CatalogLocaleImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRODUCT_SKU_LOOKUP, productSkuLookup);
		expectationsFactory.allowingBeanFactoryGetBean(TaxContextIdNames.MUTABLE_TAXABLE_ITEM_CONTAINER, MutableTaxableItemContainer.class);
		expectationsFactory.allowingBeanFactoryGetBean(TaxContextIdNames.TAXABLE_ITEM, TaxableItemImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.TAX_CATEGORY, TaxCategoryImpl.class);

		taxManager = context.mock(TaxManager.class);
		taxDocument = context.mock(TaxDocument.class);
		storeService = context.mock(StoreService.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.STORE_SERVICE, storeService);

		context.checking(new Expectations() {
			{
				allowing(taxManager).calculate(with(any(TaxableItemContainer.class)));
				will(returnValue(taxDocument));
				allowing(storeService).findStoreWithCode(getInclusiveStore().getCode());
				will(returnValue(getInclusiveStore()));
				allowing(storeService).findStoreWithCode(getExclusiveStore().getCode());
				will(returnValue(getExclusiveStore()));
			}
		});

		taxableItemContainerAdapter = new TaxableItemContainerAdapter();
		taxableItemContainerAdapter.setBeanFactory(beanFactory);
		taxableItemContainerAdapter.setProductSkuLookup(productSkuLookup);
		taxableItemContainerAdapter.setTaxCodeRetriever(new TaxCodeRetrieverImpl());

		final DiscountApportioningCalculatorImpl discountCalculator = new DiscountApportioningCalculatorImpl();
		discountCalculator.setProductSkuLookup(productSkuLookup);

		taxCalculationService = new TaxCalculationServiceImpl();
		taxCalculationService.setDiscountCalculator(discountCalculator);
		taxCalculationService.setStoreService(storeService);
		taxCalculationService.setTaxableItemContainerAdapter(taxableItemContainerAdapter);
		taxCalculationService.setTaxManager(taxManager);
		taxCalculationService.setBeanFactory(beanFactory);

		inclusiveTaxCalculationResult = createTaxCalculationResult(GB_CURRENCY);
		exclusiveTaxCalculationResult = createTaxCalculationResult(CA_CURRENCY);
	}

	/**
	 * Tests calculateTaxesAndAddToResult() by passing invalid (null) parameter.
	 */
	@Test
	public void testCalculateTaxesAndAddToResultWithInvalidParameter() {
		TaxOperationContext taxOperationContext = getTaxOperationContext(CA_CURRENCY);
		try {
			taxCalculationService.calculateTaxesAndAddToResult(
					exclusiveTaxCalculationResult, null, null, null, null, null, null, taxOperationContext);
			fail("should throw exception if an invalid parameter is passed");
		} catch (EpServiceException exc) {
			assertNotNull(exc);
		}
	}

	/**
	 * Test that when a null address is passed in, no tax calculations are performed.
	 */
	@Test
	public void testCalculateTaxesWithNullAddressReturnsEmptyResult() {

		Money shippingCost = Money.valueOf(BigDecimal.ZERO, CA_CURRENCY);
		Money discount = Money.valueOf(BigDecimal.ZERO, CA_CURRENCY);

		TaxOperationContext taxOperationContext = getTaxOperationContext(CA_CURRENCY);

		prepareTaxCalculationResult(false, CA_CURRENCY,
				Arrays.asList(getTaxedItem(false, discount.getAmount(), CA_CURRENCY, TaxCode.TAX_CODE_SHIPPING,
						shippingCost.getAmount(), shippingCost.getAmount())));

		TaxCalculationResult result = taxCalculationService.calculateTaxesAndAddToResult(exclusiveTaxCalculationResult,
				getInclusiveStore().getCode(),
				null,
				null,
				shippingCost,
				Maps.<ShoppingItem, ShoppingItemPricingSnapshot>newHashMap(),
				discount,
				taxOperationContext);

		final TaxCalculationResult emptyResult =  createTaxCalculationResult(CA_CURRENCY);

		assertEquals(result.isTaxInclusive(), emptyResult.isTaxInclusive());
		assertEquals(result.getShippingTax(), emptyResult.getShippingTax());
		assertEquals(result.getTotalItemTax(), emptyResult.getTotalItemTax());
	}

	/**
	 * Test single inclusive tax calculation from the top with discount.
	 */
	@Test
	public void testCalculateInclusiveTaxesFromTopWithDiscount() {
		final BigDecimal itemTaxes = new BigDecimal("11.74");
		final BigDecimal discount = BigDecimal.TEN;

		testCalculateInclusiveTaxesFromTop(discount, ONE_HUNDRED, SHIPPING_COST, itemTaxes, SHIPPING_TAXES);
	}

	/**
	 * Test single inclusive taxes using highest level method.
	 */
	@Test
	public void testCalculateInclusiveTaxesFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("13.04");

		testCalculateInclusiveTaxesFromTop(BigDecimal.ZERO, ONE_HUNDRED, SHIPPING_COST, itemTaxes, SHIPPING_TAXES);
	}

	private void testCalculateInclusiveTaxesFromTop(final BigDecimal discount,
			final BigDecimal itemPrice,
			final BigDecimal shippingCost,
			final BigDecimal itemTax,
			final BigDecimal shippingTax) {

		prepareTaxCalculationResult(true, GB_CURRENCY,
				Arrays.asList(getTaxedItem(true, discount, GB_CURRENCY, SALES_TAX_CODE_GOODS, itemPrice, itemTax),
						getTaxedItem(true, discount, GB_CURRENCY, TaxCode.TAX_CODE_SHIPPING, shippingCost, shippingTax)));

		final Money lineItemPriceMoney = Money.valueOf(itemPrice, GB_CURRENCY);
		final Money shippingCostMoney = Money.valueOf(shippingCost, GB_CURRENCY);
		final Money discountMoney = Money.valueOf(discount, GB_CURRENCY);

		final ShoppingItem shoppingItem = mockShoppingItem();

		final ProductSku productSku = createTestProductSku(TEST_SKU_CODE, SALES_TAX_CODE_GOODS);
		prepareExpectationsOnTaxCalculations(lineItemPriceMoney, shoppingItem, productSku);

		final Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap = ImmutableMap.of(shoppingItem, pricingSnapshot);

		TaxOperationContext taxOperationContext = getTaxOperationContext(GB_CURRENCY);

		final TaxCalculationResult result = taxCalculationService.calculateTaxesAndAddToResult(inclusiveTaxCalculationResult,
				getInclusiveStore().getCode(),
				getUkAddress(),
				getUkAddress(),
				shippingCostMoney,
				lineItemPricingSnapshotMap,
				discountMoney,
				taxOperationContext);

		assertEquals(itemTax, result.getTotalItemTax().getAmount());
		assertEquals(shippingTax, result.getShippingTax().getAmount());
		assertEquals(lineItemPriceMoney, result.getSubtotal());
		assertEquals(shippingCostMoney.subtract(Money.valueOf(shippingTax, GB_CURRENCY)), result.getBeforeTaxShippingCost());
		assertEquals(shippingTax.add(itemTax), result.getTotalTaxes().getAmount());
		assertEquals(lineItemPriceMoney.getAmount().subtract(itemTax), result.getBeforeTaxSubTotal().getAmount());
	}

	private ShoppingItem mockShoppingItem() {
		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
		context.checking(new Expectations() {
			{
				allowing(shoppingItem).isShippable(with(any(ProductSkuLookup.class)));
				will(returnValue(true));
				allowing(shoppingItem).getQuantity();
			}
		});
		return shoppingItem;
	}

	private TaxOperationContext getTaxOperationContext(final Currency currency) {
		TaxOperationContext taxOperationContext = TaxOperationContextBuilder
				.newBuilder()
				.withCurrency(currency)
				.withTaxJournalType(TaxJournalType.PURCHASE)
				.build();
		return taxOperationContext;
	}

	private void prepareExpectationsOnTaxCalculations(final Money lineItemPriceMoney, final ShoppingItem shoppingItem, final ProductSku productSku) {
		context.checking(new Expectations() {
			{
				final PriceCalculator priceCalc = context.mock(PriceCalculator.class);
				allowing(pricingSnapshot).getPriceCalc();
				will(returnValue(priceCalc));
				allowing(priceCalc).withCartDiscounts();
				will(returnValue(priceCalc));
				allowing(priceCalc).getAmount();
				will(returnValue(lineItemPriceMoney.getAmount()));

				allowing(shoppingItem).hasPrice();
				will(returnValue(true));
				allowing(shoppingItem).getSkuGuid();
				will(returnValue(productSku.getGuid()));
				allowing(shoppingItem).getGuid();
				will(returnValue(TEST_SKU_CODE));
				allowing(pricingSnapshot).getListUnitPrice();
				will(returnValue(lineItemPriceMoney));
				atLeast(0).of(shoppingItem).hasBundleItems(productSkuLookup);
				will(returnValue(false));
				allowing(shoppingItem).isBundle(productSkuLookup);
				allowing(shoppingItem).isDiscountable(productSkuLookup);
				will(returnValue(true));
			}
		});
	}

	/**
	 * Test calculation for multiple inclusive taxes from highest level method.
	 */
	@Test
	public void testCalculateInclusiveMultipleTaxesFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("16.67");
		final BigDecimal shippingTaxes = new BigDecimal("1.33");

		testCalculateInclusiveTwoTaxesFromTop(BigDecimal.ZERO, ONE_HUNDRED, SHIPPING_COST, itemTaxes, shippingTaxes);
	}

	/**
	 * Test calculation for multiple inclusive taxes from highest level method with a discount applied.
	 */
	@Test
	public void testCalculateInclusiveMultipleTaxesWithDiscountFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("15.00");
		final BigDecimal shippingTaxes = new BigDecimal("1.33");
		final BigDecimal discount = BigDecimal.TEN;

		testCalculateInclusiveTwoTaxesFromTop(discount, ONE_HUNDRED, SHIPPING_COST, itemTaxes, shippingTaxes);
	}

	private void testCalculateInclusiveTwoTaxesFromTop(final BigDecimal discount,
			final BigDecimal itemPrice,
			final BigDecimal shippingCost,
			final BigDecimal itemTaxes,
			final BigDecimal shippingTaxes) {

		prepareTaxCalculationResult(true, GB_CURRENCY,
				Arrays.asList(getTaxedItem(true, discount, GB_CURRENCY, SALES_TAX_CODE_GOODS, itemPrice, itemTaxes),
						getTaxedItem(true, discount, GB_CURRENCY, TaxCode.TAX_CODE_SHIPPING, shippingCost, shippingTaxes)));

		final Money lineItemPriceMoney = Money.valueOf(itemPrice, GB_CURRENCY);
		final Money shippingCostMoney = Money.valueOf(shippingCost, GB_CURRENCY);
		final Money discountMoney = Money.valueOf(discount, GB_CURRENCY);

		final ShoppingItem lineItem = mockShoppingItem();

		final ProductSku productSku = createTestProductSku(TEST_SKU_CODE, SALES_TAX_CODE_GOODS);
		prepareExpectationsOnTaxCalculations(lineItemPriceMoney, lineItem, productSku);

		final Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap = ImmutableMap.of(lineItem, pricingSnapshot);

		inclusiveTaxCalculationResult.setTaxInclusive(true);

		TaxOperationContext taxOperationContext = getTaxOperationContext(GB_CURRENCY);

		final TaxCalculationResult result = taxCalculationService.calculateTaxesAndAddToResult(inclusiveTaxCalculationResult,
				getInclusiveStore().getCode(),
				getUkAddress(),
				getUkAddress(),
				shippingCostMoney,
				lineItemPricingSnapshotMap,
				discountMoney,
				taxOperationContext);

		assertEquals(itemTaxes, result.getTotalItemTax().getAmount());
		assertEquals(shippingTaxes, result.getShippingTax().getAmount());
		assertEquals(lineItemPriceMoney, result.getSubtotal());
		assertEquals(shippingCostMoney.subtract(Money.valueOf(shippingTaxes, GB_CURRENCY)), result.getBeforeTaxShippingCost());
		assertEquals(shippingTaxes.add(itemTaxes), result.getTotalTaxes().getAmount());
		assertEquals(lineItemPriceMoney.getAmount().subtract(itemTaxes), result.getBeforeTaxSubTotal().getAmount());
	}

	/**
	 * Test exclusive tax calculation from high level calculation method including a discount and verify the tax calculation result values are as
	 * expected.
	 */
	@Test
	public void testCalculationExclusiveTaxesWithDiscountFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("11.70");
		final BigDecimal shippingTaxes = SHIPPING_TAXES;
		final BigDecimal discount = TEN;

		testCalculateExclusiveTaxesFromTop(discount, ONE_HUNDRED, SHIPPING_COST, itemTaxes, shippingTaxes, SALES_TAX_CODE_GOODS);
	}

	/**
	 * Test exclusive tax calculation from high level calculation method and verify the tax calculation result values are as expected.
	 */
	@Test
	public void testCalculateExclusiveTaxesFromTop() {
		final BigDecimal itemTaxes = new BigDecimal("13.00");
		final BigDecimal shippingTaxes = SHIPPING_TAXES;

		testCalculateExclusiveTaxesFromTop(BigDecimal.ZERO, ONE_HUNDRED, SHIPPING_COST, itemTaxes, shippingTaxes, SALES_TAX_CODE_GOODS);
	}

	private void testCalculateExclusiveTaxesFromTop(final BigDecimal discount,
			final BigDecimal itemPrice,
			final BigDecimal shippingCost,
			final BigDecimal itemTaxes,
			final BigDecimal shippingTaxes,
			final String productTaxCode) {

		final MutableTaxedItemContainer taxedItemContainer = new MutableTaxedItemContainer();
		taxedItemContainer.setTaxInclusive(false);
		taxedItemContainer.addTaxedItem(getTaxedItem(false, discount, CA_CURRENCY, productTaxCode, itemPrice, itemTaxes));
		taxedItemContainer.addTaxedItem(getTaxedItem(false, discount, CA_CURRENCY, TaxCode.TAX_CODE_SHIPPING, shippingCost, shippingTaxes));

		final ShoppingItem lineItem = mockShoppingItem();

		context.checking(new Expectations() {
			{
				allowing(taxDocument).getTaxedItemContainer();
				will(returnValue(taxedItemContainer));
				allowing(taxDocument).getDocumentId();
				will(returnValue(StringTaxDocumentId.fromString(String.valueOf(System.currentTimeMillis()))));
			}
		});

		final Money lineItemPriceMoney = Money.valueOf(itemPrice, CA_CURRENCY);
		final Money shippingCostMoney = Money.valueOf(shippingCost, CA_CURRENCY);
		final Money discountMoney = Money.valueOf(discount, CA_CURRENCY);

		final ProductSku productSku = createTestProductSku(TEST_SKU_CODE, productTaxCode);
		prepareExpectationsOnTaxCalculations(lineItemPriceMoney, lineItem, productSku);

		final Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap = ImmutableMap.of(lineItem, pricingSnapshot);

		TaxOperationContext taxOperationContext = getTaxOperationContext(CA_CURRENCY);

		final TaxCalculationResult result = taxCalculationService.calculateTaxesAndAddToResult(exclusiveTaxCalculationResult,
				getExclusiveStore().getCode(),
				getCaAddress(),
				getCaAddress(),
				shippingCostMoney,
				lineItemPricingSnapshotMap,
				discountMoney,
				taxOperationContext);

		assertEquals(itemTaxes, result.getTotalItemTax().getAmount());
		assertEquals(shippingTaxes, result.getShippingTax().getAmount());
		assertEquals(lineItemPriceMoney, result.getSubtotal());
		assertEquals(shippingCostMoney, result.getBeforeTaxShippingCost());
		assertEquals(shippingTaxes.add(itemTaxes), result.getTotalTaxes().getAmount());
		assertEquals(lineItemPriceMoney.getAmount(), result.getBeforeTaxSubTotal().getAmount());
	}

	/**
	 * When a tax code is disabled on the store level, no product should have the tax applied.
	 */
	@Test
	public void testCalculateTaxesWithNonStoreEnabledTax() {
		final BigDecimal zeroCurrencyScale = BigDecimal.ZERO.setScale(2);
		testCalculateExclusiveTaxesFromTopUnusedTaxCode(zeroCurrencyScale,
				ONE_HUNDRED,
				SHIPPING_COST,
				zeroCurrencyScale,
				SHIPPING_TAXES,
				"NOTAXCODE");
	}

	private void testCalculateExclusiveTaxesFromTopUnusedTaxCode(final BigDecimal discount,
			final BigDecimal itemPrice,
			final BigDecimal shippingCost,
			final BigDecimal itemTaxes,
			final BigDecimal shippingTaxes,
			final String productTaxCode) {

		prepareTaxCalculationResult(false, CA_CURRENCY,
				Arrays.asList(getTaxedItem(false, discount, CA_CURRENCY, SALES_TAX_CODE_GOODS, itemPrice, itemTaxes),
						getTaxedItem(false, discount, CA_CURRENCY, TaxCode.TAX_CODE_SHIPPING, shippingCost, shippingTaxes)));

		final Money lineItemPriceMoney = Money.valueOf(itemPrice, CA_CURRENCY);
		final Money shippingCostMoney = Money.valueOf(shippingCost, CA_CURRENCY);
		final Money discountMoney = Money.valueOf(discount, CA_CURRENCY);

		final ShoppingItem lineItem = mockShoppingItem();

		final ProductSku productSku = createTestProductSku(TEST_SKU_CODE, productTaxCode);
		prepareExpectationsOnTaxCalculations(lineItemPriceMoney, lineItem, productSku);

		final Map<ShoppingItem, ShoppingItemPricingSnapshot> lineItemPricingSnapshotMap = ImmutableMap.of(lineItem, pricingSnapshot);

		TaxOperationContext taxOperationContext = getTaxOperationContext(CA_CURRENCY);

		final TaxCalculationResult result = taxCalculationService.calculateTaxesAndAddToResult(exclusiveTaxCalculationResult,
				getExclusiveStore().getCode(),
				getCaAddress(),
				getCaAddress(),
				shippingCostMoney,
				lineItemPricingSnapshotMap,
				discountMoney,
				taxOperationContext);

		assertEquals(itemTaxes, result.getTotalItemTax().getAmount());
		assertEquals(shippingTaxes, result.getShippingTax().getAmount());
		assertEquals(lineItemPriceMoney, result.getSubtotal());
		assertEquals(shippingCostMoney, result.getBeforeTaxShippingCost());
		assertEquals(shippingTaxes.add(itemTaxes), result.getTotalTaxes().getAmount());
		assertEquals(lineItemPriceMoney.getAmount(), result.getBeforeTaxSubTotal().getAmount());
	}

	// Helper methods
	private void prepareTaxCalculationResult(final boolean taxInclusive,
			final Currency currency, final List<TaxedItem> taxedItems) {

		final MutableTaxedItemContainer taxedItemContainer = new MutableTaxedItemContainer();
		taxedItemContainer.setTaxInclusive(taxInclusive);
		taxedItemContainer.setCurrency(currency);

		for (TaxedItem item : taxedItems) {
			taxedItemContainer.addTaxedItem(item);
		}
		context.checking(new Expectations() {
			{
				allowing(taxDocument).getTaxedItemContainer();
				will(returnValue(taxedItemContainer));
				allowing(taxDocument).getDocumentId();
				will(returnValue(StringTaxDocumentId.fromString(String
						.valueOf(System.currentTimeMillis()))));
			}
		});
	}

	private TaxedItem getTaxedItem(final boolean taxInclusive,
			final BigDecimal discount,
			final Currency currency,
			final String taxCode,
			final BigDecimal itemPrice,
			final BigDecimal itemTaxes) {

		TaxableItemImpl item = new TaxableItemImpl();
		item.setItemGuid(taxCode);
		item.setCurrency(currency);
		item.setItemCode(taxCode);
		item.setTaxCode(taxCode);
		item.setPrice(itemPrice);
		item.setTaxCodeActive(true);
		item.applyDiscount(discount);

		MutableTaxRecord taxRecord = new MutableTaxRecord();
		taxRecord.setTaxCode(item.getTaxCode());
		taxRecord.setTaxName(GST_TAX_CODE);
		taxRecord.setTaxValue(itemTaxes);

		final MutableTaxedItem taxedItem = new MutableTaxedItem();
		taxedItem.setTaxableItem(item);
		taxedItem.setItemGuid(item.getItemGuid());

		if (taxInclusive) {
			taxedItem.setPriceBeforeTax(item.getPrice().subtract(itemTaxes));
		} else {
			taxedItem.setPriceBeforeTax(item.getPrice());
		}

		taxedItem.addTaxRecord(taxRecord);

		return taxedItem;
	}

	private TaxCalculationResultImpl createTaxCalculationResult(final Currency currency) {
		TaxCalculationResultImpl result = new TaxCalculationResultImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			Money getMoneyZero() {
				return Money.valueOf(BigDecimal.ZERO, currency);
			}
		};
		result.setDefaultCurrency(currency);
		return result;
	}

	private ProductSku createTestProductSku(final String skuCode, final String productTaxCode) {
		final ProductSku productSku = new ProductSkuImpl();
		productSku.initialize();
		productSku.setSkuCode(skuCode);
		final Product product = new ProductImpl() {
			/** Serial version id. */
			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getDisplayName(final Locale locale) {
				return "Some Item Name";
			}
		};
		TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(productTaxCode);
		product.setTaxCodeOverride(taxCode);
		productSku.setProduct(product);

		context.checking(new Expectations() { {
			allowing(productSkuLookup).findByGuid(productSku.getGuid()); will(returnValue(productSku));
		} });

		return productSku;
	}

	private static TaxCode createTaxCode(final String taxCodeName) {
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(taxCodeName);
		taxCode.setGuid(System.currentTimeMillis() + taxCodeName);
		return taxCode;
	}

	private Store getInclusiveStore() {
		return getStore(getActiveInclusiveTaxCodes(), CA_CURRENCY);
	}

	private Store getExclusiveStore() {
		return getStore(getActiveExclusiveTaxCodes(), CA_CURRENCY);
	}

	private Store getStore(final Set<TaxCode> taxCodes, final Currency currency) {
		Store store = new StoreImpl();
		store.setTaxCodes(taxCodes);
		store.setDefaultCurrency(currency);
		return store;
	}

	private Set<TaxCode> getActiveInclusiveTaxCodes() {
		Set<TaxCode> taxCodes = new HashSet<>();
		taxCodes.add(createTaxCode(SALES_TAX_CODE_GOODS));
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(VAT_TAX_CODE));
		return taxCodes;
	}

	private Set<TaxCode> getActiveExclusiveTaxCodes() {
		Set<TaxCode> taxCodes = new HashSet<>();
		taxCodes.add(createTaxCode(SALES_TAX_CODE_GOODS));
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(GST_TAX_CODE));
		taxCodes.add(createTaxCode(PST_TAX_CODE));
		return taxCodes;
	}

	private TaxAddress getCaAddress() {
		return TaxAddressBuilder
				.newBuilder()
				.withStreet1("1295 Charleston Road")
				.withCity("Mountain View")
				.withCountry("CA")
				.withSubCountry("BC").withZipOrPostalCode("94043").build();
	}

	private TaxAddress getUkAddress() {
		return TaxAddressBuilder
				.newBuilder()
				.withStreet1("1295 Charing Cross Road")
				.withCity("London")
				.withCountry("UK")
				.withZipOrPostalCode("V2T 1E4").build();
	}
}
