/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;
import java.util.Objects;

import com.elasticpath.plugin.tax.domain.TaxDocumentId;

/**
 * Simple {@link String} based tax document ID.
 */
public class StringTaxDocumentId implements TaxDocumentId, Serializable {

	private static final long serialVersionUID = 500000000001L;
	
	private String documentId;

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof StringTaxDocumentId)) {
			return false;
		}
		StringTaxDocumentId other = (StringTaxDocumentId) obj;
		return Objects.equals(getId(), other.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public String toString() {
		return getId();
	}
	
	public String getId() {
		return documentId;
	}

	public void setId(final String documentId) {
		this.documentId = documentId;
	}
	
	/**
	 * Creates a new tax document id from a string.
	 * 
	 * @param value the value
	 * @return the instance
	 */
	public static TaxDocumentId fromString(final String value) {
		if (value == null) {
			return null;
		}
		StringTaxDocumentId result = new StringTaxDocumentId();
		
		result.setId(value);
		
		return result;
	}
}
