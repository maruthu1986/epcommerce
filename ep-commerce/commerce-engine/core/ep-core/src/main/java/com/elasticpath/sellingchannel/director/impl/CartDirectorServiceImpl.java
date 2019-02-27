/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.sellingchannel.director.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.CartDirectorService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;

/**
 * Service which wraps CartDirector's shopping cart update functionality within Container managed
 * transactional boundaries.
 * <p>
 * This allows outside callers like Controllers and Cortex to safely access CartDirector functionality from
 * within an over-arching transaction.
 */
public class CartDirectorServiceImpl implements CartDirectorService {
	private CartDirector cartDirector;
	private WishListService wishListService;
	private ShoppingCartService shoppingCartService;
	private PricingSnapshotService pricingSnapshotService;

	@Override
	public ShoppingItem addItemToCart(final ShoppingCart shoppingCart, final ShoppingItemDto dto) {
		final ShoppingItem shoppingItem = getCartDirector().addItemToCart(shoppingCart, dto);

		final ShoppingCart persistedShoppingCart = saveShoppingCart(shoppingCart);

		return persistedShoppingCart.getCartItemByGuid(shoppingItem.getGuid());
	}

	@Override
	public ShoppingCart updateCartItem(final ShoppingCart shoppingCart, final long itemId, final ShoppingItemDto dto) {
		getCartDirector().updateCartItem(shoppingCart, itemId, dto);

		return saveShoppingCart(shoppingCart);
	}

	@Override
	public ShoppingCart updateCompoundCartItem(
			final ShoppingCart shoppingCart, final long rootItemId, final ShoppingItemDto rootItemDto,
			final List<ShoppingItemDto> dependentItemDtos, final List<ShoppingItemDto> associatedItemDtos) {
		ShoppingItem parentItem;
		if (isUpdate(rootItemId)) {
			parentItem = cartDirector.updateCartItem(shoppingCart, rootItemId, rootItemDto);

			// remove existing dependents, we will add back newly selected dependents below
			final List<ShoppingItem> dependents = parentItem.getChildren().stream()
					.filter(shoppingItem -> !shoppingItem.isBundleConstituent())
					.collect(Collectors.toList());

			parentItem.getChildren().removeAll(dependents);

			final Collection<String> dependentGuids = dependents.stream()
					.map(ShoppingItem::getGuid)
					.collect(Collectors.toSet());

			shoppingCart.removeCartItems(dependentGuids);
		} else {
			parentItem = cartDirector.addItemToCart(shoppingCart, rootItemDto, null);
		}
		for (ShoppingItemDto dependentItemDto : dependentItemDtos) {
			cartDirector.addItemToCart(shoppingCart, dependentItemDto, parentItem);
		}
		for (ShoppingItemDto associatedItemDto : associatedItemDtos) {
			cartDirector.addItemToCart(shoppingCart, associatedItemDto, null);
		}

		return saveShoppingCart(shoppingCart);
	}

	private boolean isUpdate(final long rootItemId) {
		return rootItemId != 0;
	}

	@Override
	public ShoppingCart removeItemsFromCart(final ShoppingCart shoppingCart, final String... doomedItemGuids) {
		for (final String doomedItemGuid : doomedItemGuids) {
			shoppingCart.removeCartItem(doomedItemGuid);
		}

		return saveShoppingCart(shoppingCart);
	}

	@Override
	public ShoppingCart refresh(final ShoppingCart shoppingCart) {
		getCartDirector().refresh(shoppingCart);

		return saveShoppingCart(shoppingCart);
	}

	@Override
	public void reApplyCatalogPromotions(final ShoppingCart shoppingCart) {
		getCartDirector().refresh(shoppingCart);
	}

	@Override
	public ShoppingItem addSkuToWishList(final String skuCode, final Shopper shopper, final Store store) {
		ShoppingItem item = getCartDirector().createShoppingItem(skuCode, store, 1);

		WishList wishList = getWishListService().findOrCreateWishListByShopper(shopper);
		final AddToWishlistResult result = getWishListService().addItem(wishList, item);
		final WishList persistedWishList = getWishListService().save(wishList);

		return persistedWishList.getAllItems().stream()
				.filter(shoppingItem -> shoppingItem.getGuid().equals(result.getShoppingItem().getGuid()))
				.findAny()
				.orElse(null);
	}

	@Override
	public ShoppingItem moveItemFromWishListToCart(final ShoppingCart shoppingCart, final ShoppingItemDto dto, final String wishlistLineItemGuid) {
		WishList wishList = getWishListService().findOrCreateWishListByShopper(shoppingCart.getShopper());
		wishList.removeItem(wishlistLineItemGuid);
		getWishListService().save(wishList);

		ShoppingItem shoppingItem = getCartDirector().addItemToCart(shoppingCart, dto);

		if (dto.getQuantity() > 1) {
			shoppingCart.setItemWithNoTierOneFromWishList(true);
		}

		final ShoppingCart persistedShoppingCart = saveShoppingCart(shoppingCart);

		return persistedShoppingCart.getCartItemByGuid(shoppingItem.getGuid());
	}

	@Override
	public AddToWishlistResult moveItemFromCartToWishList(final ShoppingCart shoppingCart, final String cartLineItemGuid) {

		ShoppingItem cartItem = shoppingCart.getShoppingItemByGuid(cartLineItemGuid);

		WishList wishList = getWishListService().findOrCreateWishListByShopper(shoppingCart.getShopper());
		AddToWishlistResult addToWishlistResult = wishListService.addItem(wishList, cartItem);
		getWishListService().save(wishList);

		removeItemsFromCart(shoppingCart, cartItem.getGuid());

		return addToWishlistResult;
	}

	@Override
	public ShoppingCart clearItems(final ShoppingCart shoppingCart) {
		getCartDirector().clearItems(shoppingCart);

		return saveShoppingCart(shoppingCart);
	}

	/**
	 * Persists {@code shoppingCart} in the database.
	 *
	 * @param shoppingCart The shopping cart to save.
	 * @return The now updated shopping cart.
	 */
	protected ShoppingCart saveShoppingCart(final ShoppingCart shoppingCart) {
		getCartDirector().reorderItems(shoppingCart);
		getPricingSnapshotService().getPricingSnapshotForCart(shoppingCart);
		return getShoppingCartService().saveOrUpdate(shoppingCart);
	}

	protected CartDirector getCartDirector() {
		return cartDirector;
	}

	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	protected WishListService getWishListService() {
		return wishListService;
	}

	public void setWishListService(final WishListService wishListService) {
		this.wishListService = wishListService;
	}

	protected ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	protected PricingSnapshotService getPricingSnapshotService() {
		return pricingSnapshotService;
	}

	public void setPricingSnapshotService(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}
}
