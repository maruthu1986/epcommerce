/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.openjpa.sampledata;

import java.beans.Transient;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Sample domain class with a one-to-many relationship to {@link Detail} and both persistent and transient fields.
 */
@Entity
@Table(name = "master")
public class Master extends AbstractPersistableImpl implements TransientDataHolder {
	private static final long serialVersionUID = 1L;

	private long uidPk;
	private String name;
	private String transientData;
	private Set<Detail> details;

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

	@OneToMany(targetEntity = Detail.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@ElementJoinColumn(name = "master_uid", updatable = false)
	@ElementForeignKey
	@ElementDependent
	public Set<Detail> getDetails() {
		return details;
	}

	public void setDetails(final Set<Detail> details) {
		this.details = details;
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
