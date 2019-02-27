/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.common.dto.datapolicy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

/**
 *	Tests DataPolicyDTO methods.
 */
public class DataPolicyDTOTest {

	private static final String KEY = "key";
	private static final String GUID = "guid";
	private static final String GUID1 = "guid1";

	/**
	 * Tests correct getDataPointGuids().
	 */
	@Test
	public void testDataPolicyGetDataPointGuidsReturnsCorrectList() {
		DataPointDTO dataPointDTO = new DataPointDTO();
		dataPointDTO.setGuid(GUID);
		dataPointDTO.setDescriptionKey(KEY);
		dataPointDTO.setDataKey(KEY);
		DataPointDTO dataPointDTO1 = new DataPointDTO();
		dataPointDTO1.setGuid(GUID1);
		dataPointDTO1.setDescriptionKey(KEY);
		dataPointDTO1.setDataKey(KEY);
		List<DataPointDTO> dataPoints = Arrays.asList(dataPointDTO, dataPointDTO1);
		DataPolicyDTO dataPolicyDTO = new DataPolicyDTO();
		dataPolicyDTO.setDataPoints(dataPoints);

		Collection<String> dataPointGuids = Arrays.asList(GUID, GUID1);

		assertThat(dataPointGuids).isEqualTo(dataPolicyDTO.getDataPointGuids());
	}

	/**
	 * Tests incorrect getDataPointGuids().
	 */
	@Test
	public void testIncorrectDataPolicyGetDataPointGuidsReturnsIncorrectList() {
		DataPointDTO dataPointDTO = new DataPointDTO();
		dataPointDTO.setGuid(GUID);
		dataPointDTO.setDescriptionKey(KEY);
		dataPointDTO.setDataKey(KEY);
		DataPointDTO dataPointDTO1 = new DataPointDTO();
		dataPointDTO1.setGuid(GUID1);
		dataPointDTO1.setDescriptionKey(KEY);
		dataPointDTO1.setDataKey(KEY);
		List<DataPointDTO> dataPoints = Arrays.asList(dataPointDTO, dataPointDTO1);
		DataPolicyDTO dataPolicyDTO = new DataPolicyDTO();
		dataPolicyDTO.setDataPoints(dataPoints);

		List<String> incorrectDataPointGuids = Arrays.asList(GUID);

		assertThat(incorrectDataPointGuids).isNotEqualTo(dataPolicyDTO.getDataPointGuids());
	}

}
