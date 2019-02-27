/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.contentspace.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.concurrent.FutureTask;

import groovy.lang.Script;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.service.contentspace.impl.AbstractGroovyScriptEngineImpl.RunnableScript;
import com.elasticpath.service.contentspace.impl.FutureTaskGroovyScriptEngineImpl.SynchronizedRunnableScript;
import com.elasticpath.test.MapBasedSimpleTimeoutCache;

/**
 * Test for the caching in the FutureTaskGroovyScriptEngineImpl.
 */
public class FutureTaskGroovyScriptEngineImplTest {

	private static final String GROOVY_SCRIPT = "a = 1";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@SuppressWarnings("unchecked")
	private final SimpleTimeoutCache<String, FutureTask<Script>> mockSimpleTimeoutCache = context.mock(SimpleTimeoutCache.class);
	private final FutureTaskGroovyScriptEngineImpl scriptEngine = new FutureTaskGroovyScriptEngineImpl();

	@Before
	public void setUp() {
		scriptEngine.setGroovyScriptTimeoutCache(mockSimpleTimeoutCache);
	}


	/** Test that the first call causes compilation. */
	@SuppressWarnings("unchecked")
	@Test
	public void testFirstCallCausesCompilation() {
		context.checking(new Expectations() { {
			oneOf(mockSimpleTimeoutCache).get(GROOVY_SCRIPT);	will(returnValue(null));
			oneOf(mockSimpleTimeoutCache).put(with(GROOVY_SCRIPT), with(any(FutureTask.class)));

		} });
		
		RunnableScript script = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
		assertNotNull("A null script means it didn't successfully compile", script);
	}
	
	/** Test that consecutive calls use the cached script. */
	@Test
	public void testSecondCallUsesCachedScript() {

		final SimpleTimeoutCache<String, FutureTask<Script>> mapBasedSimpleTimeoutCache =
			new MapBasedSimpleTimeoutCache<>();

		scriptEngine.setGroovyScriptTimeoutCache(mapBasedSimpleTimeoutCache);

		RunnableScript script1 = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
		Script firstScript = ((SynchronizedRunnableScript) script1).getScriptForTestingOnly();
		
		RunnableScript script2 = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
		Script secondScript = ((SynchronizedRunnableScript) script2).getScriptForTestingOnly();

		assertNotSame("We should get different script wrapper.", script1, script2);
		assertSame("The same underlying script should be returned from the cache.", firstScript, secondScript);
	}

	/** Test multi-threaded calls use the cached script (only one is used). */
	@Test
	public void testSubsequentMultiThreadedCallUsesCachedScript() {

		final SimpleTimeoutCache<String, FutureTask<Script>> mapBasedSimpleTimeoutCache =
			new MapBasedSimpleTimeoutCache<>();
		scriptEngine.setGroovyScriptTimeoutCache(mapBasedSimpleTimeoutCache);

		final RunnableScript script1 = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
		final Script firstScript = ((SynchronizedRunnableScript) script1).getScriptForTestingOnly();
		
		Runnable runnable = () -> {
			RunnableScript script2 = scriptEngine.getCompiledScript(GROOVY_SCRIPT);
			Script secondScript = ((SynchronizedRunnableScript) script2).getScriptForTestingOnly();
			assertNotSame("We should get different script wrapper.", script1, script2);
			assertSame("The same underlying script should be returned from the cache.", firstScript, secondScript);
		};
		
		final int max = 20;
		threadAndWait(runnable, max);
	}

	
	private void threadAndWait(final Runnable runnable, final int max) {
		Thread [] threads = new Thread[max];
		for (int x = 0; x < max; x++) {
			threads[x] = new Thread(runnable);
			threads[x].start();
		}
		for (int x = 0; x < max; x++) {
			try {
				threads[x].join();
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	
}
