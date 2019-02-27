/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.plugin.tax.domain.TaxExemption;

/**
 * Represents an entitlement that exempts an Order from sales taxes.
 */
public class TaxExemptionImpl implements TaxExemption, Serializable {

	private static final long serialVersionUID = 50000000001L;

	private String exemptionId;
	private final Map<String, String> data = new HashMap<>();

	@Override
	public String getExemptionId() {
		return exemptionId;
	}

	@Override
	public void setExemptionId(final String exemptionId) {
		this.exemptionId = exemptionId;
	}

	@Override
	public String toString() {
		return getExemptionId();
	}

	@Override
	public void addData(final String key, final String value) {
		getDataMap().put(key, value);
	}

	@Override
	public String getData(final String key) {
		return getDataMap().get(key);
	}

	@Override
	public Map<String, String> getAllData() {
		return getDataMap();
	}

	private Map<String, String> getDataMap() {
		return data;
	}
}