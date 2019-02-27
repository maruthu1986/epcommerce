/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import static org.junit.Assert.assertEquals;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.impl.AddressDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class AddressToDtoTest {
	private static final String LASTNAME = "Doe";
	private static final String FIRSTNAME = "John";
	private static final String PHONENUMBER = "123-456-7890";
	private static final String FAXNUMBER = "098-765-4321";
	private static final String STREET1 = "123 Main Street";
	private static final String STREET2 = "Apt 100";
	private static final String CITY = "San Francisco";
	private static final String SUBCOUNTRY = "CA";
	private static final String ZIPORPOSTALCODE = "90210";
	private static final String COUNTRY = "US";
	private static final String FULLNAME = "John Doe";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final AddressToDto addressToDto = new AddressToDto();

	/**
	 * Set up and mocking.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ADDRESS_DTO, AddressDtoImpl.class);
		addressToDto.setBeanFactory(beanFactory);
	}

	@Test
	public void testConvert() throws Exception {
		Address source = new OrderAddressImpl();
		source.setLastName(LASTNAME);
		source.setFirstName(FIRSTNAME);
		source.setPhoneNumber(PHONENUMBER);
		source.setFaxNumber(FAXNUMBER);
		source.setStreet1(STREET1);
		source.setStreet2(STREET2);
		source.setCity(CITY);
		source.setSubCountry(SUBCOUNTRY);
		source.setZipOrPostalCode(ZIPORPOSTALCODE);
		source.setCountry(COUNTRY);

		AddressDto target = addressToDto.convert(source);
		assertEquals(FULLNAME, target.getFullName());
		assertEquals(LASTNAME, target.getLastName());
		assertEquals(FIRSTNAME, target.getFirstName());
		assertEquals(PHONENUMBER, target.getPhoneNumber());
		assertEquals(FAXNUMBER, target.getFaxNumber());
		assertEquals(STREET1, target.getStreet1());
		assertEquals(STREET2, target.getStreet2());
		assertEquals(CITY, target.getCity());
		assertEquals(SUBCOUNTRY, target.getSubCountry());
		assertEquals(ZIPORPOSTALCODE, target.getZipOrPostalCode());
		assertEquals(COUNTRY, target.getCountry());
	}
}
