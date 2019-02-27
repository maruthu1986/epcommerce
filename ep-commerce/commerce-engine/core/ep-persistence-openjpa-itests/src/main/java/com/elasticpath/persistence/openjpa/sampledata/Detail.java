/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.openjpa.sampledata;

import java.beans.Transient;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Sample entity class with owned by Master
 */
@Entity
@Table(name = "detail")
public class Detail extends AbstractPersistableImpl implements TransientDataHolder {
	private static final long serialVersionUID = 1L;

	private long uidPk;
	private String name;
	private String transientData;

	@Override
	@Id
	@GeneratedValue
	@Column(name = "uidpk")
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Basic
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	@Transient
	public String getTransientData() {
		return transientData;
	}

	@Override
	public void setTransientData(final String transientData) {
		this.transientData = transientData;
	}
}