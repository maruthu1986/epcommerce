/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.attribute.impl.TransientAttributeValueImpl;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeKeywordFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>AttributeKeywordFilterImpl</code>.
 */
public class AttributeKeywordFilterImplTest {

	private static final String A00001 = "A00001_";

	private static final String JPEG = "JPEG";

	private static final String RAW = "RAW";

	private AttributeKeywordFilterImpl attributeKeywordFilter;

	private BeanFactoryExpectationsFactory expectationsFactory;

	private BeanFactory beanFactory;

	private AttributeService attributeService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		attributeService = context.mock(AttributeService.class);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_SERVICE, attributeService);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_USAGE, AttributeUsageImpl.class);

		context.checking(new Expectations() {
			{
				allowing(attributeService).findByKey("A00001"); will(returnValue(getAttribute("A00001")));
				allowing(attributeService).findByKey("PR_Author"); will(returnValue(getAttribute("PR_Author")));
			}
		});
		this.attributeKeywordFilter = getAttributeKeywordFilter();
	}

	private AttributeKeywordFilterImpl getAttributeKeywordFilter() {
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_KEYWORD_FILTER, AttributeKeywordFilterImpl.class);

		return beanFactory.getBean(ContextIdNames.ATTRIBUTE_KEYWORD_FILTER);
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.getId()'.
	 */
	@Test
	public void testGetId() {
		assertNull(attributeKeywordFilter.getId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.getDisplayName(String)'.
	 */
	@Test
	public void testGetDisplayName() {
		final String filterId = SeoConstants.ATTRIBUTE_KEYWORD_FILTER_PREFIX + A00001 + "SlBFRw";
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeKeywordFilter.initialize(filterId);
		assertEquals(filterId, attributeKeywordFilter.getId());
		assertEquals(JPEG, attributeKeywordFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.getDisplayName(String)'.
	 */
	@Test
	public void testGetDisplayNameWhereBase64AddsEqualsSign() {
		String testString = "biology";
		assertTrue("The test string should be encoded to a value ending with equals sign",
				Base64.encodeBase64String(testString.getBytes()).endsWith("="));

		final String filterId = SeoConstants.ATTRIBUTE_KEYWORD_FILTER_PREFIX + A00001 + "YmlvbG9neQ";
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeKeywordFilter.initialize(filterId);
		assertEquals(filterId, attributeKeywordFilter.getId());
		assertEquals(testString, attributeKeywordFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.getDisplayName(String)'.
	 */
	@Test
	public void testGetDisplayNameWithoutInitialization() {
		assertNull(attributeKeywordFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test that explicitly setting the display name of an attributeKeywordFilter overrides the
	 * default behavior.
	 */
	@Test
	public void testGetDisplayNameWithExplicitDispayNameOverride() {
		final String filterId = SeoConstants.ATTRIBUTE_KEYWORD_FILTER_PREFIX + A00001 + Base64.encodeBase64String(JPEG.getBytes());
		attributeKeywordFilter.setDisplayName("test display name");
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeKeywordFilter.initialize(filterId);
		assertEquals("test display name", attributeKeywordFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test that explicitly setting the display name of an attributeKeywordFilter to
	 * null doesn't cause a failure, and therefore keeps the storefront
	 * resilient.
	 */
	@Test
	public void testGetDisplayNameWithExplicitDispayNameOverrideOfNull() {
		final String filterId = SeoConstants.ATTRIBUTE_KEYWORD_FILTER_PREFIX + A00001 + Base64.encodeBase64String(JPEG.getBytes());
		attributeKeywordFilter.setDisplayName(null);
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeKeywordFilter.initialize(filterId);
		assertEquals("Null display name was set, fallback should have been attribute value", JPEG,
				attributeKeywordFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test that explicitly setting the display name of an attributeKeywordFilter to
	 * blank doesn't cause a failure, and therefore keeps the storefront
	 * resilient.
	 */
	@Test
	public void testGetDisplayNameWithExplicitDispayNameOverrideOfBlank() {
		final String filterId = SeoConstants.ATTRIBUTE_KEYWORD_FILTER_PREFIX + A00001 + Base64.encodeBase64String(JPEG.getBytes());
		attributeKeywordFilter.setDisplayName("   ");
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeKeywordFilter.initialize(filterId);
		assertEquals("Blank display name was set, null should have been returned", JPEG, attributeKeywordFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.initialize(String)'.
	 */
	@Test(expected = EpCatalogViewRequestBindException.class)
	public void testInitializeWithBadId1() {
		this.attributeKeywordFilter.initialize("bad filter id");
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.initialize(String)'.
	 */
	@Test(expected = EpCatalogViewRequestBindException.class)
	public void testInitializeWithBadId2() {
		this.attributeKeywordFilter.initialize("A00001-aaa");
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.initialize(String)'.
	 */
	@Test(expected = EpCatalogViewRequestBindException.class)
	public void testInitializeWithBadId3() {
		this.attributeKeywordFilter.initialize("A00001");
	}
	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.initialize(String)'.
	 */
	@Test(expected = EpCatalogViewRequestBindException.class)
	public void testInitializeWithBadId4() {
		this.attributeKeywordFilter.initialize("A00001-333");
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.equals()'.
	 */
	@Test
	public void testEquals() {
		final String filterId = SeoConstants.ATTRIBUTE_KEYWORD_FILTER_PREFIX + A00001 + Base64.encodeBase64String(JPEG.getBytes());
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeKeywordFilter.initialize(filterId);

		final AttributeKeywordFilterImpl anotherattributeKeywordFilter = getAttributeKeywordFilter();
		anotherattributeKeywordFilter.initialize(filterId);

		assertEquals(attributeKeywordFilter, anotherattributeKeywordFilter);
		assertEquals(attributeKeywordFilter.hashCode(), anotherattributeKeywordFilter.hashCode());
		assertFalse(attributeKeywordFilter.equals(new Object()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.compare(Object)'.
	 */
	@Test
	public void testCompare() {
		final String filterId1 = SeoConstants.ATTRIBUTE_KEYWORD_FILTER_PREFIX + A00001 + Base64.encodeBase64String(JPEG.getBytes());
		final String filterId2 = SeoConstants.ATTRIBUTE_KEYWORD_FILTER_PREFIX + A00001 + Base64.encodeBase64String(RAW.getBytes());
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeKeywordFilter.initialize(filterId1);

		AttributeKeywordFilterImpl anotherattributeFilter = getAttributeKeywordFilter();
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		anotherattributeFilter.initialize(filterId1);

		assertEquals(0, attributeKeywordFilter.compareTo(anotherattributeFilter));

		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		anotherattributeFilter.initialize(filterId2);
		assertTrue(attributeKeywordFilter.compareTo(anotherattributeFilter) < 0);
		assertTrue(anotherattributeFilter.compareTo(attributeKeywordFilter) > 0);
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeKeywordFilterImpl.getSeoId()'.
	 */
	@Test
	public void testGetSeoId() {
		String attributeKey = "PR_Author";
		String attributeKeyword = "Steven peter";

		final String filterId = SeoConstants.ATTRIBUTE_KEYWORD_FILTER_PREFIX + attributeKey
				+ SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN
				+ Base64.encodeBase64String(attributeKeyword.getBytes());
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeKeywordFilter.initialize(filterId);
		assertEquals(filterId, attributeKeywordFilter.getSeoId());

	}

	/**
	 * Test that when we set an Id on the filter it is used in the SEO Id as an alias for the value.
	 */
	@Test
	public void testGetAliasedSeoId() {
		AttributeKeywordFilter newFilter = getAttributeKeywordFilter();
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		Map<String, Object> filterProperties = new HashMap<>();
		filterProperties.put(AttributeFilter.ATTRIBUTE_PROPERTY, getAttribute("A00002"));
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, "01");
		filterProperties.put(AttributeKeywordFilter.ATTRIBUTE_KEYWORD_PROPERTY, "SD Memory Card");
		newFilter.initialize(filterProperties);
		newFilter.setDisplayName("display name");
		assertEquals("Seo ID should use the provided alias", "akA00002_MDE", newFilter.getSeoId());
	}

	private Attribute getAttribute(final String key) {
		Attribute attribute = new AttributeImpl();
		attribute.setAttributeType(AttributeType.SHORT_TEXT);
		attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		attribute.setKey(key);
		return attribute;
	}

	/**
	 * Test that when we set the attribute Id on the filter it only returns the prefix of the attribute,
	 * excluding the actual attribute value.
	 */
	@Test
	public void testGetAttributePrefixAndKey() {
		AttributeKeywordFilter newFilter = getAttributeKeywordFilter();
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		Map<String, Object> filterProperties = new HashMap<>();
		filterProperties.put(AttributeFilter.ATTRIBUTE_PROPERTY, getAttribute("A00002"));
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, "01");
		filterProperties.put(AttributeKeywordFilter.ATTRIBUTE_KEYWORD_PROPERTY, "SD Memory Card");
		newFilter.initialize(filterProperties);
		assertEquals("Attribute ID should use the Attribute prefix plus the attribute key", "akA00002", newFilter.getAttributePrefixAndKey());
	}
}
