/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.actions;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.pricelistmanager.event.PriceListSearchEvent;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.PriceListSearchEventListener;
import com.elasticpath.cmclient.pricelistmanager.views.PriceListSearchResultsView;

/**
 * Listens for {@link PriceListSearchEvent}s and opens
 * the PriceListSearchResultsView.
 */
public class OpenPriceListSearchResultsViewAction extends Action implements PriceListSearchEventListener {
	private static final Logger LOG = Logger.getLogger(OpenPriceListSearchResultsViewAction.class);
	
	@Override
	public void run() {
		LOG.debug("running OpenPriceListSearchResultsView action"); //$NON-NLS-1$
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PriceListSearchResultsView.VIEW_ID);
		} catch (PartInitException ex) {
			LOG.error("Cannot open PriceListSearchResultsView.", ex); //$NON-NLS-1$
		}
	}

	/**
	 * This implementation won't show any tooltip text.
	 * @return the tooltip text for the action
	 */
	@Override
	public String getToolTipText() {
		return StringUtils.EMPTY; //this action will never show up on a button
	}

	@Override
	public void searchPriceList(final PriceListSearchEvent event) {
		run();
	}
}
