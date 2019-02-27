/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalogview.impl;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalogview.SearchService;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.query.CategorySearchCriteria;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Represents a default implementation of <code>SearchService</code>.
 */
public class SearchServiceImpl extends AbstractSearchServiceImpl implements SearchService {
	private static final Logger LOG = Logger.getLogger(SearchServiceImpl.class);

	private SearchConfigFactory searchConfigFactory;

	private BeanFactory beanFactory;

	private SettingValueProvider<Boolean> searchCategoriesFirstSettingProvider;

	/**
	 * <p>Perform searching based on the given search request and returns the search result.</p>
	 * 
	 * <p>By giving the previous search result history, you may get response quicker. If you don't
	 * have it, give a <code>null</code>. It doesn't affect search result.</p>
	 * 
	 * <p>By giving a shopping cart, promotion rules will be applied to the returned products.</p>
	 * 
	 * <p>By giving the product load tuner, you can fine control what data to be loaded for each
	 * product. It is used to improve performance.</p>
	 * 
	 * @param searchRequest the search request
	 * @param previousSearchResultHistory the previous search results, give <code>null</code> if
	 *            you don't have it
	 * @param shoppingCart the shopping cart, give <code>null</code> if you don't have it
	 * @param pageNumber the current page number
	 * @return a <code>SearchResult</code> instance
	 */
	@Override
	public SearchResult search(final SearchRequest searchRequest, final CatalogViewResultHistory previousSearchResultHistory,
							   final ShoppingCart shoppingCart, final int pageNumber) {
		// perform the initial search
		SearchResult result = performSearch(searchRequest, previousSearchResultHistory, shoppingCart,
				pageNumber);

		if (isResultInvalidSize(result)) {
			//generate suggestions
			List<String> suggestions = suggest(searchRequest);
			// no results, can we shortcut to a new result?
			if (suggestions.size() == 1) {
				SearchRequest newRequest = createSearchRequestWithSuggestedKeywords(searchRequest, suggestions);
				//use the suggestion to run the search, but only return the new result if it produces more result
				final SearchResult newResult = performSearch(newRequest, previousSearchResultHistory, shoppingCart, pageNumber);
				if (newResult.getResultsCount() > result.getResultsCount()) {
					logSearch(searchRequest, result); //log the search before we replace it
					result = newResult;
				}
				
			} else {
				// don't want to set the suggestions for the suggestions result
				result.setSuggestions(suggestions);
			}
		}
		logSearch(searchRequest, result);
		return result;
	}

	/**
	 * Creates a new search request based on the given search request but incorporating
	 * the given list of suggested keywords.
	 * @param searchRequest the search request upon which to base the new search request
	 * @param suggestions the suggested keywords
	 * @return a new search request
	 */
	private SearchRequest createSearchRequestWithSuggestedKeywords(
			final SearchRequest searchRequest, final List<String> suggestions) {
		SearchRequest newRequest;
		try {
			newRequest = (SearchRequest) searchRequest.clone();
		} catch (CloneNotSupportedException e) {
			// should never get here
			throw new EpServiceException("Clone not supported!", e);
		}
		newRequest.setKeyWords(suggestions.get(0));
		return newRequest;
	}
	
	/**
	 * Calculates whether the given result is either below the minimum or 
	 * above the maximum threshold of results.
	 * @param result the search result to check for size validity
	 * @return true if the size of the given search result is invalid
	 */
	boolean isResultInvalidSize(final SearchResult result) {
		final int resultSize = result.getResultsCount();
		final SearchConfig searchConfig = getSearchConfigFactory().getSearchConfig(SolrIndexConstants.PRODUCT_SOLR_CORE);
		return resultSize <= searchConfig.getMinimumResultsThreshold() || resultSize >= searchConfig.getMaximumResultsThreshold();
	}

	/**
	 * Performs a search.
	 *
	 * @param request the search request
	 * @param history the history
	 * @param shoppingCart the shopping cart
	 * @param pageNumber the page number
	 * @return the search result
	 */
	SearchResult performSearch(final SearchRequest request, final CatalogViewResultHistory history,
								final ShoppingCart shoppingCart, final int pageNumber) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Searching for items");
		}

		SearchResult result = (SearchResult) loadHistory(request, history);
		final String storeCode = shoppingCart.getStore().getCode();

		// search categories first
		if (getSearchCategoriesFirstSettingProvider().get(storeCode)
				&& !result.isCategoryMatch()) {
			// we only need 1 result here
			List<Long> searchedCategoryUids = searchCategoryIndex(request).getResults(0, 1);

			if (!searchedCategoryUids.isEmpty()) {
				// lets default to first match
				final Category category = getCategoryLookup().findByUid(searchedCategoryUids.get(0));
				result.setCategory(category);
				result.setAvailableChildCategories(
						Lists.newArrayList(Iterables.filter(getCategoryLookup().findChildren(category), getCategoryIsAvailablePredicate())));
				result.setCategoryPath(getCategoryService().getPath(category));
				result.setCategoryMatch(true);
				List<StoreProduct> emptyList = Collections.emptyList();
				result.setProducts(emptyList);
			}
		}

		// nothing? search products
		if (!result.isCategoryMatch() && result.getProducts() == null) {
			searchForProducts(request, shoppingCart, pageNumber, result);
		}

		return result;
	}

	/**
	 * Generates a suggested list of keywords for searching, that are similar to the
	 * keywords in the given search request.
	 * 
	 * @param searchRequest the request from which to derive and suggest new keywords
	 * @return the list of suggested keywords similar to those in the given search request
	 */
	List<String> suggest(final SearchRequest searchRequest) {
		final ProductSearchCriteria searchCriteria = beanFactory.getBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
		searchCriteria.setProductName(searchRequest.getKeyWords());
		searchCriteria.setLocale(searchRequest.getLocale());
		// let's assume all of these suggestions are good
		return getIndexSearchService().suggest(searchCriteria);
	}

	/**
	 * Returns Solr index documents found in the category search index
	 * matching the criteria found in the searchRequest.
	 * 
	 * @param searchRequest - The external request with search criteria in it. 
	 * @return Solr index search results containing category documents matching the criteria. 
	 */
	private IndexSearchResult searchCategoryIndex(final SearchRequest searchRequest) {
		final CategorySearchCriteria searchCriteria =  getSearchCriteriaFactory().createCategorySearchCriteria(searchRequest);
		return getIndexSearchService().search(searchCriteria);
	}

	/**
	 * <p>
	 * Creates a search criteria object with proprietary fields (for browsing/search) filled.
	 * </p>
	 * <p>
	 * This implementation creates a {@link ProductCategorySearchCriteria} containing the words in the given <code>CatalogViewRequest</code> as well
	 * as any keywords that are configured synonyms of the words in the given Request. The synonyms are only found one level deep (only a single pass
	 * to find synonyms is performed - synonyms of synonyms are not relevant).
	 * </p>
	 * 
	 * @param request the current request, which must be a {@link SearchRequest}
	 * @param includeSubCategories not used in this implementation
	 * @return a <code>KeywordSearchCritiera</code> object
	 * @throws EpServiceException if the given <code>CatalogViewRequest</code> is not an instance of <code>SearchRequest</code>
	 */
	@Override
	protected ProductCategorySearchCriteria createCriteriaForProductSearch(final CatalogViewRequest request, final boolean includeSubCategories) {

		return getSearchCriteriaFactory().createKeywordProductCategorySearchCriteria(request);
	}

	/**
	 * Get the search config factory used to get a search configuration.
	 * 
	 * @return the <code>SearchConfigFactory</code>
	 */
	public SearchConfigFactory getSearchConfigFactory() {
		return searchConfigFactory;
	}

	/**
	 * Set the search config factory used to get a search configuration.
	 * 
	 * @param searchConfigFactory the <code>SearchConfigFactory</code> to set
	 */
	public void setSearchConfigFactory(final SearchConfigFactory searchConfigFactory) {
		this.searchConfigFactory = searchConfigFactory;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);
		this.beanFactory = beanFactory;
	}

	protected SettingValueProvider<Boolean> getSearchCategoriesFirstSettingProvider() {
		return searchCategoriesFirstSettingProvider;
	}

	public void setSearchCategoriesFirstSettingProvider(final SettingValueProvider<Boolean> searchCategoriesFirstSettingProvider) {
		this.searchCategoriesFirstSettingProvider = searchCategoriesFirstSettingProvider;
	}

}
