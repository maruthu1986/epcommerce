/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.EmailDtoTestData;
import com.elasticpath.email.producer.api.EmailProducer;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePredicate;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.camel.test.support.TransactionPolicyRegistryManager;
import com.elasticpath.messaging.impl.EventMessageImpl;

/**
 * Test class for {@link EventMessageTriggeredEmailRouteBuilder}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventMessageTriggeredEmailRouteBuilderTest extends CamelTestSupport {

	private static final int SECONDS_TO_WAIT = 2;
	private static final String ROUTE_ID = "TEST_ROUTE";

	private EventMessageTriggeredEmailRouteBuilder routeBuilder;
	private Endpoint incomingEndpoint;
	private MockEndpoint outgoingEndpoint;

	@Mock
	private DataFormat eventMessageDataFormat;

	@Mock
	private EmailProducer emailProducer;

	@Mock
	private EventMessagePredicate eventMessagePredicate;

	@Mock
	private Predicate emailEnabledPredicate;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		incomingEndpoint = getMandatoryEndpoint("direct:incoming");
		outgoingEndpoint = getMockEndpoint("mock:outgoing");

		final DataFormat emailDataFormat = new JacksonDataFormat(new ObjectMapper(), EmailDto.class);

		routeBuilder = new EventMessageTriggeredEmailRouteBuilder();
		routeBuilder.setRouteId(ROUTE_ID);
		routeBuilder.setIncomingEndpoint(incomingEndpoint);
		routeBuilder.setOutgoingEndpoint(outgoingEndpoint);
		routeBuilder.setEmailEnabledPredicate(emailEnabledPredicate);
		routeBuilder.setEventMessagePredicateFilter(eventMessagePredicate);
		routeBuilder.setEmailDataFormat(emailDataFormat);
		routeBuilder.setEventMessageDataFormat(eventMessageDataFormat);
		routeBuilder.setEmailProducer(emailProducer);

		context().addRoutes(routeBuilder);
	}

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		final JndiRegistry registry = super.createRegistry();

		new TransactionPolicyRegistryManager(registry).registerDefaultTransactionPolicy();

		return registry;
	}

	@Test
	public void verifyFilterIgnoresMessagesWhenEmailIsDisabled() throws Exception {
		final EventMessage eventMessage = new EventMessageImpl(new SampleEventType(), "foo");

		when(emailEnabledPredicate.matches(any(Exchange.class))).thenReturn(false);

		template().sendBody(incomingEndpoint, getJson(eventMessage));

		verifyNoInteractionsOnOutgoingEndpoint();
	}

	@Test
	public void testFilterIgnoresMessagesWithIncompatibleEventType() throws Exception {
		final EventMessage eventMessage = new EventMessageImpl(new SampleEventType(), "abc123");

		givenEmailIsEnabled();
		givenDataFormatUnmarshalsExchangeTo(eventMessage);

		when(eventMessagePredicate.apply(eventMessage)).thenReturn(false);

		template().sendBody(incomingEndpoint, getJson(eventMessage));

		verifyNoInteractionsOnOutgoingEndpoint();
	}

	@Test
	public void testHtmlEmailDtoJsonIsCreatedAndSentToPublishingQueue() throws Exception {
		final EmailDto htmlEmailDto = EmailDtoTestData.htmlEmailDtoBuilder().build();
		verifyEmailDtoJsonIsCreatedAndSentToPublishingQueue(Collections.singletonList(htmlEmailDto));
	}

	@Test
	public void testPlainTextEmailDtoJsonIsCreatedAndSentToPublishingQueue() throws Exception {
		final EmailDto plainTextEmailDto = EmailDtoTestData.plainTextEmailDtoBuilder().build();
		verifyEmailDtoJsonIsCreatedAndSentToPublishingQueue(Collections.singletonList(plainTextEmailDto));
	}

	private void verifyNoInteractionsOnOutgoingEndpoint() throws InterruptedException {
		// Make sure everything's done processing so we don't get false positives
		// (since we're testing for a _lack_ of activity)
		try {
			Thread.sleep(SECONDS_TO_WAIT);
		} catch (InterruptedException e) {
			// do nothing
		}

		outgoingEndpoint.expectedMessageCount(0);
		outgoingEndpoint.assertIsSatisfied();
	}

	public void verifyEmailDtoJsonIsCreatedAndSentToPublishingQueue(final List<EmailDto> emailDtos) throws Exception {
		final String orderNumber = "abc123";

		final EventType eventType = new SampleEventType();
		final Map<String, Object> data = Collections.singletonMap("key", "value");
		final EventMessage eventMessage = new EventMessageImpl(eventType, orderNumber, data);

		givenEmailIsEnabled();
		givenDataFormatUnmarshalsExchangeTo(eventMessage);
		givenRouteBuilderCompatibleWithEventMessage(eventMessage);
		givenEmailProducerCreatesEmailDtos(orderNumber, data, emailDtos);

		final int expectedSentMessageCount = emailDtos.size();

		final NotifyBuilder notifyBuilder = new NotifyBuilder(context)
				.wereSentTo(outgoingEndpoint.getEndpointUri())
				.whenDone(expectedSentMessageCount)
				.create();

		outgoingEndpoint.expectedMessageCount(expectedSentMessageCount);
		outgoingEndpoint.expectedBodiesReceivedInAnyOrder(createExpectedBodiesList(emailDtos));

		template().sendBody(incomingEndpoint, getJson(eventMessage));

		Assertions.assertThat(notifyBuilder.matches(SECONDS_TO_WAIT, TimeUnit.SECONDS))
				.isTrue()
				.as("Did not receive message on outgoing endpoint within " + SECONDS_TO_WAIT + " seconds");

		outgoingEndpoint.assertIsSatisfied();
	}

	private List<String> createExpectedBodiesList(final List<EmailDto> splitEmailDtos) {
		return Lists.transform(splitEmailDtos, input -> {
			try {
				return new ObjectMapper().writeValueAsString(input);
			} catch (JsonProcessingException e) {
				throw new IllegalStateException("Unable to marshall EmailDto", e);
			}
		});
	}

	private void givenEmailIsEnabled() {
		when(emailEnabledPredicate.matches(any(Exchange.class))).thenReturn(true);
	}

	private void givenDataFormatUnmarshalsExchangeTo(final EventMessage eventMessage) throws Exception {
		when(eventMessageDataFormat.unmarshal(any(Exchange.class), any(InputStream.class))).thenReturn(eventMessage);
	}

	private void givenRouteBuilderCompatibleWithEventMessage(final EventMessage eventMessage) {
		when(eventMessagePredicate.apply(eventMessage)).thenReturn(true);
	}

	private void givenEmailProducerCreatesEmailDtos(final String orderNumber, final Map<String, Object> data, final List<EmailDto> emailDtos) {
		when(emailProducer.createEmails(orderNumber, data)).thenReturn(emailDtos);
	}

	private String getJson(final EventMessage eventMessage) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(eventMessage);
	}

	private class SampleEventType implements EventType {

		private static final long serialVersionUID = -2789537988705742438L;

		@Override
		public String getName() {
			return "sampleEventType";
		}

	}
}