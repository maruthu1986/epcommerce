/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.common.dto.datapolicy;


import com.google.common.testing.EqualsTester;
import org.junit.Test;

/**
 * Tests DataPointDTO methods.
 */
public class DataPointDTOTest {

	private static final String KEY = "key";
	private static final String GUID = "guid";

	@Test
	public void testEqualsHashCode() {
		DataPointDTO dataPointDTO1 = getDataPointDTO();

		DataPointDTO dataPointDTO2 = getDataPointDTO("", "1");
		DataPointDTO dataPointDTO3 = getDataPointDTO("1", "1");

		new EqualsTester()
				.addEqualityGroup(dataPointDTO1, dataPointDTO1, dataPointDTO2)
				.addEqualityGroup(dataPointDTO3)
				.testEquals();
	}

	private DataPointDTO getDataPointDTO() {
		return getDataPointDTO("", "");
	}

	private DataPointDTO getDataPointDTO(final String uniqueGuid, final String uniqueExtraData) {
		DataPointDTO dataPointDTO = new DataPointDTO();
		dataPointDTO.setGuid(GUID + uniqueGuid);
		dataPointDTO.setDescriptionKey(KEY + uniqueExtraData);
		dataPointDTO.setDataKey(KEY + uniqueExtraData);
		return dataPointDTO;
	}
}
