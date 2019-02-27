/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui;

/**
 * Class that contains ratios for the views sizes.
 * Used in PerspectiveFactories
 */
public final class PerspectiveDefaults {

	private PerspectiveDefaults() {
		//empty constructor
	}

	private static final float FULFILLMENT_DIFF_ERROR = 1.355f;


	/**
	 * Ratio for the view, which is on the left side form the editor.
	 */
	public static final float LEFT_RATIO = 0.22f;

	/**
	 * Ratio for the view, which is on the top side form the editor.
	 */
	public static final float TOP_RATIO = 0.38f;
	/**
	 * Ratio for the fulfilment view. The only perspective that behaves differently.
	 */
	public static final float FULFILLMENT_LEFT_RATIO = LEFT_RATIO * FULFILLMENT_DIFF_ERROR;
}
