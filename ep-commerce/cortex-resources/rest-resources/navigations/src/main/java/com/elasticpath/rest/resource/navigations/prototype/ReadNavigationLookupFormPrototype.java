/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.navigations.prototype;

import io.reactivex.Single;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.rest.definition.navigations.CategoryCodeEntity;
import com.elasticpath.rest.definition.navigations.NavigationLookupFormResource;

/**
 * Read prototype for navigation lookup form resource.
 */
public class ReadNavigationLookupFormPrototype implements NavigationLookupFormResource.Read {
	@Override
	public Single<CategoryCodeEntity> onRead() {
		return Single.just(CategoryCodeEntity.builder()
				.withCode(StringUtils.EMPTY)
				.build());
	}
}
