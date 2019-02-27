/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.service.changeset.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Resolves metadata for cart item modifier group objects.
 */
public class CartItemModifierGroupMetadataResolverImpl extends AbstractMetadataResolverImpl {
	private static final String NAME_KEY = "objectName";

	@Override
	public Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		String name = objectDescriptor.getObjectIdentifier();
		Map<String, String> metaData = new HashMap<>();
		metaData.put(NAME_KEY, name);
		return metaData;
	}

	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "CartItemModifierGroup".equals(objectType);
	}
}
