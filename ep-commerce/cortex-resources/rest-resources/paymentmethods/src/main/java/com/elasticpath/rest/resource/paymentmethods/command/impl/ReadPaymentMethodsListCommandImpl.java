/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.commons.paymentmethod.rel.PaymentMethodCommonsConstants;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.alias.DefaultPaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.command.ReadPaymentMethodsListCommand;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Strategy to lookup paymentmethods for a profile.
 */
@Named
public final class ReadPaymentMethodsListCommandImpl implements ReadPaymentMethodsListCommand {

	private final String resourceServerName;
	private final ResourceOperationContext operationContext;
	private final ProfilesUriBuilderFactory profilesUriBuilderFactory;
	private final PaymentMethodLookup paymentMethodLookup;
	private final DefaultPaymentMethodLookup defaultPaymentMethodLookup;

	private String scope;


	/**
	 * Instantiates a new read payment method list strategy.
	 *
	 * @param resourceServerName the root resource name
	 * @param operationContext the resource operation context
	 * @param profilesUriBuilderFactory the profiles uri builder factory
	 * @param paymentMethodLookup the payment method lookup
	 * @param defaultPaymentMethodLookup the default payment method lookup
	 */
	@Inject
	public ReadPaymentMethodsListCommandImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext,
			@Named("profilesUriBuilderFactory")
			final ProfilesUriBuilderFactory profilesUriBuilderFactory,
			@Named("paymentMethodLookup")
			final PaymentMethodLookup paymentMethodLookup,
			@Named("defaultPaymentMethodLookup")
			final DefaultPaymentMethodLookup defaultPaymentMethodLookup) {

		this.resourceServerName = resourceServerName;
		this.operationContext = operationContext;
		this.profilesUriBuilderFactory = profilesUriBuilderFactory;
		this.paymentMethodLookup = paymentMethodLookup;
		this.defaultPaymentMethodLookup = defaultPaymentMethodLookup;
	}


	@Override
	public ExecutionResult<ResourceState<LinksEntity>> execute() {

		String profileId = operationContext.getUserIdentifier();
		LinksEntity linksEntity = LinksEntity.builder()
				.withName(PaymentMethodCommonsConstants.PAYMENT_METHOD_LINKS_NAME)
				.build();

		// add self link
		String selfUri = URIUtil.format(resourceServerName, scope);
		Self self = SelfFactory.createSelf(selfUri);


		Collection<ResourceLink> links = new ArrayList<>();
		// add profile link
		String profileUri = profilesUriBuilderFactory.get()
				.setProfileId(Base32Util.encode(profileId))
				.setScope(scope)
				.build();
		ResourceLink profileLink = ResourceLinkFactory.create(profileUri, ProfilesMediaTypes.PROFILE.id(), PaymentMethodRels.PROFILE_REL,
				PaymentMethodRels.PAYMENTMETHODS_REV);
		links.add(profileLink);

		Collection<ResourceLink> paymentMethodLinks;
		try {
			paymentMethodLinks = Assign.ifSuccessful(
					paymentMethodLookup.getPaymentMethodLinksForUser(scope, profileId)
			);
		} catch (BrokenChainException bce) {
			paymentMethodLinks = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, Collections.<ResourceLink>emptyList());
		}
		links.addAll(paymentMethodLinks);

		try {
			ExecutionResult<ResourceLink> defaultPaymentMethodElementLinkResult =
					defaultPaymentMethodLookup.getDefaultPaymentMethodElementLink(scope, profileId);

			if (defaultPaymentMethodElementLinkResult.isSuccessful()) {
				links.add(defaultPaymentMethodElementLinkResult.getData());
			}
		} catch (BrokenChainException bce) {

		}

		ResourceState<LinksEntity> paymentMethodListRepresentation = ResourceState.Builder.create(linksEntity)
				.withScope(scope)
				.withSelf(self)
				.addingLinks(links)
				.build();

		return ExecutionResultFactory.createReadOK(paymentMethodListRepresentation);
	}

	/**
	 * Read payment method list command builder.
	 */
	@Named("readPaymentMethodsListCommandBuilder")
	public static class BuilderImpl implements ReadPaymentMethodsListCommand.Builder {

		private final ReadPaymentMethodsListCommandImpl cmd;

		/**
		 * Constructor for injection.
		 *
		 * @param cmd the cmd
		 */
		@Inject
		public BuilderImpl(final ReadPaymentMethodsListCommandImpl cmd) {
			this.cmd = cmd;
		}

		@Override
		public ReadPaymentMethodsListCommand build() {
			assert cmd.scope != null : "The scope must not be null";
			return cmd;
		}

		@Override
		public Builder setScope(final String scope) {
			cmd.scope = scope;
			return this;
		}
	}
}
