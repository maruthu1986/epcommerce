/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.test.integration.datapolicy;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.attribute.CustomerProfileValue;
import com.elasticpath.domain.builder.OrderBuilder;
import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.builder.shopper.ShoppingContextBuilder;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.impl.DataPointImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.persister.Persister;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.datapolicy.DataPointLocationEnum;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

public class DataPointValueServiceImplTest extends AbstractDataPolicyTest {

	private static final String CUSTOMER_FIRST_NAME = "Shaun";
	private static final String CUSTOMER_LAST_NAME = "The Sheep";
	private static final String HYPHEN = "‐";

	@Autowired private DataPointValueService dataPointValueService;
	@Autowired private ShoppingCartService shoppingCartService;
	@Autowired private CheckoutTestCartBuilder checkoutTestCartBuilder;
	@Autowired private ShoppingContextBuilder shoppingContextBuilder;
	@Autowired private OrderBuilder orderBuilder;
	@Autowired private Persister<ShoppingContext> shoppingContextPersister;
	@Autowired private OrderService orderService;
	@Autowired private GiftCertificateService giftCertificateService;

	private SimpleStoreScenario scenario;
	private Customer customer;
	private ShoppingContext shoppingContext;

	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		customer = createPersistedCustomer(scenario.getStore().getCode(), "customer@xxx.com", false);
		customer.setFirstName(CUSTOMER_FIRST_NAME);
		customer.setLastName(CUSTOMER_LAST_NAME);

		customerService.update(customer);

		shoppingContext = shoppingContextBuilder
			.withCustomer(customer)
			.build();

		shoppingContextPersister.persist(shoppingContext);

		checkoutTestCartBuilder
			.withScenario(scenario)
			.withCustomer(customer)
			.withCustomerSession(shoppingContext.getCustomerSession());
	}

	//tests for readers

	@Test
	@DirtiesDatabase
	public void shouldGetDataPointValueForCustomerProfileEmail() {
		String dataPointKey = "CP_EMAIL";
		String expectedDataPointValue = "customer@xxx.com";

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = createMapWithDataPoints(DATA_POINT_NAME, dataPointKey,
			DataPointLocationEnum.CUSTOMER_PROFILE.getName());

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);

		assertActualDataPointValues(dataPointValues, expectedDataPointValue);
	}

	@Test
	@DirtiesDatabase
	public void shouldGetDataPointValueForCartGiftCertificateRecipientName() {
		String recipientName = "Recipient";
		String dataPointKey = "giftCertificate.recipientName";
		String expectedDataPointValue = recipientName;

		ShoppingCart cart = checkoutTestCartBuilder
			.withGiftCertificateProduct("Sender", recipientName, "email@xx.com" )
			.withCustomerSession(shoppingContext.getCustomerSession())
			.build();

		shoppingCartService.saveOrUpdate(cart);

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = createMapWithDataPoints(DATA_POINT_NAME, dataPointKey,
			DataPointLocationEnum.CART_GIFT_CERTIFICATE.getName());

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);

		assertActualDataPointValues(dataPointValues, expectedDataPointValue);
	}

	@Test
	@DirtiesDatabase
	public void shouldGetDataPointValueForCustomerBillingAddressFirstName() {
		String dataPointKey = "FIRST_NAME";
		String expectedDataPointValue = CUSTOMER_FIRST_NAME;

		CustomerAddress custBillingAddress = new CustomerAddressImpl();
		fillAddressInstance(custBillingAddress);

		customer.setPreferredBillingAddress(custBillingAddress);
		customerService.update(customer);

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = createMapWithDataPoints(DATA_POINT_NAME, dataPointKey,
			DataPointLocationEnum.CUSTOMER_BILLING_ADDRESS.getName());

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);

		assertActualDataPointValues(dataPointValues, expectedDataPointValue);
	}

	@Test
	@DirtiesDatabase
	public void shouldGetDataPointValueForCustomerShippingAddressLastName() {

		String dataPointKey = "LAST_NAME";
		String expectedDataPointValue = CUSTOMER_LAST_NAME;

		CustomerAddress custShippingAddress = new CustomerAddressImpl();
		fillAddressInstance(custShippingAddress);

		customer.setPreferredShippingAddress(custShippingAddress);
		customerService.update(customer);

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = createMapWithDataPoints(DATA_POINT_NAME, dataPointKey,
			DataPointLocationEnum.CUSTOMER_SHIPPING_ADDRESS.getName());

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);

		assertActualDataPointValues(dataPointValues, expectedDataPointValue);
	}

	@Test
	@DirtiesDatabase
	public void shouldGetDataPointValueForOrderBillingAddressPhoneNumber() {
		String dataPointKey = "PHONE_NUMBER";
		String expectedDataPointValue = "12345678";

		Order order = createOrderWithGiftCertificateProduct(false);

		OrderAddress orderAddress = new OrderAddressImpl();
		fillAddressInstance(orderAddress);

		order.setBillingAddress(orderAddress);
		orderService.update(order);

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = createMapWithDataPoints(DATA_POINT_NAME, dataPointKey,
			DataPointLocationEnum.ORDER_BILLING_ADDRESS.getName());

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);

		assertActualDataPointValues(dataPointValues, expectedDataPointValue);
	}

	@Test
	@DirtiesDatabase
	public void shouldGetDataPointValueForOrderIPAddress() {
		String dataPointKey = "ORDER_IP_ADDRESS";
		String expectedDataPointValue = "127.0.0.1";

		Order order = createOrderWithGiftCertificateProduct(false);

		order.setIpAddress(expectedDataPointValue);
		orderService.update(order);

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = createMapWithDataPoints(DATA_POINT_NAME, dataPointKey,
			DataPointLocationEnum.ORDER_IP_ADDRESS.getName());

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);

		assertActualDataPointValues(dataPointValues, expectedDataPointValue);
	}

	@Test
	@DirtiesDatabase
	public void shouldGetDataPointValueForOrderData() {
		String dataPointKey = "ORDER_DATA_KEY";
		String expectedDataPointValue = "Order Data Value";

		Order order = createOrderWithGiftCertificateProduct(false);

		order.setFieldValue(dataPointKey, expectedDataPointValue);
		orderService.update(order);

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = createMapWithDataPoints(DATA_POINT_NAME, dataPointKey,
			DataPointLocationEnum.ORDER_DATA.getName());

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);

		assertActualDataPointValues(dataPointValues, expectedDataPointValue);
	}


	@Test
	@DirtiesDatabase
	public void shouldGetDataPointValueForOrderGiftCertificateRecipientEmail() {
		String dataPointKey = "giftCertificate.recipientEmail";

		//given an order with a gift certificate product
		createOrderWithGiftCertificateProduct(true);

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = createMapWithDataPoints(DATA_POINT_NAME, dataPointKey,
			DataPointLocationEnum.ORDER_GIFT_CERTIFICATE.getName());

		//when getting data point value for order gift certificate recipient email
		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);

		assertThat(dataPointValues)
			.hasSize(1);

		assertThat(dataPointValues)
			.allMatch(dpv ->
				dpv.getValue().matches("giftcertificate[.]recipient.*?@elasticpath[.]com"));
	}

	@Test
	@DirtiesDatabase
	public void shouldGetDataPointValueForOrderPaymentGiftCertificateSenderName() {
		String dataPointKey = "SENDER_NAME";
		String expectedDataPointValue = customer.getFullName();

		//given an order with a gift certificate product
		createOrderWithGiftCertificateProduct(true);

		Map<String, Collection<DataPoint>> customerGuidToDataPoints = createMapWithDataPoints(DATA_POINT_NAME, dataPointKey,
			DataPointLocationEnum.ORDER_PAYMENT_GIFT_CERTIFICATE.getName());

		//when getting data point value for order gift certificate sender name
		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerGuidToDataPoints);

		//then the returned value should be customer's full name
		assertActualDataPointValues(dataPointValues, expectedDataPointValue);
	}

	//tests for removers

	@Test
	@DirtiesDatabase
	public void shouldDeleteCustomerProfileEmail() {

		String dataPointKey = "CP_EMAIL";
		String expectedDataPointValue = "customer@xxx.com";

		CustomerProfileValue cpvEmail = customer.getProfileValueMap().get(dataPointKey);

		DataPointValue dataPointValue = createDataPointValueForRemoval(cpvEmail.getUidPk(),
			DataPointLocationEnum.CUSTOMER_PROFILE.getName(), dataPointKey, null);

		assertThat(customer.getEmail())
			.as("The current and expected data point values must be equal")
			.isEqualTo(expectedDataPointValue);

		int numOfDeleteValues = dataPointValueService.removeValues(Collections.singletonList(dataPointValue));

		assertThat(numOfDeleteValues)
			.as("The data point values must be deleted")
			.isEqualTo(1);

		Customer updatedCustomer = customerService.findByGuid(customer.getGuid());

		assertThat(updatedCustomer.getEmail())
			.as("The field must be null")
			.isNull();
	}

	@Test
	@DirtiesDatabase
	public void shouldDeleteCartGiftCertificateRecipientName() {
		String expectedDataPointValue = "Recipient";
		String dataPointKey = "giftCertificate.recipientName";

		ShoppingCart cart = checkoutTestCartBuilder
			.withGiftCertificateProduct("Sender", expectedDataPointValue, "email@xx.com" )
			.withCustomerSession(shoppingContext.getCustomerSession())
			.build();

		shoppingCartService.saveOrUpdate(cart);

		DataPointValue dataPointValue = createDataPointValueForRemoval(200002L,
			DataPointLocationEnum.CART_GIFT_CERTIFICATE.getName(), dataPointKey, null);

		assertThat(getCartGiftCertificateFieldValue(cart, dataPointKey))
			.as("The current and expected data point values must be equal")
			.isEqualTo(expectedDataPointValue);

		int numOfDeleteValues = dataPointValueService.removeValues(Collections.singletonList(dataPointValue));

		assertThat(numOfDeleteValues)
			.as("The data point values must be deleted")
			.isEqualTo(1);

		ShoppingCart updatedCart = shoppingCartService.findOrCreateByCustomerSession(shoppingContext.getCustomerSession());

		assertThat(getCartGiftCertificateFieldValue(updatedCart,dataPointKey))
			.as("The field must be null")
			.isNull();
	}

	@Test
	@DirtiesDatabase
	public void shouldDeleteCustomerBillingAddressFirstName() {
		String dataPointKey = "FIRST_NAME";
		String expectedDataPointValue = CUSTOMER_FIRST_NAME;

		CustomerAddress custBillingAddress = new CustomerAddressImpl();
		fillAddressInstance(custBillingAddress);

		customer.setPreferredBillingAddress(custBillingAddress);
		customerService.update(customer);

		assertThat(customer.getPreferredBillingAddress().getFirstName())
			.as("The current and expected data point values must be equal")
			.isEqualTo(expectedDataPointValue);

		DataPointValue dataPointValue = createDataPointValueForRemoval(customer.getUidPk(),
			DataPointLocationEnum.CUSTOMER_BILLING_ADDRESS.getName(), dataPointKey, "firstName");

		int numOfDeleteValues = dataPointValueService.removeValues(Collections.singletonList(dataPointValue));

		assertThat(numOfDeleteValues)
			.as("The data point values must be deleted")
			.isEqualTo(1);

		Customer updatedCustomer = customerService.findByGuid(customer.getGuid());

		assertThat(updatedCustomer.getPreferredBillingAddress().getFirstName())
			.as("The field must be empty")
			.isEqualTo(HYPHEN);
	}

	@Test
	@DirtiesDatabase
	public void shouldDeleteCustomerShippingAddressLastName() {
		String dataPointKey = "LAST_NAME";
		String expectedDataPointValue = CUSTOMER_LAST_NAME;

		CustomerAddress custShippingAddress = new CustomerAddressImpl();
		fillAddressInstance(custShippingAddress);

		customer.setPreferredShippingAddress(custShippingAddress);
		customerService.update(customer);

		assertThat(customer.getPreferredShippingAddress().getLastName())
			.as("The current and expected data point values must be equal")
			.isEqualTo(expectedDataPointValue);

		DataPointValue dataPointValue = createDataPointValueForRemoval(customer.getUidPk(),
			DataPointLocationEnum.CUSTOMER_SHIPPING_ADDRESS.getName(), dataPointKey, "lastName");

		int numOfDeleteValues = dataPointValueService.removeValues(Collections.singletonList(dataPointValue));

		assertThat(numOfDeleteValues)
			.as("The data point values must be deleted")
			.isEqualTo(1);

		Customer updatedCustomer = customerService.findByGuid(customer.getGuid());

		assertThat(updatedCustomer.getPreferredShippingAddress().getLastName())
			.as("The field must be empty")
			.isEqualTo(HYPHEN);
	}

	@Test
	@DirtiesDatabase
	public void shouldDeleteOrderBillingAddressPhoneNumber() {
		String dataPointKey = "PHONE_NUMBER";
		String expectedDataPointValue = "12345678";

		Order order = createOrderWithGiftCertificateProduct(false);

		OrderAddress orderAddress = new OrderAddressImpl();
		fillAddressInstance(orderAddress);

		order.setBillingAddress(orderAddress);
		order = orderService.update(order);

		assertThat(order.getBillingAddress().getPhoneNumber())
			.as("The current and expected data point values must be equal")
			.isEqualTo(expectedDataPointValue);

		DataPointValue dataPointValue = createDataPointValueForRemoval(order.getBillingAddress().getUidPk(),
			DataPointLocationEnum.ORDER_BILLING_ADDRESS.getName(), dataPointKey, "phoneNumber");

		int numOfDeleteValues = dataPointValueService.removeValues(Collections.singletonList(dataPointValue));

		assertThat(numOfDeleteValues)
			.as("The data point values must be deleted")
			.isEqualTo(1);

		Order updatedOrder = orderService.findOrderByOrderNumber(order.getOrderNumber());

		assertThat(updatedOrder.getBillingAddress().getPhoneNumber())
			.as("The field must be empty")
			.isEqualTo(HYPHEN);
	}

	@Test
	@DirtiesDatabase
	public void shouldDeleteForOrderIPAddress() {
		String dataPointKey = "ORDER_IP_ADDRESS";
		String expectedDataPointValue = "127.0.0.1";

		Order order = createOrderWithGiftCertificateProduct(false);

		order.setIpAddress(expectedDataPointValue);
		order = orderService.update(order);

		assertThat(order.getIpAddress())
			.as("The current and expected data point values must be equal")
			.isEqualTo(expectedDataPointValue);

		DataPointValue dataPointValue = createDataPointValueForRemoval(order.getUidPk(),
			DataPointLocationEnum.ORDER_IP_ADDRESS.getName(), dataPointKey, null);

		int numOfDeleteValues = dataPointValueService.removeValues(Collections.singletonList(dataPointValue));

		assertThat(numOfDeleteValues)
			.as("The data point values must be deleted")
			.isEqualTo(1);

		Order updatedOrder = orderService.findOrderByOrderNumber(order.getOrderNumber());

		assertThat(updatedOrder.getIpAddress())
			.as("The field must be empty")
			.isEqualTo(HYPHEN);
	}

	@Test
	@DirtiesDatabase
	public void shouldDeleteOrderData() {
		String dataPointKey = "ORDER_DATA_KEY";
		String expectedDataPointValue = "Order Data Value";

		Order order = createOrderWithGiftCertificateProduct(false);

		order.setFieldValue(dataPointKey, expectedDataPointValue);
		order = orderService.update(order);

		assertThat(order.getFieldValue(dataPointKey))
			.as("The current and expected data point values must be equal")
			.isEqualTo(expectedDataPointValue);

		DataPointValue dataPointValue = createDataPointValueForRemoval(1L,
			DataPointLocationEnum.ORDER_DATA.getName(), dataPointKey, null);

		int numOfDeleteValues = dataPointValueService.removeValues(Collections.singletonList(dataPointValue));

		assertThat(numOfDeleteValues)
			.as("The data point values must be deleted")
			.isEqualTo(1);

		Order updatedOrder = orderService.findOrderByOrderNumber(order.getOrderNumber());

		assertThat(updatedOrder.getFieldValue(dataPointKey))
			.as("The field must be null")
			.isNull();
	}


	@Test
	@DirtiesDatabase
	public void shouldDeleteOrderGiftCertificateSenderName() {
		String dataPointKey = "giftCertificate.senderName";
		String expectedDataPointValue = customer.getFullName();

		//given an order with a gift certificate product
		Order createdOrder = createOrderWithGiftCertificateProduct(true);
		String orderGiftCertificateSenderName = createdOrder.getAllShipments().get(0).getShipmentOrderSkus().iterator().next().getFieldValue(dataPointKey);

		assertThat(orderGiftCertificateSenderName)
			.as("The current and expected data point values must be equal")
			.isEqualTo(expectedDataPointValue);

		DataPoint dataPoint = new DataPointImpl();
		dataPoint.setDataKey(dataPointKey);
		dataPoint.setDataLocation(DataPointLocationEnum.ORDER_GIFT_CERTIFICATE.getName());

		Map<String, Collection<DataPoint>> customerDataPoints = new HashMap<>(1);
		customerDataPoints.put(customer.getGuid(), Collections.singletonList(dataPoint));

		Collection<DataPointValue> dataPointValues = dataPointValueService.getValues(customerDataPoints);
		DataPointValue dataPointValueToDelete = dataPointValues.iterator().next();

		int numOfDeleteValues = dataPointValueService.removeValues(Collections.singletonList(dataPointValueToDelete));

		assertThat(numOfDeleteValues)
			.as("The data point values must be deleted")
			.isEqualTo(1);

		createdOrder = orderService.findOrderByOrderNumber(createdOrder.getOrderNumber());
		orderGiftCertificateSenderName = createdOrder.getAllShipments().get(0).getShipmentOrderSkus().iterator().next().getFieldValue(dataPointKey);

		assertThat(orderGiftCertificateSenderName)
			.as("The field must be null")
			.isNull();
	}

	@Test
	@DirtiesDatabase
	public void shouldDeleteOrderPaymentGiftCertificateSenderName() {
		String dataPointKey = "SENDER_NAME";
		String expectedDataPointValue = customer.getFullName();
		List<Long> gcUIdPKs = Collections.singletonList(200000L);

		//given an order with a gift certificate product
		createOrderWithGiftCertificateProduct(true);

		GiftCertificate giftCertificate = giftCertificateService.findByUids(gcUIdPKs).get(0);

		assertThat(giftCertificate.getSenderName())
			.as("The current and expected data point values must be equal")
			.isEqualTo(expectedDataPointValue);

		DataPointValue dataPointValue = createDataPointValueForRemoval(giftCertificate.getUidPk(),
			DataPointLocationEnum.ORDER_PAYMENT_GIFT_CERTIFICATE.getName(), dataPointKey, "senderName");

		int numOfDeleteValues = dataPointValueService.removeValues(Collections.singletonList(dataPointValue));

		assertThat(numOfDeleteValues)
			.as("The data point values must be deleted")
			.isEqualTo(1);

		giftCertificate = giftCertificateService.findByUids(gcUIdPKs).get(0);

		assertThat(giftCertificate.getSenderName())
			.as("The field must be empty")
			.isEqualTo(HYPHEN);
	}

	private DataPointValue createDataPointValueForRemoval(final Long uidPk, final String dataPointLocation, final String dataPointKey, final String
		fieldName) {

		DataPointValue dataPointValue = new DataPointValue();
		dataPointValue.setUidPk(uidPk);
		dataPointValue.setLocation(dataPointLocation);
		dataPointValue.setKey(dataPointKey);
		dataPointValue.setField(fieldName);

		return dataPointValue;
	}
	private String getCartGiftCertificateFieldValue(final ShoppingCart cart, final String fieldName) {
		return cart.getRootShoppingItems().iterator().next().getFieldValue(fieldName);
	}

	private void fillAddressInstance(final Address addressInstance) {

		addressInstance.setCountry("CA");
		addressInstance.setSubCountry("AB");
		addressInstance.setCity("City");
		addressInstance.setFirstName(customer.getFirstName());
		addressInstance.setLastName(customer.getLastName());
		addressInstance.setZipOrPostalCode("H0H0H0");
		addressInstance.setStreet1("Elm street");
		addressInstance.setGuid(UUID.randomUUID().toString());
		addressInstance.setCreationDate(new Date());
		addressInstance.setLastModifiedDate(new Date());
		addressInstance.setPhoneNumber("12345678");
	}


	private Order createOrderWithGiftCertificateProduct(final boolean toCheckout) {
		OrderPayment orderPayment = new OrderPaymentImpl();

		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		orderPayment.setAmount(BigDecimal.valueOf(1223L));
		orderPayment.setCreatedDate(new Date());
		orderPayment.setStatus(OrderPaymentStatus.APPROVED);
		orderPayment.setEmail("xx@email.com");

		OrderBuilder gcOrderBuilder =  orderBuilder.withCheckoutTestCartBuilder(checkoutTestCartBuilder)
			.withShoppingContext(shoppingContext)
			.withGiftCertificateProduct()
			.withTemplateOrderPayment(orderPayment);

		return toCheckout ? gcOrderBuilder.checkout() : gcOrderBuilder.build();
	}

	private Map<String, Collection<DataPoint>> createMapWithDataPoints(
			final String dataPointName, final String dataPointKey, final String dataPointLocation) {
		DataPoint dataPoint = createDataPoint(dataPointName, dataPointKey, dataPointLocation);
		Map<String, Collection<DataPoint>> customerGuidToDataPoints = new HashMap<>(1);
		customerGuidToDataPoints.put(customer.getGuid(), Collections.singletonList(dataPoint));

		return customerGuidToDataPoints;
	}

	private void assertActualDataPointValues(final Collection<DataPointValue> dataPointValues, final String expectedValue) {
		assertThat(dataPointValues)
			.hasSize(1);

		assertThat(dataPointValues.iterator().next().getValue())
			.isEqualTo(expectedValue);
	}
}
