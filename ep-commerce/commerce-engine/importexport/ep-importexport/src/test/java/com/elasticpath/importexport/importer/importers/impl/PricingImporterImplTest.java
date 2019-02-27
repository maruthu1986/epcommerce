/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.importexport.common.adapters.pricing.ProductPricesAdapter;
import com.elasticpath.importexport.common.dto.pricing.CatalogPriceDTO;
import com.elasticpath.importexport.common.dto.pricing.ProductPricesDTO;
import com.elasticpath.importexport.common.dto.pricing.SkuPricesDTO;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;

/**
 * Test for <code>PricingImporterImpl</code>.
 */
public class PricingImporterImplTest {

	private PricingImporterImpl pricingImporter;

	private SavingStrategy<Product, ProductPricesDTO> mockSavingStrategy;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductPricesDTO productPricesDTO;


	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		productPricesDTO = new ProductPricesDTO();
		pricingImporter = new PricingImporterImpl();

		mockSavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT, new SavingManager<Product>() {

			@Override
			public void save(final Product persistable) {
				// do nothing
			}

			@Override
			public Product update(final Product persistable) {
				return null;
			}

		});

		pricingImporter.setProductPricesAdapter(new MockProductPricesAdapter());
		pricingImporter.setStatusHolder(new ImportStatusHolder());
	}

	/**
	 * Check an import with non-initialized importer.
	 */
	@Test(expected = ImportRuntimeException.class)
	public void testExecuteNonInitializedImport() {
		pricingImporter.executeImport(productPricesDTO);
	}

	/**
	 * Check an import of product pricing.
	 */
	@Test
	public void testExecuteImport() {
		ImportConfiguration importConfiguration = new ImportConfiguration();
		importConfiguration.setImporterConfigurationMap(new HashMap<>());

		pricingImporter.initialize(new ImportContext(importConfiguration), mockSavingStrategy);
		pricingImporter.executeImport(productPricesDTO);
		assertEquals("product", pricingImporter.getImportedObjectName());
		assertNotNull(pricingImporter.getSavingStrategy());
	}

	/**
	 * Test for count objects qty.
	 */
	@Test
	public void testCountQtyObjects() {
		ProductPricesDTO pricesDTO = new ProductPricesDTO();
		pricesDTO.setBaseCatalogPriceDTO(new CatalogPriceDTO());

		List<CatalogPriceDTO> catalogPricesList = new ArrayList<>();
		catalogPricesList.add(new CatalogPriceDTO());
		pricesDTO.setOverridenCatalogPriceList(catalogPricesList);

		List<SkuPricesDTO> skuPricesDTOList = new ArrayList<>();
		SkuPricesDTO skuPricesDTO = new SkuPricesDTO();
		List<CatalogPriceDTO> skuCatalogPricesList = new ArrayList<>();
		skuCatalogPricesList.add(new CatalogPriceDTO());
		skuCatalogPricesList.add(new CatalogPriceDTO());
		skuCatalogPricesList.add(new CatalogPriceDTO());
		skuPricesDTO.setSkuCatalogPrices(skuCatalogPricesList);
		skuPricesDTOList.add(skuPricesDTO);
		pricesDTO.setSkuPricesDTOList(skuPricesDTOList);

		assertEquals(1 + 2 + 2, pricingImporter.getObjectsQty(pricesDTO));
	}

	/** The dto class must be present and correct. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", ProductPricesDTO.class, pricingImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(pricingImporter.getAuxiliaryJaxbClasses());
	}

	/**
	 * Mock product prices adapter.
	 */
	private class MockProductPricesAdapter extends ProductPricesAdapter {

		@Override
		public void populateDomain(final ProductPricesDTO catalogPricesDto, final Product product) {
			// do nothing
		}

		@Override
		public void populateDTO(final Product product, final ProductPricesDTO productPrices) {
			// do nothing
		}
	}
}
