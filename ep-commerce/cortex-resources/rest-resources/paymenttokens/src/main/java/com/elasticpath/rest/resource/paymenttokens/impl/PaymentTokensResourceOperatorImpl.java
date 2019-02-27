/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.read.ReadResourceCommand;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.paymenttokens.PaymentTokenFormForOwnerLookup;
import com.elasticpath.rest.resource.paymenttokens.PaymentTokenWriter;
import com.elasticpath.rest.resource.paymenttokens.rels.PaymentTokensResourceRels;
import com.elasticpath.rest.resource.paymenttokens.validator.PaymentTokenValidator;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Processes resource operations on payment tokens.
 */
@Singleton
@Named("paymentTokensResourceOperator")
@Path(ResourceName.PATH_PART)
public final class PaymentTokensResourceOperatorImpl implements ResourceOperator {

	private final PaymentTokenFormForOwnerLookup paymentTokenFormForOwnerLookup;
	private final ProfilesUriBuilderFactory profilesUriBuilderFactory;
	private final ResourceOperationContext resourceOperationContext;
	private final PaymentTokenWriter paymentTokenWriter;
	private final ReadResourceCommandBuilderProvider readResourceCommandBuilderProvider;
	private final PaymentTokenValidator paymentTokenValidator;

	/**
	 *  @param paymentTokenFormForOwnerLookup reads PaymentToken Form For Owner
	 * @param profilesUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory}
	 * @param resourceOperationContext the {@link com.elasticpath.rest.resource.ResourceOperationContext}
	 * @param readResourceCommandBuilderProvider for reading another resource manually by uri
	 * @param paymentTokenWriter the {@link com.elasticpath.rest.resource.paymenttokens.PaymentTokenWriter}
	 * @param paymentTokenValidator the payment token validator
	 */
	@Inject
	PaymentTokensResourceOperatorImpl(
			@Named("paymentTokenFormForOwnerLookup")
			final PaymentTokenFormForOwnerLookup paymentTokenFormForOwnerLookup,
			@Named("profilesUriBuilderFactory")
			final ProfilesUriBuilderFactory profilesUriBuilderFactory,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("readResourceCommandBuilderProvider")
			final ReadResourceCommandBuilderProvider readResourceCommandBuilderProvider,
			@Named("paymentTokenWriter")
			final PaymentTokenWriter paymentTokenWriter,
			@Named("paymentTokenValidator")
			final PaymentTokenValidator paymentTokenValidator) {
		this.paymentTokenFormForOwnerLookup = paymentTokenFormForOwnerLookup;
		this.profilesUriBuilderFactory = profilesUriBuilderFactory;
		this.resourceOperationContext = resourceOperationContext;
		this.paymentTokenWriter = paymentTokenWriter;
		this.readResourceCommandBuilderProvider = readResourceCommandBuilderProvider;
		this.paymentTokenValidator = paymentTokenValidator;
	}

	/**
	 * Handles the CREATE of a payment token for an order.
	 *
	 * @param order  the owning order
	 * @param operation the {@link ResourceOperation}
	 * @return the {@link OperationResult}
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.CREATE)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public OperationResult processCreatePaymentTokenForOrder(
			@AnyResourceUri
			final ResourceState<OrderEntity> order,
			final ResourceOperation operation) {

		PaymentTokenEntity token = getPostedEntity(operation);

		ExecutionResult result = paymentTokenWriter.createPaymentToken(token, order);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles  the CREATE of a payment token for an profile.
	 * @param scope   the scope
	 * @param operation the {@link ResourceOperation}
	 * @return the {@link OperationResult}
	 */
	@Path(Scope.PATH_PART)
	@OperationType(Operation.CREATE)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public OperationResult processCreatePaymentTokenForProfile(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		ResourceState<ProfileEntity> profile = readProfile(getProfileUri(scope));

		PaymentTokenEntity token = getPostedEntity(operation);

		ExecutionResult result = paymentTokenWriter.createPaymentToken(token, profile);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);

	}

	/**
	 * Handles reading payment token form for an order.
	 * @param order  the owning order
	 * @param operation the {@link ResourceOperation}
	 * @return the {@link OperationResult}
	 */
	@Path({ AnyResourceUri.PATH_PART, Form.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadPaymentTokenFormForOrder(
			@AnyResourceUri
			final ResourceState<OrderEntity> order,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<PaymentTokenEntity>> result = paymentTokenFormForOwnerLookup.readPaymentTokenForm(
				order.getSelf().getUri(),
				PaymentTokensResourceRels.CREATE_PAYMENT_TOKEN_FOR_ORDER_ACTION_REL);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles reading payment token form for a profile.
	 *
	 * @param scope the scope
	 * @param operation the {@link ResourceOperation}
	 * @return the {@link OperationResult}
	 */
	@Path({ Scope.PATH_PART, Form.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadPaymentTokenFormForProfile(
			@Scope
			final String scope,
			final ResourceOperation operation) {
		//This manual construction of the owner uri for the form is required because of the implicit association of a payment token form to a profile
		//if an owner uri isn't specified.
		String createProfilePaymentTokenUri = URIUtil.format(scope);
		readProfile(getProfileUri(scope));
		ExecutionResult<ResourceState<PaymentTokenEntity>> result = paymentTokenFormForOwnerLookup.readPaymentTokenForm(
				createProfilePaymentTokenUri,
				PaymentTokensResourceRels.CREATE_PAYMENT_TOKEN_FOR_PROFILE_ACTION_REL);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}


	private String getProfileUri(final String scope) {
		return profilesUriBuilderFactory.get()
				.setScope(scope)
				.setProfileId(Base32Util.encode(resourceOperationContext.getUserIdentifier()))
				.build();
	}

	@SuppressWarnings("unchecked")
	private ResourceState<ProfileEntity> readProfile(final String profileUri) {
		ReadResourceCommand readOrderCommand = readResourceCommandBuilderProvider.get()
				.setReadLinks(false)
				.setResourceUri(profileUri)
				.setExpectedType(ProfilesMediaTypes.PROFILE.id())
				.build();
		ResourceState<?> resourceState = Assign.ifSuccessful(readOrderCommand.execute());
		ProfileEntity profileEntity = ResourceTypeFactory.adaptResourceEntity(resourceState.getEntity(), ProfileEntity.class);
		return ResourceState.Builder.create(profileEntity)
				.withScope(resourceState.getScope())
				.withSelf(resourceState.getSelf())
				.build();
	}

	private PaymentTokenEntity getPostedEntity(final ResourceOperation operation) {
		ResourceState<?> incomingToken = operation.getResourceState();
		Ensure.notNull(incomingToken, OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		PaymentTokenEntity tokenEntity = ResourceTypeFactory.adaptResourceEntity(incomingToken.getEntity(), PaymentTokenEntity.class);
		Ensure.successful(paymentTokenValidator.validate(tokenEntity));
		return tokenEntity;
	}
}
