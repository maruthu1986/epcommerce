/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.datapolicy.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Default implementation of <code>DataPoint</code>.
 */
@Entity
@Table(name = DataPointImpl.TABLE_NAME)
public class DataPointImpl extends AbstractEntityImpl implements DataPoint {

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TDATAPOINT";

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	private static final String UNDERSCORE = "_";

	private long uidPk;

	private String guid;

	private String name;

	private String dataLocation;

	private String dataKey;

	private String descriptionKey;

	private boolean removable;

	private String expandedLocation;

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
	public String getName() {
		return name;
	}

	@Override
	@Basic
	@Column(name = "NAME", nullable = false, unique = true)
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	@Basic
	@Column(name = "DATA_LOCATION", nullable = false)
	public String getDataLocation() {
		return this.dataLocation;
	}

	@Override
	public void setDataLocation(final String dataLocation) {
		this.dataLocation = dataLocation;
	}

	@Override
	@Basic
	@Column(name = "DATA_KEY", nullable = false)
	public String getDataKey() {
		return this.dataKey;
	}

	@Override
	public void setDataKey(final String dataKey) {
		this.dataKey = dataKey;
	}

	@Override
	@Basic
	@Column(name = "DESCRIPTION_KEY", nullable = false)
	public String getDescriptionKey() {
		return this.descriptionKey;
	}

	@Override
	public void setDescriptionKey(final String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	@Override
	@Basic
	@Column(name = "REMOVABLE", nullable = false)
	public boolean isRemovable() {
		return removable;
	}

	@Override
	public void setRemovable(final boolean removable) {
		this.removable = removable;
	}

	@Transient
	@Override
	public String getExpandedLocation() {
		if (expandedLocation == null) {
			expandedLocation = dataLocation + UNDERSCORE + dataKey;
		}
		return expandedLocation;
	}
}
