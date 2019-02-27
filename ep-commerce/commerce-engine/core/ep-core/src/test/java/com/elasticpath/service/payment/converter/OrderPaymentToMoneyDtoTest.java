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
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.dto.impl.MoneyDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class OrderPaymentToMoneyDtoTest {
	private static final BigDecimal AMOUNT = new BigDecimal("100.25");
	private static final String CURRENCYCODE = "USD";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private final OrderPaymentToMoneyDto orderPaymentToMoneyDto = new OrderPaymentToMoneyDto();

	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.MONEY_DTO, MoneyDtoImpl.class);
		orderPaymentToMoneyDto.setBeanFactory(beanFactory);
	}

	@Test
	public void testConvert() throws Exception {
		OrderPayment source = new OrderPaymentImpl();
		source.setAmount(AMOUNT);
		source.setCurrencyCode(CURRENCYCODE);

		MoneyDto target = orderPaymentToMoneyDto.convert(source);
		assertEquals(AMOUNT, target.getAmount());
		assertEquals(CURRENCYCODE, target.getCurrencyCode());
	}
}
