/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.test.integration.pricing;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.apache.openjpa.enhance.PersistenceCapable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.caching.core.pricing.CachingPriceAdjustmentServiceImpl;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.impl.PriceAdjustmentImpl;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;

public class CachingPriceAdjustmentServiceImplITest extends DbTestCase {

	private static final String PRICE_LIST_CODE = "pl1";

	private static final String PRICE_LIST_NAME = PRICE_LIST_CODE;

	private static final String DEFAULT_CURRENCY = "CAD";

	private static final String DEFAULT_DESCRIPTION = "DESC";

	@Autowired
	@Qualifier("cachingPriceAdjustmentService")
	private CachingPriceAdjustmentServiceImpl cachingPriceAdjustmentService;

	private Product product;
	private CatalogTestPersister catalogPersister;
	private BundleConstituent bundleConstituent;

	/**
	 * Set up required for each test.
	 *
	 * @throws Exception if an exception occurs
	 */
	@Before
	public void setUp() throws Exception {
		TestDataPersisterFactory persistersFactory = getTac().getPersistersFactory();

		catalogPersister = persistersFactory.getCatalogTestPersister();

		product = generateProductWithInventory();
		PriceListDescriptor priceList = persistersFactory.getPriceListPersister().createAndPersistPriceList(
			PRICE_LIST_CODE, PRICE_LIST_NAME, DEFAULT_CURRENCY, DEFAULT_DESCRIPTION, true);

		ProductBundle bundle = generateProductBundle(priceList);
		bundleConstituent = bundle.getConstituents().get(0);
	}

	@Test
	@DirtiesDatabase
	public void testPriceAdjustmentReturnedFromDbMustBeDetached() {

		Map<String, PriceAdjustment> resultMap = cachingPriceAdjustmentService
			.findByPriceListAndBundleConstituentsAsMap(bundleConstituent.getPriceAdjustments().get(0).getPriceListGuid(),
				Arrays.asList(bundleConstituent.getGuid()));

		assertThat(resultMap).isNotEmpty();

		PriceAdjustment priceAdjustment = resultMap.values()
			.stream()
			.findFirst()
			.get();

		assertThat(((PersistenceCapable)priceAdjustment).pcIsDetached()).isTrue();
	}

	private ProductBundle generateProductBundle(final PriceListDescriptor priceList) {
		ProductBundle bundle = catalogPersister.createSimpleProductBundle("productType", "bundle", scenario.getCatalog(),
			scenario.getCategory(), getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS));

		addProductConstituentToBundle(bundle, product, priceList.getGuid());

		catalogPersister.persistSimpleProductSku("bundle_sku", 10.00, DEFAULT_CURRENCY, false, bundle, scenario.getWarehouse());
		return bundle;
	}

	private Product generateProductWithInventory() {
		Product product = generateProductWithoutInventory();
		catalogPersister.persistInventory(product.getDefaultSku().getSkuCode(), scenario.getWarehouse(), 10, 0, 0, "Add inventory to product.");
		return product;
	}

	private Product generateProductWithoutInventory() {
		return catalogPersister.persistProductWithSku(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse(),
			new BigDecimal(10.00), TestDataPersisterFactory.DEFAULT_CURRENCY, "brand", "productCode", "product", "skuCode",
			TaxTestPersister.TAX_CODE_GOODS,
			AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, 1);
	}

	private BundleConstituent addProductConstituentToBundle(final ProductBundle bundle, final Product constituent, final String plGuid) {

		BundleConstituent bundleConstituent = catalogPersister.createSimpleBundleConstituent(constituent, 1);

		PriceAdjustment pa = new PriceAdjustmentImpl();
		pa.setGuid("paGuid");
		pa.setAdjustmentAmount(BigDecimal.TEN);
		pa.setPriceListGuid(plGuid);

		bundleConstituent.addPriceAdjustment(pa);

		bundle.addConstituent(bundleConstituent);

		return bundleConstituent;
	}


}
