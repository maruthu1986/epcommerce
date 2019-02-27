/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration.UpdatePolicy;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.conversion.EpBigDecimalToStringConverter;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.domain.order.ElectronicOrderShipment;

/**
 * Represents the physical shipment shipping summary sub section.
 */
public class OrderDetailsElectronicShipmentSubSectionSummary implements IPropertyListener { //NOPMD

	private static final int SUMMARY_INFO_COLUMNS = 3;

	private final ElectronicOrderShipment shipment;

	private Text itemSubTotalCurrencyText;

	private Text itemSubTotalText;

	private Text shipmentDiscountCurrencyText;

	private Text shipmentDiscountText;

	private Text itemTaxCurrencyText;

	private Text itemTaxText;

	private Text shipmentTotalCurrencyText;

	private Text shipmentTotalText;

	private IEpLayoutComposite shipmentSummaryInfoPane;

	private IEpLayoutComposite shippingSummaryPane;

	/**
	 * Constructor.
	 * 
	 * @param shipment the physical shipment
	 */
	public OrderDetailsElectronicShipmentSubSectionSummary(final ElectronicOrderShipment shipment) {
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
		this.shippingSummaryPane = CompositeFactory.createTableWrapLayoutComposite(section, 2, false);
		shippingSummaryPane.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));
		createShipmentSummaryControls(shippingSummaryPane);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));
		section.setClient(shippingSummaryPane.getSwtComposite());
	}

	private void createShipmentSummaryControls(final IEpLayoutComposite shippingSummaryPane) {
		final IEpLayoutData shippingCostPaneData = shippingSummaryPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, false, false, 2, 1);

		shipmentSummaryInfoPane = shippingSummaryPane.addTableWrapLayoutComposite(SUMMARY_INFO_COLUMNS, false, shippingCostPaneData);
		final IEpLayoutData shipmentSummaryData = shippingSummaryPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER);
		final IEpLayoutData shipmentSummaryCurrencyData = shippingSummaryPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData shipmentSummaryLabelData = shippingSummaryPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);

		shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_ShippingItemTotal, shipmentSummaryLabelData);
		this.itemSubTotalCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
		this.itemSubTotalText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);

		Label shipmentDiscountLabel = shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ShipmentSection_ShipmentDiscount,
				shipmentSummaryLabelData);
		shipmentDiscountLabel.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
		this.shipmentDiscountCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
		this.shipmentDiscountText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);

		//Different UI for inclusive or exclusive tax price to give the correct and clear information for CSR
		if (shipment.isInclusiveTax()) {
			final IEpLayoutData horizontalData2 = shippingSummaryPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, false, 3, 1);
			shipmentSummaryInfoPane.addHorizontalSeparator(horizontalData2);

			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_ShipmentTotal, shipmentSummaryLabelData);
			this.shipmentTotalCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.shipmentTotalText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);
			
			//the item tax is already included in Item sub-total, so display the tax after total price
			shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ShipmentSection_ShippingItemTax, shipmentSummaryLabelData);
			this.itemTaxCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.itemTaxText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);

		} else {
									
			shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ShipmentSection_ShippingItemTax, shipmentSummaryLabelData);
			this.itemTaxCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.itemTaxText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);
			
			final IEpLayoutData horizontalData2 = shippingSummaryPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, false, 3, 1);
			shipmentSummaryInfoPane.addHorizontalSeparator(horizontalData2);

			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_ShipmentTotal, shipmentSummaryLabelData);
			this.shipmentTotalCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.shipmentTotalText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);
		}
		

				
	}

	/**
	 * Populate the controls with their initial values.
	 */
	protected void populateControls() {
		final String orderCurrency = shipment.getOrder().getCurrency().toString();

		itemSubTotalCurrencyText.setText(orderCurrency);
		itemTaxCurrencyText.setText(orderCurrency);
		shipmentTotalCurrencyText.setText(orderCurrency);
		shipmentDiscountCurrencyText.setText(orderCurrency);
		shipmentDiscountText.setText(shipment.getSubtotalDiscount().toString());
	}

	/**
	 * Perform the bindings between the controls and the domain model.
	 * 
	 * @param bindingContext the binding context
	 */
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		EpBindingConfiguration bindingConfig = new EpBindingConfiguration(bindingContext, itemSubTotalText, shipment, "itemSubtotal"); //$NON-NLS-1$
		bindingConfig.configureModelToUiBinding(new EpBigDecimalToStringConverter(), UpdatePolicy.UPDATE);
		bindingProvider.bind(bindingConfig);

		bindingConfig = new EpBindingConfiguration(bindingContext, itemTaxText, shipment, "itemTax"); //$NON-NLS-1$
		bindingConfig.configureModelToUiBinding(new EpBigDecimalToStringConverter(), UpdatePolicy.UPDATE);
		bindingProvider.bind(bindingConfig);

		bindingConfig = new EpBindingConfiguration(bindingContext, shipmentTotalText, shipment, "total"); //$NON-NLS-1$
		bindingConfig.configureModelToUiBinding(new EpBigDecimalToStringConverter(), UpdatePolicy.UPDATE);
		bindingProvider.bind(bindingConfig);
	}

	/**
	 * Notify the section when a specific event has been fired.
	 * 
	 * @param source the event object
	 * @param propId the property id
	 */
	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (propId == OrderEditor.PROP_ADDR_METHOD_CHANGE) {
			// FIXME: this is not a correct implementation, see MSC-5087 for one of the issues
			populateControls();
		}

	}

	// ---- DOCOrderDetailsElectronicShipmentSubSectionSummarygetSectionTitle

	/**
	 * Gets the section title.
	 * 
	 * @return string
	 */

	protected String getSectionTitle() {
		return FulfillmentMessages.get().ShipmentSection_SubSectionSummary;

   // ---- DOCOrderDetailsElectronicShipmentSubSectionSummarygetSectionTitle
	}
}
