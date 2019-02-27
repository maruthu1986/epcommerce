/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.common.dto.customer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.customer.CustomerGroupDTO;

/**
 * Wrapper JAXB entity for schema generation to collect a group of CustomerGroupDTOs.
 */
@XmlRootElement(name = "customer_groups")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "customerGroupsDTO", propOrder = { })
public class CustomerGroupsDTO {

	@XmlElement(name = "customer_group")
	private final List<CustomerGroupDTO> customerGroups = new ArrayList<>();

	public List<CustomerGroupDTO> getCustomerGroups() {
		return customerGroups;
	}

}

