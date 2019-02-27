/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;

/**
 * Test case for {@link AbstractCategoryImpl}.
 */
public class AbstractCategoryImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private AbstractCategoryImpl abstractCategoryImpl;
	
	private Category parentImpl;
	
	private Catalog masterCatalog;

	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		abstractCategoryImpl = createNewAbstractCategoryImpl();
		parentImpl = getCategory();
		parentImpl.setUidPk(1L);
	}

	@SuppressWarnings({ "PMD.ExcessiveMethodLength" })
	private AbstractCategoryImpl createNewAbstractCategoryImpl() {
		return new AbstractCategoryImpl() {
			private static final long serialVersionUID = -2685396194326764453L;

			@Override
			public AttributeValueGroup getAttributeValueGroup() {
				return null;
			}
			@Override
			public Map<String, AttributeValue> getAttributeValueMap() {
				return null;
			}
			@Override
			public CategoryType getCategoryType() {
				return null;
			}
			@Override
			public String getCode() {
				return null;
			}
			@Override
			public Date getEndDate() {
				return null;
			}
			@Override
			public Category getMasterCategory() {
				return null;
			}
			@Override
			public Date getStartDate() {
				return null;
			}
			@Override
			public Set<TopSeller> getTopSellers() {
				return null;
			}
			@Override
			public boolean isAvailable() {
				return false;
			}
			@Override
			public boolean isHidden() {
				return false;
			}
			@Override
			public boolean isIncluded() {
				return false;
			}
			@Override
			public boolean isLinked() {
				return false;
			}
			@Override
			public boolean isVirtual() {
				return false;
			}
			@Override
			public void setAttributeValueGroup(final AttributeValueGroup attributeValueGroup) {
				// stub method
			}
			@Override
			public void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
				// stub method
			}
			@Override
			public void setCategoryType(final CategoryType categoryType) {
				// stub method
			}
			@Override
			public void setCode(final String code) {
				// stub method
			}
			@Override
			public void setEndDate(final Date endDate) {
				// stub method
			}
			@Override
			public void setHidden(final boolean hidden) {
				// stub method
			}
			@Override
			public void setIncluded(final boolean include) {
				// stub method
			}
			@Override
			public void setMasterCategory(final Category masterCategory) {
				// stub method
			}
			@Override
			public void setStartDate(final Date startDate) {
				// stub method
			}
			@Override
			public void setTopSellers(final Set<TopSeller> topSellers) {
				// stub method
			}
			@Override
			public void setVirtual(final boolean virtual) {
				// stub method
			}
			@Override
			public void addOrUpdateLocaleDependantFields(final LocaleDependantFields ldf) {
				// stub method
			}
			@Override
			public String getDisplayName(final Locale locale) {
				return null;
			}
			@Override
			public LocaleDependantFields getLocaleDependantFields(final Locale locale) {
				return null;
			}
			@Override
			public LocaleDependantFields getLocaleDependantFieldsWithoutFallBack(final Locale locale) {
				return null;
			}
			@Override
			public void setDisplayName(final String name, final Locale locale) {
				// stub method
			}
			@Override
			public LocaleDependantFields getLocaleDependantFields(final LocaleFallbackPolicy policy) {
				return null;
			}
			@Override
			public void setLocaleDependantFieldsMap(final Map<Locale, LocaleDependantFields> localeDependantFieldsMap) {
				// stub method
			}
		};
	}
	
	/**
	 * Test method for {@link AbstractCategoryImpl#setParent(Category)}.
	 */
	@Test
	public void testSetParent() {
		abstractCategoryImpl.setCatalog(getCatalog());
		abstractCategoryImpl.setParent(parentImpl);
		assertSame(parentImpl.getGuid(), abstractCategoryImpl.getParentGuid());
	}

	/**
	 * Test method for {@link AbstractCategoryImpl#setParent(Category)}.
	 */
	@Test
	public void testSetParentWithNull() {
		abstractCategoryImpl.setParent(null);
		assertNull(abstractCategoryImpl.getParentGuid());
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
			masterCatalog.setCode("Irrelevant catalog code");
		}
		
		return masterCatalog;
	}
}
