/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tags.service.impl;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.ConditionValidationFacade;
import com.elasticpath.validation.domain.ValidationConstraint;
import com.elasticpath.validation.domain.ValidationResult;
import com.elasticpath.validation.domain.impl.CompoundValidationResult;
import com.elasticpath.validation.service.ValidationService;

/**
 * Service that validates the conditions using validation service.
 */
public class ConditionValidationFacadeImpl implements
		ConditionValidationFacade {

	private ValidationService validationService;
	
	/**
	 * @return validation service that performs the validation.
	 */
	public ValidationService getValidationService() {
		return validationService;
	}

	/**
	 * @param validationService validation service that performs the validation.
	 */
	public void setValidationService(final ValidationService validationService) {
		this.validationService = validationService;
	}

	@Override
	public ValidationResult validate(final Condition condition) throws IllegalArgumentException {
		
		isValidConditionForArgument(condition);
			
		final Collection<ValidationConstraint> constraints = 
			getValidationConstraintsForTagDefinition(condition.getTagDefinition());
	
		return validationService.validate(condition, constraints);
		
	}

	private void isValidConditionForArgument(final Condition condition) {
		if (condition == null || condition.getTagDefinition() == null 
				|| condition.getTagDefinition().getValueType() == null) {
			throw new IllegalArgumentException(
					"Condition, its tag definition and the value type of that definition must not be null");
		}
	}
	
	@Override
	public ValidationResult validate(final Condition condition, final Object newValue) throws IllegalArgumentException {
		
		isValidConditionForArgument(condition);
		
		final Condition conditionWithNewValue = new Condition(condition.getTagDefinition(), condition.getOperator(), newValue);
		
		return validate(conditionWithNewValue);
	}
	
	private Collection<ValidationConstraint> getValidationConstraintsForTagDefinition(final TagDefinition tagDefinition) {
		return tagDefinition.getValueType().getValidationConstraints();
	}

	@Override
	public ValidationResult validateTree(final LogicalOperator logicalOperatorTreeRootNode) throws IllegalArgumentException {
		
		if (logicalOperatorTreeRootNode == null) {
			throw new IllegalArgumentException("Logical Operator must not be null");
		}
		
		final CompoundValidationResult result = new CompoundValidationResult();
		validateTree(logicalOperatorTreeRootNode, result);
		
		return result;
		
	}

	/**
	 * validates a tree/sub tree of a given node and updates the compund result with
	 * outcome of the validation.
	 * @param logicalOperatorTreeNode the root node of tree/ sub tree.
	 * @param result the compound result of the validation of the whole tree.
	 */
	private void validateTree(final LogicalOperator logicalOperatorTreeNode, 
			final CompoundValidationResult result) {
		
		validateConditionTreeNodeConditions(logicalOperatorTreeNode, result);
		
		validateConditionTreeNodeSubNodes(logicalOperatorTreeNode, result);
		
	}

	/**
	 * Provides a recursive call for the collection of sub nodes back to 
	 * {@link #validateTree(LogicalOperator, CompoundValidationResult)}.
	 * @param logicalOperatorTreeNode the root node of tree/ sub tree.
	 * @param result the compound result of the validation of the whole tree.
	 */
	private void validateConditionTreeNodeSubNodes(
			final LogicalOperator logicalOperatorTreeNode,
			final CompoundValidationResult result) {
		final Set<LogicalOperator> subOperators = logicalOperatorTreeNode.getLogicalOperators();
		
		if (CollectionUtils.isNotEmpty(subOperators)) {
			
			for (LogicalOperator logicalOperatorTreeSubNode : subOperators) {
				
				validateTree(logicalOperatorTreeSubNode, result);
				
			}
			
		}
	}

	/**
	 * Validates the conditions of the currently examined tree node.
	 * @param logicalOperatorTreeNode currently examined tree node.
	 * @param result result the compound result of the validation of the whole tree.
	 */
	private void validateConditionTreeNodeConditions(
			final LogicalOperator logicalOperatorTreeNode,
			final CompoundValidationResult result) {
		final Set<Condition> conditions = logicalOperatorTreeNode.getConditions();
		
		if (CollectionUtils.isNotEmpty(conditions)) {
			
			for (Condition condition : conditions) {
			
				final ValidationResult conditionResult = validate(condition);
				result.addValidationResult(conditionResult);
				
			}
			
		}
	}

}
