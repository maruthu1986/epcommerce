/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.definition.profiles.ProfileIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;

/**
 * Profile Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class ProfileEntityRepositoryImpl<E extends ProfileEntity, I extends ProfileIdentifier>
		implements Repository<ProfileEntity, ProfileIdentifier> {

	private ConversionService conversionService;

	private CustomerRepository customerRepository;

	@Override
	public Completable update(final ProfileEntity entity, final ProfileIdentifier profileIdentifier) {
		return customerRepository.getCustomer(profileIdentifier.getProfileId().getValue())
				.map(customer -> updateCustomerData(entity, customer))
				.flatMapCompletable(customerRepository::updateCustomerAsCompletable);
	}

	/**
	 * Updates the customer attributes with the profile entity.
	 *
	 * @param profileEntity profile entity
	 * @param customer      customer to update
	 * @return the updated customer
	 */
	protected Customer updateCustomerData(final ProfileEntity profileEntity, final Customer customer) {
		//it is apparently important that this if condition is checked otherwise
		//cucumber tests will fail
		if (profileEntity.getGivenName() != null) {
			customer.setFirstName(StringUtils.trimToNull(profileEntity.getGivenName()));
		}
		//it is apparently important that this if condition is checked otherwise
		//cucumber tests will fail
		if (profileEntity.getFamilyName() != null) {
			customer.setLastName(StringUtils.trimToNull(profileEntity.getFamilyName()));
		}
		return customer;
	}

	@Override
	@CacheResult
	public Single<ProfileEntity> findOne(final ProfileIdentifier profileIdentifier) {
		String profileId = profileIdentifier.getProfileId().getValue();
		return customerRepository.getCustomer(profileId)
				.map(this::convertCustomerToProfileEntity);
	}

	/**
	 * Converts given Customer to ProfileEntity.
	 *
	 * @param customer customer to convert
	 * @return the converted profile entity
	 */
	protected ProfileEntity convertCustomerToProfileEntity(final Customer customer) {
		return conversionService.convert(customer, ProfileEntity.class);
	}

	@Reference
	protected void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Reference
	public void setCustomerRepository(final CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
}
