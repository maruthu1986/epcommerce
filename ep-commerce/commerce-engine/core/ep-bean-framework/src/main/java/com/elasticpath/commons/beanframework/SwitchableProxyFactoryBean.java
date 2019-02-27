/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.beanframework;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;

/**
 * An implementation of {@link org.springframework.beans.factory.FactoryBean} that delegates
 * to two separate implementations.  It is used in the scenario where the primary implementation
 * may or may not be present in the spring context.  One situation in which this capability is useful
 * is when the primary implementation is an optional service reference retrieved via an OSGI blueprint.
 *
 * In that case, if the primary implementation is available through OSGI, then the proxy will delegate
 * to the primary.  If the primary implementation is not available, then the proxy will delegate to the
 * secondary instead.
 *
 * @param <T> the generic type of the object being created
 */
public class SwitchableProxyFactoryBean<T> implements FactoryBean<T> {

	private Class<T> proxyInterface;
	private SwitchableProxyBinder<T> proxy;

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public T getObject() throws Exception {
		// to get the classloader from target object type.
		return (T) Proxy.newProxyInstance(getObjectType().getClassLoader(),
				new Class[] {getObjectType()},
				new SwitchableProxyInvocationHandler(proxy));
	}

	@Override
	public Class<?> getObjectType() {
		return proxyInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setProxyInterface(final Class<T> proxyInterface) {
		this.proxyInterface = proxyInterface;
	}

	public void setProxy(final SwitchableProxyBinder<T> proxy) {
		this.proxy = proxy;
	}
}
