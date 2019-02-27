/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.base.GloballyIdentifiable;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.cmimportjob.CmImportJobDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Implements an exporter for a {@link ImportJob}.
 */
public class CmImportJobExporter extends AbstractExporterImpl<ImportJob, CmImportJobDTO, String> {

	private static final Logger LOG = Logger.getLogger(CmImportJobExporter.class);

	private ImportExportSearcher importExportSearcher;
	
	private ImportService importService;

	private DomainAdapter<ImportJob, CmImportJobDTO> cmImportJobAdapter;

	private Set<String> cmImportJobGuids;

	@Override
	public JobType getJobType() {
		return JobType.CM_IMPORT_JOB;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { ImportJob.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		cmImportJobGuids = new HashSet<>(
			getImportExportSearcher().searchGuids(getContext().getSearchConfiguration(), EPQueryType.CM_IMPORT_JOB));
	}

	@Override
	protected Class<? extends CmImportJobDTO> getDtoClass() {
		return CmImportJobDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(ImportJob.class)) {
			cmImportJobGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(ImportJob.class));
		}
		LOG.info("The list for " + this.cmImportJobGuids.size() + " import job is retrieved from the database.");
		return new ArrayList<>(cmImportJobGuids);
	}

	@Override
	protected List<ImportJob> findByIDs(final List<String> subList) {
		Set<String> subGuids = new HashSet<>(subList);
		List<ImportJob> results = new ArrayList<>(getImportService().findByGuids(subGuids));
		Collections.sort(results, Comparator.comparing(GloballyIdentifiable::getGuid));
		return results;
	}

	@Override
	protected DomainAdapter<ImportJob, CmImportJobDTO> getDomainAdapter() {
		return this.cmImportJobAdapter;
	}
	
	public void setDomainAdapter(final DomainAdapter<ImportJob, CmImportJobDTO> cmImportJobAdapter) {
		this.cmImportJobAdapter = cmImportJobAdapter;
	}

	private ImportService getImportService() {
		return importService;
	}
	
	public void setImportService(final ImportService service) {
		this.importService = service;
	}
	
	private ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}
}