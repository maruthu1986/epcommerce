/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.tax;

/**
 * Tax document modification type enumeration.
 */
public enum TaxDocumentModificationType {

	/**
	 * New.
	 */
	NEW("new"),
	
	/**
	 * Update.
	 */
	UPDATE("update"), 
	
	/**
	 * Cancel. 
	 */
	CANCEL("cancel");
	
	private final String name;
	
	/**
	 * Constructor.
	 * 
	 * @param name the String representation of the enumeration element
	 */
	TaxDocumentModificationType(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
}