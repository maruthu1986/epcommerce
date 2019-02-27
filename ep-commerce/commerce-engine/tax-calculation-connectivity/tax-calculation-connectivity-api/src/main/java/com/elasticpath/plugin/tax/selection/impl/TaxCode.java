/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.selection.impl;

import org.apache.commons.lang.StringUtils;

/**
 * The tax calculation tax code.
 */
public class TaxCode {

	private String code;
	
	public String getTaxCode() {
		return code;
	}

	public void setTaxCode(final String code) {
		this.code = code;
	}
	
	/**
	 * Determines whether this tax code matches a given tax code.
	 * 
	 * @param code the tax code to be matches
	 * @return true if the codes match
	 */
	public boolean matches(final String code) {
		return StringUtils.equalsIgnoreCase(code, getTaxCode());
	}
	
}
