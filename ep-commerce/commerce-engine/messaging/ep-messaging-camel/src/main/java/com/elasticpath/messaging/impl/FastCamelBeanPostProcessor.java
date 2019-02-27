/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.messaging.impl;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Service;
import org.apache.camel.core.xml.CamelJMXAgentDefinition;
import org.apache.camel.impl.CamelPostProcessorHelper;
import org.apache.camel.impl.DefaultCamelBeanPostProcessor;
import org.apache.camel.spring.GenericBeansException;
import org.apache.camel.util.ServiceHelper;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * FastCamelBeanPostProcessor.
 */
@XmlRootElement(name = "beanPostProcessor")
@XmlAccessorType(XmlAccessType.FIELD)
public class FastCamelBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
	private static final transient Logger LOG = LoggerFactory.getLogger(FastCamelBeanPostProcessor.class);
	@XmlTransient
	private final Set<String> prototypeBeans = new LinkedHashSet<>();
	@XmlTransient
	private CamelContext camelContext;
	@XmlTransient
	private ApplicationContext applicationContext;
	@XmlTransient
	private String camelId;
	@XmlTransient
	private Set<Class<?>> annotatedClassList;

	// must use a delegate, as we cannot extend DefaultCamelBeanPostProcessor, as this will cause the
	// XSD schema generator to include the DefaultCamelBeanPostProcessor as a type, which we do not want to
	@XmlTransient
	private final DefaultCamelBeanPostProcessor delegate = new DefaultCamelBeanPostProcessor() {
		@Override
		public CamelContext getOrLookupCamelContext() {
			if (camelContext == null) {
				if (camelId == null) {
					// lookup by type and grab the single CamelContext if exists. Grabs the first CamelContext if multiple found.
					LOG.trace("Looking up CamelContext by type from Spring ApplicationContext: {}", applicationContext);
					final Map<String, CamelContext> contexts = applicationContext.getBeansOfType(CamelContext.class);
					if (MapUtils.isNotEmpty(contexts)) {
						camelContext = contexts.values().iterator().next();
					}
				} else {
					LOG.trace("Looking up CamelContext by id: {} from Spring ApplicationContext: {}", camelId, applicationContext);
					camelContext = applicationContext.getBean(camelId, CamelContext.class);
				}
			}
			return camelContext;
		}

		@Override
		public boolean canPostProcessBean(final Object bean, final String beanName) {
			// the JMXAgent is a bit strange and causes Spring issues if we let it being
			// post processed by this one. It does not need it anyway so we are good to go.
			// We should also avoid to process the null object bean (in Spring 2.5.x)
			if (bean == null || bean instanceof CamelJMXAgentDefinition || !getAnnotatedClassList().contains(bean.getClass())) {
				return false;
			}

			return super.canPostProcessBean(bean, beanName);
		}

		@Override
		public CamelPostProcessorHelper getPostProcessorHelper() {
			// lets lazily create the post processor
			if (camelPostProcessorHelper == null) {
				camelPostProcessorHelper = new CamelPostProcessorHelper() {

					@Override
					public CamelContext getCamelContext() {
						// lets lazily lookup the camel context here
						// as doing this will cause this context to be started immediately
						// breaking the lifecycle ordering of different camel contexts
						// so we only want to do this on demand
						return delegate.getOrLookupCamelContext();
					}

					@Override
					protected RuntimeException createProxyInstantiationRuntimeException(final Class<?> type, final Endpoint endpoint,
																						final Exception exception) {
						return new BeanInstantiationException(type, "Could not instantiate proxy of type " + type.getName()
								+ " on endpoint " + endpoint, exception);
					}

					@Override
					protected boolean isSingleton(final Object bean, final String beanName) {
						// no application context has been injected which means the bean
						// has not been enlisted in Spring application context
						if (applicationContext == null || beanName == null) {
							return super.isSingleton(bean, beanName);
						} else {
							return applicationContext.isSingleton(beanName);
						}
					}

					@Override
					protected void startService(final Service service, final CamelContext camelContext, final Object bean, final String beanName)
							throws Exception {
						if (isSingleton(bean, beanName)) {
							camelContext.addService(service);
						} else {
							// only start service and do not add it to CamelContext
							ServiceHelper.startService(service);
							if (prototypeBeans.add(beanName)) {
								// do not spam the log with WARN so do this only once per bean name
								FastCamelBeanPostProcessor.LOG.warn("The bean with id [{}] is prototype scoped and cannot stop the injected service"
									+ " when bean is destroyed: {}. You may want to stop the service manually from the bean.", beanName, service);
							}
						}
					}
				};
			}
			return camelPostProcessorHelper;
		}

	};

	/**
	 * Apply this post processor to the given new bean instance <i>before</i> any bean
	 * initialization callbacks (like <code>afterPropertiesSet</code>
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 *
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one; if
	 * <code>null</code>, no subsequent BeanPostProcessors will be invoked
	 * @throws BeansException is thrown if error post processing bean
	 */
	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		try {
			return delegate.postProcessBeforeInitialization(bean, beanName);
		} catch (final Exception e) {
			// do not wrap already beans exceptions
			if (e instanceof BeansException) { // NOPMD - to maintain Spring source compatibility
				throw (BeansException) e;
			}
			throw new GenericBeansException("Error post processing bean: " + beanName, e);
		}
	}

	/**
	 * Apply this post processor to the given new bean instance <i>after</i> any bean
	 * initialization callbacks (like <code>afterPropertiesSet</code>
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 *
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one; if
	 * <code>null</code>, no subsequent BeanPostProcessors will be invoked
	 * @throws BeansException is thrown if error post processing bean
	 */
	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		try {
			return delegate.postProcessAfterInitialization(bean, beanName);
		} catch (final Exception e) {
			// do not wrap already beans exceptions
			if (e instanceof BeansException) { // NOPMD - to maintain Spring source compatibility
				throw (BeansException) e;
			}
			throw new GenericBeansException("Error post processing bean: " + beanName, e);
		}
	}

	// Properties
	// -------------------------------------------------------------------------

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public CamelContext getCamelContext() {
		return camelContext;
	}

	public void setCamelContext(final CamelContext camelContext) {
		this.camelContext = camelContext;
	}

	public String getCamelId() {
		return camelId;
	}

	public void setCamelId(final String camelId) {
		this.camelId = camelId;
	}
	public Set<Class<?>> getAnnotatedClassList() {
		return annotatedClassList;
	}

	public void setAnnotatedClassList(final Set<Class<?>> annotatedClassList) {
		this.annotatedClassList = annotatedClassList;
	}
}
