/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.exception.EpDateBindException;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.misc.PayerAuthValidationValue;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.money.Money;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Represents customer payment information.
 */
@Entity
@Table(name = OrderPaymentImpl.TABLE_NAME)
@DataCache(enabled = false)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = {
			@FetchAttribute(name = "currencyCode")
		}),
	@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = {
		@FetchAttribute(name = "paymentMethod"),
		@FetchAttribute(name = "displayValue"),
		@FetchAttribute(name = "currencyCode")
	}, postLoad = true)
})
@SuppressWarnings({ "PMD.TooManyFields", "PMD.CyclomaticComplexity", "PMD.GodClass" })
public class OrderPaymentImpl extends AbstractLegacyPersistenceImpl implements OrderPayment, DatabaseLastModifiedDate {
	private static final long serialVersionUID = 5000000001L;

	private static final int MEDIUM_TEXT_LENGTH = 100;

	private static final int SHORT_TEXT_LENGTH = 50;

	private static final int CURRENCY_LENGTH = 10;

	private static final int TRANS_TYPE_LENGTH = 20;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERPAYMENT";

	private BigDecimal amount;

	private PaymentType paymentMethod;

	private String authorizationCode;

	private String referenceId;

	private String requestToken;

	private String currencyCode;

	private String email;

	private String transactionType;

	private OrderPaymentStatus status;

	private String gatewayToken;

	private String token;
	
	private Date createdDate;

	private GiftCertificate giftCertificate;

	private String ipAddress;

	private PayerAuthValidationValue payerAuthValidationValue;

	private long uidPk;

	private Order order;

	private OrderShipment orderShipment;

	private Date lastModifiedDate;
	
	private boolean paymentForSubscriptions;

	private String displayValue;

	@Override
	@ManyToOne(targetEntity = OrderImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "ORDER_UID")
	@ForeignKey(name = "torderpayment_ibfk_1")
	public Order getOrder() {
		return order;
	}

	@Override
	public void setOrder(final Order order) {
		this.order = order;
	}

	/**
	 * Get the related orderShipment. The ordershipment maybe null when the payment is on the order level and not related to a
	 * shipment.
	 * 
	 * @return the orderShipment
	 */
	@Override
	@ManyToOne(targetEntity = AbstractOrderShipmentImpl.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "ORDERSHIPMENT_UID")
	@ForeignKey(name = "torderpayment_ibfk_3")
	public OrderShipment getOrderShipment() {
		return orderShipment;
	}

	/**
	 * Set the orderShipment for the payment.
	 * 
	 * @param orderShipment the orderShipment to set
	 */
	@Override
	public void setOrderShipment(final OrderShipment orderShipment) {
		this.orderShipment = orderShipment;
	}

	/**
	 * Get the date that this order was created on.
	 * 
	 * @return the created date
	 */
	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Set the date that the order is created.
	 * 
	 * @param createdDate the start date
	 */
	@Override
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Gets display value.
	 *
	 * @return the display value
	 */
	@Override
	@Basic
	@Column(name = "DISPLAY_VALUE")
	public String getDisplayValue() {
		return displayValue;
	}

	@Override
	public void setDisplayValue(final String displayValue) {
		this.displayValue = displayValue;
	}
	
	/**
	 * Creates a Date object from a zero-based month and a year, using the default locale.
	 * @param month the two-digit integer representing the month (zero-based)
	 * @param year the four-digit integer representing the year
	 * @return a Date representing the given month and year, or null if either
	 * of the given Strings is blank.
	 * @throws EpDateBindException if there is a parsing error
	 */
	Date createDate(final String month, final String year) {
		final String datePattern = "MMyyyy";
		if (!StringUtils.isBlank(month) && !StringUtils.isBlank(year)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(datePattern, Locale.getDefault());
				return sdf.parse(month + year);
			} catch (ParseException pe) {
				throw new EpDateBindException("Invalid month or year strings. Month:" + month + " Year:" + year, pe);
			}
		}
		return null;
	}

	/**
	 * Get the payment method. A payment method could be the name of the payment processor/gateway.
	 * 
	 * @return the payment method
	 */
	@Override
	@Persistent
	@Column(name = "PAYMENT_GATEWAY", length = MEDIUM_TEXT_LENGTH)
	@Externalizer("getName")
	@Factory("PaymentType.valueOf")
	public PaymentType getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * Set the payment method.
	 * 
	 * @param paymentMethod the payment method
	 */
	@Override
	public void setPaymentMethod(final PaymentType paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * Get the amount of this payment.
	 * 
	 * @return the amount
	 */
	@Override
	@Basic
	@Column(name = "AMOUNT", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getAmount() {
		return this.amount;
	}

	/**
	 * Set the amount of this payment.
	 * 
	 * @param amount the amount
	 */
	@Override
	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Get the reference id.
	 * 
	 * @return the reference id.
	 */
	@Override
	@Basic
	@Column(name = "REFERENCE_ID", length = SHORT_TEXT_LENGTH)
	public String getReferenceId() {
		return this.referenceId;
	}

	/**
	 * Set the reference id.
	 * 
	 * @param referenceId the reference id
	 */
	@Override
	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	/**
	 * Get the request token.
	 * 
	 * @return the request token
	 */
	@Override
	@Basic
	@Column(name = "REQUEST_TOKEN")
	public String getRequestToken() {
		return this.requestToken;
	}

	/**
	 * Set the request token.
	 * 
	 * @param requestToken the request token
	 */
	@Override
	public void setRequestToken(final String requestToken) {
		this.requestToken = requestToken;
	}

	/**
	 * Get the authorization code.
	 * 
	 * @return the authorization code
	 */
	@Override
	@Basic
	@Column(name = "AUTHORIZATION_CODE", length = SHORT_TEXT_LENGTH)
	public String getAuthorizationCode() {
		return this.authorizationCode;
	}

	/**
	 * Set the authorization code.
	 * 
	 * @param authorizationCode the authorization code
	 */
	@Override
	public void setAuthorizationCode(final String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	/**
	 * Get the currency code (e.g. CAD or USD).
	 * 
	 * @return the currency code
	 */
	@Override
	@Basic
	@Column(name = "CURRENCY", length = CURRENCY_LENGTH)
	public String getCurrencyCode() {
		return this.currencyCode;
	}

	/**
	 * Set the currency code.
	 * 
	 * @param currencyCode the currency code code
	 */
	@Override
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * Get the customer's email address (Required for card processing).
	 * 
	 * @return the customer email address
	 */
	@Override
	@Basic
	@Column(name = "EMAIL", length = MEDIUM_TEXT_LENGTH)
	public String getEmail() {
		return this.email;
	}

	/**
	 * Set the customer's email address (Required for card processing).
	 * 
	 * @param email the customer's email address
	 */
	@Override
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @return the payment transaction type
	 */
	@Override
	@Basic
	@Column(name = "TRANSACTION_TYPE", length = TRANS_TYPE_LENGTH)
	public String getTransactionType() {
		return this.transactionType;
	}

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @param transactionType the payment transaction type
	 */
	@Override
	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * Get the status of the order payment.
	 * 
	 * @return the order payment status
	 */
	@Override
	@Basic
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	public OrderPaymentStatus getStatus() {
		return this.status;
	}

	/**
	 * Set the status of the order payment.
	 * 
	 * @param status the status of the order payment
	 */
	@Override
	public void setStatus(final OrderPaymentStatus status) {
		this.status = status;
	}

	/**
	 * Store the temporary token needed by some payment gateway, i.e. PayPal to complete the process.
	 * 
	 * @param gatewayToken payment gateway token
	 */
	@Override
	public void setGatewayToken(final String gatewayToken) {
		this.gatewayToken = gatewayToken;
	}

	/**
	 * Return the payment gateway token.
	 * 
	 * @return the temproary payment gateway token.
	 */
	@Override
	@Transient
	public String getGatewayToken() {
		return this.gatewayToken;
	}

	@Override
	public void usePaymentToken(final PaymentToken paymentToken) {
		this.displayValue = paymentToken.getDisplayValue();
		this.token = paymentToken.getValue();
	}

	@Override
	public PaymentToken extractPaymentToken() {
		return new PaymentTokenImpl.TokenBuilder().withValue(token).withDisplayValue(displayValue).build();
	}

	/**
	 * Get the ipAddress of the user from the Order Payment.
	 * 
	 * @return the ipAddress
	 */
	@Override
	@Transient
	public String getIpAddress() {
		return this.ipAddress;
	}

	/**
	 * Set the users ip Address into the Order Payment.
	 * 
	 * @param ipAddress the ipAddress of the user.
	 */
	@Override
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Get the gift certificate for the payment.
	 * 
	 * @return the giftCertificate
	 */
	@Override
	@ManyToOne(targetEntity = GiftCertificateImpl.class, cascade = {CascadeType.REFRESH, CascadeType.MERGE})
	@JoinColumn(name = "GIFTCERTIFICATE_UID")
	public GiftCertificate getGiftCertificate() {
		return giftCertificate;
	}

	/**
	 * Set the gift certificate for the payment.
	 * 
	 * @param giftCertificate the giftCertificate to set
	 */
	@Override
	public void setGiftCertificate(final GiftCertificate giftCertificate) {
		this.giftCertificate = giftCertificate;
	}

	/**
	 * Get Payer Authentication Validate value for transaction.
	 * 
	 * @return the payerAuthValidationValue.
	 */
	@Override
	@Transient
	public PayerAuthValidationValue getPayerAuthValidationValue() {
		return this.payerAuthValidationValue;
	}

	/**
	 * Set the payerAuthValidationValue for order payment.
	 * 
	 * @param payerAuthValidationValue the payerAuthValidationValue
	 */
	@Override
	public void setPayerAuthValidationValue(final PayerAuthValidationValue payerAuthValidationValue) {
		this.payerAuthValidationValue = payerAuthValidationValue;
	}

	/**
	 * Copy orderPayment's Gateway info to <code>this</code> Order Payment.
	 * 
	 * @param orderPayment the source of Gateway info.
	 */
	@Override
	public void copyTransactionFollowOnInfo(final OrderPayment orderPayment) {
		setPaymentMethod(orderPayment.getPaymentMethod());
		setReferenceId(orderPayment.getReferenceId());
		setAuthorizationCode(orderPayment.getAuthorizationCode());
		setCurrencyCode(orderPayment.getCurrencyCode());
		setEmail(orderPayment.getEmail());
		setDisplayValue(orderPayment.getDisplayValue());
		setRequestToken(orderPayment.getRequestToken());
	}

	/**
	 * Get the total amount money.
	 * 
	 * @return a <code>Money</code> object representing the total amount
	 */
	@Override
	@Transient
	public Money getAmountMoney() {
		return Money.valueOf(getAmount(), Currency.getInstance(getCurrencyCode()));
	}
	
	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * Set the date that this was last modified on.
	 * 
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME, allocationSize = HIGH_CONCURRENCY_ALLOCATION_SIZE)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Basic
	@Column(name = "PAYMENT_FOR_SUBSCRIPTIONS")
	public boolean isPaymentForSubscriptions() {
		return paymentForSubscriptions;
	}

	@Override
	public void setPaymentForSubscriptions(final boolean paymentForSubscriptions) {
		this.paymentForSubscriptions = paymentForSubscriptions;
	}

	/**
	 * Display info for the order payment.
	 * 
	 * @return the string info
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("paymentMethod", getPaymentMethod())
				.append("transactionType", getTransactionType())
				.append("status", getStatus())				
				.append("amount", getAmount())
				.append("currency", getCurrencyCode())
				.append("createdDate", getCreatedDate())
				.append("email", getEmail())
				.toString();
	}

	/**
	 * Is string actual.
	 * 
	 * @param str string
	 * @return false if str is null or length of the str equals zero.
	 */
	protected boolean isActual(final String str) {
		return !StringUtils.isEmpty(str);
	}

	@Override
	public void copyLastModifiedDate(final OrderPayment payment) {
		this.setLastModifiedDate(payment.getLastModifiedDate());
	}
}
