/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter;

import java.util.List;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;

/**
 * LogicalOperatorModelAdapter is a UI model based on LogicalOperator. 
 * @param <M> model for adapter
 * @param <O> operator object
 */
public interface LogicalOperatorModelAdapter<M, O> extends BaseModelAdapter<M> {

	/** Value for property change events. */
	String LOGICAL_OPERATOR_TYPE = "logicalOperatorType"; //$NON-NLS-1$
	
	/** Value for property change events. */
	String ADD_LOGICAL_OPERATOR = "addLogicalOperator"; //$NON-NLS-1$
	/** Value for property change events. */
	String REMOVE_LOGICAL_OPERATOR = "removeLogicalOperator"; //$NON-NLS-1$
	
	/** Value for property change events. */
	String ADD_CONDITION = "addCondition"; //$NON-NLS-1$
	/** Value for property change events. */
	String REMOVE_CONDITION = "removeCondition"; //$NON-NLS-1$
	
	/**
	 * Set logical operator type.
	 * @param type LogicalOperatorType
	 */
	void setLogicalOperator(O type);

	/**
	 * Get logical operator type.
	 * @return logical operator type.
	 */
	O getLogicalOperator();
	
	/**
	 * Add logical operator as child.
	 * @param logicalOperator logical operator
	 */
	void addLogicalOperator(LogicalOperator logicalOperator);

	/**
	 * Remove logical operator from children.
	 * @param logicalOperator logical operator.
	 */
	void removeLogicalOperator(LogicalOperator logicalOperator);

	/**
	 * Add condition as child.
	 * @param condition Condition
	 */
	void addCondition(Condition condition);

	/**
	 * Remove condition from children.
	 * @param condition Condition
	 */
	void removeCondition(Condition condition);

	/**
	 * Get the logical operators list.
	 * @return list
	 */
	List<O> getLogicalOperatorsList();
	
	/**
	 * Get resource adapter for operator.
	 * @return resource adapter
	 */
	ResourceAdapter<O> getResourceAdapterForOperator();
}
