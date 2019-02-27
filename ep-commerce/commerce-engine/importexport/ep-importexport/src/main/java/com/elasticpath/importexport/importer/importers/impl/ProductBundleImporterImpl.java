/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.pricing.PriceAdjustmentService;

/**
 * ProductBundle Importer Implementation.
 */
public class ProductBundleImporterImpl extends AbstractImporterImpl<ProductBundle, ProductBundleDTO> {

	private DomainAdapter<ProductBundle, ProductBundleDTO> productBundleAdapter;
	
	private ProductLookup productLookup;

	private PriceAdjustmentService priceAdjustmentService;  
	@Override
	protected ProductBundle findPersistentObject(final ProductBundleDTO dto) {
		Product productBundle = getProductLookup().findByGuid(dto.getCode());
		if (!(productBundle instanceof ProductBundle)) {
			// PopulationRuntimeException - because it should not rollback transaction, 
			// and it is not in adapter because adapter is on another level.
			throw new PopulationRuntimeException("IE-10325", dto.getCode()); 
		}
		
		return (ProductBundle) productBundle;
	}

	@Override
	protected DomainAdapter<ProductBundle, ProductBundleDTO> getDomainAdapter() {
		return productBundleAdapter;
	}

	@Override
	protected String getDtoGuid(final ProductBundleDTO dto) {
		return dto.getCode();
	}

	@Override
	protected void setImportStatus(final ProductBundleDTO object) {
		getStatusHolder().setImportStatus("(" + object.getCode() + ")");		
	}

	@Override
	public String getImportedObjectName() {
		return ProductBundleDTO.ROOT_ELEMENT;
	}

	@Override
	protected CollectionsStrategy<ProductBundle, ProductBundleDTO> getCollectionsStrategy() {
		return new ProductBundleCollectionsStrategy();
	}

	/**
	 * @param productBundleAdapter the productBundleAdapter to set
	 */
	public void setProductBundleAdapter(final DomainAdapter<ProductBundle, ProductBundleDTO> productBundleAdapter) {
		this.productBundleAdapter = productBundleAdapter;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	/**
	 * Collections strategy for Product Bundle object.
	 */
	private static final class ProductBundleCollectionsStrategy implements CollectionsStrategy<ProductBundle, ProductBundleDTO> {

		@Override
		public void prepareCollections(final ProductBundle domainObject, final ProductBundleDTO dto) {
			//no-op
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}
	}
	

	/**
	 * @return the priceAdjustmentService
	 */
	public PriceAdjustmentService getPriceAdjustmentService() {
		return priceAdjustmentService;
	}

	/**
	 * @param priceAdjustmentService the priceAdjustmentService to set
	 */
	public void setPriceAdjustmentService(final PriceAdjustmentService priceAdjustmentService) {
		this.priceAdjustmentService = priceAdjustmentService;
	}

	@Override
	public Class<? extends ProductBundleDTO> getDtoClass() {
		return ProductBundleDTO.class;
	}	
}
