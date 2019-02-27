/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.commons.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import groovy.lang.Script;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Utility class for removing expired compiled Groovy scripts from cache.
 * The class is called from {@link com.elasticpath.commons.listeners.GroovyEhcacheEventListener#notifyElementExpired}
 * method.
 */
public final class GroovyUtility {

	private static final Logger LOG = Logger.getLogger(GroovyUtility.class);

	private GroovyUtility() { }

	/**
	 * Hook to unload classes from Groovy Shell after they have been expired.
	 *
	 * @param cachedElement Expired cached element that contains Groovy script/{@link java.util.concurrent.FutureTask} key-value pair
	 */
	public static void removeExpiredCompiledScriptFromCache(final Element cachedElement) {

		@SuppressWarnings("unchecked")
		final FutureTask<Script> compiler = (FutureTask<Script>) cachedElement.getObjectValue();

		Script compiledScript;

		try {
			compiledScript = compiler.get();
		} catch (InterruptedException iex) {
			Thread.currentThread().interrupt();
			return;
		} catch (ExecutionException eex) {
			return;
		}
		if (compiledScript != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Evicting class: " + compiledScript.getClass().getCanonicalName()
								+ " for script: \n" + cachedElement.getObjectKey());
			}
			InvokerHelper.removeClass(compiledScript.getClass());
		}
	}
}
