/**
 * Copyright (c) Elastic Path Software Inc., 2007-2014
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.cmclient.fulfillment.wizards.ReAuthWizard;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.RecalculableObject;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.order.AllocationEventType;
import com.elasticpath.domain.order.AllocationResult;
import com.elasticpath.domain.order.AllocationStatus;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.order.InvalidUnlockerException;
import com.elasticpath.service.order.OrderLockService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.tax.TaxDocumentModificationContext;
import com.elasticpath.service.tax.TaxDocumentModificationType;

/**
 * Implements a multi-page editor for displaying and editing orders.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass"})
public class OrderEditor extends AbstractCmClientFormEditor {

	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	private static final String HYPHEN = "-"; //$NON-NLS-1$

	private static final String SPACE = " "; //$NON-NLS-1$

	private static final int TOTAL_WORK_UNITS = 3;

	/**
	 * ID of the editor. It is the same as the class name.
	 */
	public static final String ID_EDITOR = OrderEditor.class.getName();

	private Order order;

	private TaxDocumentModificationContext taxDocumentModificationContext;

	private Map<String, OrderAddress> previousOrderShipmentAddresses;

	private OrderService orderService;

	private LockingListener lockingListener;

	private OrderLock orderLock;

	private OrderLockService orderLockService;

	private boolean wasSavePressed;

	private Date openEditorDate;

	private boolean orderLocked;

	private AllocationService allocationService;
	private TimeService timeService;
	private ProductSkuLookup productSkuLookup;
	private PricingSnapshotService pricingSnapshotService;

	/**
	 * The add note event id.
	 */
	public static final int PROP_ADD_NOTE = 909;

	/**
	 * The property id for <code>address or method change</code>.
	 */
	public static final int PROP_ADDR_METHOD_CHANGE = 200;

	/**
	 * The refresh parts event id.
	 */
	public static final int PROP_REFRESH_PARTS = 910;

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		final Long orderUid = input.getAdapter(Long.class);
		this.order = retrieveOrder(orderUid);
		this.taxDocumentModificationContext = ServiceLocator.getService(
																ContextIdNames.TAX_DOCUMENT_MODIFICATION_CONTEXT);
		this.previousOrderShipmentAddresses = buildOrderShipmentAddress();
		this.lockingListener = new LockingListener();
		this.openEditorDate = getTimeService().getCurrentTime();

		OrderEventCmHelper.initForOrderAuditing(order);
		preCalculatePreOrBackOrderQuantity(order);
		this.setPartName(input.getName());
		this.addPropertyListener(this.lockingListener);

		orderLock = getOrderLockService().getOrderLock(order);
		this.orderLocked = orderLock != null;
	}

	/**
	 * Gets the order's lock.
	 *
	 * @return orderLock
	 */
	public OrderLock getOrderLock() {
		return orderLock;
	}

	/**
	 * Sets the order's lock.
	 *
	 * @param orderLock the new order lock value
	 */
	public void setOrderLock(final OrderLock orderLock) {
		this.orderLock = orderLock;
	}

	/**
	 * Sets whether is order locked.
	 *
	 * @param orderLocked boolean
	 */
	public void isOrderLocked(final boolean orderLocked) {
		this.orderLocked = orderLocked;
	}

	/**
	 * Whether the order is locked.
	 *
	 * @return boolean whether the order is locked
	 */
	public boolean isOrderLocked() {
		return this.orderLocked;
	}

	/**
	 * Notifies the page and the section when a note is added.
	 */
	public void fireAddNoteChanges() {
		firePropertyChange(OrderEditor.PROP_ADD_NOTE);
	}

	// ---- DOCfireShipmentAddressMethodChanges
	/**
	 * Notifies the summary section when address and/or service level is changed.
	 */
	public void fireShipmentAddressMethodChanges() {
		firePropertyChange(OrderEditor.PROP_ADDR_METHOD_CHANGE);
		fireRefreshChanges();
	}
	// ---- DOCfireShipmentAddressMethodChanges

	/**
	 * Notifies the page and the section when order detail is changed.
	 */
	public void fireRefreshChanges() {
		firePropertyChange(OrderEditor.PROP_REFRESH_PARTS);
	}

	@Override
	public Order getModel() {
		return order;
	}

	/**
	 * Retrieves the order to be displayed/edited from persistent storage. TODO: Only the information to be displayed on a given tab should be
	 * retrieved. See if there is a better way to do this such that the tab itself causes the order retrieval when the tab is displayed. Also, other
	 * tabs will need to add to the order as they fetch more data.
	 *
	 * @param orderUid the UID of the order to be retrieved.
	 * @return the <code>Order</code>
	 */
	private Order retrieveOrder(final long orderUid) {
		return getOrderService().getOrderDetail(orderUid);
	}

	private OrderService getOrderService() {
		if (orderService == null) {
			orderService = ServiceLocator.getService(ContextIdNames.ORDER_SERVICE);
		}
		return orderService;
	}

	private OrderLockService getOrderLockService() {
		if (orderLockService == null) {
			orderLockService = ServiceLocator.getService(ContextIdNames.ORDER_LOCK_SERVICE);
		}
		return orderLockService;
	}

	// ---- DOCaddPages
	@Override
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	protected void addPages() {
		try {
			addPage(new OrderSummaryPage(this));
			addPage(new OrderDetailPage(this));
			addPage(new OrderPaymentsPage(this));
			addPage(new OrderReturnsPage(this));
			addPage(new OrderNotePage(this));
			addExtensionPages(getClass().getSimpleName(), FulfillmentPlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			throw new RuntimeException(e);
		}
	}
	// ---- DOCaddPages

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(FulfillmentMessages.get().OrderEditor_Save_StatusBarMsg, TOTAL_WORK_UNITS);
		wasSavePressed = true;

		for (OrderSku orderSku : getModel().getOrderSkus()) {
			final ShoppingItemPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForOrderSku(orderSku);

			if (pricingSnapshot.getListUnitPrice() == null && pricingSnapshot.getSaleUnitPrice() == null) {
				MessageDialog.openError(null, FulfillmentMessages.get().OrderDetailsErrorAddingItem_Title_NoPrice,

						NLS.bind(FulfillmentMessages.get().OrderDetailsErrorAddingItem_Message_NoPrice,
						new Object[]{orderSku.getDisplayName(), orderSku.getSkuCode()}));
				monitor.setCanceled(true);
				return;
			}
		}

		int validation = getOrderLockService().validateLock(orderLock, openEditorDate);
		if (validation == OrderLockService.ORDER_IS_LOCKED) {
			showOrderLockedMessage(FulfillmentMessages.get().OrderEditor_SaveOrderErrorTitle,

					NLS.bind(FulfillmentMessages.get().OrderEditor_SaveOrderIsLockedMessage,
					order.getOrderNumber(), LoginManager.getCmUserUsername())
			);
			monitor.setCanceled(true);
			return;
		}
		if (validation == OrderLockService.ORDER_WAS_MODIFIED) {
			showOrderLockedMessage(FulfillmentMessages.get().OrderEditor_SaveOrderErrorTitle,
				NLS.bind(FulfillmentMessages.get().OrderEditor_SaveOrderWasModifiedMessage,
				order.getOrderNumber()));
			monitor.setCanceled(true);
			return;
		}
		if (validation == OrderLockService.ORDER_WAS_UNLOCKED || validation == OrderLockService.LOCK_IS_ALIEN) {
			showOrderLockedMessage(FulfillmentMessages.get().OrderEditor_SaveOrderErrorTitle,
				NLS.bind(FulfillmentMessages.get().OrderEditor_SaveOrderWasUnlockedMessage,
				order.getOrderNumber()));
			monitor.setCanceled(true);
			return;
		}


		// save and don't forget to release isLocked flag
		try {
			if (ReAuthWizard.reAuthIfRequired(this.getModel())) {
				// Allocation/ Deallocation the order
				// Step 1: check inventory
				if (!checkInventory()) {
					return;
				}

				// Step 2: allocate/deallocate inventory
				proceedQuantityAllocation();

				monitor.worked(1);

				/** the order was locked, try to release it now. */
				try {
					this.order = getOrderService().update(order, taxDocumentModificationContext);

					// MSC-7372 & MSC-7303 - when order contains more than 1 orderSku, after update() method calls, not all field are refreshed
					// so repeated call update method throws OptimisticLock Exception
					this.order = getOrderService().get(order.getUidPk());
					OrderEventCmHelper.initForOrderAuditing(order);
					getOrderLockService().releaseOrderLock(orderLock, LoginManager.getCmUser());
					openEditorDate = getOrderService().get(order.getUidPk()).getLastModifiedDate();

					// rebuild the order shipment addresses
					this.previousOrderShipmentAddresses = buildOrderShipmentAddress();

					this.orderLock = null;
				} catch (final InvalidUnlockerException e) {
					showOrderLockedMessage(FulfillmentMessages.get().OrderEditor_UnlockOrderErrorTytle,
						NLS.bind(FulfillmentMessages.get().OrderEditor_UnlockOrderFailedMessage,
						order.getOrderNumber()));

					monitor.setCanceled(true);
					return;
				}
				//refresh is killing all active pointers that listeners hold and
				//creates problems on order details
				refreshEditorPages();
				postUpdateOrder();
				monitor.worked(1);
				FulfillmentEventService.getInstance().fireOrderChangeEvent(new ItemChangeEvent<>(this, order));
			} else {
				saveActionCanceled();
				monitor.setCanceled(true);
			}
		} finally {
			monitor.done();
		}
	}

	/*
	 * A hook method called before the save action.
	 * Disables the recalculation as we do not want the order to be changed after this stage.
	 * The binding framework executes the update to the model in threads which interferes with JPA by
	 * changing the object at the same time the transaction goes on.
	 *
	 * The recalculation will be enabled again after the new updated JPA object is loaded by the editor.
	 * This functionality counts on the fact that the recalculationEnabled field on OrderImpl is true by default.
	 */
	@Override
	protected void beforeSaveAction() {
		// disable the recalculation on each shipments in the order
		for (OrderShipment shipment : this.order.getAllShipments()) {
			((RecalculableObject) shipment).disableRecalculation();
		}
	}

	@Override
	protected void saveActionCanceled() {
		// enable the recalculation on each shipments in the order
		for (OrderShipment shipment : this.order.getAllShipments()) {
			((RecalculableObject) shipment).enableRecalculation();
		}
	}

	@Override
	public int promptToSaveOnClose() {
		final int result = super.promptToSaveOnClose();
		if (result == NO) {
			/** the order was locked, try to release it now. */
			final CmUser cmUser = LoginManager.getCmUser();
			final OrderLock freshOrderLock = getOrderLockService().getOrderLock(order);
			try {
				if (orderLock != null && freshOrderLock != null && freshOrderLock.getCreatedDate() == orderLock.getCreatedDate()) {
					getOrderLockService().releaseOrderLock(orderLock, cmUser);
				}
				this.orderLock = null;
			} catch (final InvalidUnlockerException e) {
				return result;
			}
		}
		return result;
	}

	/*
	 * Step2: Allocate/ de-allocate inventory TODO: (MSC-7585) Move this logic to the domain model
	 */
	private void proceedQuantityAllocation() {
		for (final PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (final OrderSku orderSku : shipment.getShipmentRemovedOrderSku()) {
				orderSku.setShipment(shipment);
				getAllocationService().processAllocationEvent(orderSku, AllocationEventType.ORDER_ADJUSTMENT_REMOVESKU,
						"CM User: " + LoginManager.getCmUserId(), //$NON-NLS-1$
						orderSku.getAllocatedQuantity(), null);
				orderSku.setShipment(null);
			}

			for (final OrderSku orderSku : shipment.getShipmentOrderSkus()) {
				final int quantityIntentToAllocate = orderSku.getQuantity() - orderSku.getAllocatedQuantity() - orderSku.getPreOrBackOrderQuantity();
				AllocationResult result = getAllocationService().processAllocationEvent(orderSku, AllocationEventType.ORDER_ADJUSTMENT_CHANGEQTY,
						"CM User: " + LoginManager.getCmUserId(), //$NON-NLS-1$
						quantityIntentToAllocate, null);
				// this has to be changed so that it is handled in the model
				orderSku.setChangedQuantityAllocated(0);
				if (quantityIntentToAllocate > 0) {
					final int quantityAvailableToAllocate = result.getQuantityAllocatedInStock();
					orderSku.setAllocatedQuantity(orderSku.getAllocatedQuantity() + quantityAvailableToAllocate);
				} else if (quantityIntentToAllocate < 0) {
					orderSku.setAllocatedQuantity(orderSku.getQuantity());
				}
			}
		}
	}

	/*
	 * Step 1: checking inventory Create a HashMap to record all the orderSku including existing ones and removed ones HashMap is constructed as
	 * ProductSku(Key) | InventoryQuantityToChange(Value) If any of product sku is out of stock, the outOfStock flag will turn to true and will pop
	 * up message dialog warning later on else continue to step 2
	 */
	private boolean checkInventory() {
		boolean success = true;
		final HashMap<ProductSku, Integer> productSkuList = new HashMap<>();
		boolean outOfStock = false;
		final StringBuilder insufficientInventoryWarningMessage = new StringBuilder();
		for (final PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (final OrderSku orderSku : shipment.getShipmentOrderSkus()) {
				updateOrAddToProductSkuList(productSkuList, orderSku, false);
			}
			for (final OrderSku orderSku : shipment.getShipmentRemovedOrderSku()) {
				updateOrAddToProductSkuList(productSkuList, orderSku, true);
			}
		}

		for (final Entry<ProductSku, Integer> entry : productSkuList.entrySet()) {
			ProductSku productSku = entry.getKey();
			long warehouse = getFulfillingWarehouse(order);
			AllocationStatus status;
			if (entry.getValue() > 0) {
				status = getAllocationService().getAllocationStatus(productSku, warehouse, entry.getValue());
			} else {
				continue;
			}
			if (status == AllocationStatus.NOT_ALLOCATED) {
				insufficientInventoryWarningMessage.append(productSku.getSkuCode() + SPACE + HYPHEN + SPACE
						+ "Insufficient stock." + NEW_LINE); //$NON-NLS-1$
				outOfStock = true;
			}
		}
		if (outOfStock) {
			MessageDialog.openWarning(this.getSite().getShell(), FulfillmentMessages.get().ShipmentSection_InsufficientInventoryTitle,
					FulfillmentMessages.get().ShipmentSection_InSufficientSkuQuantityWarningHeader + NEW_LINE + NEW_LINE
							+ insufficientInventoryWarningMessage);
			success = false;
		}
		return success;
	}

	private AllocationService getAllocationService() {
		if (allocationService == null) {
			allocationService = ServiceLocator.getService(ContextIdNames.ALLOCATION_SERVICE);
		}
		return allocationService;
	}

	private void updateOrAddToProductSkuList(final Map<ProductSku, Integer> productSkuList, final OrderSku orderSku,
			final boolean fromRemovedOrderSku) {
		int quantityToAdd;
		int quantityToAllocate;
		if (fromRemovedOrderSku) {
			quantityToAdd = -orderSku.getAllocatedQuantity();
		} else {
			quantityToAdd = orderSku.getQuantity() - orderSku.getAllocatedQuantity() - orderSku.getPreOrBackOrderQuantity();
		}

		final ProductSku productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
		if (productSkuList.containsKey(productSku)) {
			quantityToAllocate = productSkuList.get(productSku);
			quantityToAllocate += quantityToAdd;

		} else {
			quantityToAllocate = quantityToAdd;
		}
		productSkuList.put(productSku, quantityToAllocate);
	}

	private void postUpdateOrder() {
		for (final PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			shipment.getShipmentRemovedOrderSku().clear();
		}
		this.taxDocumentModificationContext.clear();
	}

	private void preCalculatePreOrBackOrderQuantity(final Order order) {
		for (final PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (final OrderSku orderSku : shipment.getShipmentOrderSkus()) {
				orderSku.setPreOrBackOrderQuantity(orderSku.getQuantity() - orderSku.getAllocatedQuantity());
			}
		}

	}

	private void showOrderLockedMessage(final String title, final String message) {
		final String[] okButton = {
				JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY),
				JFaceResources.getString(IDialogLabelKeys.CANCEL_LABEL_KEY) };
		final MessageDialog lockWarning = new MessageDialog(this.getSite().getShell(), title, null, message, MessageDialog.WARNING, okButton, 0);
		if (lockWarning.open() == 0) {
			if (orderLock != null) {
				orderLock.setCreatedDate(0L); // not valid lock
			}
			this.close(false);
		}
	}

	/**
	 * This class uses to listen for order modification and lock the order.
	 */
	class LockingListener implements IPropertyListener {

		@Override
		public void propertyChanged(final Object source, final int propId) {
			if (orderLock == null && isDirty() && !wasSavePressed) {
				final Order order = ((OrderEditor) source).getModel();
				orderLock = getOrderLockService().obtainOrderLock(order, LoginManager.getCmUser(), openEditorDate);
				int pageCount = OrderEditor.this.getPageCount();
				for (int ind = 0; ind < pageCount; ind++) {
					AbstractOrderPage editorPage = (AbstractOrderPage) OrderEditor.this.pages.get(ind);
					editorPage.refreshOrderLockedByLabel();
				}
			}
			if (isDirty() && !wasSavePressed) {
				int validation = getOrderLockService().validateLock(orderLock, openEditorDate);
				if (validation == OrderLockService.ORDER_IS_LOCKED) {
					showOrderLockedMessage(FulfillmentMessages.get().OrderEditor_EditOrderErrorTitle,
						NLS.bind(FulfillmentMessages.get().OrderEditor_EditOrderIsLockedMessage,
						order.getOrderNumber(), LoginManager.getCmUser()
						.getUserName()));
				} else if (validation == OrderLockService.ORDER_WAS_MODIFIED) {
					showOrderLockedMessage(FulfillmentMessages.get().OrderEditor_EditOrderErrorTitle,
						NLS.bind(FulfillmentMessages.get().OrderEditor_EditOrderWasModifiedMessage,
						order.getOrderNumber()));
				} else if (validation == OrderLockService.ORDER_WAS_UNLOCKED || validation == OrderLockService.LOCK_IS_ALIEN) {
					showOrderLockedMessage(FulfillmentMessages.get().OrderEditor_EditOrderErrorTitle,
						NLS.bind(FulfillmentMessages.get().OrderEditor_EditOrderWasUnlockedMessage,
						order.getOrderNumber()));
				}
			}
			wasSavePressed = false;
		}
	}

	/**
	 * Return the warehouse that the order should be fulfilled by - relies on the fact that a store can only have a single warehouse at the moment.
	 *
	 * @param order the order to find the
	 * @return the order's store's warehouse.
	 */
	private long getFulfillingWarehouse(final Order order) {
		return order.getStore().getWarehouse().getUidPk();

	}

	@Override
	public void reloadModel() {
		order = orderService.get(order.getUidPk());
		OrderEventCmHelper.initForOrderAuditing(order);
		orderLock = getOrderLockService().getOrderLock(order);
		openEditorDate = getTimeService().getCurrentTime();
		this.orderLocked = orderLock != null;
		FulfillmentEventService.getInstance().fireOrderChangeEvent(new ItemChangeEvent<>(this, getModel()));
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	/**
	 * Returns <code>true</code> if user is authorized to edit the order and the order is not locked, <code>false</code> otherwise.
	 *
	 * @return <code>true</code> if user is authorized to edit the order and the order is not locked.
	 */
	public boolean isAuthorizedAndNotLocked() {
		final AuthorizationService authorizationService = AuthorizationService.getInstance();
		return authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.ORDER_EDIT)
				&& authorizationService.isAuthorizedForStore(order.getStore())
				&& !isOrderLocked();
	}

	/**
	 * Returns <code>true</code> if user is authorized to edit the order and the order is not locked or on hold, <code>false</code> otherwise.
	 *
	 * @return <code>true</code> if user is authorized to edit the order and the order is not locked or on hold.
	 */
	public boolean isAuthorizedAndAvailableForEdit() {
		return isAuthorizedAndNotLocked() && !order.getStatus().equals(OrderStatus.ONHOLD);
	}

	/**
	 * Opens the Warning Dialog with given params if Editor is dirty.
	 *
	 * @param warnTitle the title of dialog
	 * @param warnMessage the message of dialog
	 * @return true if editor is dirty and false otherwise
	 */
	public boolean openDirtyEditorWarning(final String warnTitle, final String warnMessage) {
		if (this.isDirty()) {
			final String[] okButton = { JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY) };
			final MessageDialog unSaved = new MessageDialog(this.getSite().getShell(), warnTitle, null, warnMessage, MessageDialog.WARNING,
					okButton, 0);
			unSaved.open();

		}
		return this.isDirty();
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(FulfillmentMessages.get().OrderEditor_OnSavePrompt,
			getEditorName());
	}
	
	@Override
	protected String getEditorName() {
		return getEditorInput().getName();
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService() {
		if (timeService == null) {
			timeService = ServiceLocator.getService(ContextIdNames.TIME_SERVICE);
		}
		return timeService;
	}

	/**
	 * Remembers the original order shipment tax address, in case any ordershipment changes, the tax can be recorded correctly.
	 */
	private Map<String, OrderAddress> buildOrderShipmentAddress() {
		
		Map<String, OrderAddress> addresses = new HashMap<>();
		
		for (PhysicalOrderShipment orderShipment : order.getPhysicalShipments()) {
			addresses.put(orderShipment.getShipmentNumber(), getClonedAddress(orderShipment.getShipmentAddress()));
		}
		
		for (ElectronicOrderShipment orderShipment : order.getElectronicShipments()) {
			addresses.put(orderShipment.getShipmentNumber(), getClonedAddress(order.getBillingAddress()));
		}
		
		return addresses;
	}
	
	/**
	 * Clones the address to have a unmodified copy of the previous address.
	 */
	private OrderAddress getClonedAddress(final OrderAddress address) {
		OrderAddress clonedAddress = ServiceLocator.getService(ContextIdNames.ORDER_ADDRESS);
		clonedAddress.copyFrom(address);
		return clonedAddress;
	}

	/**
	 * Adds the order shipment for adding.
	 * 
	 * @param orderShipment the new order shipment
	 */
	public void addOrderShipmentToNew(final OrderShipment orderShipment) {
		this.taxDocumentModificationContext.add(orderShipment,
			null,
			TaxDocumentModificationType.NEW);
	}
	
	/**
	 * Adds the order shipment for updating.
	 * 
	 * @param orderShipment the order shipment to update
	 */
	public void addOrderShipmentToUpdate(final OrderShipment orderShipment) {
		
		this.taxDocumentModificationContext.add(orderShipment, 
												previousOrderShipmentAddresses.get(orderShipment.getShipmentNumber()),
												TaxDocumentModificationType.UPDATE);
	}
	
	/**
	 * Adds the order shipment for cancelling.
	 * 
	 * @param orderShipment the order shipment to cancel
	 */
	public void addOrderShipmentToCancel(final OrderShipment orderShipment) {
		this.taxDocumentModificationContext.add(orderShipment, 
												previousOrderShipmentAddresses.get(orderShipment.getShipmentNumber()),
												TaxDocumentModificationType.CANCEL);
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
}
