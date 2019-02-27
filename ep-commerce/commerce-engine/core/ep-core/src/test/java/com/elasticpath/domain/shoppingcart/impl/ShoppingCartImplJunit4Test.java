/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.coupon.specifications.PotentialCouponUse;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.impl.CartNFreeSkusActionImpl;
import com.elasticpath.domain.rules.impl.CartSubtotalPercentDiscountActionImpl;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.domain.rules.impl.CouponUsageImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.ShippingAmountDiscountActionImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.specifications.Specification;
import com.elasticpath.domain.store.Store;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.plugin.tax.domain.impl.TaxExemptionImpl;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.impl.RuleValidationResultEnum;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests how {@code ShoppingCartImpl} interacts with {@code ShoppingItem}. New test created instead of
 * modifying {@code ShoppingCartImplTest} because of the latter's complexity and reliance on ElasticPathTestCase.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ShoppingCartImplJunit4Test {

	private static final String DISCOUNT_0_50 = "0.50";
	private static final int QUANTITY_APPLIED_TO_3 = 3;
	private static final int ACTION_UID2 = 5;
	private static final String RANDOM_DISCOUNT_AMOUNT = "12.34";
	private static final int ACTION_UID = 4;
	private static final int RULE_UID = 3;
	private static final String CART_ITEM_1_GUID = "11111";
	private static final String CART_ITEM_2_GUID = "22222";
	private static final Currency CAD = Currency.getInstance(Locale.CANADA);

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	@Mock private ProductSkuLookup productSkuLookup;

	/**
	 * Tests that you can retrieve a shopping cart item from a cart by specifying its GUID.
	 */
	@Test
	public void testGetItemByGuid() {
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		BeanFactoryExpectationsFactory expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRODUCT_SKU_LOOKUP, productSkuLookup);
		try {
			final ShoppingCart shoppingCart = new ShoppingCartImpl();

			final ShoppingItem cartItem1 = context.mock(ShoppingItem.class);
			final ShoppingItem cartItem2 = context.mock(ShoppingItem.class, "cartItem2");

			final ShoppingCartMemento shoppingCartMemento = new ShoppingCartMementoImpl();

			final ProductSku productSku1 = new ProductSkuImpl();
			productSku1.initialize();
			productSku1.setSkuCode("33333");

			final ProductSku productSku2 = new ProductSkuImpl();
			productSku2.initialize();
			productSku2.setSkuCode("44444");

			context.checking(new Expectations() { {
				allowing(beanFactory).getBean("shoppingCartMemento"); will(returnValue(shoppingCartMemento));
				allowing(cartItem1).getSkuGuid(); will(returnValue(productSku1.getGuid()));
				allowing(cartItem2).getSkuGuid(); will(returnValue(productSku2.getGuid()));
				allowing(cartItem1).getGuid(); will(returnValue(CART_ITEM_1_GUID));
				allowing(cartItem2).getGuid(); will(returnValue(CART_ITEM_2_GUID));
				allowing(cartItem1).getOrdering(); will(returnValue(1));
				allowing(cartItem2).getOrdering(); will(returnValue(2));

				allowing(productSkuLookup).findByGuid(productSku1.getGuid());
				will(returnValue(productSku1));
				allowing(productSkuLookup).findByGuid(productSku2.getGuid());
				will(returnValue(productSku2));
			} });

			shoppingCart.addCartItem(cartItem1);
			shoppingCart.addCartItem(cartItem2);

			final String expected = "11111";
			ShoppingItem returnedCartItem1 = shoppingCart.getCartItemByGuid(expected);

			assertEquals("The item found should be the same as the item added.", cartItem1, returnedCartItem1);
		} finally {
			expectationsFactory.close();
		}
	}

	/**
	 * Tests that a discount record can be found for a rule that has one action applied once.
	 */
	@Test
	public void testGetDiscountRecordForRuleAndAction() {
		ShoppingCartImpl shoppingCart = new ShoppingCartImpl();
		ShoppingItem discountedItem = new ShoppingItemImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartNFreeSkusActionImpl();
		ruleAction.setUidPk(ACTION_UID);

		shoppingCart.ruleApplied(RULE_UID, ACTION_UID, discountedItem, new BigDecimal(RANDOM_DISCOUNT_AMOUNT), 2);

		ItemDiscountRecordImpl itemDiscountRecordImpl = (ItemDiscountRecordImpl) shoppingCart.getPromotionRecordContainer()
				.getDiscountRecord(rule, ruleAction);
		assertEquals(new BigDecimal(RANDOM_DISCOUNT_AMOUNT), itemDiscountRecordImpl.getDiscountAmount());
		assertEquals(discountedItem, itemDiscountRecordImpl.getShoppingItem());
		assertEquals(2, itemDiscountRecordImpl.getQuantityAppliedTo());
	}

	/**
	 * Tests that a discount record can be found for a rule that has two actions applied once each.
	 */
	@Test
	public void testGetDiscountRecordForRuleAndActionTwoActions() {
		ShoppingCartImpl shoppingCart = new ShoppingCartImpl();
		ShoppingItem discountedItem = new ShoppingItemImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartNFreeSkusActionImpl();
		ruleAction.setUidPk(ACTION_UID);
		shoppingCart.ruleApplied(RULE_UID, ACTION_UID, discountedItem, new BigDecimal(RANDOM_DISCOUNT_AMOUNT), 2);

		RuleAction ruleAction2 = new CartNFreeSkusActionImpl();
		ruleAction2.setUidPk(ACTION_UID2);
		shoppingCart.ruleApplied(RULE_UID, ACTION_UID2, discountedItem, new BigDecimal("52.34"), QUANTITY_APPLIED_TO_3);

		ItemDiscountRecordImpl itemDiscountRecordImpl = (ItemDiscountRecordImpl) shoppingCart.getPromotionRecordContainer()
				.getDiscountRecord(rule, ruleAction);
		assertEquals(new BigDecimal(RANDOM_DISCOUNT_AMOUNT), itemDiscountRecordImpl.getDiscountAmount());
		assertEquals(discountedItem, itemDiscountRecordImpl.getShoppingItem());
		assertEquals(2, itemDiscountRecordImpl.getQuantityAppliedTo());

		ItemDiscountRecordImpl discountRecord2 = (ItemDiscountRecordImpl) shoppingCart.getPromotionRecordContainer()
				.getDiscountRecord(rule, ruleAction2);
		assertEquals(new BigDecimal("52.34"), discountRecord2.getDiscountAmount());
		assertEquals(discountedItem, discountRecord2.getShoppingItem());
		assertEquals(QUANTITY_APPLIED_TO_3, discountRecord2.getQuantityAppliedTo());
	}

	/**
	 * Tests that a discount record can be found for a rule that has the same action applied from two different
	 * shopping items.
	 */
	@Test
	public void testGetDiscountRecordForRuleAndActionTwoShoppingItems() {
		ShoppingCartImpl shoppingCart = new ShoppingCartImpl();
		ShoppingItem discountedItem = new ShoppingItemImpl();
		ShoppingItem discountedItem2 = new ShoppingItemImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartNFreeSkusActionImpl();
		ruleAction.setUidPk(ACTION_UID);
		shoppingCart.ruleApplied(RULE_UID, ACTION_UID, discountedItem, new BigDecimal(RANDOM_DISCOUNT_AMOUNT), 2);

		shoppingCart.ruleApplied(RULE_UID, ACTION_UID, discountedItem2, new BigDecimal(RANDOM_DISCOUNT_AMOUNT), QUANTITY_APPLIED_TO_3);

		ItemDiscountRecordImpl itemDiscountRecordImpl = (ItemDiscountRecordImpl) shoppingCart.getPromotionRecordContainer()
				.getDiscountRecord(rule, ruleAction);
		assertEquals(new BigDecimal(RANDOM_DISCOUNT_AMOUNT), itemDiscountRecordImpl.getDiscountAmount());
		assertEquals(discountedItem, itemDiscountRecordImpl.getShoppingItem());
		assertEquals(2 + QUANTITY_APPLIED_TO_3, itemDiscountRecordImpl.getQuantityAppliedTo());

	}

	/**
	 * Tests that calling setSubtotalDiscount updates the appliedRuleIds and the discount record.
	 */
	@Test
	public void testSetSubtotalDiscountUpdatesRecords() {
		ShoppingCartImpl shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = -3967509335388001024L;

			@Override
			public BigDecimal getSubtotal() {
				return BigDecimal.ONE;
			}
		};
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartSubtotalPercentDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);

		shoppingCart.setSubtotalDiscount(new BigDecimal(DISCOUNT_0_50), RULE_UID, ACTION_UID);

		assertTrue("ShoppingCart should have applied the rule",
				shoppingCart.getPromotionRecordContainer().getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getPromotionRecordContainer().getDiscountRecord(rule, ruleAction);
		assertNotNull("Should have a discount record", discountRecord);
		assertTrue("Must be for a subtotal", discountRecord instanceof SubtotalDiscountRecordImpl);
	}

	/**
	 * Tests that calling shippingRuleApplied updates the appliedRuleIds and the discount record.
	 */
	@Test
	public void testShippingRuleAppliedUpdatesRecords() {
		ShoppingCartImpl shoppingCart = new ShoppingCartImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new ShippingAmountDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);

		final String shippingOptionCode = "Shipping001";
		shoppingCart.setSelectedShippingOption(createShippingOption(shippingOptionCode));

		Money discountAmount = Money.valueOf(new BigDecimal(DISCOUNT_0_50), Currency.getInstance(Locale.CANADA));
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, RULE_UID, ACTION_UID, discountAmount);

		assertTrue("ShoppingCart should have applied the rule",
				shoppingCart.getPromotionRecordContainer().getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getPromotionRecordContainer().getDiscountRecord(rule, ruleAction);
		assertNotNull("Should have a discount record", discountRecord);
		assertTrue("Must be for a subtotal", discountRecord instanceof ShippingDiscountRecordImpl);
	}

	/**
	 * Tests that calling setSubtotalDiscount updates the appliedRuleIds and the discount record.
	 * The discount which is biggest is the one that should be recorded.
	 */
	@Test
	public void testSetSubtotalDiscountOverrideLowerPrevious() {
		ShoppingCartImpl shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = -7761482988063818208L;

			@Override
			public BigDecimal getSubtotal() {
				return BigDecimal.ONE;
			}
		};
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartSubtotalPercentDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);

		RuleAction ruleAction2 = new CartSubtotalPercentDiscountActionImpl();
		ruleAction2.setUidPk(ACTION_UID2);

		shoppingCart.setSubtotalDiscount(new BigDecimal(DISCOUNT_0_50), RULE_UID, ACTION_UID);

		shoppingCart.setSubtotalDiscount(new BigDecimal("1.00"), RULE_UID, ACTION_UID2);

		assertTrue("ShoppingCart should have applied the rule",
				shoppingCart.getPromotionRecordContainer().getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getPromotionRecordContainer().getDiscountRecord(rule, ruleAction2);
		assertNotNull("Should have a discount record for the biggest discount", discountRecord);
		assertTrue("Must be for a subtotal", discountRecord instanceof SubtotalDiscountRecordImpl);

		DiscountRecord discountRecord2 = shoppingCart.getPromotionRecordContainer().getDiscountRecord(rule, ruleAction);
		assertTrue("Discount record for the smallest discount should be superceded.", discountRecord2.isSuperceded());
	}

	/**
	 * Tests that calling setSubtotalDiscount updates the appliedRuleIds and the discount record.
	 * The discount which is biggest is the one that should be recorded.
	 * This test reverses the order of the set subtotal discount.
	 */
	@Test
	public void testSetSubtotalDiscountNotOverrideHigherPrevious() {
		ShoppingCartImpl shoppingCart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 7390415668349322015L;

			@Override
			public BigDecimal getSubtotal() {
				return BigDecimal.ONE;
			}
		};
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new CartSubtotalPercentDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);

		RuleAction ruleAction2 = new CartSubtotalPercentDiscountActionImpl();
		ruleAction2.setUidPk(ACTION_UID2);

		shoppingCart.setSubtotalDiscount(new BigDecimal("1.00"), RULE_UID, ACTION_UID2);

		shoppingCart.setSubtotalDiscount(new BigDecimal(DISCOUNT_0_50), RULE_UID, ACTION_UID);

		assertTrue("ShoppingCart should have applied the rule",
				shoppingCart.getPromotionRecordContainer().getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getPromotionRecordContainer().getDiscountRecord(rule, ruleAction2);
		assertNotNull("Should have a discount record for the biggest discount", discountRecord);
		assertTrue("Must be for a subtotal", discountRecord instanceof SubtotalDiscountRecordImpl);

		DiscountRecord discountRecord2 = shoppingCart.getPromotionRecordContainer().getDiscountRecord(rule, ruleAction);
		assertTrue("Discount record for the smallest should be superceded (even though applied afterwards).", discountRecord2.isSuperceded());
	}

	/**
	 * Tests that calling shippingRuleApplied updates the appliedRuleIds and the discount record.
	 */
	@Test
	public void testShippingRuleAppliesOverridesPrevious() {
		ShoppingCartImpl shoppingCart = new ShoppingCartImpl();
		Rule rule = new PromotionRuleImpl();
		rule.setUidPk(RULE_UID);
		RuleAction ruleAction = new ShippingAmountDiscountActionImpl();
		ruleAction.setUidPk(ACTION_UID);

		RuleAction ruleAction2 = new ShippingAmountDiscountActionImpl();
		ruleAction2.setUidPk(ACTION_UID2);

		final String shippingOptionCode = "Shipping_Code_001";
		shoppingCart.setSelectedShippingOption(createShippingOption(shippingOptionCode));

		final Money discountAmount = Money.valueOf(new BigDecimal(DISCOUNT_0_50), CAD);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, RULE_UID, ACTION_UID, discountAmount);

		final Money discountAmount2 = Money.valueOf(new BigDecimal("0.25"), CAD);
		shoppingCart.setShippingDiscountIfLower(shippingOptionCode, RULE_UID, ACTION_UID2, discountAmount2);

		assertTrue("ShoppingCart should have applied the rule",
				shoppingCart.getPromotionRecordContainer().getAppliedRules().contains(new Long(RULE_UID)));
		DiscountRecord discountRecord = shoppingCart.getPromotionRecordContainer().getDiscountRecord(rule, ruleAction2);
		assertNotNull("Should have the last discount record", discountRecord);
		assertTrue("Must be for shipping", discountRecord instanceof ShippingDiscountRecordImpl);
		assertTrue("The second discount record should be superseded", discountRecord.isSuperceded());

		DiscountRecord discountRecord2 = shoppingCart.getPromotionRecordContainer().getDiscountRecord(rule, ruleAction);
		assertNotNull("Should have the last discount record", discountRecord2);
		assertTrue("Must be for shipping", discountRecord2 instanceof ShippingDiscountRecordImpl);
		assertFalse("The second discount record should not be superseded since it is a greater discount", discountRecord2.isSuperceded());
	}

	/**
	 * Tests that when shopper is set on the cart for a customer who has a tax exemption ID in their
	 * profile, a new tax exemption object is created on the cart if there wasn't one already.
	 */
	@Test
	public void testSettingShopperCreatesTaxExemptionWhenIdFound() {
		final String taxExemptionId = "MY_EXEMPTION_ID";

		final Shopper shopper = context.mock(Shopper.class);
		final Customer customer = context.mock(Customer.class);
		context.checking(new Expectations() { {
			allowing(shopper).getUidPk(); will(returnValue(1L));

			atLeast(1).of(shopper).getCustomer(); will(returnValue(customer));
			oneOf(customer).getTaxExemptionId(); will(returnValue(taxExemptionId));
		} });

		ShoppingCart cart = new ShoppingCartImpl();
		ShoppingCartMemento memento = new ShoppingCartMementoImpl();
		((ShoppingCartMementoHolder) cart).setShoppingCartMemento(memento);
		cart.setShopper(shopper);

		assertNotNull("The cart should have a tax exemption object", cart.getTaxExemption());
		assertEquals("The tax exemption id should match", taxExemptionId, cart.getTaxExemption().getExemptionId());
	}

	/**
	 * Tests that when a shopper is set on the cart, no tax exemption is created if the customer
	 * doesn't have a tax exemption ID.
	 */
	@Test
	public void testSettingShopperDoesNotCreateTaxExemptionWhenNoIdFound() {
		final Shopper shopper = context.mock(Shopper.class);
		final Customer customer = context.mock(Customer.class);
		context.checking(new Expectations() { {
			allowing(shopper).getUidPk(); will(returnValue(1L));

			atLeast(1).of(shopper).getCustomer(); will(returnValue(customer));
			oneOf(customer).getTaxExemptionId(); will(returnValue(null));
		} });

		ShoppingCart cart = new ShoppingCartImpl();
		ShoppingCartMemento memento = new ShoppingCartMementoImpl();
		((ShoppingCartMementoHolder) cart).setShoppingCartMemento(memento);
		cart.setShopper(shopper);

		assertNull("The cart should not have a tax exemption object", cart.getTaxExemption());
	}

	/**
	 * Tests that if the cart already has a tax exemption it won't be overwritten when
	 * setting the shopper.
	 */
	@Test
	public void testSettingShopperDoesNotOverrideExistingTaxExemption() {
		final String taxExemptionId = "MY_EXEMPTION_ID";

		final Shopper shopper = context.mock(Shopper.class);
		context.checking(new Expectations() { {
			allowing(shopper).getUidPk(); will(returnValue(1L));

			never(shopper).getCustomer();
		} });

		TaxExemption existingExemption = new TaxExemptionImpl();
		existingExemption.setExemptionId("EXISTING_EXEMPTION");

		ShoppingCart cart = new ShoppingCartImpl();
		ShoppingCartMemento memento = new ShoppingCartMementoImpl();
		((ShoppingCartMementoHolder) cart).setShoppingCartMemento(memento);

		cart.setTaxExemption(existingExemption);

		cart.setShopper(shopper);

		assertNotEquals("The tax exemption id should not be the shopper", taxExemptionId, cart.getTaxExemption().getExemptionId());
		assertEquals("The tax exemption id should be the pre-existing one", "EXISTING_EXEMPTION", cart.getTaxExemption().getExemptionId());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testApplyCouponCreatesCouponUseForRegisteredCustomer() {
		final String code = "CODE";
		final String email = "person@email.com";

		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		final CouponConfig couponConfig = context.mock(CouponConfig.class);
		final CouponService couponService = context.mock(CouponService.class);
		final CouponUsageService couponUsageService = context.mock(CouponUsageService.class);
		final Specification<PotentialCouponUse> spec = context.mock(Specification.class);
		final Rule rule = context.mock(Rule.class);
		final RuleService ruleService = context.mock(RuleService.class);
		final Store store = context.mock(Store.class);

		ShoppingCart shoppingCart = getShoppingCart(beanFactory);
		shoppingCart.setStore(store);
		Customer customer = getCustomer(shoppingCart, email);

		Coupon coupon = new CouponImpl();
		coupon.setCouponCode(code);
		coupon.setCouponConfig(couponConfig);

		CouponUsage couponUsage = new CouponUsageImpl();

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.VALID_COUPON_USE_SPEC); will(returnValue(spec));
				allowing(beanFactory).getBean(ContextIdNames.COUPON_USAGE_SERVICE); will(returnValue(couponUsageService));
				allowing(beanFactory).getBean(ContextIdNames.COUPON_USAGE); will(returnValue(couponUsage));
				allowing(beanFactory).getBean(ContextIdNames.COUPON_SERVICE); will(returnValue(couponService));
				allowing(beanFactory).getBean(ContextIdNames.RULE_SERVICE); will(returnValue(ruleService));
				allowing(rule).getUidPk(); will(returnValue(1L));
				allowing(store).getCode(); will(returnValue("STORECODE"));

				oneOf(customer).isAnonymous(); will(returnValue(false));

				oneOf(couponService).findByCouponCode(code); will(returnValue(coupon));
				oneOf(spec).isSatisfiedBy(with(any(PotentialCouponUse.class))); will(returnValue(RuleValidationResultEnum.SUCCESS));
				oneOf(ruleService).getLimitedUseRule(code); will(returnValue(rule));
				oneOf(rule).hasLimitedUseCondition(); will(returnValue(true));
				oneOf(couponConfig).getUsageType(); will(returnValue(CouponUsageType.LIMIT_PER_ANY_USER));
				oneOf(couponUsageService).findByCodeAndType(couponConfig, code, email); will(returnValue(null));
				oneOf(couponUsageService).isValidCouponUsage(email, coupon, null); will(returnValue(true));
				oneOf(couponUsageService).add(couponUsage);
			}
		});

		shoppingCart.applyPromotionCode(code);
		assertEquals("Usage should have email address set", email, couponUsage.getCustomerEmailAddress());
		assertEquals("Usage should have coupon set", coupon, couponUsage.getCoupon());
		assertEquals("Use count should be 0", 0, couponUsage.getUseCount());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testApplyCouponDoesNotCreateCouponUseForAnonymousCustomer() {
		final String code = "CODE";
		final String cortexAnonymousEmail = "public@ep-cortex.com";

		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		final CouponConfig couponConfig = context.mock(CouponConfig.class);
		final CouponService couponService = context.mock(CouponService.class);
		final Rule rule = context.mock(Rule.class);
		final RuleService ruleService = context.mock(RuleService.class);
		final Specification<PotentialCouponUse> spec = context.mock(Specification.class);
		final Store store = context.mock(Store.class);

		ShoppingCart shoppingCart = getShoppingCart(beanFactory);
		shoppingCart.setStore(store);
		Customer customer = getCustomer(shoppingCart, cortexAnonymousEmail);

		Coupon coupon = new CouponImpl();
		coupon.setCouponCode(code);
		coupon.setCouponConfig(couponConfig);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.VALID_COUPON_USE_SPEC); will(returnValue(spec));
				allowing(beanFactory).getBean(ContextIdNames.COUPON_SERVICE); will(returnValue(couponService));
				allowing(beanFactory).getBean(ContextIdNames.RULE_SERVICE); will(returnValue(ruleService));
				allowing(rule).getUidPk(); will(returnValue(1L));
				allowing(store).getCode(); will(returnValue("STORECODE"));

				oneOf(customer).isAnonymous(); will(returnValue(true));

				oneOf(couponService).findByCouponCode(code); will(returnValue(coupon));
				oneOf(spec).isSatisfiedBy(with(any(PotentialCouponUse.class))); will(returnValue(RuleValidationResultEnum.SUCCESS));
				oneOf(ruleService).getLimitedUseRule(code); will(returnValue(rule));
				oneOf(rule).hasLimitedUseCondition(); will(returnValue(true));

				never(beanFactory).getBean(ContextIdNames.COUPON_USAGE_SERVICE);
				never(couponConfig).getUsageType();
			}
		});

		shoppingCart.applyPromotionCode(code);

	}

	private ShoppingCart getShoppingCart(final BeanFactory beanFactory) {
		ShoppingCart cart = new ShoppingCartImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			protected <T> T getBean(final String beanName) {
				return beanFactory.getBean(beanName);
			}
		};

		ShoppingCartMemento memento = new ShoppingCartMementoImpl();
		((ShoppingCartMementoHolder) cart).setShoppingCartMemento(memento);

		return cart;
	}

	private Customer getCustomer(final ShoppingCart shoppingCart, final String email) {
		final Shopper shopper = context.mock(Shopper.class, "couponShopper");
		final Customer customer = context.mock(Customer.class);
		context.checking(new Expectations() {
			{
				allowing(shopper).getCustomer(); will(returnValue(customer));
				allowing(shopper).getUidPk(); will(returnValue(1L));
				allowing(customer).getEmail(); will(returnValue(email));
				ignoring(customer).getTaxExemptionId();
			}
		});

		shoppingCart.setShopper(shopper);
		return customer;
	}

	private ShippingOption createShippingOption(final String shippingOptionCode) {
		final ShippingOptionImpl shippingOption = new ShippingOptionImpl();
		shippingOption.setCode(shippingOptionCode);
		return shippingOption;
	}
}




