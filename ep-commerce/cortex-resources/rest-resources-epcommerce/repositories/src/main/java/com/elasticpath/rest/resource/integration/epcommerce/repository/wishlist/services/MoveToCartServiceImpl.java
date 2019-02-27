/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.services;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import io.reactivex.Completable;
import io.reactivex.Single;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.AddToCartAdvisorService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.ItemValidationService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.MoveToCartService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Move to cart service.
 */
@Component
public class MoveToCartServiceImpl implements MoveToCartService {

	private WishlistRepository wishlistRepository;
	private ShoppingCartRepository shoppingCartRepository;
	private ItemValidationService itemValidationService;
	private AddToCartAdvisorService addToCartAdvisorService;
	private static final Logger LOG = LoggerFactory.getLogger(MoveToCartServiceImpl.class);


	@Override
	public Single<SubmitResult<LineItemIdentifier>> move(
			final WishlistLineItemIdentifier wishlistLineItemIdentifier, final LineItemEntity lineItemEntity) {

		WishlistIdentifier wishlistIdentifier = wishlistLineItemIdentifier.getWishlistLineItems().getWishlist();
		LOG.trace("Moving item {} from favorites {} to cart", lineItemEntity, wishlistIdentifier);

		return addToCartAdvisorService.validateLineItemEntity(lineItemEntity)
				.andThen(isItemPurchasable(wishlistLineItemIdentifier))
				.andThen(moveItemToCart(wishlistLineItemIdentifier, lineItemEntity, wishlistIdentifier));
	}

	private Completable isItemPurchasable(final WishlistLineItemIdentifier wishlistLineItemIdentifier) {
		return itemValidationService.isItemPurchasable(wishlistLineItemIdentifier)
				.flatMapCompletable(this::getStateFailure);
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

	/**
	 * Move item to cart.
	 * 
	 * @param wishlistLineItemIdentifier favorites line item identifier
	 * @param lineItemEntity line item entity
	 * @param wishlistIdentifier favorites identifier
	 * @return submit result of line item identifier
	 */
	protected Single<SubmitResult<LineItemIdentifier>> moveItemToCart(
			final WishlistLineItemIdentifier wishlistLineItemIdentifier,
			final LineItemEntity lineItemEntity,
			final WishlistIdentifier wishlistIdentifier) {

		String lineItemGuid = wishlistLineItemIdentifier.getLineItemId().getValue();
		String wishlistId = wishlistIdentifier.getWishlistId().getValue();

		return wishlistRepository.getWishlist(wishlistId)
				.flatMap(wishList -> wishlistRepository.getProductSku(wishList, lineItemGuid)
						.map(ProductSku::getSkuCode)
						.flatMap(skuCode -> shoppingCartRepository.getDefaultShoppingCart()
								.flatMap(cart -> moveItemToCart(cart, lineItemGuid, skuCode, lineItemEntity))));
	}

	/**
	 * Move item to cart.
	 * 
	 * @param shoppingCart shopping cart
	 * @param lineItemGuid line item guid
	 * @param skuCode sku code
	 * @param lineItemEntity line item entity
	 * @return submit result of line item identifier
	 */
	protected Single<SubmitResult<LineItemIdentifier>> moveItemToCart(
			final ShoppingCart shoppingCart, final String lineItemGuid, final String skuCode, final LineItemEntity lineItemEntity) {

		return shoppingCartRepository.moveItemToCart(shoppingCart, lineItemGuid, skuCode, lineItemEntity.getQuantity(),
				getConfigurableFields(lineItemEntity))
				.flatMap(shoppingItem -> buildLineItemIdentifier(shoppingCart, shoppingItem, lineItemEntity));
	}

	/**
	 * Get configurable fields for a line item.
	 * 
	 * @param lineItemEntity line item entity
	 * @return configurable fields
	 */
	protected Map<String, String> getConfigurableFields(final LineItemEntity lineItemEntity) {
		return Optional.ofNullable(lineItemEntity.getConfiguration())
				.map(LineItemConfigurationEntity::getDynamicProperties)
				.orElse(Collections.emptyMap());
	}

	/**
	 * Build line item identifier.
	 *
	 * @param cart         the cart
	 * @param shoppingItem the shopping item
	 * @param lineItemEntity line item entity
	 * @return submit result of line item identifier
	 */
	protected Single<SubmitResult<LineItemIdentifier>> buildLineItemIdentifier(
			final ShoppingCart cart, final ShoppingItem shoppingItem, final LineItemEntity lineItemEntity) {

		LineItemIdentifier identifier = LineItemIdentifier.builder()
				.withLineItemId(StringIdentifier.of(shoppingItem.getGuid()))
				.withLineItems(buildLineItemsIdentifier(cart))
				.build();

		boolean cartItemCreated = shoppingItem.getQuantity() == lineItemEntity.getQuantity();

		return Single.just(SubmitResult.<LineItemIdentifier>builder()
				.withIdentifier(identifier)
				.withStatus(cartItemCreated
						? SubmitStatus.CREATED
						: SubmitStatus.UPDATED)
				.build());
	}

	private LineItemsIdentifier buildLineItemsIdentifier(final ShoppingCart cart) {
		return LineItemsIdentifier.builder()
				.withCart(buildCartIdentifier(cart))
				.build();
	}

	private CartIdentifier buildCartIdentifier(final ShoppingCart cart) {
		return CartIdentifier.builder()
				.withCartId(StringIdentifier.of(cart.getGuid()))
				.withScope(StringIdentifier.of(cart.getStore().getCode()))
				.build();
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setItemValidationService(final ItemValidationService itemValidationService) {
		this.itemValidationService = itemValidationService;
	}

	@Reference
	public void setAddToCartAdvisorService(final AddToCartAdvisorService addToCartAdvisorService) {
		this.addToCartAdvisorService = addToCartAdvisorService;
	}
}
