/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.AddToCartAdvisorService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.LineItemIdentifierRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * Repository for Line Item Entities.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class LineItemEntityRepository<E extends LineItemEntity, I extends LineItemIdentifier>
		implements Repository<LineItemEntity, LineItemIdentifier> {

	private ShoppingCartRepository shoppingCartRepository;

	private ProductSkuRepository productSkuRepository;

	private ItemRepository itemRepository;

	private CartItemModifiersRepository cartItemModifiersRepository;

	private AddToCartAdvisorService addToCartAdvisorService;

	private LineItemIdentifierRepository lineItemIdentifierRepository;

	private static final String INVALID_BUNDLE_CONFIGURATION = "Dynamic Bundle not supported";

	@Override
	public Single<LineItemEntity> findOne(final LineItemIdentifier identifier) {
		String cartId = identifier.getLineItems().getCart().getCartId().getValue();
		String lineItemId = identifier.getLineItemId().getValue();

		return getShoppingCartForItemIdentifier(identifier)
				.flatMap(cart -> shoppingCartRepository.getShoppingItem(lineItemId, cart)
						.flatMap(shoppingItem -> productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(shoppingItem.getSkuGuid())
								.flatMap(productSku -> itemRepository.getItemIdForSkuAsSingle(productSku)
										.flatMap(itemId -> buildLineItemEntity(cartId, lineItemId, shoppingItem, itemId)))));
	}

	/**
	 * Build line item entity.
	 *
	 * @param cartId       cartId
	 * @param lineItemId   lineItemId
	 * @param shoppingItem shoppingItem
	 * @param itemId       itemId
	 * @return LineItemEntity
	 */
	protected Single<LineItemEntity> buildLineItemEntity(final String cartId, final String lineItemId, final ShoppingItem shoppingItem,
														 final String itemId) {
		Map<String, String> fields = shoppingItem.getFields();
		Single<LineItemConfigurationEntity> lineItemConfigurationEntity;
		if (fields == null) {
			lineItemConfigurationEntity = Single.just(LineItemConfigurationEntity.builder().build());
		} else {
			lineItemConfigurationEntity = cartItemModifiersRepository.findCartItemModifierValues(cartId, lineItemId)
					.map(this::buildLineItemConfigurationEntity);
		}
		return lineItemConfigurationEntity.map(configuration -> LineItemEntity.builder()
				.withCartId(cartId)
				.withItemId(itemId)
				.withLineItemId(shoppingItem.getGuid())
				.withQuantity(shoppingItem.getQuantity())
				.withConfiguration(configuration).build());
	}

	/**
	 * Build the line item configuration entity.
	 *
	 * @param cartItemModifierValues Cart Item Modifier Values
	 * @return line item configuration entitiy
	 */
	protected LineItemConfigurationEntity buildLineItemConfigurationEntity(final Map<CartItemModifierField, String> cartItemModifierValues) {
		LineItemConfigurationEntity.Builder configBuilder = LineItemConfigurationEntity.builder();
		cartItemModifierValues.forEach((cartItemModifierField, value) -> configBuilder.addingProperty(cartItemModifierField.getCode(), value));
		return configBuilder.build();
	}

	@Override
	public Single<SubmitResult<LineItemIdentifier>> submit(final LineItemEntity lineItemEntity, final IdentifierPart<String> scope) {
		return addToCartAdvisorService.validateLineItemEntity(lineItemEntity)
				.andThen(addToCart(lineItemEntity));
	}

	/**
	 * Validate if the item is purchasable.
	 *
	 * @param lineItemEntity line item entity
	 * @param scope          scope
	 * @return ResourceOperationFailure if item is not purchasable
	 */
	protected Completable isItemPurchasable(final LineItemEntity lineItemEntity, final IdentifierPart<String> scope) {
		return addToCartAdvisorService.validateItemPurchasable(scope.getValue(), lineItemEntity.getItemId())
				.flatMapCompletable(this::getStateFailure);
	}

	/**
	 * Add item to cart.
	 *
	 * @param lineItemEntity line item entity
	 * @return line item identifier
	 */
	protected Single<SubmitResult<LineItemIdentifier>> addToCart(final LineItemEntity lineItemEntity) {
		String itemId = lineItemEntity.getItemId();
		final Map<String, String> fields = Optional.ofNullable(lineItemEntity.getConfiguration())
				.map(LineItemConfigurationEntity::getDynamicProperties)
				.orElse(Collections.emptyMap());

		return getShoppingCartForItemEntity(lineItemEntity)
				.flatMap(cart -> shoppingCartRepository.addItemToCart(cart, itemId, lineItemEntity.getQuantity(), fields)
						.map(shoppingItem -> buildResult(cart, shoppingItem, lineItemEntity)))
				.onErrorResumeNext(throwable -> {
					if (throwable instanceof InvalidBundleTreeStructureException) {
						return Single.error(ResourceOperationFailure.notFound(INVALID_BUNDLE_CONFIGURATION, throwable));
					}
					return Single.error(throwable);
				});
	}



	/**
	 * Build the line item identifier and set the appropriate status.
	 *
	 * @param cart           the shopping cart
	 * @param shoppingItem   the new shopping item
	 * @param lineItemEntity the line item entity
	 * @return line item identifier and the status
	 */
	protected SubmitResult<LineItemIdentifier> buildResult(final ShoppingCart cart, final ShoppingItem shoppingItem,
														   final LineItemEntity lineItemEntity) {
		SubmitStatus status = lineItemEntity.getQuantity() == shoppingItem.getQuantity()
				? SubmitStatus.CREATED
				: SubmitStatus.UPDATED;
		return SubmitResult.<LineItemIdentifier>builder()
				.withIdentifier(lineItemIdentifierRepository.buildLineItemIdentifier(cart, shoppingItem))
				.withStatus(status)
				.build();
	}

	@Override
	public Completable update(final LineItemEntity lineItemEntity, final LineItemIdentifier identifier) {
		String lineItemId = identifier.getLineItemId().getValue();
		return addToCartAdvisorService.validateLineItemEntity(lineItemEntity)
				.andThen(performUpdate(lineItemEntity, lineItemId, identifier));
	}

	/**
	 * Validate if the item is purchasable.
	 *
	 * @param scope      scope
	 * @param identifier the identifier.
	 * @return ResourceOperationFailure if item is not purchasable
	 */
	protected Completable isItemPurchasable(final String scope,  final LineItemIdentifier identifier) {
		return shoppingCartRepository.getProductSku(identifier.getLineItems().getCart().getCartId().getValue(), identifier.getLineItemId().getValue())
				.flatMapCompletable(productSku -> isItemPurchasable(scope, identifier.getLineItems().getCart().getCartId().getValue(),  productSku));
	}

	/**
	 * Validate if the item is purchasable.
	 *
	 *
	 * @param scope      scope
	 * @param cartId the cartId.
	 * @param productSku product sku
	 * @return ResourceOperationFailure if item is not purchasable
	 */
	protected Completable isItemPurchasable(final String scope, final String cartId, final ProductSku productSku) {
		return addToCartAdvisorService.validateItemPurchasable(scope, cartId, productSku, null)
				.flatMapCompletable(this::getStateFailure);
	}

	/**
	 * Update cart line item.
	 *
	 * @param lineItemEntity lineItemEntity
	 * @param lineItemId     lineItemId
	 * @param lineItemIdentifier the identifier.
	 * @return Completable
	 */
	protected Completable performUpdate(final LineItemEntity lineItemEntity, final String lineItemId, final LineItemIdentifier lineItemIdentifier) {
		return getShoppingCartForItemIdentifier(lineItemIdentifier)
				.flatMapCompletable(cart -> shoppingCartRepository.getShoppingItem(lineItemId, cart)
						.flatMapCompletable(shoppingItem -> productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(shoppingItem.getSkuGuid())
								.flatMapCompletable(productSku -> performUpdate(lineItemEntity, cart, shoppingItem, productSku.getSkuCode()))));
	}

	/**
	 * Update cart line item.
	 *
	 * @param lineItemEntity lineItemEntity
	 * @param cart           cart
	 * @param shoppingItem   shoppingItem
	 * @param skuCode        skuCode
	 * @return Completable
	 */
	protected Completable performUpdate(final LineItemEntity lineItemEntity, final ShoppingCart cart,
										final ShoppingItem shoppingItem, final String skuCode) {
		if (lineItemEntity.getQuantity() == 0) {
			return shoppingCartRepository.removeItemFromCart(cart, shoppingItem.getGuid());
		} else {
			return updateItemInCart(lineItemEntity, cart, shoppingItem, skuCode);
		}
	}

	/**
	 * Update cart line item.
	 *
	 * @param updatedLineItemEntity updatedLineItemEntity
	 * @param shoppingCart          shoppingCart
	 * @param shoppingItem          shoppingItem
	 * @param skuCode               skuCode
	 * @return Completable
	 */
	protected Completable updateItemInCart(final LineItemEntity updatedLineItemEntity, final ShoppingCart shoppingCart,
										   final ShoppingItem shoppingItem, final String skuCode) {
		Map<String, String> fields = Optional.ofNullable(updatedLineItemEntity.getConfiguration())
				.map(LineItemConfigurationEntity::getDynamicProperties)
				.orElse(Collections.emptyMap());

		shoppingItem.mergeFieldValues(fields);

		return shoppingCartRepository.updateCartItem(shoppingCart, shoppingItem, skuCode, updatedLineItemEntity.getQuantity());
	}

	/**
	 * Get the resource operation failure.
	 *
	 * @param message the message
	 * @return the resource operation failure
	 */
	protected Completable getStateFailure(final Message message) {
		return Completable.error(ResourceOperationFailure.stateFailure("Item is not purchasable",
				Collections.singletonList(message)));
	}

	@Override
	public Completable delete(final LineItemIdentifier identifier) {
		String lineItemId = identifier.getLineItemId().getValue();
		return getShoppingCartForItemIdentifier(identifier)
				.flatMapCompletable(cart -> shoppingCartRepository.removeItemFromCart(cart, lineItemId));
	}

	private Single<ShoppingCart> getShoppingCartForItemIdentifier(final LineItemIdentifier identifier) {

		return shoppingCartRepository.getShoppingCart(identifier.getLineItems().getCart().getCartId().getValue());
	}

	private Single<ShoppingCart> getShoppingCartForItemEntity(final LineItemEntity lineItemEntity) {
		return Default.URI_PART.equals(lineItemEntity.getCartId()) || null == lineItemEntity.getCartId()
				? shoppingCartRepository.getDefaultShoppingCart()
				: shoppingCartRepository.getShoppingCart(lineItemEntity.getCartId());
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setProductSkuRepository(final ProductSkuRepository productSkuRepository) {
		this.productSkuRepository = productSkuRepository;
	}

	@Reference
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Reference
	public void setCartItemModifiersRepository(final CartItemModifiersRepository cartItemModifiersRepository) {
		this.cartItemModifiersRepository = cartItemModifiersRepository;
	}

	@Reference
	public void setAddToCartAdvisorService(final AddToCartAdvisorService addToCartAdvisorService) {
		this.addToCartAdvisorService = addToCartAdvisorService;
	}

	@Reference
	public void setLineItemIdentifierRepository(final LineItemIdentifierRepository lineItemIdentifierRepository) {
		this.lineItemIdentifierRepository = lineItemIdentifierRepository;
	}

}
