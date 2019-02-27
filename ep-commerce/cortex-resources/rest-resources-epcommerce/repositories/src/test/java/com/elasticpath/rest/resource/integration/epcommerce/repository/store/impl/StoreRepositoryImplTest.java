/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.store.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.store.StoreService;

/**
 * Test class for {@link StoreRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StoreRepositoryImplTest {

	private static final String STORE_CODE = "STORE_CODE";
	private static final String INVALID_STORE_CODE = "INVALID_STORE_CODE";

	@Mock
	private StoreService storeService;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;
	
	private StoreRepository storeRepository;

	@Before
	public void setUp() {
		storeRepository = new StoreRepositoryImpl(storeService, reactiveAdapter);
	}

	@Test
	public void testIsStoreCodeEnabled() {
		Store mockStore = createMockStore(true, StoreState.OPEN);
		when(storeService.findStoreWithCode(STORE_CODE)).thenReturn(mockStore);

		ExecutionResult<Boolean> result = storeRepository.isStoreCodeEnabled(STORE_CODE);

		assertThat(result.isSuccessful()).isTrue();
		assertThat(result.getData()).isTrue();
	}

	private Store createMockStore(final boolean isEnabled, final StoreState storeState) {
		Store mockStore = mock(Store.class);
		when(mockStore.isEnabled()).thenReturn(isEnabled);
		when(mockStore.getStoreState()).thenReturn(storeState);
		return mockStore;
	}

	@Test
	public void testStoreCodeDisabled() {
		Store mockStore = createMockStore(true, StoreState.RESTRICTED);
		when(storeService.findStoreWithCode(STORE_CODE)).thenReturn(mockStore);

		ExecutionResult<Boolean> result = storeRepository.isStoreCodeEnabled(STORE_CODE);

		assertThat(result.isSuccessful()).isTrue();
		assertThat(result.getData()).isFalse();
	}

	@Test
	public void testBuildingStoreCodesWithAStoreWithNoCodeWhileSearchingForValidEnabledStore() {
		Store mockStore2 = createMockStore(true, StoreState.RESTRICTED);
		when(storeService.findStoreWithCode(STORE_CODE)).thenReturn(mockStore2);

		ExecutionResult<Boolean> result = storeRepository.isStoreCodeEnabled(STORE_CODE);

		assertThat(result.isSuccessful()).isTrue();
		assertThat(result.getData()).isFalse();
	}

	/**
	 * Test is store code enabled with an invalid store code.
	 */
	@Test
	public void testIsStoreCodeEnabledWithAnInvalidStoreCode() {
		ExecutionResult<Boolean> result = storeRepository.isStoreCodeEnabled(INVALID_STORE_CODE);

		assertThat(result.isFailure()).isTrue();
		assertThat(result.getResourceStatus()).isEqualTo(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testIsStoreCodeEnabledWhenFindStoreByCodeFails() {
		when(storeService.findStoreWithCode(STORE_CODE)).thenReturn(null);

		ExecutionResult<Boolean> result = storeRepository.isStoreCodeEnabled(STORE_CODE);

		assertThat(result.isFailure()).isTrue();
		assertThat(result.getResourceStatus()).isEqualTo(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testIsStoreCodeEnabledWhenStoreDisabled() {
		Store mockStore = createMockStore(false, StoreState.OPEN);

		when(storeService.findStoreWithCode(STORE_CODE)).thenReturn(mockStore);

		ExecutionResult<Boolean> result = storeRepository.isStoreCodeEnabled(STORE_CODE);

		assertThat(result.isSuccessful()).isTrue();
		assertThat(result.getData()).isEqualTo(false);
	}

	@Test
	public void shouldFindStore() {
		Store mockStore = createMockStore(true, StoreState.OPEN);
		when(storeService.findStoreWithCode(STORE_CODE)).thenReturn(mockStore);

		ExecutionResult<Store> result = storeRepository.findStore(STORE_CODE);

		assertThat(result.isSuccessful()).isTrue();
		assertThat(result.getData()).isEqualTo(mockStore);

	}

	@Test
	public void testFindStoreWithInvalidStoreCode() {
		ExecutionResult<Store> result = storeRepository.findStore(INVALID_STORE_CODE);

		assertThat(result.isFailure()).isTrue();
		assertThat(result.getResourceStatus()).isEqualTo(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testFindStoreWhenFindStoreByCodeFails() {
		when(storeService.findStoreWithCode(STORE_CODE)).thenReturn(null);

		ExecutionResult<Store> result = storeRepository.findStore(STORE_CODE);

		assertThat(result.isFailure()).isTrue();
		assertThat(result.getResourceStatus()).isEqualTo(ResourceStatus.NOT_FOUND);
	}
}
