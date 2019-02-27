/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.AbstractCategoryImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class CategoryLookupImplTest {
	public static final long CATEGORY_UID = 1234L;
	private static final String CATEGORY_CODE = "category-1234";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock private PersistenceEngine persistenceEngine;
	@Mock private FetchPlanHelper fetchPlanHelper;
	@Mock private BeanFactory beanFactory;
	private CategoryLookupImpl categoryLookup;
	private Catalog catalog, virtualCatalog;
	private Category category;
	private CategoryImpl childCategory;
	private LinkedCategoryImpl linkedCategory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	@Before
	public void setUp() {
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER, FetchGroupLoadTunerImpl.class);

		catalog = new CatalogImpl();
		catalog.setCode("catalog");

		virtualCatalog = new CatalogImpl();
		virtualCatalog.setCode("virtualCatalog");

		category = new CategoryImpl();
		category.initialize();
		category.setUidPk(CATEGORY_UID);
		category.setCode(CATEGORY_CODE);
		category.setCatalog(catalog);

		childCategory = new CategoryImpl();
		childCategory.initialize();
		childCategory.setUidPk(CATEGORY_UID + 1);
		childCategory.setCode(CATEGORY_CODE + "-child");
		childCategory.setCatalog(catalog);
		childCategory.setParent(category);

		linkedCategory = new LinkedCategoryImpl();
		linkedCategory.initialize();
		linkedCategory.setUidPk(CATEGORY_UID + 2);
		linkedCategory.setCatalog(virtualCatalog);
		linkedCategory.setMasterCategory(category);

		categoryLookup = new CategoryLookupImpl();
		categoryLookup.setBeanFactory(beanFactory);
		categoryLookup.setFetchPlanHelper(fetchPlanHelper);
		categoryLookup.setPersistenceEngine(persistenceEngine);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	@Test
	public void testFindByUidWhenUidIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));
				oneOf(persistenceEngine).get(AbstractCategoryImpl.class, CATEGORY_UID); will(returnValue(category));
				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});
		assertSame(category, categoryLookup.findByUid(CATEGORY_UID));
	}

	@Test
	public void testFindByUidWhenUidIsNotFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));
				oneOf(persistenceEngine).get(AbstractCategoryImpl.class, CATEGORY_UID); will(returnValue(null));
				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});
		assertNull(categoryLookup.findByUid(CATEGORY_UID));
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.findByUids(categoryUids)'.
	 */
	@Test
	public void testFindByUids() {
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQueryWithList(
						"CATEGORY_BY_UIDS", "list", Collections.singletonList(CATEGORY_UID));
				will(returnValue(Collections.singletonList(category)));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		// Should return an empty list if no product UID is given.
		final List<Category> result = categoryLookup.findByUids(Collections.singletonList(CATEGORY_UID));
		assertEquals("Reader should delegate to persistence Engine", Collections.singletonList(category), result);
	}

	@Test
	public void testFindByCodeAndCatalogWhenMasterCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, catalog.getCode());
				will(returnValue(Collections.singletonList(category)));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertSame(category, categoryLookup.findByCategoryCodeAndCatalog(CATEGORY_CODE, catalog));
	}

	@Test
	public void testFindByCodeAndCatalogWhenLinkedCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, virtualCatalog.getCode());
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.LINKED_CATEGORY_FIND_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, virtualCatalog.getCode());
				will(returnValue(Collections.singletonList(linkedCategory)));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertSame(linkedCategory, categoryLookup.findByCategoryCodeAndCatalog(CATEGORY_CODE, virtualCatalog));
	}

	@Test
	public void testFindByCodeAndCatalogWhenNeitherAMasterNorLinkedCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, catalog.getCode());
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.LINKED_CATEGORY_FIND_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, catalog.getCode());
				will(returnValue(Collections.emptyList()));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});
		assertNull(categoryLookup.findByCategoryCodeAndCatalog(CATEGORY_CODE, catalog));
	}

	@Test
	public void testFindByGuidWhenCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_GUID, category.getGuid());
				will(returnValue(Collections.singletonList(category)));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertSame(category, categoryLookup.findByGuid(category.getGuid()));
	}

	@Test
	public void testFindByGuidWhenCategoryIsNotFoundReturnsNull() {
		context.checking(new Expectations() {
			{
				allowing(fetchPlanHelper);

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_GUID, category.getGuid());
				will(returnValue(Collections.emptyList()));
			}
		});

		assertNull(categoryLookup.findByGuid(category.getGuid()));
	}

	@Test
	public void testFindByCodeAndCatalogCodeWhenMasterCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, catalog.getCode());
				will(returnValue(Collections.singletonList(category)));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertSame(category, categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, catalog.getCode()));
	}

	@Test
	public void testFindByCodeAndCatalogCodeWhenLinkedCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, virtualCatalog.getCode());
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.LINKED_CATEGORY_FIND_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, virtualCatalog.getCode());
				will(returnValue(Collections.singletonList(linkedCategory)));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertSame(linkedCategory, categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, virtualCatalog.getCode()));
	}

	@Test
	public void testFindByCodeAndCatalogCodeWhenNeitherAMasterNorLinkedCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, catalog.getCode());
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.LINKED_CATEGORY_FIND_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, catalog.getCode());
				will(returnValue(Collections.emptyList()));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});
		assertNull(categoryLookup.findByCategoryAndCatalogCode(CATEGORY_CODE, catalog.getCode()));
	}

	@Test
	public void testFindByCompoundCodeWhenMasterCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, catalog.getCode());
				will(returnValue(Collections.singletonList(category)));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertSame(category, categoryLookup.findByCompoundCategoryAndCatalogCodes(CATEGORY_CODE + "|" + catalog.getCode()));
	}

	@Test
	public void testFindByCompoundCodeWhenLinkedCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, virtualCatalog.getCode());
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.LINKED_CATEGORY_FIND_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, virtualCatalog.getCode());
				will(returnValue(Collections.singletonList(linkedCategory)));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		assertSame(linkedCategory, categoryLookup.findByCompoundCategoryAndCatalogCodes(CATEGORY_CODE + "|" + virtualCatalog.getCode()));
	}

	@Test
	public void testFindByCompoundCodeWhenNeitherAMasterNorLinkedCategoryIsFound() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.CATEGORY_SELECT_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, catalog.getCode());
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.LINKED_CATEGORY_FIND_BY_CODE_AND_CATALOG_CODE, CATEGORY_CODE, catalog.getCode());
				will(returnValue(Collections.emptyList()));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});
		assertNull(categoryLookup.findByCompoundCategoryAndCatalogCodes(CATEGORY_CODE + "|" + catalog.getCode()));
	}

	@Test
	public void testFindChildrenByParentQueriesDatabaseForResults() {
		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(with(any(FetchGroupLoadTuner.class)));

				allowing(persistenceEngine).retrieveByNamedQuery(
						CategoryLookupImpl.SUBCATEGORY_SELECT_BY_PARENT_GUID, category.getGuid());
				will(returnValue(Collections.singletonList(childCategory)));

				oneOf(fetchPlanHelper).clearFetchPlan();
			}
		});

		List<CategoryImpl> children = categoryLookup.findChildren(category);
		assertEquals("Children should be retrieved from db",
				Collections.singletonList(childCategory), children);
	}

	@Test
	public void testFindParentByChild() {
		context.checking(new Expectations() {
			{
				allowing(fetchPlanHelper);
				oneOf(persistenceEngine).retrieveByNamedQuery(
						with(any(String.class)), with(equal(new Object[] {category.getGuid()})));
				will(returnValue(Collections.singletonList(category)));
			}
		});

		Category parent = categoryLookup.findParent(childCategory);
		assertEquals("Parent should be retrieved from db", category, parent);
	}

	@Test
	public void testFindParentByChildWhenParentIsNull() {
		Category found = categoryLookup.findParent(category);
		assertNull("Category is root, therefore no parent should be retrieved from db", found);
	}
}
