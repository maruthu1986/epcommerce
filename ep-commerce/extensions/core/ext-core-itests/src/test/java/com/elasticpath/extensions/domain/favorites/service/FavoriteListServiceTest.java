package com.elasticpath.extensions.domain.favorites.service;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.core.messaging.customer.CustomerEventType;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionMementoImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.shoppingcart.impl.WishListMessageImpl;
import com.elasticpath.extensions.dao.favorites.FavoritesDao;
import com.elasticpath.extensions.domain.favorites.FavoriteList;
import com.elasticpath.extensions.domain.favorites.impl.FavoriteListImpl;
import com.elasticpath.extensions.service.favorites.impl.FavoriteListServiceImpl;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.shoppingcart.impl.AddToWishlistResult;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;
import java.util.UUID;
import java.util.Collections;
import java.util.Map;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteListServiceTest {
    private FavoriteListServiceImpl favoriteListService;

    @Mock
    private FavoriteListImpl favoriteList;

    @Mock
    private BeanFactory beanFactory;

    @Mock
    private FavoritesDao favoritesDao;

    @Mock
    private EventMessageFactory eventMessageFactory;

    @Mock
    private EventMessagePublisher eventMessagePublisher;

    @Mock
    private CartDirector cartDirector;

    @Before
    public void setUp() {
        favoriteListService = new FavoriteListServiceImpl();

        favoriteListService.setBeanFactory(beanFactory);
        favoriteListService.setFavoritesDao(favoritesDao);
        favoriteListService.setEventMessageFactory(eventMessageFactory);
        favoriteListService.setEventMessagePublisher(eventMessagePublisher);
        favoriteListService.setCartDirector(cartDirector);
    }

    /**
     * Test creating a wish list.
     */
    @Test
    public void testCreateFavoriteList() {
        final CustomerSession customerSession = createNewCustomerSessionWithContext();
        final Shopper shopper = customerSession.getShopper();

        when(beanFactory.getBean("favoritesFlag")).thenReturn(new FavoriteListImpl() {
            private static final long serialVersionUID = -7785511152889149172L;

            @Override
            public void initialize() {
                //nothing to do ...
            }
        });

        FavoriteList favoriteList = favoriteListService.createFavoriteList(shopper);

        assertThat(favoriteList)
                .as("the returned FavoriteList should not be null")
                .isNotNull();
        assertThat(favoriteList.getShopper())
                .as("the shopping context in FavoriteList is not expected one")
                .isEqualTo(shopper);

        verify(beanFactory).getBean("favoritesFlag");
    }

    @Test
    public void verifyShareFavoriteList() throws Exception {
        final String favoriteListGuid = "FAVORITE-001";
        final String storeCode = "STORE-1";
        final long favoriteListUidPk = 123L;
        final String message = "This product is my favorite";
        final String recipientEmails = "example@techmahindra.com";
        final String senderName = "Sender";

        final WishListMessage listMessage = new WishListMessageImpl();
        final FavoriteList favoriteList = mock(FavoriteList.class);
        final Locale locale = Locale.CANADA;

        final Map<String, Object> favoritesMessageData = Maps.newHashMap();
        favoritesMessageData.put("locale", locale.toString());
        favoritesMessageData.put("storeCode", storeCode);
        favoritesMessageData.put("favoriteUid", favoriteListUidPk);
        favoritesMessageData.put("favoriteMessage", message);
        favoritesMessageData.put("favoriteRecipients", recipientEmails);
        favoritesMessageData.put("favoriteSender", senderName);

        listMessage.setMessage(message);
        listMessage.setRecipientEmails(recipientEmails);
        listMessage.setSenderName(senderName);

        when(favoriteList.getGuid()).thenReturn(favoriteListGuid);
        when(favoriteList.getUidPk()).thenReturn(favoriteListUidPk);
        final EventMessage eventMessage = mock(EventMessage.class);
        when(eventMessageFactory.createEventMessage(CustomerEventType.WISH_LIST_SHARED, favoriteListGuid,
                favoritesMessageData)).thenReturn(eventMessage);

        favoriteListService.shareFavoriteList(listMessage, favoriteList, storeCode, locale);

        verify(eventMessagePublisher).publish(eventMessage);
    }

    /**
     * Test get existing item method.
     */
    @Test
    public void testGetExistingItem() {
        when(cartDirector.itemsAreEqual(new ShoppingItemImpl(), new ShoppingItemImpl())).thenReturn(true);
        when(favoriteList.getAllItems()).thenReturn(Arrays.asList(new ShoppingItemImpl(), new ShoppingItemImpl()));

        favoriteListService.getExistingItem(favoriteList, new ShoppingItemImpl());

        verify(cartDirector).itemsAreEqual(any(), any());
    }

    @Test
    public void testAddNewItemToFavorites() {
        ShoppingItemImpl shoppingItem = new ShoppingItemImpl();
        shoppingItem.setGuid("one");

        when(favoriteList.getAllItems()).thenReturn(Collections.emptyList());
        when(favoriteList.addItem(any())).thenReturn(shoppingItem);
        AddToWishlistResult addToFavoritesResult = favoriteListService.addItem(favoriteList, shoppingItem);

        assertThat(addToFavoritesResult.getShoppingItem()).isEqualTo(shoppingItem);
        assertThat(addToFavoritesResult.isNewlyCreated()).isTrue();
    }

    @Test
    public void testAddExistingItemToFavorites() {
        ShoppingItemImpl shoppingItem = new ShoppingItemImpl();
        shoppingItem.setGuid("one");

        when(favoriteList.getAllItems()).thenReturn(Collections.singletonList(shoppingItem));
        when(cartDirector.itemsAreEqual(any(), any())).thenReturn(true);
        AddToWishlistResult addToFavoritesResult = favoriteListService.addItem(favoriteList, shoppingItem);

        assertThat(addToFavoritesResult.getShoppingItem()).isEqualTo(shoppingItem);
        assertThat(addToFavoritesResult.isNewlyCreated()).isFalse();

    }

    public CustomerSession createNewCustomerSessionWithContext() {
        Shopper shopper = createNewShopperWithMemento();
        final CustomerSession customerSession = new CustomerSessionImpl();
        final CustomerSessionMemento customerSessionMemento = new CustomerSessionMementoImpl();

        customerSession.setCustomerSessionMemento(customerSessionMemento);
        customerSession.setShopper(shopper);
        shopper.updateTransientDataWith(customerSession);

        customerSession.setGuid(UUID.randomUUID().toString());

        return customerSession;
    }

    public Shopper createNewShopperWithMemento() {
        final ShopperMemento shopperMemento = new ShopperMementoImpl();
        shopperMemento.setGuid(UUID.randomUUID().toString());

        final Shopper shopper = new ShopperImpl();
        shopper.setShopperMemento(shopperMemento);

        return shopper;
    }
}
