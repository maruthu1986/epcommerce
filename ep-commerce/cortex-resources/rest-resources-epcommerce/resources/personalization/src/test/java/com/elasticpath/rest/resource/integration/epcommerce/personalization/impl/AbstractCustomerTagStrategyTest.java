/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import java.util.Optional;

import org.junit.Test;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerTagStrategy;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Test the populate code path for edge cases.
 */
public class AbstractCustomerTagStrategyTest {

	static final String TAGNAME = "tagName";

	CustomerTagStrategy classUnderTest = new AbstractCustomerTagStrategy() {
		@Override
		protected String tagName() {
			return TAGNAME;
		}

		@Override
		protected Optional<Tag> createTag(final Customer customer) {
			throw new UnsupportedOperationException();
		}
	};

	@Test
	public void testPopulateWithExistingAttribute() throws Exception {
		TagSet tagSet = new TagSet();
		tagSet.addTag(TAGNAME, new Tag(new Object()));

		classUnderTest.populate(null, tagSet);
		//throws exception if test fails
	}
}