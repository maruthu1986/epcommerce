/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.editors.orderreturn;

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
 * Represents the UI of the order return and exchange page.
 */
public class OrderReturnPage extends AbstractCmClientEditorPage {
	/** Page identifier. */
	public static final String PAGE_ID = OrderReturnPage.class.getName();

	private static final int FORM_COLUMNS_COUNT = 1;

	private OrderReturnOverviewSectionPart overviewPart;

	/**
	 * Constructor.
	 * 
	 * @param editor <code>FormEditor</code>
	 */
	public OrderReturnPage(final AbstractCmClientFormEditor editor) {
		super(editor, PAGE_ID, WarehouseMessages.get().OrderReturnPage_Title);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		final EpState editMode;
		if (AuthorizationService.getInstance().isAuthorizedForWarehouse(WarehousePerspectiveFactory.getCurrentWarehouse())
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(WarehousePermissions.WAREHOUSE_ORDER_RETURN_EDIT)) {
			editMode = EpState.EDITABLE;
		} else {
			editMode = EpState.READ_ONLY;
		}
		overviewPart = new OrderReturnOverviewSectionPart(this, editor, editMode);
		managedForm.addPart(overviewPart);
		managedForm.addPart(new OrderReturnDetailsSectionPart(this, editor, editMode));
		managedForm.addPart(new OrderReturnNoteSectionPart(this, editor, editMode));

		getCustomPageData().put("editMode", editMode);
		addExtensionEditorSections(editor, managedForm, WarehousePlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected int getFormColumnsCount() {
		return FORM_COLUMNS_COUNT;
	}

	@Override
	protected String getFormTitle() {
		return WarehouseMessages.get().OrderReturnPage_Title;
	}

	/**
	 * Invokes RMA status update.
	 */
	public void updateStatus() {
		overviewPart.updateStatus();
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		//No buttons or anything else to add in the toolbar.		
	}
}
