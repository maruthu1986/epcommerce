/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.domain.pricing.BaseAmountObjectType;

/**
 * 
 * UI viewer filter based on {@link BaseAmountFilterExt} 
 * for Base amount table.
 *
 */
public class BaseAmountViewerFilter extends ViewerFilter {
	
	private final BaseAmountFilterExt baseAmountFilterExt;

	/**
	 * Constructor.
	 * @param baseAmountFilterExt base amount filter to reduce result set on UI. 
	 */
	public BaseAmountViewerFilter(final BaseAmountFilterExt baseAmountFilterExt) {
		super();
		this.baseAmountFilterExt = baseAmountFilterExt;
	}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
		
		if (element == null) {
			return false;
		}
		
		if (baseAmountFilterExt == null) {
			return true;
		}
		
		BaseAmountDTO baseAmountDTO = (BaseAmountDTO) element;
		
		return typeIncluded(baseAmountDTO) 
				&& codeIncluded(baseAmountDTO) 
				&& quantityIncluded(baseAmountDTO) 
				&& priceIncluded(baseAmountDTO);
	}

	private boolean quantityIncluded(final BaseAmountDTO baseAmountDTO) {
		return baseAmountFilterExt.getQuantity() == null 
			|| (baseAmountDTO.getQuantity() != null && baseAmountFilterExt.getQuantity().compareTo(baseAmountDTO.getQuantity()) == 0);
	}

	private boolean codeIncluded(final BaseAmountDTO baseAmountDTO) {
		String objectType = baseAmountFilterExt.getObjectType();
		String objectGuid = baseAmountFilterExt.getObjectGuid();
		
		if (StringUtils.isBlank(objectType)) {
			return isContainedInAny(objectGuid, baseAmountDTO.getProductCode(), baseAmountDTO.getSkuCode());
		} else if (BaseAmountObjectType.PRODUCT.toString().equals(objectType)) {
			return isContainedInAny(objectGuid, baseAmountDTO.getProductCode());
		} else if (BaseAmountObjectType.SKU.toString().equals(objectType)) {
			return isContainedInAny(objectGuid, baseAmountDTO.getSkuCode());
		}
		return false;
	}

	private boolean isContainedInAny(final String searchString, final String ... stringsToSearch) {
		if (StringUtils.isBlank(searchString)) {
			return true;
		}
		for (String string : stringsToSearch) {
			if (StringUtils.containsIgnoreCase(string, searchString)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean typeIncluded(final BaseAmountDTO baseAmountDTO) {
		if (StringUtils.isBlank(baseAmountFilterExt.getObjectGuid())) {
			return StringUtils.isBlank(baseAmountFilterExt.getObjectType()) 
			|| StringUtils.equals(baseAmountFilterExt.getObjectType(), baseAmountDTO.getObjectType());			
		}
		return true;
	}

	private boolean priceIncluded(final BaseAmountDTO baseAmountDTO) {
		BigDecimal lowestPrice = baseAmountFilterExt.getLowestPrice();
		BigDecimal highestPrice = baseAmountFilterExt.getHighestPrice();
		
		if (lowestPrice == null && highestPrice == null) {
			return true;
		}
		
		if (baseAmountDTO.getListValue() == null && baseAmountDTO.getSaleValue() == null) {
			return false;
		}
		
		return isPriceInRange(baseAmountDTO.getListValue(), lowestPrice, highestPrice)
			|| isPriceInRange(baseAmountDTO.getSaleValue(), lowestPrice, highestPrice);
	}
	
	private boolean isPriceInRange(final BigDecimal price, final BigDecimal lowestPrice,
			final BigDecimal highestPrice) {
		if (price == null) {
			return false;
		}
		if (lowestPrice == null) {
			return price.compareTo(highestPrice) <= 0;
		}
		
		if (highestPrice == null) {
			return price.compareTo(lowestPrice) >= 0;
		}
		
		return price.compareTo(lowestPrice) >= 0 && price.compareTo(highestPrice) <= 0;
	}
	
	

}
