/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin.payment.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.payment.AdminPaymentImageRegistry;
import com.elasticpath.cmclient.admin.payment.AdminPaymentMessages;
import com.elasticpath.cmclient.admin.payment.AdminPaymentPlugin;
import com.elasticpath.cmclient.admin.payment.actions.CreatePaymentGatewayAction;
import com.elasticpath.cmclient.admin.payment.actions.DeletePaymentGatewayAction;
import com.elasticpath.cmclient.admin.payment.actions.EditPaymentGatewayAction;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * View to show and allow the manipulation of the available Payment Gateways in CM.
 */
public class PaymentListView extends AbstractListView {

	/** The View's ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.payment.views.PaymentListView"; //$NON-NLS-1$

	private static final String PAYMENT_TABLE = "Payment"; //$NON-NLS-1$

	// Column indices

	/** payment name column. */
	public static final int INDEX_GATEWAYNAME = 0;

	/** payment implementation column. */
	public static final int INDEX_GATEWAYIMPLE = 1;

	/** Column widths for payment gateway's list view. */
	private static final int[] COLUMN_WIDTHS = new int[]{200, 200};

	// Actions
	private Action createPaymentGatewayAction;

	private Action editPaymentGatewayAction;

	private Action deletePaymentGatewayAction;

	/** The Payment Gateway service. */
	private final PaymentGatewayService gatewayService;

	/**
	 * The constructor.
	 */
	public PaymentListView() {
		super(false, PAYMENT_TABLE);
		gatewayService = ServiceLocator.getService(ContextIdNames.PAYMENT_GATEWAY_SERVICE);
	}

	@Override
	protected String getPluginId() {
		return AdminPaymentPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		Separator paymentActionGroup = new Separator("paymentActionGroup"); //$NON-NLS-1$		

		getToolbarManager().add(paymentActionGroup);

		createPaymentGatewayAction = new CreatePaymentGatewayAction(this, AdminPaymentMessages.get().CreatePaymentGateway,
				AdminPaymentImageRegistry.IMAGE_PAYMENT_GATEWAY_CREATE);
		createPaymentGatewayAction.setToolTipText(AdminPaymentMessages.get().CreatePaymentGateway);

		editPaymentGatewayAction = new EditPaymentGatewayAction(this, AdminPaymentMessages.get().EditPaymentGateway,
				AdminPaymentImageRegistry.IMAGE_PAYMENT_GATEWAY_EDIT);
		editPaymentGatewayAction.setToolTipText(AdminPaymentMessages.get().EditPaymentGateway);
		editPaymentGatewayAction.setEnabled(false);
		addDoubleClickAction(editPaymentGatewayAction);

		deletePaymentGatewayAction = new DeletePaymentGatewayAction(this, AdminPaymentMessages.get().DeletePaymentGateway,
				AdminPaymentImageRegistry.IMAGE_PAYMENT_GATEWAY_DELETE);
		deletePaymentGatewayAction.setToolTipText(AdminPaymentMessages.get().DeletePaymentGateway);
		deletePaymentGatewayAction.setEnabled(false);

		// Actions have to be wrapped in ActionContributionItems so that they
		// can be forced to display both text and image
		ActionContributionItem createPaymentGatewayActionContribItem = new ActionContributionItem(createPaymentGatewayAction);
		ActionContributionItem editPaymentGatewayActionContribItem = new ActionContributionItem(editPaymentGatewayAction);
		ActionContributionItem deletePaymentGatewayActionContribItem = new ActionContributionItem(deletePaymentGatewayAction);

		createPaymentGatewayActionContribItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editPaymentGatewayActionContribItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		deletePaymentGatewayActionContribItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(paymentActionGroup.getGroupName(), editPaymentGatewayActionContribItem);
		getToolbarManager().appendToGroup(paymentActionGroup.getGroupName(), createPaymentGatewayActionContribItem);
		getToolbarManager().appendToGroup(paymentActionGroup.getGroupName(), deletePaymentGatewayActionContribItem);

		this.getViewer().addSelectionChangedListener(event -> {
			final ISelection selection = event.getSelection();

			if (selection instanceof StructuredSelection) {
				final IStructuredSelection strSelection = (IStructuredSelection) selection;
				Object firstSelection = strSelection.getFirstElement();

				deletePaymentGatewayAction.setEnabled(firstSelection != null);
				editPaymentGatewayAction.setEnabled(firstSelection != null);
			}
		});
	}

	@Override
	protected void initializeTable(final IEpTableViewer viewerTable) {
		String[] columnNames = new String[]{AdminPaymentMessages.get().GatewayName, AdminPaymentMessages.get().GatewayImpl};

		for (int i = 0; i < columnNames.length; i++) {
			viewerTable.addTableColumn(columnNames[i], COLUMN_WIDTHS[i]);
		}
	}

	/**
	 * Return a the table's selected payment gateway item.
	 * 
	 * @return the selected payment gateway
	 */
	public PaymentGateway getSelectedPaymentGateway() {
		IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		PaymentGateway paymentGateway = null;
		if (!selection.isEmpty()) {
			paymentGateway = (PaymentGateway) selection.getFirstElement();
		}
		return paymentGateway;
	}

	@Override
	protected Object[] getViewInput() {
		List<PaymentGateway> paymentGateways = gatewayService.findAllPaymentGateways();
		return paymentGateways.toArray(new PaymentGateway[paymentGateways.size()]);
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new PaymentViewLabelProvider();
	}

	/**
	 * Label provider for Payment Gateway view.
	 */
	protected class PaymentViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Get the image to put in each column.
		 *
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Get the text to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			PaymentGateway payment = (PaymentGateway) element;

			switch (columnIndex) {
			case PaymentListView.INDEX_GATEWAYNAME:
				return payment.getName();
			case PaymentListView.INDEX_GATEWAYIMPLE:
				return payment.getType();
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
