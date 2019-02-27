package com.elasticpath.service.datapolicy.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class to ensure no clash between {@link DataPointValue} objects.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataPointValueTest {

	private static final String NAME_1 = "Customer shipping address street 1";
	private static final String NAME_2 = "Customer shipping address street 2";

	private static final long UIDPK_1 = 200000;
	private static final long UIDPK_2 = 200001;

	@Test
	public void verifyDataPointValuesEqualsWhenUidPkAndNameEqual() {
		DataPointValue dataPointValue1 = createDataPointValue(NAME_1, UIDPK_1);
		DataPointValue dataPointValue2 = createDataPointValue(NAME_1, UIDPK_1);

		assertThat(dataPointValue1)
				.isEqualTo(dataPointValue2);
	}

	@Test
	public void verifyDataPointValuesNotEqualsWhenUidPkNotEqual() {
		DataPointValue dataPointValue1 = createDataPointValue(NAME_1, UIDPK_1);
		DataPointValue dataPointValue2 = createDataPointValue(NAME_1, UIDPK_2);

		assertThat(dataPointValue1)
				.isNotEqualTo(dataPointValue2);
	}

	@Test
	public void verifyDataPointValuesNotEqualsWhenNameNotEqual() {
		DataPointValue dataPointValue1 = createDataPointValue(NAME_1, UIDPK_1);
		DataPointValue dataPointValue2 = createDataPointValue(NAME_2, UIDPK_1);

		assertThat(dataPointValue1)
				.isNotEqualTo(dataPointValue2);
	}


	private DataPointValue createDataPointValue(final String name, final long uidpk) {
		DataPointValue dataPointValue = new DataPointValue();
		dataPointValue.setDataPointName(name);
		dataPointValue.setUidPk(uidpk);
		return dataPointValue;
	}
}