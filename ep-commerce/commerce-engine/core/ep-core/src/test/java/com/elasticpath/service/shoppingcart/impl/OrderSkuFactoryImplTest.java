/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.impl;

import static com.elasticpath.service.shoppingcart.impl.OrderSkuAsShoppingItemPricingSnapshotAction.returnTheSameOrderSkuAsPricingSnapshot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.SoftAssertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.tax.TaxCodeRetriever;
import com.elasticpath.service.tax.impl.DiscountApportioningCalculatorImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests for OrderSkuFactoryImpl.
 */
public class OrderSkuFactoryImplTest {
	private static final int THREE_INT = 3;

	private static final BigDecimal TWO_CENTS = BigDecimal.valueOf(0.02);

	private static final BigDecimal ONE_CENT = BigDecimal.valueOf(0.01);

	private static final BigDecimal THREE_CENTS = BigDecimal.valueOf(0.03);

	private static final String ID_ONE_TWO = "1.2";

	private static final int TWO = 2;

	private static final int ONE = 1;

	private static final String ID_ONE_ONE_TWO = "1.1.2";

	private static final String ID_ONE = "1";

	private static final String ID_ONE_ONE_ONE = "1.1.1";

	private static final String ID_ONE_ONE = "1.1";

	private static final int FIVE = 5;

	private static final BigDecimal EIGHT_POINT_ONE = new BigDecimal("8.1");

	private static final BigDecimal SEVEN = new BigDecimal(7);

	private static final BigDecimal THREE = new BigDecimal(3);

	private static final BigDecimal NINE = new BigDecimal(9);

	private static final String GUID_SUFIX = "-guid";

	private static final String DEFAULT_DISPLAY_NAME = "DEFAULT_DISPLAY_NAME";

	private static final String SHOPPING_ITEM_GUID = "SHOPPING_ITEM_GUID";

	private static final String TAX_CODE = "TAX_CODE";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock private BeanFactory beanFactory;
	@Mock private ProductSkuLookup productSkuLookup;
	@Mock private ShoppingCartPricingSnapshot cartPricingSnapshot;
	@Mock private ShoppingCartTaxSnapshot cartTaxSnapshot;
	@Mock private TaxCodeRetriever taxCodeRetriever;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private OrderSkuFactoryImpl factory;

	/** */
	@Before
	public void setUp() {
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ORDER_SKU, OrderSkuImpl.class);

		DiscountApportioningCalculatorImpl discountApportioningCalculator = new DiscountApportioningCalculatorImpl();
		discountApportioningCalculator.setProductSkuLookup(productSkuLookup);

		context.checking(new Expectations() {
			{
				allowing(cartTaxSnapshot).getShoppingCartPricingSnapshot();
				will(returnValue(cartPricingSnapshot));

				allowing(cartTaxSnapshot).getShoppingItemTaxSnapshot(with(any(ShoppingItem.class)));
				will(returnTheSameOrderSkuAsPricingSnapshot());

				allowing(cartPricingSnapshot).getShoppingItemPricingSnapshot(with(any(ShoppingItem.class)));
				will(returnTheSameOrderSkuAsPricingSnapshot());
			}
		});
		factory = new OrderSkuFactoryImpl();
		factory.setBeanFactory(beanFactory);
		factory.setDiscountApportioner(discountApportioningCalculator);
		factory.setProductSkuLookup(productSkuLookup);
		factory.setTaxCodeRetriever(taxCodeRetriever);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that data fields are copied correctly from a {@code ShoppingItem} to an OrderSku.
	 */
	@Test
	public void testDataFieldsCopied() {
		final ShoppingItem cartItem = context.mock(ShoppingItem.class);
		final OrderSku orderSku = new OrderSkuImpl();
		final Map<String, String> data = new HashMap<>();
		data.put("KEY1", "VALUE1");
		data.put("KEY2", "VALUE2");

		context.checking(new Expectations() {
			{
				allowing(cartItem).getFields();
				will(returnValue(data));
			}
		});

		factory.copyData(cartItem, orderSku);

		assertEquals(data, orderSku.getFields());
	}

	/** */
	@Test
	public void testExtractRootPricing() {
		ShoppingItem root1 = createShoppingItem(ID_ONE, BigDecimal.ONE, BigDecimal.ONE, 1);
		ShoppingItem root2 = createShoppingItem("2", BigDecimal.ONE, BigDecimal.TEN, 2);

		Map<String, ItemPricing> actualMap = factory.extractRootPricing(Arrays.asList(root1, root2), cartTaxSnapshot);

		assertEquals(2, actualMap.size());

		ItemPricing pricing1 = actualMap.get(ID_ONE);
		assertEquals(BigDecimal.ONE, pricing1.getPrice());
		assertEquals(BigDecimal.ONE, pricing1.getDiscount());

		ItemPricing pricing2 = actualMap.get("2");
		assertEquals(BigDecimal.ONE, pricing2.getPrice());
		assertEquals(BigDecimal.TEN, pricing2.getDiscount());
	}

	/** */
	@Test
	public void testExtractConstituentPricingNestedBundle() {
		ShoppingItem root1 = createShoppingItem(ID_ONE, BigDecimal.ONE, BigDecimal.ONE, 1);
		ProductSku rootSku = productSkuLookup.findByGuid(root1.getSkuGuid());
		rootSku.setProduct(new ProductBundleImpl());
		ShoppingItem child1 = createShoppingItem(ID_ONE_ONE, BigDecimal.ONE, BigDecimal.ONE, 1);
		ProductSku childSku = productSkuLookup.findByGuid(child1.getSkuGuid());
		childSku.setProduct(new ProductBundleImpl());
		ShoppingItem child11 = createShoppingItem(ID_ONE_ONE_ONE, BigDecimal.TEN, BigDecimal.TEN, 1);

		child1.setBundleConstituent(true);
		child11.setBundleConstituent(true);

		root1.addChild(child1);
		child1.addChild(child11);

		Map<String, Map<String, ItemPricing>> actualMap = factory.extractConstituentPricingInOrder(Arrays.asList(root1), cartTaxSnapshot);

		assertEquals(1, actualMap.size());
		Map<String, ItemPricing> const1 = actualMap.get(ID_ONE);

		assertEquals(1, const1.size());

		ItemPricing item11Pricing = const1.get(ID_ONE_ONE);
		assertNull(item11Pricing);

		ItemPricing item111Pricing = const1.get(ID_ONE_ONE_ONE);
		assertEquals(BigDecimal.TEN, item111Pricing.getPrice());
	}

	/** */
	@Test
	public void testExtractConstituentPricing() {
		ShoppingItem root1 = createShoppingItem(ID_ONE, BigDecimal.ONE, BigDecimal.ONE, 1);
		ProductSku rootSku = productSkuLookup.findByGuid(root1.getSkuGuid());
		rootSku.setProduct(new ProductBundleImpl());
		ShoppingItem child1 = createShoppingItem(ID_ONE_ONE, BigDecimal.TEN, BigDecimal.TEN, 1);
		child1.setBundleConstituent(true);

		root1.addChild(child1);

		Map<String, Map<String, ItemPricing>> actualMap = factory.extractConstituentPricingInOrder(Arrays.asList(root1), cartTaxSnapshot);
		Map<String, ItemPricing> const1 = actualMap.get(ID_ONE);
		assertEquals(1, const1.size());
		ItemPricing itemPricing = const1.get(ID_ONE_ONE);
		assertEquals(BigDecimal.TEN, itemPricing.getPrice());
	}

	/** */
	@Test
	public void testExtractConstituentPricingNoBundle() {
		ShoppingItem root1 = createShoppingItem(ID_ONE, BigDecimal.ONE, BigDecimal.ONE, 1);

		Map<String, Map<String, ItemPricing>> actualMap = factory.extractConstituentPricingInOrder(Arrays.asList(root1), cartTaxSnapshot);

		assertEquals(1, actualMap.size());
		Map<String, ItemPricing> const1 = actualMap.get(ID_ONE);
		assertEquals(0, const1.size());
	}

	/** */
	@Test
	public void testItemSortingByAmount() {
		ShoppingItem root1 = createShoppingItem(ID_ONE, BigDecimal.ONE, BigDecimal.ONE, 1);
		ProductSku rootSku = productSkuLookup.findByGuid(root1.getSkuGuid());
		rootSku.setProduct(new ProductBundleImpl());
		ShoppingItem child1 = createShoppingItem(ID_ONE_ONE, BigDecimal.ONE, BigDecimal.ONE, 1);
		ShoppingItem child2 = createShoppingItem(ID_ONE_TWO, BigDecimal.TEN, BigDecimal.TEN, 1);

		child1.setBundleConstituent(true);
		child2.setBundleConstituent(true);

		root1.addChild(child1);
		root1.addChild(child2);

		Map<String, Map<String, ItemPricing>> actualMap = factory.extractConstituentPricingInOrder(Arrays.asList(root1), cartTaxSnapshot);

		Collection<ItemPricing> actualPricing = actualMap.get(ID_ONE).values();
		Iterator<ItemPricing> iterator = actualPricing.iterator();

		ItemPricing child2Pricing = iterator.next();
		assertEquals(BigDecimal.TEN, child2Pricing.getPrice());
	}

	/** */
	@Test
	public void testItemSortingBySkuCode() {
		// sku code == guid
		ShoppingItem root1 = createShoppingItem(ID_ONE, BigDecimal.ONE, BigDecimal.ONE, 1);
		ProductSku rootSku = productSkuLookup.findByGuid(root1.getSkuGuid());
		rootSku.setProduct(new ProductBundleImpl());
		ShoppingItem child1 = createShoppingItem(ID_ONE_ONE, BigDecimal.ONE, BigDecimal.ONE, 1);
		ShoppingItem child2 = createShoppingItem(ID_ONE_TWO, BigDecimal.ONE, BigDecimal.ONE, 1);

		child1.setBundleConstituent(true);
		child2.setBundleConstituent(true);

		root1.addChild(child1);
		root1.addChild(child2);

		Map<String, Map<String, ItemPricing>> actualMap = factory.extractConstituentPricingInOrder(Arrays.asList(root1), cartTaxSnapshot);

		Collection<String> actualKeys = actualMap.get(ID_ONE).keySet();
		Iterator<String> iterator = actualKeys.iterator();

		String child1Pricing = iterator.next();
		assertEquals(ID_ONE_TWO, child1Pricing);
	}

	/**	 */
	@Test
	public void testCreateOrderSkus() {
		factory = new OrderSkuFactoryImpl() {
			@Override
			protected void copyFields(final ShoppingItem shoppingItem, final OrderSku orderSku, final Locale locale) {
				// do nothing
			}

			@Override
			protected void copyPrices(final ShoppingItem item, final OrderSku orderSku, final ShoppingItemTaxSnapshot taxSnapshot) {
				orderSku.setUnitPrice(taxSnapshot.getPricingSnapshot().getLinePricing().getPrice());
			}
		};
		factory.setBeanFactory(beanFactory);
		factory.setProductSkuLookup(productSkuLookup);

		final Map<String, List<ItemPricing>> leavesPricingMap = new HashMap<>();
		leavesPricingMap.put(ID_ONE_ONE_TWO, Arrays.asList(new ItemPricing(EIGHT_POINT_ONE, EIGHT_POINT_ONE, 2)));

		ShoppingItem root = createShoppingItem(ID_ONE, NINE, NINE, FIVE, true);
		ShoppingItem child1 = createShoppingItem(ID_ONE_ONE, THREE, THREE, ONE, true);
		ShoppingItem child12 = createShoppingItem(ID_ONE_ONE_TWO, SEVEN, SEVEN, TWO);

		child1.setBundleConstituent(true);
		child12.setBundleConstituent(true);

		root.addChild(child1);
		child1.addChild(child12);

		Collection<OrderSku> actualSkus =
				factory.createOrderSkusWithApportionedPrices(Arrays.asList(root), leavesPricingMap, cartTaxSnapshot, null);

		assertEquals(1, actualSkus.size());

		OrderSku rootSku = actualSkus.iterator().next();
		assertEquals(NINE, rootSku.getUnitPrice());
		assertEquals(1, rootSku.getBundleItems(productSkuLookup).size());

		OrderSku childSku1 = (OrderSku) rootSku.getBundleItems(productSkuLookup).iterator().next();
		assertEquals(THREE, childSku1.getUnitPrice());
		assertEquals(1, childSku1.getBundleItems(productSkuLookup).size());

		OrderSku childSku12 = (OrderSku) childSku1.getBundleItems(productSkuLookup).iterator().next();
		assertEquals(EIGHT_POINT_ONE, childSku12.getUnitPrice());
		assertEquals(0, childSku12.getBundleItems(productSkuLookup).size());
	}

	/**	 */
	@Test
	public void testSplitByQuantity() {
		Map<String, Map<String, ItemPricing>> pricingMap = new HashMap<>();

		Map<String, ItemPricing> childPricingMap = new HashMap<>();
		ItemPricing pricing = new ItemPricing(THREE_CENTS, THREE_CENTS, 2);
		childPricingMap.put("child", pricing);
		pricingMap.put("root", childPricingMap);

		Map<String, Map<String, List<ItemPricing>>> splitPricingMap = factory.splitByQuantity(pricingMap);

		ItemPricing expected1 = new ItemPricing(ONE_CENT, BigDecimal.ZERO, 1);
		ItemPricing expected2 = new ItemPricing(TWO_CENTS, BigDecimal.ZERO, 1);

		assertEquals(1, splitPricingMap.size());
		Collection<ItemPricing> actualPricings = splitPricingMap.get("root").get("child");
		assertEquals(2, actualPricings.size());

		assertTrue(actualPricings.contains(expected1));
		assertTrue(actualPricings.contains(expected2));
	}

	/**	 */
	@Test
	public void testExtractDiscount() {
		Map<String, ItemPricing> pricingMap = new HashMap<>();
		ItemPricing pricing = new ItemPricing(TWO_CENTS, THREE_CENTS, 2);
		pricingMap.put("1", pricing);

		Map<String, BigDecimal> discountMap = factory.extractDiscount(pricingMap);
		assertEquals(1, discountMap.size());
		assertEquals(THREE_CENTS, discountMap.get("1"));
	}

	/**	 */
	@Test
	public void testApportionDiscount() {

		Map<String, BigDecimal> discountMap = new HashMap<>();
		discountMap.put("root1", THREE_CENTS);

		Map<String, Map<String, List<ItemPricing>>> splitPricingMap = new HashMap<>();

		Map<String, List<ItemPricing>> splitChildPricingMap = new HashMap<>();
		List<ItemPricing> pricings = new ArrayList<>();
		pricings.add(new ItemPricing(TWO_CENTS, null, 2));
		pricings.add(new ItemPricing(ONE_CENT, null, THREE_INT));
		splitChildPricingMap.put("child1", pricings);

		splitPricingMap.put("root1", splitChildPricingMap);

		factory.applyApportionedDiscount(splitPricingMap, discountMap);

		List<ItemPricing> actualPricing = splitPricingMap.get("root1").get("child1");
		ItemPricing pricing1 = new ItemPricing(TWO_CENTS, TWO_CENTS, 2);
		ItemPricing pricing2 = new ItemPricing(ONE_CENT, ONE_CENT, THREE_INT);
		assertTrue(actualPricing.contains(pricing1));
		assertTrue(actualPricing.contains(pricing2));
	}

	/**	 */
	@Test
	public void testApplyDiscount() {
		Map<String, List<ItemPricing>> splitChildPricing = new HashMap<>();
		splitChildPricing.put("child", Arrays.asList(new ItemPricing(null, null, 0), new ItemPricing(null, null, 0)));

		Map<String, BigDecimal> allSplitDiscount = new HashMap<>();
		allSplitDiscount.put("child0", BigDecimal.ONE);
		allSplitDiscount.put("child1", BigDecimal.TEN);

		factory.setApportionedDiscount(splitChildPricing, allSplitDiscount);

		assertEquals(1, splitChildPricing.size());

		List<ItemPricing> actualItemPricing = splitChildPricing.values().iterator().next();
		assertEquals(2, actualItemPricing.size());

		Iterator<ItemPricing> actualItemPricingIterator = actualItemPricing.iterator();
		assertEquals(BigDecimal.ONE, actualItemPricingIterator.next().getDiscount());
		assertEquals(BigDecimal.TEN, actualItemPricingIterator.next().getDiscount());
	}

	/** */
	@Test
	public void testSortByAmount() {
		// given
		Map<String, BigDecimal> input = new HashMap<>();
		input.put("A", BigDecimal.ONE);
		input.put("B", BigDecimal.TEN);

		// test
		Map<String, BigDecimal> result = factory.sortByAmount(input);
		Iterator<BigDecimal> iterator = result.values().iterator();

		assertEquals(BigDecimal.TEN, iterator.next());
		assertEquals(BigDecimal.ONE, iterator.next());
	}

	@Test
	public void verifyNewOrderSkuPopulatedWithAllSuppliedFields() throws Exception {
		final ProductSku sku = context.mock(ProductSku.class);
		final String skuCode = "SKU123";
		final String skuGuid = UUID.randomUUID().toString();
		final int quantity = 1;
		final int ordering = 2;
		final Map<String, String> fields = ImmutableMap.of("Foo", "Bar");
		final TaxCode taxCode = context.mock(TaxCode.class);
		final String taxCodeString = "TAXCODE";
		final BigDecimal unitPrice = TWO_CENTS;
		final Price price = new PriceImpl();
		price.setListPrice(Money.valueOf(unitPrice, Currency.getInstance(Locale.CANADA)), quantity);

		context.checking(new Expectations() {
			{
				allowing(sku).getSkuCode();
				will(returnValue(skuCode));

				allowing(sku).getGuid();
				will(returnValue(skuGuid));

				allowing(taxCodeRetriever).getEffectiveTaxCode(sku);
				will(returnValue(taxCode));

				allowing(taxCode).getCode();
				will(returnValue(taxCodeString));
			}
		});

		final OrderSku orderSku = factory.createOrderSku(sku, price, quantity, ordering, fields);

		assertEquals(skuCode, orderSku.getSkuCode());
		assertEquals(skuGuid, orderSku.getSkuGuid());
		assertEquals(quantity, orderSku.getQuantity());
		assertEquals(ordering, orderSku.getOrdering());
		assertEquals(taxCodeString, orderSku.getTaxCode());
		assertEquals(unitPrice, orderSku.getUnitPrice());
	}

	@Test
	public void testCopyFields() {

		// Given
		final ProductSku productSku = givenProductSkuLookupWillFindSku(ID_ONE);

		givenTaxCodeForSku(TAX_CODE, productSku);

		ShoppingItem shoppingItem = createShoppingItem(SHOPPING_ITEM_GUID, productSku, BigDecimal.ONE, BigDecimal.ONE, 1, true);

		Date expectedCreatedDate = givenOrderSkuCreatedDate();

		OrderSku orderSku = factory.createSimpleOrderSku();

		// When
		factory.copyFields(shoppingItem, orderSku, Locale.CANADA);

		// Then
		SoftAssertions assertSoftly = new SoftAssertions();

		assertSoftly.assertThat(orderSku).isNotNull();
		assertSoftly.assertThat(orderSku.getCreatedDate()).isEqualTo(expectedCreatedDate);
		assertSoftly.assertThat(orderSku.getSkuGuid()).isEqualTo(ID_ONE + GUID_SUFIX);
		assertSoftly.assertThat(orderSku.getSkuCode()).isEqualTo(ID_ONE);
		assertSoftly.assertThat(orderSku.getDigitalAsset()).isNull();
		assertSoftly.assertThat(orderSku.getTaxCode()).isEqualTo(TAX_CODE);
		assertSoftly.assertThat(orderSku.getDisplaySkuOptions()).isEmpty();
		assertSoftly.assertThat(orderSku.getDisplayName()).isEqualTo(DEFAULT_DISPLAY_NAME);
		assertSoftly.assertThat(orderSku.getImage()).isNull();
		assertSoftly.assertThat(orderSku.getOrdering()).isEqualTo(0);
		assertSoftly.assertThat(orderSku.isBundleConstituent()).isTrue();

		assertSoftly.assertAll();

	}

	private void givenTaxCodeForSku(final String expectedTaxCode, final ProductSku productSku) {
		TaxCode taxCode = mock(TaxCode.class);
		when(taxCode.getCode()).thenReturn(expectedTaxCode);

		TaxCodeRetriever taxCodeRetriever = mock(TaxCodeRetriever.class);
		when(taxCodeRetriever.getEffectiveTaxCode(productSku)).thenReturn(taxCode);
		factory.setTaxCodeRetriever(taxCodeRetriever);
	}

	private Date givenOrderSkuCreatedDate() {
		Date expectedCreatedDate = new Date();
		TimeService timeService = mock(TimeService.class);
		when(timeService.getCurrentTime()).thenReturn(expectedCreatedDate);
		factory.setTimeService(timeService);
		return expectedCreatedDate;
	}

	private ShoppingItem createShoppingItem(final String itemGuid, final ProductSku productSku, final BigDecimal totalUnitPrice, final BigDecimal totalUnitDiscount,
											final int quantity, final boolean bundleConstituent) {
		ShoppingItem item = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1950286126608207488L;

			@Override
			public ItemPricing getLinePricing() {
				return new ItemPricing(totalUnitPrice, totalUnitDiscount, quantity);
			}
		};
		item.setGuid(itemGuid);
		item.setSkuGuid(productSku.getGuid());

		item.setBundleConstituent(bundleConstituent);

		return item;
	}

	private ShoppingItem createShoppingItem(final String guid, final BigDecimal totalUnitPrice, final BigDecimal totalUnitDiscount,
			final int quantity, final boolean bundle) {
		final ProductSku sku = new ProductSkuImpl();
		sku.setGuid(guid + GUID_SUFIX);
		sku.setSkuCode(guid);
		if (bundle) {
			sku.setProduct(new ProductBundleImpl());
		}
		givenProductSkuLookupWillFindSku(sku);

		ShoppingItem item = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1950286126608207488L;

			@Override
			public ItemPricing getLinePricing() {
				return new ItemPricing(totalUnitPrice, totalUnitDiscount, quantity);
			}
		};
		item.setGuid(guid);
		item.setSkuGuid(sku.getGuid());

		return item;
	}

	private ProductSku givenProductSkuLookupWillFindSku(final String skuCode) {
		final Product product = mock(Product.class);
		when(product.getDisplayName(Locale.CANADA)).thenReturn(DEFAULT_DISPLAY_NAME);

		final ProductSku sku = new ProductSkuImpl();
		sku.setGuid(skuCode + GUID_SUFIX);
		sku.setSkuCode(skuCode);
		sku.setProduct(product);

		givenProductSkuLookupWillFindSku(sku);

		return sku;
	}

	private void givenProductSkuLookupWillFindSku(final ProductSku sku) {
		context.checking(new Expectations() {
			{
				allowing(productSkuLookup).findByGuid(sku.getGuid());
				will(returnValue(sku));
			}
		});
	}


	private ShoppingItem createShoppingItem(final String guid, final BigDecimal totalUnitPrice, final BigDecimal totalUnitDiscount,
			final int quantity) {
		return createShoppingItem(guid, totalUnitPrice, totalUnitDiscount, quantity, false);
	}
}
