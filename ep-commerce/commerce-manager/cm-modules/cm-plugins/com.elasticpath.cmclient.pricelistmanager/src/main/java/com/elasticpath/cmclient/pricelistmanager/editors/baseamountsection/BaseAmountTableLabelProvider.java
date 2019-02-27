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
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.pricelistmanager.model.impl.BaseAmountType;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Provides label for BaseAmount table viewer.
 */
class BaseAmountTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	private final BaseAmountSection baseAmountSection;

	private final PriceListEditorController controller;
	
	private final int baseColumnCount;

	/**
	 * Constructor.
	 * @param baseAmountSection - parent <code>BaseAmountSection</code>
	 * @param controller - <code>PriceListEditorController</code> instance.
	 * @param baseColumnCount - the base column count.
	 */
	BaseAmountTableLabelProvider(final BaseAmountSection baseAmountSection, final PriceListEditorController controller, final int baseColumnCount) {
		this.baseAmountSection = baseAmountSection;
		this.controller = controller;
		this.baseColumnCount = baseColumnCount;
	}

	private static final int COL_IS_LOCKED = 0;

	private static final int COL_UNSAVED_CHANGES = 1;

	private static final int COL_INDEX_TYPE = 2;

	private static final int COL_INDEX_PRODUCT_NAME = 3;

	private static final int COL_INDEX_PRODUCT_CODE = 4;

	private static final int COL_INDEX_SKU_CODE = 5;

	private static final int COL_INDEX_SKU_CONFIGURATION = 6;

	private static final int COL_INDEX_QTY = 7;

	private static final int COL_INDEX_LIST = 8;

	private static final int COL_INDEX_SALE = 9;

	private static final int COL_INDEX_PAYMENT_SCHEDULE = 10;

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
		boolean isLocked = baseAmountSection.isObjectLocked(baDto);

		switch (columnIndex) {
		case COL_IS_LOCKED:
			if (isLocked) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_LOCKED_SMALL);
			}
			return null;
		case COL_UNSAVED_CHANGES:
			if (controller.isNewlyAdded(baDto)) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_ADDED_SMALL);
			}
			if (controller.isDeleted(baDto)) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_DELETED_SMALL);
			}
			if (controller.isEdited(baDto)) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_CHANGED_SMALL);
			}
			return null;
		case COL_INDEX_TYPE:
			if (BaseAmountType.PRODUCT.getType().equals(baDto.getObjectType())) {
				return CoreImageRegistry.getImage(CoreImageRegistry.PRODUCT_SMALL);
			}
			if (BaseAmountType.SKU.getType().equals(baDto.getObjectType())) {
				return CoreImageRegistry.getImage(CoreImageRegistry.PRODUCT_SKU_SMALL);
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
		case COL_INDEX_PRODUCT_NAME:
			return baDto.getProductName();
		case COL_INDEX_PRODUCT_CODE:
			if (BaseAmountType.PRODUCT.getType().equals(baDto.getObjectType())) {
				return baDto.getObjectGuid();
			} else if (BaseAmountType.SKU.getType().equals(baDto.getObjectType())) {
				return baDto.getProductCode();
			}
			return null;
		case COL_INDEX_SKU_CODE:
			if (BaseAmountType.SKU.getType().equals(baDto.getObjectType())) {
				return baDto.getObjectGuid();
			} else if (BaseAmountType.PRODUCT.getType().equals(baDto.getObjectType()) && !baDto.isMultiSku()) {
				return baDto.getSkuCode();
			}
			return null;
		case COL_INDEX_SKU_CONFIGURATION:
			return baDto.getSkuConfiguration();
		case COL_INDEX_QTY:
			Integer quantity = 0;
			if (baDto.getQuantity() != null) {
				quantity = baDto.getQuantity().intValue();
			}
			return quantity.toString();
		case COL_INDEX_LIST:
			if (baseAmountSection.getEmptyObjects().contains(baDto)) {
				return StringUtils.EMPTY;
			} 
			return formatUnits(baDto.getListValue());
		case COL_INDEX_SALE:
			return formatUnits(baDto.getSaleValue());

		case COL_INDEX_PAYMENT_SCHEDULE:
			return getPaymentScheduleMessage(baDto);
		default:
			return "";  //$NON-NLS-1$
		}
	}

	
	/**
	 * Get the label for the payment schedule column. For now, it will be empty if the list price is null or if
	 * the element is a bundle. If the payment schedule is selected, the name of the payment schedule is returned. 
	 * Otherwise it will return N/A.
	 * @param baDto the row DTO
	 * @return the label for the payment schedule column in the table
	 */
	protected String getPaymentScheduleMessage(final BaseAmountDTO baDto) {
		if (baDto.getListValue() == null) {
			return StringUtils.EMPTY;
		}
		String psName = baDto.getPaymentScheduleName(); 
		if (StringUtils.isEmpty(psName)) {
			return StringUtils.EMPTY;
		}
		return psName;
	}
	
	
	// Hook for displaying any type of currency formatting in front of prices
	private String formatUnits(final BigDecimal number) {
		if (number == null) {
			return StringUtils.EMPTY;
		}
		return number.setScale(2, RoundingMode.HALF_EVEN).toString();
	}
}