/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.domain.rules.Rule;

import org.eclipse.rap.rwt.testfixture.TestContext;

/**
 * Test class for {@link ConditionOperatorConfiguration}.
 */
public class ConditionOperatorConfigurationTest {

	private Map<String, Boolean> testValueConditionOperatorMapping;

	private static final String KEY_0 = "k1"; //$NON-NLS-1$

	private static final String KEY_1 = "k2"; //$NON-NLS-1$

	private static final boolean VAL_0 = Boolean.FALSE;

	private static final boolean VAL_1 = Boolean.TRUE;

	private ConditionOperatorConfiguration conditionOperatorConfiguration;

	@org.junit.Rule
	public TestContext context = new TestContext();

	/**
	 * Setup for each test method.
	 */
	@Before
	public void setUp() {
		testValueConditionOperatorMapping = new LinkedHashMap<>();
		testValueConditionOperatorMapping.put(KEY_0, VAL_0);
		testValueConditionOperatorMapping.put(KEY_1, VAL_1);

		conditionOperatorConfiguration = new ConditionOperatorConfiguration(testValueConditionOperatorMapping);
	}

	/**
	 * Test method for {@link ConditionOperatorConfiguration#getAllLabels()}. Verifies all labels are returned in the expected order.
	 */
	@Test
	public void testGetAllLabels() {
		final List<String> expected = new ArrayList<>();
		expected.add(KEY_0);
		expected.add(KEY_1);

		assertEquals(expected, conditionOperatorConfiguration.getAllLabels());
	}

	/**
	 * Test method for {@link ConditionOperatorConfiguration#getConditionOperatorByIndex(int)}.
	 */
	@Test
	public void testGetConditionOperatorByIndex() {
		assertEquals(VAL_0, conditionOperatorConfiguration.getConditionOperatorByIndex(0));
		assertEquals(VAL_1, conditionOperatorConfiguration.getConditionOperatorByIndex(1));
	}

	/**
	 * Test method for {@link ConditionOperatorConfiguration#getConditionOperatorByIndex(int)}.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetConditionOperatorByIndexThrowsExceptionForInvalidIndex() {
		int indexThatDoesNotExist = conditionOperatorConfiguration.getAllLabels().size() + 1;
		conditionOperatorConfiguration.getConditionOperatorByIndex(indexThatDoesNotExist);
	}

	/**
	 * Test method for {@link ConditionOperatorConfiguration#getLabelForConditionOperator(boolean)}.
	 */
	@Test
	public void testGetLabelForConditionOperator() {
		assertEquals(KEY_0, conditionOperatorConfiguration.getLabelForConditionOperator(VAL_0));
		assertEquals(KEY_1, conditionOperatorConfiguration.getLabelForConditionOperator(VAL_1));
	}

	/*
	 * Tests to cover real values/expected real behaviour:
	 */

	/**
	 * Test method for {@link ConditionOperatorConfiguration#getAllLabels()}.
	 */
	@Test
	public void testGetAllLabelsReturnsDefaultValues() {
		final List<String> expected = new ArrayList<>();
		expected.add(PromotionsMessages.get().PromoRulesDefinition_Label_All);
		expected.add(PromotionsMessages.get().PromoRulesDefinition_Label_Any);

		assertEquals(expected, new ConditionOperatorConfiguration().getAllLabels());
	}

	/**
	 * Test method for {@link ConditionOperatorConfiguration#getDefaultConditionOperator()}.
	 */
	@Test
	public void testGetDefaultConditionOperator() {
		assertEquals(Rule.OR_OPERATOR, new ConditionOperatorConfiguration().getDefaultConditionOperator());
	}

}
