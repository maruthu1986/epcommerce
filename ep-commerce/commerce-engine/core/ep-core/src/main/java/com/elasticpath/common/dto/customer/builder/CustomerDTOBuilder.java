/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.common.dto.customer.builder;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.common.dto.AddressDTO;
import com.elasticpath.common.dto.customer.AttributeValueDTO;
import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.common.dto.customer.DefaultPaymentMethodDTO;
import com.elasticpath.common.dto.customer.LegacyCreditCardDTO;
import com.elasticpath.common.dto.customer.PaymentMethodDto;

/**
 * Customer dto builder used to construct {@link com.elasticpath.common.dto.customer.CustomerDTO}s.
 */
public class CustomerDTOBuilder {
	private String guid;
	private String userId;
	private String storeCode;
	private int status;
	private Date creationDate;
	private Date lastEditDate;
	private String password;
	private String salt;
	private PaymentMethodDto[] paymentMethods;
	private Set<AttributeValueDTO> profileValues = new HashSet<>();
	private LegacyCreditCardDTO[] legacyCreditCards;
	private PaymentMethodDto defaultPaymentMethod;
	private String billingAddressGuid;
	private String shippingAddressGuid;
	private AddressDTO[] addresses;
	private String[] groups;
	private boolean isFirstTimeBuyer;

	/**
	 * Set Guid.
	 *
	 * @param guid the guid
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withGuid(final String guid) {
		this.guid = guid;
		return this;
	}

	/**
	 * Sets the store code.
	 *
	 * @param storeCode the store code
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withStoreCode(final String storeCode) {
		this.storeCode = storeCode;
		return this;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the user id
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withUserId(final String userId) {
		this.userId = userId;
		return this;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status.
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withStatus(final int status) {
		this.status = status;
		return this;
	}

	/**
	 * Sets the creation date.
	 *
	 * @param creationDate the creation date
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	/**
	 * Sets the last edit date.
	 *
	 * @param lastEditDate the last edit date
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withLastEditDate(final Date lastEditDate) {
		this.lastEditDate = lastEditDate;
		return this;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the password
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withPassword(final String password) {
		this.password = password;
		return this;
	}

	/**
	 * Sets the salt.
	 *
	 * @param salt the salt
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withSalt(final String salt) {
		this.salt = salt;
		return this;
	}

	/**
	 * Sets the payment methods.
	 *
	 * @param paymentMethods the payment methods
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withPaymentMethods(final PaymentMethodDto... paymentMethods) {
		this.paymentMethods = paymentMethods;
		return this;
	}

	/**
	 * Sets the default payment method.
	 *
	 * @param defaultPaymentMethod the default payment method
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withDefaultPaymentMethod(final PaymentMethodDto defaultPaymentMethod) {
		this.defaultPaymentMethod = defaultPaymentMethod;
		return this;
	}

	/**
	 * Sets the list of legacy of credit cards.
	 *
	 * @param legacyCreditCards the legacy credit cards
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withCreditCards(final LegacyCreditCardDTO... legacyCreditCards) {
		this.legacyCreditCards = legacyCreditCards;
		return this;
	}

	/**
	 * Sets the profile values.
	 *
	 * @param profileValues the profile values.
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withProfileValues(final Set<AttributeValueDTO> profileValues) {
		this.profileValues = profileValues;
		return this;
	}

	/**
	 * Sets the preferred billing address guid.
	 *
	 * @param billingAddressGuid the billing address guid
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withPreferredBillingAddressGuid(final String billingAddressGuid) {
		this.billingAddressGuid = billingAddressGuid;
		return this;
	}

	/**
	 * Sets the preferred shipping address guid.
	 *
	 * @param shippingAddressGuid the shipping address guid
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withPreferredShippingAddressGuid(final String shippingAddressGuid) {
		this.shippingAddressGuid = shippingAddressGuid;
		return this;
	}

	/**
	 * Sets addresses.
	 *
	 * @param addresses the addresses
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withAddresses(final AddressDTO... addresses) {
		this.addresses = addresses;
		return this;
	}

	/**
	 * Sets groups.
	 *
	 * @param groups the groups
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withGroups(final String... groups) {
		this.groups = groups;
		return this;
	}

	/**
	 * Set a flag if customer is first time buyer.
	 * @param isFirstTimeBuyer flag
	 * @return this {@link CustomerDTOBuilder}
	 */
	public CustomerDTOBuilder withFirstTimeBuyer(final boolean isFirstTimeBuyer) {
		this.isFirstTimeBuyer = isFirstTimeBuyer;
		return this;
	}

	/**
	 * Builds the {@link CustomerDTO} specified.
	 *
	 * @return the new built {@link CustomerDTO}
	 */
	public CustomerDTO build() {
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setGuid(guid);
		customerDTO.setStoreCode(storeCode);
		customerDTO.setUserId(userId);
		customerDTO.setStatus(status);
		customerDTO.setCreationDate(creationDate);
		customerDTO.setLastEditDate(lastEditDate);
		customerDTO.setPassword(password);
		customerDTO.setSalt(salt);
		customerDTO.setFirstTimeBuyer(isFirstTimeBuyer);

		if (paymentMethods != null) {
			customerDTO.setPaymentMethods(Arrays.asList(paymentMethods));
		}

		customerDTO.setProfileValues(profileValues);
		if (legacyCreditCards != null) {
			customerDTO.setCreditCards(Arrays.asList(legacyCreditCards));
		}

		customerDTO.setPreferredBillingAddressGuid(billingAddressGuid);
		customerDTO.setPreferredShippingAddressGuid(shippingAddressGuid);

		if (addresses != null) {
			customerDTO.setAddresses(Arrays.asList(addresses));
		}

		if (groups != null) {
			customerDTO.setGroups(Arrays.asList(groups));
		}

		if (defaultPaymentMethod != null) {
			DefaultPaymentMethodDTO defaultPaymentMethodDTO = new DefaultPaymentMethodDTO();
			defaultPaymentMethodDTO.setPaymentMethod(defaultPaymentMethod);
			customerDTO.setDefaultPaymentMethod(defaultPaymentMethodDTO);
		}

		return customerDTO;
	}
}
