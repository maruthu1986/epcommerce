/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.comparator;

import java.util.Comparator;

import com.elasticpath.domain.contentspace.DynamicContent;

/**
 * Comparator for {@link DynamicContent}.
 *
 */
public class DynamicContentComparator implements Comparator<DynamicContent> {

	/**
	 * Compares two {@link DynamicContent} objects.
	 * @param dynaimcContent1 - left object to compare
	 * @param dynamicContent2 - right object to compare
	 * @return int - result of compare 
	 */
	public int compare(final DynamicContent dynaimcContent1, final DynamicContent dynamicContent2) {
		return dynaimcContent1.getName().compareToIgnoreCase(dynamicContent2.getName());
	}

}
