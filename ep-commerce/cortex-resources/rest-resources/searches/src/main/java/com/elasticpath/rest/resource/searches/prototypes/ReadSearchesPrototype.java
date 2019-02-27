/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.searches.prototypes;

import io.reactivex.Completable;

import com.elasticpath.rest.definition.searches.SearchesResource;

/**
 * Read prototype for searches resource.
 */
public class ReadSearchesPrototype implements SearchesResource.Read {
	@Override
	public Completable onRead() {
		return Completable.complete();
	}
}
