/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.emails.prototypes;

import io.reactivex.Single;

import com.elasticpath.rest.definition.emails.AddEmailFormResource;
import com.elasticpath.rest.definition.emails.EmailEntity;

/**
 * Add item to email form.
 */
public class ReadAddEmailFormPrototype implements AddEmailFormResource.Read {

	@Override
	public Single<EmailEntity> onRead() {
		return Single.just(EmailEntity.builder()
				.withEmail("")
				.withEmailId("")
				.build());
	}
}
