/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.domain.rules.Rule;

/**
 * Convenience class to represent the configuration of the Condition Operator menu displayed on Promotions UI dialog windows.
 */
public class ConditionOperatorConfiguration {

	private final Map<String, Boolean> labelToConditionOperatorMapping = new LinkedHashMap<>();

	/**
	 * Public constructor.
	 */
	public ConditionOperatorConfiguration() {
		labelToConditionOperatorMapping.put(PromotionsMessages.get().PromoRulesDefinition_Label_All, Rule.AND_OPERATOR);
		labelToConditionOperatorMapping.put(PromotionsMessages.get().PromoRulesDefinition_Label_Any, Rule.OR_OPERATOR);
	}

	/**
	 * Constructor provided to facilitate testing.
	 *
	 * @param conditionOperatorMapping replaces the default mappings
	 */
	ConditionOperatorConfiguration(final Map<String, Boolean> conditionOperatorMapping) {
		labelToConditionOperatorMapping.putAll(conditionOperatorMapping);
	}

	/**
	 * Returns the ordered list of labels that represent all possible Condition Operator choices.
	 *
	 * @return an ordered list of labels that represent all possible Condition Operator choices.
	 */
	public List<String> getAllLabels() {
		return new ArrayList<>(labelToConditionOperatorMapping.keySet());
	}

	/**
	 * Returns the Condition Operator corresponding to the given index.
	 *
	 * @param index the index by which to identify a Condition Operator
	 * @return the Condition Operator corresponding to the given index.
	 */
	public boolean getConditionOperatorByIndex(final int index) {
		return new ArrayList<>(labelToConditionOperatorMapping.values()).get(index);
	}

	/**
	 * Returns the label corresponding to the given Condition Operator.
	 *
	 * @param conditionOperator the Condition Operator by which to identify a label
	 * @return the label corresponding to the given Condition Operator, or {@code null} if none exists
	 */
	public String getLabelForConditionOperator(final boolean conditionOperator) {
		for (final Entry<String, Boolean> entry : labelToConditionOperatorMapping.entrySet()) {
			if (entry.getValue() == conditionOperator) {
				return entry.getKey();
			}
		}

		return null;
	}

	/**
	 * Returns the default label to be presented to the user.
	 *
	 * @return the default label to be presented to the user; never {@code null}
	 */
	public String getDefaultLabel() {
		return PromotionsMessages.get().PromoRulesDefinition_Label_Any;
	}

	/**
	 * Returns the default Condition Operator to be used. Always corresponds to the label returned by {@link #getDefaultLabel()}.
	 *
	 * @return the default Condition Operator
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getDefaultConditionOperator() {
		return Rule.OR_OPERATOR;
	}

}
