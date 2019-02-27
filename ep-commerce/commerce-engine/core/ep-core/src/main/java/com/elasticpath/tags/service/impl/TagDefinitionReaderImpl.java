/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service.impl;

import java.util.List;

import com.elasticpath.tags.dao.TagDefinitionDao;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.TagDefinitionReader;

/**
 * The TagDefinitionReaderImpl loads persistent TagDefinitions.
 */
public class TagDefinitionReaderImpl implements TagDefinitionReader {

	private TagDefinitionDao tagDefinitionDao;

	@Override
	public List<TagDefinition> getTagDefinitions() {
		return tagDefinitionDao.getTagDefinitions();
	}

	@Override
	public TagDefinition findByGuid(final String guid) {
		return tagDefinitionDao.findByGuid(guid);
	}

	@Override
	public TagDefinition findByName(final String name) {
		return tagDefinitionDao.findByName(name);
	}
	/**
	 * TagDefinitionDao injection method.
	 * @param tagDefinitionDao TagDefinitionDao.
	 */
	public void setTagDefinitionDao(final TagDefinitionDao tagDefinitionDao) {
		this.tagDefinitionDao = tagDefinitionDao;
	}
}