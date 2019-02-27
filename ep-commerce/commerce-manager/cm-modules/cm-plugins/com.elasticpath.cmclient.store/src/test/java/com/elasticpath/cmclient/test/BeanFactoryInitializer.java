/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.impl.ElasticPathImpl;

/**
 * Initializes the bean factory by registering it with ElasticPathImpl for cases
 * where domain object get beans using the ElasticPath interface.
 * 
 */
public class BeanFactoryInitializer {
    
    /**
     * Create a new ExpectationsFactory.
     * @param beanFactory the bean factory
     */
    @SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
    public BeanFactoryInitializer(final BeanFactory beanFactory) {
        ElasticPathImpl elasticPath = (ElasticPathImpl) ElasticPathImpl.getInstance();
        elasticPath.setBeanFactory(beanFactory);
    }

    /**
     * Clean up after factory.
     */
    @SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
    public void close() {
        ElasticPathImpl elasticPath = (ElasticPathImpl) ElasticPathImpl.getInstance();
        elasticPath.setBeanFactory(null);
    }

}
