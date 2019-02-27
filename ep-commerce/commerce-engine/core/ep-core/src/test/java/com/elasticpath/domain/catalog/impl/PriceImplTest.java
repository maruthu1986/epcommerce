/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.catalog.impl;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.domain.subscriptions.impl.PaymentScheduleImpl;
import com.elasticpath.money.Money;
import com.elasticpath.money.StandardMoneyFormatter;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>PriceImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class PriceImplTest {

	private PriceImpl priceImpl;

	private static final int FIRST_TIER_MTY = 1;

	private static final int SECOND_TIER_MTY = 5;

	private static final int THIRD_TIER_MTY = 10;

	private static final int QTY_5 = 5;

	private static final int QTY_6 = 6;

	private static final int NON_EXIST_TIER_MTY = 99;

	private static final int QTY_100 = 100;

	private static final Quantity MONTHLY_QTY = new Quantity(1, "month");

	private static final Currency CURRENCY_CAD = Currency.getInstance(Locale.CANADA);

	private PricingScheme pricingScheme;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepares for the next test.
	 */
	@Before
	public void setUp() {

		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		priceImpl = new PriceImpl();

		PriceTierImpl priceTier1 = new PriceTierImpl();
		priceTier1.setMinQty(FIRST_TIER_MTY);
		priceImpl.addOrUpdatePriceTier(priceTier1);

		PriceTierImpl priceTier2 = new PriceTierImpl();
		priceTier2.setMinQty(SECOND_TIER_MTY);
		priceImpl.addOrUpdatePriceTier(priceTier2);

		PriceTierImpl priceTier3 = new PriceTierImpl();
		priceTier3.setMinQty(THIRD_TIER_MTY);
		priceImpl.addOrUpdatePriceTier(priceTier3);

		PaymentSchedule monthlyPaymentSchedule = new PaymentScheduleImpl();
		monthlyPaymentSchedule.setName("Monthly");
		monthlyPaymentSchedule.setPaymentFrequency(MONTHLY_QTY);
		//null duration on purpose

		PriceSchedule monthlyPriceSchedule = new PriceScheduleImpl();
		monthlyPriceSchedule.setType(PriceScheduleType.RECURRING);
		monthlyPriceSchedule.setPaymentSchedule(monthlyPaymentSchedule);

		Price monthlyPrice = new PriceImpl();
		monthlyPrice.setCurrency(CURRENCY_CAD);
		final Money cadMoney = Money.valueOf("22.22", CURRENCY_CAD);
		monthlyPrice.setListPrice(cadMoney);

		pricingScheme = new PricingSchemeImpl();
		pricingScheme.setPriceForSchedule(monthlyPriceSchedule, monthlyPrice);



	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.getCurrency()'.
	 */
	@Test
	public void testGetCurrency() {
		assertEquals("Check get currency", priceImpl.getCurrency(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.setCurrency(Currency)'.
	 */
	@Test
	public void testSetCurrency() {
		priceImpl.setCurrency(CURRENCY_CAD);
		assertSame("Check set currency", priceImpl.getCurrency(), CURRENCY_CAD);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.setDefaultValues()'.
	 */
	@Test
	public void testGetCurrencyFromPriceScheme() {
		final Price priceImpl = new PriceImpl(); //use an empty PriceImpl object
		assertNull(priceImpl.getCurrency());  //make sure currency at the priceImpl level is null
		assertNull(priceImpl.getPriceTiers());

		priceImpl.setPricingScheme(pricingScheme); //add the scheme to the price

		assertEquals(CURRENCY_CAD, priceImpl.getCurrency());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.getListPrice()'.
	 */
	@Test
	public void testGetListPrice() {
		assertEquals("Check get list price", priceImpl.getListPrice(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.setListPrice()'.
	 */
	@Test
	public void testSetListPrice() {
		priceImpl.setCurrency(CURRENCY_CAD);

		final Money cadMoney = Money.valueOf("18.88", CURRENCY_CAD);
		priceImpl.setListPrice(cadMoney);
		Money cadListPrice = priceImpl.getListPrice();
		assertEquals(cadListPrice.getCurrency(), CURRENCY_CAD);
		assertEquals(cadMoney.getAmount(), cadListPrice.getAmount());

		priceImpl.setListPrice(cadMoney, SECOND_TIER_MTY);
		cadListPrice = priceImpl.getListPrice(QTY_6);
		assertEquals(cadListPrice.getCurrency(), CURRENCY_CAD);
		assertEquals(cadMoney.getAmount(), cadListPrice.getAmount());

		final Money cadMoney2 = Money.valueOf("8.88", CURRENCY_CAD);
		priceImpl.setListPrice(cadMoney2, NON_EXIST_TIER_MTY);
		cadListPrice = priceImpl.getListPrice(QTY_100);
		assertEquals(cadListPrice.getCurrency(), CURRENCY_CAD);
		assertEquals(cadMoney2.getAmount(), cadListPrice.getAmount());

	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.getSalePrice()'.
	 */
	@Test
	public void testGetSalePrice() {
		assertEquals("Check get sale price", priceImpl.getSalePrice(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.setSalePrice()'.
	 */
	@Test
	public void testSetSalePrice() {
		final Currency currencyOfCAD = Currency.getInstance(Locale.CANADA);
		priceImpl.setCurrency(currencyOfCAD);

		final Money cadMoney = Money.valueOf("18.88", currencyOfCAD);
		priceImpl.setSalePrice(cadMoney);
		Money cadSalePrice = priceImpl.getSalePrice();
		assertEquals(cadSalePrice.getCurrency(), currencyOfCAD);
		assertEquals(cadMoney.getAmount(), cadSalePrice.getAmount());

		priceImpl.setSalePrice(cadMoney, SECOND_TIER_MTY);
		cadSalePrice = priceImpl.getSalePrice(QTY_6);
		assertEquals(cadSalePrice.getCurrency(), currencyOfCAD);
		assertEquals(cadMoney.getAmount(), cadSalePrice.getAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.getComputedPrice()'.
	 */
	@Test
	public void testGetComputedPrice() {
		assertEquals("Check get computed price", priceImpl.getComputedPrice(), null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.setComputedPriceIfLower()'.
	 */
	@Test
	public void testSetComputedPrice() {
		final Currency currencyOfCAD = Currency.getInstance(Locale.CANADA);
		priceImpl.setCurrency(currencyOfCAD);

		final Money cadMoney = Money.valueOf("18.88", currencyOfCAD);
		priceImpl.setComputedPriceIfLower(cadMoney);
		Money cadComputedPrice = priceImpl.getComputedPrice();
		assertEquals(cadComputedPrice.getCurrency(), currencyOfCAD);
		assertEquals(cadMoney.getAmount(), cadComputedPrice.getAmount());

		priceImpl.setComputedPriceIfLower(cadMoney, SECOND_TIER_MTY);
		cadComputedPrice = priceImpl.getComputedPrice(QTY_6);
		assertEquals(cadComputedPrice.getCurrency(), currencyOfCAD);
		assertEquals(cadMoney.getAmount(), cadComputedPrice.getAmount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.setComputedPriceIfLower()'.
	 */
	@Test
	public void testSetComputedPriceNegative() {
		final Currency currencyOfCAD = Currency.getInstance(Locale.CANADA);
		priceImpl.setCurrency(currencyOfCAD);

		final Money cadMoney = Money.valueOf("-18.88", currencyOfCAD);
		priceImpl.setComputedPriceIfLower(cadMoney);
		Money returnedCadComputedPrice = priceImpl.getComputedPrice();
		assertEquals(returnedCadComputedPrice.getCurrency(), currencyOfCAD);
		assertEquals(0, BigDecimal.ZERO.compareTo(returnedCadComputedPrice.getAmount()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.getMatchPriceTier(minQty)'.
	 */
	@Test
	public void testGetMatchPriceTier() {

		PriceTier priceTier = priceImpl.getPriceTierByExactMinQty(1);
		assertEquals(1, priceTier.getMinQty());

		priceTier = priceImpl.getPriceTierByExactMinQty(SECOND_TIER_MTY);
		assertEquals(QTY_5, priceTier.getMinQty());

		priceTier = priceImpl.getPriceTierByExactMinQty(QTY_6);
		assertEquals(priceTier, null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.getFirstPriceTierMinQty()'.
	 */
	@Test
	public void testGetFirstPriceTierMinQty() {
		assertEquals(1, priceImpl.getFirstPriceTierMinQty());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.hasPriceTiers()'.
	 */
	@Test
	public void testhasPriceTiers() {
		assertTrue(priceImpl.hasPriceTiers());

		priceImpl.setPersistentPriceTiers(null);
		PriceTier priceTier1 = new PriceTierImpl();
		priceTier1.setMinQty(SECOND_TIER_MTY);
		assertFalse(priceImpl.hasPriceTiers());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.AbstractPriceImpl.setDefaultValues()'.
	 */
	@Test
	public void testInitialize() {
		final Price priceImpl = new PriceImpl();
		assertNull(priceImpl.getCurrency());
		assertNull(priceImpl.getPriceTiers());

		priceImpl.initialize();
		// After set default values, the following field should get populated.
		assertNotNull(priceImpl.getPriceTiers());
		assertEquals(0, priceImpl.getPriceTiers().size());
	}

	@Test
	public void verifyGetDiscountRecordsWithQuantityFindsDiscountRecordsOfCorrespondingPriceTier() throws Exception {
		final Price price = new PriceImpl();
		final DiscountRecord discountRecord1 = context.mock(DiscountRecord.class, "Discount Record 1");
		final DiscountRecord discountRecord2 = context.mock(DiscountRecord.class, "Discount Record 2");

		// Given price has a price tier for qty 1 with discount record 1
		final PriceTierImpl priceTier1 = new PriceTierImpl();
		priceTier1.setMinQty(FIRST_TIER_MTY);
		priceTier1.addDiscountRecord(discountRecord1);
		price.addOrUpdatePriceTier(priceTier1);

		// And price has a price tier for qty 5 with discount record 2
		final PriceTierImpl priceTier2 = new PriceTierImpl();
		priceTier2.setMinQty(SECOND_TIER_MTY);
		priceTier2.addDiscountRecord(discountRecord2);
		price.addOrUpdatePriceTier(priceTier2);

		// When I get the Discount Records for qty 5
		final Collection<DiscountRecord> discountRecords = price.getDiscountRecords(SECOND_TIER_MTY);

		// Then I get discount record 2
		assertThat(discountRecords, contains(discountRecord2));
	}

	@Test
	public void verifyGetDiscountRecordsFindsFromFirstPriceTier() throws Exception {
		final Price price = new PriceImpl();
		final DiscountRecord discountRecord1 = context.mock(DiscountRecord.class, "Discount Record 1");
		final DiscountRecord discountRecord2 = context.mock(DiscountRecord.class, "Discount Record 2");

		// Given price has a price tier for qty 1 with discount record 1
		final PriceTierImpl priceTier1 = new PriceTierImpl();
		priceTier1.setMinQty(FIRST_TIER_MTY);
		priceTier1.addDiscountRecord(discountRecord1);
		price.addOrUpdatePriceTier(priceTier1);

		// And price has a price tier for qty 5 with discount record 2
		final PriceTierImpl priceTier2 = new PriceTierImpl();
		priceTier2.setMinQty(SECOND_TIER_MTY);
		priceTier2.addDiscountRecord(discountRecord2);
		price.addOrUpdatePriceTier(priceTier2);

		// When I get the Discount Record without specifying a quantity
		final Collection<DiscountRecord> discountRecords = price.getDiscountRecords();

		// Then I get the first discount record, discount record 1
		assertThat(discountRecords, contains(discountRecord1));
	}

	@Test
	public void verifyGetDiscountsRecordsEmptyWhenNoPriceTiers() throws Exception {
		// Given price with no price tiers
		final Price price = new PriceImpl();

		// When I get the Discount Record without specifying a quantity
		final Collection<DiscountRecord> discountRecords = price.getDiscountRecords();

		// Then no Discount Record is returned
		assertThat(discountRecords, empty());
	}

	@Test
	public void verifyGetDiscountRecordsEmptyWhenNoMatchingPriceTier() throws Exception {
		final Price price = new PriceImpl();
		final DiscountRecord discountRecord = context.mock(DiscountRecord.class);

		// Given price has a price tier for qty 5 with discount record 1
		final PriceTier priceTier1 = new PriceTierImpl();
		priceTier1.setMinQty(SECOND_TIER_MTY);
		priceTier1.addDiscountRecord(discountRecord);
		price.addOrUpdatePriceTier(priceTier1);

		// When I get the Discount Record for qty 1
		final Collection<DiscountRecord> discountRecords = price.getDiscountRecords(FIRST_TIER_MTY);

		// Then no Discount Record is returned
		assertThat(discountRecords, empty());
	}

	@Test
	public void verifyAddDiscountRecordWithoutQtySetsOnFirstPriceTier() throws Exception {
		final Price price = new PriceImpl();
		final DiscountRecord discountRecord = context.mock(DiscountRecord.class);

		// Given price has a price tier for qty 1
		final PriceTierImpl priceTier1 = new PriceTierImpl();
		priceTier1.setMinQty(FIRST_TIER_MTY);
		price.addOrUpdatePriceTier(priceTier1);

		// And price has a price tier for qty 5
		final PriceTierImpl priceTier2 = new PriceTierImpl();
		priceTier2.setMinQty(SECOND_TIER_MTY);
		price.addOrUpdatePriceTier(priceTier2);

		// When I add a discount record without specifying a quantity
		price.addDiscountRecord(discountRecord);

		// Then the discount record is set on the first price tier
		assertThat(priceTier1.getDiscountRecords(), contains(discountRecord));
	}

	@Test
	public void verifyAddDiscountRecordWithQtySetsOnCorrespondingPriceTier() throws Exception {
		final Price price = new PriceImpl();
		final DiscountRecord discountRecord = context.mock(DiscountRecord.class);

		// Given price has a price tier for qty 1
		final PriceTierImpl priceTier1 = new PriceTierImpl();
		priceTier1.setMinQty(FIRST_TIER_MTY);
		price.addOrUpdatePriceTier(priceTier1);

		// And price has a price tier for qty 5
		final PriceTierImpl priceTier2 = new PriceTierImpl();
		priceTier2.setMinQty(SECOND_TIER_MTY);
		price.addOrUpdatePriceTier(priceTier2);

		// When I add a discount record for qty 5
		price.addDiscountRecord(discountRecord, SECOND_TIER_MTY);

		// Then the discount record is set on the second price tier
		assertThat(priceTier2.getDiscountRecords(), contains(discountRecord));
	}

	@Test
	public void verifyClearDiscountRecordsClearsFromAllPriceTiers() throws Exception {
		final Price price = new PriceImpl();
		final DiscountRecord discountRecord1 = context.mock(DiscountRecord.class, "Discount Record 1");
		final DiscountRecord discountRecord2 = context.mock(DiscountRecord.class, "Discount Record 2");

		// Given price has a price tier for qty 1 with discount record 1
		final PriceTierImpl priceTier1 = new PriceTierImpl();
		priceTier1.setMinQty(FIRST_TIER_MTY);
		priceTier1.addDiscountRecord(discountRecord1);
		price.addOrUpdatePriceTier(priceTier1);

		// And price has a price tier for qty 5 with discount record 2
		final PriceTierImpl priceTier2 = new PriceTierImpl();
		priceTier2.setMinQty(SECOND_TIER_MTY);
		priceTier2.addDiscountRecord(discountRecord2);
		price.addOrUpdatePriceTier(priceTier2);

		// When I clear the computed price
		price.clearDiscountRecords();

		// Then the first price tier has no discount record
		assertThat(priceTier1.getDiscountRecords(), empty());

		// And second price tier has no discount record
		assertThat(priceTier2.getDiscountRecords(), empty());
	}

}
