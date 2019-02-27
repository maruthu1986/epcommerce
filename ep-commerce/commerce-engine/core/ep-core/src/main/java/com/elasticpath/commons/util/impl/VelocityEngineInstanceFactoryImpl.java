/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.commons.util.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineFactory;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.commons.util.VelocityEngineInstanceFactory;

/**
 * Factory class for retrieving a velocity engine.
 * Each store needs its own instance to support store specific resource loading, as well as a cm specific one.
 * Supports invalidation of the engine through recreation.
 */
public class VelocityEngineInstanceFactoryImpl extends VelocityEngineFactory implements
		VelocityEngineInstanceFactory, InvalidatableCache {

	private Map<String, VelocityEngine> engineMap = new ConcurrentHashMap<>();

	/**
	 * @see com.elasticpath.commons.util.VelocityEngineInstanceFactory#getVelocityEngine(String) ()
	 * @param storeCode for the particular store's velocity engine.
	 * @return a velocity engine
	 */
	@Override
	public VelocityEngine getVelocityEngine(final String storeCode) {
		String key = storeCode;
		if (key == null) {
			key = StringUtils.EMPTY;
		}
		synchronized (this) {
			VelocityEngine engine = engineMap.get(key);
			if (engine == null) {
				try {
					engine = createVelocityEngine();
					engineMap.put(key, engine);
				} catch (Exception e) {
					throw new EpServiceException("Error getting velocity engine for store " + storeCode, e);
				}
			}
			return engine;
		}
	}

	/**
	 * @see com.elasticpath.commons.util.InvalidatableCache#invalidate()
	 */
	@Override
	public void invalidate() {
		synchronized (this) {
			engineMap = new ConcurrentHashMap<>();
		}
	}

}
