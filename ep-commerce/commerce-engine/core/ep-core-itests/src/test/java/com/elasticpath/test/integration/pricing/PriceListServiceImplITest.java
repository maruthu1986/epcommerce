/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration.pricing;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.common.pricing.service.impl.PriceListServiceImpl;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;

public class PriceListServiceImplITest extends DbTestCase {

	private static final String PRICE_LIST_CODE = "pl1";

	private static final String PRICE_LIST_NAME = PRICE_LIST_CODE;

	private static final String NON_HIDDEN_PLA_NAME = "pla";

	private static final String PLA_HIDDEN_NAME = "plaHidden";

	private static final String CATALOG_CODE = "catalog1";

	private static final String CATALOG_NAME = CATALOG_CODE;

	private static final String DEFAULT_CURRENCY = "CAD";

	private static final String DEFAULT_DESCRIPTION = "DESC";

	@Autowired
	private PriceListServiceImpl priceListService;

	@Autowired
	private BeanFactory beanFactory;

	private Catalog defaultCatalog;
	private PriceListAssignment priceListAssignment;
	private Category category;
	private CategoryType categoryType;
	private TaxCode taxCode;
	private Product product;
	private BaseAmount baseAmount;

	/**
	 * Set up required for each test.
	 *
	 * @throws Exception if an exception occurs
	 */
	@Before
	public void setUp() throws Exception {
		TestDataPersisterFactory persistersFactory = getTac().getPersistersFactory();
		List<String> currencies = new ArrayList<>();
		List<String> locales = new ArrayList<>();

		final CatalogTestPersister catalogTestPersister = persistersFactory.getCatalogTestPersister();
		defaultCatalog = catalogTestPersister.persistCatalog(
				CATALOG_CODE, CATALOG_NAME, true, DEFAULT_CURRENCY, "EN", currencies, locales, false);
		categoryType = catalogTestPersister.persistCategoryType("categoryType", defaultCatalog);
		category = catalogTestPersister.persistCategory("category", defaultCatalog, categoryType, "category", "EN");
		taxCode = persistersFactory.getTaxTestPersister().persistTaxCode("TAX");
		product = catalogTestPersister.persistSimpleProduct("product", "productType", defaultCatalog, category, taxCode);

		PriceListDescriptor priceList = persistersFactory.getPriceListPersister().createAndPersistPriceList(
				PRICE_LIST_CODE, PRICE_LIST_NAME, DEFAULT_CURRENCY, DEFAULT_DESCRIPTION, true);

		priceListAssignment = persistersFactory.getPriceListAssignmentPersister().
				createPriceListAssignment(defaultCatalog.getGuid(), priceList.getGuid(), PLA_HIDDEN_NAME, DEFAULT_DESCRIPTION, 0);
		persistersFactory.getPriceListPersister().addOrUpdateBaseAmount(
				PRICE_LIST_CODE, BaseAmountObjectType.PRODUCT.getName(), product.getGuid(),
				new BigDecimal(1), new BigDecimal(10.0), new BigDecimal(5.0));

	}

	@Test
	@DirtiesDatabase
	public void testGetBaseAmountsExtWithNonExactMatchForLowestPrice() {
		BaseAmountFilterExt filterExpr = beanFactory.getBean(ContextIdNames.BASE_AMOUNT_FILTER_EXT);
		filterExpr.setObjectGuid(product.getGuid());
		filterExpr.setObjectType(BaseAmountObjectType.PRODUCT.getName());
		filterExpr.setLocale(Locale.CANADA);
		filterExpr.setLowestPrice(new BigDecimal("10.00"));
		filterExpr.setLimit(100);

		Collection<BaseAmountDTO> baseAmounts = priceListService.getBaseAmountsExt(filterExpr);
		assertEquals("Should have found the base amount", 1, baseAmounts.size());
	}

	@Test
	@DirtiesDatabase
	public void testGetBaseAmountsExtWithNonExactMatchForLowestPriceDoesFilterOnPrice() {
		BaseAmountFilterExt filterExpr = beanFactory.getBean(ContextIdNames.BASE_AMOUNT_FILTER_EXT);
		filterExpr.setObjectGuid(product.getGuid());
		filterExpr.setObjectType(BaseAmountObjectType.PRODUCT.getName());
		filterExpr.setLocale(Locale.CANADA);
		filterExpr.setLowestPrice(new BigDecimal("10.01"));
		filterExpr.setLimit(100);

		Collection<BaseAmountDTO> baseAmounts = priceListService.getBaseAmountsExt(filterExpr);
		assertEquals("Should not have found any base amounts", 0, baseAmounts.size());
	}

	@Test
	@DirtiesDatabase
	public void smokeTestGetBaseAmountsExtQueriesWithAllValidCombinations() {
		boolean[] boolValues = { false, true };

		for (int isCaseSensitive = 0; isCaseSensitive < boolValues.length; isCaseSensitive ++) {
			for (int hasLowestPrice = 0; hasLowestPrice < boolValues.length; hasLowestPrice ++ ) {
				for (int hasHighestPrice = 0; hasHighestPrice < boolValues.length; hasHighestPrice ++) {
					BaseAmountFilterExt filterExpr = beanFactory.getBean(ContextIdNames.BASE_AMOUNT_FILTER_EXT);
					filterExpr.setObjectGuid(product.getGuid());
					filterExpr.setObjectType(BaseAmountObjectType.PRODUCT.getName());
					filterExpr.setLocale(Locale.CANADA);
					if (hasLowestPrice > 0) {
						filterExpr.setLowestPrice(new BigDecimal("1.00"));
					}
					if (hasHighestPrice > 0) {
						filterExpr.setHighestPrice(new BigDecimal("25.00"));
					}
					filterExpr.setLimit(100);

					Collection<BaseAmountDTO> baseAmounts = priceListService.getBaseAmountsExt(filterExpr, isCaseSensitive> 0);
					assertEquals("Should have found the base amount - exact "
							+ isCaseSensitive + " lowest " + hasLowestPrice + " highest " + hasHighestPrice,
							1, baseAmounts.size());
				}
			}
		}
	}
}
