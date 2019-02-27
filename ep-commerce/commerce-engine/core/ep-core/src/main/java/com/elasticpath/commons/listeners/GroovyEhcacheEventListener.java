/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.commons.listeners;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import com.elasticpath.commons.util.GroovyUtility;

/**
 * Custom Ehcache listener used to remove expired compiled Groovy scripts from cache
 * by calling {@link GroovyUtility#removeExpiredCompiledScriptFromCache}.
 */
@SuppressWarnings("PMD.UncommentedEmptyMethodBody")
public class GroovyEhcacheEventListener implements CacheEventListener {


	@Override
	public void notifyElementRemoved(final Ehcache cache, final Element element) throws CacheException {
	}

	@Override
	public void notifyElementPut(final Ehcache cache, final Element element) throws CacheException {
	}

	@Override
	public void notifyElementUpdated(final Ehcache cache, final Element element) throws CacheException {
	}

	@Override
	public void notifyElementExpired(final Ehcache cache, final Element element) {
		GroovyUtility.removeExpiredCompiledScriptFromCache(element);
	}

	@Override
	public void notifyElementEvicted(final Ehcache cache, final Element element) {
	}

	@Override
	public void notifyRemoveAll(final Ehcache cache) {
	}

	@Override
	public void dispose() {
	}

	@SuppressWarnings("PMD.CloneMethodMustImplementCloneable")
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
