/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.store.Store;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.shipping.ShippingServiceLevelDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Exporter for {@link ShippingServiceLevel} objects.
 */
public class ShippingServiceLevelExporterImpl extends AbstractExporterImpl<ShippingServiceLevel, ShippingServiceLevelDTO, String> {

	private static final Logger LOG = Logger.getLogger(ShippingServiceLevelExporterImpl.class);

	private ImportExportSearcher importExportSearcher;
	private ShippingServiceLevelService shippingServiceLevelService;
	private DomainAdapter<ShippingServiceLevel, ShippingServiceLevelDTO> domainAdapter;
	private List<String> shippingServiceLevelGuids;

	@Override
	protected List<ShippingServiceLevel> findByIDs(final List<String> subList) {
		List<ShippingServiceLevel> sslList = new ArrayList<>();

		for (String guid : subList) {
			ShippingServiceLevel current = getShippingServiceLevelService().findByGuid(guid);
			if (current != null) {
				sslList.add(current);
			}
		}
		return sslList;
	}

	@Override
	protected void addDependencies(final List<ShippingServiceLevel> ssls,
			final DependencyRegistry dependencyRegistry) {
		for (ShippingServiceLevel shippingServiceLevel : ssls) {
			if (dependencyRegistry.supportsDependency(Store.class)) {
				dependencyRegistry.addGuidDependency(Store.class, shippingServiceLevel.getStore().getCode());
			}
			if (dependencyRegistry.supportsDependency(ShippingRegion.class)) {
				dependencyRegistry.addGuidDependency(ShippingRegion.class, shippingServiceLevel.getShippingRegion().getName());
			}
		}
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(ShippingServiceLevel.class)) {
			shippingServiceLevelGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(ShippingServiceLevel.class));
		}
		return shippingServiceLevelGuids;
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		shippingServiceLevelGuids = new ArrayList<>();
		shippingServiceLevelGuids.addAll(
				getImportExportSearcher().searchGuids(
						getContext().getSearchConfiguration(),
						EPQueryType.SHIPPING_SERVICE_LEVEL));
		
		LOG.info("Shipping Service Level Export \n\t" + shippingServiceLevelGuids.size()
				+ " Shipping Service Level GUIDs found for export [" + shippingServiceLevelGuids + "]");
	}

	@Override
	protected Class<? extends ShippingServiceLevelDTO> getDtoClass() {
		return ShippingServiceLevelDTO.class;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { ShippingServiceLevel.class };
	}

	@Override
	public JobType getJobType() {
		return JobType.SHIPPING_SERVICE_LEVEL;
	}

	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	public void setDomainAdapter(final DomainAdapter<ShippingServiceLevel, ShippingServiceLevelDTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}

	@Override
	protected DomainAdapter<ShippingServiceLevel, ShippingServiceLevelDTO> getDomainAdapter() {
		return domainAdapter;
	}

	public ShippingServiceLevelService getShippingServiceLevelService() {
		return shippingServiceLevelService;
	}

	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

}
