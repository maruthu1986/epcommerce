/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer;

import com.elasticpath.rest.identity.Subject;
import com.elasticpath.tags.TagSet;

/**
 * A UserTraitsToTagSetTransformer is responsible for adding tags to the CustomerSession.
 */
public interface UserTraitsToTagSetTransformer {

	/**
	 * Set up the customer session tags.
	 * @param subject The subject contains user traits.
	 * @return TagSet - The tags.
	 */
	TagSet transformUserTraitsToTagSet(Subject subject);
}
