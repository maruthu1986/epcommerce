/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service;

import com.elasticpath.tags.domain.TagDefinition;

/**
 * Converts tag value string to a value object.
 */
public interface TagTypeValueConverter {

	/**
	 * Convert the given tag definition and tag value to a java object.
	 *
	 * @param tagDefinition the tag definition.
	 * @param tagValue the tag string value
	 * @return the value object
	 */
	Object convertValueTypeToTagJavaType(TagDefinition tagDefinition, String tagValue);

}
