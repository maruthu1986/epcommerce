/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.giftcertificatesummary.parameters;

import java.util.Date;

/**
 * A parameters container.
 *
 */
public class GiftCertificateSummaryParameters {

	private long[] storeUidPkList;
	
	private Date startDate;
	
	private Date endDate;

	private String[] currencies;

	/**
	 * @return store Uid Pk
	 */
	public long[] getStoreUidPkList() {
		return storeUidPkList;			// NOPMD
	}
	/**
	 * @param storeUidPk store Uid Pk
	 */
	public void setStoreUidPkList(final long[] storeUidPk) {  //NOPMD
		this.storeUidPkList = storeUidPk;
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
