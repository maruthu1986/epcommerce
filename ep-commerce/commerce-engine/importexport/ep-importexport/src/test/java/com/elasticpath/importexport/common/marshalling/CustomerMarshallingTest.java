/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.marshalling;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.importexport.common.factory.TestCustomerDTOBuilderFactory;

/**
 * Test XML marshalling of a customer.
 */
public class CustomerMarshallingTest {
	private final XMLMarshaller xmlMarshaller = new XMLMarshaller(CustomerDTO.class);
	private final TestCustomerDTOBuilderFactory testCustomerDTOBuilderFactory = new TestCustomerDTOBuilderFactory();

	/**
	 * Test to ensure marshalling of customer with payment methods produces correct XML representation.
	 *
	 * @throws Exception if a problem occurs reading the output stream created by the marshaller
	 */
	@Test
	public void ensureMarshallingOfCustomerWithPaymentMethodsProducesCorrectXmlRepresentation() throws Exception {
		XMLUnit.setIgnoreWhitespace(true);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		xmlMarshaller.marshal(testCustomerDTOBuilderFactory
				.createWithPaymentMethods()
				.build(),
				out);

		String expectedCustomerXmlRepresentation = IOUtils.toString(getClass().getClassLoader()
				.getResourceAsStream("customers/testCustomerXmlRepresentation.xml"), "UTF-8");
		assertXMLEqual("The XML representation should be the same as expected", expectedCustomerXmlRepresentation,
				out.toString());
	}
}
