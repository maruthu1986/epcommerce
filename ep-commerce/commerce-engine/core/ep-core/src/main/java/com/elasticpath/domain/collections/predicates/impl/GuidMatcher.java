/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.collections.predicates.impl;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.elasticpath.domain.collections.predicates.Predicate;
import com.elasticpath.persistence.api.Entity;

/**
 * Selector for matching an entity's guid against a list by guids.
 */
public class GuidMatcher implements Predicate {

	private final Set<String> guids;

	/**
	 * Creates an instance of GuidMatcher with the <code>guids</code> set into it to use as
	 * selection criteria.
	 *
	 * @param guids Array of guids to match
	 */
	GuidMatcher(final String ...guids) {
		this.guids = ImmutableSet.copyOf(guids);
	}

	/**
	 * Returns a new instance of <code>ByGuidSelector</code> setup with <code>guids</code> as its criteria.
	 *
	 * @param guids The guids to match against.
	 * @return An instance of <code>ByGuidSelector</code> setup with the <code>guids</code> passed in.
	 */
	public static GuidMatcher byGuids(final String ...guids) {
		return new GuidMatcher(guids);
	}

	/**
	 * Indicates whether an entity matches the selection criteria. 
	 * 
	 * @param entity The entity to match against the selection criteria
	 * @return true if the entity passed in matches the selection criteria; false otherwise.
	 */
	@Override
	public boolean apply(final Entity entity) {
		return guids.contains(entity.getGuid());
	}
	
}
