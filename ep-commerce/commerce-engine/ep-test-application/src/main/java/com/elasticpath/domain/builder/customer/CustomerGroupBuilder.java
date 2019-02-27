/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.builder.customer;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.DomainObjectBuilder;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerRole;

/**
 * A builder that builds {@link CustomerGroup}s for testing purposes.
 */
public class CustomerGroupBuilder implements DomainObjectBuilder<CustomerGroup> {

	@Autowired
	private BeanFactory beanFactory;

	private Long uidpk = 0L;
	private String guid;
	private String name;
	private String description;
	private Boolean enabled = Boolean.TRUE;
	private Set<CustomerRole> customerRoles = new HashSet<>();

	public CustomerGroupBuilder newInstance() {
		final CustomerGroupBuilder newBuilder = new CustomerGroupBuilder();
		newBuilder.setBeanFactory(beanFactory);

		return newBuilder;
	}

	public CustomerGroupBuilder withUidpk(final Long uidpk) {
		this.uidpk = uidpk;
		return this;
	}

	public CustomerGroupBuilder withGuid(final String guid) {
		this.guid = guid;
		return this;
	}

	public CustomerGroupBuilder withName(final String name) {
		this.name = name;
		return this;
	}

	public CustomerGroupBuilder withDescription(final String description) {
		this.description = description;
		return this;
	}

	public CustomerGroupBuilder withEnabled(final Boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public CustomerGroupBuilder withCustomerRoles(final Set<CustomerRole> customerRoles) {
		this.customerRoles = customerRoles;
		return this;
	}

	public CustomerGroupBuilder withCustomerRole(final CustomerRole customerRole) {
		this.customerRoles.add(customerRole);
		return this;
	}

	@Override
	public CustomerGroup build() {
		CustomerGroup customerGroup = beanFactory.getBean(ContextIdNames.CUSTOMER_GROUP);
		customerGroup.setGuid(guid);
		customerGroup.setUidPk(uidpk);
		customerGroup.setName(name);
		customerGroup.setDescription(description);
		customerGroup.setEnabled(enabled);
		customerGroup.setCustomerRoles(customerRoles);

		return customerGroup;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
