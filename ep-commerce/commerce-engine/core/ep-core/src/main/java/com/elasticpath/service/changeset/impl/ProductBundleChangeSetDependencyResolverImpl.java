/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;

/**
 * the class to resolve the change set dependent objects of product bundle.
 */
/**
 * @author dyao
 *
 */
public class ProductBundleChangeSetDependencyResolverImpl implements
		ChangeSetDependencyResolver {
	
	private ProductLookup productLookup;
	
	@Override
	public ProductBundle getObject(final BusinessObjectDescriptor businessObjectDescriptor, final Class<?> objectClass) {
		if (ProductBundle.class.isAssignableFrom(objectClass)) {
			return getProductLookup().findByGuid(businessObjectDescriptor.getObjectIdentifier());
		}
		return null;
	}
	
	@Override
	public Set<?> getChangeSetDependency(final Object object) {
		if (object instanceof ProductBundle) {
			ProductBundle productBundle = (ProductBundle) object;
			List<BundleConstituent> constituents = productBundle.getConstituents();
			Set<Object> dependents = new LinkedHashSet<>();
			for (BundleConstituent constituent : constituents) {
				ConstituentItem constituentItem = constituent.getConstituent();
				if (constituentItem.isProductSku()) {
					dependents.add(constituentItem.getProductSku());
				} else {
					dependents.add(constituentItem.getProduct());
				}
			}
			return dependents;
		}
		return Collections.emptySet();
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}
}
