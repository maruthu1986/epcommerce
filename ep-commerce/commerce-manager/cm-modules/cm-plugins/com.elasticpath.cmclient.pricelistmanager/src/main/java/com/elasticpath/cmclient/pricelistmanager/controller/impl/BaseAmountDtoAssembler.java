/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.controller.impl;

import java.math.BigDecimal;
import java.util.Locale;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.BaseAmountDTOCreator;
import com.elasticpath.cmclient.pricelistmanager.model.impl.BaseAmountType;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.RandomGuid;

/**
 * Class contains static methods used to assemble {@link BaseAmountDTO} out of different persistent objects. 
 */
public final class BaseAmountDtoAssembler {

	private BaseAmountDtoAssembler() {
		// prevent instantiation
	}
	
	/**
	 * Assembles base amount dto out of the SKU. 
	 *
	 * @param source - product SKU
	 * @param quantity - quantity
	 * @param priceListDescriptorDTO - price list descriptor
	 * @param locale locale
	 * @return new base amount DTO
	 */
	public static BaseAmountDTO assembleFromSku(final ProductSku source, final BigDecimal quantity, 
				final PriceListDescriptorDTO priceListDescriptorDTO, final Locale locale) {
		final BaseAmountDTO target = BaseAmountDTOCreator.createModel();
		RandomGuid randomGuid = ServiceLocator.getService(ContextIdNames.RANDOM_GUID);
		target.setGuid(randomGuid.toString());
		target.setObjectGuid(source.getSkuCode());
		target.setObjectType(BaseAmountType.SKU.getType());
		target.setQuantity(quantity);
		target.setSkuCode(source.getSkuCode());
		target.setProductCode(source.getProduct().getCode());
		String skuConfiguration = PriceListEditorControllerHelper.formatSkuConfiguration(source.getOptionValues(), locale);
		target.setSkuConfiguration(skuConfiguration);
		target.setPriceListDescriptorGuid(priceListDescriptorDTO.getGuid());
		return target;
	}
	
	

	/**
	 * Assembles base amount dto out of the PRODUCT. 
	 *
	 * @param source - product SKU
	 * @param quantity - quantity
	 * @param priceListDescriptorDTO - price list descriptor
	 * @return new base amount DTO
	 */	
	public static BaseAmountDTO assembleFromProduct(final Product source, final BigDecimal quantity, 
						final PriceListDescriptorDTO priceListDescriptorDTO) {
		final BaseAmountDTO target = BaseAmountDTOCreator.createModel();
		RandomGuid randomGuid = ServiceLocator.getService(ContextIdNames.RANDOM_GUID);
		target.setGuid(randomGuid.toString());
		target.setObjectGuid(source.getGuid());
		target.setObjectType(BaseAmountType.PRODUCT.getType());
		if (source.getProductType().isMultiSku()) {
			target.setMultiSku(true);
		} 
		target.setQuantity(quantity);
		target.setSkuCode(source.getDefaultSku().getSkuCode());
		target.setProductCode(source.getCode());
		target.setSkuConfiguration(null);
		target.setPriceListDescriptorGuid(priceListDescriptorDTO.getGuid());
		return target;
	}
}
