/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.lookups.prototypes;

import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.definition.lookups.LookupItemFormResource;

/**
 * Read prototype for item lookup form resource.
 */
public class ReadItemLookupFormPrototype implements LookupItemFormResource.Read {

	@Override
	public Single<CodeEntity> onRead() {
		return Single.just(CodeEntity.builder()
				.withCode(StringUtils.EMPTY)
				.build());
	}
}