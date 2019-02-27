/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.common.dto.datapolicy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;

/**
 * Wrapper JAXB entity for schema generation to collect a group of CustomerConsentDTOs.
 */
@XmlRootElement(name = CustomerConsentsDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "customerConsentsDTO", propOrder = { })
public class CustomerConsentsDTO {

	/** Root element name for {@link com.elasticpath.domain.datapolicy.CustomerConsent}. */
	public static final String ROOT_ELEMENT = "customer_consents";

	@XmlElement(name = "customer_consent")
	private final List<CustomerConsentDTO> customerConsents = new ArrayList<>();

	public List<CustomerConsentDTO> getCustomerConsents() {
		return customerConsents;
	}
}
