/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Reads a payment token form.
 */
public interface PaymentTokenFormForOwnerLookup {

	/**
	 * Read a payment token form.
	 * @param createActionUri the create action uri
	 * @param createActionRel the rel to use
	 * @return the form.
	 */
	ExecutionResult<ResourceState<PaymentTokenEntity>> readPaymentTokenForm(String createActionUri, String createActionRel);
}
