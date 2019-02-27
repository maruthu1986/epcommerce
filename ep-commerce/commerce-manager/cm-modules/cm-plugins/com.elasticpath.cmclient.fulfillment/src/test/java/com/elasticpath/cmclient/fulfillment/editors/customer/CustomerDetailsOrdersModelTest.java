/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.store.StoreService;


/**
 * Test the CustomerDetailsOrdersModel for correctness of logic
 * and that methods are working correctly.
 */
public class CustomerDetailsOrdersModelTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private Customer customer;

	@Mock
	private Store regStore;

	@Mock
	private StoreService storeService;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private FetchGroupLoadTuner fetchGroupLoadTuner;

	private static final Long SAMPLE_UIDPK = 100L;
	private static final String SAMPLE_CODE = "storecode"; //$NON-NLS-1$
	
	/**
	 * Set up before the test case is executed.
	 * @throws Exception exception on setup
	 */
	@Before
	public void setUp() throws Exception {
		when(customer.getStoreCode()).thenReturn(SAMPLE_CODE);
		when(regStore.getUidPk()).thenReturn(SAMPLE_UIDPK);
		when(regStore.getName()).thenReturn("Registered Store").thenReturn("new store"); //$NON-NLS-1$ //$NON-NLS-2$
		when(regStore.getAssociatedStoreUids()).thenReturn(Collections.singleton(SAMPLE_UIDPK));
		when(regStore.compareTo(regStore)).thenReturn(1);
		ServiceLocator.setBeanFactory(beanFactory);
		when(beanFactory.getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER)).thenReturn(fetchGroupLoadTuner);
		when(storeService.getTunedStore(SAMPLE_CODE, fetchGroupLoadTuner)).thenReturn(regStore);
		when(storeService.getTunedStores(Collections.singleton(SAMPLE_UIDPK), fetchGroupLoadTuner)).thenReturn(Collections.singleton(regStore));
	}
	
	/**
	 * Method that checks the condition when both stores associated with the customer
	 * are in the open store states, the CM user is authorized to view both stores.
	 */
	@Test
	public void testGetAccessableStoreNamesTwoOpenStores() {

		Set<Store> storeList = new HashSet<>();
		CustomerDetailsOrdersModel ordersModel = new CustomerDetailsOrdersModel() {
			@Override
			protected void init() {
				//do nothing
			}
			
			@Override
			protected boolean storeStateCheckNotUnderConstruction(final Store store) {
				return true;
			}
			
			@Override
			protected boolean storeAuthorizationCheck(final Store store) {
				return true;
			}
			
			@Override
			protected StoreService getStoreService() {
				return storeService;
			}
		};
		
		String[] stores = ordersModel.getAccessableStoreNames(customer, storeList);
		assertEquals("There should be two stores, both are authorized and both are NOT under construction",  //$NON-NLS-1$
				stores.length, 2);
	}
	
	/**
	 * Method that checks the condition when one stores (the registered store) associated
	 * with the customer is in the store state open but the other is in the under construction 
	 * state, the CM user is not authorized to view any stores.
	 */
	@Test
	public void testGetAccessableStoreNamesOpenUnderConstruction() {
		
		Set<Store> storeList = new HashSet<>();
		CustomerDetailsOrdersModel ordersModel = new CustomerDetailsOrdersModel() {
			@Override
			protected void init() {
				//do nothing
			}
			
			@Override
			protected boolean storeStateCheckNotUnderConstruction(final Store store) {
				return false;
			}
			
			@Override
			protected boolean storeAuthorizationCheck(final Store store) {
				return false;
			}
			
			@Override
			protected StoreService getStoreService() {
				return storeService;
			}
		};
		
		String[] stores = ordersModel.getAccessableStoreNames(customer, storeList);
		assertEquals("There should be zero stores, both are not authorized", stores.length, 0); //$NON-NLS-1$
	}
	
	/**
	 * Method that checks the condition when one stores (the registered store) associated
	 * with the customer is in the store state open but the other is in the under construction 
	 * state, the CM user is able to view all stores.
	 */
	@Test
	public void testGetAccessableStoreNamesOpenAuthorizedUnderConstruction() {
		
		Set<Store> storeList = new HashSet<>();
		CustomerDetailsOrdersModel ordersModel = new CustomerDetailsOrdersModel() {
			@Override
			protected void init() {
				//do nothing
			}
			
			@Override
			protected boolean storeStateCheckNotUnderConstruction(final Store store) {
				return false;
			}
			
			@Override
			protected boolean storeAuthorizationCheck(final Store store) {
				return true;
			}
			
			@Override
			protected StoreService getStoreService() {
				return storeService;
			}
		};
		
		String[] stores = ordersModel.getAccessableStoreNames(customer, storeList);
		assertEquals("There should be one stores, both are authorized but only one is NOT under construction", //$NON-NLS-1$
				stores.length, 1);
	}
	
	/**
	 * Method that checks the condition when one stores (the registered store) associated
	 * with the customer is in the store state open but the other is in the under construction 
	 * state, the CM user is not able to view any stores.
	 */
	@Test
	public void testGetAccessableStoreNamesOpenTwoNotAuthorized() {
		
		Set<Store> storeList = new HashSet<>();
		CustomerDetailsOrdersModel ordersModel = new CustomerDetailsOrdersModel() {
			@Override
			protected void init() {
				//do nothing
			}
			
			@Override
			protected boolean storeStateCheckNotUnderConstruction(final Store store) {
				return true;
			}
			
			@Override
			protected boolean storeAuthorizationCheck(final Store store) {
				return false;
			}
			
			@Override
			protected StoreService getStoreService() {
				return storeService;
			}
		};
		
		String[] stores = ordersModel.getAccessableStoreNames(customer, storeList);
		assertEquals("There no stores, both are unauthorized but both are NOT under construction",  //$NON-NLS-1$
				stores.length, 0);
	}
}
