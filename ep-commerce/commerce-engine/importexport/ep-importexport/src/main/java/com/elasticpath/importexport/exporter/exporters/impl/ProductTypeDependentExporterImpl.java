/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.ProductTypeDTO;
import com.elasticpath.service.catalog.ProductTypeService;

/**
 * This class is responsible for exporting {@link ProductType}.
 */
public class ProductTypeDependentExporterImpl extends AbstractDependentExporterImpl<ProductType, ProductTypeDTO, CatalogDTO> {
	private ProductTypeService productTypeService;

	@Override
	public List<ProductType> findDependentObjects(final long primaryObjectUid) {
		if (getFilter().isFiltered(primaryObjectUid)) {
			return getByCatalog(primaryObjectUid);
		}

		List<ProductType> resultList = new ArrayList<>();

		Iterator<String> iter = getContext().getDependencyRegistry().getDependentGuids(ProductType.class).iterator();
		while (iter.hasNext()) {
			final String guid = iter.next();
			ProductType productType = productTypeService.findByGuid(guid);
			addAttributeAndSkuOptionDependencies(primaryObjectUid, productType);

			if (productType.getCatalog().getUidPk() == primaryObjectUid) {
				resultList.add(productType);
				iter.remove();
			}
		}
		return resultList;
	}

	private void addAttributeAndSkuOptionDependencies(final long primaryObjectUid, final ProductType productType) {
		Set<AttributeGroupAttribute> productAttributeGroupAttributes = productType.getProductAttributeGroupAttributes();

		NavigableSet<String> attributeGuidsSet = new TreeSet<>();
		for (AttributeGroupAttribute attributeGroupAttribute : productAttributeGroupAttributes) {
			Attribute attribute = attributeGroupAttribute.getAttribute();
			if (attribute.getCatalog().getUidPk() == primaryObjectUid || attribute.isGlobal()) {
				attributeGuidsSet.add(attribute.getGuid());
			}
		}

		Set<AttributeGroupAttribute> skuAttributeGroupAttributes = productType.getSkuAttributeGroup().getAttributeGroupAttributes();
		for (AttributeGroupAttribute attributeGroupAttribute : skuAttributeGroupAttributes) {
			Attribute attribute = attributeGroupAttribute.getAttribute();
			if (attribute.getCatalog().getUidPk() == primaryObjectUid || attribute.isGlobal()) {
				attributeGuidsSet.add(attribute.getGuid());
			}
		}

		getContext().getDependencyRegistry().addGuidDependencies(Attribute.class, attributeGuidsSet);

		Set<SkuOption> skuOptions = productType.getSkuOptions();

		NavigableSet<String> skuOptionGuidsSet = new TreeSet<>();
		for (SkuOption skuOption : skuOptions) {
			skuOptionGuidsSet.add(skuOption.getGuid());
		}

		getContext().getDependencyRegistry().addGuidDependencies(SkuOption.class, skuOptionGuidsSet);
	}

	private List<ProductType> getByCatalog(final long primaryObjectUid) {
		return productTypeService.findAllProductTypeFromCatalog(primaryObjectUid);
	}

	@Override
	public void bindWithPrimaryObject(final List<ProductTypeDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setProductTypes(dependentDtoObjects);
	}

	public void setProductTypeService(final ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}
}
