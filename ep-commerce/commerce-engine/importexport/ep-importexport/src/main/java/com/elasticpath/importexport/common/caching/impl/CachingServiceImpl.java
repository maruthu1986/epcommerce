/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.common.caching.impl;

import java.util.List;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.persistence.dao.ProductTypeDao;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * The simple implementation of the <code>CachingService</code> interface.
 * <p>
 * This implementation does not provide any caching strategy, it only uses existent EP services for finding necessary entities.
 */
public class CachingServiceImpl implements CachingService {

	private CategoryLookup categoryLookup;

	private CatalogService catalogService;

	private ProductTypeDao productTypeDao;

	private TaxCodeService taxCodeService;

	private BrandService brandService;

	private AttributeService attributeService;

	private SkuOptionService skuOptionService;
	
	private ProductDao productDao;
	
	private ProductLookup productLookup;
	
	private CategoryTypeService categoryTypeService;
	
	private WarehouseService warehouseService;
	
	private StoreService storeService;
	private ProductSkuLookup productSkuLookup;

	@Override
	public Brand findBrandByCode(final String code) {
		return brandService.findByCode(code);
	}

	@Override
	public Product findProductByCode(final String code) {
		return productLookup.findByGuid(code);
	}

	@Override
	public ProductSku findSkuByCode(final String code) {
		return getProductSkuLookup().findBySkuCode(code);
	}

	/**
	 * Uses the product service to find a product by its code and load only the fields
	 * specified by the fetch group load tuner.
	 * 
	 * @param code the product code
	 * @param loadTuner the load tuner
	 * @return the product or null
	 */
	@Override
	public Product findProductByCode(final String code, final LoadTuner loadTuner) {
		return productDao.findByGuid(code, loadTuner);
	}
	
	/**
	 * Finds ProductBundle by given code.
	 * 
	 * @param code the code of product
	 * @return the obtained ProductBundle or null if product with given code does not exist
	 */
	@Override
	public ProductBundle findProductBundleByCode(final String code) {
		return productLookup.findByGuid(code);
	}
	
	/**
	 * Finds ProductBundle by given code loaded with the given load tuner.
	 * 
	 * @param code the code of product
	 * @param loadTuner the load tuner to use
	 * @return the obtained ProductBundle or null if product with given code does not exist
	 */
	@Override
	public ProductBundle findProductBundleByCode(final String code, final LoadTuner loadTuner) {
		return findProductBundleByCode(code);
	}

	@Override
	public Category findCategoryByCode(final String categoryCode, final String catalogCode) {
		return categoryLookup.findByCategoryAndCatalogCode(categoryCode, catalogCode);
	}

	@Override
	public ProductType findProductTypeByName(final String type) {
		return productTypeDao.findProductTypeWithAttributes(type);
	}

	@Override
	public TaxCode findTaxCodeByCode(final String code) {
		return taxCodeService.findByCode(code);
	}

	@Override
	public Catalog findCatalogByCode(final String catalogCode) {
		return catalogService.findByCode(catalogCode);
	}

	/**
	 * Finds a catalog by its code.
	 * 
	 * @param code the catalog code
	 * @param loadTuner the load tuner
	 * @return the catalog or null if not found
	 */
	@Override
	public Catalog findCatalogByCode(final String code, final LoadTuner loadTuner) {
		return catalogService.findByGuid(code, loadTuner);
	}


	@Override
	public Warehouse findWarehouseByCode(final String code) {
		return warehouseService.findByCode(code);
	}

	@Override
	public List<Warehouse> findAllWarehouses() {
		return warehouseService.findAllWarehouses();
	}

	@Override
	public Attribute findAttribiteByKey(final String key) {
		return attributeService.findByKey(key);
	}

	@Override
	public SkuOption findSkuOptionByKey(final String key) {
		return skuOptionService.findByKey(key);
	}
	
	@Override
	public boolean isSkuOptionValueInUse(final long uid) {
		return skuOptionService.isSkuOptionValueInUse(uid);
	}
	
	@Override
	public CategoryType findCategoryTypeByName(final String typeName) {
		return categoryTypeService.findCategoryType(typeName);
	}
	
	@Override
	public Store findStoreByCode(final String storeCode) {
		return storeService.findStoreWithCode(storeCode);
	}
	
	protected CategoryLookup getCategoryLookup() {
		return categoryLookup;
	}

	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}

	/**
	 * Sets the productTypeService.
	 * 
	 * @param productTypeDao the productTypeDao to set
	 */
	public void setProductTypeDao(final ProductTypeDao productTypeDao) {
		this.productTypeDao = productTypeDao;
	}

	/**
	 * Gets the taxCodeService.
	 * 
	 * @return the taxCodeService
	 */
	public TaxCodeService getTaxCodeService() {
		return taxCodeService;
	}

	/**
	 * Sets the taxCodeService.
	 * 
	 * @param taxCodeService the taxCodeService to set
	 */
	public void setTaxCodeService(final TaxCodeService taxCodeService) {
		this.taxCodeService = taxCodeService;
	}

	/**
	 * Gets the brandService.
	 * 
	 * @return the brandService
	 */
	public BrandService getBrandService() {
		return brandService;
	}

	/**
	 * Sets the brandService.
	 * 
	 * @param brandService the brandService to set
	 */
	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}

	/**
	 * Gets the catalogService.
	 * 
	 * @return catalogService the catalogService
	 */
	public CatalogService getCatalogService() {
		return catalogService;
	}

	/**
	 * Sets the catalogService.
	 * 
	 * @param catalogService the catalogService to set
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	/**
	 * Gets the attributeService.
	 * 
	 * @return the attributeService
	 */
	public AttributeService getAttributeService() {
		return attributeService;
	}

	/**
	 * Sets the attributeService.
	 * 
	 * @param attributeService the attributeService to set
	 */
	public void setAttributeService(final AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	/**
	 * Gets the skuOptionService.
	 * 
	 * @return the skuOptionService
	 */
	public SkuOptionService getSkuOptionService() {
		return skuOptionService;
	}

	/**
	 * Sets the skuOptionService.
	 * 
	 * @param skuOptionService the skuOptionService to set
	 */
	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}

	/**
	 * Gets the productService.
	 * 
	 * @return the productService
	 */
	public ProductDao getProductService() {
		return productDao;
	}

	/**
	 * Sets the product DAO.
	 * 
	 * @param productDao the productDao to set
	 */
	public void setProductDao(final ProductDao productDao) {
		this.productDao = productDao;
	}
	
	/**
	 * Gets the categoryTypeService.
	 * 
	 * @return the categoryTypeService
	 */
	public CategoryTypeService getCategoryTypeService() {
		return categoryTypeService;
	}

	/**
	 * Sets the categoryTypeService.
	 * 
	 * @param categoryTypeService the categoryTypeService to set
	 */
	public void setCategoryTypeService(final CategoryTypeService categoryTypeService) {
		this.categoryTypeService = categoryTypeService;
	}

	/**
	 * Gets warehouseService.
	 *
	 * @return the warehouseService
	 */
	public WarehouseService getWarehouseService() {
		return warehouseService;
	}

	/**
	 * Sets warehouseService.
	 *
	 * @param warehouseService the warehouseService to set
	 */
	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	/**
	 * Gets StoreService.
	 * 
	 * @return the storeService
	 */
	public final StoreService getStoreService() {
		return storeService;
	}

	/**
	 * Sets StoreService.
	 * 
	 * @param storeService the storeService 
	 */
	public final void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}
