/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.tax.TaxCodeDTO;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * Implements an exporter for {@link TaxCode}.
 */
public class TaxCodeExporter extends AbstractExporterImpl<TaxCode, TaxCodeDTO, String> {

	private static final Logger LOG = Logger.getLogger(TaxCodeExporter.class);

	private TaxCodeService taxCodeService;

	private DomainAdapter<TaxCode, TaxCodeDTO> taxCodeAdapter;

	private List<String> taxCodeGuids;

	private ImportExportSearcher importExportSearcher;
	
	@Override
	public JobType getJobType() {
		return JobType.TAXCODE;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { TaxCode.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		taxCodeGuids = new ArrayList<>();
		taxCodeGuids.addAll(
				getImportExportSearcher().searchGuids(
						getContext().getSearchConfiguration(),
						EPQueryType.TAXCODE));
		
		/**
		 * Eventually: taxCodeUidPkList = importExportSearcher.searchUids(getContext().getSearchConfiguration(), EPQueryType.TAXCODE);
		 */

		LOG.info("The list for " + taxCodeGuids.size() + " tax codes is retrieved from database.");
	}

	@Override
	protected Class<? extends TaxCodeDTO> getDtoClass() {
		return TaxCodeDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {

		if (getContext().getDependencyRegistry().supportsDependency(TaxCode.class)) {
			taxCodeGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(TaxCode.class));
		}

		return taxCodeGuids;
	}

	@Override
	protected List<TaxCode> findByIDs(final List<String> subList) {
		List<TaxCode> codes = new ArrayList<>();
		for (String guid : subList) {
			TaxCode taxCode = taxCodeService.findByGuid(guid);
			if (taxCode != null) {
				codes.add(taxCode);
			}
		}
		return codes;
	}

	@Override
	protected DomainAdapter<TaxCode, TaxCodeDTO> getDomainAdapter() {
		return taxCodeAdapter;
	}

	public void setTaxCodeService(final TaxCodeService taxCodeService) {
		this.taxCodeService = taxCodeService;
	}

	public void setTaxCodeAdapter(final DomainAdapter<TaxCode, TaxCodeDTO> adapter) {
		this.taxCodeAdapter = adapter;
	}

	private ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}
	
	/**
	 * @param importExportSearcher The ImportExportSearcher to use.
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}
}
