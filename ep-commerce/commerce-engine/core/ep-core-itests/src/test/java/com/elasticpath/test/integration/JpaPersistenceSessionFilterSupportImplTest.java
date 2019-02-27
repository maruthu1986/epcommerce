/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSessionFilterSupport;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

public class JpaPersistenceSessionFilterSupportImplTest extends BasicSpringContextTest {
	private static final Logger log = Logger.getLogger(JpaPersistenceSessionFilterSupportImplTest.class);

	@Autowired
	private PersistenceSessionFilterSupport filterSupport;

	@Autowired
	@Qualifier("customerService")
	private CustomerService customerService;

	private SimpleStoreScenario scenario;

	@Autowired
	private PersistenceEngine persistenceEngine;

	@Autowired
	private BeanFactory beanFactory;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		log.trace("setUp() Complete");
	}

	@DirtiesDatabase
	@Test
	public void testLoadObjectTwiceWithDifferentSession() {
		log.trace("testLoadObjectTwiceWithDifferentSession");
		Customer persisted = createPersistedCustomer("x@x.com", scenario.getStore());

		Customer first = customerService.findByGuid(persisted.getGuid());
		Customer second = customerService.findByGuid(persisted.getGuid());

		assertNotSame("Objects should not be the same instance", first, second);
	}

	@DirtiesDatabase
	@Test
	public void testLoadObjectTwiceWithSessionFilterActive() {
		log.trace("testLoadObjectTwiceWithDifferentSession");
		Customer persisted = createPersistedCustomer("x@x.com", scenario.getStore());

		filterSupport.openSharedSession();
		try {
			Customer first = customerService.findByGuid(persisted.getGuid());
			Customer second = customerService.findByGuid(persisted.getGuid());

			assertSame("Objects should be the same instance", first, second);
		} finally {
			filterSupport.closeSharedSession();
		}
	}

	@DirtiesDatabase
	@Test
	public void testLoadAndSaveWithSessionFilterActiveAndATransaction() {
		log.trace("testLoadObjectTwiceWithDifferentSession");
		Customer persisted = createPersistedCustomer("x@x.com", scenario.getStore());

		filterSupport.openSharedSession();
		try {
			Customer first = customerService.findByGuid(persisted.getGuid());
			first.setFirstName("Jane");

			Customer updated = customerService.update(first);
		} finally {
			filterSupport.closeSharedSession();
		}

		Customer reloaded = customerService.findByGuid(persisted.getGuid());
		assertEquals("Jane", reloaded.getFirstName());
	}

	private Customer createPersistedCustomer(final String emailAddress, final Store store) {
		final Customer customer = beanFactory.getBean(ContextIdNames.CUSTOMER);
		customer.setEmail(emailAddress);
		customer.setStoreCode(store.getCode());
		customer.setAnonymous(false);
		customer.setLastName("Doe");
		customer.setFirstName("John");

		CustomerAddress address = beanFactory.getBean(ContextIdNames.CUSTOMER_ADDRESS);
		address.setCity("Vancouver");
		address.setZipOrPostalCode("V6J5G4");
		address.setFirstName("John");
		address.setLastName("Doe");
		address.setStreet1("1234 Pine Street");
		address.setSubCountry("BC");
		address.setCountry("CA");

		CustomerAddress address2 = beanFactory.getBean(ContextIdNames.CUSTOMER_ADDRESS);
		address2.setCity("St. John's");
		address2.setZipOrPostalCode("V6J5G4");
		address2.setFirstName("John");
		address2.setLastName("Doe");
		address2.setStreet1("28 Main Street");
		address2.setSubCountry("NL");
		address2.setCountry("CA");

		customer.addAddress(address);

		return customerService.add(customer);
	}
}
