/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.importexport.common.adapters.shipping;

import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_REGION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.shipping.region.ShippingRegionDTO;
import com.elasticpath.importexport.common.dto.shipping.region.ShippingSubRegionsDTO;

/**
 * Helper class for mediating data between ShippingRegion entitys and dtos.
 *
 */
public class ShippingRegionAdapter extends AbstractDomainAdapterImpl<ShippingRegion, ShippingRegionDTO>  {

	/**
	 * Populate a dto from an entity.
	 * @param source the entity
	 * @param target the dto
	 */
	@Override
	public void populateDTO(final ShippingRegion source, final ShippingRegionDTO target) {

		target.setGuid(source.getGuid());
		target.setName(source.getName());
		List<ShippingSubRegionsDTO> subRegions = new ArrayList<>();

		Map<String, Region> regionMap = source.getRegionMap();
		Set<String> keySet = regionMap.keySet();
		Iterator<String> iter = keySet.iterator();
		String key = null;
		while (iter.hasNext()) {
			key = iter.next();
			if (key != null) {
				ShippingSubRegionsDTO subRegionsDto = new ShippingSubRegionsDTO();
				Region region = regionMap.get(key);
				if (region != null) {
					subRegionsDto.setCountryCode(region.getCountryCode());
					subRegionsDto.setRegionCodes(region.getSubCountryCodeList());
				}
				subRegions.add(subRegionsDto);
			}

		}

		target.setShippingSubRegions(subRegions);
	}

	/**
	 * Populate an entity from a dto.
	 * @param source the dto
	 * @param target the entity
	 */
	@Override
	public void populateDomain(final ShippingRegionDTO source, final ShippingRegion target) {
		target.setGuid(source.getGuid());
		target.setName(source.getName());

		Map<String, Region> regionMap = new HashMap<>();

		List<ShippingSubRegionsDTO> subRegions = source.getShippingSubRegions();
		for (ShippingSubRegionsDTO subRegionsDto : subRegions) {
			Region region = getBeanFactory().getBean(ContextIdNames.REGION);
			region.setCountryCode(subRegionsDto.getCountryCode());
			region.setSubCountryCodeList(subRegionsDto.getRegionCodes());
			regionMap.put(subRegionsDto.getCountryCode(), region);
		}

		target.setRegionMap(regionMap);

	}

	@Override
	public ShippingRegionDTO createDtoObject() {
		return new ShippingRegionDTO();
	}

	@Override
	public ShippingRegion createDomainObject() {
		return getBeanFactory().getBean(SHIPPING_REGION);
	}
}
