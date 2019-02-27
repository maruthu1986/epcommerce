/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.csv;

/**
 *
 */
public class PriceListCsvExportChunkListener {

	private PriceListCsvExportNotificationListener listener;

	public boolean isCancelled() {
		return listener.isCancelled();
	}
	public void setListener(final PriceListCsvExportNotificationListener listener) {
		this.listener = listener;
	}

}
