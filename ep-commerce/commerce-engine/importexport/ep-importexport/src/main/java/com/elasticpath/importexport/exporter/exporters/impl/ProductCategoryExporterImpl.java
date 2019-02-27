/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.productcategories.ProductCategoryAdapter;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoriesDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ProductRelation;

/**
 * Exporter implementation for product category exporter.
 */
public class ProductCategoryExporterImpl extends AbstractExporterImpl<Product, ProductCategoriesDTO, String> {

	private ProductCategoryAdapter productCategoryAdapter;

	private QueryService<Product> productQueryService;

	private ProductLoadTuner productLoadTuner;

	@Override
	protected void initializeExporter(final ExportContext context) {
		// do nothing
	}

	@Override
	protected List<Product> findByIDs(final List<String> subList) {
		QueryResult<Product> result = getProductQueryService().query(CriteriaBuilder.criteriaFor(Product.class)
				.with(ProductRelation.having().codes(subList))
				.usingLoadTuner(productLoadTuner)
				.returning(ResultType.ENTITY));
		return result.getResults();
	}

	@Override
	protected DomainAdapter<Product, ProductCategoriesDTO> getDomainAdapter() {
		return productCategoryAdapter;
	}

	@Override
	protected Class<? extends ProductCategoriesDTO> getDtoClass() {
		return ProductCategoriesDTO.class;
	}

	@Override
	public JobType getJobType() {
		return JobType.PRODUCTCATEGORYASSOCIATION;
	}

	@Override
	protected List<String> getListExportableIDs() {
		return new ArrayList<>(getContext().getDependencyRegistry().getDependentGuids(Product.class));
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { Product.class };
	}

	@Override
	protected void addDependencies(final List<Product> objects, final DependencyRegistry dependencyRegistry) {
		if (dependencyRegistry.supportsDependency(Category.class)) {
			addCategoryDependency(objects, dependencyRegistry);
		}
	}

	private void addCategoryDependency(final List<Product> objects, final DependencyRegistry dependencyRegistry) {
		NavigableSet<String> categoryGuids = new TreeSet<>();
		for (Product product : objects) {
			for (Category category : product.getCategories()) {
				categoryGuids.add(category.getGuid());
			}
		}
		dependencyRegistry.addGuidDependencies(Category.class, categoryGuids);
	}

	@Override
	protected int getObjectsQty(final Product domain) {
		return domain.getCategories().size();
	}

	/**
	 * Gets the productCategoryAdapter.
	 * 
	 * @return the productCategoryAdapter
	 * @see ProductCategoryAdapter
	 */
	public ProductCategoryAdapter getProductCategoryAdapter() {
		return productCategoryAdapter;
	}

	/**
	 * Sets the productCategoryAdapter.
	 * 
	 * @param productCategoryAdapter the productCategoryAdapter to set
	 * @see ProductCategoryAdapter
	 */
	public void setProductCategoryAdapter(final ProductCategoryAdapter productCategoryAdapter) {
		this.productCategoryAdapter = productCategoryAdapter;
	}

	/**
	 * Gets the productQueryService.
	 * 
	 * @return the productQueryService
	 */
	public QueryService<Product> getProductQueryService() {
		return productQueryService;
	}

	/**
	 * Sets the productQueryService.
	 * 
	 * @param productQueryService the productQueryService to set
	 */
	public void setProductQueryService(final QueryService<Product> productQueryService) {
		this.productQueryService = productQueryService;
	}

	/**
	 * Gets the productLoadTuner.
	 * 
	 * @return the productLoadTuner
	 */
	public ProductLoadTuner getProductLoadTuner() {
		return productLoadTuner;
	}

	/**
	 * Sets the productLoadTuner.
	 * 
	 * @param productLoadTuner the productLoadTuner to set
	 */
	public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}
}
