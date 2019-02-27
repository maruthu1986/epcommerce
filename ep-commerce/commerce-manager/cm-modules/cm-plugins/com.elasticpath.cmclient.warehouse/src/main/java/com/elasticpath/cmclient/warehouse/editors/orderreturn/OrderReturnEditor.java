/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.editors.orderreturn;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.WarehousePlugin;
import com.elasticpath.cmclient.warehouse.event.OrderReturnChangeEvent;
import com.elasticpath.cmclient.warehouse.event.WarehouseEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.order.OrderReturnOutOfDateException;
import com.elasticpath.service.order.ReturnAndExchangeService;

/**
 * Implements a multi-page editor for displaying and editing order returns.
 */
public class OrderReturnEditor extends AbstractCmClientFormEditor {
	/** ID of the editor. It is the same as the class name. */
	public static final String ID_EDITOR = OrderReturnEditor.class.getName();

	private static final int TOTAL_WORK_UNITS = 3;

	private OrderReturn orderReturn;

	private ReturnAndExchangeService returnAndExchangeService;

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		this.returnAndExchangeService = ServiceLocator.getService(
				ContextIdNames.ORDER_RETURN_SERVICE);
		this.orderReturn = retrieveOrderReturn(input.getAdapter(Long.class));
	}

	@Override
	public OrderReturn getModel() {
		return orderReturn;
	}

	@Override
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	protected void addPages() {
		OrderReturnPage orderReturnPage = new OrderReturnPage(this);

		try {
			addPage(orderReturnPage);
			addExtensionPages(getClass().getSimpleName(), WarehousePlugin.PLUGIN_ID);
		} catch (final PartInitException ex) {
			// TODO: Find out what should be done in this case
			// Can't throw the PartInitException because it is checked
			// and the super-implementation doesn't check for it.
			// throwing an unchecked generic exception for now (bad)
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(WarehouseMessages.get().OrderEditor_Save_StatusBarMsg, TOTAL_WORK_UNITS);
		try {
			monitor.worked(1);
			orderReturn.getOrder().setModifiedBy(getEventOriginator());
			try {
				orderReturn = returnAndExchangeService.receiveReturn(orderReturn);
			} catch (OrderReturnOutOfDateException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						WarehouseMessages.get().OrderReturn_ErrDlgCollisionTitle, WarehouseMessages.get().OrderReturn_ErrDlgCollisionMessage);
				monitor.setCanceled(true);
				return;
			}
			refreshEditorPages();
			monitor.worked(1);

			WarehouseEventService.getInstance().fireOrderReturnChangeEvent(new OrderReturnChangeEvent(this, orderReturn));
		} finally {
			monitor.done();
		}

	}

	/**
	 * Retrieves the order return by uid.
	 *
	 * @param orderReturnUid the UID of the order return to be retrieved.
	 * @return the <code>Order</code>
	 */
	private OrderReturn retrieveOrderReturn(final long orderReturnUid) {
		FetchGroupLoadTuner tuner = ServiceLocator.getService(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		tuner.addFetchGroup(FetchGroupConstants.ORDER_INDEX,
			FetchGroupConstants.ORDER_NOTES,
			FetchGroupConstants.ALL);

		return returnAndExchangeService.get(orderReturnUid, tuner);
	}

	@Override
	public void reloadModel() {
		orderReturn = retrieveOrderReturn(orderReturn.getUidPk());
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(WarehouseMessages.get().OrderReturn_Editor_OnSavePrompt,
			getEditorName());
	}
	
	@Override
	protected String getEditorName() {
		return getEditorInput().getName();
	}
	

}