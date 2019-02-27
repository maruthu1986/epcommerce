/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link com.elasticpath.cmclient.core.csv.PriceListCsvExportNotificationListener} that acts as a
 * composite of a list of listeners.
 */
public class PriceListCsvExportNotificationListenerComposite implements PriceListCsvExportNotificationListener {

	private final List<PriceListCsvExportNotificationListener> listeners = new ArrayList<PriceListCsvExportNotificationListener>();

	@Override
	public void notifyError(final Exception exception) {
		for (PriceListCsvExportNotificationListener listener : listeners) {
			listener.notifyError(exception);
		}
	}

	@Override
	public void notifyDone() {
		for (PriceListCsvExportNotificationListener listener : listeners) {
			listener.notifyDone();
		}
	}

	@Override
	public void notifyExportingData() {
		for (PriceListCsvExportNotificationListener listener : listeners) {
			listener.notifyExportingData();
		}
	}

	@Override
	public void notifyTransformingData() {
		for (PriceListCsvExportNotificationListener listener : listeners) {
			listener.notifyTransformingData();
		}
	}

	@Override
	public void notifyLoadingData() {
		for (PriceListCsvExportNotificationListener listener : listeners) {
			listener.notifyLoadingData();
		}
	}

	@Override
	public void notifyStart() {
		for (PriceListCsvExportNotificationListener listener : listeners) {
			listener.notifyStart();
		}
	}

	/**
	 * Add a listener.
	 *
	 * @param listener the listener to add
	 */
	public void addListener(final PriceListCsvExportNotificationListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener.
	 *
	 * @param listener the listener to remove
	 */
	public void removeListener(final PriceListCsvExportNotificationListener listener) {
		listeners.remove(listener);
	}

	@Override
	public boolean isCancelled() {
		for (PriceListCsvExportNotificationListener listener : listeners) {
			if (listener.isCancelled()) {
				return true;
			}
		}
		return false;
	}
}
