/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.datapolicy;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.elasticpath.persistence.api.Entity;

/**
 * Represents Data policy.
 */
public interface DataPolicy extends Entity {

	/**
	 * Return the policy name.
	 *
	 * @return the policy name.
	 */
	String getPolicyName();

	/**
	 * Set the policy name.
	 *
	 * @param policyName the policy name to set.
	 */
	void setPolicyName(String policyName);

	/**
	 * Return the retention period.
	 *
	 * @return the retention period.
	 */
	Integer getRetentionPeriodInDays();

	/**
	 * Set the retention period.
	 *
	 * @param retentionPeriodInDays the retention period to set.
	 */
	void setRetentionPeriodInDays(Integer retentionPeriodInDays);

	/**
	 * Return retention policy.
	 *
	 * @return the retention policy.
	 */
	RetentionType getRetentionType();

	/**
	 * Set the retention policy.
	 *
	 * @param retentionType the retentionType to set.
	 */
	void setRetentionType(RetentionType retentionType);

	/**
	 * Return Data policy segments.
	 *
	 * @return collection of data policy segments.
	 */
	Set<String> getSegments();

	/**
	 * Set Data policy segments.
	 *
	 * @param segments collection of data policy segments to set.
	 */
	void setSegments(Set<String> segments);

	/**
	 * Return Data points.
	 *
	 * @return collection of data points.
	 */
	List<DataPoint> getDataPoints();

	/**
	 * Set Data points.
	 *
	 * @param dataPoints collection of data points to set.
	 */
	void setDataPoints(List<DataPoint> dataPoints);

	/**
	 * Return start date.
	 *
	 * @return start date.
	 */
	Date getStartDate();

	/**
	 * Set start date.
	 *
	 * @param startDate start date to set.
	 */
	void setStartDate(Date startDate);

	/**
	 * Return end date.
	 *
	 * @return end date.
	 */
	Date getEndDate();

	/**
	 * Set end date.
	 *
	 * @param endDate end date to set.
	 */
	void setEndDate(Date endDate);

	/**
	 * Return Data policy state.
	 *
	 * @return Data policy state.
	 */
	DataPolicyState getState();

	/**
	 * Set Data policy state.
	 *
	 * @param state data policy state to set.
	 */
	void setState(DataPolicyState state);

	/**
	 * Return description.
	 *
	 * @return description.
	 */
	String getDescription();

	/**
	 * Set description.
	 *
	 * @param description description to set.
	 */
	void setDescription(String description);

	/**
	 * Return reference key.
	 *
	 * @return reference key.
	 */
	String getReferenceKey();

	/**
	 * Set reference key.
	 *
	 * @param referenceKey reference key to set.
	 */
	void setReferenceKey(String referenceKey);

	/**
	 * Is data policy editable.
	 *
	 * @return returns true if data policy can be edited, otherwise returns false.
	 */
	boolean isEditable();

	/**
	 * Is data policy not disabled.
	 *
	 * @return returns true if data policy is not disable, otherwise returns false.
	 */
	boolean isNotDisabled();

	/**
	 * Return Data policy activities.  These activities serve as attachable tags for data policies.
	 *
	 * @return collection of data policy activities.
	 */
	Set<String> getActivities();

	/**
	 * Set Data policy activities.
	 *
	 * @param activities collection of data policy activities to set.
	 */
	void setActivities(Set<String> activities);

	/**
	 * Sets the data policy's state to {@link DataPolicyState#DISABLED}
	 * If an end date of the policy is not set or end date is in the future - sets it to the current datetime.
	 * Uses application's datetime.
	 */
	void disable();
}
