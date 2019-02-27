/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.ElasticPath;

/**
 * This class provides elastic path configurations and context to others.
 */
public class ElasticPathImpl implements ElasticPath, BeanFactory {

	private static ElasticPath epInstance = new ElasticPathImpl();

	private BeanFactory beanFactory;

	/**
	 * Default constructor.
	 */
	protected ElasticPathImpl() {
		super();
	}

	/**
	 * Return the singleton <code>ElasticPath</code>. Create default EP if one hasn't been created already.
	 *
	 * @return the singleton <code>ElasticPath</code>
	 */
	public static ElasticPath getInstance() {
		return epInstance;
	}

	/**
	 * Lifecycle hook to be called when the containing context intends to shut down.  This ensures any static variables are reset so that the JVM
	 * does not contain stale data, which would be undesirable in the event of a new context being initialised.
	 */
	public void destroy() {
		epInstance = new ElasticPathImpl();
	}

	@Override
	public <T> T getBean(final String name) {
		return beanFactory.getBean(name);
	}

	@Override
	public <T> Class<T> getBeanImplClass(final String beanName) {
		return beanFactory.getBeanImplClass(beanName);
	}



	/**
	 * Sets the bean factory object.
	 *
	 * @param beanFactory the bean factory instance.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


}
