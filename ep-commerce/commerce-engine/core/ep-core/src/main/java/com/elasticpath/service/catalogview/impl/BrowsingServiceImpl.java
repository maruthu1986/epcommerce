/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.browsing.BrowsingRequest;
import com.elasticpath.domain.catalogview.browsing.BrowsingResult;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.BrowsingService;
import com.elasticpath.service.catalogview.PaginationService;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;
import com.elasticpath.service.search.query.ProductSearchCriteria;

/**
 * Represents a default implementation of <code>BrowsingService</code>.
 */
public class BrowsingServiceImpl extends AbstractCatalogViewServiceImpl implements BrowsingService {

	private static final Logger LOG = Logger.getLogger(BrowsingServiceImpl.class);

	private PaginationService paginationService;
	private CategoryService categoryService;

	private BeanFactory beanFactory;

	/**
	 * Perform browsing based on the given browsing request and returns the browsing result.
	 * <p>
	 * By giving the previous browsing result history, you may get response quicker. If you don't have it, give a <code>null</code>. It doesn't
	 * affect the result.
	 * <p>
	 * By giving a shopping cart, promotion rules will be applied to the returned products.
	 * <p>
	 * By giving the product load tuner, you can fine control what data to be loaded for each product. It is used to improve performance.
	 *
	 *
	 * @param browsingRequest the browsing request
	 * @param previousBrowsingResultHistory the previous browsing results, give <code>null</code> if you don't have it
	 * @param shoppingCart the shopping cart, , give <code>null</code> if you don't have it
	 * @param loadProductAssociations true if product associations should be loaded for each product
	 * @param pageNumber the current pageNumber
	 * @return a <code>BrowsingResult</code> instance
	 */
	@Override
	public BrowsingResult browsing(final BrowsingRequest browsingRequest, final CatalogViewResultHistory previousBrowsingResultHistory,
								   final ShoppingCart shoppingCart, final boolean loadProductAssociations, final int pageNumber) {
		BrowsingResult result = (BrowsingResult) loadHistory(browsingRequest, previousBrowsingResultHistory);

		if (result.getCategory() == null) {
			String storeCode = shoppingCart.getStore().getCode();
			//Get the category from category filter to save some DB calls
			Category category = null; 
			for (Filter<?> filter : browsingRequest.getFilters()) {
				if (filter instanceof CategoryFilter) {
					category = ((CategoryFilter) filter).getCategory();
				}
			}
			if (category == null) {
				category = getCategoryLookup().findByUid(browsingRequest.getCategoryUid());
			}

			result.setCategory(category);
			result.setAvailableChildCategories(
					Lists.newArrayList(Iterables.filter(getCategoryLookup().findChildren(category), getCategoryIsAvailablePredicate())));
			result.setCategoryPath(getCategoryService().getPath(category));

			// get featured product list first
			final boolean includeSubCategories = false;
			searchAndSetFeaturedProducts(browsingRequest, result, shoppingCart, loadProductAssociations, includeSubCategories);

			// get normal products, don't include featured products
			final IndexSearchResult productResults = retrieveProducts(browsingRequest, category, 
					shoppingCart.getShopper().getPriceListStack());
			
			List<Long> productUids = getProductsUsingPageNumber(pageNumber, storeCode, productResults);		
						
			List<StoreProduct> products = getStoreProductService().getProductsForStore(productUids, shoppingCart.getStore(), loadProductAssociations);
			products = getIndexUtility().sortDomainList(productUids, products);
			result.setProducts(products);
			result.setResultsCount(productResults.getLastNumFound());
			
			setFilterOptions(browsingRequest, result, productResults);
		}

		// check whether result contains products, if not retrieve top sellers
		if (productsExist(result)) {
			final List<StoreProduct> topSellingProducts = retrieveTopSellingProducts(browsingRequest, loadProductAssociations, shoppingCart);
			result.setTopSellers(topSellingProducts);
		}

		return result;
	}

	/**
	 * Retrieves products based on the page number requested.
	 * @param pageNumber is the pageNumber
	 * @param storeCode is the storeCode
	 * @param productResults is the search result for products
	 * @return list of product uids
	 */
	protected List<Long> getProductsUsingPageNumber(final int pageNumber,
													final String storeCode, 
													final IndexSearchResult productResults) {
		
		int paginationNumber = paginationService.getNumberOfItemsPerPage(storeCode); 
		
		int numberOfResults = productResults.getNumFound();
		
		int lastPageNumber = paginationService.getLastPageNumber(numberOfResults, storeCode);
		
		List<Long> productUids = Collections.emptyList();
		
		productResults.setRememberOptions(true);
		
		// Return the max num pages if the page number entered is too large
		if (pageNumber <= lastPageNumber) {
			productUids = getPagedResults(productResults, pageNumber, paginationNumber);								
		} else {
			LOG.info("Page number requested " + pageNumber + " ignored, returning page number " + lastPageNumber);
			productUids = getPagedResults(productResults, lastPageNumber, paginationNumber);
		}
		return productUids;
	}

	/**
	 * Products exist in the result returned.
	 * @param result
	 * @return
	 */
	private boolean productsExist(final BrowsingResult result) {
		return result.getProducts() != null && result.getProducts().size() == 0;
	}

	private IndexSearchResult retrieveProducts(final BrowsingRequest browsingRequest, final Category category, final PriceListStack priceListStack) {
		
		final IndexSearchResult results = searchProducts(browsingRequest, false, false, priceListStack);
			
		// TA376 Logic changes when retrieve products under category.
		// 1. Check the category contains products.
		// If no products in this category, and its the root category,
		// and no other filter options, don't retrieve the sub category.
		if ((results.getNumFound() <= 0) && (!category.hasParent()) && (browsingRequest.getFilters().size() <= 1)) {
			return searchProducts(browsingRequest, false, true, priceListStack);
		}

		// 2. Get all with sub categories.
		return searchProducts(browsingRequest, true, true, priceListStack);
	}

	private List<StoreProduct> retrieveTopSellingProducts(final BrowsingRequest browsingRequest, final boolean loadProductAssociations,
			final ShoppingCart shoppingCart) {
		final long categoryUid = browsingRequest.getCategoryUid();
		final TopSeller topSeller = getTopSellerService().findTopSellerByCategoryUid(categoryUid);
		if (topSeller == null) {
			return Collections.emptyList();
		}

		List<Long> topSellerUids = new ArrayList<>(topSeller.getProductUids());
		return getStoreProductService().getProductsForStore(topSellerUids, shoppingCart.getStore(), loadProductAssociations);
	}

	/**
	 * Returns a new instance of {@link BrowsingResult}.
	 * 
	 * @return a new instance of {@link BrowsingResult}
	 */
	@Override
	protected CatalogViewResult createCatalogViewResult() {
		return beanFactory.getBean(ContextIdNames.BROWSING_RESULT);
	}

	@Override
	protected ProductCategorySearchCriteria createCriteriaForProductSearch(final CatalogViewRequest request,
			final boolean includeSubCategories) {
		final BrowsingRequest browsingRequest = (BrowsingRequest) request;
		final ProductSearchCriteria searchCriteria = beanFactory.getBean(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
		
		// only want exact matches when browsing
		searchCriteria.setFuzzySearchDisabled(true);
		searchCriteria.setLocale(request.getLocale());
		searchCriteria.setOnlyWithinDirectCategory(!includeSubCategories);
		searchCriteria.setDirectCategoryUid(browsingRequest.getCategoryUid());
		searchCriteria.setStoreCode(getStoreConfig().getStoreCode());
		
		// this is done elsewhere, but need to do this here for additional logic
		if (includeSubCategories) {
			final Set<Long> categoryUids = new HashSet<>();
			categoryUids.add(browsingRequest.getCategoryUid());
			searchCriteria.setAncestorCategoryUids(categoryUids);
		}
		
		searchCriteria.setCatalogCode(getStoreConfig().getStore().getCatalog().getCode());
		return searchCriteria;
	}

	/**
	 * Creates a CategoryIsAvailablePredicate.
	 * @return the predicate
	 */
	protected Predicate<Category> getCategoryIsAvailablePredicate() {
		return new CategoryIsAvailablePredicate();
	}

	/**
	 * @param paginationService service
	 */
	public void setPaginationService(final PaginationService paginationService) {
		this.paginationService = paginationService;
	}

	/**
	 * @return pagination service
	 */
	public PaginationService getPaginationService() {
		return paginationService;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);
		this.beanFactory = beanFactory;
	}

	protected CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}
}
