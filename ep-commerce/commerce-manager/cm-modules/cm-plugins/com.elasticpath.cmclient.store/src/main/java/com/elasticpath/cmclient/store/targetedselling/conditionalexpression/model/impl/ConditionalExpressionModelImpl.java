/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 *
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ConditionalExpressionSearchTabModel;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * ConditionalExpressionModelImpl is implementation for ConditionalExpressionSearchTabModel.
 */
public class ConditionalExpressionModelImpl implements ConditionalExpressionSearchTabModel {

	private String name;
	private TagDictionary tagDictionary;
	private TagDefinition tagDefinition;
	private DynamicContentDelivery dynamicContentDelivery;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		String oldValue = this.name;
		this.name = name;
		this.propertyChangeSupport.firePropertyChange("name", oldValue, this.name); //$NON-NLS-1$
	}

	@Override
	public TagDictionary getTagDictionary() {
		return tagDictionary;
	}

	@Override
	public void setTagDictionary(final TagDictionary tagDictionary) {
		TagDictionary oldValue = this.tagDictionary;
		this.tagDictionary = tagDictionary;
		this.propertyChangeSupport.firePropertyChange("tagDictionary", oldValue, this.tagDictionary); //$NON-NLS-1$
	}

	@Override
	public TagDefinition getTagDefinition() {
		return tagDefinition;
	}

	@Override
	public void setTagDefinition(final TagDefinition tagDefinition) {
		TagDefinition oldValue = this.tagDefinition;
		this.tagDefinition = tagDefinition;
		this.propertyChangeSupport.firePropertyChange("tagDefinition", oldValue, this.tagDefinition); //$NON-NLS-1$
	}

	@Override
	public DynamicContentDelivery getDynamicContentDelivery() {
		return dynamicContentDelivery;
	}

	@Override
	public void setDynamicContentDelivery(final DynamicContentDelivery dynamicContentDelivery) {
		DynamicContentDelivery oldValue = this.dynamicContentDelivery;
		this.dynamicContentDelivery = dynamicContentDelivery;
		this.propertyChangeSupport.firePropertyChange("dynamicContentDelivery", oldValue, this.dynamicContentDelivery); //$NON-NLS-1$
	}

	@Override
	public void addPropertyListener(final PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyListener(final PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
