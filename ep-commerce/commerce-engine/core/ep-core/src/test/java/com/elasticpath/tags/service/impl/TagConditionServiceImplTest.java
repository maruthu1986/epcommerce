/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tags.service.impl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.dao.ConditionalExpressionDao;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.impl.ConditionalExpressionImpl;
import com.elasticpath.tags.service.TagConditionService.ConditionalExpressionPredicate;

/**
 * Tests for the TagConditionServiceImpl logic.
 */
public class TagConditionServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ConditionalExpressionDao tagConditionDao;

	private TagConditionServiceImpl tagConditionService;

	private List<ConditionalExpression> expressions;

	/**
	 * Set up the test data.
	 */
	@Before
	public void setUp() {
		tagConditionDao = context.mock(ConditionalExpressionDao.class);

		tagConditionService = new TagConditionServiceImpl();
		tagConditionService.setTagConditionDao(tagConditionDao);

		final ConditionalExpression expression1 = new ConditionalExpressionImpl();
		expression1.initialize();
		expression1.setConditionString("{ AND { AND { CUSTOMER_SEGMENT.includes 'MY_SEG_NAME' }  }  }");

		final ConditionalExpression expression2 = new ConditionalExpressionImpl();
		expression2.initialize();
		expression2.setConditionString("{ AND { AND { CUSTOMER.includes 'WHATEVER' }  }  }");

		expressions = Arrays.asList(expression1, expression2);

		context.checking(new Expectations() { {
			allowing(tagConditionDao).getClass(); will(returnValue(expressions));
		} });
	}

	/**
	 * Test the an always true predicate returns everything.
	 * Also test the count method matches.
	 */
	@Test
	public void testAlwaysTrueExpression() {
		final List<ConditionalExpression> actual = tagConditionService.getMatchingTagConditions(
				new ConditionalExpressionPredicate() {
					@Override
					public boolean apply(final ConditionalExpression conditionalExpression) {
						return true;
					}
				});
		assertThat(actual, is(equalTo(expressions)));
	}

	/**
	 * Test the an always false predicate returns nothing.
	 * Also test the count method matches.
	 */
	@Test
	public void testAlwaysFalseExpression() {
		final ConditionalExpressionPredicate predicate = new ConditionalExpressionPredicate() {
			@Override
			public boolean apply(final ConditionalExpression conditionalExpression) {
				return false;
			}
		};

		final List<ConditionalExpression> actual = tagConditionService.getMatchingTagConditions(predicate);
		assertThat(actual, is(equalTo(Collections.<ConditionalExpression>emptyList())));

		final int count = tagConditionService.countMatchingTagConditions(predicate);
		assertThat(count, is(equalTo(actual.size())));
	}

	/**
	 * Test that passing a null predicate returns nothing.
	 * Also test the count method matches.
	 */
	@Test
	public void testNullPredicate() {
		final List<ConditionalExpression> actual = tagConditionService.getMatchingTagConditions(null);
		assertThat(actual, is(equalTo(Collections.<ConditionalExpression>emptyList())));

		final int count = tagConditionService.countMatchingTagConditions(null);
		assertThat(count, is(equalTo(actual.size())));
	}

	/**
	 * Test the predicates are evaluated properly.
	 * Also test the count method matches.
	 */
	@Test
	public void testPredicateEvaluation() {
		final ConditionalExpressionPredicate predicate = new ConditionalExpressionPredicate() {
			@Override
			public boolean apply(final ConditionalExpression conditionalExpression) {
				if (conditionalExpression.getConditionString().contains("WHATEVER")) {
					return true;
				}
				return false;
			}
		};

		final List<ConditionalExpression> actual = tagConditionService.getMatchingTagConditions(predicate);
		assertThat(actual, is(equalTo(Arrays.asList(expressions.get(1)))));

		final int count = tagConditionService.countMatchingTagConditions(predicate);
		assertThat(count, is(equalTo(actual.size())));
	}

	/**
	 * Test 'countMatchingTagExpressionStrings'.
	 */
	@Test
	public void testCountMatchingTagExpressionStrings() {
		int actual = tagConditionService.countMatchingTagExpressionStrings("(?=.*WHATEVER)(.*(?i)and(?-i)).*");
		assertThat(actual, is(equalTo(1)));

		actual = tagConditionService.countMatchingTagExpressionStrings("(?=.*whatever)(.*(?i)and(?-i)).*");
		assertThat(actual, is(equalTo(0)));
	}

}
