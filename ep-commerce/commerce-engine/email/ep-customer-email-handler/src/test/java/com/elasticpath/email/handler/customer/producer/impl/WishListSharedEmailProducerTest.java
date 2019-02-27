/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.shoppingcart.impl.WishListMessageImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.customer.helper.WishListEmailPropertyHelper;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.service.store.StoreService;

/**
 * Test class for {@link WishListSharedEmailProducer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class WishListSharedEmailProducerTest {

	private static final String WISH_LIST_SENDER_KEY = "wishListSender";
	private static final String WISH_LIST_RECIPIENTS_KEY = "wishListRecipients";
	private static final Integer WISH_LIST_UID = 1;
	private static final String WISH_LIST_GUID = "WISH_LIST_GUID";
	private static final String TEST_STORE_CODE = "TEST_STORE";
	private static final String LOCALE_KEY = "locale";
	private static final String WISH_LIST_MESSAGE_KEY = "wishListMessage";
	private static final String WISH_LIST_UID_KEY = "wishListUid";
	private static final String STORE_CODE_KEY = "storeCode";
	private static final Locale LOCALE = Locale.CANADA;

	@Mock
	private WishListEmailPropertyHelper wishListEmailPropertyHelper;

	@Mock
	private EmailComposer emailComposer;

	@Mock
	private WishListService wishlistService;

	@Mock
	private StoreService storeService;

	@Spy
	@InjectMocks
	private WishListSharedEmailProducer emailProducer;

	@Test
	public void verifyWishlistSharedEmailIsCreated() throws Exception {
		final String recipient = "recipient@elasticpath.com";

		final EmailDto expectedEmail = EmailDto.builder()
				.withTo(recipient)
				.build();

		final EmailProperties emailProperties = mock(EmailProperties.class);
		final Store store = mock(Store.class);
		final WishList wishList = mock(WishList.class);
		final WishListMessage wishListMessage = new WishListMessageImpl();

		when(emailProducer.createWishListMessage())
				.thenReturn(wishListMessage);

		when(wishlistService.get(WISH_LIST_UID))
				.thenReturn(wishList);

		when(storeService.findStoreWithCode(TEST_STORE_CODE))
				.thenReturn(store);

		when(wishListEmailPropertyHelper.getWishListEmailProperties(wishListMessage, wishList, store, LOCALE))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(EmailDto.builder().build());

		final Map<String, Object> additionalData = createValidAdditionalData(TEST_STORE_CODE, LOCALE, WISH_LIST_UID, recipient);

		final Collection<EmailDto> actualEmail = emailProducer.createEmails(WISH_LIST_GUID, additionalData);

		assertThat(actualEmail)
				.as("Unexpected email created by producer")
				.hasSize(1)
				.containsExactly(expectedEmail);
	}

	@Test
	public void verifyMultipleWishlistSharedEmailsAreCreated() throws Exception {
		final String recipient1 = "recipient1@elasticpath.com";
		final String recipient2 = "recipient2@elasticpath.com";
		final String recipient3 = "recipient3@elasticpath.com";

		final String recipients = recipient1 + "," + recipient2 + "," + recipient3;

		final EmailDto expectedEmailTemplate = EmailDto.builder().build();

		final EmailDto expectedEmail1 = EmailDto.builder()
				.fromPrototype(expectedEmailTemplate)
				.withTo(recipient1)
				.build();

		final EmailDto expectedEmail2 = EmailDto.builder()
				.fromPrototype(expectedEmailTemplate)
				.withTo(recipient2)
				.build();

		final EmailDto expectedEmail3 = EmailDto.builder()
				.fromPrototype(expectedEmailTemplate)
				.withTo(recipient3)
				.build();

		final EmailProperties emailProperties = mock(EmailProperties.class);
		final Store store = mock(Store.class);
		final WishList wishList = mock(WishList.class);
		final WishListMessage wishListMessage = new WishListMessageImpl();

		when(emailProducer.createWishListMessage())
				.thenReturn(wishListMessage);

		when(wishlistService.get(WISH_LIST_UID))
				.thenReturn(wishList);

		when(storeService.findStoreWithCode(TEST_STORE_CODE))
				.thenReturn(store);

		when(wishListEmailPropertyHelper.getWishListEmailProperties(wishListMessage, wishList, store, LOCALE))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(expectedEmailTemplate);


		final Map<String, Object> additionalData = createValidAdditionalData(TEST_STORE_CODE, LOCALE, WISH_LIST_UID, recipients);

		final Collection<EmailDto> actualEmails = emailProducer.createEmails(WISH_LIST_GUID, additionalData);

		assertThat(actualEmails).isNotNull();

		assertThat(actualEmails)
				.containsExactlyInAnyOrder(expectedEmail1, expectedEmail2, expectedEmail3);
	}

	@Test
	public void verifyExceptionThrownWhenNoWishListIsFound() throws Exception {
		assertThatThrownBy(() -> emailProducer.createEmails(WISH_LIST_GUID, null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyExceptionThrownWhenNoLocaleIsFound() throws Exception {
		when(wishlistService.get(WISH_LIST_UID)).thenReturn(mock(WishList.class));

		final Map<String, Object> additionalData = createValidAdditionalData(TEST_STORE_CODE, LOCALE, WISH_LIST_UID, "test@test.com");
		additionalData.put(LOCALE_KEY, null);

		assertThatThrownBy(() -> emailProducer.createEmails(WISH_LIST_GUID, additionalData))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyExceptionThrownWhenNoStoreIsFound() throws Exception {
		when(wishlistService.get(WISH_LIST_UID)).thenReturn(mock(WishList.class));

		when(storeService.findStoreWithCode(any(String.class))).thenReturn(null);

		final Map<String, Object> additionalData = createValidAdditionalData(TEST_STORE_CODE, LOCALE, WISH_LIST_UID, "test@test.com");

		assertThatThrownBy(() -> emailProducer.createEmails(WISH_LIST_GUID, additionalData))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyExceptionThrownWhenNoWishlistMessageIsFound() throws Exception {
		when(wishlistService.get(WISH_LIST_UID)).thenReturn(mock(WishList.class));

		when(storeService.findStoreWithCode(any(String.class))).thenReturn(mock(Store.class));

		final Map<String, Object> additionalData = createValidAdditionalData(TEST_STORE_CODE, LOCALE, WISH_LIST_UID, "test@test.com");
		additionalData.remove(WISH_LIST_MESSAGE_KEY);

		assertThatThrownBy(() -> emailProducer.createEmails(WISH_LIST_GUID, additionalData))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void verifyExceptionThrownWhenInvalidRecipientEmailGiven() throws Exception {
		final Store store = mock(Store.class);
		final WishList wishList = mock(WishList.class);
		final WishListMessage wishListMessage = new WishListMessageImpl();
		final EmailProperties emailProperties = mock(EmailProperties.class);

		when(emailProducer.createWishListMessage())
				.thenReturn(wishListMessage);

		when(wishlistService.get(WISH_LIST_UID))
				.thenReturn(wishList);

		when(storeService.findStoreWithCode(TEST_STORE_CODE))
				.thenReturn(store);

		when(wishListEmailPropertyHelper.getWishListEmailProperties(wishListMessage, wishList, store, LOCALE))
				.thenReturn(emailProperties);

		when(emailComposer.composeMessage(emailProperties))
				.thenReturn(EmailDto.builder().build());

		final Map<String, Object> additionalData = createValidAdditionalData(TEST_STORE_CODE, LOCALE, WISH_LIST_UID, "");

		assertThatThrownBy(() -> emailProducer.createEmails(WISH_LIST_GUID, additionalData))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private Map<String, Object> createValidAdditionalData(final String testStoreCode,
														  final Locale locale,
														  final Integer wishListUid,
														  final String recipients) {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put(LOCALE_KEY, locale.toLanguageTag());
		additionalData.put(STORE_CODE_KEY, testStoreCode);
		additionalData.put(WISH_LIST_MESSAGE_KEY, "Test Message Text");
		additionalData.put(WISH_LIST_SENDER_KEY, "James Bond");
		additionalData.put(WISH_LIST_RECIPIENTS_KEY, recipients);
		additionalData.put(WISH_LIST_UID_KEY, wishListUid);
		return additionalData;
	}

}
