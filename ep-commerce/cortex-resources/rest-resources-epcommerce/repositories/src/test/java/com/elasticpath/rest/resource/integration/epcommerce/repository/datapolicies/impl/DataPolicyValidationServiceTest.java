/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.USER_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyRepositoryTest.DATA_POLICY_NAME;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyRepositoryTest.KEY_1;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyRepositoryTest.POLICY_GUID_1;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyRepositoryTest.SCOPE;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyValidationServiceImpl.POLICY_NAME_FIELD;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyValidationServiceImpl
		.POLICY_REFERENCE_KEY_FIELD;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Sets;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.rest.advise.LinkedMessage;
import com.elasticpath.rest.definition.datapolicies.DataPoliciesIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyConsentFormIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.StructuredErrorMessageIdConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.DataPolicyRepository;
import com.elasticpath.rest.schema.StructuredMessageTypes;

/**
 * Test for {@link DataPolicyValidationServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataPolicyValidationServiceTest {

	private static final String REGISTRATION_DATA_POLICY = "Required Registration Personal Details";
	private static final String ADDRESS_DATA_POLICY = "Saved Addresses";
	private static final String CUSTOMER_BILLING_ADDRESS = "CUSTOMER_BILLING_ADDRESS";
	private static final String CUSTOMER_PROFILE = "CUSTOMER_PROFILE";
	private static final List<String> DATA_POINT_LOCATIONS = Arrays.asList(CUSTOMER_BILLING_ADDRESS, CUSTOMER_PROFILE);

	private DataPolicy dataPolicy1;
	private DataPolicy dataPolicy2;

	@Mock
	private DataPolicyRepository dataPolicyRepository;

	@Mock
	private SubjectAttribute subjectAttribute;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ResourceOperationContext resourceOperationContext;

	@InjectMocks
	private DataPolicyValidationServiceImpl validationService;

	@Before
	public void initialize() {
		DataPoint dataPoint1 = createMockDataPoint(CUSTOMER_BILLING_ADDRESS);
		DataPoint dataPoint2 = createMockDataPoint(CUSTOMER_PROFILE);

		dataPolicy1 = createMockDataPolicy(ADDRESS_DATA_POLICY, POLICY_GUID_1, KEY_1, Arrays.asList(dataPoint1, dataPoint2)
		);
		dataPolicy2 = createMockDataPolicy(REGISTRATION_DATA_POLICY, POLICY_GUID_1, KEY_1, Collections.singletonList(dataPoint2)
		);

		when(resourceOperationContext.getSubject().getAttributes()).thenReturn(Collections.singletonList(subjectAttribute));
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
	}

	@Test
	public void validateAddressReturnsEmptyWhenPolicyIsAccepted() {
		List<String> headers = Collections.singletonList(DATA_POLICY_NAME);
		when(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.singletonList(subjectAttribute))).thenReturn(headers);
		when(dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(headers, SCOPE.getValue())).thenReturn(Observable.just(dataPolicy1));
		when(dataPolicyRepository.customerHasGivenConsentForAtLeastOneDataPolicy(USER_ID, Sets.newHashSet(dataPolicy1))).thenReturn(true);

		validationService.validate(SCOPE, DATA_POINT_LOCATIONS)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void validateAddressReturnsEmptyWhenNoRelevantHeaderPresent() {
		when(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.singletonList(subjectAttribute))).thenReturn(Collections.emptyList());
		when(dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(Collections.emptyList(), SCOPE.getValue()))
				.thenReturn(Observable.empty());

		validationService.validate(SCOPE, DATA_POINT_LOCATIONS)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void validateAddressReturnsLinkedMessageWhenPolicyIsNotAccepted() {
		List<String> headers = Collections.singletonList(DATA_POLICY_NAME);
		when(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.singletonList(subjectAttribute))).thenReturn(headers);
		when(dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(headers, SCOPE.getValue())).thenReturn(Observable.just(dataPolicy1));

		validationService.validate(SCOPE, DATA_POINT_LOCATIONS)
				.test()
				.assertNoErrors()
				.assertValueCount(1)
				.assertValue(this::validateLinkedMessage);
	}

	@Test
	public void validateAddressReturnsLinkedMessagesForAllNotAccepted() {
		List<String> headers = Collections.singletonList(DATA_POLICY_NAME);
		when(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.singletonList(subjectAttribute))).thenReturn(headers);
		when(dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(headers, SCOPE.getValue()))
				.thenReturn(Observable.just(dataPolicy1, dataPolicy2));

		validationService.validate(SCOPE, DATA_POINT_LOCATIONS)
				.test()
				.assertNoErrors()
				.assertValueCount(2)
				.assertValueAt(0, this::validateLinkedMessage)
				.assertValueAt(1, this::validateLinkedMessage);
	}

	@Test
	public void validateAddressReturnsLinkedMessageWhenPolicyIsNotAcknowledged() {
		List<String> headers = Collections.singletonList(DATA_POLICY_NAME);
		when(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.singletonList(subjectAttribute))).thenReturn(headers);
		when(dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(headers, SCOPE.getValue())).thenReturn(Observable.just(dataPolicy1));

		validationService.validate(SCOPE, DATA_POINT_LOCATIONS)
				.test()
				.assertNoErrors()
				.assertValueCount(1)
				.assertValue(this::validateLinkedMessage);
	}

	private boolean validateLinkedMessage(final LinkedMessage<DataPolicyConsentFormIdentifier> linkedMessage) {
		return linkedMessage.getLinkedIdentifier().get().equals(buildDataPolicyConsentFormIdentifier(dataPolicy1, SCOPE))
				&& linkedMessage.getType().equals(StructuredMessageTypes.NEEDINFO)
				&& linkedMessage.getId().equals(StructuredErrorMessageIdConstants.NEED_DATA_POLICY_CONSENT)
				&& linkedMessage.getDebugMessage().contains("Need user consent for data policy")
				&& linkedMessage.getData().containsKey(POLICY_NAME_FIELD)
				&& linkedMessage.getData().containsKey(POLICY_REFERENCE_KEY_FIELD);
	}

	private DataPolicyIdentifier buildDataPolicyIdentifier(final DataPolicy dataPolicy, final IdentifierPart<String> scope) {
		return DataPolicyIdentifier.builder()
				.withPolicyId(StringIdentifier.of(dataPolicy.getGuid()))
				.withDataPolicies(buildDataPoliciesIdentifier(scope))
				.build();
	}

	private DataPoliciesIdentifier buildDataPoliciesIdentifier(final IdentifierPart<String> scope) {
		return DataPoliciesIdentifier.builder().withScope(scope).build();
	}

	private DataPolicyConsentFormIdentifier buildDataPolicyConsentFormIdentifier(final DataPolicy dataPolicy, final IdentifierPart<String> scope) {
		return DataPolicyConsentFormIdentifier.builder()
				.withDataPolicy(buildDataPolicyIdentifier(dataPolicy, scope))
				.build();
	}

	private DataPoint createMockDataPoint(final String location) {
		DataPoint dataPoint = mock(DataPoint.class);
		when(dataPoint.getDataLocation()).thenReturn(location);
		return dataPoint;
	}

	private DataPolicy createMockDataPolicy(final String policyName, final String guid, final String referenceKey,
											final List<DataPoint> dataPoints) {
		DataPolicy dataPolicy = mock(DataPolicy.class, RETURNS_DEEP_STUBS);
		when(dataPolicy.getState().getOrdinal()).thenReturn(DataPolicyState.ACTIVE_ORDINAL);
		when(dataPolicy.getPolicyName()).thenReturn(policyName);
		when(dataPolicy.getStartDate().before(any(Date.class))).thenReturn(true);
		when(dataPolicy.getGuid()).thenReturn(guid);
		when(dataPolicy.getReferenceKey()).thenReturn(referenceKey);
		when(dataPolicy.getDataPoints()).thenReturn(dataPoints);
		return dataPolicy;
	}
}
