/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.beanframework;

/**
 * Value class used by the {@link SwitchableProxyInvocationHandler} to bind and unbind
 * an optional overriding proxy implementation for a proxied service.
 *
 * @param <T> the interface type being proxied
 */
public class SwitchableProxyBinder<T> {
	private T boundImplementation;
	private T fallbackImplementation;

	/**
	 * Sets the fallback (default) implementation for this proxy.  This setter should be called
	 * before the proxy is invoked.
	 *
	 * @param fallbackImplementation the fallback implementation
	 */
	public void setFallbackImplementation(final T fallbackImplementation) {
		this.fallbackImplementation = fallbackImplementation;
	}

	/**
	 * Binds the given object as the new implementation for the proxied service.
	 * @param proxyImplementation an implementation class
	 * @return the proxyImplementation
	 */
	public Object bindImplementation(final T proxyImplementation) {
		this.boundImplementation = proxyImplementation;

		return proxyImplementation;
	}

	/**
	 * Unbinds the given object as the new implementation for the proxied service.  The SwitchableProxy
	 * will now use its fallback implementation instead.
	 *
	 * @param proxyImplementation an implementation class
	 * @return the proxyImplementation
	 */
	public Object unbindImplementation(final T proxyImplementation) {
		this.boundImplementation = null;

		return proxyImplementation;
	}

	/**
	 * If a bound (override) implementation is available, then the bound implementation is returned.  Otherwise,
	 * returns the fallback implementation.
	 *
	 * @return an implementation
	 */
	public T getProxy() {
		if (boundImplementation != null) {
			return boundImplementation;
		}

		return fallbackImplementation;
	}
}
