/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.changesetsupport.impl;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.commons.util.CategoryGuidUtil;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.importexport.common.dto.category.LinkedCategoryDTO;
import com.elasticpath.importexport.importer.changesetsupport.ObjectDescriptorPopulator;

/**
 * An object descriptor populator for {@link LinkedCategoryDTO}s.
 */
public class LinkedCategoryObjectDescriptorPopulator implements ObjectDescriptorPopulator {

	private CategoryGuidUtil categoryGuidUtil;

	private String objectType;

	@Override
	public BusinessObjectDescriptor populate(final BusinessObjectDescriptor descriptor, final Dto dto) {
		LinkedCategoryDTO linkedCategoryDto = (LinkedCategoryDTO) dto;
		descriptor.setObjectIdentifier(getCategoryGuidUtil().get(linkedCategoryDto.getCategoryCode(), linkedCategoryDto.getVirtualCatalogCode()));
		descriptor.setObjectType(getObjectType());
		return descriptor;
	}

	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @return the categoryGuidUtil
	 */
	protected CategoryGuidUtil getCategoryGuidUtil() {
		return categoryGuidUtil;
	}

	/**
	 * @param categoryGuidUtil the categoryGuidUtil to set
	 */
	public void setCategoryGuidUtil(final CategoryGuidUtil categoryGuidUtil) {
		this.categoryGuidUtil = categoryGuidUtil;
	}

}
