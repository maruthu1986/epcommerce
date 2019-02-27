/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.ReturnExchangeEmailPropertyHelper;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.order.ReturnAndExchangeService;

/**
 * Test class for {@link ReturnExchangeEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReturnExchangeEmailProducerTest {

	private static final String UID_KEY = "UID";
	private static final String EMAIL_KEY = "EMAIL";
	private static final long ORDER_RETURN_UID = 1L;

	@Mock
	private ReturnAndExchangeService returnAndExchangeService;

	@Mock
	private ReturnExchangeEmailPropertyHelper returnExchangeEmailPropertyHelper;

	@Mock
	private EmailComposer emailComposer;

	@InjectMocks
	private ReturnExchangeEmailProducer emailProducer;

	@Test
	public void verifyThatReturnExchangeEmailCreatedFromUid() {
		final String recipient = "recipient@elasticpath.com";
		final OrderReturn orderReturn = mock(OrderReturn.class);

		final EmailDto expectedEmail = EmailDto.builder()
				.withTo(recipient)
				.build();

		final EmailProperties emailProperties = mock(EmailProperties.class);

		final List<Long> uids = Collections.singletonList(ORDER_RETURN_UID);
		final List<OrderReturn> orderReturns = Collections.singletonList(orderReturn);

		when(returnAndExchangeService.findByUids(uids))
				.thenReturn(orderReturns);

		when(returnExchangeEmailPropertyHelper.getOrderReturnEmailProperties(orderReturn))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final EmailDto actualEmail = emailProducer.createEmail(null, createValidAdditionalData(ORDER_RETURN_UID, null));

		assertThat(actualEmail)
				.as("Unexpected Email instance produced")
				.isEqualTo(expectedEmail);
	}

	@Test
	public void verifyExceptionIsThrownWhenNoOrderReturnUid() {
		final Map<String, Object> emailData = createValidAdditionalData(ORDER_RETURN_UID, "foo");
		emailData.remove(UID_KEY);

		assertThatThrownBy(() -> emailProducer.createEmail(null, emailData))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyThatReturnExchangeEmailCreatedWhenPhysicalReturnIsNotRequired() {
		final String recipient = "recipient@elasticpath.com";

		final EmailDto expectedEmail = EmailDto.builder()
				.withTo(recipient)
				.build();

		final OrderReturn orderReturn = mock(OrderReturn.class);

		final EmailProperties emailProperties = mock(EmailProperties.class);

		final List<Long> uids = Collections.singletonList(ORDER_RETURN_UID);
		final List<OrderReturn> orderReturns = Collections.singletonList(orderReturn);

		when(returnAndExchangeService.findByUids(uids))
				.thenReturn(orderReturns);

		when(returnExchangeEmailPropertyHelper.getOrderReturnEmailProperties(orderReturn))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmail);

		final Map<String, Object> emailData = createValidAdditionalData(ORDER_RETURN_UID, null);

		final EmailDto actualEmail = emailProducer.createEmail(null, emailData);

		assertThat(actualEmail)
				.as("Unexpected Email instance produced")
				.isEqualTo(expectedEmail);
	}

	@Test
	public void verifyThatEmailSuppliedInDataWillOverrideOriginalRecipientAddress() {
		final String originalRecipient = "original.recipient@elasticpath.com";
		final String overrideRecipient = "override.recipient@elasticpath.com";

		final EmailDto emailDtoTemplate = EmailDto.builder()
				.withTo(originalRecipient)
				.build();

		final EmailDto expectedEmail = EmailDto.builder()
				.withTo(overrideRecipient)
				.build();

		final OrderReturn orderReturn = mock(OrderReturn.class);

		final EmailProperties emailProperties = mock(EmailProperties.class);

		final List<Long> uids = Collections.singletonList(ORDER_RETURN_UID);
		final List<OrderReturn> orderReturns = Collections.singletonList(orderReturn);

		when(returnAndExchangeService.findByUids(uids))
				.thenReturn(orderReturns);

		when(returnExchangeEmailPropertyHelper.getOrderReturnEmailProperties(orderReturn))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(emailDtoTemplate);

		final Map<String, Object> emailData = createValidAdditionalData(ORDER_RETURN_UID, overrideRecipient);

		final EmailDto actualEmail = emailProducer.createEmail(null, emailData);

		assertThat(actualEmail)
				.as("Unexpected Email instance produced")
				.isEqualTo(expectedEmail);
	}

	private Map<String, Object> createValidAdditionalData(final long orderReturnUid, final String recipient) {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put(UID_KEY, (int) orderReturnUid);

		if (recipient != null) {
			additionalData.put(EMAIL_KEY, recipient);
		}

		return additionalData;
	}

}
