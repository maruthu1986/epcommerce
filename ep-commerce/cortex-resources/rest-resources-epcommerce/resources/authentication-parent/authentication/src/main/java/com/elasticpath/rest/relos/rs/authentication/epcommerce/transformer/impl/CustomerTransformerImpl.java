/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.impl;

import org.osgi.service.component.annotations.Component;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.relos.rs.authentication.User;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.impl.UserImpl;
import com.elasticpath.rest.relos.rs.authentication.epcommerce.transformer.CustomerTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link Customer} into a  instance {@link User}.
 */
@Component(property = AbstractDomainTransformer.DS_SERVICE_RANKING)
public class CustomerTransformerImpl implements CustomerTransformer {

	@Override
	public User transform(final Customer customer) {
		assert customer != null : "Customer is null.";

		User user = new UserImpl();

		user.setRequestedScope(customer.getStoreCode());
		user.setUsername(customer.getUserId());
		if (customer.getPassword() != null) {
			user.setPassword(customer.getPassword());
		}
		user.setPrincipals(PrincipalsUtil.createRolePrincipals(customer.getCustomerRoleMapper().getAllRoles()));
		setUserStatesFromCustomerStatus(user, customer.getStatus());
		user.setUserId(customer.getGuid());
		user.setSalt(customer.getCustomerAuthentication().getSalt());
		user.setAccountExpired(!customer.isAccountNonExpired());

		return user;
	}

	private void setUserStatesFromCustomerStatus(final User user, final int status) {
		boolean active = status == Customer.STATUS_ACTIVE;
		user.setAccountEnabled(active);
		user.setAccountLocked(!active);
	}
}
