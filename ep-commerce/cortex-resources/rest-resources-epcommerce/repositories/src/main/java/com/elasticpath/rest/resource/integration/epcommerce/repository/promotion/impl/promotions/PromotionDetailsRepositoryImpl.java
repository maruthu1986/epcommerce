/*
 *  Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl.promotions;

import java.util.Locale;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.promotions.PromotionIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Repository that implements reading promotion details.
 *
 * @param <E> extends PromotionEntity
 * @param <I> extends PromotionIdentifier
 */
@Component
public class PromotionDetailsRepositoryImpl<E extends PromotionEntity, I extends PromotionIdentifier> implements
		Repository<PromotionEntity, PromotionIdentifier> {

	private PromotionRepository promotionRepository;
	private PromotionTransformer promotionTransformer;
	private ResourceOperationContext resourceOperatorContext;

	@Override
	public Single<PromotionEntity> findOne(final PromotionIdentifier identifier) {
		String promotionId = identifier.getPromotionId().getValue();
		Locale locale = SubjectUtil.getLocale(resourceOperatorContext.getSubject());
		return promotionRepository.findByPromotionId(promotionId)
				.map(rule -> promotionTransformer.transformToEntity(rule, locale));
	}

	@Reference
	public void setPromotionRepository(final PromotionRepository promotionRepository) {
		this.promotionRepository = promotionRepository;
	}

	@Reference
	public void setPromotionTransformer(final PromotionTransformer promotionTransformer) {
		this.promotionTransformer = promotionTransformer;
	}

	@Reference
	public void setResourceOperatorContext(final ResourceOperationContext resourceOperatorContext) {
		this.resourceOperatorContext = resourceOperatorContext;
	}
}
