/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.commons.beanframework.config;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.config.ListFactoryBean;

/**
 * Factory class to build extensible lists.
 */
public class ExtensibleListFactoryBean extends ListFactoryBean {

	private List<?> removeList = Collections.emptyList();

	/**
	 * Setter for list of objects to be removed.
	 *
	 * @param removeList list of objects to be removed
	 */
	public void setRemoveList(final List<?> removeList) {
		Objects.requireNonNull(removeList, "List of elements for removal cannot be null");
		this.removeList = removeList;
	}

	/**
	 * Method to create a list.
	 *
	 * @return array list with specified values, without the objects in the remove list
	 */
	@Override
	protected List<Object> createInstance() {
		@SuppressWarnings("unchecked")
		List<Object> sourceList = super.createInstance();
		sourceList.removeAll(removeList);
		return sourceList;
	}

}
