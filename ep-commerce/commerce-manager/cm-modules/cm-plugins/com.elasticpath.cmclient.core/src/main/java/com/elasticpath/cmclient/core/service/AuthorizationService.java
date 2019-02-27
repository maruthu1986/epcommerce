/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.security.Permission;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserPermission;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.persistence.api.Persistable;

/**
 * Provides methods to ascertain whether a logged-in user is authorized.
 */
@SuppressWarnings({ "PMD.GodClass" })
public final class AuthorizationService {
	private static final Logger LOG = Logger.getLogger(AuthorizationService.class);

	/** Map of UserPermission identifying String to UserPermission object. * */
	private final Map<String, UserPermission> userPermissions = new HashMap<String, UserPermission>();

	/** Map of UserRole identifying String to UserRole object. * */
	private final Map<String, UserRole> userRoles = new HashMap<String, UserRole>();

	private boolean allStoresAccess;

	private boolean allWarehousesAccess;

	private boolean allCatalogsAccess;

	private boolean allPriceListsAccess;

	private final Set<String> accessibleStoreCodes = new HashSet<String>();

	private final Set<Long> accessibleWarehouseUidPks = new HashSet<Long>();

	private final Set<Long> accessibleCatalogUidPks = new HashSet<Long>();
	
	private final Set<String> accessiblePriceListGuids = new HashSet<String>();

	/**
	 * Keeps track of whether the current user is a SuperUser, so we don't have to look it up all the time. *
	 */
	private boolean superUser;



	private AuthorizationService() {
		// singleton

		refreshRolesAndPermissions();
	}

	/**
	 * Get the singleton instance of <code>AuthorizationService</code>.
	 * 
	 * @return an instance of <code>AuthorizationService</code>
	 */
	public static AuthorizationService getInstance() {
		return CmSingletonUtil.getSessionInstance(AuthorizationService.class);
	}

	/**
	 * Decides whether the current user is authorized to access Commerce Manager.
	 * 
	 * @return true if the current user has permission, false if not.
	 */
	public static boolean isAuthorized() {
		Authentication authToken = SecurityContextHolder.getContext().getAuthentication();
		for (final GrantedAuthority authority : authToken.getAuthorities()) {
			if (!(authority instanceof UserRole)) {
				continue;
			}
			
			final UserRole role = (UserRole) authority;
			if (role.getName().equals(UserRole.CMUSER) || role.getName().equals(UserRole.SUPERUSER)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Decides whether the current user is authorized to access a perspective indicated by a perspectiveId.
	 * Returns true if perspective has no permissions or cm user is super user.
	 * 
	 * @param perspectiveId the id of the perspective.
	 * @return true if the current user has permission, false if not.
	 */
	public boolean isAuthorizedToAccessPerspective(final String perspectiveId) {
		Collection<Permission> permissions = PermissionsProvider.getInstance().retrievePermissions(perspectiveId);
		if (permissions.isEmpty() || this.isSuperUser()) {
			return true;
		}
		for (Permission permission : permissions) {
			if (AuthorizationService.getInstance().isAuthorizedWithPermission(permission.getKey())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Decides whether the current user is authorized to access a resource indicated by a permission.
	 * 
	 * @param permission the id of the resource
	 * @return true if the current user has permission, false if not.
	 */
	public boolean isAuthorizedWithPermission(final String permission) {
		if (this.isSuperUser()) {
			LOG.debug("User is superuser - bypassing permissions"); //$NON-NLS-1$
			return true;
		}
		if (userPermissions.containsKey(permission)) {
			return true;
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Current user does not have permission for permission = " + permission); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Decides whether the current user is authorized to access a resource placed in the specified store.
	 * This implementation calls {@link #isAuthorizedForStore(String)}
	 *
	 * @deprecated Use {@link #isAuthorizedForStore(String)} instead
	 * @param store to be checked
	 * @return true if the current user has permission or if the given store is null or if the given store's code is null, 
	 * false if not.
	 */
	@Deprecated
	public boolean isAuthorizedForStore(final Store store) {
		return this.allStoresAccess  || store != null && isAuthorizedForStore(store.getCode());
	}

	/**
	 * Decides whether the current user is authorized to access a resource placed in the specified store.
	 *
	 * @param storeCode the store code
	 * @return true if the current user is authorized to access/modify the store with the given code or the given
	 * code is null, false if not.
	 */
	public boolean isAuthorizedForStore(final String storeCode) {
		boolean result = allStoresAccess || storeCode != null && getAccessibleStoreCodes().contains(storeCode);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Permission check for store is: " + result); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * @return the set of store codes representing stores accessible by the current user.
	 */
	Set<String> getAccessibleStoreCodes() {
		return this.accessibleStoreCodes;
	}

	/**
	 * @return true if the user is authorized to access all stores.
	 */
	public boolean isAuthorizedAllStores() {
		return this.allStoresAccess;
	}

	/**
	 * Decides whether the current user is authorized to access a resource placed in the specified warehouse.
	 * 
	 * @param warehouse to be checked
	 * @return true if the current user has permission, false if not.
	 */
	public boolean isAuthorizedForWarehouse(final Warehouse warehouse) {
		boolean result = allWarehousesAccess || warehouse != null && isUidPkInSet(warehouse.getUidPk(), accessibleWarehouseUidPks);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Permission check for warehouse is: " + result); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Decides whether the current user is authorized to access a resource placed in the specified catalog.
	 * 
	 * @param catalog to be checked
	 * @return true if the current user has permission, false if not.
	 */
	public boolean isAuthorizedForCatalog(final Catalog catalog) {
		boolean result = allCatalogsAccess || catalog != null && isUidPkInSet(catalog.getUidPk(), accessibleCatalogUidPks);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Permission check for catalog is: " + result); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Decides whether the current user is authorized to access a resource placed in the specified price list.
	 *
	 * @deprecated use {@link isAuthorizedForPriceList(final String priceListGuid)} instead
	 * @param priceListDescriptorDto to be checked
	 * @return true if the current user has permission, false if not.
	 */
	@Deprecated
	public boolean isAuthorizedForPriceList(final PriceListDescriptorDTO priceListDescriptorDto) {
		boolean result = false;
		if (priceListDescriptorDto != null) {
			result = isAuthorizedForPriceList(priceListDescriptorDto.getGuid());
		}
		return result;
	}

	/**
	 * Decides whether the current user is authorized to access a resource placed in the specified price list.
	 *
	 * @deprecated use {@link isAuthorizedForPriceList(final String priceListGuid)} instead
	 * @param priceListDescriptor to be checked
	 * @return true if the current user has permission, false if not.
	 */
	@Deprecated
	public boolean isAuthorizedForPriceList(final PriceListDescriptor priceListDescriptor) {
		boolean result = false;
		if (priceListDescriptor != null) {
			result = isAuthorizedForPriceList(priceListDescriptor.getGuid());
		}
		return result;
	}

	/**
	 * Decides whether the current user is authorized to access a resource placed in the specified price list.
	 * @param priceListGuid  price list guid to be checked
	 * @return true if the current user has permission, false if not. 
	 */
	public boolean isAuthorizedForPriceList(final String priceListGuid) {
		boolean result = allPriceListsAccess || accessiblePriceListGuids.contains(priceListGuid);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Permission check for price list " + priceListGuid + " is: " + result); //$NON-NLS-1$ //$NON-NLS-2$ 
		}
		return result;
	}

	/**
	 * Decides whether the current user is authorized to access a resource placed in the specified product.
	 * User needs access to the master catalog of product to edit settings that aren't overridable by virtual catalogs.
	 * e.g. product code, image.
	 * 
	 * @param product to be checked
	 * @return true if the current user has permission, false if not.
	 */
	public boolean isAuthorizedForProduct(final Product product) {
		Catalog catalog = product.getMasterCatalog();
		return isAuthorizedForCatalog(catalog);
	}

	/**
	 * Decides whether the current user is authorized to access the catalogs which the product belongs to.
	 * The catalogs includes master catalog and virutal catalogs the product may belong to. 
	 * 
	 * @param product to be checked
	 * @return true if the current user has permission, false if not.
	 */
	public boolean isAuthorizedForAnyProductCatalog(final Product product) {
		Set<Catalog> catalogs = product.getCatalogs();
		filterAuthorizedCatalogs(catalogs);
		return CollectionUtils.isNotEmpty(catalogs);
	}

	/**
	 * Returns whether the user is a SuperUser.
	 * 
	 * @return true if the user is a SuperUser, false if not
	 */
	public boolean isSuperUser() {
		return superUser;
	}

	/**
	 * Filter stores by user's store permissions.
	 * Remove unauthorized stores from given list of stores.
	 * 
	 * @param stores the stores to filter on
	 * @deprecated use {@link #removeUnathorizedStoresFrom(Collection)} instead.
	 */
	@Deprecated
	public void filterAuthorizedStores(final Collection<Store> stores) {
		this.removeUnathorizedStoresFrom(stores);
	}

	/**
	 * Filter warehouses by user's warehouse permissions.
	 * Remove unauthorized warehouses from given list of warehouses.
	 * 
	 * @param warehouses the warehouses to filter on
	 */
	public void filterAuthorizedWarehouses(final Collection<Warehouse> warehouses) {
		if (!allWarehousesAccess) {
			filterAuthorized(accessibleWarehouseUidPks, warehouses);
		}
	}

	/**
	 * Filter catalogs by user's catalog permissions.
	 * Remove unauthorized catalogs from given list of catalogs.
	 * 
	 * @param catalogs the catalogs to filter on
	 */
	public void filterAuthorizedCatalogs(final Collection<Catalog> catalogs) {
		if (!allCatalogsAccess) {
			filterAuthorized(accessibleCatalogUidPks, catalogs);
		}
	}

	private void filterAuthorized(final Set<Long> uidPks, final Collection< ? extends Persistable> persistences) {
		for (Iterator< ? extends Persistable> iterator = persistences.iterator(); iterator.hasNext();) {
			if (!uidPks.contains(iterator.next().getUidPk())) {
				iterator.remove();
			}
		}
	}

	/** 
	 * Removes stores that are not in the current user's collection of accessible
	 * stores from the given collection. 
	 * @param stores the collection from which to remove unaccessible stores
	 */
	public void removeUnathorizedStoresFrom(final Collection<Store> stores) {
		if (!allStoresAccess) {
			for (Iterator<Store> iterator = stores.iterator(); iterator.hasNext();) {
				if (!getAccessibleStoreCodes().contains(iterator.next().getCode())) {
					iterator.remove();
				}
			}
		}
	}

	private boolean isUidPkInSet(final long uidPkToCompare, final Set<Long> uidPkSet) {
		boolean inSet = false;
		for (long uidPk : uidPkSet) {
			if (uidPkToCompare == uidPk) {
				inSet = true;
				break;
			}
		}
		return inSet;
	}

	private void addUidPksToSet(final Collection< ? extends Persistable> persistenceList, final Set<Long> uidPksSet) {
		for (Persistable persistable : persistenceList) {
			uidPksSet.add(persistable.getUidPk());
		}
	}

	/**
	 * Populates a lookup map of user permissions, user roles, set authorized stores/warehouses/catalogs and sets the superUser boolean if the user
	 * has the superUser role.
	 */
	public void refreshRolesAndPermissions() {
		LOG.debug("Populating maps of userPermissions, userRoles and store/warehouse/catalog permission sets"); //$NON-NLS-1$
		this.userPermissions.clear();
		this.userRoles.clear();
		this.superUser = false;
		this.allStoresAccess = false;
		this.allWarehousesAccess = false;
		this.allCatalogsAccess = false;
		this.allPriceListsAccess = false;
		getAccessibleStoreCodes().clear();
		this.accessibleWarehouseUidPks.clear();
		this.accessibleCatalogUidPks.clear();

		// explicit check to make sure application is available before retreiving cmUser and initializing
		if (LoginManager.getInstance() == null) {
			return;
		}
		CmUser cmUser = LoginManager.getCmUser();
		for (final UserRole userRole : cmUser.getUserRoles()) {
			if (userRole.isSuperUserRole()) {
				this.superUser = true;
			}
			userRoles.put(userRole.getAuthority(), userRole);
			for (final UserPermission userPermission : userRole.getUserPermissions()) {
				userPermissions.put(userPermission.getAuthority(), userPermission);
			}
		}
		if (cmUser.isAllStoresAccess()) {
			allStoresAccess = true;
		} else {
			addStoreCodesToSet(cmUser.getStores(), accessibleStoreCodes);
		}
		if (cmUser.isAllWarehousesAccess()) {
			allWarehousesAccess = true;
		} else {
			addUidPksToSet(cmUser.getWarehouses(), accessibleWarehouseUidPks);
		}
		if (cmUser.isAllCatalogsAccess()) {
			allCatalogsAccess = true;
		} else {
			addUidPksToSet(cmUser.getCatalogs(), accessibleCatalogUidPks);
		}
		if (cmUser.isAllPriceListsAccess()) {
			allPriceListsAccess = true;
		} else {
			addGuidsToSet(cmUser.getPriceLists(), accessiblePriceListGuids);
		}
	}

	private void addGuidsToSet(final Collection<String> guids, final Collection<String> accessiblePriceListGuids) {
		accessiblePriceListGuids.addAll(guids);		
	}

	/**
	 * Adds the codes for the given stores to the given set of store codes.
	 * @param stores the stores
	 * @param storeCodes the store codes set
	 */
	void addStoreCodesToSet(final Collection<Store> stores, final Set<String> storeCodes) {
		for (Store store : stores) {
			storeCodes.add(store.getCode());
		}
	}

}
