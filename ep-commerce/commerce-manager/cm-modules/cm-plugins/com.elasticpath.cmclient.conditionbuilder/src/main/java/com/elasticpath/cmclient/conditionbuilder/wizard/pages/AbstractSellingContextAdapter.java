/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.pages;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.conditionbuilder.wizard.conditions.SellingContextResolver;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.sellingcontext.SellingContextService;
/**
 * Adapter for {@link SellingContext} object. 
 */
public abstract class AbstractSellingContextAdapter {

	private SellingContext sellingContext;
	
	/**
	 * @return this {@link SellingContext}.
	 */
	public SellingContext getSellingContext() {
		return sellingContext;
	}

	/**
	 * Sets selling context.
	 * @param sellingContext - {@link SellingContext} object
	 */
	public void setSellingContext(final SellingContext sellingContext) {
		this.sellingContext = sellingContext;
	}
	
	
	/**
	 * Populates Selling context with default values using given string. 
	 * 
	 * @param guid - selling context guid
	 * @param name - string to use for selling context default properties 
	 */
	protected void populateSellingContext(final String guid, final String name) {
		if (StringUtils.isEmpty(guid)) {
			setSellingContext(new SellingContextResolver().create(name));
		} else {
			final SellingContextService sellingContextService = ServiceLocator.getService(ContextIdNames.SELLING_CONTEXT_SERVICE);
			setSellingContext(sellingContextService.getByGuid(guid));			
		}
	}
	/**
	 * Populates Selling context with default values using given string.
	 * 
	 * @param sellingContext - selling context
	 * @param name - string to use for selling context default properties
	 */
	protected void populateSellingContext(final SellingContext sellingContext, final String name) {
		if (sellingContext == null) {
			populateSellingContext(StringUtils.EMPTY, name);
		} else {
			populateSellingContext(sellingContext.getGuid(), name);
		}
	}
	
	/**
	 * Removes selling context reference from owner object.
	 * 
	 */
	public abstract  void clearSellingContext();


}
