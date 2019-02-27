/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.repositories;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.addresses.AddressDetailEntity;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.base.NameEntity;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.CartOrdersDefaultAddressPopulator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.validator.AddressValidator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Address Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component(property = "name=addressEntityRepositoryImpl")
@SuppressWarnings("PMD.GodClass")
public class AddressEntityRepositoryImpl<E extends AddressEntity, I extends AddressIdentifier>
		implements Repository<AddressEntity, AddressIdentifier> {

	/**
	 * Error message when address cannot be found.
	 */
	@VisibleForTesting
	public static final String ADDRESS_NOT_FOUND = "Address not found.";

	private CustomerRepository customerRepository;

	private CartOrdersDefaultAddressPopulator cartOrdersDefaultAddressPopulator;

	private AddressValidator addressValidator;

	private ReactiveAdapter reactiveAdapter;

	private ResourceOperationContext resourceOperationContext;

	private ConversionService conversionService;

	@Override
	public Single<SubmitResult<AddressIdentifier>> submit(final AddressEntity addressEntity, final IdentifierPart<String> scope) {
		CustomerAddress customerAddress = convertAddressEntityToCustomerAddress(addressEntity);
		return addressValidator.validate(addressEntity)
				.andThen(customerRepository.getCustomer(resourceOperationContext.getUserIdentifier())
						.flatMap(customer -> createCustomerAddress(scope.getValue(), customer, customerAddress)));
	}

	/**
	 * Create a new customer address if the customer doesn't already have an existing equivalent one.
	 *
	 * @param scope           scope
	 * @param customer        customer
	 * @param customerAddress customerAddress
	 * @return address identifer
	 */
	protected Single<SubmitResult<AddressIdentifier>> createCustomerAddress(
			final String scope, final Customer customer, final CustomerAddress customerAddress) {

		for (CustomerAddress existingAddress : customer.getAddresses()) {
			if (existingAddress.equals(customerAddress)) {
				return Single.just(buildAddressIdentifier(scope, existingAddress, false));
			}
		}

		return customerRepository.addAddress(customer, customerAddress)
				.flatMap(newAddressCustomer -> addCustomerPreferredAddress(scope, newAddressCustomer, customerAddress))
				.flatMap(updatedCustomer -> getExistingAddressByGuid(customerAddress.getGuid(), updatedCustomer))
				.map(newCustomerAddress -> buildAddressIdentifier(scope, customerAddress, true));
	}

	/**
	 * Add the customer's preferred billing and shipping address if the customer doesn't already have existing ones.
	 *
	 * @param scope              scope
	 * @param newAddressCustomer newAddressCustomer
	 * @param address            address
	 * @return the customer
	 */
	protected Single<Customer> addCustomerPreferredAddress(final String scope, final Customer newAddressCustomer, final CustomerAddress address) {
		boolean addPreferredBillingAddress = newAddressCustomer.getPreferredBillingAddress() == null;
		if (addPreferredBillingAddress) {
			newAddressCustomer.setPreferredBillingAddress(address);
		}

		boolean addPreferredShippingAddress = newAddressCustomer.getPreferredShippingAddress() == null;
		if (addPreferredShippingAddress) {
			newAddressCustomer.setPreferredShippingAddress(address);
		}

		return updateCartOrdersAddresses(scope, newAddressCustomer, address, addPreferredBillingAddress, addPreferredShippingAddress)
				.andThen(customerRepository.update(newAddressCustomer));
	}

	/**
	 * Update all cart order addresses of the customer if needed.
	 *
	 * @param scope                          scope
	 * @param newAddressCustomer             newAddressCustomer
	 * @param address                        address
	 * @param updatePreferredBillingAddress  updatePreferredBillingAddress
	 * @param updatePreferredShippingAddress updatePreferredShippingAddress
	 * @return Completable
	 */
	protected Completable updateCartOrdersAddresses(final String scope, final Customer newAddressCustomer, final CustomerAddress address,
			final boolean updatePreferredBillingAddress, final boolean updatePreferredShippingAddress) {
		if (updatePreferredBillingAddress || updatePreferredShippingAddress) {
			return cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(newAddressCustomer, address, scope, updatePreferredBillingAddress,
					updatePreferredShippingAddress);
		}

		return Completable.complete();
	}

	/**
	 * Converts AddressEntity to CustomerAddress.
	 *
	 * @param addressEntity addressEntity
	 * @return customer address
	 */
	protected CustomerAddress convertAddressEntityToCustomerAddress(final AddressEntity addressEntity) {
		return conversionService.convert(addressEntity, CustomerAddress.class);
	}

	@Override
	public Single<AddressEntity> findOne(final AddressIdentifier identifier) {
		String addressGuid = identifier.getAddressId().getValue();
		return customerRepository.getCustomer(resourceOperationContext.getUserIdentifier())
				.flatMap(customer -> getExistingAddressByGuid(addressGuid, customer))
				.map(this::convertCustomerAddressToAddressEntity);
	}

	/**
	 * Get the customer's address by guid.
	 *
	 * @param addressGuid addressGuid
	 * @param customer    customer
	 * @return customer address
	 */
	protected Single<CustomerAddress> getExistingAddressByGuid(final String addressGuid, final Customer customer) {
		return reactiveAdapter.fromNullableAsSingle(() -> customer.getAddressByGuid(addressGuid), ADDRESS_NOT_FOUND);
	}

	/**
	 * Converts Address to AddressEntity.
	 *
	 * @param address address
	 * @return address entity
	 */
	protected AddressEntity convertCustomerAddressToAddressEntity(final Address address) {
		return conversionService.convert(address, AddressEntity.class);
	}

	@Override
	public Observable<AddressIdentifier> findAll(final IdentifierPart<String> scope) {
		return customerRepository.getCustomer(resourceOperationContext.getUserIdentifier())
				.flatMapObservable(customer -> Observable.fromIterable(customer.getAddresses()))
				.map(customerAddress -> buildAddressIdentifier(scope.getValue(), customerAddress));
	}

	/**
	 * Builds the AddressIdentifier given the scope and address.
	 *
	 * @param scope           scope
	 * @param customerAddress customerAddress
	 * @return the AddressIdentifier
	 */
	protected AddressIdentifier buildAddressIdentifier(final String scope, final CustomerAddress customerAddress) {
		return AddressIdentifier.builder()
				.withAddressId(StringIdentifier.of(customerAddress.getGuid()))
				.withAddresses(AddressesIdentifier.builder()
						.withScope(StringIdentifier.of(scope))
						.build())
				.build();
	}

	/**
	 * Builds the AddressIdentifier given the scope and address.
	 *
	 * @param scope           scope
	 * @param customerAddress customerAddress
	 * @param isNewlyCreated  true if address was just created
	 * @return submit result of address identifier
	 */
	protected SubmitResult<AddressIdentifier> buildAddressIdentifier(
			final String scope, final CustomerAddress customerAddress, final boolean isNewlyCreated) {

		AddressIdentifier identifier = buildAddressIdentifier(scope, customerAddress);

		return SubmitResult.<AddressIdentifier>builder()
				.withIdentifier(identifier)
				.withStatus(isNewlyCreated
						? SubmitStatus.CREATED
						: SubmitStatus.EXISTING)
				.build();
	}

	@Override
	public Completable update(final AddressEntity addressEntity, final AddressIdentifier identifier) {
		String addressGuid = identifier.getAddressId().getValue();
		return addressValidator.validate(addressEntity)
				.andThen(customerRepository.getCustomer(resourceOperationContext.getUserIdentifier())
				.flatMapCompletable(customer -> getExistingAddressByGuid(addressGuid, customer)
						.map(address -> updateCustomerAddress(addressEntity, address))
						.flatMapCompletable(address -> customerRepository.updateAddress(customer, address))));
	}

	/**
	 * Updates the customer address from the given address entity.
	 *
	 * @param addressEntity addressEntity
	 * @param address       address
	 * @return the updated customer address
	 */
	protected CustomerAddress updateCustomerAddress(final AddressEntity addressEntity, final CustomerAddress address) {
		AddressDetailEntity addressDetailEntity = addressEntity.getAddress();
		if (addressDetailEntity != null) {
			updateCountry(address, addressDetailEntity.getCountryName());
			updateState(address, addressDetailEntity.getRegion());
			updateCity(address, addressDetailEntity.getLocality());
			updatePostal(address, addressDetailEntity.getPostalCode());
			updateStreet1(address, addressDetailEntity.getStreetAddress());
			updateStreet2(address, addressDetailEntity.getExtendedAddress());
			updatePhoneNumber(address, addressDetailEntity.getPhoneNumber());
			updateOrganization(address, addressDetailEntity.getOrganization());
		}

		NameEntity nameEntity = addressEntity.getName();
		if (nameEntity != null) {
			updateFirstName(address, nameEntity.getGivenName());
			updateLastName(address, nameEntity.getFamilyName());
		}

		return address;
	}

	/**
	 * Update the last name on the address.
	 *
	 * @param address  address
	 * @param lastName lastName
	 */
	protected void updateLastName(final Address address, final String lastName) {
		if (lastName != null) {
			address.setLastName(StringUtils.trimToNull(lastName));
		}
	}

	/**
	 * Update the first name on the address.
	 *
	 * @param address   address
	 * @param firstName firstName
	 */
	protected void updateFirstName(final Address address, final String firstName) {
		if (firstName != null) {
			address.setFirstName(StringUtils.trimToNull(firstName));
		}
	}

	/**
	 * Update the street on the address.
	 *
	 * @param address address
	 * @param street1 street1
	 */
	protected void updateStreet1(final Address address, final String street1) {
		if (street1 != null) {
			address.setStreet1(StringUtils.trimToNull(street1));
		}
	}

	/**
	 * Update the extended street on the address.
	 *
	 * @param address address
	 * @param street2 street2
	 */
	protected void updateStreet2(final Address address, final String street2) {
		if (street2 != null) {
			address.setStreet2(StringUtils.trimToNull(street2));
		}
	}

	/**
	 * Update the postal code on the address.
	 *
	 * @param address         address
	 * @param zipOrPostalCode zipOrPostalCode
	 */
	protected void updatePostal(final Address address, final String zipOrPostalCode) {
		if (zipOrPostalCode != null) {
			address.setZipOrPostalCode(StringUtils.trimToNull(zipOrPostalCode));
		}
	}

	/**
	 * Update the city on the address.
	 *
	 * @param address address
	 * @param city    city
	 */
	protected void updateCity(final Address address, final String city) {
		if (city != null) {
			address.setCity(StringUtils.trimToNull(city));
		}
	}

	/**
	 * Update the state on the address.
	 *
	 * @param address    address
	 * @param subCountry subCountry
	 */
	protected void updateState(final Address address, final String subCountry) {
		if (subCountry != null) {
			address.setSubCountry(StringUtils.trimToNull(subCountry));
		}
	}

	/**
	 * Update the country on the address.
	 *
	 * @param address address
	 * @param country country
	 */
	protected void updateCountry(final Address address, final String country) {
		if (country != null) {
			address.setCountry(StringUtils.trimToNull(country));
		}
	}

	/**
	 * Update the phone number on the address.
	 *
	 * @param address     address
	 * @param phoneNumber phoneNumber
	 */
	protected void updatePhoneNumber(final Address address, final String phoneNumber) {
		if (phoneNumber != null) {
			address.setPhoneNumber(StringUtils.trimToNull(phoneNumber));
		}
	}

	/**
	 * Update the organization on the address.
	 *
	 * @param address      address
	 * @param organization organization
	 */
	protected void updateOrganization(final Address address, final String organization) {
		if (organization != null) {
			address.setOrganization(StringUtils.trimToNull(organization));
		}
	}

	@Override
	public Completable delete(final AddressIdentifier identifier) {
		String addressGuid = identifier.getAddressId().getValue();
		return customerRepository.getCustomer(resourceOperationContext.getUserIdentifier())
				.flatMap(customer -> getExistingAddressByGuid(addressGuid, customer)
						.flatMap(address -> removeCustomerAddress(customer, address))).toCompletable();
	}

	/**
	 * Removes the address from the customer.
	 *
	 * @param customer customer
	 * @param address  address
	 * @return the customer
	 */
	protected Single<Customer> removeCustomerAddress(final Customer customer, final CustomerAddress address) {
		customer.removeAddress(address);
		return customerRepository.update(customer);
	}

	@Reference
	public void setCustomerRepository(final CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Reference
	public void setCartOrdersDefaultAddressPopulator(final CartOrdersDefaultAddressPopulator cartOrdersDefaultAddressPopulator) {
		this.cartOrdersDefaultAddressPopulator = cartOrdersDefaultAddressPopulator;
	}

	@Reference
	public void setAddressValidator(final AddressValidator addressValidator) {
		this.addressValidator = addressValidator;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
