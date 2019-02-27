/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.customer.CustomerGroupDTO;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * Importer for {@link CustomerGroup} objects.
 */
public class CustomerGroupImporter extends AbstractImporterImpl<CustomerGroup, CustomerGroupDTO> {

	private DomainAdapter<CustomerGroup, CustomerGroupDTO> customerGroupAdapter;

	private CustomerGroupService customerGroupService;

	@Override
	public String getImportedObjectName() {
		return CustomerGroupDTO.ROOT_ELEMENT;
	}

	@Override
	public Class<? extends CustomerGroupDTO> getDtoClass() {
		return CustomerGroupDTO.class;
	}

	@Override
	protected String getDtoGuid(final CustomerGroupDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<CustomerGroup, CustomerGroupDTO> getDomainAdapter() {
		return customerGroupAdapter;
	}

	@Override
	protected CustomerGroup findPersistentObject(final CustomerGroupDTO dto) {
		return customerGroupService.findByGuid(dto.getGuid());
	}

	@Override
	protected CollectionsStrategy<CustomerGroup, CustomerGroupDTO> getCollectionsStrategy() {
		return new CustomerGroupCollectionsStrategy(
				getContext().getImportConfiguration().getImporterConfiguration(JobType.CUSTOMER_GROUP), customerGroupService);
	}

	@Override
	protected void setImportStatus(final CustomerGroupDTO object) {
		getStatusHolder().setImportStatus("(" + object.getGuid() + ")");
	}

	public void setCustomerGroupAdapter(final DomainAdapter<CustomerGroup, CustomerGroupDTO> customerGroupAdapter) {
		this.customerGroupAdapter = customerGroupAdapter;
	}

	public void setCustomerGroupService(final CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}

}
