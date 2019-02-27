/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.cartorder.CartOrderService;

/**
 * Test {@link ShoppingCartCleanupServiceImpl} functionality.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartCleanupServiceImplTest {

	private static final String DELETE_SHOPPING_CART_QUERY = "SHOPPING_CART_DELETE_BY_GUID";

	private static final String ABANDONED_CART_JPA_QUERY = "FIND_SHOPPING_CART_GUIDS_LAST_MODIFIED_BEFORE_DATE";

	private static final String INACTIVE_CARTS_JPA_QUERY = "FIND_INACTIVE_SHOPPING_CART_GUIDS";

	private static final String CART_GUID_1 = "CART_GUID_1";

	private static final int EXPECTED_MAX_RESULTS = 1000;

	@Mock
	private PersistenceEngine persistenceEngine;
	@Mock
	private CartOrderService cartOrderService;

	@InjectMocks
	private ShoppingCartCleanupServiceImpl shoppingCartCleanupService;

	/**
	 * Test a happy path where the service successfully finds candidates for removal.
	 */
	@Test
	public void testSuccessfullyFindsCandidatesForRemoval() {

		final Date expectedRemovalDate = new Date();
		final List<String> expectedShoppingCartGuids = new ArrayList<>();
		expectedShoppingCartGuids.add(CART_GUID_1);

		when(persistenceEngine.<String>retrieveByNamedQuery(ABANDONED_CART_JPA_QUERY,
			new Object[] { expectedRemovalDate },
			0,
			EXPECTED_MAX_RESULTS)).thenReturn(expectedShoppingCartGuids);


		int result = shoppingCartCleanupService.deleteAbandonedShoppingCarts(expectedRemovalDate, EXPECTED_MAX_RESULTS);

		assertThat(result)
			.as("Shopping cart record must be removed")
			.isEqualTo(1);

		verify(persistenceEngine).retrieveByNamedQuery(ABANDONED_CART_JPA_QUERY,
			new Object[] { expectedRemovalDate },
			0,
			EXPECTED_MAX_RESULTS);
		verify(persistenceEngine).executeNamedQueryWithList(DELETE_SHOPPING_CART_QUERY, "list", expectedShoppingCartGuids);
		verify(cartOrderService).removeIfExistsByShoppingCartGuids(expectedShoppingCartGuids);
	}

	/**
	 * Test a path where the service finds no candidates for removal.
	 */
	@Test
	public void testFindsNoCandidatesForRemoval() {

		final Date expectedRemovalDate = new Date();
		final List<String> expectedShoppingCartGuids = new ArrayList<>();

		when(persistenceEngine.<String>retrieveByNamedQuery(ABANDONED_CART_JPA_QUERY,
			new Object[] { expectedRemovalDate },
			0,
			EXPECTED_MAX_RESULTS)).thenReturn(expectedShoppingCartGuids);

		int result = shoppingCartCleanupService.deleteAbandonedShoppingCarts(expectedRemovalDate, EXPECTED_MAX_RESULTS);

		assertThat(result)
			.as("Shopping cart record must not be removed")
			.isEqualTo(0);

		verify(persistenceEngine).retrieveByNamedQuery(ABANDONED_CART_JPA_QUERY,
			new Object[] { expectedRemovalDate },
			0,
			EXPECTED_MAX_RESULTS);
	}

	/**
	 * Test failure when the removal date is null.
	 */
	public void testRemovalDateIsNull() {
		final Date expectedRemovalDate = null;

		assertThatThrownBy(() -> shoppingCartCleanupService.deleteAbandonedShoppingCarts(expectedRemovalDate, EXPECTED_MAX_RESULTS))
			.isInstanceOf(EpServiceException.class);
	}

	/**
	 * Test removal of inactive shopping carts.
	 */
	@Test
	public void testFindInactiveCartsForRemoval() {

		final List<String> expectedShoppingCartGuids = new ArrayList<>();
		expectedShoppingCartGuids.add(CART_GUID_1);

		when(persistenceEngine.<String>retrieveByNamedQuery(INACTIVE_CARTS_JPA_QUERY,
			new Object[] {},
			0,
			EXPECTED_MAX_RESULTS)).thenReturn(expectedShoppingCartGuids);

		int result = shoppingCartCleanupService.deleteInactiveShoppingCarts(EXPECTED_MAX_RESULTS);

		assertThat(result)
			.as("Shopping cart record must be removed")
			.isEqualTo(1);

		verify(persistenceEngine).retrieveByNamedQuery(INACTIVE_CARTS_JPA_QUERY,
			new Object[] { },
			0,
			EXPECTED_MAX_RESULTS);
		verify(persistenceEngine).executeNamedQueryWithList(DELETE_SHOPPING_CART_QUERY, "list", expectedShoppingCartGuids);
		verify(cartOrderService).removeIfExistsByShoppingCartGuids(expectedShoppingCartGuids);
	}

	/**
	 * Test when there is no inactive cart for removal.
	 */
	@Test
	public void testCantFindInactiveCartForRemoval() {

		final List<String> expectedShoppingCartGuids = new ArrayList<>();

		when(persistenceEngine.<String>retrieveByNamedQuery(INACTIVE_CARTS_JPA_QUERY,
			new Object[] {},
			0,
			EXPECTED_MAX_RESULTS)).thenReturn(expectedShoppingCartGuids);

		int result = shoppingCartCleanupService.deleteInactiveShoppingCarts(EXPECTED_MAX_RESULTS);

		assertThat(result)
			.as("Shopping cart record must not be removed")
			.isEqualTo(0);

		verify(persistenceEngine).retrieveByNamedQuery(INACTIVE_CARTS_JPA_QUERY,
			new Object[] {},
			0,
			EXPECTED_MAX_RESULTS);
	}

}
