/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import java.io.InputStream;

/**
 * Container for an input stream to a sql file and the type of the sql (stored procedure or not).
 */
public class SqlInputStream {

	private InputStream stream;
	private boolean storedProcedure;

	/**
	 * Simple constructor for the container.
	 * Accepts the input stream and a boolean to signify if the sql is a stored procedure or not.
	 *
	 * @param stream          the input stream.
	 * @param storedProcedure stored procedure flag.
	 */
	public SqlInputStream(final InputStream stream, final boolean storedProcedure) {
		this.stream = stream;
		this.storedProcedure = storedProcedure;
	}

	public boolean isStoredProcedure() {
		return storedProcedure;
	}

	public void setStoredProcedure(final boolean isStoredProcedure) {
		this.storedProcedure = isStoredProcedure;
	}

	public InputStream getStream() {
		return stream;
	}

	public void setStream(final InputStream stream) {
		this.stream = stream;
	}
}
