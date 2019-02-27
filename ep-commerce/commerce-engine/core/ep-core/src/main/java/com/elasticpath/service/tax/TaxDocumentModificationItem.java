/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.tax;

import java.io.Serializable;

import com.elasticpath.domain.customer.Address;

/**
 * 
 * Container class for data required by one tax document modification.
 *
 */
public class TaxDocumentModificationItem implements Serializable {
	
	/**
	 * Serial version id. 
	 */
	private static final long serialVersionUID = 500000000001L;
	
	private String taxDocumentReferenceId;
	private TaxDocumentModificationType modificationType;
	private String previousTaxDocumentId;
	private Address previousAddress;
	
	/**
	 * @return the taxDocumentReferenceId
	 */
	public String getTaxDocumentReferenceId() {
		return taxDocumentReferenceId;
	}
	
	/**
	 * @param taxDocumentReferenceId the taxDocumentReferenceId to set
	 */
	public void setTaxDocumentReferenceId(final String taxDocumentReferenceId) {
		this.taxDocumentReferenceId = taxDocumentReferenceId;
	}

	/**
	 * @return the modificationType
	 */
	public TaxDocumentModificationType getModificationType() {
		return modificationType;
	}
	
	/**
	 * @param modificationType the modificationType to set
	 */
	public void setModificationType(final TaxDocumentModificationType modificationType) {
		this.modificationType = modificationType;
	}
	/**
	 * @return the previousTaxDocumentId
	 */
	public String getPreviousTaxDocumentId() {
		return previousTaxDocumentId;
	}
	
	/**
	 * @param previousTaxDocumentId the previousTaxDocumentId to set
	 */
	public void setPreviousTaxDocumentId(final String previousTaxDocumentId) {
		this.previousTaxDocumentId = previousTaxDocumentId;
	}
	
	/**
	 * @return the previousAddress
	 */
	public Address getPreviousAddress() {
		return previousAddress;
	}
	
	/**
	 * @param previousAddress the previousAddress to set
	 */
	public void setPreviousAddress(final Address previousAddress) {
		this.previousAddress = previousAddress;
	}
}
