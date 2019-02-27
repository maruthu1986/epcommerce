/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tags.service.impl;

import static com.elasticpath.tags.service.impl.GroovyExpressionBuilder.buildExpression;

import java.util.concurrent.FutureTask;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.log4j.Logger;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.GroovyConditionProcessingService;

/**
 * Condition processing service that caches compiled scripts.
 */
public class GroovyConditionProcessingServiceImpl implements GroovyConditionProcessingService {
	private static final Logger LOG = Logger.getLogger(GroovyConditionProcessingServiceImpl.class);

	//injected via Spring
	private SimpleTimeoutCache<String, FutureTask<Script>> scriptCache;

	@Override
	public Script preprocess(final ConditionalExpression condition) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Processing expression " + condition.getConditionString());
		}
		FutureTask<Script> compilationTask = getScriptCompilationTaskAtomic(condition);
		return compilationTask.get();
	}


	private FutureTask<Script> getScriptCompilationTaskAtomic(final ConditionalExpression condition) throws Exception {
		boolean newlyCreated = false;
		FutureTask<Script> compilationTask;
		synchronized (scriptCache) {
			compilationTask = scriptCache.get(condition.getConditionString());

			if (compilationTask == null) {
				newlyCreated = true;
				compilationTask = new FutureTask<>(() -> new GroovyShell().parse(buildExpression(condition.getConditionString())));
				scriptCache.put(condition.getConditionString(), compilationTask);
			}
		}
		if (newlyCreated) {
			compilationTask.run();
		}
		return compilationTask;
	}

	protected SimpleTimeoutCache<String, FutureTask<Script>> getScriptCache() {
		return scriptCache;
	}

	public void setScriptCache(final SimpleTimeoutCache<String, FutureTask<Script>> scriptCache) {
		this.scriptCache = scriptCache;
	}

}
