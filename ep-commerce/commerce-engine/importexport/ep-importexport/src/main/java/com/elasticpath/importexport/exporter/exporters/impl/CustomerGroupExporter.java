/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.customer.CustomerGroupDTO;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * Exporter for {@link CustomerGroup} objects. Used in importexport.
 */
public class CustomerGroupExporter extends AbstractExporterImpl<CustomerGroup, CustomerGroupDTO, String> {

	private static final Logger LOG = Logger.getLogger(CustomerGroupExporter.class);

	private List<String> customerGroupGuids;

	private DomainAdapter<CustomerGroup, CustomerGroupDTO> customerGroupAdapter;

	private CustomerGroupService customerGroupService;

	@Override
	public JobType getJobType() {
		return JobType.CUSTOMER_GROUP;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { Customer.class };
	}

	@Override
	protected Class<? extends CustomerGroupDTO> getDtoClass() {
		return CustomerGroupDTO.class;
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		customerGroupGuids = new ArrayList<>();
		for (CustomerGroup customerGroup : customerGroupService.list()) {
			customerGroupGuids.add(customerGroup.getGuid());
		}

		LOG.info("The list for " + customerGroupGuids.size() + " customer groups retrieved from the database.");
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(CustomerGroup.class)) {
			customerGroupGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(CustomerGroup.class));
		}

		return customerGroupGuids;
	}

	@Override
	protected List<CustomerGroup> findByIDs(final List<String> customerGroupGuids) {
		List<CustomerGroup> customerGroups = new ArrayList<>();
		for (String guid : customerGroupGuids) {
			CustomerGroup customerGroup = customerGroupService.findByGuid(guid);
			if (customerGroup != null) {
				customerGroups.add(customerGroup);
			}
		}

		return customerGroups;
	}

	@Override
	protected DomainAdapter<CustomerGroup, CustomerGroupDTO> getDomainAdapter() {
		return customerGroupAdapter;
	}

	public void setCustomerGroupAdapter(final DomainAdapter<CustomerGroup, CustomerGroupDTO> customerGroupAdapter) {
		this.customerGroupAdapter = customerGroupAdapter;
	}

	public void setCustomerGroupService(final CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}

}
