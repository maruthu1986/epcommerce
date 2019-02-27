/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.options;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.id.type.PathIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

/**
 * Test for the  {@link OptionsToOptionValuesLinksRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OptionsToOptionValuesLinksRepositoryImplTest {

	private static final String OPTION_ID = "optionId";
	private static final String SKU_GUID = "skuGuid";
	private static final String OPTION_VALUE = "optionValue";

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;
	@InjectMocks
	private OptionsToOptionValuesLinksRepositoryImpl<PurchaseLineItemOptionIdentifier, PurchaseLineItemOptionValueIdentifier> repository;
	@Mock
	private OrderRepository orderRepository;
	private PurchaseLineItemOptionIdentifier identifier;

	@Mock
	private ProductSku productSku;
	@Mock
	private SkuOptionValue optionValue;

	@Before
	public void setUp() {
		repository.setReactiveAdapter(reactiveAdapter);
		setUpPurchaseLineItemOptionIdentifier();
		when(orderRepository.findProductSku(any(), any(), any())).thenReturn(Single.just(productSku));
	}

	@Test
	public void testOptionValueForOptionIdentifier() {

		Map<String, SkuOptionValue> optionValueMap = new HashMap<>();
		optionValueMap.put(OPTION_ID, optionValue);
		when(productSku.getOptionValueMap()).thenReturn(optionValueMap);
		when(optionValue.getOptionValueKey()).thenReturn(OPTION_VALUE);

		repository.getElements(identifier)
				.test()
				.assertValueSequence(ImmutableList.of(PurchaseLineItemOptionValueIdentifier.builder()
						.withOptionValueId(StringIdentifier.of(OPTION_VALUE))
						.withPurchaseLineItemOption(identifier)
						.build()
				));
	}

	@Test
	public void testOptionValueNotFoundInMap() {

		when(productSku.getOptionValueMap()).thenReturn(new HashMap<>());

		repository.getElements(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(OptionsToOptionValuesLinksRepositoryImpl.VALUE_FOR_OPTION_NOT_FOUND));
	}

	private void setUpPurchaseLineItemOptionIdentifier() {
		PurchaseLineItemIdentifier purchaseLineItem = PurchaseLineItemIdentifier.builder()
				.withLineItemId(PathIdentifier.of(SKU_GUID))
				.withPurchaseLineItems(IdentifierTestFactory.buildPurchaseLineItemsIdentifier(SCOPE, PURCHASE_ID))
				.build();
		PurchaseLineItemOptionsIdentifier options = PurchaseLineItemOptionsIdentifier.builder()
				.withPurchaseLineItem(purchaseLineItem)
				.build();
		identifier = PurchaseLineItemOptionIdentifier.builder()
				.withOptionId(StringIdentifier.of(OPTION_ID))
				.withPurchaseLineItemOptions(options)
				.build();
	}
}
