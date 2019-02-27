/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserPermission;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;

/**
 * Tests the authorization service.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class AuthorizationServiceTest {

	private static final String TEST_ROLE = "ROLE_TEST"; //$NON-NLS-1$
	private static final String TEST_PERMISSION = "PERMISSION_TEST"; //$NON-NLS-1$
	private static final long STORE_UIDPK = 1;
	private static final String STORE_CODE = Long.toString(STORE_UIDPK);
	private static final long WAREHOUSE_UIDPK = 2;
	private static final long CATALOG_UIDPK = 3;

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public TestContext context = new TestContext();

	@Mock
	private UserRole mockUserRole;

	@Mock
	private UserPermission mockUserPermission;

	@Mock
	private CmUser mockCmUser;

	@Mock
	private Store mockAccessibleStore;

	@Mock
	private Warehouse mockAccessibleWarehouse;

	@Mock
	private Catalog mockAccessibleCatalog;


	private LoginManager loginManager;

	private AuthorizationService authorizationService;

	/**
	 * Setup test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		loginManager = LoginManager.getInstance();
		loginManager.setCmUser(mockCmUser);
		authorizationService = AuthorizationService.getInstance();
		setupTestRolesAndPermissions();
	}

	private void setupTestRolesAndPermissions() {

		when(mockUserPermission.getAuthority()).thenReturn(TEST_PERMISSION);
		when(mockUserRole.getAuthority()).thenReturn(TEST_ROLE);
		when(mockUserRole.getUserPermissions()).thenReturn(Collections.singleton(mockUserPermission));
		when(mockAccessibleStore.getUidPk()).thenReturn(STORE_UIDPK);
		when(mockAccessibleStore.getCode()).thenReturn(STORE_CODE);
		when(mockAccessibleWarehouse.getUidPk()).thenReturn(WAREHOUSE_UIDPK);
		when(mockAccessibleCatalog.getUidPk()).thenReturn(CATALOG_UIDPK);

		when(mockCmUser.getUserRoles()).thenReturn(Collections.singleton(mockUserRole));
		when(mockCmUser.getStores()).thenReturn(Collections.singleton(mockAccessibleStore));
		when(mockCmUser.getWarehouses()).thenReturn(Collections.singleton(mockAccessibleWarehouse));
		when(mockCmUser.getCatalogs()).thenReturn(Collections.emptySet());
		when(mockCmUser.getPriceLists()).thenReturn(Collections.singleton("PriceListGuidAssigned"));
	}

	/**
	 * Test isSuperUser returns true.
	 * @throws Exception on error
	 */
	@Test
	public void testIsSuperUserTrue() throws Exception {

		givenUserIsSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertTrue(authorizationService.isSuperUser());
	}

	/**
	 * Test isSuperUser returns false.
	 * @throws Exception on error
	 */
	@Test
	public void testIsSuperUserFalse() throws Exception {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertFalse(authorizationService.isSuperUser());
	}

	/**
	 * Test isAuthorizedForPriceList returns true.
	 * @throws Exception on error
	 */
	@Test
	public void testIsAuthorizedTrue() throws Exception {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertTrue(authorizationService.isAuthorizedWithPermission(TEST_PERMISSION));
	}

	/**
	 * Test isAuthorizedForPriceList returns false.
	 * @throws Exception on error
	 */
	@Test
	public void testIsAuthorizedFalse() throws Exception {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertFalse(authorizationService.isAuthorizedWithPermission("UNKNOWN_PERMISSION")); //$NON-NLS-1$
	}

	/**
	 * Test isAuthorizedStore returns true.
	 * @throws Exception on error
	 */
	@Test
	public void testIsAuthorizedStore() throws Exception {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertTrue(authorizationService.isAuthorizedForStore(mockAccessibleStore));
	}

	/**
	 * Check that a null store means we are not authorized.
	 */
	@Test
	public void testIsAuthorizedStoreNull() {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertFalse(authorizationService.isAuthorizedForStore((Store) null));
	}

	/**
	 * Test isAuthorizedStore returns true when allStoreAccess and store is null.
	 */
	@Test
	public void testIsAuthorizedStoreAllStoreAccessWithNullPassedIn() {
		givenUserIsNotSuperUser();
		givenAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();

		assertTrue(authorizationService.isAuthorizedForStore((Store) null));
	}

	/**
	 * Test isAuthorizedWarehouse returns true.
	 * @throws Exception on error
	 */
	@Test
	public void testIsAuthorizedWarehouse() throws Exception {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertTrue(authorizationService.isAuthorizedForWarehouse(mockAccessibleWarehouse));
	}

	/**
	 * Test isAuthorizedWarehouse returns when no warehouse is passed in.
	 * This changed as part of 6.1.2, previously null would mean all warehouses.
	 */
	@Test
	public void testIsAuthorizedWarehouseNull() {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertFalse(authorizationService.isAuthorizedForWarehouse((Warehouse) null));
	}

	/**
	 * Test isAuthorizedWarehouse returns true.
	 */
	@Test
	public void testIsAuthorizedWarehouseAllWarehouseAccessWithNullPassedIn() {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();

		assertTrue(authorizationService.isAuthorizedForWarehouse((Warehouse) null));
	}

	/**
	 * Test isAuthorizedCatalog returns false.
	 * @throws Exception on error
	 */
	@Test
	public void testIsAuthorizedCatalog() throws Exception {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertFalse(authorizationService.isAuthorizedForCatalog(mockAccessibleCatalog));
	}

	/**
	 * Check that a null catalog means we are not authorized.
	 */
	@Test
	public void testIsAuthorizedCatalogNull() {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertFalse(authorizationService.isAuthorizedForCatalog((Catalog) null));
	}

	/**
	 * Test isAuthorizedStore returns true when allCatalogAccess and catalog is null.
	 */
	@Test
	public void testIsAuthorizedCatalogAllCatalogAccessWithNullPassedIn() {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();

		assertTrue(authorizationService.isAuthorizedForCatalog((Catalog) null));
	}

	/**
	 * Check that a null price list guid means we are not authorized.
	 */
	@Test
	public void testIsAuthorizedPriceListNull() {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertFalse(authorizationService.isAuthorizedForPriceList((String) null));
	}

	/**
	 * Check we fail to authorize on a price list we are not asssigned to.
	 * @throws Exception on error
	 */
	@Test
	public void testIsAuthorizedNonAssignedPriceList() throws Exception {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertFalse(authorizationService.isAuthorizedForPriceList("PriceListGuidNotAssigned")); //$NON-NLS-1$
	}

	/**
	 * Check authorize on a price list we are asssigned to.
	 * @throws Exception on error
	 */
	@Test
	public void testIsAuthorizedAssignedPriceList() throws Exception {
		givenUserIsNotSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();
		assertTrue(authorizationService.isAuthorizedForPriceList("PriceListGuidAssigned")); //$NON-NLS-1$
	}

	/**
	 * Test isAuthorizedStore returns true when allCatalogAccess and catalog is null.
	 */
	@Test
	public void testIsAuthorizedPriceListAllPriceListAccessWithNullPassedIn() {
		givenUserIsNotSuperUser();
		givenAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();

		assertTrue(authorizationService.isAuthorizedForPriceList((String) null));
	}

	/**
	 * Test isAuthorizedStore returns true when allCatalogAccess and catalog is null.
	 */
	@Test
	public void testIsAuthorizedSuperUserWithNullPassedIn() {
		givenUserIsSuperUser();
		givenNotAllStoresAccess();
		givenNotAllWarehousesAccess();
		givenNotAllCatalogsAccess();
		givenNotAllPriceListsAccess();
		authorizationService.refreshRolesAndPermissions();

		assertFalse(authorizationService.isAuthorizedForPriceList((String) null));
	}

	private void givenAllWarehousesAccess() {
		when(mockCmUser.isAllWarehousesAccess()).thenReturn(true);
	}

	private void givenNotAllWarehousesAccess() {
		when(mockCmUser.isAllWarehousesAccess()).thenReturn(false);
	}

	private void givenAllCatalogsAccess() {
		when(mockCmUser.isAllCatalogsAccess()).thenReturn(true);
	}

	private void givenNotAllCatalogsAccess() {
		when(mockCmUser.isAllCatalogsAccess()).thenReturn(false);
	}

	private void givenAllStoresAccess() {
		when(mockCmUser.isAllStoresAccess()).thenReturn(true);
	}

	private void givenNotAllStoresAccess() {
		when(mockCmUser.isAllStoresAccess()).thenReturn(false);

	}

	private void givenAllPriceListsAccess() {
		when(mockCmUser.isAllPriceListsAccess()).thenReturn(true);
	}

	private void givenNotAllPriceListsAccess() {
		when(mockCmUser.isAllPriceListsAccess()).thenReturn(false);
	}

	private void givenUserIsSuperUser() {
		when(mockUserRole.isSuperUserRole()).thenReturn(true);
	}

	private void givenUserIsNotSuperUser() {
		when(mockUserRole.isSuperUserRole()).thenReturn(false);
	}

}
