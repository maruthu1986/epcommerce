/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.jmock;


import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;

import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseAddressImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.persistence.openjpa.JpaPersistenceEngine;
import com.elasticpath.service.misc.FetchPlanHelper;

/**
 * A parent class for typical service tests that provides a mock <code>PersistenceEngine</code>
 * and a mock <code>FetchPlanHelper</code>.
 */
public abstract class AbstractEPServiceTestCase extends AbstractEPTestCase {


	private EntityManager mockEntityManager;

	private JpaPersistenceEngine mockPersistenceEngine;
	
	private JpaPersistenceEngine persistenceEngine;

	private FetchPlanHelper mockFetchPlanHelper;

	private FetchPlanHelper fetchPlanHelper;

	private Store store;

	/**
	 * Sets up the mock persistence engine with no expectations.
	 * 
	 * @throws Exception if something goes wrong during set up.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mockPersistenceEngine = context.mock(JpaPersistenceEngine.class);
		persistenceEngine = mockPersistenceEngine;

		mockFetchPlanHelper = context.mock(FetchPlanHelper.class);
		fetchPlanHelper = mockFetchPlanHelper;

		mockEntityManager = context.mock(EntityManager.class);
	}
	
	/**
	 * Returns the <code>Mock</code> instance of the <code>PersistenceEngine</code>.
	 * 
	 * @return the <code>Mock</code> instance of the <code>PersistenceEngine</code>.
	 */
	protected JpaPersistenceEngine getMockPersistenceEngine() {
		return mockPersistenceEngine;
	}
	
	protected JpaPersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}
	
	/**
	 * @return the <code>Mock</code> instance of the <code>FetchPlanHelper</code>.
	 */
	protected FetchPlanHelper getMockFetchPlanHelper() {
		return mockFetchPlanHelper;
	}
	
	/**
	 * @return the <code>FetchPlanHelper</code>.
	 */
	protected FetchPlanHelper getFetchPlanHelper() {
		return fetchPlanHelper;
	}

	/**
	 * Returns the default static mocked store.
	 *
	 * @return the default mocked store.
	 */
	protected Store getMockedStore() {
		if (store == null) {
			store = new StoreImpl();
			store.setCode("SAMPLE_STORECODE");
			store.setDefaultLocale(Locale.US);
			store.setDefaultCurrency(Currency.getInstance(Locale.US));
			store.setContentEncoding("UTF-8");

			List<Warehouse> warehouseList = new ArrayList<>();
			Warehouse warehouse = new WarehouseImpl();
			warehouse.setAddress(new WarehouseAddressImpl());
			warehouse.setUidPk(1);
			warehouseList.add(warehouse);
			store.setWarehouses(warehouseList);
		}
		return store;
	}

	protected EntityManager getMockEntityManager() {
		return mockEntityManager;
	}
}
