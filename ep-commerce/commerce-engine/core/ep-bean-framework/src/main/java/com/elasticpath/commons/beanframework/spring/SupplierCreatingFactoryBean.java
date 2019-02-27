/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.commons.beanframework.spring;

import java.util.function.Supplier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;

/**
 * This is the {@link Supplier} equivalent of Spring's {@link org.springframework.beans.factory.config.ProviderCreatingFactoryBean}
 * class which allows code which takes {@link Supplier} arguments to be dependency injected via a Spring prototype.
 * <p>
 * The reason this is useful, rather than just having the bean under DI injected with a {@link javax.inject.Provider} rather than a {@link Supplier}
 * is that {@link javax.inject.Provider} is designed for DI and so having the bean declare a field with that type implicitly implies that this bean
 * will be created by a DI framework, whereas using a {@link Supplier} the class is deliberately ignoring whether it is being instantiated
 * manually or via a DI framework, it just wants a standard {@link Supplier} object passed in and if it is supplied via a DI framework,
 * it is irrelevant.
 */
public class SupplierCreatingFactoryBean extends AbstractFactoryBean<Supplier<Object>> {

	private String targetBeanName;

	/**
	 * Set the name of the target bean.
	 * <p>The target does not <i>have</i> to be a non-singleton bean, but realistically
	 * always will be (because if the target bean were a singleton, then said singleton
	 * bean could simply be injected straight into the dependent object, thus obviating
	 * the need for the extra level of indirection afforded by this factory approach).
	 *
	 * @param targetBeanName name of target bean
	 */
	public void setTargetBeanName(final String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(this.targetBeanName, "Property 'targetBeanName' is required");
		super.afterPropertiesSet();
	}


	@Override
	public Class<?> getObjectType() {
		return Supplier.class;
	}

	@Override
	protected Supplier<Object> createInstance() {
		return new TargetBeanSupplier(getBeanFactory(), this.targetBeanName);
	}


	/**
	 * Independent inner class - for serialization purposes.
	 */
	protected static final class TargetBeanSupplier implements Supplier<Object> {

		private final BeanFactory beanFactory;

		private final String targetBeanName;

		/**
		 * Constructor.
		 *
		 * @param beanFactory    bean factory
		 * @param targetBeanName target bean name
		 */
		protected TargetBeanSupplier(final BeanFactory beanFactory, final String targetBeanName) {
			this.beanFactory = beanFactory;
			this.targetBeanName = targetBeanName;
		}

		@Override
		public Object get() throws BeansException {
			return this.beanFactory.getBean(this.targetBeanName);
		}
	}

}
