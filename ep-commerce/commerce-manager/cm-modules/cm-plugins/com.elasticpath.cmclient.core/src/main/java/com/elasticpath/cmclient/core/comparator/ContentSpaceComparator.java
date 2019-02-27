/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.comparator;

import java.util.Comparator;

import com.elasticpath.domain.contentspace.ContentSpace;

/**
 * Comparator for {@link ContentSpace}.
 *
 */
public class ContentSpaceComparator implements Comparator<ContentSpace> {

	/**
	 * Compares two {@link ContentSpace} objects.
	 * @param contentSpace1 - left object to compare
	 * @param contentSpace2 - right object to compare
	 * @return int - result of compare 
	 */
	public int compare(final ContentSpace contentSpace1, final ContentSpace contentSpace2) {
		return contentSpace1.getTargetId().compareToIgnoreCase(contentSpace2.getTargetId());
	}

}
