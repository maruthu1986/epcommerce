/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.dialogs;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.warehouse.WarehouseImageRegistry;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.WarehousePermissions;
import com.elasticpath.cmclient.warehouse.WarehousePlugin;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.order.CompleteShipmentFailedException;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.order.ReleaseShipmentFailedException;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Dialog for creating and editing orderShipment.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.GodClass" })
public class CompleteShipmentDialog extends AbstractEpDialog {
	
	private static final int FORCE_COMPLETION_BUTTON_ID = 1000;
	/** This dialog's title. Depends from whether this is create or edit dialog */
	private final String title;

	/** This dialog's image. Depends from whether this is create or edit dialog */
	private final Image image;

	/** This dialog's data binding context. */
	private final DataBindingContext dataBindingContext;

	private final OrderService orderService;

	/** The orderShipment. */
	private PhysicalOrderShipment orderShipment;

	private SelectionContainer selectionContainer;

	private DetailsSectionPart detailsSectionPart;

	private Text shipmentIDText;

	private Text customerIDText;

	private Text shippingAddressText;

	private Text shippingMethodText;

	private Text trackingNumberText;
	
	private Button validateButton;
	
	private Button forceCompletionButton;

	private IEpLayoutComposite mainComposite;
	
	private IEpLayoutComposite notificationComposite;
	
	private Button completeButton;
	
	private final AllocationService allocationService;

	private final ProductInventoryManagementService productInventoryManagementService; 
	private ProductSkuLookup productSkuLookup;
	
	
	/**
	 * The constructor.
	 * 
	 * @param parentShell the parent Shell
	 * @param image the image for this dialog
	 * @param title the title for this dialog
	 */
	public CompleteShipmentDialog(final Shell parentShell, final String title, final Image image) {
		super(parentShell, 1, true);

		this.title = title;
		this.image = image;
		this.orderShipment = null;
		this.dataBindingContext = new DataBindingContext();
		this.orderService = ServiceLocator.getService(ContextIdNames.ORDER_SERVICE);
		this.allocationService = ServiceLocator.getService(ContextIdNames.ALLOCATION_SERVICE);
		this.productInventoryManagementService = ServiceLocator.getService(
				ContextIdNames.PRODUCT_INVENTORY_MANAGEMENT_SERVICE);
	}

	/**
	 * Convenience method to open a create dialog.
	 * 
	 * @param parentShell the parent Shell
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openDialog(final Shell parentShell) {
		CompleteShipmentDialog dialog = new CompleteShipmentDialog(parentShell, WarehouseMessages.get().CompleteShipment_DialogTitle, null);

		return dialog.open() == 0;
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected Image getWindowImage() {
		return image;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite parent) {
		mainComposite = parent.addTableWrapLayoutComposite(1, true, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		selectionContainer = new SelectionContainer();
		selectionContainer.createControls();

		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);

		IManagedForm managedForm = EpControlFactory.getInstance().createManagedForm(mainComposite.getSwtComposite());
		managedForm.getForm().getBody().setLayout(tableWrapLayout);
		managedForm.getForm().setLayoutData(tableWrapData);

		detailsSectionPart = new DetailsSectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(), dataBindingContext);

		managedForm.addPart(detailsSectionPart);

	}

	@Override
	protected String getPluginId() {
		return WarehousePlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return orderShipment;
	}

	@Override
	protected void populateControls() {
		selectionContainer.populateControls();
		detailsSectionPart.populateControls();
	}

	@Override
	protected void bindControls() {
		boolean hideDecorationOnFirstValidation = false;
		
		// create a simple ObservableUpdateValueStrategy target for the trackingNumberText binding
		final ObservableUpdateValueStrategy trackingNumberUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				return Status.OK_STATUS;
			}
		};
		
		// add validation to the trackingNumberText binding
		EpControlBindingProvider.getInstance().bind(dataBindingContext, trackingNumberText,	
				EpValidatorFactory.MAX_LENGTH_255, null, trackingNumberUpdateStrategy, hideDecorationOnFirstValidation);
		
		// create the binding
		EpDialogSupport.create(this, dataBindingContext);

		setComplete(false);
	}

	@Override
	protected void okPressed() {
		dataBindingContext.updateModels();

		try {
			//check if we have enough inventory. If not, throw an error.
			if (insufficientInventoryToCompleteShipment(orderShipment.getShipmentNumber())) {
				MessageDialog.openError(getShell(), WarehouseMessages.get().CompleteShipment_ShipmentCompletionFailedDialogTitle,
						WarehouseMessages.get().CompleteShipment_InsufficientInventory);
				return;
			}
			
			boolean sendConfEmail = true;
			//complete the shipment assuming everything's ok.
			orderService.completeShipment(orderShipment.getShipmentNumber(), trackingNumberText.getText(), 
					true, null, sendConfEmail, getEventOriginator());

			super.okPressed();
		} catch (CompleteShipmentFailedException ex) {
			MessageDialog.openError(getShell(), WarehouseMessages.get().CompleteShipment_ShipmentCompletionFailedDialogTitle,
					WarehouseMessages.get().CompleteShipment_ShipmentCompletionFailedDialogMessage);
			enableForceCompletionButton();
		}
	}

	private boolean insufficientInventoryToCompleteShipment(final String shipmentNumber) {
		OrderShipment orderShipment = orderService.findOrderShipment(shipmentNumber, ShipmentType.PHYSICAL);
		if (orderShipment == null) {
			return false;
		}

		long warehouseUid = orderShipment.getOrder().getStore().getWarehouse().getUidPk();

		for (OrderSku orderSku : orderShipment.getShipmentOrderSkus()) {
			final ProductSku sku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
			if (!allocationService.hasSufficientUnallocatedQty(sku, warehouseUid, orderSku.getQuantity())) {
				if (getProductInventoryManagementService().isSelfAllocationSufficient(orderSku, warehouseUid)) {
					continue;
				}

				return true;
			}
		}

		return false;
	}

	private void enableForceCompletionButton() {
		if (forceCompletionButton != null && !forceCompletionButton.isEnabled()) {
			forceCompletionButton.setEnabled(true);
			notificationComposite.addLabel(WarehouseMessages.get().CompleteShipment_ForceCompletionNotification,
					notificationComposite.createLayoutData());
			getShell().pack();
		}
	}

	@Override
	protected void createEpButtonsForButtonsBar(final ButtonsBarType buttonsBarType, final Composite parent) {
		completeButton = createButton(parent, IDialogConstants.OK_ID, WarehouseMessages.get().CompleteShipment_OkButton, true);
		completeButton.setImage(WarehouseImageRegistry.getImage(WarehouseImageRegistry.IMAGE_COMPLETE_SHIPMENT));
		if (AuthorizationService.getInstance().isAuthorizedWithPermission(WarehousePermissions.WAREHOUSE_FORCE_ORDER_SHIPMENT_COMPLETE)
				&& AuthorizationService.getInstance().isAuthorizedForWarehouse(WarehousePerspectiveFactory.getCurrentWarehouse())) {
			forceCompletionButton = createButton(parent, FORCE_COMPLETION_BUTTON_ID,
					WarehouseMessages.get().CompleteShipment_ForceCompleteButton, false);
			forceCompletionButton.setEnabled(false);
			forceCompletionButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					try {
						boolean sendConfEmail = true;
						orderService.completeShipment(orderShipment.getShipmentNumber(),
								trackingNumberText.getText(), false, null, sendConfEmail, getEventOriginator());
						MessageDialog.openInformation(getShell(), 
								WarehouseMessages.get().CompleteShipment_ShipmentForceCompletionOkDialogTitle,
								WarehouseMessages.get().CompleteShipment_ShipmentForceCompletionOkDialogMessage);
						CompleteShipmentDialog.super.okPressed();
					} catch (ReleaseShipmentFailedException ex) {
						MessageDialog.openError(getShell(), WarehouseMessages.get().CompleteShipment_ShipmentCompletionFailedDialogTitle,
								WarehouseMessages.get().CompleteShipment_ShipmentForceCompletionFailedDialogMessage);
					}
				}
			});
		}
		
		createEpCancelButton(parent);
	}
	
	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}

	private void resetError() {
		setErrorMessage(null);
	}

	/**
	 * Selection components container.
	 */
	private class SelectionContainer {
		private static final int SELECT_SECTION_PART_COLUMN_COUNT = 3;

		private IEpLayoutComposite selectionComposite;
		
		public void createControls() {
			selectionComposite = CompositeFactory.createTableWrapLayoutComposite(mainComposite.getSwtComposite(), SELECT_SECTION_PART_COLUMN_COUNT,
					false);
			selectionComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

			selectionComposite.addLabelBoldRequired(WarehouseMessages.get().CompleteShipment_ShipmentIDText, EpState.EDITABLE, selectionComposite
					.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false));
			shipmentIDText = selectionComposite.addTextField(EpState.EDITABLE, selectionComposite.createLayoutData(IEpLayoutData.FILL,
					IEpLayoutData.BEGINNING, true, false));

			validateButton = selectionComposite.addPushButton(WarehouseMessages.get().CompleteShipment_ValidateButton, WarehouseImageRegistry
					.getImage(WarehouseImageRegistry.IMAGE_VALIDATE_BUTTON), EpState.EDITABLE, selectionComposite.createLayoutData());			
		}

		public void populateControls() {
			validateButton.setEnabled(false);

			shipmentIDText.addModifyListener((ModifyListener) event -> {
				validateButton.setEnabled(shipmentIDText.getText().length() > 0);
				completeButton.setEnabled(false);
				if (null != forceCompletionButton) {
					forceCompletionButton.setEnabled(false);
				}

			});
			shipmentIDText.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(final SelectionEvent event) {
					validatePressed();
				}

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Nothing
				}
				
			});
			validateButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(final SelectionEvent event) {
					// Nothing
				}

				@Override
				public void widgetSelected(final SelectionEvent event) {
					validatePressed();
				}

			});
			
			// pressing enter while entering a shipment ID will also validate and 
			// load the shipment, which simulates pressing of the validate button
			shipmentIDText.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(final KeyEvent event) {
					if (event.character == SWT.CR) {
						validatePressed();
					}
				}
			});
			
			trackingNumberText.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(final KeyEvent event) {
					if (event.character == SWT.CR) {
						okPressed();
					}
				}
			});
			
			selectionComposite.setControlModificationListener(CompleteShipmentDialog.this::resetError);
		}

		private void validatePressed() {
			setComplete(false);
			detailsSectionPart.getSection().setVisible(false);
			
			PhysicalOrderShipment foundShipment = 
					(PhysicalOrderShipment) orderService.findOrderShipment(shipmentIDText.getText(), ShipmentType.PHYSICAL);
			
			if (foundShipment == null) {
				setErrorMessage(WarehouseMessages.get().CompleteShipment_InvalidShipmentIDErrorMessage);
				return;
			}
			
			List<Warehouse> warehouses = foundShipment.getOrder().getStore().getWarehouses();
			AuthorizationService.getInstance().filterAuthorizedWarehouses(warehouses);
			
			if (CollectionUtils.isEmpty(warehouses)) {
				setErrorMessage(WarehouseMessages.get().CompleteShipment_Warehouse_No_Permission);
				return;
			} 

			OrderShipmentStatus shipmentStatus = foundShipment.getShipmentStatus();
			
			if (OrderShipmentStatus.RELEASED.equals(shipmentStatus)) {
				orderShipment = foundShipment;
				detailsSectionPart.repopulate();
				
				setComplete(true);
				detailsSectionPart.getSection().setVisible(true);
				completeButton.setEnabled(true);
			} else {
				setErrorMessage(WarehouseMessages.get().CompleteShipment_InvalidShipmentStateErrorMessage);
			}
		}
	}

	/**
	 * Details section part.
	 */
	private class DetailsSectionPart extends AbstractCmClientFormSectionPart {
		private IEpLayoutComposite mainComposite;

		DetailsSectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
		}

		@Override
		protected String getSectionTitle() {
			return WarehouseMessages.get().CompleteShipment_DetailsSectionPart;
		}

		@Override
		protected void createControls(final Composite clientComposite, final FormToolkit toolkit) {
			mainComposite = CompositeFactory.createTableWrapLayoutComposite(clientComposite, 1, false);
			mainComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

			IEpLayoutComposite dataComposite = mainComposite.addTableWrapLayoutComposite(2, false, mainComposite.createLayoutData(
					IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));
			notificationComposite = mainComposite.addTableWrapLayoutComposite(1, false, mainComposite.createLayoutData(
					IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

			dataComposite.addLabelBold(WarehouseMessages.get().CompleteShipment_CustomerIDText, dataComposite.createLayoutData(IEpLayoutData.END,
					IEpLayoutData.BEGINNING, true, false));
			customerIDText = dataComposite.addTextField(EpState.READ_ONLY, dataComposite.createLayoutData(IEpLayoutData.FILL,
					IEpLayoutData.BEGINNING, true, false));

			dataComposite.addLabelBold(WarehouseMessages.get().CompleteShipment_ShippingAddressText, dataComposite.createLayoutData(IEpLayoutData.END,
					IEpLayoutData.BEGINNING, true, false));
			shippingAddressText = dataComposite.addTextField(EpState.READ_ONLY, dataComposite.createLayoutData(IEpLayoutData.FILL,
					IEpLayoutData.BEGINNING, true, false));

			dataComposite.addLabelBold(WarehouseMessages.get().CompleteShipment_ShippingMethodText, dataComposite.createLayoutData(IEpLayoutData.END,
					IEpLayoutData.BEGINNING, true, false));
			shippingMethodText = dataComposite.addTextField(EpState.READ_ONLY, dataComposite.createLayoutData(IEpLayoutData.FILL,
					IEpLayoutData.BEGINNING, true, false));

			dataComposite.addLabelBold(WarehouseMessages.get().CompleteShipment_TrackingNumberText, dataComposite.createLayoutData(IEpLayoutData.END,
					IEpLayoutData.BEGINNING, true, false));
			trackingNumberText = dataComposite.addTextField(EpState.EDITABLE, dataComposite.createLayoutData(IEpLayoutData.BEGINNING,
					IEpLayoutData.BEGINNING, false, false));

			notificationComposite.addLabel(WarehouseMessages.get().CompleteShipment_OperationNotification, notificationComposite.createLayoutData());

		}

		@Override
		protected void populateControls() {
			getSection().setVisible(false);

			mainComposite.setControlModificationListener(CompleteShipmentDialog.this::resetError);
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// See public parent class bindControls
		}

		private void repopulate() {
			PhysicalOrderShipment physicalOrderShipment = orderShipment;
			final List<ShippingOption> shippingOptions = getShippingOptionService().getAllShippingOptions(
					orderShipment.getOrder().getStore().getCode(),
					orderShipment.getOrder().getLocale()).getAvailableShippingOptions();
			final ShippingOption foundShippingOption = shippingOptions.stream()
					.filter(shippingOption -> shippingOption.getCode().equals(physicalOrderShipment.getShippingOptionCode()))
					.findFirst().get();

			customerIDText.setText(String.valueOf(orderShipment.getOrder().getCustomer().getUidPk()));

			shippingAddressText.setText(orderShipment.getOrder().getCustomer().getFirstName() + WarehouseMessages.SPACE
					+ orderShipment.getOrder().getCustomer().getLastName() + WarehouseMessages.COMMA + WarehouseMessages.SPACE
					+ getBillingAddress(physicalOrderShipment.getShipmentAddress()));

			shippingMethodText.setText(foundShippingOption.getDisplayName(orderShipment.getOrder().getLocale()).orElse(null));

			detailsSectionPart.getSection().pack();
			detailsSectionPart.getManagedForm().getForm().getShell().pack();
		}

		private String getBillingAddress(final OrderAddress orderAddress) {
			if (orderAddress == null) {
				return WarehouseMessages.get().CompleteShipment_NoAddressDefined;
			}
			final StringBuilder addressStr = new StringBuilder();
			final String separator = WarehouseMessages.COMMA + WarehouseMessages.SPACE;
			addressStr.append(orderAddress.getStreet1());
			addressStr.append(separator);
			if (orderAddress.getStreet2() != null 
					&& !orderAddress.getStreet2().equals(WarehouseMessages.EMPTY_STRING)) {
				addressStr.append(orderAddress.getStreet2());
				addressStr.append(separator);
			}
			addressStr.append(orderAddress.getCity());
			addressStr.append(separator);
			addressStr.append(orderAddress.getSubCountry());
			addressStr.append(separator);
			addressStr.append(orderAddress.getZipOrPostalCode());
			addressStr.append(separator);
			addressStr.append(orderAddress.getCountry());

			return addressStr.toString();
		}
	}
	
	/**
	* Gets inventoryService.
	* @return inventoryService - inventoryService.
	*/
	public ProductInventoryManagementService getProductInventoryManagementService() {
		return productInventoryManagementService;
	}

	protected ShippingOptionService getShippingOptionService() {
		return ServiceLocator.getService(ContextIdNames.SHIPPING_OPTION_SERVICE);
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
}

