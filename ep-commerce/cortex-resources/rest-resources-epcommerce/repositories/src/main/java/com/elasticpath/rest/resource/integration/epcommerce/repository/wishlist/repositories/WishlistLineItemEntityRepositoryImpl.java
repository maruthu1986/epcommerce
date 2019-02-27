/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.LineItemConfigurationEntity;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemEntity;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Repository for Wishlist Line Item Entities.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class WishlistLineItemEntityRepositoryImpl<E extends WishlistLineItemEntity, I extends WishlistLineItemIdentifier>
		implements Repository<WishlistLineItemEntity, WishlistLineItemIdentifier> {

	private static final Logger LOG = LoggerFactory.getLogger(WishlistLineItemEntityRepositoryImpl.class);

	private WishlistRepository wishlistRepository;
	private ItemRepository itemRepository;
	private CartItemModifiersRepository cartItemModifiersRepository;
	private ReactiveAdapter reactiveAdapter;

	@Override
	public Single<SubmitResult<WishlistLineItemIdentifier>> submit(final WishlistLineItemEntity entity, final IdentifierPart<String>
			scope) {
		LOG.trace("Adding item id {} to favorites {}", entity.getItemId(), entity.getWishlistId());

		String sku = entity.getItemId();

		return wishlistRepository.getDefaultWishlistId(scope.getValue())
				.flatMap(wishlistId -> wishlistRepository.addItemToWishlist(wishlistId, scope.getValue(), sku)
						.flatMap(addToWishlistResult -> wishlistRepository.buildSubmitResult(scope.getValue(), wishlistId,
								addToWishlistResult)));
	}

	@Override
	public Single<WishlistLineItemEntity> findOne(final WishlistLineItemIdentifier identifier) {
		WishlistIdentifier wishlistIdentifier = identifier.getWishlistLineItems().getWishlist();
		String wishlistId = wishlistIdentifier.getWishlistId().getValue();
		String lineItemGuid = identifier.getLineItemId().getValue();

		return wishlistRepository.getWishlist(wishlistId)
				.flatMap(toWishlistLineItemEntity(lineItemGuid));
	}

	/**
	 * Get the favorites line item entity.
	 *
	 * @param lineItemGuid the line item guid
	 * @return function
	 */
	protected Function<WishList, Single<WishlistLineItemEntity>> toWishlistLineItemEntity(final String lineItemGuid) {
		LOG.info("WishlistEntityRepositoryImpl.toWishlistLineItemEntity()");
		return wishList -> wishlistRepository.getShoppingItem(wishList, lineItemGuid)
				.flatMap(shoppingItem -> wishlistRepository.getProductSku(wishList, lineItemGuid)
						.flatMap(productSku -> itemRepository.getItemIdForSkuAsSingle(productSku)
								.flatMap(itemId -> buildWishlistLineItemEntity(shoppingItem, productSku, itemId, lineItemGuid, wishList))));
	}

	/**
	 * Build the favorites line item entity.
	 *
	 * @param shoppingItem the shopping item
	 * @param productSku   the product sku
	 * @param itemId       item id
	 * @param lineItemGuid line item guid
	 * @param wishList     favorites
	 * @return favorites line item entity
	 */
	protected Single<WishlistLineItemEntity> buildWishlistLineItemEntity(final ShoppingItem shoppingItem, final ProductSku
			productSku, final String itemId, final String lineItemGuid, final WishList wishList) {
		LOG.info("WishlistEntityRepositoryImpl.buildWishlistLineItemEntity() :: "+wishList);
		Map<String, String> fields = shoppingItem.getFields();
		Single<LineItemConfigurationEntity> lineItemConfigurationEntity;
		if (fields == null) {
			lineItemConfigurationEntity = Single.just(LineItemConfigurationEntity.builder().build());
		} else {
			lineItemConfigurationEntity = reactiveAdapter
					.fromServiceAsSingle(() -> cartItemModifiersRepository.findCartItemModifiersByProduct(productSku.getProduct()))
					.map(cartItemModifierFields -> buildLineItemConfigurationEntity(fields, cartItemModifierFields));
		}
		return lineItemConfigurationEntity.map(configuration -> WishlistLineItemEntity.builder()
				.withItemId(itemId)
				.withLineItemId(lineItemGuid)
				.withWishlistId(wishList.getGuid())
				.withConfiguration(configuration)
				.build());
	}

	/**
	 * Build the line item configuration entity.
	 *
	 * @param fields                 shopping item configuration fields
	 * @param cartItemModifierFields cart item modifier fields
	 * @return line item configuration entity
	 */
	protected LineItemConfigurationEntity buildLineItemConfigurationEntity(final Map<String, String> fields,
																		   final List<CartItemModifierField> cartItemModifierFields) {
		LineItemConfigurationEntity.Builder builder = LineItemConfigurationEntity.builder();
		cartItemModifierFields.forEach(field -> builder.addingProperty(field.getCode(), fields.get(field.getCode())));
		return builder.build();
	}

	@Override
	public Completable delete(final WishlistLineItemIdentifier lineItemIdentifier) {
		WishlistIdentifier wishlistIdentifier = lineItemIdentifier.getWishlistLineItems().getWishlist();
		String wishlistId = wishlistIdentifier.getWishlistId().getValue();
		return wishlistRepository.removeItemFromWishlist(wishlistId, lineItemIdentifier.getLineItemId().getValue());
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
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
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}
