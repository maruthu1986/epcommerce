/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.common.dto.customer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.common.dto.Dto;

/**
 * JAXB DTO of a CustomerGroup.
 */
@XmlRootElement(name = CustomerGroupDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class CustomerGroupDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/** Root XML element name. */
	public static final String ROOT_ELEMENT = "customer_group";

	@XmlAttribute(required = true)
	private String guid;

	@XmlAttribute(required = true)
	private String name;

	@XmlAttribute(required = true)
	private String description;

	@XmlElement(required = true)
	private boolean enabled;

	@XmlElementWrapper(name = "customer_roles")
	@XmlElement(name = "customer_role")
	private List<CustomerRoleDTO> customerRoles = new ArrayList<>();


	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public List<CustomerRoleDTO> getCustomerRoles() {
		return customerRoles;
	}

	public void setCustomerRoles(final List<CustomerRoleDTO> customerRoles) {
		this.customerRoles = customerRoles;
	}

	/**
	 * Adds a customer role.
	 * @param customerRoleDTO a customer role dto
	 */
	public void addCustomerRole(final CustomerRoleDTO customerRoleDTO) {
		this.customerRoles.add(customerRoleDTO);
	}

	/**
	 * Adds a customer role.
	 * @param authority the authority of the customer role
	 */
	public void addCustomerRole(final String authority) {
		final CustomerRoleDTO customerRoleDTO = new CustomerRoleDTO();
		customerRoleDTO.setAuthority(authority);

		addCustomerRole(customerRoleDTO);
	}

	@Override
	public boolean equals(final Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
