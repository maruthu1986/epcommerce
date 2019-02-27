/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import java.util.Map;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.customer.helper.CustomerEmailPropertyHelper;
import com.elasticpath.email.producer.spi.AbstractEmailProducer;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.customer.CustomerService;

/**
 * Producer for a customer-triggered password change confirmation email.
 */
public class PasswordChangedEmailProducer extends AbstractEmailProducer {

	private EmailComposer emailComposer;

	private CustomerEmailPropertyHelper customerEmailPropertyHelper;

	private CustomerService customerService;

	@Override
	public EmailDto createEmail(final String guid, final Map<String, Object> emailData) {

		final Customer customer = getCustomer(guid);
		final EmailProperties customerEmailProperties = getCustomerEmailPropertyHelper().getPasswordConfirmationEmailProperties(customer);

		return getEmailComposer().composeMessage(customerEmailProperties);
	}
	
	/**
	 * Retrieves a {@link Customer} with the given guid.
	 * 
	 * @param guid the customer GUID
	 * @return a {@link Customer}
	 * @throws IllegalArgumentException if an {@link Customer} can not be retrieved from the given parameters
	 */
	protected Customer getCustomer(final String guid) {

		if (guid == null) {
			throw new IllegalArgumentException("A customer guid must be provided.");
		}

		final Customer customer = getCustomerService().findByGuid(guid);

		if (customer == null) {
			throw new IllegalArgumentException("Could not locate a Customer with GUID [" + guid + "]");
		}

		return customer;
	}

	public void setCustomerEmailPropertyHelper(final CustomerEmailPropertyHelper customerEmailPropertyHelper) {
		this.customerEmailPropertyHelper = customerEmailPropertyHelper;
	}

	public CustomerEmailPropertyHelper getCustomerEmailPropertyHelper() {
		return customerEmailPropertyHelper;
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	public EmailComposer getEmailComposer() {
		return emailComposer;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}
}
