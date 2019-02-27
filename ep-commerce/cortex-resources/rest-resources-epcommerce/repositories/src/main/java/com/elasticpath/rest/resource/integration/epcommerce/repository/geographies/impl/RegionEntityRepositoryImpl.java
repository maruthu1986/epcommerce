/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.geographies.impl;

import java.util.Locale;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.misc.Geography;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.geographies.RegionEntity;
import com.elasticpath.rest.definition.geographies.RegionIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Region Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class RegionEntityRepositoryImpl<E extends RegionEntity, I extends RegionIdentifier>
		implements Repository<RegionEntity, RegionIdentifier> {

	private static final String REGION_NOT_FOUND_MESSAGE = "region not found.";

	private ResourceOperationContext resourceOperationContext;
	private Geography geography;
	private ReactiveAdapter reactiveAdapter;

	@Override
	public Single<RegionEntity> findOne(final RegionIdentifier regionIdentifier) {
		String countryCode = regionIdentifier
				.getRegions()
				.getCountry()
				.getCountryId()
				.getValue();
		String subCountryCode = regionIdentifier
				.getRegionId()
				.getValue();
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		return getSubCountryDisplayName(countryCode, subCountryCode, locale)
				.flatMap(subCountryDisplayName -> Single.just(RegionEntity.builder()
						.withDisplayName(subCountryDisplayName)
						.withName(subCountryCode)
						.build()));
	}

	/**
	 * Returns the sub country (region) display name.
	 *
	 * @param countryCode    countryCode
	 * @param subCountryCode subCountryCode
	 * @param locale         locale
	 * @return sub country display name
	 */
	protected Single<String> getSubCountryDisplayName(final String countryCode, final String subCountryCode, final Locale locale) {
		return reactiveAdapter.fromNullableAsSingle(() -> geography.getSubCountryDisplayName(countryCode, subCountryCode, locale),
				REGION_NOT_FOUND_MESSAGE);
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
