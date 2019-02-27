/**
 * Copyright (c) Elastic Path Software Inc., 2007-2014
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEditor;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEventCmHelper;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shipping.transformers.PricedShippableItemContainerFromOrderShipmentTransformer;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shoppingcart.OrderSkuSubtotalCalculator;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.shipping.connectivity.commons.constants.ShippingContextIdNames;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItem;
import com.elasticpath.shipping.connectivity.dto.PricedShippableItemContainer;
import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;

/**
 * Represents the UI for move item dialog.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.TooManyFields"})
public class MoveItemDialog extends AbstractEpDialog {

	private static final int MINIMUM_QUANTITY = 1;
	private static final int SCALE_FOR_PROPORTION = 10;

	private Button existingShipmentRadio;

	private Button newShipmentRadio;

	private CCombo addressCombo;

	private CCombo shipmentMethodCombo;

	private CCombo existingShipmentCombo;

	private Spinner orderSkuQuantity;

	private final Order order;

	private List<ShippingOption> shippingOptions;

	private final AbstractCmClientFormEditor editor;

	private final PhysicalOrderShipment physicalOrderShipment;

	private final String shipmentNumber;

	private final PhysicalOrderShipment newShipment;

	private PhysicalOrderShipment existingShipment;

	private final OrderSku orderSku;

	private final List<OrderAddress> orderAddresses; // all the order addresses belongs to the customer who places the order

	private final List<PhysicalOrderShipment> existingShipments; // all except canceled or released shipment

	private final DataBindingContext bindingContext;

	private ProductSkuLookup productSkuLookup;

	private TimeService timeService;

	private PricingSnapshotService pricingSnapshotService;
	private TaxSnapshotService taxSnapshotService;

	private OrderSkuSubtotalCalculator orderSkuSubtotalCalculator;

	private static final String BLANK = " "; //$NON-NLS-1$

	private static final String COMMASEPERATOR = ", "; //$NON-NLS-1$
	private static final String NULLSTRING = ""; //$NON-NLS-1$

	/**
	 * Constructor.
	 *
	 * @param parentShell parent shell
	 * @param editor editor
	 * @param orderSku order sku
	 * @param physicalOrderShipment the current physical shipment
	 * @param shipmentNumber shipment number
	 */
	public MoveItemDialog(final Shell parentShell, final AbstractCmClientFormEditor editor, final OrderSku orderSku,
			final PhysicalOrderShipment physicalOrderShipment, final String shipmentNumber) {
		super(parentShell, 1, false);
		this.editor = editor;
		this.shipmentNumber = shipmentNumber;
		this.order = (Order) editor.getModel();
		this.physicalOrderShipment = physicalOrderShipment;
		this.orderSku = orderSku;
		this.newShipment = ServiceLocator.getService(ContextIdNames.PHYSICAL_ORDER_SHIPMENT);
		this.bindingContext = new DataBindingContext();
		this.orderAddresses = new ArrayList<>();
		this.existingShipments = new ArrayList<>();
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return FulfillmentMessages.get().MoveItem_DialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		return FulfillmentMessages.get().MoveItem_DialogTitle;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_MOVE);
	}

	@Override
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final EpState epStateEdit = EpState.EDITABLE;

		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true);
		final IEpLayoutData compositeLayout = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		final IEpLayoutData existingShippmentLayout = dialogComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, true);

		final IEpLayoutComposite shipmentComposite = dialogComposite.addTableWrapLayoutComposite(2, false, existingShippmentLayout);
		shipmentComposite.addLabelBoldRequired(FulfillmentMessages.get().MoveItem_QuantityToMove, epStateEdit, labelData);
		orderSkuQuantity = shipmentComposite.addSpinnerField(epStateEdit, labelData);
		int maxQuantity = orderSku.getQuantity();
		if (physicalOrderShipment.getShipmentOrderSkus().size() == 1) {
			maxQuantity = orderSku.getQuantity() - 1;
		}
		orderSkuQuantity.setSelection(maxQuantity);
		orderSkuQuantity.setMaximum(maxQuantity);
		orderSkuQuantity.setMinimum(MINIMUM_QUANTITY);

		if (orderSku.getQuantity() < 2) {
			orderSkuQuantity.setSelection(1);
			orderSkuQuantity.setEnabled(false);
		}
		existingShipmentRadio = shipmentComposite.addRadioButton(FulfillmentMessages.get().MoveItem_MoveToExistingShipment, epStateEdit, fieldData);
		// set the label font bold
		existingShipmentRadio.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				addressCombo.setEnabled(false);
				shipmentMethodCombo.setEnabled(false);
				existingShipmentCombo.setEnabled(true);
				addressCombo.setText(FulfillmentMessages.get().MoveItem_SelectAddress);
				shipmentMethodCombo.setText(FulfillmentMessages.get().MoveItem_SelectShippingMethod);
				getOkButton().setEnabled(false);
			}
		});
		existingShipmentCombo = shipmentComposite.addComboBox(epStateEdit, fieldData);
		existingShipmentCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// nothing

			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				int index = existingShipmentCombo.getSelectionIndex();
				if (index >= 0) {
					getOkButton().setEnabled(true);
				}
			}
		});
		newShipmentRadio = shipmentComposite.addRadioButton(FulfillmentMessages.get().MoveItem_CreateNewShipment, epStateEdit, fieldData);
		// set the label font bold
		newShipmentRadio.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// nothing

			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				existingShipmentCombo.setEnabled(false);
				addressCombo.setEnabled(true);
				shipmentMethodCombo.setEnabled(true);
				existingShipmentCombo.setText(FulfillmentMessages.get().MoveItem_SelectShipment);
				getOkButton().setEnabled(false);
			}
		});
		final IEpLayoutComposite addressShippingMethodComposite = dialogComposite.addGroup(null, 2, false, compositeLayout);
		addressShippingMethodComposite.addLabelBoldRequired(FulfillmentMessages.get().MoveItem_Address, epStateEdit, labelData);
		addressCombo = addressShippingMethodComposite.addComboBox(epStateEdit, fieldData);
		addressCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				shipmentMethodCombo.setItems(getShippingMethods(orderAddresses.get(addressCombo.getSelectionIndex())));
				shipmentMethodCombo.setText(FulfillmentMessages.get().MoveItem_SelectShippingMethod);
			}
		});
		addressShippingMethodComposite.addLabelBoldRequired(FulfillmentMessages.get().MoveItem_ShippingMethod, epStateEdit, labelData);
		shipmentMethodCombo = addressShippingMethodComposite.addComboBox(epStateEdit, fieldData);
		shipmentMethodCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				int index = shipmentMethodCombo.getSelectionIndex();
				if (index >= 0) {
					getOkButton().setEnabled(true);
				}
			}
		});

	}

	@Override
	protected String getPluginId() {
		return  FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return order;
	}

	@Override
	@SuppressWarnings("PMD.CyclomaticComplexity")
	protected void populateControls() {
		this.addressCombo.setText(FulfillmentMessages.get().MoveItem_SelectAddress);
		this.existingShipmentCombo.setText(FulfillmentMessages.get().MoveItem_SelectShipment);
		// populate existing shipping combo whose contents exclude cancelled shipment and itself
		int index = 1;
		boolean existOtherNonCancelledShipment = false;
		for (PhysicalOrderShipment shipment : this.order.getPhysicalShipments()) {
			if (!shipment.getShipmentNumber().equals(this.shipmentNumber) && shipment.getShipmentStatus() != OrderShipmentStatus.CANCELLED
					&& shipment.getShipmentStatus() != OrderShipmentStatus.RELEASED
					&& shipment.getShipmentStatus() != OrderShipmentStatus.SHIPPED) {
				this.existingShipments.add(shipment);
				this.existingShipmentCombo.add(FulfillmentMessages.get().ShipmentSection_Title + BLANK + index);
				existOtherNonCancelledShipment = true;
			}
			index++;
		}
		if (existOtherNonCancelledShipment) {
			this.existingShipmentRadio.setSelection(true);
			this.addressCombo.setEnabled(false);
			this.shipmentMethodCombo.setEnabled(false);
		} else {
			// if no other existing shipping address available, then disable move item to existing shipping address option
			this.existingShipmentRadio.setEnabled(false);
			this.existingShipmentCombo.setEnabled(false);
			this.newShipmentRadio.setSelection(true);
		}

		// populate existing address combo, customer addresses
		for (CustomerAddress customerAddress : this.order.getCustomer().getAddresses()) {
			OrderAddress orderAddress = ServiceLocator.getService(ContextIdNames.ORDER_ADDRESS);
			orderAddress.copyFrom(customerAddress);

			this.orderAddresses.add(orderAddress);
			this.addressCombo.add(getAddressLine(orderAddress));

		}
		if (this.orderAddresses.isEmpty()) {
			this.newShipmentRadio.setEnabled(false);
			this.addressCombo.setEnabled(false);
			this.shipmentMethodCombo.setEnabled(false);
		} else {
			this.shipmentMethodCombo.setText(FulfillmentMessages.get().MoveItem_SelectShippingMethod);
		}

	}

	private String getAddressLine(final OrderAddress address) {
		final StringBuilder builder = new StringBuilder();
		builder.append(address.getStreet1());
		if (address.getStreet2() != null && !address.getStreet2().equals(NULLSTRING)) {
			builder.append(BLANK);
			builder.append(address.getStreet2());
		}
		builder.append(COMMASEPERATOR);
		builder.append(address.getCity());
		if (address.getSubCountry() != null && !address.getSubCountry().equals(NULLSTRING)) {
			builder.append(COMMASEPERATOR);
			builder.append(address.getSubCountry());
		}
		builder.append(COMMASEPERATOR);
		builder.append(address.getZipOrPostalCode());
		return builder.toString();
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, "OK", null); //$NON-NLS-1$
		createEpCancelButton(parent);
		this.getOkButton().setEnabled(false);
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		final boolean hideDecorationOnFirstValidation = true;

		final ObservableUpdateValueStrategy existingShipmentOptionUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				int index = (Integer) newValue;
				if (index > -1) {
					existingShipment = existingShipments.get(index);
				}

				return Status.OK_STATUS;

			}
		};

		final ObservableUpdateValueStrategy orderShipmentAddressUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {

				Integer value = (Integer) newValue;
				if (value >= 0) {

					OrderAddress orderAddress = MoveItemDialog.this.orderAddresses.get(value);
					OrderAddress newOrderAddress = ServiceLocator.getService(ContextIdNames.ORDER_ADDRESS);
					newOrderAddress.copyFrom(orderAddress);
					newShipment.setShipmentAddress(newOrderAddress);
				}
				return Status.OK_STATUS;
			}
		};

		final ObservableUpdateValueStrategy orderShipmentMethodUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {

				if (shippingOptions != null && shipmentMethodCombo != null) {
					ShippingOption newShippingOption = shippingOptions.get(shipmentMethodCombo.getSelectionIndex());
					newShipment.setShippingOptionCode(newShippingOption.getCode());
					newShipment.setCarrierCode(newShippingOption.getCarrierCode().orElse(null));
					newShipment.setCarrierName(newShippingOption.getCarrierDisplayName().orElse(null));
					Integer value = (Integer) newValue;
					if (value > -1) {

						newShipment.setShippingOptionName(shipmentMethodCombo.getItem(value));
						newShipment.setShippingOptionCode(shippingOptions.get(value).getCode());  // ?!?!?!
					}
				}

				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(bindingContext, this.existingShipmentCombo, null, null, existingShipmentOptionUpdateStrategy,
			hideDecorationOnFirstValidation);
		bindingProvider.bind(bindingContext, this.addressCombo, null, null, orderShipmentAddressUpdateStrategy, hideDecorationOnFirstValidation);
		bindingProvider.bind(bindingContext, this.shipmentMethodCombo, null, null, orderShipmentMethodUpdateStrategy,
			hideDecorationOnFirstValidation);
	}

	@Override
	protected void okPressed() {
		order.setModifiedBy(getEventOriginator());
		OrderSku newOrderSku = ServiceLocator.getService(ContextIdNames.ORDER_SKU);
		// get a copy of order sku to avoid JPA issue when deleting and adding order sku with same UIDPK
		final ShoppingItemPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForOrderSku(this.orderSku);
		final ShoppingItemTaxSnapshot taxSnapshot = getTaxSnapshotService().getTaxSnapshotForOrderSku(this.orderSku, pricingSnapshot);
		newOrderSku.copyFrom(this.orderSku, getProductSkuLookup(), taxSnapshot);
		final int quantityToMove = orderSkuQuantity.getSelection();
		newOrderSku.setQuantity(quantityToMove);
		final int quantityLeftInOldOrderSku = this.orderSku.getQuantity() - quantityToMove;
		OrderShipment destinationShipment;

		OrderShipment sourceOrderShipment = this.orderSku.getShipment();
		((OrderEditor) editor).addOrderShipmentToUpdate(sourceOrderShipment);

		BigDecimal amountToMove = this.orderSku.getUnitPrice().multiply(BigDecimal.valueOf(quantityToMove));
		BigDecimal amountToMoveProportion = amountToMove.divide(physicalOrderShipment.getSubtotal(), SCALE_FOR_PROPORTION, BigDecimal.ROUND_HALF_UP);
		BigDecimal discountToMove = physicalOrderShipment.getSubtotalDiscount().multiply(amountToMoveProportion).setScale(2,
				RoundingMode.HALF_UP);

		physicalOrderShipment.setSubtotalDiscount(physicalOrderShipment.getSubtotalDiscount().subtract(discountToMove));

		if (this.existingShipmentRadio.getSelection()) {

			allocateQuantity(this.orderSku, newOrderSku, quantityToMove);

			boolean existOrderSku = false;
			for (OrderSku orderSkuItem : existingShipment.getShipmentOrderSkus()) {
				if (orderSkuItem.getSkuCode().equals(newOrderSku.getSkuCode())) {
					orderSkuItem.setQuantity(orderSkuItem.getQuantity() + newOrderSku.getQuantity());
					orderSkuItem.setChangedQuantityAllocated(orderSkuItem.getChangedQuantityAllocated() + newOrderSku.getChangedQuantityAllocated());
					orderSkuItem.setAllocatedQuantity(orderSkuItem.getAllocatedQuantity() + newOrderSku.getAllocatedQuantity());
					existOrderSku = true;
					break;
				}
			}
			if (!existOrderSku) {
				this.existingShipment.addShipmentOrderSku(newOrderSku);
			}
			newOrderSku.setShipment(existingShipment);

			this.orderSku.setQuantity(quantityLeftInOldOrderSku);

			if (quantityLeftInOldOrderSku == 0) {
				this.physicalOrderShipment.removeShipmentOrderSku(this.orderSku, getProductSkuLookup());
			}
			existingShipment.setSubtotalDiscount(existingShipment.getSubtotalDiscount().add(discountToMove));

			((OrderEditor) editor).addOrderShipmentToUpdate(existingShipment);

			((OrderEditor) editor).fireRefreshChanges();
			editor.controlModified();
			destinationShipment = existingShipment;
		} else  { // if (this.newShipmentRadio.getSelection()) MUTUALLY EXCLUSIVE with existingShipmentRadio!
			this.newShipmentRadio.setSelection(true);
			newShipment.setOrder(order);
			newShipment.setInclusiveTax(physicalOrderShipment.isInclusiveTax());

			allocateQuantity(this.orderSku, newOrderSku, quantityToMove);
			this.orderSku.setQuantity(quantityLeftInOldOrderSku);

			this.newShipment.addShipmentOrderSku(newOrderSku);
			newOrderSku.setShipment(newShipment);

			updatePhysicalOrderShipment(newShipment);

			if (quantityLeftInOldOrderSku == 0) {
				this.physicalOrderShipment.removeShipmentOrderSku(this.orderSku, getProductSkuLookup());
			}

			this.order.addShipment(newShipment);
			((OrderEditor) editor).addOrderShipmentToNew(newShipment);

			newShipment.setSubtotalDiscount(discountToMove);

			((OrderEditor) editor).fireRefreshChanges();
			editor.controlModified();
			destinationShipment = newShipment;
		}
		updatePhysicalOrderShipment(physicalOrderShipment);
		// Log the orderSku move event.
		OrderEventCmHelper.getOrderEventHelper().logOrderSkuMoved(destinationShipment, newOrderSku);
		((OrderEditor) editor).fireAddNoteChanges();
		editor.getDataBindingContext().updateTargets();

		super.okPressed();

	}

	private String[] getShippingMethods(final OrderAddress address) {
		shippingOptions = getShippingOptionService().getShippingOptions(
				address,
				order.getStore().getCode(),
				this.order.getLocale()).getAvailableShippingOptions();

		return shippingOptions.stream()
				.map(shippingOption -> shippingOption.getDisplayName(this.order.getLocale()).orElse(null))
				.collect(Collectors.toList())
				.stream().toArray(String[]::new);

	}

	private void updatePhysicalOrderShipment(final PhysicalOrderShipment shipment) {
		shipment.setCreatedDate(getTimeService().getCurrentTime());

		final PricedShippableItemContainerFromOrderShipmentTransformer<PricedShippableItem> shippableItemContainerTransformer = ServiceLocator
				.getService(ContextIdNames.PRICED_SHIPPABLE_CONTAINER_FROM_SHIPMENT_TRANSFORMER);

		final PricedShippableItemContainer<PricedShippableItem> pricedShippableItemContainer = shippableItemContainerTransformer.apply(shipment);

		final List<ShippingOption> shippingOptions = getShippingCalculationService()
				.getPricedShippingOptions(pricedShippableItemContainer).getAvailableShippingOptions();

		final ShippingOption foundShippingOption = shippingOptions.stream()
				.filter(shippingOption -> shippingOption.getCode().equals(shipment.getShippingOptionCode()))
				.findFirst().get();

		shipment.setShippingCost(foundShippingOption.getShippingCost().get().getAmount());
		shipment.setBeforeTaxShippingCost(BigDecimal.ZERO);
		shipment.setSubtotalDiscount(BigDecimal.ZERO);
		shipment.setStatus(physicalOrderShipment.getShipmentStatus());
	}

	private void allocateQuantity(final OrderSku oldOrderSku, final OrderSku newOrderSku, final int quantityToMove) {
		final int quantityLeft = oldOrderSku.getQuantity() - quantityToMove;
		if (quantityLeft <= oldOrderSku.getAllocatedQuantity()) {
			newOrderSku.setAllocatedQuantity(oldOrderSku.getAllocatedQuantity() - quantityLeft);
			newOrderSku.setChangedQuantityAllocated(oldOrderSku.getChangedQuantityAllocated());
			oldOrderSku.setQuantity(quantityLeft);
			oldOrderSku.setAllocatedQuantity(quantityLeft);
			oldOrderSku.setChangedQuantityAllocated(0);
		} else if (quantityLeft <= oldOrderSku.getAllocatedQuantity() + oldOrderSku.getChangedQuantityAllocated()) {
			newOrderSku.setAllocatedQuantity(0);
			newOrderSku.setChangedQuantityAllocated(oldOrderSku.getChangedQuantityAllocated() - (quantityLeft - oldOrderSku.getAllocatedQuantity()));
			oldOrderSku.setQuantity(quantityLeft);
			oldOrderSku.setAllocatedQuantity(oldOrderSku.getAllocatedQuantity());
			oldOrderSku.setChangedQuantityAllocated(quantityLeft - oldOrderSku.getAllocatedQuantity());
		} else {
			newOrderSku.setAllocatedQuantity(0);
			newOrderSku.setChangedQuantityAllocated(0);
			oldOrderSku.setQuantity(quantityLeft);
			oldOrderSku.setAllocatedQuantity(oldOrderSku.getAllocatedQuantity());
			oldOrderSku.setChangedQuantityAllocated(oldOrderSku.getChangedQuantityAllocated());
		}
	}

	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}

	protected ShippingOptionService getShippingOptionService() {
		return ServiceLocator.getService(ContextIdNames.SHIPPING_OPTION_SERVICE);
	}

	protected ShippingCalculationService getShippingCalculationService() {
		return ServiceLocator.getService(ShippingContextIdNames.SHIPPING_CALCULATION_SERVICE);
	}

	/**
	 * Lazy loads a ProductSkuLookup.
	 *
	 * @return a product sku reader.
	 */
	protected ProductSkuLookup getProductSkuLookup() {
		if (productSkuLookup == null) {
			productSkuLookup = new LocalProductSkuLookup();
		}
		return productSkuLookup;
	}

	/**
	 * Get the Time Service.
	 *
	 * @return the time service
	 */
	private TimeService getTimeService() {
		if (timeService == null) {
			timeService = ServiceLocator.getService(ContextIdNames.TIME_SERVICE);
		}
		return timeService;
	}

	/**
	 * Get the pricing snapshot service.
	 *
	 * @return the pricing snapshot service
	 */
	protected PricingSnapshotService getPricingSnapshotService() {
		if (pricingSnapshotService == null) {
			pricingSnapshotService = ServiceLocator.getService(ContextIdNames.PRICING_SNAPSHOT_SERVICE);
		}
		return pricingSnapshotService;
	}


	/**
	 * Get the tax snapshot service.
	 *
	 * @return the tax snapshot service
	 */
	protected TaxSnapshotService getTaxSnapshotService() {
		if (taxSnapshotService == null) {
			taxSnapshotService = ServiceLocator.getService(ContextIdNames.TAX_SNAPSHOT_SERVICE);
		}
		return taxSnapshotService;
	}

	/**
	 * Get the order sku subtotal calculator.
	 *
	 * @return the order sku subtotal calculator
	 */
	protected OrderSkuSubtotalCalculator getOrderSkuSubtotalCalculator() {
		if (orderSkuSubtotalCalculator == null) {
			orderSkuSubtotalCalculator = ServiceLocator.getService(ContextIdNames.ORDER_SKU_SUBTOTAL_CALCULATOR);
		}
		return orderSkuSubtotalCalculator;
	}
}

