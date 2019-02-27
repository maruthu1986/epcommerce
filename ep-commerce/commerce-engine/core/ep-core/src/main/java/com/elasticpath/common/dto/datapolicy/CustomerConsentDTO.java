/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.common.dto.datapolicy;

import java.util.Date;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Data Transfer Object for {@link com.elasticpath.domain.datapolicy.CustomerConsent}.
 */
@XmlRootElement(name = CustomerConsentDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {})
public class CustomerConsentDTO implements Dto {

	/**
	 * Root element name for {@link com.elasticpath.domain.datapolicy.CustomerConsent}.
	 */
	public static final String ROOT_ELEMENT = "customer_consent";

	private static final long serialVersionUID = 5000000001L;

	@XmlAttribute(required = true)
	private String guid;

	@XmlElement(name = "data_policy_guid", required = true)
	private String dataPolicyGuid;

	@XmlElement(required = true)
	private String action;

	@XmlElement(name = "consent_date", required = true)
	private Date consentDate;

	@XmlElement(name = "customer_guid", required = true)
	private String customerGuid;

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public String getDataPolicyGuid() {
		return dataPolicyGuid;
	}

	public void setDataPolicyGuid(final String dataPolicyGuid) {
		this.dataPolicyGuid = dataPolicyGuid;
	}

	public String getAction() {
		return action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public Date getConsentDate() {
		return consentDate;
	}

	public void setConsentDate(final Date consentDate) {
		this.consentDate = consentDate;
	}

	public String getCustomerGuid() {
		return customerGuid;
	}

	public void setCustomerGuid(final String customerGuid) {
		this.customerGuid = customerGuid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(guid);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		CustomerConsentDTO other = (CustomerConsentDTO) obj;

		return Objects.equals(guid, other.guid);
	}
}
