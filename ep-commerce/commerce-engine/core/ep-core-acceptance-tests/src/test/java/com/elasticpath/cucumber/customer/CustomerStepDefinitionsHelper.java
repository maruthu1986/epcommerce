/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.customer;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.AddressDTO;
import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.builder.customer.CustomerAddressBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.util.Utils;

/**
 * Help class for updating customer state of the current test context.
 */
public class CustomerStepDefinitionsHelper {

	@Inject
	@Named("customerHolder")
	private ScenarioContextValueHolder<Customer> customerHolder;

	@Inject
	@Named("storeHolder")
	private ScenarioContextValueHolder<Store> storeHolder;

	@Autowired
	private TestApplicationContext tac;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerAddressBuilder customerAddressBuilder;

	/**
	 * Gets a default persisted customer for a store.
	 */
	public void setUpDefaultCustomer() {
		customerHolder.set(tac.getPersistersFactory().getStoreTestPersister().createDefaultCustomer(storeHolder.get()));
	}

	/**
	 * Gets a default persisted customer for a store with provided address.
	 *
	 * @param customerAddressMap customer address.
	 */
	public void setUpCustomerWithAddress(final Map<String, String> customerAddressMap) {
		CustomerAddress customerAddress = tac.getPersistersFactory().getStoreTestPersister().createCustomerAddress(
				customerAddressMap.get("lastName"),
				customerAddressMap.get("firstName"),
				customerAddressMap.get("street1"),
				customerAddressMap.get("street2"),
				customerAddressMap.get("city"),
				customerAddressMap.get("country"),
				customerAddressMap.get("state"),
				customerAddressMap.get("zip"),
				customerAddressMap.get("phone")
		);
		customerAddress.setCreationDate(Utils.getDate(customerAddressMap.get("creationDate")));
		customerAddress.setLastModifiedDate(Utils.getDate(customerAddressMap.get("lastModifiedDate")));
		customerHolder.set(tac.getPersistersFactory().getStoreTestPersister().
				createCustomerWithAddress(customerAddressMap.get("guid"), storeHolder.get(), customerAddress));
	}

	/**
	 * Adds customer addresses.
	 *
	 * @param addressDtos the address data
	 */
	public void addAddresses(final List<AddressDTO> addressDtos) {

		Customer customer = customerHolder.get();

		for (AddressDTO addressDto : addressDtos) {
			CustomerAddress customerAddress = customerAddressBuilder
					.withGuid(new RandomGuidImpl().toString())
					.withFirstName(addressDto.getFirstName())
					.withLastName(addressDto.getLastName())
					.withStreet1(addressDto.getStreet1())
					.withStreet2(addressDto.getStreet2())
					.withCity(addressDto.getCity())
					.withSubCountry(addressDto.getSubCountry())
					.withCountry(addressDto.getCountry())
					.withZipOrPostalCode(addressDto.getZipOrPostalCode())
					.withCommercialAddress(addressDto.isCommercialAddress())
					.withFaxNumber(addressDto.getFaxNumber())
					.withPhoneNumber(addressDto.getPhoneNumber())
					.build();
			customer.addAddress(customerAddress);

			if (customer.getPreferredBillingAddress() == null) {
				customer.setPreferredBillingAddress(customerAddress);
			}

			if (customer.getPreferredShippingAddress() == null) {
				customer.setPreferredShippingAddress(customerAddress);
			}
		}

		customerHolder.set(customerService.update(customer));
	}

	/**
	 * Gets a customer address which matches to a given sub country and a given country.
	 *
	 * @param subCountry the sub country
	 * @param country    the country
	 * @return a matched address if found, otherwise, return null
	 */
	public CustomerAddress getAddress(final String subCountry, final String country) {

		for (CustomerAddress customerAddress : customerHolder.get().getAddresses()) {
			if (customerAddress.getCountry().equals(country) && customerAddress.getSubCountry().equals(subCountry)) {
				return customerAddress;
			}
		}

		return null;
	}

}
