/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Action to open up the password change dialog.
 */
public class UserDropDownMenuHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (null == event || !(event.getTrigger() instanceof Event)) {
			return null;
		}

		Event eventWidget = (Event) event.getTrigger();

		if (!(eventWidget.widget instanceof ToolItem)) {
			return null;
		}

		ToolItem toolItem = (ToolItem) eventWidget.widget;

		Event newEvent = new Event();
		newEvent.button = 1;
		newEvent.widget = toolItem;
		newEvent.detail = SWT.ARROW;
		newEvent.x = toolItem.getBounds().x;
		newEvent.y = toolItem.getBounds().y + toolItem.getBounds().height;

		toolItem.notifyListeners(SWT.Selection, newEvent);

		return null;
	}
}
