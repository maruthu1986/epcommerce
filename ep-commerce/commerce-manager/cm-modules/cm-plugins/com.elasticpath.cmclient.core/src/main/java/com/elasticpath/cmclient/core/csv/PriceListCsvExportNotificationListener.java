/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.csv;

/**
 * A listener of price list CSV export events.
 *
 * TODO: Could use a command object like notify(Event) ... more open for extension.
 */
public interface PriceListCsvExportNotificationListener {

	/**
	 * Notify that an exception has occurred.
	 *
	 * @param exception the exception that occurred
	 */
	void notifyError(Exception exception);

	/**
	 * Notify that the export is done.
	 */
	void notifyDone();

	/**
	 * Notify that the export has started.
	 */
	void notifyExportingData();

	/**
	 * Notify that the data is being transformed.
	 */
	void notifyTransformingData();

	/**
	 * Notify that the data is being loaded.
	 */
	void notifyLoadingData();

	/**
	 * Notify that the export process has started.
	 */
	void notifyStart();

	/**
	 * Indicate whether the export has been cancelled.
	 *
	 * @return true if cancelled
	 */
	boolean isCancelled();
}
