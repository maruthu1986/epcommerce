/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.service.catalog.ProductBundleService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.target.impl.AbstractDaoAdapter;

/**
 * ProductBundleDao Adapter.
 */
public class ProductBundleDaoAdapterImpl extends AbstractDaoAdapter<ProductBundle> {
	
	private BeanFactory beanFactory;
	
	private ProductBundleService productBundleService;
	private ProductLookup productLookup;

	@Override
	public void add(final ProductBundle newPersistence)throws SyncToolRuntimeException {
		productBundleService.saveOrUpdate(newPersistence);
	}

	@Override
	public ProductBundle createBean(final ProductBundle bean) {
		return beanFactory.getBean(ContextIdNames.PRODUCT_BUNDLE);
	}

	@Override
	public ProductBundle get(final String guid) {
		return getProductLookup().findByGuid(guid);
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		ProductBundle foundByGuid = get(guid);
		if (foundByGuid == null) {
			return false;
		}
		productBundleService.removeProductTree(foundByGuid.getUidPk());
		return true;
	}

	@Override
	public ProductBundle update(final ProductBundle mergedPersistence)
			throws SyncToolRuntimeException {
		return (ProductBundle) productBundleService.saveOrUpdate(mergedPersistence);
	}

	/**
	 * Set product bundle service.
	 *
	 * @param productBundleService the product bundle service
	 */
	public void setProductBundleService(final ProductBundleService productBundleService) {
		this.productBundleService = productBundleService;
	}

	/**
	 * Set bean factory.
	 *
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}
}
