/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import java.util.Calendar;
import java.util.Date;
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
 * Creates customer age tag.
 */
@Component(
		service = CustomerTagStrategy.class,
		property = Constants.SERVICE_RANKING + ":Integer=300")
public class CustomerAgeTagStrategy extends AbstractCustomerTagStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerAgeTagStrategy.class);

	/** Tag name. */
	@VisibleForTesting
	static final String CUSTOMER_AGE_YEARS = "CUSTOMER_AGE_YEARS";


	@Reference
	private TagFactory tagFactory;


	@Override
	protected String tagName() {
		return CUSTOMER_AGE_YEARS;
	}

	@Override
	protected Optional<Tag> createTag(final Customer customer) {

		Date dateOfBirth = customer.getDateOfBirth();

		if (dateOfBirth == null) {
			LOG.debug("Customer's Date Of Birth not available.");
			return Optional.empty();
		}

		int customerAge = getAgeInYearsAsOfNow(dateOfBirth);
		LOG.debug("Adding customer's age to set of subject attributes: {}", customerAge);

		return Optional.of(tagFactory
			.createTagFromTagName(CUSTOMER_AGE_YEARS, String.valueOf(customerAge)));
	}

	private int getAgeInYearsAsOfNow(final Date dateOfBirth) {
		Calendar birthday = convertDateToCalendar(dateOfBirth);
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
	}

	private Calendar convertDateToCalendar(final Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
}
