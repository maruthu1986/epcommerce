/*
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.store.Store;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.customer.CustomerAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.customer.CustomerService;

/**
 * Exporter for {@link Customer} objects. Used in importexport. 
 */
public class CustomerExporter extends AbstractExporterImpl<Customer, CustomerDTO, String> {

	private static final Logger LOG = Logger.getLogger(CustomerExporter.class);

	private List<String> customerGuids;

	private CustomerAdapter customerAdapter;

	private CustomerService customerService;

	private ImportExportSearcher importExportSearcher;

	@Override
	public JobType getJobType() {
		return JobType.CUSTOMER;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { Customer.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		List<Long> customerUids = getImportExportSearcher().searchUids(getContext().getSearchConfiguration(), EPQueryType.CUSTOMER);
		customerGuids = new ArrayList<>(customerUids.size());
		
		for (Customer customer : customerService.findByUids(customerUids)) {
			if (!customer.isAnonymous()) {
				customerGuids.add(customer.getGuid());
			}
		}
		
		LOG.info("The list for " + customerGuids.size() + " customers retrieved from the database.");
	}

	@Override
	protected Class<? extends CustomerDTO> getDtoClass() {
		return CustomerDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(Customer.class)) {
			customerGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(Customer.class));
		}

		return customerGuids;
	}

	@Override
	protected List<Customer> findByIDs(final List<String> subList) {
		List<Customer> customers = new ArrayList<>();
		for (String guid : subList) {
			Customer customer = customerService.findByGuid(guid);
			if (customer != null) {
				customers.add(customer);
			}
		}

		return customers;
	}

	@Override
	protected void addDependencies(final List<Customer> customers, final DependencyRegistry dependencyRegistry) {
		for (Customer customer : customers) {
			if (dependencyRegistry.supportsDependency(Store.class)) {
				dependencyRegistry.addGuidDependency(Store.class, customer.getStoreCode());
			}

			if (dependencyRegistry.supportsDependency(CustomerGroup.class)) {
				for (CustomerGroup customerGroup : customer.getCustomerGroups()) {
					dependencyRegistry.addGuidDependency(CustomerGroup.class, customerGroup.getGuid());
				}
			}
		}
	}

	@Override
	protected DomainAdapter<Customer, CustomerDTO> getDomainAdapter() {
		return customerAdapter;
	}

	public void setCustomerAdapter(final CustomerAdapter customerAdapter) {
		this.customerAdapter = customerAdapter;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}
	
	/**
	 * @return The importExportSearcher.
	 */
	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	/**
	 * @param importExportSearcher The ImportExportSearcher.
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

}
