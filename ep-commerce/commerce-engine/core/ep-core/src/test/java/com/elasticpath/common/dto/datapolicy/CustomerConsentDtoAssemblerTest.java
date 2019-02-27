/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.common.dto.datapolicy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.impl.CustomerConsentImpl;
import com.elasticpath.domain.datapolicy.impl.DataPolicyImpl;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Test {@link CustomerConsentDtoAssembler} functionality.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerConsentDtoAssemblerTest {

	private static final String EXPECTED_DTO_SHOULD_EQUAL_ACTUAL = "The assembled customer consentDTO should be equal to the expected customer "
			+ "consentDTO.";

	private static final String EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL =
			"The assembled customer consentdomain object should be equal to the expected customer consentdomain object.";

	private static final String GUID = "guid";
	private static final String GUID1 = "guid1";
	private static final String DATA_POLICY_GUID = "dataPolicyGuid";
	private static final String CUSTOMER_GUID = "customerGuid";
	private static final Date DATE = new Date();

	@InjectMocks
	private CustomerConsentDtoAssembler customerConsentDtoAssembler;

	@Mock
	@SuppressWarnings("PMD.UnusedPrivateField")
	private BeanFactory beanFactory;

	@Mock
	@SuppressWarnings("PMD.UnusedPrivateField")
	private DataPolicyService dataPolicyService;

	/**
	 * Test customer consentDTO assembly from domain object.
	 */
	@Test
	public void testCustomerConsentAssembleDtoFromDomainObject() {
		CustomerConsent testCustomerConsent = createCustomerConsent(GUID);
		CustomerConsentDTO expectedCustomerConsentDTO = createCustomerConsentDTO(GUID);

		CustomerConsentDTO customerConsentDTO = new CustomerConsentDTO();

		customerConsentDtoAssembler.assembleDto(testCustomerConsent, customerConsentDTO);

		assertThat(expectedCustomerConsentDTO)
				.as(EXPECTED_DTO_SHOULD_EQUAL_ACTUAL)
				.isEqualToComparingOnlyGivenFields(customerConsentDTO, "guid", "action", "customerGuid", "dataPolicyGuid");
	}

	/**
	 * Test customer consentDTO assembly from domain object is not the same.
	 */
	@Test
	public void testCustomerConsentAssembleDtoFromDomainObjectNotEquals() {
		CustomerConsent testCustomerConsent = createCustomerConsent(GUID1);
		CustomerConsentDTO expectedCustomerConsentDTO = createCustomerConsentDTO(GUID);

		CustomerConsentDTO customerConsentDTO = new CustomerConsentDTO();

		customerConsentDtoAssembler.assembleDto(testCustomerConsent, customerConsentDTO);

		assertThat(expectedCustomerConsentDTO.getGuid())
				.as("Unexpected customer consent DTO created by assembler")
				.isNotSameAs(customerConsentDTO.getGuid());
	}

	/**
	 * Test customer consent assembly from DTO object.
	 */
	@Test
	public void testCustomerConsentAssembleDomainObjectFromDto() {

		CustomerConsentDTO customerConsentDTO = createCustomerConsentDTO(GUID1);
		CustomerConsent expectedCustomerConsent = createCustomerConsent(GUID);

		CustomerConsent customerConsent = new CustomerConsentImpl();

		customerConsentDtoAssembler.assembleDomain(customerConsentDTO, customerConsent);

		assertThat(expectedCustomerConsent)
				.as(EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL)
				.isEqualToComparingOnlyGivenFields(expectedCustomerConsent, "guid", "action", "customerGuid", "dataPolicy.guid");
	}

	/**
	 * Test customer consent assembly from DTO object  is not the same.
	 */
	@Test
	public void testCustomerConsentAssembleDomainObjectFromDtoNotEquals() {

		CustomerConsentDTO customerConsentDTO = createCustomerConsentDTO(GUID1);
		CustomerConsent expectedCustomerConsent = createCustomerConsent(GUID);

		CustomerConsent customerConsent = new CustomerConsentImpl();

		customerConsentDtoAssembler.assembleDomain(customerConsentDTO, customerConsent);

		assertThat(expectedCustomerConsent.getGuid())
				.as("Unexpected customer consentdomain created by assembler")
				.isNotSameAs(customerConsent.getGuid());
	}

	private CustomerConsentDTO createCustomerConsentDTO(final String guid) {
		CustomerConsentDTO customerConsent = new CustomerConsentDTO();
		customerConsent.setGuid(guid);
		customerConsent.setCustomerGuid(CUSTOMER_GUID);
		customerConsent.setDataPolicyGuid(DATA_POLICY_GUID);
		customerConsent.setConsentDate(DATE);
		customerConsent.setAction(ConsentAction.GRANTED.getName());
		return customerConsent;
	}

	private CustomerConsent createCustomerConsent(final String guid) {
		DataPolicy dataPolicy = new DataPolicyImpl();
		dataPolicy.setGuid(DATA_POLICY_GUID);

		CustomerConsent customerConsent = new CustomerConsentImpl();
		customerConsent.setGuid(guid);
		customerConsent.setCustomerGuid(CUSTOMER_GUID);
		customerConsent.setDataPolicy(dataPolicy);
		customerConsent.setConsentDate(DATE);
		customerConsent.setAction(ConsentAction.GRANTED);
		return customerConsent;
	}

}
