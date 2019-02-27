/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.epcommerce.events;

import static com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants.PUBLIC_ROLENAME;
import static com.elasticpath.rest.resource.integration.epcommerce.common.authentication.AuthenticationConstants.REGISTERED_ROLE;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.events.RoleTransitionEvent;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;

@RunWith(MockitoJUnitRunner.class)
public class AnonymousToRegisteredTransitionEventHandlerTest {

	private static final String SCOPE = "scope";

	private static final String OLD_USER_GUID = "oldUserGuid";
	private static final String NEW_USER_GUID = "newUserGuid";

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private RoleTransitionEvent mockRoleTransitionEvent;

	@Mock
	private Customer mockRecipientCustomer;

	@Mock
	private CustomerSessionRepository mockCustomerSessionRepository;

	@Mock
	private CustomerSession mockCustomerSession;

	@InjectMocks
	private AnonymousToRegisteredTransitionEventHandler classToTest;

	/**
	 * Test two different roles. Should return nothing
	 * @throws Exception
	 */
	@Test
	public void testOldRoleNotSameAsNewRoleShouldNotMerge() throws Exception {
		when(mockRoleTransitionEvent.getOldRole()).thenReturn("nonExistentRole");

		classToTest.handleEvent(SCOPE, mockRoleTransitionEvent);

		verify(mockRoleTransitionEvent, never()).getOldUserGuid();
		verify(mockRoleTransitionEvent, never()).getNewUserGuid();
	}


	@Test
	public void shouldMergeAnonymousToRegisteredCustomerSession() throws Exception {
		mockRoleTransition();
		mockGUIDTransition();

		ExecutionResult<CustomerSession> customerSession = ExecutionResultFactory.createReadOK(mockCustomerSession);
		ExecutionResult<Object> mergedCustomerSession = ExecutionResultFactory.createUpdateOK();

		when(mockCustomerSessionRepository.findCustomerSessionByGuid(anyString())).thenReturn(customerSession);

		when(customerRepository.mergeCustomer(mockCustomerSession, mockRecipientCustomer, SCOPE)).thenReturn(mergedCustomerSession);

		classToTest.handleEvent(SCOPE, mockRoleTransitionEvent);

		verify(mockRoleTransitionEvent).getNewUserGuid();
		verify(mockRoleTransitionEvent).getOldUserGuid();
		verify(customerRepository).findCustomerByGuid(NEW_USER_GUID);
		verify(mockCustomerSessionRepository).findCustomerSessionByGuid(OLD_USER_GUID);
		verify(customerRepository).mergeCustomer(any(CustomerSession.class), any(Customer.class), any(String.class));
	}

	private void mockGUIDTransition() {
		ExecutionResult<Customer> recipientCustomer = ExecutionResultFactory.createReadOK(mockRecipientCustomer);
		when(mockRoleTransitionEvent.getOldUserGuid()).thenReturn(OLD_USER_GUID);
		when(mockRoleTransitionEvent.getNewUserGuid()).thenReturn(NEW_USER_GUID);
		when(customerRepository.findCustomerByGuid(NEW_USER_GUID)).thenReturn(recipientCustomer);
	}

	private void mockRoleTransition() {
		when(mockRoleTransitionEvent.getOldRole()).thenReturn(PUBLIC_ROLENAME);
		when(mockRoleTransitionEvent.getNewRole()).thenReturn(REGISTERED_ROLE);
	}
}
