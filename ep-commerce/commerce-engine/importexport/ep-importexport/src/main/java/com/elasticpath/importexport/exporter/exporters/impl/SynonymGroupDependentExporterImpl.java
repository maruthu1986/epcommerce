/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.SynonymGroupDTO;
import com.elasticpath.service.search.SynonymGroupService;

/**
 * This class is responsible for exporting {@link SynonymGroup}s.
 */
public class SynonymGroupDependentExporterImpl extends AbstractDependentExporterImpl<SynonymGroup, SynonymGroupDTO, CatalogDTO> {
	private SynonymGroupService synonymGroupService;

	@Override
	public List<SynonymGroup> findDependentObjects(final long primaryObjectUid) {
		if (getFilter().isFiltered(primaryObjectUid)) {
			return getByCatalog(primaryObjectUid);
		}
		return new ArrayList<>();
	}

	private List<SynonymGroup> getByCatalog(final long primaryObjectUid) {
		return new ArrayList<>(synonymGroupService.findAllSynonymGroupForCatalog(primaryObjectUid));
	}

	@Override
	public void bindWithPrimaryObject(final List<SynonymGroupDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setSynonymGroups(dependentDtoObjects);
	}

	public void setSynonymGroupService(final SynonymGroupService synonymGroupService) {
		this.synonymGroupService = synonymGroupService;
	}
}
