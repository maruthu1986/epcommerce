/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerTagStrategy;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.service.TagFactory;

/**
 * Creates customer group tag.
 */
@Component(
		service = CustomerTagStrategy.class,
		property = Constants.SERVICE_RANKING + ":Integer=500")
public class CustomerGroupTagStrategy extends AbstractCustomerTagStrategy {

	/** Name separator char. */
	@VisibleForTesting
	static final String NAME_SEPARATOR = ",";

	/** Tag name. */
	@VisibleForTesting
	static final String CUSTOMER_GROUP = "CUSTOMER_SEGMENT";


	@Reference
	private TagFactory tagFactory;


	@Override
	public String tagName() {
		return CUSTOMER_GROUP;
	}

	@Override
	protected Optional<Tag> createTag(final Customer customer) {
		Collection<CustomerGroup> customerGroups = customer.getCustomerGroups();
		if (customerGroups.isEmpty()) {
			return Optional.empty();
		}

		String value = customerGroups.stream()
				.filter(CustomerGroup::isEnabled)
				.map(CustomerGroup::getName)
				.collect(Collectors.joining(NAME_SEPARATOR));

		return Optional.of(
			tagFactory.createTagFromTagName(CUSTOMER_GROUP, value));
	}
}
