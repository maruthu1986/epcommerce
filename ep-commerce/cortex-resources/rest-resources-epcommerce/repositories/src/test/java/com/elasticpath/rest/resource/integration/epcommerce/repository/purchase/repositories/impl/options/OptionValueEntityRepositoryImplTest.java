/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.options;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Locale.CANADA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueIdentifier;
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

/**
 * Test for the  {@link OptionValueEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OptionValueEntityRepositoryImplTest {

	private static final String OPTION_VALUE = "optionValue";
	private static final String OPTION_ID = "optionId";
	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String RANDOM_OPTION_KEY = "someDifferentKey"; //Different from requested key
	private static final String OPTION_VALUE_DISPLAY = "display";

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private OptionValueEntityRepositoryImpl<PurchaseLineItemOptionValueEntity, PurchaseLineItemOptionValueIdentifier> repository;
	private PurchaseLineItemOptionValueIdentifier identifier;

	@Mock
	private ProductSku productSku;
	@Mock
	private Product product;
	@Mock
	private ProductType productType;
	@Mock
	private SkuOption skuOption1;
	@Mock
	private SkuOption skuOption2;
	@Mock
	private SkuOptionValue skuOptionValue;

	@Before
	public void setUp() {
		setUpPurchaseLineItemOptionValueIdentifier();
		setUpLocale();
	}

	@Test
	public void testNoSkuOptions() {

		Set<SkuOption> skuOptions = new HashSet<>();

		setUpProductSkuOptions(skuOptions);

		repository.findOne(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(OptionValueEntityRepositoryImpl.VALUE_NOT_FOUND));
	}

	@Test
	public void testOptionsNotFoundForOptionIds() {

		Set<SkuOption> skuOptions = new HashSet<>();
		skuOptions.add(skuOption1);
		when(skuOption1.getOptionKey()).thenReturn(RANDOM_OPTION_KEY);

		setUpProductSkuOptions(skuOptions);

		repository.findOne(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(OptionValueEntityRepositoryImpl.VALUE_NOT_FOUND));
	}

	@Test
	public void testOptionsWithNullValues() {

		Set<SkuOption> skuOptions = new HashSet<>();
		skuOptions.add(skuOption1);
		skuOptions.add(skuOption2);
		when(skuOption1.getOptionKey()).thenReturn(RANDOM_OPTION_KEY);
		when(skuOption2.getOptionKey()).thenReturn(OPTION_ID);

		when(skuOption2.getOptionValue(OPTION_VALUE)).thenReturn(null);

		setUpProductSkuOptions(skuOptions);

		repository.findOne(identifier)
				.test()
				.assertError(ResourceOperationFailure.notFound(OptionValueEntityRepositoryImpl.VALUE_NOT_FOUND));
	}

	@Test
	public void testOptionValueWithSuccess() {

		Set<SkuOption> skuOptions = new HashSet<>();
		skuOptions.add(skuOption2);
		when(skuOption2.getOptionKey()).thenReturn(OPTION_ID);

		when(skuOption2.getOptionValue(OPTION_VALUE)).thenReturn(skuOptionValue);
		when(skuOptionValue.getOptionValueKey()).thenReturn(OPTION_VALUE);
		when(skuOptionValue.getDisplayName(CANADA, true)).thenReturn(OPTION_VALUE_DISPLAY);

		setUpProductSkuOptions(skuOptions);

		PurchaseLineItemOptionValueEntity result = PurchaseLineItemOptionValueEntity.builder()
				.withName(OPTION_VALUE)
				.withDisplayName(OPTION_VALUE_DISPLAY)
				.build();

		repository.findOne(identifier)
				.test()
				.assertValue(result);
	}

	private void setUpProductSkuOptions(final Set<SkuOption> skuOptions) {
		when(orderRepository.findProductSku(any(), any(), any())).thenReturn(Single.just(productSku));
		when(productSku.getProduct()).thenReturn(product);
		when(product.getProductType()).thenReturn(productType);
		when(productType.getSkuOptions()).thenReturn(skuOptions);
	}

	private void setUpLocale() {
		SubjectAttribute attribute = new LocaleSubjectAttribute(CANADA.toLanguageTag(), CANADA);
		Subject subject = new ImmutableSubject(emptyList(), singleton(attribute));
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

	private void setUpPurchaseLineItemOptionValueIdentifier() {
		PurchaseLineItemIdentifier purchaseLineItem = PurchaseLineItemIdentifier.builder()
				.withLineItemId(PathIdentifier.of(LINE_ITEM_ID))
				.withPurchaseLineItems(IdentifierTestFactory.buildPurchaseLineItemsIdentifier(SCOPE, PURCHASE_ID))
				.build();
		PurchaseLineItemOptionsIdentifier options = PurchaseLineItemOptionsIdentifier.builder()
				.withPurchaseLineItem(purchaseLineItem)
				.build();
		PurchaseLineItemOptionIdentifier purchaseLineItemOptionIdentifier = PurchaseLineItemOptionIdentifier.builder()
				.withOptionId(StringIdentifier.of(OPTION_ID))
				.withPurchaseLineItemOptions(options)
				.build();
		identifier = PurchaseLineItemOptionValueIdentifier.builder()
				.withOptionValueId(StringIdentifier.of(OPTION_VALUE))
				.withPurchaseLineItemOption(purchaseLineItemOptionIdentifier)
				.build();
	}
}
