/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.customer.impl;

import java.util.Currency;
import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.customer.CustomerSessionShopperUpdateHandler;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;

/**
 * Service for retrieving and saving CustomerSessions.
 *
 * Note: CustomerSessions being returned do not have a ShoppingCart attached to them.
 */
public class CustomerSessionServiceImpl extends AbstractEpPersistenceServiceImpl implements CustomerSessionService {

	private static final String SHOPPING_START_TIME_TAG = "SHOPPING_START_TIME";

	private static final String SELLING_CHANNEL_TAG = "SELLING_CHANNEL";

	private List<CustomerSessionShopperUpdateHandler> customerSessionShopperUpdateHandlers;

	private ShopperService shopperService;

	private TimeService timeService;

	/**
	 * Adds the given customer session.
	 *
	 * @param customerSession the customer session to add
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public void add(final CustomerSession customerSession) throws EpServiceException {
		getPersistenceEngine().save(customerSession.getCustomerSessionMemento());
	}

	@Override
	public void update(final CustomerSession customerSession) throws EpServiceException {

		final CustomerSessionMemento previousCustomerSessionMemento = customerSession.getCustomerSessionMemento();
		final CustomerSessionMemento updatedCustomerSessionMemento =
				getPersistenceEngine().update(previousCustomerSessionMemento);

		customerSession.setCustomerSessionMemento(updatedCustomerSessionMemento);
	}

	@Override
	public void handleShopperChangeAndUpdate(final CustomerSession customerSession, final String storeCode) throws EpServiceException {
		final Customer incomingCustomer = customerSession.getShopper().getCustomer();
		changeShopper(customerSession, storeCode, incomingCustomer);
	}

	@Override
	public void changeFromAnonymousToRegisteredCustomer(final CustomerSession customerSession, final Customer registeredCustomer,
														final String storeCode) throws EpServiceException {
		customerSession.setSignedIn(true);
		changeShopper(customerSession, storeCode, registeredCustomer);
	}

	private void changeShopper(final CustomerSession customerSession, final String storeCode, final Customer incomingCustomer) {
		final Shopper invalidatedShopper = customerSession.getShopper();
		final Shopper currentShopper = shopperService.findOrCreateShopper(incomingCustomer, storeCode);
		customerSession.setShopper(currentShopper);

		currentShopper.updateTransientDataWith(customerSession);
		updateCustomerSessionForShopperChange(customerSession);

		// Call list of CustomerSessionShopperUpdateHandlers.
		handleShopperUpdate(customerSession, invalidatedShopper);

		getCustomerSessionService().update(customerSession);
		cleanupShopper(invalidatedShopper, customerSession.getShopper());
		shopperService.save(customerSession.getShopper());
	}

	/**
	 * Updates the {@link CustomerSession} in case of a Shopper change.
	 *
	 * @param customerSession a {@link CustomerSession}
	 */
	protected void updateCustomerSessionForShopperChange(final CustomerSession customerSession) {
		// Nothing needs to be done in the stock implementation.
		customerSession.setPriceListStackValid(false);
	}

	private void cleanupShopper(final Shopper invalidatedShopper, final Shopper currentShopper) {
		if (!currentShopper.equals(invalidatedShopper)) {
			shopperService.removeIfOrphaned(invalidatedShopper);
		}
	}

	@Override
	public void updateCustomerAndSave(final CustomerSession customerSession, final Customer customer) throws EpServiceException {
		customerSession.getShopper().setCustomer(customer);
		update(customerSession);
	}

	/**
	 * Finds a {@link CustomerSessionMemento} just by its GUID.
	 *
	 * @param guid {@link CustomerSessionMemento} GUID.
	 * @return {@link CustomerSessionMemento} or null.
	 * @throws EpServiceException - when guid is null.
	 */
	private CustomerSessionMemento findMementoByGuid(final String guid) throws EpServiceException {
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null guid.");
		}

		final List<CustomerSessionMemento> results = getPersistenceEngine().retrieveByNamedQuery("CUSTOMER_SESSION_FIND_BY_GUID", guid);

		if (results.isEmpty()) {
			return null;
		}

		return results.get(0);
	}


	@Override
	public CustomerSession findByGuid(final String guid) throws EpServiceException {
		final CustomerSessionMemento customerSessionMemento = findMementoByGuid(guid);

		if (customerSessionMemento == null) {
			return null;
		}

		return recreatePersistedCustomerSessionWithShopper(customerSessionMemento);
	}



	/**
	 * Find the customer session with the given userId and storeCode. If more than
	 * one session is found that matches the criteria, returns only one.
	 * The Store value that is saved on the Customer is actually the store where the client was created,
	 * is not actually the store associated with the session.
	 * Should be used the same select that is used in <code>CustomerService.findByUserId</code>
	 *
	 * @param userId the customer userId
	 * @param storeCode the code for the store in which the customer should have a session
	 * @return the customer session if guid address exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public CustomerSession findByCustomerIdAndStoreCode(final String userId, final String storeCode) throws EpServiceException {
		if (userId == null || storeCode == null) {
			throw new EpServiceException("Both CustomerId and StoreCode must be supplied.");
		}

		final List<CustomerSessionMemento> results = getPersistenceEngine()
			.retrieveByNamedQuery("CUSTOMER_SESSION_FIND_BY_CUSTOMER_USERNAME_AND_STORE_CODE", new Object[] { userId, storeCode }, 0, 1);

		if (results.isEmpty()) {
			return null;
		}

		// get the first result. there can be more than one valid session for a given username
		final CustomerSessionMemento customerSessionMemento = results.get(0);

		// The JPQL query finds the store through the customerInternal, so both customerInternal and store must be filled.

		return recreatePersistedCustomerSessionWithShopper(customerSessionMemento);
	}


	/**
	 * Load the customer session with the given UID.
	 *
	 * @param customerSessionUid the customer session UID
	 * @return the customer session if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	private CustomerSession load(final long customerSessionUid) throws EpServiceException {
		if (customerSessionUid <= 0) {
			return null;
		}

		final CustomerSessionMemento customerSessionMemento = getPersistentBeanFinder().load(
				ContextIdNames.CUSTOMER_SESSION_MEMENTO, customerSessionUid);
		if (customerSessionMemento == null) {
			return null;
		}

		return recreatePersistedCustomerSessionWithShopper(customerSessionMemento);
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return load(uid);
	}

	/**
	 * Fires customer session updated event.
	 *
	 * @param customerSession the customer session that was updated.
	 * @param invalidatedShopper the recently invalidated shopping context.
	 */
	void handleShopperUpdate(final CustomerSession customerSession, final Shopper invalidatedShopper) {
		for (CustomerSessionShopperUpdateHandler handler : customerSessionShopperUpdateHandlers) {
			handler.invalidateShopper(customerSession, invalidatedShopper);
		}
	}

	@Override
	public CustomerSession createWithShopper(final Shopper shopper) {
		final CustomerSession customerSession = create();

		customerSession.setShopper(shopper);
		shopper.updateTransientDataWith(customerSession);

		return customerSession;
	}
	
	@Override
	public CustomerSession initializeCustomerSessionForPricing(final CustomerSession customerSession, final String storeCode, 
			final Currency currency) {
		TagSet tagSet = getBean(ContextIdNames.TAG_SET);
		tagSet.addTag(SELLING_CHANNEL_TAG, new Tag(storeCode));
		tagSet.addTag(SHOPPING_START_TIME_TAG, new Tag(getTimeService().getCurrentTime().getTime()));
		customerSession.setCustomerTagSet(tagSet);
		customerSession.setCurrency(currency);
		return customerSession;
	}

	/**
	 * Creates a new {@link CustomerSession}.  Does not persist it.
	 *
	 * WARNING: Does not attach a Shopper to it!
	 *
	 * @return a new {@link CustomerSession} with no {@link Shopper}.
	 */
	private CustomerSession create() {
		final CustomerSession customerSession = createEmpty();
		final CustomerSessionMemento customerSessionMemento = getBean(ContextIdNames.CUSTOMER_SESSION_MEMENTO);
		customerSession.setCustomerSessionMemento(customerSessionMemento);

		return customerSession;
	}

	private CustomerSession recreatePersistedCustomerSessionWithShopper(final CustomerSessionMemento persistentData) {
		final CustomerSession customerSession = createEmpty();
		customerSession.setCustomerSessionMemento(persistentData);

		final Shopper shopper = shopperService.findByPersistedCustomerSessionMemento(persistentData);
		attachShopper(customerSession, shopper);

		return customerSession;
	}

	private CustomerSession createEmpty() {
		return getBean(ContextIdNames.CUSTOMER_SESSION);
	}

	private void attachShopper(final CustomerSession customerSession, final Shopper shopper) {
		customerSession.setShopper(shopper);
		shopper.updateTransientDataWith(customerSession);
	}

	// Settings/Getters.
	// ------------------

	/**
	 * Sets shopperService.
	 *
	 * @param shopperService the service to set.
	 */
	public void setShopperService(final ShopperService shopperService) {
		this.shopperService = shopperService;
	}

	protected CustomerSessionService getCustomerSessionService() {
		return getBean(ContextIdNames.CUSTOMER_SESSION_SERVICE);
	}

	/**
	 * Sets the list of CustomerSessionUpdateHandlers.
	 * @param customerSessionShopperUpdateHandlers listeners to set
	 */
	public void setCustomerSessionUpdateHandlers(
			final List<CustomerSessionShopperUpdateHandler> customerSessionShopperUpdateHandlers) {
		this.customerSessionShopperUpdateHandlers = customerSessionShopperUpdateHandlers;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	protected TimeService getTimeService() {
		return timeService;
	}
}
