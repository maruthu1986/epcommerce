/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.events.SelectionListener;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Interface for the search tabs.
 */
public interface ISearchTab {

	/**
	 * Called when search button is pressed.
	 */
	void search();

	/**
	 * Called when the form should be cleared. Clear button is pressed.
	 */
	void clear();

	/**
	 * @return boolean
	 */
	boolean hasSearchTermEntered();

	/**
	 * Called when tab is activated.
	 */
	void tabActivated();

	/**
	 * Sets the focus.
	 */
	void setFocus();

	/**
	 * The controls of the search tab should be bound here.
	 * 
	 * @param bindingProvider binding provider
	 * @param context binding context used by the binding provider
	 */
	void bindControls(EpControlBindingProvider bindingProvider, DataBindingContext context);

	/**
	 * @param listener control modification listener
	 */
	void setControlModificationListener(ControlModificationListener listener);

	/**
	 * Returns the View ID that should display the results.
	 * 
	 * @return String
	 */
	String getResultViewId();

	/**
	 * @param listener selection listener
	 */
	void setSelectionListener(SelectionListener listener);

	/**
	 * Check if widget is disposed.
	 *
	 * @return True if widget is disposed.
	 */
	boolean isWidgetDisposed();

	/**
	 * The helper class to pop up the store combo box.
	 */
	@SuppressWarnings({ "PMD.UseSingleton", "PMD.UseUtilityClass" })
	class StoreFilterHelper {
		/**
		 * Users will be able to search any store.
		 * 
		 * @return the store array.
		 */
		protected static List <Store> getAvailableStores() {
			List <Store> resultStores = new ArrayList<>();
			List <Store> accessibleStores = new ArrayList<>();
			StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
			accessibleStores.addAll(storeService.findAllCompleteStores());
			AuthorizationService.getInstance().removeUnathorizedStoresFrom(accessibleStores);
		
			Store allStoreOption = ServiceLocator.getService(ContextIdNames.STORE);
			allStoreOption.setName(FulfillmentMessages.get().SearchView_AllStore);
			resultStores.add(allStoreOption);
			if (!accessibleStores.isEmpty()) { 
				resultStores.addAll(accessibleStores);
			} 
			return resultStores;
		}

		/**
		 * Get all the available Store names from the store array.
		 * 
		 * @param stores the store array
		 * @return the name of the stores
		 */
		protected static String[] getAvailableStoreNames(final List <Store> stores) {
			String[] ret = new String[stores.size()];
			for (int nIndex = 0; nIndex < ret.length; nIndex++) {
					ret[nIndex] = stores.get(nIndex).getName();
			}
			return ret;
		}
	}
}
