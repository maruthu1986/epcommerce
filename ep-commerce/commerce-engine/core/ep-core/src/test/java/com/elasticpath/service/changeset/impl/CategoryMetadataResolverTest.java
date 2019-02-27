/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Test that categoryMetadataResolver resolves metadata as expected.
 */
public class CategoryMetadataResolverTest {

	private CategoryMetadataResolver resolver;
	
	private PersistenceEngine persistenceEngine;
	private static final String OBJECT_TYPE = "Category";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Set up required for each test.
	 */
	@Before
	public void setUp()  {
		resolver = new CategoryMetadataResolver();
		persistenceEngine = context.mock(PersistenceEngine.class);
		resolver.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * Test validation behaves as required.
	 */
	@Test
	public void testIsValidResolverForObjectType() {
		assertTrue("Category should be valid", resolver.isValidResolverForObjectType(OBJECT_TYPE));
		assertFalse("Null should not be valid", resolver.isValidResolverForObjectType(null));
		assertFalse("Empty string should not be valid", resolver.isValidResolverForObjectType(StringUtils.EMPTY));
		assertFalse("A different object string should not be valid", resolver.isValidResolverForObjectType("Product"));
		assertFalse("Abritrary string should not be valid", resolver.isValidResolverForObjectType("anything"));
	}

	/**
	 * Test name metadata for a category is retrieved by a named query.
	 */
	@Test
	public void testResolveMetaDataCategory() {
		final String categoryGuid = "CATEGORYGUID";
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier(categoryGuid);
		objectDescriptor.setObjectType(OBJECT_TYPE);
		
		final String name = "Specials";
		final List<String> nameList = new ArrayList<>();
		nameList.add(name);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_NAME_IN_DEFAULT_LOCALE_BY_GUID", categoryGuid);
				will(returnValue(nameList));
			}
		});
		
		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be metadata returned", 1, metadata.size());
		assertEquals("The category name should be as expected", name, metadata.get("objectName"));
	}

	/**
	 * Test no metadata returned if the query returns no results (e.g. category does not exist). 
	 */
	@Test
	public void testResolveMetaDataNonExistentCategory() {
		final String categoryGuid = "NOSUCHGUID";
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier(categoryGuid);
		objectDescriptor.setObjectType(OBJECT_TYPE);
		
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_NAME_IN_DEFAULT_LOCALE_BY_GUID", categoryGuid);
				will(returnValue(Collections.emptyList()));

				oneOf(persistenceEngine).retrieveByNamedQuery("LINKED_CATEGORY_NAME_IN_DEFAULT_LOCALE_BY_MASTER_CATEGORY_GUID", categoryGuid);
				will(returnValue(Collections.emptyList()));
			}
		});
		
		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be no metadata returned", 0, metadata.size());
	}

	/**
	 * Test metadata with object name is returned for linked category.
	 */
	@Test
	public void testResolveMetaDataForLinkedCategory() {
		final String categoryGuid = "LINKED_CATEGORY_GUID";
		final String categoryName = "test linked category";

		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier(categoryGuid);
		objectDescriptor.setObjectType(OBJECT_TYPE);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("CATEGORY_NAME_IN_DEFAULT_LOCALE_BY_GUID", categoryGuid);
				will(returnValue(Collections.emptyList()));

				oneOf(persistenceEngine).retrieveByNamedQuery("LINKED_CATEGORY_NAME_IN_DEFAULT_LOCALE_BY_MASTER_CATEGORY_GUID", categoryGuid);
				will(returnValue(Arrays.asList(categoryName)));
			}
		});

		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be metadata returned", 1, metadata.size());
		assertEquals("The objectName entry must exist in metadata map", categoryName, metadata.get("objectName"));
	}
	
	/**
	 * Test that no metadata is returned when the object is not a valid type.
	 */
	@Test
	public void testResolveMetaDataForNonCategoryObject() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier("WHATEVER");
		objectDescriptor.setObjectType("Product");
		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be no metadata returned", 0, metadata.size());
	}

}
