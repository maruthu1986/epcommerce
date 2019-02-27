/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.util;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.exception.EpIntBindException;
import com.elasticpath.domain.catalog.ProductAssociationType;

/**
 * Utility class for converting an association type string to code and back.
 */
public final class AssociationTypeConverter {

	private static final String CROSS_SELL_TEXT = "Cross Sell";

	private static final String UP_SELL_TEXT = "Up Sell";

	private static final String WARRANTY_TEXT = "Warranty";

	private static final String ACCESSORY_TEXT = "Accessory";

	private static final String REPLACEMENT_TEXT = "Replacement";

	private static final String RECOMMENDATION_TEXT = "Recommendation";

	private static final Map<String, ProductAssociationType> FIT_NAME_TO_ASSOCIATION_TYPE;
	static {
		FIT_NAME_TO_ASSOCIATION_TYPE = new HashMap<>();
		FIT_NAME_TO_ASSOCIATION_TYPE.put(CROSS_SELL_TEXT, ProductAssociationType.CROSS_SELL);
		FIT_NAME_TO_ASSOCIATION_TYPE.put(UP_SELL_TEXT, ProductAssociationType.UP_SELL);
		FIT_NAME_TO_ASSOCIATION_TYPE.put(WARRANTY_TEXT, ProductAssociationType.WARRANTY);
		FIT_NAME_TO_ASSOCIATION_TYPE.put(ACCESSORY_TEXT, ProductAssociationType.ACCESSORY);
		FIT_NAME_TO_ASSOCIATION_TYPE.put(REPLACEMENT_TEXT, ProductAssociationType.REPLACEMENT);
		FIT_NAME_TO_ASSOCIATION_TYPE.put(RECOMMENDATION_TEXT, ProductAssociationType.RECOMMENDATION);
	}
	
	private static final Map<ProductAssociationType, String> ASSOCIATION_TYPE_TO_FIT_NAME;
	static {
		ASSOCIATION_TYPE_TO_FIT_NAME = new HashMap<>();
		ASSOCIATION_TYPE_TO_FIT_NAME.put(ProductAssociationType.CROSS_SELL, CROSS_SELL_TEXT);
		ASSOCIATION_TYPE_TO_FIT_NAME.put(ProductAssociationType.UP_SELL, UP_SELL_TEXT);
		ASSOCIATION_TYPE_TO_FIT_NAME.put(ProductAssociationType.WARRANTY, WARRANTY_TEXT);
		ASSOCIATION_TYPE_TO_FIT_NAME.put(ProductAssociationType.ACCESSORY, ACCESSORY_TEXT);
		ASSOCIATION_TYPE_TO_FIT_NAME.put(ProductAssociationType.REPLACEMENT, REPLACEMENT_TEXT);
		ASSOCIATION_TYPE_TO_FIT_NAME.put(ProductAssociationType.RECOMMENDATION, RECOMMENDATION_TEXT);
	}

	private AssociationTypeConverter() {

	}

	/**
	 * Converts an association type from string to product association type.
	 * 
	 * @param associationType association type as fit display name
	 * @return association type as a product association type.
	 * @throws EpIntBindException in case of any errors
	 */
	public static ProductAssociationType convert2ProductAssociationType(final String fitDisplayName) throws EpIntBindException {
		if (fitDisplayName == null || fitDisplayName.length() < 1 || !FIT_NAME_TO_ASSOCIATION_TYPE.containsKey(fitDisplayName)) {
			throw new EpIntBindException("An association type string is invalid");
		}

		return FIT_NAME_TO_ASSOCIATION_TYPE.get(fitDisplayName);
	}

	/**
	 * Convert product association type to its fit display name.
	 *
	 * @param associationType the association type.
	 * @return the product association type.
	 * @throws EpIntBindException if any errors
	 */
	public static String convert2FitDisplayText(final ProductAssociationType associationType) throws EpIntBindException {
		if (associationType == null || !ASSOCIATION_TYPE_TO_FIT_NAME.containsKey(associationType)) {
			throw new EpIntBindException("An association type string is invalid");
		}

		return ASSOCIATION_TYPE_TO_FIT_NAME.get(associationType);
	}
}
