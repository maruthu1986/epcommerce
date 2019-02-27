/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.common.dto.datapolicy;


import java.util.Date;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

import com.elasticpath.domain.datapolicy.ConsentAction;

/**
 * Tests CustomerConsentDTO methods.
 */
public class CustomerConsentDTOTest {

	private static final String CUSTOMER_CONSENT_GUID = "customerConsentGuid";
	private static final String CUSTOMER_CONSENT_GUID_1 = "customerConsentGuid1";
	private static final String CUSTOMER_GUID = "customerGuid";
	private static final String CUSTOMER_GUID_1 = "customerGuid1";
	private static final String DATA_POLICY_GUID = "dataPolicyGuid";
	private static final String DATA_POLICY_GUID_1 = "dataPolicyGuid1";


	@Test
	public void testEqualsHashCode() {
		CustomerConsentDTO customerConsentDTO1 = getCustomerConsentDTO(CUSTOMER_CONSENT_GUID, CUSTOMER_GUID, DATA_POLICY_GUID);
		CustomerConsentDTO customerConsentDTO2 = getCustomerConsentDTO(CUSTOMER_CONSENT_GUID, CUSTOMER_GUID_1, DATA_POLICY_GUID_1);
		CustomerConsentDTO customerConsentDTO3 = getCustomerConsentDTO(CUSTOMER_CONSENT_GUID_1, CUSTOMER_GUID_1, DATA_POLICY_GUID_1);

		new EqualsTester()
				.addEqualityGroup(customerConsentDTO1, customerConsentDTO1, customerConsentDTO2)
				.addEqualityGroup(customerConsentDTO3)
				.testEquals();
	}

	private CustomerConsentDTO getCustomerConsentDTO(final String customerConsentGuid, final String customerGuid, final String datePolicyGuid) {
		CustomerConsentDTO customerConsentDTO = new CustomerConsentDTO();
		customerConsentDTO.setGuid(customerConsentGuid);
		customerConsentDTO.setCustomerGuid(customerGuid);
		customerConsentDTO.setDataPolicyGuid(datePolicyGuid);
		customerConsentDTO.setConsentDate(new Date());
		customerConsentDTO.setAction(ConsentAction.GRANTED.getName());

		return customerConsentDTO;
	}
}
