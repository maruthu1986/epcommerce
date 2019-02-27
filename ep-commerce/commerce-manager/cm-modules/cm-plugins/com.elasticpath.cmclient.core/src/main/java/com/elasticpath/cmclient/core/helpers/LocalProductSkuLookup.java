/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * A "Local" implementation of {@link ProductSkuLookup} that delegates to a remoted ProductSkuLookup
 * via Spring-http and caches results locally.
 * 
 * This class is NOT thread-safe, and not particularly heap friendly either.  The purpose of this
 * class is to provide a local cache for individual SWT components - not to provide a global cache for all of
 * cmclient.
 */
public class LocalProductSkuLookup implements ProductSkuLookup {

	private static final String NOT_YET_IMPLEMENTED = "Not yet implemented";  //$NON-NLS-1$
	
	private final Map<String, ProductSku> productSkusByGuid = new HashMap<String, ProductSku>();
	
	@Override
	@SuppressWarnings("unchecked")
	public <P extends ProductSku> P findByGuid(final String guid) throws EpServiceException {
		if (!productSkusByGuid.containsKey(guid)) {
			ProductSku sku = getRemoteProductSkuLookup().findByGuid(guid);
			productSkusByGuid.put(guid, sku);
		}
		
		return (P) productSkusByGuid.get(guid);
	}

	@Override
	public <P extends ProductSku> List<P> findByGuids(final Collection<String> guids) throws EpServiceException {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);
	}

	@Override
	public <P extends ProductSku> P findBySkuCode(final String skuCode) throws EpServiceException {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);  
	}

	@Override
	public <P extends ProductSku> List<P> findBySkuCodes(final Collection<String> skuCodes) throws EpServiceException {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);  
	}

	@Override
	public <P extends ProductSku> P findByUid(final long uidPk) throws EpServiceException {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);  
	}

	@Override
	public <P extends ProductSku> List<P> findByUids(final Collection<Long> uidPks) throws EpServiceException {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);  
	}

	@Override
	public Boolean isProductSkuExist(final String skuCode) {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);
	}

	protected ProductSkuLookup getRemoteProductSkuLookup() {
		return ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_LOOKUP);
	}
}
