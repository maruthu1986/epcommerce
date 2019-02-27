/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.prototypes;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.AddToDefaultCartFormResource;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.RequestForm;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Submit add to default cart form.
 */
public class SubmitAddToDefaultCartFormPrototype implements AddToDefaultCartFormResource.SubmitWithResult {

	private final LineItemEntity lineItemEntity;

	private final String itemId;

	private final IdentifierPart<String> scope;

	private final Repository<LineItemEntity, LineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param lineItemEntity lineItemEntity
	 * @param itemId         item id
	 * @param scope          scope
	 * @param repository     repository
	 */
	@Inject
	public SubmitAddToDefaultCartFormPrototype(@RequestForm final LineItemEntity lineItemEntity,
											   @UriPart(ItemIdentifier.ITEM_ID) final IdentifierPart<Map<String, String>> itemId,
											   @UriPart(CartIdentifier.SCOPE) final IdentifierPart<String> scope,
											   @ResourceRepository final Repository<LineItemEntity, LineItemIdentifier> repository) {
		this.lineItemEntity = lineItemEntity;
		this.itemId = itemId.getValue().get(ItemRepository.SKU_CODE_KEY);
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Single<SubmitResult<LineItemIdentifier>> onSubmitWithResult() {
		LineItemEntity entity = LineItemEntity.builderFrom(this.lineItemEntity)
				.withItemId(itemId)
				.withCartId(Default.URI_PART)
				.build();
		return repository.submit(entity, scope);
	}

}