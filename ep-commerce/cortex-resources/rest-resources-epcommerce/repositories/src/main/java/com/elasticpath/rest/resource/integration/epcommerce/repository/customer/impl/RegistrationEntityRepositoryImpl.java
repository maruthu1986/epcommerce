/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import io.reactivex.Completable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.definition.registrations.RegistrationEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.FormEntityToCustomerEnhancer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.customer.CustomerRegistrationService;

/**
 * Registration Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class RegistrationEntityRepositoryImpl<E extends RegistrationEntity, I extends ProfileIdentifier>
		implements Repository<RegistrationEntity, ProfileIdentifier> {

	private CustomerRepository customerRepository;
	private CustomerRegistrationService customerRegistrationService;
	private FormEntityToCustomerEnhancer formEntityToCustomerEnhancer;
	private ReactiveAdapter reactiveAdapter;

	@Override
	public Completable update(final RegistrationEntity registrationEntity, final ProfileIdentifier identifier) {
		return customerRepository.getCustomer(identifier.getProfileId().getValue())
				.map(customer -> updateCustomer(registrationEntity, customer))
				.flatMapCompletable(this::registerCustomer);
	}

	/**
	 * Update the customer.
	 *
	 * @param registrationEntity the registration entity.
	 * @param customer           the customer.
	 * @return the updated customer.
	 */
	protected Customer updateCustomer(final RegistrationEntity registrationEntity, final Customer customer) {
		return formEntityToCustomerEnhancer.registrationEntityToCustomer(registrationEntity, customer);
	}

	/**
	 * Register the customer.
	 *
	 * @param customer the customer.
	 * @return the Completable
	 */
	protected Completable registerCustomer(final Customer customer) {
		return reactiveAdapter.fromServiceAsSingle(() -> customerRegistrationService.registerCustomer(customer))
				.toCompletable();
	}

	@Reference
	protected void setFormEntityToCustomerEnhancer(final FormEntityToCustomerEnhancer formEntityToCustomerEnhancer) {
		this.formEntityToCustomerEnhancer = formEntityToCustomerEnhancer;
	}

	@Reference
	public void setCustomerRegistrationService(final CustomerRegistrationService customerRegistrationService) {
		this.customerRegistrationService = customerRegistrationService;
	}

	@Reference
	public void setCustomerRepository(final CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}
