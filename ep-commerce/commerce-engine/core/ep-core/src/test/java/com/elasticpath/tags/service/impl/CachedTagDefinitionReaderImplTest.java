/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.TagDefinitionReader;

/**
 * Unit test.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CachedTagDefinitionReaderImplTest {

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private Cache/*<String,String>*/ tagDefinitionNameToTagDefinitionGuidCache;
	private Cache/*<String,TagDefinition>*/ tagDefinitionGuidToTagDefinitionCache;
	@Mock
	private TagDefinitionReader tagDefinitionReader;
	private CachedTagDefinitionReaderImpl cachedTagDefinitionReader;

	@Before
	public void setUp() throws Exception {
		CacheManager singletonManager = CacheManager.create();
		singletonManager.removalAll();
		singletonManager.addCache("tagDefinitionGuidToTagDefinitionCache");
		singletonManager.addCache("tagDefinitionNameToTagDefinitionGuidCache");
		tagDefinitionGuidToTagDefinitionCache = singletonManager.getCache("tagDefinitionGuidToTagDefinitionCache");
		tagDefinitionNameToTagDefinitionGuidCache = singletonManager.getCache("tagDefinitionNameToTagDefinitionGuidCache");

		cachedTagDefinitionReader = new CachedTagDefinitionReaderImpl();
		cachedTagDefinitionReader.setTagDefinitionGuidToTagDefinitionCache(tagDefinitionGuidToTagDefinitionCache);
		cachedTagDefinitionReader.setTagDefinitionNameToTagDefinitionGuidCache(tagDefinitionNameToTagDefinitionGuidCache);
		cachedTagDefinitionReader.setTagDefinitionReader(tagDefinitionReader);
	}

	@Test
	public void testGetCachedTagDefinitions() throws Exception {
		final String key = "fred";
		final TagDefinition expectedTagDefinition = context.mock(TagDefinition.class);
		tagDefinitionGuidToTagDefinitionCache.put(new Element(key, expectedTagDefinition));

		List<TagDefinition> tagDefinitions = cachedTagDefinitionReader.getTagDefinitions();
		assertThat("Expected cached tag definitions", tagDefinitions, is(not(empty())));
		assertEquals("Unexpected cached tag definition", expectedTagDefinition, tagDefinitions.get(0));
	}

	@Test
	public void testGetUncachedTagDefinitions() throws Exception {
		final String key = "testKey";
		final String name = "testName";
		final TagDefinition expectedTagDefinition = context.mock(TagDefinition.class);
		final List<TagDefinition> expectedTagDefinitions = Arrays.asList(expectedTagDefinition);
		context.checking(new Expectations() { {
			oneOf(tagDefinitionReader).getTagDefinitions();
			will(returnValue(expectedTagDefinitions));

			oneOf(expectedTagDefinition).getName();
			will(returnValue(name));

			exactly(2).of(expectedTagDefinition).getGuid();
			will(returnValue(key));
		} });

		List<TagDefinition> tagDefinitions = cachedTagDefinitionReader.getTagDefinitions();
		assertEquals("Unexpected number of tag definitions", 1, tagDefinitions.size());
		assertEquals("Unexpected tag definition", expectedTagDefinition, tagDefinitions.get(0));

		assertEquals("Expected cached tag def", expectedTagDefinition, tagDefinitionGuidToTagDefinitionCache.get(key).getObjectValue());
		assertEquals("Expected cached tag def", key, tagDefinitionNameToTagDefinitionGuidCache.get(name).getObjectValue());
	}

	@Test
	public void testGetNoTagDefinitionsFound() throws Exception {
		final List<TagDefinition> expectedTagDefinitions = new ArrayList<>();
		context.checking(new Expectations() { {
			oneOf(tagDefinitionReader).getTagDefinitions();
			will(returnValue(expectedTagDefinitions));
		} });

		List<TagDefinition> tagDefinitions = cachedTagDefinitionReader.getTagDefinitions();
		assertTrue("Expected no tag definitions", tagDefinitions.isEmpty());
	}


	@Test
	public void testFindByGuidFromCache() throws Exception {
		final String key = "fred";
		final TagDefinition expectedTagDefinition = context.mock(TagDefinition.class);
		tagDefinitionGuidToTagDefinitionCache.put(new Element(key, expectedTagDefinition));

		TagDefinition tagDefinition = cachedTagDefinitionReader.findByGuid(key);
		assertEquals("Unexpected cached tag definition", expectedTagDefinition, tagDefinition);
	}

	@Test
	public void testFindByGuidFromDatabase() throws Exception {
		final String key = "testKey";
		final String name = "testName";
		final TagDefinition expectedTagDefinition = context.mock(TagDefinition.class);
		context.checking(new Expectations() { {
			oneOf(tagDefinitionReader).findByGuid(key);
			will(returnValue(expectedTagDefinition));

			oneOf(expectedTagDefinition).getName();
			will(returnValue(name));

			exactly(2).of(expectedTagDefinition).getGuid();
			will(returnValue(key));
		} });

		TagDefinition tagDefinition = cachedTagDefinitionReader.findByGuid(key);
		assertEquals("Unexpected tag definition", expectedTagDefinition, tagDefinition);

		assertEquals("Expected cached tag def", expectedTagDefinition, tagDefinitionGuidToTagDefinitionCache.get(key).getObjectValue());
		assertEquals("Expected cached tag def", key, tagDefinitionNameToTagDefinitionGuidCache.get(name).getObjectValue());
	}

	@Test
	public void testFindByGuidNotFound() throws Exception {
		final String key = "testKey";
		context.checking(new Expectations() { {
			oneOf(tagDefinitionReader).findByGuid(key);
			will(returnValue(null));
		} });

		TagDefinition tagDefinition = cachedTagDefinitionReader.findByGuid(key);
		assertNull("Unexpected tag definition", tagDefinition);

		assertNull("Expected null tag definition to not be cached", tagDefinitionGuidToTagDefinitionCache.get(key));
	}

	@Test
	public void testFindByNameFromCache() throws Exception {
		final String key = "testKey";
		final String name = "testName";
		final TagDefinition expectedTagDefinition = context.mock(TagDefinition.class);
		tagDefinitionNameToTagDefinitionGuidCache.put(new Element(name, key));
		tagDefinitionGuidToTagDefinitionCache.put(new Element(key, expectedTagDefinition));

		TagDefinition tagDefinition = cachedTagDefinitionReader.findByName(name);
		assertEquals("Unexpected cached tag definition", expectedTagDefinition, tagDefinition);
	}

	@Test
	public void testFindByNameNotCached() throws Exception {
		final String key = "testKey";
		final String name = "testName";
		final TagDefinition expectedTagDefinition = context.mock(TagDefinition.class);
		context.checking(new Expectations() { {
			oneOf(tagDefinitionReader).findByName(name);
			will(returnValue(expectedTagDefinition));

			oneOf(expectedTagDefinition).getName();
			will(returnValue(name));

			exactly(2).of(expectedTagDefinition).getGuid();
			will(returnValue(key));
		} });

		TagDefinition tagDefinition = cachedTagDefinitionReader.findByName(name);
		assertEquals("Unexpected tag definition", expectedTagDefinition, tagDefinition);

		assertEquals("Expected cached tag def", key, tagDefinitionNameToTagDefinitionGuidCache.get(name).getObjectValue());
		assertEquals("Expected cached tag def", expectedTagDefinition, tagDefinitionGuidToTagDefinitionCache.get(key).getObjectValue());
	}

	@Test
	public void testFindByNameNotFound() throws Exception {
		final String name = "testName";
		context.checking(new Expectations() { {
			oneOf(tagDefinitionReader).findByName(name);
			will(returnValue(null));
		} });

		TagDefinition tagDefinition = cachedTagDefinitionReader.findByName(name);
		assertNull("Unexpected tag definition", tagDefinition);

		assertNull("Expected null tag definition to not be cached", tagDefinitionNameToTagDefinitionGuidCache.get(name));
	}
}
