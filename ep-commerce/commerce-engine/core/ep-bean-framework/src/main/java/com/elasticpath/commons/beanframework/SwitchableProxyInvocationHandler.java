/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.beanframework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Dynamic Proxy InvocationHandler which allows a fallback (default) service implementation to be
 * overridden with another implementation at run time.
 *
 * This is quite useful for creating a single alias to multiple implementations of services (e.g. cached and non-cached
 * versions of the same service) in a way that is compatible with both OSGI and non-OSGI environments.
 *
 * @param <T> the interface being proxied
 */
public class SwitchableProxyInvocationHandler<T> implements InvocationHandler {
	private final SwitchableProxyBinder<T> switchableProxyBinder;

	/**
	 * Constructor.
	 * @param switchableProxyBinder the proxy binding
	 */
	public SwitchableProxyInvocationHandler(final SwitchableProxyBinder<T> switchableProxyBinder) {
		this.switchableProxyBinder = switchableProxyBinder;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		return method.invoke(switchableProxyBinder.getProxy(), args);
	}
}
