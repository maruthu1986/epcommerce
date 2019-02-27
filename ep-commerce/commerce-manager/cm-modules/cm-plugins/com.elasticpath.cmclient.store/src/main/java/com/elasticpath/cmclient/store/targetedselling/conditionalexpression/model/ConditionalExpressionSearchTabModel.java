/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model;

import java.beans.PropertyChangeListener;

import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * ConditionalExpressionSearchTabModel is a model for the search tab.
 *
 */
public interface ConditionalExpressionSearchTabModel {

	/**
	 * Add property change listener.
	 * @param listener PropertyChangeListener
	 */
	void addPropertyListener(PropertyChangeListener listener);
	
	/**
	 * Remove property change listener.
	 * @param listener PropertyChangeListener
	 */
	void removePropertyListener(PropertyChangeListener listener);
	
	/**
	 * Get name.
	 * @return String name
	 */
	String getName();
	
	/**
	 * Set name.
	 * @param name String name
	 */
	void setName(String name);
	
	/**
	 * Get TagDictionary. NULL for all.
	 * @return tag dictionary
	 */
	TagDictionary getTagDictionary();
	
	/**
	 * Set TagDictionary.
	 * @param tagDictionary tag dictionary
	 */
	void setTagDictionary(TagDictionary tagDictionary);
	
	/**
	 * Get TagDictionary. NULL for all.
	 * @return TagDefinition tag definition
	 */
	TagDefinition getTagDefinition();
	
	/**
	 * Set TagDefinition.
	 * @param tagDefinition tag definition
	 */
	void setTagDefinition(TagDefinition tagDefinition);
	
	/**
	 * Get DynamicContentDelivery. NULL for all.
	 * @return DynamicContentDelivery
	 */
	DynamicContentDelivery getDynamicContentDelivery();
	
	/**
	 * Set DynamicContentDelivery.
	 * @param dynamicContentDelivery dynamic content delivery
	 */
	void setDynamicContentDelivery(DynamicContentDelivery dynamicContentDelivery);

}
