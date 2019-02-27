/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.views;

import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;

/**
 * Provides methods for creating the search view.
 */
public class SearchView extends AbstractCmClientView {

	/**
	 * SearchView ID specified in the plugin.xml file. It is the same as the class name.
	 */
	public static final String ID_SEARCH_VIEW = SearchView.class.getName();

	private static final int TAB_CHANGESETS = 0;

	private ChangeSetSearchTab changeSetSearchTab;

	private IEpTabFolder tabFolder;

	@Override
	protected void createViewPartControl(final Composite parentComposite) {

		// Create the container for the view
		final IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parentComposite, 1, false);

		// Create the tab container
		tabFolder = parentEpComposite.addTabFolder(parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		changeSetSearchTab = new ChangeSetSearchTab(tabFolder, TAB_CHANGESETS, this);
		tabFolder.setSelection(TAB_CHANGESETS);
	}

	@Override
	protected Object getModel() {
		return null;
	}

	@Override
	public void setFocus() {
		if (tabFolder.getSelectedTabIndex() == TAB_CHANGESETS) {
			changeSetSearchTab.setFocus();
		}
	}

	@Override
	protected String getPartId() {
		return ID_SEARCH_VIEW;
	}
}