/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.attribute.impl.AttributeValueGroupImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueFactoryImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueImpl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalogview.AttributeKeywordFilter;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.search.AdvancedSearchRequest;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.pricing.impl.PriceListStackImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.search.index.solr.service.IndexBuildService;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalogview.AdvancedSearchService;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.persister.testscenarios.ShoppingCartSimpleStoreScenario;

/**
 * Definition for the Advanced Keyword Search Filter feature.
 */
public class AdvancedKeywordFilterSearchStepDefinitionsHelper {

	private static final int TWENTY = 20;
	private Product productA;
	
	@Autowired
	private TestApplicationContext tac;
	
	@Autowired
	private AdvancedSearchService advancedSearchService;
	
	@Autowired
	private BeanFactory beanFactory;
	
	@Autowired
	@Qualifier("threadLocalStorage")
	private ThreadLocalStorageImpl storeConfig;
	
	private ShoppingCartSimpleStoreScenario scenario;
	
	private AdvancedSearchRequest searchRequest;
	
	private SearchResult searchResult;
	
	@Autowired
	private AttributeService attributeService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private FilterFactory filterFactory;
	
	@Autowired
	private ProductTypeService productTypeService;
	
	@Autowired
	private IndexBuildService indexBuildService;

	private static final String PL_GUID = "PLR";
	
	private Attribute attribute;
	
	private void setUpAttributes(final AttributeType attributeType, final String attributeKey) {
		attribute = new AttributeImpl();
		attribute.setCatalog(scenario.getCatalog());
		attribute.setKey(attributeKey);
		attribute.setAttributeType(attributeType);
		attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		attribute.setGlobal(false);
		attribute.setName(attributeKey + "name");
		attribute.setRequired(false);
		attribute.setLocaleDependant(false);
		attributeService.add(attribute);
		assertTrue(attribute.isPersisted());
	}

	private void setUpProducts(final String productAttributeValue) {
		ProductLoadTuner tuner = beanFactory.getBean(ContextIdNames.PRODUCT_LOAD_TUNER);
		tuner.setLoadingAttributeValue(true);
		tuner.setLoadingSkus(false);
		
		List<Attribute> attributes = attributeService.getProductAttributes();
		Attribute attribute = attributes.get(0);
		
		List<Product> products = productService.findByCategoryUidPaginated(
				scenario.getCategory().getUidPk(), 0, TWENTY, tuner);
		
		AttributeValueGroup avg = new AttributeValueGroupImpl(new ProductAttributeValueFactoryImpl());
		
		Map<String, AttributeValue> attributeValueMap = new HashMap<>();
		AttributeValue value = new ProductAttributeValueImpl();
		value.setLocalizedAttributeKey(attribute.getKey());
		value.setAttribute(attribute);
		value.setAttributeType(attribute.getAttributeType());
		value.setStringValue(productAttributeValue);
		attributeValueMap.put(attribute.getKey(), value);
		
		avg.setAttributeValueMap(attributeValueMap);
		
		Set<AttributeGroupAttribute> groupValue = getGroupAttributeFromList(attributes);
		
		productA = products.get(0);
		
		ProductType productType = productA.getProductType();
		productType.setProductAttributeGroupAttributes(groupValue);
		productTypeService.update(productType);
		
		productA.setAttributeValueMap(attributeValueMap);
		productA.setAttributeValueGroup(avg);
		productService.saveOrUpdate(productA);
	}

	private Set<AttributeGroupAttribute> getGroupAttributeFromList(final List<Attribute> attributes) {
		int order = 0;
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<>();

		for (final Attribute attribute : attributes) {
			final AttributeGroupAttribute groupAttribute = beanFactory.getBean(ContextIdNames.PRODUCT_TYPE_PRODUCT_ATTRIBUTE);
			groupAttribute.setAttribute(attribute);
			groupAttribute.setOrdering(order++);
			attributeGroupAttributes.add(groupAttribute);
		}
		return attributeGroupAttributes;
	}
	
	/**
	 * The setup statement.
	 */
	public void advancedSearchSetup() {
		scenario = tac.useScenario(ShoppingCartSimpleStoreScenario.class);
		storeConfig.setStoreCode(scenario.getStore().getCode());
	}
	
	/**
	 * The statement that specifies the attribute name and the type of the attribute.
	 * @param attributeName attribute name
	 */
	public void configureAttributeType(final String attributeName) {
		setUpAttributes(AttributeType.LONG_TEXT, attributeName);
	}

	/**
	 * The statement that specifies the attribute text value for the given attribute key.
	 * @param attrKey the attribute key
	 * @param productAttrValue the product attribute value
	 */
	public void setUpAttributeValue(final String attrKey, final String productAttrValue) {
		setUpProducts(productAttrValue);
		
		// setup finished
		build();
		
		assertTrue(attributeService.isInUse(attribute.getUidPk()));
		assertNotNull(productA.getAttributeValueMap().get(attrKey));
	}
	
	/**
	 * The statement that specifies the search term applied on the attribute key.
	 * @param searchTerm the search term
	 * @param attrKey the attribute key
	 */
	public void runAdvancedSearch(final String searchTerm, final String attrKey) {
		searchRequest = beanFactory.getBean(ContextIdNames.ADVANCED_SEARCH_REQUEST);
		searchRequest.initialize();
		searchRequest.setLocale(Locale.ENGLISH);
		searchRequest.parseSorterIdStr(SortUtility.constructSortTypeOrderString(
				StandardSortBy.RELEVANCE, SortOrder.DESCENDING));
		addKeywordFilter(attrKey, searchTerm);
		
		ShoppingCart shoppingCart = beanFactory.getBean(ContextIdNames.SHOPPING_CART);
		setupShoppingCart(shoppingCart);
		
		searchResult = advancedSearchService.search(searchRequest, shoppingCart, 1);
	}
	
	/**
	 * Check the results.
	 */
	public void ensureSearchServiceResultsIsSuccessful() {
		assertNotEquals(0, searchResult.getResultsCount());
		List<StoreProduct> storeProducts = searchResult.getProducts();
		StoreProduct wrappedProduct = storeProducts.get(0);
		assertNotNull(wrappedProduct.getWrappedProduct());
	}
	
	/**
	 * Check the results.
	 */
	public void ensureSearchServiceResultsFails() {
		assertEquals(0, searchResult.getResultsCount());
	}
	
	private void addKeywordFilter(final String attributeKey, final String attributeValue) {
		final AttributeKeywordFilter attributeKeywordFilter = 
			filterFactory.createAttributeKeywordFilter(attributeKey, Locale.ENGLISH, attributeValue);
		
		if (!attributeValue.isEmpty()) {
			searchRequest.setFiltersIdStr(attributeKeywordFilter.getId(), scenario.getStore());
		}
	}
	
	private void setupShoppingCart(final ShoppingCart shoppingCart) {
		Shopper shopper = beanFactory.getBean(ContextIdNames.SHOPPER);
		shopper.setShopperMemento(new ShopperMementoImpl());
		shopper.setGuid("shopper-1");
		shopper.setStoreCode(scenario.getStore().getCode());
		CustomerSession customerSession = new CustomerSessionImpl();
		shopper.updateTransientDataWith(customerSession);                                                                              
		PriceListStackImpl priceListStack = new PriceListStackImpl();
		priceListStack.addPriceList(PL_GUID);
		priceListStack.setCurrency(Currency.getInstance("CAD"));
		shopper.setPriceListStack(priceListStack);
		
		shoppingCart.setShopper(shopper);
		shoppingCart.setStore(scenario.getStore());
	}
	
	/**
	 * Builds concrete search indexes. <br>
	 * NOTE: This method should be called before the search is performed.
	 * 
	 * @return true in any case.
	 */
	private boolean build() {
		indexBuildService.buildIndex(IndexType.PRODUCT);
		return true;
	}
}
