/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;

/**
 * Unit tests for {@link AutoSelectableBundleConstituentDelegateValidatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AutoSelectableBundleConstituentDelegateValidatorImplTest {

	private static final String ERROR_ID = "ERROR_ID";

	private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

	private static final String SKU_CODE_KEY = "SKU_CODE_KEY";

	private static final String BRANCH_SKU_CODE_1 = "BRANCH_SKU_CODE_1";

	private static final String BRANCH_SKU_CODE_2 = "BRANCH_SKU_CODE_2";

	private static final String SHOPPER_KEY = "SHOPPER_KEY";

	private static final String STORE_CODE_KEY = "STORE_CODE_KEY";

	private static final String SHOPPER_GUID = "SHOPPER_GUID";

	private static final String STORE_CODE = "STORE_CODE";

	@InjectMocks
	private AutoSelectableBundleConstituentDelegateValidatorImpl validator;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private ProductBundle rootBundle;

	@Mock
	private BundleConstituent branchConstituent1, branchConstituent2;

	@Mock
	private ConstituentItem branchConstituentItem1, branchConstituentItem2;

	@Mock
	private ProductSku rootProductSku, branchProductSku1, branchProductSku2;

	@Mock
	private Price price;

	@Mock
	private Store store;

	@Mock
	private Shopper shopper;

	@Mock
	private PriceLookupFacade priceLookupFacade;

	@Test
	public void testTreeWithNestedBundles() {
		// Given
		given(beanFactory.getBean(ContextIdNames.PRODUCT_SKU_VALIDATION_CONTEXT))
				.willAnswer(invocation -> new ProductSkuValidationContextImpl());
		given(priceLookupFacade.getPromotedPriceForSku(any(ProductSku.class), eq(store), eq(shopper))).willReturn(price);

		validator.setValidators(Collections.singleton(context -> ImmutableList.of(
				new StructuredErrorMessage(ERROR_ID, ERROR_MESSAGE, ImmutableMap.of(
						SKU_CODE_KEY, context.getProductSku().getSkuCode(),
						SHOPPER_KEY, context.getShopper().getGuid(),
						STORE_CODE_KEY, context.getStore().getCode())))));


		given(shopper.getGuid()).willReturn(SHOPPER_GUID);
		given(store.getCode()).willReturn(STORE_CODE);

		/*
					rootBundle
				/				\
			branchBundle1	branchBundle2
				|				|
			leafBundle1		leafBundle2

		 */
		givenBundleTreeStructure();

		// Conditions on each node
		given(rootBundle.isConstituentAutoSelectable(branchConstituent1)).willReturn(true);
		given(rootBundle.isConstituentAutoSelectable(branchConstituent2)).willReturn(true);

		final ProductSkuValidationContext context = new ProductSkuValidationContextImpl();
		context.setProductSku(rootProductSku);
		context.setStore(store);
		context.setShopper(shopper);

		// When
		Collection<StructuredErrorMessage> errorMessages = validator.validate(context);

		// Then
		assertThat(errorMessages)
				.extracting(StructuredErrorMessage::getData)
				.extracting(SKU_CODE_KEY, SHOPPER_KEY, STORE_CODE_KEY)
				.containsExactlyInAnyOrder(
						tuple(BRANCH_SKU_CODE_1, SHOPPER_GUID, STORE_CODE),
						tuple(BRANCH_SKU_CODE_2, SHOPPER_GUID, STORE_CODE)
				);
	}

	private void givenBundleTreeStructure() {
		given(rootProductSku.getProduct()).willReturn(rootBundle);
		given(rootBundle.getConstituents()).willReturn(ImmutableList.of(branchConstituent1, branchConstituent2));
		given(branchConstituent1.getConstituent()).willReturn(branchConstituentItem1);
		given(branchConstituent2.getConstituent()).willReturn(branchConstituentItem2);
		given(branchConstituentItem1.getProductSku()).willReturn(branchProductSku1);
		given(branchConstituentItem2.getProductSku()).willReturn(branchProductSku2);

		given(branchProductSku1.getSkuCode()).willReturn(BRANCH_SKU_CODE_1);
		given(branchProductSku2.getSkuCode()).willReturn(BRANCH_SKU_CODE_2);
	}
}
