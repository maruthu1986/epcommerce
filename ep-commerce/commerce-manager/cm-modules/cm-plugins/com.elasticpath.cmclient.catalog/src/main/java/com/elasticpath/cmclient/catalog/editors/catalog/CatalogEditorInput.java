/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.EntityEditorInput;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.catalog.CatalogService;

/**
 * Catalog editor input.
 */
public class CatalogEditorInput extends EntityEditorInput<Long> {

	private Catalog catalog;

	/**
	 * New editor input from a catalog UID.
	 *
	 * @param catalogUid the UID of the catalog for input
	 */
	public CatalogEditorInput(final long catalogUid) {
		super(null, catalogUid, Catalog.class);
	}

	/**
	 * New editor input from an existing catalog object.
	 *
	 * @param catalog the catalog
	 */
	public CatalogEditorInput(final Catalog catalog) {
		super(null, catalog.getUidPk(), Catalog.class);
		this.catalog = catalog;
	}

	@Override
	public String getName() {
		return getCatalog().getName();
	}

	@Override
	public String getToolTipText() {
		return
			NLS.bind(CatalogMessages.get().CatalogEditor_Tooltip,
			new Object[]{getCatalog().getName()});
	}

	/**
	 * Gets a catalog from persistent storage.
	 * 
	 * @return the {@link Catalog}
	 */
	public Catalog getCatalog() {
		if (catalog == null) {
			// TODO: should there be a context id name for catalogService?
			final CatalogService catalogService = ServiceLocator.getService(
					ContextIdNames.CATALOG_SERVICE);
			
			//catalog = catalogService.getCatalog(getUid());
			FetchGroupLoadTuner catalogEditorLoadTuner = ServiceLocator.getService(
					ContextIdNames.FETCH_GROUP_LOAD_TUNER);
			catalogEditorLoadTuner.addFetchGroup(FetchGroupConstants.CATALOG_EDITOR);
			catalog = catalogService.load(getUid(), catalogEditorLoadTuner, false);
		}
		return catalog;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(final Class adapter) {
		if (adapter == Catalog.class) {
			return getCatalog();
		}
		return super.getAdapter(adapter);
	}
}
