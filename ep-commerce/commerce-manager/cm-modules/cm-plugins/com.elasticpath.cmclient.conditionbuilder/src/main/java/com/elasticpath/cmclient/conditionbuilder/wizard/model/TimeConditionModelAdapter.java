/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.model;

import java.util.Date;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;
import com.elasticpath.tags.domain.LogicalOperator;
/**
 * 
 * Interface for TIME condition wrapper.
 */
public interface TimeConditionModelAdapter extends BaseModelAdapter<LogicalOperator> {

	/** Start date. */
	String START_DATE = "startDate"; //$NON-NLS-1$
	/** End date. */
	String END_DATE = "endDate"; //$NON-NLS-1$
	
	/**
	 * Indicates if this wrapper is used in the condition editor model.
	 * @return true, if it is. Otherwise returns false
	 */
	boolean isEditorUsage();
	
	/**
	 * Sets isEditorUsage flag.
	 * @param isEditorUsage isEditorUsage 
	 */
	void setEditorUsage(boolean isEditorUsage);
	
	/**
	 * * Get the start date that this campaign will become available. * *
	 * 
	 * @return the start date
	 */

	Date getStartDate();

	/**
	 * * Get the end date. After the end date, the campaign will be unavailable. * *
	 * 
	 * @return the end date
	 */
	Date getEndDate();

	/**
	 * Set the start date that this campaign will become available.
	 * 
	 * @param startDate
	 *            the start date
	 */
	void setStartDate(Date startDate);

	/**
	 * Set the end date.
	 * 
	 * @param endDate
	 *            the end date
	 */
	void setEndDate(Date endDate);
	
}
