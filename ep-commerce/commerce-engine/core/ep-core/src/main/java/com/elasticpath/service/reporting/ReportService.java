/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.reporting;

import java.util.Collection;
import java.util.List;


/**
 * Defines the interface that server-side reporting services must implement in
 * order to make use of the framework.
 *
 */
public interface ReportService {

	/**
	 * Executes a given prepared statement against the database.
	 *
	 * @param statement the prepared statement
	 * @param parameters the parameters for the prepared statement
	 * contains the prepared statement and its parameters
	 *
	 * @return a List of rows of data
	 */
	List<Object[]> execute(String statement, Object[] parameters);

	/**
	 * Executes a given prepared statement against the database. The prepared statement
	 * contains a list parameter, and the parameters contains a List.
	 *
	 * @param statement the prepared statement
	 * @param values the collection of values
	 * @param listParameterName the name of the parameter for the list values
	 * @param parameters the parameters for the prepared statement
	 * contains the prepared statement and its parameters
	 *
	 * @return a List of rows of data
	 */
	List<Object[]> executeWithList(String statement, String listParameterName,
			Collection<String> values, Object[] parameters);

	/**
	 * Executes a given prepared statement against the database, use when
	 * want to return a whole object instead of rows of data.
	 *
	 * @param statement the prepared statement
	 * @param parameters the parameters for the prepared statement
	 * contains the prepared statement and its parameters
	 *
	 * @return a List of object
	 */
	List<Object> excuteGetObject(String statement, Object[] parameters);

	/**
	 * Executes a given prepared statement against the database, use when
	 * want to return a whole object instead of rows of data and the parameters
	 * contains a List.
	 *
	 * @param statement the prepared statement
	 * @param values the collection of values
	 * @param listParameterName the name of the parameter for the list values
	 * @param parameters the parameters for the prepared statement
	 * contains the prepared statement and its parameters
	 *
	 * @return a List of object
	 */
	List<Object> excuteGetObject(String statement, String listParameterName,
			Collection<String> values, Object[] parameters);

}