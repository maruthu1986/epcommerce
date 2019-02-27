/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.warehouse.editors.inventory;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;
import com.elasticpath.inventory.InventoryDto;

/**
 * UI representation of the inventory summary section.
 */
public class InventorySummarySectionPart extends AbstractCmClientEditorPageSectionPart {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private final InventoryModel inventoryModel;

	private Label quantityOnHand;

	private Label availableQuantity;

	private Label quantityAllocated;


	/**
	 * Constructor.
	 * 
	 * @param formPage the FormPage
	 * @param editor the AbstractCmClientFormEditor
	 */
	public InventorySummarySectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
		inventoryModel = (InventoryModel) editor.getModel();

	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// Do nothing
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		IEpLayoutComposite composite = CompositeFactory.createTableWrapLayoutComposite(client, 2, true);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		IEpLayoutData labelData = composite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		IEpLayoutData fieldData = composite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);

		composite.addLabelBold(WarehouseMessages.get().Inventory_Warehouse, labelData);
		composite.addLabel(EMPTY_STRING, fieldData).setText(WarehousePerspectiveFactory.getCurrentWarehouse().getName());

		composite.addLabelBold(WarehouseMessages.get().Inventory_QuantityOnHand, labelData);
		quantityOnHand = composite.addLabel(EMPTY_STRING, fieldData);

		composite.addLabelBold(WarehouseMessages.get().Inventory_AvailableQuantity, labelData);
		availableQuantity = composite.addLabel(EMPTY_STRING, fieldData);

		composite.addLabelBold(WarehouseMessages.get().Inventory_QuantityAllocated, labelData);
		quantityAllocated = composite.addLabel(EMPTY_STRING, fieldData);

	}

	@Override
	protected void populateControls() {
		InventoryDto inventoryDto = inventoryModel.getInventory();
		quantityOnHand.setText(String.valueOf(inventoryDto.getQuantityOnHand()));
		availableQuantity.setText(String.valueOf(inventoryDto.getAvailableQuantityInStock()));
		quantityAllocated.setText(String.valueOf(inventoryDto.getAllocatedQuantity()));
		
	}

	@Override
	protected String getSectionTitle() {
		return WarehouseMessages.get().Inventory_SummarySectionPart;
	}

}
