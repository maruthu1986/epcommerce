/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price.baseamounts;

import java.util.Comparator;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * 
 * Comparator for {@link BaseAmountDTO} class.
 *
 */
public class ProductEditorBaseAmountDTOComparator implements Comparator<BaseAmountDTO> {

	@Override
	public int compare(final BaseAmountDTO descriptor1, final BaseAmountDTO descriptor2) {
		int result = 0;
		if (descriptor1.getQuantity() != null) {
			result = descriptor1.getQuantity().compareTo(descriptor2.getQuantity());
		}
		if (result == 0 && descriptor1.getObjectType() != null) {
			result = descriptor1.getObjectType().compareTo(descriptor2.getObjectType());
		}		 
		if (result == 0) {
			result = compareSkuCodes(descriptor1, descriptor2);
		}
		return result;
	}

	private int compareSkuCodes(final BaseAmountDTO descriptor1, final BaseAmountDTO descriptor2) {
		if (descriptor1.getSkuCode() == null && descriptor2.getSkuCode() == null) {
			return 0;
		} else if (descriptor1.getSkuCode() == null) {
			return 1;
		} else if (descriptor2.getSkuCode() == null) {
			return -1;
		}
		return descriptor1.getSkuCode().compareToIgnoreCase(descriptor2.getSkuCode());
	}
	
}
