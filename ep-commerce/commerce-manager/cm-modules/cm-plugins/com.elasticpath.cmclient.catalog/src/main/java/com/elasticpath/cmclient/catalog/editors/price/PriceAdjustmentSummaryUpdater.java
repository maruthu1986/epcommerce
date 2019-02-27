/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors.price;

import java.math.BigDecimal;

/**
 * Updates the price adjustment summary on UI.
 */
public interface PriceAdjustmentSummaryUpdater {
	/**
	 * Updates the summary.
	 * 
	 * @param listPricesBasedOnSelections listPricesBasedOnSelections
	 * @param totalWithAdjustments totalWithAdjustments
	 * @param savings savings
	 */ 
	void updatePriceAdjustmentSummary(BigDecimal listPricesBasedOnSelections, BigDecimal totalWithAdjustments, BigDecimal savings);
}
