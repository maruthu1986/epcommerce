/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.conditions;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;

/**
 * Resolves Selling context - creates new one if selling context does not exist for DCA. 
 */
public class SellingContextResolver {

	private static final String SELLING_CONTEXT_NAME = "SELLING_CONTEXT_NAME_"; //$NON-NLS-1$

	private static final String SELLING_CONTEXT_DESCR = "SELLING_CONTEXT_DESCR_"; //$NON-NLS-1$
	
	private static final int SELLING_CONTEXT_NAME_MAX_SIZE = 255;
	
	private static final int SELLING_CONTEXT_DESCR_MAX_SIZE = 255;
	
	/**
	 * Gets selling context for DynamicContentAssignment. If doesn't exist yet - create new one.
	 *
	 * @param name - DynamicContentDelivery object.
	 * @return - Selling context
	 */
	public SellingContext create(final String name) {
		
		SellingContext sellingContext = ServiceLocator.getService(ContextIdNames.SELLING_CONTEXT);

        String sellingContextNameStr = SELLING_CONTEXT_NAME + name;
		if (sellingContextNameStr.length() > SELLING_CONTEXT_NAME_MAX_SIZE) {
			sellingContextNameStr = sellingContextNameStr.substring(0, SELLING_CONTEXT_NAME_MAX_SIZE);
		}
		sellingContext.setName(sellingContextNameStr);

        String sellingContextDescrStr = SELLING_CONTEXT_DESCR + name;
		if (sellingContextDescrStr.length() > SELLING_CONTEXT_DESCR_MAX_SIZE) {
			sellingContextDescrStr = sellingContextDescrStr.substring(0, SELLING_CONTEXT_DESCR_MAX_SIZE);
		}
		sellingContext.setDescription(sellingContextDescrStr);
		return sellingContext;
	}
}