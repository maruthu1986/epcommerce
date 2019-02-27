/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.BaseAmountChangedEventListener;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Manages events related to Price List Management.
 */
public final class BaseAmountEventService {

	private final List<BaseAmountChangedEventListener> baseAmountChangedEventListeners = new ArrayList<BaseAmountChangedEventListener>();
	
	private BaseAmountEventService() {
		//do nothing
	}
	
	/**
	 * @return the session instance of the event service
	 */
	public static BaseAmountEventService getInstance() {
		return CmSingletonUtil.getSessionInstance(BaseAmountEventService.class);
	}

	/**
	 * Add base amount change event listener.
	 * 
	 * @param listener the listener
	 */
	public void addBaseAmountChangedEventListener(final BaseAmountChangedEventListener listener) {
		baseAmountChangedEventListeners.add(listener);
	}

	/**
	 * Remove base amount change event listener.
	 * 
	 * @param listener the listener
	 */
	public void removeBaseAmountChangedEventListener(final BaseAmountChangedEventListener listener) {
		baseAmountChangedEventListeners.remove(listener);
	}

	/**
	 * Fire base amount changed event.
	 * 
	 * @param event the base amount changed event
	 */
	public void fireBaseAmountChangedEvent(final ItemChangeEvent<BaseAmountDTO> event) {
		for (final BaseAmountChangedEventListener listener : baseAmountChangedEventListeners) {
			Display.getDefault().asyncExec(() -> listener.baseAmountChanged(event));
		}
	}
}
