/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform;

import java.util.Collection;
import java.util.List;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.rest.advise.Message;

/**
 * Transforms a list of {@link StructuredErrorMessage} to a list of {@link Message}.
 */
public interface StructuredErrorMessageTransformer {

	/**
	 * Provides a way to generate {@link Message} for {@link StructuredErrorMessage}.
	 * @param structuredErrorMessageList the list of StructuredErrorMessage coming from ce.
	 * @param cortexResourceID the id of the relevant object, from cortex.
	 * @return Collection<Message> Message list.
	 */
	List<Message> transform(Collection<StructuredErrorMessage> structuredErrorMessageList, String cortexResourceID);
}
