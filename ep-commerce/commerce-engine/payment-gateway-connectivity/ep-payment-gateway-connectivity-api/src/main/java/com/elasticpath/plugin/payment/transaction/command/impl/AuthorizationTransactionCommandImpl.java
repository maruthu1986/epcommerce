/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.transaction.command.impl;

import com.elasticpath.plugin.payment.PaymentGatewayPlugin;
import com.elasticpath.plugin.payment.capabilities.PreAuthorizeCapability;
import com.elasticpath.plugin.payment.capabilities.TokenAuthorizationCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.exceptions.PaymentOperationNotSupportedException;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.command.AuthorizationTransactionCommand;

/**
 * Command used to execute an authorization transaction against a specified gateway.
 */
public final class AuthorizationTransactionCommandImpl implements AuthorizationTransactionCommand {
	private final PaymentGatewayPlugin paymentGatewayPlugin;
	private final AddressDto billingAddress;
	private final OrderShipmentDto orderShipment;
	private final AuthorizationTransactionRequest authorizationTransactionRequest;
	
	/**
	 * Constructor.
	 *
	 * @param builder the {@link AuthorizationTransactionCommandImpl} builder.
	 */
	AuthorizationTransactionCommandImpl(final BuilderImpl builder) {
		paymentGatewayPlugin = builder.paymentGatewayPlugin;
		billingAddress = builder.billingAddress;
		orderShipment = builder.orderShipment;
		authorizationTransactionRequest = builder.authorizationTransactionRequest;
	}

	@Override
	public AuthorizationTransactionResponse execute() {
		PreAuthorizeCapability authorizationCapability = paymentGatewayPlugin.getCapability(PreAuthorizeCapability.class);
		if (authorizationCapability != null) {
			return authorizationCapability.preAuthorize(authorizationTransactionRequest, billingAddress, orderShipment);
		}
		TokenAuthorizationCapability tokenAuthCapability = paymentGatewayPlugin.getCapability(TokenAuthorizationCapability.class);
		if (tokenAuthCapability != null) {
			return tokenAuthCapability.preAuthorize(authorizationTransactionRequest);
		}
		throw new PaymentOperationNotSupportedException("Server-side authorization is not supported.");
	}
	
	/**
	 * {@link AuthorizationTransactionCommandImpl} builder.
	 */
	static class BuilderImpl implements AuthorizationTransactionCommand.Builder {
		private PaymentGatewayPlugin paymentGatewayPlugin;
		private AddressDto billingAddress;
		private OrderShipmentDto orderShipment;
		private AuthorizationTransactionRequest authorizationTransactionRequest;
		
		@Override
		public Builder setPaymentGatewayPlugin(final PaymentGatewayPlugin paymentGatewayPlugin) {
			this.paymentGatewayPlugin = paymentGatewayPlugin;
			return this;
		}

		@Override
		public Builder setOrderShipment(final OrderShipmentDto orderShipment) {
			this.orderShipment = orderShipment;
			return this;
		}

		@Override
		public Builder setBillingAddress(final AddressDto billingAddress) {
			this.billingAddress = billingAddress;
			return this;
		}
		
		@Override
		public Builder setAuthorizationTransactionRequest(final AuthorizationTransactionRequest authorizationTransactionRequest) {
			this.authorizationTransactionRequest = authorizationTransactionRequest;
			return this;
		}
		
		@Override
		public AuthorizationTransactionCommand build() {
			AuthorizationTransactionCommandImpl command = new AuthorizationTransactionCommandImpl(this);
			
			if (command.authorizationTransactionRequest == null) {
				throw new IllegalStateException("The Authorization Request needs to be set");
			}
			
			if (command.paymentGatewayPlugin == null) {
				throw new IllegalStateException("The payment gateway plugin invoker needs to be set");
			}

			return command;
		}

	}
}
