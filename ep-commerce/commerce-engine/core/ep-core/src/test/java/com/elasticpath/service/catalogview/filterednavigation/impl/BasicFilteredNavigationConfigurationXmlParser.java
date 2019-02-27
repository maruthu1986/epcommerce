/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 * Abstract Test class for testing the implementations of {@code FilteredNavigationConfigurationXmlElementParser} and 
 * {@code FilteredNavigationConfigurationParser}.
 *
 */
public class BasicFilteredNavigationConfigurationXmlParser {

	/**
	 * Converts a string into XML format and creates an input stream from it.
	 * 
	 * @param content the testing content
	 * @return an input steam of content in XML format
	 */
	protected InputStream toStream(final String content) {
			try {
				String xmlContent = "<?xml version='1.0' encoding='UTF-8'?>"
									+ "<IntelligentBrowsing>"
									+ content
									+ "</IntelligentBrowsing>";
				
				return new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				fail("Unsupported Encoding in test" + e);
			}
			return null;
	}
	
	/**
	 * Converts an input stream to a jdom document object.
	 * 
	 * @param inputStream the inputstream for a XML content
	 * @return a jdom document object
	 */
	protected Document toDocument(final InputStream inputStream) {

		try {
			final SAXBuilder builder = new SAXBuilder();
			return builder.build(inputStream);
		} catch (Exception e) {
			return null;
		}
	}

}

