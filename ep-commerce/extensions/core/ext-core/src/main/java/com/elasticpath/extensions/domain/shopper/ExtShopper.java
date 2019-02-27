package com.elasticpath.extensions.domain.shopper;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.domain.shoppingcart.ShopperBrowsingActivity;

public interface ExtShopper extends Shopper, FavoriteAccessor {
    /**
     * Gets the {@link ShopperMemento} for this Shopper.
     *
     * @return the {@link ShopperMemento} for this Shopper.
     */
    ShopperMemento getShopperMemento();

    /**
     * Sets the {@link ShopperMemento} for this Shopper.
     *
     * @param shopperMomento the
     */
    void setShopperMemento(ShopperMemento shopperMomento);

    /**
     * Gets the {@link ShopperBrowsingActivity} for this Shopper.
     *
     * @return this shopper's browsing activity
     */
    ShopperBrowsingActivity getBrowsingActivity();
}
