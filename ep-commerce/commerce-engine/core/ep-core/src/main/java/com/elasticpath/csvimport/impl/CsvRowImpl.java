/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport.impl;

import com.elasticpath.csvimport.CsvRow;

/**
 * Default implementation of CsvRow.
 */
public class CsvRowImpl implements CsvRow {
	
	private String row;
	private int rowNumber;
	
	/**
	 * @return the row
	 */
	@Override
	public String getRow() {
		return row;
	}
	
	/**
	 * @param row the row to set
	 */
	@Override
	public void setRow(final String row) {
		this.row = row;
	}
	
	/**
	 * @return the rowNumber
	 */
	@Override
	public int getRowNumber() {
		return rowNumber;
	}
	
	/**
	 * @param rowNumber the rowNumber to set
	 */
	@Override
	public void setRowNumber(final int rowNumber) {
		this.rowNumber = rowNumber;
	}
}
