/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Resolves metadata for Category objects.
 */
public class CategoryMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {
	
	@Override
	protected String getNamedQueryForObjectName() {
		return "CATEGORY_NAME_IN_DEFAULT_LOCALE_BY_GUID";
	}

	/**
	 * This resolver is only valid for "Category" objects.
	 * 
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Category".equals(objectType);
	}

	@Override
	public Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		Map<String, String> metadata = super.resolveMetaDataInternal(objectDescriptor);

		if (metadata.isEmpty()) {
			String linkedCategoryNamedQuery = "LINKED_CATEGORY_NAME_IN_DEFAULT_LOCALE_BY_MASTER_CATEGORY_GUID";
			String objectName = retrieveName(linkedCategoryNamedQuery, objectDescriptor.getObjectIdentifier());

			if (StringUtils.isNotBlank(objectName)) {
				metadata.put("objectName", objectName);
			}
		}

		return metadata;
	}
}
