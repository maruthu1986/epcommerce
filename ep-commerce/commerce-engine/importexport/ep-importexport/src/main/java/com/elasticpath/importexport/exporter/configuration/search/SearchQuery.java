/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.exporter.configuration.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * An object representing a search query (and optional type).
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchQuery {

	/**
	 * An {@code EPQueryType}.
	 */
	@XmlAttribute(name = "for")
	private String queryFor;

	@XmlValue
	private String epql;

	/**
	 * Default constructor.
	 */
	public SearchQuery() {
		// default constructor
	}

	/**
	 * The constructor.
	 *
	 * @param type - the query type
	 * @param epql - the epql
	 */
	public SearchQuery(final String type, final String epql) {
		this.queryFor = type;
		this.epql = epql;
	}

	public String getType() {
		return queryFor;
	}

	public String getEPQL() {
		return epql;
	}

	@Override
	public String toString() {
		return "searchQuery(" + queryFor + "=" + epql + ")";
	}
}