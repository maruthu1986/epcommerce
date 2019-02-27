/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalog.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductCharacteristicsImpl;
import com.elasticpath.service.catalog.ProductBundleService;
import com.elasticpath.service.catalog.ProductCharacteristicsService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.catalogview.ProductWrapper;

/**
 * Determine the characteristics of a product.
 */
@SuppressWarnings("PMD.GodClass")
public class ProductCharacteristicsServiceImpl implements ProductCharacteristicsService {

	private ProductBundleService productBundleService;
	private ProductLookup productLookup;
	private ProductTypeService productTypeService;
	
	@Override
	public ProductCharacteristics getProductCharacteristics(final Product product) {
		Product wrappedProduct = getWrappedProduct(product);
		ProductBundle bundle = asProductBundle(wrappedProduct);
		
		if (bundle == null) {
			return getCharacteristicsForNonBundle(wrappedProduct);
		} 
		
		return getCharacteristicsForBundle(bundle);
	}

	@Override
	public ProductCharacteristics getProductCharacteristics(final ProductSku productSku) {
		return getProductCharacteristics(productSku.getProduct());
	}

	@Override
	public ProductCharacteristics getProductCharacteristicsForSkuCode(final String skuCode) {
		String bundleCode = getProductBundleService().findBundleCodeBySkuCode(skuCode);
		if (bundleCode == null) {
			ProductType productType = getProductTypeService().findBySkuCode(skuCode);
			return getCharacteristicsForNonBundle(productType);
		}

		ProductBundle bundle = getProductLookup().findByGuid(bundleCode);
		return getCharacteristicsForBundle(bundle);
	}

	/**
	 * Gets the characteristics for bundle.
	 *
	 * @param bundle the bundle
	 * @return the characteristics for bundle
	 */
	protected ProductCharacteristics getCharacteristicsForBundle(final ProductBundle bundle) {
		ProductCharacteristicsImpl productCharacteristics = createProductCharacteristics();
		productCharacteristics.setBundleUid(bundle.getUidPk());
		productCharacteristics.setBundle(true);
		productCharacteristics.setCalculatedBundle(bundle.isCalculated());
		productCharacteristics.setDynamicBundle(isDynamicBundle(bundle));
		productCharacteristics.setIsMultiSku(hasMultipleSkus(bundle));
		productCharacteristics.setRequiresSelection(productCharacteristics.isDynamicBundle() || offerConstituentRequiresSelection(bundle));
		
		return productCharacteristics;
	}

	/**
	 * Checks for selection rule greater than zero.
	 *
	 * @param bundle the bundle
	 * @return true, if successful
	 */
	private boolean hasSelectionRuleGreaterThanZero(final ProductBundle bundle) {
		return bundle.getSelectionRule() != null && bundle.getSelectionRule().getParameter() > 0;
	}

	/**
	 * Checks for dynamic bundle.
	 *
	 * @param constituents the constituents
	 * @return true, if successful
	 */
	private boolean hasDynamicBundle(final List<BundleConstituent> constituents) {
		boolean isDynamic = false;

		for (BundleConstituent bundleConstituent : constituents) {
			ConstituentItem bundleItem = bundleConstituent.getConstituent();
			if (bundleItem.isBundle()) {
				isDynamic |= isDynamicBundle(asProductBundle(bundleItem.getProduct())); 
			}
		}

		return isDynamic;
	}
	
	/**
	 * Gets the characteristics for non bundle.
	 *
	 * @param product the product
	 * @return the characteristics for non bundle
	 */
	protected ProductCharacteristics getCharacteristicsForNonBundle(final Product product) {
		return getCharacteristicsForNonBundle(product.getProductType());
	}

	/**
	 * Gets the characteristics for non bundle.
	 *
	 * @param productType the product type
	 * @return the characteristics for non bundle
	 */
	protected ProductCharacteristics getCharacteristicsForNonBundle(final ProductType productType) {
		ProductCharacteristicsImpl productCharacteristics = createProductCharacteristics();
		productCharacteristics.setBundle(false);
		productCharacteristics.setCalculatedBundle(false);
		productCharacteristics.setDynamicBundle(false);
		productCharacteristics.setIsMultiSku(productType.isMultiSku());
		productCharacteristics.setRequiresSelection(productCharacteristics.hasMultipleSkus() || productCharacteristics.isDynamicBundle());
		return productCharacteristics;
	}

	@Override
	public boolean isConfigurable(final Product product) {
		if (isBundle(product)) {
			return bundleContainsConfigurableConstituent((ProductBundle) product);
		}
		return product.getProductType() != null && product.getProductType().isConfigurable();
	}

	/**
	 * Checks if a bundle contains a configurable constituent.
	 *
	 * @param bundle the containing ProductBundle
	 * @return true if the bundle contains a constituent that is configurable
	 */
	protected boolean bundleContainsConfigurableConstituent(final ProductBundle bundle) {
		for (BundleConstituent constituent: bundle.getConstituents()) {
			ConstituentItem constituentItem = constituent.getConstituent();
			if (constituentItem.isProduct() && isConfigurable(constituentItem.getProduct())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates the product characteristics.
	 *
	 * @return the product characteristics impl
	 */
	protected ProductCharacteristicsImpl createProductCharacteristics() {
		return new ProductCharacteristicsImpl();
	}

	/**
	 * Get the original product instance. If the product is a product wrapper, e.g. an IndexProduct, 
	 * this method returns the wrapped product.
	 * @param product the product
	 * @return the inner-most product in a product wrapper
	 */
	protected Product getWrappedProduct(final Product product) {
		Product wrappedProduct = product;
		while (wrappedProduct instanceof ProductWrapper) {
			wrappedProduct = ((ProductWrapper) wrappedProduct).getWrappedProduct();
		}
		return wrappedProduct;
	}
	
	/**
	 * Checks if the product is a bundle.
	 *
	 * @param product the product
	 * @return true, if it is a bundle
	 */
	protected boolean isBundle(final Product product) {
		Product wrappedProduct = getWrappedProduct(product);
		return asProductBundle(wrappedProduct) != null;
	}
	
	/**
	 * Checks if the product is a dynamic bundle.
	 *
	 * @param bundle the bundle
	 * @return true, if it is a dynamic bundle
	 */
	protected boolean isDynamicBundle(final ProductBundle bundle) {
		return hasSelectionRuleGreaterThanZero(bundle) || hasDynamicBundle(bundle.getConstituents());
	}
	
	/**
	 * Casts the product to productBundle.
	 * @param product the product
	 * @return <code>null</code> if the product is not a bundle, the ProductBundle otherwise.
	 */
	protected ProductBundle asProductBundle(final Product product) {
		if (product instanceof ProductBundle) {
			return (ProductBundle) product;
		}
		return null;
	}
	
	@Override
	public boolean offerRequiresSelection(final Product product) {
		if (isBundle(product)) {
			ProductBundle bundle = asProductBundle(product);
			return isDynamicBundle(bundle) || offerConstituentRequiresSelection(bundle);
		}
		return product.getProductType() != null && hasMultipleSkus(product);
	}

	/**
	 * Checks whether the bundle contains any product requiring selection.
	 *
	 * @param bundle the bundle to be checked
	 * @return <code>true</code> iff there is at least one constituent which requires selection.
	 */
	protected boolean offerConstituentRequiresSelection(final ProductBundle bundle) {
		for (BundleConstituent constituent : bundle.getConstituents()) {
			ConstituentItem constituentItem = constituent.getConstituent();
			if (constituentItem.isProduct() && offerRequiresSelection(constituentItem.getProduct())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasMultipleSkus(final Product product) {
		if (isBundle(product)) {
			return hasMultipleSkusInBundle((ProductBundle) product);
		}
		return product.getProductType().isMultiSku();
	}

	/**
	 * Checks whether the bundle contains a multi-sku product.
	 *
	 * @param bundle the bundle to be checked
	 * @return <code>true</code> iff there is at least one constituent which has multiple skus.
	 */
	private boolean hasMultipleSkusInBundle(final ProductBundle bundle) {
		for (BundleConstituent constituent : bundle.getConstituents()) {
			ConstituentItem constituentItem = constituent.getConstituent();
			if (hasMultipleSkus(constituentItem.getProduct())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, ProductCharacteristics> getProductCharacteristicsMap(final Collection<? extends Product> products) {
		Map<String, ProductCharacteristics> characteristics = new HashMap<>();
		if (products != null) {
			for (Product product : products) {
				characteristics.put(product.getCode(), getProductCharacteristics(product));
			}
		}
		return characteristics;
	}

	public void setProductBundleService(final ProductBundleService productBundleService) {
		this.productBundleService = productBundleService;
	}

	protected ProductBundleService getProductBundleService() {
		return productBundleService;
	}

	public void setProductTypeService(final ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}

	protected ProductTypeService getProductTypeService() {
		return productTypeService;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}
}
