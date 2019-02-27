/*
  Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import com.google.common.net.InetAddresses;
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
import com.elasticpath.domain.misc.PayerAuthValidationValue;
import com.elasticpath.domain.misc.impl.PayerAuthValidationValueImpl;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PayerAuthValidationValueDto;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthValidationValueDtoImpl;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;
import com.elasticpath.service.payment.gateway.impl.GiftCertificateOrderPaymentDtoImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

public class OrderPaymentToPaymentMethodTest {
	private static final String REFERENCEID = "referenceId";
	private static final String EMAIL = "john.doe@elasticpath.com";
	private static final String TRANSACTIONTYPE = "AUTHORIZATION";
	private static final String GATEWAYTOKEN = "gatewayToken";
	private static final String IPADDRESS = InetAddresses.fromInteger(ThreadLocalRandom.current().nextInt()).getHostAddress();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final OrderPaymentToPaymentMethod orderPaymentToPaymentMethod = new OrderPaymentToPaymentMethod();
	private final PayerAuthValidationValueDto payerAuthValidationDto = new PayerAuthValidationValueDtoImpl();
	private final PayerAuthValidationValue payerAuthValidationValue = new PayerAuthValidationValueImpl();
	private final GiftCertificate giftCertificate = new GiftCertificateImpl();
	@Mock private ConversionService mockConversionService;

	@Before
	public void setUp() throws Exception {
		BeanFactory beanFactory = context.mock(BeanFactory.class);
		BeanFactoryExpectationsFactory expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.GIFT_CERTIFICATE_ORDER_PAYMENT_DTO, GiftCertificateOrderPaymentDtoImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.CONVERSION_SERVICE, mockConversionService);
		orderPaymentToPaymentMethod.setBeanFactory(beanFactory);
		context.checking(new Expectations() {
			{
				allowing(mockConversionService).convert(with(any(PayerAuthValidationValue.class)), with(same(PayerAuthValidationValueDto.class)));
				will(returnValue(payerAuthValidationDto));
			}
		});
	}

	@Test
	public void testConvertForGiftCertificate() throws Exception {
		OrderPayment source = new OrderPaymentImpl();
		source.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);
		source.setReferenceId(REFERENCEID);
		source.setEmail(EMAIL);
		source.setTransactionType(TRANSACTIONTYPE);
		source.setGatewayToken(GATEWAYTOKEN);
		source.setIpAddress(IPADDRESS);
		source.setGiftCertificate(giftCertificate);
		source.setPayerAuthValidationValue(payerAuthValidationValue);
		PaymentMethod target = orderPaymentToPaymentMethod.convert(source);

		assertTrue(target instanceof GiftCertificateOrderPaymentDto);
		GiftCertificateOrderPaymentDto giftCertificateTarget = (GiftCertificateOrderPaymentDto) target;
		assertEquals(REFERENCEID, giftCertificateTarget.getReferenceId());
		assertEquals(EMAIL, giftCertificateTarget.getEmail());
		assertEquals(giftCertificate, giftCertificateTarget.getGiftCertificate());
	}
}
