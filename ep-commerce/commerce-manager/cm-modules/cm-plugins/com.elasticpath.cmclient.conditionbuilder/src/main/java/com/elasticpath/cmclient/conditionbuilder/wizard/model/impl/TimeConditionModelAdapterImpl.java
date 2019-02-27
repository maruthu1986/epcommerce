/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.model.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.BaseModelAdapterImpl;
import com.elasticpath.cmclient.conditionbuilder.wizard.conditions.handlers.ConditionHandler;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.TimeConditionModelAdapter;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;

/**
 * TimeConditionModelAdapterImpl
 * TIME condition model adapter class.
 */
@SuppressWarnings("PMD")
public class TimeConditionModelAdapterImpl extends BaseModelAdapterImpl<LogicalOperator> implements TimeConditionModelAdapter {

	private static final String SHOPPING_START_TIME = "SHOPPING_START_TIME"; //$NON-NLS-1$
	private static final String LESS_THAN = "lessThan"; //$NON-NLS-1$
	private static final String GREATER_THAN = "greaterThan"; //$NON-NLS-1$

	private Date startDate;
	private Date endDate;

	private boolean editorUsage;

	private ConditionHandler conditionHandler = new ConditionHandler();

	
	/**
	 * Default constructor.
	 * @param model LogicalOperator
	 */
	public TimeConditionModelAdapterImpl(final LogicalOperator model) {
		super(model);
		
		this.extractTimes(model);
		this.addPropertyChangeListener(event -> {
			for (Condition condition : new HashSet<>(getModel().getConditions())) {
				getModel().removeCondition(condition);
			}

			if (startDate != null) {
				Long date = startDate.getTime();
				Condition condition = conditionHandler.buildCondition(SHOPPING_START_TIME, GREATER_THAN, date);
				getModel().addCondition(condition);
			}
			if (endDate != null) {
				Long date = endDate.getTime();
				Condition condition = conditionHandler.buildCondition(SHOPPING_START_TIME, LESS_THAN, date);
				getModel().addCondition(condition);
			}
		});
	}


	@Override
	public boolean isEditorUsage() {
		return editorUsage;
	}

	@Override
	public void setEditorUsage(final boolean isEditorUsage) {
		this.editorUsage = isEditorUsage;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public void setEndDate(final Date endDate) {
		Date oldValue = this.endDate;
		this.endDate = endDate;

		this.getPropertyChangeSupport().firePropertyChange(TimeConditionModelAdapter.END_DATE, oldValue, endDate);
	}

	@Override
	public void setStartDate(final Date startDate) {
		Date oldValue = this.startDate;
		this.startDate = startDate;

		this.getPropertyChangeSupport().firePropertyChange(TimeConditionModelAdapter.START_DATE, oldValue, startDate);
	}

	/**
	 * Extract Start date and End date from {@link LogicalOperator}.
	 * 
	 * @param logicalOperator - {@link LogicalOperator}
	 */
	private void extractTimes(final LogicalOperator logicalOperator) {
		Set<Condition> conditions = logicalOperator.getConditions();
		for (Condition condition : conditions) {
			String operator = condition.getOperator();
			
			Date conditionDate = new Date();
			conditionDate.setTime((Long) condition.getTagValue());

			if (GREATER_THAN.equals(operator)) {
				this.setStartDate(conditionDate);
			}
			
			if (LESS_THAN.equals(operator)) {				
				this.setEndDate(conditionDate);
			}
		}
	}
}
