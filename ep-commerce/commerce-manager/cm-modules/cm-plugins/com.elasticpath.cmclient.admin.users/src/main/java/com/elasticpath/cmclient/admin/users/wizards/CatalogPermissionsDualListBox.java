/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.users.wizards;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.catalog.CatalogService;

/**
 * The Catalog Permissions dual listbox.
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class CatalogPermissionsDualListBox extends AbstractEpDualListBoxControl<CmUser> {

	/**
	 * Constructor.
	 * 
	 * @param parent the parent composite
	 * @param cmUser the model object (the CmUser)
	 * @param availableTitle the title for the Available listbox
	 * @param assignedTitle the title for the Assigned listbox
	 * @param data EP layout data
	 */
	public CatalogPermissionsDualListBox(final IEpLayoutComposite parent, final CmUser cmUser, final String availableTitle,
			final String assignedTitle, final IEpLayoutData data) {
		super(parent, cmUser, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION, data, EpState.EDITABLE);
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		CmUser cmUser = getModel();
		for (Iterator< ? > it = selection.iterator(); it.hasNext();) {
			cmUser.addCatalog((Catalog) it.next());
		}
		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		
		CmUser cmUser = getModel();
		for (Iterator< ? > it = selection.iterator(); it.hasNext();) {
			cmUser.removeCatalog((Catalog) it.next());
		}
		return true;
	}

	@Override
	public Collection<Catalog> getAssigned() {
		return getModel().getCatalogs();
	}

	@Override
	public Collection<Catalog> getAvailable() {
		CatalogService service = (CatalogService) ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
		return service.findAllCatalogs();
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new CatalogPermissionsAvailableViewerFilter();
	}

	/**
	 * Filters the AvailableListView so that it doesn't display any objects that are in the AssignedListView.
	 */
	protected class CatalogPermissionsAvailableViewerFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			long catalogUid = ((Catalog) element).getUidPk();
			final Collection<Catalog> catalogs = getAssigned();
			if (catalogs == null || catalogs.isEmpty()) {
				return true;
			}

			for (Catalog catalog : catalogs) {
				if (catalog.getUidPk() == catalogUid) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Label provider for CatalogPermissions listviewers.
	 */
	class CatalogPermissionsLabelProvider extends LabelProvider {

		@Override
		public String getText(final Object element) {
			if (element instanceof Catalog) {
				return ((Catalog) element).getName();
			}
			return null;
		}

		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			return false;
		}
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new CatalogPermissionsLabelProvider();
	}
}
