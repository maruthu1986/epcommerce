/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.pricelistmanager.views;


import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;
import com.elasticpath.cmclient.pricelistassignments.views.PriceListAssigmentsSearchTab;

/**
 * The search view contains 1 or more tabs. Each tab has a composite that allows one to search for
 * objects, the results of which will show up in the search results view.
 * This implementation creates the tab folder and all the tabs. It also wraps composites
 * to be shown within the tabs in scrolled composites.
 */
public class PriceListSearchView extends AbstractCmClientView {
	/**
	 * View ID.
	 */
	public static final String ID_PRICELISTSEARCH_VIEW = "com.elasticpath.cmclient.pricelistmanager.views.PriceListSearchView";

	private static final int TAB_PRICELIST = 0;

	private static final int TAB_PRICELIST_ASSIGNMENT = 1;

	private IEpTabFolder tabFolder;

	/**
	 * The constructor.
	 */
	public PriceListSearchView() {
		super();
	}

	@Override
	protected void createViewPartControl(final Composite parentComposite) {
		// Create the container for the view
		final IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parentComposite, 1, false);

		// Create the tab container
		tabFolder = parentEpComposite.addTabFolder(parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		new PriceListSearchTab(tabFolder, TAB_PRICELIST);
		new PriceListAssigmentsSearchTab(tabFolder, TAB_PRICELIST_ASSIGNMENT);
		tabFolder.setSelection(TAB_PRICELIST);

		EPTestUtilFactory.getInstance().getTestIdUtil().setTestIdsToTabFolderItems(tabFolder);
	}

	@Override
	public void setFocus() {
		//Do nothing.
	}

	@Override
	protected Object getModel() {
		return null;
	}

	@Override
	protected String getPartId() {
		return ID_PRICELISTSEARCH_VIEW;
	}
}