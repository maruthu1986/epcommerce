/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.commons.util.OrderShipmentComparatorFactory;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.ServiceOrderShipment;

/**
 * A page used within the Order details editor. Represents order's shipments.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class OrderDetailPage extends AbstractOrderPage implements IPropertyListener {

	private final OrderEditor orderEditor;
	
	/**
	 * Constructor.
	 * 
	 * @param editor the form editor
	 */
	public OrderDetailPage(final AbstractCmClientFormEditor editor) {
		super(editor, "productDetails", FulfillmentMessages.get().OrderDetailPage_Title); //$NON-NLS-1$
		orderEditor = (OrderEditor) editor;
		orderEditor.addPropertyListener(this);
	}

	@Override
	// ---- DOCorderdetailaddEditorSections
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		final Order order = (Order) editor.getModel();
		// Create the sections
		
		// populate the physical order shipment section
		List<PhysicalOrderShipment> physicalShipmetsList = new LinkedList<>(order.getPhysicalShipments());
		physicalShipmetsList.sort(OrderShipmentComparatorFactory.getOrderShipmentNumberComparator());
		for (final PhysicalOrderShipment shipment : physicalShipmetsList) {
			managedForm.addPart(new OrderDetailsPhysicalShipmentSectionPart(this, editor, shipment, shipment.getShipmentNumber(),
					getEditMode(shipment)));
		}
		
		
		// populate the e-shipment section of the details page
		List<ElectronicOrderShipment> electronicShipmetsList = new LinkedList<>(order.getElectronicShipments());
		electronicShipmetsList.sort(OrderShipmentComparatorFactory.getOrderShipmentNumberComparator());
		for (final ElectronicOrderShipment shipment : electronicShipmetsList) {
		
			managedForm.addPart(new OrderDetailsElectronicShipmentSectionPart(this, editor, shipment));
		}
		
		// populate the service shipment section of the details page
		List<ServiceOrderShipment> serviceShipmentsList = new LinkedList<>(order.getServiceShipments());
		serviceShipmentsList.sort(OrderShipmentComparatorFactory.getOrderShipmentNumberComparator());
		for (final ServiceOrderShipment shipment : serviceShipmentsList) {
			
			managedForm.addPart(new OrderDetailsRecurringItemsSectionPart(this, editor, shipment));

		}
		

		if (order.getAppliedRules() != null && !order.getAppliedRules().isEmpty()) {
			managedForm.addPart(new OrderDetailsPromotionSectionPart(this, editor));
		}
		
		managedForm.addPart(new OrderDetailsOrderDataSectionPart(this, editor));
		addExtensionEditorSections(editor, managedForm, FulfillmentPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}
	// ---- DOCorderdetailaddEditorSections

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return FulfillmentMessages.get().OrderDetailPage_Form_Title;
	}
	
	@Override
	public void pageDisposed() {
		orderEditor.removePropertyListener(this);
	}

	/**
	 * Invoked when property of this section is changed.
	 * 
	 * @param source the event source
	 * @param propId the specific id
	 */
	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (getManagedForm() != null && propId == OrderEditor.PROP_REFRESH_PARTS) {
			cleanEditorSections(this.getManagedForm());
			this.getEditor().refreshAllDataBindings();
			addEditorSections(this.getEditor(), this.getManagedForm());						
			this.getManagedForm().refresh();
			this.getManagedForm().getForm().getParent().layout(true);
			this.getManagedForm().getForm().getBody().layout(true);

		}

	}

	private void cleanEditorSections(final IManagedForm managedForm) {
		if (managedForm != null) {
			final IFormPart[] parts = managedForm.getParts();
			for (final IFormPart part : parts) {
				part.dispose();
				managedForm.removePart(part);
			}
		}
	}

	private boolean getEditMode(final PhysicalOrderShipment shipment) {		
		
		final OrderShipmentStatus shipmentStatus = shipment.getShipmentStatus();

		return ((OrderEditor) getEditor()).isAuthorizedAndAvailableForEdit() && !shipment.getOrder().isExchangeOrder()
				&& shipmentStatus != OrderShipmentStatus.RELEASED && shipmentStatus != OrderShipmentStatus.SHIPPED
				&& shipmentStatus != OrderShipmentStatus.CANCELLED;

	}
}