/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.domain.store.StoreType;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.service.command.CommandService;
import com.elasticpath.service.command.UpdateStoreCommand;
import com.elasticpath.service.command.UpdateStoreCommandResult;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Verifies process of sharing stores between each other and general operations such as flush.
 */
public class StoreEditorModelHelperTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public TestContext context = new TestContext();

	private static final String STORE_CODE = "store_code"; //$NON-NLS-1$

	@Mock
	private StoreService mockStoreService;

	@Mock
	private Store newStore;

	/**
	 * Tests the StoreEditorModelHelper.loadStore method.
	 */
	@Test
	public void testLoadStore() {
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper() {
			@Override
			protected Store createStore() {
				return newStore;
			}

			@Override
			Locale getDefaultLocale() {
				return Locale.CANADA;
			}
		};

		final Store store = mock(Store.class);

		when(mockStoreService.getTunedStore(1, null)).thenReturn(store);
		editorModelHelper.setStoreService(mockStoreService);
		assertSame(store, editorModelHelper.loadStore(1L, null));
		verify(mockStoreService).getTunedStore(1, null);

		when(mockStoreService.getTunedStore(2, null)).thenReturn(null);
		try {
			editorModelHelper.loadStore(2L, null);
			fail("exception should be thrown"); //$NON-NLS-1$
		} catch (EpServiceException exceptionExpected) {
			assertNotNull(exceptionExpected);
		}
		verify(mockStoreService).getTunedStore(2, null);

		assertSame(newStore, editorModelHelper.loadStore(0L, null));
		verify(newStore).setStoreType(StoreType.B2B);
		verify(newStore).setCountry(Locale.CANADA.getCountry());
		verify(newStore).setEnabled(true);
	}

	/**
	 * Tests the StoreEditorModelHelper.loadStoreWithSharedStores method.
	 */
	@Test
	public void testLoadStoreWithSharedStores() {
		final Store mockStore = mock(Store.class);
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper() {
			@Override
			Store loadStore(final long storeUid, final FetchGroupLoadTuner loadTuner) {
				assertEquals(1L, storeUid);
				assertNull(loadTuner);
				return mockStore;
			}
			@Override
			Collection<Store> loadSharedStores(final Store store) {
				return Collections.emptySet();
			}
		};

		assertSame(mockStore, editorModelHelper.loadStoreWithSharedStores(1L, null));
	}

	/**
	 * Tests the StoreEditorModelHelper.loadSettings method.
	 */
	@Test
	public void testLoadSettings() {
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper() {
			@Override
			List<SettingModel> getMarketingSettings(final StoreEditorModel model) {
				return Collections.emptyList();
			}

			@Override
			List<SettingModel> getSystemSettings(final StoreEditorModel model) {
				return Collections.emptyList();
			}
		};

		final SettingsService mockSettingsService = mock(SettingsService.class);
		editorModelHelper.setSettingsService(mockSettingsService);

		final SettingValue mockThemeValue = mock(SettingValue.class, "theme setting");
		final SettingValue mockBrowsingValue = mock(SettingValue.class, "browsing setting");
		final SettingValue mockAsValue = mock(SettingValue.class, "adv search setting");
		final SettingValue mockDataPolicyValue = mock(SettingValue.class, "data policy setting");
		final Store mockStore = mock(Store.class);
		final String themeValue = "themeValue"; //$NON-NLS-1$
		final String browsingValue = "browsingValue"; //$NON-NLS-1$
		final String asValue = "asValue"; //$NON-NLS-1$
		final Boolean dataPolicyValue = false; //$NON-NLS-1$

		when(mockThemeValue.getValue()).thenReturn(themeValue);
		when(mockBrowsingValue.getValue()).thenReturn(browsingValue);
		when(mockAsValue.getValue()).thenReturn(asValue);
		when(mockDataPolicyValue.getBooleanValue()).thenReturn(dataPolicyValue);
		when(mockSettingsService.getSettingValue("COMMERCE/STORE/theme", STORE_CODE)).thenReturn(mockThemeValue);
		when(mockSettingsService.getSettingValue("COMMERCE/STORE/FILTEREDNAVIGATION/filteredNavigationConfiguration", STORE_CODE))
				.thenReturn(mockBrowsingValue);
		when(mockSettingsService.getSettingValue("COMMERCE/STORE/ADVANCEDSEARCH/advancedSearchConfiguration", STORE_CODE)).thenReturn(mockAsValue);
		when(mockSettingsService.getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE)).thenReturn(mockDataPolicyValue);

		when(mockStore.getCode()).thenReturn(STORE_CODE);

		StoreEditorModel editorModel = new StoreEditorModel(mockStore);
		editorModelHelper.loadSettings(editorModel);

		assertTrue(editorModel.getMarketingSettings().isEmpty());
		assertTrue(editorModel.getSystemSettings().isEmpty());
		assertEquals(themeValue, editorModel.getStoreThemeSetting());
		assertEquals(browsingValue, editorModel.getStoreBrowsingSetting());
		assertEquals(asValue, editorModel.getStoreAdvancedSearchSetting());
		assertEquals(dataPolicyValue, editorModel.isStoreEnableDataPoliciesSettingEnabled());

		verify(mockThemeValue).getValue();
		verify(mockBrowsingValue).getValue();
		verify(mockAsValue).getValue();
		verify(mockDataPolicyValue).getBooleanValue();
		verify(mockSettingsService).getSettingValue("COMMERCE/STORE/theme", STORE_CODE);
		verify(mockSettingsService).getSettingValue("COMMERCE/STORE/FILTEREDNAVIGATION/filteredNavigationConfiguration", STORE_CODE);
		verify(mockSettingsService).getSettingValue("COMMERCE/STORE/ADVANCEDSEARCH/advancedSearchConfiguration", STORE_CODE);
		verify(mockSettingsService).getSettingValue(DataPolicyService.COMMERCE_STORE_ENABLE_DATA_POLICIES, STORE_CODE);
	}

	/**
	 * Tests the StoreEditorModelHelper.destroyModel method.
	 */
	@Test
	public void testDestroyModel() {
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper();

		final Store mockStore = mock(Store.class);
		editorModelHelper.setStoreService(mockStoreService);

		StoreEditorModel editorModel = new StoreEditorModel(mockStore);
		editorModelHelper.destroyModel(editorModel);
		verify(mockStoreService).remove(mockStore);
	}

	/**
	 * Tests the StoreEditorModelHelper.reload method.
	 */
	@Test
	public void testReload() {
		final String themeValue = "themeValue"; //$NON-NLS-1$
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper() {
			@Override
			Store loadStoreWithSharedStores(final long storeUid, final FetchGroupLoadTuner loadTuner) {
				assertEquals(1L, storeUid);
				assertNull(loadTuner);
				return newStore;
			}

			@Override
			void loadSettings(final StoreEditorModel model) {
				model.setStoreThemeSetting(themeValue);
			}
		};

		final Store mockOldStore = mock(Store.class);
		when(mockOldStore.getUidPk()).thenReturn(1L);

		StoreEditorModel editorModel = new StoreEditorModel(mockOldStore);
		editorModelHelper.reload(editorModel);
		assertEquals(themeValue, editorModel.getStoreThemeSetting());
		assertSame(newStore, editorModel.getStore());
		verify(mockOldStore).getUidPk();
	}

	/**
	 * Tests the StoreEditorModelHelper.CheckStoresDifference method.
	 */
	@Test
	public void testCheckStoresDifference() {
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper();
		assertFalse(editorModelHelper.checkStoresDifference(new StoreEditorModel(null), null));

		final Store mockStore = mock(Store.class);
		StoreEditorModel model = new StoreEditorModel(mockStore);
		final Store loadedStore = mock(Store.class);

		when(mockStore.getUidPk()).thenReturn(1L);
		when(loadedStore.getUidPk()).thenReturn(2L);

		assertTrue(editorModelHelper.checkStoresDifference(model, loadedStore));
		verify(mockStore).getUidPk();


		final Store mockMatchingStore = mock(Store.class);
		StoreEditorModel matchingModel = new StoreEditorModel(mockMatchingStore);
		when(mockMatchingStore.getUidPk()).thenReturn(2L);
		assertFalse(editorModelHelper.checkStoresDifference(matchingModel, loadedStore));

	}

	/**
	 * Tests the StoreEditorModelHelper.sanityCheck method. Checks the case when store with given code already exists.
	 */
	@Test
	public void testSanityCheckStoreCodeExists() {
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper() {
			@Override
			boolean checkStoresDifference(final StoreEditorModel model, final Store storeByCode) {
				return true;
			}
		};
		editorModelHelper.setStoreService(mockStoreService);
		final Store mockStore = mock(Store.class);
		final Store otherStore = mock(Store.class);
		StoreEditorModel model = new StoreEditorModel(mockStore);

		when(mockStoreService.findStoreWithCode(any(String.class))).thenReturn(otherStore);
		when(mockStore.getCode()).thenReturn(STORE_CODE);

		try {
			editorModelHelper.sanityCheck(model);
			fail("exception shoud be thrown because store with given code already exists"); //$NON-NLS-1$
		} catch (EpServiceException expected) {
			assertNotNull(expected);
		}
		verify(mockStoreService).findStoreWithCode(any(String.class));
	}

	/**
	 * Tests the StoreEditorModelHelper.sanityCheck method. Checks the case when store with given url already exists.
	 */
	@Test
	public void testSanityCheckUrlExists() {
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper() {
			@Override
			boolean checkStoresDifference(final StoreEditorModel model, final Store storeByCode) {
				return false;
			}
		};

		when(mockStoreService.findStoreWithCode(any(String.class))).thenReturn(newStore);
		when(mockStoreService.isStoreUrlUniqueForState(any(Store.class), any(StoreState.class))).thenReturn(true).thenReturn(false).thenReturn(false);
		editorModelHelper.setStoreService(mockStoreService);

		// mock the credit card types set return false when asked if it's empty
		// at least one payment method is required
		final Store mockStore = mock(Store.class);

		final String storeUrl = "http://test.com"; //$NON-NLS-1$
		final Map<PaymentGatewayType, PaymentGateway> paymentGatewayMap = new HashMap<PaymentGatewayType, PaymentGateway>();
		final PaymentGateway mockPaymentGateway = mock(PaymentGateway.class);
		paymentGatewayMap.put(PaymentGatewayType.HOSTED_PAGE, mockPaymentGateway);

		when(mockStore.getCode()).thenReturn(STORE_CODE);
		when(mockStore.getUrl()).thenReturn("").thenReturn(storeUrl).thenReturn(storeUrl);
		when(mockStore.getStoreState()).thenReturn(StoreState.OPEN);
		when(mockStore.getPaymentGatewayMap()).thenReturn(paymentGatewayMap);
		when(mockStore.getCreditCardTypes()).thenReturn(Collections.<CreditCardType>emptySet());

		StoreEditorModel model = new StoreEditorModel(mockStore);
		try {
			editorModelHelper.sanityCheck(model);
		} catch (EpServiceException e) {
			fail("store with empty url should be correct"); //$NON-NLS-1$
		}

		try {
			editorModelHelper.sanityCheck(model);
		} catch (EpServiceException exception) {
			fail("store should have ability for saving"); //$NON-NLS-1$
		}

		try {
			editorModelHelper.sanityCheck(model);
			fail("exception should be thrown because store with given url already exists"); //$NON-NLS-1$
		} catch (EpServiceException expected) {
			assertNotNull(expected);
		}
	}

	/**
	 * Tests the StoreEditorModelHelper.updateStoreModel method.
	 */
	@Test
	public void testUpdateStoreModel() {
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper() {
			@Override
			Map<String, String> getSettingsMap(final StoreEditorModel model) {
				return Collections.emptyMap();
			}
		};

		final Store mockStore = mock(Store.class);
		final UpdateStoreCommand mockStoreCommand = mock(UpdateStoreCommand.class);
		when(mockStore.getAssociatedStoreUids()).thenReturn(new HashSet<Long>());
		editorModelHelper.setUpdateStoreCommand(mockStoreCommand);

		final CommandService mockCommandService = mock(CommandService.class);
		final UpdateStoreCommandResult mockUpdateResult = mock(UpdateStoreCommandResult.class);
		when(mockUpdateResult.getStore()).thenReturn(newStore);
		when(mockCommandService.execute(mockStoreCommand)).thenReturn(mockUpdateResult);
		editorModelHelper.setCommandService(mockCommandService);

		StoreEditorModel model = new StoreEditorModel(mockStore) {
			@Override
			public Set<StoreEditorModel> getSharedLoginStoreEntries() {
				return Collections.emptySet();
			}
		};
		editorModelHelper.updateStoreModel(model);
		assertSame(newStore, model.getStore());

		verify(mockStoreCommand).setStore(mockStore);
		verify(mockStoreCommand).setSettingValues(Collections.EMPTY_MAP);
		verify(mockUpdateResult).getStore();
		verify(mockCommandService).execute(mockStoreCommand);
	}

	/**
	 * Tests the StoreEditorModelHelper.findAllStoreEditorModels method.
	 */
	@Test
	public void testFindAllStoreEditorModels() {
		final Store mockStore = mock(Store.class);
		when(mockStore.getUidPk()).thenReturn(1L);
		final StoreEditorModelHelper editorModelHelper = new StoreEditorModelHelper() {
			@Override
			Store loadStore(final long storeUid, final FetchGroupLoadTuner loadTuner) {
				assertEquals(1L, storeUid);
				assertNull(loadTuner);
				return mockStore;
			}
		};

		when(mockStoreService.findAllStores()).thenReturn(Arrays.asList(mockStore));
		editorModelHelper.setStoreService(mockStoreService);

		final List<StoreEditorModel> editorModels = editorModelHelper.findAllStoreEditorModels();
		assertNotNull(editorModels);
		assertEquals(1, editorModels.size());
		assertSame(mockStore, editorModels.get(0).getStore());
		verify(mockStore).getUidPk();
		verify(mockStoreService).findAllStores();
	}

}
