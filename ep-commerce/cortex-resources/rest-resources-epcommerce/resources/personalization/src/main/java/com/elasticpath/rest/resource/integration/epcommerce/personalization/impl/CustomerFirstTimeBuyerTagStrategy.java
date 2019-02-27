/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerTagStrategy;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.service.TagFactory;

/**
 * Creates customer first time buyer tag.
 */
@Component(
		service = CustomerTagStrategy.class,
		property = Constants.SERVICE_RANKING + ":Integer=400")
public class CustomerFirstTimeBuyerTagStrategy extends AbstractCustomerTagStrategy {

	/** Tag name. */
	@VisibleForTesting
	static final String FIRST_TIME_BUYER = "FIRST_TIME_BUYER";


	@Reference
	private CustomerRepository customerRepository;

	@Reference
	private TagFactory tagFactory;


	@Override
	public String tagName() {
		return FIRST_TIME_BUYER;
	}

	@Override
	protected Optional<Tag> createTag(final Customer customer) {
		boolean isFirstTimeBuyer = customerRepository.isFirstTimeBuyer(customer);
		return Optional.of(tagFactory
			.createTagFromTagName(FIRST_TIME_BUYER, String.valueOf(isFirstTimeBuyer)));
	}
}
