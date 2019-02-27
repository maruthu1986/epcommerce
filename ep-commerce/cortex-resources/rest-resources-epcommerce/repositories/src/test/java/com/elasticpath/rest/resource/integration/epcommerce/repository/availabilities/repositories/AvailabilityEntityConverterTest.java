/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.availabilities.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.availabilities.AvailabilityTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.transform.DateTransformer;
import com.elasticpath.rest.util.date.DateUtil;

/**
 * Test for {@link AvailabilityEntityConverter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AvailabilityEntityConverterTest {

	private static final Locale TEST_LOCALE = Locale.CANADA;
	private static final String USER_ID = "userid";
	private final Date testDate = new Date();
	private final Subject testSubject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(ResourceTestConstants.SCOPE, USER_ID, TEST_LOCALE);

	@Mock
	private StoreProduct storeProduct;

	@Mock
	private ProductSku productSku;

	@Mock
	private DateTransformer dateTransformer;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@InjectMocks
	private AvailabilityEntityConverter converter;

	@Test
	public void shouldGetAvailabilityWithReleaseDate() {
		when(productSku.getSkuCode()).thenReturn(ResourceTestConstants.SKU_CODE);
		when(storeProduct.getSkuAvailability(ResourceTestConstants.SKU_CODE)).thenReturn(Availability.NOT_AVAILABLE);
		when(storeProduct.getExpectedReleaseDate()).thenReturn(testDate);
		when(dateTransformer.transformToEntity(testDate, TEST_LOCALE))
				.thenReturn(AvailabilityTestFactory.createDateEntity(testDate.getTime(), DateUtil.formatDateTime(testDate, TEST_LOCALE)));
		when(resourceOperationContext.getSubject()).thenReturn(testSubject);

		final AvailabilityEntity availabilityEntity = converter.convert(new Pair<>(storeProduct, productSku));
		assertThat(availabilityEntity.getState()).isEqualTo(Availability.NOT_AVAILABLE.toString());
		assertThat(availabilityEntity.getReleaseDate()).isNotNull();
	}

	@Test
	public void shouldGetAvailabilityWithNoReleaseDate() {
		when(productSku.getSkuCode()).thenReturn(ResourceTestConstants.SKU_CODE);
		when(storeProduct.getSkuAvailability(ResourceTestConstants.SKU_CODE)).thenReturn(Availability.AVAILABLE);
		when(storeProduct.getExpectedReleaseDate()).thenReturn(null);
		when(resourceOperationContext.getSubject()).thenReturn(testSubject);

		final AvailabilityEntity availabilityEntity = converter.convert(new Pair<>(storeProduct, productSku));
		assertThat(availabilityEntity.getState()).isEqualTo(Availability.AVAILABLE.toString());
		assertThat(availabilityEntity.getReleaseDate()).isNull();
	}
}