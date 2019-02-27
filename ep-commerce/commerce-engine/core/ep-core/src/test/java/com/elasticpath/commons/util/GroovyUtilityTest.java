/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.FutureTask;

import groovy.lang.Script;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.commons.listeners.GroovyEhcacheEventListener;


/**
 * Test for {@link com.elasticpath.commons.util.GroovyUtility#removeExpiredCompiledScriptFromCache} method.
 */
public class GroovyUtilityTest {

	private static final String GROOVY_SCRIPT = "a = 1";

	private final CacheManager cacheManager = CacheManager.getInstance();
	private final Cache ehCache = new Cache("simple_timeout", 2, false, false, 1L, 1L);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private final SimpleTimeoutCache<String, FutureTask<Script>> groovyScriptTimeoutCache = new SimpleTimeoutCache<>();

	@SuppressWarnings("unchecked")
	private final FutureTask<Script> mockedCompiler = context.mock(FutureTask.class);
	private final Script mockedScript = context.mock(Script.class);

	@Before
	public void before() throws Exception {
		ehCache.setCacheManager(cacheManager);
		ehCache.initialise();

		groovyScriptTimeoutCache.setCache(ehCache);
		groovyScriptTimeoutCache.setCacheEventListener(new GroovyEhcacheEventListener());

		context.checking(new Expectations() { {
			oneOf(mockedCompiler).get(); will(returnValue(mockedScript));
		} });
	}

	@After
	public void after() {
		ehCache.dispose();
	}

	@Test
	public void shouldRemoveCompiledScriptClassWhenScriptIsNotNull() {
		final long sleepTimeMillis = 1010L;

		groovyScriptTimeoutCache.put(GROOVY_SCRIPT, mockedCompiler);

		assertEquals(mockedCompiler, groovyScriptTimeoutCache.get(GROOVY_SCRIPT));

		try {
			Thread.sleep(sleepTimeMillis);
		} catch (Exception e) {
		}

		assertNull(groovyScriptTimeoutCache.get(GROOVY_SCRIPT));
	}
}
