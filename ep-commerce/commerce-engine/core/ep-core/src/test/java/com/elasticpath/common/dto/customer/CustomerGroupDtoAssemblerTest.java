/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.common.dto.customer;


import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerRole;
import com.elasticpath.domain.customer.impl.CustomerGroupImpl;
import com.elasticpath.domain.customer.impl.CustomerRoleImpl;

/**
 * Test {@link CustomerGroupDtoAssembler} functionality.
 */
public class CustomerGroupDtoAssemblerTest {

	private final CustomerGroupDtoAssembler customerGroupDtoAssembler = new CustomerGroupDtoAssembler();

	private final BeanFactory stubbedBeanFactory = createStubbedBeanFactory();

	@Before
	public void setUp() {
		customerGroupDtoAssembler.setBeanFactory(stubbedBeanFactory);
	}

	/**
	 * Tests assembly of DTO from domain.
	 */
	@Test
	public void testAssembleDto() {
		final String groupGuid = "0110100101000111";
		final String groupName = "nameOfTheCustomerGroup";
		final String groupDescription = "the description of the group";
		final boolean groupEnabled = true;
		final String authority1 = "ROLE1";
		final String authority2 = "ROLE2";

		final CustomerGroup sourceDomain = new CustomerGroupImpl();
		sourceDomain.setGuid(groupGuid);
		sourceDomain.setName(groupName);
		sourceDomain.setDescription(groupDescription);
		sourceDomain.setEnabled(groupEnabled);
		final CustomerRole role1 = new CustomerRoleImpl();
		role1.setAuthority(authority1);
		final CustomerRole role2 = new CustomerRoleImpl();
		role2.setAuthority(authority2);
		sourceDomain.setCustomerRoles(Sets.newHashSet(role1, role2));

		final CustomerGroupDTO targetDTO = new CustomerGroupDTO();

		// Run test
		customerGroupDtoAssembler.assembleDto(sourceDomain, targetDTO);

		Assert.assertEquals(groupGuid, targetDTO.getGuid());
		Assert.assertEquals(groupName, targetDTO.getName());
		Assert.assertEquals(groupDescription, targetDTO.getDescription());
		Assert.assertEquals(groupEnabled, targetDTO.isEnabled());
		Assert.assertEquals(2, targetDTO.getCustomerRoles().size());

		final Collection<String> targetDTOAuthorities = Collections2.transform(targetDTO.getCustomerRoles(),
				new Function<CustomerRoleDTO, String>() {
					@Override
					public String apply(final CustomerRoleDTO customerRoleDTO) {
						return customerRoleDTO.getAuthority();
					}
				}
			);
		Assert.assertTrue(targetDTOAuthorities.containsAll(Sets.newHashSet(authority1, authority2)));
	}

	/**
	 * Tests assembly of domain object from DTO.
	 */
	@Test
	public void testAssembleDomain() {
		final String groupGuid = "0110100101000111";
		final String groupName = "nameOfTheCustomerGroup";
		final String groupDescription = "the description of the group";
		final boolean groupEnabled = true;
		final String authority1 = "ROLE1";
		final String authority2 = "ROLE2";
		final String originalAuthority = "ROLE_ORIGINAL";

		final CustomerGroupDTO sourceDTO = new CustomerGroupDTO();
		sourceDTO.setGuid(groupGuid);
		sourceDTO.setName(groupName);
		sourceDTO.setDescription(groupDescription);
		sourceDTO.setEnabled(groupEnabled);
		final CustomerRoleDTO roleDTO1 = new CustomerRoleDTO();
		roleDTO1.setAuthority(authority1);
		final CustomerRoleDTO roleDTO2 = new CustomerRoleDTO();
		roleDTO2.setAuthority(authority2);
		sourceDTO.setCustomerRoles(Arrays.asList(roleDTO1, roleDTO2));

		final int sourceDTOCustomerRoleCount = sourceDTO.getCustomerRoles().size();

		final CustomerGroup targetDomain = new CustomerGroupImpl();
		final CustomerRole originalRole = new CustomerRoleImpl();
		originalRole.setAuthority(originalAuthority);
		targetDomain.setCustomerRoles(Sets.newHashSet(originalRole));

		final int targetDomainCustomerRoleCount = targetDomain.getCustomerRoles().size();

		// Run test
		customerGroupDtoAssembler.assembleDomain(sourceDTO, targetDomain);

		Assert.assertEquals(groupGuid, targetDomain.getGuid());
		Assert.assertEquals(groupName, targetDomain.getName());
		Assert.assertEquals(groupDescription, targetDomain.getDescription());
		Assert.assertEquals(groupEnabled, targetDomain.isEnabled());
		Assert.assertEquals(sourceDTOCustomerRoleCount + targetDomainCustomerRoleCount, targetDomain.getCustomerRoles().size());
		// retains the original role already in the domain before assembly
		Assert.assertTrue(targetDomain.getCustomerRoles().contains(originalRole));

		final Collection<String> targetDomainAuthorities = Collections2.transform(targetDomain.getCustomerRoles(),
				new Function<CustomerRole, String>() {
					@Override
					public String apply(final CustomerRole customerRole) {
						return customerRole.getAuthority();
					}
				}
			);
		Assert.assertTrue(targetDomainAuthorities.containsAll(Sets.newHashSet(originalAuthority, authority1, authority2)));
	}

	@SuppressWarnings("unchecked")
	private BeanFactory createStubbedBeanFactory() {
		return new BeanFactory() {
			@Override
			public Object getBean(final String name) {
				if (ContextIdNames.CUSTOMER_ROLE.equals(name)) {
					return new CustomerRoleImpl();
				}
				return null;
			}

			@Override
			public <T> Class<T> getBeanImplClass(final String beanName) {
				return null;
			}
		};
	}
}
