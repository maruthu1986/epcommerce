/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.persistence.support.impl;

import static java.util.stream.Collectors.toList;

import static com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup.JpqlMatchType.AS_IS;
import static com.elasticpath.service.search.query.SortOrder.ASCENDING;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.LongValidator;

import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup.ConjunctionType;
import com.elasticpath.persistence.support.OrderCriterion;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.EpInvalidOrderCriterionResultTypeException;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Implementation of OrderCriterion that constructs the appropriate criteria for order queries
 * using the JpqlQueryBuilder.
 */
@SuppressWarnings("PMD.GodClass")
public class OrderCriterionImpl implements OrderCriterion {
	private static final String ORDER = "OrderImpl";
	private static final String ORDER_STATUS = "o.status";
	private static final String ORDER_NUMBER = "o.orderNumber";
	private static final String CP_SHORT_TEXT_VALUE = "cp.shortTextValue";

	@Override
	public CriteriaQuery getStatusCriteria(final OrderStatus orderStatus, final OrderPaymentStatus paymentStatus,
										   final OrderShipmentStatus shipmentStatus) {
		JpqlQueryBuilder queryBuilder = new JpqlQueryBuilder(ORDER, "o");
		final JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
		appendEqualsClause(whereGroup, ORDER_STATUS, orderStatus);
		if (paymentStatus != null) {
			queryBuilder.appendInnerJoin("o.orderPayments", "op");
			whereGroup.appendWhereEquals("op.status", paymentStatus);
		}
		if (shipmentStatus != null) {
			queryBuilder.appendInnerJoin("o.shipments", "os");
			whereGroup.appendWhereEquals("os.status", shipmentStatus);
		}

		return new CriteriaQuery(queryBuilder);
	}

	@Override
	public CriteriaQuery getOrderCustomerCriteria(final String propertyName, final String criteriaValue, final boolean isExactMatch) {
		final JpqlQueryBuilder queryBuilder = new JpqlQueryBuilder(ORDER, "o");
		final JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
		final String fieldName = "o.customer." + propertyName;
		if (isExactMatch) {
			whereGroup.appendWhereEquals(fieldName, criteriaValue);
		} else {
			whereGroup.appendLikeWithWildcards(fieldName, criteriaValue);
		}
		return new CriteriaQuery(queryBuilder);
	}

	@Override
	public CriteriaQuery getOrderCustomerProfileCriteria(final String attributeKey, final String attributeValue, final boolean isExactMatch) {
		final JpqlQueryBuilder queryBuilder = new JpqlQueryBuilder(ORDER, "o");
		final JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
		queryBuilder.appendInnerJoin("o.customer", "c");
		queryBuilder.appendInnerJoin("c.profileValueMap", "cp");
		whereGroup.appendWhereEquals("cp.localizedAttributeKey", attributeKey);
		if (isExactMatch) {
			whereGroup.appendWhereEquals(CP_SHORT_TEXT_VALUE, attributeValue);
		} else {
			whereGroup.appendLikeWithWildcards(CP_SHORT_TEXT_VALUE, attributeValue);
		}
		return new CriteriaQuery(queryBuilder);
	}

	@Override
	public CriteriaQuery getOrderGiftCertificateCriteria(final String propertyName, final String criteriaValue, final boolean isExactMatch) {
		final JpqlQueryBuilder queryBuilder = new JpqlQueryBuilder(ORDER, "o");
		final JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
		final String fieldName = "op.giftCertificate." + propertyName;
		queryBuilder.appendLeftJoin("o.orderPayments", "op");
		if (isExactMatch) {
			whereGroup.appendWhereEquals(fieldName, criteriaValue);
		} else {
			whereGroup.appendLikeWithWildcards(fieldName, criteriaValue);
		}
		return new CriteriaQuery(queryBuilder);
	}

	@Override
	public CriteriaQuery getOrderSearchCriteria(final OrderSearchCriteria orderSearchCriteria,
										 final Collection<String> stores, final ResultType resultType) {
		JpqlQueryBuilder queryBuilder = createOrderQueryBuilder(resultType);

		final CustomerSearchCriteria customerSearchCriteria = orderSearchCriteria.getCustomerSearchCriteria();
		final OrderShipmentStatus shipmentStatus = orderSearchCriteria.getShipmentStatus();
		final String skuCode = orderSearchCriteria.getSkuCode();

		if (shipmentStatus != null || StringUtils.isNotEmpty(skuCode)) {
			queryBuilder.appendInnerJoin("o.shipments", "os");
		}

		final JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();

		appendEqualsClause(whereGroup, ORDER_NUMBER, orderSearchCriteria.getOrderNumber());
		appendEqualsClause(whereGroup, ORDER_STATUS, orderSearchCriteria.getOrderStatus());
		if (orderSearchCriteria.getExcludedOrderStatus() != null) {
			whereGroup.appendWhere(ORDER_STATUS, "<>", orderSearchCriteria.getExcludedOrderStatus(), AS_IS);
		}
		appendEqualsClause(whereGroup, "os.status", orderSearchCriteria.getShipmentStatus());

		addCustomerClauses(queryBuilder, customerSearchCriteria, whereGroup);

		appendLikeClause(whereGroup, "UPPER(o.billingAddress.zipOrPostalCode)", StringUtils.upperCase(orderSearchCriteria.getShipmentZipcode()));

		appendEqualsClauseWithJoin(queryBuilder, whereGroup, "os.shipmentOrderSkusInternal", "sku", "sku.skuCode", skuCode);
		appendEqualsClauseWithJoin(queryBuilder, whereGroup, "o.returns", "ort", "ort.rmaCode", orderSearchCriteria.getRmaCode());
		if (orderSearchCriteria.getOrderToDate() != null) {
			whereGroup.appendWhere("o.createdDate", "<=", orderSearchCriteria.getOrderToDate(), AS_IS);
		}
		if (orderSearchCriteria.getOrderFromDate() != null) {
			whereGroup.appendWhere("o.createdDate", ">=", orderSearchCriteria.getOrderFromDate(), AS_IS);
		}
		addStoreCodeClauses(orderSearchCriteria, whereGroup);

		if (!ResultType.COUNT.equals(resultType)) {
			addOrderBy(orderSearchCriteria, queryBuilder);
		}

		return new CriteriaQuery(queryBuilder);
	}

	private void addCustomerClauses(final JpqlQueryBuilder queryBuilder, final CustomerSearchCriteria customerSearchCriteria,
									final JpqlQueryBuilderWhereGroup whereGroup) {
		if (customerSearchCriteria != null) {
			Long customerNumber = LongValidator.getInstance().validate(customerSearchCriteria.getCustomerNumber());
			appendEqualsClause(whereGroup, "o.customer.uidPk", customerNumber);
			appendEqualsClause(whereGroup, "o.customer.guid", customerSearchCriteria.getGuid());
			appendLikeClause(whereGroup, "UPPER(o.customer.userId)", StringUtils.upperCase(customerSearchCriteria.getUserId()));

			if (StringUtils.isNotEmpty(customerSearchCriteria.getFirstName())) {
				queryBuilder.appendLeftJoin("o.customer.profileValueMap", "cpf");
				addFirstNameClause(customerSearchCriteria.getFirstName().toUpperCase(), whereGroup);
			}

			if (StringUtils.isNotEmpty(customerSearchCriteria.getLastName())) {
				queryBuilder.appendLeftJoin("o.customer.profileValueMap", "cpl");
				addLastNameClause(customerSearchCriteria.getLastName().toUpperCase(), whereGroup);
			}

			appendLikeClause(whereGroup, "o.billingAddress.phoneNumber", customerSearchCriteria.getPhoneNumber());

		}
	}

	private void addStoreCodeClauses(final OrderSearchCriteria orderSearchCriteria, final JpqlQueryBuilderWhereGroup whereGroup) {
		Collection<String> codes = orderSearchCriteria.getStoreCodes();
		if (codes != null && !codes.isEmpty()) {
			final List<String> ucStoreCodes = codes.stream()
				.map(StringUtils::upperCase)
				.collect(toList());
			whereGroup.appendWhereInCollection("UPPER(o.storeCode)", ucStoreCodes);
		}
	}

	private void addFirstNameClause(final String firstName, final JpqlQueryBuilderWhereGroup whereGroup) {
		whereGroup.appendWhereEquals("cpf.localizedAttributeKey", CustomerImpl.ATT_KEY_CP_FIRST_NAME);
		final JpqlQueryBuilderWhereGroup firstNameWhere = new JpqlQueryBuilderWhereGroup(ConjunctionType.OR);
		firstNameWhere.appendLikeWithWildcards("UPPER(o.billingAddress.firstName)", firstName);
		firstNameWhere.appendLikeWithWildcards("UPPER(cpf.shortTextValue)", firstName);
		whereGroup.appendWhereGroup(firstNameWhere);
	}

	private void addLastNameClause(final String lastName, final JpqlQueryBuilderWhereGroup whereGroup) {
		whereGroup.appendWhereEquals("cpl.localizedAttributeKey", CustomerImpl.ATT_KEY_CP_LAST_NAME);
		final JpqlQueryBuilderWhereGroup lastNameWhere = new JpqlQueryBuilderWhereGroup(ConjunctionType.OR);
		lastNameWhere.appendLikeWithWildcards("UPPER(o.billingAddress.lastName)", lastName);
		lastNameWhere.appendLikeWithWildcards("UPPER(cpl.shortTextValue)", lastName);
		whereGroup.appendWhereGroup(lastNameWhere);
	}

	/**
	 * Add the order by clauses based on the search criteria.
	 *
	 * @param orderSearchCriteria the criteria that includes the sorting requirements
	 * @param queryBuilder the query builder being used to build the query
	 */
	protected void addOrderBy(final OrderSearchCriteria orderSearchCriteria, final JpqlQueryBuilder queryBuilder) {
		boolean ascending = ASCENDING.equals(orderSearchCriteria.getSortingOrder());
		switch (orderSearchCriteria.getSortingType().getOrdinal()) {
			case StandardSortBy.DATE_ORDINAL:
				queryBuilder.appendOrderBy("o.createdDate", ascending);
				break;
			case StandardSortBy.STATUS_ORDINAL:
				queryBuilder.appendOrderBy(ORDER_STATUS, ascending);
				break;
			case StandardSortBy.CUSTOMER_ID_ORDINAL:
				queryBuilder.appendOrderBy("o.customer.userId", ascending);
				break;
			case StandardSortBy.CUSTOMER_NAME_ORDINAL:
				queryBuilder.appendOrderBy("o.billingAddress.firstName", ascending);
				queryBuilder.appendOrderBy("o.billingAddress.lastName", ascending);
				break;
			case StandardSortBy.PHONE_ORDINAL:
				queryBuilder.appendOrderBy("o.billingAddress.phoneNumber", ascending);
				break;
			case StandardSortBy.STORE_CODE_ORDINAL:
				queryBuilder.appendOrderBy("o.storeCode", ascending);
				break;
			case StandardSortBy.TOTAL_ORDINAL:
				queryBuilder.appendOrderBy("o.currency", false);
				queryBuilder.appendOrderBy("o.total", ascending);
				break;
			default:
				break;
		}
		queryBuilder.appendOrderBy(ORDER_NUMBER, ascending);
	}

	private void appendEqualsClause(final JpqlQueryBuilderWhereGroup whereGroup, final String fieldName, final String value) {
		if (StringUtils.isNotEmpty(value)) {
			whereGroup.appendWhereEquals(fieldName, value);
		}
	}


	private void appendLikeClause(final JpqlQueryBuilderWhereGroup whereGroup, final String fieldName, final String value) {
		if (StringUtils.isNotEmpty(value)) {
			whereGroup.appendLikeWithWildcards(fieldName, value);
		}
	}
	private void appendEqualsClause(final JpqlQueryBuilderWhereGroup whereGroup, final String fieldName, final Object value) {
		if (value != null) {
			whereGroup.appendWhereEquals(fieldName, value);
		}
	}

	private void appendEqualsClauseWithJoin(final JpqlQueryBuilder queryBuilder, final JpqlQueryBuilderWhereGroup whereGroup,
												 final String relatedField, final String alias,
												 final String fieldName, final String value) {
		if (StringUtils.isNotEmpty(value)) {
			queryBuilder.appendInnerJoin(relatedField, alias);
			whereGroup.appendWhereEquals(fieldName, value);
		}
	}

	@Override
	public CriteriaQuery getOrderReturnSearchCriteria(final OrderReturnSearchCriteria orderReturnSearchCriteria, final ResultType resultType) {

		JpqlQueryBuilder queryBuilder = createOrderReturnQueryBuilder(resultType);

		String orderNumber = orderReturnSearchCriteria.getOrderNumber();
		final boolean hasCustomerCriteria = hasCustomerSearchCriteria(orderReturnSearchCriteria);
		if (StringUtils.isNotBlank(orderNumber) || hasCustomerCriteria) {
			queryBuilder.appendInnerJoin("ort.order", "o");
		}

		final JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
		appendEqualsClause(whereGroup, "ort.rmaCode", orderReturnSearchCriteria.getRmaCode());
		appendEqualsClause(whereGroup, ORDER_NUMBER, orderNumber);
		if (hasCustomerCriteria) {
			appendLikeClause(whereGroup, "o.billingAddress.firstName", orderReturnSearchCriteria.getCustomerSearchCriteria().getFirstName());
			appendLikeClause(whereGroup, "o.billingAddress.lastName", orderReturnSearchCriteria.getCustomerSearchCriteria().getLastName());
		}

		if (resultType != ResultType.COUNT) {
			boolean ascending = ASCENDING.equals(orderReturnSearchCriteria.getSortingOrder());
			switch (orderReturnSearchCriteria.getSortingType().getOrdinal()) {
				case StandardSortBy.DATE_ORDINAL:
					queryBuilder.appendOrderBy("ort.createdDate", ascending);
					break;
				case StandardSortBy.STATUS_ORDINAL:
					queryBuilder.appendOrderBy("ort.returnStatus", ascending);
					break;
				case StandardSortBy.ORDER_NUMBER_ORDINAL:
					queryBuilder.appendOrderBy(ORDER_NUMBER, ascending);
					break;
				default:
					queryBuilder.appendOrderBy("ort.rmaCode", ascending);
					break;
			}
		}

		return new CriteriaQuery(queryBuilder);
	}

	private boolean hasCustomerSearchCriteria(final OrderReturnSearchCriteria orderReturnSearchCriteria) {
		CustomerSearchCriteria customerSearchCriteria = orderReturnSearchCriteria.getCustomerSearchCriteria();
		return customerSearchCriteria != null
			   && (StringUtils.isNotBlank(customerSearchCriteria.getFirstName()) || StringUtils.isNotBlank(customerSearchCriteria.getLastName()));
	}

	private JpqlQueryBuilder createOrderQueryBuilder(final ResultType resultType) {
		if (resultType == ResultType.ORDER_NUMBER) {
			return new JpqlQueryBuilder(ORDER, "o", ORDER_NUMBER);
		}

		JpqlQueryBuilder queryBuilder = new JpqlQueryBuilder(ORDER, "o");
		if (resultType == ResultType.COUNT) {
			queryBuilder.count();
		}

		return queryBuilder;
	}

	private JpqlQueryBuilder createOrderReturnQueryBuilder(final ResultType resultType) {
		JpqlQueryBuilder queryBuilder = new JpqlQueryBuilder("OrderReturnImpl", "ort");
		if (resultType == ResultType.COUNT) {
			queryBuilder.count();
		} else if (resultType != ResultType.ENTITY) {
			throw new EpInvalidOrderCriterionResultTypeException("Invalid result type for order return.");
		}
		return queryBuilder;
	}

}
