/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag;

import java.util.List;

import com.elasticpath.cmclient.conditionbuilder.adapter.LogicalOperatorModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.ResourceAdapter;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;

/**
 * LogicalOperatorModelAdapterImpl is implementation of LogicalOperatorModelAdapter.
 * 
 */
public class LogicalOperatorModelAdapterImpl 
	extends BaseModelAdapterImpl<LogicalOperator> implements LogicalOperatorModelAdapter<LogicalOperator, LogicalOperatorType> {

	private boolean dirty;

	private ResourceAdapter<LogicalOperatorType> resourceAdapterForOperator;
	private List<LogicalOperatorType> logicalOperatorsList;
	
	/**
	 * Default constructor.
	 * @param logicalOperator model
	 */
	public LogicalOperatorModelAdapterImpl(final LogicalOperator logicalOperator) {
		super(logicalOperator);
		this.getPropertyChangeSupport().addPropertyChangeListener(event -> LogicalOperatorModelAdapterImpl.this.dirty = true);
	}

	@Override
	public void setLogicalOperator(final LogicalOperatorType type) {
		LogicalOperatorType oldValue = this.getModel().getOperatorType();
		this.getModel().setOperatorType(type);
		this.getPropertyChangeSupport().firePropertyChange(LogicalOperatorModelAdapter.LOGICAL_OPERATOR_TYPE, oldValue, type);
	}

	// children
	@Override
	public void addLogicalOperator(final LogicalOperator logicalOperator) {
		this.getModel().addLogicalOperator(logicalOperator);
		this.getPropertyChangeSupport().firePropertyChange(LogicalOperatorModelAdapter.ADD_LOGICAL_OPERATOR, null, logicalOperator);
	}

	@Override
	public void removeLogicalOperator(final LogicalOperator logicalOperator) {
		this.getModel().removeLogicalOperand(logicalOperator);
		this.getPropertyChangeSupport().firePropertyChange(LogicalOperatorModelAdapter.REMOVE_LOGICAL_OPERATOR, logicalOperator, null);
	}

	@Override
	public void addCondition(final Condition condition) {
		this.getModel().addCondition(condition);
		this.getPropertyChangeSupport().firePropertyChange(LogicalOperatorModelAdapter.ADD_CONDITION, null, condition);
	}

	@Override
	public void removeCondition(final Condition condition) {
		this.getModel().removeCondition(condition);
		this.getPropertyChangeSupport().firePropertyChange(LogicalOperatorModelAdapter.REMOVE_CONDITION, condition, null);
	}

	/**
	 * Check if model is dirty.
	 * @return the dirty boolean
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Set dirty flag.
	 * @param dirty the dirty to set
	 */
	public void setDirty(final boolean dirty) {
		this.dirty = dirty;
	}

	@Override
	public List<LogicalOperatorType> getLogicalOperatorsList() {
		return this.logicalOperatorsList;
	}

	/**
	 * Set the logical operators list.
	 * @param logicalOperatorsList the logicalOperatorsList to set
	 */
	public void setLogicalOperatorsList(final List<LogicalOperatorType> logicalOperatorsList) {
		this.logicalOperatorsList = logicalOperatorsList;
	}


	@Override
	public ResourceAdapter<LogicalOperatorType> getResourceAdapterForOperator() {
		return this.resourceAdapterForOperator;
	}
	
	/**
	 * Set resource adapter for operator.
	 * @param resourceAdapter resource adapter
	 */
	public void setResourceAdapterForOperator(final ResourceAdapter<LogicalOperatorType> resourceAdapter) {
		this.resourceAdapterForOperator = resourceAdapter;
	}

	@Override
	public LogicalOperatorType getLogicalOperator() {
		return this.getModel().getOperatorType();
	}

}
