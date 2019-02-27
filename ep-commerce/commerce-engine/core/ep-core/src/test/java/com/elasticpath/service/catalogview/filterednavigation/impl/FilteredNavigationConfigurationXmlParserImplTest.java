/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalogview.FilterType;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationXmlElementParserFactory;
import com.elasticpath.test.util.mock.PropertyEnabledExpectations;

/**
 * Test class for {@link FilteredNavigationConfigurationXmlParserImpl}.
 *
 */
public class FilteredNavigationConfigurationXmlParserImplTest extends BasicFilteredNavigationConfigurationXmlParser {

	private FilteredNavigationConfigurationXmlParserImpl filteredNavigationCoingurationXmlParserImpl;
	private FilteredNavigationConfiguration config;
	private FilteredNavigationConfigurationXmlElementParserFactory filteredNavigationConfigurationXmlElementParserFactory;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		this.filteredNavigationCoingurationXmlParserImpl = new FilteredNavigationConfigurationXmlParserImpl();
		this.config = new FilteredNavigationConfigurationImpl();

		filteredNavigationConfigurationXmlElementParserFactory = context.mock(FilteredNavigationConfigurationXmlElementParserFactory.class);
		filteredNavigationCoingurationXmlParserImpl.setFilteredNavigationConfigurationXmlElementParserFactory(
														filteredNavigationConfigurationXmlElementParserFactory);
		
		context.checking(new PropertyEnabledExpectations() {
			{
				allowing(filteredNavigationConfigurationXmlElementParserFactory)
							.getFilteredNavigationConfigurationXmlElementParser(FilterType.BRAND_FILTER);
				will(returnValue(new BrandFilterXmlElementParserImpl()));
			}
		});
	}

	
	/**
	 * Config should be cleared before new config added.
	 */
	@Test
	public void testConfigShouldBeClearedBeforeNewConfigIsAdded() {		
		parseWithContent("<brands><brand key='A0001'/><brand key='A0002'/></brands>");
		assertEquals(2, config.getAllBrandCodes().size());

		parseWithContent("<brands><brand key='ADIDAS'/></brands>");
		assertEquals(1, config.getAllBrandCodes().size());
	}	
	
	private void parseWithContent(final String content) {
		filteredNavigationCoingurationXmlParserImpl.parse(toStream(content), config);
	}

}

