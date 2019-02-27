/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.changeset.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * The product change set dependency resolver class.
 */
public class ProductChangeSetDependencyResolverImpl implements ChangeSetDependencyResolver {
	
	private ProductAssociationService productAssociationService;
	
	private ProductLookup productLookup;
	
	private LoadTuner loadTuner;
	
	/**
	 * Set the production association service. 
	 * 
	 * @param productAssociationService the production association service
	 */
	public void setProductAssociationService(
			final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}

	@Override
	public Product getObject(final BusinessObjectDescriptor businessObjectDescriptor, final Class<?> objectClass) {
		if (Product.class.isAssignableFrom(objectClass)) {
			return productLookup.findByGuid(businessObjectDescriptor.getObjectIdentifier());
		}
		return null;
	}

	@Override
	public Set<?> getChangeSetDependency(final Object object) {
		if (object instanceof Product) {
			Set<Object> dependents = new LinkedHashSet<>();
			Product product = (Product) object;
			dependents.addAll(getDependentCategories(product));
			dependents.addAll(getDependentAssociatedProducts(product));
			dependents.addAll(getDependentProductType(product));
			dependents.addAll(getDependentBrand(product));
			dependents.addAll(getDependentSkuOptions(product));
			
			return dependents;
		} 
		return Collections.emptySet();
	}

	private Collection<?> getDependentSkuOptions(final Product product) {
		Set<Object> dependents = new LinkedHashSet<>();
		Map<String, ProductSku> productSkuMap = product.getProductSkus();
		if (productSkuMap == null || productSkuMap.isEmpty()) {
			return dependents;
		}
		Collection<ProductSku> productSkus = productSkuMap.values();
		
		for (ProductSku productSku : productSkus) {
			Collection<SkuOptionValue> optionValues = productSku.getOptionValues();
			if (optionValues != null && !optionValues.isEmpty()) {
				for (SkuOptionValue optionValue : optionValues) {
					dependents.add(optionValue.getSkuOption());
				}
			}
		}

		return dependents; 
	}

	private Collection<?> getDependentBrand(final Product product) {
		Set<Object> dependents = new LinkedHashSet<>();
		Brand brand = product.getBrand();
		if (brand != null) {
			dependents.add(brand);
		}
		return dependents; 
	}


	private Collection<?> getDependentProductType(final Product product) {

		Set<Object> dependents = new LinkedHashSet<>();
		ProductType productType = product.getProductType();
		if (productType != null) {
			dependents.add(productType);
		}
		return dependents;
	}

	private Collection<?> getDependentAssociatedProducts(final Product product) {
		ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setSourceProductCode(product.getCode());
		criteria.setWithinCatalogOnly(false);

		Set<Product> associationSet = new LinkedHashSet<>();
		List<ProductAssociation> productAssociations = productAssociationService.findByCriteria(criteria, getLoadTuner());
		for (ProductAssociation productAssociation : productAssociations) {
			associationSet.add(productAssociation.getTargetProduct());
		}
		return associationSet;
	}

	private Collection<?> getDependentCategories(final Product product) {
		return product.getCategories();
	}

	/**
	 * @return the product load tuner
	 */
	public LoadTuner getLoadTuner() {
		return loadTuner;
	}

	/**
	 * @param loadTuner the load tuner to be used for product associations
	 */
	public void setLoadTuner(final LoadTuner loadTuner) {
		this.loadTuner = loadTuner;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}
}
