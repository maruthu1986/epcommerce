/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.email.sender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.processor.RedeliveryPolicy;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spring.spi.TransactionErrorHandlerBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.email.EmailDto;
import com.elasticpath.email.EmailDtoTestData;
import com.elasticpath.messaging.camel.test.support.TransactionPolicyRegistryManager;

/**
 * Test class for {@link EmailSendingRouteBuilder}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases") // TODO remove this suppression; the PMD rule should be revoked
@RunWith(MockitoJUnitRunner.class)
public class EmailSendingRouteBuilderTest extends CamelTestSupport {

	private static final int MAXIMUM_RETRIES = 3;
	private static final int RETRY_DELAY = 100;
	private static final int SECONDS_TO_WAIT = 2;

	private static final String REDELIVERY_POLICY_REF = "emailSendingRedeliveryPolicy";
	private static final String SMTP_SCHEME = "smtps";
	private static final String MAIL_HOST = "localhost";
	private static final Integer MAIL_PORT = 25;
	private static final String MAIL_USERNAME = "username";
	private static final String MAIL_PASSWORD = "password";
	private RedeliveryPolicy redeliveryPolicy;
	private TransactionErrorHandlerBuilder transactionErrorHandlerBuilder;

	private EmailSendingRouteBuilder routeBuilder;

	private Endpoint incomingEndpoint;
	private MockEndpoint outgoingEndpoint;
	private MockEndpoint deadLetterEndpoint;
	private ErrorProcessor errorGenerator;

	@Mock
	private Processor attachmentProcessor;

	/**
	 * Tell Camel Test we are using adviceWith(), which allows us to intercept SMTP component and replace it with our mock below.
	 *
	 * @return true to indicate we are using adviceWith.
	 */
	@Override
	public boolean isUseAdviceWith() {
		return true;
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		incomingEndpoint = getMandatoryEndpoint("direct:incoming");
		deadLetterEndpoint = getMockEndpoint("mock:dlq");

		final DataFormat emailDataFormat = new JacksonDataFormat(new ObjectMapper(), EmailDto.class);

		routeBuilder = new EmailSendingRouteBuilder();
		routeBuilder.setIncomingEndpoint(incomingEndpoint);
		routeBuilder.setDeadLetterEndpoint(deadLetterEndpoint);
		routeBuilder.setRedeliveryPolicyRef(REDELIVERY_POLICY_REF);
		routeBuilder.setSmtpScheme(SMTP_SCHEME);
		routeBuilder.setMailHost(MAIL_HOST);
		routeBuilder.setMailPort(MAIL_PORT);
		routeBuilder.setMailUsername(MAIL_USERNAME);
		routeBuilder.setMailPassword(MAIL_PASSWORD);
		routeBuilder.setEmailDataFormat(emailDataFormat);
		routeBuilder.setAttachmentProcessor(attachmentProcessor);

		routeBuilder.setErrorHandlerBuilder(transactionErrorHandlerBuilder);

		context().addRoutes(routeBuilder);

		outgoingEndpoint = getMockEndpoint("mock:outgoing");
		errorGenerator = new ErrorProcessor();

		context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint(SMTP_SCHEME + "://*")
						.skipSendToOriginalEndpoint()
						.process(errorGenerator)
						.to(outgoingEndpoint);
			}
		});

		// We need to manually start when using adviceWith()
		context.start();
	}

	@Override
	protected JndiRegistry createRegistry() throws Exception {
		final JndiRegistry registry = super.createRegistry();

		new TransactionPolicyRegistryManager(registry).registerDefaultTransactionPolicy();

		// Configured here as this method is called prior to setUp. The redelivery policy must be configured prior to registration.
		redeliveryPolicy = new RedeliveryPolicy();
		redeliveryPolicy.setMaximumRedeliveries(MAXIMUM_RETRIES);
		redeliveryPolicy.setRedeliveryDelay(RETRY_DELAY);

		transactionErrorHandlerBuilder = new TransactionErrorHandlerBuilder();
		transactionErrorHandlerBuilder.setRedeliveryPolicy(redeliveryPolicy);

		registry.bind(REDELIVERY_POLICY_REF, redeliveryPolicy);

		return registry;
	}

	@Test
	public void verifyOutgoingEndpointIsConstructedUsingMailConfigurationSettings() throws Exception {
		final String expectedEndpointUri = "smtps://username@localhost:25?password=password";

		final Endpoint endpoint = routeBuilder.getOutgoingEndpoint();

		assertEquals("Unexpected endpoint URI constructed", expectedEndpointUri, endpoint.getEndpointUri());
	}

	@Test
	public void verifyOutgoingEndpointURLsWithBlankPassword() throws Exception {
		routeBuilder.setMailPassword("");

		final String expectedEndpointUri = "smtps://username@localhost:25";

		final Endpoint endpoint = routeBuilder.getOutgoingEndpoint();

		assertEquals("Unexpected blank password endpoint URI constructed", expectedEndpointUri, endpoint.getEndpointUri());
	}

	@Test
	public void verifyOutgoingEndpointURLsWithBlankUsername() throws Exception {
		routeBuilder.setMailUsername("");

		final String expectedEndpointUri = "smtps://localhost:25";

		final Endpoint endpoint = routeBuilder.getOutgoingEndpoint();

		assertEquals("Unexpected blank username endpoint URI constructed", expectedEndpointUri, endpoint.getEndpointUri());
	}

	@Test
	public void verifyOutgoingEndpointURLsWithBlankUsernameAndBlankPassword() throws Exception {
		routeBuilder.setMailUsername("");
		routeBuilder.setMailPassword("");

		final String expectedEndpointUri = "smtps://localhost:25";

		final Endpoint endpoint = routeBuilder.getOutgoingEndpoint();

		assertEquals("Unexpected blank username and blank password endpoint URI constructed", expectedEndpointUri, endpoint.getEndpointUri());
	}

	@Test
	public void verifyFullyPopulatedPlainTextEmailMessageSentToOutgoingChannel() throws Exception {
		final EmailDto emailDto = EmailDtoTestData.plainTextEmailDtoBuilder().build();

		sendJsonMessage(emailDto);

		verifyMessageReceivedWithCommonHeaders(emailDto);
		outgoingEndpoint.expectedBodyReceived().constant(emailDto.getTextBody());
		outgoingEndpoint.assertIsSatisfied();
	}

	@Test
	public void verifyMinimalPlainTextEmailMessageSentToOutgoingChannel() throws Exception {
		final EmailDto emailDto = EmailDto.builder()
				.withFrom("Sender <sender@elasticpath.com>")
				.withTo("recipient@elasticpath.com")
				.build();

		sendJsonMessage(emailDto);

		verifyMessageReceivedWithMininmalHeaders(emailDto);
		outgoingEndpoint.expectedBodyReceived().constant(emailDto.getTextBody());
		outgoingEndpoint.assertIsSatisfied();
	}

	@Test
	public void verifyHtmlEmailMessageSentToOutgoingChannel() throws Exception {
		final EmailDto emailDto = EmailDtoTestData.htmlEmailDtoBuilder().build();

		sendJsonMessage(emailDto);

		verifyMessageReceivedWithCommonHeaders(emailDto);
		outgoingEndpoint.expectedHeaderReceived("CamelMailAlternativeBody", emailDto.getTextBody());
		outgoingEndpoint.expectedBodyReceived().constant(emailDto.getHtmlBody());
		outgoingEndpoint.assertIsSatisfied();
	}

	@Test
	public void verifyRetryWhenSmtpIsUnavailable() throws Exception {
		errorGenerator.setNumberOfConsecutiveCallsToProduceError(MAXIMUM_RETRIES);
		redeliveryPolicy.setMaximumRedeliveries(MAXIMUM_RETRIES);

		final EmailDto emailDto = EmailDtoTestData.plainTextEmailDtoBuilder().build();

		sendJsonMessage(emailDto);

		verifyMessageReceivedWithCommonHeaders(emailDto);
		outgoingEndpoint.expectedBodyReceived().constant(emailDto.getTextBody());
		outgoingEndpoint.assertIsSatisfied();
	}

	@Test
	public void verifyMessageSentToDeadLetterQueueWhenRetriesExhausted() throws Exception {
		errorGenerator.setNumberOfConsecutiveCallsToProduceError(MAXIMUM_RETRIES + 1);

		final EmailDto emailDto = EmailDtoTestData.plainTextEmailDtoBuilder().build();

		final NotifyBuilder notifyBuilder = new NotifyBuilder(context)
				.wereSentTo(deadLetterEndpoint.getEndpointUri())
				.whenDone(1)
				.create();

		sendJsonMessage(emailDto, notifyBuilder);

		outgoingEndpoint.expectedMessageCount(0);
		outgoingEndpoint.assertIsSatisfied();

		deadLetterEndpoint.expectedMessageCount(1);
		deadLetterEndpoint.expectedBodyReceived().constant(convertToJson(emailDto));
		deadLetterEndpoint.assertIsSatisfied();
	}

	@Test
	public void verifyMessageSentWithAttachments() throws Exception {
		sendJsonMessage(EmailDtoTestData.plainTextEmailDtoBuilder().build());

		verify(attachmentProcessor).process(any(Exchange.class));
	}

	private void sendJsonMessage(final EmailDto emailDto) throws JsonProcessingException {
		final NotifyBuilder notifyBuilder = new NotifyBuilder(context)
				.wereSentTo(outgoingEndpoint.getEndpointUri())
				.whenDone(1)
				.create();

		sendJsonMessage(emailDto, notifyBuilder);
	}

	private void sendJsonMessage(final EmailDto emailDto, final NotifyBuilder notifyBuilder) throws JsonProcessingException {
		template().sendBody(incomingEndpoint, convertToJson(emailDto));

		assertTrue("Did not receive message on outgoing endpoint within " + SECONDS_TO_WAIT + " seconds",
					notifyBuilder.matches(SECONDS_TO_WAIT, TimeUnit.SECONDS));
	}

	private void verifyMessageReceivedWithMininmalHeaders(final EmailDto emailDto) {
		outgoingEndpoint.expectedMessageCount(1);
		outgoingEndpoint.expectedHeaderReceived("contentType", emailDto.getContentType());
		outgoingEndpoint.expectedHeaderReceived("from", emailDto.getFrom());
		outgoingEndpoint.expectedHeaderReceived("to", join(emailDto.getTo()));
		outgoingEndpoint.message(0).header("subject").isNull();
		outgoingEndpoint.message(0).header("CC").isNull();
		outgoingEndpoint.message(0).header("BCC").isNull();
		outgoingEndpoint.message(0).header("replyTo").isNull();
	}

	private void verifyMessageReceivedWithCommonHeaders(final EmailDto emailDto) {
		outgoingEndpoint.expectedMessageCount(1);
		outgoingEndpoint.expectedHeaderReceived("contentType", emailDto.getContentType());
		outgoingEndpoint.expectedHeaderReceived("from", emailDto.getFrom());
		outgoingEndpoint.expectedHeaderReceived("to", join(emailDto.getTo()));
		outgoingEndpoint.expectedHeaderReceived("subject", emailDto.getSubject());
		outgoingEndpoint.expectedHeaderReceived("CC", join(emailDto.getCc()));
		outgoingEndpoint.expectedHeaderReceived("BCC", join(emailDto.getBcc()));
		outgoingEndpoint.expectedHeaderReceived("replyTo", join(emailDto.getReplyTo()));
	}

	private String join(final Iterable<String> list) {
		return StringUtils.join(list, ",");
	}

	private String convertToJson(final EmailDto emailDto) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(emailDto);
	}

	/**
	 * Processor that can be configured to simulate errors.
	 */
	private class ErrorProcessor implements Processor {
		private int remainingErrors;

		@Override
		public void process(final Exchange exchange) throws Exception {
			if (remainingErrors > 0) {
				remainingErrors--;
				throw new Exception("Boom!");
			}
		}

		void setNumberOfConsecutiveCallsToProduceError(final int callsToThrowError) {
			remainingErrors = callsToThrowError;
		}

	}

}
