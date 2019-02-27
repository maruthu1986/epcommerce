/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import java.util.Optional;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerTagStrategy;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.service.TagFactory;

/**
 * Create customer registered Tag.
 */
@Component(
		service = CustomerTagStrategy.class,
		property = Constants.SERVICE_RANKING + ":Integer=200")
public class CustomerRegisteredTagStrategy extends AbstractCustomerTagStrategy {

	/** Tag name. */
	static final String REGISTERED_CUSTOMER = "REGISTERED_CUSTOMER";


	@Reference
	private TagFactory tagFactory;


	@Override
	public String tagName() {
		return REGISTERED_CUSTOMER;
	}

	@Override
	protected Optional<Tag> createTag(final Customer customer) {
		return Optional.of(tagFactory
			.createTagFromTagName(REGISTERED_CUSTOMER, String.valueOf(customer.isRegistered())));
	}
}
