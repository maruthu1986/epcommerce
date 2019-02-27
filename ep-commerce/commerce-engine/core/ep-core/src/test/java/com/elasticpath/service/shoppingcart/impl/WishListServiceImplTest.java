/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.core.messaging.customer.CustomerEventType;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.shoppingcart.impl.WishListImpl;
import com.elasticpath.domain.shoppingcart.impl.WishListMessageImpl;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.shoppingcart.dao.WishListDao;
import com.elasticpath.test.factory.TestCustomerSessionFactory;


/**
 * Test case for <code>WishListServiceImpl</code>.
 */
@RunWith(MockitoJUnitRunner.class)
public class WishListServiceImplTest {

	private WishListServiceImpl wishListService;

	@Mock
	private WishListImpl wishList;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private WishListDao wishListDao;

	@Mock
	private EventMessageFactory eventMessageFactory;

	@Mock
	private EventMessagePublisher eventMessagePublisher;

	@Mock
	private CartDirector cartDirector;

	@Before
	public void setUp() {
		wishListService = new WishListServiceImpl();

		wishListService.setBeanFactory(beanFactory);
		wishListService.setWishListDao(wishListDao);
		wishListService.setEventMessageFactory(eventMessageFactory);
		wishListService.setEventMessagePublisher(eventMessagePublisher);
		wishListService.setCartDirector(cartDirector);
	}

	/**
	 * Test creating a wish list.
	 */
	@Test
	public void testCreateWishList() {
		final CustomerSession customerSession = TestCustomerSessionFactory.getInstance().createNewCustomerSession();
		final Shopper shopper = customerSession.getShopper();

		when(beanFactory.getBean(ContextIdNames.WISH_LIST)).thenReturn(new WishListImpl() {
			private static final long serialVersionUID = -7785511152889149172L;

			@Override
			public void initialize() {
				//nothing to do ...
			}
		});

		WishList wishList = wishListService.createWishList(shopper);

		assertThat(wishList)
				.as("the returned wish list should not be null")
				.isNotNull();
		assertThat(wishList.getShopper())
				.as("the shopping context in wish list is not expected one")
				.isEqualTo(shopper);

		verify(beanFactory).getBean(ContextIdNames.WISH_LIST);
	}

	@Test
	public void verifyShareWishList() throws Exception {
		final String wishListGuid = "WISHLIST-001";
		final String storeCode = "STORE-1";
		final long wishListUidPk = 123L;
		final String message = "Please buy me this stuff!";
		final String recipientEmails = "you@example.org,thatotherguy@example.org";
		final String senderName = "Sender Sendingman";

		final WishListMessage wishListMessage = new WishListMessageImpl();
		final WishList wishList = mock(WishList.class);
		final Locale locale = Locale.CANADA;

		final Map<String, Object> wishListMessageData = Maps.newHashMap();
		wishListMessageData.put(WishListServiceImpl.LOCALE_KEY, locale.toString());
		wishListMessageData.put(WishListServiceImpl.STORE_CODE_KEY, storeCode);
		wishListMessageData.put(WishListServiceImpl.WISH_LIST_UID_KEY, wishListUidPk);
		wishListMessageData.put(WishListServiceImpl.WISH_LIST_MESSAGE_KEY, message);
		wishListMessageData.put(WishListServiceImpl.WISH_LIST_RECIPIENTS_KEY, recipientEmails);
		wishListMessageData.put(WishListServiceImpl.WISH_LIST_SENDER_NAME_KEY, senderName);

		wishListMessage.setMessage(message);
		wishListMessage.setRecipientEmails(recipientEmails);
		wishListMessage.setSenderName(senderName);

		when(wishList.getGuid()).thenReturn(wishListGuid);
		when(wishList.getUidPk()).thenReturn(wishListUidPk);
		final EventMessage eventMessage = mock(EventMessage.class);
		when(eventMessageFactory.createEventMessage(CustomerEventType.WISH_LIST_SHARED, wishListGuid,
				wishListMessageData)).thenReturn(eventMessage);

		wishListService.shareWishList(wishListMessage, wishList, storeCode, locale);

		verify(eventMessagePublisher).publish(eventMessage);
	}

	/**
	 * Test get existing item method.
	 */
	@Test
	public void testGetExistingItem() {
		when(cartDirector.itemsAreEqual(new ShoppingItemImpl(), new ShoppingItemImpl())).thenReturn(true);
		when(wishList.getAllItems()).thenReturn(Arrays.asList(new ShoppingItemImpl(), new ShoppingItemImpl()));

		wishListService.getExistingItem(wishList, new ShoppingItemImpl());

		verify(cartDirector).itemsAreEqual(any(), any());
	}

	@Test
	public void testAddNewItemToWishlist() {
		ShoppingItemImpl shoppingItem = new ShoppingItemImpl();
		shoppingItem.setGuid("one");

		when(wishList.getAllItems()).thenReturn(Collections.emptyList());
		when(wishList.addItem(any())).thenReturn(shoppingItem);
		AddToWishlistResult addToWishlistResult = wishListService.addItem(wishList, shoppingItem);

		assertThat(addToWishlistResult.getShoppingItem()).isEqualTo(shoppingItem);
		assertThat(addToWishlistResult.isNewlyCreated()).isTrue();
	}

	@Test
	public void testAddExistingItemToWishlist() {
		ShoppingItemImpl shoppingItem = new ShoppingItemImpl();
		shoppingItem.setGuid("one");

		when(wishList.getAllItems()).thenReturn(Collections.singletonList(shoppingItem));
		when(cartDirector.itemsAreEqual(any(), any())).thenReturn(true);
		AddToWishlistResult addToWishlistResult = wishListService.addItem(wishList, shoppingItem);

		assertThat(addToWishlistResult.getShoppingItem()).isEqualTo(shoppingItem);
		assertThat(addToWishlistResult.isNewlyCreated()).isFalse();

	}

}
