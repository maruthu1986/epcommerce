/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service.impl;

import java.util.List;

import com.elasticpath.tags.dao.TagOperatorDao;
import com.elasticpath.tags.domain.TagOperator;
import com.elasticpath.tags.service.TagOperatorService;

/**
 * The default TagOperatorService implementation retrieves persisted TagOperators.
 */
public class TagOperatorServiceImpl implements TagOperatorService {

	private TagOperatorDao tagOperatorDao;

	@Override
	public List<TagOperator> getTagOperators() {
		return tagOperatorDao.getTagOperators();
	}

	@Override
	public TagOperator findByGuid(final String guid) {
		return tagOperatorDao.findByGuid(guid);
	}

	/**
	 * TagOperatorDao injection method.
	 * @param tagOperatorDao TagOperatorDao.
	 */
	public void setTagOperatorDao(final TagOperatorDao tagOperatorDao) {
		this.tagOperatorDao = tagOperatorDao;
	}
}