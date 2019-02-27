/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.cmclient.reporting.customerpersonaldata.parameters;

import java.util.Date;

import com.elasticpath.cmclient.reporting.common.ReportParameters;

/**
 * A parameters container.
 *
 */
public class CustomerPersonalDataParameters implements ReportParameters {

	private long storeUidPk;
	private String store;
	private Date startDate;
	private Date endDate;
	private String userId;

	public String getStore() {
		return store;
	}

	public void setStore(final String store) {
		this.store = store;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public long getStoreUidPk() {
		return storeUidPk;
	}

	public void setStoreUidPk(final long storeUidPk) {
		this.storeUidPk = storeUidPk;
	}
}
