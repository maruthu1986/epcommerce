/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.importer.importers.impl;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Importer for {@link CustomerConsentDTO} and its associated domain class.
 */
public class CustomerConsentImporterImpl extends AbstractImporterImpl<CustomerConsent, CustomerConsentDTO> {

	private static final Logger LOG = Logger.getLogger(CustomerConsentImporterImpl.class);

	private DomainAdapter<CustomerConsent, CustomerConsentDTO> customerConsentAdapter;

	private CustomerConsentService customerConsentService;

	private CustomerService customerService;

	private DataPolicyService dataPolicyService;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<CustomerConsent, CustomerConsentDTO> savingStrategy) {
		super.initialize(context, savingStrategy);

		final SavingManager<CustomerConsent> dataPolicySavingManager = new SavingManager<CustomerConsent>() {

			@Override
			public CustomerConsent update(final CustomerConsent persistable) {
				LOG.warn(new Message("IE-31300", persistable.getGuid()));
				return null;
			}

			@Override
			public void save(final CustomerConsent persistable) {
				customerConsentService.save(persistable);
			}
		};
		getSavingStrategy().setSavingManager(dataPolicySavingManager);
	}

	@Override
	public boolean executeImport(final CustomerConsentDTO customerConsentDTO) {
		sanityCheck();
		setImportStatus(customerConsentDTO);
		final CustomerConsent persistedCustomerConsent = findPersistentObject(customerConsentDTO);

		// if savedCustomerConsent == null it means that this CustomerConsent was not imported due to the import strategy configuration
		if (persistedCustomerConsent != null) {
			LOG.warn(new Message("IE-31300", customerConsentDTO.getGuid()));
			return false;
		}

		if (getCustomerService().findByGuid(customerConsentDTO.getCustomerGuid()) == null) {
			LOG.warn(new Message("IE-31301", customerConsentDTO.getGuid(), customerConsentDTO.getCustomerGuid()));
			return false;
		}

		if (getDataPolicyService().findByGuid(customerConsentDTO.getDataPolicyGuid()) == null) {
			LOG.warn(new Message("IE-31302", customerConsentDTO.getGuid(), customerConsentDTO.getDataPolicyGuid()));
			return false;
		}

		getSavingStrategy().setDomainAdapter(customerConsentAdapter);

		getSavingStrategy().populateAndSaveObject(persistedCustomerConsent, customerConsentDTO);

		return true;
	}

	@Override
	public String getImportedObjectName() {
		return CustomerConsentDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final CustomerConsentDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<CustomerConsent, CustomerConsentDTO> getDomainAdapter() {
		return customerConsentAdapter;
	}

	@Override
	protected CustomerConsent findPersistentObject(final CustomerConsentDTO dto) {
		return customerConsentService.findByGuid(dto.getGuid());
	}

	@Override
	protected void setImportStatus(final CustomerConsentDTO object) {
		getStatusHolder().setImportStatus(object.getGuid());
	}

	@Override
	public Class<? extends CustomerConsentDTO> getDtoClass() {
		return CustomerConsentDTO.class;
	}

	public void setCustomerConsentAdapter(final DomainAdapter<CustomerConsent, CustomerConsentDTO> domainAdapter) {
		this.customerConsentAdapter = domainAdapter;
	}

	public void setCustomerConsentService(final CustomerConsentService customerConsentService) {
		this.customerConsentService = customerConsentService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	public DataPolicyService getDataPolicyService() {
		return dataPolicyService;
	}

	public void setDataPolicyService(final DataPolicyService dataPolicyService) {
		this.dataPolicyService = dataPolicyService;
	}
}
