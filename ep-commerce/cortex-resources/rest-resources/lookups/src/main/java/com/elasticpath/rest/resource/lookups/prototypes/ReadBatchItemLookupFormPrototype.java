/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.lookups.prototypes;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.definition.lookups.BatchItemsFormResource;
import com.elasticpath.rest.definition.lookups.CodesEntity;

/**
 * Read prototype for batch items lookup form resource.
 */
public class ReadBatchItemLookupFormPrototype implements BatchItemsFormResource.Read {

	@Override
	public Single<CodesEntity> onRead() {
		return Single.just(CodesEntity.builder()
				.withCodes(ImmutableList.of(StringUtils.EMPTY))
				.build());
	}
}