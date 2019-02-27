/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cmclient.core.service;

import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductSkuModel;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

import java.util.List;

/**
 * Provides product-related business services to the CM Client.
 */
public interface ProductModelService {

	/**
	 * Builds a product model for the product editor.
	 * @param products the products to build the model for.
	 * @return the models for the specified products.
	 */
	ProductModel[] buildLiteProductModels(List<Product> products);

	/**
	 * Builds an array of lightweight productSkuModels for a list of productSkus.
	 *
	 * @param productSkus the productSkus to build the models for
	 * @return the models for the specified productSkus
	 */
	ProductSkuModel[] buildLiteProductSkuModels(List<ProductSku> productSkus);

	/**
	 * Builds product wizard model.
	 *
	 * @param product the blank product
	 * @return product wizard model
	 */
	ProductModel buildProductWizardModel(Product product);

	/**
	 * Builds product editor model.
	 *
	 * @param productGuid product GUID
	 * @return product editor model
	 */
	ProductModel buildProductEditorModel(String productGuid);

	/**
	 * Builds product sku editor model.
	 *
	 * @param productSku product sku
	 * @return product sku editor model
	 */
	ProductSkuModel buildProductSkuEditorModel(ProductSku productSku);

	/**
	 * Builds product sku editor model.
	 *
	 * @param productSkuGuid the guid for the sku to build the editor for.
	 * @return product sku editor model
	 */
	ProductSkuModel buildProductSkuEditorModel(String productSkuGuid);
}
