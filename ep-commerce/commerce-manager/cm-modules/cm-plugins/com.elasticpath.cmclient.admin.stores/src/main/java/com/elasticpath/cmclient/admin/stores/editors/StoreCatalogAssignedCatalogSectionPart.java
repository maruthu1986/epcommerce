/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.Collection;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.admin.stores.AdminStoresImageRegistry;
import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.catalog.CatalogService;

/**
 * UI representation of the Store Catalog Assigned Catalog Section.
 */
public class StoreCatalogAssignedCatalogSectionPart extends AbstractStoreAssignedSectionPart<Catalog> {

	private final CatalogService catalogService;
	
	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the form page
	 * @param editable whether the section should be editable
	 */
	public StoreCatalogAssignedCatalogSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor,
			final boolean editable) {
		super(formPage, editor);
		this.catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
		this.getSection().setEnabled(editable);
	}

	@Override
	protected String getSectionTitle() {
		return AdminStoresMessages.get().StoreAssignedCatalog;
	}

	@Override
	protected Image getDomainImage(final Catalog domain) {
		if (domain.isMaster()) {
			return AdminStoresImageRegistry.getImage(AdminStoresImageRegistry.IMAGE_CATALOG);
		}
		return AdminStoresImageRegistry.getImage(AdminStoresImageRegistry.IMAGE_VIRTUAL_CATALOG);
	}

	@Override
	protected String getDomainName(final Catalog domain) {
		return domain.getName();
	}

	@Override
	protected Catalog getSelectedDomain() {
		return getStoreEditorModel().getCatalog();
	}

	@Override
	protected Collection<Catalog> listAllDomainObjects() {
		return catalogService.findAllCatalogs();
	}

	@Override
	protected void setSelectedDomain(final Catalog domain) {
		getStoreEditorModel().setCatalog(domain);
	}

}
