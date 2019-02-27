/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.adapters.catalogs;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.adapters.catalogs.helper.AttributeGroupHelper;
import com.elasticpath.importexport.common.dto.catalogs.CategoryTypeDTO;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>CategoryType</code> and
 * <code>CategoryTypeDTO</code> objects.
 */
public class CategoryTypeAdapter extends AbstractDomainAdapterImpl<CategoryType, CategoryTypeDTO> {
	
	private AttributeGroupHelper attributeGroupHelper;

	@Override
	public void populateDTO(final CategoryType categoryType, final CategoryTypeDTO categoryTypeDTO) {
		categoryTypeDTO.setGuid(categoryType.getGuid());
		categoryTypeDTO.setName(categoryType.getName());
		categoryTypeDTO.setAssignedAttributes(
				attributeGroupHelper.createAssignedAttributes(
						categoryType.getAttributeGroup().getAttributeGroupAttributes()));			
	}

	@Override
	public void populateDomain(final CategoryTypeDTO categoryTypeDTO, final CategoryType categoryType) {
		if (StringUtils.isNotBlank(categoryTypeDTO.getGuid())) {
			categoryType.setGuid(categoryTypeDTO.getGuid());
		}
		categoryType.setName(categoryTypeDTO.getName());

		attributeGroupHelper.populateAttributeGroupAttributes(createAttributeGroupAttributes(categoryType), 
										 					  categoryTypeDTO.getAssignedAttributes(), 
										 					  ContextIdNames.CATEGORY_TYPE_ATTRIBUTE);
	}

	/**
	 * Creates set of AttributeGroupAttributes.
	 * @param categoryType the category type 
	 * 
	 * @return a Set of AttributeGroupAttributes
	 */
	Set<AttributeGroupAttribute> createAttributeGroupAttributes(final CategoryType categoryType) {
		final AttributeGroup attributeGroup = categoryType.getAttributeGroup();
		Set<AttributeGroupAttribute> categoryAttributeGroupAttributes = attributeGroup.getAttributeGroupAttributes();
		if (categoryAttributeGroupAttributes == null) {
			categoryAttributeGroupAttributes = new HashSet<>();
			attributeGroup.setAttributeGroupAttributes(categoryAttributeGroupAttributes);
			categoryType.setAttributeGroup(attributeGroup);
		}
		return categoryAttributeGroupAttributes;
	}

	@Override
	public CategoryType createDomainObject() {
		return getBeanFactory().getBean(ContextIdNames.CATEGORY_TYPE);
	}

	@Override
	public CategoryTypeDTO createDtoObject() {
		return new CategoryTypeDTO();
	}
	
	/**
	 * Gets AttributeGroupHelper.
	 * 
	 * @return AttributeGroupHelper
	 */
	public AttributeGroupHelper getAttributeGroupHelper() {
		return attributeGroupHelper;
	}

	/**
	 * Sets AttributeGroupHelper.
	 * 
	 * @param attributeGroupHelper the AttributeGroupHelper instance
	 */
	public void setAttributeGroupHelper(final AttributeGroupHelper attributeGroupHelper) {
		this.attributeGroupHelper = attributeGroupHelper;
	}
}
