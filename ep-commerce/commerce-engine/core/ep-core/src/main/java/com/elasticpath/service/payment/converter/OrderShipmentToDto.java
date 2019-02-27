/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import java.util.HashSet;
import java.util.Set;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.OrderSkuDto;

/**
 * Converter from OrderShipment to OrderShipmentDto.
 */
public class OrderShipmentToDto implements Converter<OrderShipment, OrderShipmentDto> {
	private BeanFactory beanFactory;

	@Override
	public OrderShipmentDto convert(final OrderShipment source) {
		ConversionService conversionService = beanFactory.getBean(ContextIdNames.CONVERSION_SERVICE);
		OrderShipmentDto target = beanFactory.getBean(ContextIdNames.ORDER_SHIPMENT_DTO);
		if (source instanceof PhysicalOrderShipment) {
			PhysicalOrderShipment physicalOrderShipment = (PhysicalOrderShipment) source;

			if (ShipmentType.PHYSICAL.equals(physicalOrderShipment.getOrderShipmentType())) {
				target.setPhysical(true);
			}
			target.setCarrierCode(physicalOrderShipment.getCarrierCode());
			target.setTrackingCode(physicalOrderShipment.getTrackingCode());
			target.setShippingOptionCode(physicalOrderShipment.getShippingOptionCode());
			target.setShippingCost(physicalOrderShipment.getShippingCost());
			target.setShippingTax(physicalOrderShipment.getShippingTax());
		}

		target.setShipmentNumber(source.getShipmentNumber());
		target.setExternalOrderNumber(source.getOrder().getExternalOrderNumber());
		if (source.getShipmentOrderSkus() != null) {
			Set<OrderSkuDto> orderSkuDtos = new HashSet<>();
			for (final OrderSku orderSku : source.getShipmentOrderSkus()) {
				orderSkuDtos.add(conversionService.convert(orderSku, OrderSkuDto.class));
			}
			target.setOrderSkuDtos(orderSkuDtos);
		}
		return target;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
