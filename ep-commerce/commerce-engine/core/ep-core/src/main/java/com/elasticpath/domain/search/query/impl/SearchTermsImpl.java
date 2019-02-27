/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.search.query.impl;

import java.io.Serializable;
import java.util.Objects;

import com.elasticpath.domain.search.query.SearchTerms;

/**
 * Represents a keyword search operation request.
 */
public class SearchTermsImpl implements SearchTerms, Serializable {
	private static final long serialVersionUID = 1L;

	private String keywords;
	
	@Override
	public String getKeywords() {
		return keywords;
	}

	@Override
	public void setKeywords(final String keywords) {
		this.keywords = keywords;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof SearchTermsImpl) {
			return Objects.equals(keywords, ((SearchTermsImpl) obj).keywords);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(keywords);
	}

}
