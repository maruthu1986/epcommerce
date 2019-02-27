/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalog;

import java.util.List;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.service.EpPersistenceService;


/**
 * Provide attribute related business service.
 */
public interface ProductTypeService extends EpPersistenceService {

	/**
	 * Adds the given attribute.
	 *
	 * @param productType the productType to add
	 * @return the persisted instance of productType
	 * @throws DuplicateKeyException - if a productType with the speicifed key already exists.
	 */
	ProductType add(ProductType productType) throws DuplicateKeyException;

	/**
	 * Updates the given productType.
	 *
	 * @param productType the productType to update
	 * @return the updated product type
	 * @throws DuplicateKeyException - if a productType with the speicifed key already exists.
	 */
	ProductType update(ProductType productType) throws DuplicateKeyException;

	/**
	 * Delete the productType.
	 *
	 * @param productType the productType to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(ProductType productType) throws EpServiceException;

	/**
	 * Lists all productType stored in the database.
	 *
	 * @return a list of productType
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	List<ProductType> list() throws EpServiceException;

	/**
	 * Finds all the {@link ProductType}s for the specified catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link ProductType}s
	 * @throws EpServiceException in case of any errors
	 */
	List<ProductType> findAllProductTypeFromCatalog(long catalogUid) throws EpServiceException;

	/**
	 * Lists all productType uids used by categories.
	 * @return a list of used productType uids
	 */
	List<Long> listUsedUids();

	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 */
	boolean isInUse(long uidToCheck) throws EpServiceException;

	/**
	 * Initialize (fill in) category attributes for the given <code>ProductType</code>.
	 * @param productType productType that needs attributes filled in.
	 * @return a productType with the attributeGroup filled in.
	 */
	ProductType initialize(ProductType productType);

	/**
	 * Finds productType for given name.
	 * @param name product type name.
	 * @return product type
	 */
	ProductType findProductType(String name);


	/**
	 * Finds a productType for a given guid.
	 *
	 * @param guid the guid
	 * @return the productType
	 */
	ProductType findByGuid(String guid);

	/**
	 * Find a productType for the given sku code.
	 *
	 * @param skuCode the sku code
	 * @return the product type
	 */
	ProductType findBySkuCode(String skuCode);

}