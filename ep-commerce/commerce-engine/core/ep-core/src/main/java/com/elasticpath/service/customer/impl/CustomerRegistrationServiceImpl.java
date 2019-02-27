/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.customer.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.core.messaging.customer.CustomerEventType;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.service.auth.UserIdentityService;
import com.elasticpath.service.customer.CustomerRegistrationResult;
import com.elasticpath.service.customer.CustomerRegistrationService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.validation.ConstraintViolationTransformer;
import com.elasticpath.validation.groups.PasswordChange;

/**
 * Services providing customer registration operations.
 */
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService {

	private static final String PASSWORD_KEY = "password";

	private BeanFactory beanFactory;
	private CustomerService customerService;
	private UserIdentityService userIdentityService;
	private Validator validator;
	private EventMessagePublisher eventMessagePublisher;
	private EventMessageFactory eventMessageFactory;

	// Turn off line too long PMD warning
	private ConstraintViolationTransformer constraintViolationTransformer; //NOPMD

	public void setConstraintViolationTransformer(
			final ConstraintViolationTransformer constraintViolationTransformer) {
		this.constraintViolationTransformer = constraintViolationTransformer;
	}

	@Override
	public CustomerRegistrationResult registerAnonymousCustomer(final Customer customer) {
		final CustomerRegistrationResult result = getBeanFactory().getBean(ContextIdNames.CUSTOMER_REGISTRATION_RESULT);
		customer.setAnonymous(false);

		final Set<ConstraintViolation<Customer>> violations = getValidator().validate(customer, PasswordChange.class);

		result.setConstraintViolations(violations);

		if (!violations.isEmpty()) {
			return result;
		}

		final Customer updatedCustomer = getCustomerService().update(customer);
		result.setRegisteredCustomer(updatedCustomer);

		sendCustomerEvent(CustomerEventType.CUSTOMER_REGISTERED, customer.getGuid(), null);

		return result;
	}

	@Override
	public Customer registerCustomer(final Customer customer) {
		customer.setAnonymous(false);

		final Set<ConstraintViolation<Customer>> violations = getValidator().validate(customer, PasswordChange.class);

		if (!violations.isEmpty()) {
			List<StructuredErrorMessage> structuredErrorMessageList = constraintViolationTransformer.transform(violations);
			throw new EpValidationException("Customer validation failure.", structuredErrorMessageList);
		}

		final CustomerRegistrationResult result = getBeanFactory().getBean(ContextIdNames.CUSTOMER_REGISTRATION_RESULT);
		final Customer updatedCustomer = getCustomerService().update(customer);
		result.setRegisteredCustomer(updatedCustomer);

		sendCustomerEvent(CustomerEventType.CUSTOMER_REGISTERED, customer.getGuid(), null);

		return updatedCustomer;
	}

	@Override
	public Customer registerCustomerAndSendPassword(final Customer customer) {
		if (!customer.isRegistered()) {
			customer.setAnonymous(false);
		}
		final String newPassword = customer.resetPassword();
		getUserIdentityService().setPassword(customer.getUserId(), customer.getClearTextPassword());

		final Customer updatedCustomer = getCustomerService().update(customer);

		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put(PASSWORD_KEY, newPassword);

		sendCustomerEvent(CustomerEventType.ANONYMOUS_CUSTOMER_REGISTERED, updatedCustomer.getGuid(), additionalData);

		return updatedCustomer;
	}

	/**
	 * Sends an Event Message for Customer-related events.
	 *
	 * @param eventType the event type
	 * @param customerGuid the Customer GUID
	 * @param additionalData any additional data required for the given message
	 */
	protected void sendCustomerEvent(final EventType eventType, final String customerGuid, final Map<String, Object> additionalData) {
		try {
			final EventMessage orderCreatedEventMessage = getEventMessageFactory().createEventMessage(eventType, customerGuid, additionalData);

			getEventMessagePublisher().publish(orderCreatedEventMessage);

		} catch (final Exception e) {
			throw new EpSystemException("Failed to publish Event Message", e);
		}
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	protected CustomerService getCustomerService() {
		return customerService;
	}

	public void setUserIdentityService(final UserIdentityService userIdentityService) {
		this.userIdentityService = userIdentityService;
	}

	protected UserIdentityService getUserIdentityService() {
		return userIdentityService;
	}

	public void setValidator(final Validator validator) {
		this.validator = validator;
	}

	protected Validator getValidator() {
		return validator;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setEventMessageFactory(final EventMessageFactory eventMessageFactory) {
		this.eventMessageFactory = eventMessageFactory;
	}

	protected EventMessageFactory getEventMessageFactory() {
		return this.eventMessageFactory;
	}

	public void setEventMessagePublisher(final EventMessagePublisher eventMessagePublisher) {
		this.eventMessagePublisher = eventMessagePublisher;
	}

	protected EventMessagePublisher getEventMessagePublisher() {
		return eventMessagePublisher;
	}

}
