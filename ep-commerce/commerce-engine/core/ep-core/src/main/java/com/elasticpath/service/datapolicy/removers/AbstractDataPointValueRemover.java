/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.removers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.service.datapolicy.DataPointValueRemover;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;
import com.elasticpath.service.search.IndexType;

/**
 * The abstract data point value remover.
 */
public abstract class AbstractDataPointValueRemover implements DataPointValueRemover {
	private static final String LIST_NAME = "uidpks";
	/** Default value for removed data point values. */
	static final String HYPHEN = "‐";

	private DataPointValueService dataPointValueService;

	@Override
	public int removeValues(final Collection<DataPointValue> dataPointValues) {
		Collection<Long> entityUidPks = getDataPointValueUidPks(dataPointValues);

		return dataPointValueService.removeValuesByQuery(getRemoveQuery(dataPointValues), LIST_NAME, entityUidPks, getIndexType());
	}

	/**
	 * Every remover must provide a dynamic query used for removing data point values.
	 * The removal doesn't need to be deletion of a record per se. It could be a record update where selected
	 * fields will be set to NULL.
	 *
	 * @param dataPointValues the list of data point value unique ids.
	 * @return the remove query.
	 */
	protected String getRemoveQuery(final Collection<DataPointValue> dataPointValues) {
		Pair<String, String> entityAliasPair = getJPQLUpdate();

		String entity = entityAliasPair.getFirst();
		String alias = entityAliasPair.getSecond();

		return new StringBuilder("UPDATE ")
			.append(entity).append(' ').append(alias).append(' ')
			.append(getUpdateSetForEntity(dataPointValues, alias)).append(' ')
			.append(getWhereClauseForEntity(alias))
			.toString();
	}

	/**
	 * Create a JPQL Update part consisting of target entity class and its alias. Each remover implements its own version.
	 * @return the JPQL Update part or null if {@link #getRemoveQuery(Collection)} is overridden
	 */
	protected Pair<String, String> getJPQLUpdate() {
		return null;
	}

	/**
	 * Create a JPQL WHERE clause like
	 * WHERE <strong>entity</strong>.uidPk IN (:list).
	 *
	 * @param alias the JPA entity alias e.g. custAddress
	 * @return the WHERE clause
	 */
	protected String getWhereClauseForEntity(final String alias) {
		return new StringBuilder("WHERE ")
			.append(alias).append(".uidPk IN (:").append(LIST_NAME).append(")")
			.toString();
	}

	/**
	 * The SOLR indexing needs to be triggered for certain entities (e..g Customer).
	 * @return null, if not required; otherwise one of the supported {@link IndexType}.
	 */
	protected IndexType getIndexType() {
		return null;
	}

	/**
	 * Return the CSV string of UidPKs for given a list of {@link DataPointValue}.
	 * @param dataPointValues the list of {@link DataPointValue}
	 * @return the CSV
	 */
	@VisibleForTesting
	protected Collection<Long> getDataPointValueUidPks(final Collection<DataPointValue> dataPointValues) {
		return dataPointValues.stream()
			.map(DataPointValue::getUidPk)
			.collect(Collectors.toList());
	}

	/**
	 * Create a JPQL Update <strong>SET</strong> with multiple fields
	 * e.g SET custAddress.firstName=null, custAddress.lastName=null.
	 *
	 * @param dataPointValues the list of {@link DataPointValue}
	 * @param alias the JPA entity alias e.g. custAddress
	 * @return the JPQL SET
	 */
	protected String getUpdateSetForEntity(final Collection<DataPointValue> dataPointValues, final String alias) {
		//the predicate is used to filter out already added fields (e.g. order.ipAddress must appear only once)
		List<String> alreadyAddedFields = new ArrayList<>();
		Predicate<DataPointValue> isAlreadyAddedFiledPredicate = dpv -> {
			if (alreadyAddedFields.contains(dpv.getField())) {
				return false;
			}
			alreadyAddedFields.add(dpv.getField());
			return true;
		};

		//must update last modified date
		String updateSetPart = "SET " + alias + ".lastModifiedDate=CURRENT_TIMESTAMP, ";
		//e..g SET address.firstName=null, address.lastName=null etc
		return  updateSetPart + getCSVFieldsForUpdate(dataPointValues, alias, isAlreadyAddedFiledPredicate);
	}

	private String getCSVFieldsForUpdate(final Collection<DataPointValue> dataPointValues, final String alias,
		final Predicate<DataPointValue> isAlreadyAddedFiledPredicate) {

		return dataPointValues.stream()
			.filter(isAlreadyAddedFiledPredicate)
			.map(dpv -> alias + "." + dpv.getField() + "='" + HYPHEN + "'")
			.collect(Collectors.joining(","));
	}

	public DataPointValueService getDataPointValueService() {
		return dataPointValueService;
	}

	public void setDataPointValueService(final DataPointValueService dataPointValueService) {
		this.dataPointValueService = dataPointValueService;
	}
}
