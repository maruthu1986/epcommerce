/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.editors.actions.CreateReturnElectronicOrderContributionAction;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;

/**
 *Represents the electronic shipment Returns sub section.
 */
public class OrderDetailElectronicShipmentSubSectionReturn implements SelectionListener {

		private static final String STRING_NEW_LINE = "\n"; //$NON-NLS-1$

		private final ElectronicOrderShipment orderShipment;

		private final OrderEditor editor;

		private Button createReturnButton;
		
		/**
		 * The constructor.
		 * 
		 * @param orderShipment the physical shipment
		 * @param editor the editor
		 */
		public OrderDetailElectronicShipmentSubSectionReturn(final ElectronicOrderShipment orderShipment, 
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
			
			EpState rolePermissionReturn = EpState.DISABLED;
			if (authorizedReturn) {
				rolePermissionReturn = EpState.EDITABLE;
			}

			IEpLayoutData shipmentButtonsPaneData = shippingStatusPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
			IEpLayoutComposite shipmentButtonsPane = shippingStatusPane.addTableWrapLayoutComposite(1, true, shipmentButtonsPaneData);
			createReturnButton = shipmentButtonsPane.addPushButton(FulfillmentMessages.get().RAESection_CreateReturnButton, FulfillmentImageRegistry
					.getImage(FulfillmentImageRegistry.IMAGE_RETURN_CREATE), rolePermissionReturn, shipmentButtonsPaneData);
			createReturnButton.addSelectionListener(this);
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
					formatEditorDirtyCreateReturnMessage()) && event.getSource() == createReturnButton) {
				// RCPRAP - get this on the UI thread.
				Display.getDefault().asyncExec(
						() -> {
							CreateReturnElectronicOrderContributionAction createReturnAction = new
									CreateReturnElectronicOrderContributionAction(FulfillmentMessages.EMPTY_STRING, editor,	orderShipment);
							createReturnAction.run();
						});
			}
		}

		/**
		 * Gets the section title.
		 * 
		 * @return string
		 */

		protected String getSectionTitle() {
			return FulfillmentMessages.get().ReturnSection_SubSectionStatus;
		}

}
