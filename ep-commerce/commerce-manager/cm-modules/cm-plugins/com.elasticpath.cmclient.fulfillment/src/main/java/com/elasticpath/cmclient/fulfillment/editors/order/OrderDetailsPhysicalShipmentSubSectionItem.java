/**
 * Copyright (c) Elastic Path Software Inc., 2007-2014
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.catalog.editors.product.ProductEditor;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration.ValidationErrorLocation;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.conversion.EpStringToBigDecimalConverter;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.core.helpers.PriceHelper;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.dialog.SkuFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.AbstractInlineEditingSupport;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.ProductSkuChecker;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.InlinePriceEditingSupport;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.MoveItemDialog;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.OrderItemFieldValueDialog;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.OrderSkuComparatorFactory;
import com.elasticpath.domain.RecalculableObject;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.AllocationStatus;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.tax.TaxCodeRetriever;

/**
 * Represents the physical shipment item sub section.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveImports", "PMD.TooManyMethods", "PMD.ExcessiveClassLength", 
	"PMD.GodClass", "PMD.PrematureDeclaration" })
public class OrderDetailsPhysicalShipmentSubSectionItem implements IPropertyListener, SelectionListener {

	private static final Logger LOG = Logger.getLogger(OrderDetailsPhysicalShipmentSubSectionItem.class);

	private static final String STRING_MINUS = " - "; //$NON-NLS-1$

	private static final String STRING_NEW_LINE = "\n"; //$NON-NLS-1$

	private static final String SPACE = " "; //$NON-NLS-1$

	private static final int ORDER_SKU_TABLE_HEIGHT = 200;

	private static final int COLUMN_WIDTH_BUNDLE_NAME = 120;
	
	private static final int COLUMN_WIDTH_INVENTORY_STATUS = 100;

	private static final int COLUMN_WIDTH_SKU_CODE = 120;

	private static final int COLUMN_WIDTH_PRODUCT_NAME = 120;

	private static final int COLUMN_WIDTH_SKU_OPTION = 80;

	private static final int COLUMN_WIDTH_LIST_PRICE = 80;

	private static final int COLUMN_WIDTH_TOTAL_PRICE = 80;

	private static final int COLUMN_WIDTH_SALE_PRICE = 80;

	private static final int COLUMN_WIDTH_QUANTITY = 60;

	private static final int COLUMN_WIDTH_DISCOUNT = 80;

	private static final String ORDER_DETAILS_PHYSICAL_SHIPMENT_TABLE = "Order Details Physical Shipment Table"; //$NON-NLS-1$

	private final boolean editMode;

	private IEpTableViewer shipmentOrderSkuTable;

	private Button moveItemButton;

	private Button removeItemButton;

	private Button addItemButton;

	private Button openProductButton;
	
	private Button editItemAttributes;

	private final PhysicalOrderShipment shipment;

	private final AbstractCmClientFormEditor editor;

	private final String shipmentNumber;
	
	private AllocationService allocationService;

	private ProductSkuLookup productSkuLookup;

	private TaxCodeRetriever taxCodeRetriever;

	private TimeService timeService;

	private PricingSnapshotService pricingSnapshotService;

	/**
	 * Constructor.
	 * 
	 * @param shipment the physical shipment
	 * @param editor the editor
	 * @param shipmentNumber the shipment number
	 * @param editMode if the section item should be editable
	 */
	public OrderDetailsPhysicalShipmentSubSectionItem(final PhysicalOrderShipment shipment, 
			final AbstractCmClientFormEditor editor,
			final String shipmentNumber, final boolean editMode) {

		this.shipmentNumber = shipmentNumber;
		this.shipment = shipment;
		this.editMode = editMode;
		this.editor = editor;
		editor.addPropertyListener(this);
	}

	/**
	 * Creates the item table and buttons.
	 * 
	 * @param client the composite
	 * @param toolkit the form tool kit
	 */

	protected void createControls(final Composite client, final FormToolkit toolkit) {
		final Section section = toolkit.createSection(client, ExpandableComposite.TITLE_BAR);
		section.setText(getSectionTitle());

		final IEpLayoutComposite tablePane = CompositeFactory.createTableWrapLayoutComposite(section, 2, false);
		tablePane.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));
		createOrderSkuTableControl(tablePane);
		createTableButtons(tablePane);

		shipmentOrderSkuTable.setContentProvider(new OrderSkuContentProvider());
		shipmentOrderSkuTable.setInput(this.shipment);

		section.setClient(tablePane.getSwtComposite());
	}

	private EpState getStateFromPermissions() {
		return editMode ? EpState.EDITABLE : EpState.READ_ONLY;
	}

	private AllocationService getAllocationService() {
		if (allocationService == null) {
			allocationService = ServiceLocator.getService(ContextIdNames.ALLOCATION_SERVICE);
		}
		return allocationService;
	}
	
	/**
	 * Lazy loads the ProductSkuLookup.
	 * @return the ProductSkuLookup
	 */
	protected ProductSkuLookup getProductSkuLookup() {
		if (productSkuLookup == null) {
			productSkuLookup = new LocalProductSkuLookup();
		}
		return productSkuLookup;
	}
	
	private OrderSku getSelectedOrderSku() {
		return (OrderSku) ((IStructuredSelection) shipmentOrderSkuTable.getSwtTableViewer().getSelection()).getFirstElement();
	}
	
	private void createOrderSkuTableControl(final IEpLayoutComposite mainPane) {
		final IEpLayoutData tableData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		shipmentOrderSkuTable = mainPane.addTableViewer(false, getStateFromPermissions(), tableData, ORDER_DETAILS_PHYSICAL_SHIPMENT_TABLE);
		shipmentOrderSkuTable.getSwtTable().setLinesVisible(true);

		((TableWrapData) shipmentOrderSkuTable.getSwtTable().getLayoutData()).maxHeight = ORDER_SKU_TABLE_HEIGHT;
		shipmentOrderSkuTable.getSwtTable().addSelectionListener(getSkuSelectionAdapter());
		
		final IEpTableColumn bundleNameColumn = shipmentOrderSkuTable.addTableColumn(FulfillmentMessages.get().ShipmentSection_BundleName,
				COLUMN_WIDTH_BUNDLE_NAME);
		bundleNameColumn.setLabelProvider(getBundleNameLabelProvider());
		
		final IEpTableColumn inventoryStatusColumn = shipmentOrderSkuTable.addTableColumn(FulfillmentMessages.get().ShipmentSection_InventoryStatus,
				COLUMN_WIDTH_INVENTORY_STATUS);		
		inventoryStatusColumn.setLabelProvider(getInventoryStatusColumnLabelProvider());
		final IEpTableColumn skuCodeColumn = shipmentOrderSkuTable
				.addTableColumn(FulfillmentMessages.get().ShipmentSection_SkuCode, COLUMN_WIDTH_SKU_CODE);
		skuCodeColumn.setLabelProvider(getSkuCodeLabelProvider());
		final IEpTableColumn productNameColumn = shipmentOrderSkuTable.addTableColumn(FulfillmentMessages.get().ShipmentSection_ProductName,
				COLUMN_WIDTH_PRODUCT_NAME);
		productNameColumn.setLabelProvider(getDisplayNameLabelProvider());

		final IEpTableColumn skuOptionColumn = shipmentOrderSkuTable.addTableColumn(FulfillmentMessages.get().ShipmentSection_SkuOption,
				COLUMN_WIDTH_SKU_OPTION);
		skuOptionColumn.setLabelProvider(getSkuOptionLabelProvider());

		final IEpTableColumn skuListPriceColumn = shipmentOrderSkuTable.addTableColumn(FulfillmentMessages.get().ShipmentSection_ListPrice,
				COLUMN_WIDTH_LIST_PRICE);
		skuListPriceColumn.setLabelProvider(getListPriceLabelProvider());
		
		final IEpTableColumn skuUnitPriceColumn = shipmentOrderSkuTable.addTableColumn(FulfillmentMessages.get().ShipmentSection_SalePrice,
				COLUMN_WIDTH_SALE_PRICE);
		skuUnitPriceColumn.setLabelProvider(getUnitPriceLabelProvider());

		final IEpTableColumn skuQuantityColumn = shipmentOrderSkuTable.addTableColumn(FulfillmentMessages.get().ShipmentSection_Quantity,
				COLUMN_WIDTH_QUANTITY);
		skuQuantityColumn.setLabelProvider(getQuantityLabelProvider());
		if (editMode) {
			skuQuantityColumn.setEditingSupport(new InlineQuantityEditingSupport(shipmentOrderSkuTable.getSwtTableViewer(), editor
					.getDataBindingContext()));
		}

		final IEpTableColumn skuDiscountColumn
				= shipmentOrderSkuTable.addTableColumn(FulfillmentMessages.get().ShipmentSection_Discount, COLUMN_WIDTH_DISCOUNT);
		skuDiscountColumn.setLabelProvider(getDiscountLabelProvider());
		if (editMode) {
			skuDiscountColumn.setEditingSupport(new InlineDiscountEditingSupport(shipmentOrderSkuTable.getSwtTableViewer(), editor
					.getDataBindingContext()));
		}
		final IEpTableColumn skuTotalPriceColumn
				= shipmentOrderSkuTable.addTableColumn(FulfillmentMessages.get().ShipmentSection_TotalPrice, COLUMN_WIDTH_TOTAL_PRICE);
		skuTotalPriceColumn.setLabelProvider(getTotalPriceLabelProvider());
		if (editMode) {
			skuTotalPriceColumn.setEditingSupport(
					new InlineOrderShipmentPriceEditingSupport(shipmentOrderSkuTable, shipment, editor.getDataBindingContext()));
		}
		mainPane.setControlModificationListener(editor);
	}

	private ColumnLabelProvider getTotalPriceLabelProvider() {
		return new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final OrderSku orderSku = (OrderSku) element;
				ShoppingItemPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForOrderSku(orderSku);
				BigDecimal unitPrice = pricingSnapshot.getPriceCalc().forUnitPrice().getAmount();
				BigDecimal invoicePrice;
				if (unitPrice == null) {
					return ""; //$NON-NLS-1$
				}
				if (pricingSnapshot.getDiscount() == null || pricingSnapshot.getDiscount().getAmount() == null) {
					invoicePrice = unitPrice.multiply(new BigDecimal(orderSku.getQuantity()));
				} else {
					BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(orderSku.getQuantity()));
					BigDecimal totalDiscount = pricingSnapshot.getDiscount().getAmount();
					invoicePrice = totalPrice.subtract(totalDiscount);
				}
				return invoicePrice.toString();
			}
			@Override
			public Image getImage(final Object element) {
				if (editMode) {
					return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
				}
				return null;
			}
		};
	}

	private ColumnLabelProvider getDiscountLabelProvider() {
		return new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final OrderSku orderSku = (OrderSku) element;
				ShoppingItemPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForOrderSku(orderSku);

				if (pricingSnapshot.getDiscount() == null || pricingSnapshot.getDiscount().getAmount() == null) {
					return ""; //$NON-NLS-1$
				}
				return pricingSnapshot.getDiscount().getAmount().toString();
			}

			@Override
			public Image getImage(final Object element) {
				if (editMode) {
					return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
				}
				return null;
			}
		};
	}

	private ColumnLabelProvider getQuantityLabelProvider() {
		return new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final OrderSku orderSku = (OrderSku) element;
				return Integer.toString(orderSku.getQuantity());
			}

			@Override
			public Image getImage(final Object element) {
				if (editMode) {
					return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
				}
				return null;
			}
		};
	}

	private ColumnLabelProvider getUnitPriceLabelProvider() {
		return new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final OrderSku orderSku = (OrderSku) element;				
				BigDecimal unitPrice = orderSku.getUnitPrice();
				if (unitPrice == null) {
					return ""; //$NON-NLS-1$
				}
				return unitPrice.toString();
			}

		};
	}

	private ColumnLabelProvider getListPriceLabelProvider() {
		return new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final OrderSku orderSku = (OrderSku) element;
				ShoppingItemPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForOrderSku(orderSku);

				Money listUnitPrice = pricingSnapshot.getListUnitPrice();
				if (listUnitPrice == null) {
					return ""; //$NON-NLS-1$
				}
				return listUnitPrice.getAmount().toString();
			}
		};
	}

	private ColumnLabelProvider getSkuOptionLabelProvider() {
		return new SkuOptionColumnLabelProvider();
	}

	private ColumnLabelProvider getDisplayNameLabelProvider() {
		return new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final OrderSku orderSku = (OrderSku) element;
				return orderSku.getDisplayName();
			}
		};
	}

	private ColumnLabelProvider getSkuCodeLabelProvider() {
		return new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final OrderSku orderSku = (OrderSku) element;
				return orderSku.getSkuCode();
			}
		};
	}

	private SelectionAdapter getSkuSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				
				final OrderSku orderSku = getSelectedOrderSku();
				
				if (orderSku != null && editMode) {	
					turnOnOrOffMoveItemButton(orderSku);
					turnOnOrOffRemoveItemButton(orderSku);
				}
				ProductSku productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
				if (AuthorizationService.getInstance().isAuthorizedForAnyProductCatalog(productSku.getProduct())) {
					if (!shipment.getOrder().getStatus().equals(OrderStatus.COMPLETED)) {
						openProductButton.setEnabled(true);						
					}					
					editItemAttributes.setEnabled(true);
				}
			}
		};
	}

	private ColumnLabelProvider getBundleNameLabelProvider() {
		return new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final OrderSku rootOrderSku = ((OrderSku) element).getRoot();
				if (rootOrderSku == null) {
					return emptyString();
				}

				final ProductSku productSku = getProductSkuLookup().findByGuid(rootOrderSku.getSkuGuid());
				final Product product = productSku.getProduct();
				if (!isBundle(product)) {
					return emptyString();
				}

				final Order order = (Order) editor.getModel();
				return product.getDisplayName(order.getLocale());
			}

			private String emptyString() {
				return ""; //$NON-NLS-1$;
			}

			private boolean isBundle(final Product product) {
				return product instanceof ProductBundle;
			}
		};
	}

	private ColumnLabelProvider getInventoryStatusColumnLabelProvider() {
		return new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				String statusText;
				final OrderSku orderSku = (OrderSku) element;
				if (orderSku.getShipment().getShipmentStatus() == OrderShipmentStatus.CANCELLED) {
					statusText = null;
				} else if (orderSku.isAllocated()) {
					statusText = FulfillmentMessages.get().ShipmentSection_InventoryAllocated;
				} else {
					statusText = FulfillmentMessages.get().ShipmentSection_WaitingForAllocation;
				}
				return statusText;
			}

			@Override
			public String getToolTipText(final Object element) {
				return FulfillmentMessages.get().ShipmentSection_InventoryAllocated;
			}
		};
	}

	/**
	 * Inline editing support for item quantity.
	 */
	private class InlineQuantityEditingSupport extends AbstractInlineEditingSupport {

		private final InlineQuantityEditorActivationlistenerHelper activationListener = new InlineQuantityEditorActivationlistenerHelper();
		
		/**
		 * Doc.
		 */
		private class InlineQuantityEditorActivationlistenerHelper extends ColumnViewerEditorActivationListenerHelper {

			private final OrderSku oldOrderSku = ServiceLocator.getService(ContextIdNames.ORDER_SKU);
			private final ProductInventoryManagementService productInventoryManagementService =
				ServiceLocator.getService(ContextIdNames.PRODUCT_INVENTORY_MANAGEMENT_SERVICE);

			@Override
			public void afterEditorActivated(final ColumnViewerEditorActivationEvent event) {
				super.afterEditorActivated(event);

				final ViewerCell viewCell = (ViewerCell) event.getSource();
				final OrderSku orderSku = (OrderSku) viewCell.getElement();
				oldOrderSku.setQuantity(orderSku.getQuantity());
				oldOrderSku.setChangedQuantityAllocated(orderSku.getChangedQuantityAllocated());
			}

			@Override
			public void afterEditorDeactivated(final ColumnViewerEditorDeactivationEvent event) {
				final ViewerCell viewCell = (ViewerCell) event.getSource();
				final OrderSku orderSku = (OrderSku) viewCell.getElement();
				turnOnOrOffMoveItemButton(orderSku);
				final Order order = (Order) editor.getModel();
				final long warehouseUid = order.getStore().getWarehouse().getUidPk();
				
				final int newlyEnteredQuantity = orderSku.getQuantity();
				final int quantityToAllocate = newlyEnteredQuantity - orderSku.getAllocatedQuantity();
				final int quantityToAllocateInShipment = getTotalIncreasedQuantitySinceLastSave(orderSku.getSkuCode())
						- getTotalPreOrBackOrderQuantity(orderSku.getSkuCode());

				if (quantityToAllocateInShipment <= 0) {
					orderSku.setChangedQuantityAllocated(quantityToAllocate - orderSku.getPreOrBackOrderQuantity());
				} else {
					
					final ProductSku productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
					final AllocationStatus allocationStatus = getAllocationService().
						getAllocationStatus(productSku, warehouseUid, quantityToAllocateInShipment);

					final int availableQtyInStock = getAvailableQuantityInStock(orderSku, warehouseUid);
					switch (allocationStatus) {
					case ALLOCATED_IN_STOCK:
						orderSku.setChangedQuantityAllocated(quantityToAllocate);
						shipmentOrderSkuTable.getSwtTableViewer().refresh();
						break;
					case AWAITING_ALLOCATION:
						// in AWAITING_ALLOCATION status, some items are not available in stock, the shipment cannot be shipped
						// until those items are acquired in inventory

						final int totalAvailableCanbeAllocatedQuantity = availableQtyInStock;
						final int alreadyAllocatedQuantity = getAllocatedInStockQty(orderSku, warehouseUid)
								+ getTotalChangedQuantityAllocated(orderSku.getSkuCode());
						final int alreadyAllocatedQuantityExceptCurrentOrderSku = alreadyAllocatedQuantity - orderSku.getChangedQuantityAllocated();
						orderSku.setChangedQuantityAllocated(totalAvailableCanbeAllocatedQuantity - alreadyAllocatedQuantityExceptCurrentOrderSku);

						shipmentOrderSkuTable.getSwtTableViewer().refresh();
						break;
					case NOT_ALLOCATED:
						final int availableQuantityCanbeAddedIntoCurrentOrderSku = availableQtyInStock
								+ getTotalPreOrBackOrderQuantity(orderSku.getSkuCode())
								- getTotalIncreasedQuantitySinceLastSave(orderSku.getSkuCode()) + orderSku.getQuantity();
						if (availableQuantityCanbeAddedIntoCurrentOrderSku == 0) {
							openInsufficientInventoryWarningDialog(FulfillmentMessages.get().ShipmentSection_InSufficientSkuQuantityWarningHeader
									+ STRING_NEW_LINE + STRING_NEW_LINE + orderSku.getSkuCode() + STRING_NEW_LINE + STRING_NEW_LINE
									+ FulfillmentMessages.get().ShipmentSection_InsufficientInventoryNotInStock);
							viewCell.setText("0"); //$NON-NLS-1$
						} else {
							openInsufficientInventoryWarningDialog(FulfillmentMessages.get().ShipmentSection_InSufficientSkuQuantityWarningHeader
									+ STRING_NEW_LINE + STRING_NEW_LINE + orderSku.getSkuCode() + STRING_NEW_LINE + STRING_NEW_LINE
									+ FulfillmentMessages.get().ShipmentSection_InsufficientInventoryOnly + SPACE
									+ availableQtyInStock + SPACE
									+ FulfillmentMessages.get().ShipmentSection_InsufficientInventoryItemsInStock);
							// convert back to original ordersku
							orderSku.setQuantity(oldOrderSku.getQuantity());
							orderSku.setChangedQuantityAllocated(oldOrderSku.getChangedQuantityAllocated());
							shipmentOrderSkuTable.getSwtTableViewer().refresh();
						}
						break;
					default:
							// nothing to do
					}
				}

				// Log the quantity changed event. 
				final int qtyChanged = orderSku.getQuantity() - oldOrderSku.getQuantity();
				if (qtyChanged != 0) {
					OrderEventCmHelper.getOrderEventHelper().logOrderSkuQuantityChanged(orderSku.getShipment(), orderSku, qtyChanged);
					((OrderEditor) editor).addOrderShipmentToUpdate(shipment);
					((OrderEditor) editor).fireAddNoteChanges();
				}
				
				super.afterEditorDeactivated(event);
			}

			private int getAllocatedInStockQty(final OrderSku orderSku, final long warehouseUid) {
				final InventoryDto inventoryDto = productInventoryManagementService.getInventory(orderSku.getSkuCode(), warehouseUid);
				if (inventoryDto != null) {
					return inventoryDto.getAllocatedQuantity();
				}
				return 0;
			}

			private int getAvailableQuantityInStock(final OrderSku orderSku, final long warehouseUid) {
				final InventoryDto inventoryDto = productInventoryManagementService.getInventory(orderSku.getSkuCode(), warehouseUid);
				if (inventoryDto != null) {
					return inventoryDto.getAvailableQuantityInStock();
				}
				return 0;
			}

		}

		/**
		 * @param viewer the column viewer
		 * @param bindingContext the data binding context
		 */
		InlineQuantityEditingSupport(final ColumnViewer viewer, final DataBindingContext bindingContext) {
			super(viewer, bindingContext);
		}

		@Override
		public InlineQuantityEditorActivationlistenerHelper getActivationListener() {
			return activationListener;

		}

		@Override
		protected void initializeCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			cellEditor.setValue(cell.getText());

			final EpBindingConfiguration bindingConfig = new EpBindingConfiguration(getBindingContext(), cellEditor.getControl(), cell.getElement(),
					"quantity"); //$NON-NLS-1$
			bindingConfig.configureUiToModelBinding(StringToNumberConverter.toInteger(true), EpValidatorFactory.POSITIVE_INTEGER, true);
			bindingConfig.setErrorLocation(ValidationErrorLocation.LEFT);
			setBinding(bindingProvider.bind(bindingConfig));
			getViewer().getColumnViewerEditor().addEditorActivationListener(getActivationListener());
		}
	}

	/**
	 * Inline editing support for item discount.
	 */
	private class InlineDiscountEditingSupport extends AbstractInlineEditingSupport {
		
		private final InlineDiscountEditorActivationlistenerHelper discountActivationListener = new InlineDiscountEditorActivationlistenerHelper();
		
		/**
		 * Doc.
		 */
		private class InlineDiscountEditorActivationlistenerHelper extends ColumnViewerEditorActivationListenerHelper {

			private final OrderSku oldOrderSku = ServiceLocator.getService(ContextIdNames.ORDER_SKU);

			@Override
			public void afterEditorActivated(final ColumnViewerEditorActivationEvent event) {
				super.afterEditorActivated(event);

				final ViewerCell viewCell = (ViewerCell) event.getSource();
				final OrderSku orderSku = (OrderSku) viewCell.getElement();
				oldOrderSku.setDiscountBigDecimal(orderSku.getDiscountBigDecimal());
			}

			@Override
			public void afterEditorDeactivated(final ColumnViewerEditorDeactivationEvent event) {
				final ViewerCell viewCell = (ViewerCell) event.getSource();
				final OrderSku orderSku = (OrderSku) viewCell.getElement();
				turnOnOrOffMoveItemButton(orderSku);

				if (orderSku.getDiscountBigDecimal().setScale(2).compareTo(oldOrderSku.getDiscountBigDecimal().setScale(2)) != 0) {
					((OrderEditor) editor).addOrderShipmentToUpdate(shipment);
				}
				
				super.afterEditorDeactivated(event);
			}
		}
		
		/**
		 * @param viewer the column viewer
		 * @param bindingContext the data binding context
		 */
		InlineDiscountEditingSupport(final ColumnViewer viewer, final DataBindingContext bindingContext) {
			super(viewer, bindingContext);
		}	

		@Override
		protected void initializeCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {

			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpBindingConfiguration bindingConfig = new EpBindingConfiguration(getBindingContext(), cellEditor.getControl(), cell.getElement(),
					"discountBigDecimal"); //$NON-NLS-1$
			cellEditor.setValue(cell.getText());			
			bindingConfig.configureUiToModelBinding(new EpStringToBigDecimalConverter(), EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, true);
			bindingConfig.setErrorLocation(ValidationErrorLocation.LEFT);
			setBinding(bindingProvider.bind(bindingConfig));
			getViewer().getColumnViewerEditor().addEditorActivationListener(getActivationListener());
		}
		
		@Override
		public InlineDiscountEditorActivationlistenerHelper getActivationListener() {
			return discountActivationListener;

		}
	}

	/**
	 * Inline editing support for item discount.
	 */
	private class InlineOrderShipmentPriceEditingSupport extends InlinePriceEditingSupport {
		
		/**
		 * @param viewer the column viewer
		 * @param shipment - shipment
		 * @param bindingContext data binding context
		 */
		InlineOrderShipmentPriceEditingSupport(final IEpTableViewer viewer, 
														final OrderShipment shipment,
														final DataBindingContext bindingContext) {
			super(viewer, shipment, bindingContext);
		}	

		@Override
		protected void setValue(final Object element, final Object selectionResult) {
			
			if (!(element instanceof OrderSku)) { 
				return; 
			}
			
			BigDecimal oldInvoicePrice = ((OrderSku) element).getUnitPrice();

			super.setValue(element, selectionResult);
			
			BigDecimal newInvoicePrice = null;
			
			// only deal with the change from the line item editor
			if (selectionResult instanceof String && StringUtils.isNotEmpty((String) selectionResult)) {
					newInvoicePrice = new BigDecimal((String) selectionResult).setScale(2);
			} 
			
			if (newInvoicePrice != null && oldInvoicePrice.compareTo(newInvoicePrice) != 0) {
				((OrderEditor) editor).addOrderShipmentToUpdate(shipment);
			}
		}
	}

	private IEpLayoutData createTableButtons(final IEpLayoutComposite tablePane) {
		final IEpLayoutData buttonPaneData = tablePane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutComposite buttonsPane = tablePane.addTableWrapLayoutComposite(1, true, buttonPaneData);
		final IEpLayoutData buttonData = tablePane.createLayoutData();

		addItemButton = buttonsPane.addPushButton(FulfillmentMessages.get().ShipmentSection_AddItemButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_ADD), getStateFromPermissions(), buttonData);
		addItemButton.addSelectionListener(this);

		moveItemButton = buttonsPane.addPushButton(FulfillmentMessages.get().ShipmentSection_MoveItemButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_MOVE), getStateFromPermissions(), buttonData);
		moveItemButton.setEnabled(false);
		moveItemButton.addSelectionListener(this);
		removeItemButton = buttonsPane.addPushButton(FulfillmentMessages.get().ShipmentSection_RemoveItemButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_REMOVE), getStateFromPermissions(), buttonData);
		removeItemButton.setEnabled(false);
		removeItemButton.addSelectionListener(this);

		editItemAttributes = buttonsPane.addPushButton(FulfillmentMessages.get().ShipmentSection_EditItemAttributesButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT), EpState.EDITABLE, buttonData);
		editItemAttributes.setEnabled(false);
		editItemAttributes.addSelectionListener(this);
		
		openProductButton = buttonsPane.addPushButton(FulfillmentMessages.get().ShipmentSection_OpenProductButton, CoreImageRegistry
				.getImage(CoreImageRegistry.PRODUCT), EpState.EDITABLE, buttonData);
		openProductButton.setEnabled(false);
		openProductButton.addSelectionListener(this);
		return buttonData;
	}

	/**
	 * Content provider for displaying the shipment's <code>OrderSku</code>s.
	 */
	class OrderSkuContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			OrderShipment shipment = (OrderShipment) inputElement;
			List<OrderSku> orderSkuList = new LinkedList<>(shipment.getShipmentOrderSkus());
			orderSkuList.sort(OrderSkuComparatorFactory.getOrderSkuCodeComparator());
			return orderSkuList.toArray();
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// not used
		}
	}

	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (propId == OrderEditor.PROP_REFRESH_PARTS && !shipmentOrderSkuTable.getSwtTableViewer().getTable().isDisposed()) {
			shipmentOrderSkuTable.getSwtTableViewer().refresh();
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing
	}
	
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == addItemButton) {
			final SkuFinderDialog finderDialog = new SkuFinderDialog(editor.getSite().getShell(), shipment.getOrder().getStore().getCatalog(),
					shipment.getOrder().getCurrency(), true);
			if (finderDialog.open() == Window.OK) {
				Object selectedObject = finderDialog.getSelectedObject();
				BaseAmountDTO selectedItemPriceSummary = finderDialog.getSelectedItemPriceSummary();
				if (selectedObject instanceof ProductSku) {
					addToShipment((ProductSku) selectedObject, shipment, selectedItemPriceSummary);
				} else if (selectedObject instanceof Product) {
					ProductSku defaultSku = ((Product) selectedObject).getDefaultSku();
					addToShipment(defaultSku, shipment, selectedItemPriceSummary);
				}
			}
		} else if (event.getSource() == moveItemButton) {
			final OrderSku orderSku = (OrderSku) ((IStructuredSelection) shipmentOrderSkuTable.getSwtTableViewer().getSelection()).getFirstElement();
			new MoveItemDialog(editor.getSite().getShell(), editor, orderSku, shipment, shipmentNumber).open();
		} else if (event.getSource() == removeItemButton) {
			final OrderSku orderSku = (OrderSku) ((IStructuredSelection) shipmentOrderSkuTable.getSwtTableViewer().getSelection()).getFirstElement();
			removeFromShipment(orderSku, shipment);
		} else if (event.getSource() == openProductButton) {
			final OrderSku orderSku = (OrderSku) ((IStructuredSelection) shipmentOrderSkuTable.getSwtTableViewer().getSelection()).getFirstElement();
			try {
				IWorkbenchPage workbenchPage = editor.getSite().getPage();
				final ProductSku productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
				workbenchPage.openEditor(new GuidEditorInput(productSku.getProduct().getGuid(), Product.class), ProductEditor.PART_ID);
			} catch (final PartInitException exc) {
				LOG.error("Error opening the Product SKU Editor", exc); //$NON-NLS-1$
			}
		} else if (event.getSource() == editItemAttributes) {
			final OrderSku orderSku = (OrderSku) ((IStructuredSelection) shipmentOrderSkuTable.getSwtTableViewer().getSelection()).getFirstElement();
			
			final ProductSku productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
			boolean giftCertificate = productSku.isDigital() && !productSku.isDownloadable(); 
			
			final boolean canEdit = !shipment.getShipmentStatus().equals(OrderShipmentStatus.SHIPPED)
					&& !giftCertificate;
			
			final OrderItemFieldValueDialog attrDialog = 
				OrderItemFieldValueDialog.createOrderItemDataDialog(
						editor.getSite().getShell(), 
						orderSku,
						shipment.getOrder(),
						canEdit
						);
			
			

			if (attrDialog.open() == Window.OK && attrDialog.isChanged()) {
				editor.controlModified();
				((OrderEditor) editor).addOrderShipmentToUpdate(shipment);
			}
			
		}
	}
	
	/**
	 * Confirms that the user wants to remove the given {@code OrderSku} from the given {@code OrderShipment},
	 * and then removes it if confirmed. If, after removing the sku, the shipment is empty, then the
	 * shipment is cancelled.
	 * @param orderSku the {@code OrderSku} to remove
	 * @param orderShipment the shipment from which the {@code OrderSku} should be removed
	 */
	void removeFromShipment(final OrderSku orderSku, final OrderShipment orderShipment) {
		if (confirmOrderSkuRemoval(orderSku)) {
			orderShipment.removeShipmentOrderSku(orderSku, getProductSkuLookup());
			orderShipment.getOrder().setModifiedBy(getEventOriginator());
			
			// Log the sku removed event.
			OrderEventCmHelper.getOrderEventHelper().logOrderSkuRemoved(orderShipment, orderSku);
			((OrderEditor) editor).fireAddNoteChanges();
	
			removeItemButton.setEnabled(false);
			moveItemButton.setEnabled(false);
			openProductButton.setEnabled(false);
			if (orderShipment.getShipmentOrderSkus().size() == 0) {
				orderShipment.setStatus(OrderShipmentStatus.CANCELLED);
				((OrderEditor) editor).addOrderShipmentToCancel(orderShipment);
				((OrderEditor) editor).fireRefreshChanges();
			} else {
				((OrderEditor) editor).addOrderShipmentToUpdate(orderShipment);
				shipmentOrderSkuTable.getSwtTableViewer().refresh();
			}
			
			((RecalculableObject) orderSku).enableRecalculation();
			editor.controlModified();
			editor.getDataBindingContext().updateTargets();
		}
	}
	
	/**
	 * Displays a message box confirming that the user wants to remove the given OrderSku.
	 * @param orderSku the orderSku to be removed
	 * @return true if the user confirms removal, false if not.
	 */
	boolean confirmOrderSkuRemoval(final OrderSku orderSku) {
		final StringBuilder stringQuery = new StringBuilder();
		stringQuery.append(FulfillmentMessages.get().ShipmentSection_ConfirmRemoveItem + STRING_NEW_LINE + STRING_NEW_LINE + orderSku.getSkuCode()
				+ STRING_MINUS + orderSku.getDisplayName());

		if (shipment.getShipmentOrderSkus().size() == 1) {
			stringQuery.append(STRING_NEW_LINE + STRING_NEW_LINE + FulfillmentMessages.get().ShipmentSection_ConfirmRemoveShipment);
		}
		return MessageDialog.openConfirm(editor.getSite().getShell(), FulfillmentMessages.get().ShipmentSection_RemoveItemConfirm,
				stringQuery.toString());
	}

	/**
	 * Check whether a sku is out of stock. The allocation service is used
	 * to determine whether enough quantity can be allocated for the provided SKU.
	 *
	 * @param sku the sku
	 * @return true if the sku is out of stock, false if not.
	 */
	protected boolean isSkuOutOfStock(final ProductSku sku) {
		final Order order = (Order) editor.getModel();
		final long warehouseUid = order.getStore().getWarehouse().getUidPk();
		return !getAllocationService().hasSufficientUnallocatedQty(sku, warehouseUid, sku.getProduct().getMinOrderQty());
	}
	
	/**
	 * Adds the given {@code ProductSku} to the given {@link OrderShipment}.
	 * @param sku the sku to add
	 * @param orderShipment the shipment to which the sku should be added
	 * @param selectedPrice the price to set into the added item
	 */
	void addToShipment(final ProductSku sku, final OrderShipment orderShipment, final BaseAmountDTO selectedPrice) {
		// check if the product is digital and if yes show error message and cancel the addition to order shipment
		if (!validRequest(sku, orderShipment.getShipmentOrderSkus())) {
			return;
		}
		
		final OrderSku orderSku = getOrderSkuBean();

		copyFromProductSkuToOrderSku(sku, orderSku, selectedPrice); //here we setup the price
		orderShipment.addShipmentOrderSku(orderSku);
		orderShipment.getOrder().setModifiedBy(getEventOriginator());
		handleQuantityAllocationWhenAddItem(orderSku);
		((OrderEditor) editor).addOrderShipmentToUpdate(orderShipment);
		
		OrderEventCmHelper.getOrderEventHelper().logOrderSkuAdded(orderShipment, orderSku);
		
		((OrderEditor) editor).fireAddNoteChanges();
		shipmentOrderSkuTable.getSwtTableViewer().refresh();
		editor.controlModified();
		editor.getDataBindingContext().updateTargets();
	}

	private OrderSku getOrderSkuBean() {
		return ServiceLocator.getService(ContextIdNames.ORDER_SKU);
	}
	
	private boolean validRequest(final ProductSku sku, final Set<OrderSku> orderSkus) {
		
		if (!isEnabledAndVisible(sku)) {
			MessageDialog.openError(null, FulfillmentMessages.get().OrderDetailsErrorAddingItem_Title,
					FulfillmentMessages.get().OrderDetailsErrorAddingItemNotEnabled_Message);
			return false;
		}
		
		if (sku.getProduct() instanceof ProductBundle) {
			MessageDialog.openError(null, FulfillmentMessages.get().OrderDetailsErrorAddingItem_Title,
					FulfillmentMessages.get().OrderDetailsErrorAddingBundle_Message);
			return false;
		}
		
		ProductSkuChecker psc = new ProductSkuChecker((Order) editor.getModel());
		if (psc.isRecurringSku(sku)) {
			MessageDialog.openError(null, FulfillmentMessages.get().OrderDetailsErrorAddingItem_Title,
					FulfillmentMessages.get().OrderDetailsErrorAddingRecurring_Message);
			return false;
		}

		
		if (isSkuOutOfStock(sku)) {
			MessageDialog.openError(null, FulfillmentMessages.get().ShipmentSection_InsufficientInventoryNotInStock,
					FulfillmentMessages.get().ShipmentSection_MinimumOrderQuantityOutOfStock);
			return false;
		}
		
		for (final OrderSku orderSkuItem : orderSkus) {
			String skuCode = sku.getSkuCode();
			if (orderSkuItem.getSkuCode().equals(sku.getSkuCode())) {
				openDuplicateProductSkuWarningDialog(FulfillmentMessages.get().ShipmentSection_DuplicateProductSkuWarningHeader + STRING_NEW_LINE
						+ STRING_NEW_LINE + skuCode + STRING_NEW_LINE + STRING_NEW_LINE
						+ FulfillmentMessages.get().ShipmentSection_DuplicateProductSkuWarningMessage);
				shipmentOrderSkuTable.getSwtTableViewer().refresh();
				return false;
			}
		}

		return true;
	}
	
	private boolean isEnabledAndVisible(final ProductSku sku) {
		return sku.isWithinDateRange() && sku.getProduct().isWithinDateRange(new Date()) && !sku.getProduct().isHidden();
	}

	private void handleQuantityAllocationWhenAddItem(final OrderSku orderSku) {
		final int quantityToAllocateInShipment = getTotalIncreasedQuantitySinceLastSave(orderSku.getSkuCode());
		if (quantityToAllocateInShipment <= 0) {
			orderSku.setChangedQuantityAllocated(1);
		} else {
			final Order order = (Order) editor.getModel();
			Warehouse warehouse = order.getStore().getWarehouse();
			final ProductSku productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
			final AllocationStatus allocationStatus = 
				getAllocationService().getAllocationStatus(productSku, warehouse.getUidPk(), quantityToAllocateInShipment);

			if (allocationStatus == AllocationStatus.ALLOCATED_IN_STOCK) {
				orderSku.setChangedQuantityAllocated(1);
			} else if (allocationStatus == AllocationStatus.NOT_ALLOCATED) {
				openInsufficientInventoryWarningDialog(FulfillmentMessages.get().ShipmentSection_InSufficientSkuQuantityWarningHeader
						+ STRING_NEW_LINE + STRING_NEW_LINE + orderSku.getSkuCode() + STRING_NEW_LINE + STRING_NEW_LINE
						+ FulfillmentMessages.get().ShipmentSection_InsufficientInventoryNotInStock);
				return;
			}
		}
	}

	/**
	 * Gets the section description.
	 * 
	 * @return string
	 */

	protected String getSectionDescription() {
		return FulfillmentMessages.get().ShipmentSection_Description;
	}

	/**
	 * Gets the section title.
	 * 
	 * @return string
	 */

	protected String getSectionTitle() {
		return FulfillmentMessages.get().ShipmentSection_SubSectionItem;
	}

	private void copyFromProductSkuToOrderSku(final ProductSku productSku, final OrderSku orderSku, final BaseAmountDTO selectedPrice) {
		// FIXME: compared with checkoutserviceimpl -- createOrderSkuFromCartItem
		final Order order = (Order) editor.getModel();
		final Product product = productSku.getProduct();
		
		orderSku.setCreatedDate(getTimeService().getCurrentTime());
		orderSku.setDigitalAsset(productSku.getDigitalAsset());
		orderSku.setDisplayName(product.getDisplayName(order.getLocale()));
		
		if (productSku.getOptionValues().size() > 0) {
			final StringBuilder skuOptionValues = new StringBuilder();
			for (final Iterator<SkuOptionValue> optionValueIter = productSku.getOptionValues().iterator(); optionValueIter.hasNext();) {
				final SkuOptionValue currOptionValue = optionValueIter.next();
				skuOptionValues.append(currOptionValue.getDisplayName(order.getLocale(), true));
				if (optionValueIter.hasNext()) {
					skuOptionValues.append(", "); //$NON-NLS-1$
				}
			}
			orderSku.setDisplaySkuOptions(skuOptionValues.toString());
		}
        
		orderSku.setSkuGuid(productSku.getGuid());
		orderSku.setSkuCode(productSku.getSkuCode());
		orderSku.setImage(productSku.getImage());
		orderSku.setLastModifiedBy(LoginManager.getCmUser());
		PriceHelper priceHelper = new PriceHelper();
		Price price = priceHelper.createPriceFromBaseAmountDto(selectedPrice, order.getCurrency());
		
		orderSku.setPrice(product.getMinOrderQty(), price);	
		if (selectedPrice.getSaleValue() == null) {
			orderSku.setUnitPrice(price.getListPrice().getAmount());
		} else {
			orderSku.setUnitPrice(selectedPrice.getSaleValue());
		}
				
		if (productSku.getWeight() != null) {
			orderSku.setWeight(productSku.getWeight().intValue());
		}
		orderSku.setTaxCode(getTaxCodeRetriever().getEffectiveTaxCode(productSku).getCode());
		
	}

	/*
	 * Sum up quantityAllocated for all the orderSku with the same skuCode in the order.
	 */
	private int getTotalChangedQuantityAllocated(final String skuCode) {
		final Order order = (Order) editor.getModel();
		int totalChangedQuantityAllocated = 0;
		for (final PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (final OrderSku orderSkuItem : shipment.getShipmentOrderSkus()) {
				if (orderSkuItem.getSkuCode().equals(skuCode)) {
					totalChangedQuantityAllocated += orderSkuItem.getChangedQuantityAllocated();
				}
			}
		}
		return totalChangedQuantityAllocated;
	}

	/*
	 * Increased quantity since last save refers to the amount of quantity newly added to the currently order sku since last save. For each order sku
	 * increasedQuantitySinceLastSave = orderSku.getQuantity - orderSku.getAllocatedQuantity Summing up (quantity - allocated quantity) for all the
	 * orderSku with the same skuCode in the order, you will get totalIncreasedQauntitySinceLastSave
	 */
	private int getTotalIncreasedQuantitySinceLastSave(final String skuCode) {
		final Order order = (Order) editor.getModel();
		int totalIncreasedQuantitySinceLastSave = 0;
		for (final PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (final OrderSku orderSkuItem : shipment.getShipmentOrderSkus()) {
				if (orderSkuItem.getSkuCode().equals(skuCode)) {
					totalIncreasedQuantitySinceLastSave += (orderSkuItem.getQuantity() - orderSkuItem.getAllocatedQuantity());
				}
			}
		}
		return totalIncreasedQuantitySinceLastSave;
	}

	private int getTotalPreOrBackOrderQuantity(final String skuCode) {
		Order order = (Order) editor.getModel();
		int totalPreOrBackOrderQuantity = 0;
		for (PhysicalOrderShipment shipment : order.getPhysicalShipments()) {
			for (OrderSku orderSkuItem : shipment.getShipmentOrderSkus()) {
				if (orderSkuItem.getSkuCode().equals(skuCode)) {
					totalPreOrBackOrderQuantity += orderSkuItem.getPreOrBackOrderQuantity();
				}
			}
		}
		return totalPreOrBackOrderQuantity;
	}

	private void openInsufficientInventoryWarningDialog(final String message) {
		MessageDialog.openWarning(editor.getSite().getShell(), FulfillmentMessages.get().ShipmentSection_InsufficientInventoryTitle, message);
	}

	private void openDuplicateProductSkuWarningDialog(final String message) {
		MessageDialog.openWarning(editor.getSite().getShell(), FulfillmentMessages.get().ShipmentSection_DuplicateProductSkuTitle, message);
	}

	private void turnOnOrOffMoveItemButton(final OrderSku orderSku) {
		if (orderSku.getShipment().getShipmentOrderSkus().size() == 1 && orderSku.getQuantity() == 1) {
			moveItemButton.setEnabled(false);
		} else {
			moveItemButton.setEnabled(true);
		}
	}

	private void turnOnOrOffRemoveItemButton(final OrderSku orderSku) {
		if (orderSku.getShipment().getShipmentOrderSkus().size() > 1) {
			removeItemButton.setEnabled(true);
		}
	}

	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}

	private TaxCodeRetriever getTaxCodeRetriever() {
		if (taxCodeRetriever == null) {
			taxCodeRetriever = ServiceLocator.getService(ContextIdNames.TAX_CODE_RETRIEVER);
		}
		return taxCodeRetriever;
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

}
