/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.shippinginfo;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl.CartOrderRepositoryImpl.ORDER_WITH_GUID_NOT_FOUND;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil.createShipmentDetailsId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingOptionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl.ShippingOptionRepositoryImpl;
import com.elasticpath.rest.selector.ChoiceStatus;

/**
 * Test for {@link ShippingOptionInfoSelectorRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionInfoSelectorRepositoryImplTest {

	private static final String SCOPE = "scope";
	private static final String ORDER_ID = "orderId";
	private static final String SELECTED_ID = "selectedId";
	private static final String NOT_SELECTED = "notSelected";
	private static final int NUM_OF_IDS = 2;
	private final Map<String, String> shipmentDetailsId = createShipmentDetailsId(ORDER_ID, ShipmentDetailsConstants.SHIPMENT_TYPE);
	private final IdentifierPart<String> scopeIdentifierPart = StringIdentifier.of(SCOPE);
	private final IdentifierPart<Map<String, String>> shipmentDetailsIdentifierPart = CompositeIdentifier.of(shipmentDetailsId);

	private final ShippingOptionIdentifier shippingOptionIdentifier = ShippingOptionIdentifier.builder()
			.withShipmentDetailsId(shipmentDetailsIdentifierPart)
			.withScope(scopeIdentifierPart)
			.withShippingOptionId(StringIdentifier.of(SELECTED_ID))
			.build();

	private final ShippingOptionInfoIdentifier shippingOptionInfoIdentifier = ShippingOptionInfoIdentifier.builder()
			.withScope(StringIdentifier.of(SCOPE))
			.withShipmentDetailsId(CompositeIdentifier.of(shipmentDetailsId))
			.build();

	private final ShippingOptionInfoSelectorIdentifier selectorIdentifier = ShippingOptionInfoSelectorIdentifier.builder()
			.withShippingOptionInfo(shippingOptionInfoIdentifier)
			.build();

	private final ShippingOptionInfoSelectorChoiceIdentifier choiceIdentifier = ShippingOptionInfoSelectorChoiceIdentifier.builder()
			.withShippingOptionInfoSelector(selectorIdentifier)
			.withShippingOption(shippingOptionIdentifier)
			.build();

	@Mock
	private CartOrder cartOrder;

	@InjectMocks
	private ShippingOptionInfoSelectorRepositoryImpl<ShippingOptionInfoSelectorIdentifier, ShippingOptionInfoSelectorChoiceIdentifier> repository;

	@Mock
	private ShippingOptionRepository shippingOptionRepository;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Test
	public void verifyGetChoicesReturnsNotFoundWhenNoShippingIdsAreFound() {
		when(shippingOptionRepository.findShippingOptionCodesForShipment(SCOPE, shipmentDetailsId))
				.thenReturn(Observable.error(ResourceOperationFailure.notFound(ShippingOptionRepositoryImpl.SHIPPING_OPTIONS_NOT_FOUND)));

		repository.getChoices(selectorIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ShippingOptionRepositoryImpl.SHIPPING_OPTIONS_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetChoicesReturnsShippingOptionIdsWhenShippingIdsAreFound() {
		List<String> shippingOptionIdentifiers = new ArrayList<>(NUM_OF_IDS);
		shippingOptionIdentifiers.add(SELECTED_ID);
		shippingOptionIdentifiers.add(NOT_SELECTED);

		when(shippingOptionRepository.findShippingOptionCodesForShipment(SCOPE, shipmentDetailsId))
				.thenReturn(Observable.fromIterable(shippingOptionIdentifiers));
		when(shippingOptionRepository.getSelectedShippingOptionCodeForShipmentDetails(SCOPE, shipmentDetailsId))
				.thenReturn(Maybe.just(SELECTED_ID));

		repository.getChoices(selectorIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(NUM_OF_IDS)
				.assertValueAt(0, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOSEN)
				.assertValueAt(1, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOOSABLE);
	}

	@Test
	public void verifyGetChoicesReturnChoosableWhenNoSelectedIdIsFound() {
		List<String> shippingOptionIdentifiers = new ArrayList<>(NUM_OF_IDS);
		shippingOptionIdentifiers.add(SELECTED_ID);
		shippingOptionIdentifiers.add(NOT_SELECTED);

		when(shippingOptionRepository.findShippingOptionCodesForShipment(SCOPE, shipmentDetailsId))
				.thenReturn(Observable.fromIterable(shippingOptionIdentifiers));
		when(shippingOptionRepository.getSelectedShippingOptionCodeForShipmentDetails(SCOPE, shipmentDetailsId)).thenReturn(Maybe.empty());

		repository.getChoices(selectorIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(NUM_OF_IDS)
				.assertValueAt(0, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOOSABLE)
				.assertValueAt(1, selectorChoice -> selectorChoice.getStatus() == ChoiceStatus.CHOOSABLE);
	}

	@Test
	public void verifyGetChoiceReturnsChoosableWhenNoSelectedIdIsFound() {
		when(shippingOptionRepository.getSelectedShippingOptionCodeForShipmentDetails(SCOPE, shipmentDetailsId)).thenReturn(Maybe.empty());

		repository.getChoice(choiceIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(choice -> choice.getStatus() == ChoiceStatus.CHOOSABLE);
	}

	@Test
	public void verifyGetChoiceReturnsChoosableWhenShippingOptionIsNotSelected() {
		when(shippingOptionRepository.getSelectedShippingOptionCodeForShipmentDetails(SCOPE, shipmentDetailsId))
				.thenReturn(Maybe.just(NOT_SELECTED));

		repository.getChoice(choiceIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(choice -> choice.getStatus() == ChoiceStatus.CHOOSABLE);
	}

	@Test
	public void verifyGetChoiceReturnsChosenWhenShippingOptionIsSelected() {
		when(shippingOptionRepository.getSelectedShippingOptionCodeForShipmentDetails(SCOPE, shipmentDetailsId))
				.thenReturn(Maybe.just(SELECTED_ID));

		repository.getChoice(choiceIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(choice -> choice.getStatus() == ChoiceStatus.CHOSEN);
	}

	@Test
	public void verifySelectChoiceReturnsCompletable() {
		when(cartOrderRepository.findByShipmentDetailsId(SCOPE, shipmentDetailsId)).thenReturn(Single.just(cartOrder));
		when(cartOrderRepository.saveCartOrderAsSingle(cartOrder)).thenReturn(Single.just(cartOrder));

		repository.selectChoice(choiceIdentifier)
				.test()
				.assertNoErrors()
				.assertComplete();

		verify(cartOrder).setShippingOptionCode(SELECTED_ID);
		verify(cartOrderRepository).saveCartOrderAsSingle(cartOrder);
	}

	@Test
	public void verifySelectChoiceIsNotCompleteWhenShipmentDetailsIdDoesNotExist() {
		String errorMsg = String.format(ORDER_WITH_GUID_NOT_FOUND, ORDER_ID, SCOPE);
		when(cartOrderRepository.findByShipmentDetailsId(SCOPE, shipmentDetailsId))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(errorMsg)));

		repository.selectChoice(choiceIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(errorMsg, ResourceStatus.NOT_FOUND));
	}
}
