/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.common;

import java.util.Collection;
import java.util.List;

/**
 * Encapsulates a prepared statement: 
 * the query string, the parameters, the collection, 
 * and the string corresponding to the collection name in the prepared statement.
 */
public class PreparedStatement {

	private String queryString;
	
	private List<Object> parameters;
	
	private Collection<String> collection;
	
	private String collectionParameterName;

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString the queryString to set
	 * @param collectionParameterName the string representing the collection to be inserted as
	 * a parameter to the query. This value can be null if there is no collection in the given query.
	 */
	public void setQueryString(final String queryString, final String collectionParameterName) {
		this.queryString = queryString;
		this.collectionParameterName = collectionParameterName;
	}

	/**
	 * @return the parameters
	 */
	public List<Object> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(final List<Object> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the collection
	 */
	public Collection<String> getCollection() {
		return collection;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(final Collection<String> collection) {
		this.collection = collection;
	}

	/**
	 * @return the collectionParameterName
	 */
	public String getCollectionParameterName() {
		return collectionParameterName;
	}
}
