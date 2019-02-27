/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.payment.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.misc.PayerAuthenticationEnrollmentResult;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PayPalExpressPaymentGateway;
import com.elasticpath.domain.payment.PaymentGatewayFactory;
import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentGatewayPlugin;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.capabilities.CreditCardCapability;
import com.elasticpath.plugin.payment.capabilities.DirectPostAuthCapability;
import com.elasticpath.plugin.payment.capabilities.ExternalAuthCapability;
import com.elasticpath.plugin.payment.capabilities.ExternalTokenAcquireCapability;
import com.elasticpath.plugin.payment.capabilities.FinalizeShipmentCapability;
import com.elasticpath.plugin.payment.capabilities.HostedPageAuthCapability;
import com.elasticpath.plugin.payment.capabilities.PaymentGatewayCapability;
import com.elasticpath.plugin.payment.capabilities.PaypalExpressCapability;
import com.elasticpath.plugin.payment.capabilities.RefundCapability;
import com.elasticpath.plugin.payment.capabilities.ReversePreAuthorizationCapability;
import com.elasticpath.plugin.payment.capabilities.SaleCapability;
import com.elasticpath.plugin.payment.capabilities.VoidCaptureCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptor;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.exceptions.PaymentOperationNotSupportedException;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;
import com.elasticpath.plugin.payment.transaction.PaymentTransactionResponse;
import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionRequest;
import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionResponse;
import com.elasticpath.plugin.payment.transaction.service.PaymentGatewayTransactionService;

/**
 * Abstract payment processing gateway. Extend this to implement specific gateways.
 */
@Entity
@Table(name = PaymentGatewayImpl.TABLE_NAME)
@DataCache
@SuppressWarnings({ "PMD.ExcessiveImports", "PMD.GodClass", "PMD.CouplingBetweenObjects" })
public class PaymentGatewayImpl extends AbstractLegacyPersistenceImpl implements CreditCardPaymentGateway,
	PayPalExpressPaymentGateway {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 6000000004L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TPAYMENTGATEWAY";

	private static final String DOES_NOT_SUPPORT_EXPRESS_CHECKOUT = " does not support express checkout";

	private List<String> supportedCurrencies;

	private String name;

	private Map<String, PaymentGatewayProperty> propertiesMap;

	private long uidPk;
	
	private String type;
	
	private static final Logger LOG = Logger.getLogger(PaymentGatewayImpl.class);

	/**
	 * Get the name of the payment gateway (e.g. CyberSource).
	 *
	 * @return the gateway name
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "NAME", unique = true)
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the payment gateway (e.g. CyberSource).
	 *
	 * @param name the gateway name
	 */
	@Override
	public void setName(final String name) {
		this.name = name;
	}
	
	@Override
	@Basic(optional = false)
	@Column(name = "TYPE")
	public String getType() {
		return type;
	}

	/**
	 * Set the payment type.
	 *
	 * @param type the new type
	 */
	@Override
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Get the properties map of the payment gateway (e.g. merchantID, keysDirectory).
	 *
	 * @return the gateway properties map
	 */
	@Override
	@OneToMany(targetEntity = PaymentGatewayPropertyImpl.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@MapKey(name = "key")
	@ElementJoinColumn(name = "PAYMENTGATEWAY_UID", nullable = false)
	@ElementForeignKey
	@ElementDependent
	public Map<String, PaymentGatewayProperty> getPropertiesMap() {
		if (propertiesMap == null && type != null) {
			propertiesMap = new TreeMap<>();
			populateDefaultProperties(propertiesMap);
		}
		return propertiesMap;
	}

	/**
	 * Set the properties map of the payment gateway (e.g. merchantID, keysDirectory).
	 *
	 * @param properties the gateway properties map
	 */
	@Override
	public void setPropertiesMap(final Map<String, PaymentGatewayProperty> properties) {
		this.propertiesMap = properties;
	}
	
	/**
	 * Get the payment gateway plugin associated with the payment gateway type of this record.
	 * 
	 * @return the payment gateway plugin
	 */
	@Transient
	protected PaymentGatewayPlugin getPaymentGatewayPlugin() {
			PaymentGatewayFactory pluginFactory = this.getBean(ContextIdNames.PAYMENT_GATEWAY_FACTORY);
		return pluginFactory.createConfiguredPaymentGatewayPlugin(getType(), getPropertiesMap());
	}
	
	/**
	 * Get the currencies supported by this payment gateway.
	 *
	 * @return a List of currency code strings (e.g. CAD)
	 */
	@Override
	@Transient
	public List<String> getSupportedCurrencies() {
		return this.supportedCurrencies;
	}

	/**
	 * Set the currencies supported by this payment gateway.
	 *
	 * @param currencies a List of currency code strings (e.g. CAD)
	 */
	@Override
	public void setSupportedCurrencies(final List<String> currencies) {
		this.supportedCurrencies = currencies;
	}

	/**
	 * Refunds a previous capture or creates independent refund.
	 *
	 * @param payment the payment to be refunded
	 * @param billingAddress the billing address if the refund is not a follow-up
	 */
	@Override
	public void refund(final OrderPayment payment, final Address billingAddress) {
		RefundCapability refundCapability = getPaymentGatewayPlugin().getCapability(RefundCapability.class);
		if (refundCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support refunds");
		}
		OrderPaymentDto orderPaymentDto = getConversionService().convert(payment, OrderPaymentDto.class);
		AddressDto addressDto = null;
		if (billingAddress != null) {
			addressDto = getConversionService().convert(billingAddress, AddressDto.class);
		}
		refundCapability.refund(orderPaymentDto, addressDto);
		updateOrderPaymentWithResponse(orderPaymentDto, payment);
	}

	/**
	 * Gateways should implement the {@link FinalizeShipmentCapability} if they need to finalize a shipment
	 * once all payment processing has been completed.  This may include, for example, sending confirmation emails from external checkouts.
	 * If the gateway does not implement the capability, it is ignored.
	 *
	 * @param orderShipment <CODE>OrderShipment</CODE> to be finalized
	 */
	@Override
	public void finalizeShipment(final OrderShipment orderShipment) {
		FinalizeShipmentCapability finalizeShipmentCapability = getPaymentGatewayPlugin().getCapability(FinalizeShipmentCapability.class);
		if (finalizeShipmentCapability == null) {
			LOG.debug("This gateway does not support finalizing shipments. Ignoring Shipment Number: " + orderShipment.getShipmentNumber());
		} else {
			OrderShipmentDto orderShipmentDto = getConversionService().convert(orderShipment, OrderShipmentDto.class);
			finalizeShipmentCapability.finalizeShipment(orderShipmentDto);
		}
	}

	/**
	 * Builds a properties object from the properties map. One difference from this and the
	 * properties map is that this will be a direct <code>String</code> -> <code>String</code>
	 * relationship. Changes in this object will not be reflected within the original properties
	 * map.
	 *
	 * @return A clone of the properties map in <code>String</code> -> <code>String</code>
	 *         format
	 */
	@Override
	public Properties buildProperties() {
		Properties prop = new Properties();
		for (Entry<String, PaymentGatewayProperty> entry : getPropertiesMap().entrySet()) {
			String value = entry.getValue().getValue();
			if (value == null) {
				value = "";
			}
			prop.setProperty(entry.getValue().getKey(), value);
		}
		return prop;
	}

	/**
	 * Merges the given properties with the existing properties map by adding each property to the
	 * property map. Each key and value will be casted to String via their <code>toString()</code>
	 * method.
	 *
	 * @param properties a properties object
	 */
	@Override
	public void mergeProperties(final Properties properties) {
		// don't try to new the properties map here, let getPropertiesMap new it, if needed
		Map<String, PaymentGatewayProperty> propertiesMap = getPropertiesMap();
		PaymentGatewayProperty value;
		String key;

		for (Entry<Object, Object> entry : properties.entrySet()) {
			key = entry.getKey().toString();
			value = getBean(ContextIdNames.PAYMENT_GATEWAY_PROPERTY);
			value.setValue(entry.getValue().toString());
			value.setKey(key);

			propertiesMap.put(key, value);
		}
	}

	/**
	 * Sets the properties map with the given properties by overwriting the existing properties
	 * map. Each key and value will be casted to a <code>String</code> via their
	 * <code>toString()</code> method.
	 *
	 * @param properties a properties object
	 */
	@Override
	public void setProperties(final Properties properties) {
		setPropertiesMap(null);
		mergeProperties(properties);
	}

	/**
	 * Gets the list of default property keys for a payment gateway.
	 *
	 * @return the list of default property keys for a payment gateway
	 */
	@Transient
	protected Set<String> getDefaultPropertyKeys() {
		if (type == null) {
			throw new IllegalStateException("Payment gateway type not set");
		}
		Set<String> defaultPropertyKeys = new TreeSet<>();
			PaymentGatewayFactory pluginFactory = this.getBean(ContextIdNames.PAYMENT_GATEWAY_FACTORY);
		PaymentGatewayPlugin plugin = pluginFactory.createUnconfiguredPluginGatewayPlugin(type);
		if (null != plugin) {
			Collection<String> parameters = plugin.getConfigurationParameters();
			defaultPropertyKeys.addAll(parameters);
		}

		return defaultPropertyKeys;
	}

	/**
	 * Populates the <code>propertiesMap</code> with keys from the
	 * <code>getDefaultPropertyKeys()</code> method.
	 *
	 * @param propertiesMap the properties map to populate the keys
	 */
	private void populateDefaultProperties(final Map<String, PaymentGatewayProperty> propertiesMap) {
		Set<String> defaultPropertKeys = getDefaultPropertyKeys();
		for (String key : defaultPropertKeys) {
			PaymentGatewayProperty property = getBean(ContextIdNames.PAYMENT_GATEWAY_PROPERTY);
			property.setKey(key);
			propertiesMap.put(key, property);
		}
	}

	/**
	 * Check whether a payment gateway property is null or empty.
	 * @param property the property to check
	 * @return true if the property is null or empty
	 */
	protected boolean isEmpty(final PaymentGatewayProperty property) {
		return property == null || StringUtils.isBlank(property.getValue());
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
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
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
	public String setExpressMarkCheckout(final ShoppingCart shoppingCart, final String returnUrl, final String cancelUrl) {
		PaypalExpressCapability paypalExpressCapability = getPaymentGatewayPlugin().getCapability(PaypalExpressCapability.class);
		if (paypalExpressCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + DOES_NOT_SUPPORT_EXPRESS_CHECKOUT);
		}

		ShoppingCartDto shoppingCartDto = getConversionService().convert(shoppingCart, ShoppingCartDto.class);
		return paypalExpressCapability.setExpressMarkCheckout(shoppingCartDto, returnUrl, cancelUrl);
	}

	@Override
	public String setExpressShortcutCheckout(final ShoppingCart shoppingCart, final String returnUrl, final String cancelUrl) {
		PaypalExpressCapability paypalExpressCapability = getPaymentGatewayPlugin().getCapability(PaypalExpressCapability.class);
		if (paypalExpressCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + DOES_NOT_SUPPORT_EXPRESS_CHECKOUT);
		}

		ShoppingCartDto shoppingCartDto = getConversionService().convert(shoppingCart, ShoppingCartDto.class);
		return paypalExpressCapability.setExpressShortcutCheckout(shoppingCartDto, returnUrl, cancelUrl);
	}

	@Override
	public Map<String, String> getExpressCheckoutDetails(final String token) {
		PaypalExpressCapability paypalExpressCapability = getPaymentGatewayPlugin().getCapability(PaypalExpressCapability.class);
		if (paypalExpressCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + DOES_NOT_SUPPORT_EXPRESS_CHECKOUT);
		}

		return paypalExpressCapability.getExpressCheckoutDetails(token);
	}

	@Override
	public void authorizeOrder(final OrderPayment orderPayment) {
		PaypalExpressCapability paypalExpressCapability = getPaymentGatewayPlugin().getCapability(PaypalExpressCapability.class);
		if (paypalExpressCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + DOES_NOT_SUPPORT_EXPRESS_CHECKOUT);
		}

			OrderPaymentDto orderPaymentDto = getConversionService().convert(orderPayment, OrderPaymentDto.class);
			paypalExpressCapability.authorizeOrder(orderPaymentDto, orderPaymentDto);
			updateOrderPaymentWithResponse(orderPaymentDto, orderPayment);
	}

	@Override
	public void order(final OrderPayment payment, final Address billingAddress) {
		PaypalExpressCapability paypalExpressCapability = getPaymentGatewayPlugin().getCapability(PaypalExpressCapability.class);
		if (paypalExpressCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + DOES_NOT_SUPPORT_EXPRESS_CHECKOUT);
		}

			OrderPaymentDto orderPaymentDto = getConversionService().convert(payment, OrderPaymentDto.class);
			AddressDto addressDto = null;
			if (billingAddress != null) {
				addressDto = getConversionService().convert(billingAddress, AddressDto.class);
			}
			paypalExpressCapability.order(orderPaymentDto, orderPaymentDto, addressDto);
			updateOrderPaymentWithResponse(orderPaymentDto, payment);
	}

	@Override
	@Transient
	public List<String> getSupportedCardTypes() {
		CreditCardCapability creditCardCapability = getPaymentGatewayPlugin().getCapability(CreditCardCapability.class);
		if (creditCardCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support multiple card types");
		}

		return creditCardCapability.getSupportedCardTypes();
	}

	@Override
	@Transient
	public Map<String, String> getEnabledCardTypes(final Store store) {
		CreditCardCapability creditCardCapability = getPaymentGatewayPlugin().getCapability(CreditCardCapability.class);
		if (creditCardCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support multiple card types");
		}

		// Only return card type if it's enabled in the store configuration
		Set<CreditCardType> storeCardTypes = store.getCreditCardTypes();
		Set<String> enabledCardTypes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (CreditCardType storeCardType : storeCardTypes) {
			enabledCardTypes.add(storeCardType.getCreditCardType());
		}

		// Only return card type if it's also supported by the payment gateway
		enabledCardTypes.retainAll(creditCardCapability.getSupportedCardTypes());

		// Create a map of card type names to card type codes
		Map<String, String> result = new HashMap<>();
		DirectPostAuthCapability directPostAuthCapability = getPaymentGatewayPlugin().getCapability(DirectPostAuthCapability.class);
		for (String enabledCardType : enabledCardTypes) {
			if (directPostAuthCapability == null) {
				result.put(enabledCardType, enabledCardType);
			} else {
				result.put(enabledCardType, directPostAuthCapability.getCardTypeInternalCode(enabledCardType));
			}
		}
		return result;
	}

	@Override
	@Transient
	public boolean isCvv2ValidationEnabled() {
		CreditCardCapability creditCardCapability = getPaymentGatewayPlugin().getCapability(CreditCardCapability.class);
		if (creditCardCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support CVV2 validation");
		}

		return creditCardCapability.isCvv2ValidationEnabled();
	}

	@Override
	@Transient
	public void setValidateCvv2(final boolean validate) {
		CreditCardCapability creditCardCapability = getPaymentGatewayPlugin().getCapability(CreditCardCapability.class);
		if (creditCardCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support CVV2 validation");
		}

		creditCardCapability.setValidateCvv2(validate);
	}

	@Override
	public PayerAuthenticationEnrollmentResult checkEnrollment(final ShoppingCart shoppingCart, final OrderPayment payment) {
		CreditCardCapability creditCardCapability = getPaymentGatewayPlugin().getCapability(CreditCardCapability.class);
		if (creditCardCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support enrollment");
		}

			ShoppingCartDto shoppingCartDto = getConversionService().convert(shoppingCart, ShoppingCartDto.class);
			OrderPaymentDto orderPaymentDto = getConversionService().convert(payment, OrderPaymentDto.class);
			PayerAuthenticationEnrollmentResultDto resultDto = 
				creditCardCapability.checkEnrollment(shoppingCartDto, orderPaymentDto);
			return getConversionService().convert(resultDto, PayerAuthenticationEnrollmentResult.class);
	}

	@Override
	public boolean validateAuthentication(final OrderPayment payment, final String paRes) {
		CreditCardCapability creditCardCapability = getPaymentGatewayPlugin().getCapability(CreditCardCapability.class);
		if (creditCardCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support authentication validation");
		}

		OrderPaymentDto orderPaymentDto = getConversionService().convert(payment, OrderPaymentDto.class);
		boolean result = creditCardCapability.validateAuthentication(orderPaymentDto, paRes);
		OrderPayment modifiedOrderPayment = getConversionService().convert(orderPaymentDto, OrderPayment.class);
		payment.setPayerAuthValidationValue(modifiedOrderPayment.getPayerAuthValidationValue());
		return result;
	}

	@Override
	@Transient
	public PaymentGatewayType getPaymentGatewayType() {
		PaymentGatewayFactory pluginFactory = this.getBean(ContextIdNames.PAYMENT_GATEWAY_FACTORY);
		return pluginFactory.getPaymentGatewayTypeForPlugin(getType());
	}

	@Override
	public void preAuthorize(final OrderPayment payment, final Address billingAddress) {
		AddressDto billingAddressDto = null;
		if (billingAddress != null) {
			billingAddressDto = getConversionService().convert(billingAddress, AddressDto.class);
		}

		OrderShipmentDto shipmentDto = null;
		if (payment.getOrderShipment() != null) {
			shipmentDto = getConversionService().convert(payment.getOrderShipment(), OrderShipmentDto.class);
		}
		
		AuthorizationTransactionRequest authorizationRequest = getConversionService().convert(payment, AuthorizationTransactionRequest.class);
		
		PaymentTransactionResponse response = getPaymentGatewayTransactionService().authorize(authorizationRequest, 
				billingAddressDto, shipmentDto, getPaymentGatewayPlugin());
		updateOrderPaymentWithResponse(response, payment);
	}

	@Override
	public void capture(final OrderPayment payment) {
		CaptureTransactionRequest captureTransactionRequest = getConversionService().convert(payment, CaptureTransactionRequest.class);
		
		CaptureTransactionResponse response = getPaymentGatewayTransactionService().capture(captureTransactionRequest, getPaymentGatewayPlugin());
		updateOrderPaymentWithResponse(response, payment);
	}

	@Override
	public void sale(final OrderPayment payment, final Address billingAddress) {
		SaleCapability saleCapability = getPaymentGatewayPlugin().getCapability(SaleCapability.class);
		if (saleCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support sale (single step auth-capture)");
		}

		OrderPaymentDto orderPaymentDto = getConversionService().convert(payment, OrderPaymentDto.class);
		AddressDto addressDto = null;
		if (billingAddress != null) {
			addressDto = getConversionService().convert(billingAddress, AddressDto.class);
		}

		OrderShipmentDto shipmentDto = null;
		if (payment.getOrderShipment() != null) {
			shipmentDto = getConversionService().convert(payment.getOrderShipment(), OrderShipmentDto.class);
		}

		saleCapability.sale(orderPaymentDto, addressDto, shipmentDto);
		updateOrderPaymentWithResponse(orderPaymentDto, payment);
	}

	@Override
	public void voidCaptureOrCredit(final OrderPayment payment) {
		VoidCaptureCapability voidCaptureCapability =
				getPaymentGatewayPlugin().getCapability(VoidCaptureCapability.class);
		if (voidCaptureCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support voiding captures or credits");
		}

		OrderPaymentDto orderPaymentDto = getConversionService().convert(payment, OrderPaymentDto.class);
		voidCaptureCapability.voidCaptureOrCredit(orderPaymentDto);
		updateOrderPaymentWithResponse(orderPaymentDto, payment);
	}

	@Override
	public void reversePreAuthorization(final OrderPayment payment) {
		ReversePreAuthorizationCapability reversePreAuthorizationCapability =
				getPaymentGatewayPlugin().getCapability(ReversePreAuthorizationCapability.class);
		if (reversePreAuthorizationCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support reversal of preauthorizations");
		}

		OrderPaymentDto orderPaymentDto = getConversionService().convert(payment, OrderPaymentDto.class);
		reversePreAuthorizationCapability.reversePreAuthorization(orderPaymentDto);
		updateOrderPaymentWithResponse(orderPaymentDto, payment);
	}
	
	@Override
	public boolean isPaymentGatewayPluginInstalled() {
		return getPaymentGatewayPlugin().isResolved();
	}
	
	
	private void updateOrderPaymentWithResponse(final PaymentTransactionResponse response, final OrderPayment payment) {
		if (response.getEmail() != null) {
			payment.setEmail(response.getEmail());
		}
		if (response.getReferenceId() != null) {
			payment.setReferenceId(response.getReferenceId());
		}
		if (response.getAuthorizationCode() != null) {
			payment.setAuthorizationCode(response.getAuthorizationCode());
		}
		if (response.getRequestToken() != null) {
			payment.setRequestToken(response.getRequestToken());
		}
	}

	@Override
	public PaymentOptionFormDescriptor buildExternalTokenAcquireRequest(final TokenAcquireTransactionRequest tokenAcquireTransactionRequest,
			final Address billingAddress, final String finishExternalAuthUrl, final String cancelExternalAuthUrl) {
		final ExternalTokenAcquireCapability externalTokenAcquireCapability = getPaymentGatewayPlugin().getCapability(
				ExternalTokenAcquireCapability.class);
		if (externalTokenAcquireCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support external token acquire");
		}
		AddressDto billingAddressDto = null;
		if (billingAddress != null) {
			billingAddressDto = getConversionService().convert(billingAddress, AddressDto.class);
		}
		return externalTokenAcquireCapability.buildExternalTokenAcquireRequest(tokenAcquireTransactionRequest, billingAddressDto,
				finishExternalAuthUrl, cancelExternalAuthUrl);
	}

	@Override
	public TokenAcquireTransactionResponse handleExternalTokenAcquireResponse(final Map<String, String> responseMap) {
		final ExternalTokenAcquireCapability externalTokenAcquireCapability = getPaymentGatewayPlugin().getCapability(
				ExternalTokenAcquireCapability.class);
		if (externalTokenAcquireCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support external token acquire");
		}
		return externalTokenAcquireCapability.handleExternalTokenAcquireResponse(responseMap);
	}

	@Override
	public PaymentOptionFormDescriptor buildExternalAuthRequest(final OrderPayment orderPayment, final Address billingAddress,
			final String redirectExternalAuthUrl, final String finishExternalAuthUrl, final String cancelExternalAuthUrl) {
		final ExternalAuthCapability externalAuthCapability = getPaymentGatewayPlugin().getCapability(ExternalAuthCapability.class);
		if (externalAuthCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support external authentication");
		}
		final AuthorizationTransactionRequest authorizationTransactionRequest = getConversionService().convert(orderPayment,
				AuthorizationTransactionRequest.class);
		final OrderShipmentDto orderShipmentDto = getConversionService().convert(orderPayment.getOrderShipment(), OrderShipmentDto.class);
		AddressDto billingAddressDto = null;
		if (billingAddress != null) {
			billingAddressDto = getConversionService().convert(billingAddress, AddressDto.class);
		}
		return externalAuthCapability.buildExternalAuthRequest(authorizationTransactionRequest, billingAddressDto,
				orderShipmentDto, redirectExternalAuthUrl, finishExternalAuthUrl, cancelExternalAuthUrl);
	}

	@Override
	public OrderPayment handleExternalAuthResponse(final PaymentType paymentType, final Map<String, String> responseMap) {
		final ExternalAuthCapability externalAuthCapability = getPaymentGatewayPlugin().getCapability(ExternalAuthCapability.class);
		if (externalAuthCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support external authentication");
		}
		final OrderPaymentDto orderPaymentDto = externalAuthCapability.handleExternalAuthResponse(responseMap);
		OrderPayment orderPayment = getConversionService().convert(orderPaymentDto, OrderPayment.class);
		orderPayment.setPaymentMethod(paymentType);
		return orderPayment;
	}
	
	@Override
	public PaymentOptionFormDescriptor prepareForRedirect(final PaymentType paymentType, final Map<String, String> responseMap) {
		final HostedPageAuthCapability hostedPageAuthCapability = getPaymentGatewayPlugin().getCapability(HostedPageAuthCapability.class);
		if (hostedPageAuthCapability == null) {
			throw new PaymentOperationNotSupportedException(getType() + " does not support hosted page authentication");
		}
		return hostedPageAuthCapability.prepareForRedirect(responseMap);
	}

	@Override
	@Transient
	public boolean supportsCapability(final Class<? extends PaymentGatewayCapability> capabilityClass) {
		return getPaymentGatewayPlugin().getCapability(capabilityClass) != null;
	}

	@Transient
	protected PaymentGatewayTransactionService getPaymentGatewayTransactionService() {
		return getBean(ContextIdNames.PAYMENT_GATEWAY_TRANSACTION_SERVICE);
	}

	@Transient
	protected ConversionService getConversionService() {
		return getBean(ContextIdNames.CONVERSION_SERVICE);
	}
}
