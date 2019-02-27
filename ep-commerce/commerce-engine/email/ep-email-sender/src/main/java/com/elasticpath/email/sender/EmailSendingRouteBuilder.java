/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.email.sender;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.camel.Endpoint;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.DataFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

/**
 * Configures a route to send email messages corresponding to {@link com.elasticpath.email.EmailDto EmailDto} instances.
 */
public class EmailSendingRouteBuilder extends RouteBuilder {

	private Endpoint incomingEndpoint;
	private DataFormat emailDataFormat;
	private String transactionPropagationRef;
	private String redeliveryPolicyRef;
	private Endpoint deadLetterEndpoint;
	private String smtpScheme;
	private String mailHost;
	private Integer mailPort;
	private String mailUsername;
	private String mailPassword;

	// hook for extension - also useful for testing
	private String postRouteEndpoint = "log:dev/null?level=OFF";
	private Processor attachmentProcessor;

	@Override
	public void configure() throws Exception {
		onException(Exception.class)
				.redeliveryPolicyRef(getRedeliveryPolicyRef())
				.handled(true)
				.useOriginalMessage()
				.log(LoggingLevel.ERROR, getClass().getName(), "Unable to deliver email. Error ${exception}")
				.to(getDeadLetterEndpoint());

		errorHandler(getErrorHandlerBuilder());

		from(getIncomingEndpoint())
				.routeId("ep-email-sender")
				.transacted(getTransactionPropagationRef())
				.log(LoggingLevel.DEBUG, getClass().getName(), "Received message ${body}")
				.unmarshal(getEmailDataFormat())
				.setHeader("from").simple("${body.from}")
				.choice()
					.when(simple("${body.replyTo?.size} > 0"))
						.setHeader("replyTo").groovy("request.body.replyTo.join(',')")
					.end()
				.choice()
					.when(simple("${body.to?.size} > 0"))
						.setHeader("to").groovy("request.body.to.join(',')")
					.end()
				.choice()
					.when(simple("${body.cc?.size} > 0"))
						.setHeader("CC").groovy("request.body.cc.join(',')")
					.end()
				.choice()
					.when(simple("${body.bcc?.size} > 0"))
						.setHeader("BCC").groovy("request.body.bcc.join(',')")
					.end()
				.setHeader("subject", simple("${body.subject}"))
				.setHeader("contentType", simple("${body.contentType}"))
				.process(getAttachmentProcessor())
				.choice()
					.when(simple("${body.htmlBody} == null"))
						.setBody(simple("${body.textBody}"))
					.otherwise()
						.setHeader("CamelMailAlternativeBody", simple("${body.textBody}"))
						.setBody(simple("${body.htmlBody}"))
					.end()
				.to(getOutgoingEndpoint())
				.log(LoggingLevel.DEBUG, getClass().getName(), "Email message successfully sent")
				.to(postRouteEndpoint);
	}

	public void setIncomingEndpoint(final Endpoint incomingEndpoint) {
		this.incomingEndpoint = incomingEndpoint;
	}

	protected Endpoint getIncomingEndpoint() {
		return incomingEndpoint;
	}

	/**
	 * Creates an endpoint. If mailUsername and mailPassword are not blank, this endpoint is in the format:
	 * "smtp://<code>username</code>@<code>host</code>:<code>port</code>?password=<code>password</code>"
	 * If mailUsername is blank, the endpoint is:
	 * "smtp://<code>host</code>:<code>port</code>"
	 * If only mailPassword is blank, the endpoint is:
	 * "smtp://<code>username</code>@<code>host</code>:<code>port</code>"
	 *
	 * @return an SMTP endpoint
	 * @throws URISyntaxException if mail configurations can not be parsed as a valid endpoint URI
	 */
	protected Endpoint getOutgoingEndpoint() throws URISyntaxException {
		URIBuilder builder = new URIBuilder()
				.setScheme(getSmtpScheme())
				.setHost(getMailHost())
				.setPort(getMailPort());

		if (StringUtils.isNotBlank(getMailUsername())) {
			builder.setUserInfo(getMailUsername());
			if (StringUtils.isNotBlank(getMailPassword())) {
				builder.setParameter("password", getMailPassword());
			}
		}

		URI endpointURI = builder.build();

		return getContext().getEndpoint(endpointURI.toString());
	}

	/**
	 * Sets the registry reference ID (Spring bean name) of the transaction policy to use in this route. May be {@code null} if the default
	 * transaction (PROPAGATION_REQUIRED) is sufficient.
	 *
	 * @param transactionPropagationRef the reference to the transaction policy bean, or {@code null} to use the default transaction policy
	 */
	public void setTransactionPropagationRef(final String transactionPropagationRef) {
		this.transactionPropagationRef = transactionPropagationRef;
	}

	protected String getTransactionPropagationRef() {
		return transactionPropagationRef;
	}

	public void setEmailDataFormat(final DataFormat emailDataFormat) {
		this.emailDataFormat = emailDataFormat;
	}

	protected DataFormat getEmailDataFormat() {
		return emailDataFormat;
	}

	/**
	 * Sets the registry reference ID (Spring bean name) of the retry redelivery policy to use in this route.
	 *
	 * @param redeliveryPolicyRef the reference to the redelivery policy bean
	 */
	public void setRedeliveryPolicyRef(final String redeliveryPolicyRef) {
		this.redeliveryPolicyRef = redeliveryPolicyRef;
	}

	protected String getRedeliveryPolicyRef() {
		return redeliveryPolicyRef;
	}

	public void setDeadLetterEndpoint(final Endpoint deadLetterEndpoint) {
		this.deadLetterEndpoint = deadLetterEndpoint;
	}

	protected Endpoint getDeadLetterEndpoint() {
		return deadLetterEndpoint;
	}

	public void setSmtpScheme(final String smtpScheme) {
		this.smtpScheme = smtpScheme;
	}

	protected String getSmtpScheme() {
		return smtpScheme;
	}

	public void setMailHost(final String mailHost) {
		this.mailHost = mailHost;
	}

	protected String getMailHost() {
		return mailHost;
	}

	public void setMailPort(final Integer mailPort) {
		this.mailPort = mailPort;
	}

	protected Integer getMailPort() {
		return mailPort;
	}

	public void setMailUsername(final String mailUsername) {
		this.mailUsername = mailUsername;
	}

	protected String getMailUsername() {
		return mailUsername;
	}

	public void setMailPassword(final String mailPassword) {
		this.mailPassword = mailPassword;
	}

	protected String getMailPassword() {
		return mailPassword;
	}

	protected String getPostRouteEndpoint() {
		return postRouteEndpoint;
	}

	public void setPostRouteEndpoint(final String postRouteEndpoint) {
		this.postRouteEndpoint = postRouteEndpoint;
	}

	public void setAttachmentProcessor(final Processor attachmentProcessor) {
		this.attachmentProcessor = attachmentProcessor;
	}

	protected Processor getAttachmentProcessor() {
		return attachmentProcessor;
	}

}