/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.editors.EntityEditorInput;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.editors.inventory.InventoryEditor;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * An action used to open the inventory editor.
 */
public class OpenInventoryEditorAction extends Action {

	private static final Logger LOG = Logger.getLogger(OpenInventoryEditorAction.class);

	private final ProductSku productSku;

	private final IWorkbenchPartSite workbenchPartSite;

	/**
	 * Constructs new <code>Action</code>.
	 *
	 * @param productSku the productSku.
	 * @param workbenchPartSite the workbench site from the view
	 */
	public OpenInventoryEditorAction(final ProductSku productSku, final IWorkbenchPartSite workbenchPartSite) {
		this.workbenchPartSite = workbenchPartSite;
		this.productSku = productSku;
	}

	@Override
	public void run() {
		long editorUid = Long.parseLong(productSku.getUidPk() + "" + WarehousePerspectiveFactory.getCurrentWarehouse().getUidPk()); //NOPMD
		final EntityEditorInput<?> editorInput = new EntityEditorInput<Object>(productSku.getSkuCode() + "-" //$NON-NLS-1$
				+ WarehousePerspectiveFactory.getCurrentWarehouse().getName(), editorUid, ProductSku.class);
		editorInput.setToolTipText(
			NLS.bind(WarehouseMessages.get().Inventory_EditorTooltip,
			productSku.getSkuCode(), WarehousePerspectiveFactory.getCurrentWarehouse().getName()));
		try {
			workbenchPartSite.getPage().openEditor(editorInput, InventoryEditor.ID_EDITOR);
		} catch (final PartInitException e) {
			LOG.error("Can not inventory details editor", e); //$NON-NLS-1$
		}
	}
}
