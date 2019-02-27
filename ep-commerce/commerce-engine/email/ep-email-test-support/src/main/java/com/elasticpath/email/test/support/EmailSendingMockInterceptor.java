/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.test.support;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Creates a Mock Endpoint and attaches it to the end of the route responsible for sending emails.
 */
public class EmailSendingMockInterceptor {

	/** The name of the camel Context responsible for sending emails. */
	public static final String EMAIL_SENDER_CAMEL_CONTEXT_ID = "ep-email-sender";

	/** The name of the Camel route ID responsible for sending emails. */
	public static final String EMAIL_SENDER_ROUTE_ID = "ep-email-sender";

	private static final String EMAIL_SUCCESSFULLY_SENT_ENDPOINT_URI = "log:dev/null?*";

	@Autowired
	@Qualifier(EMAIL_SENDER_CAMEL_CONTEXT_ID)
	private ModelCamelContext emailSendingCamelContext;

	/**
	 * Intercept messages sent to to the email sending endpoint, and copies them to the returned mock endpoint. The email sending endpoint will
	 * continue to receive email messages.
	 *
	 * @return a mock endpoint to receive email messages
	 * @throws Exception in case of any errors
	 */
	public MockEndpoint wireTapEmailSending() throws Exception {
		final MockEndpoint mockEndpoint = emailSendingCamelContext.getEndpoint("mock:email-sending", MockEndpoint.class);

		emailSendingCamelContext.getRouteDefinition(EMAIL_SENDER_ROUTE_ID).adviceWith(emailSendingCamelContext, new AdviceWithRouteBuilder() {
			@Override
			public void configure() throws Exception {
				interceptSendToEndpoint(EMAIL_SUCCESSFULLY_SENT_ENDPOINT_URI)
						.to(mockEndpoint);
			}
		});

		return mockEndpoint;
	}

	/**
	 * Creates a new NotifyBuilder bound to the correct Camel Context.
	 *
	 * @return a new NotifyBuilder
	 */
	public NotifyBuilder createNotifyBuilderForEmailSendingMockInterceptor() {
		return new NotifyBuilder(emailSendingCamelContext);
	}

}
