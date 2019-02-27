/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.beanframework;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SwitchableProxyBinderTest {

	private static final String IMPL_FOO = "foo", IMPL_BAR = "bar";

	private SwitchableProxyBinder<String> proxy;

	@Before
	public void setUp() {
		proxy = new SwitchableProxyBinder<>();
		proxy.setFallbackImplementation(IMPL_FOO);
	}

	@Test
	public void testUnboundState() {
		assertEquals("If nothing is bound to the proxy, then the proxy should return the default impl", IMPL_FOO, proxy.getProxy());
	}

	@Test
	public void testBindImplementation() throws Exception {
		proxy.bindImplementation(IMPL_BAR);
		assertEquals("If an alternative impl is bound, then the proxy should return that bound impl", IMPL_BAR, proxy.getProxy());
	}

	@Test
	public void testUnbindImplementation() throws Exception {
		proxy.bindImplementation(IMPL_BAR);
		proxy.unbindImplementation(IMPL_BAR);
		assertEquals("If an alternative impl is unbound, then the proxy should return the default again", IMPL_FOO, proxy.getProxy());
	}
}
