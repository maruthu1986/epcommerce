/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.support;

import java.util.Collection;

import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.persistence.support.impl.CriteriaQuery;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;

/**
 * <code>OrderCriterion</code> generates the proper criteria for order domain object.
 */
public interface OrderCriterion {

	/**
	 * The Enum ResultType.
	 */
	enum ResultType {

		/** Query returns COUNT. */
		COUNT,

		/** Query returns OrderImpl.orderNumber. */
		ORDER_NUMBER,

		/** Query returns ENTITY. */
		ENTITY
	}

	/**
	 * Returns the criteria used to query by status.
	 *
	 * @param orderStatus the status of the order
	 * @param paymentStatus the status of the payment
	 * @param shipmentStatus the status of the shipment
	 * @return the criteria used to query by status
	 */
	CriteriaQuery getStatusCriteria(OrderStatus orderStatus, OrderPaymentStatus paymentStatus, OrderShipmentStatus shipmentStatus);

	/**
	 * Returns the criteria used to query by customer property of String type, i.e. 'email', 'firstName', 'lastName' etc.
	 *
	 * @param propertyName customer property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return the criteria used to query by order.
	 */
	CriteriaQuery getOrderCustomerCriteria(String propertyName, String criteriaValue, boolean isExactMatch);

	/**
	 * Returns the criteria used to query by customer profile value such as 'CP_EMAIL'.
	 *
	 * @param attributeKey customer profile property to search on.
	 * @param attributeValue criteria value to be used for searching.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return the criteria used to query by order.
	 */
	CriteriaQuery getOrderCustomerProfileCriteria(String attributeKey, String attributeValue, boolean isExactMatch);

	/**
	 * Returns the criteria used to query by giftCertificate with name and value type, i.e. 'giftCertificateCode'.
	 *
	 * @param propertyName customer property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @param isExactMatch true for doing an exact match; false for doing a fuzzy match.
	 * @return the criteria used to query by order.
	 */
	CriteriaQuery getOrderGiftCertificateCriteria(String propertyName, String criteriaValue, boolean isExactMatch);

	/**
	 * Return the criteria used for order search.
	 *
	 * @param orderSearchCriteria the order search criteria.
	 * @param stores the list of store filters to be returned
	 * @param resultType the result type
	 * @return the criteria used to complete order search
	 */
	CriteriaQuery getOrderSearchCriteria(OrderSearchCriteria orderSearchCriteria,
			Collection<String> stores, ResultType resultType);


	/**
	 * Return the criteria used for order return search.
	 *
	 * @param orderReturnSearchCriteria the order return search criteria.
	 * @param resultType the result type
	 * @return the query string used to complete order return search
	 */
	CriteriaQuery getOrderReturnSearchCriteria(OrderReturnSearchCriteria orderReturnSearchCriteria, ResultType resultType);
}
