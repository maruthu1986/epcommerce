/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.catalog;


/**
 * Specifies characteristics of a specifically configured item.
 */
public interface ItemCharacteristics {

	/**
	 * Is the item shippable?
	 *
	 * @return true, if is shippable
	 */
	boolean isShippable();

	/**
	 * Checks if the offer requires selection action from the user (i.e. if the offer includes a multi-sku product or dynamic bundle).
	 *
	 * @return true if the offer requires selection
	 */
	boolean isConfigurable();

}
