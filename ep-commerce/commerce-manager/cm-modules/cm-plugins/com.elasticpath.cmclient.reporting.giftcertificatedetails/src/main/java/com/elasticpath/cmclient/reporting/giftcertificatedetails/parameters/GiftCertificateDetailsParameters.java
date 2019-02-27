/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.giftcertificatedetails.parameters;

import java.util.Date;

/**
 * Represents the parameters defined for the Gift Certificate Details report.
 *
 */
public class GiftCertificateDetailsParameters {

	private long storeUidPk;
	
	private String storeName;
	
	private Date startDate;
	
	private Date endDate;
	
	private String[] currencies;

	
	/**
	 * @return store Uid Pk
	 */
	public long getStoreUidPk() {
		return storeUidPk;
	}
	/**
	 * @param storeUidPk store Uid Pk
	 */
	public void setStoreUidPk(final long storeUidPk) {
		this.storeUidPk = storeUidPk;
	}

	/**
	 * @return String the store name
	 */
	public String getStoreName() {
		return storeName;
	}

	/**
	 * @param storeName the store name
	 */
	public void setStoreName(final String storeName) {
		this.storeName = storeName;
	}

	/**
	 * @return the purchased from date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the purchased from date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the purchased to date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the purchased to date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return array of currencies for GC's
	 */
	public String[] getCurrencies() {
		return currencies; // NOPMD
	}

	/**
	 * @param currencies array of currencies for GC's
	 */
	public void setCurrencies(final String[] currencies) { // NOPMD
		this.currencies = currencies;
	}
	
}
