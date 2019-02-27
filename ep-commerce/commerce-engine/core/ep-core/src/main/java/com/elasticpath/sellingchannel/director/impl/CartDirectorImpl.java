/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.sellingchannel.director.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.tree.impl.PreOrderTreeTraverser;
import com.elasticpath.commons.tree.impl.ProductPriceMemento;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shoppingcart.validation.AddOrUpdateShoppingItemDtoToCartValidationService;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;

/**
 * Business domain delegate of the functionality required to add a cart item to a shopping cart. This object is delegated to from the CartDirector.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public class CartDirectorImpl implements CartDirector {

	private static final Logger LOG = Logger.getLogger(CartDirectorImpl.class);

	private ProductSkuLookup productSkuLookup;

	private PriceLookupFacade priceLookupFacade;

	private ShoppingItemAssembler shoppingItemAssembler;

	private ShoppingItemDtoFactory shoppingItemDtoFactory;

	private TimeService timeService;

	private ProductAssociationService productAssociationService;

	private AddOrUpdateShoppingItemDtoToCartValidationService validationService;

	/**
	 * @param shoppingItem The shoppingItem to add.
	 * @param shoppingCart The cart to add the items to.
	 * @param parentItem   is the item dependent
	 * @return The cart item that was added. Null if it could not be found.
	 */
	protected ShoppingItem addToCart(final ShoppingItem shoppingItem, final ShoppingCart shoppingCart, final ShoppingItem parentItem) {
		ShoppingItem cartItemToAdd = shoppingItem;
		int quantity = cartItemToAdd.getQuantity();

		final ShoppingItem existingItem = getExistingItemWithSameParent(shoppingCart, shoppingItem, parentItem);

		if (existingItem == null || !itemsAreEqual(shoppingItem, existingItem)) {
			priceShoppingItemWithAdjustments(shoppingCart, cartItemToAdd);

			if (parentItem == null) {
				cartItemToAdd = shoppingCart.addShoppingCartItem(cartItemToAdd);
				cartItemToAdd.setOrdering(shoppingCart.getRootShoppingItems().size());
			} else {
				parentItem.addChildItem(cartItemToAdd);
			}

			addDependentItemsForParentItem(shoppingCart, cartItemToAdd);

			// fire rules & persist here?
		} else if (parentItem == null) { // non-dependent item
			quantity = quantity + existingItem.getQuantity();
			cartItemToAdd = changeQuantityForCartItem(existingItem, quantity, shoppingCart);

			// fire rules & persist here?
		} else {
			// else do nothing when existing dependent item found
			LOG.debug("dependent sku already in cart: " + cartItemToAdd.getSkuGuid());
		}

		return cartItemToAdd;
	}

	/**
	 * Fetches and adds the dependent items per the parent cart item added.
	 *
	 * @param shoppingCart    the shopping cart
	 * @param parentItemAdded the added parent item
	 */
	protected void addDependentItemsForParentItem(final ShoppingCart shoppingCart, final ShoppingItem parentItemAdded) {
		final ProductSku parentProductSku = getProductSkuLookup().findByGuid(parentItemAdded.getSkuGuid());
		getProductAssociationService().findDependentItemsForSku(shoppingCart.getStore(), parentProductSku).stream()
			.map(sku -> getShoppingItemDtoFactory().createDto(sku.getSkuCode(), sku.getProduct().getMinOrderQty()))
			.forEach(dependentShoppingItem -> addItemToCart(shoppingCart, dependentShoppingItem, parentItemAdded));
	}

	private ShoppingItem createShoppingItem(final ShoppingItemDto shoppingItemDto) {
		return getShoppingItemAssembler().createShoppingItem(shoppingItemDto);
	}

	/**
	 * Applies the appropriate prices to the given {@code ShoppingItem}. This implementation uses a {@link PricingFunctor} created with this
	 * instance's {@link PriceLookupFacade}, along with a {@code PreOrderTreeTraverser} to walk the {@code ShoppingItem} tree and set the prices.
	 * @param shoppingItem the root shopping item upon which the price should be set
	 *
	 * @param store the store in which the shoppingItem is being used
	 * @param shopper {@code CustomerSession}
	 */
	protected void priceShoppingItem(final ShoppingItem shoppingItem, final Store store, final Shopper shopper) {

		final PreOrderTreeTraverser<ShoppingItem, ProductPriceMemento> pricingTraverser =
			new PreOrderTreeTraverser<>();
		final PricingFunctor functor = new PricingFunctor(priceLookupFacade, getProductSkuLookup(), store, shopper);
		if (shoppingItem.isBundle(getProductSkuLookup())) {
			pricingTraverser.traverseTree(shoppingItem, null, null, functor, 0);
		} else {
			functor.processNode(shoppingItem, null, null, 0);
		}
	}

	/**
	 * Reorders and renumbers the shopping cart items in the given shopping cart.
	 * After completion, should be in order, and the order numbers should be
	 * monotonically increasing.
	 *
	 * @param shoppingCart the shopping cart to reorder
	 */
	@Override
	public void reorderItems(final ShoppingCart shoppingCart) {
		int order = 0;

		final List<ShoppingItem> shoppingItems = shoppingCart.getRootShoppingItems();

		for (final ShoppingItem item : shoppingItems) {
			item.setOrdering(++order);
		}
	}

	/**
	 * Changes the quantity of {@code shoppingItem} to be {@code quantity}. Note that this method will ensure that a new price is looked up and
	 * promotions calculated for the new quantity. This implementation creates a brand new shoppingItem from the given shoppingItem.
	 *
	 * @param shoppingItem The shoppingItem to update.
	 * @param quantity The quantity to set.
	 * @param shoppingCart the shoppingCart within which the shoppingItem is contained
	 * @return the ShoppingItem that replaces the given ShoppingItem
	 */
	public ShoppingItem changeQuantityForCartItem(final ShoppingItem shoppingItem, final int quantity, final ShoppingCart shoppingCart) {
		final ShoppingItemDto dto = getShoppingItemAssembler().assembleShoppingItemDtoFrom(shoppingItem);
		dto.setQuantity(quantity);
		return updateCartItem(shoppingCart, shoppingItem.getUidPk(), dto);
	}

	@Override
	public ShoppingItem addItemToCart(final ShoppingCart shoppingCart, final ShoppingItemDto dto) {
		return addItemToCart(shoppingCart, dto, null);
	}

	@Override
	public ShoppingItem addItemToCart(final ShoppingCart shoppingCart, final ShoppingItemDto dto, final ShoppingItem parentItem) {
		validateShoppingItemDto(shoppingCart, dto, parentItem, false);
		final ShoppingItem shoppingItem = createShoppingItem(dto);
		return addToCart(shoppingItem, shoppingCart, parentItem);
	}

	/**
	 * Updates a {@code ShoppingItem} having the given itemId with the data from the given {@code ShoppingItemDto}. This implementation creates a new
	 * ShoppingItem from the DTO, adds it to the shopping cart, and deletes the old one.
	 * Calls {@link #addToCart(ShoppingItem, ShoppingCart, ShoppingItem)}.
	 *
	 * @param shoppingCart {@code ShoppingCart}
	 * @param itemId id of cart item for update
	 * @param dto of new item
	 * @return the ShoppingItem that's replacing the one with the given id
	 */
	@Override
	public ShoppingItem updateCartItem(final ShoppingCart shoppingCart, final long itemId, final ShoppingItemDto dto) {
		validateShoppingItemDto(shoppingCart, dto, null, true);

		// find the ShoppingItem for this id
		final ShoppingItem item = shoppingCart.getCartItemById(itemId);

		// if this is a dependent item, get its parent
		final Optional<ShoppingItem> parentItem = getParent(shoppingCart.getAllShoppingItems(), item);

		// delete/re-create (delete's all it's dependents!) to "update" existing item from dto
		shoppingCart.removeCartItem(itemId);
		final int ordering = item.getOrdering();
		final ShoppingItem newShoppingItem = getShoppingItemAssembler().createShoppingItem(dto);
		retainShoppingItemIdentity(item, newShoppingItem);

		// re-connect with the parent item, if this item is dependent
		parentItem.ifPresent(shoppingItem -> shoppingItem.addChildItem(newShoppingItem));

		// add the new updated item back to the cart
		final ShoppingItem updatedItem = addToCart(newShoppingItem, shoppingCart, parentItem.orElse(null));
		if (updatedItem != null) {
			updatedItem.setOrdering(ordering);

			item.getChildren().stream()
					.filter(shoppingItem -> !shoppingItem.isBundleConstituent())
					.forEach(dependent -> {
						// put back dependent items with the new item quantity
						final ShoppingItemDto dependentDto = getShoppingItemAssembler().assembleShoppingItemDtoFrom(dependent);
						dependentDto.setQuantity(updatedItem.getQuantity());

						final ShoppingItem newDependentItem = getShoppingItemAssembler().createShoppingItem(dependentDto);
						addToCart(newDependentItem, shoppingCart, updatedItem);
					});
		}
		return updatedItem;
	}

	@Override
	public ShoppingItem createShoppingItem(final String skuCode, final Store store, final int quantity) {
		if (skuCode == null) {
			throw new IllegalArgumentException("Sku code cannot be null");
		}

		final ProductSku selectedProductSku = getProductSkuLookup().findBySkuCode(skuCode);
		if (selectedProductSku == null) { // not found.
			throw new EpServiceException("ProductSku with the specified sku code [" + skuCode + "] does not exist");
		}

		final ShoppingItemDto dto = getShoppingItemDtoFactory().createDto(skuCode, quantity);
		return createShoppingItem(dto);
	}

	/**
	 * Makes sure the new shopping item can retain the identity of the existing shopping item.
	 *
	 * When an item is being updated in the cart, a new shopping item will be created, and the new shopping item will
	 * substitute the existing one. This method can decide to either keep the old shopping item's identity (e.g. GUID),
	 * or use a new one.

	 * @param existingShoppingItem the old shopping item
	 * @param newShoppingItem the new shopping item
	 */
	protected void retainShoppingItemIdentity(final ShoppingItem existingShoppingItem, final ShoppingItem newShoppingItem) {
		newShoppingItem.setGuid(existingShoppingItem.getGuid());
	}

	@Override
	public Optional<ShoppingItem> getParent(final Collection<ShoppingItem> cartItems, final ShoppingItem child) {
		return cartItems.stream()
				.filter(shoppingItem -> shoppingItem.getChildren().contains(child))
				.findAny();
	}

	/**
	 * Gets the existing item that shares the same parent.
	 * @param shoppingCart The {@link ShoppingCart}
	 * @param shoppingItem The {@link ShoppingItem}
	 * @param parentItem The parent {@link ShoppingItem}
	 * @return The item that has the same parent
	 */
	protected ShoppingItem getExistingItemWithSameParent(final ShoppingCart shoppingCart, final ShoppingItem shoppingItem,
			final ShoppingItem parentItem) {
		final Collection<ShoppingItem> cartItems = shoppingCart.getAllShoppingItems();
		final List<ShoppingItem> itemsBySkuGuid = shoppingCart.getCartItemsBySkuGuid(shoppingItem.getSkuGuid());

		// Matching items must have matching field values
		final Map<String, String> fields = shoppingItem.getFields();
		final List<ShoppingItem> existingItems = itemsBySkuGuid.stream()
			.filter(existingItem -> fields.equals(existingItem.getFields()))
			.collect(Collectors.toList());

		for (final ShoppingItem existingItem : existingItems) {
			if (shoppingItem.equals(existingItem)) {
				continue;
			}

			final Optional<ShoppingItem> existingParentItem = getParent(cartItems, existingItem);

			if (existingParentItem.isPresent() && existingParentItem.get().equals(parentItem)) {
				return existingItem;
			}
			if (!existingParentItem.isPresent() && parentItem == null) {
				return existingItem;
			}
		}

		return null;
	}

	/**
	 * Check if the specified child is dependent on an element in the list.
	 *
	 * @param cartItems list to check for the parent
	 * @param child the child
	 * @return true if there is a parent in the list
	 */
	@Override
	public boolean isDependent(final List<ShoppingItem> cartItems, final ShoppingItem child) {
		return getParent(cartItems, child).isPresent();
	}

	@Override
	public void refresh(final ShoppingCart shoppingCart) throws EpServiceException {
		refreshShoppingItems(shoppingCart.getShoppingItems(shoppingItem -> !shoppingItem.isBundleConstituent()), shoppingCart);
	}

	/**
	 * Refreshed pricing information for shopping items.
	 * @param shoppingCart cart
	 * @param items shopping items
	 */
	protected void refreshShoppingItems(final Collection<ShoppingItem> items, final ShoppingCart shoppingCart) {
		final List<ShoppingItem> nullPricedItems = new ArrayList<>();
		for (final ShoppingItem item : items) {
			try {
				priceShoppingItemWithAdjustments(shoppingCart, item);
			} catch (final ProductUnavailableException exception) {
				nullPricedItems.add(item);
			}
		}
		// we must remove null priced items from the cart
		for (final ShoppingItem item : nullPricedItems) {
			LOG.warn("Sku has no price, removing from cart: " + item.getSkuGuid());
			items.remove(item);
		}
	}

	@Override
	public void clearItems(final ShoppingCart shoppingCart) {
		shoppingCart.clearItems();
	}

	/**
	 * This method, in sequence, prices the shopping item, applies bundle adjustments, if applicable, and finally promotes the prices.
	 *
	 * @param shoppingCart the cart
	 * @param item the item to price and adjust
	 */
	public void priceShoppingItemWithAdjustments(final ShoppingCart shoppingCart, final ShoppingItem item) {
		final Shopper shopper = shoppingCart.getShopper();
		priceShoppingItem(item, shoppingCart.getStore(), shopper);

		if (item.isBundle(getProductSkuLookup())) {
			final Price adjustedPrice = priceLookupFacade.getShoppingItemPrice(item, shoppingCart.getStore(),
					shopper);
			item.setPrice(item.getQuantity(), adjustedPrice);
		}
	}

	private void validateShoppingItemDto(final ShoppingCart shoppingCart, final ShoppingItemDto shoppingItemDto,
										 final ShoppingItem parentShoppingItem, final boolean isUpdate) {

		final ShoppingItemDtoValidationContext context = validationService.buildContext(shoppingCart, shoppingItemDto, parentShoppingItem, isUpdate);

		Collection<StructuredErrorMessage> commerceMessageList = validationService.validate(context);

		if (!commerceMessageList.isEmpty()) {
			throw new EpValidationException("CartItem validation failure.", ImmutableList.copyOf(commerceMessageList));
		}
	}

	/**
	 * Sets the productSkuService.
	 *
	 * @param productSkuLookup The ProductSkuService.
	 */
	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	/**
	 * @param priceLookupFacade The price lookup facade to set.
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	/**
	 * @param shoppingItemAssembler The assembler to set.
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * @return the shoppingItemAssembler
	 */
	ShoppingItemAssembler getShoppingItemAssembler() {
		return shoppingItemAssembler;
	}

	/**
	 * @param shoppingItemDtoFactory The factory to set.
	 */
	public void setShoppingItemDtoFactory(final ShoppingItemDtoFactory shoppingItemDtoFactory) {
		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
	}

	/**
	 * @return the shoppingItemDtoFactory
	 */
	ShoppingItemDtoFactory getShoppingItemDtoFactory() {
		return shoppingItemDtoFactory;
	}

	/**
	 * Get the time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Set the time service.
	 *
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	public AddOrUpdateShoppingItemDtoToCartValidationService getValidationService() {
		return validationService;
	}

	public void setValidationService(final AddOrUpdateShoppingItemDtoToCartValidationService validationService) {
		this.validationService = validationService;
	}

	@Override
	public boolean itemsAreEqual(final ShoppingItem shoppingItem, final ShoppingItem existingItem) {
		if (existingItem.isMultiSku(productSkuLookup)) {
			return shoppingItem.isSameMultiSkuItem(productSkuLookup, existingItem);
		} else if (shoppingItem.isConfigurable(productSkuLookup)) {
			return shoppingItem.isSameConfigurableItem(productSkuLookup, existingItem);
		}
		return Objects.equals(shoppingItem.getSkuGuid(), existingItem.getSkuGuid());
	}

	public void setProductAssociationService(final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}

	public ProductAssociationService getProductAssociationService() {
		return productAssociationService;
	}
}
