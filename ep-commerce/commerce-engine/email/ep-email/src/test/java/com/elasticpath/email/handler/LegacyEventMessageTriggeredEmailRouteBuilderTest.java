/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.email.handler;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
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
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.EmailDtoTestData;
import com.elasticpath.email.handler.producer.LegacyEmailProducer;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePredicate;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.camel.test.support.TransactionPolicyRegistryManager;
import com.elasticpath.messaging.impl.EventMessageImpl;

/**
 * Test class for {@link LegacyEventMessageTriggeredEmailRouteBuilder}.
 */
public class LegacyEventMessageTriggeredEmailRouteBuilderTest extends CamelTestSupport {

	private static final int SECONDS_TO_WAIT = 2;
	private static final String CHARSET = "UTF-8";
	private static final String ROUTE_ID = "TEST_ROUTE";

	private LegacyEventMessageTriggeredEmailRouteBuilder routeBuilder;
	private Endpoint incomingEndpoint;
	private MockEndpoint outgoingEndpoint;

	@Rule
	public final JUnitRuleMockery mockery = new JUnitRuleMockery();

	private final DataFormat eventMessageDataFormat = mockery.mock(DataFormat.class, "event message data format");
	private final LegacyEmailProducer emailProducer = mockery.mock(LegacyEmailProducer.class);
	private final EventMessagePredicate eventMessagePredicate = mockery.mock(EventMessagePredicate.class);
	private final Predicate emailEnabledPredicate = mockery.mock(Predicate.class);

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		incomingEndpoint = getMandatoryEndpoint("direct:incoming");
		outgoingEndpoint = getMockEndpoint("mock:outgoing");

		final DataFormat emailDataFormat = new JacksonDataFormat(new ObjectMapper(), EmailDto.class);

		routeBuilder = new LegacyEventMessageTriggeredEmailRouteBuilder();
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

		mockery.checking(new Expectations() {
			{
				oneOf(emailEnabledPredicate).matches(with(any(Exchange.class)));
				will(returnValue(false));
			}
		});

		template().sendBody(incomingEndpoint, getJson(eventMessage));

		verifyNoInteractionsOnOutgoingEndpoint();
	}

	@Test
	public void testFilterIgnoresMessagesWithIncompatibleEventType() throws Exception {
		final EventMessage eventMessage = new EventMessageImpl(new SampleEventType(), "abc123");

		givenEmailIsEnabled();
		givenDataFormatUnmarshalsExchangeTo(eventMessage);

		mockery.checking(new Expectations() {
			{
				oneOf(eventMessagePredicate).apply(eventMessage);
				will(returnValue(false));
			}
		});

		template().sendBody(incomingEndpoint, getJson(eventMessage));

		verifyNoInteractionsOnOutgoingEndpoint();
	}

	@Test
	public void testHtmlEmailDtoJsonIsCreatedAndSentToPublishingQueue() throws Exception {
		final EmailDto htmlEmailDto = EmailDtoTestData.htmlEmailDtoBuilder().build();
		verifyEmailDtoJsonIsCreatedAndSentToPublishingQueue(htmlEmailDto);
	}

	@Test
	public void testPlainTextEmailDtoJsonIsCreatedAndSentToPublishingQueue() throws Exception {
		final EmailDto plainTextEmailDto = EmailDtoTestData.plainTextEmailDtoBuilder().build();
		verifyEmailDtoJsonIsCreatedAndSentToPublishingQueue(plainTextEmailDto);
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

	public void verifyEmailDtoJsonIsCreatedAndSentToPublishingQueue(final EmailDto expectedEmailDto) throws Exception {
		final String orderNumber = "abc123";

		final Collection<Email> emails = createEmailsFrom(expectedEmailDto);

		final EventType eventType = new SampleEventType();
		final Map<String, Object> data = Collections.<String, Object>singletonMap("key", "value");
		final EventMessage eventMessage = new EventMessageImpl(eventType, orderNumber, data);

		givenEmailIsEnabled();
		givenDataFormatUnmarshalsExchangeTo(eventMessage);
		givenRouteBuilderCompatibleWithEventMessage(eventMessage);

		verifyEmailProducerWillReturnEmailsForOrderNumberAndData(orderNumber, emails, data);

		final int expectedSentMessageCount = emails.size();

		final NotifyBuilder notifyBuilder = new NotifyBuilder(context)
				.wereSentTo(outgoingEndpoint.getEndpointUri())
				.whenDone(expectedSentMessageCount)
				.create();

		outgoingEndpoint.expectedMessageCount(expectedSentMessageCount);
		outgoingEndpoint.expectedBodiesReceivedInAnyOrder(createExpectedBodiesList(splitEmailDtosByRecipient(expectedEmailDto)));

		template().sendBody(incomingEndpoint, getJson(eventMessage));

		Assert.assertTrue("Did not receive message on outgoing endpoint within " + SECONDS_TO_WAIT + " seconds",
						notifyBuilder.matches(SECONDS_TO_WAIT, TimeUnit.SECONDS));

		outgoingEndpoint.assertIsSatisfied();
	}

	private List<String> createExpectedBodiesList(final List<EmailDto> splitEmailDtos) {
		return Lists.transform(splitEmailDtos, new Function<EmailDto, String>() {
			@Override
			public String apply(final EmailDto input) {
				try {
					return new ObjectMapper().writeValueAsString(input);
				} catch (JsonProcessingException e) {
					throw new IllegalStateException("Unable to marshall EmailDto", e);
				}
			}
		});
	}

	private void givenEmailIsEnabled() {
		mockery.checking(new Expectations() {
			{
				oneOf(emailEnabledPredicate).matches(with(any(Exchange.class)));
				will(returnValue(true));
			}
		});
	}

	private void givenDataFormatUnmarshalsExchangeTo(final EventMessage eventMessage) throws Exception {
		mockery.checking(new Expectations() {
			{
				allowing(eventMessageDataFormat).unmarshal(with(Expectations.any(Exchange.class)), with(Expectations.any(InputStream.class)));
				will(Expectations.returnValue(eventMessage));
			}
		});
	}

	private void givenRouteBuilderCompatibleWithEventMessage(final EventMessage eventMessage) {
		mockery.checking(new Expectations() {
			{
				oneOf(eventMessagePredicate).apply(eventMessage);
				will(returnValue(true));
			}
		});
	}

	private Collection<Email> createEmailsFrom(final EmailDto dto) throws EmailException {
		final Collection<Email> emails = Lists.newArrayList();

		for (final String recipient : dto.getTo()) {
			final Email email;

			if (dto.getHtmlBody() == null) {
				email = new SimpleEmail();
				email.setMsg(dto.getTextBody());
			} else {
				email = new HtmlEmail();
				((HtmlEmail) email).setTextMsg(dto.getTextBody());
				((HtmlEmail) email).setHtmlMsg(dto.getHtmlBody());
			}

			email.setCharset(CHARSET);
			email.setFrom(dto.getFrom());
			email.setSubject(dto.getSubject());

			email.getToAddresses().clear();
			email.addTo(recipient);
			email.addCc(dto.getCc().toArray(new String[dto.getCc().size()]));
			email.addBcc(dto.getBcc().toArray(new String[dto.getBcc().size()]));

			for (final String replyTo : dto.getReplyTo()) {
				email.addReplyTo(replyTo);
			}

			emails.add(email);
		}

		return emails;
	}

	private void verifyEmailProducerWillReturnEmailsForOrderNumberAndData(final String orderNumber, final Collection<Email> emails,
																			final Map<String, Object> data)
			throws EmailException {
		mockery.checking(new Expectations() {
			{
				oneOf(emailProducer).createEmails(orderNumber, data);
				will(Expectations.returnValue(emails));
			}
		});
	}

	@SuppressWarnings("PMD.ShortVariable")
	private List<EmailDto> splitEmailDtosByRecipient(final EmailDto expectedEmailDto) {
		final List<EmailDto> dtos = Lists.newArrayListWithExpectedSize(expectedEmailDto.getTo().size());

		for (final String to : expectedEmailDto.getTo()) {
			dtos.add(EmailDto.builder()
					.fromPrototype(expectedEmailDto)
					.withTo(to)
					.build());
		}

		return dtos;
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