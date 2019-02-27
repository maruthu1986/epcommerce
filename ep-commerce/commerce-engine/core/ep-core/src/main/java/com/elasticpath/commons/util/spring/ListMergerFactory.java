/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.commons.util.spring;

import java.util.List;

import org.springframework.beans.factory.config.ListFactoryBean;

/** Spring utility class for merging two list beans. */
public class ListMergerFactory extends ListFactoryBean {

	private List<Object> mergeList;

	public void setMergeList(final List<Object> mergeList) {
		this.mergeList = mergeList;
	}

	/** Concatenate <code>mergeList</code> onto the end of <code>sourceList</code>.
	 * @return Merged list
	 */
	@Override
	protected List<Object> createInstance() {
		@SuppressWarnings("unchecked")
		List<Object> newList = super.createInstance();
		newList.addAll(mergeList);
		return newList;
	}
}
