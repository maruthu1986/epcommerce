/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.impl;

import java.util.Date;
import java.util.Objects;


/**
 * Represents the underlying value of a given db field.
 */
public class DataPointValue {

	private static final String UNDERSCORE = "_";

	private Long uidPk = 0L;
	private String customerGuid;
	private String location;
	private String expandedLocation;
	//db field name
	private String field;
	private String dataPointName;
	private String key;
	private String value = "";

	private Date createdDate;
	private Date lastModifiedDate;

	private boolean removable;
	private boolean populated;


	public Long getUidPk() {
		return uidPk;
	}
	public String getLocation() {
		return location;
	}
	public String getField() {
		return field;
	}
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
	public String getCustomerGuid() {
		return customerGuid;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public String getDataPointName() {
		return dataPointName;
	}
	public boolean isRemovable() {
		return removable;
	}
	public boolean isPopulated() {
		return populated;
	}

	public void setUidPk(final Long uidPk) {
		this.uidPk = uidPk;
	}
	public void setCustomerGuid(final String customerGuid) {
		this.customerGuid = customerGuid;
	}
	public void setLocation(final String location) {
		this.location = location;
	}
	public void setField(final String field) {
		this.field = field;
	}
	public void setKey(final String key) {
		this.key = key;
	}
	public void setValue(final String value) {
		this.value = value;
	}
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public void setDataPointName(final String dataPointName) {
		this.dataPointName = dataPointName;
	}
	public void setRemovable(final boolean removable) {
		this.removable = removable;
	}
	public void setPopulated(final boolean populated) {
		this.populated = populated;
	}

	/**
	 * Get expanded location, which is built from location and key fields.
	 *
	 * @return expanded location.
	 */
	public String getExpandedLocation() {
		if (expandedLocation == null) {
			expandedLocation = location + UNDERSCORE + key;
		}
		return expandedLocation;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		DataPointValue that = (DataPointValue) other;
		return Objects.equals(uidPk, that.uidPk)
				&& Objects.equals(dataPointName, that.dataPointName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uidPk, dataPointName);
	}
}
