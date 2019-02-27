/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Helper class for registering dependencies for Product. Dependencies may be to external types,
 * i.e. Brand or Catalog or they may be to Products (through ProductAssociations)
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.GodClass" })
class ProductDependencyHelper {

	private final DependencyRegistry dependencyRegistry;

	private final List<Long> productUidPkList;

	private final ProductAssociationService productAssociationService;

	private final List<Long> futureNonDependant;

	/**
	 * Constructs ProductDependencyHelper and sets the data it depends on.
	 *
	 * @param dependencyRegistry registry of dependencies
	 * @param productUidPkList List of <code>Long</code> UIDpks of Products to export
	 * @param futureNonDependant the futureNonDependant
	 * @param productAssociationService the product association service
	 */
	ProductDependencyHelper(final DependencyRegistry dependencyRegistry,
			final List<Long> productUidPkList,
			final List<Long> futureNonDependant,
			final ProductAssociationService productAssociationService) {
		this.dependencyRegistry = dependencyRegistry;
		this.productUidPkList = productUidPkList;
		this.futureNonDependant = futureNonDependant;
		this.productAssociationService = productAssociationService;
	}

	/**
	 * Adds dependencies for a given List of Products.
	 *
	 * @param products List of Products
	 */
	void addDependencies(final List<Product> products) {
		// NOTE: product list must be updated before adding other dependencies.
		if (dependencyRegistry.supportsDependency(ProductAssociation.class)) {
			updateProductsAndAddProductAssociationsIntoRegistry(products);
		}

		addProductRelatedDependencies(products);

		if (dependencyRegistry.supportsDependency(Brand.class)) {
			addBrandsIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(ProductBundle.class)) {
			addProductBundlesIntoRegistry(products);
		}
	}

	private void addProductRelatedDependencies(final List<Product> products) {
		if (dependencyRegistry.supportsDependency(Attribute.class)) {
			addAttributesIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(ProductType.class)) {
			addProductTypesIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(SkuOption.class)) {
			addSkuOptionsIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(Product.class)) {
			addProductsIntoRegistry(products);
		}
		if (dependencyRegistry.supportsDependency(ProductSku.class)) {
			addProductSkuIntoRegistry(products);
		}
	}

	private void addProductSkuIntoRegistry(final List<Product> products) {
		final NavigableSet<String> productSkuCodes = new TreeSet<>();
		for (Product product : products) {
			for (Entry<String, ProductSku> skuEntry : product.getProductSkus().entrySet()) {
				productSkuCodes.add(skuEntry.getValue().getSkuCode());
			}
		}
		dependencyRegistry.addGuidDependencies(ProductSku.class, productSkuCodes);
	}

	private void addProductsIntoRegistry(final List<Product> products) {
		final NavigableSet<String> productGuids = new TreeSet<>();
		for (Product product : products) {
			productGuids.add(product.getGuid());
		}
		dependencyRegistry.addGuidDependencies(Product.class, productGuids);
	}

	/*
	 * Add UIDs of product associations if its source product is in the list of products to be exported.
	 *
	 * @param products the list of products to be exported
	 */
	private void updateProductsAndAddProductAssociationsIntoRegistry(final List<Product> products) {
		for (Product product : products) {
			if (futureNonDependant == null || !futureNonDependant.contains(product.getUidPk())) {
				addAssociationUids(product);
			}
		}
	}

	/*
	 * Adds GUIDs of associations into dependency registry.
	 * Updates the list of product UIDs used for determination of product associations to be exported
	 */
	private void addAssociationUids(final Product product) {
		final NavigableSet<String> dependentProductAssociations = new TreeSet<>();
		final ProductAssociationSearchCriteria associationCriteria = new ProductAssociationSearchCriteria();
		associationCriteria.setSourceProduct(product);
		for (ProductAssociation productAssociation : productAssociationService.findByCriteria(associationCriteria)) {
			dependentProductAssociations.add(productAssociation.getGuid());
			final Long targetProductUid = productAssociation.getTargetProduct().getUidPk();
			if (!productUidPkList.contains(targetProductUid)) {
				productUidPkList.add(targetProductUid);
				if (futureNonDependant != null) {
					futureNonDependant.add(targetProductUid);
				}
			}
		}
		dependencyRegistry.addGuidDependencies(ProductAssociation.class, dependentProductAssociations);
	}

	/*
	 * Add GUIDs of brands exported products depend on into dependency register.
	 */
	private void addBrandsIntoRegistry(final List<Product> products) {
		final NavigableSet<String> dependentBrands = new TreeSet<>();
		for (Product product : products) {
			if (product.getBrand() != null) {
				dependentBrands.add(product.getBrand().getGuid());
			}
		}
		dependencyRegistry.addGuidDependencies(Brand.class, dependentBrands);
	}

	/*
	 * Add GUIDs of product bundles exported products depend on into dependency register.
	 */
	private void addProductBundlesIntoRegistry(final List<Product> products) {
		final NavigableSet<String> dependentGuids = new TreeSet<>();
		for (Product product : products) {
			if (product instanceof ProductBundle) {
				addBundleUids((ProductBundle) product);
				dependentGuids.add(product.getGuid());
			}
		}
		dependencyRegistry.addGuidDependencies(ProductBundle.class, dependentGuids);
	}

	private void addBundleUids(final ProductBundle productBundle) {
		for (BundleConstituent bundleConstituent : productBundle.getConstituents()) {
			final Long targetProductUid = bundleConstituent.getConstituent().getProduct().getUidPk();
			if (!productUidPkList.contains(targetProductUid)) {
				productUidPkList.add(targetProductUid);
			}
		}
	}

	private void addSkuOptionsIntoRegistry(final List<Product> products) {
		final NavigableSet<String> dependents = new TreeSet<>();
		for (Product product : products) {
			for (Entry<String, ProductSku> entry : product.getProductSkus().entrySet()) {
				for (SkuOptionValue skuOptionValue : entry.getValue().getOptionValues()) {
					dependents.add(skuOptionValue.getSkuOption().getGuid());
				}
			}
		}
		dependencyRegistry.addGuidDependencies(SkuOption.class, dependents);

	}

	private void addProductTypesIntoRegistry(final List<Product> products) {
		final NavigableSet<String> dependents = new TreeSet<>();
		for (Product product : products) {
			if (product.getProductType() != null) {
				dependents.add(product.getProductType().getGuid());
			}
		}
		dependencyRegistry.addGuidDependencies(ProductType.class, dependents);
	}

	private void addAttributesIntoRegistry(final List<Product> products) {
		final NavigableSet<String> dependents = new TreeSet<>();
		for (Product product : products) {
			for (Entry<String, AttributeValue> entry : product.getAttributeValueMap().entrySet()) {
				dependents.add(entry.getValue().getAttribute().getGuid());
			}
		}
		dependencyRegistry.addGuidDependencies(Attribute.class, dependents);
	}

}
