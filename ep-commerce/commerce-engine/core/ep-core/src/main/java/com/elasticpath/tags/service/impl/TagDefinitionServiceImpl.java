/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service.impl;

import com.elasticpath.tags.dao.TagDefinitionDao;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.TagDefinitionService;

/**
 *Implementation of TagDefinitionService interface.
 */
public class TagDefinitionServiceImpl implements TagDefinitionService {

	private TagDefinitionDao tagDefinitionDao;

	@Override
	public void saveOrUpdate(final TagDefinition tagDefinition) {
		tagDefinitionDao.saveOrUpdate(tagDefinition);

	}

	@Override
	public void delete(final TagDefinition tagDefinition) {
		tagDefinitionDao.remove(tagDefinition);
	}

	/**
	 * TagDefinitionDao injection method.
	 * @param tagDefinitionDao TagDefinitionDao.
	 */
	public void setTagDefinitionDao(final TagDefinitionDao tagDefinitionDao) {
		this.tagDefinitionDao = tagDefinitionDao;
	}
}