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

import com.elasticpath.common.dto.datapolicy.DataPolicyDTO;

/**
 * Wrapper JAXB entity for schema generation to collect a group of DataPolicyDTOs.
 */
@XmlRootElement(name = DataPoliciesDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "dataPoliciesDTO", propOrder = { })
public class DataPoliciesDTO {

	/** Root element name for {@link com.elasticpath.domain.datapolicy.DataPolicy}. */
	public static final String ROOT_ELEMENT = "data_policies";

	@XmlElement(name = "data_policy")
	private final List<DataPolicyDTO> dataPolicies = new ArrayList<>();

	public List<DataPolicyDTO> getDataPolicies() {
		return dataPolicies;
	}
}
