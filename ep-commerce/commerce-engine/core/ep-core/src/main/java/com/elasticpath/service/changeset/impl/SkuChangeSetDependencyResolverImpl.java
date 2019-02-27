/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.changeset.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;

/**
 * the class to resolve the change set dependent objects of product Sku's.
 */
public class SkuChangeSetDependencyResolverImpl implements
		ChangeSetDependencyResolver {

	private ProductSkuLookup productSkuLookup;

	@Override
	public ProductSku getObject(final BusinessObjectDescriptor businessObjectDescriptor, final Class<?> objectClass) {
		if (ProductSku.class.isAssignableFrom(objectClass)) {
			return getProductSkuLookup().findByGuid(businessObjectDescriptor.getObjectIdentifier());
		}
		return null;
	}

	@Override
	public Set<?> getChangeSetDependency(final Object object) {
		if (object instanceof ProductSku) {
			ProductSku sku = (ProductSku) object;
			if (sku.getProduct().hasMultipleSkus()) {
				Set<Object> dependents = new LinkedHashSet<>();
				dependents.add(sku.getProduct());
				dependents.addAll(getSkuOptions(sku));
				return dependents;
			}
		}
		return Collections.emptySet();
	}

	private Collection<? extends Object> getSkuOptions(final ProductSku sku) {
		Set<Object> dependents = new LinkedHashSet<>();
		for (SkuOptionValue skuOptionValue : sku.getOptionValues()) {
			SkuOption skuOption = skuOptionValue.getSkuOption();
			dependents.add(skuOption);
		}
		return dependents;
	}

	/**
	 * Set the product sku service.
	 *
	 * @param productSkuLookup the product sku service
	 */
	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}
}
