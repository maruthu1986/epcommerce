/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.db;

import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.misc.impl.OpenJPAFetchPlanHelperImpl;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.util.Utils;

/**
 * This class mainly tests category's children category retrieval ability by specifying the recursionDepth in fetch group. May need to add more test
 * methods later.
 */
public class CategoryPersistenceTest extends DbTestCase {

	private CategoryType categoryType;

	private Catalog catalog;

	private final OpenJPAFetchPlanHelperImpl fetchPlanHelper = new OpenJPAFetchPlanHelperImpl();

	private Category category1;

	private Category category2;

	private Category category3;

	private Category category4;


	/**
	 * Sets up the test case.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		category1 = createCategory(getCatalog(), createCategoryType(getCatalog()));
		category2 = createCategory(getCatalog(), createCategoryType(getCatalog()));
		category3 = createCategory(getCatalog(), createCategoryType(getCatalog()));
		category4 = createCategory(getCatalog(), createCategoryType(getCatalog()));

		category2.setParent(category1);
		category3.setParent(category2);
		category4.setParent(category3);

		getTxTemplate().execute(new TransactionCallback<Category>() {
			@Override
			public Category doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().saveOrUpdate(category1);
				getPersistenceEngine().saveOrUpdate(category2);
				getPersistenceEngine().saveOrUpdate(category3);
				getPersistenceEngine().saveOrUpdate(category4);
				return null;
			}
		});

	}
	/**
	 * Tests category retrieving.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrieveCategory() {

		Category returnedCategory1 = getTxTemplate().execute(new TransactionCallback<Category>() {
			@Override
			public Category doInTransaction(final TransactionStatus arg0) {

				fetchPlanHelper.setPersistenceEngine(getPersistenceEngine());
				fetchPlanHelper.configureFetchGroupLoadTuner(getLoadTuner());

				return getPersistenceEngine().get(CategoryImpl.class, category1.getUidPk());
			}
		});

		assertNotNull("Category1 not retrieved - null", returnedCategory1);
	}

	private FetchGroupLoadTuner getLoadTuner() {
			FetchGroupLoadTuner loadTuner = new FetchGroupLoadTunerImpl();
			loadTuner.addFetchGroup(FetchGroupConstants.CATEGORY_BASIC,
					FetchGroupConstants.CATALOG_DEFAULTS // need default locale
			);
		return loadTuner;
	}

	/**
	 * Callback class for transactional code.
	 */
	public class TransactionCallbackForMerge implements TransactionCallback<Persistable> {

		private Persistable model;

		/**
		 * The Constructor.
		 *
		 * @param model object to persist.
		 */
		public TransactionCallbackForMerge(final Persistable model) {
			this.model = model;
		}

		@Override
		public Persistable doInTransaction(final TransactionStatus arg0) {
			this.model = getPersistenceEngine().saveOrUpdate(this.model);
			return this.model;

		}
	}

	/**
	 * Returns current catalog or creates new one.
	 *
	 * @return catalog.
	 */
	public Catalog getCatalog() {
		if (this.catalog == null) {
			catalog = createPersistedCatalog();
		}
		return this.catalog;
	}

	/**
	 * Returns current catalog or creates new one.
	 *
	 * @return category type.
	 */
	public CategoryType getCategoryType() {
		if (categoryType == null) {
			categoryType = createCategoryType(catalog);
		}
		return this.categoryType;
	}

	@Override
	protected Catalog createPersistedCatalog() {
		final Catalog catalog = new CatalogImpl();
		catalog.setCode(Utils.uniqueCode("catalog"));
		catalog.setDefaultLocale(Locale.getDefault());
		catalog.setName(catalog.getCode());

		getTxTemplate().execute(new TransactionCallback<Catalog>() {
			@Override
			public Catalog doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(catalog);
				return catalog;
			}
		});

		return catalog;
	}

}
