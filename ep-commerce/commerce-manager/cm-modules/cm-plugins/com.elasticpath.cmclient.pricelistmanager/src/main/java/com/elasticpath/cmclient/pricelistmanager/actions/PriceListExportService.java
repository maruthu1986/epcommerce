/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.actions;

import com.elasticpath.cmclient.core.csv.PriceListCsvExportNotificationListener;

/**
 * Export pricing information for a price list.
 */
public interface PriceListExportService {

	/**
	 * @param priceListGuid The price list guid.
	 */
	void exportPrices(String priceListGuid);

	// TODO: Clean up these accessors.
	/**
	 * @return String - The exported file name.
	 */
	// TODO: This ties the interface to a file-based paradigm for no good reason.
	String getFileName();

	/**
	 * @param listener The listener.
	 */
	void addListenter(PriceListCsvExportNotificationListener listener);

	/**
	 * @param listener The listener.
	 */
	void removeListener(PriceListCsvExportNotificationListener listener);
}