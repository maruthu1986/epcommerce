/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerImageRegistry;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Provides label for BaseAmount table viewer.
 */
class SimpleBaseAmountTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	private final BaseAmountSection baseAmountSection;
	private final PriceListEditorController controller;
	
	private final int baseColumnCount;

	/**
	 * Constructor.
	 * @param baseAmountSection - parent <code>BaseAmountSection</code>
	 * @param controller price list editor controller
	 * @param baseColumnCount TODO
	 */
	SimpleBaseAmountTableLabelProvider(
			final BaseAmountSection baseAmountSection, final PriceListEditorController controller, final int baseColumnCount) {
		this.baseAmountSection = baseAmountSection;
		this.controller = controller;
		this.baseColumnCount = baseColumnCount;
	}
	
	private static final int COL_UNSAVED_CHANGES = 0;
	
	private static final int COL_INDEX_QTY = 1;

	private static final int COL_INDEX_LIST = 2;

	private static final int COL_INDEX_SALE = 3;

	/**
	 * Get the column image.
	 * 
	 * @param element not used
	 * @param columnIndex the column to create an image for
	 * @return the image
	 */
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		final BaseAmountDTO baDto = (BaseAmountDTO) element;
		boolean enabledState = baseAmountSection.canEdit(baDto);

		switch (columnIndex) {
		case COL_UNSAVED_CHANGES:
			if (controller.isNewlyAdded(baDto)) {
				return PriceListManagerImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_ADDED_SMALL);
			}
			if (controller.isDeleted(baDto)) {
				return PriceListManagerImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_DELETED_SMALL);
			}
			if (controller.isEdited(baDto)) {
				return PriceListManagerImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_CHANGED_SMALL);
			}
			return null;
		case COL_INDEX_LIST:
			if (enabledState) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
			}
			return null;
		case COL_INDEX_SALE:
			if (enabledState) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
			}
			return null;
		default:
			return null;
		}
	}

	/**
	 * Get the column text from selected row.
	 * 
	 * @param element the data input element
	 * @param columnIndex the column index
	 * @return text
	 */
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		// If the columnIndex is in the extensions range get it.
		if (columnIndex >= this.baseColumnCount) {
			return baseAmountSection.getExtensionColumnText(element, columnIndex);
		}
		
		final BaseAmountDTO baDto = (BaseAmountDTO) element;
		
		switch (columnIndex) {
		case COL_INDEX_QTY:
			Integer quantity = 0;
			if (baDto.getQuantity() != null) {
				quantity = baDto.getQuantity().intValue();
			}
			return quantity.toString();
		case COL_INDEX_LIST:
			return formatUnits(baDto.getListValue());
		case COL_INDEX_SALE:
			return formatUnits(baDto.getSaleValue());
		default:
			return "";  //$NON-NLS-1$
		}
	}

	// Hook for displaying any type of currency formatting in front of prices
	private String formatUnits(final BigDecimal number) {
		if (number == null) {
			return StringUtils.EMPTY;
		}
		return number.setScale(2, RoundingMode.HALF_EVEN).toString();
	}

}