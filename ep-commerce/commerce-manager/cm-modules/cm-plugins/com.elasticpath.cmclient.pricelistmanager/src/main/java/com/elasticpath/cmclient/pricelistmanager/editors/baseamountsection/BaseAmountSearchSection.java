/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.events.SelectionEvent;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.pricelistmanager.event.BaseAmountSearchEvent;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.BaseAmountSearchEventListener;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.settings.SettingsService;

/**
 * 
 * Base amount search section.
 *
 */
public class BaseAmountSearchSection  extends AbstractBaseAmountFilterSection {
	
	private final int numOfResults;

	private AbstractBaseAmountFilterSection dependentSection;
		
	private final List<BaseAmountSearchEventListener> searchPerformedListeners;
	
	/**
	 * Constructor.
	 * @param controller extended base amount filter from controller.
	 */
	public BaseAmountSearchSection(final PriceListEditorController controller) {
		super(controller, controller.getBaseAmountsFilter());
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
		SettingsService settingsService = ServiceLocator.getService(ContextIdNames.SETTINGS_SERVICE);
		numOfResults = 
			Integer.valueOf(settingsService.getSettingValue("COMMERCE/APPSPECIFIC/RCP/PRICING/maximumBaseAmounts").getValue()); //$NON-NLS-1$
		searchPerformedListeners = new ArrayList<>();
		performSearch();
		
	}
	
	/**
	 * Add {@link BaseAmountSearchEventListener}.
	 * @param listener BaseAmountSearchEventListener to add
	 */
	public void addBaseAmountSearchEventListener(final BaseAmountSearchEventListener listener) {
		searchPerformedListeners.add(listener);		
	}
	
	/**
	 * Remove given listener.
	 * @param listener listener to remove.
	 */
	public void removeBaseAmountSearchEventListener(final BaseAmountSearchEventListener listener) {
		searchPerformedListeners.remove(listener);		
	}
	
	/**
	 * Remove all search performed listeners.
	 */
	public void removeAllBaseAmountSearchEventListener() {
		searchPerformedListeners.clear();		
	}
	
	private void baseAbountSearchPerformed(final BaseAmountFilterExt baseAmountFilterExt) {		
		final BaseAmountSearchEvent event = new BaseAmountSearchEvent(baseAmountFilterExt);
		
		for (BaseAmountSearchEventListener listener : searchPerformedListeners) {
			listener.searchBaseAmounts(event);
		}
		
	}
	


	
	@Override
	protected String getSearchButtonLabel() {
		return PriceListManagerMessages.get().PriceListBaseAmountSearch_Search;
		
	}
	
	/**
	 * Perform search operation.
	 */
	public final void performSearch() {
		
		// only need to search if model is persistent
		getFilterExt().setLimit(numOfResults);
		final ChangeSetObjects<BaseAmountDTO> changeSet = getController().getModel().getChangeSet();
		if (StringUtils.isNotBlank(getController().getModel().getPriceListDescriptor().getGuid())) {
			getController().reloadModel();
		}
		getController().getModel().setChangeSet(changeSet);
		if (getBaseAmountTableViewer() != null) {
			getBaseAmountTableViewer().setInput(getController());							
		}
		performClearOnClientFilter();
		
		baseAbountSearchPerformed(getFilterExt());
		
	}

	private void performClearOnClientFilter() {
		if (dependentSection != null) {
			dependentSection.doClear(null);
		}
	}
	
	@Override
	protected void onSearch(final SelectionEvent event, final BaseAmountFilterExt baseAmountFilterExt) {
		if (getController().isModelPersistent()) {
			performSearch();						
		}
	}
	
	@Override
	protected void onClear(final SelectionEvent event, final BaseAmountFilterExt baseAmountFilterExt) {
		baseAmountFilterExt.setLimit(numOfResults);
		
		performClearOnClientFilter();

		final ChangeSetObjects<BaseAmountDTO> changeSet = getController().getModel().getChangeSet();
		//if price list in the model is unsaved it will be forced to create new one
		//it's no need to reload if new and not saved
		if (getController().getModel().getPriceListDescriptor() != null
			&& StringUtils.isNotEmpty(getController().getModel().getPriceListDescriptor().getGuid())) {
			getController().reloadModel();
		}
		getController().getModel().setChangeSet(changeSet);
		if (getBaseAmountTableViewer() != null) {
			getBaseAmountTableViewer().setInput(getController());							
		}
	}

	/**
	 * Set dependant section if any.
	 * @param dependentSection optional dependant section
	 */
	public void setDependentSection(
			final AbstractBaseAmountFilterSection dependentSection) {
		this.dependentSection = dependentSection;
	}
	
	
	

}
