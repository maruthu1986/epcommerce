/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerTagStrategy;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.service.TagFactory;

/**
 * Creates customer gender tag.
 */
@Component(
		service = CustomerTagStrategy.class,
		property = Constants.SERVICE_RANKING + ":Integer=100")
public class CustomerGenderTagStrategy extends AbstractCustomerTagStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerGenderTagStrategy.class);

	/** Tag name. */
	@VisibleForTesting
	static final String CUSTOMER_GENDER = "CUSTOMER_GENDER";


	@Reference
	private TagFactory tagFactory;


	@Override
	public String tagName() {
		return CUSTOMER_GENDER;
	}

	@Override
	protected Optional<Tag> createTag(final Customer customer) {
		char gender = customer.getGender();

		if (gender == 0) {
			LOG.debug("Customer's gender not available; no gender trait provided.");
			return Optional.empty();
		}
		LOG.debug("Adding customer's gender: {}", gender);
		return Optional.of(tagFactory
			.createTagFromTagName(CUSTOMER_GENDER, String.valueOf(gender)));
	}
}
