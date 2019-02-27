/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.adapters.catalogs.helper.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.importexport.common.adapters.catalogs.helper.AttributeGroupHelper;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * The Implementation of AttributeGroupHelper.
 */
public class AttributeGroupHelperImpl implements AttributeGroupHelper {

	private CachingService cachingService;

	private BeanFactory beanFactory;

	/**
	 * Checks if attribute group exists.
	 * 
	 * @param attributeGroupAttributes a set of AttributeGroupAttributes
	 * @param groupToFind the AttributeGroupAttribute which we want to find
	 * @return true if attributeGroupAttributes contains groupToFind, method compares by attribute only.
	 */
	@Override
	public boolean isAttributeGroupExist(final Set<AttributeGroupAttribute> attributeGroupAttributes, final AttributeGroupAttribute groupToFind) {
		for (AttributeGroupAttribute groupAttribute : attributeGroupAttributes) {
			if (groupAttribute.getAttribute().equals(groupToFind.getAttribute())) {
				return true;
			}
		}
	
		return false;
	}

	/**
	 * Creates AssignedAttributes List.
	 * 
	 * @param attributeGroupAttributes the set of AttributeGroupAttribute
	 * @return List of assigned attributes names
	 */
	@Override
	public List<String> createAssignedAttributes(final Set<AttributeGroupAttribute> attributeGroupAttributes) {
		final List<String> assignedAttributes = new ArrayList<>();
		for (AttributeGroupAttribute attribute : attributeGroupAttributes) {
			assignedAttributes.add(attribute.getAttribute().getKey());
		}
		Collections.sort(assignedAttributes);
		return assignedAttributes;
	}

	/**
	 * Tries to find attribute with key.
	 * 
	 * @param attributeKey the key of the attribute
	 * @throws PopulationRollbackException if attribute does not exist
	 * @return Attribute instance if attribute was found
	 */
	@Override
	public Attribute findAttribute(final String attributeKey) {
		Attribute attribute = cachingService.findAttribiteByKey(attributeKey);
		if (attribute == null) {
			throw new PopulationRollbackException("IE-10007", attributeKey);
		}
		return attribute;
	}

	/**
	 * Populate AttributeGroupAttributes with list of assigned attributes.
	 * 
	 * @param attributeGroupAttributes the set to populate
	 * @param assignedAttributes assigned attributes which is used to populate
	 * @param attributeType the bean type for creating attributes.
	 */
	@Override
	public void populateAttributeGroupAttributes(final Set<AttributeGroupAttribute> attributeGroupAttributes,
												 final List<String> assignedAttributes,
												 final String attributeType) {
		for (String attributeKey : assignedAttributes) {
			Attribute attribute = findAttribute(attributeKey);
	
			AttributeGroupAttribute attributeGroupAttribute = beanFactory.getBean(attributeType);
			attributeGroupAttribute.setAttribute(attribute);
			if (!isAttributeGroupExist(attributeGroupAttributes, attributeGroupAttribute)) {
				attributeGroupAttributes.add(attributeGroupAttribute);
			}
		}
	}

	/**
	 * Gets the cachingService.
	 * 
	 * @return the cachingService
	 * @see CachingService
	 */
	public CachingService getCachingService() {
		return cachingService;
	}

	/**
	 * Sets the cachingService.
	 * 
	 * @param cachingService the cachingService to set
	 * @see CachingService
	 */
	public void setCachingService(final CachingService cachingService) {
		this.cachingService = cachingService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
