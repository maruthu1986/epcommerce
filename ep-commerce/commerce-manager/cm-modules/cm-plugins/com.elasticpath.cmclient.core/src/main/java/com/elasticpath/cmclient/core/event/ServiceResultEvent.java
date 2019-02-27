/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

import java.util.EventObject;

/**
 * Class represent generic result from services.
 * @param <T> type.
 */
public class ServiceResultEvent<T> extends EventObject {
	private static final long serialVersionUID = -2841535613654648076L;

	/**
	 * Default constructor.
	 * @param source - the event source.
	 */	
	public ServiceResultEvent(final T source) {
		super(source);
	}

    /**
     * The object on which the Event initially occurred.
     *
     * @return   The object on which the Event initially occurred.
     */
	public T getSource() {
		return (T) super.getSource();
	}
	

}
