/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalogview.impl.AttributeKeywordFilterImpl;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.test.util.mock.PropertyEnabledExpectations;

/**
 * Test class for {@link AttributeKeywordFilterXmlElementParserImpl}.
 *
 */
public class AttributeKeywordFilterXmlElementParserImplTest extends BasicFilteredNavigationConfigurationXmlParser {

	private AttributeKeywordFilterXmlElementParserImpl attributeKeywordFilterXmlElementParserImpl;
	private FilteredNavigationConfiguration config;
	private FilterFactory filterFactory;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		this.attributeKeywordFilterXmlElementParserImpl = new AttributeKeywordFilterXmlElementParserImpl();
		this.config = new FilteredNavigationConfigurationImpl();

		filterFactory = context.mock(FilterFactory.class);
		attributeKeywordFilterXmlElementParserImpl.setFilterFactory(filterFactory);
	}

	/**
	 * Tests parsing attribute key word.
	 */
	@Test
	public void testParseTwoKeywordAttribute() {
		
		context.checking(new PropertyEnabledExpectations() {
			{
				allowing(filterFactory).getFilterBean(ContextIdNames.ATTRIBUTE_KEYWORD_FILTER);
				will(returnValue(new AttributeKeywordFilterImpl() {
					private static final long serialVersionUID = -7870906218902871495L;

					@Override
					public void setAttributeKey(final String attributeKey) {
						super.setAttribute(new AttributeImpl());
					}
				}));
			}
		});
		
		parseWithContent("<attributeKeyword key = \"A00373\"/><attributeKeyword key = \"A00100\"/>");
		assertEquals(2, config.getAllAttributeKeywords().size());
		assertTrue(config.getAllAttributeKeywords().containsKey("A00373"));
		assertTrue(config.getAllAttributeKeywords().containsKey("A00100"));
	}
	
	/**
	 * Tests parsing attribute key word.
	 */
	@Test
	public void testParseOneKeywordAttribute() {
		
		context.checking(new PropertyEnabledExpectations() {
			{
				allowing(filterFactory).getFilterBean(ContextIdNames.ATTRIBUTE_KEYWORD_FILTER);
				will(returnValue(new AttributeKeywordFilterImpl() {
					private static final long serialVersionUID = -7870906218902871495L;

					@Override
					public void setAttributeKey(final String attributeKey) {
						super.setAttribute(new AttributeImpl());
					}
				}));
			}
		});
		
		parseWithContent("<attributeKeyword key = \"A00373\"/>");
		assertEquals(1, config.getAllAttributeKeywords().size());
		assertTrue(config.getAllAttributeKeywords().containsKey("A00373"));
	}
	
	@SuppressWarnings("unchecked")
	private void parseWithContent(final String content) {
		
		InputStream inputStream = toStream(content);
		
		Document doc = toDocument(inputStream);
		
		List<Element> children = doc.getRootElement().getChildren("attributeKeyword");
		
		for (Element element : children) {
			attributeKeywordFilterXmlElementParserImpl.parse(element, config);
		}
	}
	
}

