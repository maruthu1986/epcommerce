/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.service.catalog.CategoryLookup;

/**
 * The unit test class for category change set dependency resolver.
 */
public class CategoryChangeSetDependencyResolverImplTest {
	
	private CategoryChangeSetDependencyResolverImpl resolver;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final CategoryLookup categoryLookup = context.mock(CategoryLookup.class);

	@Before
	public void setUp() {
		resolver = new CategoryChangeSetDependencyResolverImpl();
		resolver.setCategoryLookup(categoryLookup);
	}
	
	/**
	 * Test getting change set dependency for category.
	 */
	@Test
	public void testGetChangeSetDependency() {
		Object obj = new Object();
		Set<?>  dependencies = resolver.getChangeSetDependency(obj);
		assertTrue("Non-Category object should not be processed", dependencies.isEmpty());
		
		final Category parentCategory = new CategoryImpl();
		final Category category = new CategoryImpl();
		context.checking(new Expectations() { {
			allowing(categoryLookup).findParent(category); will(returnValue(parentCategory));
			allowing(categoryLookup).findParent(parentCategory); will(returnValue(null));
		} });

		dependencies = resolver.getChangeSetDependency(category);
		assertEquals("parent category is not found in dependency list", parentCategory, dependencies.iterator().next());
		
		final Category masterCategory = new CategoryImpl();
		Category linkedCategory = new CategoryImpl() {
			private static final long serialVersionUID = 293738477416763576L;

			@Override
			public boolean isLinked() {
				return true;
			}
			@Override
			public Category getMasterCategory() {
				return masterCategory;
			}
		};
		dependencies = resolver.getChangeSetDependency(linkedCategory);
		assertEquals("linked category is not found in dependency list", masterCategory, dependencies.iterator().next());
		
	}
	
	/**
	 * Test dependency on category type.
	 */
	@Test
	public void testDependencyOnCategoryType() {

		final Category category = new CategoryImpl();
		CategoryType catType = context.mock(CategoryType.class);
		category.setCategoryType(catType);

		context.checking(new Expectations() {
			{
				allowing(categoryLookup).findParent(category); will(returnValue(null));
			}
		});
		
		Set<?>  dependencies = resolver.getChangeSetDependency(category);
		assertEquals("category type is not found in dependency list", catType, dependencies.iterator().next());
	}
}
