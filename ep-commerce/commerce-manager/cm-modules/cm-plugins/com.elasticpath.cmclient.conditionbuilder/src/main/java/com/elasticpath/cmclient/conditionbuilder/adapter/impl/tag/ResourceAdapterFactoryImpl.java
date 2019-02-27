/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag;

import java.util.Locale;

import com.elasticpath.cmclient.conditionbuilder.adapter.ResourceAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.ResourceAdapterFactory;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagGroup;
import com.elasticpath.tags.domain.TagOperator;

/**
 * ResourceAdapterFactoryImpl implementation of {@link ResourceAdapterFactory}.
 *
 */
public class ResourceAdapterFactoryImpl implements ResourceAdapterFactory {

	private final Locale locale;

	/**
	 * Default constructor.
	 * @param locale Locale
	 */
	public ResourceAdapterFactoryImpl(final Locale locale) {
		super();
		this.locale = locale;
	}

	private final ResourceAdapter<TagDefinition> resourceAdapterForTagDefinition = 
		new ResourceAdapter<TagDefinition>() {
			public String getLocalizedResource(final TagDefinition object) {
				return object.getLocalizedName(locale);
			} 
		};
	
	private final ResourceAdapter<TagOperator> resourceAdapterForTagOperator = 
		new ResourceAdapter<TagOperator>() {
			public String getLocalizedResource(final TagOperator object) {
				return object.getName(locale);
			}
		};
		
	private ResourceAdapter<LogicalOperatorType> resourceAdapterForLogicalOperator =
        object -> {
			return "!" + object.getMessageKey() + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		};
	
	private ResourceAdapter<String> resourceAdapterForUiElements =
        object -> ConditionBuilderMessages.get().getMessage(object);
		
	private final ResourceAdapter<TagGroup> resourceAdapterForTagGroup =
		new ResourceAdapter<TagGroup>() {
			public String getLocalizedResource(final TagGroup object) {
				return object.getLocalizedGroupName(locale);
			} };

	@Override
	public <T> ResourceAdapter<T> getResourceAdapter(final Class<T> type) {
		ResourceAdapter resourceAdapter = null;
		if (TagDefinition.class.equals(type)) {
			resourceAdapter = resourceAdapterForTagDefinition;
		} else if (TagOperator.class.equals(type)) {
			resourceAdapter = resourceAdapterForTagOperator;
		} else if (LogicalOperatorType.class.equals(type)) {
			resourceAdapter = resourceAdapterForLogicalOperator;
		} else if (TagGroup.class.equals(type)) {
			resourceAdapter = resourceAdapterForTagGroup;
		} else if (String.class.equals(type)) {
			resourceAdapter = resourceAdapterForUiElements;
		}
		return resourceAdapter;
	}

	/**
	 * Set ResourceAdapter for LogicalOperatorType.
	 * @param resourceAdapterForLogicalOperator the resourceAdapterForLogicalOperator to set
	 */
	public void setResourceAdapterForLogicalOperator(final ResourceAdapter<LogicalOperatorType> resourceAdapterForLogicalOperator) {
		this.resourceAdapterForLogicalOperator = resourceAdapterForLogicalOperator;
	}

	/**
	 * Set ResourceAdapter for String.
	 * @param resourceAdapterForUiElements the resourceAdapterForUiElements to set
	 */
	public void setResourceAdapterForUiElements(final ResourceAdapter<String> resourceAdapterForUiElements) {
		this.resourceAdapterForUiElements = resourceAdapterForUiElements;
	}

}
