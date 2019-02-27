/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.admin.stores.AdminStoresImageRegistry;
import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.store.WarehouseService;

/**
 * UI representation of the Store Catalog Assigned Warehouse Section.
 */
public class StoreWarehouseAssignedWarehouseSectionPart extends AbstractStoreAssignedSectionPart<Warehouse> {

	private final WarehouseService warehouseService;

	/**
	 * Constructor.
	 *
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the form page
	 * @param editable whether the section should be editable
	 */
	public StoreWarehouseAssignedWarehouseSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, 
			final boolean editable) {
		super(formPage, editor);
		this.getSection().setEnabled(editable);
		this.warehouseService = ServiceLocator.getService(ContextIdNames.WAREHOUSE_SERVICE);
	}

	@Override
	protected String getSectionTitle() {
		return AdminStoresMessages.get().StoreAssignedWarehouse;
	}

	@Override
	protected Image getDomainImage(final Warehouse domain) {
		return AdminStoresImageRegistry.getImage(AdminStoresImageRegistry.IMAGE_WAREHOUSE);
	}

	@Override
	protected String getDomainName(final Warehouse domain) {
		return domain.getName();
	}

	@Override
	protected Warehouse getSelectedDomain() {
		return getStoreEditorModel().getWarehouse();
	}

	@Override
	protected Collection<Warehouse> listAllDomainObjects() {
		return warehouseService.findAllWarehouses();
	}

	@Override
	protected void setSelectedDomain(final Warehouse domain) {
		final List<Warehouse> warehouseList = new ArrayList<>();
		if (domain != null) {
			warehouseList.add(domain);
		}
		getStoreEditorModel().setWarehouses(warehouseList);
	}
	
}
