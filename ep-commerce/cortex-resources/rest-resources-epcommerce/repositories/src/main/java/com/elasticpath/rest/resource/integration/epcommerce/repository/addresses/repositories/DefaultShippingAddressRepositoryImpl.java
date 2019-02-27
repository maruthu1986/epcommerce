/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.repositories;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.AliasRepository;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.addresses.AddressesIdentifier;
import com.elasticpath.rest.definition.addresses.DefaultShippingAddressIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Default Shipping Address Alias Repository.
 *
 * @param <AI> the alias identifier type
 * @param <I>  the identifier type
 */
@Component
public class DefaultShippingAddressRepositoryImpl<AI extends DefaultShippingAddressIdentifier, I extends AddressIdentifier>
		implements AliasRepository<DefaultShippingAddressIdentifier, AddressIdentifier> {

	private CustomerRepository customerRepository;

	private ResourceOperationContext resourceOperationContext;

	private ReactiveAdapter reactiveAdapter;

	@Override
	public Single<AddressIdentifier> resolve(final DefaultShippingAddressIdentifier defaultShippingAddressIdentifier) {
		String userGuid = resourceOperationContext.getUserIdentifier();
		AddressesIdentifier addressesIdentifier = defaultShippingAddressIdentifier.getShippingAddresses().getAddresses();
		return customerRepository.getCustomer(userGuid)
				.flatMap(customer -> reactiveAdapter.fromServiceAsSingle(customer::getPreferredShippingAddress))
				.map(address -> AddressIdentifier.builder()
						.withAddresses(addressesIdentifier)
						.withAddressId(StringIdentifier.of(address.getGuid()))
						.build());
	}

	@Reference
	public void setCustomerRepository(final CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}
