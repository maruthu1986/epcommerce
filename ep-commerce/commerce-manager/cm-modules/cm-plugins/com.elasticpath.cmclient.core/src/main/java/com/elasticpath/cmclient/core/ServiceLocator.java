/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import com.elasticpath.commons.beanframework.BeanFactory;

/**
 *Thread-safe class for getting services from the Spring context.
 */
public final class ServiceLocator {

	private static BeanFactory beanFactory;

	private ServiceLocator() {
		//private constructor
	}


	/**
	 * Set Spring bean factory.
	 *
	 * @param springBeanFactory Spring bean factory.
	 */
	public static void setBeanFactory(final BeanFactory springBeanFactory) {
		beanFactory = springBeanFactory;
	}

	/**
	 * Get a service for given service name.
	 * @param serviceName the service name
	 * @param <T> the type
	 * @return Spring bean
	 */
	public static <T> T getService(final String serviceName) {
		return (T) beanFactory.getBean(serviceName);
	}
}
