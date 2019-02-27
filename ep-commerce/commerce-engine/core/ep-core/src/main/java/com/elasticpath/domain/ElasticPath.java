/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.domain;


/**
 * <code>ElasticPath</code> provides configuration and context information to a number of clients across the system.
 */
public interface ElasticPath {
	/**
	 * Get the bean with the given id from spring application context.
	 *
	 * @param <T> the type of the bean to return.
	 * @param name bean id
	 * @return instance of the bean with the given id.
	 */
	<T> T getBean(String name);

	/**
	 * Return the <code>Class</code> object currently registered with the
	 * specified <code>beanName</code>.
	 *
	 * @param <T> the type of the bean to return.
	 * @param beanName the name of the bean to get the class for.
	 * @return the class object if the bean is registered, null otherwise.
	 */
	<T> Class<T> getBeanImplClass(String beanName);

}
