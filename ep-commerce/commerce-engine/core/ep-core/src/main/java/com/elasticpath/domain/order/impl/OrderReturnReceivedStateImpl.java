/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.order.impl;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.misc.impl.AbstractPropertyBasedImpl;
import com.elasticpath.domain.order.OrderReturnReceivedState;

/**
 * Represents received state for SKU.
 */
public class OrderReturnReceivedStateImpl extends AbstractPropertyBasedImpl implements OrderReturnReceivedState {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Returns a map of available return received states.
	 * 
	 * @return the map of available states
	 */
	@Override
	public Map<String, String> getStateMap() {
		return super.getPropertyMap(getPropertyName());
	}

	/**
	 * Returns a map of available return received states based on locale.
	 * 
	 * @param locale the selected locale
	 * @return a map of available states based on locale
	 */
	@Override
	public Map<String, String> getStateMap(final Locale locale) {
		return super.getPropertyMap(getPropertyName(), locale);
	}

}
