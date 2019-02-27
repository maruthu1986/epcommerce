/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.store.StoreService;

/**
 * Order model which contains the logic used for the order tab of the customer profile,
 * and the logic is separate from the user interface.
 */
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class CustomerDetailsOrdersModel {

	private StoreService storeService;

	/**
	 * Constructor.
	 */
	public CustomerDetailsOrdersModel() {
		init();
	}

	/**
	 * Initialize the store service.
	 */
	protected void init() {
		storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
	}

	/**
	 * Gets all stores that the CSR has permission to access and that the customer can access.
	 * This is dependent on the Customer's registered store's login sharing settings. The store
	 * service must be set before this method is called.
	 * @param customer customer for which the accessible stores are retrieved
	 * @param storeList list which will be populated with the stores that will map to the drop down box, should be an empty set initially
	 * @return String[] containing names of the stores
	 */
	public String[] getAccessableStoreNames(final Customer customer, final Set<Store> storeList) {
		final Set<String> accessibleStoreNames = new TreeSet<>();

		final FetchGroupLoadTuner fetchGroupLoadTuner = ServiceLocator.getService(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		fetchGroupLoadTuner.addFetchGroup(FetchGroupConstants.STORE_SHARING);
		final Store registeredStore = getStoreService().getTunedStore(customer.getStoreCode(), fetchGroupLoadTuner);

		if (storeAuthorizationCheck(registeredStore)) {
			accessibleStoreNames.add(registeredStore.getName());
			storeList.add(registeredStore);
		}

		Collection<Store> sharedLoginStores = getStoreService().getTunedStores(registeredStore.getAssociatedStoreUids(), fetchGroupLoadTuner);
		if (sharedLoginStores != null) {
			for (Store sharedStore : sharedLoginStores) {
				// attempt to add every shared store to the list
				if (storeAuthorizationCheck(sharedStore) && storeStateCheckNotUnderConstruction(sharedStore)) {
					// ensure the CM user has permission to view the store we want to add
					accessibleStoreNames.add(sharedStore.getName());
					storeList.add(sharedStore);
				}
			}
		}
		return accessibleStoreNames.toArray(new String[accessibleStoreNames.size()]);
	}

	/**
	 * Checks to to ensure that the user is authorized to view the store.
	 * @param store stores to check for authorization
	 * @return boolean depending if you are able to create an order or not for this store
	 */
	protected boolean storeAuthorizationCheck(final Store store) {
		return AuthorizationService.getInstance().isAuthorizedForStore(store);
	}

	/**
	 * Checks the store state to ensure that the user is authorized to view the store and the store state
	 * is not currently under construction, a state for which you should not be able to create orders.
	 * @param store store to the store state
	 * @return boolean depending if the store is under construction (false) or not (true)
	 */
	protected boolean storeStateCheckNotUnderConstruction(final Store store) {
		StoreState state = store.getStoreState();
		return !state.equals(StoreState.UNDER_CONSTRUCTION);
	}

	/**
	 * Gets the store service.
	 * @return the store service
	 */
	protected StoreService getStoreService() {
		return storeService;
	}

}