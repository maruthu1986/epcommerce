/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.catalog.CatalogPermissions;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * A <code>StatePolicy</code> that will determine UI state for product related UI elements.
 * It will return editable based on the following criteria:
 * <ol>
 *   <li>The user has permission to the product's catalog</li>
 *   <li>The user has permission to edit products</li>
 * </ol>
 */
public class ProductAuthorizationStatePolicy extends AbstractCatalogDeterminerStatePolicy {
	private Product product;
	
	private AuthorizationService authorizationService;
	
	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	/**
	 * Construct an instance of this policy that ties to the product guid.
	 * 
	 * @param productModel the <code>ProductModel</code> to which product the policy applies to
	 */
	@Override
	public void init(final Object productModel) {
		if (productModel instanceof ProductModel) {
			this.product = ((ProductModel) productModel).getProduct();
		} else if (productModel instanceof Product) {
			this.product = (Product) productModel;
		}
		authorizationService = AuthorizationService.getInstance();
	}
	
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (getEditableContainerNames().contains(targetContainer.getName())) {
			return EpState.EDITABLE;
		}
		if (getReadOnlyContainerNames().contains(targetContainer.getName())) {
			return EpState.READ_ONLY;
		}
		return super.determineState(targetContainer);
	}

	/**
	 * Get the collection of names of <code>PolicyTargetContainer</code> objects
	 * that should should always have a read only status.
	 * 
	 * @return the collection of names
	 */
	protected Collection<String> getReadOnlyContainerNames() {
		return Arrays.asList(
				"guid", //$NON-NLS-1$
				"relatedControls", //$NON-NLS-1$
				"skuRelatedControls", //$NON-NLS-1$
				"inventoryControls" //$NON-NLS-1$
				);
	}
	
	/**
	 * Get the collection of names of <code>PolicyTargetContainer</code> objects
	 * that should should always be in edit mode.
	 * 
	 * @return the collection of names
	 */
	protected Collection<String> getEditableContainerNames() {
		return Arrays.asList(
				"navigationControls" //$NON-NLS-1$
		);

	}

	@Override
	protected StateDeterminer getDeterminer(final String containerName) {
		StateDeterminer determiner = super.getDeterminer(containerName);
		if (determiner == null) {
			determiner = new DefaultAuthorizationDeterminer();
		}
		return determiner;
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("priceListContainer", new PriceAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("editablePriceControls", new PriceAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("merchAssociationControls", new MerchandisingAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("categoryAssignmentControls", new CategoryAssignmentAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("openSkuControls", new OpenSkuAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("deleteSkuControls", new OpenSkuAuthorizationDeterminer()); //$NON-NLS-1$			
		}
		return determinerMap;
	}

	/**
	 * Default state determiner.
	 */
	public class DefaultAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state based on authorization.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)
			&& authorizationService.isAuthorizedForProduct(product)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}
	
	/**
	 * Determines state based on authorization and if the dependent object is a persisted SKU.
	 */
	public class OpenSkuAuthorizationDeterminer implements StateDeterminer {
		@Override
		@SuppressWarnings("PMD.SimplifyConditional")
		public EpState determineState(final PolicyActionContainer targetContainer) {
			Object policyDependent = targetContainer.getPolicyDependent(); 
			if (policyDependent != null
					&& policyDependent instanceof ProductSku
					&& ((ProductSku) policyDependent).isPersisted()
					&& authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)
					&& authorizationService.isAuthorizedForProduct(product)) {
				
				return EpState.EDITABLE;
			}
			
			return EpState.READ_ONLY;
		}
	}
	
	/**
	 * State determiner for price tabs.
	 */
	public class PriceAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state for price container controls which requires further investigation.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			Catalog catalog = (Catalog) targetContainer.getPolicyDependent();
			if ((authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)
					/*|| authorizationService.isAuthorized(CatalogPermissions.MANAGE_PRODUCT_PRICING)*/) 
					//TODO shall be done when related to bb-1124 stories done  
					&& authorizationService.isAuthorizedForCatalog(catalog)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}
	
	/**
	 * State determiner for merchandising associations.
	 */
	public class MerchandisingAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state for merch association container controls which requires further investigation.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			Catalog catalog = (Catalog) targetContainer.getPolicyDependent();
			if ((authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)
					|| authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_MERCHANDISING))
					&& authorizationService.isAuthorizedForCatalog(catalog)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}

	/**
	 * State determiner for merchandising associations.
	 */
	public class CategoryAssignmentAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state for category assignment container controls which requires further investigation.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			Catalog catalog = (Catalog) targetContainer.getPolicyDependent();
			if (authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)
					&& authorizationService.isAuthorizedForCatalog(catalog)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}
	
	/**
	 * Determines state based on authorisation and if the dependent object.
	 */
	public class DefaultProductEditAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (product != null
					&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)
					&& authorizationService.isAuthorizedForProduct(product)) {
				return EpState.EDITABLE;
			}
			
			return EpState.READ_ONLY;
		}
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultProductEditAuthorizationDeterminer();
	}

}
