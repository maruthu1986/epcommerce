/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.test.util.mock.PropertyEnabledExpectations;

/**
 * Test class for {@link AttributeValueFilterXmlElementParserImpl}.
 *
 */
public class AttributeValueFilterXmlElementParserImplTest extends BasicFilteredNavigationConfigurationXmlParser {

	private AttributeValueFilterXmlElementParserImpl attributeValueFilterXmlElementParserImpl;
	private FilteredNavigationConfiguration config;
	private FilterFactory filterFactory;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		this.attributeValueFilterXmlElementParserImpl = new AttributeValueFilterXmlElementParserImpl();
		this.config = new FilteredNavigationConfigurationImpl();

		filterFactory = context.mock(FilterFactory.class);
		attributeValueFilterXmlElementParserImpl.setFilterFactory(filterFactory);
	}

	/** When parsing languages with a country code, the language and variant of a locale should be populated correctly. */
	@Test
	public void testLocaleCountryForAttributeValueNode() {
		final String key = "key";
		final String attributeVal = "val";
		final String displayName = "display";
		final Locale locale = new Locale("en", "US", "POSIX");
		final String attributeFilterId = "01";

		final String content = String.format(
					"<attribute key=\"%s\" localized=\"true\">"
					+ 	"<simple id=\"" + attributeFilterId + "\" language=\"en_US_POSIX\" displayName=\"%s\" value=\"" + attributeVal + "\" />"
					+ "</attribute>",
				key,
				displayName);

		final AttributeValueFilter filter = context.mock(AttributeValueFilter.class);
		final Attribute attribute = context.mock(Attribute.class);

		final Map<String, Object> filterInitMap = new HashMap<>();
		filterInitMap.put(AttributeFilter.ATTRIBUTE_PROPERTY, attribute);
		filterInitMap.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, attributeFilterId);
		filterInitMap.put(AttributeValueFilter.ATTRIBUTE_VALUE_PROPERTY, attributeVal);

		context.checking(new PropertyEnabledExpectations() {
			{
				allowing(filterFactory).getFilterBean(ContextIdNames.ATTRIBUTE_FILTER);
				will(returnValue(filter));
				allowingProperty(filter).getId();
				allowingProperty(filter).getAttributeKey();
				allowing(filter).getAttribute();
				will(returnValue(attribute));
				allowingProperty(filter).isLocalized();
				allowing(filter).initialize(filterInitMap);
				allowing(filter).setDisplayName(displayName);
				allowing(filter).setLocale(locale);
				allowing(filter).getLocale();
				will(returnValue(locale));
				allowing(filter).compareTo(filter);
				will(returnValue(0));
				allowing(filter).getAttributeValue();
			}
		});


		parseWithContent(content);
		assertEquals(1, config.getAllAttributeSimpleValues().size());
		assertEquals("Filter with our key not created", filter, config.getAllAttributeSimpleValues().get(key));
	}

	
	@SuppressWarnings("unchecked")
	private void parseWithContent(final String content) {
		
		InputStream inputStream = toStream(content);
		
		Document doc = toDocument(inputStream);
		
		List<Element> children = doc.getRootElement().getChildren("attribute");
		
		for (Element element : children) {
			attributeValueFilterXmlElementParserImpl.parse(element, config);
		}
	}
	
}

