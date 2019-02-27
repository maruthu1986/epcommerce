/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.SkuOptionDTO;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * This class is responsible for exporting {@link SkuOption}s.
 */
public class SkuOptionDependentExporterImpl extends AbstractDependentExporterImpl<SkuOption, SkuOptionDTO, CatalogDTO> {
	private SkuOptionService skuOptionService;

	@Override
	public List<SkuOption> findDependentObjects(final long primaryObjectUid) {
		final DependencyRegistry dependencyRegistry = getContext().getDependencyRegistry();
		List<SkuOption> resultList = new ArrayList<>();

		if (getFilter().isFiltered(primaryObjectUid)) {
			resultList = getByCatalog(primaryObjectUid);
		} else {
			Iterator<String> iter = dependencyRegistry.getDependentGuids(SkuOption.class).iterator();
			while (iter.hasNext()) {
				final String guid = iter.next();
				SkuOption skuOption = skuOptionService.findByKey(guid);
				if (skuOption.getCatalog().getUidPk() == primaryObjectUid) {
					resultList.add(skuOption);
					iter.remove();
				}
			}
		}
		return resultList;
	}

	private List<SkuOption> getByCatalog(final long primaryObjectUid) {
		return skuOptionService.findAllSkuOptionFromCatalog(primaryObjectUid);
	}

	@Override
	public void bindWithPrimaryObject(final List<SkuOptionDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setSkuOptions(dependentDtoObjects);
	}

	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}
}
