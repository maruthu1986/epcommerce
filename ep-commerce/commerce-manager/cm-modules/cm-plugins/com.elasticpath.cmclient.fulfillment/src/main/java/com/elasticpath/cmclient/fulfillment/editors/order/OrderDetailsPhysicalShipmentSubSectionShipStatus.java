/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.order.ReleaseShipmentFailedException;

/**
 * Represents the physical shipment shipping status sub section.
 */
public class OrderDetailsPhysicalShipmentSubSectionShipStatus implements IPropertyListener, SelectionListener {

	private static final String STRING_SPACE = " "; //$NON-NLS-1$

	private static final String STRING_NEW_LINE = "\n"; //$NON-NLS-1$

	private final PhysicalOrderShipment shipment;

	private Text shippingStatusText;
	
	private Text shippingTrackingNumberText;
	
	private Text shippingDateText;

	private Button releaseShipmentButton;

	private Button cancelShipmentButton;

	private final OrderEditor editor;

	private final String shipmentNumber;

	private final OrderEditor orderEditor;

	/**
	 * Constructor.
	 * 
	 * @param shipment the physical shipment
	 * @param editor the editor
	 * @param shipmentNumber shipment number
	 */
	public OrderDetailsPhysicalShipmentSubSectionShipStatus(final PhysicalOrderShipment shipment, final AbstractCmClientFormEditor editor,
			final String shipmentNumber) {

		this.shipment = shipment;
		this.shipmentNumber = shipmentNumber;
		this.editor = (OrderEditor) editor;
		this.orderEditor = (OrderEditor) editor;
	}

	/**
	 * Creates the controls.
	 * 
	 * @param client the composite
	 * @param toolkit the form tool kit
	 */

	protected void createControls(final Composite client, final FormToolkit toolkit) {
		final Section section = toolkit.createSection(client, ExpandableComposite.TITLE_BAR);
		section.setText(FulfillmentMessages.get().ShipmentSection_SubSectionStatus);

		final IEpLayoutComposite shippingStatusPane = CompositeFactory.createTableWrapLayoutComposite(section, 1, false);
		shippingStatusPane.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		createShipmentStatusControls(shippingStatusPane);

		section.setClient(shippingStatusPane.getSwtComposite());

	}

	private void createShipmentStatusControls(final IEpLayoutComposite shippingPane) {
		final IEpLayoutData shippingStatusData = shippingPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);
		final IEpLayoutComposite shipmentStatusPane = shippingPane.addTableWrapLayoutComposite(2, false, shippingStatusData);

		shipmentStatusPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_ShipmentStatus, shippingStatusData);

		final IEpLayoutData ashippingStatusData = shipmentStatusPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutData ashippingTrackingNumber = shipmentStatusPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutData ashippingDate = shipmentStatusPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);

		this.shippingStatusText = shipmentStatusPane.addTextField(EpState.READ_ONLY, ashippingStatusData);
		
		if (this.shipment.getShipmentStatus() == OrderShipmentStatus.SHIPPED) {
			shipmentStatusPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_TrackingNumber, shippingStatusData);
			this.shippingTrackingNumberText = shipmentStatusPane.addTextField(EpState.READ_ONLY, ashippingTrackingNumber);
			
			shipmentStatusPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_ShipmentDate, shippingStatusData);
			this.shippingDateText = shipmentStatusPane.addTextField(EpState.READ_ONLY, ashippingDate);
		}
		
		final IEpLayoutData shipmentButtonsPaneData = shippingPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutComposite shipmentButtonsPane = shippingPane.addTableWrapLayoutComposite(1, true, shipmentButtonsPaneData);
		releaseShipmentButton = shipmentButtonsPane.addPushButton(FulfillmentMessages.get().ShipmentSection_ReleaseShipmentButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_SHIPMENT_RELEASE), getReleaseShipmentButtonState(), shipmentButtonsPaneData);

		releaseShipmentButton.addSelectionListener(this);
		cancelShipmentButton = shipmentButtonsPane.addPushButton(FulfillmentMessages.get().ShipmentSection_CancelShipmentButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_SHIPMENT_CANCEL), getCancelShipmentButtonState(), shipmentButtonsPaneData);
		cancelShipmentButton.addSelectionListener(this);
	}

	/**
	 * Perform the bindings between the controls and the domain model.
	 * 
	 * @param bindingContext the binding context
	 */
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		final EpBindingConfiguration bindingConfig = new EpBindingConfiguration(bindingContext, shippingStatusText,
				shipment, "shipmentStatus"); //$NON-NLS-1$
		bindingProvider.bind(bindingConfig);
	}

	/**
	 * Populate the controls with their initial values.
	 */
	protected void populateControls() {
		this.shippingStatusText.setText(resolveStatusText(this.shipment.getShipmentStatus()));
		
		if (this.shipment.getShipmentStatus() == OrderShipmentStatus.SHIPPED) {
			shippingTrackingNumberText.setText(StringUtils.defaultIfEmpty(shipment.getTrackingCode(), "")); //$NON-NLS-1$
			setShipmentDateText();
		}
	}

	private void setShipmentDateText() {
		if (shipment.getShipmentDate() == null) {
			shippingDateText.setText(""); //$NON-NLS-1$
		} else {
			shippingDateText.setText(shipment.getShipmentDate().toString());
		}
	}
	
	private EpState getReleaseShipmentButtonState() {
		if (orderEditor.isAuthorizedAndAvailableForEdit() && shipment.getShipmentStatus() == OrderShipmentStatus.INVENTORY_ASSIGNED) {
			return EpState.EDITABLE;
		}
		return EpState.READ_ONLY;
	}

	private EpState getCancelShipmentButtonState() {
		if (orderEditor.isAuthorizedAndAvailableForEdit() && shipment.isCancellable()) {
			return EpState.EDITABLE;
		}
		return EpState.READ_ONLY;
	}

	private String resolveStatusText(final OrderShipmentStatus status) {
		return FulfillmentMessages.get().getLocalizedName(status);
	}

	/**
	 * Notify the section when a specific event has been fired.
	 * 
	 * @param source the event object
	 * @param propId the property id
	 */
	@Override
	public void propertyChanged(final Object source, final int propId) {
		// nothing
	}

	/**
	 * Not used.
	 * 
	 * @param event the selection event
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing

	}

	// ---- DOCwidgetSelected

	/**
	 * Invoked when Release Shipment or Cancel Shipment button is clicked.
	 * 
	 * @param event the selection event
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == releaseShipmentButton) {
			if (editor.openDirtyEditorWarning(FulfillmentMessages.get().ShipmentSection_EditorDirtyReleaseShipmentTitle,
					formatEditorDirtyReleaseShipmentMessage())) {
				return;
			}

			boolean okPressed = MessageDialog.openConfirm(editor.getSite().getShell(),
					FulfillmentMessages.get().ShipmentSection_ReleaseShipmentConfirm, formatReleaseShipmentMessage());

			if (!okPressed) {
				return;
			}

			releaseShipmentButton.setEnabled(false);

			try {
				shipment.getOrder().setModifiedBy(getEventOriginator());
				getOrderService().processReleaseShipment(shipment);
			} catch (ReleaseShipmentFailedException e) {
				MessageDialog.openError(editor.getSite().getShell(), FulfillmentMessages.get().ErrorReleasingShipment_Title,
						FulfillmentMessages.get().ErrorReleasingShipment_Message);
				return;
			}

			FulfillmentEventService.getInstance().fireOrderChangeEvent(new ItemChangeEvent<>(this, shipment.getOrder()));
			editor.reloadModel();
			editor.refreshEditorPages();

		} else if (event.getSource() == cancelShipmentButton) {
			if (editor.openDirtyEditorWarning(FulfillmentMessages.get().ShipmentSection_EditorDirtyCancelShipmentTitle,
					FulfillmentMessages.get().ShipmentSection_EditorDirtyCancelShipmentMessage)) {
				return;
			}

			boolean okPressed = MessageDialog.openConfirm(editor.getSite().getShell(), FulfillmentMessages.get().ShipmentSection_CancelShipmentTitle,
					formatCancelShipmentMessage());
			if (!okPressed) {
				return;
			}
			shipment.getOrder().setModifiedBy(getEventOriginator());

			getOrderService().cancelOrderShipment(shipment);

			FulfillmentEventService.getInstance().fireOrderChangeEvent(new ItemChangeEvent<>(this, shipment.getOrder()));
			editor.reloadModel();
			editor.refreshEditorPages();
		}
	}
	// ---- DOCwidgetSelected

	private String formatReleaseShipmentMessage() {
		// [MSC-5291] When attempting to release a shipment for a Guest order - confirmation dialog displays 'null null' instead
		// of First and Last Name
		return FulfillmentMessages.get().ShipmentSection_ReleaseShipmentConfirmMessage + STRING_NEW_LINE + STRING_NEW_LINE
				+ FulfillmentMessages.get().ShipmentSection_CancelShipmentShipment + STRING_SPACE + this.shipmentNumber + STRING_SPACE
				+ FulfillmentMessages.get().ShipmentSection_CancelShipmentTo + STRING_SPACE
				+ shipment.getShipmentAddress().getFirstName() + STRING_SPACE
				+ shipment.getShipmentAddress().getLastName();
	}

	private String formatEditorDirtyReleaseShipmentMessage() {
		return FulfillmentMessages.get().ShipmentSection_EditorDirtyReleaseShipmentMessage1 + STRING_NEW_LINE + STRING_NEW_LINE
				+ FulfillmentMessages.get().ShipmentSection_EditorDirtyReleaseShipmentMessage2;
	}

	private String formatCancelShipmentMessage() {
		final Customer customer = this.editor.getModel().getCustomer();
		return FulfillmentMessages.get().ShipmentSection_CancelShipmentMessage + STRING_NEW_LINE + STRING_NEW_LINE
				+ FulfillmentMessages.get().ShipmentSection_CancelShipmentShipment + STRING_SPACE + shipmentNumber + STRING_SPACE
				+ FulfillmentMessages.get().ShipmentSection_CancelShipmentTo + STRING_SPACE + customer.getFullName();
	}

	private OrderService getOrderService() {
		return (OrderService) ServiceLocator.getService(ContextIdNames.ORDER_SERVICE);
	}

	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}

}
