/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tools.sync.beanfactory;


/**
 * Bean Factory which is capable of accessing two environments. Each of the two environments may be
 * local or remote. Appropriate getXXXBean(...) (e.g. getSourceBean(beanName)) method may be used to obtain a bean from either source
 * or destination environment.
 */
public interface SyncBeanFactory {

	/**
	 * Gets a bean from the source system context.
	 *
	 * @param beanName bean name
	 * @param <T> the class of the requested bean
	 * @return the bean
	 */
	<T> T getSourceBean(String beanName);

	/**
	 * Gets a bean from the target system context.
	 *
	 * @param beanName bean name
	 * @param <T> the class of the requested bean
	 * @return bean
	 */
	<T> T getTargetBean(String beanName);

}