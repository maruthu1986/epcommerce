/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.shippinginfo;

import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorIdentifier;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.ShippingOptionRepository;
import com.elasticpath.rest.selector.Choice;
import com.elasticpath.rest.selector.ChoiceStatus;
import com.elasticpath.rest.selector.SelectResult;
import com.elasticpath.rest.selector.SelectStatus;
import com.elasticpath.rest.selector.SelectorChoice;

/**
 * Shipping option info selector repository.
 *
 * @param <SI>	extends ShippingOptionInfoSelectorIdentifier
 * @param <CI>	extends ShippingOptionInfoSelectorChoiceIdentifier
 */
@Component
public class ShippingOptionInfoSelectorRepositoryImpl<SI extends ShippingOptionInfoSelectorIdentifier,
		CI extends ShippingOptionInfoSelectorChoiceIdentifier> implements SelectorRepository<ShippingOptionInfoSelectorIdentifier,
		ShippingOptionInfoSelectorChoiceIdentifier> {

	private ShippingOptionRepository shippingOptionRepository;
	private CartOrderRepository cartOrderRepository;

	@Override
	public Observable<SelectorChoice> getChoices(final ShippingOptionInfoSelectorIdentifier selectorId) {
		ShippingOptionInfoIdentifier shippingOptionInfoIdentifier = selectorId.getShippingOptionInfo();
		String scope = shippingOptionInfoIdentifier.getScope().getValue();
		Map<String, String> shipmentDetailsId = shippingOptionInfoIdentifier.getShipmentDetailsId().getValue();
		return shippingOptionRepository.findShippingOptionCodesForShipment(scope, shipmentDetailsId)
				.flatMapSingle(shippingOptionId -> buildSelectorChoiceIfExists(selectorId, scope, shipmentDetailsId, shippingOptionId));
	}

	private Single<SelectorChoice> buildSelectorChoiceIfExists(final ShippingOptionInfoSelectorIdentifier selectorId,
															   final String scope,
															   final Map<String, String> shipmentDetailsId,
															   final String shippingOptionId) {
		ShippingOptionIdentifier shippingOptionIdentifier = ShippingOptionIdentifier.builder()
				.withScope(StringIdentifier.of(scope))
				.withShippingOptionId(StringIdentifier.of(shippingOptionId))
				.withShipmentDetailsId(CompositeIdentifier.of(shipmentDetailsId))
				.build();

		return shippingOptionRepository.getSelectedShippingOptionCodeForShipmentDetails(scope, shipmentDetailsId)
				.map(selectedShippingOptionId -> buildSelectorChoice(selectorId, shippingOptionIdentifier,
						getChoiceStatus(shippingOptionId, selectedShippingOptionId)))
				.switchIfEmpty(Maybe.just(buildSelectorChoice(selectorId, shippingOptionIdentifier, getChoiceStatus(shippingOptionId, ""))))
				.toSingle();
	}

	private ChoiceStatus getChoiceStatus(final String shippingOptionId, final String selectedShippingOptionId) {
		return shippingOptionId.equals(selectedShippingOptionId) ? ChoiceStatus.CHOSEN : ChoiceStatus.CHOOSABLE;
	}

	private SelectorChoice buildSelectorChoice(final ShippingOptionInfoSelectorIdentifier shippingOptionInfoSelectorIdentifier,
											   final ShippingOptionIdentifier shippingOptionIdentifier,
											   final ChoiceStatus choiceStatus) {
		return SelectorChoice.builder()
				.withChoice(buildShippingOptionInfoSelectorChoiceIdentifier(shippingOptionInfoSelectorIdentifier, shippingOptionIdentifier))
				.withStatus(choiceStatus)
				.build();
	}

	private ShippingOptionInfoSelectorChoiceIdentifier buildShippingOptionInfoSelectorChoiceIdentifier(
			final ShippingOptionInfoSelectorIdentifier shippingOptionInfoSelectorIdentifier,
			final ShippingOptionIdentifier shippingOptionIdentifier) {
		return ShippingOptionInfoSelectorChoiceIdentifier.builder()
				.withShippingOption(shippingOptionIdentifier)
				.withShippingOptionInfoSelector(shippingOptionInfoSelectorIdentifier)
				.build();
	}

	@Override
	public Single<Choice> getChoice(final ShippingOptionInfoSelectorChoiceIdentifier shippingOptionInfoSelectorChoiceIdentifier) {
		ShippingOptionIdentifier shippingOptionIdentifier = shippingOptionInfoSelectorChoiceIdentifier.getShippingOption();
		String scope = shippingOptionIdentifier.getScope().getValue();
		Map<String, String> shipmentDetailsid = shippingOptionIdentifier.getShipmentDetailsId().getValue();
		return shippingOptionRepository.getSelectedShippingOptionCodeForShipmentDetails(scope, shipmentDetailsid)
				.map(selectedShippingOptionId -> buildChoice(shippingOptionInfoSelectorChoiceIdentifier, selectedShippingOptionId))
				.switchIfEmpty(Maybe.just(buildChoice(shippingOptionInfoSelectorChoiceIdentifier, "")))
				.toSingle();
	}

	private Choice buildChoice(final ShippingOptionInfoSelectorChoiceIdentifier shippingOptionInfoSelectorChoiceIdentifier,
							   final String selectedShippingOptionId) {
		ShippingOptionIdentifier shippingOptionIdentifier = shippingOptionInfoSelectorChoiceIdentifier.getShippingOption();
		String shippingOptionId = shippingOptionIdentifier.getShippingOptionId().getValue();
		boolean isSelected = selectedShippingOptionId.equals(shippingOptionId);
		ChoiceStatus choiceStatus = isSelected ? ChoiceStatus.CHOSEN : ChoiceStatus.CHOOSABLE;
		return buildChoiceIfSelected(shippingOptionInfoSelectorChoiceIdentifier, shippingOptionIdentifier, isSelected, choiceStatus);
	}

	private Choice buildChoiceIfSelected(final ShippingOptionInfoSelectorChoiceIdentifier shippingOptionInfoSelectorChoiceIdentifier,
										 final ShippingOptionIdentifier shippingOptionIdentifier,
										 final boolean isSelected,
										 final ChoiceStatus choiceStatus) {
		Choice.Builder choiceBuilder = Choice.builder()
				.withDescription(shippingOptionIdentifier)
				.withStatus(choiceStatus);
		if (!isSelected) {
			choiceBuilder.withAction(shippingOptionInfoSelectorChoiceIdentifier);
		}
		return choiceBuilder.build();
	}

	@Override
	public Single<SelectResult<ShippingOptionInfoSelectorIdentifier>> selectChoice(final ShippingOptionInfoSelectorChoiceIdentifier
																						   shippingOptionInfoSelectorChoiceIdentifier) {
		ShippingOptionIdentifier shippingOptionIdentifier = shippingOptionInfoSelectorChoiceIdentifier.getShippingOption();
		String scope = shippingOptionIdentifier.getScope().getValue();
		Map<String, String> shipmentDetailsId =  shippingOptionIdentifier.getShipmentDetailsId().getValue();
		String shippingOptionId = shippingOptionIdentifier.getShippingOptionId().getValue();
		return cartOrderRepository.findByShipmentDetailsId(scope, shipmentDetailsId)
				.flatMap(cartOrder -> updateAndSaveCartOrder(shippingOptionId, cartOrder))
				.map(selectStatus -> SelectResult.<ShippingOptionInfoSelectorIdentifier>builder()
						.withIdentifier(shippingOptionInfoSelectorChoiceIdentifier.getShippingOptionInfoSelector())
						.withStatus(selectStatus)
						.build());
	}

	private Single<SelectStatus> updateAndSaveCartOrder(final String shippingOptionId, final CartOrder cartOrder) {
		if (shippingOptionId.equals(cartOrder.getShippingOptionCode())) {
			return Single.just(SelectStatus.EXISTING);
		} else {
			cartOrder.setShippingOptionCode(shippingOptionId);
			return cartOrderRepository.saveCartOrderAsSingle(cartOrder)
					.flatMap(savedCartOrder -> Single.just(SelectStatus.SELECTED));
		}
	}

	@Reference
	public void setShippingOptionRepository(final ShippingOptionRepository shippingOptionRepository) {
		this.shippingOptionRepository = shippingOptionRepository;
	}

	@Reference
	public void setCartOrderRepository(final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}
}
