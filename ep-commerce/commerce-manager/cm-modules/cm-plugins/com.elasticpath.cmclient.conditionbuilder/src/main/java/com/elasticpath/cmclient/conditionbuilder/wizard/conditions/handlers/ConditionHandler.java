/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.conditions.handlers;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.ConditionBuilder;
import com.elasticpath.tags.service.ConditionDSLBuilder;
import com.elasticpath.tags.service.InvalidConditionTreeException;

/**
 * Conditions Handler for DynamicContentDelivery wizard.
 * 
 * 
 */
public class ConditionHandler {

	private static final Logger LOG = Logger.getLogger(ConditionHandler.class);
	
	private final ConditionDSLBuilder conditionDSLBuilder;

	private final ConditionBuilder conditionBuilder;
	
	/**
	 * Default constructor. 
	 */
	public ConditionHandler() {
		this.conditionDSLBuilder = (ConditionDSLBuilder) getBeanByName(ContextIdNames.TAG_CONDITION_DSL_BUILDER);
		this.conditionBuilder = (ConditionBuilder) getBeanByName(ContextIdNames.TAG_CONDITION_BUILDER);
	}

	/**
	 * Convert expression to logical operator.
	 * @param conditionalExpression conditional expression object
	 * @return LogicalOperator
	 */
	public LogicalOperator convertConditionExpressionStringToLogicalOperator(final ConditionalExpression conditionalExpression) {
		return this.convertConditionExpressionStringToLogicalOperator(conditionalExpression, conditionalExpression.getTagDictionaryGuid());
	}
	
	/**
	 * Convert expression to logical operator.
	 * @param conditionalExpression conditional expression object
	 * @param tagDictionaryGuid tag dictionary GUID
	 * @return LogicalOperator
	 */
	public LogicalOperator convertConditionExpressionStringToLogicalOperator(
			final ConditionalExpression conditionalExpression, final String tagDictionaryGuid) {

		LogicalOperator result = null;

		if (conditionalExpression != null) {
			String conditionString = conditionalExpression.getConditionString();
			if (StringUtils.isNotEmpty(conditionString)) {
				try {
					result = conditionDSLBuilder.getLogicalOperationTree(conditionString);
				} catch (final Exception exception) {
					LOG.error("Unable to get logical tree for condition:\n" + conditionString + "\n", exception);  //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		if (result == null) {
			if (TagDictionary.DICTIONARY_STORES_GUID.equalsIgnoreCase(tagDictionaryGuid)) {
				result = new LogicalOperator(LogicalOperatorType.OR);
			} else if (TagDictionary.DICTIONARY_SHOPPER_GUID.equalsIgnoreCase(tagDictionaryGuid)) {
				result = new LogicalOperator(LogicalOperatorType.AND);
				result.addLogicalOperator(new LogicalOperator(LogicalOperatorType.AND, result));
			} else {
				result = new LogicalOperator(LogicalOperatorType.AND);
			}
		}
		return result;
	}

	/**
	* Returns bean by specified name.
	*
	* @param name - name of the bean
	* @return requested bean
	* */
	protected static Object getBeanByName(final String name) {
		return ServiceLocator.getService(name);
	}

	/**
	 * Transfer model object attributes to TAG framework conditions.
	 * @param logicalOperator logical operator
	 * @return expression string
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	public String convertLogicalOperatorToConditionExpressionString(final LogicalOperator logicalOperator) throws InvalidConditionTreeException {

		String conditionString = null;
		
		if (logicalOperator != null && logicalOperator.hasChildren()) {
			conditionString = conditionDSLBuilder.getConditionalDSLString(logicalOperator);
		}
		return conditionString;
	}

	/**
	 * Builds the {@link Condition} object by using given arguments. Downcast the value according to Tag Definition
	 * data type.
	 * 
	 * @param tagDefinitionName the name of the Tag Definition
	 * @param operator the operator
	 * @param value the value of the condition - right operand
	 * @return the built Condition
	 */
	public Condition buildCondition(final String tagDefinitionName, final String operator, final Object value) {
		return this.conditionBuilder.build(tagDefinitionName, operator, value);
	}
	
}