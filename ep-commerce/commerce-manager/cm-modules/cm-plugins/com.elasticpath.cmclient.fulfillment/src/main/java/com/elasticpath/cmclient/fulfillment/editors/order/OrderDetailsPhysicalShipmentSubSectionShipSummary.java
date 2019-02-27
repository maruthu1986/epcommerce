/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.math.BigDecimal;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.conversion.EpBigDecimalToStringConverter;
import com.elasticpath.cmclient.core.conversion.EpMoneyToStringConverter;
import com.elasticpath.cmclient.core.conversion.EpStringToBigDecimalConverter;
import com.elasticpath.cmclient.core.conversion.EpStringToMoneyConverter;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.PhysicalOrderShipment;

/**
 * Represents the physical shipment shipping summary sub section.
 */
public class OrderDetailsPhysicalShipmentSubSectionShipSummary implements IPropertyListener, ModifyListener { //NOPMD

	private static final int SUMMARY_INFO_COLUMNS = 3;

	private final PhysicalOrderShipment shipment;

	private final boolean editMode;

	private final Order order;

	private Text itemSubTotalCurrencyText;

	private Text itemSubTotalText;

	private Text itemTaxCurrencyText;

	private Text itemTaxText;

	private Text shippingCostCurrencyText;

	private Text shippingCostText;

	private Text shipmentDiscountCurrencyText;

	private Text shipmentDiscountText;

	private Text totalBeforeTaxCurrencyText;

	private Text totalBeforeTaxText;

	private Text shippingTaxCurrencyText;

	private Text shippingTaxText;

	private Text shipmentTotalCurrencyText;

	private Text shipmentTotalText;

	private final AbstractCmClientFormEditor editor;

	private IEpLayoutComposite shipmentSummaryInfoPane;
	
	private IEpLayoutComposite shippingSummaryPane;
	
	private final Composite parent;

	private DataBindingContext bindingContext;
	/**
	 * Constructor.
	 * 
	 * @param shipment the physical shipment
	 * @param editor the editor
	 * @param editMode true if the page is editable
	 * @param mainPane the parent of the parent of the section, the mainPane
	 */
	public OrderDetailsPhysicalShipmentSubSectionShipSummary(final PhysicalOrderShipment shipment, final AbstractCmClientFormEditor editor,
			final boolean editMode, final Composite mainPane) {

		this.shipment = shipment;
		this.editMode = editMode;
		this.order = (Order) editor.getModel();
		this.editor = editor;
		this.parent = mainPane;
	}

	private EpState getStateFromPermissions() {
		EpState state;
		if (editMode) {
			state = EpState.EDITABLE;
		} else {
			state = EpState.READ_ONLY;
		}
		return state;
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
		shippingSummaryPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));
		createShipmentSummaryControls(shippingSummaryPane);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));
		section.setClient(shippingSummaryPane.getSwtComposite());
	}

	private void createShipmentSummaryControls(final IEpLayoutComposite shippingSummaryPane) {
		final IEpLayoutData shippingCostPaneData = shippingSummaryPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, false, false, 2, 1);

		shipmentSummaryInfoPane = shippingSummaryPane.addTableWrapLayoutComposite(SUMMARY_INFO_COLUMNS, false, shippingCostPaneData);
		final IEpLayoutData shipmentSummaryData = shippingSummaryPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER);
		final IEpLayoutData shipmentSummaryCurrencyData = shippingSummaryPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData shipmentSummaryLabelData = shippingSummaryPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);

		shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_ShippingItemTotal, shipmentSummaryLabelData);
		this.itemSubTotalCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
		this.itemSubTotalText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);

		shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ShipmentSection_ShippingCost, shipmentSummaryLabelData);
		this.shippingCostCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
		this.shippingCostText = shipmentSummaryInfoPane.addTextField(getStateFromPermissions(), shipmentSummaryData);

		Label shipmentDiscountLabel = shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ShipmentSection_ShipmentDiscount,
				shipmentSummaryLabelData);
		shipmentDiscountLabel.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
		this.shipmentDiscountCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
		this.shipmentDiscountText = shipmentSummaryInfoPane.addTextField(getStateFromPermissions(), shipmentSummaryData);

		final IEpLayoutData horizontalData1 = shippingSummaryPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, false, 3, 1);
		shipmentSummaryInfoPane.addHorizontalSeparator(horizontalData1);
		
		if (shipment.isInclusiveTax()) {
			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_ShipmentTotal, shipmentSummaryLabelData);
			this.shipmentTotalCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.shipmentTotalText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);
		} else {
			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ShipmentSection_TotalBeforeTax, shipmentSummaryLabelData);
			this.totalBeforeTaxCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.totalBeforeTaxText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);
		}

		shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ShipmentSection_ShippingItemTax, shipmentSummaryLabelData);
		this.itemTaxCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
		this.itemTaxText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);

		shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ShipmentSection_ShipmentTaxes, shipmentSummaryLabelData);
		this.shippingTaxCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
		this.shippingTaxText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);

		if (!shipment.isInclusiveTax()) {
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
		final String orderCurrency = order.getCurrency().toString();

		itemSubTotalCurrencyText.setText(orderCurrency);
		shippingCostCurrencyText.setText(orderCurrency);
		shippingCostText.setText(shipment.getShippingCost().toString());
		shipmentDiscountCurrencyText.setText(orderCurrency);
		shipmentDiscountText.setText(shipment.getSubtotalDiscount().toString());
		
		if (!shipment.isInclusiveTax()) {
			totalBeforeTaxCurrencyText.setText(orderCurrency);
		}
		itemTaxCurrencyText.setText(orderCurrency);
		shippingTaxCurrencyText.setText(orderCurrency);
		shipmentTotalCurrencyText.setText(orderCurrency);
		if (bindingContext != null) {
			bindingContext.updateTargets();
		}
	}

	/**
	 * Perform the bindings between the controls and the domain model.
	 * 
	 * @param bindingContext the binding context
	 */
	protected void bindControls(final DataBindingContext bindingContext) {

		this.bindingContext = bindingContext;
		
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		EpBindingConfiguration bindingConfig = new EpBindingConfiguration(bindingContext, itemSubTotalText, shipment, "subtotal"); //$NON-NLS-1$
		bindingConfig.configureModelToUiBinding(new EpBigDecimalToStringConverter(), UpdatePolicy.UPDATE);
		bindingConfig.configureUiToModelBinding(new EpStringToBigDecimalConverter(), EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, false);
		bindingProvider.bind(bindingConfig);

		bindingConfig = new EpBindingConfiguration(bindingContext, shippingCostText, shipment, "shippingCost"); //$NON-NLS-1$

		// bind shippingCost
		final ObservableUpdateValueStrategy shippingCostUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final BigDecimal newShippingCost = (BigDecimal) value;
				final BigDecimal oldShippingCost = shipment.getShippingCost();
				if (!newShippingCost.equals(oldShippingCost)) {  // NOPMD  '!='
					shipment.setShippingCost(newShippingCost);
				}
				return Status.OK_STATUS;
			}
		};

		bindingConfig.configureUiToModelBinding(new EpStringToBigDecimalConverter(), EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL,
				shippingCostUpdateStrategy, false);
		bindingConfig.configureModelToUiBinding(new EpBigDecimalToStringConverter(), UpdatePolicy.UPDATE);
		bindingProvider.bind(bindingConfig);

		// bind subtotalDiscount
		final ObservableUpdateValueStrategy subtotalDiscountUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final BigDecimal newSubtotalDiscount = (BigDecimal) value;
				final BigDecimal oldSubtotalDiscount = shipment.getSubtotalDiscount();
				if (!newSubtotalDiscount.equals(oldSubtotalDiscount)) {  // NOPMD  '!='
					shipment.setSubtotalDiscount(newSubtotalDiscount);
				}
				return Status.OK_STATUS;
			}
		};

		bindingConfig = new EpBindingConfiguration(bindingContext, shipmentDiscountText, shipment, "subtotalDiscount"); //$NON-NLS-1$
		bindingConfig.configureUiToModelBinding(new EpStringToBigDecimalConverter(), EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL,
				subtotalDiscountUpdateStrategy, false);
		bindingProvider.bind(bindingConfig);

		if (!shipment.isInclusiveTax()) {
			bindingConfig = new EpBindingConfiguration(bindingContext, totalBeforeTaxText, shipment, "totalBeforeTaxMoney"); //$NON-NLS-1$
			bindingConfig.configureModelToUiBinding(new EpMoneyToStringConverter(), UpdatePolicy.UPDATE);
			bindingConfig.configureUiToModelBinding(new EpStringToMoneyConverter(shipment.getOrder().getCurrency()),
				EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, false);
			bindingProvider.bind(bindingConfig);
		}

		bindingConfig = new EpBindingConfiguration(bindingContext, itemTaxText, shipment, "itemTax"); //$NON-NLS-1$
		bindingConfig.configureModelToUiBinding(new EpBigDecimalToStringConverter(), UpdatePolicy.UPDATE);
		bindingConfig.configureUiToModelBinding(new EpStringToBigDecimalConverter(), EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, false);
		bindingProvider.bind(bindingConfig);

		bindingConfig = new EpBindingConfiguration(bindingContext, shippingTaxText, shipment, "shippingTax"); //$NON-NLS-1$
		bindingConfig.configureModelToUiBinding(new EpBigDecimalToStringConverter(), UpdatePolicy.UPDATE);
		bindingConfig.configureUiToModelBinding(new EpStringToBigDecimalConverter(), EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, false);
		bindingProvider.bind(bindingConfig);

		bindingConfig = new EpBindingConfiguration(bindingContext, shipmentTotalText, shipment, "total"); //$NON-NLS-1$
		bindingConfig.configureModelToUiBinding(new EpBigDecimalToStringConverter(), UpdatePolicy.UPDATE);
		bindingConfig.configureUiToModelBinding(new EpStringToBigDecimalConverter(), EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, false);
		bindingProvider.bind(bindingConfig);

		shipmentDiscountText.addModifyListener(this);
		shippingCostText.addModifyListener(this);
		shipmentTotalText.addModifyListener(this);
	}
	
	
	// ---- DOCpropertyChanged
	/**
	 * Notify the section when a specific event has been fired.
	 * 
	 * @param source the event object
	 * @param propId the property id
	 */
	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (!shipmentSummaryInfoPane.getSwtComposite().isDisposed()
				&& (propId == OrderEditor.PROP_ADDR_METHOD_CHANGE || propId == OrderEditor.PROP_REFRESH_PARTS)) {
			populateControls();
		}	
	}
	// ---- DOCpropertyChanged

	/**
	 * Gets the section title.
	 * 
	 * @return string
	 */

	protected String getSectionTitle() {
		return FulfillmentMessages.get().ShipmentSection_SubSectionSummary;
	}

	/**
	 * Handles refreshing of values when a control has been modified.
	 * 
	 * @param event the event
	 */
	@Override
	public void modifyText(final ModifyEvent event) {
		if (event.getSource() == this.shipmentTotalText) {
			this.parent.layout();
		}
		editor.controlModified();
	}

}
