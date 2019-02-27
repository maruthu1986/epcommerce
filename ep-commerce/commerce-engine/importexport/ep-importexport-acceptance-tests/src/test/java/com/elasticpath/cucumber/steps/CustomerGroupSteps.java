/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.steps;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.customer.CustomerGroupBuilder;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerRole;
import com.elasticpath.domain.customer.impl.CustomerRoleImpl;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * Customer group steps.
 */
public class CustomerGroupSteps {

	@Autowired
	private CustomerGroupService customerGroupService;

	@Autowired
	private CustomerGroupBuilder customerGroupBuilder;


	/**
	 * Ensure customer group already exists.
	 *
	 * @param customerGroupName the customer group name
	 */
	@Given("^the customer segment \\[([A-Z0-9_]+)\\] exists$")
	public void ensureCustomerGroupAlreadyExists(final String customerGroupName) {
		Assert.assertNotNull(
				String.format("Customer segment [%s] does not exist", customerGroupName),
				customerGroupService.findByGroupName(customerGroupName));
	}

	/**
	 * Creates a customer group.
	 *
	 * @param customerGroupName the customer group name
	 */
	@Given("^the customer segment \\[([A-Z0-9_]+)\\] has been created$")
	public void createCustomerGroup(final String customerGroupName) {
		addCustomerGroup(customerGroupName, true, Collections.<CustomerRole>emptySet());
	}

	/**
	 * Creates a customer group with enabled value.
	 *
	 * @param customerGroupName the customer group name
	 * @param enabledString the enabled string
	 */
	@Given("^the customer segment \\[([A-Z0-9_]+)\\] has been created with enabled value of \\[(TRUE|FALSE)\\]$")
	public void createCustomerGroupWithEnabledValue(final String customerGroupName, final String enabledString) {
		addCustomerGroup(customerGroupName, "TRUE".equals(enabledString), Collections.<CustomerRole>emptySet());
	}

	/**
	 * Creates a customer group with roles.
	 *
	 * @param customerGroupName the customer group name
	 * @param authoritiesString the authorities string
	 */
	@Given("^the customer segment \\[([A-Z0-9_]+)\\] has been created with roles? \\[([A-Z_,]+)\\]$")
	public void createCustomerGroupWithRoles(final String customerGroupName, final String authoritiesString) {
		final Set<CustomerRole> customerRoles = new HashSet<>();
		for (String authority : authoritiesString.split(",")) {
			final CustomerRole customerRole = new CustomerRoleImpl();
			customerRole.setAuthority(authority);
			customerRoles.add(customerRole);
		}
		addCustomerGroup(customerGroupName, true, customerRoles);
	}

	private void addCustomerGroup(final String customerGroupName, final boolean enabled, final Set<CustomerRole> customerRoles) {
		final CustomerGroup customerGroup = customerGroupBuilder.newInstance()
				.withGuid(String.format("guid_%s", customerGroupName))
				.withName(customerGroupName)
				.withDescription(String.format("Description for %s", customerGroupName))
				.withEnabled(enabled)
				.withCustomerRoles(customerRoles)
				.build();

		customerGroupService.add(customerGroup);
	}

	/**
	 * Ensure customer group has enabled value.
	 *
	 * @param customerGroupName the customer group name
	 * @param enabledString the enabled string
	 */
	@Then("the customer segment \\[([A-Z0-9_]+)\\] should have an enabled value of \\[(TRUE|FALSE)\\]$")
	public void ensureCustomerGroupHasEnabledValue(final String customerGroupName, final String enabledString) {
		final CustomerGroup customerGroup = customerGroupService.findByGroupName(customerGroupName);
		final boolean shouldBeEnabled = "TRUE".equals(enabledString);

		Assert.assertEquals(
				String.format("Customer segment [%s] has incorrect enabled value", customerGroup.getName()),
				shouldBeEnabled, customerGroup.isEnabled());
	}

	/**
	 * Ensure customer group has roles.
	 *
	 * @param customerGroupName the customer group name
	 * @param authoritiesString the authorities string
	 */
	@Then("the customer segment \\[([A-Z0-9_]+)\\] should have roles? \\[([A-Z_,]+)\\]$")
	public void ensureCustomerGroupHasRoles(final String customerGroupName, final String authoritiesString) {
		validateCustomerGroupWithRoles(customerGroupName, Arrays.asList(authoritiesString.split(",")));
	}

	/**
	 * Ensure customer group has no role.
	 *
	 * @param customerGroupName the customer group name
	 */
	@Then("the customer segment \\[([A-Z0-9_]+)\\] should have no role")
	public void ensureCustomerGroupHasNoRole(final String customerGroupName) {
		validateCustomerGroupWithRoles(customerGroupName, Collections.<String>emptyList());
	}

	private void validateCustomerGroupWithRoles(final String customerGroupName, final List<String> expectedAuthorities) {
		final CustomerGroup customerGroup = customerGroupService.findByGroupName(customerGroupName);

		final Collection<String> groupAuthorities =
			Collections2.transform(customerGroup.getCustomerRoles(), new Function<CustomerRole, String>() {
				@Override
				public String apply(final CustomerRole customerRole) {
					return customerRole.getAuthority();
				}
			});

		Assert.assertTrue(
				String.format("Customer segment [%s] has incorrect roles", customerGroupName),
				CollectionUtils.isEqualCollection(expectedAuthorities, groupAuthorities));
	}

}
