/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.service.datapolicy.CustomerConsentService;

/**
 * Exporter for customer consent.
 */
public class CustomerConsentExporterImpl extends AbstractExporterImpl<CustomerConsent, CustomerConsentDTO, String> {

	private static final Logger LOG = Logger.getLogger(CustomerConsentExporterImpl.class);

	private ImportExportSearcher importExportSearcher;

	private CustomerConsentService customerConsentService;

	private List<String> customerConsentGuids;

	private DomainAdapter<CustomerConsent, CustomerConsentDTO> customerConsentAdapter;

	@Override
	public JobType getJobType() {
		return JobType.CUSTOMER_CONSENT;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { CustomerConsent.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		customerConsentGuids = new ArrayList<>();

		for (CustomerConsent customerConsent : customerConsentService.list()) {
			customerConsentGuids.add(customerConsent.getGuid());
		}
		LOG.info("The list for " + customerConsentGuids.size() + " customer consent(s) is retrieved from the database.");

	}

	@Override
	protected Class<? extends CustomerConsentDTO> getDtoClass() {
		return CustomerConsentDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(CustomerConsent.class)) {
			customerConsentGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(CustomerConsent.class));
		}
		return customerConsentGuids;
	}

	@Override
	protected List<CustomerConsent> findByIDs(final List<String> subList) {
		return customerConsentService.findByGuids(subList);
	}

	@Override
	protected DomainAdapter<CustomerConsent, CustomerConsentDTO> getDomainAdapter() {
		return this.customerConsentAdapter;
	}

	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	public void setCustomerConsentService(final CustomerConsentService customerConsentService) {
		this.customerConsentService = customerConsentService;
	}

	public void setCustomerConsentAdapter(final DomainAdapter<CustomerConsent, CustomerConsentDTO> customerConsentAdapter) {
		this.customerConsentAdapter = customerConsentAdapter;
	}
}
