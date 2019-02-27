/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.editors.inventory;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.WarehousePlugin;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Implements a multi-page editor for displaying and editing inventory.
 */
public class InventoryEditor extends AbstractCmClientFormEditor {

	/** ID of the editor. It is the same as the class name. */
	public static final String ID_EDITOR = InventoryEditor.class.getName();

	/** Property changed uid. */
	public static final int INVENTORY_PROPERTY = 128;

	private ProductSkuLookup productSkuLookup;

	private ProductInventoryManagementService productInventoryManagementService;

	private ProductSku productSKU;

	private InventoryModel inventoryModel;

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		final long editorID = input.getAdapter(Long.class);
		final String editorUid = String.valueOf(editorID);
		final Long productSkuUid = Long.valueOf(editorUid.substring(0,
				editorUid.length() - String.valueOf(WarehousePerspectiveFactory.getCurrentWarehouse().getUidPk()).length()));
		productInventoryManagementService = ServiceLocator.getService(ContextIdNames.PRODUCT_INVENTORY_MANAGEMENT_SERVICE);
		productSkuLookup = ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_LOOKUP);
		initModel(productSkuUid);
		setPartName(productSKU.getSkuCode() + "-" + WarehousePerspectiveFactory.getCurrentWarehouse().getName()); //$NON-NLS-1$
	}

	@Override
	public InventoryModel getModel() {
		if (inventoryModel == null) {
			inventoryModel = new InventoryModel();
		}
		return inventoryModel;
	}

	@Override
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	protected void addPages() {
		try {
			addPage(new AdjustInventoryPage(this));
			addExtensionPages(getClass().getSimpleName(), WarehousePlugin.PLUGIN_ID);
		} catch (final PartInitException ex) {
			// TODO: Find out what should be done in this case
			// Can't throw the PartInitException because it is checked
			// and the super-implementation doesn't check for it.
			// throwing an unchecked generic exception for now (bad)
			throw new RuntimeException(ex);
		}

	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		final InventoryDto inventoryDtoBegin = getModel().getInventory();
		// if this is the first time to assign inventory for a sku, the updatedInventoryDto will have a uidpk.
		final InventoryDto updatedInventoryDto = productInventoryManagementService.saveOrUpdate(inventoryDtoBegin);

		final InventoryAudit inventoryAudit = getModel().getInventoryAudit();
		if (inventoryAudit != null) {
			productInventoryManagementService.processInventoryUpdate(updatedInventoryDto, inventoryAudit);
			// remote service will return a new inventory dto after modifying the numbers
			final InventoryDto inventoryDtoEnd = productInventoryManagementService.getInventory(updatedInventoryDto.getSkuCode(),
					updatedInventoryDto.getWarehouseUid());
			// We shall update the model.
			getModel().setInventory(inventoryDtoEnd);
		}

		refreshEditorPages();
	}

	@Override
	public void refreshEditorPages() {
		super.refreshEditorPages();
		firePropertyChange(INVENTORY_PROPERTY);
	}

	/**
	 * Init the model.
	 *
	 * @param productSkuUid The product sku uid.
	 */
	private void initModel(final Long productSkuUid) {
		productSKU = productSkuLookup.findByUid(productSkuUid);
		final InventoryDto inventoryDto = loadInventory(productSKU, WarehousePerspectiveFactory.getCurrentWarehouse().getUidPk());
		getModel().setInventory(inventoryDto);
	}

	@Override
	public void reloadModel() {
		productSKU = productSkuLookup.findByUid(productSKU.getUidPk());
		final InventoryDto inventoryDto = loadInventory(productSKU, getModel().getInventory().getWarehouseUid());
		getModel().setInventory(inventoryDto);
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	private InventoryDto loadInventory(final ProductSku productSku, final long warehouseUid) {
		InventoryDto inventoryDto = productInventoryManagementService.getInventory(productSku.getSkuCode(), warehouseUid);
		if (inventoryDto == null) {
			inventoryDto = ServiceLocator.getService(ContextIdNames.INVENTORYDTO);
			inventoryDto.setWarehouseUid(warehouseUid);
			inventoryDto.setSkuCode(productSku.getSkuCode());
		}
		return inventoryDto;
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(WarehouseMessages.get().Inventory_Editor_OnSavePrompt,
			getEditorName());
	}

	@Override
	protected String getEditorName() {
		return getEditorInput().getName();
	}

	@Override
	public Object getDependentObject() {
		return getModel().getInventory();
	}
}
