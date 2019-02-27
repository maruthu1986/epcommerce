/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cucumber.tax;

import static org.junit.Assert.assertEquals;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.tax.TaxCodeRetriever;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Helper class for {@link TaxCodeRetrieverStepDefinitions}.
 */
public class TaxCodeRetrieverStepDefinitionsHelper {

	@Autowired private ProductService productService;
	@Autowired private ProductSkuService productSkuService;
	@Autowired private ProductSkuLookup productSkuLookup;
	@Autowired private TaxCodeService taxCodeService;
	@Autowired private TestDataPersisterFactory persisterFactory;
	@Autowired private TaxCodeRetriever taxCodeRetriever;

	@Inject @Named("simpleStoreScenarioHolder") private ScenarioContextValueHolder<SimpleStoreScenario> simpleStoreScenarioHolder;

	private String productSkuCode;
	private String resultProductSkuTaxCode;

	/**
	 * Create a product with the given tax code.
	 *
	 * @param taxCode the tax code
	 */
	public void prepareProductWithTaxCode(final String taxCode) {
		SimpleStoreScenario scenario = simpleStoreScenarioHolder.get();
		List<Product> products = persisterFactory.getCatalogTestPersister().persistDefaultShippableProducts(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		Product product = products.get(0);

		TaxCode taxCodeObj = taxCodeService.findByCode(taxCode);
		product.setTaxCodeOverride(taxCodeObj);
		Product savedProduct = productService.saveOrUpdate(product);
		this.productSkuCode = savedProduct.getDefaultSku().getSkuCode();
	}

	/**
	 * Set the product SKU tax code.
	 *
	 * @param taxCode the tax code
	 */
	public void setProductSkuTaxCode(final String taxCode) {
		TaxCode taxCodeObj = taxCodeService.findByCode(taxCode);
		ProductSku productSku = productSkuLookup.findBySkuCode(this.productSkuCode);
		productSku.setTaxCodeOverride(taxCodeObj);
		productSkuService.saveOrUpdate(productSku);
	}

	/**
	 * Set the product SKU tax code to null.
	 */
	public void setNullProductSkuTaxCode() {
		ProductSku productSku = productSkuLookup.findBySkuCode(this.productSkuCode);
		productSku.setTaxCodeOverride(null);
		productSkuService.saveOrUpdate(productSku);
	}

	/**
	 * Get and store the product SKU's tax code.
	 */
	public void fetchProductSkuTaxCode() {
		ProductSku foundSku = productSkuLookup.findBySkuCode(this.productSkuCode);
		resultProductSkuTaxCode = taxCodeRetriever.getEffectiveTaxCode(foundSku).getCode();
	}

	/**
	 * Compare the stored tax code against a desired tax code.
	 * 
	 * @param desiredTaxCode the desired tax code
	 */
	public void checkProductSkuTaxCode(final String desiredTaxCode) {
		assertEquals("Resulting tax code should match expected tax code.", desiredTaxCode, resultProductSkuTaxCode);
	}

}
