/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.applied;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions.PromotionIdentifierUtil
		.buildPromotionIdentifiers;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.promotions.AppliedPromotionsForShippingOptionIdentifier;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingOptionRepository;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Repository that implements reading applied promotions for shipping option.
 *
 * @param <I>	extends AppliedPromotionsForShippingOptionIdentifier
 * @param <IE>	extends PromotionIdentifier
 */
@Component
public class AppliedPromotionsForShippingOptionRepositoryImpl<I extends AppliedPromotionsForShippingOptionIdentifier,
		IE extends PromotionIdentifier> implements LinksRepository<AppliedPromotionsForShippingOptionIdentifier, PromotionIdentifier> {

	private CartOrderRepository cartOrderRepository;
	private ShippingOptionRepository shippingOptionRepository;
	private PricingSnapshotRepository pricingSnapshotRepository;
	private PromotionRepository promotionRepository;
	private ShippingOptionService shippingOptionService;

	@Override
	public Observable<PromotionIdentifier> getElements(final AppliedPromotionsForShippingOptionIdentifier identifier) {
		ShippingOptionIdentifier shippingOptionIdentifier = identifier.getShippingOption();
		String scope = shippingOptionIdentifier.getScope().getValue();
		Map<String, String> shipmentDetailsId = shippingOptionIdentifier.getShipmentDetailsId().getValue();
		String shippingOptionId = shippingOptionIdentifier.getShippingOptionId().getValue();
		return cartOrderRepository.getEnrichedShoppingCartForShipments(scope, shipmentDetailsId)
				.flatMap(shoppingCart -> getAppliedPromotions(shippingOptionId, shoppingCart))
				.flatMapObservable(appliedPromotions -> buildPromotionIdentifiers(scope, appliedPromotions));
	}

	private Single<Collection<String>> getAppliedPromotions(final String shippingOptionId, final ShoppingCart shoppingCart) {
		final List<ShippingOption> shippingOptions = shippingOptionService.getShippingOptions(shoppingCart).getAvailableShippingOptions();
		return pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(shoppingCart)
				.flatMap(cartPricingSnapshot -> getShippingPromotions(shippingOptionId,
						Observable.fromIterable(shippingOptions), cartPricingSnapshot));
	}

	private Single<Collection<String>> getShippingPromotions(final String shippingOptionId,
															 final Observable<ShippingOption> shippingOptions,
															 final ShoppingCartPricingSnapshot cartPricingSnapshot) {
		return shippingOptionRepository.getShippingOption(shippingOptionId, shippingOptions)
				.map(shippingOption -> promotionRepository.getAppliedShippingPromotions(cartPricingSnapshot, shippingOption));
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setShippingOptionRepository(final ShippingOptionRepository shippingOptionRepository) {
		this.shippingOptionRepository = shippingOptionRepository;
	}

	@Reference
	public void setPricingSnapshotRepository(final PricingSnapshotRepository pricingSnapshotRepository) {
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}

	@Reference
	public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
		this.shippingOptionService = shippingOptionService;
	}
}
