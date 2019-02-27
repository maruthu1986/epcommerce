/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import java.io.InputStream;

import com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException;

/**
 * Utility class to resolve classpath resources related to database types. It employs a fallback strategy based on the naming of
 * the database type where it strips off the suffix starting from the last '-' symbol until it finds a matching classpath resource.
 * If the resource is not found, and there are no more dashes left, it will throw a
 * {@link com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException}.
 * For example, if the database type is "mysql-dev-one" and the format is "abc/%s.txt", it will try to find a resource with the name
 * "abc/mysql-dev-one.txt", then "abc/mysql-dev.txt", and finally just "abc/mysql.txt".
 */
public class ClasspathResourceResolverUtil {

	/**
	 * Gets the input stream of the sql file either as a .sql or .sproc extension when given a database type and the matching format.
	 * Decides if the sql resource is a stored procedure or not.
	 * This part is necessary because on stored procedures, the entire sql file must be fully sent through the SqlRunner versus line-by-line.
	 *
	 * @param dbType the database type
	 * @param format the matching format
	 * @return inputFile the unfiltered sql resource as a stream
	 */
	public SqlInputStream getSqlInputStreamWithFallback(final String dbType, final String format) {
		SqlInputStream inputFile = getSqlInputStream(dbType, format);
		int dashIndex = dbType.lastIndexOf('-');
		if (inputFile == null) {
			if (dashIndex == -1) { // The base case has no more dashes
				throw new DataPopulationActionException("Cannot find a classpath resource file with the root: " + dbType);
			}
			inputFile = getSqlInputStreamWithFallback(dbType.substring(0, dashIndex), format);
		}
		return inputFile;
	}

	private SqlInputStream getSqlInputStream(final String dbType, final String format) {
		InputStream sqlFile = getFileResourceStream(String.format(format + ".sql", dbType));
		InputStream sprocFile = getFileResourceStream(String.format(format + ".sproc", dbType));

		if (isMultipleSqlInputStream(sqlFile, sprocFile)) {
			throw new DataPopulationActionException("Multiple resources found for the same database type " + dbType);
		} else if (isFileExists(sprocFile)) {
			return new SqlInputStream(sprocFile, true);
		} else if (isFileExists(sqlFile)) {
			return new SqlInputStream(sqlFile, false);
		} else {
			return null;
		}
	}

	private boolean isFileExists(final InputStream file) {
		return file != null;
	}

	private boolean isMultipleSqlInputStream(final InputStream sqlFile, final InputStream sprocFile) {
		return isFileExists(sqlFile) && isFileExists(sprocFile);
	}

	/**
	 * Find a classpath resource with the given database type and format. Database type will be delimited by dashes and the
	 * suffix is stripped off recursively until a match is found or there is nothing more to strip off. If no matches are found,
	 * it will throw a {@link DataPopulationActionException}.
	 *
	 * @param dbType the database type
	 * @param format the sub directory and the extension of the classpath resource
	 * @return an input stream to the resource if found
	 */
	public InputStream getFileResourceStreamWithFallback(final String dbType, final String format) {
		InputStream inputFile = getFileResourceStream(String.format(format, dbType));
		int dashIndex = dbType.lastIndexOf('-');
		if (inputFile == null) {
			if (dashIndex == -1) { // The base case has no more dashes
				throw new DataPopulationActionException("Cannot find a classpath resource file with the root: " + dbType);
			}
			inputFile = getFileResourceStreamWithFallback(dbType.substring(0, dashIndex), format);
		}
		return inputFile;
	}

	/**
	 * Finds the file resource relative to this classpath. If the file doesn't exist, then it will return null.
	 * Did not employ the fallback strategy.
	 *
	 * @param path the sub-path relative to this classpath
	 * @return the input stream to the classpath resource
	 */
	public InputStream getFileResourceStream(final String path) {
		return this.getClass().getClassLoader().getResourceAsStream(path);
	}
}
