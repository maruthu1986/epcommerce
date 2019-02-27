/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shopper.impl;

import java.util.UUID;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.ObjectNotExistException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shopper.dao.ShopperDao;
import com.elasticpath.service.store.StoreService;

/** Service manipulating Shopper. */
public class ShopperServiceImpl implements ShopperService {

	private final BeanFactory beanFactory;

	private final ShopperDao shopperDao;
	private final StoreService storeService;

	/**
	 * Main constructor.
	 *
	 * @param beanFactory bean factory for constructing needed beans.
	 * @param shopperDao Dao used to manipulate and persist the shopping context.
	 * @param storeService store service used to fetch valid store code.
	 */
	public ShopperServiceImpl(final BeanFactory beanFactory, final ShopperDao shopperDao, final StoreService storeService) {
		super();
		this.beanFactory = beanFactory;
		this.shopperDao = shopperDao;
		this.storeService = storeService;
	}

	@Override
	public Shopper findByPersistedCustomerSessionMemento(final CustomerSessionMemento customerSessionMemento) {
		if (customerSessionMemento == null) {
			throw new IllegalArgumentException("CustomerSession cannot be null.");
		}

		if (!customerSessionMemento.isPersisted()) {
			throw new IllegalArgumentException("CustomerSession must be persisted.");
		}

		final ShopperMemento retrievedShopperMemento = shopperDao.get(customerSessionMemento.getShopperUid());

		if (retrievedShopperMemento == null) {
			throw new ObjectNotExistException(String.format("Shopper (uid:%d) should exist for CustomerSession (guid: %s)",
					customerSessionMemento.getShopperUid(),
					customerSessionMemento.getGuid()));
		}

		return createNewShopperFromMemento(retrievedShopperMemento);
	}

	@Override
	public Shopper findByCustomerGuid(final String customerGuid) {
		ShopperMemento shopperMemento = shopperDao.findByCustomerGuid(customerGuid);

		if (shopperMemento == null) {
			return null;
		}
		return createNewShopperFromMementoAndStore(shopperMemento, shopperMemento.getStoreCode());
	}

	@Override
	public Shopper findByCustomerGuidAndStoreCode(final String customerGuid, final String storeCode) {
		ShopperMemento shopperMemento = shopperDao.findByCustomerGuidAndStoreCode(customerGuid, storeCode);

		if (shopperMemento == null) {
			return null;
		}
		return createNewShopperFromMementoAndStore(shopperMemento, shopperMemento.getStoreCode());
	}

	@Override
	public Shopper findByCustomerUserIdAndStoreCode(final String customerUserId, final String storeCode) {
		ShopperMemento shopperMemento = shopperDao.findByCustomerUserIdAndStoreCode(customerUserId, storeCode);

		if (shopperMemento == null) {
			return null;
		}
		return createNewShopperFromMementoAndStore(shopperMemento, shopperMemento.getStoreCode());
	}
	
	@Override
	public Shopper findOrCreateShopper(final Customer customer, final String storeCode) {
		if (customer == null) {
			throw new IllegalArgumentException("Customer cannot be null.");
		}

		ShopperMemento shopperMemento = shopperDao.findByCustomerAndStoreCode(customer, storeCode);

		if (shopperMemento == null) {
			// create a new one.
			final String validStoreCode = storeService.findValidStoreCode(storeCode);
			shopperMemento = createAndSaveNewShoppingMementoWithCustomerAndStore(customer, validStoreCode);
		}

		return createNewShopperFromMementoAndStore(shopperMemento, shopperMemento.getStoreCode());
	}

	private ShopperMemento createAndSaveNewShoppingMementoWithCustomerAndStore(final Customer customer, final String storeCode) {
		ShopperMemento shopperMemento = createShopperMemento();
		shopperMemento.setCustomer(customer);
		shopperMemento.setStoreCode(storeCode);
		return shopperDao.saveOrUpdate(shopperMemento);
	}

	@Override
	public Shopper createAndSaveShopper(final String storeCode) {
		final ShopperMemento shopperMemento = createShopperMemento();
		final Shopper shopper = createNewShopperFromMementoAndStore(shopperMemento, storeCode);
		shopper.setShopperMemento(shopperDao.saveOrUpdate(shopperMemento));

		return shopper;
	}

	@Override
	public Shopper get(final long uid) {
		final ShopperMemento retrievedShopperMemento = shopperDao.get(uid);
		if (retrievedShopperMemento == null) {
			return null;
		}

		return createNewShopperFromMemento(retrievedShopperMemento);
	}

	@Override
	public Shopper save(final Shopper shopper) {
		if (shopper != null) {
			final ShopperMemento persistedShopperMemento = shopperDao.saveOrUpdate(shopper.getShopperMemento());
			shopper.setShopperMemento(persistedShopperMemento);

			return shopper;
		}
		return null;
	}

	@Override
	public void remove(final Shopper shopper) {
		if (shopper != null && shopper.isPersisted()) {
			shopperDao.remove(shopper.getShopperMemento());
		}
	}

	@Override
	public void removeIfOrphaned(final Shopper shopper) {
		if (shopper != null && shopper.isPersisted()) {
			shopperDao.removeIfOrphaned(shopper.getShopperMemento());
		}
	}

	private Shopper createNewShopperFromMemento(final ShopperMemento shopperMemento) {
		return createNewShopperFromMementoAndStore(shopperMemento, shopperMemento.getStoreCode());
	}

	private Shopper createNewShopperFromMementoAndStore(final ShopperMemento shopperMemento, final String storeCode) {
		final Shopper shopper = beanFactory.getBean(ContextIdNames.SHOPPER);
		shopper.setShopperMemento(shopperMemento);
		shopper.setStoreCode(storeCode);

		return shopper;
	}

	private ShopperMemento createShopperMemento() {
		final ShopperMemento newShopperMemento = beanFactory.getBean(ContextIdNames.SHOPPER_MEMENTO);
		final String mementoGuid = getNewGuid();
		newShopperMemento.setGuid(mementoGuid);
		return newShopperMemento;
	}

	private String getNewGuid() {
		final UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

}
