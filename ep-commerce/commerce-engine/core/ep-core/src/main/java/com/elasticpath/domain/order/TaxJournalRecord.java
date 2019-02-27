/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.domain.order;

import java.math.BigDecimal;
import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * The Tax Journal contains detailed tax calculation information.
 */
public interface TaxJournalRecord extends Persistable {

	/**
	 * Gets the document ID.
     *
	 * @return the document ID
	 */
	String getDocumentId();
	
	/**
	 * Sets the document ID.
     *
	 * @param documentId the document ID
	 */
	void setDocumentId(String documentId);

	/**
	 * Gets the tax name.
     *
	 * @return the tax name
	 */
	String getTaxName();

	/**
	 * Sets the tax name.
     *
	 * @param name the tax name
	 */
	void setTaxName(String name);
	
	/**
	 * Gets the tax amount.
     *
	 * @return the tax amount
	 */
	BigDecimal getTaxAmount();
	
	/**
	 * Sets the tax amount.
     *
	 * @param value the tax amount
	 */
	void setTaxAmount(BigDecimal value);

	/**
	 * Gets the tax code.
     *
	 * @return the tax code
	 */
	String getTaxCode();
	
	/**
	 * Sets the tax code.
     *
	 * @param taxCode the tax code
	 */
	void setTaxCode(String taxCode);

	/**
	 * Gets the tax rate.
     *
	 * @return the tax rate
	 */
	BigDecimal getTaxRate();
	
	/**
	 * Sets the tax rate.
     *
	 * @param taxRate the tax rate
	 */
	void setTaxRate(BigDecimal taxRate);

	/**
	 * Gets the item code.
     *
	 * @return the item code
	 */
	String getItemCode();
	
	/**
	 * Sets the item code.
     *
	 * @param itemCode the item code
	 */
	void setItemCode(String itemCode);
	
	/**
	 * Gets the item GUID.
     *
	 * @return the item guid
	 */
	String getItemGuid();
	
	/**
	 * Sets the item GUID.
     *
	 * @param itemGuid the item guid
	 */
	void setItemGuid(String itemGuid);
	
	/**
	 * Gets the item amount.
     *
	 * @return the item amount
	 */
	BigDecimal getItemAmount();
	
	/**
	 * Sets the item amount.
     *
	 * @param itemAmount the item amount
	 */
	void setItemAmount(BigDecimal itemAmount);
	
	/**
	 * Gets the transaction date.
	 * 
	 * @return the transaction date
	 */
	Date getTransactionDate();
	
	/**
	 * Gets the journal type.
	 * 
	 * @return the journal type
	 */
	String getJournalType();

	/**
	 * Sets the journal type.
	 * 
	 * @param journalType the journalType to set
	 */
	void setJournalType(String journalType);

	/**
	 * Gets the tax jurisdiction.
	 * 
	 * @return the tax jurisdiction
	 */
	String getTaxJurisdiction();
	
	/**
	 * Sets the tax jurisdiction.
	 * 
	 * @param taxJurisdiction the tax jurisdiction to set
	 */
	void setTaxJurisdiction(String taxJurisdiction);
	
	/**
	 * Gets the tax region.
	 * 
	 * @return the tax region
	 */
	String getTaxRegion();
	
	/**
	 * Sets the tax region.
	 * 
	 * @param taxRegion the tax region
	 */
	void setTaxRegion(String taxRegion);
	
	/**
	 * Gets the tax provider.
	 * 
	 * @return the tax provider
	 */
	String getTaxProvider();
	
	/**
	 * Sets the tax provider.
	 * 
	 * @param taxProvider the tax provider
	 */
	void setTaxProvider(String taxProvider);
	
	/**
	 * Gets the store code.
	 * 
	 * @return the store code.
	 */
	String getStoreCode();
	
	/**
	 * Sets the store code.
	 * 
	 * @param storeCode the store code
	 */
	void setStoreCode(String storeCode);
	
	/**
	 * Gets the currency code.
	 * 
	 * @return the tax currency
	 */
	String getCurrency();
	
	/**
	 * Sets the currency code.
	 * 
	 * @param currency the tax currency
	 */
	void setCurrency(String currency);
	
	/**
	 * Returns whether tax calculations are inclusive.
	 * 
	 * @return true if the tax journal is in a tax inclusive jurisdiction
	 */
	boolean isTaxInclusive();
	
	/**
	 * Sets the tax inclusive flag.
	 * 
	 * @param taxInclusive the tax inclusive flag
	 */
	void setTaxInclusive(boolean taxInclusive);
	
	/**
	 * Gets the order number.
	 * 
	 * @return the order number
	 */
	String getOrderNumber();
	
	/**
	 * Sets the order number.
	 * 
	 * @param orderNumber the order number
	 */
	void setOrderNumber(String orderNumber);

	/**
	 * Gets the transaction type.
	 * 
	 * @return the transaction type
	 */
	String getTransactionType();
	
	/**
	 * Sets the transaction type.
	 * 
	 * @param transactionType the transactionType
	 */
	void setTransactionType(String transactionType);

	/**
	 * Gets the item object type, which is related to {@OrderSku} or {@link OrderReturnSku}.
	 * 
	 * @return the item object type
	 */
	String getItemObjectType();
		
	/**
	 * Sets the item object type, which is related to {@OrderSku} or {@link OrderReturnSku}.
	 * 
	 * @param itemObjectType the given item object type
	 */
	void setItemObjectType(String itemObjectType);
	
}
