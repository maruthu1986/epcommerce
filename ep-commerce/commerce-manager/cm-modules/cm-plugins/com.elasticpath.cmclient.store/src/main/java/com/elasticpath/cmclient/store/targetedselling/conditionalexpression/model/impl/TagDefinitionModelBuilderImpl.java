/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ComboViewerModelBuilder;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.TagDefinitionReader;

/**
 * TagDefinitionModelBuilderImpl.
 *
 */
public class TagDefinitionModelBuilderImpl implements ComboViewerModelBuilder<TagDefinition> {

	private final List<TagDefinition> tagDefinitionList;
	
	/**
	 * Constructor with parameter.
	 * @param tagDefinitionCollection the collection of TagDefinitions (sorting will be applied to it)
	 */
	public TagDefinitionModelBuilderImpl(final Collection<TagDefinition> tagDefinitionCollection) {
		this.tagDefinitionList = getSortedSetByLocalizedName(tagDefinitionCollection);
		addAllMarkerAsFirstElement(this.tagDefinitionList);
	}

	/**
	 * Default Constructor.
	 */
	public TagDefinitionModelBuilderImpl() {
		final TagDefinitionReader tagDefinitionReader = this.getTagDefinitionReader();
		this.tagDefinitionList = getSortedSetByLocalizedName(tagDefinitionReader.getTagDefinitions());
		addAllMarkerAsFirstElement(this.tagDefinitionList);
	}
	
	private List<TagDefinition> getSortedSetByLocalizedName(final Collection<TagDefinition> unsortedCollection) {

		final Set<TagDefinition> tagDefinitionSortedSet = new TreeSet<>(new TagDefinitionLocalizedNameComparator());
		tagDefinitionSortedSet.addAll(unsortedCollection);
		return new ArrayList<>(tagDefinitionSortedSet);
	}
	
	/**
	 * Comparator to allow sorting of tag definitions by localized tag name.
	 */
	private class TagDefinitionLocalizedNameComparator implements Comparator<TagDefinition> {

		@Override
		public int compare(final TagDefinition tagDefinition1, final TagDefinition tagDefinition2) {

			final Locale defaultLocale = CorePlugin.getDefault().getDefaultLocale();
			if (tagDefinition1 != null && tagDefinition2 != null) {
				final String localName1 = tagDefinition1.getLocalizedName(defaultLocale);
				final String localName2 = tagDefinition2.getLocalizedName(defaultLocale);
				if (localName1 != null && localName2 != null) {
					return localName1.compareToIgnoreCase(localName2);
				}
			}
			return 1;
		}
		
	}
	
	private void addAllMarkerAsFirstElement(final List<TagDefinition> tagDefinitionList) {
		
		TagDefinition tagDefinitionAll = ServiceLocator.getService(ContextIdNames.TAG_DEFINITION);
		tagDefinitionAll.setName(TargetedSellingMessages.get().ConditionalExpressionAll);
		
		tagDefinitionList.add(0, tagDefinitionAll);
	}

	@Override
	public TagDefinition[] getModel() {

		TagDefinition[] result = new TagDefinition[tagDefinitionList.size()];
		return tagDefinitionList.toArray(result);
	} 

	private TagDefinitionReader getTagDefinitionReader() {
		return ServiceLocator.getService(ContextIdNames.TAG_DEFINITION_READER);
	}

}
