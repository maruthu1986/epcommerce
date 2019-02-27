/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.store.WarehouseService;

/**
 * This class implements the section of the Product editor that displays product attribute information.
 */
public class ProductSkuInventoryViewPart extends DefaultStatePolicyDelegateImpl implements IEpViewPart {

	private static final int COLUMN_WIDTH_ATTRIBUTES = 150;

	private static final int COLUMN_WIDTH_WAREHOUSE = 90;
	private static final String SKU_INVENTORY_TABLE = "Sku Inventory"; //$NON-NLS-1$

	private final ProductSku productSku;

	private final ControlModificationListener controlModificationListener;

	private final ProductInventoryManagementService productInventoryManagementService;
	
	private IEpTableViewer inventoryTable;

	private IPolicyTargetLayoutComposite inventoryPane;
	
	private List<InventoryDto> inventoriesList;


	/**
	 * Constructor.
	 * 
	 * @param productSku ProductSku
	 * @param controlModificationListener the editor where the detail section will be placed
	 */
	public ProductSkuInventoryViewPart(final ProductSku productSku, final ControlModificationListener controlModificationListener) {
		this.productSku = productSku;
		this.controlModificationListener = controlModificationListener;
		this.productInventoryManagementService =
			ServiceLocator.getService(ContextIdNames.PRODUCT_INVENTORY_MANAGEMENT_SERVICE);
	}

	/**
	 * Get the title of the section.
	 * 
	 * @return the title of the section.
	 */
	protected String getSectionTitle() {
		return CatalogMessages.get().ProductEditorSingleSkuInventory_Title;
	}

	@Override
	public void createControls(final IEpLayoutComposite mainPane, final IEpLayoutData data) {
		createControls(PolicyTargetCompositeFactory.wrapLayoutComposite(mainPane), data);
	}

	/**
	 * Overrides to provide the controls in the section.
	 * 
	 * @param mainPane the main EP layout pane
	 * @param epLayoutData the layout data
	 */
	public void createControls(final IPolicyTargetLayoutComposite mainPane, final IEpLayoutData epLayoutData) {
		
		PolicyActionContainer inventoryControls = addPolicyActionContainer("inventoryControls"); //$NON-NLS-1$
		
		inventoryPane = mainPane.addGridLayoutSection(1, getSectionTitle(), ExpandableComposite.TITLE_BAR, epLayoutData, inventoryControls);
		
		// ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
		
		inventoryTable = inventoryPane.addTableViewer(false, mainPane.createLayoutData(IEpLayoutData.FILL,
			IEpLayoutData.FILL, true, false), inventoryControls, SKU_INVENTORY_TABLE);
		
		inventoryTable.addTableColumn(CatalogMessages.get().ProductEditorSingleSkuShipping_Warehouse, COLUMN_WIDTH_ATTRIBUTES);
		
		final Map<Long, InventoryDto> inventoryDtos = productInventoryManagementService.getInventoriesForSku(getModel().getSkuCode());
		final WarehouseService warehouseService = ServiceLocator.getService(ContextIdNames.WAREHOUSE_SERVICE);
		
		inventoriesList = new ArrayList<>(inventoryDtos.size());
		Set<Warehouse> warehouses = new HashSet<>();
		for (final Long warehouseUid : inventoryDtos.keySet()) {
			final Warehouse warehouse = warehouseService.getWarehouse(warehouseUid);
			warehouses.add(warehouse);
		}
		AuthorizationService.getInstance().filterAuthorizedWarehouses(warehouses);
		
		for (Warehouse warehouse : warehouses) {
			inventoryTable.addTableColumn(warehouse.getName(), COLUMN_WIDTH_WAREHOUSE);
			inventoriesList.add(inventoryDtos.get(warehouse.getUidPk()));
		}
		
		inventoryTable.setContentProvider(new InventoryContentProvider());
		inventoryTable.setLabelProvider(new InventoryLableProvider());
	}
	
	@Override
	public void populateControls() {
		final boolean isEnabled = getModel().getProduct().getAvailabilityCriteria() != AvailabilityCriteria.ALWAYS_AVAILABLE;
		inventoryPane.getSwtComposite().getParent().setEnabled(isEnabled);
		((Section) inventoryPane.getSwtComposite().getParent()).setExpanded(isEnabled);
		
		inventoryTable.setInput(getAllAttributes());
		
		if (controlModificationListener != null) {
			inventoryPane.setControlModificationListener(controlModificationListener);
		}
		
	}

	private Attribute[] getAllAttributes() {
		final List<Attribute> attribs = new ArrayList<>();
		
		attribs.add(new Attribute(CatalogMessages.get().ProductEditorSingleSkuShipping_OnHandQuantity, Attribute.ATTR_ON_HAND));
		attribs.add(new Attribute(CatalogMessages.get().ProductEditorSingleSkuShipping_AvailableQuantity, Attribute.ATTR_AVAILABLE));
		attribs.add(new Attribute(CatalogMessages.get().ProductEditorSingleSkuShipping_AllocatedQuantity, Attribute.ATTR_ALLOCATED));
		attribs.add(new Attribute(CatalogMessages.get().ProductEditorSingleSkuShipping_ReservedQuantity, Attribute.ATTR_RESERVED));
		attribs.add(new Attribute(CatalogMessages.get().ProductEditorSingleSkuShipping_ReorderMinimum, Attribute.ATTR_REORDER_MIN));
		attribs.add(new Attribute(CatalogMessages.get().ProductEditorSingleSkuShipping_ReorderQuantity, Attribute.ATTR_REORDER_QTY));
		attribs.add(new Attribute(CatalogMessages.get().ProductEditorSingleSkuShipping_ExpectedRestockDate, Attribute.ATTR_EXP_REORDER_DATE));
		

		return attribs.toArray(new Attribute[attribs.size()]);
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		// no controls to bind
	}

	/**
	 * Content provider for the inventory table.
	 */
	private class InventoryContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			return (Attribute[]) inputElement;
		}

		@Override
		public void dispose() {
			// not used
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// not used
		}
	}

	/**
	 * Label provider for the inventory table.
	 */
	public class InventoryLableProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * 
		 */
		private static final int COLUMN_ATTRIBUTES = 0;

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			String result;
			if (columnIndex == COLUMN_ATTRIBUTES) {
				result = getAttributeName((Attribute) element);
			} else {
				// get inventory by column index depending on the warehouse, like:
				// inventory = warehouseList.get(columnIndex).getInventory()
				final Attribute attr = (Attribute) element;
				result = getAttributeValue(inventoriesList.get(columnIndex - 1), attr);
			}
			return result;
		}

		private String getAttributeValue(final InventoryDto inventoryDto, final Attribute attr) {
			String result = ""; //$NON-NLS-1$
			switch (attr.getId()) {
			case Attribute.ATTR_ON_HAND:
				result = String.valueOf(inventoryDto.getQuantityOnHand());
				break;
			case Attribute.ATTR_AVAILABLE:
				result = getAvailableQty(inventoryDto);
				break;
			case Attribute.ATTR_ALLOCATED :
				result = String.valueOf(inventoryDto.getAllocatedQuantity());
				break;
			case Attribute.ATTR_RESERVED:
				result = String.valueOf(inventoryDto.getReservedQuantity());
				break;
			case Attribute.ATTR_REORDER_MIN:
				result = String.valueOf(inventoryDto.getReorderMinimum());
				break;
			case Attribute.ATTR_EXP_REORDER_DATE:
				final Date restockDate = inventoryDto.getRestockDate();
				if (restockDate != null) {
					result = DateTimeUtilFactory.getDateUtil().formatAsDate(restockDate);
				}
				break;
			case Attribute.ATTR_REORDER_QTY :
				result = String.valueOf(inventoryDto.getReorderQuantity());
				break;
			default :
				// do nothing
			}
			return result;
		}

		/**
		 * Gets the available qty and if it is < 0 applies the N/A message.
		 */
		private String getAvailableQty(final InventoryDto inventoryDto) {
			String result;
			int availableQty = inventoryDto.getAvailableQuantityInStock();
			if (availableQty < 0) {
				result = CatalogMessages.get().NotAvailable;
			} else {
				result = String.valueOf(availableQty);
			}
			return result;
		}

		private String getAttributeName(final Attribute element) {
			return element.getName();
		}
	}

//	/**
//	 * Sets the layout data.
//	 * 
//	 * @param layoutData the layout data
//	 */
//	public void setLayoutData(final Object layoutData) {
//		section.setLayoutData(layoutData);
//	}
	
	/**
	 * Represents an attribute of the inventory object represented in the inventory table.
	 */
	private class Attribute {
		static final int ATTR_ON_HAND = 10;
		static final int ATTR_AVAILABLE = 11;
		static final int ATTR_ALLOCATED = 12;
		static final int ATTR_RESERVED = 13;
		static final int ATTR_REORDER_MIN = 14;
		static final int ATTR_REORDER_QTY = 15;
		static final int ATTR_EXP_REORDER_DATE = 16;
		
		private final String name;
		private final int attributeId;
		
		Attribute(final String name, final int attributeId) {
			this.name = name;
			this.attributeId = attributeId;
		}

		protected String getName() {
			return name;
		}
		
		protected int getId() {
			return attributeId;
		}
		
	}

	@Override
	public ProductSku getModel() {
		return productSku;
	}
	
}
