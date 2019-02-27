/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.mail.EmailException;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.core.messaging.customer.CustomerEventType;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.service.auth.UserIdentityService;
import com.elasticpath.service.customer.CustomerRegistrationResult;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.validation.ConstraintViolationTransformer;
import com.elasticpath.validation.groups.PasswordChange;

/**
 * Test class for {@link CustomerRegistrationServiceImpl}.
 */
public class CustomerRegistrationServiceImplTest {

	private static final String PASSWORD = "password";
	private static final String USER_ID = "userId";
	private static final String CUSTOMER_GUID = "CUST-1234";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory beanExpectations;

	@Mock
	private CustomerService customerService;

	@Mock
	private Validator validator;

	@Mock
	private UserIdentityService userIdentityService;

	@Mock
	private EventMessageFactory eventMessageFactory;

	@Mock
	private EventMessagePublisher eventMessagePublisher;

	@Mock
	private ConstraintViolationTransformer constraintViolationTransformer;

	private CustomerRegistrationServiceImpl customerRegistrationService;

	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		beanExpectations = new BeanFactoryExpectationsFactory(context, beanFactory);

		customerRegistrationService = new CustomerRegistrationServiceImpl();
		customerRegistrationService.setBeanFactory(beanFactory);
		customerRegistrationService.setCustomerService(customerService);
		customerRegistrationService.setEventMessageFactory(eventMessageFactory);
		customerRegistrationService.setEventMessagePublisher(eventMessagePublisher);
		customerRegistrationService.setValidator(validator);
		customerRegistrationService.setUserIdentityService(userIdentityService);
		customerRegistrationService.setConstraintViolationTransformer(constraintViolationTransformer);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.CUSTOMER_REGISTRATION_RESULT); will(returnValue(new CustomerRegistrationResultImpl()));
			}
		});
	}

	@After
	public void tearDown() {
		beanExpectations.close();
	}

	@Test
	public void testRegisterAnonymousCustomer() throws EmailException {
		final Customer customer = context.mock(Customer.class);
		final Customer updatedCustomer = new CustomerImpl();

		context.checking(new Expectations() {
			{
				oneOf(customer).setAnonymous(false);
				oneOf(validator).validate(customer, PasswordChange.class); will(returnValue(new HashSet<ConstraintViolation<Customer>>()));
				oneOf(customerService).update(customer); will(returnValue(updatedCustomer));
				atLeast(1).of(customer).getGuid(); will(returnValue(CUSTOMER_GUID));

				final EventMessage eventMessage = context.mock(EventMessage.class);
				oneOf(eventMessageFactory).createEventMessage(CustomerEventType.CUSTOMER_REGISTERED, CUSTOMER_GUID, null);
				will(returnValue(eventMessage));

				oneOf(eventMessagePublisher).publish(eventMessage);
			}
		});

		CustomerRegistrationResult customerRegistrationResult = customerRegistrationService.registerAnonymousCustomer(customer);

		assertTrue("Violation Constraints should be empty.", customerRegistrationResult.getConstraintViolations().isEmpty());
		assertEquals("Result customer should be the updated customer.", updatedCustomer, customerRegistrationResult.getRegisteredCustomer());
	}

	@Test
	public void testRegisterCustomer() throws EmailException {
		final Customer customer = context.mock(Customer.class);
		final Customer updatedCustomer = new CustomerImpl();

		context.checking(new Expectations() {
			{
				oneOf(customer).setAnonymous(false);
				oneOf(validator).validate(customer, PasswordChange.class); will(returnValue(new HashSet<ConstraintViolation<Customer>>()));
				oneOf(customerService).update(customer); will(returnValue(updatedCustomer));
				atLeast(1).of(customer).getGuid(); will(returnValue(CUSTOMER_GUID));

				final EventMessage eventMessage = context.mock(EventMessage.class);
				oneOf(eventMessageFactory).createEventMessage(CustomerEventType.CUSTOMER_REGISTERED, CUSTOMER_GUID, null);
				will(returnValue(eventMessage));

				oneOf(eventMessagePublisher).publish(eventMessage);
			}
		});

		Customer registeredCustomer = customerRegistrationService.registerCustomer(customer);

		assertEquals("Result customer should be the updated customer.", updatedCustomer, registeredCustomer);
	}

	@Test
	public void testRegisterAnonymousCustomerWithConstraintViolations() {
		final Customer customer = context.mock(Customer.class);

		final Set<ConstraintViolation<Customer>> constraintViolations = new HashSet<>();
		@SuppressWarnings("unchecked")
		ConstraintViolation<Customer> violation = (ConstraintViolation<Customer>) context.mock(ConstraintViolation.class);
		constraintViolations.add(violation);

		context.checking(new Expectations() {
			{
				oneOf(customer).setAnonymous(false);
				oneOf(validator).validate(customer, PasswordChange.class); will(returnValue(constraintViolations));
			}
		});

		CustomerRegistrationResult customerRegistrationResult = customerRegistrationService.registerAnonymousCustomer(customer);

		assertEquals("Expected constraint violations do not match.", constraintViolations, customerRegistrationResult.getConstraintViolations());
		assertNull("Result customer should be null.", customerRegistrationResult.getRegisteredCustomer());
	}

	@Test(expected = EpValidationException.class)
	public void testRegisterCustomerWithConstraintViolations() {
		final Customer customer = context.mock(Customer.class);

		final Set<ConstraintViolation<Customer>> constraintViolations = new HashSet<>();
		@SuppressWarnings("unchecked")
		ConstraintViolation<Customer> violation = (ConstraintViolation<Customer>) context.mock(ConstraintViolation.class);
		constraintViolations.add(violation);

		context.checking(new Expectations() {
			{
				oneOf(customer).setAnonymous(false);
				allowing(customer).getUserId(); will(returnValue(USER_ID));
				oneOf(validator).validate(customer, PasswordChange.class); will(returnValue(constraintViolations));
				oneOf(constraintViolationTransformer).transform(constraintViolations); will(returnValue(Collections.emptyList()));
			}
		});

		customerRegistrationService.registerCustomer(customer);
	}

	@Test
	public void testRegisterCustomerAndSendPassword() throws EmailException {
		final Customer customer = context.mock(Customer.class);
		final Customer updatedCustomer = context.mock(Customer.class, "Updated Customer");

		context.checking(new Expectations() {
			{
				oneOf(customer).isRegistered(); will(returnValue(false));
				oneOf(customer).setAnonymous(false);
				oneOf(customer).resetPassword(); will(returnValue(PASSWORD));
				atLeast(1).of(customer).getUserId(); will(returnValue(USER_ID));
				oneOf(customer).getClearTextPassword(); will(returnValue(PASSWORD));
				oneOf(userIdentityService).setPassword(USER_ID, PASSWORD);
				oneOf(customerService).update(customer); will(returnValue(updatedCustomer));
				atLeast(1).of(updatedCustomer).getGuid(); will(returnValue(CUSTOMER_GUID));

				final EventMessage eventMessage = context.mock(EventMessage.class);
				final Map<String, Object> eventMessageData = Collections.<String, Object>singletonMap("password", PASSWORD);
				oneOf(eventMessageFactory).createEventMessage(CustomerEventType.ANONYMOUS_CUSTOMER_REGISTERED, CUSTOMER_GUID, eventMessageData);
				will(returnValue(eventMessage));

				oneOf(eventMessagePublisher).publish(eventMessage);
			}
		});

		Customer resultCustomer = customerRegistrationService.registerCustomerAndSendPassword(customer);

		assertEquals("Result customer should be same as updated customer.", updatedCustomer, resultCustomer);
	}

}
