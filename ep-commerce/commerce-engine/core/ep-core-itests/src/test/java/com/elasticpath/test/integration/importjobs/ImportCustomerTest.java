/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test import job for Customer.
 */
public class ImportCustomerTest extends ImportJobTestCase {

	@Autowired
	private Utility utility;

	@Autowired
	private CustomerService customerService;

	private Locale locale = Locale.ENGLISH;

	/**
	 * Test import Customer insert.
	 */
	@DirtiesDatabase
	@Test
	public void testImportCustomerInsert() throws Exception {
		executeImportJob(createInsertCustomerImportJob());

		Customer customer = customerService.findByGuid("101");

		assertEquals("101", customer.getGuid());
		assertEquals("john@connor.com", customer.getUserId());
		assertEquals("John", customer.getFirstName());
		assertEquals("Connor", customer.getLastName());
		assertEquals("john@connor.com", customer.getEmail());
		assertFalse(customer.isAnonymous());
		assertTrue(customer.isHtmlEmailPreferred());
		assertEquals(1, customer.getStatus());
		assertEquals(string2Date("Fri Mar 16 15:49:37 2007", locale), customer.getCreationDate());
		assertEquals("4444444444", customer.getPhoneNumber());
	}

	/**
	 * Test import Customer insert/update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportCustomerInsertUpdate() throws Exception {
		executeImportJob(createInsertCustomerImportJob());
		executeImportJob(createInsertUpdateCustomerImportJob());

		// assert existing customer was not changed during update
		Customer customer = customerService.findByGuid("101");
		assertEquals("101", customer.getGuid());
		assertEquals("john@connor.com", customer.getUserId());
		assertEquals("John", customer.getFirstName());
		assertEquals("Connor", customer.getLastName());
		assertEquals("john@connor.com", customer.getEmail());
		assertFalse(customer.isAnonymous());
		assertTrue(customer.isHtmlEmailPreferred());
		assertEquals(1, customer.getStatus());
		assertEquals(string2Date("Fri Mar 16 15:49:37 2007", locale), customer.getCreationDate());
		assertEquals("4444444444", customer.getPhoneNumber());

		// assert existing customer has been updated during import
		Customer customer2 = customerService.findByGuid("102");
		assertEquals("102", customer2.getGuid());
		assertEquals("will@smith.com", customer2.getUserId());
		assertEquals("Willie", customer2.getFirstName()); // has been modified
		assertEquals("Smith", customer2.getLastName());
		assertEquals("willie@smith.com", customer2.getEmail()); // has been modified
		assertFalse(customer2.isAnonymous());
		assertTrue(customer2.isHtmlEmailPreferred());
		assertEquals(1, customer2.getStatus());
		assertEquals(string2Date("Fri Mar 17 15:49:37 2007", locale), customer2.getCreationDate());
		assertEquals("9999999999", customer2.getPhoneNumber()); // has been modified

		// assert new customer has been created during import
		Customer customer3 = customerService.findByGuid("103");
		assertEquals("103", customer3.getGuid());
		assertEquals("george@michael.com", customer3.getUserId());
		assertEquals("George", customer3.getFirstName());
		assertEquals("Michael", customer3.getLastName());
		assertEquals("george@michael.com", customer3.getEmail());
		assertFalse(customer3.isAnonymous());
		assertTrue(customer3.isHtmlEmailPreferred());
		assertEquals(1, customer3.getStatus());
		assertEquals(string2Date("Fri Mar 17 15:49:37 2007", locale), customer3.getCreationDate());
		assertEquals("0101010101", customer3.getPhoneNumber());

	}

	/**
	 * Test import Customer update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportCustomerUpdate() throws Exception {
		executeImportJob(createInsertCustomerImportJob());
		executeImportJob(createUpdateCustomerImportJob());

		Customer customer = customerService.findByGuid("101");
		assertEquals("101", customer.getGuid());
		assertEquals("john@connor.com", customer.getUserId());
		assertEquals("John", customer.getFirstName());
		assertEquals("Connor", customer.getLastName());
		assertEquals("john@connor.net", customer.getEmail()); // has been modified
		assertFalse(customer.isAnonymous());
		assertTrue(customer.isHtmlEmailPreferred());
		assertEquals(1, customer.getStatus());
		assertEquals(string2Date("Fri Mar 16 15:49:37 2007", locale), customer.getCreationDate());
		assertEquals("8888888888", customer.getPhoneNumber()); // has been modified
	}

	/**
	 * Test input Customer delete.
	 */
	@DirtiesDatabase
	@Test
	public void testImportCustomerDelete() throws Exception {
		executeImportJob(createInsertCustomerImportJob());
		executeImportJob(createDeleteCustomerImportJob());

		assertNull(customerService.findByGuid("101"));
		assertNotNull(customerService.findByGuid("102"));
	}
}
