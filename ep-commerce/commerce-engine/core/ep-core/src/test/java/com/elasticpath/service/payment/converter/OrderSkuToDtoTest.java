/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.plugin.payment.dto.OrderSkuDto;
import com.elasticpath.plugin.payment.dto.impl.OrderSkuDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class OrderSkuToDtoTest {
	private static final String SKUCODE = "skuCode";
	private static final BigDecimal UNITPRICE = new BigDecimal("10.00");
	private static final String DISPLAYNAME = "displayName";
	private static final int QUANTITY = 2;
	private static final BigDecimal TAXAMOUNT = new BigDecimal("2.25");

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final OrderSkuToDto orderSkuToDto = new OrderSkuToDto();

	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ORDER_SKU_DTO, OrderSkuDtoImpl.class);
		orderSkuToDto.setBeanFactory(beanFactory);
	}

	@Test
	public void testConvert() throws Exception {
		OrderSku source = new OrderSkuImpl();
		source.setSkuCode(SKUCODE);
		source.setUnitPrice(UNITPRICE);
		source.setDisplayName(DISPLAYNAME);
		source.setQuantity(QUANTITY);
		source.setTaxAmount(TAXAMOUNT);

		OrderSkuDto target = orderSkuToDto.convert(source);
		assertEquals(SKUCODE, target.getSkuCode());
		assertEquals(UNITPRICE, target.getUnitPrice());
		assertEquals(DISPLAYNAME, target.getDisplayName());
		assertEquals(QUANTITY, target.getQuantity());
		assertEquals(TAXAMOUNT, target.getTaxAmount());
	}
}
