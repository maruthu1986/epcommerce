/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationContextFactory;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentmethodsMediaTypes;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.commons.paymentmethod.rel.PaymentMethodCommonsConstants;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.alias.DefaultPaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.command.ReadPaymentMethodsListCommand;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilder;
import com.elasticpath.rest.schema.uri.ProfilesUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests the {@link ReadPaymentMethodsListCommandImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadPaymentMethodsListCommandImplTest {
	private static final String RESOURCE_NAME = "paymentMethods";
	private static final String SCOPE = "mockScope";
	private static final int FOUR_EXPECTED_LINKS = 4;
	private static final String USER_ID = UUID.randomUUID().toString();
	private static final String PROFILE_ID = Base32Util.encode(USER_ID);
	private static final String PROFILE_URI = "/mock/profile/uri";
	private static final String TEST_REPRESENTATION_TYPE_TWO = "testRepresentationTypewTwo";
	private static final String TEST_REPRESENTATION_TYPE_ONE = "testRepresentationType";
	private static final String PAYMENT_METHOD_URI = "/paymentMethodUri";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private ProfilesUriBuilderFactory mockProfilesUriBuilderFactory;
	@Mock
	private PaymentMethodLookup mockPaymentMethodLookup;
	@Mock
	private DefaultPaymentMethodLookup mockDefaultPaymentMethodLookup;

	private final Subject testSubject = TestSubjectFactory.createWithScopeAndUserId(SCOPE, USER_ID);

	private ReadPaymentMethodsListCommand readPaymentMethodsListCommand;
	private ResourceLink expectedPaymentMethodLinkOne;
	private Collection<ResourceLink> expectedPaymentMethodLinks;
	private ResourceLink expectedProfileLink;


	@Before
	public void setUp() {
		readPaymentMethodsListCommand = createReadPaymentMethodsListCommand();
		ProfilesUriBuilder profilesUriBuilder = createProfilesUriBuilder();
		when(mockProfilesUriBuilderFactory.get()).thenReturn(profilesUriBuilder);

		expectedPaymentMethodLinkOne = getFirstTestPaymentMethodElement();
		ResourceLink expectedPaymentMethodLinkTwo = getSecondTestPaymentMethodElement();
		ResourceLink defaultPaymentMethodLink = createDefaultPaymentMethodElementLink();

		expectedPaymentMethodLinks = Arrays.asList(expectedPaymentMethodLinkOne, expectedPaymentMethodLinkTwo);
		shouldPaymentMethodLookupFindPaymentMethods(expectedPaymentMethodLinks);
		shouldDefaultPaymentMethodLookupFindDefaultPaymentMethod(defaultPaymentMethodLink);

		expectedProfileLink = createExpectedProfileLink();
	}

	@Test
	public void ensureSuccessfulRetrievalOfPaymentMethodListRepresentationWithDefault() {
		ExecutionResult<ResourceState<LinksEntity>> result = readPaymentMethodsListCommand.execute();

		assertResourceState(result.getData())
				.self(createExpectedSelf())
				.linkCount("The lookup should contain 4 links for the payment methods, default, and profile link.", FOUR_EXPECTED_LINKS)
				.containsLink(expectedProfileLink)
				.containsLinks(expectedPaymentMethodLinks);
	}

	@Test
	public void ensureSuccessfulRetrievalOfPaymentMethodListRepresentationWithNoDefault() {
		List<ResourceLink> expectedPaymentMethodLinks = Arrays.asList(expectedPaymentMethodLinkOne);
		shouldPaymentMethodLookupFindPaymentMethods(expectedPaymentMethodLinks);
		shouldNotFindDefaultPaymentMethod();

		ExecutionResult<ResourceState<LinksEntity>> result = readPaymentMethodsListCommand.execute();

		assertResourceState(result.getData())
				.self(createExpectedSelf())
				.linkCount("The lookup should contain 1 links for the payment methods and profile link.", 2)
				.containsLink(expectedProfileLink)
				.containsLinks(expectedPaymentMethodLinks);
	}

	@Test
	public void ensureExecuteWithPaymentMethodLinksLookupFailureOtherThanNotFoundIsPropagated() {
		when(mockPaymentMethodLookup.getPaymentMethodLinksForUser(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<Collection<ResourceLink>>createBadURI(""));
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_URI));

		readPaymentMethodsListCommand.execute();
	}

	@Test
	public void ensureNoPaymentMethodLinksReturnedWhenPaymentMethodLookupReturnsNotFound() {
		when(mockPaymentMethodLookup.getPaymentMethodLinksForUser(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.<Collection<ResourceLink>>createNotFound());
		shouldNotFindDefaultPaymentMethod();

		ExecutionResult<ResourceState<LinksEntity>> result = readPaymentMethodsListCommand.execute();

		assertResourceState(result.getData())
				.linkCount("The lookup should contain 1 link for the profile.", 1);
	}

	@Test
	public void ensureExecuteWithNoPaymentMethodsReturnsRepresentationWithOneLinkToProfiles() {
		ResourceLink expectedProfileLink = createExpectedProfileLink();

		shouldPaymentMethodLookupFindPaymentMethods(Collections.<ResourceLink>emptyList());
		shouldNotFindDefaultPaymentMethod();

		ExecutionResult<ResourceState<LinksEntity>> result = readPaymentMethodsListCommand.execute();

		assertResourceState(result.getData())
				.self(createExpectedSelf())
				.linkCount("The lookup should contain 1 link for the profile.", 1)
				.containsLink(expectedProfileLink);
	}

	@Test
	public void ensureNameIsSetCorrectlyOnPaymentMethodLinksRepresentation() {
		ExecutionResult<ResourceState<LinksEntity>> result = readPaymentMethodsListCommand.execute();

		assertEquals("The internal name field should be set on the links representation",
				PaymentMethodCommonsConstants.PAYMENT_METHOD_LINKS_NAME, result.getData().getEntity().getName());
	}

	@Test
	public void ensureScopeIsSetCorrectlyOnPaymentMethodLinksRepresentation() {
		ExecutionResult<ResourceState<LinksEntity>> result = readPaymentMethodsListCommand.execute();

		assertEquals("The scope field should be set on the links representation",
				SCOPE, result.getData().getScope());
	}

	private ResourceLink getFirstTestPaymentMethodElement() {
		return ElementListFactory.createElementOfList(PAYMENT_METHOD_URI, TEST_REPRESENTATION_TYPE_ONE);
	}

	private ResourceLink getSecondTestPaymentMethodElement() {
		return ElementListFactory.createElementOfList(PAYMENT_METHOD_URI, TEST_REPRESENTATION_TYPE_TWO);
	}

	private Self createExpectedSelf() {
		String selfUri = URIUtil.format(RESOURCE_NAME, SCOPE);
		return SelfFactory.createSelf(selfUri);
	}

	private ResourceLink createDefaultPaymentMethodElementLink() {
		String paymentMethodUri = URIUtil.format(RESOURCE_NAME, SCOPE, Default.URI_PART);
		return ResourceLinkFactory.create(paymentMethodUri, PaymentmethodsMediaTypes.PAYMENT_TOKEN.id(),
				PaymentMethodRels.DEFAULT_REL, ListElementRels.LIST);
	}

	private ResourceLink createExpectedProfileLink() {
		return ResourceLinkFactory.create(PROFILE_URI, ProfilesMediaTypes.PROFILE.id(), PaymentMethodRels.PROFILE_REL,
				PaymentMethodRels.PAYMENTMETHODS_REV);
	}


	private ProfilesUriBuilder createProfilesUriBuilder() {
		ProfilesUriBuilder mockProfilesUriBuilder = Mockito.mock(ProfilesUriBuilder.class);
		when(mockProfilesUriBuilder.setProfileId(PROFILE_ID)).thenReturn(mockProfilesUriBuilder);
		when(mockProfilesUriBuilder.setScope(SCOPE)).thenReturn(mockProfilesUriBuilder);
		when(mockProfilesUriBuilder.build()).thenReturn(PROFILE_URI);
		return mockProfilesUriBuilder;
	}

	private void shouldPaymentMethodLookupFindPaymentMethods(final Collection<ResourceLink> expectedPaymentMethodLinks) {
		when(mockPaymentMethodLookup.getPaymentMethodLinksForUser(SCOPE, USER_ID)).thenReturn(
				ExecutionResultFactory.createReadOK(expectedPaymentMethodLinks));
	}

	@SuppressWarnings("unchecked")
	private void shouldNotFindDefaultPaymentMethod() {
		when(mockDefaultPaymentMethodLookup.getDefaultPaymentMethodElementLink(SCOPE, USER_ID))
				.thenThrow(BrokenChainException.class);
	}

	private void shouldDefaultPaymentMethodLookupFindDefaultPaymentMethod(final ResourceLink defaultLink) {
		when(mockDefaultPaymentMethodLookup.getDefaultPaymentMethodElementLink(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(defaultLink));
	}

	private ReadPaymentMethodsListCommand createReadPaymentMethodsListCommand() {
		ResourceOperationContext operationContext = TestResourceOperationContextFactory.create(Operation.CREATE, "/uri", null, testSubject);
		ReadPaymentMethodsListCommandImpl readPaymentMethodsListCommand = new ReadPaymentMethodsListCommandImpl(
				RESOURCE_NAME, operationContext, mockProfilesUriBuilderFactory, mockPaymentMethodLookup, mockDefaultPaymentMethodLookup);

		ReadPaymentMethodsListCommand.Builder builder = new ReadPaymentMethodsListCommandImpl.BuilderImpl(readPaymentMethodsListCommand);
		return builder
				.setScope(SCOPE)
				.build();
	}
}
