/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.common.dto.customer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerRole;
import com.elasticpath.domain.customer.impl.CustomerRoleImpl;

/**
 * Assembles CustomerGroup domains and DTOs.
 */
public class CustomerGroupDtoAssembler extends AbstractDtoAssembler<CustomerGroupDTO, CustomerGroup> {

	private BeanFactory beanFactory;

	@Override
	public CustomerGroup getDomainInstance() {
		return getBeanFactory().getBean(ContextIdNames.CUSTOMER_GROUP);
	}

	@Override
	public void assembleDto(final CustomerGroup source, final CustomerGroupDTO target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setEnabled(source.isEnabled());

		final List<CustomerRoleDTO> customerRoleDTOs = new ArrayList<>();
		for (CustomerRole customerRole : source.getCustomerRoles()) {
			CustomerRoleDTO customerRoleDTO = createCustomerRoleDTO();
			customerRoleDTO.setAuthority(customerRole.getAuthority());

			customerRoleDTOs.add(customerRoleDTO);
		}
		target.setCustomerRoles(customerRoleDTOs);
	}

	@Override
	public void assembleDomain(final CustomerGroupDTO source, final CustomerGroup target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setEnabled(source.isEnabled());

		Set<CustomerRole> customerRoles = target.getCustomerRoles();
		if (customerRoles == null) {
			customerRoles = new HashSet<>();
			target.setCustomerRoles(customerRoles);
		}

		for (CustomerRoleDTO customerRoleDTO : source.getCustomerRoles()) {
			CustomerRole customerRole = createCustomerRoleDomain();
			customerRole.setAuthority(customerRoleDTO.getAuthority());

			customerRoles.add(customerRole);
		}
	}

	@Override
	public CustomerGroupDTO getDtoInstance() {
		return new CustomerGroupDTO();
	}

	/**
	 * Create a new CustomerRoleDTO instance.
	 * 
	 * @return a new CustomerRoleDTO instance
	 */
	protected CustomerRoleDTO createCustomerRoleDTO() {
		return new CustomerRoleDTO();
	}

	/**
	 * Create a new CustomerRole instance.
	 * 
	 * @return a new CustomerRole instance
	 */
	protected CustomerRoleImpl createCustomerRoleDomain() {
		return getBeanFactory().getBean(ContextIdNames.CUSTOMER_ROLE);
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

}
