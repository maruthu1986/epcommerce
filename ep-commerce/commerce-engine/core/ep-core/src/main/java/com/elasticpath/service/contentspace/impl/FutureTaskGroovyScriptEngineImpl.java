/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.contentspace.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.log4j.Logger;

import com.elasticpath.cache.SimpleTimeoutCache;

/**
 * This implementation of the GroovyScriptEngine uses a single GroovyShell instance and
 * is biased to perform better where the same script is run very frequently: all text 
 * expressions are pre-compiled and the resulting Script objects cached.
 */
public class FutureTaskGroovyScriptEngineImpl extends AbstractGroovyScriptEngineImpl {

	private static final Logger LOG = Logger.getLogger(FutureTaskGroovyScriptEngineImpl.class);

	private final GroovyShell groovyShell = new GroovyShell();

	//injected via Spring
	private SimpleTimeoutCache<String, FutureTask<Script>> groovyScriptTimeoutCache;

	/**
	 * Return the compiled script version of the incoming text script.
	 * @param textScript the script to get a compiled version of.
	 * @return the compiled script.
	 */
	@Override
	public RunnableScript getCompiledScript(final String textScript) {
		boolean newlyCreated = false;

		FutureTask<Script> compiler;
		synchronized (groovyScriptTimeoutCache) {
			compiler = groovyScriptTimeoutCache.get(textScript);
			if (compiler == null) {
				newlyCreated = true;
				compiler = new FutureTask<>(new Callable<Script>() {
					@Override
					public Script call() {
						return groovyShell.parse(textScript);
					}
				});
				groovyScriptTimeoutCache.put(textScript, compiler);
			}
		}

		if (newlyCreated) {
			compiler.run();
		}

		Script script = null;
		try {
			script = compiler.get();
		} catch (Exception e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to compile groovy script", e);
			}
		}
		if (script == null) {
			return null;
		}
		return new SynchronizedRunnableScript(script);
	}



	/**
	 * A script wrapper that makes setting the binding on a script and running the 
	 * script an atomic operation - by synchronizing on the script.
	 */
	static class SynchronizedRunnableScript implements RunnableScript {

		private final Script script;

		/**
		 * @param script the script to run.
		 */
		@SuppressWarnings("checkstyle:redundantmodifier")
		public SynchronizedRunnableScript(final Script script) {
			this.script = script;

		}

		/**
		 * Runs the script in the context of the binding as an atomic operation.
		 * @param binding the binding to run the script in the context of.
		 * @return the result of the script.
		 */
		@Override
		public Object run(final Binding binding) {
			synchronized (script) {
				script.setBinding(binding);
				return script.run();
			}
		}
		
		/** @return the currenct script object - for testing only. */
		Script getScriptForTestingOnly() {
			return script;
		}
	}

	public SimpleTimeoutCache<String, FutureTask<Script>> getGroovyScriptTimeoutCache() {
		return groovyScriptTimeoutCache;
	}

	public void setGroovyScriptTimeoutCache(final SimpleTimeoutCache<String, FutureTask<Script>> groovyScriptTimeoutCache) {
		this.groovyScriptTimeoutCache = groovyScriptTimeoutCache;
	}

	public GroovyShell getGroovyShell() {
		return groovyShell;
	}
}
