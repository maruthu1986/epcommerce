/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.views.order;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.editors.EntityEditorInput;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEditor;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;

/**
 * An action used to open the order details editor.
 */
public class OpenOrderEditorAction extends Action implements IDoubleClickListener {

	private static final Logger LOG = Logger.getLogger(OpenOrderEditorAction.class);

	private final TableViewer viewer;

	private final IWorkbenchPartSite workbenchPartSite;

	/**
	 * Constructs new <code>Action</code>.
	 * 
	 * @param viewer the table viewer
	 * @param workbenchPartSite the workbench site from the view
	 */
	public OpenOrderEditorAction(final TableViewer viewer, final IWorkbenchPartSite workbenchPartSite) {
		this.viewer = viewer;
		this.workbenchPartSite = workbenchPartSite;
	}

	@Override
	public void run() {
		final ISelection selection = this.viewer.getSelection();
		final Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof Order) {
			showEditor((Order) obj, workbenchPartSite);
		}

	}

	/**
	 * Show order editor for the specified order.
	 * 
	 * @param order order to be shown.
	 * @param workbenchPartSite workbench on which editor will be shown.
	 */
	public static void showEditor(final Order order, final IWorkbenchPartSite workbenchPartSite) {
		// TODO: Fix the first parameter here...
		// TODO: Externalize the first parameter here...
		final IEditorInput editorInput = new EntityEditorInput(getOrderEditorName(order), 
				getOrderToolTipText(order), order.getUidPk(), Order.class);

		try {
			workbenchPartSite.getPage().openEditor(editorInput, OrderEditor.ID_EDITOR);
		} catch (final PartInitException e) {
			LOG.error("Can not open order details editor", e); //$NON-NLS-1$
		}
	}

	/**
	 * Called when a double click user event occurs.
	 * 
	 * @param event double click event
	 */
	@Override
	public void doubleClick(final DoubleClickEvent event) {
		this.run();
	}

	private static String getOrderEditorName(final Order order) {
		return "#" + order.getOrderNumber(); //$NON-NLS-1$
	}

	private static String getOrderToolTipText(final Order order) {
		final String orderNumber = order.getOrderNumber();
		final OrderAddress address = order.getBillingAddress();
		String customerFullName = StringUtils.EMPTY;
		if (address != null) {
			customerFullName = address.getFullName();
		}
		return FulfillmentMessages.get().OrderEditor_ToolTipText + orderNumber + " - " + customerFullName; //$NON-NLS-1$
	}

}
