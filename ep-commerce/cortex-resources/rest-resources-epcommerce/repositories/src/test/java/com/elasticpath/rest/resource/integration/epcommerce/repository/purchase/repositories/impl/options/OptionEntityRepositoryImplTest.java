/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.options;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.id.type.PathIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.attribute.LocaleSubjectAttribute;
import com.elasticpath.rest.identity.attribute.SubjectAttribute;
import com.elasticpath.rest.identity.type.ImmutableSubject;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

/**
 * Test for the  {@link OptionEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OptionEntityRepositoryImplTest {

	private static final String OPTION_ID = "optionId";
	private static final String LINE_ITEM_ID = "itemId";
	private static final String OPTION_KEY = "optionKey";
	private static final String DISPLAY_NAME = "displayName";
	private static final String OPTION_VALUE_KEY = "optionValueKey";
	private static final Locale CANADA = Locale.CANADA;
	@Mock
	private ProductSku productSku;
	@Mock
	private SkuOptionValue optionValue;
	@Mock
	private SkuOption skuOption;
	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;
	@InjectMocks
	private OptionEntityRepositoryImpl<PurchaseLineItemOptionEntity, PurchaseLineItemOptionIdentifier> repository;
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private OrderRepository orderRepository;


	private PurchaseLineItemOptionIdentifier identifier;

	@Before
	public void setUp() {
		repository.setReactiveAdapter(reactiveAdapter);
		setUpPurchaseLineItemOptionIdentifier();
		setUpLocale();
		setUpSkuOption();
	}

	@Test
	public void checkPurchaseLineItemOptionEntity() {
		Map<String, SkuOptionValue> map = new HashMap<>();
		map.put(OPTION_ID, optionValue);

		when(orderRepository.findProductSku(any(), any(), any())).thenReturn(Single.just(productSku));
		when(productSku.getOptionValueMap()).thenReturn(map);

		PurchaseLineItemOptionEntity result = PurchaseLineItemOptionEntity.builder()
				.withName(OPTION_KEY)
				.withDisplayName(DISPLAY_NAME)
				.withSelectedValueId(OPTION_VALUE_KEY)
				.withOptionId(OPTION_ID)
				.build();

		repository.findOne(identifier)
				.test()
				.assertValue(result);
	}

	@Test
	public void testWithError() {
		ResourceOperationFailure notFound = ResourceOperationFailure.notFound();
		when(orderRepository.findProductSku(any(), any(), any())).thenReturn(Single.error(notFound));

		repository.findOne(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(OptionEntityRepositoryImpl.OPTION_NOT_FOUND_FOR_ITEM));
	}

	@Test
	public void testNoOptions() {
		when(orderRepository.findProductSku(any(), any(), any())).thenReturn(Single.just(productSku));
		when(productSku.getOptionValueMap()).thenReturn(new HashMap<>());

		repository.findOne(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(OptionEntityRepositoryImpl.OPTION_NOT_FOUND_FOR_ITEM));
	}


	private void setUpLocale() {
		SubjectAttribute attribute = new LocaleSubjectAttribute(CANADA.toLanguageTag(), CANADA);
		Subject subject = new ImmutableSubject(emptyList(), singleton(attribute));
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

	private void setUpSkuOption() {
		when(optionValue.getSkuOption()).thenReturn(skuOption);
		when(skuOption.getOptionKey()).thenReturn(OPTION_KEY);
		when(skuOption.getDisplayName(CANADA, true)).thenReturn(DISPLAY_NAME);
		when(optionValue.getOptionValueKey()).thenReturn(OPTION_VALUE_KEY);
		when(skuOption.getGuid()).thenReturn(OPTION_ID);
	}

	private void setUpPurchaseLineItemOptionIdentifier() {
		PurchaseLineItemIdentifier purchaseLineItem = PurchaseLineItemIdentifier.builder()
				.withPurchaseLineItems(IdentifierTestFactory.buildPurchaseLineItemsIdentifier(SCOPE, PURCHASE_ID))
				.withLineItemId(PathIdentifier.of(LINE_ITEM_ID))
				.build();
		PurchaseLineItemOptionsIdentifier options = PurchaseLineItemOptionsIdentifier.builder()
				.withPurchaseLineItem(purchaseLineItem)
				.build();
		identifier = PurchaseLineItemOptionIdentifier.builder()
				.withPurchaseLineItemOptions(options)
				.withOptionId(StringIdentifier.of(OPTION_ID))
				.build();
	}

}
