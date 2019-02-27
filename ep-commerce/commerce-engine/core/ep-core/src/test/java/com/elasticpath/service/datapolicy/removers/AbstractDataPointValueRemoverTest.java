/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.datapolicy.removers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;

import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * Common test class for all data point value removers.
 */
public abstract class AbstractDataPointValueRemoverTest {
	private static final String LIST_NAME = "uidpks";
	private static final Long DPV_1_UIDPK = 1L;
	private static final Long DPV_2_UIDPK = 2L;

	@Mock
	private DataPointValueService dataPointValueService;

	private DataPointValue dpv1;
	private DataPointValue dpv2;

	private Collection<Long> uidPks1;
	private Collection<Long> uidPks2;

	@Before
	public void init() {
		dpv1 = createDataPointValue(DPV_1_UIDPK, getDPV1Field());
		dpv2 = createDataPointValue(DPV_2_UIDPK, getDPV2Field());

		uidPks1 = Collections.singletonList(DPV_1_UIDPK);
		uidPks2 = Arrays.asList(DPV_1_UIDPK, DPV_2_UIDPK);
	}

	@Test
	public void shouldRemoveOneDataPointValue() {
		List<DataPointValue> dataPointValues = Collections.singletonList(dpv1);
		String expectedRemoveQuery = getRemover().getRemoveQuery(dataPointValues);

		when(dataPointValueService.removeValuesByQuery(eq(expectedRemoveQuery), eq(LIST_NAME), eq(uidPks1), any()))
			.thenReturn(1);

		int numOfUpdatedRecords = getRemover().removeValues(dataPointValues);

		assertThat(numOfUpdatedRecords)
			.as("One record should be updated")
			.isEqualTo(1);

		verify(dataPointValueService).removeValuesByQuery(eq(expectedRemoveQuery), eq(LIST_NAME), eq(uidPks1), any());
	}

	@Test
	public void shouldRemoveTwoDataPointValues() {
		List<DataPointValue> dataPointValues = Arrays.asList(dpv1, dpv2);
		String expectedRemoveQuery = getRemover().getRemoveQuery(dataPointValues);

		when(dataPointValueService.removeValuesByQuery(eq(expectedRemoveQuery), eq(LIST_NAME), eq(uidPks2), any()))
			.thenReturn(2);

		int numOfUpdatedRecords = getRemover().removeValues(dataPointValues);

		assertThat(numOfUpdatedRecords)
			.as("One record should be updated")
			.isEqualTo(2);

		verify(dataPointValueService).removeValuesByQuery(eq(expectedRemoveQuery), eq(LIST_NAME), eq(uidPks2), any());
	}

	@Test
	public void shouldBeApplicableForLocation() {
		assertThat(getRemover().isApplicableTo(getLocation()))
			.as("Should be applicable for", getLocation())
			.isTrue();
	}

	@Test
	public void shouldGenerateValidJPQLRemoveQueryForOneDataPointValue() {
		List<DataPointValue> dataPointValues = Collections.singletonList(dpv1);

		assertThat(getRemover().getRemoveQuery(dataPointValues))
			.as("JPQL remove query is invalid")
			.isEqualTo(getExpectedRemoveQuery(false));
	}

	@Test
	public void shouldGenerateValidJPQLRemoveQueryForTwoDataPointValuesWithDifferentKeys() {
		List<DataPointValue> dataPointValues = Arrays.asList(dpv1, dpv2);

		assertThat(getRemover().getRemoveQuery(dataPointValues))
			.as("JPQL remove query is invalid")
			.isEqualTo(getExpectedRemoveQuery(true));
	}

	@Test
	public void shouldGenerateValidJPQLRemoveQueryForTwoDataPointValuesWithSameKeys() {
		List<DataPointValue> dataPointValues = Arrays.asList(dpv1, dpv1);

		assertThat(getRemover().getRemoveQuery(dataPointValues))
			.as("JPQL remove query is invalid")
			.isEqualTo(getExpectedRemoveQuery(false));
	}

	private DataPointValue createDataPointValue(final Long uidPk, final String fieldName) {

		DataPointValue dpv = new DataPointValue();
		dpv.setUidPk(uidPk);
		dpv.setLocation(getLocation());
		dpv.setField(fieldName);

		return dpv;
	}

	/**
	 * Return field name for the data point value 1.
	 *
	 * @return the field name
	 */
	protected String getDPV1Field() {
		return "";
	}

	/**
	 * Return field name for the data point value 2.
	 *
	 * @return the field name
	 */
	protected String getDPV2Field() {
		return "";
	}

	/**
	 * The data point value location.
	 * @return the location
	 */
	protected abstract String getLocation();

	/**
	 * The implementation of {@link com.elasticpath.service.datapolicy.DataPointValueRemover}.
	 * @return the remover
	 */
	protected abstract AbstractDataPointValueRemover getRemover();

	/**
	 * The expected JPQL remove query of the specific remover.
	 *
	 * @param updateMoreFields true when updating multiple data point values.
	 * @return the JPQL remove query
	 */
	protected abstract String getExpectedRemoveQuery(boolean updateMoreFields);
}
