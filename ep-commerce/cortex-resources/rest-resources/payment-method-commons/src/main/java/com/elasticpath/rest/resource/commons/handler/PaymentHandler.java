/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.commons.handler;

import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Strategy lookup the type for a payment. This is necessary when creating links to a payment when your not sure what payment type that is
 */
public interface PaymentHandler {
	/**
	 * Indicates which implementation of payment {@link ResourceEntity} this handler applies to.
	 *
	 * @return the specific subtype of payment {@link ResourceEntity}
	 */
	Class<? extends ResourceEntity> handledType();

	/**
	 * The representation type.
	 *
	 * @return the representation type.
	 */
	String representationType();

}
