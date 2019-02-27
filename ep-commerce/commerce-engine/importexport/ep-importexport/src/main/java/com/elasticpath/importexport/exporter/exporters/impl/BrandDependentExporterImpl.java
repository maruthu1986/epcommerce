/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.importexport.common.dto.catalogs.BrandDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.service.catalog.BrandService;

/**
 * This class is responsible for exporting {@link Brand}.
 */
public class BrandDependentExporterImpl extends AbstractDependentExporterImpl<Brand, BrandDTO, CatalogDTO> {
	private BrandService brandService;

	@Override
	public List<Brand> findDependentObjects(final long primaryObjectUid) {
		DependencyRegistry dependencyRegistry = getContext().getDependencyRegistry();
		List<Brand> brandList = new ArrayList<>();

		if (getFilter().isFiltered(primaryObjectUid)) {
			brandList = getByCatalog(primaryObjectUid);
		} else {
			Set<String> brandGuids = dependencyRegistry.getDependentGuids(Brand.class);
			List<String> tempBrandUids = new ArrayList<>(brandGuids);
			for (String brandGuid : tempBrandUids) {
				Brand brand = brandService.findByCode(brandGuid);
				if (brand.getCatalog().getUidPk() == primaryObjectUid) {
					brandList.add(brand);
					brandGuids.remove(brandGuid);
				}
			}
		}

		return brandList;
	}

	@Override
	public void bindWithPrimaryObject(final List<BrandDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setBrands(dependentDtoObjects);
	}

	private List<Brand> getByCatalog(final Long catalogUid) {
		return brandService.findAllBrandsFromCatalog(catalogUid);
	}

	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}
}
