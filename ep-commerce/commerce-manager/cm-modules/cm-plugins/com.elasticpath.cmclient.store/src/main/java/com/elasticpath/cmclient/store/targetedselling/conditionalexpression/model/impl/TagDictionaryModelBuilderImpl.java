/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ComboViewerModelBuilder;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.TagDictionaryService;

/**
 * TagDictionaryModelBuilderImpl.
 *
 */
public class TagDictionaryModelBuilderImpl implements ComboViewerModelBuilder<TagDictionary> {
	
	private static final String[] DICTIONARIES_WHITE_LIST = { 
		"SHOPPER", //$NON-NLS-1$
		"TIME", //$NON-NLS-1$
		"STORES" //$NON-NLS-1$
	};

	@Override
	public TagDictionary[] getModel() {

		TagDictionary tagDictionaryAll = ServiceLocator.getService(ContextIdNames.TAG_DICTIONARY);
		tagDictionaryAll.setName(TargetedSellingMessages.get().ConditionalExpressionAll);

		List<TagDictionary> list = new ArrayList<>(getDictionariesForSavedConditions());
		list.add(0, tagDictionaryAll);
		TagDictionary[] result = new TagDictionary[list.size()];
		result = list.toArray(result);
		return result;
	}

	/**
	 * Get dictionaries for saved conditions.
	 *
	 * @return a tag dictionary list.
	 */
	public List<TagDictionary> getDictionariesForSavedConditions() {
		List<TagDictionary> databaseList = getTagDictionaryService().getTagDictionaries();
		List<TagDictionary> returnList = new ArrayList<>();
		for (TagDictionary tagDictionary : databaseList) {
			if (isWhitelistedDictionary(tagDictionary, DICTIONARIES_WHITE_LIST)) {
				returnList.add(tagDictionary);
			}
		}
		
		return returnList;
	}

	private TagDictionaryService getTagDictionaryService() {
		return ServiceLocator.getService(ContextIdNames.TAG_DICTIONARY_SERVICE);
	}


	private boolean isWhitelistedDictionary(final TagDictionary dictionary, final String[] whitelist) {
		if (dictionary == null || dictionary.getGuid() == null) {
			return false;
		}
		for (String item : whitelist) {
			if (item.equals(dictionary.getGuid())) {
				return true;
			}
		}
		return false;
	}
}
