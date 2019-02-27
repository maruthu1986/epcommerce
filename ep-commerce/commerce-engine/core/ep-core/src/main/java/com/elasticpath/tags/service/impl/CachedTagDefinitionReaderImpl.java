/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.TagDefinitionReader;

/**
 * <p>
 * TagDefinitions are cached in an application cache to improve performance.
 * </p>
 * <p>
 * Since you can look up a TagDefinition by either name or guid we have a
 * double map structure that looks like this:
 * </p>
 * <pre>
 *            +-------------+      +-------------+
 * by name -> | TagDef.name | ---> | TagDef.guid | ------+
 *            +-------------+      +-------------+       |
 *                                                       |
 *                   +-----------------------------------+
 *                   |
 *                   V
 *            +-------------+      +--------+
 * by guid -> | TagDef.guid | ---> | TagDef |
 *            +-------------+      +--------+
 * </pre>
 */
@SuppressWarnings("PMD.ConfusingTernary")
public class CachedTagDefinitionReaderImpl implements TagDefinitionReader {

	private Cache/*<String,String>*/ tagDefinitionNameToTagDefinitionGuidCache;
	private Cache/*<String,TagDefinition>*/ tagDefinitionGuidToTagDefinitionCache;
	private TagDefinitionReader tagDefinitionReader;

	@Override
	public List<TagDefinition> getTagDefinitions() {
		List<TagDefinition> result = new ArrayList<>();
		List<?> keys = tagDefinitionGuidToTagDefinitionCache.getKeys();
		if (!keys.isEmpty()) {
			for (Object key : keys) {
				Element element = tagDefinitionGuidToTagDefinitionCache.get(key);
				if (element != null) {
					result.add((TagDefinition) element.getObjectValue());
				}
			}
		} else {
			List<TagDefinition> tagDefinitions = tagDefinitionReader.getTagDefinitions();
			for (TagDefinition tagDefinition : tagDefinitions) {
				put(tagDefinition);
			}
			result.addAll(tagDefinitions);
		}

		return result;
	}

	@Override
	public TagDefinition findByGuid(final String guid) {
		TagDefinition result;
		Element cacheEntry = tagDefinitionGuidToTagDefinitionCache.get(guid);
		if (cacheEntry != null) {
			result = (TagDefinition) cacheEntry.getObjectValue();
		} else {
			result = tagDefinitionReader.findByGuid(guid);
			if (result != null) {
				put(result);
			}
		}

		return result;
	}

	@Override
	public TagDefinition findByName(final String name) {
		TagDefinition result;
		Element cacheEntry = tagDefinitionNameToTagDefinitionGuidCache.get(name);
		if (cacheEntry != null) {
			String guid = (String) cacheEntry.getObjectValue();
			result = findByGuid(guid);
		} else {
			result = tagDefinitionReader.findByName(name);
			if (result != null) {
				put(result);
			}
		}

		return result;
	}

	private void put(final TagDefinition tagDefinition) {
		tagDefinitionGuidToTagDefinitionCache.put(new Element(tagDefinition.getGuid(), tagDefinition));
		tagDefinitionNameToTagDefinitionGuidCache.put(new Element(tagDefinition.getName(), tagDefinition.getGuid()));
	}

	/**
	 * TagDefinitionReader injection method.
	 * @param tagDefinitionReader tagDefinitionReader.
	 */
	public void setTagDefinitionReader(final TagDefinitionReader tagDefinitionReader) {
		this.tagDefinitionReader = tagDefinitionReader;
	}

	/**
	 * The name to TagDefinition cache.
	 * @param tagDefinitionNameToTagDefinitionGuidCache The cache.
	 */
	public void setTagDefinitionNameToTagDefinitionGuidCache(final Cache tagDefinitionNameToTagDefinitionGuidCache) {
		this.tagDefinitionNameToTagDefinitionGuidCache = tagDefinitionNameToTagDefinitionGuidCache;
	}

	/**
	 * The name to TagDefinition cache.
	 * @param tagDefinitionGuidToTagDefinitionCache The cache.
	 */
	public void setTagDefinitionGuidToTagDefinitionCache(final Cache tagDefinitionGuidToTagDefinitionCache) {
		this.tagDefinitionGuidToTagDefinitionCache = tagDefinitionGuidToTagDefinitionCache;
	}
}