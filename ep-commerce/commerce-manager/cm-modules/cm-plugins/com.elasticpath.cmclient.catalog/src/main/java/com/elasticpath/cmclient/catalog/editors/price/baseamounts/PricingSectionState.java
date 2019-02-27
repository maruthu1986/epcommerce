/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors.price.baseamounts;

/**
 * Class keeps any information that need to be restored
 * when pricing section is created.  
 */
public class PricingSectionState {
	private String selectedPL;

	
	/**
	 * Returns price list guid.
	 *
	 * @return price list guid.
	 */
	public String getSelectedPL() {
		return selectedPL;
	}
	/**
	 * Sets price list guid.
	 *
	 * @param selectedPL - price list guid.
	 */
	public void setSelectedPL(final String selectedPL) {
		this.selectedPL = selectedPL;
	}
	
	
}
