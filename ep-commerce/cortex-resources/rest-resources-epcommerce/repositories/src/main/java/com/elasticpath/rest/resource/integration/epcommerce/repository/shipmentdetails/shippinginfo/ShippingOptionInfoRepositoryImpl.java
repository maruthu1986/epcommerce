/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.shippinginfo;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.shoppingcart.ShippingPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingOptionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Repository that implements reading a shipping option info.
 *
 * @param <E>	extends ShippingOptionEntity
 * @param <I>	extends ShippingOptionIdentifier
 */
@Component
public class ShippingOptionInfoRepositoryImpl<E extends ShippingOptionEntity, I extends ShippingOptionIdentifier> implements
		Repository<ShippingOptionEntity, ShippingOptionIdentifier> {

	private ResourceOperationContext resourceOperationContext;
	private ShippingOptionRepository shippingOptionRepository;
	private CartOrderRepository cartOrderRepository;
	private PricingSnapshotRepository pricingSnapshotRepository;
	private MoneyTransformer moneyTransformer;

	@Override
	public Single<ShippingOptionEntity> findOne(final ShippingOptionIdentifier identifier) {
		String scope = identifier.getScope().getValue();
		Map<String, String> shipmentDetailsId = identifier.getShipmentDetailsId().getValue();
		String shippingOptionId = identifier.getShippingOptionId().getValue();
		return getShippingOptionForShipmentDetails(scope, shipmentDetailsId, shippingOptionId);
	}

	private Single<ShippingOptionEntity> getShippingOptionForShipmentDetails(final String scope, final Map<String, String> shipmentDetailsId,
																			final String shippingOptionId) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		return shippingOptionRepository.findByCode(scope, shipmentDetailsId, shippingOptionId)
				.flatMap(shippingOption -> getShippingPricingSnapshot(scope, shipmentDetailsId, shippingOption)
						.map(shippingPricingSnapshot -> buildShippingOptionEntity(shippingOption, shippingPricingSnapshot, locale)));
	}

	private Single<ShippingPricingSnapshot> getShippingPricingSnapshot(final String scope, final Map<String, String> shipmentDetailsId,
																	   final ShippingOption shippingOption) {
		return cartOrderRepository.getEnrichedShoppingCartForShipments(scope, shipmentDetailsId)
				.flatMap(cart -> pricingSnapshotRepository.getShoppingCartPricingSnapshotSingle(cart)
						.map(shoppingCartPricingSnapshot -> shoppingCartPricingSnapshot.getShippingPricingSnapshot(shippingOption)));
	}

	private ShippingOptionEntity buildShippingOptionEntity(final ShippingOption shippingOption,
														   final ShippingPricingSnapshot shippingPricingSnapshot,
														   final Locale locale) {
		return ShippingOptionEntity.builder()
				.withCarrier(shippingOption.getCarrierCode().orElse(null))
				.withCost(getCosts(shippingPricingSnapshot, locale))
				.withDisplayName(shippingOption.getDisplayName(locale).orElse(null))
				.withName(shippingOption.getCode())
				.withShippingOptionId(shippingOption.getCode())
				.build();
	}

	private Collection<CostEntity> getCosts(final ShippingPricingSnapshot shippingPricingSnapshot, final Locale locale) {
		Money shippingCost = shippingPricingSnapshot.getShippingPromotedPrice();
		if (shippingCost == null) {
			return Collections.emptyList();
		}

		CostEntity cost = moneyTransformer.transformToEntity(shippingCost, locale);
		return Collections.singleton(cost);
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setShippingOptionRepository(final ShippingOptionRepository shippingOptionRepository) {
		this.shippingOptionRepository = shippingOptionRepository;
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Reference
	public void setPricingSnapshotRepository(final PricingSnapshotRepository pricingSnapshotRepository) {
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Reference
	public void setMoneyTransformer(final MoneyTransformer moneyTransformer) {
		this.moneyTransformer = moneyTransformer;
	}
}
