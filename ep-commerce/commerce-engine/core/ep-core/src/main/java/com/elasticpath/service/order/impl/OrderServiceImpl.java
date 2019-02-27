/*
 * Copyright (c) Elastic Path Software Inc., 2006-2014
 */
package com.elasticpath.service.order.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.core.messaging.order.OrderEventType;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.EventOriginatorType;
import com.elasticpath.domain.event.OrderEventHelper;
import com.elasticpath.domain.order.AllocationEventType;
import com.elasticpath.domain.order.AllocationResult;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.PurchaseHistorySearchCriteria;
import com.elasticpath.domain.order.impl.AbstractOrderShipmentImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.OrderCriterion;
import com.elasticpath.persistence.support.OrderCriterion.ResultType;
import com.elasticpath.persistence.support.impl.CriteriaQuery;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchMode;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.order.CompleteShipmentFailedException;
import com.elasticpath.service.order.IncorrectRefundAmountException;
import com.elasticpath.service.order.OrderLockService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.order.OrderShipmentNotFoundException;
import com.elasticpath.service.order.ReleaseShipmentFailedException;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxDocumentModificationContext;
import com.elasticpath.service.tax.TaxDocumentModificationItem;
import com.elasticpath.service.tax.TaxDocumentModificationType;
import com.elasticpath.service.tax.TaxOperationService;

/**
 * Provides storage and access to <code>Order</code> objects.
 */
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.GodClass" })
public class OrderServiceImpl extends AbstractEpPersistenceServiceImpl implements OrderService {

	private static final long MINUTE_IN_MS = 60L * 1000L; // 60 sec * 1000 msec

	private static final Logger LOG = Logger.getLogger(OrderServiceImpl.class);

	private FetchPlanHelper fetchPlanHelper;

	private TimeService timeService;

	private PaymentService paymentService;

	private AllocationService allocationService;

	private LoadTuner defaultLoadTuner;

	private RuleService ruleService;

	private StoreService storeService;

	private TaxOperationService taxOperationService;

	private EventMessageFactory eventMessageFactory;

	private EventMessagePublisher eventMessagePublisher;

	/**
	 * Adds the given order.
	 *
	 * @param order the order to add
	 * @return the persisted instance of order
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Order add(final Order order) throws EpServiceException {
		sanityCheck();
		order.setLastModifiedDate(timeService.getCurrentTime());
		getPersistenceEngine().save(order);

		return populateRelationships(order);
	}

	/**
	 * Updates the given order.
	 *
	 * @param order the order to update
	 * @return the persisted instance of order
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	@SuppressWarnings("deprecation")
	public Order update(final Order order) throws EpServiceException {
		sanityCheck();
		order.setLastModifiedDate(timeService.getCurrentTime());

		// Log payment captured event
		for (final OrderPayment orderPayment : order.getOrderPayments()) {
			if (!orderPayment.isPersisted()) {
				logOrderPaymentEvents(order, orderPayment);
			}
		}
		Order persistedOrder = getPersistenceEngine().merge(order);
		// Avoid using storeService to avoid flushing the OpenJPA Context
		if (order.getStore() == null) {
			persistedOrder.setStore(getStore(order));
		} else {
			persistedOrder.setStore(order.getStore());
		}

		return persistedOrder;
	}

	@Override
	public Order update(final Order order, final TaxDocumentModificationContext taxDocumentModificationContext) {

		// update the order shipment document id as needed
		for (TaxDocumentModificationItem item : taxDocumentModificationContext.get(TaxDocumentModificationType.UPDATE)) {
			OrderShipment orderShipment = order.getShipment(item.getTaxDocumentReferenceId());
			orderShipment.resetTaxDocumentId();
		}
		// update the order with its shipments
		Order updatedOrder = update(order);

		// update the order shipment' tax documents
		getTaxOperationService().updateTaxes(updatedOrder, taxDocumentModificationContext);

		return updatedOrder;
	}

	private void logOrderPaymentEvents(final Order order, final OrderPayment orderPayment) {
		if (StringUtils.equals(orderPayment.getTransactionType(), OrderPayment.CAPTURE_TRANSACTION)) {
			getOrderEventHelper().logOrderPaymentCaptured(order, orderPayment);
		} else if (StringUtils.equals(orderPayment.getTransactionType(), OrderPayment.CREDIT_TRANSACTION)) {
			getOrderEventHelper().logOrderPaymentRefund(order, orderPayment);
		}
	}

	/**
	 * Retrieve the list of orders with the specified statuses.
	 *
	 * @param orderStatus the status of the order
	 * @param paymentStatus the status of the payment
	 * @param shipmentStatus the status of the shipment
	 * @return the list of orders with the specified statuses
	 */
	@Override
	public List<Order> findOrderByStatus(final OrderStatus orderStatus, final OrderPaymentStatus paymentStatus,
			final OrderShipmentStatus shipmentStatus) {
		sanityCheck();
		//determine parameters List
		List<Object> params = new LinkedList<>();
		if (orderStatus != null) {
			params.add(orderStatus);
		}
		if (paymentStatus != null) {
			params.add(paymentStatus);
		}
		if (shipmentStatus != null) {
			params.add(shipmentStatus);
		}
		if (params.isEmpty()) {
			throw new IllegalArgumentException("No status criteria provided. If you need all orders in the system use list() method instead");
		}

		prepareFetchPlan();

		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		final CriteriaQuery statusCriteria = orderCriterion.getStatusCriteria(orderStatus, paymentStatus, shipmentStatus);
		List<Order> orders = getPersistenceEngine().retrieve(statusCriteria.getQuery(), statusCriteria.getParameters().toArray());
		fetchPlanHelper.clearFetchPlan();

		return populateRelationships(orders);
	}

	/**
	 * Retrieves list of <code>Order</code> where the created date is later than the specified date.
	 *
	 * @param date date to compare with the created date
	 * @return list of <code>Order</code> whose created date is later than the specified date
	 */
	@Override
	public List<Order> findByCreatedDate(final Date date) {
		sanityCheck();
		prepareFetchPlan();
		final List<Order> orders = getPersistenceEngine().retrieveByNamedQuery("ORDER_SELECT_BY_CREATED_DATE", date);
		fetchPlanHelper.clearFetchPlan();
		return populateRelationships(orders);
	}

	/**
	 * Retrieve the list of orders, whose specified property matches the given criteria value.
	 *
	 * @param propertyName order property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return list of orders matching the given criteria.
	 */
	@Override
	public List<Order> findOrder(final String propertyName, final String criteriaValue, final boolean isExactMatch) {
		if (propertyName == null || propertyName.length() == 0) {
			throw new EpServiceException("propertyName not set");
		}
		prepareFetchPlan();
		List<Order> orders;
		if (StringUtils.isNotBlank(criteriaValue)) {
			sanityCheck();
			if (isExactMatch) {
				orders = getPersistenceEngine().retrieve("SELECT o FROM OrderImpl " + "as o WHERE o." + propertyName + " = ?1", criteriaValue);
			} else {
				orders = getPersistenceEngine().retrieve("SELECT o FROM OrderImpl as o WHERE o." + propertyName + " LIKE ?1",
						"%" + criteriaValue + "%");
			}
		} else {
			orders = Collections.emptyList();
		}
		fetchPlanHelper.clearFetchPlan();
		return populateRelationships(orders);
	}

	/**
	 * Retrieve the list of orders by the customer's guid. This will exclude failed orders. Use findOrdersBySearchCriteria()
	 * to retrieve the list including failed.
	 *
	 * @param customerGuid the customer's guid
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return list of orders matching the customer's guid.
	 */
	@Override
	public List<Order> findOrderByCustomerGuid(final String customerGuid, final boolean isExactMatch) {
		sanityCheck();
		prepareFetchPlan();

		CustomerSearchCriteria customerSearchCriteria = getBean(ContextIdNames.CUSTOMER_SEARCH_CRITERIA);
		customerSearchCriteria.setGuid(customerGuid);
		customerSearchCriteria.setFuzzySearchDisabled(isExactMatch);

		OrderSearchCriteria orderSearchCriteria = getBean(ContextIdNames.ORDER_SEARCH_CRITERIA);
		orderSearchCriteria.setCustomerSearchCriteria(customerSearchCriteria);
		orderSearchCriteria.setExcludedOrderStatus(OrderStatus.FAILED);

		final List<Order> orderList = findOrdersBySearchCriteria(orderSearchCriteria, 0, Integer.MAX_VALUE, null);
		fetchPlanHelper.clearFetchPlan();
		return orderList;
	}

	@Override
	public List<Order> findOrdersByCustomerGuidAndStoreCode(final String customerGuid, final String storeCode, final boolean retrieveFullInfo) {
		sanityCheck();

		if (retrieveFullInfo) {
			this.prepareFetchPlan();
		} else {
			FetchGroupLoadTuner loadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
			loadTuner.addFetchGroup(FetchGroupConstants.ORDER_LIST_BASIC);
			fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		}

		final List<Order> orderList = getPersistenceEngine().retrieveByNamedQuery("ORDER_SELECT_BY_CUSTOMER_GUID_AND_STORECODE",
				customerGuid,
				storeCode);

		fetchPlanHelper.clearFetchPlan();
		return populateRelationships(orderList);
	}

	/**
	 * Retrieve the list of orders by the gift certificate code.
	 *
	 * @param giftCertificateCode the gift certificate code
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return list of orders matching the gift certificate code.
	 */
	@Override
	public List<Order> findOrderByGiftCertificateCode(final String giftCertificateCode, final boolean isExactMatch) {
		sanityCheck();
		prepareFetchPlan();
		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		final List<Order> orderList = getPersistenceEngine().retrieve(
				orderCriterion.getOrderGiftCertificateCriteria("giftCertificateCode", giftCertificateCode, isExactMatch).getQuery());
		fetchPlanHelper.clearFetchPlan();
		return populateRelationships(orderList);
	}

	/**
	 * Retrieve the list of orders by the customer's email address.
	 *
	 * @param customerEmail the customer's email address.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return list of orders matching the customer's email address.
	 */
	@Override
	public List<Order> findOrderByCustomerEmail(final String customerEmail, final boolean isExactMatch) {
		sanityCheck();
		prepareFetchPlan();
		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		final CriteriaQuery query = orderCriterion.getOrderCustomerProfileCriteria("CP_EMAIL", customerEmail, isExactMatch);
		final List<Order> orderList = getPersistenceEngine().retrieve(query.getQuery(), query.getParameters().toArray());
		fetchPlanHelper.clearFetchPlan();
		return populateRelationships(orderList);
	}

	/**
	 * order count function based on the OrderSearchCriteria.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @return the count of orders matching the given criteria.
	 */
	@Override
	public long getOrderCountBySearchCriteria(final OrderSearchCriteria orderSearchCriteria) {
		sanityCheck();
		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		Collection<String> storeCodes = new LinkedList<>();
		CriteriaQuery query = orderCriterion.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.COUNT);
		List<Long> orderCount = null;

		FetchGroupLoadTuner loadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.ORDER_SEARCH);
		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		fetchPlanHelper.setFetchMode(FetchMode.JOIN);

		if (query.getParameters().isEmpty() && storeCodes.isEmpty()) {
			orderCount = getPersistenceEngine().retrieve(query.getQuery());
		} else if (storeCodes.isEmpty()) {
			orderCount = getPersistenceEngine().retrieve(query.getQuery(), query.getParameters().toArray());
		} else {
			orderCount = getPersistenceEngine().retrieveWithList(query.getQuery(), "storeList", storeCodes,
				query.getParameters().toArray(), 0, Integer.MAX_VALUE);
		}

		fetchPlanHelper.clearFetchPlan();

		return orderCount.get(0);
	}

	/**
	 * Order search function based on the OrderSearchCriteria. This uses the ORDER_SEARCH fetch group
	 * load tuner and forces JOIN fetch mode.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @param start the starting record to search
	 * @param maxResults the max results to be returned
	 * @return the list of orders matching the given criteria.
	 */
	@Override
	public List<Order> findOrdersBySearchCriteria(final OrderSearchCriteria orderSearchCriteria,
													final int start,
													final int maxResults) {
		sanityCheck();

		FetchGroupLoadTuner loadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.ORDER_SEARCH);
		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
		fetchPlanHelper.setFetchMode(FetchMode.JOIN);

		List<Order> orderList = findOrdersBySearchCriteria(orderSearchCriteria, start, maxResults, null);

		fetchPlanHelper.clearFetchPlan();

		return orderList;
	}

	/**
	 * Find orders by search criteria using the given load tuner.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @param start the starting record to search
	 * @param maxResults the max results to be returned
	 * @param loadTuner the load tuner
	 * @return the list of orders matching the given criteria.
	 */
	@Override
	public List<Order> findOrdersBySearchCriteria(final OrderSearchCriteria orderSearchCriteria, final int start, final int maxResults,
			final LoadTuner loadTuner) {

		if (loadTuner != null) {
			fetchPlanHelper.configureLoadTuner(loadTuner);
		}

		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		Collection<String> storeCodes = new LinkedList<>();
		CriteriaQuery query = orderCriterion.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.ENTITY);

		List<Order> orderList = null;
		if (query.getParameters().isEmpty() && storeCodes.isEmpty()) {
			orderList = getPersistenceEngine().retrieve(query.getQuery(), start, maxResults);
		} else if (storeCodes.isEmpty()) {
			orderList = getPersistenceEngine().retrieve(query.getQuery(), query.getParameters().toArray(), start, maxResults);
		} else {
			orderList = getPersistenceEngine().retrieveWithList(query.getQuery(), "storeList", storeCodes,
				query.getParameters().toArray(), start, maxResults);
		}

		if (loadTuner != null) {
			fetchPlanHelper.clearFetchPlan();
		}

		return populateRelationships(orderList);
	}

	/**
	 * Find orders by search criteria using the given load tuner.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @param start the starting record to search
	 * @param maxResults the max results to be returned
	 * @return the list of order numbers matching the given criteria.
	 */
	@Override
	public List<String> findOrderNumbersBySearchCriteria(final OrderSearchCriteria orderSearchCriteria, final int start, final int maxResults) {

		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		Collection<String> storeCodes = new LinkedList<>();
		CriteriaQuery query = orderCriterion.getOrderSearchCriteria(orderSearchCriteria, storeCodes, ResultType.ORDER_NUMBER);

		List<String> orderList;
		if (query.getParameters().isEmpty() && storeCodes.isEmpty()) {
			orderList = getPersistenceEngine().retrieve(query.getQuery(), start, maxResults);
		} else if (storeCodes.isEmpty()) {
			orderList = getPersistenceEngine().retrieve(query.getQuery(), query.getParameters().toArray(), start, maxResults);
		} else {
			orderList = getPersistenceEngine().retrieveWithList(query.getQuery(), "storeList", storeCodes,
				query.getParameters().toArray(), start, maxResults);
		}

		return orderList;
	}

	/**
	 * Configures the fetch plan helper with a default load tuner.
	 */
	protected void prepareFetchPlan() {
		fetchPlanHelper.configureLoadTuner(defaultLoadTuner);
	}

	/**
	 * Configures the fetch plan with a default load tuner, and allows retrieving of orders with partial details, such as shipment order SKUs.
	 */
	protected void prepareFetchPlanWithDetails() {
		prepareFetchPlan();
		fetchPlanHelper.addField(AbstractOrderShipmentImpl.class, "shipmentOrderSkusInternal");
	}

	/**
	 * Configures the fetch plan with a default load tuner, and allows retrieving of orders with full order details.
	 */
	protected void prepareFetchPlanWithFullDetails() {
		prepareFetchPlanWithDetails();
		fetchPlanHelper.addField(OrderSkuImpl.class, "productSku");
	}

	/**
	 * Returns a list of <code>Order</code> based on the given uids. The returned orders will be populated based on the given load tuner.
	 *
	 * @param orderUids a collection of order uids
	 * @return a list of <code>Order</code>s
	 */
	@Override
	public List<Order> findByUids(final Collection<Long> orderUids) {
		return findOrdersByUids(orderUids, false);
	}

	/**
	 * Returns a list of fully initialized <code>Order</code> objects based on the given uids. The returned orders will be populated based on the
	 * given load tuner.
	 *
	 * @param orderUids a collection of order uids
	 * @return a list of <code>Order</code>s
	 */
	@Override
	public List<Order> findDetailedOrdersByUids(final Collection<Long> orderUids) {
		return findOrdersByUids(orderUids, true);
	}

	/**
	 * Returns a list of <code>Order</code> based on the given uids. The returned orders will be populated based on the given load tuner.
	 *
	 * @param orderUids a collection of order uids
	 * @param isDetailedFetchPlan is indicator which define what fetch plan must be used. If it is true then fetch plan with order details is used
	 *            {@link #prepareFetchPlanWithFullDetails()}. Otherwise, simple fetch plan is used {@link #prepareFetchPlan()}
	 * @return a list of <code>Order</code>s
	 */
	private List<Order> findOrdersByUids(final Collection<Long> orderUids, final boolean isDetailedFetchPlan) {
		sanityCheck();

		if (orderUids == null || orderUids.isEmpty()) {
			return new ArrayList<>();
		}

		if (isDetailedFetchPlan) {
			prepareFetchPlanWithFullDetails();
		} else {
			prepareFetchPlan();
		}
		final List<Order> orders = getPersistenceEngine().retrieveByNamedQueryWithList("ORDER_BY_UIDS", "list", orderUids);
		fetchPlanHelper.clearFetchPlan();
		return populateRelationships(orders);
	}

	/**
	 * List all orders stored in the database.
	 *
	 * @return a list of orders
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public List<Order> list() throws EpServiceException {
		sanityCheck();
		prepareFetchPlan();
		final List<Order> orders = getPersistenceEngine().retrieveByNamedQuery("ORDER_SELECT_ALL");
		fetchPlanHelper.clearFetchPlan();
		return populateRelationships(orders);
	}

	/**
	 * Sanity check of this service instance.
	 */
	@Override
	protected void sanityCheck() {
		if (getPersistenceEngine() == null) {
			throw new EpServiceException("The persistence engine is not correctly initialized.");
		}
	}

	/**
	 * Get the order with the given UID. Return null if no matching record exists.
	 *
	 * @param orderUid the order UID
	 * @return the order if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Order get(final long orderUid) throws EpServiceException {
		sanityCheck();
		Order order;
		if (orderUid <= 0) {
			order = getBean(ContextIdNames.ORDER);
		} else {
			prepareFetchPlanWithDetails();
			order = getPersistentBeanFinder().get(ContextIdNames.ORDER, orderUid);
			fetchPlanHelper.clearFetchPlan();
		}
		return populateRelationships(order);
	}

	/**
	 * Get the order with the given UID. Return <code>null</code> if no matching record exists. Fine tune the order with the given load tuner. If
	 * <code>null</code> is given, the default load tuner will be used.
	 *
	 * @param orderUid the order UID
	 * @param loadTuner the load tuner to use (or <code>null</code> for the default)
	 * @return the order if UID exists, otherwise <code>null</code>
	 * @throws EpServiceException in case of any errors
	 */
	@Override
	public Order get(final long orderUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		Order order;
		if (orderUid <= 0) {
			order = getBean(ContextIdNames.ORDER);
		} else {
			fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);
			order = getPersistentBeanFinder().get(ContextIdNames.ORDER, orderUid);
			fetchPlanHelper.clearFetchPlan();
		}
		return populateRelationships(order);
	}

	/**
	 * Generic get method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Return the fully initialized order object.
	 *
	 * @param orderUid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Order getOrderDetail(final long orderUid) throws EpServiceException {
		sanityCheck();
		Order order;
		if (orderUid <= 0) {
			order = getBean(ContextIdNames.ORDER);
		} else {
			prepareFetchPlanWithFullDetails();
			order = getPersistentBeanFinder().get(ContextIdNames.ORDER, orderUid);
			fetchPlanHelper.clearFetchPlan();
		}
		return populateRelationships(order);
	}

	@Override
	public void updateOrderShipmentStatus() {
		final long startTime = System.currentTimeMillis();
		LOG.debug("Start release shipments quartz job at: " + new Date(startTime));

		sanityCheck();

		List<OrderStatus> orderStatusList = new ArrayList<>();
		orderStatusList.add(OrderStatus.IN_PROGRESS);
		orderStatusList.add(OrderStatus.PARTIALLY_SHIPPED);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("listOrderStatus", orderStatusList);
		parameters.put("shipmentStatus", OrderShipmentStatus.INVENTORY_ASSIGNED);

		final List<Order> results = getPersistenceEngine().retrieveByNamedQuery("ORDERS_BY_ORDER_STATUS_AND_SHIPMENT_STATUS", parameters);

		for (final Order order : results) {
			releaseReleasableShipments(order);
		}

		LOG.debug("Release shipments quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
	}

	@Override
	public void releaseReleasableShipments(final Order order) {
		final long currentTime = timeService.getCurrentTime().getTime();
		final EventOriginator systemEventOriginator = getSystemEventOriginator();

		final List<PhysicalOrderShipment> shipments = order.getPhysicalShipments();
		final Store store = getStore(order);
		final long pickTimeStamp = currentTime - store.getWarehouse().getPickDelay() * MINUTE_IN_MS;
		final Date pickDate = new Date(pickTimeStamp);

		for (final OrderShipment shipment : shipments) {
			if (shipment.getCreatedDate().before(pickDate)
					&& OrderShipmentStatus.INVENTORY_ASSIGNED.equals(shipment.getShipmentStatus())) {
				try {
					shipment.getOrder().setModifiedBy(systemEventOriginator);
					getOrderService().processReleaseShipment(shipment);
				} catch (ReleaseShipmentFailedException e) {
					LOG.error("Quartz job release shipment failed.", e);
				}
			}
		}
	}

	/**
	 * Gets the system originator for automatic releasing of the ready shipments.
	 */
	private EventOriginator getSystemEventOriginator() {
		EventOriginatorHelper helper = getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
		return helper.getSystemOriginator();
	}

	/**
	 * Sends an OrderEvent in the case of the automatic release shipment failing.
	 */
	private void sendOrderEventForFailedReleaseShipment(final OrderShipment shipment, final String errorMessage) {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put("shipmentType", shipment.getOrderShipmentType().getName());
		additionalData.put("orderGuid", shipment.getOrder().getOrderNumber());
		additionalData.put("errorMessage", errorMessage);

		sendOrderEvent(OrderEventType.ORDER_SHIPMENT_RELEASE_FAILED, shipment.getShipmentNumber(), additionalData);
	}

	/**
	 * Searches for order shipment with the specified shipment number and shipment type.
	 *
	 * @param shipmentNumber the order shipment number
	 * @param shipmentType the type of shipment (physical or electronic)
	 * @return the shipment requested, or null if not found
	 */
	@Override
	public OrderShipment findOrderShipment(final String shipmentNumber, final ShipmentType shipmentType) {
		OrderShipment shipment = null;
		prepareFetchPlan();
		String queryName;
		if (ShipmentType.PHYSICAL.equals(shipmentType)) {
			queryName = "PHYSICAL_SHIPMENT_BY_SHIPMENT_NUMBER";
		} else {
			queryName = "ELECTRONIC_SHIPMENT_BY_SHIPMENT_NUMBER";
		}

		final List<OrderShipment> results = getPersistenceEngine().retrieveByNamedQuery(queryName, shipmentNumber);
		fetchPlanHelper.clearFetchPlan();
		if (!results.isEmpty()) {
			shipment = results.get(0);
		}
		populateRelationships(shipment);
		return shipment;
	}

	@Override
	public Order completeShipment(final String shipmentNumber, final String trackingCode, final boolean captureFunds, final Date shipmentDate,
			final boolean sendConfEmail, final EventOriginator eventOriginator) {
		PaymentResult paymentResult = null;
		try {
			// Capture funds. This will update the OrderShipment's Order's list of OrderPayments.
			if (captureFunds) {
				paymentResult = getOrderService().processOrderShipmentPayment(shipmentNumber);
				if (paymentResult != null && paymentResult.getResultCode() != PaymentResult.CODE_OK) {
					throw new CompleteShipmentFailedException("Cannot complete shipment. Payments have failed.", paymentResult.getCause());
				}
			}

			final Order updatedOrder = getOrderService().processOrderShipment(shipmentNumber, trackingCode, shipmentDate, eventOriginator);

			sendOrderEvent(OrderEventType.ORDER_SHIPMENT_SHIPPED,
							shipmentNumber,
							ImmutableMap.<String, Object>of("orderGuid", updatedOrder.getGuid()));

			return updatedOrder;
		} catch (final Exception e) {
			LOG.error("Complete shipment failed during release shipment phase.", e);
			if (paymentResult != null) {
				paymentService.rollBackPayments(paymentResult.getProcessedPayments());
			}
			throw new CompleteShipmentFailedException("Complete shipment failed. Caused by: " + e.getMessage(), e);
		}
	}

	/**
	 * Process the payment for a shipment. This will attempt to capture funds for the shipment and return a <code>PaymentResult</code>. If the
	 * payment processing fails, then any failed capture transactions will be saved to the order.
	 *
	 * @param shipmentNumber the shipment number to process a payment on (GUID)
	 * @return the result of the payment processing
	 */
	@Override
	public PaymentResult processOrderShipmentPayment(final String shipmentNumber) {
		final PhysicalOrderShipment orderShipmentToComplete = (PhysicalOrderShipment) getOrderService().findOrderShipment(shipmentNumber,
				ShipmentType.PHYSICAL);

		if (orderShipmentToComplete == null) {
			throw new OrderShipmentNotFoundException("Unable to find Physical Shipment with number " + shipmentNumber);
		}

		PaymentResult paymentResult;
		paymentResult = paymentService.processShipmentPayment(orderShipmentToComplete);

		// Save the updated Order with its new orderPayment
		try {
			update(orderShipmentToComplete.getOrder());
		} catch (RuntimeException e) {
			//  Eat Exception to preserve existing logic flow in checkout
			LOG.error("Error occurred when attempting to save failed payments to order.", e);
		}

		return paymentResult;
	}

	private OrderService getOrderService() {
		return getBean(ContextIdNames.ORDER_SERVICE);
	}

	/**
	 * IMPORTANT: This method is defined for the transaction issue purpose, and should not be called by your code. Refactor the code to notify Spring
	 * to start a transaction on the processOrderShipment function. Release the order shipment after the payment is captured. Send the order shipment
	 * email and execute the extra tasks, eg. capture the payment for gift certificate. This method will run as an atomic DB transaction (This is
	 * specified in the Spring configuration)
	 *
	 * @param shipmentNumber the number (GUID) of the PHYSICAL orderShipment to be released.
	 * @param trackingCode the trackingCode for the orderShipment to be released.
	 * @param shipmentDate the date of complete shipment process
	 * @param eventOriginator the event originator, could be cm user, ws user, customer or system originator. See {@link EventOriginatorHelper }
	 * @return the updated order
	 * @throws OrderShipmentNotFoundException if a physical shipment with the given number cannot be found
	 * @throws EpServiceException if a single order can't be found with the given order number
	 */
	@Override
	public Order processOrderShipment(final String shipmentNumber, final String trackingCode, final Date shipmentDate,
			final EventOriginator eventOriginator) {
		final PhysicalOrderShipment shipment = (PhysicalOrderShipment) getOrderService().findOrderShipment(shipmentNumber, ShipmentType.PHYSICAL);

		if (shipment == null) {
			throw new OrderShipmentNotFoundException("Unable to find Physical Shipment with number " + shipmentNumber);
		}

		shipment.getOrder().setModifiedBy(eventOriginator);

		// release the inventory
		for (OrderSku sku : shipment.getShipmentOrderSkus()) {

			allocationService.processAllocationEvent(sku, AllocationEventType.ORDER_SHIPMENT_COMPLETED, eventOriginator.getType().toString(),
					sku.getQuantity(), "Order shipment " + shipment.getShipmentNumber() + " completed");
		}

		shipment.setTrackingCode(trackingCode);
		if (shipmentDate == null) {
			shipment.setShipmentDate(timeService.getCurrentTime());
		} else {
			shipment.setShipmentDate(shipmentDate);
		}
		shipment.setStatus(OrderShipmentStatus.SHIPPED);
		// will call our external payment gateways to confirm shipment and send shipment notice to customer
		paymentService.finalizeShipment(shipment);

		return update(shipment.getOrder());
	}

	@Override
	public Order refundOrderPayment(final long orderUid, final String shipmentNumber, final OrderPayment orderPayment,
			final BigDecimal refundAmount, final EventOriginator eventOriginator) {
		return getOrderService().processRefundOrderPayment(orderUid, shipmentNumber, orderPayment, refundAmount, eventOriginator);
	}

	@Override
	public Order processRefundOrderPayment(final long orderUid, final String shipmentNumber, final OrderPayment refundPayment,
			final BigDecimal refundAmount, final EventOriginator eventOriginator) {

		final Order order = get(orderUid);
		order.setModifiedBy(eventOriginator);

		if (!order.isRefundable()) {
			throw new EpServiceException("Order is not applicable for a refund.");
		}

		OrderShipment orderShipmentToRefund = findRefundableShipment(shipmentNumber, order);

		final BigDecimal capturedTotal = calculateTotalCaptured(order).setScale(2);

		BigDecimal totalCreditAmount = BigDecimal.ZERO.setScale(2);
		// calculate total credit amount on the order
		for (final OrderPayment orderPayment : order.getOrderPayments()) {
			if (orderPayment.getTransactionType().equals(OrderPayment.CREDIT_TRANSACTION)) {
				totalCreditAmount = totalCreditAmount.add(orderPayment.getAmount());
			}
		}

		final BigDecimal totalMinusCredit = capturedTotal.subtract(totalCreditAmount);

		// refund only when the refund amount <= available credit
		if (refundAmount.compareTo(totalMinusCredit) <= 0) {
			Order updatedOrder = order;
			PaymentResult paymentResult;

			paymentResult = paymentService.refundShipmentPayment(orderShipmentToRefund, refundPayment, refundAmount);

			// persist refund payment regardless of result
			try {
				updatedOrder = update(orderShipmentToRefund.getOrder());
			} catch (RuntimeException e) {
				LOG.error("Error occured when attempting to save failed refunds to order.", e);
			}

			if (paymentResult != null && paymentResult.getResultCode() != PaymentResult.CODE_OK) {
				// In order to preserve existing client catch blocks we need to throw this exception.
				throw new PaymentGatewayException("Refund was unsuccesful.", paymentResult.getCause());
			}
			return updatedOrder;
		}
		throw new IncorrectRefundAmountException("The refund amount exceeds total amount left to be captured.");
	}

	/**
	 * Calculates the total of the amount captured.
	 *
	 * @param order The order to total for.
	 * @return The total.
	 */
	BigDecimal calculateTotalCaptured(final Order order) {
		BigDecimal returnValue = BigDecimal.ZERO;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (OrderPayment.CAPTURE_TRANSACTION.equals(payment.getTransactionType())) {
				returnValue = returnValue.add(payment.getAmount());
			}
		}
		return returnValue;
	}

	/**
	 * Get the orderSku uid -> returned quantity map for the order with given uid.
	 *
	 * @param orderUid the uid of the order.
	 * @return the orderSku uid -> returned quantity map.
	 */
	@Override
	public Map<Long, Integer> getOrderSkuReturnQtyMap(final long orderUid) {
		// load the order instance
		final Order order = get(orderUid);

		final Multiset<Long> orderSkuReturnQtyMap = HashMultiset.create();
		if (order.getReturns() != null) {
			for (final OrderReturn orderReturn : order.getReturns()) {
				for (final OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
					final Long orderSkuUid = orderReturnSku.getOrderSku().getUidPk();
					orderSkuReturnQtyMap.add(orderSkuUid, orderReturnSku.getQuantity());
				}
			}
		}

		HashMap<Long, Integer> result = Maps.newHashMap();
		for (Multiset.Entry<Long> entry : orderSkuReturnQtyMap.entrySet()) {
			result.put(entry.getElement(), entry.getCount());
		}

		return result;
	}

	/**
	 * Add the given <code>OrderReturn</code> to the order with given uid.
	 *
	 * @param order the given order.
	 * @param orderReturn orderReturn to be added.
	 * @return the updated order.
	 */
	@Override
	public Order addOrderReturn(final Order order, final OrderReturn orderReturn) {
		order.addReturn(orderReturn);
		return update(order);
	}

	/**
	 * Returns all order uids as a list.
	 *
	 * @return all order uids as a list
	 */
	@Override
	public List<Long> findAllUids() {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_UIDS_ALL");
	}

	/**
	 * Retrieves list of <code>Order</code> uids where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Order</code> whose last modified date is later than the specified date
	 */
	@Override
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}

	/**
	 * @param fetchPlanHelper the fetchPlanHelper to set
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	/**
	 * Returns a list of ProductCodes for all Products of which the product's skus have been purchased by a particular user, given additional search
	 * criteria.
	 *
	 * @param criteria the criteria to use in finding the product codes
	 * @return distinct list of product codes corresponding to skus that were purchased by a user, filtered by the given search criteria
	 */
	@Override
	public List<String> findProductCodesPurchasedByUser(final PurchaseHistorySearchCriteria criteria) {
		List<String> result;

		if (criteria.getFromDate() == null) {
			result = getPersistenceEngine().retrieveByNamedQuery("FIND_PRODUCT_CODES_PURCHASED_BY_USER", criteria.getUserId(),
					criteria.getStoreCode(),
					criteria.getToDate());
		} else {
			result = getPersistenceEngine().retrieveByNamedQuery("FIND_PRODUCT_CODES_PURCHASED_BY_USER_BETWEEN_DATES",
					criteria.getUserId(),
					criteria.getStoreCode(),
					criteria.getToDate(),
					criteria.getFromDate());
		}

		return Collections.unmodifiableList(result);
	}

	/**
	 * Update the address for order, sometimes we don't want to update the whole order.
	 *
	 * @param address the given address.
	 */
	@Override
	public void updateAddress(final OrderAddress address) {
		getPersistenceEngine().update(address);
	}

	/**
	 * Given a list of domain objects fresh from persistence, populates related fields (i.e. Store) that are
	 * not directly linked from OpenJPA.
	 *
	 * @param orders The orders to populate
	 * @return the list of orders passed in (for chaining)
	 */
	protected List<Order> populateRelationships(final List<Order> orders) {
		for (Order order : orders) {
			populateRelationships(order);
		}

		return orders;
	}

	/**
	 * Given an order fresh from persistence, populates related fields (i.e. Store) that are
	 * not directly linked from OpenJPA.
	 *
	 * @param order The order to populate
	 * @return the order passed in (for chaining)
	 */
	@SuppressWarnings("deprecation")
	protected Order populateRelationships(final Order order) {
		if (order == null) {
			return null;
		}

		String storeCode = order.getStoreCode();
		if (storeCode != null) {
			order.setStore(getStoreService().findStoreWithCode(storeCode));
		}

		return order;
	}

	/**
	 * Given an order shipment fresh from persistence, populates related fields (i.e. Store) that are
	 * not directly linked from OpenJPA.
	 *
	 * @param shipment The order shipment to populate
	 * @return the order passed in (for chaining)
	 */
	protected OrderShipment populateRelationships(final OrderShipment shipment) {
		if (shipment == null) {
			return null;
		}

		populateRelationships(shipment.getOrder());

		return shipment;
	}

	/**
	 * Set the time service.
	 *
	 * @param timeService the <code>TimeService</code> instance.
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * Set the allocation service.
	 *
	 * @param allocationService the <code>allocationService</code> instance.
	 */
	public void setAllocationService(final AllocationService allocationService) {
		this.allocationService = allocationService;
	}

	/**
	 * @param paymentService the paymentService to set
	 */
	public void setPaymentService(final PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	/**
	 * Remove an order. Should only be called on an unpopulated order object.
	 *
	 * @param order the order to remove
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public void remove(final Order order) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(order);
	}

	@Override
	public List<PhysicalOrderShipment> getAwaitingShipments(final Warehouse warehouse) {
		sanityCheck();
		List<PhysicalOrderShipment> releasedShipments =
			getPersistenceEngine().retrieveByNamedQuery("PHYSICAL_SHIPMENTS_BY_STATUS_AND_WAREHOUSE",
					OrderShipmentStatus.RELEASED, warehouse.getUidPk());
		List<PhysicalOrderShipment> pickableShipments = new ArrayList<>();

		//Released state is dependent on Order status
		for (PhysicalOrderShipment shipment : releasedShipments) {
			if (shipment.getShipmentStatus().equals(OrderShipmentStatus.RELEASED)) {
				pickableShipments.add(shipment);
			}
		}
		return pickableShipments;
	}

	@Override
	public Long getAwaitingShipmentsCount(final Warehouse warehouse) {
		sanityCheck();
		return getPersistenceEngine().<Long>retrieveByNamedQuery("COUNT_PHYSICAL_SHIPMENTS_BY_STATUS_AND_WAREHOUSE",
				OrderShipmentStatus.RELEASED,
				warehouse.getUidPk()).get(0);
	}

	/**
	 * Releases order lock if order was locked and updates the order.
	 *
	 * @param order the order to be unlocked and updated.
	 * @param cmUser the user which is releasing the order lock.
	 * @throws EpServiceException - in case of any errors, InvalidUnlockerException if when the orderLock was obtained not by the cmUser, but by some
	 *             other user.
	 */
	@Override
	public void unlockAndUpdate(final Order order, final CmUser cmUser) {
		final OrderLockService orderLockService = getBean(ContextIdNames.ORDER_LOCK_SERVICE);
		final OrderLock orderLock = orderLockService.getOrderLock(order);
		if (orderLock != null) {
			/* the order was locked, try to release it now. */
			orderLockService.releaseOrderLock(orderLock, cmUser);
		}
		update(order);
	}

	/**
	 * Cancel an order.
	 *
	 * @param order the order to be cancelled.
	 * @return the updated order
	 */
	@Override
	public Order cancelOrder(final Order order) {
		if (!order.isCancellable()) {
			throw new EpServiceException("Order is not cancellable.");
		}
		try {
			paymentService.cancelOrderPayments(order);
		} catch (final Exception e) {
			// If anything wrong on the payment, won't affect the order
			// cancellation. Since we only try to reverse the auth.
			LOG.error("Reverse payment failed when cancel the order.", e);
		}
		getOrderEventHelper().logOrderCanceled(order);

		// Run it in a transaction.
		Order result = getOrderService().processOrderCancellation(order);

		getTaxOperationService().reverseTaxes(result);

		return result;
	}

	/**
	 * Cancel an order, update the db and objects in one transaction. This method should not be called outside of the service. Use cancelOrder()
	 * instead.
	 *
	 * @param order the order to be canceled.
	 * @return the updated order
	 */
	@Override
	public Order processOrderCancellation(final Order order) {
		// Since this is still a public method, should verify again that the order can be cancelled.
		if (!order.isCancellable()) {
			throw new EpServiceException("Order is not cancellable");
		}
		String eventOriginator = getEventOriginator(order);

		for (final ShoppingItem shoppingItem : order.getRootShoppingItems()) {
			OrderSku orderSku = (OrderSku) shoppingItem;
			if (orderSku.getSkuGuid() != null) {
				allocationService.processAllocationEvent(orderSku, AllocationEventType.ORDER_CANCELLATION, eventOriginator, orderSku
						.getAllocatedQuantity(), null);
			}
		}

		order.cancelOrder();

		sendOrderEvent(OrderEventType.ORDER_CANCELLED, order.getOrderNumber());

		return update(order);
	}

	/**
	 * Gets an event originator from an order.
	 *
	 * @param order the order
	 * @return the InventoryAudit String representing the event originator
	 */
	String getEventOriginator(final Order order) {
		String eventOriginator = InventoryAudit.EVENT_ORIGINATOR_WS;
		if (order.getModifiedBy() != null && order.getModifiedBy().getType() == EventOriginatorType.CMUSER) {
			eventOriginator = InventoryAudit.EVENT_ORIGINATOR_CMUSER + order.getModifiedBy().getCmUser().getGuid();
		}
		return eventOriginator;
	}

	/**
	 * Cancel an orderShipment.
	 *
	 * @param orderShipment the orderShipment to be canceled.
	 * @return the updated orderShipment
	 */
	@Override
	public PhysicalOrderShipment cancelOrderShipment(final PhysicalOrderShipment orderShipment) {
		try {
			paymentService.cancelShipmentPayment(orderShipment);
		} catch (final Exception e) {
			// If anything wrong on the payment, won't affect the order
			// cancellation. Since we only try to reverse the auth.
			LOG.error("Reverse payment failed when cancelling order: " + orderShipment.getOrder().getOrderNumber(), e);
		}

		// Run it in a transaction.
		PhysicalOrderShipment result = getOrderService().processOrderShipmentCancellation(orderShipment);

		getTaxOperationService().reverseTaxes(result, orderShipment.getShipmentAddress());
		return result;
	}

	/**
	 * Cancel an orderShipment, update the db and objects in one transaction.
	 *
	 * @param orderShipment the orderShipment to be canceled.
	 * @return the updated orderShipment
	 */
	@Override
	public PhysicalOrderShipment processOrderShipmentCancellation(final PhysicalOrderShipment orderShipment) {
		String eventOriginator = getEventOriginator(orderShipment.getOrder());
		for (final OrderSku orderSku : orderShipment.getShipmentOrderSkus()) {
			if (orderSku.getSkuGuid() != null) {
				allocationService.processAllocationEvent(orderSku, AllocationEventType.ORDER_CANCELLATION, eventOriginator, orderSku
						.getAllocatedQuantity(), null);
			}
		}

		orderShipment.setStatus(OrderShipmentStatus.CANCELLED);
		final Order updatedOrder = update(orderShipment.getOrder());

		return (PhysicalOrderShipment) updatedOrder.getShipment(orderShipment.getShipmentNumber());
	}

	/**
	 * Finds order by order number.
	 *
	 * @param orderNumber order number.
	 * @return the order
	 */
	@Override
	public Order findOrderByOrderNumber(final String orderNumber) {
		sanityCheck();
		prepareFetchPlan();
		List<Order> results = getPersistenceEngine().retrieveByNamedQuery("ORDER_SELECT_BY_ORDERNUMBER", orderNumber);
		if (fetchPlanHelper.doesPlanContainFetchGroup(FetchGroupConstants.ORDER_DEFAULT)) {
			// Workaround to explicitly initialize "parent" field for all OrderSku entities.
			results.stream()
					.filter(order -> order != null && order.getAllShipments() != null)
					.flatMap(order -> order.getAllShipments().stream())
					.filter(shipment -> shipment != null && shipment.getShipmentOrderSkus() != null)
					.flatMap(shipment -> shipment.getShipmentOrderSkus().stream())
					.forEach(OrderSku::getParent);
		}
		fetchPlanHelper.clearFetchPlan();
		Order order = null;
		if (results.size() == 1) {
			order = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate order numbers exist -- " + orderNumber);
		}
		return order;
	}

	/**
	 * Places a hold on an order.
	 *
	 * @param order the order upon which to place a hold
	 * @return the modified order
	 */
	@Override
	public Order holdOrder(final Order order) {
		if (!order.isHoldable()) {
			LOG.error("Order (uidpk:  " + order.getUidPk() + " is not holdable but hold was invoked on it.");
		}
		order.holdOrder();

		getOrderEventHelper().logOrderOnHold(order);

		sendOrderEvent(OrderEventType.ORDER_HELD, order.getOrderNumber());

		return update(order);
	}

	@Deprecated
	@Override
	public Order releaseHoldOnOrder(final Order order) {
		return releaseOrder(order);
	}

	@Override
	public Order releaseOrder(final Order order) {
		if (!order.isReleasable()) {
			throw new EpServiceException("Cannot release order with status [" + order.getStatus() + "].");
		}

		final OrderStatus previousStatus = order.getStatus();

		order.releaseOrder();

		if (OrderStatus.ONHOLD.equals(previousStatus)) {
			// Log the order on hold released event.
			getOrderEventHelper().logOrderHoldReleased(order);

			// Capture payments for electronic items, since they have not been captured during checkout.
			captureImmediatelyShippableShipments(order);
		}

		getOrderEventHelper().logOrderReleasedForFulfilment(order);

		releaseReleasableShipments(order);

		sendOrderEvent(OrderEventType.ORDER_RELEASED, order.getOrderNumber(), null);

		return update(order);
	}

	/**
	 * Places order which exchange requires physical return to AWAITING_EXCHANGE state.
	 *
	 * @param order the order upon which to place a hold
	 * @return the modified order
	 */
	@Override
	public Order awaitExchnageCompletionForOrder(final Order order) {
		order.awaitExchnageCompletionOrder();
		return order;
	}

	@Override
	public OrderShipment processReleaseShipment(final OrderShipment orderShipment) throws ReleaseShipmentFailedException {
		try {
			return processReleaseShipmentInternal(orderShipment);
		} catch (final ReleaseShipmentFailedException e) {
			sendOrderEventForFailedReleaseShipment(orderShipment, e.getMessage());
			throw e;
		}
	}

	/**
	 * Internal method for releasing the shipment for pick/pack.  No {@code ORDER_SHIPMENT_RELEASE_FAILED} message is published in the event of a
	 * {@link ReleaseShipmentFailedException} being thrown.
	 *
	 * @param orderShipment the order shipment to be released
	 * @return the updated order shipment
	 * @throws ReleaseShipmentFailedException on error setting up the authorization payments
	 */
	protected OrderShipment processReleaseShipmentInternal(final OrderShipment orderShipment) throws ReleaseShipmentFailedException {
		// We need to grab fresh shipment and do changes directly in DB, because of order locking issues.
		// If one RCP session releases the shipment, DB changes are taken place, but order won't be marked dirty
		// That's why if another session tries to release shipment again, it couldnt't detect that shipment was already released.
		Order freshOrder = get(orderShipment.getOrder().getUidPk());
		if (freshOrder == null) {
			throw new ReleaseShipmentFailedException("Cannot find order with uidPk: " + orderShipment.getOrder().getUidPk());
		}
		OrderShipment foundShipment = null;
		for (OrderShipment freshShipment : freshOrder.getAllShipments()) {
			if (freshShipment.getUidPk() == orderShipment.getUidPk()) {
				foundShipment = freshShipment;
				break;
			}
		}
		if (foundShipment == null) {
			throw new ReleaseShipmentFailedException("Shipment to be released cannot be found");
		}

		if (!OrderShipmentStatus.INVENTORY_ASSIGNED.equals(foundShipment.getShipmentStatus())) {
			throw new ReleaseShipmentFailedException("Shipment to be released is not in INVENTORY_ASSIGNED " + "state. Current state is "
					+ foundShipment.getShipmentStatus());
		}
		// adjust the auth payments if needed
		PaymentResult paymentResult = paymentService.adjustShipmentPayment(foundShipment);
		// check the result code
		if (paymentResult != null && paymentResult.getResultCode() != PaymentResult.CODE_OK) {
			throw new ReleaseShipmentFailedException("Cannot release shipment", paymentResult.getCause());
		}

		foundShipment.setStatus(OrderShipmentStatus.RELEASED);

		getOrderEventHelper().logOrderShipmentReleased(orderShipment.getOrder(), foundShipment);
		// update the order and get the updated order instance
		Order updatedOrder = update(foundShipment.getOrder());
		// grab the updated shipment instance from the updated order instance
		return updatedOrder.getShipment(foundShipment.getShipmentNumber());
	}

	/**
	 * Allocates the inventory for the given order.
	 *
	 * @param order the order
	 */
	private void allocateInventoryForOrder(final Order order) {
		for (final OrderSku orderSku : order.getOrderSkus()) {
			// Allocate inventory to the order
			final AllocationResult eventResult = allocationService.processAllocationEvent(orderSku, AllocationEventType.ORDER_PLACED,
					InventoryAudit.EVENT_ORIGINATOR_SF, orderSku.getQuantity(), null);
			final int allocatedQuantity = eventResult.getQuantityAllocatedInStock();
			orderSku.setAllocatedQuantity(allocatedQuantity);
			// Need to set the updated inventory object back into the product sku, otherwise the inventory change will be overwritten when
			// the order is saved.
		}
	}

	/**
	 * Allocates inventory, logs order event and saves the order to the database.
	 *
	 * @param order the order
	 * @param isExchangeOrder true if the order is of exchange type
	 * @return the completed order object
	 * @throws EpServiceException on error
	 */
	@Override
	public Order processOrderOnCheckout(final Order order, final boolean isExchangeOrder) throws EpServiceException {

		if (order == null) {
			throw new IllegalArgumentException("Cannot process a null order");
		}

		allocateInventoryForOrder(order);

		if (isExchangeOrder) {
			getOrderEventHelper().logOrderExchangeCreated(order);
		} else {
			getOrderEventHelper().logOrderPlaced(order);
		}

		order.getCustomer().setFirstTimeBuyer(false);

		Order persistedOrder = update(order);
		persistedOrder.setModifiedBy(order.getModifiedBy());

		return persistedOrder;
	}

	@Override
	public void updateLimitedUsagePromotionCurrentNumbers(final Collection<Long> appliedRuleUids, final List<String> limitedUsagePromotionCodes) {

		if (CollectionUtils.isEmpty(limitedUsagePromotionCodes)) {
			return;
		}

		for (Long appliedRuleUid : appliedRuleUids) {
			Rule rule = getRuleService().get(appliedRuleUid);
			if (limitedUsagePromotionCodes.contains(rule.getCode())) {
				rule.setCurrentLupNumber(rule.getCurrentLupNumber() + 1);
				getPersistenceEngine().merge(rule);
			}
		}
	}

	@Override
	public long getCustomerOrderCountByEmail(final String email, final long storeId) {
		final List<Object> results = getPersistenceEngine().retrieveByNamedQuery("ORDER_COUNT_BY_EMAIL", email, storeId);
		Long count = 0L;
		if (!results.isEmpty()) {
			count = (Long) results.get(0);
		}
		return count;
	}

	@Override
	public List<Long> getFailedOrderUids(final Date toDate, final int maxResults) {
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_UID_FOR_FAILED_ORDERS_BEFORE_DATE", new Object[] {toDate}, 0, maxResults);
	}

	@Override
	public void deleteOrders(final List<Long> orderUids) {
		getPersistenceEngine().executeNamedQueryWithList("DELETE_ORDER_BY_ORDER_UID_LIST", "list", orderUids);
	}

	@Override
	public String findLatestOrderGuidByCartOrderGuid(final String cartOrderGuid) {
		List<String> results = getPersistenceEngine().retrieveByNamedQuery("FIND_ORDER_GUIDS_BY_CART_ORDER_GUID",
				new Object[] { cartOrderGuid }, 0, 1);
		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}

	@Override
	public List<String> findOrderNumbersByCustomerGuid(final String storeCode, final String customerGuid) {
		return getPersistenceEngine().retrieveByNamedQuery("FIND_ORDER_NUMBERS_BY_CUSTOMER_GUID", customerGuid, storeCode);
	}

	@Override
	public OrderShipment findOrderShipment(final String shipmentNumber) {

		if (StringUtils.isBlank(shipmentNumber)) {
			throw new EpServiceException("shipmentNumber cannot be null or empty.");
		}
		OrderShipment shipment = null;
		prepareFetchPlan();
		final List<OrderShipment> results = getPersistenceEngine().retrieveByNamedQuery("ABSTRACT_ORDER_SHIPMENT_BY_SHIPMENT_NUMBER",
				new Object[] { shipmentNumber });
		fetchPlanHelper.clearFetchPlan();
		if (!results.isEmpty()) {
			shipment = results.get(0);
		}
		return shipment;
	}

	@Override
	public void captureImmediatelyShippableShipments(final Order order) {

		List<OrderShipment> nonPhysicalShipments = getImmediatelyShippableShipments(order);

		for (final OrderShipment orderShipment : nonPhysicalShipments) {
			if (orderHasPaymentForShipment(order, orderShipment)) {
				paymentService.processShipmentPayment(orderShipment);
			}
			// if we don't have to pay for electronically shipments, we should still put the status on SHIPPED
			orderShipment.setStatus(OrderShipmentStatus.SHIPPED);
		}
	}

	/**
	 * Extracts the shipments whose payments must be captured during checkout.
	 * @param order the order.
	 * @return the list of shipments
	 */
	protected List<OrderShipment> getImmediatelyShippableShipments(final Order order) {
		return order.getAllShipments().stream()
				.filter(orderShipment -> !(orderShipment instanceof PhysicalOrderShipment))
				.collect(Collectors.toList());
	}

	/**
	 * Check the order has a payment for the shipment.
	 *
	 * @param order Order to check payment on
	 * @param orderShipment Shipment to check for payment
	 * @return true if order has payment for the shipment or if order payment has no shipment, false otherwise
	 */
	protected boolean orderHasPaymentForShipment(final Order order,
												 final OrderShipment orderShipment) {
		if (orderShipment == null) {
			return false;
		}

		for (OrderPayment orderPayment : order.getOrderPayments()) {
			if (orderPayment.getOrderShipment() == null || orderShipment.equals(orderPayment.getOrderShipment())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds and verifies that the indicated shipment is refundable.  Throws an
	 * EpServiceException if the shipment cannot be found or is non-refundable.
	 * @param shipmentNumber the shipment number
	 * @param order the order
	 * @return the order shipment
	 */
	private OrderShipment findRefundableShipment(final String shipmentNumber, final Order order) {
		OrderShipment orderShipmentToRefund = null;
		if (shipmentNumber != null) {
			orderShipmentToRefund = order.getShipment(shipmentNumber);
		}

		if (orderShipmentToRefund == null) {
			throw new EpServiceException("The shipment could not be found.");
		} else if (!orderShipmentToRefund.isRefundable()) {
			throw new EpServiceException("The shipment is not refundable.");
		}
		return orderShipmentToRefund;
	}


	@Override
	public void resendOrderConfirmationEvent(final String orderNumber) {
		sendOrderEvent(OrderEventType.RESEND_ORDER_CONFIRMATION, orderNumber, null);
	}

	/**
	 * Helper method which returns the store associated with a given order.
	 * @param order the order
	 * @return the store
	 */
	protected Store getStore(final Order order) {
		return getStoreService().findStoreWithCode(order.getStoreCode());
	}

	/**
	 * Triggers an order event.
	 *
	 * @param eventType the type of Order Event to trigger
	 * @param orderNumber the order id associated with the event
	 */
	protected void sendOrderEvent(final EventType eventType, final String orderNumber) {
		sendOrderEvent(eventType, orderNumber, null);
	}

	/**
	 * Triggers an order event.
	 *
	 * @param eventType the type of Order Event to trigger
	 * @param orderNumber the order id associated with the event
	 * @param additionalData additional data to include in the message
	 */
	protected void sendOrderEvent(final EventType eventType, final String orderNumber, final Map<String, Object> additionalData) {
		// Send notification via messaging system
		try {
			final EventMessage eventMessage = getEventMessageFactory().createEventMessage(eventType, orderNumber, additionalData);

			getEventMessagePublisher().publish(eventMessage);

		} catch (final Exception e) {
			throw new EpSystemException("Failed to publish Event Message", e);
		}
	}

	public void setDefaultLoadTuner(final LoadTuner loadTuner) {
		defaultLoadTuner = loadTuner;
	}

	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	protected OrderEventHelper getOrderEventHelper() {
		return getBean(ContextIdNames.ORDER_EVENT_HELPER);
	}

	public TaxOperationService getTaxOperationService() {
		return taxOperationService;
	}

	public void setTaxOperationService(final TaxOperationService taxOperationService) {
		this.taxOperationService = taxOperationService;
	}

	public void setEventMessageFactory(final EventMessageFactory eventMessageFactory) {
		this.eventMessageFactory = eventMessageFactory;
	}

	protected EventMessageFactory getEventMessageFactory() {
		return this.eventMessageFactory;
	}

	public void setEventMessagePublisher(final EventMessagePublisher eventMessagePublisher) {
		this.eventMessagePublisher = eventMessagePublisher;
	}

	protected EventMessagePublisher getEventMessagePublisher() {
		return eventMessagePublisher;
	}

}
