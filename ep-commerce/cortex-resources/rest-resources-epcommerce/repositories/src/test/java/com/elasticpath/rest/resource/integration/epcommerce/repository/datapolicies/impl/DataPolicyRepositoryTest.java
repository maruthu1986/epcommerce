/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.USER_ID;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.datapolicies.DataPolicyEntity;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.resource.integration.epcommerce.repository.datapolicies.DataPolicyRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.service.misc.TimeService;

/**
 * Test for {@link DataPolicyEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataPolicyRepositoryTest {

	static final StringIdentifier SCOPE = StringIdentifier.of("SCOPE");
	static final String DATA_POLICY_NAME = "EU_Data_Policy";
	static final String GLOBAL_DATA_POLICY_NAME = "Global_Data_Policy";
	static final String KEY_1 = "Reference_key_1";
	static final String KEY_2 = "Reference_key_2";
	static final String POLICY_GUID_1 = "policy_guid_1";
	static final String POLICY_GUID_2 = "policy_guid_2";
	static final String DATA_POLICY_SEGMENTS = "DATA_POLICY_SEGMENTS";
	private static final Date TWO_MINUTES_AGO_DATE = new Date(new Date().getTime() - 2 * 60 * 1000);
	private static final Date NOW_DATE = new Date();

	@Mock
	private DataPolicyService dataPolicyService;

	@Mock
	private CustomerConsentService customerConsentService;

	@Mock
	private TimeService timeService;

	@Mock
	private CustomerConsent customerConsent;

	@Mock
	private SubjectAttribute subjectAttribute;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	private DataPolicy dataPolicy1;

	private DataPolicy dataPolicy2;

	private DataPolicyRepository dataPolicyRepository;

	@Before
	public void initialize() {
		dataPolicyRepository = new DataPolicyRepositoryImpl(dataPolicyService, customerConsentService, timeService, reactiveAdapter);

		when(subjectAttribute.getType()).thenReturn(DATA_POLICY_SEGMENTS);
		when(subjectAttribute.getValue()).thenReturn(DATA_POLICY_NAME);

		dataPolicy1 = createMockDataPolicy(POLICY_GUID_1, DATA_POLICY_NAME, GLOBAL_DATA_POLICY_NAME);
		dataPolicy2 = createMockDataPolicy(POLICY_GUID_2, DATA_POLICY_NAME);
	}

	@Test
	public void findCustomerConsentByDataPolicyGuidForCustomerReturnsWhenConsentExistsForPolicy() {
		when(customerConsentService.findByDataPolicyGuidForCustomerLatest(POLICY_GUID_1, USER_ID)).thenReturn(customerConsent);

		dataPolicyRepository.findCustomerConsentByDataPolicyGuidForCustomer(POLICY_GUID_1, USER_ID)
				.test()
				.assertNoErrors()
				.assertValue(customerConsent);
	}

	@Test
	public void findCustomerConsentByDataPolicyGuidForCustomerReturnsNoCustomerConsentWhenNoneExistsForPolicy() {
		when(customerConsentService.findByDataPolicyGuidForCustomerLatest(POLICY_GUID_1, USER_ID)).thenReturn(null);

		dataPolicyRepository.findCustomerConsentByDataPolicyGuidForCustomer(POLICY_GUID_1, USER_ID)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void findValidDataPolicyReturnsDataPolicy() {
		when(dataPolicyService.areEnabledByStore(SCOPE.getValue())).thenReturn(true);
		when(dataPolicyService.findActiveByGuid(POLICY_GUID_1)).thenReturn(dataPolicy1);

		dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute))
				.test()
				.assertNoErrors()
				.assertValue(dataPolicy1);
	}

	@Test
	public void findValidDataPolicyReturns404WhenDataPolicyNotEnabledForStorePolicy() {
		when(dataPolicyService.areEnabledByStore(SCOPE.getValue())).thenReturn(false);

		dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute))
				.test()
				.assertError(ResourceOperationFailure.notFound(DataPolicyRepositoryImpl.VALUE_NOT_FOUND));
	}

	@Test
	public void findValidDataPolicyReturns404WhenNoRelevantHeadersExist() {
		when(dataPolicyService.areEnabledByStore(SCOPE.getValue())).thenReturn(true);
		when(dataPolicyService.findActiveByGuid(POLICY_GUID_1)).thenReturn(dataPolicy1);

		dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.emptyList())
				.test()
				.assertError(ResourceOperationFailure.notFound(DataPolicyRepositoryImpl.HEADER_NOT_FOUND));
	}

	@Test
	public void findValidDataPolicyReturns404WhenNoActiveDataPolicyExists() {
		when(dataPolicyService.areEnabledByStore(SCOPE.getValue())).thenReturn(true);

		dataPolicyRepository.findValidDataPolicy(POLICY_GUID_1, SCOPE.getValue(), Collections.singletonList(subjectAttribute))
				.test()
				.assertError(ResourceOperationFailure.notFound(DataPolicyRepositoryImpl.ACTIVE_POLICY_NOT_FOUND));
	}

	@Test
	public void getSegmentHeadersReturnsListOfLowerCaseHeaders() {
		SubjectAttribute subjectAttribute2 = mock(SubjectAttribute.class);
		when(subjectAttribute2.getType()).thenReturn(DATA_POLICY_SEGMENTS);
		when(subjectAttribute2.getValue()).thenReturn(String.format("%s,%s", DATA_POLICY_NAME, GLOBAL_DATA_POLICY_NAME));

		assertThat(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.singletonList(subjectAttribute2)))
				.containsExactly(DATA_POLICY_NAME.toLowerCase(Locale.getDefault()), GLOBAL_DATA_POLICY_NAME.toLowerCase(Locale.getDefault()));
	}

	@Test
	public void getSegmentHeadersReturnsEmptyListWhenNoSegmentHeadersExist() {
		assertThat(dataPolicyRepository.getSegmentHeadersLowerCase(Collections.emptyList()))
				.isEmpty();
	}

	@Test
	public void findActiveDataPoliciesForSegmentsAndStoreReturnsActivePolicies() {
		when(dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(DATA_POLICY_NAME), SCOPE.getValue()))
				.thenReturn(Arrays.asList(dataPolicy1, dataPolicy2));

		dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(DATA_POLICY_NAME), SCOPE.getValue())
				.test()
				.assertValueCount(2)
				.assertValues(dataPolicy1, dataPolicy2);
	}

	@Test
	public void findActiveDataPoliciesForSegmentsAndStoreReturnsNoActivePoliciesWhenNoneExist() {
		when(dataPolicyService.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(DATA_POLICY_NAME), SCOPE.getValue()))
				.thenReturn(Collections.emptyList());

		dataPolicyRepository.findActiveDataPoliciesForSegmentsAndStore(Collections.singletonList(DATA_POLICY_NAME), SCOPE.getValue())
				.test()
				.assertNoValues();
	}

	@Test
	public void verifyNeverSaveNewCustomerConsentRecordWithSameConsentActionWhenSubmittedWithinTimeThreshold() {
		dataPolicyRepository = Mockito.spy(dataPolicyRepository);
		DataPolicyEntity dataPolicyEntity = createDataPolicyEntity(POLICY_GUID_1, KEY_1, Boolean.TRUE.toString());

		when(customerConsentService.findByDataPolicyGuidForCustomerLatest(POLICY_GUID_1, USER_ID)).thenReturn(customerConsent);
		when(customerConsent.getAction()).thenReturn(ConsentAction.GRANTED);
		when(customerConsent.getConsentDate()).thenReturn(NOW_DATE);
		when(timeService.getCurrentTime()).thenReturn(NOW_DATE);

		dataPolicyRepository.createCustomerConsentForDataPolicy(USER_ID, dataPolicy1, dataPolicyEntity)
				.test()
				.assertValue(customerConsent);

		verify(dataPolicyRepository, never()).saveCustomerConsent(any(CustomerConsent.class));
	}

	@Test
	public void verifySaveNewCustomerConsentRecordWhenSubmittedWithoutSameConsentAction() {
		dataPolicyRepository = Mockito.spy(dataPolicyRepository);
		DataPolicyEntity dataPolicyEntity = createDataPolicyEntity(POLICY_GUID_1, KEY_1, Boolean.TRUE.toString());

		when(customerConsentService.findByDataPolicyGuidForCustomerLatest(POLICY_GUID_1, USER_ID)).thenReturn(customerConsent);
		when(customerConsent.getAction()).thenReturn(ConsentAction.REVOKED);
		when(timeService.getCurrentTime()).thenReturn(NOW_DATE);
		when(customerConsentService.save(any(CustomerConsent.class))).thenReturn(customerConsent);

		dataPolicyRepository.createCustomerConsentForDataPolicy(USER_ID, dataPolicy1, dataPolicyEntity)
				.test()
				.assertValue(customerConsent);

		verify(dataPolicyRepository).saveCustomerConsent(any(CustomerConsent.class));
	}

	@Test
	public void verifySaveNewCustomerConsentRecordWithSameConsentActionWhenNotSubmittedWithinTimeThreshold() {
		dataPolicyRepository = Mockito.spy(dataPolicyRepository);
		DataPolicyEntity dataPolicyEntity = createDataPolicyEntity(POLICY_GUID_1, KEY_1, Boolean.TRUE.toString());

		when(customerConsentService.findByDataPolicyGuidForCustomerLatest(POLICY_GUID_1, USER_ID)).thenReturn(customerConsent);
		when(customerConsent.getAction()).thenReturn(ConsentAction.GRANTED);
		when(customerConsent.getConsentDate()).thenReturn(TWO_MINUTES_AGO_DATE);
		when(timeService.getCurrentTime()).thenReturn(NOW_DATE);
		when(customerConsentService.save(any(CustomerConsent.class))).thenReturn(customerConsent);


		dataPolicyRepository.createCustomerConsentForDataPolicy(USER_ID, dataPolicy1, dataPolicyEntity)
				.test()
				.assertValue(customerConsent);

		verify(dataPolicyRepository).saveCustomerConsent(any(CustomerConsent.class));
	}

	/**
	 * Helper to create data policy entity objects.
	 */
	private DataPolicyEntity createDataPolicyEntity(final String policyName, final String referenceKey, final String consentAction) {
		return DataPolicyEntity.builder()
				.withDataPolicyConsent(consentAction)
				.withPolicyName(policyName)
				.withPolicyReferenceKey(referenceKey).build();
	}

	/**
	 * Helper to create mock data policy objects.
	 */
	private static DataPolicy createMockDataPolicy(final String guid, final String... segmentStrings) {
		DataPolicy dataPolicy = mock(DataPolicy.class, RETURNS_DEEP_STUBS);
		when(dataPolicy.getState().getOrdinal()).thenReturn(DataPolicyState.ACTIVE_ORDINAL);
		when(dataPolicy.getStartDate().before(any(Date.class))).thenReturn(true);
		when(dataPolicy.getGuid()).thenReturn(guid);
		when(dataPolicy.getSegments()).thenReturn(new HashSet<>(Arrays.asList(segmentStrings)));
		return dataPolicy;
	}

}
