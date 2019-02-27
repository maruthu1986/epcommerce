/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.dto.impl.MoneyDtoImpl;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionRequestImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateCaptureRequest;
import com.elasticpath.service.payment.gateway.impl.GiftCertificateCaptureRequestImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class OrderPaymentToCaptureRequestTest {
	private static final BigDecimal AMOUNT = new BigDecimal("100.25");
	private static final String CURRENCYCODE = "USD";
	private static final String REQUESTTOKEN = "requestToken";
	private static final String AUTHORIZATIONCODE = "authorizationCode";
	private static final String REFERENCEID = "referenceId";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final OrderPaymentToCaptureRequest orderPaymentToCaptureRequest = new OrderPaymentToCaptureRequest();
	private final MoneyDto moneyDto = new MoneyDtoImpl();
	private final GiftCertificate giftCertificate = new GiftCertificateImpl();
	@Mock private ConversionService mockConversionService;

	@Before
	public void setUp() throws Exception {
		BeanFactory beanFactory = context.mock(BeanFactory.class);
		BeanFactoryExpectationsFactory expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CONVERSION_SERVICE, mockConversionService);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CAPTURE_TRANSACTION_REQUEST, CaptureTransactionRequestImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.GIFT_CERTIFICATE_CAPTURE_REQUEST, GiftCertificateCaptureRequestImpl.class);
		orderPaymentToCaptureRequest.setBeanFactory(beanFactory);
		context.checking(new Expectations() {
			{
				allowing(mockConversionService).convert(with(any(OrderPaymentImpl.class)), with(same(MoneyDto.class)));
				will(returnValue(moneyDto));
			}
		});
	}

	@Test
	public void testConvertForGiftCertificate() throws Exception {
		OrderPayment source = new OrderPaymentImpl();
		source.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);
		source.setRequestToken(REQUESTTOKEN);
		source.setAuthorizationCode(AUTHORIZATIONCODE);
		source.setReferenceId(REFERENCEID);
		source.setAmount(AMOUNT);
		source.setCurrencyCode(CURRENCYCODE);
		source.setGiftCertificate(giftCertificate);

		CaptureTransactionRequest target = orderPaymentToCaptureRequest.convert(source);
		assertTrue(target instanceof GiftCertificateCaptureRequest);
		assertEquals(REQUESTTOKEN, target.getRequestToken());
		assertEquals(AUTHORIZATIONCODE, target.getAuthorizationCode());
		assertEquals(REFERENCEID, target.getReferenceId());
		assertEquals(moneyDto, target.getMoney());
		assertEquals(giftCertificate, ((GiftCertificateCaptureRequest) target).getGiftCertificate());
	}
}
