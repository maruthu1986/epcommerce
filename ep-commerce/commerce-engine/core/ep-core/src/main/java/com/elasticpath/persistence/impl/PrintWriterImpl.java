/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.persistence.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.elasticpath.persistence.PrintWriter;
import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * This is a default implementation of <code>PrintWriter</code>. It's just a wrapper of <code>java.io.PrintWriter</code>.
 */
public class PrintWriterImpl implements PrintWriter {

	private java.io.PrintWriter printWriter;

	/**
	 * Open a file to write.
	 * 
	 * @param fileName the file name
	 * @throws EpPersistenceException if any error happens
	 */
	@Override
	public void open(final String fileName) throws EpPersistenceException {
		try {
			this.printWriter = new java.io.PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
		} catch (final IOException e) {
			throw new EpPersistenceException("Cannot open file.", e);
		}
	}

	/**
	 * Writes the given string as a line.
	 * 
	 * @param string the string to write
	 * @throws EpPersistenceException in case any error happens
	 */
	@Override
	public void println(final String string) throws EpPersistenceException {
		this.printWriter.println(string);
	}

	/**
	 * Close the file.
	 * 
	 * @throws EpPersistenceException in case of any IO error happens
	 */
	@Override
	public void close() throws EpPersistenceException {
		this.printWriter.close();
	}
}
