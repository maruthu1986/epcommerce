/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.sellingcontext.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.rules.impl.RuleValidationResultEnum;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * Test for evaluation capabilities of Selling Context.
 * The names of the Tag Dictionaries GUIDS must be valid since by default
 * the selling context goes through all GUIDS.
 */
public class SellingContextImplTest  {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SellingContext sellingContext;

	private static final String SHOPPER = "SHOPPER";
	private static final String TIME = "TIME";
	private static final String STORE = "STORE";

	private ConditionalExpression exp1;
	private ConditionalExpression exp2;
	private ConditionalExpression exp3;

	private ConditionEvaluatorService mockConditionEvaluatorService;

	private static final TagSet TAG_SET = new TagSet();

	/**
	 * set up objects for test.
	 */
	@Before
	public void setUp() {
		sellingContext = new SellingContextImpl();
		mockConditionEvaluatorService = context.mock(ConditionEvaluatorService.class);
		exp1 = context.mock(ConditionalExpression.class, "exp1");
		exp2 = context.mock(ConditionalExpression.class, "exp2");
		exp3 = context.mock(ConditionalExpression.class, "exp3");
	}

	/**
	 * Tests that isSatisfied method returns true if all conditions are null.
	 */
	@Test
	public void testAllConditionsNull() {
		assertEquals(RuleValidationResultEnum.SUCCESS, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));
	}

	/**
	 * Tests that isSatisfied method returns true if all conditions are evaluated to true.
	 */
	@Test
	public void testAllConditionsTrue() {
		givenThreeConditionsInTheSellingContext(true, true, true);
		assertEquals(RuleValidationResultEnum.SUCCESS, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));
	}

	/**
	 * Tests that isSatisfied method returns false if at least one condition is false.
	 */
	@Test
	public void testAtLeastOneConditionFalse() {
		givenThreeConditionsInTheSellingContext(false, true, true);
		assertEquals(RuleValidationResultEnum.ERROR_UNSPECIFIED, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));

		givenThreeConditionsInTheSellingContext(true, false, true);
		assertEquals(RuleValidationResultEnum.ERROR_UNSPECIFIED, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));

		givenThreeConditionsInTheSellingContext(true, true, false);
		assertEquals(RuleValidationResultEnum.ERROR_UNSPECIFIED, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));

		// TODO: I think the original tester was going for rough coverage of ordering, not
		// exhaustive testing of the &= operator over the 3 expressions.
		// I am ok with this testing approach, but just to call it out:
		// givenThreeConditionsInTheSellingContext(true, true, true);    Tested
		// givenThreeConditionsInTheSellingContext(true, true, false);   Tested
		// givenThreeConditionsInTheSellingContext(true, false, true);   Tested
		// givenThreeConditionsInTheSellingContext(true, false, false);  No
		// givenThreeConditionsInTheSellingContext(false, true, true);   Tested
		// givenThreeConditionsInTheSellingContext(false, true, false);  No
		// givenThreeConditionsInTheSellingContext(false, false, true);  No
		// givenThreeConditionsInTheSellingContext(false, false, false); No
	}

	@Test
	public void testInvalidConditionResultsInNotSatisfied() {
		context.checking(new Expectations() { {
			// Mock a conditional expression for the test.
			allowing(exp1).setTagDictionaryGuid(SHOPPER);
			allowing(exp1).getName();
			will(returnValue("test expr"));

			// Simulate an error when the expression is evaluated.
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp1);
			will(throwException(new EpServiceException("Test failure")));
		} });

		// Set up the expression in the sellingcontext.
		sellingContext.setCondition(TagDictionary.DICTIONARY_SHOPPER_GUID, exp1);

		// Check that the sellingcontext is not satisfied with the expression evaluation results in an error.
		assertFalse("SellingContext should not be satisfied when an expression evaluation error occurs",
				sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET).isSuccess());
	}

	private void givenThreeConditionsInTheSellingContext(final boolean isExpr1Satisfied,
			final boolean isExpr2Satisfied,
			final boolean isExpr3Satisfied) {
		context.checking(new Expectations() { {
			allowing(exp1).setTagDictionaryGuid(SHOPPER);
			allowing(exp2).setTagDictionaryGuid(TIME);
			allowing(exp3).setTagDictionaryGuid(STORE);
			allowing(exp1).getConditionString(); will(returnValue(SHOPPER));
			allowing(exp2).getConditionString(); will(returnValue(TIME));
			allowing(exp3).getConditionString(); will(returnValue(STORE));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp1); will(returnValue(isExpr1Satisfied));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp2); will(returnValue(isExpr2Satisfied));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp3); will(returnValue(isExpr3Satisfied));
		} });

		sellingContext.setCondition(SHOPPER, exp1);
		sellingContext.setCondition(TIME, exp2);
		sellingContext.setCondition(STORE, exp3);
	}
}