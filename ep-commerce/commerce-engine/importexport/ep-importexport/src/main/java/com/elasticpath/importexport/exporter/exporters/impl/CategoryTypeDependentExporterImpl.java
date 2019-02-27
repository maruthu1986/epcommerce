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
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.CategoryTypeDTO;
import com.elasticpath.service.catalog.CategoryTypeService;

/**
 * This class is responsible for exporting {@link CategoryType}s.
 */
public class CategoryTypeDependentExporterImpl extends AbstractDependentExporterImpl<CategoryType, CategoryTypeDTO, CatalogDTO> {
	private CategoryTypeService categoryTypeService;

	@Override
	public List<CategoryType> findDependentObjects(final long primaryObjectUid) {
		if (getFilter().isFiltered(primaryObjectUid)) {
			return getByCatalog(primaryObjectUid);
		}

		List<CategoryType> resultList = new ArrayList<>();

		Iterator<String> iter = getContext().getDependencyRegistry().getDependentGuids(CategoryType.class).iterator();
		while (iter.hasNext()) {
			final String guid = iter.next();
			CategoryType categoryType = categoryTypeService.findByGuid(guid);
			addAttributeDependencies(primaryObjectUid, categoryType);

			if (categoryType.getCatalog().getUidPk() == primaryObjectUid) {
				resultList.add(categoryType);
				iter.remove();
			}
		}
		return resultList;
	}

	private void addAttributeDependencies(final long primaryObjectUid, final CategoryType categoryType) {
		Set<AttributeGroupAttribute> attributeGroupAttributes = categoryType.getAttributeGroup().getAttributeGroupAttributes();

		NavigableSet<String> attributeSet = new TreeSet<>();
		for (AttributeGroupAttribute attributeGroupAttribute : attributeGroupAttributes) {
			if (attributeGroupAttribute.getAttribute().getCatalog().getUidPk() == primaryObjectUid
					|| attributeGroupAttribute.getAttribute().isGlobal()) {
				attributeSet.add(attributeGroupAttribute.getAttribute().getGuid());
			}
		}
		getContext().getDependencyRegistry().addGuidDependencies(Attribute.class, attributeSet);
	}

	private List<CategoryType> getByCatalog(final long primaryObjectUid) {
		return categoryTypeService.findAllCategoryTypeFromCatalog(primaryObjectUid);
	}

	@Override
	public void bindWithPrimaryObject(final List<CategoryTypeDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setCategoryTypes(dependentDtoObjects);
	}

	public void setCategoryTypeService(final CategoryTypeService categoryTypeService) {
		this.categoryTypeService = categoryTypeService;
	}
}
