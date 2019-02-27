/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.promotions.PurchaseCouponPromotionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository that implements reading purchase coupon promotion.
 *
 * @param <E> extends PromotionEntity
 * @param <I> extends PurchaseCouponPromotionIdentifier
 */
@Component
public class PurchaseCouponPromotionDetailsRepositoryImpl<E extends PromotionEntity, I extends PurchaseCouponPromotionIdentifier> implements
		Repository<PromotionEntity, PurchaseCouponPromotionIdentifier> {

	private PromotionRepository promotionRepository;

	@Override
	public Single<PromotionEntity> findOne(final PurchaseCouponPromotionIdentifier identifier) {
		PurchaseIdentifier purchaseIdentifier = identifier.getPurchaseCoupon().getPurchaseCouponList().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		String promotionId = identifier.getPromotionId().getValue();
		return promotionRepository.getPromotionEntity(scope, purchaseId, promotionId);
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}
}
