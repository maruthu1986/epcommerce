/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.editors.actions.CreateExchangeContributionAction;
import com.elasticpath.cmclient.fulfillment.editors.actions.CreateReturnContributionAction;
import com.elasticpath.cmclient.fulfillment.wizards.ExchangeWizard;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.service.customer.CustomerService;

/**
 * Represents the physical shipment Returns/Exchanges sub section.
 */
public class OrderDetailsPhysicalShipmentSubSectionReturnAndExchange implements SelectionListener {

	private static final String STRING_NEW_LINE = "\n"; //$NON-NLS-1$

	private final PhysicalOrderShipment orderShipment;

	private final OrderEditor editor;

	private Button createReturnButton;

	private Button createExchangeButton;

	/**
	 * The constructor.
	 *
	 * @param orderShipment the physical shipment
	 * @param editor the editor
	 */
	public OrderDetailsPhysicalShipmentSubSectionReturnAndExchange(final PhysicalOrderShipment orderShipment,
			final AbstractCmClientFormEditor editor) {
		this.orderShipment = orderShipment;
		this.editor = (OrderEditor) editor;
	}

	/**
	 * Creates the controls.
	 *
	 * @param client the composite
	 * @param toolkit the form tool kit
	 */

	protected void createControls(final Composite client, final FormToolkit toolkit) {
		Section section = toolkit.createSection(client, ExpandableComposite.TITLE_BAR);
		section.setText(getSectionTitle());

		IEpLayoutComposite shippingStatusPane = CompositeFactory.createTableWrapLayoutComposite(section, 1, false);
		shippingStatusPane.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		createShipmentStatusControls(shippingStatusPane);

		section.setClient(shippingStatusPane.getSwtComposite());
	}

	private void createShipmentStatusControls(final IEpLayoutComposite shippingStatusPane) {
		AuthorizationService authorizationService = AuthorizationService.getInstance();
		boolean authorized = editor.isAuthorizedAndAvailableForEdit();
		boolean authorizedReturn = authorized && authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CREATE_EDIT_RETURNS)
				&& orderShipment.getShipmentStatus() == OrderShipmentStatus.SHIPPED;
		boolean authorizedExchange = authorized && authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CREATE_EDIT_EXCHANGES)
				&& orderShipment.getShipmentStatus() == OrderShipmentStatus.SHIPPED;

		EpState rolePermissionReturn = EpState.DISABLED;
		if (authorizedReturn) {
			rolePermissionReturn = EpState.EDITABLE;
		}
		EpState exchangeButtonState = EpState.DISABLED;
		// TODO: Exchanges for purchase made with payment tokens was to be implemented in CE-164: Modify Purchase made with tokenized payment.
		if (authorizedExchange) {
			exchangeButtonState = EpState.EDITABLE;
		}

		IEpLayoutData shipmentButtonsPaneData = shippingStatusPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		IEpLayoutComposite shipmentButtonsPane = shippingStatusPane.addTableWrapLayoutComposite(1, true, shipmentButtonsPaneData);
		createReturnButton = shipmentButtonsPane.addPushButton(FulfillmentMessages.get().RAESection_CreateReturnButton, FulfillmentImageRegistry
				.getImage(FulfillmentImageRegistry.IMAGE_RETURN_CREATE), rolePermissionReturn, shipmentButtonsPaneData);
		createReturnButton.addSelectionListener(this);

		createExchangeButton = shipmentButtonsPane.addPushButton(FulfillmentMessages.get().RAESection_CreateExchangeButton, FulfillmentImageRegistry
				.getImage(FulfillmentImageRegistry.IMAGE_EXCHANGE_CREATE), exchangeButtonState, shipmentButtonsPaneData);
		createExchangeButton.addSelectionListener(this);
	}

	/**
	 * Not used.
	 *
	 * @param event the selection event
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// Nothing
	}

	private String formatEditorDirtyCreateReturnMessage() {
		return FulfillmentMessages.get().ShipmentSection_EditorDirtyReleaseShipmentMessage1 + STRING_NEW_LINE + STRING_NEW_LINE
				+ FulfillmentMessages.get().RAESection_EditorDirtyCreateReturnMessage;
	}

	/**
	 * Invoked when Release Shipment or Cancel Shipment button is clicked.
	 *
	 * @param event the selection event
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (!editor.openDirtyEditorWarning(FulfillmentMessages.get().RAESection_EditorDirtyCreateReturnTitle,
				formatEditorDirtyCreateReturnMessage())) {
			if (event.getSource() == createReturnButton) {
				CreateReturnContributionAction createReturnAction = new CreateReturnContributionAction(FulfillmentMessages.EMPTY_STRING, editor,
						orderShipment);
				createReturnAction.run();
			}

			if (event.getSource() == createExchangeButton) {
				ExchangeWizard.createExchangeWizard(orderShipment);
				CreateExchangeContributionAction createExchangeAction = new CreateExchangeContributionAction(FulfillmentMessages.EMPTY_STRING,
						editor, orderShipment);
				// new order(Exchange) can be created only for the customer with status ACTIVE. see MSC-5339
				int customerStatus = ((CustomerService) ServiceLocator.getService(ContextIdNames.CUSTOMER_SERVICE))
						.findCustomerStatusByUid(getOrder().getCustomer().getUidPk());
				if (customerStatus == Customer.STATUS_ACTIVE) {
					createExchangeAction.run();
				} else {
					MessageDialog.openInformation(editor.getSite().getShell(), FulfillmentMessages.get().OrderDetailsCustomerIsDisabled_Title,

							NLS.bind(FulfillmentMessages.get().OrderDetailsCustomerIsDisabled_Message,
							getOrder().getCustomer().getFullName()));
				}

			}
		}
	}

	/**
	 * Gets the section title.
	 * 
	 * @return string
	 */
	protected String getSectionTitle() {
		return FulfillmentMessages.get().RAESection_SubSectionStatus;
	}

	private Order getOrder() {
		return editor.getModel();
	}
	
}
