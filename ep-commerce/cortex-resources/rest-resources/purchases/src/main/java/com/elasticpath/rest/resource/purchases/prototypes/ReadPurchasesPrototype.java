/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.prototypes;

import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesResource;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Purchases prototype for Read operation.
 */
public class ReadPurchasesPrototype implements PurchasesResource.Read {

	private final IdentifierPart<String> scope;

	private final Repository<PurchaseEntity, PurchaseIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param scope      scope
	 * @param repository repository
	 */
	@Inject
	public ReadPurchasesPrototype(
			@UriPart(PurchasesIdentifier.SCOPE) final IdentifierPart<String> scope,
			@ResourceRepository final Repository<PurchaseEntity, PurchaseIdentifier> repository) {
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Observable<PurchaseIdentifier> onRead() {
		return repository.findAll(scope);
	}
}
