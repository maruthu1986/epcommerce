/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import java.util.Currency;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;

/**
 * Converter from ShoppingCart to ShoppingCartDto.
 */
public class ShoppingCartToDto implements Converter<ShoppingCart, ShoppingCartDto> {
	private BeanFactory beanFactory;

	@Override
	public ShoppingCartDto convert(final ShoppingCart source) {
		ConversionService conversionService = beanFactory.getBean(ContextIdNames.CONVERSION_SERVICE);
		ShoppingCartDto target = beanFactory.getBean(ContextIdNames.SHOPPING_CART_DTO);
		PricingSnapshotService pricingSnapshotService = beanFactory.getBean(ContextIdNames.PRICING_SNAPSHOT_SERVICE);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(source);
		final Currency currency = source.getCustomerSession().getCurrency();
		target.setCurrencyCode(currency.getCurrencyCode());
		target.setTotalAmount(pricingSnapshot.getBeforeTaxTotal().getAmount());
		target.setRequiresShipping(source.requiresShipping());
		Address shippingAddress = source.getShippingAddress();
		if (shippingAddress != null) {
			target.setShippingAddress(conversionService.convert(shippingAddress, AddressDto.class));
		}
		return target;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
