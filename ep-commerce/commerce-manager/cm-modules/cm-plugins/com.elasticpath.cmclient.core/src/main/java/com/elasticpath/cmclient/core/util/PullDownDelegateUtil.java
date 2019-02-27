/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.util;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Pull down delegate utility.
 */
public final class PullDownDelegateUtil {

	private static final int CLICK_EVENT = 13;

	private static final int DETAIL = 4;

	/**
	 * When there is an icon to the action, this method allow a click to the icon being sensed.
	 *
	 * @param event  the event
	 */
	public static void runWithEvent(final Event event) {
		Widget widget = event.widget;
		if (widget instanceof ToolItem) {
			ToolItem toolItem = (ToolItem) widget;
			Listener[] listeners = toolItem.getListeners(CLICK_EVENT);
			if (listeners.length > 0) {
				Listener listener = listeners[0];

				Event innerEvent = new Event();
				innerEvent.type = CLICK_EVENT;
				innerEvent.widget = toolItem;
				innerEvent.detail = DETAIL;
				innerEvent.x = toolItem.getBounds().x;
				innerEvent.y = toolItem.getBounds().height;

				listener.handleEvent(innerEvent);
			}
		}
	}

	private PullDownDelegateUtil() {
		//private constructor
	}

}
