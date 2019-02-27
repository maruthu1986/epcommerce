/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

/**
 * Represents properties class for managing view of BaseAmountSection.  
 */
public interface BaseAmountTableProperties {

	/**
	 * Returns base amount table height.
	 *
	 * @return base amount table height
	 */
	int getTableHeight();

	/**
	 * Sets base amount table height.
	 *
	 * @param tableHeight - base amount table height
	 */
	void setTableHeight(int tableHeight);

	/**
	 * Returns type column width.
	 *
	 * @return type column width
	 */
	int getTypeWidth();

	/**
	 * Sets type column width.
	 *
	 * @param typeWidth - type column width
	 */
	void setTypeWidth(int typeWidth);

	/**
	 * Returns name column width.
	 *
	 * @return name column width.
	 */
	int getNameWidth();

	/**
	 * Sets name column width.
	 *
	 * @param nameWidth - name column width.
	 */
	void setNameWidth(int nameWidth);

	/**
	 * Returns code column width.
	 *
	 * @return code column width.
	 */
	int getCodeWidth();
	/**
	 * Sets code column width.
	 *
	 * @param codeWidth - code column width.
	 */
	void setCodeWidth(int codeWidth);

	/**
	 * Returns sku code column width.
	 *
	 * @return sku code column width.
	 */
	int getSkuCodeWidth();
	/**
	 * Sets sku code column width.
	 *
	 * @param skuCodeWidth - sku code column width.
	 */
	void setSkuCodeWidth(int skuCodeWidth);

	/**
	 * Returns sku configuration column width.
	 *
	 * @return sku configuration column width.
	 */
	int getSkuConfigurationWidth();
	/**
	 * Sets sku configuration column width.
	 *
	 * @param skuConfigurationWidth - sku configuration column width.
	 */
	void setSkuConfigurationWidth(int skuConfigurationWidth);

	/**
	 * Returns quantity column width.
	 *
	 * @return quantity column width.
	 */
	int getQuantityWidth();
	/**
	 * Sets quantity column width.
	 *
	 * @param quantityWidth - quantity column width.
	 */
	void setQuantityWidth(int quantityWidth);
	/**
	 * Returns list price column width.
	 *
	 * @return list price column width.
	 */
	int getListPriceWidth();
	/**
	 * Sets list price column width.
	 *
	 * @param listPriceWidth - list price column width.
	 */
	void setListPriceWidth(int listPriceWidth);

	/**
	 * Returns sale price column width.
	 *
	 * @return sale price column width.
	 */
	int getSalePriceWidth();
	/**
	 * Sets sale price column width.
	 *
	 * @param salePriceWidth - sale price column width.
	 */
	void setSalePriceWidth(int salePriceWidth);

	/**
	 * Returns payment schedule column width.
	 *
	 * @return payment schedule column width.
	 */
	int getPaymentScheduleWidth();
	/**
	 * Sets payment schedule column width.
	 *
	 * @param paymentScheduleWidth - payment schedule column width.
	 */
	void setPaymentScheduleWidth(int paymentScheduleWidth);



	/**
	 * Returns unsaved changes column width.
	 *
	 * @return unsaved changes column width.
	 */
	int getUnsavedChangesWidth();
	/**
	 * Sets unsaved changes column width.
	 *
	 * @param unsavedChangesWidth - unsaved changes column width.
	 */
	void setUnsavedChangesWidth(int unsavedChangesWidth);
	/**
	 * Returns "is object locked" column width.
	 *
	 * @return "is object locked" column width.
	 */
	int getIsLockedWidth();
	/**
	 * Sets "is object locked" column width.
	 *
	 * @param isLockedWidth - "is object locked" column width.
	 */
	void setIsLockedWidth(int isLockedWidth);

	/**
	 * Returns edit button caption.
	 *
	 * @return edit button caption.
	 */
	String getEditButtonCaption();

	/**
	 * Sets edit button caption.
	 *
	 * @param editButtonCaption - edit button caption.
	 */
	void setEditButtonCaption(String editButtonCaption);

	/**
	 * Returns add button caption.
	 *
	 * @return add button caption.
	 */
	String getAddButtonCaption();
	/**
	 * Sets add button caption.
	 *
	 * @param addButtonCaption - add button caption.
	 */
	void setAddButtonCaption(String addButtonCaption);
	/**
	 * Returns edit button caption.
	 *
	 * @return edit button caption.
	 */
	String getDeleteButtonCaption();
	/**
	 * Sets delete button caption.
	 *
	 * @param deleteButtonCaption - delete button caption.
	 */
	void setDeleteButtonCaption(String deleteButtonCaption);

	/**
	 * Sets the edit button enabled flag.
	 * @param editButtonEnabled true if enabled
	 */
	void setEditButtonEnabled(boolean editButtonEnabled);

	/**
	 * @return true if enabled
	 */
	boolean isEditButtonEnabled();
}