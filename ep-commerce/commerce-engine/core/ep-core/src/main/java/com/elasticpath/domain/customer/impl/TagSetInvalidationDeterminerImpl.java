/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.customer.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.domain.customer.TagSetInvalidationDeterminer;
import com.elasticpath.tags.dao.TagDictionaryDao;

/**
 * Default implementation of {@link TagSetInvalidationDeterminer}.
 */
public class TagSetInvalidationDeterminerImpl implements TagSetInvalidationDeterminer, Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20091023L;

	private final Set<String> tagGuids = new HashSet<>();

	private List<String> tagDictionaries;

	private transient TagDictionaryDao tagDictionaryDao;


	/**
	 * Set the tag dictionary DAO.
	 * @param tagDictionaryDao dao to set.
	 */
	public void setTagDictionaryDao(final TagDictionaryDao tagDictionaryDao) {
		this.tagDictionaryDao = tagDictionaryDao;
	}

	/**
	 * Set the tag dictionaries, that can invalidate price list stack.
	 * @param tagDictionaries list of dictionaries to set.
	 */
	public void setTagDictionaries(final List<String> tagDictionaries) {
		this.tagDictionaries = tagDictionaries;
	}

	/**
	 * Initialize the set of tag GUIDs for given list of tag dictionaries. Called from Spring, during application startup
	 */
	public void initTagGuids() {

		if (tagDictionaries != null) {
			Collection<String> dbTagGuids = tagDictionaryDao.getUniqueTagDefinitionGuidsByTagDictionaryGuids(tagDictionaries);
			tagGuids.addAll(dbTagGuids);
		}
	}

	@Override
	public boolean needInvalidate(final String key) {
		return tagGuids.contains(key);
	}

}
