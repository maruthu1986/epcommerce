/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.datapolicy;

import java.util.Map;
import java.util.TreeMap;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Data point locations required by readers and removers.
 */
public class DataPointLocationEnum extends AbstractExtensibleEnum<DataPointLocationEnum> {
	/*
	 Every data point location may have 0 or more supported fields.
	 E.g. addresses, order fields, GCs
	 */
	private static final Map<String, String> SUPPORTED_ADDRESS_FIELDS = new TreeMap<>();
	private static final Map<String, String> SUPPORTED_ORDER_PAYMENT_GIFT_CERTIFICATE_FIELDS = new TreeMap<>();
	private static final Map<String, String> SUPPORTED_ORDER_FIELDS = new TreeMap<>();

	static {
		SUPPORTED_ADDRESS_FIELDS.put("FIRST_NAME", "firstName");
		SUPPORTED_ADDRESS_FIELDS.put("LAST_NAME", "lastName");
		SUPPORTED_ADDRESS_FIELDS.put("PHONE_NUMBER", "phoneNumber");
		SUPPORTED_ADDRESS_FIELDS.put("STREET_1", "street1");
		SUPPORTED_ADDRESS_FIELDS.put("STREET_2", "street2");

		SUPPORTED_ORDER_PAYMENT_GIFT_CERTIFICATE_FIELDS.put("RECIPIENT_NAME", "recipientName");
		SUPPORTED_ORDER_PAYMENT_GIFT_CERTIFICATE_FIELDS.put("SENDER_NAME", "senderName");
		SUPPORTED_ORDER_PAYMENT_GIFT_CERTIFICATE_FIELDS.put("RECIPIENT_EMAIL", "recipientEmail");

		SUPPORTED_ORDER_FIELDS.put("ORDER_IP_ADDRESS", "ipAddress");

	}
	/**
	 * Ordinal constant for CART_GIFT_CERTIFICATE.
	 */
	public static final int CART_GIFT_CERTIFICATE_ORDINAL = 1;

	/**
	 * Ordinal constant for CUSTOMER_BILLING_ADDRESS.
	 */
	public static final int CUSTOMER_BILLING_ADDRESS_ORDINAL = 2;

	/**
	 * Ordinal constant for CUSTOMER_PROFILE.
	 */
	public static final int CUSTOMER_PROFILE_ORDINAL = 3;

	/**
	 * Ordinal constant for CUSTOMER_SHIPPING_ADDRESS.
	 */
	public static final int CUSTOMER_SHIPPING_ADDRESS_ORDINAL = 4;

	/**
	 * Ordinal constant for ORDER_BILLING_ADDRESS.
	 */
	public static final int ORDER_BILLING_ADDRESS_ORDINAL = 5;

	/**
	 * Ordinal constant for ORDER_DATA.
	 */
	public static final int ORDER_DATA_ORDINAL = 6;

	/**
	 * Ordinal constant for ORDER_GIFT_CERTIFICATE.
	 */
	public static final int ORDER_GIFT_CERTIFICATE_ORDINAL = 7;

	/**
	 * Ordinal constant for ORDER_IP_ADDRESS.
	 */
	public static final int ORDER_IP_ADDRESS_ORDINAL = 8;


	/**
	 * Ordinal constant for ORDER_PAYMENT_GIFT_CERTIFICATE.
	 */
	public static final int ORDER_PAYMENT_GIFT_CERTIFICATE_ORDINAL = 10;

	/**
	 * Data point location for cart gift certificate.
	 */
	public static final DataPointLocationEnum CART_GIFT_CERTIFICATE = new DataPointLocationEnum(CART_GIFT_CERTIFICATE_ORDINAL,
		"CART_GIFT_CERTIFICATE");
	/**
	 * Data point location for customer billing address.
	 */
	public static final DataPointLocationEnum CUSTOMER_BILLING_ADDRESS = new DataPointLocationEnum(CUSTOMER_BILLING_ADDRESS_ORDINAL,
		"CUSTOMER_BILLING_ADDRESS", SUPPORTED_ADDRESS_FIELDS);
	/**
	 * Data point location for customer profile.
	 */
	public static final DataPointLocationEnum CUSTOMER_PROFILE = new DataPointLocationEnum(CUSTOMER_PROFILE_ORDINAL,
		"CUSTOMER_PROFILE");

	/**
	 * Data point location for customer shipping address.
	 */
	public static final DataPointLocationEnum CUSTOMER_SHIPPING_ADDRESS = new DataPointLocationEnum(CUSTOMER_SHIPPING_ADDRESS_ORDINAL,
		"CUSTOMER_SHIPPING_ADDRESS", SUPPORTED_ADDRESS_FIELDS);

	/**
	 * Data point location for order billing address.
	 */
	public static final DataPointLocationEnum ORDER_BILLING_ADDRESS = new DataPointLocationEnum(ORDER_BILLING_ADDRESS_ORDINAL,
			"ORDER_BILLING_ADDRESS", SUPPORTED_ADDRESS_FIELDS);

	/**
	 * Data point location for order data.
	 */
	public static final DataPointLocationEnum ORDER_DATA = new DataPointLocationEnum(ORDER_DATA_ORDINAL,
		"ORDER_DATA");

	/**
	 * Data point location for order gift certificate.
	 */
	public static final DataPointLocationEnum ORDER_GIFT_CERTIFICATE = new DataPointLocationEnum(ORDER_GIFT_CERTIFICATE_ORDINAL,
		"ORDER_GIFT_CERTIFICATE");

	/**
	 * Data point location for order IP address.
	 */
	public static final DataPointLocationEnum ORDER_IP_ADDRESS = new DataPointLocationEnum(ORDER_IP_ADDRESS_ORDINAL,
		"ORDER_IP_ADDRESS", SUPPORTED_ORDER_FIELDS);

	/**
	 * Data point location for order payment gift certificate.
	 */
	public static final DataPointLocationEnum ORDER_PAYMENT_GIFT_CERTIFICATE = new DataPointLocationEnum(ORDER_PAYMENT_GIFT_CERTIFICATE_ORDINAL,
		"ORDER_PAYMENT_GIFT_CERTIFICATE", SUPPORTED_ORDER_PAYMENT_GIFT_CERTIFICATE_FIELDS);

	private static final long serialVersionUID = 5000000001L;

	private final Map<String, String> supportedFields;

	/**
	 * Create an enum value for a enum type. Name is case-insensitive and will be stored in upper-case.
	 *
	 * @param ordinal the ordinal value
	 * @param name    the name value (this will be converted to upper-case).
	 */
	protected DataPointLocationEnum(final int ordinal, final String name) {
		this(ordinal, name, new TreeMap<>());
	}

	/**
	 * Custom constructor.
	 *
	 * @param ordinal the enum ordinal.
	 * @param name the enum name.
	 * @param supportedFields the map with supported fields.
	 */
	protected DataPointLocationEnum(final int ordinal, final String name, final Map<String, String> supportedFields) {
		super(ordinal, name, DataPointLocationEnum.class);
		this.supportedFields = supportedFields;
	}

	/**
	 * Find the enum value with the specified ordinal value.
	 *
	 * @param ordinal the ordinal value
	 * @return the enum value
	 */
	public static DataPointLocationEnum valueOf(final int ordinal) {
		return valueOf(ordinal, DataPointLocationEnum.class);
	}

	/**
	 * Find the enum value with the specified name value.
	 *
	 * @param name the name
	 * @return the enum value
	 */
	public static DataPointLocationEnum valueOf(final String name) {
		return valueOf(name, DataPointLocationEnum.class);
	}

	public Map<String, String> getSupportedFields() {
		return supportedFields;
	}

	@Override
	protected Class<DataPointLocationEnum> getEnumType() {
		return DataPointLocationEnum.class;
	}
}
