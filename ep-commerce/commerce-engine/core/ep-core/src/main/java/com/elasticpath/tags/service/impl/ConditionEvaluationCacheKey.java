/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tags.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Maps;

import com.elasticpath.tags.Tag;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * An key used to define a unique conditional evaluation. Combines a TagSet and ConditionalExpression.
 */
class ConditionEvaluationCacheKey {

	private final Map<String, Tag> tags;
	private final ConditionalExpression conditionalExpression;

	/**
	 * Create a new Evaluation Key.
	 *
	 * @param tagMap the tag map to use
	 * @param conditionalExpression the conditional expression to use
	 * @param excludedTags a list of tag names to exclude from the key
	 */
	ConditionEvaluationCacheKey(final Map<String, Tag> tagMap, final ConditionalExpression conditionalExpression, final List<String> excludedTags) {
		this.tags = Maps.filterKeys(tagMap, key -> !excludedTags.contains(key));
		this.conditionalExpression = conditionalExpression;
	}

	public Map<String, Tag> getTags() {
		return tags;
	}

	public ConditionalExpression getConditionalExpression() {
		return conditionalExpression;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ConditionEvaluationCacheKey)) {
			return false;
		}
		ConditionEvaluationCacheKey that = (ConditionEvaluationCacheKey) other;
		return Objects.equals(tags, that.tags)
			   && Objects.equals(conditionalExpression, that.conditionalExpression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tags, conditionalExpression);
	}

}
