/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.prototype;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.EmailInfoIdentifier;
import com.elasticpath.rest.definition.orders.EmailInfoResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;

/**
 * Read operation for the email information.
 */
public class ReadEmailInfoPrototype implements EmailInfoResource.Read {

	private static final String EMAIL_INFO_NAME = "email-info";

	private final EmailInfoIdentifier emailInfoIdentifier;

	/**
	 * Constructor.
	 *
	 * @param emailInfoIdentifier emailInfoIdentifier
	 */
	@Inject
	public ReadEmailInfoPrototype(@RequestIdentifier final EmailInfoIdentifier emailInfoIdentifier) {
		this.emailInfoIdentifier = emailInfoIdentifier;
	}

	@Override
	public Single<InfoEntity> onRead() {
		return Single.just(InfoEntity.builder()
				.withName(EMAIL_INFO_NAME)
				.withInfoId(emailInfoIdentifier.getOrder().getOrderId().getValue())
				.build());
	}
}
