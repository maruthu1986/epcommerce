/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.changeset.impl;

/**
 * Resolves metadata for category type objects.
 */
public class CategoryTypeMetadataResolver extends AbstractNamedQueryMetadataResolverImpl {
	
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "CategoryType".equals(objectType);
	}

	@Override
	protected String getNamedQueryForObjectName() {
		return "CATEGORY_TYPE_NAME_FIND_BY_GUID";
	}
}
