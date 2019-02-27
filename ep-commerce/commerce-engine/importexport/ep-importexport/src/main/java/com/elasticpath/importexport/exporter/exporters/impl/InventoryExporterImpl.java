/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.inventory.InventorySkuAdapter;
import com.elasticpath.importexport.common.dto.inventory.InventorySkuDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Exporter implementation for productSku and inventorySku.
 */
public class InventoryExporterImpl extends AbstractExporterImpl<ProductSku, InventorySkuDTO, String> {
	
	private InventorySkuAdapter inventorySkuAdapter;

	private ProductSkuLookup productSkuLookup;
	
	private static final Logger LOG = Logger.getLogger(InventoryExporterImpl.class);
	
	private ProductInventoryManagementService productInventoryManagementService;
	
	@Override
	protected void initializeExporter(final ExportContext context) {
		// do nothing
	}

	@Override
	protected List<ProductSku> findByIDs(final List<String> subList) {
		List<ProductSku> productSkus = new ArrayList<>();
		for (String skuCode : subList) {
			ProductSku productSku = getProductSkuLookup().findBySkuCode(skuCode);
			if (productSku == null) {
				LOG.error(new Message("IE-22000", skuCode));
				continue;
			}

			productSkus.add(productSku);
		}
		return productSkus;
	}

	@Override
	protected int getObjectsQty(final ProductSku domain) {
		Map<Long, InventoryDto> inventories = productInventoryManagementService.getInventoriesForSku(domain);
		return inventories.size();
	}

	@Override
	protected DomainAdapter<ProductSku, InventorySkuDTO> getDomainAdapter() {
		return inventorySkuAdapter;
	}

	@Override
	protected Class<? extends InventorySkuDTO> getDtoClass() {
		return InventorySkuDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		return new ArrayList<>(getContext().getDependencyRegistry().getDependentGuids(ProductSku.class));
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] {ProductSku.class};
	}

	@Override
	public JobType getJobType() {
		return JobType.INVENTORY;
	}

	/**
	 * Gets inventorySkuAdapter.
	 *
	 * @return the inventorySkuAdapter
	 */
	public InventorySkuAdapter getInventorySkuAdapter() {
		return inventorySkuAdapter;
	}

	/**
	 * Sets inventorySkuAdapter.
	 *
	 * @param inventorySkuAdapter the inventorySkuAdapter to set
	 */
	public void setInventorySkuAdapter(final InventorySkuAdapter inventorySkuAdapter) {
		this.inventorySkuAdapter = inventorySkuAdapter;
	}

	public void setProductInventoryManagementService(
			final ProductInventoryManagementService productInventoryManagementService) {
		this.productInventoryManagementService = productInventoryManagementService;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}
