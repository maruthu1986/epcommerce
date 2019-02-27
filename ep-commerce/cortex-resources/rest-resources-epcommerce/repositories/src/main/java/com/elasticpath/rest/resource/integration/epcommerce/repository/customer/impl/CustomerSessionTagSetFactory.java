/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerTagStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.UserTraitsToTagSetTransformer;
import com.elasticpath.tags.TagSet;

/**
 * Constructs a TagSet for use in a Customer Session.
 */
@Singleton
@Named("customerSessionTagSetFactory")
public class CustomerSessionTagSetFactory {

	private final ResourceOperationContext resourceOperationContext;
	private final CustomerTagStrategyRegistry tagStrategyRegistry;
	private final UserTraitsToTagSetTransformer userTraitTransformer;

	/**
	 * Constructor.
	 *
	 * @param resourceOperationContext the resource operation context
	 * @param tagStrategyRegistry tag strategy registry
	 * @param userTraitTransformer The customer session tagger.
	 */
	@Inject
	CustomerSessionTagSetFactory(
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("customerTagStrategyRegistry")
			final CustomerTagStrategyRegistry tagStrategyRegistry,
			@Named("userTraitsToTagSetTransformer")
			final UserTraitsToTagSetTransformer userTraitTransformer) {

		this.resourceOperationContext = resourceOperationContext;
		this.tagStrategyRegistry = tagStrategyRegistry;
		this.userTraitTransformer = userTraitTransformer;
	}

	/**
	 * Create a TagSet.
	 *
	 * @param customer the customer to create a TagSet for
	 * @return the tag set
	 */
	TagSet createTagSet(final Customer customer) {
		Subject subject = resourceOperationContext.getSubject();
		TagSet tagSet = subject == null
			? new TagSet()
			: userTraitTransformer.transformUserTraitsToTagSet(subject);

		for (CustomerTagStrategy customerTagStrategy : tagStrategyRegistry.getStrategies()) {
			customerTagStrategy.populate(customer, tagSet);
		}

		return tagSet;
	}
}
