/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain.datapolicy.impl;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;

import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Default implementation of <code>CustomerConsent</code>.
 */
@Entity
@Table(name = CustomerConsentImpl.TABLE_NAME)
public class CustomerConsentImpl extends AbstractEntityImpl implements CustomerConsent {

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCUSTOMERCONSENT";
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private long uidPk;

	private String guid;

	private DataPolicy dataPolicy;

	private ConsentAction action;

	private Date consentDate;

	private String customerGuid;

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
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	@ManyToOne(targetEntity = DataPolicyImpl.class)
	@JoinColumn(name = "DATAPOLICY_UID", nullable = false)
	public DataPolicy getDataPolicy() {
		return dataPolicy;
	}

	@Override
	public void setDataPolicy(final DataPolicy dataPolicy) {
		this.dataPolicy = dataPolicy;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONSENT_DATE", nullable = false)
	public Date getConsentDate() {
		return consentDate;
	}

	@Override
	public void setConsentDate(final Date consentDate) {
		this.consentDate = consentDate;
	}

	@Override
	@Persistent(optional = false)
	@Externalizer("getOrdinal")
	@Factory("valueOf")
	@Column(name = "ACTION")
	public ConsentAction getAction() {
		return action;
	}

	@Override
	public void setAction(final ConsentAction action) {
		this.action = action;
	}

	@Override
	@Basic
	@Column(name = "CUSTOMER_GUID", nullable = false)
	public String getCustomerGuid() {
		return customerGuid;
	}

	@Override
	public void setCustomerGuid(final String customerGuid) {
		this.customerGuid = customerGuid;
	}
}
