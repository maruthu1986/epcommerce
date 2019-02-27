/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler.registry;

import com.elasticpath.rest.integrations.payment.tokens.handler.CreatePaymentTokenHandler;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;

/**
 * Registry to lookup {@link CreatePaymentTokenHandler}s.
 */
@SuppressWarnings("rawtypes")
public interface CreatePaymentTokenHandlerRegistry {

	/**
	 * Find the {@link CreatePaymentTokenHandler} for the {@link PaymentTokenOwnerType}.
	 *
	 *
	 * @param ownerType the {@link PaymentTokenOwnerType}
	 * @return the {@link CreatePaymentTokenHandler} for the owning representation type
	 */
	CreatePaymentTokenHandler lookupHandler(PaymentTokenOwnerType ownerType);
}
