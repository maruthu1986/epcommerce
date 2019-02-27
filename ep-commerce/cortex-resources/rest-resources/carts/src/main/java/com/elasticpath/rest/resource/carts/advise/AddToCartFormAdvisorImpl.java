/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.advise;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Observable;

import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.carts.AddToCartFormAdvisor;
import com.elasticpath.rest.definition.carts.AddToDefaultCartFormIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.AddToCartAdvisorService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Add to cart form advisor.
 */
public final class AddToCartFormAdvisorImpl implements AddToCartFormAdvisor.FormAdvisor {

	private final String itemId;
	private final String scope;
	private final AddToCartAdvisorService addToCartAdvisorService;

	/**
	 * Constructor.
	 *
	 * @param addToDefaultCartFormIdentifier addToDefaultCartFormIdentifier
	 * @param addToCartAdvisorService        addToCartAdvisorService
	 */
	@Inject
	public AddToCartFormAdvisorImpl(@RequestIdentifier final AddToDefaultCartFormIdentifier addToDefaultCartFormIdentifier,
									@ResourceService final AddToCartAdvisorService addToCartAdvisorService) {
		Map<String, String> itemIdentifier = addToDefaultCartFormIdentifier.getItem().getItemId().getValue();
		this.itemId = itemIdentifier.get(ItemRepository.SKU_CODE_KEY);
		this.scope = addToDefaultCartFormIdentifier.getItem().getItems().getScope().getValue();
		this.addToCartAdvisorService = addToCartAdvisorService;
	}

	@Override
	public Observable<Message> onAdvise() {
		return addToCartAdvisorService.validateItemPurchasable(scope, itemId);
	}
}
