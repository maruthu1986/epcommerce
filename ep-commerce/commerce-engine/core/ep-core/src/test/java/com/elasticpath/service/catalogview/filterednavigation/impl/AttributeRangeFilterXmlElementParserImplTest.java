/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.jdom.Document;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.test.util.mock.PropertyEnabledExpectations;

/**
 * Test class for {@link AttributeRangeFilterXmlElementParserImpl}.
 */
public class AttributeRangeFilterXmlElementParserImplTest extends BasicFilteredNavigationConfigurationXmlParser {

	private static final int NUMBER_3 = 3;

	private static final String ATTRIBUTE_RANGE_PREFIX = "ar";

	private static final String ATTRIBUTE_KEY = "ATTRIBUTE_KEY";

	private static final String LOWER_RANGE_1 = "LOWER_RANGE_1";

	private static final String UPPER_RANGE_1 = "UPPER_RANGE_1";

	private static final String ATTRIBUTE_ID_1 = "ATTRIBUTE_ID_1";

	private static final String ATTRIBUTE_VALUE_1 = "ATTRIBUTE_VALUE_1";

	private static final String SEO_VALUE_1 = "SEO_VALUE_1";

	private static final String LOWER_RANGE_2 = "LOWER_RANGE_2";

	private static final String UPPER_RANGE_2 = "UPPER_RANGE_2";

	private static final String ATTRIBUTE_ID_2 = "ATTRIBUTE_ID_2";

	private static final String ATTRIBUTE_VALUE_2 = "ATTRIBUTE_VALUE_2";

	private static final String SEO_VALUE_2 = "SEO_VALUE_2";

	private static final String ATTRIBUTE_RANGE_OPENING = "<attributeRange key=\"" + ATTRIBUTE_KEY + "\" localized=\"false\">";

	private static final String ATTRIBUTE_RANGE_CLOSING = "</attributeRange>";

	private AttributeRangeFilterXmlElementParserImpl attributeRangeFilterXmlElementParserImpl;

	private FilteredNavigationConfiguration config;

	private FilterFactory filterFactory;

	private AttributeValueWithType productAttributeValueImpl;

	private Attribute attribute;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		attributeRangeFilterXmlElementParserImpl = new AttributeRangeFilterXmlElementParserImpl();
		config = new FilteredNavigationConfigurationImpl();

		filterFactory = context.mock(FilterFactory.class);
		attributeRangeFilterXmlElementParserImpl.setFilterFactory(filterFactory);

		productAttributeValueImpl = context.mock(AttributeValueWithType.class);
		attribute = context.mock(Attribute.class);

		context.checking(new PropertyEnabledExpectations() {
			{
				allowing(productAttributeValueImpl).compareTo(productAttributeValueImpl);
				will(returnValue(0));
				allowing(productAttributeValueImpl).getAttribute();
				will(returnValue(attribute));

				allowing(productAttributeValueImpl).setAttribute(attribute);
				allowing(productAttributeValueImpl).setAttributeType(AttributeType.SHORT_TEXT);
				allowing(productAttributeValueImpl).setStringValue(LOWER_RANGE_1);
				allowing(productAttributeValueImpl).setStringValue(UPPER_RANGE_1);
				allowing(productAttributeValueImpl).setStringValue(LOWER_RANGE_2);
				allowing(productAttributeValueImpl).setStringValue(UPPER_RANGE_2);

				allowing(attribute).getAttributeType();
				will(returnValue(AttributeType.SHORT_TEXT));
				allowing(attribute).getKey();
				will(returnValue(ATTRIBUTE_KEY));

				allowing(filterFactory).getFilterBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER);
				will(returnValue(new AttributeRangeFilterImpl() {
					private static final long serialVersionUID = 7381787975203941219L;

					@Override
					public void setAttributeKey(final String attributeKey) {
						super.setAttribute(attribute);
					}

					@SuppressWarnings("unchecked")
					@Override
					protected <T> T getBean(final String beanName) {
						Object object = null;
						if (ContextIdNames.ATTRIBUTE_VALUE.equals(beanName)) {
							object = productAttributeValueImpl;
						}
						return (T) object;
					}
				}));
			}
		});
	}

	/**
	 * Shouldn't fail to parse when there is no attribute range element.
	 */
	@Test
	public void testParserMissingAttributeRangeSection() {
		config.clearAllAttributeRanges();
		parseWithContent("");
		assertEquals(0, config.getAllAttributeRanges().size());
	}

	/**
	 * Parse empty attribute range section.
	 */
	@Test
	public void testParserEmptyAttributeRangeSection() {
		config.clearAllAttributeRanges();
		StringBuilder content = new StringBuilder(ATTRIBUTE_RANGE_OPENING);
		content.append(ATTRIBUTE_RANGE_CLOSING);
		parseWithContent(content.toString());

		assertEquals(1, config.getAllAttributeRanges().size());
		assertTrue(config.getAllAttributeRanges().containsKey(ATTRIBUTE_KEY));
	}

	/**
	 * Parse single attribute range section.
	 */
	@Test
	public void testParserSingleAttributeRangeSection() {
		config.clearAllAttributeRanges();
		StringBuilder content = new StringBuilder(ATTRIBUTE_RANGE_OPENING);
		content.append(createRangeElement(LOWER_RANGE_1, UPPER_RANGE_1, ATTRIBUTE_ID_1, ATTRIBUTE_VALUE_1, SEO_VALUE_1));
		content.append(ATTRIBUTE_RANGE_CLOSING);
		parseWithContent(content.toString());

		assertEquals(2, config.getAllAttributeRanges().size());
		assertTrue(config.getAllAttributeRanges().containsKey(ATTRIBUTE_KEY));
		assertTrue(config.getAllAttributeRanges().containsKey(ATTRIBUTE_RANGE_PREFIX + ATTRIBUTE_KEY + "_" + ATTRIBUTE_ID_1));
	}

	/**
	 * Parse multiple attribute range section.
	 */
	@Test
	public void testParserMultipleAttributeRanges() {
		config.clearAllAttributeRanges();
		StringBuilder content = new StringBuilder(ATTRIBUTE_RANGE_OPENING);
		content.append(createRangeElement(LOWER_RANGE_1, UPPER_RANGE_1, ATTRIBUTE_ID_1, ATTRIBUTE_VALUE_1, SEO_VALUE_1));
		content.append(createRangeElement(LOWER_RANGE_2, UPPER_RANGE_2, ATTRIBUTE_ID_2, ATTRIBUTE_VALUE_2, SEO_VALUE_2));
		content.append(ATTRIBUTE_RANGE_CLOSING);
		parseWithContent(content.toString());

		assertEquals(NUMBER_3, config.getAllAttributeRanges().size());
		assertTrue(config.getAllAttributeRanges().containsKey(ATTRIBUTE_KEY));
		assertTrue(config.getAllAttributeRanges().containsKey(ATTRIBUTE_RANGE_PREFIX + ATTRIBUTE_KEY + "_" + ATTRIBUTE_ID_1));
		assertTrue(config.getAllAttributeRanges().containsKey(ATTRIBUTE_RANGE_PREFIX + ATTRIBUTE_KEY + "_" + ATTRIBUTE_ID_2));
	}

	private String createRangeElement(final String lowerRange, final String upperRange, final String attributeId, final String attributeValue,
			final String seoValue) {
		return String.format(
			"<range lower=\"%s\" upper=\"%s\" id=\"%s\">"
			+ "<display language=\"en_US_POSIX\">"
			+ "<value>%s</value>"
			+ "<seo>%s</seo>"
			+ "</display>"
			+ "</range>",			
			lowerRange, upperRange, attributeId, attributeValue, seoValue);
	}

	private void parseWithContent(final String content) {
		InputStream inputStream = toStream(content);
		Document doc = toDocument(inputStream);
		attributeRangeFilterXmlElementParserImpl.parse(doc.getRootElement().getChild("attributeRange"), config);
	}
}
