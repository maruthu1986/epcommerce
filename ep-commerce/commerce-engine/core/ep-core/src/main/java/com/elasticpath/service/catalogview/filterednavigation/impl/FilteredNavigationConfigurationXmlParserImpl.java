/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.elasticpath.domain.catalogview.FilterType;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationParser;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationXmlElementParserFactory;

/**
 * Parses the XML representation of the filtered navigation configuration; creates all the filters
 * defined by the configuration and populates a given FilteredNavigationConfiguration object.
 */
public class FilteredNavigationConfigurationXmlParserImpl implements FilteredNavigationConfigurationParser {
	private static final Logger LOG = Logger.getLogger(FilteredNavigationConfigurationXmlParserImpl.class);

	private static final String ATTRIBUTE = "attribute";

	private static final String ATTRIBUTE_RANGE = "attributeRange";
	
	private static final String ATTRIBUTE_KEYWORD = "attributeKeyword";

	private static final String PRICE = "price";

	private static final String BRANDS = "brands";

	private FilteredNavigationConfigurationXmlElementParserFactory filteredNavigationConfigurationXmlElementParserFactory;

	/**
	 * Parses the given FilteredNavigation XML configuration file and populated the
	 * given configuration object.
	 * @param filteredNavigationConfigurationXml the xml stream to parse
	 * @param config the configuration object to populate
	 */
	@Override
	public void parse(final InputStream filteredNavigationConfigurationXml, final FilteredNavigationConfiguration config) {
		// Create the document
		final Document doc = constructIntelligentBrowsingXmlDocument(filteredNavigationConfigurationXml);

		// Reset any existing price ranges to avoid duplicate entries when the file is
		// read multiple times due to restart by the application server
		config.clearAllPriceRanges();
		config.clearAllAttributeRanges();
		config.clearAllAttributeSimpleValues();
		config.clearAllBrandCodes();
		config.clearAllAttributeKeywords();
		
		final Element msRootNode = doc.getRootElement();
		final List<Element> children = getChildren(msRootNode);
		for (Element sectionElement : children) {
			
			String elementName = sectionElement.getName();
			
			// if the element does not have name, then ignore element
			if (StringUtils.isBlank(elementName)) {
				continue;
			}
			
			if (elementName.equals(ATTRIBUTE)) {
				parseElement(FilterType.ATTRIBUTE_FILTER, sectionElement, config);
			} else if (elementName.equals(ATTRIBUTE_RANGE)) {
				parseElement(FilterType.ATTRIBUTE_RANGE_FILTER, sectionElement, config);
			} else if (elementName.equals(ATTRIBUTE_KEYWORD)) {
				parseElement(FilterType.ATTRIBUTE_KEYWORD_FILTER, sectionElement, config);
			} else if (elementName.equals(PRICE)) {
				parseElement(FilterType.PRICE_FILTER, sectionElement, config);
			} else if (elementName.equals(BRANDS)) {
				parseElement(FilterType.BRAND_FILTER, sectionElement, config);
			} else {
				LOG.warn("Could not find a filter parser for the element " + elementName);
			}
		}
	}
	
	private void parseElement(final FilterType filterType, final Element element, final FilteredNavigationConfiguration config) {
		
		getFilteredNavigationConfigurationXmlElementParserFactory()
			.getFilteredNavigationConfigurationXmlElementParser(filterType).parse(element, config);
	}

	/**
	 * Get the intelligent browsing configuration xml from the settings service
	 * and parse it into a JDOM Document.
	 * @param intelligentBrowsingXmlStream the XML document as a string
	 * @return the Document, or null if there was a parsing error
	 */
	Document constructIntelligentBrowsingXmlDocument(final InputStream intelligentBrowsingXmlStream) {
		// Build the document with SAX and Xerces, no validation
		final SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(intelligentBrowsingXmlStream);
		} catch (JDOMException | IOException e) {
			LOG.error("Exception parsing intelligent browsing xml configuration.", e);
		}
		return doc;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getChildren(final Element node) {
		return node.getChildren();
	}
	
	public FilteredNavigationConfigurationXmlElementParserFactory getFilteredNavigationConfigurationXmlElementParserFactory() {
		return filteredNavigationConfigurationXmlElementParserFactory;
	}

	public void setFilteredNavigationConfigurationXmlElementParserFactory(
			final FilteredNavigationConfigurationXmlElementParserFactory filteredNavigationConfigurationXmlElementParserFactory) {
		this.filteredNavigationConfigurationXmlElementParserFactory = filteredNavigationConfigurationXmlElementParserFactory;
	}
}
