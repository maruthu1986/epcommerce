/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.importer.importers.impl;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.datapolicy.DataPolicyDTO;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Importer for {@link DataPolicyDTO} and its associated domain class.
 */
public class DataPolicyImporterImpl extends AbstractImporterImpl<DataPolicy, DataPolicyDTO> {

	private static final Logger LOG = Logger.getLogger(DataPolicyImporterImpl.class);

	private DomainAdapter<DataPolicy, DataPolicyDTO> dataPolicyAdapter;

	private DataPolicyService dataPolicyService;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<DataPolicy, DataPolicyDTO> savingStrategy) {
		super.initialize(context, savingStrategy);

		final SavingManager<DataPolicy> dataPolicySavingManager = new SavingManager<DataPolicy>() {

			@Override
			public DataPolicy update(final DataPolicy persistable) {
				return dataPolicyService.update(persistable);
			}

			@Override
			public void save(final DataPolicy persistable) {
				update(persistable);
			}
		};
		getSavingStrategy().setSavingManager(dataPolicySavingManager);
	}

	@Override
	public boolean executeImport(final DataPolicyDTO dataPolicyDTO) {
		sanityCheck();
		setImportStatus(dataPolicyDTO);
		final DataPolicy persistedDataPolicy = findPersistentObject(dataPolicyDTO);

		// if savedDataPolicy == null it means that this DataPolicy was not imported due to the import strategy configuration
		if (persistedDataPolicy != null) {
			LOG.warn(new Message("IE-31200", dataPolicyDTO.getGuid()));
			return false;
		}

		getSavingStrategy().setDomainAdapter(dataPolicyAdapter);

		getSavingStrategy().populateAndSaveObject(persistedDataPolicy, dataPolicyDTO);

		return true;
	}

	@Override
	public String getImportedObjectName() {
		return DataPolicyDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final DataPolicyDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<DataPolicy, DataPolicyDTO> getDomainAdapter() {
		return dataPolicyAdapter;
	}

	@Override
	protected DataPolicy findPersistentObject(final DataPolicyDTO dto) {
		return dataPolicyService.findByGuid(dto.getGuid());
	}

	@Override
	protected void setImportStatus(final DataPolicyDTO object) {
		getStatusHolder().setImportStatus(object.getGuid());
	}

	public void setDataPolicyAdapter(final DomainAdapter<DataPolicy, DataPolicyDTO> domainAdapter) {
		this.dataPolicyAdapter = domainAdapter;
	}

	public void setDataPolicyService(final DataPolicyService dataPolicyService) {
		this.dataPolicyService = dataPolicyService;
	}

	@Override
	public Class<? extends DataPolicyDTO> getDtoClass() {
		return DataPolicyDTO.class;
	}
}
