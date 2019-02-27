/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.binding.EpBindingConfiguration;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;

/**
 * Represents the physical shipment shipping status sub section.
 */
public class OrderDetailsElectronicShipmentSubSectionStatus implements IPropertyListener {

	private final ElectronicOrderShipment shipment;

	private Text shippingStatusText;

	/**
	 * Constructor.
	 * 
	 * @param shipment the physical shipment
	 */
	public OrderDetailsElectronicShipmentSubSectionStatus(final ElectronicOrderShipment shipment) {
		this.shipment = shipment;
	}

	/**
	 * Creates the controls.
	 * 
	 * @param client the composite
	 * @param toolkit the form tool kit
	 */

	protected void createControls(final Composite client, final FormToolkit toolkit) {
		final Section section = toolkit.createSection(client, ExpandableComposite.TITLE_BAR);
		section.setText(getSectionTitle());

		final IEpLayoutComposite shippingStatusPane = CompositeFactory.createTableWrapLayoutComposite(section, 1, false);
		shippingStatusPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));

		createShipmentStatusControls(shippingStatusPane);

		section.setClient(shippingStatusPane.getSwtComposite());

	}

	private void createShipmentStatusControls(final IEpLayoutComposite shippingPane) {
		final IEpLayoutData shippingStatusData = shippingPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);
		final IEpLayoutComposite shipmentStatusPane = shippingPane.addTableWrapLayoutComposite(2, true, shippingStatusData);
		
		shipmentStatusPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_ShipmentStatus, shippingStatusData);
		final IEpLayoutData ashippingStatusData = shipmentStatusPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);
		this.shippingStatusText = shipmentStatusPane.addTextField(EpState.READ_ONLY, ashippingStatusData);
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
	 * Gets the section title.
	 * 
	 * @return string
	 */

	protected String getSectionTitle() {
		return FulfillmentMessages.get().ShipmentSection_SubSectionStatus;
	}

}
