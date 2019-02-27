/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;

/**
 * Class represents properties class for managing view of BaseAmountSection.  
 */
public class DefaultBaseAmountTableProperties implements BaseAmountTableProperties {

	private static final int TABLE_HEIGHT = 100; 

	private static final int IMAGE_WIDTH = 25;

	private static final int PRODUCT_NAME_WIDTH = 130;

	private static final int CODE_WIDTH = 130;

	private static final int SKU_CONFIGURATION_WIDTH = 125;

	private static final int PAYMENT_SCHEDULE_WIDTH = 100;

	private static final int QUANTITY_WIDTH = 75;

	private static final int PRICE_WIDTH = 100;

	private int tableHeight = TABLE_HEIGHT;

	private int typeWidth = IMAGE_WIDTH;

	private int nameWidth = PRODUCT_NAME_WIDTH;

	private int codeWidth = CODE_WIDTH;

	private int skuCodeWidth = CODE_WIDTH;

	private int skuConfigurationWidth = SKU_CONFIGURATION_WIDTH;

	private int quantityWidth = QUANTITY_WIDTH;

	private int listPriceWidth = PRICE_WIDTH;

	private int paymentScheduleWidth = PAYMENT_SCHEDULE_WIDTH;
	
	private int salePriceWidth = PRICE_WIDTH;

	private int unsavedChangesWidth = IMAGE_WIDTH;

	private int isLockedWidth = IMAGE_WIDTH;
	
	private String editButtonCaption = PriceListManagerMessages.get().BaseAmount_Edit;
	
	private String addButtonCaption = PriceListManagerMessages.get().BaseAmount_Add;
	
	private String deleteButtonCaption = PriceListManagerMessages.get().BaseAmount_Delete;

	private boolean editButtonEnabled = true;
	
	@Override
	public int getTableHeight() {
		return tableHeight;
	}
	
	@Override
	public void setTableHeight(final int tableHeight) {
		this.tableHeight = tableHeight;
	}

	@Override
	public int getTypeWidth() {
		return typeWidth;
	}

	@Override
	public void setTypeWidth(final int typeWidth) {
		this.typeWidth = typeWidth;
	}
	
	@Override
	public int getNameWidth() {
		return nameWidth;
	}

	@Override
	public void setNameWidth(final int nameWidth) {
		this.nameWidth = nameWidth;
	}

	@Override
	public int getCodeWidth() {
		return codeWidth;
	}

	@Override
	public void setCodeWidth(final int codeWidth) {
		this.codeWidth = codeWidth;
	}

	@Override
	public int getSkuCodeWidth() {
		return skuCodeWidth;
	}

	@Override
	public void setSkuCodeWidth(final int skuCodeWidth) {
		this.skuCodeWidth = skuCodeWidth;
	}

	@Override
	public int getSkuConfigurationWidth() {
		return skuConfigurationWidth;
	}

	@Override
	public void setSkuConfigurationWidth(final int skuConfigurationWidth) {
		this.skuConfigurationWidth = skuConfigurationWidth;
	}

	@Override
	public int getQuantityWidth() {
		return quantityWidth;
	}
	
	@Override
	public void setQuantityWidth(final int quantityWidth) {
		this.quantityWidth = quantityWidth;
	}

	@Override
	public int getListPriceWidth() {
		return listPriceWidth;
	}

	@Override
	public void setListPriceWidth(final int listPriceWidth) {
		this.listPriceWidth = listPriceWidth;
	}

	@Override
	public int getSalePriceWidth() {
		return salePriceWidth;
	}

	@Override
	public void setSalePriceWidth(final int salePriceWidth) {
		this.salePriceWidth = salePriceWidth;
	}

	@Override
	public int getUnsavedChangesWidth() {
		return unsavedChangesWidth;
	}

	@Override
	public void setUnsavedChangesWidth(final int unsavedChangesWidth) {
		this.unsavedChangesWidth = unsavedChangesWidth;
	}

	@Override
	public int getIsLockedWidth() {
		return isLockedWidth;
	}

	@Override
	public void setIsLockedWidth(final int isLockedWidth) {
		this.isLockedWidth = isLockedWidth;
	}

	@Override
	public String getEditButtonCaption() {
		return editButtonCaption;
	}

	@Override
	public void setEditButtonCaption(final String editButtonCaption) {
		this.editButtonCaption = editButtonCaption;
	}
	@Override
	public String getAddButtonCaption() {
		return addButtonCaption;
	}
	@Override
	public void setAddButtonCaption(final String addButtonCaption) {
		this.addButtonCaption = addButtonCaption;
	}
	@Override
	public String getDeleteButtonCaption() {
		return deleteButtonCaption;
	}
	@Override
	public void setDeleteButtonCaption(final String deleteButtonCaption) {
		this.deleteButtonCaption = deleteButtonCaption;
	}
	@Override
	public void setEditButtonEnabled(final boolean editButtonEnabled) {
		this.editButtonEnabled = editButtonEnabled;
	}
	@Override
	public boolean isEditButtonEnabled() {
		return editButtonEnabled;
	}

	@Override
	public int getPaymentScheduleWidth() {
		return paymentScheduleWidth;
	}

	@Override
	public void setPaymentScheduleWidth(final int paymentScheduleWidth) {
		this.paymentScheduleWidth = paymentScheduleWidth;
	}


	
	
}
