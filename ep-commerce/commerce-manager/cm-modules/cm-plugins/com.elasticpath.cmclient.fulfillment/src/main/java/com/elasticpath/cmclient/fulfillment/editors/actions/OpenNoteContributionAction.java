/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderNoteNotesSectionPart;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.EditNoteDialog;
import com.elasticpath.domain.order.OrderEvent;

/**
 * Edit order note action.
 */
public class OpenNoteContributionAction extends Action {

	private final OrderEvent orderEvent;
	private final AbstractCmClientEditorPageSectionPart sectionPart;
	private boolean viewOnly;

	/**
	 * Constructor.
	 *
	 * @param orderEvent the order note to edit
	 * @param sectionPart the source where this dialog is opened
	 */
	public OpenNoteContributionAction(final OrderEvent orderEvent,
			final AbstractCmClientEditorPageSectionPart sectionPart) {
		super(FulfillmentMessages.get().OrderNoteNotes_Button, CoreImageRegistry.IMAGE_OPEN);
		setToolTipText(FulfillmentMessages.get().OrderNoteNotes_Button);
		this.orderEvent = orderEvent;
		this.sectionPart = sectionPart;

	}

	@Override
	public void run() {
		final EditNoteDialog dialog =
			new EditNoteDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			this.orderEvent, this.viewOnly);
		if (dialog.open() == Window.OK && !viewOnly) {
			((OrderNoteNotesSectionPart) sectionPart).refreshNotes();
			((OrderNoteNotesSectionPart) sectionPart).getControlModificationListener().controlModified();
		}

	}

	/**
	 * Sets the view only mode.
	 *
	 * @param isViewOnly whether the dialog should be view only
	 */
	public void setViewOnly(final boolean isViewOnly) {
		this.viewOnly = isViewOnly;
	}
}
