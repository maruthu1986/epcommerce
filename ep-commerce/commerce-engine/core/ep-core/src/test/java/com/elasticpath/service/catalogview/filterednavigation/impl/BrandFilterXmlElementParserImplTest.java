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

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;

/**
 * Test class for {@link BrandFilterXmlElementParserImpl}.
 *
 */
public class BrandFilterXmlElementParserImplTest extends BasicFilteredNavigationConfigurationXmlParser {

	private BrandFilterXmlElementParserImpl brandFilterXmlElementParserImpl;
	private FilteredNavigationConfiguration config;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		this.brandFilterXmlElementParserImpl = new BrandFilterXmlElementParserImpl();
		this.config = new FilteredNavigationConfigurationImpl();
	}

	/**
	 * Shouldn't fail to parse when there is not 'brands' element.
	 */
	@Test
	public void testParserSkipsBrandsWhenNotSpecified() {		
		parseWithContent("");
		assertEquals(0, config.getAllBrandCodes().size());
	}
	
	/**
	 * Parse empty brands section.
	 */
	@Test
	public void testEmptyBrandsSection() {
		parseWithContent("<brands/>");
		assertEquals(0, config.getAllBrandCodes().size());
	}

	/**
	 * Parse two brand.
	 */
	@Test
	public void testParseTwoBrands() {		
		parseWithContent("<brands><brand key='NIKE'/><brand key='PUMA'/></brands>");

		assertEquals(2, config.getAllBrandCodes().size());
		assertTrue(config.getAllBrandCodes().contains("NIKE"));
		assertTrue(config.getAllBrandCodes().contains("PUMA"));
	}
	
	/**
	 * A brand element with no 'key' attribute will throw an exception.
	 */
	@Test(expected = EpPersistenceException.class)
	public void testParseBrandWithMalFormedAttribute() {
		parseWithContent("<brands><brand name='NIKE'/></brands>");
	}
	
	/**
	 * Duplicate brand entries should not result in multiple config elements.
	 */
	@Test
	public void testDuplicateBrandCodesNotDuplicatedInConfig() {		
		parseWithContent("<brands><brand key='NIKE'/><brand key='NIKE'/></brands>");
		
		assertEquals(1, config.getAllBrandCodes().size());
		assertEquals("NIKE", config.getAllBrandCodes().iterator().next());
	}	

	private void parseWithContent(final String content) {
		
		InputStream inputStream = toStream(content);
		
		Document doc = toDocument(inputStream);
		brandFilterXmlElementParserImpl.parse(doc.getRootElement().getChild("brands"), config);
	}
	
}

