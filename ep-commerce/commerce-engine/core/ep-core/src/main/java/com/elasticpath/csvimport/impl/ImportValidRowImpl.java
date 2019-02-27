/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.csvimport.impl;

import com.elasticpath.csvimport.ImportValidRow;

/**
 * Represents a record of a valid csv import row.
 * @param <T> The type of DTO in the Valid Rows (e.g. BaseAmountDTO)
 */
public class ImportValidRowImpl<T> implements ImportValidRow<T> {
	
	private T dto;
	private String row;
	private int rowNumber;
	
	/**
	 * @return the dto
	 */
	@Override
	public T getDto() {
		return dto;
	}
	
	/**
	 * @param dto the dto to set
	 */
	@Override
	public void setDto(final T dto) {
		this.dto = dto;
	}
	
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
