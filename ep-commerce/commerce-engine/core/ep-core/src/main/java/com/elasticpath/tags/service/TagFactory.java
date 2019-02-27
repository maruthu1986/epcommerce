/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service;

import com.elasticpath.tags.Tag;
import com.elasticpath.tags.domain.TagDefinition;

/**
 * Factory for creating tags.
 */
public interface TagFactory {

	/**
	 * Create a tag from the given tag definition and value.
	 * @param tagDefinition the tag definition.
	 * @param tagValue the tag string value.
	 * @return the tag object.
	 */
	Tag createTagFromTagDefinition(TagDefinition tagDefinition, String tagValue);

	/**
	 * Create a tag form the given tag name and value.
	 *
	 * @param tagName the tag name.
	 * @param tagValue the tag value.
	 * @return the tag object.
	 */
	Tag createTagFromTagName(String tagName, String tagValue);
}
