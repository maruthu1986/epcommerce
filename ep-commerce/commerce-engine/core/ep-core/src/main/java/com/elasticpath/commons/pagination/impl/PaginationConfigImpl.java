/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination.impl;

import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.PaginationConfig;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * The pagination configuration bean.
 */
public class PaginationConfigImpl implements PaginationConfig {

	private int pageSize;
	private DirectedSortingField[] sortingFields;
	private String objectId;
	private LoadTuner loadTuner;
	
	/**
	 *
	 * @return the objectId
	 */
	@Override
	public String getObjectId() {
		return objectId;
	}
	/**
	 *
	 * @param objectId the objectId to set
	 */
	@Override
	public void setObjectId(final String objectId) {
		this.objectId = objectId;
	}
	/**
	 *
	 * @return the pageSize
	 */
	@Override
	public int getPageSize() {
		return pageSize;
	}
	/**
	 *
	 * @param pageSize the pageSize to set
	 */
	@Override
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}
	
	/**
	 *
	 * @return the sortingFields
	 */
	@Override
	public DirectedSortingField[] getSortingFields() {
		return (DirectedSortingField[]) ArrayUtils.clone(sortingFields);
	}
	
	/**
	 *
	 * @param sortingFields the sortingFields to set
	 */
	@Override
	public void setSortingFields(final DirectedSortingField... sortingFields) {
		this.sortingFields = (DirectedSortingField[]) ArrayUtils.clone(sortingFields);
	}
	/**
	 *
	 * @return the load tuner 
	 */
	@Override
	public LoadTuner getLoadTuner() {
		return loadTuner;
	}
	/**
	 *
	 * @param loadTuner the load tuner to set
	 */
	@Override
	public void setLoadTuner(final LoadTuner loadTuner) {
		this.loadTuner = loadTuner;
	}
}
