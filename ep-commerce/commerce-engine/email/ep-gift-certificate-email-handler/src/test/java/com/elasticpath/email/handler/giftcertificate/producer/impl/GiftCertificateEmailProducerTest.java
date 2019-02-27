/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.giftcertificate.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.giftcertificate.helper.GiftCertificateEmailPropertyHelper;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.OrderService;

/**
 * Test class for {@link com.elasticpath.email.handler.giftcertificate.producer.impl.GiftCertificateEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class GiftCertificateEmailProducerTest {

	private static final String GIFT_CERTIFICATE_GUID = "ABCD-1234-FGHI-5678";

	private static final String ORDER_GUID = "200000";

	private static final String ORDER_SKU_GUID = "ZYXW-8765-VUTS-4321";

	private static final String EMAIL_KEY = "emailAddress";

	private static final String RECIPIENT_EMAIL_ADDRESS = "recipient@elasticpath.com";

	@Mock
	private GiftCertificateEmailPropertyHelper giftCertificateEmailPropertyHelper;

	@Mock
	private EmailComposer emailComposer;

	@Mock
	private OrderService orderService;

	@Mock
	private ProductSkuLookup productSkuLookup;

	@InjectMocks
	private GiftCertificateEmailProducer emailProducer;

	@Test
	public void verifyExceptionIsThrownWhenNoOrderGuid() throws Exception {
		final Map<String, Object> emailDataMap = createEmailDataMap(ORDER_GUID, ORDER_SKU_GUID, RECIPIENT_EMAIL_ADDRESS);
		emailDataMap.put("orderGuid", null);

		assertThatThrownBy(() -> emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailDataMap))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyExceptionIsThrownWhenNoOrderSkuGuid() throws Exception {
		final Map<String, Object> emailDataMap = createEmailDataMap(ORDER_GUID, ORDER_SKU_GUID, RECIPIENT_EMAIL_ADDRESS);
		emailDataMap.put("orderSkuGuid", null);

		givenOrderServiceFindsOrder(mock(Order.class));

		assertThatThrownBy(() -> emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailDataMap))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyExceptionIsThrownWhenNoOrderMatchingOrderGuid() throws Exception {
		final Map<String, Object> emailDataMap = createEmailDataMap(ORDER_GUID, ORDER_SKU_GUID, RECIPIENT_EMAIL_ADDRESS);

		givenOrderServiceFindsOrder(null);

		assertThatThrownBy(() -> emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailDataMap))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyExceptionIsThrownWhenNoOrderSkuMatchingOrderSkuGuid() throws Exception {
		final Map<String, Object> emailDataMap = createEmailDataMap(ORDER_GUID, ORDER_SKU_GUID, RECIPIENT_EMAIL_ADDRESS);

		final Order order = mock(Order.class);

		givenOrderServiceFindsOrder(order);
		givenOrderFindsOrderSkuForGuid(order, null);

		assertThatThrownBy(() -> emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailDataMap))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testVerifyThatGiftCertificateEmailIsConstructedForOrderAndOrderSku() throws Exception {
		final String giftCertificateThemeImageFilename = "hello.jpg";
		final OrderSku orderSku = createOrderSku(giftCertificateThemeImageFilename);
		final Order order = mock(Order.class);

		final EmailDto expectedEmail = EmailDto.builder()
				.withTo(RECIPIENT_EMAIL_ADDRESS)
				.build();

		givenOrderServiceFindsOrder(order);
		givenOrderFindsOrderSkuForGuid(order, orderSku);

		final EmailProperties emailProperties = mock(EmailProperties.class);

		when(giftCertificateEmailPropertyHelper.getEmailProperties(order, orderSku, giftCertificateThemeImageFilename))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final Map<String, Object> emailData = createEmailDataMap(ORDER_GUID, ORDER_SKU_GUID, null);

		final EmailDto actualEmail = emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailData);

		assertThat(actualEmail)
				.as("Unexpected Email instance produced")
				.isEqualTo(expectedEmail);
	}

	@Test
	public void testVerifyThatSpecifiedEmailAddressAddsToOriginalRecipient() throws Exception {
		final String overrideRecipientAddress = "override.recipient@elasticpath.com";
		final String giftCertificateThemeImageFilename = "hello.jpg";
		final OrderSku orderSku = createOrderSku(giftCertificateThemeImageFilename);
		final Order order = mock(Order.class);

		final EmailDto emailDtoTemplate = EmailDto.builder()
				.withTo(RECIPIENT_EMAIL_ADDRESS)
				.build();

		final EmailDto expectedEmail = EmailDto.builder()
				.fromPrototype(emailDtoTemplate)
				.addTo(overrideRecipientAddress)
				.build();

		givenOrderServiceFindsOrder(order);
		givenOrderFindsOrderSkuForGuid(order, orderSku);

		final EmailProperties emailProperties = mock(EmailProperties.class);

		when(giftCertificateEmailPropertyHelper.getEmailProperties(order, orderSku, giftCertificateThemeImageFilename))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(emailDtoTemplate);

		final Map<String, Object> emailData = createEmailDataMap(ORDER_GUID, ORDER_SKU_GUID, overrideRecipientAddress);

		final EmailDto actualEmail = emailProducer.createEmail(GIFT_CERTIFICATE_GUID, emailData);

		assertThat(actualEmail)
				.as("Unexpected Email instance produced")
				.isEqualTo(expectedEmail);
	}

	private Map<String, Object> createEmailDataMap(final String orderGuid, final String orderSkuGuid, final String recipient) {
		final Map<String, Object> emailDataMap = Maps.newHashMapWithExpectedSize(3);
		emailDataMap.put("orderGuid", orderGuid);
		emailDataMap.put("orderSkuGuid", orderSkuGuid);

		if (recipient != null) {
			emailDataMap.put(EMAIL_KEY, recipient);
		}

		return emailDataMap;
	}

	private OrderSku createOrderSku(final String giftCertificateThemeImageFilename) {
		final ProductSku productSku = new ProductSkuImpl();
		productSku.initialize();
		productSku.setImage(giftCertificateThemeImageFilename);

		final OrderSku orderSku = new OrderSkuImpl();
		orderSku.setSkuGuid(productSku.getGuid());

		when(productSkuLookup.findByGuid(productSku.getGuid())).thenReturn(productSku);

		return orderSku;
	}

	private void givenOrderServiceFindsOrder(final Order order) {
		when(orderService.findOrderByOrderNumber(ORDER_GUID)).thenReturn(order);
	}

	private void givenOrderFindsOrderSkuForGuid(final Order order, final OrderSku orderSku) {
		when(order.getOrderSkuByGuid(ORDER_SKU_GUID)).thenReturn(orderSku);
	}

}
