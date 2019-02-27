/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.handler;

import org.apache.camel.Endpoint;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.DataFormat;

import com.elasticpath.email.producer.api.EmailProducer;
import com.elasticpath.messaging.EventMessagePredicate;

/**
 * Route that consumes an {@link com.elasticpath.messaging.EventMessage EventMessage} and prepares a corresponding email message for sending.
 */
public class EventMessageTriggeredEmailRouteBuilder extends RouteBuilder {

	private Endpoint incomingEndpoint;

	private Endpoint outgoingEndpoint;

	private String transactionPropagationRef;

	private EventMessagePredicate eventMessagePredicateFilter;

	private DataFormat emailDataFormat;

	private DataFormat eventMessageDataFormat;

	private EmailProducer emailProducer;

	private Predicate emailEnabledPredicate;

	private String routeId;

	@Override
	public void configure() throws Exception {
		from(getIncomingEndpoint())
				.routeId(getRouteId())
				.transacted(getTransactionPropagationRef())

				.log(LoggingLevel.DEBUG, getClass().getName(), "Received message ${body}")

				.filter().method(getEmailEnabledPredicate())
					.unmarshal(getEventMessageDataFormat())

					.filter().method(getEventMessagePredicateFilter())
						.log(LoggingLevel.DEBUG, getClass().getName(), "Preparing email for GUID [${body.guid}] and data [${body.data}]")

						.bean(getEmailProducer(), "createEmails(${body.guid}, ${body.data})")

						.split().body()
							.marshal(getEmailDataFormat()).convertBodyTo(String.class)

							.log(LoggingLevel.DEBUG, getClass().getName(), "Queueing email message ${body}")

							.to(getOutgoingEndpoint());
	}

	public void setIncomingEndpoint(final Endpoint incomingEndpoint) {
		this.incomingEndpoint = incomingEndpoint;
	}

	protected Endpoint getIncomingEndpoint() {
		return incomingEndpoint;
	}

	public void setOutgoingEndpoint(final Endpoint outgoingEndpoint) {
		this.outgoingEndpoint = outgoingEndpoint;
	}

	protected Endpoint getOutgoingEndpoint() {
		return outgoingEndpoint;
	}

	public void setEventMessageDataFormat(final DataFormat eventMessageDataFormat) {
		this.eventMessageDataFormat = eventMessageDataFormat;
	}

	protected DataFormat getEventMessageDataFormat() {
		return eventMessageDataFormat;
	}

	public void setEmailDataFormat(final DataFormat emailDataFormat) {
		this.emailDataFormat = emailDataFormat;
	}

	protected DataFormat getEmailDataFormat() {
		return emailDataFormat;
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

	public void setEmailEnabledPredicate(final Predicate emailEnabledPredicate) {
		this.emailEnabledPredicate = emailEnabledPredicate;
	}

	protected Predicate getEmailEnabledPredicate() {
		return emailEnabledPredicate;
	}

	public void setEventMessagePredicateFilter(final EventMessagePredicate eventMessagePredicateFilter) {
		this.eventMessagePredicateFilter = eventMessagePredicateFilter;
	}

	protected EventMessagePredicate getEventMessagePredicateFilter() {
		return eventMessagePredicateFilter;
	}

	public void setEmailProducer(final EmailProducer emailProducer) {
		this.emailProducer = emailProducer;
	}

	protected EmailProducer getEmailProducer() {
		return emailProducer;
	}

	protected String getRouteId() {
		return routeId;
	}

	public void setRouteId(final String routeId) {
		this.routeId = routeId;
	}

}
