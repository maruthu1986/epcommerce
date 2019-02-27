/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.pricing.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.service.pricing.PriceAdjustmentService;
import com.elasticpath.service.pricing.dao.PriceAdjustmentDao;

/**
 * Implementation for service.
 */
public class PriceAdjustmentServiceImpl implements PriceAdjustmentService {
	private PriceAdjustmentDao dao;

	@Override
	public void delete(final PriceAdjustment priceAdjustment) throws EpServiceException {
		dao.delete(priceAdjustment);
	}
	
	/**
	 * @param dao the dao to use
	 */
	public void setPriceAdjustmentDao(final PriceAdjustmentDao dao) {
		this.dao = dao;
	}

	@Override
	@Deprecated
	public Collection<PriceAdjustment> findAllAdjustmentsOnBundle(final String plGuid, final Collection<String> bcList) {
		return dao.findByPriceListBundleConstituents(plGuid, bcList);
	}

	@Override
	public Map<String, PriceAdjustment> findByPriceListAndBundleConstituentsAsMap(final String priceListGuid, final Collection<String> bcList) {
		return dao.findByPriceListAndBundleConstituentsAsMap(priceListGuid, bcList);
	}
	

	
	@Override
	public List<PriceAdjustment> findByPriceList(final String plGuid) {
		return dao.findByPriceList(plGuid);
	}

}
