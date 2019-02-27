/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;
/**
 *	Resolver for Catalog Category Types. 
 */
public class CatalogCategoryTypeChangeSetResolverImpl implements ChangeSetDependencyResolver {

	private  CategoryTypeService categoryTypeService;
	
	@Override
	public Set<?> getChangeSetDependency(final Object object) {

	
		if (object instanceof CategoryType) {
			Set<Object> depends = new LinkedHashSet<>();
			CategoryType categoryType = (CategoryType) object;
			Set<AttributeGroupAttribute> attributeGroupAttributes = categoryType.getAttributeGroup().getAttributeGroupAttributes();
			for (AttributeGroupAttribute attributeGroupAttribute : attributeGroupAttributes) {
				Attribute attribute = attributeGroupAttribute.getAttribute();
				depends.add(attribute);
			}
			return depends;
		}
		
		return Collections.emptySet();
	}
	
	
	
	@Override
	public Object getObject(final BusinessObjectDescriptor object, final Class<?> objectClass) {
		
		if (CategoryType.class.isAssignableFrom(objectClass)) {
			return categoryTypeService.findByGuid(object.getObjectIdentifier());
		}
		return null;
	}


	/**
	 *
	 * @return the categoryTypeService
	 */
	public CategoryTypeService getCategoryTypeService() {
		return categoryTypeService;
	}


	/**
	 *
	 * @param categoryTypeService the categoryTypeService to set
	 */
	public void setCategoryTypeService(final CategoryTypeService categoryTypeService) {
		this.categoryTypeService = categoryTypeService;
	}

}
