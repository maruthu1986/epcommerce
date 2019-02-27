/*
  Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;

import org.springframework.core.convert.ConversionService;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.PayerAuthValidationValue;
import com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.impl.OrderPaymentDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthValidationValueDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class DtoToOrderPaymentTest {
	private static final BigDecimal AMOUNT = new BigDecimal("100.25");
	private static final String REFERENCEID = "referenceId";
	private static final String REQUESTTOKEN = "requestToken";
	private static final String AUTHORIZATIONCODE = "authCode";
	private static final String CURRENCYCODE = "USD";
	private static final String EMAIL = "john.doe@elasticpath.com";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final DtoToOrderPayment dtoToOrderPayment = new DtoToOrderPayment();
	private final PayerAuthValidationValue payerAuthValidationValue = new PayerAuthValidationValueImpl();
	@Mock private ConversionService mockConversionService;

	/**
	 * Set up and mocking.
	 */
	@Before
	public void setUp() {
		BeanFactory beanFactory = context.mock(BeanFactory.class);
		BeanFactoryExpectationsFactory expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CONVERSION_SERVICE, mockConversionService);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ORDER_PAYMENT, OrderPaymentImpl.class);
		dtoToOrderPayment.setBeanFactory(beanFactory);
		context.checking(new Expectations() {
			{
				allowing(mockConversionService).convert(with(any(PayerAuthValidationValueDtoImpl.class)),
						with(same(PayerAuthValidationValue.class)));
				will(returnValue(payerAuthValidationValue));
			}
		});
	}

	@Test
	public void testConvert() throws Exception {
		OrderPaymentDto source = new OrderPaymentDtoImpl();
		source.setAmount(AMOUNT);
		source.setReferenceId(REFERENCEID);
		source.setRequestToken(REQUESTTOKEN);
		source.setAuthorizationCode(AUTHORIZATIONCODE);
		source.setCurrencyCode(CURRENCYCODE);
		source.setEmail(EMAIL);

		OrderPayment target = dtoToOrderPayment.convert(source);
		assertEquals(AMOUNT, target.getAmount());
		assertEquals(REFERENCEID, target.getReferenceId());
		assertEquals(REQUESTTOKEN, target.getRequestToken());
		assertEquals(AUTHORIZATIONCODE, target.getAuthorizationCode());
		assertEquals(CURRENCYCODE, target.getCurrencyCode());
		assertEquals(EMAIL, target.getEmail());
	}
}
