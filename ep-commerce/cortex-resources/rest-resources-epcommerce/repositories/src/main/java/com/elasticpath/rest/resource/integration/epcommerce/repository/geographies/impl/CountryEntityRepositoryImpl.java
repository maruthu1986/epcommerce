/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.geographies.impl;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.geographies.CountriesIdentifier;
import com.elasticpath.rest.definition.geographies.CountryEntity;
import com.elasticpath.rest.definition.geographies.CountryIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Country Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class CountryEntityRepositoryImpl<E extends CountryEntity, I extends CountryIdentifier>
		implements Repository<CountryEntity, CountryIdentifier> {

	private static final String COUNTRY_NOT_FOUND_MESSAGE = "Cannot find country with scope %s";
	private static final String CANNOT_FIND_COUNTRY_CODES = "Cannot find country codes.";

	private ResourceOperationContext resourceOperationContext;
	private Geography geography;
	private ReactiveAdapter reactiveAdapter;

	@Override
	public Single<CountryEntity> findOne(final CountryIdentifier countryIdentifier) {
		String countryCode = countryIdentifier.getCountryId().getValue();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		String notFoundMsg = String.format(COUNTRY_NOT_FOUND_MESSAGE, countryIdentifier.getCountries().getScope().toString());

		return getCountryDisplayName(countryCode, locale, notFoundMsg)
				.flatMap(displayName -> Single.just(CountryEntity.builder()
						.withDisplayName(displayName)
						.withName(countryCode)
						.build()));
	}

	/**
	 * Returns the country display name given the country code and locale.
	 *
	 * @param countryCode country code
	 * @param locale      locale
	 * @param notFoundMsg error message when country not found
	 * @return country display name
	 */
	protected Single<String> getCountryDisplayName(final String countryCode, final Locale locale, final String notFoundMsg) {
		return reactiveAdapter.fromNullableAsSingle(() -> geography.getCountryDisplayName(countryCode, locale), notFoundMsg);
	}

	@Override
	public Observable<CountryIdentifier> findAll(final IdentifierPart<String> scope) {
		return getCountryCodes()
				.map(countryCode -> CountryIdentifier.builder()
						.withCountryId(StringIdentifier.of(countryCode))
						.withCountries(CountriesIdentifier.builder()
								.withScope(scope)
								.build())
						.build());
	}

	/**
	 * Returns all the country codes.
	 *
	 * @return country codes
	 */
	protected Observable<String> getCountryCodes() {
		return reactiveAdapter.fromNullable(() -> geography.getCountryCodes(), CANNOT_FIND_COUNTRY_CODES)
				.flatMapIterable(countryCodes -> countryCodes);
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setGeography(final Geography geography) {
		this.geography = geography;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}
