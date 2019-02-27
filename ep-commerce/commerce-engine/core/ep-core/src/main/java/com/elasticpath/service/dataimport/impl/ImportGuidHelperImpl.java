/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.dataimport.impl;

import java.util.List;
import javax.persistence.EntityManager;

import org.apache.openjpa.persistence.FetchPlan;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.CategoryGuidUtil;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.openjpa.JpaPersistenceSession;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.dataimport.ImportGuidHelper;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * This helper service provides the ability to load various entity by the given guid.
 */
@SuppressWarnings("PMD.GodClass")
public class ImportGuidHelperImpl extends AbstractEpPersistenceServiceImpl implements ImportGuidHelper {

	private static final String DUPLICATE_GUID_ERR_MSG = "Inconsistent data -- duplicate guid:";
	
	private CategoryService categoryService;
	private CategoryGuidUtil categoryGuidUtil;
	private CategoryLookup categoryLookup;

	/**
	 * Retrieve the category with the given guid.
	 *
	 * @param guid               the guid of the category
	 * @param catalogGuid        the catalog UID
	 * @return the category with the given guid
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public Category findCategoryByGuidAndCatalogGuid(
			final String guid, final String catalogGuid) throws EpServiceException {

		return getCategoryLookup().findByCategoryAndCatalogCode(guid, catalogGuid);
	}

	/**
	 * Return <code>true</code> if the given category guid exist for the specified catalog with catalogUid.
	 *
	 * @param guid        the guid of the category
	 * @param catalogGuid the catalog UID
	 * @return <code>true</code> if the given category guid exist
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public boolean isCategoryGuidExist(final String guid, final String catalogGuid) throws EpServiceException {
		final String compoundCategoryGuid = getCategoryGuidUtil().get(guid, catalogGuid);
		return getCategoryService().categoryExistsWithCompoundGuid(compoundCategoryGuid);
	}

	/**
	 * Retrieve the product with the given guid.
	 *
	 * @param guid the guid of the product
	 * @param flagLoadCategories set it to <code>true</code> to load categories
	 * @param flagLoadAttributes set it to <code>true</code> to load attributes values
	 * @param flagLoadSkus set it to <code>true</code> to load product SKUs
	 * @return the product with the given guid
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public Product findProductByGuid(final String guid, final boolean flagLoadCategories,
									 final boolean flagLoadAttributes, final boolean flagLoadSkus) throws EpServiceException {

		if (flagLoadCategories) {
			addFieldToFetchPlan(ProductImpl.class, "productCategories");
		}

		if (flagLoadAttributes) {
			addFieldToFetchPlan(ProductImpl.class, "attributeValueMap");
		}

		if (flagLoadSkus) {
			addFieldToFetchPlan(ProductImpl.class, "productSkusInternal");
		}

		final List<Product> products = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SELECT_BY_GUID", guid);
		
		clearFetchPlan();

		if (products.isEmpty()) {
			return null;
		}
		if (products.size() > 1) {
			throw new EpServiceException(DUPLICATE_GUID_ERR_MSG + guid);
		}

		return products.get(0);
	}

	/**
	 * Return <code>true</code> if the given product guid exist.
	 *
	 * @param guid the guid of the product
	 * @return <code>true</code> if the given product guid exist
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public boolean isProductGuidExist(final String guid) throws EpServiceException {
		final List<String> products = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_GUID_SELECT_BY_GUID", guid);
		final int size = products.size();
		if (size > 1) {
			throw new EpServiceException(DUPLICATE_GUID_ERR_MSG + guid);
		}
		return size != 0;
	}

	/**
	 * Not used.
	 *
	 * @param uid not used.
	 * @return nothing.
	 * @throws EpServiceException - in case of called
	 */
	@Override
	public Object getObject(final long uid) throws EpServiceException {
		throw new EpServiceException("Should not be called.");
	}

	/**
	 * Delete all product associations from the specified product in the 
	 * specified catalog. 
	 * 
	 * @param sourceProductCode the unique code for the product whose
	 *        associations are to be removed.
	 * @param catalogCode the code for the catalog containing the product whose
	 *        associations are to be removed.
	 */
	@Override
	public void deleteProductAssociations(final String sourceProductCode, final String catalogCode) {
		sanityCheck();
		List<Long> uidsToDelete = getPersistenceEngine().retrieveByNamedQuery("PRODUCTASSOCIATION_UIDS_BY_SOURCE_PRODUCT_CODE_AND_CATALOG",
				sourceProductCode, catalogCode);
		if (!uidsToDelete.isEmpty()) {
			getPersistenceEngine().executeNamedQueryWithList("DELETE_PRODUCT_ASSOCIATIONS_BY_UID", "list", uidsToDelete);
		}
	}
	
	/**
	 * Retrieve the product sku with the given guid.
	 *
	 * @param guid the guid of the product sku
	 * @return the product sku with the given guid
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public ProductSku findProductSkuByGuid(final String guid) throws EpServiceException {
		final List<ProductSku> productskus = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SKU_SELECT_BY_GUID", guid);
		if (productskus.isEmpty()) {
			return null;
		}
		if (productskus.size() > 1) {
			throw new EpServiceException(DUPLICATE_GUID_ERR_MSG + guid);
		}

		return productskus.get(0);
	}
	
	/**
	 * Return <code>true</code> if the given productsku guid exist.
	 *
	 * @param guid the guid of the productsku
	 * @return <code>true</code> if the given productsku guid exist
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public boolean isProductSkuGuidExist(final String guid) throws EpServiceException {
		final List<String> productskus = getPersistenceEngine().retrieveByNamedQuery("PRODUCT_SKU_GUID_SELECT_BY_GUID", guid);
		final int size = productskus.size();
		if (size > 1) {
			throw new EpServiceException(DUPLICATE_GUID_ERR_MSG + guid);
		}
		return size != 0;
	}

	/**
	 * Retrieve the brand with the given guid.
	 *
	 * @param guid the guid of the brand
	 * @param catalogGuid the catalog UID
	 * @return the brand with the given guid
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public Brand findBrandByGuidAndCatalogGuid(final String guid, final String catalogGuid) throws EpServiceException {
		final List<Brand> brands = getPersistenceEngine().retrieveByNamedQuery("BRAND_SELECT_BY_GUID_AND_CATALOG_GUID", guid, catalogGuid);
		if (brands.isEmpty()) {
			return null;
		}
		if (brands.size() > 1) {
			throw new EpServiceException(DUPLICATE_GUID_ERR_MSG + guid);
		}
		return brands.get(0);
	}

	/**
	 * Return <code>true</code> if the given brand guid exist.
	 *
	 * @param guid the guid of the brand
	 * @param catalogGuid the catalog UID
	 * @return <code>true</code> if the given brand guid exist
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public boolean isBrandGuidExist(final String guid, final String catalogGuid) {
		final List<String> brands = getPersistenceEngine().retrieveByNamedQuery("BRAND_SELECT_BY_GUID_AND_CATALOG_GUID", guid, catalogGuid);
		final int size = brands.size();
		if (size > 1) {
			throw new EpServiceException(DUPLICATE_GUID_ERR_MSG + guid);
		}
		return size != 0;
	}

	/**
	 * Retrieve the customer with the given guid.
	 * @param guid the guid of the Customer
	 * @return the <code>Customer</code> with the given guid
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public Customer findCustomerByGuid(final String guid) throws EpServiceException {
		final List<Customer> customers = getPersistenceEngine().retrieveByNamedQuery("CUSTOMER_FIND_BY_GUID", guid);
		if (customers.isEmpty()) {
			return null;
		}
		if (customers.size() > 1) {
			throw new EpServiceException(DUPLICATE_GUID_ERR_MSG + guid);
		}

		return customers.get(0);
	}

	/**
	 * Return <code>true</code> if the given Customer guid exists.
	 * @param guid the guid of the Customer
	 * @return <code>true</code> if the given Customer guid exists
	 * @throws EpServiceException in case of any error
	 */
	@Override
	public boolean isCustomerGuidExist(final String guid) throws EpServiceException {
		final List<String> customerGuids = getPersistenceEngine().retrieveByNamedQuery("CUSTOMER_GUID_SELECT_BY_GUID", guid);
		final int size = customerGuids.size();
		if (size > 1) {
			throw new EpServiceException(DUPLICATE_GUID_ERR_MSG + guid);
		}
		return size != 0;
	}

	/**
	 * Retrieve the <code>SkuOptionValue</code> with the given key.
	 *
	 * @param key the key of the SKU option value
	 * @return the <code>SkuOptionValue</code>
	 */
	@Override
	public SkuOptionValue findSkuOptionValueByKey(final String key) {
		final List<SkuOptionValue> optionValues = getPersistenceEngine().retrieveByNamedQuery("SKU_OPTION_VALUE_FIND_BY_KEY", key);
		if (optionValues.isEmpty()) {
			return null;
		}
		if (optionValues.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate key: " + key);
		}
		return optionValues.get(0);
	}

	/**
	 * Get the fetch plan.
	 */
	private FetchPlan getFetchPlan() {
		EntityManager entityManager;
		try {
			entityManager = ((JpaPersistenceSession) getPersistenceEngine().getSharedPersistenceSession()).getEntityManager();
			return OpenJPAPersistence.cast(entityManager).getFetchPlan();
		} catch (ClassCastException ex) {
			return null;
		}
	}

	/**
	 * Add a single field to the fetch plan.
	 * @param clazz class of the object on which to request the specified fields
	 * @param fieldToLoad the fields to load in requested objects
	 */
	public void addFieldToFetchPlan(final Class<?> clazz, final String fieldToLoad) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null) {
			return;
		}
		fetchPlan.addField(clazz, fieldToLoad);
	}

	/**
	 * Clear the fetch plan configuration.
	 */
	private void clearFetchPlan() {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null) {
			return;
		}
		fetchPlan.clearFields();
	}
	
	/**
	 * Returns a <code>List</code> of Category objects linked to the Category indicated by the given <code>masterCategoryUid</code>.
	 * 
	 * @param masterCategoryUid the master category uid to look up
	 * @return a <code>List</code> of all UIDs of all Category objects linked to the Category indicated by the given <code>masterCategoryUid</code>
	 */
	@Override
	public List<Category> findLinkedCategories(final long masterCategoryUid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("LINKED_CATEGORY_SELECT_BY_MASTER_CATEGORY_UID", masterCategoryUid);
	}

	/**
	 * Return true if the given tax code exists.
	 * 
	 * @param taxCode the tax code to check
	 * @return true if the tax code exists
	 */
	@Override
	public boolean isTaxCodeExist(final String taxCode) {
		List<TaxCode> result = getPersistenceEngine().retrieveByNamedQuery("TAXCODE_FIND_BY_CODE", taxCode);
		return !result.isEmpty();
	}

	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	protected CategoryService getCategoryService() {
		return this.categoryService;
	}

	protected CategoryGuidUtil getCategoryGuidUtil() {
		return categoryGuidUtil;
	}

	public void setCategoryGuidUtil(final CategoryGuidUtil categoryGuidUtil) {
		this.categoryGuidUtil = categoryGuidUtil;
	}

	protected CategoryLookup getCategoryLookup() {
		return categoryLookup;
	}

	public void setCategoryLookup(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}
}
