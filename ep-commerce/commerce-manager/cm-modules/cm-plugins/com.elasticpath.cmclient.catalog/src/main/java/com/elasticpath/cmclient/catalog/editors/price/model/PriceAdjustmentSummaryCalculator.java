/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.cmclient.catalog.editors.price.PriceAdjustmentSummaryUpdater;

/**
 * Updates price adjustment summary on given event.
 */
public class PriceAdjustmentSummaryCalculator implements PropertyChangeListener {
	/**
	 * An ID to identify price change.
	 */
	public static final String PRICE_CHANGED_PROPERTY = 
		"com.elasticpath.cmclient.catalog.editors.price.PriceAdjustmentSummaryCalculator.PRICE_CHANGED_PROPERTY"; //$NON-NLS-1$

	private final PriceAdjustmentSummaryUpdater updater;

	/**
	 * Constructor.
	 * 
	 * @param updater updater
	 */
	public PriceAdjustmentSummaryCalculator(final PriceAdjustmentSummaryUpdater updater) {
		this.updater = updater;
	}

	private BigDecimal getPriceBigDecimal(final BigDecimal price) {
		if (price == null) {
			return BigDecimal.ZERO;
		}

		return price;
	}

	private Collection<PriceAdjustmentModel> getSelectedModels(final PriceAdjustmentModel parent) {
		List<PriceAdjustmentModel> result = new ArrayList<>();
		for (PriceAdjustmentModel child : parent.getChildren()) {
			if (child.isSelected()) {
				result.add(child);
			}

			result.addAll(getSelectedModels(child));
		}

		return result;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		PriceAdjustmentModel rootModel = (PriceAdjustmentModel) event.getNewValue();
		Collection<PriceAdjustmentModel> selectedModels = getSelectedModels(rootModel);

		BigDecimal itemPriceTotal = null;
		BigDecimal adjustedTotal = null;

		if (rootModel.getPrice() != null) {
			for (PriceAdjustmentModel model : selectedModels) {
				BigDecimal price = getPriceBigDecimal(model.getPrice());
				BigDecimal priceAdjustmentAmount = getPriceBigDecimal(model.getPriceAdjustment());

				if (model.getChildren().isEmpty()) {
					itemPriceTotal = addTo(itemPriceTotal, price, model.getQuantity());
				}

				adjustedTotal = addTo(adjustedTotal, priceAdjustmentAmount, 1);
			}
		}

		BigDecimal totalAdjustedPrice = null;
		if (rootModel.getPrice() != null && adjustedTotal != null) {
			totalAdjustedPrice = rootModel.getPrice().add(adjustedTotal);
		}

		BigDecimal savings = null;
		if (itemPriceTotal != null && totalAdjustedPrice != null) {
			savings = itemPriceTotal.subtract(totalAdjustedPrice);
		}

		updater.updatePriceAdjustmentSummary(itemPriceTotal, totalAdjustedPrice, savings);
	}

	private BigDecimal addTo(final BigDecimal toBeAddedTo, final BigDecimal addTo, final int quantityToAddTo) {
		BigDecimal result = toBeAddedTo;
		if (result == null) {
			result = BigDecimal.ZERO;
		}

		return result.add(addTo.multiply(BigDecimal.valueOf(quantityToAddTo)));
	}
}
