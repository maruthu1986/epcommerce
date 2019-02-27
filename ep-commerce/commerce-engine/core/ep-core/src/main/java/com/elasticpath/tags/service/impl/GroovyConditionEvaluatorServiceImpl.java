/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tags.service.impl;

import static com.elasticpath.tags.service.impl.GroovyExpressionBuilder.EVAL_METHOD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import groovy.lang.Script;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.ConditionEvaluatorService;
import com.elasticpath.tags.service.GroovyConditionProcessingService;

/**
 * ConditionalExpression evaluator with a Groovy script engine.
 * Evaluates conditions strings need to be expressed in tag framework DSL format. 
 */
public class GroovyConditionEvaluatorServiceImpl implements ConditionEvaluatorService {
	private static final Logger LOG = Logger.getLogger(GroovyConditionEvaluatorServiceImpl.class);

	private GroovyConditionProcessingService conditionProcessingService;
	private SimpleTimeoutCache<ConditionEvaluationCacheKey, Boolean> evaluationCache;
	private List<String> excludedFromCacheTags = new ArrayList<>();

	/**
	 * Initialize the groovy environment by loading the groovy
	 * initialization script to make modifications to MetaClasses.
	 *
	 * Getting class from classpath to avoid dependency on groovy classes from java code.
	 */
	public void initialize() {
		//Nothing to do
	}

	@Override
	public boolean evaluateConditionOnTags(final TagSet tags, final ConditionalExpression condition) {
		ConditionEvaluationCacheKey evaluationKey = new ConditionEvaluationCacheKey(tags.getTags(), condition, excludedFromCacheTags);
		Boolean result;
		synchronized (evaluationCache) {
			result = evaluationCache.get(evaluationKey);
			if (result == null) {
				result = evaluateConditionOnMap(tags.getTags(), condition);
				evaluationCache.put(evaluationKey, result);
			}
		}
		return result;
	}

	/**
	 * Evaluation on a map.
	 * @param map of string tag key to string tag values.
	 * @param condition the condition to evaluate
	 * @return true if condition script evaluates true
	 */
	boolean evaluateConditionOnMap(final Map<String, Tag> map, final ConditionalExpression condition) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Evaluating on map \n" + map + "\ncondition \n" + condition);
		}
		try {
			Script script = conditionProcessingService.preprocess(condition);
			return (Boolean) script.invokeMethod(EVAL_METHOD, new Object [] {map});
		} catch (Exception e) {
			throw new EpServiceException("Exception evaluating condition \n"
					+ condition.getConditionString()
					+ "\n\tOn\n" + map, e);
		}
	}

	protected GroovyConditionProcessingService getConditionProcessingService() {
		return conditionProcessingService;
	}

	public void setConditionProcessingService(final GroovyConditionProcessingService conditionProcessingService) {
		this.conditionProcessingService = conditionProcessingService;
	}

	protected List<String> getExcludedFromCacheTags() {
		return excludedFromCacheTags;
	}

	public void setExcludedFromCacheTags(final List<String> excludedFromCacheTags) {
		this.excludedFromCacheTags = excludedFromCacheTags;
	}

	protected SimpleTimeoutCache<ConditionEvaluationCacheKey, Boolean> getEvaluationCache() {
		return evaluationCache;
	}

	public void setEvaluationCache(final SimpleTimeoutCache<ConditionEvaluationCacheKey, Boolean> evaluationCache) {
		this.evaluationCache = evaluationCache;
	}

}
