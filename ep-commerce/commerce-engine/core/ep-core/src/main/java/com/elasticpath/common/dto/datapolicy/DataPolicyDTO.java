/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.common.dto.datapolicy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Data Transfer Object for {@link com.elasticpath.domain.datapolicy.DataPolicy}.
 */
@XmlRootElement(name = DataPolicyDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {})
public class DataPolicyDTO implements Dto {

	/**
	 * Root element name for {@link com.elasticpath.domain.datapolicy.DataPolicy}.
	 */
	public static final String ROOT_ELEMENT = "data_policy";

	private static final long serialVersionUID = 5000000001L;

	@XmlAttribute(required = true)
	private String guid;

	@XmlElement(name = "policy_name", required = true)
	private String policyName;

	@XmlElement(name = "retention_period_in_days", required = true)
	private Integer retentionPeriodInDays;

	@XmlElement(name = "retention_type", required = true)
	private String retentionType;

	@XmlElementWrapper(name = "segments")
	@XmlElement(name = "segment")
	private Set<String> segments = new HashSet<>();

	@XmlElementWrapper(name = "data_points")
	@XmlElement(name = DataPointDTO.ROOT_ELEMENT)
	private List<DataPointDTO> dataPoints = new ArrayList<>();

	@XmlElement(name = "start_date", required = true)
	private Date startDate;

	@XmlElement(name = "end_date")
	private Date endDate;

	@XmlElement(required = true)
	private String state;

	@XmlElement(name = "reference_key", required = true)
	private String referenceKey;

	@XmlElement
	private String description;

	@XmlElementWrapper(name = "activities")
	@XmlElement(name = "activity")
	private Set<String> activities;

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(final String policyName) {
		this.policyName = policyName;
	}

	public Integer getRetentionPeriodInDays() {
		return retentionPeriodInDays;
	}

	public void setRetentionPeriodInDays(final Integer retentionPeriodInDays) {
		this.retentionPeriodInDays = retentionPeriodInDays;
	}

	public void setRetentionType(final String retentionType) {
		this.retentionType = retentionType;
	}

	public String getRetentionType() {
		return retentionType;
	}

	public Set<String> getSegments() {
		return segments;
	}

	public void setSegments(final Set<String> segments) {
		this.segments = segments;
	}

	public List<DataPointDTO> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(final List<DataPointDTO> dataPoints) {
		this.dataPoints = dataPoints;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public String getReferenceKey() {
		return referenceKey;
	}

	public void setReferenceKey(final String referenceKey) {
		this.referenceKey = referenceKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Set<String> getActivities() {
		return activities;
	}

	public void setActivities(final Set<String> activities) {
		this.activities = activities;
	}

	/**
	 * Return the list of data point guids.
	 *
	 * @return list of data point guids
	 */
	public List<String> getDataPointGuids() {
		final List<String> dataPointGuids = getDataPoints().stream()
				.map(DataPointDTO::getGuid)
				.collect(Collectors.toList());

		if (dataPointGuids.isEmpty()) {
			return null;
		}

		return dataPointGuids;
	}
}
