/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.builder;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirectorService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Builder for Wish Lists.
 */
public class WishListBuilder implements DomainObjectBuilder<WishList> {

	@Autowired
	private CartDirectorService cartDirectorService;

	@Autowired
	private WishListService wishListService;

	@Autowired
	private ProductSkuService productSkuService;

	@Autowired
	private TestDataPersisterFactory persisterFactory;

	private final Collection<String> skuCodes = new ArrayList<>();
	private Store store;
	private Shopper shopper;
	private SimpleStoreScenario scenario;

	/**
	 * Select the scenario to use with the builder.
	 *
	 * @param scenario the scenario
	 * @return the wish list builder
	 */
	public WishListBuilder withScenario(final SimpleStoreScenario scenario) {
		this.scenario = scenario;
		store = scenario.getStore();
		return this;
	}

	/**
	 * Select the Shopper to use when creating the Wish List.
	 *
	 * @param shopper the shopper
	 * @return the wish list builder
	 */
	public WishListBuilder withShopper(final Shopper shopper) {
		this.shopper = shopper;
		return this;
	}

	/**
	 * Add a physical product to the wish list.
	 *
	 * @return the wish list test cart builder
	 */
	public WishListBuilder withPhysicalProduct() {
		final Product physicalProduct = persisterFactory.getCatalogTestPersister().persistDefaultShippableProducts(scenario.getCatalog(),
																												   scenario.getCategory(),
																												   scenario.getWarehouse()).get(0);
		return withProductSku(physicalProduct.getDefaultSku());
	}

	/**
	 * Adds a product SKU to the Wish List, along with associated Shopping Item Fields.  The SKU will be persisted if necessary.
	 *
	 * @param productSku the product sku to add to the cart
	 * @return the wish list builder
	 */
	public WishListBuilder withProductSku(final ProductSku productSku) {
		skuCodes.add(productSku.getSkuCode());
		return this;
	}

	@Override
	public WishList build() {
		WishList wishList = wishListService.findOrCreateWishListByShopper(shopper);

		wishList.setAllItems(new ArrayList<>());
		wishListService.save(wishList);

		for (final String skuCode : skuCodes) {
			cartDirectorService.addSkuToWishList(skuCode, shopper, store);
		}

		// This looks stupid, and it is, but unfortunately CartDirectorService#addSkuToWishList will create and populate the Wish List items in a new
		// instance, persist it, but not return it (instead returning the ShoppingItem).  We have to go back to the DB in order to return the
		// populated instance.  However, the Shopper field is transient... so findOrCreateWishListByShopper will return a WishList with a
		// null Shopper.  Even though we already told the method what the value for Shopper should be.  Ugh.
		wishList = wishListService.findOrCreateWishListByShopper(shopper);
		wishList.setShopper(shopper);
		return wishList;
	}

}
