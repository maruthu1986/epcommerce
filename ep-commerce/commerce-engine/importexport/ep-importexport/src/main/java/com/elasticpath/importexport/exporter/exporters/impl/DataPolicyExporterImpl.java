/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.datapolicy.DataPolicyDTO;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Exporter for data policy and their data points.
 */
public class DataPolicyExporterImpl extends AbstractExporterImpl<DataPolicy, DataPolicyDTO, String> {

	private static final Logger LOG = Logger.getLogger(DataPolicyExporterImpl.class);

	private ImportExportSearcher importExportSearcher;

	private DataPolicyService dataPolicyService;

	private List<String> dataPolicyGuids;

	private DomainAdapter<DataPolicy, DataPolicyDTO> dataPolicyAdapter;

	@Override
	public JobType getJobType() {
		return JobType.DATA_POLICY;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { DataPolicy.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		dataPolicyGuids = new ArrayList<>();

		for (DataPolicy dataPolicy : dataPolicyService.list()) {
			dataPolicyGuids.add(dataPolicy.getGuid());
		}
		LOG.info("The list for " + dataPolicyGuids.size() + " data policy(s) is retrieved from the database.");

	}

	@Override
	protected Class<? extends DataPolicyDTO> getDtoClass() {
		return DataPolicyDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(DataPolicy.class)) {
			dataPolicyGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(DataPolicy.class));
		}
		return dataPolicyGuids;
	}

	@Override
	protected List<DataPolicy> findByIDs(final List<String> subList) {
		return dataPolicyService.findByGuids(subList);
	}

	@Override
	protected DomainAdapter<DataPolicy, DataPolicyDTO> getDomainAdapter() {
		return this.dataPolicyAdapter;
	}

	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	public void setDataPolicyService(final DataPolicyService dataPolicyService) {
		this.dataPolicyService = dataPolicyService;
	}

	public void setDataPolicyAdapter(final DomainAdapter<DataPolicy, DataPolicyDTO> dataPolicyAdapter) {
		this.dataPolicyAdapter = dataPolicyAdapter;
	}

}
