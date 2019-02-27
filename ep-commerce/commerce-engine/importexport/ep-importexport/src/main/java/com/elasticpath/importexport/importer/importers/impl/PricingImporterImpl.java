/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.importer.importers.impl;

import java.util.List;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.pricing.ProductPricesAdapter;
import com.elasticpath.importexport.common.dto.pricing.CatalogPriceDTO;
import com.elasticpath.importexport.common.dto.pricing.ProductPricesDTO;
import com.elasticpath.importexport.common.dto.pricing.SkuPricesDTO;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;

/**
 * Pricing importer implementation.
 */
public class PricingImporterImpl extends AbstractImporterImpl<Product, ProductPricesDTO> {

	private static final int TEMPORARY_COMMIT_UNIT = 5000;

	private ProductPricesAdapter productPricesAdapter;
	
	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Product, ProductPricesDTO> savingStrategy) {
		super.initialize(
				context,
				AbstractSavingStrategy.<Product, ProductPricesDTO>createStrategy(ImportStrategyType.UPDATE,
						savingStrategy.getSavingManager()));
		
		this.setCommitUnit(TEMPORARY_COMMIT_UNIT);
	}

	@Override
	protected Product findPersistentObject(final ProductPricesDTO dto) {
		return null;
	}
	
	@Override
	protected String getDtoGuid(final ProductPricesDTO dto) {
		return dto.getProductCode();
	}

	@Override
	protected void setImportStatus(final ProductPricesDTO object) {
		getStatusHolder().setImportStatus("(for product " + object.getProductCode() + ")");
	}

	@Override
	protected DomainAdapter<Product, ProductPricesDTO> getDomainAdapter() {
		return productPricesAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return ProductPricesDTO.ROOT_ELEMENT;
	}

	@Override
	public int getObjectsQty(final ProductPricesDTO dto) {
		int result = 0;
		if (dto.getBaseCatalogPriceDTO() != null) {
			result++;
		}

		List<CatalogPriceDTO> overridenCatalogPriceList = dto.getOverridenCatalogPriceList();
		result += overridenCatalogPriceList.size();

		List<SkuPricesDTO> skuPricesDTOList = dto.getSkuPricesDTOList();
		for (SkuPricesDTO skuPricesDTO : skuPricesDTOList) {
			result += skuPricesDTO.getSkuCatalogPrices().size();
		}

		return result;		
	}

	/**
	 * Sets the productPricesAdapter.
	 * 
	 * @param productPricesAdapter the productPricesAdapter to set
	 */
	public void setProductPricesAdapter(final ProductPricesAdapter productPricesAdapter) {
		this.productPricesAdapter = productPricesAdapter;
	}

	@Override
	public Class<? extends ProductPricesDTO> getDtoClass() {
		return ProductPricesDTO.class;
	}
}
