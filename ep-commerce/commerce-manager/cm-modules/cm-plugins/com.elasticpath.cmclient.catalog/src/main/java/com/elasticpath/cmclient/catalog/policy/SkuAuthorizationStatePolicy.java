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
import com.elasticpath.cmclient.core.dto.catalog.ProductSkuModel;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * A <code>StatePolicy</code> that will determine UI state for sku related UI elements.
 * It will return editable based on the following criteria:
 * <ol>
 *   <li>The user has permission to the sku's catalog</li>
 *   <li>The user has permission to edit skus</li>
 * </ol>
 */
public class SkuAuthorizationStatePolicy extends AbstractCatalogDeterminerStatePolicy {

	private ProductSku productSku;
	
	private AuthorizationService authorizationService;
	
	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	/**
	 * Construct an instance of this policy that ties to the product guid.
	 * 
	 * @param productSku the <code>ProductSku</code> this policy applies to
	 */
	@Override
	public void init(final Object productSku) {
		if (productSku instanceof ProductSkuModel) {
			this.productSku = ((ProductSkuModel) productSku).getProductSku();			
		} else if (productSku instanceof ProductSku) {
			this.productSku = (ProductSku) productSku;
		}
		authorizationService = AuthorizationService.getInstance();
	}
	
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
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
			
			if (productSku != null 
					&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)
					&& AuthorizationService.getInstance().isAuthorizedForProduct(productSku.getProduct())) {
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
					/*|| authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_PRICING)*/
					//TODO shall be done when related to bb-1124 stories done
				) && authorizationService.isAuthorizedForCatalog(catalog)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}
	
	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultAuthorizationDeterminer();
	}

}
