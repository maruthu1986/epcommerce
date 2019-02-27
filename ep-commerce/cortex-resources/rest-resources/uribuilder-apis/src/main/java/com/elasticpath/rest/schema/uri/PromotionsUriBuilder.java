/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * URI Builder for promotions resource.
 */
public interface PromotionsUriBuilder extends ScopedUriBuilder<PromotionsUriBuilder>, ReadFromOtherUriBuilder<PromotionsUriBuilder> {

	/**
	 * Set the promotion ID.
	 *
	 * @param promotionId the promotion ID
	 * @return this builder
	 */
	PromotionsUriBuilder setPromotionId(String promotionId);

	/**
	 * Set the promotion promotionType.
	 *
	 * @param promotionType the promotion promotionType
	 * @return this builder
	 */
	PromotionsUriBuilder setPromotionType(String promotionType);
}
