/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.pricing.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.pricing.dao.BaseAmountDao;

/**
 * DAO for data operations on BaseAmount objects.
 */
public class BaseAmountDaoImpl implements BaseAmountDao {

	//Total batch size is 2099 to remain under the MSSQL 2100 parameter limit.
	private static final int OBJ_BATCH_SIZE = 1999;
	private static final int PL_BATCH_SIZE = 100;
	private static final String LIST_PLACEHOLDER_NAME = "list";
	private PersistenceEngine persistenceEngine;

	@Override
	public void delete(final BaseAmount baseAmount) {
		getPersistenceEngine().delete(baseAmount);
		getPersistenceEngine().flush();
	}

	@Override
	public void delete(final String priceListDescriptorGuid) {
		getPersistenceEngine().executeNamedQuery("DELETE_BASE_AMOUNTS_BY_PRICE_LIST_GUID", priceListDescriptorGuid);
	}

	/**
	 * {@inheritDoc}
	 *
	 * BASE_AMOUNTS_BY_EXT_FILTER and BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE
	 * allowed as named query name.
	 *
	 */
	@Override
	public List<BaseAmount> findBaseAmounts(
			final String namedQuery,
			final Object[] searchCriteria,
			final int limit,  final List<String> guids) {
		return findBaseAmounts(namedQuery, searchCriteria, 0, limit, guids);
	}

	/**
	 * {@inheritDoc}
	 *
	 * BASE_AMOUNTS_BY_EXT_FILTER and BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE
	 * allowed as named query name.
	 *
	 */
	@Override
	public List<BaseAmount> findBaseAmounts(
			final String namedQuery,
			final Object[] searchCriteria,
			final int startIndex, final int pageSize,
			final List<String> guids) {
		return getPersistenceEngine().retrieveByNamedQueryWithList(namedQuery, LIST_PLACEHOLDER_NAME, guids, searchCriteria, startIndex, pageSize);
	}

	@Override
	public List<BaseAmount> findBaseAmounts(final String priceListDescriptorGuid, final String objectType, final String ... objectGuids) {

		final Map<String, Object> parameterValueMap = new HashMap<>();
		parameterValueMap.put("priceListDescriptorGuid", priceListDescriptorGuid);
		parameterValueMap.put("objectType", objectType);

		List<BaseAmount> baseAmounts = new ArrayList<>();
		List<List<String>> batchesOfObjGuids = Lists.partition(Arrays.asList(objectGuids), OBJ_BATCH_SIZE);
		for (List<String> batchOfObjGuids: batchesOfObjGuids) {
			parameterValueMap.put("objectGuids", batchOfObjGuids);

			List<BaseAmount> queryResults = getPersistenceEngine().retrieveByNamedQuery(
					"BASE_AMOUNTS_BY_PRICELIST_BY_OBJECTS",
					parameterValueMap);
			baseAmounts.addAll(queryResults);
		}

		return baseAmounts;
	}

	@Override
	public Collection<BaseAmount> findBaseAmounts(final BaseAmountFilter filter) {
		final BaseAmountJPQLBuilder queryBuilder = new BaseAmountJPQLBuilder(filter);

		final Collection<BaseAmount> amounts = getPersistenceEngine().retrieve(queryBuilder.toString());
		if (amounts == null) {
			return new ArrayList<>();
		}
		return amounts;
	}

	@Override
	public void deleteBaseAmounts(final String objectGuid, final String objectType) {
		getPersistenceEngine().executeNamedQuery("DELETE_BASE_AMOUNTS", objectGuid, objectType);
	}

	@Override
	public BaseAmount findBaseAmountByGuid(final String guid) {
		final List<BaseAmount> baseAmounts =
				getPersistenceEngine().retrieveByNamedQuery("BASE_AMOUNT_BY_GUID", guid);
		if (!baseAmounts.isEmpty()) {
			return baseAmounts.get(0);
		}
		return null;
	}

	@Override
	public List<BaseAmount> getBaseAmounts(final List<String> plGuids, final List<String> objectGuids) {
		List<BaseAmount> baseAmounts = new ArrayList<>();

		List<List<String>> batchesOfPriceLists = Lists.partition(plGuids, PL_BATCH_SIZE);
		for (List<String> batchOfPlGuids : batchesOfPriceLists) {
			List<List<String>> batchesOfObjGuids = Lists.partition(objectGuids, OBJ_BATCH_SIZE);
			for (List<String> batchOfObjGuids : batchesOfObjGuids) {
				final Map<String, Collection<String>> parameterValueMap = new HashMap<>();
				parameterValueMap.put("pricelists", batchOfPlGuids);
				parameterValueMap.put("productskus", batchOfObjGuids);

				List<BaseAmount> queryResults = getPersistenceEngine().retrieveByNamedQueryWithList(
						"BASE_AMOUNT_BY_PLDG_OBJECTG", parameterValueMap);
				baseAmounts.addAll(queryResults);
			}
		}
		return baseAmounts;
	}

	@Override
	public BaseAmount add(final BaseAmount baseAmount) throws EpPersistenceException {
		BaseAmount updatedBaseAmount;
		try {
			updatedBaseAmount = getPersistenceEngine().saveOrUpdate(baseAmount);
		} catch (final Exception ex) {
			throw new EpPersistenceException("Exception on adding baseAmount.", ex);
		}
		return updatedBaseAmount;
	}

	/**
	 * Saves an updated BaseAmount. Persistence is not checked.
	 *
	 * @param updatedBaseAmount the BaseAmount that will replace the existing one
	 * @return the updated BaseAmount
	 * @throws EpPersistenceException if object is not persistent
	 */
	@Override
	public BaseAmount update(final BaseAmount updatedBaseAmount) {
		if (!updatedBaseAmount.isPersisted() || StringUtils.isEmpty(updatedBaseAmount.getGuid())) {
			throw new EpPersistenceException("Object is not persistent");
		}
		return getPersistenceEngine().saveOrUpdate(updatedBaseAmount);
	}

	/**
	 * Sets the persistence engine to use.
	 *
	 * @param persistenceEngine The persistence engine.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Gets the persistence engine.
	 *
	 * @return The persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	@Override
	public boolean guidExists(final String guid) {
		if (guid == null) {
			return false;
		}
		final List<String> baseAmountGuids = getPersistenceEngine().retrieveByNamedQuery("BASE_AMOUNT_GUID_SELECT_BY_GUID", guid);

		return !baseAmountGuids.isEmpty();
	}

	/**
	 * JPA Query builder for base amounts.
	 */
	static class BaseAmountJPQLBuilder {
		private final String query;
		private boolean multipleStatements;
		private static final String SELECT = "SELECT baseAmount FROM BaseAmountImpl AS baseAmount";

		/**
		 * Builder for BaseAmount queries.
		 *
		 * @param filter the query filter to transform into a query string.
		 */
		BaseAmountJPQLBuilder(final BaseAmountFilter filter) {
			StringBuilder queryBuilder = new StringBuilder(SELECT);

			queryBuilder.append(appendParameters("objectGuid", filter.getObjectGuid()));
			queryBuilder.append(appendParameters("objectType", filter.getObjectType()));
			queryBuilder.append(appendParameters("priceListDescriptorGuid", filter.getPriceListDescriptorGuid()));
			queryBuilder.append(appendParameters("listValueInternal", filter.getListValue()));
			queryBuilder.append(appendParameters("saleValueInternal", filter.getSaleValue()));
			queryBuilder.append(appendParameters("quantityInternal", filter.getQuantity()));
			this.query = queryBuilder.toString();
		}

		/**
		 * Append parameters to the query.
		 * @param field the field of baseAmount to add a criteria to
		 * @param criteria to match against
		 * @return partial query criteria
		 */
		private String appendParameters(final String field, final Object criteria) {
			if (criteria != null && !"null".equals(criteria) && !StringUtils.EMPTY.equals(criteria)) {
				final StringBuilder queryBuilder = new StringBuilder();
				queryBuilder.append(getPrefix()).append("baseAmount.").append(field).append(" = ");
				if (criteria instanceof BigDecimal) {
					return queryBuilder.append(String.valueOf(((BigDecimal) criteria).doubleValue())).toString();
				} else if (criteria instanceof String) {
					return queryBuilder.append('\'').append(criteria).append('\'').toString();
				}
			}
			return StringUtils.EMPTY;
		}

		private String getPrefix() {
			if (multipleStatements) {
				return " AND ";
			}
			multipleStatements = true;
			return " WHERE ";
		}

		@Override
		public String toString() {
			return query;
		}
	}
}
