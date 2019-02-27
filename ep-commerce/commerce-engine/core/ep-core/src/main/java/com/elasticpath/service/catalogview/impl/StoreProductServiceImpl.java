/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.catalogview.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductAssociationRetrieveStrategy;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalogview.AvailabilityStrategy;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.catalogview.ProductAvailabilityService;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Default implementation of {@link StoreProductService}.
 */
@SuppressWarnings("PMD.GodClass")
public class StoreProductServiceImpl implements StoreProductService {

	private ProductService productService;
	private ProductLookup productLookup;
	private ProductAssociationRetrieveStrategy productAssociationRetrieveStrategy;
	private ProductAvailabilityService productAvailabilityService;
	private ProductInventoryShoppingService productInventoryShoppingService;
	private ShoppingItemDtoFactory shoppingItemDtoFactory;
	private BundleIdentifier bundleIdentifier;
	private List<AvailabilityStrategy> availabilityStrategies;

	@Override
	public StoreProduct getProductForStore(final long uidPk, final Store store, final boolean loadProductAssociations) {
		Product product = getProductLookup().findByUid(uidPk);
		StoreProduct storeProduct = wrapProduct(product, store);
		if (loadProductAssociations) {
			setStoreProductAssociations(storeProduct, store, store.getCatalog()
					.getCode());
		}
		return storeProduct;
	}

	/**
	 * Get a store product for the specified product.
	 *
	 * @param product the product to get the store product for.
	 * @param store   the store the product belongs to.
	 * @return the store product
	 */
	@Override
	public StoreProduct getProductForStore(final Product product, final Store store) {
		return wrapProduct(product, store);
	}

	/**
	 * Get a list of store products for a specified store.
	 *
	 * @param uidPks                  the uids of the products.
	 * @param store                   the store the products belong to.
	 * @param loadProductAssociations true if product associations should be loaded for each product
	 * @return the store product
	 */
	@Override
	public List<StoreProduct> getProductsForStore(
			final List<Long> uidPks, final Store store, final boolean loadProductAssociations) {
		List<Product> products = getProductLookup().findByUids(uidPks);
		List<StoreProduct> storeProducts = new ArrayList<>();
		//Retrieving all products inventory details outside the loop to batch the inventory queries together
		Map<String, Map<String, SkuInventoryDetails>> skuInventoryDetails = calculateInventoryDetailsForAllProductsSkus(products, store);
		for (Product product : products) {
			StoreProduct storeProduct = wrapProduct(product, store, skuInventoryDetails.get(product.getCode()));
			if (loadProductAssociations) {
				setStoreProductAssociations(storeProduct, store, store.getCatalog().getCode());
			}
			storeProducts.add(storeProduct);
		}
		return storeProducts;
	}

	/**
	 * Finds and sets the displayable ProductAssociations on a StoreProduct. This method may be used if
	 * you have wrapped a Product in a StoreProduct and must set the Associations.
	 * Associations are also wrapped for storefront presentation.
	 *
	 * @param storeProduct the store product upon which to set the Product Associations
	 * @param store        the store this product belongs to
	 * @param catalogCode  the code for the catalog in which the StoreProduct exists (the store's catalog's code)
	 */
	protected void setStoreProductAssociations(final StoreProduct storeProduct, final Store store, final String catalogCode) {
		Set<ProductAssociation> associationSet = new HashSet<>();

		ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setSourceProductCode(storeProduct.getCode());
		criteria.setCatalogCode(catalogCode);
		criteria.setWithinCatalogOnly(true);
		Set<ProductAssociation> productAssociations = getProductAssociationRetrieveStrategy().getAssociations(criteria);

		for (ProductAssociation association : productAssociations) {
			StoreProduct targetProduct = wrapProduct(association.getTargetProduct(), store);
			if (targetProduct.isProductDisplayable()) {
				ProductAssociation associationCopy = association.deepCopy();
				associationCopy.setTargetProduct(targetProduct);
				associationSet.add(associationCopy);
			}
		}

		storeProduct.setProductAssociations(associationSet);
	}

	@Override
	public Collection<IndexProduct> getIndexProducts(final Collection<Long> productUids, final Collection<Store> stores,
													 final FetchGroupLoadTuner fetchGroupLoadTuner) {
		List<Product> products = productService.findByUidsWithFetchGroupLoadTuner(productUids, fetchGroupLoadTuner);
		Collection<IndexProduct> indexProducts = new HashSet<>();
		for (Product product : products) {
			indexProducts.add(createIndexProduct(product, stores));
		}
		return indexProducts;
	}

	/**
	 * Gets the index product.
	 *
	 * @param uidPk     the product UID
	 * @param loadTuner the load tuner
	 * @param stores    a collection of stores for which the product is being indexed
	 * @return the IndexProduct instance
	 */
	@Override
	public IndexProduct getIndexProduct(final long uidPk, final FetchGroupLoadTuner loadTuner, final Collection<Store> stores) {
		Product product = productService.getTuned(uidPk, loadTuner);

		if (product == null) {
			throw new IllegalArgumentException("No product exists with UIDPK=" + uidPk);
		}

		return createIndexProduct(product, stores);
	}

	@Override
	public IndexProduct createIndexProduct(final Product product, final Collection<Store> stores) {
		IndexProductImpl indexProduct = new IndexProductImpl(product);
		for (Store store : stores) {
			indexProduct.setAvailable(store.getCode(), false);
			indexProduct.setDisplayable(store.getCode(), false);
			if (!product.getProductSkus().isEmpty()) {
				Map<String, SkuInventoryDetails> allSkuInventoryDetails = calculateInventoryDetailsForAllSkus(product, store);

				indexProduct.setAvailable(store.getCode(),
						getProductAvailabilityService().isProductAvailable(product, allSkuInventoryDetails, false));
				indexProduct.setDisplayable(store.getCode(),
						getProductAvailabilityService().isProductDisplayable(product, store, allSkuInventoryDetails, false));
			}
		}

		return indexProduct;
	}

	/**
	 * Sets the product service instance.
	 *
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Get product association retrieve strategy.
	 *
	 * @return the product association retrieve strategy
	 */
	protected ProductAssociationRetrieveStrategy getProductAssociationRetrieveStrategy() {
		return productAssociationRetrieveStrategy;
	}

	/**
	 * Sets the product association service.
	 *
	 * @param productAssociationRetrieveStrategy the product association retrieve strategy
	 */
	public void setProductAssociationRetrieveStrategy(final ProductAssociationRetrieveStrategy productAssociationRetrieveStrategy) {
		this.productAssociationRetrieveStrategy = productAssociationRetrieveStrategy;
	}

	/**
	 * Wraps an existing Product in StoreProduct using the default product sku.
	 *
	 * @param product the product
	 * @param store   the store
	 * @return {@link StoreProduct}
	 */
	protected StoreProduct wrapProduct(final Product product, final Store store) {
		/*
		 * So bundles with multi-sku product constituents are not prematurely marked as unavailable if the
		 * constituent's default sku is unavailable and the constituent still has other skus available
		 */
		if (product instanceof ProductBundle) {
			updateBundleConstituentDefaultSkus((ProductBundle) product, store);
		}

		Map<String, SkuInventoryDetails> skuInventoryDetails;
		skuInventoryDetails = calculateInventoryDetailsForAllSkus(product, store);
		return wrapProduct(product, store, skuInventoryDetails);
	}

	/**
	 * Wraps an existing Product in StoreProduct for the sku with specific uid given all the skus inventory details. The default sku is used if
	 * there's no sku for a given uid.
	 *
	 * @param product             the product
	 * @param store               the store
	 * @param skuInventoryDetails the sku inventory details map for all skus of the product
	 * @return {@link StoreProduct}
	 */
	protected StoreProduct wrapProduct(final Product product, final Store store,
									   final Map<String, SkuInventoryDetails> skuInventoryDetails) {
		StoreProductImpl storeProductImpl = new StoreProductImpl(product);

		determineSkusAvailability(product.getProductSkus().values(), skuInventoryDetails, storeProductImpl);
		return populateProductDetails(product, store, storeProductImpl, skuInventoryDetails);
	}

	/**
	 * For the bundle, set all constituents' default skus to be an available sku.
	 * <p>
	 * If a product constituent's default sku is not available and the constituent has other skus available,
	 * then the default sku is set to the fist available sku found.
	 * If a product constituent's default sku is available, nothing is changed.
	 * If a product constituent has no available skus, nothing is changed.
	 * If a fixed sku constituent is not available, the sku is not changed even if the parent product has other skus available.
	 *
	 * @param bundle the product bundle
	 * @param store  the store
	 */
	protected void updateBundleConstituentDefaultSkus(final ProductBundle bundle, final Store store) {
		List<BundleConstituent> constituents = bundle.getConstituents();
		for (BundleConstituent constituent : constituents) {
			Product constituentProduct = constituent.getConstituent().getProduct();

			if (constituentProduct instanceof ProductBundle) {
				updateBundleConstituentDefaultSkus((ProductBundle) constituentProduct, store);
			} else if (constituent.getConstituent().isProduct()) {
				updateProductConstituentDefaultSku(constituent, store);
			}
		}
	}

	private void updateProductConstituentDefaultSku(final BundleConstituent constituent, final Store store) {
		Product constituentProduct = constituent.getConstituent().getProduct();
		ProductSku defaultSku = constituent.getConstituent().getProductSku();

		boolean isDefaultSkuAvailable = isSkuAvailable(constituentProduct, defaultSku, store);
		if (!isDefaultSkuAvailable) {
			for (ProductSku otherSku : constituentProduct.getProductSkus().values()) {
				boolean isOtherSkuAvailable = isSkuAvailable(constituentProduct, otherSku, store);
				if (isOtherSkuAvailable) {
					constituentProduct.setDefaultSku(otherSku);
					break;
				}
			}
		}
	}

	private boolean isSkuAvailable(final Product product, final ProductSku sku, final Store store) {
		SkuInventoryDetails inventoryDetails = productInventoryShoppingService.getSkuInventoryDetails(sku, store);
		return getProductAvailabilityService().isSkuAvailable(product, sku, inventoryDetails);
	}

	/**
	 * For each Sku of a product determine if is in stock and within date ranges.
	 *
	 * @param skus                - the skus.
	 * @param skuInventoryDetails - the inventory for the skus
	 * @param storeProductImpl    the wraped product
	 */
	protected void determineSkusAvailability(final Collection<ProductSku> skus, final Map<String, SkuInventoryDetails> skuInventoryDetails,
											 final StoreProductImpl storeProductImpl) {
		for (ProductSku sku : skus) {
			SkuInventoryDetails inventoryDetails = skuInventoryDetails.get(sku.getSkuCode());
			boolean hasUnallocatedInventory = false;
			if (inventoryDetails != null) {
				storeProductImpl.addInventoryDetails(sku.getSkuCode(), inventoryDetails);
				hasUnallocatedInventory = inventoryDetails.hasSufficientUnallocatedQty();
			}
			storeProductImpl.setSkuAvailable(sku.getSkuCode(), hasUnallocatedInventory && sku.isWithinDateRange());
		}
	}

	/**
	 * Populates the StoreProduct's displayable, available, purchaseable and availability fields.  If skuUid is null,
	 * then the fields are evaluated based on inventory/availability info for all skus in the product.  If the skuUid is
	 * not null, then the fields are evaluated based on the given sku only.
	 *
	 * @param product             the product
	 * @param store               the Store Product's store
	 * @param storeProductImpl    the store product to populate
	 * @param skuInventoryDetails a Map of sku code to SkuInventoryDetails for the product
	 * @return the store product.
	 */
	protected StoreProduct populateProductDetails(final Product product, final Store store, final StoreProductImpl storeProductImpl,
												  final Map<String, SkuInventoryDetails> skuInventoryDetails) {
		if (product.getProductSkus().isEmpty()) {
			storeProductImpl.setProductDisplayable(false);
			storeProductImpl.setProductAvailable(false);
			storeProductImpl.setProductAvailability(Availability.NOT_AVAILABLE);
		} else {
			boolean isAvailable = getProductAvailabilityService().isProductAvailable(
					product, skuInventoryDetails, true);
			boolean isDisplayable = getProductAvailabilityService().isProductDisplayable(
					product, store, skuInventoryDetails, true);

			storeProductImpl.setProductAvailable(isAvailable);
			storeProductImpl.setProductDisplayable(isDisplayable);
			storeProductImpl.setProductAvailability(getProductAvailability(product, isAvailable, isDisplayable));

			for (ProductSku sku : product.getProductSkus().values()) {
				setSkuDetails(product, store, storeProductImpl, skuInventoryDetails, sku);
			}
		}

		storeProductImpl.setNotSoldSeparately(product.isNotSoldSeparately());

		return storeProductImpl;
	}

	private void setSkuDetails(final Product product, final Store store, final StoreProductImpl storeProductImpl,
							   final Map<String, SkuInventoryDetails> skuInventoryDetails, final ProductSku sku) {
		final String skuCode = sku.getSkuCode();
		SkuInventoryDetails skuInventory = skuInventoryDetails.get(skuCode);
		boolean isSkuAvailable = getProductAvailabilityService().isSkuAvailable(product, sku, skuInventory);
		boolean isSkuDisplayable = getProductAvailabilityService().isSkuDisplayable(product, sku, store, skuInventory);

		storeProductImpl.setSkuAvailable(skuCode, isSkuAvailable);
		storeProductImpl.setSkuDisplayable(skuCode, isSkuDisplayable);
		storeProductImpl.setSkuAvailability(skuCode, getProductAvailability(product, isSkuAvailable, isSkuDisplayable));
	}

	/**
	 * Calculated the inventory details for the product's given sku.
	 *
	 * @param productSku the product sku
	 * @param store      the store
	 * @return the details
	 * @deprecated in favour of {@code calculateInventoryDetailsForAllSkus}
	 */
	@Deprecated
	SkuInventoryDetails calculateInventoryDetails(final ProductSku productSku, final Store store) {
		return productInventoryShoppingService.getSkuInventoryDetails(productSku, store);
	}

	/**
	 * Calculated the inventory details for all the skus for a product.
	 *
	 * @param product the product
	 * @param store   the store
	 * @return the details
	 */
	Map<String, SkuInventoryDetails> calculateInventoryDetailsForAllSkus(final Product product, final Store store) {
		return productInventoryShoppingService.getSkuInventoryDetailsForAllSkus(product, store);
	}

	/**
	 * Calculated the inventory details for all the skus for all products in the list.
	 *
	 * @param products the product
	 * @param store    the store
	 * @return a map of product code to map of sku code to sku inventory details
	 */
	Map<String, Map<String, SkuInventoryDetails>> calculateInventoryDetailsForAllProductsSkus(final List<Product> products, final Store store) {
		return productInventoryShoppingService.getSkuInventoryDetailsForAllSkus(products, store);
	}

	/**
	 * Gets the product availability. Calls a set of injected strategies, stopping at the first one that
	 * returns a non-null result.
	 *
	 * @param product       the product
	 * @param isAvailable   whether the product is available
	 * @param isDisplayable whether the product is displayable
	 * @return the product availability
	 */
	protected Availability getProductAvailability(final Product product, final boolean isAvailable, final boolean isDisplayable) {
		Availability availability = null;
		for (AvailabilityStrategy strategy : getAvailabilityStrategies()) {
			availability = strategy.getAvailability(product, isAvailable, isDisplayable);
			if (availability != null) {
				break;
			}
		}
		return availability;
	}

	/**
	 * @param productInventoryShoppingService the productInventoryShoppingService to set
	 */
	public void setProductInventoryShoppingService(
			final ProductInventoryShoppingService productInventoryShoppingService) {
		this.productInventoryShoppingService = productInventoryShoppingService;
	}

	/**
	 * @return the productInventoryShoppingService
	 */
	public ProductInventoryShoppingService getProductInventoryShoppingService() {
		return productInventoryShoppingService;
	}

	/**
	 * @param shoppingItemDtoFactory the shoppingItemDtoFactory to set
	 */
	public void setShoppingItemDtoFactory(final ShoppingItemDtoFactory shoppingItemDtoFactory) {
		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
	}

	/**
	 * @return the shoppingItemDtoFactory
	 */
	public ShoppingItemDtoFactory getShoppingItemDtoFactory() {
		return shoppingItemDtoFactory;
	}

	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}

	protected List<AvailabilityStrategy> getAvailabilityStrategies() {
		return availabilityStrategies;
	}

	public void setAvailabilityStrategies(final List<AvailabilityStrategy> availabilityStrategies) {
		this.availabilityStrategies = availabilityStrategies;
	}

	protected ProductAvailabilityService getProductAvailabilityService() {
		return productAvailabilityService;
	}

	public void setProductAvailabilityService(final ProductAvailabilityService productAvailabilityService) {
		this.productAvailabilityService = productAvailabilityService;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}
}
