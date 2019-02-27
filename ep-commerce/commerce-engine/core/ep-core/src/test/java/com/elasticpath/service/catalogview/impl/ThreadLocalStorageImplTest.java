/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Ensure the ThreadLocalStoreConfig works as intended.
 */
public class ThreadLocalStorageImplTest {

	private static final String STORE_ONE = "store1";
	private static final String STORE_TWO = "store2";
	private static final String STORE_THREE = "store3";
	private static final String STORE_PREFIX = "store";
	private final ThreadLocalStorageImpl storeConfig = new ThreadLocalStorageImpl();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setThreadingPolicy(new Synchroniser());
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private final StoreService mockStoreService = context.mock(StoreService.class);

	@SuppressWarnings("unchecked")
	private final SimpleTimeoutCache<String, Store> mockSimpleTimeoutCache = context.mock(SimpleTimeoutCache.class);

	private final Store initialStore = new StoreImpl();

	@Before
	public void before() {

		storeConfig.setStoreCache(mockSimpleTimeoutCache);
	}
	
	/**
	 * Test throwing an exception when store code not set and getStore() is called.
	 */
	@SuppressWarnings("PMD.EmptyCatchBlock")
	@Test
	public void testGetStoreWhenStoreCodeNotSet() {
		try {
			storeConfig.getStore();
			fail("No store code has been set, exception expected.");
		} catch (EpServiceException expected) {
			// This is expected.
		}
	}
	
	/**
	 * Test that getStoreCode returns the store code when one is set.
	 */
	@Test
	public void testGetStoreCode() {
		String storeCode = "MyStoreCode";
		storeConfig.setStoreCode(storeCode);
		assertEquals(storeCode, storeConfig.getStoreCode());
	}	
	
	/**
	 * Test that the correct store is returned when the store code has been set.
	 */
	@Test
	public void testThatStoreIsReturnedCorrectly() {
		context.checking(new Expectations() {
			{

				allowing(mockStoreService).findStoreWithCode(STORE_ONE);
				will(returnValue(initialStore));

				allowing(mockSimpleTimeoutCache).get(STORE_ONE);
				will(returnValue(null));

				allowing(mockSimpleTimeoutCache).put(STORE_ONE, initialStore);
			}
		});
		storeConfig.setStoreService(mockStoreService);
		storeConfig.setStoreCode(STORE_ONE);
		
		Store returnedStore = storeConfig.getStore();
		assertSame(initialStore, returnedStore);
	}
	
	/**
	 * Make sure that caching works as expected. 
	 */
	@Test
	public void testThatStoreIsCachedAfterFirstAccess() {
		context.checking(new Expectations() {
			{

				oneOf(mockStoreService).findStoreWithCode(STORE_TWO);
				will(returnValue(initialStore));

				oneOf(mockSimpleTimeoutCache).get(STORE_TWO);
				will(returnValue(null));

				exactly(2).of(mockSimpleTimeoutCache).get(STORE_TWO);
				will(returnValue(initialStore));

				allowing(mockSimpleTimeoutCache).put(STORE_TWO, initialStore);
			}
		});
		storeConfig.setStoreService(mockStoreService);
		storeConfig.setStoreCode(STORE_TWO);
		
		Store returnedStore = storeConfig.getStore();
		assertSame(initialStore, returnedStore);

		// Second call should not cause 'findStoreWithCode' a second time
		returnedStore = storeConfig.getStore();
		assertSame("Should same store that was returned for the first call", initialStore, returnedStore);

		// Once more to check we haven't invalidated the cache.
		returnedStore = storeConfig.getStore();
		assertSame("Should have same store that was returned for the first call", initialStore, returnedStore);
	}
	
	/**
	 * Test that the correct store is returned when the store code has been set.
	 */
	@SuppressWarnings("PMD.EmptyCatchBlock")
	@Test
	public void testForExceptionIfStoreCannotBeFound() {
		context.checking(new Expectations() {
			{

				allowing(mockStoreService).findStoreWithCode(STORE_THREE);
				will(returnValue(null));

				oneOf(mockSimpleTimeoutCache).get(STORE_THREE);
				will(returnValue(null));

				oneOf(mockSimpleTimeoutCache).put(STORE_THREE, null);
			}
		});
		storeConfig.setStoreService(mockStoreService);
		storeConfig.setStoreCode(STORE_THREE);
		
		try {
			storeConfig.getStore();
			fail("Should have received an exception - store wasn't found");
		} catch (EpServiceException expected) {
			// This is expected
		}
	}
	
	/**
	 * Test retrieving the store with multiple threads running.
	 */
	@Test
	public void testGetStoreDifferentThreads() {
		
		final int numberOfThreadsToTest = 200;
		storeConfig.setStoreService(mockStoreService);

		// Buckets for the test data and test results
		final Map<String, Store> resultMap = Collections.synchronizedMap(new HashMap<String, Store>());
		final List<Store> stores = new ArrayList<>();
		final List<Thread> threads = new ArrayList<>();
		
		// The runnable that will actually do the work
		final Runnable getStoreRunnable = new Runnable() {
			/** Get the thread-local store */
			@Override
			public void run() {
				storeConfig.setStoreCode(Thread.currentThread().getName());
				resultMap.put(Thread.currentThread().getName(), storeConfig.getStore());
			}
		};

		context.checking(new Expectations() {
			{
			// Set up the test data (put it in the buckets)
			for (int x = 0; x < numberOfThreadsToTest; x++) {
				stores.add(new StoreImpl());

				allowing(mockStoreService).findStoreWithCode(STORE_PREFIX + x);
				will(returnValue(stores.get(x)));

				allowing(mockSimpleTimeoutCache).get(STORE_PREFIX + x);
				will(returnValue(stores.get(x)));

				allowing(mockSimpleTimeoutCache).put(STORE_PREFIX + x, stores.get(x));

				threads.add(new Thread(getStoreRunnable, STORE_PREFIX + x));
			}
			}
		});

		// Start and wait for all threads to complete
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException ie) {
				// Doesn't really matter for the test
			}
		}

		// Check each of the threads got the expected results
		for (int x = 0; x < numberOfThreadsToTest; x++) {
			assertSame("Incorrect store returned for thread #" + x, stores.get(x), resultMap.get("store" + x));
		}
	}
	
	/**
	 * Test that getSetting throws an EpServiceException if the store code has not been initialized.
	 */
	@SuppressWarnings("PMD.EmptyCatchBlock")
	@Test
	public void testGetSettingThrowsException() {
		try {
			storeConfig.getSetting(null);
			fail("Expected EpServiceException because store code has not been set.");
		} catch (EpServiceException expected) {
			//Expected
		}
	}
	
	/**
	 * Test that getSetting calls the setting service with the given path and the threadlocal store code,
	 * and returns the service's setting value.
	 */
	@Test
	public void testGetSetting() {
		final String storeCode = "myStoreCode";
		final String path = "COMMERCE/STORE/theme";
		storeConfig.setStoreCode(storeCode);
		final SettingsService mockSettingsService = context.mock(SettingsService.class);
		storeConfig.setSettingsService(mockSettingsService);
		final SettingValue settingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(mockSettingsService).getSettingValue(path, storeCode);
				will(returnValue(settingValue));
			}
		});
		assertSame(settingValue, storeConfig.getSetting(path));
	}
}
