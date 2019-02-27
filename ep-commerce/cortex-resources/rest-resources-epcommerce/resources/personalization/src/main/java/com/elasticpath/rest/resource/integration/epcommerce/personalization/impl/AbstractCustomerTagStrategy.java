/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import java.util.Optional;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerTagStrategy;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Abstract class for TagStrategies. Takes care of when-to-create logic.
 */
public abstract class AbstractCustomerTagStrategy implements CustomerTagStrategy {

	@Override
	public void populate(final Customer customer, final TagSet tagSet) {
		if (null == tagSet.getTagValue(tagName())) {
			Optional<Tag> newTag = createTag(customer);
			if (newTag.isPresent()) {
				tagSet.addTag(tagName(), newTag.get());
			}
		}
	}

	/**
	 * The name of the tag.
	 *
	 * @return tag name
	 */
	protected abstract String tagName();

	/**
	 * Create a new tag value.
	 *
	 * @param customer the customer to create a tag from
	 * @return Optional Tag. If no data available to create a tag with, then return empty.
	 */
	protected abstract Optional<Tag> createTag(final Customer customer);
}
