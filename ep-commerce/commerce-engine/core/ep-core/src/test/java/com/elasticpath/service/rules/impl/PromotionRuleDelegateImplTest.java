/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;  // NOPMD

import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static com.elasticpath.test.factory.ShoppingCartStubBuilder.aCart;
import static com.elasticpath.test.factory.ShoppingCartStubBuilder.aShoppingItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerGroupImpl;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.CatalogItemDiscountRecordImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.money.Money;
import com.elasticpath.money.StandardMoneyFormatter;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.rules.PromotionRuleExceptions;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>PromotionRuleDelegateImpl</code>. */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class PromotionRuleDelegateImplTest {

	private static final long PRODUCT_UID = 1234L;

	private static final String PRODUCT_CODE = "1";

	private static final String BRAND_CODE = "brandCode";

	private static final long CUSTOMER_GROUP = 300L;

	private static final long CUSTOMER_UID = 100L;

	private static final long RULE_UID = 123L;

	private static final long ACTION_UID = 456L;

	private static final int TIME_UNIT = 1000000;

	private static final String PRODUCT1_CODE = "2";

	private static final String CATEGORY1_CODE = "123";

	private static final String CAD = "CAD";

	private static final Currency CANADIAN = Currency.getInstance(CAD);

	private static final String SKU_CODE1 = "SkuCode1";

	private static final String AT_LEAST_QUANTIFIER = "AT_LEAST";

	private static final String EXACTLY_QUANTIFIER = "EXACTLY";

	private PromotionRuleDelegateImpl ruleDelegate;

	private static final int QTY_3 = 3;

	private static final int QTY_5 = 5;

	private static final int QTY_10 = 10;

	private static final String DUMMY_EXCEPTION_STR = "CategoryCodes:ProductCodes:ProductSkuCodes:";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	@Mock private ProductSkuLookup productSkuLookup;
	@Mock private ProductService productService;
	@Mock private ShippingOptionService shippingOptionService;
	@Mock private ShippingOptionResult shippingOptionResult;

	/**
	 * Prepare for each test.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PROMOTION_RULE_EXCEPTIONS, PromotionRuleExceptionsImpl.class);

		ruleDelegate = new PromotionRuleDelegateImpl();
		ruleDelegate.setProductSkuLookup(productSkuLookup);
		ruleDelegate.setProductService(productService);
		ruleDelegate.setShippingOptionService(shippingOptionService);
		ruleDelegate.setBeanFactory(beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that if a product is in the given category and not excluded from the rules,
	 * and we want it to be in the category, then catalogProductInCategory will return true.
	 */
	@Test
	public void testCatalogProductInCategoryNotExcludedTrue() {
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();

		final boolean productShouldBeInTheCategory = true;
		assertProductInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is in the given category and not excluded from the rules,
	 * and we DO NOT want it to be in the category, then catalogProductInCategory will return false.
	 */
	@Test
	public void testCatalogProductInCategoryNotExcludedFalse() {
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();

		final boolean productShouldBeInTheCategory = false;
		assertProductNotInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is in the given category but is excluded from the rules,
	 * and we want it to be in the category, then catalogProductInCategory will return false.
	 */
	@Test
	public void testCatalogProductInCategoryExcludedFalse() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return true;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return true;
			}
		};
		final boolean productShouldBeInTheCategory = true;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		assertProductNotInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is in the given category but is excluded from the rules,
	 * and we DO NOT want it to be in the category, then catalogProductInCategory will return true.
	 */
	@Test
	public void testCatalogProductInCategoryExcludedTrue() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return true;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return true;
			}
		};
		final boolean productShouldBeInTheCategory = false;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		assertProductInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is NOT in the given category, but we want it to be, then catalogProductInCategory will
	 * return false.
	 */
	@Test
	public void testCatalogProductInCategoryFalse() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return false;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return false;
			}
		};
		final boolean productShouldBeInTheCategory = true;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		assertProductNotInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is NOT in the given category, and we don't want it to be,
	 * then catalogProductInCategory will return true.
	 */
	@Test
	public void testCatalogProductInCategoryTrue() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return false;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return false;
			}
		};
		final boolean productShouldBeInTheCategory = false;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		assertProductInCategory(testData, productShouldBeInTheCategory);
	}

	/**
	 * Test that if a product is in the given category and not excluded from the rules,
	 * and we want it to be in the category, but product excluded from discount,
	 * then catalogProductInCategory will return false.
	 */
	@Test
	public void testCatalogProductInCategoryProductExcludedFromDiscount() {
		ruleDelegate = new PromotionRuleDelegateImpl() {
			@Override
			PromotionRuleExceptions getPromotionRuleExceptions(final String exceptionStr) {
				return null; //not testing this
			}
			@Override
			boolean isProductExcludedFromRule(final Product product, final PromotionRuleExceptions ruleExceptions) {
				return false;
			}
			@Override
			boolean isProductInCategory(final Product product, final String categoryCode) {
				return true;
			}
		};
		final boolean productShouldBeInTheCategory = true;
		ProductInCategoryTestFixture testData = new ProductInCategoryTestFixture();
		testData.getProduct().getProductType().setExcludedFromDiscount(true);
		assertProductNotInCategory(testData, productShouldBeInTheCategory);
	}

	private void assertProductInCategory(final ProductInCategoryTestFixture testData, final boolean productShouldBeInTheCategory) {
		assertTrue(ruleDelegate.catalogProductInCategory(
				testData.getProduct(), productShouldBeInTheCategory, testData.getCategory().getCompoundGuid(), null));
	}

	private void assertProductNotInCategory(final ProductInCategoryTestFixture testData, final boolean productShouldBeInTheCategory) {
		assertFalse(ruleDelegate.catalogProductInCategory(
				testData.getProduct(), productShouldBeInTheCategory, testData.getCategory().getCompoundGuid(), null));
	}

	/**
	 * Fixture for productInCategory tests, holds the appropriate catalog, category and products.
	 */
	private class ProductInCategoryTestFixture {
		private final Catalog catalog;
		private final Category category;
		private final Product product;

		ProductInCategoryTestFixture() {
			expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER, FetchGroupLoadTunerImpl.class);
			product = new ProductImpl();
			getProduct().setProductType(new ProductTypeImpl());

			catalog = new CatalogImpl();
			catalog.setCode("irrelevant catalog code");
			category = new CategoryImpl();
			category.setCatalog(catalog);
			category.setCode("irrelevent catagory code");
			product.addCategory(category);

			context.checking(new Expectations() { {
				allowing(productService).isInCategory(product, category.getCompoundGuid());
				will(returnValue(true));
			} });
		}

		public Catalog getCatalog() {
			return catalog;
		}

		public Category getCategory() {
			return category;
		}

		public Product getProduct() {
			return product;
		}

	}

	/**
	 * Test the product is condition.
	 */
	@Test
	public void testProductIs() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_GROUP, AttributeGroupImpl.class);

		ProductType productType = new ProductTypeImpl();
		productType.initialize();

		Product product = new ProductImpl();
		product.setUidPk(PRODUCT_UID);
		product.setCode(PRODUCT_CODE);
		product.setProductType(productType);

		assertTrue(ruleDelegate.catalogProductIs(product, true, PRODUCT_CODE, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.catalogProductIs(product, true, PRODUCT_CODE + 1, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.catalogProductIs(product, false, PRODUCT_CODE, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.catalogProductIs(product, false, PRODUCT_CODE + 1, DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test the brand is condition.
	 */
	@Test
	public void testBrandIs() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_GROUP, AttributeGroupImpl.class);

		ProductType productType = new ProductTypeImpl();
		productType.initialize();

		Brand brand = new BrandImpl();
		brand.setCode(BRAND_CODE);

		Product product = new ProductImpl();
		product.setUidPk(PRODUCT_UID);
		product.setBrand(brand);
		product.setProductType(productType);

		assertTrue(ruleDelegate.catalogBrandIs(product, true, BRAND_CODE, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.catalogBrandIs(product, true, BRAND_CODE + 1, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.catalogBrandIs(product, false, BRAND_CODE, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.catalogBrandIs(product, false, BRAND_CODE + 1, DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test for: Reduces the price of a catalog item by the specified percentage if the currency matches.
	 */
	@Test
	public void testApplyCatalogCurrencyDiscountAmount() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		final String discountAmount = "4.00";
		final String computedAmount = "6.00"; // $10 - $4 = $6
		final DiscountRecord expectedDiscountRecord = new CatalogItemDiscountRecordImpl(RULE_UID, ACTION_UID, new BigDecimal(discountAmount));

		List <Price> prices = new ArrayList<>();
		Price price1 = get10Cad();
		Price price2 = get10Cad();
		prices.add(price1);
		prices.add(price2);
		ruleDelegate.applyCatalogCurrencyDiscountAmount(RULE_UID, ACTION_UID, prices, CAD, CAD, discountAmount);
		assertEquals(0, Money.valueOf(computedAmount, CANADIAN).compareTo(price1.getComputedPrice(1)));
		assertEquals(0, Money.valueOf(computedAmount, CANADIAN).compareTo(price2.getComputedPrice(1)));
		assertThat(price1.getDiscountRecords(1), contains(expectedDiscountRecord));
		assertThat(price2.getDiscountRecords(1), contains(expectedDiscountRecord));
	}

	/**
	 *
	 */
	@Test
	public void testDiscountPriceTierByPercent() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final String discountPercentNumber = "40";
		final String discountPercentAmount = "4.00"; // $10 x 40% = $4
		final String computedAmount = "6.00"; // $10 - 40% = $6
		final DiscountRecord expectedDiscountRecord = new CatalogItemDiscountRecordImpl(RULE_UID, ACTION_UID, new BigDecimal(discountPercentAmount));
		Price price = get10Cad();

		// The shopping cart's currency is CAD, so the discount shall be applied
		final BigDecimal discountPercent = ruleDelegate.setDiscountPercentScale(new BigDecimal(discountPercentNumber));
		ruleDelegate.discountPriceByPercent(RULE_UID, ACTION_UID, discountPercent, price);
		assertEquals(Money.valueOf(computedAmount, CANADIAN), price.getComputedPrice(1));
		assertThat(price.getDiscountRecords(1), contains(expectedDiscountRecord));
	}

	private Price get10Cad() {
		Price price = new PriceImpl();
		price.setCurrency(CANADIAN);
		PriceTier priceTier = new PriceTierImpl();
		priceTier.setMinQty(1);
		priceTier.setListPrice(BigDecimal.TEN);
		price.addOrUpdatePriceTier(priceTier);
		return price;
	}


	/**
	 * Test for: Checks if the shopping cart subtotal is at least equal to the specified amount.
	 * Amount in this case is $40.00
	 */
	@Test
	public void testCartSubtotalAtLeast() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final BigDecimal subTotal = new BigDecimal("40.00");

		final DiscountItemContainer discountItemContainer = context.mock(DiscountItemContainer.class);

		context.checking(new Expectations() {
			{
				allowing(discountItemContainer).calculateSubtotalOfDiscountableItemsExcluding(with(any(PromotionRuleExceptions.class)));
				will(returnValue(subTotal));
			}
		});

		assertTrue(ruleDelegate.cartSubtotalAtLeast(discountItemContainer, "0", DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartSubtotalAtLeast(discountItemContainer, "39.99", DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartSubtotalAtLeast(discountItemContainer, "40", DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartSubtotalAtLeast(discountItemContainer, "40.01", DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartSubtotalAtLeast(discountItemContainer, "500", DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test shipping discount amount.
	 */
	@Test
	public void testApplyShippingDiscountAmount() {

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final ShoppingCart shoppingCart = aCart(context)
				.withCurrency(CANADIAN)
				.build();

		final ShippingOption shippingOption = context.mock(ShippingOption.class);
		final DiscountItemContainer discountItemContainer = context.mock(DiscountItemContainer.class);

		final BigDecimal discountAmount = Money.valueOf(BigDecimal.ONE, CANADIAN).getAmount();
		final String shippingOptionCode = "SSLCode001";

		// logic flow driver
		context.checking(new Expectations() {
			{
				oneOf(shippingOption).getCode();
				will(returnValue(shippingOptionCode));
			}
		});

		mockShippingOptionServiceToReturnUnpriced(shoppingCart, singletonList(shippingOption));

		// Actual test expectations
		context.checking(new Expectations() { {
			// The rule must set the shipping discount on the shipping option
			oneOf(discountItemContainer).applyShippingOptionDiscount(shippingOption, RULE_UID, ACTION_UID, discountAmount);
		} });

		this.ruleDelegate.applyShippingDiscountAmount(
				shoppingCart, discountItemContainer, RULE_UID, ACTION_UID, discountAmount.toString(), shippingOptionCode, CANADIAN);
	}

	/**
	 * Test shipping discount amount for selected shipping option in the shopping cart.
	 */
	@Test
	public void testApplyShippingDiscountPercent() {

		//Given
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);

		final ShoppingCart shoppingCart = aCart(context)
				.withCurrency(CANADIAN)
				.build();

		final String shippingOptionCode = "SSLCode002";
		final BigDecimal price = BigDecimal.TEN;
		final String discountPercent = "25";
		final Money shippingDiscountAmount = Money.valueOf("2.50", CANADIAN);

		final ShippingOption shippingOption = context.mock(ShippingOption.class);
		final DiscountItemContainer discountItemContainer = context.mock(DiscountItemContainer.class);

		// logic flow driver
		context.checking(new Expectations() {
			{
				allowing(shippingOption).getCode();
				will(returnValue(shippingOptionCode));

				oneOf(discountItemContainer).getPrePromotionPriceAmount(shippingOption);
				will(returnValue(price));
			}
		});

		mockShippingOptionServiceToReturnUnpriced(shoppingCart, singletonList(shippingOption));

		// Actual test expectations
		context.checking(new Expectations() { {
			// The rule must be recorded as being applied to the cart
			final int expectedScale = 2;
			oneOf(discountItemContainer).applyShippingOptionDiscount(shippingOption,
					RULE_UID, ACTION_UID, shippingDiscountAmount.getAmount().setScale(expectedScale));
		} });

		//When
		this.ruleDelegate.applyShippingDiscountPercent(
				shoppingCart, discountItemContainer, RULE_UID, ACTION_UID, discountPercent, shippingOptionCode, CANADIAN);
	}

	/**
	 * Test for: Checks if the currency of a shopping cart matches the specified currency code.
	 */
	@Test
	public void testCartCurrencyMatches() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);

		final CustomerSession customerSession = context.mock(CustomerSession.class);
		context.checking(new Expectations() {
			{
				allowing(customerSession).getCurrency();
				will(returnValue(CANADIAN));
			}
		});

		final ShoppingCart shoppingCart = aCart(context)
				.withCustomerSession(customerSession)
				.build();

		assertTrue(ruleDelegate.cartCurrencyMatches(shoppingCart, CAD));
		assertFalse(ruleDelegate.cartCurrencyMatches(shoppingCart, "USD"));
	}

	/**
	 * Test for: Checks if the cart contains the specified sku.
	 */
	@Test
	public void testCartContainsSku() {
		final ShoppingCart shoppingCart = aCart(context)
				.with(aShoppingItem(context, productSkuLookup)
							  .withSkuCode(SKU_CODE1)
							  .withQuantity(QTY_5)
							  .withProduct(null)
							  .thatsDiscountable()
							  .thatsNotABundleConstituent())
				.build();

		assertTrue(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, AT_LEAST_QUANTIFIER, 1));
		assertTrue(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, AT_LEAST_QUANTIFIER, QTY_5));
		assertTrue(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, EXACTLY_QUANTIFIER, QTY_5));
		assertFalse(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, EXACTLY_QUANTIFIER, QTY_5 - 1));
		assertFalse(ruleDelegate.cartContainsSku(shoppingCart, SKU_CODE1, AT_LEAST_QUANTIFIER, QTY_5 + 1));
	}

	@Test
	public void verifyCartDoesNotContainSkuWhenNoSuchSkuCode() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(productSkuLookup).findBySkuCode(SKU_CODE1);
				will(returnValue(null));
			}
		});

		assertFalse(ruleDelegate.cartContainsSku(null, SKU_CODE1, null, 1));
	}

	/**
	 * Test for: Checks if the cart contains any sku.
	 */
	@Test
	public void testCartContainsAnySku() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final DiscountItemContainer discountItemContainer = context.mock(DiscountItemContainer.class);

		context.checking(new Expectations() {
			{
				final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
				allowing(shoppingItem).getQuantity();
				will(returnValue(QTY_10));

				allowing(shoppingCart).getAllShoppingItems();
				will(returnValue(singletonList(shoppingItem)));

				allowing(shoppingItem).isBundleConstituent();
				will(returnValue(false));

				allowing(discountItemContainer).cartItemEligibleForPromotion(with(shoppingItem), with(any(PromotionRuleExceptions.class)));
				will(returnValue(true));
			}
		});

		assertTrue(ruleDelegate.cartContainsAnySku(shoppingCart, discountItemContainer, AT_LEAST_QUANTIFIER, 1, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsAnySku(shoppingCart, discountItemContainer, AT_LEAST_QUANTIFIER, QTY_5, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartContainsAnySku(shoppingCart, discountItemContainer, EXACTLY_QUANTIFIER, QTY_5, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartContainsAnySku(shoppingCart, discountItemContainer, EXACTLY_QUANTIFIER, QTY_5 - 1, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsAnySku(shoppingCart, discountItemContainer, AT_LEAST_QUANTIFIER, QTY_5 + 1, DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test for: Checks if the cart contains the specified product.
	 */
	@Test
	public void testCartContainsProduct() {
		Product product = new ProductImpl();
		product.setCode(PRODUCT1_CODE);

		final ShoppingCart shoppingCart = aCart(context)
				.with(aShoppingItem(context, productSkuLookup)
							  .withSkuCode(SKU_CODE1)
							  .withQuantity(QTY_5)
							  .withProduct(product)
							  .thatsDiscountable()
							  .thatsNotABundleConstituent())
				.build();

		assertTrue(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, AT_LEAST_QUANTIFIER, 1, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, AT_LEAST_QUANTIFIER, QTY_5, DUMMY_EXCEPTION_STR));
		assertTrue(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, EXACTLY_QUANTIFIER, QTY_5, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, EXACTLY_QUANTIFIER, QTY_5 - 1, DUMMY_EXCEPTION_STR));
		assertFalse(ruleDelegate.cartContainsProduct(shoppingCart, PRODUCT1_CODE, AT_LEAST_QUANTIFIER, QTY_5 + 1, DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test for: Checks if the cart contains the specified product.
	 */
	@Test
	public void testCartContainsItemsofCategory() {
		final Product product = new ProductImpl();

		ruleDelegate = new PromotionRuleDelegateImpl() {
			// Stub out this method
			@Override
			public boolean catalogProductInCategory(final Product product, final boolean isIn, final String categoryCode, final String exceptionStr) {
				return true;
			}
		};
		ruleDelegate.setBeanFactory(beanFactory);
		ruleDelegate.setProductSkuLookup(productSkuLookup);

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final DiscountItemContainer discountItemContainer = context.mock(DiscountItemContainer.class);

		context.checking(new Expectations() {
			{
				final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
				allowing(shoppingItem).getQuantity();
				will(returnValue(QTY_3 + QTY_5));

				final String skuGuid = UUID.randomUUID().toString();
				allowing(shoppingItem).getSkuGuid();
				will(returnValue(skuGuid));

				allowing(shoppingItem).isBundleConstituent();
				will(returnValue(false));

				final ProductSku sku = context.mock(ProductSku.class);
				allowing(productSkuLookup).findByGuid(skuGuid);
				will(returnValue(sku));

				allowing(sku).getProduct();
				will(returnValue(product));

				allowing(shoppingCart).getAllShoppingItems();
				will(returnValue(singletonList(shoppingItem)));

				allowing(discountItemContainer).cartItemEligibleForPromotion(with(shoppingItem), with(any(PromotionRuleExceptions.class)));
				will(returnValue(true));
			}
		});

		assertTrue(ruleDelegate.cartContainsItemsOfCategory(
				shoppingCart, discountItemContainer, CATEGORY1_CODE, AT_LEAST_QUANTIFIER, 1, DUMMY_EXCEPTION_STR));

		assertTrue(ruleDelegate.cartContainsItemsOfCategory(
				shoppingCart, discountItemContainer, CATEGORY1_CODE, AT_LEAST_QUANTIFIER, QTY_5 + QTY_3, DUMMY_EXCEPTION_STR));

		assertTrue(ruleDelegate.cartContainsItemsOfCategory(
				shoppingCart, discountItemContainer, CATEGORY1_CODE, EXACTLY_QUANTIFIER, QTY_5 + QTY_3, DUMMY_EXCEPTION_STR));

		assertFalse(ruleDelegate.cartContainsItemsOfCategory(
				shoppingCart, discountItemContainer, CATEGORY1_CODE, EXACTLY_QUANTIFIER, QTY_5 + QTY_3 - 1, DUMMY_EXCEPTION_STR));

		assertFalse(ruleDelegate.cartContainsItemsOfCategory(
				shoppingCart, discountItemContainer, CATEGORY1_CODE, AT_LEAST_QUANTIFIER, QTY_5 + QTY_3 + 1, DUMMY_EXCEPTION_STR));
	}

	/**
	 * Test for: Checks if the customer is in a given group.
	 */
	@Test
	public void testCustomerInGroup() {
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		final Shopper shopper = context.mock(Shopper.class);
		final Customer customer = context.mock(Customer.class);
		final List<CustomerGroup> customerGroups = new ArrayList<>();

		context.checking(new Expectations() { {
			allowing(customerSession).getShopper();
			will(returnValue(shopper));

			allowing(shopper).getCustomer();
			will(returnValue(customer));

			allowing(customer).getCustomerGroups();
			will(returnValue(customerGroups));
		} });

		CustomerGroup customerGroup = new CustomerGroupImpl();
		customerGroup.setUidPk(CUSTOMER_GROUP);

		customerGroups.add(customerGroup);

		assertTrue(ruleDelegate.customerInGroup(customerSession, CUSTOMER_GROUP));
		assertFalse(ruleDelegate.customerInGroup(customerSession, CUSTOMER_GROUP - 1));
	}

	/**
	 * Null customers obviously can't be existing customers.
	 */
	@Test
	public void testIsExistingCustomerDoesntExistIfNull() {
		assertFalse(ruleDelegate.isExistingCustomer(null));
	}

	/**
	 * Non persisted users don't count as existing customers.
	 */
	@Test
	public void testIsExistingCustomerDoesntExistIfUsingNonPersistedUidPk() {
		final Customer customer = context.mock(Customer.class);
		context.checking(new Expectations() { {
			oneOf(customer).getUidPk(); will(returnValue(Long.valueOf(0)));
		} });
		assertFalse(ruleDelegate.isExistingCustomer(customer));
	}

	/**
	 * Registered customers are exiting customers.
	 */
	@Test
	public void testIsExistingCustomerDoesExitIfIsRegisteredCustomer() {
		final Customer customer = context.mock(Customer.class);
		context.checking(new Expectations() { {
			oneOf(customer).getUidPk(); will(returnValue(Long.valueOf(CUSTOMER_UID)));
			oneOf(customer).isAnonymous(); will(returnValue(false));
		} });
		assertTrue(ruleDelegate.isExistingCustomer(customer));
	}

	/**
	 * Anonymous customers don't count as existing customers.
	 */
	@Test
	public void testIsExistingCustomerDoesntExitIfIsAnonymousCustomer() {
		final Customer customer = context.mock(Customer.class);
		context.checking(new Expectations() { {
			oneOf(customer).getUidPk(); will(returnValue(Long.valueOf(CUSTOMER_UID)));
			oneOf(customer).isAnonymous(); will(returnValue(true));
		} });
		assertFalse(ruleDelegate.isExistingCustomer(customer));
	}


	/**
	 * Test for <code>checkDateRange()</code>.
	 */
	@Test
	public void testCheckDateRange() {
		Date currentDate = new Date();
		Date beforeNow = new Date(currentDate.getTime() - 1);
		Date afterNow = new Date(currentDate.getTime() + TIME_UNIT);

		assertTrue(ruleDelegate.checkDateRange(String.valueOf(beforeNow.getTime()), String.valueOf(afterNow.getTime())));
		assertTrue(ruleDelegate.checkDateRange(String.valueOf(beforeNow.getTime()), "0"));
		assertFalse(ruleDelegate.checkDateRange(String.valueOf(afterNow.getTime()), String.valueOf(beforeNow.getTime())));
		assertFalse(ruleDelegate.checkDateRange(String.valueOf(beforeNow.getTime() - TIME_UNIT), String.valueOf(beforeNow.getTime())));
		assertTrue(ruleDelegate.checkDateRange("0", "0"));
		assertTrue(ruleDelegate.checkDateRange("0", String.valueOf(afterNow.getTime())));
		assertFalse(ruleDelegate.checkDateRange("0", String.valueOf(beforeNow.getTime())));
	}

	/**
	 * Test for <code>checkEnabled()</code>.
	 */
	@Test
	public void testCheckEnabled() {
		assertTrue(ruleDelegate.checkEnabled("true"));
		assertFalse(ruleDelegate.checkEnabled("false"));
	}

	private void mockShippingOptionServiceToReturnUnpriced(final ShoppingCart shoppingCart, final List<ShippingOption> shippingOptions) {
		mockShippingOptionResultToReturn(shippingOptions);

		context.checking(new Expectations() {
			{
				allowing(shippingOptionService).getShippingOptions(shoppingCart);
				will(returnValue(shippingOptionResult));
				allowing(shoppingCart).getGuid();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void mockShippingOptionResultToReturn(final List<ShippingOption> shippingOptions) {
		context.checking(new Expectations() {
			{
				allowing(shippingOptionResult).isSuccessful();
				will(returnValue(true));

				allowing(shippingOptionResult).throwExceptionIfUnsuccessful(with(any(String.class)), with(any(List.class)));

				allowing(shippingOptionResult).getAvailableShippingOptions();
				will(returnValue(shippingOptions));
			}
		});
	}

} // NOPMD
