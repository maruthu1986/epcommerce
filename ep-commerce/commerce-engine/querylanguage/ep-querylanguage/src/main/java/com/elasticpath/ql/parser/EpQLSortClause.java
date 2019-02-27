/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.ql.parser;

/**
 * A field that the parser should sort by.
 */
public class EpQLSortClause {

	private final String nativeFieldName;

	private final EpQLSortOrder sortOrder;

	/**
	 * Constructor for creating an EpQlSortField.
	 * @param nativeFieldName the native field name
	 * @param sortOrder the sort order
	 */
	public EpQLSortClause(final String nativeFieldName, final EpQLSortOrder sortOrder) {
		this.nativeFieldName = nativeFieldName;
		this.sortOrder = sortOrder;
	}

	/**
	 * Gets the native field name.
	 * @return native field name
	 */
	public String getNativeFieldName() {
		return nativeFieldName;
	}

	/**
	 * Gets the sort order.
	 * @return the sort order enum
	 */
	public EpQLSortOrder getSortOrder() {
		return sortOrder;
	}
}
