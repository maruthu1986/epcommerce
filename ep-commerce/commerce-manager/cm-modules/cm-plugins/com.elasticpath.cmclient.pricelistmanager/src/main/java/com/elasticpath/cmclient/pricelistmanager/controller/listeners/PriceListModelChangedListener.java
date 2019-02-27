/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.controller.listeners;

/**
 * This interface should be implemented by the classes who need to be notified by the changes in the price list model.
 */
public interface PriceListModelChangedListener {

	/**
	 * Notifies about the price list model changes.
	 */
	void notifyPriceListModelChanged();
	
}
