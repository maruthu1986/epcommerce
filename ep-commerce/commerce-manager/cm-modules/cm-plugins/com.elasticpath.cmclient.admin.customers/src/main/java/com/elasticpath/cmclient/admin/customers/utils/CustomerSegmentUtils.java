/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.customers.utils;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.tags.service.TagConditionService;

/**
 * Utilities for working with customer segments.
 */
public final class CustomerSegmentUtils {

	private static final String CUSTOMER_SEGMENT_TAG_NAME = "CUSTOMER_SEGMENT"; //$NON-NLS-1$
	
	/**
	 * Given first string is the tag name, and second string is the parameter.
	 * Returns a regex that matches an atomic condition clause (delimited by '{' and '}')
	 * that contains the exact tag name and the exact parameter.
	 */
	private static final String TAG_REGEX = ".*\\W%s\\W[^{}]*'%s'.*"; //$NON-NLS-1$

	/**
	 * Hidden ctor.
	 */
	private CustomerSegmentUtils() {		
	}

	/**
	 * Return true is a ConditionalExpressions exists using this customer segment.
	 *  
	 * @param customerGroup the customer group to look for
	 * @return true if in use, else false
	 */
	public static boolean segmentsInUseByConditionalExpression(final CustomerGroup customerGroup) {
		final TagConditionService tagConditionService =
				ServiceLocator.getService(ContextIdNames.TAG_CONDITION_SERVICE);

		final String regex = String.format(TAG_REGEX, CUSTOMER_SEGMENT_TAG_NAME, customerGroup.getName());
		final int count = tagConditionService.countMatchingTagExpressionStrings(regex);

		return count > 0;
	}
}
