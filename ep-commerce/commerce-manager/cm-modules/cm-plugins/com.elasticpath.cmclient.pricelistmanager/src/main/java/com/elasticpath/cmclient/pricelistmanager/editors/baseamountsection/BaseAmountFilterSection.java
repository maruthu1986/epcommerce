/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import org.eclipse.swt.events.SelectionEvent;

import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;

/**
 * 
 * Base amount filter section.
 *
 */
public class BaseAmountFilterSection  extends AbstractBaseAmountFilterSection {
		
	
	/**
	 * Constructor.
	 * @param controller extended base amount filter from controller.
	 */
	public BaseAmountFilterSection(final PriceListEditorController controller) {
		super(controller, controller.getBaseAmountsUiFilter());
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);		
	}


	
	@Override
	protected String getSearchButtonLabel() {
		return PriceListManagerMessages.get().PriceListBaseAmountFilter_Search;
		
	}
	
	@Override
	protected void onSearch(final SelectionEvent event, final BaseAmountFilterExt baseAmountFilterExt) {
		removeAllViewerFilter();				
		BaseAmountViewerFilter viewerFilter = new BaseAmountViewerFilter(baseAmountFilterExt);				
		getBaseAmountTableViewer().getSwtTableViewer().addFilter(viewerFilter);
	}
	
	
	@Override
	protected void onClear(final SelectionEvent event, final BaseAmountFilterExt baseAmountFilterExt) {
		removeAllViewerFilter();				
	}

}
