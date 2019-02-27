/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.rules.predicates;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.rules.AppliedRule;

/**
 * Unit tests for {@link PurchaseAppliedRulePredicate}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseAppliedRulePredicateTest  {

	@Mock
	private AppliedRule mockAppliedRule;

	private final PurchaseAppliedRulePredicate predicate = new PurchaseAppliedRulePredicate();

	@Test
	public void testIsSatisfied() {
		boolean result = predicate.isSatisfied(mockAppliedRule);

		assertTrue("Applied rules are always satisfied.", result);
	}

}
