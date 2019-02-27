/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

import java.util.EventObject;
/**
 * Class represent generic user interface event, 
 * like button press, combo box item selected, etc. 
 * @param <T> type.
 */
public class UIEvent<T> extends EventObject {
	private static final long serialVersionUID = -4278390004183487557L;
	private final boolean startFromFirstPage;
	private final EventType eventType;
	
	/**
	 * Default constructor.
	 * @param source - the event source.
	 * @param eventType event type
	 * @param startFromFirstPage show result from first page.
	 */
	public UIEvent(final T source, final EventType eventType, final boolean startFromFirstPage) {
		super(source);
		this.eventType = eventType;
		this.startFromFirstPage = startFromFirstPage;
	}
	
    /**
     * The object on which the Event initially occurred.
     *
     * @return   The object on which the Event initially occurred.
     */
	public T getSource() {
		return (T) super.getSource();
	}

	/**
	 * Start view from first page flag.
	 * @return true if start view from first page 
	 */
	public boolean isStartFromFirstPage() {
		return startFromFirstPage;
	}

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

}
