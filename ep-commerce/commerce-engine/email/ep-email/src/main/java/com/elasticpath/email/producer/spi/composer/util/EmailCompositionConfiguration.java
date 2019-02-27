/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util;

import java.util.Optional;

import com.elasticpath.domain.store.Store;

/**
 * Contains details required to compose an email.
 */
public interface EmailCompositionConfiguration {

	/**
	 * Returns the Store used when composing the email, if applicable.
	 *
	 * @return an optional Store
	 */
	Optional<Store> getStore();

	/**
	 * Returns the email address from which the email should be sent.
	 *
	 * @return the email address from which the email should be sent
	 */
	String getSendFromAddress();

	/**
	 * Returns the email address for the recipient of the email.
	 *
	 * @return the email address for the recipient of the email
	 */
	String getRecipientAddress();

	/**
	 * Returns the subject of the email.
	 *
	 * @return the subject of the email
	 */
	String getEmailSubject();

	/**
	 * Returns the relative path and filename of the template used to render HTML email contents.
	 *
	 * @return the relative path and filename of the template used to render HTML email contents
	 */
	Optional<String> getHtmlTemplate();

	/**
	 * Returns the relative path and filename of the template used to render plain text email contents.
	 *
	 * @return the relative path and filename of the template used to render plain text email contents
	 */
	Optional<String> getTextTemplate();

	/**
	 * Returns the character encoding to use when composing the email.
	 *
	 * @return the character encoding to use when composing the email
	 */
	Optional<String> getCharacterEncoding();

	/**
	 * Indicates whether or not the email can be sent with HTML contents.
	 *
	 * @return true if the email can be sent with HTML contents
	 */
	boolean htmlEmailModeEnabled();

}
