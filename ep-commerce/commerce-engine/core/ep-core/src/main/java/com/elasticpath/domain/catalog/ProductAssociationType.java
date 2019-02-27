/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.catalog;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Represents a product association type.
 */
public class ProductAssociationType extends AbstractExtensibleEnum<ProductAssociationType> {
	private static final long serialVersionUID = -7167307554278632691L;

	private static final int CROSS_SELL_ORDINAL = 1;

	private static final String CROSS_SELL_NAME = "crosssell";

	private static final int UP_SELL_ORDINAL = 2;

	private static final String UP_SELL_NAME = "upsell";

	private static final int WARRANTY_ORDINAL = 3;

	private static final String WARRANTY_NAME = "warranty";

	private static final int ACCESSORY_ORDINAL = 4;

	private static final String ACCESSORY_NAME = "accessory";

	private static final int REPLACEMENT_ORDINAL = 5;

	private static final String REPLACEMENT_NAME = "replacement";

	private static final int RECOMMENDATION_ORDINAL = 6;

	private static final String RECOMMENDATION_NAME = "recommendation";

	/**
	 * Cross Sell Product Association Type.
	 */
	public static final ProductAssociationType CROSS_SELL = new ProductAssociationType(CROSS_SELL_ORDINAL, CROSS_SELL_NAME);

	/**
	 * Up Sell Product Association Type.
	 */
	public static final ProductAssociationType UP_SELL = new ProductAssociationType(UP_SELL_ORDINAL, UP_SELL_NAME);

	/**
	 * Warranty Product Association Type.
	 */
	public static final ProductAssociationType WARRANTY = new ProductAssociationType(WARRANTY_ORDINAL, WARRANTY_NAME);

	/**
	 * Accessory Product Association Type.
	 */
	public static final ProductAssociationType ACCESSORY = new ProductAssociationType(ACCESSORY_ORDINAL, ACCESSORY_NAME);

	/**
	 * Replacement Product Association Type.
	 */
	public static final ProductAssociationType REPLACEMENT = new ProductAssociationType(REPLACEMENT_ORDINAL, REPLACEMENT_NAME);

	/**
	 * Recommendation Product Association Type.
	 */
	public static final ProductAssociationType RECOMMENDATION = new ProductAssociationType(RECOMMENDATION_ORDINAL, RECOMMENDATION_NAME);

	/**
	 * Constructor.
	 *
	 * @param ordinal the ordinal.
	 * @param name the name.
	 */
	protected ProductAssociationType(final int ordinal, final String name) {
		super(ordinal, name, ProductAssociationType.class);
	}
	
	@Override
	protected Class<ProductAssociationType> getEnumType() {
		return ProductAssociationType.class;
	}
	
	/**
	 * Find the enum value with the specified ordinal.
	 * @param ordinal the ordinal
	 * @return the enum value
	 */
	public static ProductAssociationType fromOrdinal(final int ordinal) {
		return valueOf(ordinal, ProductAssociationType.class);
	}
	
	/**
	 * Find the enum value with the specified name.
	 * @param name the name
	 * @return the enum value
	 */
	public static ProductAssociationType fromName(final String name) {
		return valueOf(name, ProductAssociationType.class);
	}
	
	/**
	 *  @return all the values defined as a Product Association Type.
	 */
	public static Collection<ProductAssociationType> values() {
		return values(ProductAssociationType.class);
	}
}
