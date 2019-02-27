/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.customer.producer.impl;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.domain.shoppingcart.impl.WishListMessageImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.email.EmailDto;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.customer.helper.WishListEmailPropertyHelper;
import com.elasticpath.email.producer.api.EmailProducer;
import com.elasticpath.email.producer.spi.composer.EmailComposer;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.service.store.StoreService;

/**
 * Producer for the share wish list email.
 */
public class WishListSharedEmailProducer implements EmailProducer {

	private EmailComposer emailComposer;

	private WishListEmailPropertyHelper wishListEmailPropertyHelper;

	private WishListService wishListService;

	private StoreService storeService;

	private static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE = "The emailData must contain a non-null '%s' value.";

	private static final String WISH_LIST_UID_KEY = "wishListUid";

	private static final String WISH_LIST_MESSAGE_KEY = "wishListMessage";

	private static final String LOCALE_KEY = "locale";

	private static final String STORE_CODE_KEY = "storeCode";

	private static final String WISH_LIST_SENDER_KEY = "wishListSender";

	private static final String WISH_LIST_RECIPIENTS_KEY = "wishListRecipients";

	@Override
	public Collection<EmailDto> createEmails(final String guid, final Map<String, Object> emailData) {
		final WishList wishList = getWishList(emailData);

		final Locale locale = getLocale(emailData);
		final Store store = getStore(emailData);

		final WishListMessage wishListMessage = getWishListMessage(emailData);

		final EmailProperties wishListEmailProperties =
				getWishListEmailPropertyHelper().getWishListEmailProperties(wishListMessage, wishList, store, locale);

		final EmailDto emailDtoTemplate = getEmailComposer().composeMessage(wishListEmailProperties);

		return getRecipients(emailData).stream()
				.map(recipient -> EmailDto.builder()
						.fromPrototype(emailDtoTemplate)
						.addTo(recipient)
						.build())
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves a {@link WishList} with the given GUID.
	 * 
	 * @param emailData email contextual data
	 * @return a {@link WishList}
	 * @throws IllegalArgumentException if an {@link WishList} can not be retrieved from the given parameters
	 */
	protected WishList getWishList(final Map<String, Object> emailData) {
		final Long uid = Long.valueOf((Integer) getObjectFromEmailData(WISH_LIST_UID_KEY, emailData));
		final WishList wishList = getWishListService().get(uid);

		if (wishList == null) {
			throw new IllegalArgumentException("Could not locate a WishList with uid [" + uid + "]");
		}

		return wishList;
	}

	/**
	 * Retrieves the {@link WishListMessage} from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the {@link WishListMessage}
	 * @throws IllegalArgumentException if the wish list message can not be retrieved from the given parameters
	 */
	protected WishListMessage getWishListMessage(final Map<String, Object> emailData) {
		final String messageText = (String) getObjectFromEmailData(WISH_LIST_MESSAGE_KEY, emailData);

		final String sender = (String) getObjectFromEmailData(WISH_LIST_SENDER_KEY, emailData);

		final WishListMessage wishListMessage = createWishListMessage();
		wishListMessage.setMessage(messageText);
		wishListMessage.setSenderName(sender);
		return wishListMessage;
	}

	/**
	 * Factory method for {@link WishListMessage} instances.
	 *
	 * @return a new WishListMessage instance
	 */
	protected WishListMessage createWishListMessage() {
		return new WishListMessageImpl();
	}

	/**
	 * Builds a collection of wish list recipient email addresses from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the {@link Collection} of email addresses
	 * @throws IllegalArgumentException if the list of recipients can not be retrieved from the given parameters
	 */
	protected Collection<String> getRecipients(final Map<String, Object> emailData) {
		final String recipientString = (String) getObjectFromEmailData(WISH_LIST_RECIPIENTS_KEY, emailData);

		final List<String> recipients = Splitter.on(",")
				.omitEmptyStrings()
				.splitToList(recipientString);

		if (recipients.isEmpty()) {
			throw new IllegalArgumentException("At least one recipient must be listed for the Wish List to be shared.");
		}

		return recipients;
	}

	/**
	 * Retrieves the {@link Locale} from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the {@link Locale}
	 * @throws IllegalArgumentException if the Locale can not be retrieved from the given parameters
	 */
	protected Locale getLocale(final Map<String, Object> emailData) {
		final String languageTag = (String) getObjectFromEmailData(LOCALE_KEY, emailData);
		return Locale.forLanguageTag(languageTag);
	}

	/**
	 * Retrieves the {@link Store} from the given {@code Map} of email contextual data.
	 * 
	 * @param emailData email contextual data
	 * @return the {@link Store}
	 * @throws IllegalArgumentException if the Store can not be retrieved from the given parameters
	 */
	protected Store getStore(final Map<String, Object> emailData) {

		final String storeCode = (String) getObjectFromEmailData(STORE_CODE_KEY, emailData);
		final Store store = storeService.findStoreWithCode(storeCode);

		if (store == null) {
			throw new IllegalArgumentException("No store could be found with storeCode '" + storeCode + "'.");
		}

		return store;
	}

	/**
	 * Retrieves an Object from the given {@code Map} of email contextual data.
	 * 
	 * @param key the object key
	 * @param emailData email contextual data
	 * @return the Object
	 * @throws IllegalArgumentException if the Object can not be retrieved from the given parameters
	 */
	protected Object getObjectFromEmailData(final String key, final Map<String, Object> emailData) {
		if (emailData == null || !emailData.containsKey(key) || emailData.get(key) == null) {
			throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_TEMPLATE, key));
		}

		return emailData.get(key);
	}

	public void setWishListEmailPropertyHelper(final WishListEmailPropertyHelper wishListEmailPropertyHelper) {
		this.wishListEmailPropertyHelper = wishListEmailPropertyHelper;
	}

	public WishListEmailPropertyHelper getWishListEmailPropertyHelper() {
		return wishListEmailPropertyHelper;
	}

	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	public EmailComposer getEmailComposer() {
		return emailComposer;
	}

	public void setWishListService(final WishListService wishListService) {
		this.wishListService = wishListService;
	}

	public WishListService getWishListService() {
		return wishListService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

}
