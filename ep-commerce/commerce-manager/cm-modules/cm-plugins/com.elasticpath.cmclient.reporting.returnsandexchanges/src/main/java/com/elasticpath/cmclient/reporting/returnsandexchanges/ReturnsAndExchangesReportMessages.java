/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.reporting.returnsandexchanges;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;
import com.elasticpath.domain.order.OrderReturnStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Messages class for the report plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.VariableNamingConventions" })
public final class ReturnsAndExchangesReportMessages {

	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.reporting.returnsandexchanges." + //$NON-NLS-1$
			"ReturnsAndExchangesReportPluginResources"; //$NON-NLS-1$

	private ReturnsAndExchangesReportMessages() {
	}

	public String reportTitle;
	public String report;

	// ----------------------------------------------------
	// Registration Report params UI
	// ----------------------------------------------------
	public String store;
	public String currency;
	public String fromDate;
	public String toDate;
	public String rmaType;
	public String statusGroupHeader;
	
	public String waitExchangeComplete;
	public String cancelled;
	public String complete;
	public String awaitingStockReturn;
	public String awatingCompletion;

	public String selectStore;
	
	public String returnsAndExchanges;
	public String returnsOnly;
	public String exchangesOnly;
	public String checkBoxNoneSelectedError;
	
	// ----------------------------------------------------
	// Registration Report service
	// ----------------------------------------------------
	public String no_as_string;
	public String yes_as_string;

	// ----------------------------------------------------
	// Order Return Status Text
	// ----------------------------------------------------
	public String OrderReturnStatus_Cancelled;
	public String OrderReturnStatus_Completed;
	public String OrderReturnStatus_AwaitingCompletion;
	public String OrderReturnStatus_AwaitingStockReturn;
	public String OrderReturnType_Exchange;
	public String OrderReturnType_Return;


	// Define the map of enum constants to localized names
	private final Map<OrderReturnStatus, String> localizedExtensibleEnums = new HashMap<>();

	private void instantiateEnums() {
		if (localizedExtensibleEnums.isEmpty()) {
			localizedExtensibleEnums.put(OrderReturnStatus.AWAITING_COMPLETION, OrderReturnStatus_AwaitingCompletion);
			localizedExtensibleEnums.put(OrderReturnStatus.AWAITING_STOCK_RETURN, OrderReturnStatus_AwaitingStockReturn);
			localizedExtensibleEnums.put(OrderReturnStatus.CANCELLED, OrderReturnStatus_Cancelled);
			localizedExtensibleEnums.put(OrderReturnStatus.COMPLETED, OrderReturnStatus_Completed);
		}
	}

	/**
	 * Returns the localized name of the given enum constant.
	 *
	 * @param enumValue the enum to be localized
	 * @return the localized string for the enum
	 */
	public String getLocalizedName(final OrderReturnStatus enumValue) {
		return localizedExtensibleEnums.get(enumValue);
	}
	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static ReturnsAndExchangesReportMessages get() {
		ReturnsAndExchangesReportMessages returnsAndExchangesReportMessages =
				LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, ReturnsAndExchangesReportMessages.class);
		returnsAndExchangesReportMessages.instantiateEnums();
		return returnsAndExchangesReportMessages;
	}

}
