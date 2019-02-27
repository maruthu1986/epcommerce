/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.availabilities.repositories;

import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Locale;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.availabilities.AvailabilityForItemIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.id.Identifier;
import com.elasticpath.rest.id.transform.IdentifierTransformer;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.availabilities.AvailabilityTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.util.date.DateUtil;

/**
 * Test for {@link ItemAvailabilityEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemAvailabilityEntityRepositoryImplTest {

	private static final String ITEM_ID = "itemid";
	private static final String PRODUCT_GUID = "product guid";
	private static final Date DATE = new Date();

	@Mock
	private ItemRepository itemRepository;
	@Mock
	private StoreProductRepository storeProductRepository;
	@Mock
	private ConversionService conversionService;
	@Mock
	private IdentifierTransformerProvider identifierTransformerProvider;
	@Mock
	private ProductSku productSku;
	@Mock
	private Product product;
	@Mock
	private StoreProduct storeProduct;
	@Mock
	private IdentifierTransformer<Identifier> identifierTransformer;

	@InjectMocks
	private ItemAvailabilityEntityRepositoryImpl<AvailabilityEntity, AvailabilityForItemIdentifier> itemAvailabilityEntityRepository;

	@Before
	public void setUp() {
		when(identifierTransformerProvider.forUriPart(ItemIdentifier.ITEM_ID)).thenReturn(identifierTransformer);
		when(identifierTransformer.identifierToUri(CompositeIdentifier.of(ItemRepository.SKU_CODE_KEY, ITEM_ID))).thenReturn(ITEM_ID);
		when(itemRepository.getSkuForItemIdAsSingle(ITEM_ID)).thenReturn(Single.just(productSku));
		when(productSku.getProduct()).thenReturn(product);
		when(product.getGuid()).thenReturn(PRODUCT_GUID);
		when(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuidAsSingle(ResourceTestConstants.SCOPE, PRODUCT_GUID))
				.thenReturn(Single.just(storeProduct));
		when(conversionService.convert(new Pair<>(storeProduct, productSku), AvailabilityEntity.class))
				.thenReturn(AvailabilityTestFactory.createAvailabilityEntity(
						Availability.AVAILABLE.getName(),
						DATE.getTime(),
						DateUtil.formatDateTime(DATE, Locale.CANADA)));
	}

	@Test
	public void findOneReturnsItemAvailabilityEntityWithReleaseDateAndState() {
		itemAvailabilityEntityRepository.findOne(
				AvailabilityTestFactory.createAvailabilityForItemIdentifier(ITEM_ID, ResourceTestConstants.SCOPE))
				.test()
				.assertValue(itemAvailabilityEntity -> itemAvailabilityEntity.getReleaseDate().getValue().equals(DATE.getTime()))
				.assertValue(itemAvailabilityEntity -> itemAvailabilityEntity.getReleaseDate().getDisplayValue()
						.equals(DateUtil.formatDateTime(DATE, Locale.CANADA)))
				.assertValue(itemAvailabilityEntity -> itemAvailabilityEntity.getState().equals(Availability.AVAILABLE.getName()));
	}

	@Test
	public void findOneReturnsErrorWhenNoSkuFoundForTheGivenItemId() {
		String itemNotFound = "Item not found";
		when(itemRepository.getSkuForItemIdAsSingle(ITEM_ID)).thenReturn(Single.error(ResourceOperationFailure.notFound(itemNotFound)));
		itemAvailabilityEntityRepository.findOne(
				AvailabilityTestFactory.createAvailabilityForItemIdentifier(ITEM_ID, ResourceTestConstants.SCOPE))
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertErrorMessage(itemNotFound);
	}

	@Test
	public void findOneReturnsErrorWhenProductNotFoundInTheStore() {
		String productNotFound = "Product not found";
		when(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuidAsSingle(ResourceTestConstants.SCOPE, PRODUCT_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(productNotFound)));
		itemAvailabilityEntityRepository.findOne(
				AvailabilityTestFactory.createAvailabilityForItemIdentifier(ITEM_ID, ResourceTestConstants.SCOPE))
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertErrorMessage(productNotFound);
	}
}