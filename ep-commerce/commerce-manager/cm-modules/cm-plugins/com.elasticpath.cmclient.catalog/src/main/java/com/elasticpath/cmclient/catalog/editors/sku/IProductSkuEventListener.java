/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.sku;

/**
 * Used for spreading the events of the product SKU overview section.
 */
public interface IProductSkuEventListener {

	/**
	 * Callback for digital asset option selected.
	 * 
	 * @param digital true or false
	 * @param downloadable true if digital product can be download 
	 */
	void digitalAssetOptionSelected(boolean digital, boolean downloadable);

	/**
	 * Callback for shippable option selected.
	 * 
	 * @param selected true or false
	 */
	void shippableOptionSelected(boolean selected);

	/**
	 * Callback for SKU code change event.
	 * @param skuCodeString the sku code entered by user
	 */
	void skuCodeChanged(String skuCodeString);

	
}
