/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.core.controller.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import com.elasticpath.cmclient.core.controller.BaseController;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.eventlistener.UIEventListener;

/**
 * AbstractBaseControllerImpl
 * Abstract UI Controller interface.
 * @param <T> type.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractBaseControllerImpl<T> 
implements BaseController<SearchResultEvent<T>, UIEventListener<SearchResultEvent<T>>>, UIEventListener<UIEvent<?>>  {

	private final List<UIEventListener<SearchResultEvent < T >>> listeners = 
		ListUtils.synchronizedList(new ArrayList<UIEventListener<SearchResultEvent < T >>>());

	@Override
	public void addListener(final UIEventListener<SearchResultEvent < T >> eventListener) {
		synchronized (this.listeners) {
			this.listeners.add(eventListener);
		}
	}

	@Override
	public void removeAllListeners() {
		synchronized (this.listeners) {
			this.listeners.clear();
		}
	}

	@Override
	public void removeListener(final UIEventListener<SearchResultEvent < T >> eventListener) {
		synchronized (this.listeners) {
			this.listeners.remove(eventListener);
		}
	}

	/**
	 * Get the list of event listeners.
	 * @return list of event listeners.
	 */
	protected List<UIEventListener<SearchResultEvent < T >>> getListeners() {
		return listeners;
	}

	/**
	 * Fire event.
	 * @param eventObject event object.
	 */
	public void fireEvent(final SearchResultEvent < T > eventObject) {
		synchronized (this.listeners) {
			int size = listeners.size();
			for (int x = 0; x < size; x++) {
				this.listeners.get(x).onEvent(eventObject);
			}
		}
	}


}
