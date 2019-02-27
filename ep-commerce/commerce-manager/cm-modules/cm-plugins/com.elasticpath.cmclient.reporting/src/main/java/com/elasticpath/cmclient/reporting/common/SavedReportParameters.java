/**
 * Copyright (c) Elastic Path Software Inc., 2017
 *
 */
package com.elasticpath.cmclient.reporting.common;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.domain.store.Store;
import org.eclipse.swt.custom.CCombo;

import java.text.ParseException;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Remembers and restores report parameters for a user across multiple reports.
 */
public class SavedReportParameters {

	private final Map<String, Object> parameters = new HashMap<>();

	/**
	 * Get the session instance.
	 * @return the session instance.
	 */
	public static SavedReportParameters getInstance() {
		return CmSingletonUtil.getSessionInstance(SavedReportParameters.class);
	}

	/**
	 * Saves report parameters.
	 *
	 * @param parameters the parameters to be saved
	 */
	public void saveParameters(final Map<String, Object> parameters) {
		this.parameters.putAll(parameters);
	}

	/**
	 * Restores the previously selected store.
	 *
	 * @param storeCombo the store combo box
	 * @param availableStores list of available stores
	 * @param reportParameters the current parameters for the report
	 * @return index of selected store - 0 indicates no store selected
	 */
	public int restoreStoreSelection(final CCombo storeCombo, final List<Store> availableStores, final ReportParameters reportParameters) {
		storeCombo.select(0);
		reportParameters.setStore(null);
		int result = 0;
		final int storeIndex = getSavedStoreIndex(availableStores);
		if (storeIndex != -1) {
			final Store store = availableStores.get(storeIndex);
			for (int comboIndex = 0; comboIndex < storeCombo.getItemCount(); comboIndex++) {
				if (storeCombo.getItem(comboIndex).equalsIgnoreCase(store.getName())) {
					storeCombo.select(comboIndex);
					reportParameters.setStore(store.getCode());
					result = storeIndex + 1;
					break;
				}
			}
		}
		return result;
	}

	private int getSavedStoreIndex(final List<Store> availableStores) {
		final String storeCode = (String) parameters.get("store");
		if (storeCode != null) {
			for (int storeIndex = 0; storeIndex < availableStores.size(); storeIndex++) {
				if (availableStores.get(storeIndex).getCode().equals(storeCode)) {
					return storeIndex;
				}
			}
		}
		return -1;
	}

	/**
	 * Restores the previously selected currency.
	 *
	 * @param currencyCombo the currency combo box
	 * @param reportParameters the current parameters for the report
	 */
	public void restoreCurrencySelection(final CCombo currencyCombo, final ReportParameters reportParameters) {
		currencyCombo.select(0);
		reportParameters.setCurrency(null);
		final String currencyCode = (String) parameters.get("currency");
		if (currencyCode != null) {
			for (int comboIndex = 0; comboIndex < currencyCombo.getItemCount(); comboIndex++) {
				if (currencyCombo.getItem(comboIndex).equalsIgnoreCase(currencyCode)) {
					currencyCombo.select(comboIndex);
					Currency currency = (Currency) currencyCombo.getData(currencyCode);
					reportParameters.setCurrency(currency);
					break;
				}
			}
		}
	}

	/**
	 * Restores the previous start date.
	 *
	 * @param datePicker the date picker
	 */
	public void restoreStartDate(final IEpDateTimePicker datePicker) {

		Object startDate = parameters.get("startDate");

		restoreDate(datePicker, startDate);
	}

	/**
	 * Restores the previous end date.
	 *
	 * @param datePicker the date picker
	 */
	public void restoreEndDate(final IEpDateTimePicker datePicker) {
		Object endDate = parameters.get("endDate");
		restoreDate(datePicker, endDate);
	}

	private void restoreDate(final IEpDateTimePicker datePicker, final Object dateObject) {
		if (dateObject instanceof String) {
			try {
				datePicker.setDate(DateTimeUtilFactory.getDateUtil().parseDateTime((String) dateObject));
			} catch (ParseException exception) {

			}
		}
	}

}
