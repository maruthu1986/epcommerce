/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.datapolicy.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Default implementation of <code>DataPolicy</code>.
 */
@Entity
@Table(name = DataPolicyImpl.TABLE_NAME)
@SecondaryTables({
		@SecondaryTable(name = DataPolicyImpl.DATA_POLICY_SEGMENTS),
		@SecondaryTable(name = DataPolicyImpl.DATA_POLICY_ACTIVITIES)
})
@DataCache(enabled = false)
public class DataPolicyImpl extends AbstractEntityImpl implements DataPolicy {

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TDATAPOLICY";

	/**
	 * The name of the data policy segments table & generator to use for segments field persistence.
	 */
	public static final String DATA_POLICY_SEGMENTS = "TDATAPOLICYSEGMENTS";

	/**
	 * The name of the data policy activities table & generator to use for segments field persistence.
	 */
	public static final String DATA_POLICY_ACTIVITIES = "TDATAPOLICYACTIVITIES";

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private long uidPk;

	private String guid;

	private String policyName;

	private Integer retentionPeriodInDays;

	private RetentionType retentionType;

	private Set<String> segments = new HashSet<>();

	private List<DataPoint> dataPoints = new ArrayList<>();

	private Date startDate;

	private Date endDate;

	private DataPolicyState state = DataPolicyState.DRAFT;

	private String description;

	private String referenceKey;

	private Set<String> activities = new HashSet<>();

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Basic
	@Column(name = "GUID", nullable = false, unique = true)
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	@Basic
	@Column(name = "POLICY_NAME", nullable = false)
	public String getPolicyName() {
		return policyName;
	}

	@Override
	public void setPolicyName(final String policyName) {
		this.policyName = policyName;
	}

	@Override
	@Basic
	@Column(name = "RETENTION_PERIOD", nullable = false)
	public Integer getRetentionPeriodInDays() {
		return retentionPeriodInDays;
	}

	@Override
	public void setRetentionPeriodInDays(final Integer retentionPeriodInDays) {
		this.retentionPeriodInDays = retentionPeriodInDays;
	}

	@Override
	@Persistent(optional = false)
	@Externalizer("getOrdinal")
	@Factory("valueOf")
	@Column(name = "RETENTION_TYPE", nullable = false)
	public RetentionType getRetentionType() {
		return retentionType;
	}

	@Override
	public void setRetentionType(final RetentionType retentionType) {
		this.retentionType = retentionType;
	}

	@Override
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(
			name = DATA_POLICY_SEGMENTS,
			joinColumns = @JoinColumn(name = "DATAPOLICY_UID", referencedColumnName = "UIDPK", nullable = false)
	)
	@Column(name = "SEGMENT_NAME")
	public Set<String> getSegments() {
		return segments;
	}

	@Override
	public void setSegments(final Set<String> segments) {
		this.segments = segments;
	}

	@Override
	@ManyToMany(targetEntity = DataPointImpl.class, cascade = CascadeType.ALL,
			fetch = FetchType.EAGER)
	@JoinTable(name = "TDATAPOLICYDATAPOINT",
			joinColumns = @JoinColumn(name = "DATAPOLICY_UID", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "DATAPOINT_UID", nullable = false)
	)
	public List<DataPoint> getDataPoints() {
		return dataPoints;
	}

	@Override
	public void setDataPoints(final List<DataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE", nullable = false)
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	@Override
	@Persistent(optional = false)
	@Externalizer("getOrdinal")
	@Factory("valueOf")
	@Column(name = "STATE", nullable = false)
	public DataPolicyState getState() {
		return state;
	}

	@Override
	public void setState(final DataPolicyState state) {
		this.state = state;
	}

	@Override
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	@Basic
	@Column(name = "REFERENCE_KEY", nullable = false)
	public String getReferenceKey() {
		return referenceKey;
	}

	@Override
	public void setReferenceKey(final String referenceKey) {
		this.referenceKey = referenceKey;
	}

	@Transient
	@Override
	public boolean isEditable() {
		return DataPolicyState.DRAFT.equals(this.state);
	}

	@Transient
	@Override
	public boolean isNotDisabled() {
		return !DataPolicyState.DISABLED.equals(this.state);
	}

	@Override
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(
			name = DATA_POLICY_ACTIVITIES,
			joinColumns = @JoinColumn(name = "DATAPOLICY_UID", referencedColumnName = "UIDPK", nullable = false)
	)
	@Column(name = "ACTIVITY")
	public Set<String> getActivities() {
		return activities;
	}

	@Override
	public void setActivities(final Set<String> activities) {
		this.activities = activities;
	}

	@Override
	public void disable() {
		this.setState(DataPolicyState.DISABLED);
		Date currentTime = new Date();
		if (endDate == null || endDate.after(currentTime)) {
			this.setEndDate(currentTime);
		}
	}
}
