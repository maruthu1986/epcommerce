/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service;

import java.util.List;

import com.elasticpath.tags.domain.TagDefinition;

/**
 * <p>
 * The TagDefinitionReader is responsible for retrieving TagDefinitions from storage.
 * </p>
 * <p>
 * The design philosophy behind splitting the read and write operations onto
 * separate interfaces is intended to allow separation of concerns.
 * For example, if we want to introduce caching on the read operations, we don't
 * want to worry about the write operations at the same time.
 * </p>
 */
public interface TagDefinitionReader {

	/**
	 * Get all tag definitions.
	 * @return a list of tag definitions.
	 */
	List<TagDefinition> getTagDefinitions();

	/**
	 * Find a tag definition by its guid.
	 * @param guid the guid of the tag definition.
	 * @return the tag definition.
	 */
	TagDefinition findByGuid(String guid);

	/**
	 * Find a tag definition by its name.
	 * @param name the name of the tag definition.
	 * @return the tag definition.
	 */
	TagDefinition findByName(String name);
}
