/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.DataFormat;

/**
 * Configures a route from an incoming endpoint to an outgoing endpoint.
 */
public class EventRouteBuilder extends RouteBuilder {

	private Endpoint incomingEndpoint;

	private Endpoint outgoingEndpoint;

	private String transactionPropagationRef;

	private DataFormat eventMessageDataFormat;

	@Override
	public void configure() throws Exception {
		from(incomingEndpoint)
				.transacted(transactionPropagationRef)
				.marshal(eventMessageDataFormat).convertBodyTo(String.class)
				.to(outgoingEndpoint);
	}

	/**
	 * Sets the incoming endpoint.
	 * 
	 * @param endpoint the new incoming endpoint
	 */
	public void setIncomingEndpoint(final Endpoint endpoint) {
		this.incomingEndpoint = endpoint;
	}

	/**
	 * Sets the outgoing endpoint.
	 * 
	 * @param endpointUri the new outgoing endpoint
	 */
	public void setOutgoingEndpoint(final Endpoint endpointUri) {
		this.outgoingEndpoint = endpointUri;
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

	/**
	 * Sets the data format to use when marshalling and unmarshalling {@link com.elasticpath.messaging.EventMessage EventMessage} instances.
	 * 
	 * @param eventMessageDataFormat the data format
	 */
	public void setEventMessageDataFormat(final DataFormat eventMessageDataFormat) {
		this.eventMessageDataFormat = eventMessageDataFormat;
	}

}
