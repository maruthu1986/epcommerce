/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.support.impl;

import static java.util.Calendar.AUGUST;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.YEAR;
import static org.assertj.core.api.Assertions.assertThat;

import static com.elasticpath.persistence.support.OrderCriterion.ResultType.ENTITY;
import static com.elasticpath.service.search.query.SortOrder.ASCENDING;
import static com.elasticpath.service.search.query.SortOrder.DESCENDING;
import static com.elasticpath.service.search.query.StandardSortBy.CUSTOMER_NAME;
import static com.elasticpath.service.search.query.StandardSortBy.DATE;
import static com.elasticpath.service.search.query.StandardSortBy.ORDER_NUMBER;
import static com.elasticpath.service.search.query.StandardSortBy.STATUS;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.persistence.support.OrderCriterion.ResultType;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.EpInvalidOrderCriterionResultTypeException;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;

/**
 * Test <code>OrderCriterionImpl</code>.
 */
public class OrderCriterionImplTest {

	private static final int NO_3 = 3;
	private static final String SKUCODE = "SKUCODE";

	private OrderCriterionImpl orderCriterionImpl;

	private static final OrderStatus ORDER_STATUS = OrderStatus.CANCELLED;

	private static final OrderPaymentStatus PAYMENT_STATUS = OrderPaymentStatus.APPROVED;

	private static final OrderShipmentStatus SHIPMENT_STATUS = OrderShipmentStatus.CANCELLED;

	private static final String ZIPCODE = "zipcode";

	private static final String ZIPCODE_UC = "ZIPCODE";

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		this.orderCriterionImpl = new OrderCriterionImpl();
	}

	private String getOrder() {
		return "SELECT o FROM OrderImpl AS o";
	}

	private String getInnerJoinPayments() {
		return " JOIN o.orderPayments AS op";
	}

	private String getInnerJoinShipments() {
		return " JOIN o.shipments AS os";
	}

	private String getWhere() {
		return " WHERE";
	}

	private String getAnd() {
		return " AND";
	}

	private String getOrderStatusCondition(final int positionOfParam) {
		return " o.status = ?" + positionOfParam;
	}

	private String getPaymentStatusCondition(final int positionOfParam) {
		return " op.status = ?" + positionOfParam;
	}

	private String getShipmentStatusCondition(final int positionOfParam) {
		return " os.status = ?" + positionOfParam;
	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getStatusCriteria'.
	 */
	@Test
	public void testGetStatusCriteria() {
		final StringBuilder fullQuery = new StringBuilder();
		fullQuery.append(getOrder()).append(getInnerJoinPayments()).append(getInnerJoinShipments()).append(getWhere());
		fullQuery.append(getOrderStatusCondition(1))
			.append(getAnd()).append(getPaymentStatusCondition(2))
			.append(getAnd()).append(getShipmentStatusCondition(NO_3));

		final StringBuilder orderStatusQuery = new StringBuilder();
		orderStatusQuery.append(getOrder()).append(getWhere()).append(getOrderStatusCondition(1));

		final StringBuilder orderPaymentStatusQuery = new StringBuilder();
		orderPaymentStatusQuery.append(getOrder()).append(getInnerJoinPayments()).append(getWhere());
		orderPaymentStatusQuery.append(getOrderStatusCondition(1))
			.append(getAnd()).append(getPaymentStatusCondition(2));

		final StringBuilder orderShipmentStatusQuery = new StringBuilder();
		orderShipmentStatusQuery.append(getOrder()).append(getInnerJoinShipments()).append(getWhere());
		orderShipmentStatusQuery.append(getOrderStatusCondition(1))
			.append(getAnd()).append(getShipmentStatusCondition(2));

		final StringBuilder paymentStatusQuery = new StringBuilder();
		paymentStatusQuery.append(getOrder()).append(getInnerJoinPayments()).append(getWhere());
		paymentStatusQuery.append(getPaymentStatusCondition(1));

		final StringBuilder paymentShipmentStatusQuery = new StringBuilder();
		paymentShipmentStatusQuery.append(getOrder()).append(getInnerJoinPayments()).append(getInnerJoinShipments()).append(getWhere());
		paymentShipmentStatusQuery.append(getPaymentStatusCondition(1))
			.append(getAnd()).append(getShipmentStatusCondition(2));

		final StringBuilder shipmentStatusQuery = new StringBuilder();
		shipmentStatusQuery.append(getOrder()).append(getInnerJoinShipments()).append(getWhere());
		shipmentStatusQuery.append(getShipmentStatusCondition(1));

		final StringBuilder noStatusQuery = new StringBuilder();
		noStatusQuery.append(getOrder());

		CriteriaQuery result = orderCriterionImpl.getStatusCriteria(ORDER_STATUS, PAYMENT_STATUS, SHIPMENT_STATUS);
		assertThat(result.getQuery()).isEqualTo(fullQuery.toString());
		assertThat(result.getParameters())
			.containsExactly(ORDER_STATUS, PAYMENT_STATUS, SHIPMENT_STATUS);

		result = orderCriterionImpl.getStatusCriteria(ORDER_STATUS, null, null);
		assertThat(result.getQuery()).isEqualTo(orderStatusQuery.toString());
		assertThat(result.getParameters())
			.containsExactly(ORDER_STATUS);

		result = orderCriterionImpl.getStatusCriteria(ORDER_STATUS, PAYMENT_STATUS, null);
		assertThat(result.getQuery()).isEqualTo(orderPaymentStatusQuery.toString());
		assertThat(result.getParameters())
			.containsExactly(ORDER_STATUS, PAYMENT_STATUS);

		result = orderCriterionImpl.getStatusCriteria(ORDER_STATUS, null, SHIPMENT_STATUS);
		assertThat(result.getQuery()).isEqualTo(orderShipmentStatusQuery.toString());
		assertThat(result.getParameters())
			.containsExactly(ORDER_STATUS, SHIPMENT_STATUS);

		result = orderCriterionImpl.getStatusCriteria(null, PAYMENT_STATUS, null);
		assertThat(result.getQuery()).isEqualTo(paymentStatusQuery.toString());
		assertThat(result.getParameters())
			.containsExactly(PAYMENT_STATUS);

		result = orderCriterionImpl.getStatusCriteria(null, PAYMENT_STATUS, SHIPMENT_STATUS);
		assertThat(result.getQuery()).isEqualTo(paymentShipmentStatusQuery.toString());
		assertThat(result.getParameters())
			.containsExactly(PAYMENT_STATUS, SHIPMENT_STATUS);

		result = orderCriterionImpl.getStatusCriteria(null, null, SHIPMENT_STATUS);
		assertThat(result.getQuery()).isEqualTo(shipmentStatusQuery.toString());
		assertThat(result.getParameters())
			.containsExactly(SHIPMENT_STATUS);

		result = orderCriterionImpl.getStatusCriteria(null, null, null);
		assertThat(result.getQuery()).isEqualTo(noStatusQuery.toString());
		assertThat(result.getParameters())
			.isEmpty();
	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getOrderCustomerCriteria'.
	 */
	@Test
	public void testGetOrderCustomerCriteria() {
		//final String propertyName, final String criteriaValue, final boolean isExactMatch
		final String propertyName = "email";
		final String criteriaValue = "test";
		final String exactMatchQuery = "SELECT o FROM OrderImpl AS o WHERE o.customer." + propertyName + " = ?1";
		final String fuzzyMatchQuery = "SELECT o FROM OrderImpl AS o WHERE o.customer." + propertyName + " LIKE ?1";

		CriteriaQuery result = orderCriterionImpl.getOrderCustomerCriteria(propertyName, criteriaValue, true);
		assertThat(result.getQuery()).isEqualTo(exactMatchQuery);
		assertThat(result.getParameters()).containsExactly(criteriaValue);

		result = orderCriterionImpl.getOrderCustomerCriteria(propertyName, criteriaValue, false);
		assertThat(result.getQuery()).isEqualTo(fuzzyMatchQuery);
		assertThat(result.getParameters()).containsExactly("%" + criteriaValue + "%");
	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getAdvancedOrderCriteria'.
	 */
	@Test
	public void testGetOrderSearchCriteria() {
		final OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		Set<String> storeCodes = new HashSet<>();
		final String emptySearch = "SELECT o FROM OrderImpl AS o";

		CriteriaQuery result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ENTITY);
		assertThat(result.getQuery()).isEqualTo(emptySearch + " ORDER BY o.orderNumber DESC");

		orderSearchCriteria.setOrderToDate(new Date());
		final String expectedQuery1 = emptySearch + " WHERE o.createdDate <= ?1 ORDER BY o.orderNumber DESC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ENTITY);
		assertThat(result.getQuery()).isEqualTo(expectedQuery1);
		assertThat(result.getParameters())
			.containsExactly(orderSearchCriteria.getOrderToDate());

		orderSearchCriteria.setOrderStatus(OrderStatus.CANCELLED);
		Calendar from = new GregorianCalendar(YEAR, AUGUST, DAY_OF_MONTH);
		orderSearchCriteria.setOrderFromDate(from.getTime());
		final String expectedQuery2 = emptySearch
									  + " WHERE o.status = ?1 AND o.createdDate <= ?2 AND o.createdDate >= ?3 ORDER BY o.orderNumber DESC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ENTITY);
		assertThat(result.getQuery()).isEqualTo(expectedQuery2);
		assertThat(result.getParameters())
			.containsExactly(OrderStatus.CANCELLED, orderSearchCriteria.getOrderToDate(), from.getTime());

		orderSearchCriteria.clear();
		orderSearchCriteria.setSkuCode(SKUCODE);
		final String expectedQuery4 = emptySearch
			  + " JOIN o.shipments AS os JOIN os.shipmentOrderSkusInternal AS sku WHERE sku.skuCode = ?1 ORDER BY o.orderNumber DESC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ENTITY);
		assertThat(result.getQuery()).isEqualTo(expectedQuery4);
		assertThat(result.getParameters())
			.containsExactly(SKUCODE);

		orderSearchCriteria.clear();
		orderSearchCriteria.setSortingOrder(ASCENDING);
		orderSearchCriteria.setSortingType(CUSTOMER_NAME);
		orderSearchCriteria.setShipmentStatus(OrderShipmentStatus.RELEASED);
		final String expectedQuery5 = emptySearch + " JOIN o.shipments AS os WHERE os.status = ?1 ORDER BY o.billingAddress.firstName ASC,"
			  + " o.billingAddress.lastName ASC, o.orderNumber ASC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ENTITY);
		assertThat(result.getQuery()).isEqualTo(expectedQuery5);
		assertThat(result.getParameters())
			.containsExactly(OrderShipmentStatus.RELEASED);

		orderSearchCriteria.clear();
		orderSearchCriteria.setShipmentZipcode(ZIPCODE);
		final String expectedQuery6 = emptySearch + " WHERE UPPER(o.billingAddress.zipOrPostalCode) LIKE ?1 ORDER BY o.billingAddress.firstName ASC, "
			  + "o.billingAddress.lastName ASC, o.orderNumber ASC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ENTITY);
		assertThat(result.getQuery()).isEqualTo(expectedQuery6);
		assertThat(result.getParameters())
			.containsExactly("%" + ZIPCODE_UC + "%");

	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getAdvancedOrderCriteria'.
	 */
	@Test
	public void testGetOrderReturnSearchCriteria() {
		final OrderReturnSearchCriteria orderReturnSearchCriteria = new OrderReturnSearchCriteria();
		orderReturnSearchCriteria.setSortingOrder(ASCENDING);

		final String emptySearch = "SELECT ort FROM OrderReturnImpl AS ort";
		CriteriaQuery result = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(result.getQuery()).isEqualTo(emptySearch + " ORDER BY ort.rmaCode ASC");
		assertThat(result.getParameters()).isEmpty();

		final String rmaCode = "1";
		orderReturnSearchCriteria.setRmaCode(rmaCode);
		final String expectedQuery1 = emptySearch + " WHERE ort.rmaCode = ?1 ORDER BY ort.rmaCode ASC";
		result = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(result.getQuery()).isEqualTo(expectedQuery1);
		assertThat(result.getParameters())
			.containsExactly(rmaCode);

		String orderNumber = "100000";
		orderReturnSearchCriteria.setOrderNumber(orderNumber);
		final String expectedQuery2 = emptySearch + " JOIN ort.order AS o WHERE ort.rmaCode = ?1 AND o.orderNumber = ?2 ORDER BY ort.rmaCode ASC";
		result = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(result.getQuery()).isEqualTo(expectedQuery2);
		assertThat(result.getParameters())
			.containsExactly(rmaCode, orderNumber);

		String firstName = "firstName";
		CustomerSearchCriteria customerSearchCriteria = new CustomerSearchCriteria();
		customerSearchCriteria.setFirstName(firstName);
		orderReturnSearchCriteria.setCustomerSearchCriteria(customerSearchCriteria);
		final String expectedQuery3 = emptySearch
									  + " JOIN ort.order AS o WHERE ort.rmaCode = ?1 AND o.orderNumber = ?2 "
									  + "AND o.billingAddress.firstName LIKE ?3 ORDER BY ort.rmaCode ASC";
		result = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(result.getQuery()).isEqualTo(expectedQuery3);
		assertThat(result.getParameters())
			.containsExactly(rmaCode, orderNumber, "%" + firstName + "%");

		String lastName = "lastName";
		customerSearchCriteria.setLastName(lastName);
		final String expectedQuery4 = emptySearch
									  + " JOIN ort.order AS o WHERE ort.rmaCode = ?1 AND o.orderNumber = ?2 "
									  + "AND o.billingAddress.firstName LIKE ?3 AND o.billingAddress.lastName LIKE ?4 "
									  + "ORDER BY ort.rmaCode ASC";
		result = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(result.getQuery()).isEqualTo(expectedQuery4);
		assertThat(result.getParameters())
			.containsExactly(rmaCode, orderNumber, "%" + firstName + "%", "%" + lastName + "%");
	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getAdvancedOrderCriteria'.
	 */
	@Test
	public void testGetOrderReturnSearchCriteriaSorting() {
		final OrderReturnSearchCriteria orderReturnSearchCriteria = new OrderReturnSearchCriteria();
		orderReturnSearchCriteria.setSortingOrder(ASCENDING);

		//default rmaCode
		final String emptySearch = "SELECT ort FROM OrderReturnImpl AS ort";
		CriteriaQuery results = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(results.getQuery())
			.isEqualTo(emptySearch + " ORDER BY ort.rmaCode ASC");

		orderReturnSearchCriteria.setSortingOrder(DESCENDING);
		results = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(results.getQuery())
			.isEqualTo(emptySearch + " ORDER BY ort.rmaCode DESC");

		orderReturnSearchCriteria.setSortingType(ORDER_NUMBER);
		results = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(results.getQuery())
			.isEqualTo(emptySearch + " ORDER BY o.orderNumber DESC");

		orderReturnSearchCriteria.setSortingType(DATE);
		results = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(results.getQuery())
			.isEqualTo(emptySearch + " ORDER BY ort.createdDate DESC");

		orderReturnSearchCriteria.setSortingType(STATUS);
		results = orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ENTITY);
		assertThat(results.getQuery())
			.isEqualTo(emptySearch + " ORDER BY ort.returnStatus DESC");
	}

	/**
	 * Test method for 'OrderCriterionImpl.getOrderSearchCriteria(OrderSearchCriteria, List<Object>, Collection<String>, ResultType)'.
	 */
	@Test
	public void testGetSearchCriteriaForOrderNumber() {
		final OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		Set<String> storeCodes = new HashSet<>();
		final String emptySearch = "SELECT o.orderNumber FROM OrderImpl AS o";
		CriteriaQuery result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.ORDER_NUMBER);
		assertThat(result.getQuery()).isEqualTo(emptySearch + " ORDER BY o.orderNumber DESC");

		orderSearchCriteria.setOrderToDate(new Date());
		final String expectedQuery1 = emptySearch + " WHERE o.createdDate <= ?1 ORDER BY o.orderNumber DESC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.ORDER_NUMBER);
		assertThat(result.getQuery()).isEqualTo(expectedQuery1);
		assertThat(result.getParameters())
			.containsExactly(orderSearchCriteria.getOrderToDate());

		orderSearchCriteria.setOrderStatus(OrderStatus.CANCELLED);
		Calendar from = new GregorianCalendar(YEAR, AUGUST, DAY_OF_MONTH);
		orderSearchCriteria.setOrderFromDate(from.getTime());
		final String expectedQuery2 = emptySearch
			  + " WHERE o.status = ?1 AND o.createdDate <= ?2 AND o.createdDate >= ?3 ORDER BY o.orderNumber DESC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.ORDER_NUMBER);
		assertThat(result.getQuery()).isEqualTo(expectedQuery2);
		assertThat(result.getParameters())
			.containsExactly(OrderStatus.CANCELLED, orderSearchCriteria.getOrderToDate(), from.getTime());

		orderSearchCriteria.clear();
		orderSearchCriteria.setSkuCode(SKUCODE);
		final String expectedQuery4 = emptySearch
			  + " JOIN o.shipments AS os JOIN os.shipmentOrderSkusInternal AS sku WHERE sku.skuCode = ?1 ORDER BY o.orderNumber DESC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.ORDER_NUMBER);
		assertThat(result.getQuery()).isEqualTo(expectedQuery4);
		assertThat(result.getParameters())
			.containsExactly(SKUCODE);

		orderSearchCriteria.clear();
		orderSearchCriteria.setSortingOrder(ASCENDING);
		orderSearchCriteria.setSortingType(CUSTOMER_NAME);
		orderSearchCriteria.setShipmentStatus(OrderShipmentStatus.RELEASED);
		final String expectedQuery5 = emptySearch
			  + " JOIN o.shipments AS os WHERE os.status = ?1 ORDER BY o.billingAddress.firstName ASC,"
			  + " o.billingAddress.lastName ASC, o.orderNumber ASC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.ORDER_NUMBER);
		assertThat(result.getQuery()).isEqualTo(expectedQuery5);
		assertThat(result.getParameters())
			.containsExactly(OrderShipmentStatus.RELEASED);

		orderSearchCriteria.clear();
		orderSearchCriteria.setShipmentZipcode(ZIPCODE);
		final String expectedQuery6 = emptySearch + " WHERE UPPER(o.billingAddress.zipOrPostalCode) LIKE ?1 ORDER BY o.billingAddress.firstName ASC, "
			  + "o.billingAddress.lastName ASC, o.orderNumber ASC";
		result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.ORDER_NUMBER);
		assertThat(result.getQuery()).isEqualTo(expectedQuery6);
		assertThat(result.getParameters())
			.containsExactly("%" + ZIPCODE_UC + "%");

	}

	@Test
	public void testGetOrderSearchCriteriaWithCustomerName() {
		final OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria();
		final CustomerSearchCriteria customerSearchCriteria = new CustomerSearchCriteria();
		customerSearchCriteria.setFirstName("first");
		customerSearchCriteria.setLastName("last");
		orderSearchCriteria.setCustomerSearchCriteria(customerSearchCriteria);
		Set<String> storeCodes = new HashSet<>();

		final String emptySearch = "SELECT o.orderNumber FROM OrderImpl AS o";
		CriteriaQuery result = orderCriterionImpl.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.ORDER_NUMBER);
		assertThat(result.getQuery()).isEqualTo(emptySearch + " LEFT JOIN o.customer.profileValueMap AS cpf "
			+ "LEFT JOIN o.customer.profileValueMap AS cpl "
			+ "WHERE cpf.localizedAttributeKey = ?1 AND cpl.localizedAttributeKey = ?2 "
			+ "AND (UPPER(o.billingAddress.firstName) LIKE ?3 OR UPPER(cpf.shortTextValue) LIKE ?4) "
			+ "AND (UPPER(o.billingAddress.lastName) LIKE ?5 OR UPPER(cpl.shortTextValue) LIKE ?6) "
			+ "ORDER BY o.orderNumber DESC");

	}

	/**
	 * Test method for 'com.elasticpath.persistence.support.impl.OrderCriterionImpl.getOrderReturnSearchCriteria'.
	 */
	@Test(expected = EpInvalidOrderCriterionResultTypeException.class)
	public void testGetOrderReturnSearchCriteriaWithException() {
		final OrderReturnSearchCriteria orderReturnSearchCriteria = new OrderReturnSearchCriteria();
		orderReturnSearchCriteria.setSortingOrder(ASCENDING);

		orderCriterionImpl.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ResultType.ORDER_NUMBER);
	}
}
