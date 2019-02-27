/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.editors.inventory;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.WarehousePermissions;
import com.elasticpath.cmclient.warehouse.WarehousePlugin;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;

/**
 * Page that displays summary of inventory.
 */
public class AdjustInventoryPage extends AbstractCmClientEditorPage {

	private final AbstractCmClientFormEditor editor;

	/**
	 * Constructs the editor page.
	 * 
	 * @param editor The EP FormEditor
	 */
	public AdjustInventoryPage(final AbstractCmClientFormEditor editor) {
		super(editor, "AdjustInventoryPage", WarehouseMessages.get().SearchView_InventoryTab); //$NON-NLS-1$
		this.editor = editor;
	}

	/**
	 * Gets the editor.
	 * 
	 * @return AbstractCmClientFormEditor the editor
	 */
	@Override
	public AbstractCmClientFormEditor getEditor() {
		return editor;
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		final EpState editMode;
		if (AuthorizationService.getInstance().isAuthorizedForWarehouse(WarehousePerspectiveFactory.getCurrentWarehouse())
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(WarehousePermissions.WAREHOUSE_MANAGE_INVENTORY)) {
			editMode = EpState.EDITABLE;
		} else {
			editMode = EpState.READ_ONLY;
		}
		managedForm.addPart(new InventorySummarySectionPart(this, editor));
		managedForm.addPart(new InventorySettingsSectionPart(this, editor, editMode));
		managedForm.addPart(new AdjustQuantityOnHandSectionPart(this, editor, editMode));
		addExtensionEditorSections(editor, managedForm, WarehousePlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// Do nothing

	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return WarehouseMessages.get().SearchView_InventoryTab;
	}
	
}
