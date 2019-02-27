/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeGroupAttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeValueGroupFactoryImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueFactoryTestImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueGroupFactoryTestImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueGroupTestImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueTestImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>CategoryImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports", "PMD.GodClass" })
public class CategoryImplTest  {

	private static final int ORDERING_NUMBER_2 = 4;

	private static final int ORDERING_NUMBER_1 = 3;

	private static final String TEST_CATEGORY_GUID_1 = "test category 1";

	private static final String TEST_CATEGORY_GUID_2 = "test category 2";

	private static final String TEST_CATEGORY_GUID = "test category";

	private Category categoryImpl;

	private Category childImpl;

	private CategoryType categoryType;

	/**
	 * Master catalog code.
	 */
	protected static final String TEST_MASTER_CATALOG_CODE = "a master catalog code that no one would ever think of";

	private Catalog masterCatalog;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepare for tests.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CATALOG_LOCALE, CatalogLocaleImpl.class);

		setupCategoryType();

		setupCategory();

		setupChildCategory();
	}

	/**
	 * Test class for the localeFallbackPolicy.
	 *
	 */
	private class PredefinedLocaleFallbackPolicy implements LocaleFallbackPolicy {
		@Override
		public void addLocale(final Locale locale) {
			// not needed
		}
		@Override
		public List<Locale> getLocales() {
			return Arrays.asList(new Locale("en"), new Locale("fr"));
		}

		@Override
		public Locale getPrimaryLocale() {
			return new Locale("en");
		}
		@Override
		public void setPreferredLocales(final Locale... locales) {
			//not needed
		}
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private void setupChildCategory() {
		this.childImpl = getCategory();
	}

	private void setupCategory() {
		this.categoryImpl = getCategory();
	}

	private void setupCategoryType() {
		this.categoryType = new CategoryTypeImpl();
		this.categoryType.setAttributeGroup(new AttributeGroupImpl());

		final AttributeGroupAttribute caAttr1 = new AttributeGroupAttributeImpl();
		final Attribute attr1 = new AttributeImpl();
		attr1.setAttributeType(AttributeType.INTEGER);
		caAttr1.setAttribute(attr1);
		categoryType.getAttributeGroup().addAttributeGroupAttribute(caAttr1);

		final AttributeGroupAttribute caAttr2 = new AttributeGroupAttributeImpl();
		final Attribute attr2 = new AttributeImpl();
		attr2.setAttributeType(AttributeType.SHORT_TEXT);
		caAttr2.setAttribute(attr2);
		categoryType.getAttributeGroup().addAttributeGroupAttribute(caAttr2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getStartDate()'.
	 */
	@Test
	public void testGetStartDate() {
		assertNotNull(categoryImpl.getStartDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.setStartDate(Date)'.
	 */
	@Test
	public void testSetStartDate() {
		final Date date = new Date();
		categoryImpl.setStartDate(date);
		assertSame(date, categoryImpl.getStartDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getEndDate()'.
	 */
	@Test
	public void testGetEndDate() {
		assertNull(categoryImpl.getEndDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.setEndDate(Date)'.
	 */
	@Test
	public void testSetEndDate() {
		final Date date = new Date();
		categoryImpl.setEndDate(date);
		assertSame(date, categoryImpl.getEndDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getOrdering()'.
	 */
	@Test
	public void testGetOrdering() {
		assertEquals(0, categoryImpl.getOrdering());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.setOrdering(int)'.
	 */
	@Test
	public void testSetOrdering() {
		final int ordering = 999;
		categoryImpl.setOrdering(ordering);
		assertEquals(ordering, categoryImpl.getOrdering());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.compareTo()'.
	 */
	@Test
	public void testCompareToAndEquals() {
		// New categories are different
		assertTrue(0 != categoryImpl.compareTo(childImpl)); // NOPMD
		assertFalse(categoryImpl.equals(childImpl));
		assertFalse(categoryImpl.hashCode() == childImpl.hashCode()); // NOPMD

		// compare by ordering number
		categoryImpl.setOrdering(ORDERING_NUMBER_1);
		childImpl.setOrdering(ORDERING_NUMBER_2);
		assertTrue(categoryImpl.compareTo(childImpl) < 0);
		assertFalse(categoryImpl.equals(childImpl));
		assertFalse(categoryImpl.hashCode() == childImpl.hashCode()); // NOPMD

		// compare by guid
		categoryImpl.setOrdering(ORDERING_NUMBER_1);
		childImpl.setOrdering(ORDERING_NUMBER_1);
		categoryImpl.setGuid(TEST_CATEGORY_GUID_1);
		childImpl.setGuid(TEST_CATEGORY_GUID_2);
		assertTrue(categoryImpl.compareTo(childImpl) < 0);
		assertFalse(categoryImpl.equals(childImpl));
		assertFalse(categoryImpl.hashCode() == childImpl.hashCode()); // NOPMD

		// compare the same one
		categoryImpl.setOrdering(ORDERING_NUMBER_1);
		childImpl.setOrdering(ORDERING_NUMBER_1);
		categoryImpl.setGuid(TEST_CATEGORY_GUID);
		childImpl.setGuid(TEST_CATEGORY_GUID);
		assertEquals(0, categoryImpl.compareTo(childImpl));
		assertTrue(categoryImpl.equals(childImpl)); // NOPMD
		assertTrue(categoryImpl.hashCode() == childImpl.hashCode()); // NOPMD
	}

	/**
	 * Test for isAvailable().
	 */
	@Test
	public void testIsAvailable() {
		final long timeUnit = 20000;
		Date beforeNow = new Date();
		beforeNow.setTime(beforeNow.getTime() - timeUnit);

		Date muchBeforeNow = new Date();
		beforeNow.setTime(beforeNow.getTime() - timeUnit * 2);

		Date afterNow = new Date();
		afterNow.setTime(afterNow.getTime() + timeUnit);

		categoryImpl.setStartDate(beforeNow);
		assertTrue(categoryImpl.isAvailable());

		categoryImpl.setStartDate(afterNow);
		assertFalse(categoryImpl.isAvailable());

		categoryImpl.setStartDate(beforeNow);
		categoryImpl.setEndDate(afterNow);
		assertTrue(categoryImpl.isAvailable());

		categoryImpl.setStartDate(muchBeforeNow);
		categoryImpl.setEndDate(beforeNow);
		assertFalse(categoryImpl.isAvailable());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.setDefaultValues()'.
	 */
	@Test
	public void testInitialize() {

		final Date oldDate = categoryImpl.getStartDate();
		final String oldGuid = categoryImpl.getGuid();

		// set default values again, no value should be changed.
		categoryImpl.initialize();
		assertSame(oldGuid, categoryImpl.getGuid());
		assertSame(oldDate, categoryImpl.getStartDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getFullAttributeValues()'.
	 */
	@Test
	public void testGetFullAttributeValues() {
		this.categoryImpl.setCategoryType(this.categoryType);
		assertSame(this.categoryType, this.categoryImpl.getCategoryType());
	}

	/**
	 * Test falling back to empty ldf.
	 */
	@Test
	public void testGetLocaleDependantFieldsFallbackToEmptyLDF() {
		final LocaleFallbackPolicy policy = new PredefinedLocaleFallbackPolicy();

		categoryImpl.setLocaleDependantFieldsMap(new HashMap<>());
		LocaleDependantFields result;

		result = categoryImpl.getLocaleDependantFields(policy);
		assertEquals("If no LDF has been set, should get an empty LDF in the primary locale",
				new Locale("en"), result.getLocale());
	}

	/**
	 * Test matching correct locales in policy.
	 */
	@Test
	public void testMatchCorrectLocaleInLocaleFallbackPolicy() {
		final LocaleFallbackPolicy policy = new PredefinedLocaleFallbackPolicy();
		final Map<Locale, LocaleDependantFields> ldfMap = new HashMap<>();

		categoryImpl.setLocaleDependantFieldsMap(ldfMap);
		LocaleDependantFields frLdf = context.mock(LocaleDependantFields.class, "fr");
		LocaleDependantFields enLdf = context.mock(LocaleDependantFields.class, "en");

		//put only the secondary ldf into the map
		ldfMap.put(new Locale("fr"), frLdf);

		LocaleDependantFields result = categoryImpl.getLocaleDependantFields(policy);
		assertSame("LDF should match second locale in policy", frLdf, result);

		//put the primary ldf into the map
		ldfMap.put(new Locale("en"), enLdf);

		result = categoryImpl.getLocaleDependantFields(policy);
		assertSame("LDF should match first locale in policy", enLdf, result);

	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetLastModifiedDate() {
		assertNull(categoryImpl.getLastModifiedDate());
	}

	/**
	 * Test method for {@link CategoryImpl#equals(Object)}.
	 */
	@Test
	public void testEquals() {
		final CategoryImpl first = new CategoryImpl();
		final CategoryImpl second = new CategoryImpl();
		final CategoryImpl third = new CategoryImpl();
		final String guid = "some code";

		assertEquals("Should reflexive", first, first);
		assertEquals("New objects should be equal", first, second);
		assertEquals("Should be symmetric", second, first);

		first.setGuid(guid);
		second.setGuid(guid);
		assertEquals("Equal codes should be equal", first, second);
		assertEquals("Equal codes should be symmetric", second, first);

		third.setGuid(guid);
		assertEquals("Equal codes should be equal", second, third);
		assertEquals("Should be transitive", first, third);
	}

	/**
	 * Test method for {@link CategoryImpl#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		final CategoryImpl first = new CategoryImpl();
		final CategoryImpl second = new CategoryImpl();
		final String guid = "some code";

		assertEquals("hash code should be same on different calls", first.hashCode(), first.hashCode());

		first.setGuid(guid);
		second.setGuid(guid);
		assertEquals("Same code should hash to same value", first.hashCode(), second.hashCode());

		first.setUidPk(1L);
		second.setUidPk(1L);

		if (first.equals(second)) {
			assertEquals("Equal objects should have same hash code", first.hashCode(), second.hashCode());
		}

		first.setGuid(null);
		second.setGuid(null);
		assertEquals("No codes with same UIDs should be the same", first.hashCode(), second.hashCode());
	}

	/**
	 * Test that extension classes can override the AttributeValueGroup and ProductAttributeValue implementation classes.
	 */
	@Test
	public void testThatExtensionClassesCanOverrideAttributeValueImplementations() {
		AttributeImpl attribute = new AttributeImpl();
		attribute.setAttributeType(AttributeType.SHORT_TEXT);
		attribute.setName("name");
		attribute.setKey("name");

		ExtCategoryImpl category = new ExtCategoryImpl();
		category.getAttributeValueGroup().setStringAttributeValue(attribute, null, "beanie-weenie");

		assertEquals("AttributeValueGroup implementation class should have been overridden",
				ExtAttributeValueGroupTestImpl.class, category.getAttributeValueGroup().getClass());
		assertEquals("AttributeValueImpl implementation class should have been overridden",
				ExtAttributeValueTestImpl.class,
				category.getAttributeValueGroup().getAttributeValue("name", null).getClass());
	}

	/**
	 * Tests that sub-classes can override the LocaleDependantFields implementation class.
	 */
	@Test
	public void testExtensionClassesCanOverrideLocaleDependantFieldsFactory() {
		final CatalogLocaleFallbackPolicyFactory factory = new CatalogLocaleFallbackPolicyFactory();
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY, factory);
		ExtCategoryImpl category = new ExtCategoryImpl();
		LocaleDependantFields ldf = category.getLocaleDependantFieldsWithoutFallBack(Locale.ENGLISH);

		assertTrue("Extension classes should be able to override LocaleDependantFields impl",
				ldf instanceof ExtCategoryLocaleDependantFieldsImpl);
	}

	/**
	 * Faux category domain extension class.
	 */
	private static class ExtCategoryImpl extends CategoryImpl {
		private static final long serialVersionUID = 4347049078506080278L;

		/**
		 * Factory factory method override.
		 * @return the factory
		 */
		@Override
		protected AttributeValueGroupFactoryImpl getAttributeValueGroupFactory() {
			return new ExtAttributeValueGroupFactoryTestImpl(new ExtAttributeValueFactoryTestImpl());
		}

		/**
		 * Creates an appropriate LocaleDependantFields value object for the given locale.  Override this method
		 * if you need to change the implementation class in an extension project.
		 *
		 * @param locale the Locale this LDF applies to
		 * @return a LocaleDependentFields object
		 */
		@Override
		protected LocaleDependantFields createLocaleDependantFields(final Locale locale) {
			final LocaleDependantFields ldf = new ExtCategoryLocaleDependantFieldsImpl();
			ldf.setLocale(locale);

			return ldf;
		}

		@Override
		public Utility getUtility() {
			return new UtilityImpl();
		}
	}

	/**
	 * Faux categoryLDF extension class.
	 */
	private static class ExtCategoryLocaleDependantFieldsImpl extends CategoryLocaleDependantFieldsImpl {
		private static final long serialVersionUID = -2035724548443157583L;
	}

	/**
	 * Returns a new <code>Category</code> instance.
	 *
	 * @return a new <code>Category</code> instance.
	 */
	private Category getCategory() {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setCode(new RandomGuidImpl().toString());
		category.setCatalog(getCatalog());

		return category;
	}

	/**
	 * Gets the master catalog singleton.
	 *
	 * @return the master catalog singleton
	 */
	private Catalog getCatalog() {
		if (masterCatalog == null) {
			masterCatalog = new CatalogImpl();
			masterCatalog.setMaster(true);
			masterCatalog.setCode(TEST_MASTER_CATALOG_CODE);
		}
		return masterCatalog;
	}

}
