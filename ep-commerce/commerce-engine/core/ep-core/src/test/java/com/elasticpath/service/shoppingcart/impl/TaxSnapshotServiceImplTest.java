/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.domain.tax.impl.TaxJurisdictionImpl;
import com.elasticpath.domain.tax.impl.TaxRegionImpl;
import com.elasticpath.domain.tax.impl.TaxValueImpl;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.domain.TaxAddress;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;
import com.elasticpath.service.tax.impl.TaxCalculationResultImpl;
import com.elasticpath.service.tax.impl.TaxCalculationServiceImpl;
import com.elasticpath.service.tax.impl.TaxJurisdictionServiceImpl;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.factory.TestTaxCalculationServiceImpl;
import com.elasticpath.test.jmock.AbstractCatalogDataTestCase;

public class TaxSnapshotServiceImplTest extends AbstractCatalogDataTestCase {

	private static final String CAD = "CAD";
	private static final Currency CURRENCY = Currency.getInstance(CAD);

	private static final String REGION_CODE_CA = "CA";
	private static final String REGION_CODE_BC = "BC";
	private static final String GST_TAX_CODE = "GST";
	private static final String PST_TAX_CODE = "PST";
	private static final BigDecimal GST_TAX_PERCENTAGE = new BigDecimal("6");
	private static final BigDecimal PST_TAX_PERCENTAGE = new BigDecimal("7");
	private static final String SALES_TAX_CODE_GOODS = "GOODS";

	private static final BigDecimal INCLUSIVE_SUBTOTAL_BEFORE_TAX = new BigDecimal("58.66").setScale(2);
	private static final BigDecimal INCLUSIVE_BEFORE_TAX_SHIPPING_COST = new BigDecimal("11.11").setScale(2);
	private static final BigDecimal INCLUSIVE_ITEM_TAX = new BigDecimal("6.34").setScale(2);
	private static final BigDecimal INCLUSIVE_TAX = new BigDecimal("6.73").setScale(2);
	private static final BigDecimal INCLUSIVE_TOTAL = new BigDecimal("76.50").setScale(2);

	private static final BigDecimal EXCLUSIVE_SUBTOTAL = new BigDecimal("65").setScale(2);
	private static final BigDecimal EXCLUSIVE_BEFORE_TAX_SHIPPING_COST = new BigDecimal("11.50").setScale(2);
	private static final BigDecimal EXCLUSIVE_TAX = new BigDecimal("8.37").setScale(2);
	private static final BigDecimal EXCLUSIVE_TOTAL = new BigDecimal("84.87").setScale(2);

	@Mock
	private ShoppingCartPricingSnapshot cartPricingSnapshot;

	@Mock
	private ShoppingItemPricingSnapshot itemPricingSnapshot;

	@Mock
	private TaxCalculationService taxCalculationService;

	private TaxSnapshotServiceImpl taxSnapshotService;

	@SuppressWarnings("unchecked")
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		stubGetBean(ContextIdNames.TAX_CALCULATION_RESULT, TaxCalculationResultImpl.class);
		stubGetBean(ContextIdNames.TAX_CATEGORY, TaxCategoryImpl.class);
		stubGetBean(ContextIdNames.TAX_JURISDICTION, TaxJurisdictionImpl.class);

		taxSnapshotService = new TaxSnapshotServiceImpl();
		taxSnapshotService.setBeanFactory(getBeanFactory());
		taxSnapshotService.setProductSkuLookup(getProductSkuLookup());
		taxSnapshotService.setTaxAddressAdapter(new TaxAddressAdapter());
		taxSnapshotService.setDiscountApportioningCalculator(getDiscountApportioningCalculator());
		taxSnapshotService.setTaxCalculationService(taxCalculationService);

		// We need to mock this as we have a split shipment scenario and so TaxSnapshotServiceImpl.updateTaxCalculationResult() calls these
		context.checking(new Expectations() {
			{
				allowing(cartPricingSnapshot).getShoppingItemPricingSnapshot(with(any(OrderSku.class)));
				will(returnValue(itemPricingSnapshot));

				allowing(getDiscountApportioningCalculator()).apportionDiscountToShoppingItems(
						with(any(Money.class)),
						with(any(Map.class)));
			}
		});
	}

	/**
	 * Test Scenario: Add electronic item to cart and make the tax calculation service calculate the taxes.
	 * The shipping and billing address are different and therefore other taxes apply.
	 * For digital products there are no shipping taxes.
	 *
	 * The store should have the tax codes enabled on both goods and shipping
	 * =====================================================================================
	 *                  | eBooks                   | DVDs
	 * =====================================================================================
	 * Price            | $15.00                   | $50.00
	 * -----------------+--------------------------+----------------------------------------
	 * Item Tax         | $0.90 (6%)               | $5.00 (10%)
	 * -----------------+--------------------------+----------------------------------------
	 * Shipping Cost    | $0.00 (digital product)  | $10.00 ($5.00 fixed + 10% from value)
	 * -----------------+--------------------------+----------------------------------------
	 * Shipping Tax     | $0.00                    | $0.50 (5%)
	 * -----------------+--------------------------+----------------------------------------
	 * Tax Address      | Canada (Billing)         | USA (Shipping)
	 * =====================================================================================
	 * SUBTOTAL $15.00 $60.00 (price + shipping)
	 * -------------------------------------------------------------------------------------
	 * SUBTOTAL + TAXES $15.90 $65.50
	 * =====================================================================================
	 * TOTAL $81.40
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCalculateTaxes1() {
		final ShoppingCartImpl shoppingCart = getShoppingCart();
		Set<TaxCode> taxCodes = new HashSet<>();
		taxCodes.add(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxCodes.add(createTaxCode(SALES_TAX_CODE_DVDS));
		taxCodes.add(createTaxCode(SALES_TAX_CODE_BOOKS));
		shoppingCart.getStore().setTaxCodes(taxCodes);

		final List<ShoppingItem> shoppingItems = shoppingCart.getRootShoppingItems();
		assertEquals("Expecting getShoppingCart() to add two items to cart", 2, shoppingItems.size());

		// Expecting that the first item added to cart by getShoppingCart() is non-shippable, and the second is shippable
		assertFalse("Expecting getShoppingCart() has set the first item in cart as non-shippable",
					shoppingItems.get(0).isShippable(getProductSkuLookup()));
		assertTrue("Expecting getShoppingCart() should has set the second item in cart as shippable",
				   shoppingItems.get(1).isShippable(getProductSkuLookup()));

		// The first item is 3x$5, the second is 5*$10

		// set billing address to be Canada
		shoppingCart.setBillingAddress(getBillingAddress());
		// set shipping address to be US
		shoppingCart.setShippingAddress(getShippingAddress());
		// set the shipping option and cost, verify the cost is set as expected
		updateShippingOptions(shoppingCart);
		final Optional<ShippingOption> selectedShippingOption = shoppingCart.getSelectedShippingOption();
		assertTrue("Expecting shipping option to be selected on shopping cart", selectedShippingOption.isPresent());
		assertEquals("Expecting updateShippingOptions() sets shipping price correctly",
					 new BigDecimal("10.00"), shoppingCart.getShippingListPrice(selectedShippingOption.get().getCode()).getAmount());

		final StoreService storeService = context.mock(StoreService.class);
		context.checking(new Expectations() {
			{
				allowing(storeService).findStoreWithCode(getMockedStore().getCode()); will(returnValue(getMockedStore()));
			}
		});

		final TaxCalculationServiceImpl taxCalculationService = new TestTaxCalculationServiceImpl();
		taxCalculationService.setBeanFactory(getBeanFactory());
		taxCalculationService.setStoreService(storeService);

		final TaxJurisdictionServiceImpl taxJurisdictionService = new TaxJurisdictionServiceImpl();
		taxJurisdictionService.setPersistenceEngine(getPersistenceEngine());
		taxCalculationService.setTaxJurisdictionService(taxJurisdictionService);
		taxSnapshotService.setTaxCalculationService(taxCalculationService);

		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("TAX_JURISDICTIONS_FROM_STORE_BY_COUNTRY_CODE",
					shoppingCart.getStore().getCode(),
					shoppingCart.getShippingAddress().getCountry());
				will(returnValue(getTaxJurisdictionsListForUS()));


				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("TAX_JURISDICTIONS_FROM_STORE_BY_COUNTRY_CODE",
					shoppingCart.getStore().getCode(),
					shoppingCart.getBillingAddress().getCountry());
				will(returnValue(getTaxJurisdictionsListForCA()));

				allowing(getMockPersistenceEngine()).isCacheEnabled();
				will(returnValue(false));
			}
		});

		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, cartPricingSnapshot);

		TaxCalculationResult taxCalculationResult = taxSnapshot.getTaxCalculationResult();
		Money shippingCostNoTaxes = taxCalculationResult.getBeforeTaxShippingCost();
		assertEquals(new BigDecimal("10.00"), shippingCostNoTaxes.getAmount());

		Money totalTaxes = taxCalculationResult.getTotalTaxes();
		Money totalTaxInItemPrice = taxCalculationResult.getTaxInItemPrice();

		assertFalse(taxCalculationResult.isTaxInclusive());
		assertEquals(BigDecimal.ZERO.setScale(2), totalTaxInItemPrice.getAmount());

		Money shippingTax = taxCalculationResult.getShippingTax();

		assertEquals(new BigDecimal("06.40"), totalTaxes.getAmount());
		assertEquals(new BigDecimal("00.50"), shippingTax.getAmount());
		assertEquals(new BigDecimal("81.40"), shoppingCart.getTotal());

		// assertEquals(new BigDecimal("05.90"), shoppingCart.get); //TODO check the total item tax amount
		assertEquals(new BigDecimal("10.00"), shoppingCart.getShippingCost().getAmount());
		assertEquals(new BigDecimal("65.00"), shoppingCart.getBeforeTaxSubTotal().getAmount());
	}

	/**
	 * Test method for calculateShoppingCartTaxAndBeforeTaxPrices.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCalculateShoppingCartTaxAndBeforeTaxPricesInclusive() {
		// The default shopping cart will have a subtotal $65.00,
		// cartItem1: qty=3; listprice=salePrice=$5.00; salesTaxCode:BOOKS.
		// cartItem2: qty=5; listprice=salePrice=$10.00; salesTaxCode:DVDS.
		// with FixedBaseAndOrderTotalPercentageMethod for shipping(base: $5.00 and 10% of subtotal).
		// Billing Address: Joe Doe, 1295 Charleston Road, Mountain View, CA, US, 94043
		final ShoppingCartImpl shoppingCart = getShoppingCart();
		final Optional<ShippingOption> selectedShippingOption = shoppingCart.getSelectedShippingOption();
		assertTrue("Expecting shipping option to be selected on shopping cart", selectedShippingOption.isPresent());
		shoppingCart.setShippingListPrice(selectedShippingOption.get().getCode(), Money.valueOf("11.50", CURRENCY));

		// shoppingCart.setSubtotalDiscount(ORDER_DISCOUNT);

		// TaxCode.SALES_TAX_CODE_SHIPPING: 3.5
		TaxCategory taxCategory1 = new TaxCategoryImpl();

		Currency cadCurrency = Currency.getInstance(CAD);
		final TaxCalculationResult taxCalculationResult =  new TaxCalculationResultImpl() {
			private static final long serialVersionUID = 6251210027242439881L;

			@Override
			public void applyTaxes(final Collection<? extends ShoppingItem> shoppingItems) {
				// do nothing
			}
		};
		taxCalculationResult.setDefaultCurrency(cadCurrency);
		taxCalculationResult.setTaxInclusive(true);
		Money taxValue = Money.valueOf(INCLUSIVE_TAX, cadCurrency);
		taxCalculationResult.addTaxValue(taxCategory1, taxValue);
		Money inclusiveSubTotalMoney = Money.valueOf(INCLUSIVE_SUBTOTAL_BEFORE_TAX, cadCurrency);
		taxCalculationResult.addShippingTax(Money.valueOf(INCLUSIVE_TAX, cadCurrency));
		Money inclusiveBeforeTaxShippingCost = Money.valueOf(INCLUSIVE_BEFORE_TAX_SHIPPING_COST, cadCurrency);
		taxCalculationResult.setBeforeTaxShippingCost(inclusiveBeforeTaxShippingCost);
		taxCalculationResult.setBeforeTaxSubTotal(inclusiveSubTotalMoney);
		taxCalculationResult.addItemTax("SKUCODE", Money.valueOf(INCLUSIVE_ITEM_TAX, cadCurrency));

		context.checking(new Expectations() {
			{
				allowing(taxCalculationService).calculateTaxes(
						with(any(String.class)),
						with(any(TaxAddress.class)),
						with(any(TaxAddress.class)),
						with(any(Money.class)),
						with(any(Map.class)),
						with(any(Money.class)),
						with(any(TaxOperationContext.class)));
				will(returnValue(taxCalculationResult));
				allowing(taxCalculationService).calculateTaxesAndAddToResult(
						with(any(TaxCalculationResult.class)),
						with(any(String.class)),
						with(any(TaxAddress.class)),
						with(any(TaxAddress.class)),
						with(any(Money.class)),
						with(any(Map.class)),
						with(any(Money.class)),
						with(any(TaxOperationContext.class)));
				will(returnValue(taxCalculationResult));
			}
		});

		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, cartPricingSnapshot);

		// tax = 0.39 (shipping tax) + 0.88 (item1 tax: 15/1.0625 * 0.0625) + 5.46 (item2 tax: 50/1.1225 * 0.1225) = 6.73
		assertEquals(INCLUSIVE_TAX, taxSnapshot.getTaxMap().get(taxCategory1).getAmount());
		assertEquals(INCLUSIVE_TOTAL, taxSnapshot.getTotal());
	}

	/**
	 * Test method for calculateShoppingCartTaxAndBeforeTaxPrices.
	 *
	 * The default shopping cart will have a subtotal $65.00,
	 * cartItem1: qty=3; listprice=salePrice=$5.00; salesTaxCode:BOOKS.
	 * cartItem2: qty=5; listprice=salePrice=$10.00; salesTaxCode:DVDS.
	 * with FixedBaseAndOrderTotalPercentageMethod for shipping(base: $5.00 and 10% of subtotal).
	 * Billing Address: Joe Doe, 1295 Charleston Road, Mountain View, CA, US, 94043
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCalculateShoppingCartTaxAndBeforeTaxPricesExclusive() {
		final ShoppingCartImpl shoppingCart = getShoppingCart();
		final Optional<ShippingOption> selectedShippingOption = shoppingCart.getSelectedShippingOption();
		assertTrue("Expecting shipping option to be selected on shopping cart", selectedShippingOption.isPresent());
		shoppingCart.setShippingListPrice(selectedShippingOption.get().getCode(), Money.valueOf("11.50", CURRENCY));

		// TaxCode.SALES_TAX_CODE_SHIPPING: 3.5
		final TaxCategory taxCategory1 = new TaxCategoryImpl();

		final Currency cadCurrency = Currency.getInstance(CAD);
		final TaxCalculationResult taxCalculationResult = createTaxCalculationResult(taxCategory1, cadCurrency);
		taxCalculationResult.setBeforeTaxSubTotal(Money.valueOf(EXCLUSIVE_SUBTOTAL, cadCurrency));
		taxCalculationResult.setBeforeTaxShippingCost(Money.valueOf(EXCLUSIVE_BEFORE_TAX_SHIPPING_COST, cadCurrency));

		context.checking(new Expectations() {
			{
				allowing(taxCalculationService).calculateTaxes(
					with(any(String.class)),
					with(any(TaxAddress.class)),
					with(any(TaxAddress.class)),
					with(any(Money.class)),
					with(any(Map.class)),
					with(any(Money.class)),
					with(any(TaxOperationContext.class)));
				will(returnValue(taxCalculationResult));
				allowing(taxCalculationService).calculateTaxesAndAddToResult(
					with(any(TaxCalculationResult.class)),
					with(any(String.class)),
					with(any(TaxAddress.class)),
					with(any(TaxAddress.class)),
					with(any(Money.class)),
					with(any(Map.class)),
					with(any(Money.class)),
					with(any(TaxOperationContext.class)));
				will(returnValue(taxCalculationResult));
			}
		});

		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, cartPricingSnapshot);

		// tax = 11.50 * 0.035 (shipping tax) + (15 + 50) * 0.1225 = 8.37
		assertEquals(EXCLUSIVE_TAX, taxSnapshot.getTaxMap().get(taxCategory1).getAmount());
		assertEquals(EXCLUSIVE_TOTAL, taxSnapshot.getTotal());
	}

	private TaxCalculationResult createTaxCalculationResult(final TaxCategory taxCategory, final Currency cadCurrency) {
		final TaxCalculationResult taxCalculationResult = new TaxCalculationResultImpl() {
			private static final long serialVersionUID = 6251210027242439881L;

			@Override
			public void applyTaxes(final Collection<? extends ShoppingItem> shoppingItems) {
				// do nothing
			}
		};
		taxCalculationResult.setDefaultCurrency(cadCurrency);
		taxCalculationResult.setTaxInclusive(false);
		final Money taxValue = Money.valueOf(EXCLUSIVE_TAX, cadCurrency);
		taxCalculationResult.addTaxValue(taxCategory, taxValue);
		final Money inclusiveSubTotalMoney = Money.valueOf(EXCLUSIVE_SUBTOTAL, cadCurrency);
		taxCalculationResult.setBeforeTaxSubTotal(inclusiveSubTotalMoney);
		taxCalculationResult.addToTaxInItemPrice(taxValue);

		return taxCalculationResult;
	}

	/**
	 * Tax Jurisdiction - CA Used only the matching strategy for matching the country code. Tax Values for Tax Category 'CA' ======================
	 * Code | Value ====================== Books | 6% -----------+------------ DVDs | 6% ---------------------- SHIPPING | 6% TAX |
	 * ======================
	 */
	private List<TaxJurisdiction> getTaxJurisdictionsListForCA() {
		return getCaTaxJurisdictionsList();
	}

	/**
	 * Tax Jurisdiction - US. Match strategy - matches the country code. Tax Values for Tax Category 'US' ====================== Code | Value
	 * ====================== Books | 10% -----------+------------ DVDs | 10% ---------------------- SHIPPING | 5% TAX | ======================
	 *
	 * @return List of {@link TaxJurisdiction}
	 */
	public static List<TaxJurisdiction> getTaxJurisdictionsListForUS() {
		List<TaxJurisdiction> jurisdictions = new ArrayList<>();
		TaxJurisdiction taxJurisdictionUSA = new TaxJurisdictionImpl();
		taxJurisdictionUSA.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
		taxJurisdictionUSA.setRegionCode("US");
		taxJurisdictionUSA.setGuid("1002");

		// category
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxCategory.setName("US");

		TaxRegion taxRegion = new TaxRegionImpl();

		Map<String, TaxValue> taxValueMap = new HashMap<>();

		TaxValue taxValue = new TaxValueImpl();
		final TaxCode booksTaxCode = new TaxCodeImpl();
		booksTaxCode.setCode(SALES_TAX_CODE_BOOKS);
		booksTaxCode.setGuid(SALES_TAX_CODE_BOOKS);
		taxValue.setTaxCode(booksTaxCode);
		taxValue.setTaxValue(BigDecimal.TEN);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		final TaxCode dvdTaxCode = new TaxCodeImpl();
		dvdTaxCode.setCode(SALES_TAX_CODE_DVDS);
		dvdTaxCode.setGuid(SALES_TAX_CODE_DVDS);
		taxValue.setTaxCode(dvdTaxCode);
		taxValue.setTaxValue(BigDecimal.TEN);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		final TaxCode shippingTaxCode = new TaxCodeImpl();
		shippingTaxCode.setCode(TaxCode.TAX_CODE_SHIPPING);
		shippingTaxCode.setGuid(TaxCode.TAX_CODE_SHIPPING);
		taxValue.setTaxCode(shippingTaxCode);
		taxValue.setTaxValue(new BigDecimal("5"));

		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);
		taxRegion.setRegionName("US");

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdictionUSA.addTaxCategory(taxCategory);
		jurisdictions.add(taxJurisdictionUSA);

		return jurisdictions;
	}

	/**
	 * Creates CA tax jurisdiction list of the single jurisdiction. That jurisdiction is:<br>
	 * Region code: CA<br>
	 * Country category: GST<br>
	 * Tax Region: CA<br>
	 * Tax Values: SHIPPING==6%, GOODS==6%<br>
	 *
	 * Subcountry category: PST<br>
	 * Tax Region: BC<br>
	 * Tax Values: SHIPPING==7%, GOODS==7%<br>
	 *
	 * Subcountry category: ANOTHER_CATEGORY<br>
	 * Tax Region: VANCOUVER<br>
	 * Tax Values: SHIPPING==7%, GOODS==7%<br>
	 *
	 * The last category mustn't be taken into account while calculating taxes.
	 * This category doesn't match the shipping address.
	 * @return List
	 */
	private List<TaxJurisdiction> getCaTaxJurisdictionsList() {
		List<TaxJurisdiction> list = new ArrayList<>();
		TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
		taxJurisdiction.setRegionCode(REGION_CODE_CA);
		taxJurisdiction.setGuid("1001");

		// 1) category
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxCategory.setName(GST_TAX_CODE);

		TaxRegion taxRegion = new TaxRegionImpl();

		Map<String, TaxValue> taxValueMap = new HashMap<>();

		TaxValue taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(GST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_BOOKS));
		taxValue.setTaxValue(GST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_DVDS));
		taxValue.setTaxValue(GST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(GST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);
		taxRegion.setRegionName(REGION_CODE_CA);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 2) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName(PST_TAX_CODE);

		taxValueMap = new HashMap<>();

		taxRegion = new TaxRegionImpl();
		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(PST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(PST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);

		taxRegion.setRegionName(REGION_CODE_BC);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);
		// 3) category - musn't be used
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName("ANOTHER_CATEGORY");

		taxValueMap = new HashMap<>();

		taxRegion = new TaxRegionImpl();
		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(SALES_TAX_CODE_GOODS));
		taxValue.setTaxValue(PST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		taxValue.setTaxCode(createTaxCode(TaxCode.TAX_CODE_SHIPPING));
		taxValue.setTaxValue(PST_TAX_PERCENTAGE);
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxRegion.setTaxValuesMap(taxValueMap);

		taxRegion.setRegionName("VANCOUVER");

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		list.add(taxJurisdiction);
		return list;
	}

	/**
	 * Returns a newly created address.
	 *
	 * @return a newly created address
	 */
	protected Address getBillingAddress() {
		Address address = new CustomerAddressImpl();
		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry("CA");
		address.setStreet1("1295 Charleston Road");
		address.setCity("Vancouver");
		address.setSubCountry("CA");
		address.setZipOrPostalCode("V5T 4H3");
		return address;
	}

	private Address getShippingAddress() {
		Address address = new CustomerAddressImpl();
		address.setFirstName("Joe");
		address.setLastName("Doe");
		address.setCountry("US");
		address.setStreet1("1295 Charleston Road");
		address.setCity("New York");
		address.setSubCountry("US");
		address.setZipOrPostalCode("12343");

		return address;
	}

	private TaxCode createTaxCode(final String taxCodeName) {
		final TaxCode result = context.mock(TaxCode.class, "TaxCode-" + UUID.randomUUID());
		context.checking(new Expectations() {
			{
				allowing(result).getCode();
				will(returnValue(taxCodeName));
			}
		});
		return result;
	}

}
