/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.customer.CustomerGroupDTO;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * Strategy which determines how collections in {@link CustomerGroup} are managed.
 */
public class CustomerGroupCollectionsStrategy implements CollectionsStrategy<CustomerGroup, CustomerGroupDTO> {

	private final boolean clearRoles;

	private final CustomerGroupService customerGroupService;

	/**
	 * Constructor which sets individual collection strategies.
	 * 
	 * @param importerConfiguration the {@link ImporterConfiguration} to set.
	 * @param customerGroupService the {@link CustomerGroupService} to set.
	 */
	public CustomerGroupCollectionsStrategy(final ImporterConfiguration importerConfiguration,
			final CustomerGroupService customerGroupService) {

		this.customerGroupService = customerGroupService;

		clearRoles = importerConfiguration.getCollectionStrategyType(DependentElementType.CUSTOMER_ROLES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
	}

	@Override
	public void prepareCollections(final CustomerGroup customerGroup, final CustomerGroupDTO dto) {
		if (clearRoles) {
			customerGroupService.removeAllRoles(customerGroup);
		}
	}

	@Override
	public boolean isForPersistentObjectsOnly() {
		return true;
	}
}
