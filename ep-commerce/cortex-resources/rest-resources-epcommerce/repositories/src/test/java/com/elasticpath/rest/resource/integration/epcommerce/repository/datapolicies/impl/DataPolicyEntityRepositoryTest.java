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
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyRepositoryTest.KEY_2;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyRepositoryTest.POLICY_GUID_1;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyRepositoryTest.POLICY_GUID_2;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl.DataPolicyRepositoryTest.SCOPE;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.datapolicies.DataPoliciesIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyConsentFormIdentifier;
import com.elasticpath.rest.definition.datapolicies.DataPolicyEntity;
import com.elasticpath.rest.definition.datapolicies.DataPolicyIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.DataPolicyRepository;

/**
 * Test for {@link DataPolicyEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataPolicyEntityRepositoryTest {

	@Mock
	private DataPolicyRepository dataPolicyRepository;

	@Mock
	private CustomerConsent customerConsent;

	@Mock
	private SubjectAttribute subjectAttribute;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ResourceOperationContext resourceOperationContext;

	private DataPolicy dataPolicy1;

	private DataPolicy dataPolicy2;

	@InjectMocks
	private DataPolicyEntityRepositoryImpl<DataPolicyEntity, DataPolicyIdentifier> dataPolicyEntityRepository;

	@Before
	public void initialize() {
		when(resourceOperationContext.getSubject().getAttributes()).thenReturn(Collections.singletonList(subjectAttribute));
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);

		dataPolicy1 = createMockDataPolicy(POLICY_GUID_1, KEY_1);
		dataPolicy2 = createMockDataPolicy(POLICY_GUID_2, KEY_2);

		ResourceIdentifier dataPolicyIdentifier = createDataPolicyConsentFormIdentifier(dataPolicy1, SCOPE);

		when(resourceOperationContext.getResourceIdentifier()).thenReturn(Optional.of(dataPolicyIdentifier));
	}

	@Test
	public void submitReturnsSubmitResultWithDataPolicyIdentifier() {
		DataPolicyEntity dataPolicyEntity = createDataPolicyEntity(dataPolicy1, Boolean.TRUE.toString());

		when(dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute)))
				.thenReturn(Single.just(dataPolicy1));
		when(dataPolicyRepository.createCustomerConsentForDataPolicy(USER_ID, dataPolicy1, dataPolicyEntity))
				.thenReturn(Single.just(customerConsent));

		SubmitResult<DataPolicyIdentifier> submitResult = SubmitResult.<DataPolicyIdentifier>builder()
				.withStatus(SubmitStatus.UPDATED)
				.withIdentifier(createDataPolicyIdentifier(dataPolicy1, SCOPE))
				.build();

		dataPolicyEntityRepository.submit(dataPolicyEntity, SCOPE)
				.test()
				.assertValue(submitResult);
	}

	@Test
	public void submitWithoutEntityReturns400BadRequest() {
		dataPolicyEntityRepository.submit(null, SCOPE)
				.test()
				.assertError(ResourceOperationFailure.badRequestBody(DataPolicyRepositoryImpl.BOOLEAN_EXPECTED));
	}

	@Test
	public void submitEntityWithoutConsentValueReturns400BadRequest() {
		dataPolicyEntityRepository.submit(createDataPolicyEntity(dataPolicy1, null), SCOPE)
				.test()
				.assertError(ResourceOperationFailure.badRequestBody(DataPolicyRepositoryImpl.BOOLEAN_EXPECTED));
	}

	@Test
	public void findOneReturnsAcceptedDataPolicyWhenCustomerConsentExistsAndConsentGranted() {
		when(dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute)))
				.thenReturn(Single.just(dataPolicy1));
		when(dataPolicyRepository.findCustomerConsentByDataPolicyGuidForCustomer(POLICY_GUID_1, USER_ID)).thenReturn(Maybe.just(customerConsent));
		when(customerConsent.getAction()).thenReturn(ConsentAction.GRANTED);

		dataPolicyEntityRepository.findOne(createDataPolicyIdentifier(dataPolicy1, SCOPE))
				.test()
				.assertNoErrors()
				.assertValues(createDataPolicyEntity(dataPolicy1, Boolean.TRUE.toString()));
	}

	@Test
	public void findOneReturnsUnacceptedDataPolicyWhenCustomerConsentExistsAndConsentRevoked() {
		when(dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute)))
				.thenReturn(Single.just(dataPolicy1));
		when(dataPolicyRepository.findCustomerConsentByDataPolicyGuidForCustomer(POLICY_GUID_1, USER_ID)).thenReturn(Maybe.just(customerConsent));
		when(customerConsent.getAction()).thenReturn(ConsentAction.REVOKED);

		dataPolicyEntityRepository.findOne(createDataPolicyIdentifier(dataPolicy1, SCOPE))
				.test()
				.assertNoErrors()
				.assertValues(createDataPolicyEntity(dataPolicy1, Boolean.FALSE.toString()));
	}

	@Test
	public void findOneReturnsUnacceptedDataPolicyWhenExistingCustomerConsentNotFound() {
		when(dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute)))
				.thenReturn(Single.just(dataPolicy1));
		when(dataPolicyRepository.findCustomerConsentByDataPolicyGuidForCustomer(POLICY_GUID_1, USER_ID)).thenReturn(Maybe.empty());

		dataPolicyEntityRepository.findOne(createDataPolicyIdentifier(dataPolicy1, SCOPE))
				.test()
				.assertNoErrors()
				.assertValues(createDataPolicyEntity(dataPolicy1, Boolean.FALSE.toString()));
	}

	@Test
	public void findOneReturns404IndicatingDataPoliciesNotEnabledForStore() {
		when(dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute)))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(DataPolicyRepositoryImpl.VALUE_NOT_FOUND)));

		dataPolicyEntityRepository.findOne(createDataPolicyIdentifier(dataPolicy1, SCOPE))
				.test()
				.assertError(ResourceOperationFailure.notFound(DataPolicyRepositoryImpl.VALUE_NOT_FOUND));
	}

	@Test
	public void findOneReturns404IndicatingNoDataPolicySegmentHeadersExist() {
		when(dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute)))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(DataPolicyRepositoryImpl.HEADER_NOT_FOUND)));

		dataPolicyEntityRepository.findOne(createDataPolicyIdentifier(dataPolicy1, SCOPE))
				.test()
				.assertError(ResourceOperationFailure.notFound(DataPolicyRepositoryImpl.HEADER_NOT_FOUND));
	}

	@Test
	public void findOneReturns404IndicatingNoActivePolicyExists() {
		when(dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute)))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(DataPolicyRepositoryImpl.ACTIVE_POLICY_NOT_FOUND)));

		dataPolicyEntityRepository.findOne(createDataPolicyIdentifier(dataPolicy1, SCOPE))
				.test()
				.assertError(ResourceOperationFailure.notFound(DataPolicyRepositoryImpl.ACTIVE_POLICY_NOT_FOUND));
	}

	@Test
	public void findAllReturnsDataPolicyIdentifiersObservableRelevantToScope() {
		List<String> headers = Collections.singletonList(DATA_POLICY_NAME);
		when(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.singletonList(subjectAttribute))).thenReturn(headers);
		when(dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(headers, SCOPE.getValue()))
				.thenReturn(Observable.just(dataPolicy1, dataPolicy2));

		dataPolicyEntityRepository.findAll(SCOPE)
				.test()
				.assertNoErrors()
				.assertValues(createDataPolicyIdentifier(dataPolicy1, SCOPE), createDataPolicyIdentifier(dataPolicy2, SCOPE));
	}

	@Test
	public void findAllReturnsEmptyObservableWhenNoDataPoliciesFoundForScope() {
		List<String> headers = Collections.singletonList(DATA_POLICY_NAME);
		when(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.singletonList(subjectAttribute))).thenReturn(headers);
		when(dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(DATA_POLICY_NAME), SCOPE.getValue()))
				.thenReturn(Observable.empty());

		dataPolicyEntityRepository.findAll(SCOPE)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	@Test
	public void findAllReturnsEmptyObservableWhenNoRelevantHeadersExist() {
		when(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.singletonList(subjectAttribute))).thenReturn(Collections.emptyList());
		when(dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(Collections.emptyList(), SCOPE.getValue()))
				.thenReturn(Observable.empty());

		dataPolicyEntityRepository.findAll(SCOPE)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	private DataPolicyIdentifier createDataPolicyIdentifier(final DataPolicy dataPolicy, final IdentifierPart<String> scope) {
		return DataPolicyIdentifier.builder()
				.withPolicyId(StringIdentifier.of(dataPolicy.getGuid()))
				.withDataPolicies(createDataPoliciesIdentifier(scope))
				.build();
	}

	private DataPoliciesIdentifier createDataPoliciesIdentifier(final IdentifierPart<String> scope) {
		return DataPoliciesIdentifier.builder().withScope(scope).build();
	}

	private DataPolicyEntity createDataPolicyEntity(final DataPolicy dataPolicy, final String consentFlag) {
		return DataPolicyEntity.builder()
				.withDataPolicyConsent(consentFlag)
				.withPolicyReferenceKey(dataPolicy.getReferenceKey())
				.withPolicyName(dataPolicy.getPolicyName())
				.build();
	}

	private DataPolicyConsentFormIdentifier createDataPolicyConsentFormIdentifier(final DataPolicy dataPolicy, final StringIdentifier scope) {
		return DataPolicyConsentFormIdentifier.builder()
				.withDataPolicy(createDataPolicyIdentifier(dataPolicy, scope))
				.build();
	}

	private static DataPolicy createMockDataPolicy(final String guid, final String referenceKey) {
		DataPolicy dataPolicy = mock(DataPolicy.class, RETURNS_DEEP_STUBS);
		when(dataPolicy.getState().getOrdinal()).thenReturn(DataPolicyState.ACTIVE_ORDINAL);
		when(dataPolicy.getPolicyName()).thenReturn(POLICY_GUID_1);
		when(dataPolicy.getStartDate().before(any(Date.class))).thenReturn(true);
		when(dataPolicy.getGuid()).thenReturn(guid);
		when(dataPolicy.getReferenceKey()).thenReturn(referenceKey);
		return dataPolicy;
	}
}
