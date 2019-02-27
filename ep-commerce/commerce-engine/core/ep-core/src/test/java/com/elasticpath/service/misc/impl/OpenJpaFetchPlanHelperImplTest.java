/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.FetchPlan;
import org.apache.openjpa.persistence.FetchPlanImpl;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.jdbc.JDBCFetchPlan;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.ProductTypeLoadTuner;

/**
 * Test cases for <code>OpenJPAFetchPlanHelperImpl</code>.
 * As a byproduct of testing whether load tuner methods are being called,
 * if the TestFetchPlan class is used correctly then all the fields being
 * added to the fetch plans will be verified to ensure that they actually
 * exist on the classes and that they are non-transient.
 */
@RunWith(MockitoJUnitRunner.class)
public class OpenJpaFetchPlanHelperImplTest {

	private OpenJPAFetchPlanHelperImpl fetchPlanHelperImpl;

	/**
	 * Set up mock objects required for the tests.
	 *
	 * @throws Exception -- in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		//Override so that we can check whether the persistence fields we specify adding
		//to fetch groups actually exist, because currently OpenJPA will happily allow you
		//to add fields to a fetch plan for an object even when those fields don't exist
		//on the object. If you happen to mistake the name of the getter on the object,
		//you won't know it unless you happen to have an integration test that fails somewhere.
		fetchPlanHelperImpl = new OpenJPAFetchPlanHelperImpl() {

			private final FetchPlan fetchPlan = new TestFetchPlan();

			@Override
			protected FetchPlan getFetchPlan() {
				return this.fetchPlan;

			}
		};
	}

	/**
	 * Test that clearing the fetch plan clears both fields and fetch groups.
	 */
	@Test
	public void testClearFetchPlan() {
		final OpenJPAEntityManager mockEntityManager = mock(OpenJPAEntityManager.class);
		final JDBCFetchPlan mockFetchPlan = mock(JDBCFetchPlan.class);
		final FetchPlan fetchPlan = mockFetchPlan;

		OpenJPAFetchPlanHelperImpl helper = new OpenJPAFetchPlanHelperImpl() {
			@Override
			protected OpenJPAEntityManager getOpenJPAEntityManager() {
				return mockEntityManager;
			}

		};

		when(mockEntityManager.getFetchPlan()).thenReturn(fetchPlan);
		assertEquals("The returned fetch plan should be the expected one", fetchPlan, helper.getFetchPlan());

		helper.clearFetchPlan();
		verify(mockFetchPlan).clearFields();
		verify(mockFetchPlan).resetFetchGroups();
	}

	/**
	 * Test that <code>configureCategoryFetchPlan()</code> inspects all flags of the load tuner.
	 */
	@Test
	public void testConfigureCategoryFetchPlanUsesLoadTuner() {
		final CategoryLoadTuner mockCategoryLoadTuner = mock(CategoryLoadTuner.class);

		fetchPlanHelperImpl.configureCategoryFetchPlan(mockCategoryLoadTuner);
		verify(mockCategoryLoadTuner).isLoadingMaster();
		verify(mockCategoryLoadTuner).isLoadingCategoryType();
		verify(mockCategoryLoadTuner).isLoadingAttributeValue();
		verify(mockCategoryLoadTuner).isLoadingLocaleDependantFields();
		verify(mockCategoryLoadTuner).getCategoryTypeLoadTuner();
	}

	/**
	 * Test that <code>configureCategoryFetchPlan()</code> adds fields to the fetch plan.
	 */
	@Test
	public void testConfigureCategoryFetchPlanAddsFields() {
		final CategoryLoadTuner mockCategoryLoadTuner = mock(CategoryLoadTuner.class);

		when(mockCategoryLoadTuner.isLoadingMaster()).thenReturn(true);
		when(mockCategoryLoadTuner.isLoadingCategoryType()).thenReturn(true);
		when(mockCategoryLoadTuner.isLoadingAttributeValue()).thenReturn(true);
		when(mockCategoryLoadTuner.isLoadingLocaleDependantFields()).thenReturn(true);

		fetchPlanHelperImpl.configureCategoryFetchPlan(mockCategoryLoadTuner);
	}

	/**
	 * Test that <code>configureProductAssociationFetchPlan()</code> inspects all flags of the load tuner.
	 */
	@Test
	public void testConfigureProductAssociationUsesLoadTuner() {
		final ProductAssociationLoadTuner mockProductAssociationLoadTuner = mock(ProductAssociationLoadTuner.class);

		fetchPlanHelperImpl.configureProductAssociationFetchPlan(mockProductAssociationLoadTuner);
		verify(mockProductAssociationLoadTuner).isLoadingCatalog();
		verify(mockProductAssociationLoadTuner).getProductLoadTuner();
	}

	/**
	 * Test that the ProductSku load tuner is queried for all fields.
	 */
	@Test
	public void testConfigureProductSkuFetchPlanUsesLoadTuner() {
		final ProductSkuLoadTuner mockProductSkuLoadTuner = mock(ProductSkuLoadTuner.class);

		fetchPlanHelperImpl.configureProductSkuFetchPlan(mockProductSkuLoadTuner);
		verify(mockProductSkuLoadTuner).isLoadingAttributeValue();
		verify(mockProductSkuLoadTuner).isLoadingOptionValue();
		verify(mockProductSkuLoadTuner).isLoadingProduct();
		verify(mockProductSkuLoadTuner).isLoadingDigitalAsset();
	}

	/**
	 * Test that the ProductType load tuner is queried for all fields.
	 */
	@Test
	public void testConfigureProductTypeFetchPlanUsesLoadTuner() {
		final ProductTypeLoadTuner mockProductTypeLoadTuner = mock(ProductTypeLoadTuner.class);

		fetchPlanHelperImpl.configureProductTypeFetchPlan(mockProductTypeLoadTuner);
		verify(mockProductTypeLoadTuner).isLoadingAttributes();
		verify(mockProductTypeLoadTuner).isLoadingSkuOptions();
		verify(mockProductTypeLoadTuner).isLoadingCartItemModifierGroups();
	}

	/**
	 * Test that the Product load tuner is queried for all fields.
	 */
	@Test
	public void testConfigureProductFetchPlanUsesLoadTuner() {
		final ProductLoadTuner mockProductLoadTuner = mock(ProductLoadTuner.class);

		when(mockProductLoadTuner.isLoadingCategories()).thenReturn(true);
		when(mockProductLoadTuner.isLoadingSkus()).thenReturn(true);
		when(mockProductLoadTuner.isLoadingProductType()).thenReturn(true);

		fetchPlanHelperImpl.configureProductFetchPlan(mockProductLoadTuner);
		verify(mockProductLoadTuner).isLoadingAttributeValue();
		verify(mockProductLoadTuner).isLoadingCategories();
		verify(mockProductLoadTuner).isLoadingDefaultSku();
		verify(mockProductLoadTuner).isLoadingProductType();
		verify(mockProductLoadTuner).isLoadingSkus();
		verify(mockProductLoadTuner).getCategoryLoadTuner();
		verify(mockProductLoadTuner).getProductSkuLoadTuner();
		verify(mockProductLoadTuner).getProductTypeLoadTuner();
	}

	/**
	 * Test class that checks the fields we add to a fetch plan actually exist,
	 * and that they're not transient.
	 */
	public class TestFetchPlan extends FetchPlanImpl {

		/**
		 * Constructor that assumes we don't need a real FetchPlan, so passes in null to its constructor.
		 */
		public TestFetchPlan() {
			super(null);
		}
		/**
		 * Doesn't actually add a field to the fetch plan, but instead checks that the field exists
		 * on the given class. Also checks that the field specified is not marked as Transient.
		 * @param cls the class to which the field would be added
		 * @param field the name of the persistent field on the given class
		 * @return the fetch plan
		 */
		@Override
		@SuppressWarnings({"unchecked", "PMD.AvoidThrowingRawExceptionTypes", "rawtypes"})
		public FetchPlan addField(final Class cls, final String field) {
			Class<? extends Annotation> transientAnnotation = null;
			try {
				transientAnnotation = (Class<? extends Annotation>) Class.forName("javax.persistence.Transient");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Exception running test - class javax.persistence.Transient not found in classpath", e);
			}

			try {
				Method method = cls.getDeclaredMethod("get" + StringUtils.capitalize(field));
				if (method.getAnnotation(transientAnnotation) != null) {
					fail("Persistence field " + field + " in class " + cls.getName() + " is declared Transient!");
				}
			} catch (NoSuchMethodException snme) {
				fail("Persistence field " + field + " not defined for class " + cls.getName());
			}
			return this;
		}

		/**
		 * Does nothing.
		 * @param fetchGroupName the name of the fetch group to add to the fetch plan
		 * @return the fetch plan
		 */
		@Override
		public FetchPlan addFetchGroup(final String fetchGroupName) {
			return this; //ignore calls like this, can't test them easily.
		}

		@Override
		public FetchPlan setMaxFetchDepth(final int depth) {
			return this;
		}
	}
}
