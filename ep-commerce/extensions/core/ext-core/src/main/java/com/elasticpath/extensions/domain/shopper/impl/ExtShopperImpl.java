package com.elasticpath.extensions.domain.shopper.impl;

import com.elasticpath.commons.util.SimpleCache;
import com.elasticpath.commons.util.impl.SimpleCacheImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.domain.shoppingcart.ShopperBrowsingActivity;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.domain.shopper.ExtShopper;
import com.elasticpath.tags.TagSet;

import java.util.Currency;
import java.util.Locale;

public class ExtShopperImpl implements ExtShopper {
    /**
     * Serial version id.
     */
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000001L;

    private ShopperMemento shopperMemento;

    private CustomerSession customerSession;

    private boolean customerSignedIn;

    private ShoppingCart shoppingCart;

    private WishList wishList;

    private FavoriteList favoriteList;

    private final SimpleCache simpleCache;

    private transient ShopperBrowsingActivity browsingActivity;

    /**
     * Constructor for ShopperImpl.
     */
    public ExtShopperImpl() {
        simpleCache = new SimpleCacheImpl();
    }

    @Override
    public String getGuid() {
        return getShopperMemento().getGuid();
    }

    @Override
    public void setGuid(final String guid) {
        getShopperMemento().setGuid(guid);
    }

    @Override
    public long getUidPk() {
        return getShopperMemento().getUidPk();
    }

    @Override
    public void setUidPk(final long uidPk) {
        getShopperMemento().setUidPk(uidPk);
    }

    // implementing UpdateShopperTransientData

    @Override
    public void updateTransientDataWith(final CustomerSession customerSession) {
        this.customerSession = customerSession;

        if (shoppingCart != null) {
            shoppingCart.setCustomerSession(customerSession);
        }
    }

    // implementing CurrencyProvider

    @Override
    public Currency getCurrency() {
        return customerSession.getCurrency();
    }

    // implementing LocaleProvider

    @Override
    public Locale getLocale() {
        return customerSession.getLocale();
    }

    // implementing StoreCodeProvider
    @Override
    public String getStoreCode() {
        return getShopperMemento().getStoreCode();
    }

    @Override
    public void setStoreCode(final String storeCode) {
        getShopperMemento().setStoreCode(storeCode);
    }

    // implementing CustomerAccessor
    @Override
    public Customer getCustomer() {
        return shopperMemento.getCustomer();
    }

    @Override
    public void setCustomer(final Customer customer) {
        shopperMemento.setCustomer(customer);
    }

    @Override
    public void setSignedIn(final boolean signedIn) {
        customerSignedIn = signedIn;
    }

    @Override
    public boolean isSignedIn() {
        return customerSignedIn;
    }

    // implementing PriceListStackCache

    @Override
    public PriceListStack getPriceListStack() {
        return customerSession.getPriceListStack();
    }

    @Override
    public void setPriceListStack(final PriceListStack priceListStack) {
        customerSession.setPriceListStack(priceListStack);
    }

    @Override
    public boolean isPriceListStackValid() {
        return customerSession.isPriceListStackValid();
    }

    // implementing ShoppingCartAccessor

    @Override
    public ShoppingCart getCurrentShoppingCart() {
        return shoppingCart;
    }

    @Override
    public void setCurrentShoppingCart(final ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    // implementing WishListAccessor

    @Override
    public WishList getCurrentWishList() {
        return wishList;
    }

    @Override
    public void setCurrentWishList(final WishList wishList) {
        this.wishList = wishList;
    }

    // interface TagSource
    @Override
    public TagSet getTagSet() {
        return customerSession.getCustomerTagSet();
    }

    // implements SimpleCacheProvider

    @Override
    public SimpleCache getCache() {
        return simpleCache;
    }

    // implements Shopper

    @Override
    public ShopperMemento getShopperMemento() {
        return shopperMemento;
    }

    @Override
    public void setShopperMemento(final ShopperMemento shopperMemento) {
        this.shopperMemento = shopperMemento;
    }

    @Override
    public ShopperBrowsingActivity getBrowsingActivity() {
        return browsingActivity;
    }

    public void setBrowsingActivity(final ShopperBrowsingActivity browsingActivity) {
        this.browsingActivity = browsingActivity;
    }

    @Override
    public boolean isPersisted() {
        return shopperMemento.isPersisted();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        if (other instanceof ExtShopper) {
            ExtShopper otherShopper = (ExtShopper) other;
            return getGuid().equals(otherShopper.getGuid());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getGuid().hashCode();
    }

    /**
     * Gets the current {@link FavoriteList}.
     *
     * @return a {@link FavoriteList}.
     */
    @Override
    public FavoriteList getCurrentFavoriteList() {
        return favoriteList;
    }

    /**
     * Sets the current {@link FavoriteList}.
     *
     * @param favoriteList the {@link FavoriteList}.
     */
    @Override
    public void setCurrentFavoriteList(final FavoriteList favoriteList) {
        this.favoriteList = favoriteList;
    }
}
