/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;

/**
 * Represents the UI of an order shipment.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class OrderDetailsPhysicalShipmentSectionPart extends AbstractCmClientEditorPageSectionPart implements SelectionListener, IPropertyListener  {

	private final PhysicalOrderShipment shipment;

	private final AbstractCmClientFormEditor editor;

	private final String shipmentNumber;

	private IEpLayoutComposite mainPane;

	private final boolean editMode;

	private OrderDetailsPhysicalShipmentSubSectionItem item;

	private OrderDetailsPhysicalShipmentSubSectionShippingInfo shipInfo;

	private OrderDetailsPhysicalShipmentSubSectionShipSummary summary;

	private OrderDetailsPhysicalShipmentSubSectionShipStatus status;
	/**
	 * Constructor.
	 * 
	 * @param formPage the formpage
	 * @param editor the CmClientFormEditor that contains the form
	 * @param shipment the order shipment that this section represents
	 * @param shipmentNumber the shipment number
	 * @param editMode if the section part should be enabled
	 */
	public OrderDetailsPhysicalShipmentSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor,
			final PhysicalOrderShipment shipment, final String shipmentNumber, final boolean editMode) {
		super(formPage, editor, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.editor = editor;
		this.shipment = shipment;
		this.shipmentNumber = shipmentNumber;
		this.editMode = editMode;
		editor.addPropertyListener(this);
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {

		mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 1, false);

		final IEpLayoutComposite topTwoPane = CompositeFactory.createTableWrapLayoutComposite(mainPane.getSwtComposite(), 1, false);

		item = new OrderDetailsPhysicalShipmentSubSectionItem(shipment, editor, shipmentNumber, editMode);
		item.createControls(topTwoPane.getSwtComposite(), toolkit);

		shipInfo = new OrderDetailsPhysicalShipmentSubSectionShippingInfo(shipment, editor, editMode, topTwoPane.getSwtComposite());
		shipInfo.createControls(topTwoPane.getSwtComposite(), toolkit);
		shipInfo.populateControls();
		shipInfo.bindControl(editor.getDataBindingContext());

		final IEpLayoutComposite bottomTwoPane = CompositeFactory.createTableWrapLayoutComposite(mainPane.getSwtComposite(), 3, false);

		summary = new OrderDetailsPhysicalShipmentSubSectionShipSummary(shipment, editor, editMode, mainPane.getSwtComposite());
		summary.createControls(bottomTwoPane.getSwtComposite(), toolkit);
		summary.populateControls();
		summary.bindControls(editor.getDataBindingContext());
		status = new OrderDetailsPhysicalShipmentSubSectionShipStatus(shipment, editor, shipmentNumber);
		status.createControls(bottomTwoPane.getSwtComposite(), toolkit);
		status.populateControls();
		status.bindControls(editor.getDataBindingContext());

		final OrderDetailsPhysicalShipmentSubSectionReturnAndExchange returnsSection =
			new OrderDetailsPhysicalShipmentSubSectionReturnAndExchange(shipment, editor);
		returnsSection.createControls(bottomTwoPane.getSwtComposite(), toolkit);
	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.get().ShipmentSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		final OrderAddress address = shipment.getShipmentAddress();
		final StringBuilder title = new StringBuilder();
		title.append(FulfillmentMessages.get().ShipmentSection_Title).append(' ');
		title.append(shipmentNumber).append(' ');
		title.append(FulfillmentMessages.get().ShipmentSection_To).append(' ');
		title.append(address.getFirstName()).append(' ');
		title.append(address.getLastName());
		title.append(" - ").append(resolveStatusText(shipment.getShipmentStatus())); //$NON-NLS-1$

		if (shipment.getShipmentStatus() == OrderShipmentStatus.SHIPPED) {

			if (!StringUtils.isBlank(shipment.getTrackingCode())) {
				title.append(" - ").append(shipment.getTrackingCode()); //$NON-NLS-1$
			}

			if (shipment.getShipmentDate() != null) {
				title.append(" - ").append(shipment.getShipmentDate()); //$NON-NLS-1$
			}
		}

		return title.toString();
	}

	@Override
	protected void populateControls() {
		// nothing
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		//nothing

	}

	private String resolveStatusText(final OrderShipmentStatus status) {
		return FulfillmentMessages.get().getLocalizedName(status);
	}

	/**
	 * Not used.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// Nothing

	}

	/**
	 * Invoked on selection event.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		// Nothing

	}

	@Override
	public void dispose() {
		getSection().dispose();
		super.dispose();
	}

	/**
	 * Notify the section when a specific event has been fired.
	 * 
	 * @param source the event object
	 * @param propId the property id
	 */
	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (propId == OrderEditor.PROP_REFRESH_PARTS) {
			item.propertyChanged(source, propId);
			shipInfo.propertyChanged(source, propId);
			summary.propertyChanged(source, propId);
			status.propertyChanged(source, propId);
		}
	}

}
