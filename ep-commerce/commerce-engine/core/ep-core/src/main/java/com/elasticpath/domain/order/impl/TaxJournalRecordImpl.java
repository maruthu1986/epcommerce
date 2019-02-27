/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Default implementation for {@link TaxJournalRecord}.
 */

@Entity
@Table(name = TaxJournalRecordImpl.TABLE_NAME)
public class TaxJournalRecordImpl extends AbstractPersistableImpl implements TaxJournalRecord {
	
	private static final long serialVersionUID = 500000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TTAXJOURNAL";

	private long uidPk;
	private String documentId;
	private String itemGuid;
	private String itemCode;
	private String taxName;
	private BigDecimal taxAmount;
	private BigDecimal taxRate;
	private BigDecimal itemAmount;
	private String taxCode;
	private String taxJurisdiction;
	private String taxRegion;
	private Date transactionDate;
	private String journalType;
	private String taxProvider;
	private String storeCode;
	private String currency;
	private boolean taxInclusive;
	private String orderNumber;
	private String transactionType;
	private String itemObjectType;
	
	@Override
	@Basic(optional = false)
	@Column(name = "DOCUMENT_ID", nullable = false)
	public String getDocumentId() {
		return documentId;
	}

	@Override
	public void setDocumentId(final String documentId) {
		this.documentId = documentId;
	}
	
	@Override
	@Basic(optional = false)
	@Column(name = "TAX_NAME", nullable = false)
	public String getTaxName() {
		return taxName;
	}
	
	@Override
	public void setTaxName(final String taxName) {
		this.taxName = taxName;
	}
	
	@Override
	@Basic(optional = false)
	@Column(name = "TAX_AMOUNT", nullable = false)
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}
	
	@Override
	public void setTaxAmount(final BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}
	
	@Override
	@Basic(optional = false)
	@Column(name = "TAX_RATE", nullable = false)
	public BigDecimal getTaxRate() {
		return taxRate;
	}
	
	@Override
	public void setTaxRate(final BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	
	@Override
	@Basic(optional = false)
	@Column(name = "TAX_CODE", nullable = false)
	public String getTaxCode() {
		return taxCode;
	}
	
	@Override
	public void setTaxCode(final String taxCode) {
		this.taxCode = taxCode;
	}
	
	@Override
	@Basic(optional = false)
	@Column(name = "ITEM_CODE", nullable = false)
	public String getItemCode() {
		return itemCode;
	}
	
	@Override
	public void setItemCode(final String itemCode) {
		this.itemCode = itemCode;
	}
	
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME, allocationSize = HIGH_CONCURRENCY_ALLOCATION_SIZE)
	public long getUidPk() {
		return this.uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Basic(optional = false)
	@Column(name = "ITEM_AMOUNT", nullable = false)
	public BigDecimal getItemAmount() {
		return itemAmount;
	}

	@Override
	public void setItemAmount(final BigDecimal itemAmount) {
		this.itemAmount = itemAmount;
	}

	@Override
	@Basic(optional = false)
	@Column(name = "TAX_JURISDICTION", nullable = false)
	public String getTaxJurisdiction() {
		return taxJurisdiction;
	}

	@Override
	public void setTaxJurisdiction(final String taxJurisdiction) {
		this.taxJurisdiction = taxJurisdiction;
	}

	@Override
	@Basic(optional = false)
	@Column(name = "TAX_REGION", nullable = false)
	public String getTaxRegion() {
		return taxRegion;
	}

	@Override
	public void setTaxRegion(final String taxRegion) {
		this.taxRegion = taxRegion;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TRANSACTION_DATE", nullable = false, insertable = false, updatable = false)
	public Date getTransactionDate() {
		return transactionDate;
	}

	protected void setTransactionDate(final Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	@Override
	@Basic(optional = false)
	@Column(name = "ITEM_GUID")
	public String getItemGuid() {
		return itemGuid;
	}

	@Override
	public void setItemGuid(final String itemGuid) {
		this.itemGuid = itemGuid;
	}

	@Override
	@Basic(optional = false)
	@Column(name = "JOURNAL_TYPE", nullable = false)
	public String getJournalType() {
		return journalType;
	}

	@Override
	public void setJournalType(final String journalType) {
		this.journalType = journalType;
	}
	
	@Override
	@Basic(optional = false)
	@Column(name = "TAX_PROVIDER", nullable = false)
	public String getTaxProvider() {
		return this.taxProvider;
	}
	
	@Override
	public void setTaxProvider(final String taxProvider) {
		this.taxProvider = taxProvider;
	}

	@Override
	@Basic(optional = false)
	@Column(name = "STORE_CODE", nullable = false)
	public String getStoreCode() {
		return storeCode;
	}

	@Override
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}
	
	@Override
	@Basic(optional = false)
	@Column(name = "CURRENCY", nullable = false)
	public String getCurrency() {
		return currency;
	}
	
	@Override
	public void setCurrency(final String currency) {
		this.currency = currency;
	}
	
	@Override
	@Basic(optional = false)
	@Column(name = "TAX_INCLUSIVE", nullable = false)
	public boolean isTaxInclusive() {
		return taxInclusive;
	}
	
	@Override
	public void setTaxInclusive(final boolean taxInclusive) {
		this.taxInclusive = taxInclusive;
	}

	@Override
	@Basic
	@Column(name = "ORDER_NUMBER")
	public String getOrderNumber() {
		return orderNumber;
	}

	@Override
	public void setOrderNumber(final String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	@Basic
	@Column(name = "TRANSACTION_TYPE")
	public String getTransactionType() {
		return transactionType;
	}

	@Override
	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	@Override
	@Basic
	@Column(name = "ITEM_OBJECT_TYPE")
	public String getItemObjectType() {
		return itemObjectType;
	}

	@Override
	public void setItemObjectType(final String itemObjectType) {
		this.itemObjectType = itemObjectType;
	}

}
