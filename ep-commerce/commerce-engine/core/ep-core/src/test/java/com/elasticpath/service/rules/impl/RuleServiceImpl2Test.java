/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.EpRuleBase;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.impl.EpRuleBaseImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.service.ConditionEvaluatorService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>RuleServiceImpl</code>.
 * <code>RuleServiceImplTest</code> is obsolete
 */
@SuppressWarnings("PMD.TooManyMethods")
public class RuleServiceImpl2Test {

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final PersistenceEngine persistenceEngine = context.mock(PersistenceEngine.class);

	private final RuleServiceImpl ruleService = new RuleServiceImpl() {
		@Override
		protected ConditionEvaluatorService getConditionEvaluatorService() {
			return (tags, condition) -> false;
		}
	};

	private static final String FIND_RULE_BY_CODE = "RULE_FIND_BY_CODE";
	private static final String RULE_FIND_BY_COUPON_CODE = "RULE_FIND_BY_COUPON_CODE";
	private static final String RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE = "RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE";
	private static final String RULE_MAP_BY_PROMO_CODES = "RULE_MAP_BY_PROMO_CODES";
	private static final String RULE_ID_FIND_BY_COUPON_CODE = "RULE_ID_FIND_BY_COUPON_CODE";

	private static final String STORE_CODE = "STORE_CODE";
	private static final String PROMO_CODE = "PROMO_CODE";

	/**
	 * Common initialization.
	 */
	@Before
	public void setUp() {
		ruleService.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * <code>RuleService</code> must validate <code>Rule</code> when update,
	 * catch <code>EpDomainException</code> and throw <code>EpServiceException</code> if rule is invalid.
	 */
	@Test(expected = EpServiceException.class)
	public void testUpdate() {
		final Rule mockRule = context.mock(Rule.class);

		context.checking(new Expectations() { {
			oneOf(mockRule).validate(); will(throwException(new EpDomainException("Whatever is incorrect")));
		} });

		ruleService.update(mockRule);
	}

	/**
	 * Checks that if UID is less or equal to 0 then new <code>Rule</code> bean is created.
	 */
	@Test
	public void testLoad() {
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		BeanFactoryExpectationsFactory expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		try {
			final Rule rule = new PromotionRuleImpl();

			context.checking(new Expectations() { {
				exactly(2).of(beanFactory).getBean(with(same(ContextIdNames.PROMOTION_RULE)));
				will(returnValue(rule));
			} });

			assertEquals(rule, ruleService.load(0));
			assertEquals(rule, ruleService.load(-1));
		} finally {
			expectationsFactory.close();
		}
	}

	/**
	 * Checks that if <code>Rule</code> with the given code doens't exist then
	 * <code>RuleService</code> returns null.
	 */
	@Test
	public void testFindByRuleCode() {
		final String ruleCode = "Christmas discount";

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_RULE_BY_CODE, ruleCode);
			will(returnValue(null));
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_RULE_BY_CODE, ruleCode);
			will(returnValue(Collections.emptyList()));
		} });

		assertNull(ruleService.findByRuleCode(ruleCode));
		assertNull(ruleService.findByRuleCode(ruleCode));
	}

	/**
	 * Checks that if there are more then one <code>Rule</code> objects corresponding
	 * to the given code then <code>EpServiceException</code> is thrown.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindByRuleCode2() {
		final String ruleCode = "Progressive discount";

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_RULE_BY_CODE, ruleCode);
			will(returnValue(Arrays.asList(new PromotionRuleImpl(), new PromotionRuleImpl())));
		} });

		ruleService.findByRuleCode(ruleCode);
	}

	/**
	 * Checks successful case of searching for <code>Rule</code> by its code.
	 */
	@Test
	public void testFindByRuleCode3() {
		final String ruleCode = "Progressive discount";
		final Rule promotionRule = new PromotionRuleImpl();

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(FIND_RULE_BY_CODE, ruleCode);
			will(returnValue(Arrays.asList(promotionRule)));
		} });

		assertEquals(promotionRule, ruleService.findByRuleCode(ruleCode));
	}

	/**
	 * <code>RuleService</code> should return empty collection if no one <code>Rule</code> has been found.
	 */
	@Test
	public void testFindByUids() {
		assertEquals(Collections.<Rule>emptyList(), ruleService.findByUids(null));
		assertEquals(Collections.<Rule>emptyList(), ruleService.findByUids(Collections.<Long>emptyList()));
	}

	/**
	 * Checks that Store or Catalog must be provided to find scenario.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindRuleBaseByScenario1() {
		ruleService.findRuleBaseByScenario(null, null, 1);
	}

	/**
	 * Rule base by catalog.
	 * Finds nothing. Should return null.
	 */
	@Test
	public void testFindRuleBaseByScenario2() {
		final Catalog catalog = context.mock(Catalog.class);
		final long catalogUid = 100001L;
		final int scenarioUid = 1;

		context.checking(new Expectations() { {
			oneOf(catalog).getUidPk(); will(returnValue(catalogUid));
			oneOf(persistenceEngine).retrieveByNamedQuery("EP_RULE_BASE_FIND_BY_CATALOG_SCENARIO",
					catalogUid, scenarioUid);
			will(returnValue(null));
		} });

		assertNull(ruleService.findRuleBaseByScenario(null, catalog, scenarioUid));
	}

	/**
	 * Rule base by store.
	 * Query returns more then one <code>Rule</code>. Should throw an exception.
	 */
	@Test(expected = EpServiceException.class)
	public void testFindRuleBaseByScenario3() {
		final Store store = context.mock(Store.class);
		final long storeUid = 10001L;
		final int scenarioUid = 2;

		context.checking(new Expectations() { {
			oneOf(store).getUidPk(); will(returnValue(storeUid));
			oneOf(persistenceEngine).retrieveByNamedQuery("EP_RULE_BASE_FIND_BY_STORE_SCENARIO",
					storeUid, scenarioUid);
			will(returnValue(Arrays.asList(new EpRuleBaseImpl(), new EpRuleBaseImpl())));
		} });

		ruleService.findRuleBaseByScenario(store, null, scenarioUid);
	}

	/**
	 * Both store and catalog are provided.
	 * Successfully finds appropriate <code>Rule</code>.
	 */
	@Test
	public void testFindRuleBaseByScenario4() {
		final EpRuleBase ruleBase = new EpRuleBaseImpl();
		final Store store = context.mock(Store.class);
		final Catalog catalog = context.mock(Catalog.class);
		final long storeUid = 10001L;
		final long catalogUid = 10050L;
		final int scenarioUid = 3;

		context.checking(new Expectations() { {
			oneOf(store).getUidPk(); will(returnValue(storeUid));
			oneOf(catalog).getUidPk(); will(returnValue(catalogUid));
			oneOf(persistenceEngine).retrieveByNamedQuery("EP_RULE_BASE_FIND_BY_STORE_CATALOG_SCENARIO",
					storeUid, catalogUid, scenarioUid);
			will(returnValue(Arrays.asList(ruleBase)));
		} });

		assertEquals(ruleBase, ruleService.findRuleBaseByScenario(store, catalog, scenarioUid));
	}
	
	/**
	 * Verifies that if the query returns more than 1 result that the exception is thrown with the expected message.
	 */
	@Test
	public void testFindChangedStoreRuleBases() {
		final List<EpRuleBase> returnList = new ArrayList<>();
		returnList.add(new EpRuleBaseImpl());
		returnList.add(new EpRuleBaseImpl());

		context.checking(new Expectations() { {
				oneOf(persistenceEngine).retrieveByNamedQuery(
						"EP_RULE_BASE_FIND_CHANGED_STORECODE_SCENARIO",
						"storeCode", 1, null);
				will(returnValue(returnList));
		} });

		boolean expectedExceptionCaught = false;
		try {
			ruleService.findChangedStoreRuleBases("storeCode", 1, null);
		} catch (EpServiceException e) {
			expectedExceptionCaught = true;
			assertEquals(
					"Inconsistent data, found more than 1 item, expected 1 with store code storeCode, no catalog and scenario 1",
					e.getMessage());
		}

		assertTrue("Expect an EpServiceException", expectedExceptionCaught);

	}
	
	@Test
	public void testRuleCodeIsValidWhenExistingAndInStoreAndEnabledAndActiveForDateRange() {
		final Rule rule = context.mock(Rule.class);
		context.checking(new Expectations() {
			{
				allowing(rule).isEnabled();
				will(returnValue(true));
				
				allowing(rule).getStoreCode();
				will(returnValue(STORE_CODE));

				SellingContext sellingContext = context.mock(SellingContext.class);
				allowing(sellingContext).isSatisfied(
						with(Expectations.any(ConditionEvaluatorService.class)),
						with(Expectations.any(TagSet.class)),
						with(Expectations.any(String[].class)));
				will(returnValue(RuleValidationResultEnum.SUCCESS));

				allowing(rule).getSellingContext();
				will(returnValue(sellingContext));
				
				allowing(rule).isWithinDateRange();
				will(returnValue(true));
			}
		});
		
		boolean result = ruleService.isRuleValid(rule, STORE_CODE).isSuccess();
		
		assertTrue("Result should be true if rule is in store, enabled, active, and selling context is satisfied.", result);
	}
	
	@Test
	public void testRuleCodeIsInvalidWhenRuleIsNotWithinDateRange() {
		final Rule rule = context.mock(Rule.class);
		final Store store = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				allowing(rule).isEnabled();
				will(returnValue(true));
				
				allowing(rule).getStoreCode();
				will(returnValue(STORE_CODE));
				
				allowing(rule).getStore();
				will(returnValue(store));

				SellingContext sellingContext = context.mock(SellingContext.class);
				allowing(sellingContext).isSatisfied(
						with(Expectations.any(ConditionEvaluatorService.class)),
						with(Expectations.any(TagSet.class)),
						with(Expectations.any(String[].class)));
				will(returnValue(RuleValidationResultEnum.ERROR_EXPIRED));

				allowing(rule).getSellingContext();
				will(returnValue(sellingContext));
				
				allowing(rule).isWithinDateRange();
				will(returnValue(false));
			}
		});

		RuleValidationResultEnum result = ruleService.isRuleValid(rule, STORE_CODE);
		
		assertEquals("Result should be ERROR_EXPIRED if rule is not within date range.", RuleValidationResultEnum.ERROR_EXPIRED, result);
	}
	
	@Test
	public void testRuleCodeIsInvalidWhenNull() {
		boolean result = ruleService.isRuleValid(null, STORE_CODE).isSuccess();
		
		assertFalse("Null rules are not valid", result);
	}
	
	@Test
	public void testRuleCodeIsInvalidWhenNotInStore() {
		final Rule rule = context.mock(Rule.class);
		context.checking(new Expectations() {
			{
				allowing(rule).isEnabled();
				will(returnValue(true));
				
				allowing(rule).getStoreCode();
				will(returnValue("OTHER_STORE_CODE"));
			}
		});
		
		boolean result = ruleService.isRuleValid(rule, STORE_CODE).isSuccess();
		
		assertFalse("Result should be false if rule is not in store.", result);
	}
	
	@Test
	public void testRuleCodeIsInvalidWhenNotEnabled() {
		final Rule rule = context.mock(Rule.class);
		context.checking(new Expectations() {
			{
				allowing(rule).isEnabled();
				will(returnValue(false));
			}
		});
		
		boolean result = ruleService.isRuleValid(rule, STORE_CODE).isSuccess();
		
		assertFalse("Result should be false if rule is not enabled.", result);
	}

	@Test
	public void shouldReturnRuleByPromoCodeWhenRuleIsUnique() {

		final Long limitedUseConditionUidPk = 1L;

		final Rule expectedPromotionRule = new PromotionRuleImpl();
		expectedPromotionRule.setLimitedUseConditionId(limitedUseConditionUidPk);

		final Rule returnedRule = new PromotionRuleImpl();

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(RULE_FIND_BY_COUPON_CODE, PROMO_CODE);
			will(returnValue(Arrays.asList(returnedRule)));

			oneOf(persistenceEngine).retrieveByNamedQuery(RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
			will(returnValue(Arrays.asList(limitedUseConditionUidPk)));
		} });

		assertEquals(expectedPromotionRule, ruleService.findByPromoCode(PROMO_CODE));
	}

	@Test
	public void shouldReturnNullWhenRuleCanNotBeFoundByPromoCode() {
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(RULE_FIND_BY_COUPON_CODE, PROMO_CODE);
			will(returnValue(Collections.emptyList()));

			never(persistenceEngine).retrieveByNamedQuery(RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
		} });

		assertNull(ruleService.findByPromoCode(PROMO_CODE));
	}

	@Test (expected = DuplicateNameException.class)
	public void shouldThrowExceptionWhenTwoRulesExistForSamePromoCode() {
		final Rule promotionRule = new PromotionRuleImpl();

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(RULE_FIND_BY_COUPON_CODE, PROMO_CODE);
			will(returnValue(Arrays.asList(promotionRule, promotionRule)));

			never(persistenceEngine).retrieveByNamedQuery(RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
		} });

		ruleService.findByPromoCode(PROMO_CODE);
	}

	@Test
	public void shouldReturnNonEmptyMapWhenRuleIsFoundForPromotionCode() {
		final Long limitedUseConditionUidPk = 1L;
		final Collection<String> promoCodes = Arrays.asList(PROMO_CODE);

		final Rule promotionRule = new PromotionRuleImpl();
		promotionRule.setLimitedUseConditionId(limitedUseConditionUidPk);

		final Map<String, Rule> expectedMap = new HashMap<>();
		expectedMap.put(PROMO_CODE, promotionRule);

		final Object[] promoCodeAndRule = new Object[]{PROMO_CODE, promotionRule};

		final List<Object[]> results = new ArrayList<>();
		results.add(promoCodeAndRule);

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQueryWithList(RULE_MAP_BY_PROMO_CODES, "promoCodes", promoCodes);
			will(returnValue(results));

			oneOf(persistenceEngine).retrieveByNamedQuery(RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
			will(returnValue(Arrays.asList(limitedUseConditionUidPk)));
		} });

		assertEquals("Map must contain a pair of promotion code-rule", expectedMap, ruleService.getLimitedUseRulesByPromotionCodes(promoCodes));
	}

	@Test
	public void shouldReturnEmptMapWhenRuleIsNotFound() {
		final Collection<String> promoCodes = Arrays.asList(PROMO_CODE);

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQueryWithList(RULE_MAP_BY_PROMO_CODES, "promoCodes", promoCodes);
			will(returnValue(Collections.emptyList()));

			never(persistenceEngine).retrieveByNamedQuery(RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
		} });

		assertTrue("Map should be empty", ruleService.getLimitedUseRulesByPromotionCodes(promoCodes).isEmpty());
	}


	@Test
	public void shouldReturnLimitedUseRuleByPromoCodeWhenRuleIsUnique() {
		final Long limitedUseConditionUidPk = 1L;
		final Long ruleUidPk = 1L;

		final Rule expectedPromotionRule = new PromotionRuleImpl();
		expectedPromotionRule.setUidPk(ruleUidPk);
		expectedPromotionRule.setLimitedUseConditionId(limitedUseConditionUidPk);

		final Rule returnedPromotionRule = new PromotionRuleImpl();
		returnedPromotionRule.setUidPk(ruleUidPk);

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(RULE_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
			will(returnValue(Arrays.asList(returnedPromotionRule)));

			oneOf(persistenceEngine).retrieveByNamedQuery(RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
			will(returnValue(Arrays.asList(limitedUseConditionUidPk)));
		} });

		assertEquals(returnedPromotionRule, ruleService.getLimitedUseRule(PROMO_CODE));
	}

	@Test
	public void shouldReturnNullWhenLimitedUseRuleCanNotBeFoundByPromoCode() {
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(RULE_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
			will(returnValue(Collections.emptyList()));

			never(persistenceEngine).retrieveByNamedQuery(RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
		} });

		assertNull("Returned Rule must be null if not found in db", ruleService.getLimitedUseRule(PROMO_CODE));
	}

	@Test (expected = DuplicateNameException.class)
	public void shouldThrowExceptionWhenTwoLimitedUseRulesExistForSamePromoCode() {
		final Rule promotionRule = new PromotionRuleImpl();

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery(RULE_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
			will(returnValue(Arrays.asList(promotionRule, promotionRule)));

			never(persistenceEngine).retrieveByNamedQuery(RULE_LIMITED_USE_CONDITION_ID_FIND_BY_COUPON_CODE, PROMO_CODE);
		} });

		ruleService.getLimitedUseRule(PROMO_CODE);
	}

}
