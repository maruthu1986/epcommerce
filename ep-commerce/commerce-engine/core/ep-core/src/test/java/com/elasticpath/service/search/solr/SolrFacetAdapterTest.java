/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.search.solr;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalogview.AttributeKeywordFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.RangeFilterType;
import com.elasticpath.domain.catalogview.impl.AttributeKeywordFilterImpl;
import com.elasticpath.domain.catalogview.impl.BrandFilterImpl;
import com.elasticpath.domain.catalogview.impl.FilterOptionImpl;
import com.elasticpath.domain.catalogview.impl.PriceFilterImpl;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.util.mock.PropertyEnabledExpectations;

/**
 * Test cases for the functionality in SolrFacetAdapter.
 */
public class SolrFacetAdapterTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SolrFacetAdapter solrFacetAdapter;
	
	private static final String CATALOG_CODE = "catalog";
	
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	
	/**
	 * Setup required for each test.
	 * 
	 * @throws Exception in case of setup errors
	 */
	@Before
	public void setUp() throws Exception {
		solrFacetAdapter = new SolrFacetAdapter();
		solrFacetAdapter.setIndexUtility(new IndexUtilityImpl());
		solrFacetAdapter.setAnalyzer(new QueryAnalyzerImpl());
		
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
	}
	
	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test an empty price list stack.
	 */
	@Test
	public void testGetPriceFacetForEmptyStack() {
		PriceFilter filter = context.mock(PriceFilter.class);
		List<String> stackGuids = new ArrayList<>();
		Query emptyQuery = solrFacetAdapter.getPriceQueryForStack(filter, CATALOG_CODE, stackGuids);
		assertEquals("An empty stack should return an empty query", "", emptyQuery.toString());
	}

	/**
	 * Test a single list stack returns a facet query for the price list.
	 */
	@Test
	public void testGetPriceFacetForSingleListStack() {
		PriceFilter filter = new PriceFilterImpl();
		filter.setLowerValue(BigDecimal.ONE);
		filter.setUpperValue(BigDecimal.TEN);
		List<String> stackGuids = new ArrayList<>();
		stackGuids.add("PriceList1");
		Query query = solrFacetAdapter.getPriceQueryForStack(filter, CATALOG_CODE, stackGuids);
		assertEquals("There should be a single facet query for the given price list", 
				"price_catalog_PriceList1:1 price_catalog_PriceList1:{1 TO 10}", query.toString());
	}
	
	/**
	 * Test a stack with two price lists returns a query which obeys stack priority.
	 */
	@Test
	public void testGetPriceFacetForTwoListStack() {
		PriceFilter filter = new PriceFilterImpl();
		filter.setLowerValue(BigDecimal.ONE);
		filter.setUpperValue(BigDecimal.TEN);
		List<String> stackGuids = new ArrayList<>();
		stackGuids.add("PriceList1");
		stackGuids.add("PriceList2");
		Query query = solrFacetAdapter.getPriceQueryForStack(filter, CATALOG_CODE, stackGuids);
		String expected = "(price_catalog_PriceList1:1 price_catalog_PriceList1:{1 TO 10}) " 
			+ "(-price_catalog_PriceList1:[* TO *] +(price_catalog_PriceList2:1 price_catalog_PriceList2:{1 TO 10}))";
		assertEquals("There should be a facet query that excludes already included results", expected, query.toString());
		
	}

	/**
	 * Test a stack with three price lists returns a query which obeys stack priority.
	 */
	@Test
	public void testGetPriceFacetForThreeListStack() {
		PriceFilter filter = new PriceFilterImpl();
		filter.setLowerValue(BigDecimal.ONE);
		filter.setUpperValue(BigDecimal.TEN);
		List<String> stackGuids = new ArrayList<>();
		stackGuids.add("PL1");
		stackGuids.add("PL2");
		stackGuids.add("PL3");
		Query query = solrFacetAdapter.getPriceQueryForStack(filter, CATALOG_CODE, stackGuids);
		String expected = "(price_catalog_PL1:1 price_catalog_PL1:{1 TO 10}) " 
			+ "(-price_catalog_PL1:[* TO *] +((price_catalog_PL2:1 price_catalog_PL2:{1 TO 10}) (-price_catalog_PL2:[* TO *] "
			+ "+(price_catalog_PL3:1 price_catalog_PL3:{1 TO 10}))))";
		assertEquals("There should be a facet query that excludes already included results", 
				expected, 
				query.toString());
		
	}

	/**
	 * Test a BETWEEN range filter query.
	 */
	@Test
	public void testConstructRangeFilterQueryBetween() {
		PriceFilter filter = new PriceFilterImpl() {
			private static final long serialVersionUID = -4187630399471882986L;

			@Override
			public RangeFilterType getRangeType() {
				return RangeFilterType.BETWEEN;
			}
		};
		filter.setLowerValue(BigDecimal.ONE);
		filter.setUpperValue(BigDecimal.TEN);
		assertEquals("field:1 field:{1 TO 10}", solrFacetAdapter.constructRangeFilterQuery(filter, "field").toString());
	}

	/**
	 * Test a LESS_THAN range filter query.
	 */
	@Test
	public void testConstructRangeFilterQueryLessThan() {
		PriceFilter filter = new PriceFilterImpl() {
			private static final long serialVersionUID = -4663486673897612948L;

			@Override
			public RangeFilterType getRangeType() {
				return RangeFilterType.LESS_THAN;
			}
		};
		filter.setUpperValue(BigDecimal.TEN);
		assertEquals("field:{* TO 10}", solrFacetAdapter.constructRangeFilterQuery(filter, "field").toString());
	}
	
	
	/**
	 * Test a MORE_THAN range filter query.
	 */
	@Test
	public void testConstructRangeFilterQueryMoreThan() {
		PriceFilter filter = new PriceFilterImpl() {
			private static final long serialVersionUID = 1409394296912457360L;

			@Override
			public RangeFilterType getRangeType() {
				return RangeFilterType.MORE_THAN;
			}
		};
		
		filter.setLowerValue(BigDecimal.ONE);
		assertEquals("field:[1 TO *]", solrFacetAdapter.constructRangeFilterQuery(filter, "field").toString());
	}
	
	/**
	 * Test a MORE_THAN range filter query.
	 */
	@Test
	public void testConstructMultiBrand() {
		final String code1 = "F00007";
		final String code2 = "F00008";
		
		BrandFilter filter = new BrandFilterImpl() {
			private static final long serialVersionUID = 2095303506757877014L;

			@Override
			public Set<Brand> getBrands() {
				
				Brand brand1 = new BrandImpl();
				brand1.setCode(code1);
				Brand brand2 = new BrandImpl();
				brand2.setCode(code2);

				Set<Brand> brands = new LinkedHashSet<>();
				
				brands.add(brand1);
				brands.add(brand2);
//				setBrands(brands);
				
				return brands;
			}
			
		};
		
		
		assertEquals("brandCode:" + code1 + " brandCode:" + code2, 
				solrFacetAdapter.constructBrandFilterQuery(filter).toString());
	}	
	
	/**
	 * Tests the facet queries with attribute keyword filter are parsed correctly.
	 */
	@Test
	public void testParseFacetQueriesWithAttributeKeywordFilter() {
		
		final Attribute attribute = context.mock(Attribute.class, "attr");
		final AttributeService attributeService = context.mock(AttributeService.class, "attrService");
		final AttributeValue attributeValue = context.mock(AttributeValueWithType.class, "attrValue");

		final String attributeKey = "A00373";
		final String attributeValueText = "Red blur";
		final FilterOption<AttributeKeywordFilter> filterOption = new FilterOptionImpl<>();
		final AttributeType attributeType = AttributeType.valueOf(1);
		
		context.checking(new PropertyEnabledExpectations() {
			{
				allowing(attribute).getKey(); will(returnValue(attributeKey));
				allowing(attribute).isLocaleDependant(); will(returnValue(false));
				allowing(attribute).getAttributeType(); will(returnValue(attributeType));
				
				allowing(attributeValue).getStringValue(); will(returnValue(attributeValueText));
				
				allowing(attributeService).findByKey(with(any(String.class))); will(returnValue(attribute));

				allowing(beanFactory).getBean(ContextIdNames.ATTRIBUTE_SERVICE); will(returnValue(attributeService));
				allowing(beanFactory).getBean(ContextIdNames.ATTRIBUTE_KEYWORD_FILTER); will(returnValue(filterOption));
				
			}
		});
		
		final AttributeKeywordFilter attributeKeywordFilter = new AttributeKeywordFilterImpl();
		
		solrFacetAdapter.getFilterLookupMap().put(attributeKey, attributeKeywordFilter);	

		attributeKeywordFilter.setAttributeKey(attributeKey);
		attributeKeywordFilter.setId(attributeKey);
		AttributeValueWithType value = new ProductAttributeValueImpl();
		value.setAttributeType(AttributeType.valueOf(2));
		value.setStringValue(attributeValueText);
		attributeKeywordFilter.setAttributeValue(value);
		
		
		((IndexUtilityImpl) solrFacetAdapter.getIndexUtility()).setSolrAttributeTypeExt(new HashMap<>());
		Query query = solrFacetAdapter.constructAttributeKeywordFilterQuery(attributeKeywordFilter, Locale.ENGLISH, false);
		
		assertEquals("+attribute." + attributeKey + "_st:Red "
				+ "+attribute." + attributeKey + "_st:blur", query.toString());
			
	}
	
	/**
	 * Tests that when the keyword value is not trimmed we would get proper query.
	 */
	@Test
	public void testConstructAttributeKeywords() {
		
		final AttributeKeywordFilter filter = context.mock(AttributeKeywordFilter.class);
		final Attribute attribute = context.mock(Attribute.class);
		final AttributeValueWithType attributeValue = context.mock(AttributeValueWithType.class);
		
		final String keywordValue = " biology ";
		
		context.checking(new PropertyEnabledExpectations() {
			{
				oneOf(attribute).getKey();
				will(returnValue("testKey"));

				AttributeType attributeType = AttributeType.SHORT_TEXT;
				allowing(attribute).getAttributeType(); will(returnValue(attributeType));

				oneOf(attribute).isLocaleDependant();
				will(returnValue(true));

				oneOf(attributeValue).getStringValue();
				will(returnValue(keywordValue));

				oneOf(filter).getAttributeValue();
				will(returnValue(attributeValue));

				oneOf(filter).getAttribute();
				will(returnValue(attribute));
			}
		});
	
		((IndexUtilityImpl) solrFacetAdapter.getIndexUtility()).setSolrAttributeTypeExt(new HashMap<>());
		BooleanQuery query = (BooleanQuery) solrFacetAdapter.constructAttributeKeywordFilterQuery(filter, Locale.US, true);
		
		assertEquals(1, query.getClauses().length);
		assertEquals("attribute.testKey|en_US|_st:biology", query.getClauses()[0].getQuery().toString());
	}

}
