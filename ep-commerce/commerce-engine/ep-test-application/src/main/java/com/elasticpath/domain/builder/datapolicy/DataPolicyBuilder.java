/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.builder.datapolicy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.DomainObjectBuilder;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;

/**
 * A builder that builds {@link DataPolicy}s for testing purposes.
 */
public class DataPolicyBuilder implements DomainObjectBuilder<DataPolicy> {

	@Autowired
	private BeanFactory beanFactory;

	private Long uidpk = 0L;
	private String guid;
	private String policyName;
	private Integer retentionPeriodInDays;
	private RetentionType retentionType;
	private Set<String> segments = new HashSet<>();
	private List<DataPoint> dataPoints = new ArrayList<>();
	private Date startDate;
	private Date endDate;
	private DataPolicyState state;
	private String description;
	private String referenceKey;

	/**
	 * Create a new instance.
	 * @return the builder
	 */
	public DataPolicyBuilder newInstance() {
		final DataPolicyBuilder newBuilder = new DataPolicyBuilder();
		newBuilder.setBeanFactory(beanFactory);

		return newBuilder;
	}

	/**
	 * Sets the uidpk for the Data Policy.
	 *
	 * @param uidpk the uidpk
	 * @return the builder
	 */
	public DataPolicyBuilder withUidpk(final Long uidpk) {
		this.uidpk = uidpk;
		return this;
	}

	/**
	 * Sets the guid for the Data Policy.
	 *
	 * @param guid the guid
	 * @return the builder
	 */
	public DataPolicyBuilder withGuid(final String guid) {
		this.guid = guid;
		return this;
	}

	/**
	 * Sets the policy name for the Data Policy.
	 *
	 * @param policyName the policy name
	 * @return the builder
	 */
	public DataPolicyBuilder withPolicyName(final String policyName) {
		this.policyName = policyName;
		return this;
	}

	/**
	 * Sets the retention period in days for the Data Policy.
	 *
	 * @param retentionPeriodInDays the retention period in days
	 * @return the builder
	 */
	public DataPolicyBuilder withRetentionPeriodInDays(final Integer retentionPeriodInDays) {
		this.retentionPeriodInDays = retentionPeriodInDays;
		return this;
	}

	/**
	 * Sets the retention type for the Data Policy.
	 *
	 * @param retentionType the retention type
	 * @return the builder
	 */
	public DataPolicyBuilder withRetentionType(final RetentionType retentionType) {
		this.retentionType = retentionType;
		return this;
	}

	/**
	 * Sets the description for the Data Policy.
	 *
	 * @param description the description
	 * @return the builder
	 */
	public DataPolicyBuilder withDescription(final String description) {
		this.description = description;
		return this;
	}

	/**
	 * Sets the reference key for the Data Policy.
	 *
	 * @param referenceKey the reference key
	 * @return the builder
	 */
	public DataPolicyBuilder withRefernceKey(final String referenceKey) {
		this.referenceKey = referenceKey;
		return this;
	}

	/**
	 * Sets the segments for the Data Policy.
	 *
	 * @param segments the segments
	 * @return the builder
	 */
	public DataPolicyBuilder withSegments(final Set<String> segments) {
		this.segments = segments;
		return this;
	}

	/**
	 * Adds a segment for the Data Policy.
	 *
	 * @param segment the segment
	 * @return the builder
	 */
	public DataPolicyBuilder withSegment(final String segment) {
		this.segments.add(segment);
		return this;
	}

	/**
	 * Sets the data points for the Data Policy.
	 *
	 * @param dataPoints the data points
	 * @return the builder
	 */
	public DataPolicyBuilder withDataPoints(final List<DataPoint> dataPoints) {
		this.dataPoints = dataPoints;
		return this;
	}

	/**
	 * Adds a data point for the Data Policy.
	 *
	 * @param dataPoint the data point
	 * @return the builder
	 */
	public DataPolicyBuilder withDataPoint(final DataPoint dataPoint) {
		this.dataPoints.add(dataPoint);
		return this;
	}

	/**
	 * Sets the start date for the Data Policy.
	 *
	 * @param startDate the start date
	 * @return the builder
	 */
	public DataPolicyBuilder withStartDate(final Date startDate) {
		this.startDate = startDate;
		return this;
	}

	/**
	 * Sets the end date for the Data Policy.
	 *
	 * @param endDate the end date
	 * @return the builder
	 */
	public DataPolicyBuilder withEndDate(final Date endDate) {
		this.endDate = endDate;
		return this;
	}

	/**
	 * Sets the state for the Data Policy.
	 *
	 * @param state the state
	 * @return the builder
	 */
	public DataPolicyBuilder withDataPolicyState(final DataPolicyState state) {
		this.state = state;
		return this;
	}

	@Override
	public DataPolicy build() {
		DataPolicy dataPolicy = beanFactory.getBean(ContextIdNames.DATA_POLICY);
		dataPolicy.setUidPk(uidpk);
		dataPolicy.setGuid(guid);
		dataPolicy.setPolicyName(policyName);
		dataPolicy.setRetentionPeriodInDays(retentionPeriodInDays);
		dataPolicy.setRetentionType(retentionType);
		dataPolicy.setSegments(segments);
		dataPolicy.setDataPoints(dataPoints);
		dataPolicy.setStartDate(startDate);
		dataPolicy.setEndDate(endDate);
		dataPolicy.setState(state);
		dataPolicy.setDescription(description);
		dataPolicy.setReferenceKey(referenceKey);
		return dataPolicy;
	}

	/**
	 * Sets the bean factory for the Data Policy.
	 *
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
