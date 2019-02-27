/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPredicateUtils;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.ShoppingCartMerger;

/**
 * Utility class for merging two shopping lists into a single list.
 */
public class ShoppingCartMergerImpl implements ShoppingCartMerger {

	private ShoppingItemAssembler shoppingItemAssembler;
	private CartDirector cartDirector;
	private ProductSkuLookup productSkuLookup;

	@Override
	public final ShoppingCart merge(final ShoppingCart recipient, final ShoppingCart donor) {
		final Collection<ShoppingItem> recipientItems = recipient.getRootShoppingItems();
		final Collection<ShoppingItem> donorItems = donor.getRootShoppingItems();

		final boolean recipientHadItems = recipient.getNumItems() > 0;
		for (Iterator<ShoppingItem> donorItemIterator = donorItems.iterator(); donorItemIterator.hasNext();) {
			ShoppingItem donorItem = donorItemIterator.next();

			Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(donorItem, getProductSkuLookup());
			ShoppingItem matchingRecipient = (ShoppingItem) CollectionUtils.find(recipientItems, matchingShoppingItemPredicate);

			if (matchingRecipient == null) {
				if (recipientHadItems) {
					recipient.setMergedNotification(true);
				}
				addDonorItemToCart(recipient, donor, donorItem);
			} else {
				if (donorItem.getQuantity() != matchingRecipient.getQuantity()) {
					ShoppingItemDto dto = shoppingItemAssembler.assembleShoppingItemDtoFrom(donorItem);
					cartDirector.updateCartItem(recipient, matchingRecipient.getUidPk(), dto);
					recipient.setMergedNotification(true);
				}
			}
		}

		mergeTransientData(recipient, donor);
		return recipient;
	}

	/**
	 * The donor item will be deleted by JPA, so we make a clone from it, and add it to the recipient cart.
	 *
	 * @param recipientCart the cart to update
	 * @param donorCart the cart providing new line items
	 * @param donorItem the item to add to the cart
	 */
	private void addDonorItemToCart(final ShoppingCart recipientCart, final ShoppingCart donorCart, final ShoppingItem donorItem) {

		ShoppingItemDto dto = shoppingItemAssembler.assembleShoppingItemDtoFrom(donorItem);

		//Determine if donor item has a parent item in the donor cart and find the matching parent in the recipient cart
		final Optional<ShoppingItem> donorItemParent = cartDirector.getParent(donorCart.getAllShoppingItems(), donorItem);
		final Predicate matchingShoppingItemPredicate =
				ShoppingItemPredicateUtils.matchingShoppingItemPredicate(donorItemParent.orElse(null), getProductSkuLookup());

		final ShoppingItem recipientItemParent = (ShoppingItem) CollectionUtils.find(recipientCart.getAllShoppingItems(),
																					 matchingShoppingItemPredicate);

		cartDirector.addItemToCart(recipientCart, dto, recipientItemParent);
	}

	/**
	 * Merge transient data such as promo codes and gift certificates.
	 *
	 * @param recipient the recipient
	 * @param donor the donor
	 */
	protected void mergeTransientData(final ShoppingCart recipient, final ShoppingCart donor) {

		recipient.applyPromotionCodes(donor.getPromotionCodes());

		for (GiftCertificate giftCertificate : donor.getAppliedGiftCertificates()) {
			recipient.applyGiftCertificate(giftCertificate);
		}

		recipient.setCmUserUID(donor.getCmUserUID());
	}

	/**
	 * @param shoppingItemAssembler The assembler to set.
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * @param cartDirector the cartDirector to set
	 */
	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}
