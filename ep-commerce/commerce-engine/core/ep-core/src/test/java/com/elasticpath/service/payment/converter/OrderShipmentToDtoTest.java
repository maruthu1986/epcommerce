/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.OrderSkuDto;
import com.elasticpath.plugin.payment.dto.impl.OrderShipmentDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.OrderSkuDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class OrderShipmentToDtoTest {
	private static final boolean PHYSICAL = true;
	private static final String EXTERNALORDERNUMBER = "externalOrderNumber";
	private static final String SHIPMENTNUMBER = "10000-1";
	private static final String CARRIER = "FedEx";
	private static final String TRACKINGCODE = "trackingCode";
	private static final String SHIPPING_OPTION_CODE = "shippingOptionCode";
	private static final BigDecimal SHIPPINGCOST = new BigDecimal("10.00");

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final OrderShipmentToDto orderShipmentToDto = new OrderShipmentToDto();
	private final OrderSku orderSku = new OrderSkuImpl();
	private final OrderSkuDto orderSkuDto = new OrderSkuDtoImpl();
	@Mock private ConversionService mockConversionService;

	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ORDER_SHIPMENT_DTO, OrderShipmentDtoImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CONVERSION_SERVICE, mockConversionService);
		orderShipmentToDto.setBeanFactory(beanFactory);
		context.checking(new Expectations() {
			{
				allowing(mockConversionService).convert(with(any(OrderSkuImpl.class)),
						with(same(OrderSkuDto.class)));
				will(returnValue(orderSkuDto));
			}
		});
	}

	@Test
	public void testConvert() throws Exception {
		Order order = new OrderImpl();
		order.setExternalOrderNumber(EXTERNALORDERNUMBER);
		PhysicalOrderShipment source = new PhysicalOrderShipmentImpl();
		source.setCarrierCode(CARRIER);
		source.setTrackingCode(TRACKINGCODE);
		source.setShippingOptionCode(SHIPPING_OPTION_CODE);
		source.setShippingCost(SHIPPINGCOST);
		source.setOrder(order);
		source.setShipmentNumber(SHIPMENTNUMBER);
		source.addShipmentOrderSku(orderSku);

		OrderShipmentDto target = orderShipmentToDto.convert(source);
		assertEquals(PHYSICAL, target.isPhysical());
		assertEquals(CARRIER, target.getCarrierCode());
		assertEquals(TRACKINGCODE, target.getTrackingCode());
		assertEquals(SHIPPING_OPTION_CODE, target.getShippingOptionCode());
		assertEquals(SHIPPINGCOST, target.getShippingCost());
		assertEquals(EXTERNALORDERNUMBER, target.getExternalOrderNumber());
		assertEquals(SHIPMENTNUMBER, target.getShipmentNumber());
		assertEquals(1, target.getOrderSkuDtos().size());
		assertEquals(orderSkuDto, target.getOrderSkuDtos().iterator().next());
	}
}
