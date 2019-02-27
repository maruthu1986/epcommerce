/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog;

import java.math.BigDecimal;
import java.util.Date;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.money.Money;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents Gift Certificate.
 */
public interface GiftCertificate extends Entity, StoreObject {

	/** Key for data field. */
	String KEY_RECIPIENT_EMAIL = "giftCertificate.recipientEmail";
	/** Key for data field. */
	String KEY_RECIPIENT_NAME = "giftCertificate.recipientName";
	/** Key for data field. */
	String KEY_SENDER_NAME = "giftCertificate.senderName";
	/** Key for data field. */
	String KEY_MESSAGE = "giftCertificate.message";
	/** Key for the gift certificate code. */
	String KEY_CODE = "giftCertificate.code";
	/** Key for the sender email. */
	String KEY_SENDER_EMAIL = "giftCertificate.senderEmail";
	/** The String name of the Gift Certificate {@code ProductType}. */
	String KEY_PRODUCT_TYPE = "Gift Certificates";
	/** Key for the gift certificate GUID. */
	String KEY_GUID = "giftCertificate.guid";


	/**
	 * @return Gift Certificate Code
	 */
	String getGiftCertificateCode();

	/**
	 * sets Gift Certificate Code.
	 *
	 * @param giftCertificateCode Gift Certificate Code
	 */
	void setGiftCertificateCode(String giftCertificateCode);

	/**
	 * @return Creation Date of Gift Certificate
	 */
	Date getCreationDate();

	/**
	 * @param creationDate Creation Date
	 */
	void setCreationDate(Date creationDate);

	/**
	 * @return last modified date
	 */
	Date getLastModifiedDate();

	/**
	 * @param lastModifiedDate last modified date
	 */
	void setLastModifiedDate(Date lastModifiedDate);

	/**
	 * @return get the purchaser
	 */
	Customer getPurchaser();

	/**
	 * @param purchaser sets the purchaser
	 */
	void setPurchaser(Customer purchaser);

	/**
	 * @return the recipient name
	 */
	String getRecipientName();

	/**
	 * @param recipientName sets the recipient name
	 */
	void setRecipientName(String recipientName);

	/**
	 * @return the sender's name
	 */
	String getSenderName();

	/**
	 * @param senderName sets the senders name
	 */
	void setSenderName(String senderName);

	/**
	 * @return the message from sender
	 */
	String getMessage();

	/**
	 * @param message sets the message from sender
	 */
	void setMessage(String message);

	/**
	 * @return gets the theme
	 */
	String getTheme();

	/**
	 * @param theme sets the theme
	 */
	void setTheme(String theme);

	/**
	 * @return the purchase amount
	 */
	BigDecimal getPurchaseAmount();

	/**
	 * @param purchaseAmount sets the purchase amount
	 */
	void setPurchaseAmount(BigDecimal purchaseAmount);

	/**
	 * @return the currency code
	 */
	String getCurrencyCode();

	/**
	 * @param currencyCode sets the currencey code
	 */
	void setCurrencyCode(String currencyCode);

	/**
	 * @return the recipientEmail
	 */
	String getRecipientEmail();

	/**
	 * @param recipientEmail the recipientEmail to set
	 */
	void setRecipientEmail(String recipientEmail);

	/**
	 * Get the balance money of gift certificate.
	 *
	 * @return a <code>Money</code> object representing the balance money
	 */
	Money getPurchaseAmountMoney();

	/**
	 * Displays the gift certificate code.
	 *
	 * @return a gift certificate code
	 */
	String displayGiftCertificateCode();

	/**
	 * Displays the masked gift certificate code.
	 *
	 * @return a masked gift certificate code
	 */
	String displayMaskedGiftCertificateCode();

	/**
	 * Retrieves and returns the balance money of gift certificate.
	 *
	 * @return a <code>Money</code> object representing the balance money.
	 */
	Money retrieveBalanceMoney();

	/**
	 * @param orderGuid the GUID of the order in which this gift certificate was created
	 */
	void setOrderGuid(String orderGuid);

	/**
	 * @return the order in which this gift certificate was created
	 */
	String getOrderGuid();
}
