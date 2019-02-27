/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.prototypes;

import io.reactivex.Completable;

import com.elasticpath.rest.definition.lookups.LookupsResource;

/**
 * Lookups prototype for Read operation.
 */
public class ReadLookupsPrototype implements LookupsResource.Read {

	@Override
	public Completable onRead() {
		return Completable.complete();
	}
}