/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service;

import com.elasticpath.tags.domain.TagDefinition;

/**
 * Service interface for TagDefinition domain object.
 */
public interface TagDefinitionService {

	/**
	 * Create or update a TagDefinition.
	 * @param tagDefinition the tag definition.
	 */
	void saveOrUpdate(TagDefinition tagDefinition);

	/**
	 * Delete the tag definition.
	 * @param tagDefinition tag definition.
	 */
	void delete(TagDefinition tagDefinition);

}
