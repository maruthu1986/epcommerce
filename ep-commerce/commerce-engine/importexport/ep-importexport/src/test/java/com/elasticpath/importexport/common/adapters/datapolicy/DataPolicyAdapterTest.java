/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.common.adapters.datapolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.datapolicy.DataPointDTO;
import com.elasticpath.common.dto.datapolicy.DataPolicyDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.domain.datapolicy.impl.DataPointImpl;
import com.elasticpath.domain.datapolicy.impl.DataPolicyImpl;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.service.datapolicy.DataPointService;

/**
 * Tests for DataPolicyAdapter.
 */
@RunWith(MockitoJUnitRunner.class)
public class DataPolicyAdapterTest {

	private static final String EXPECTED_DTO_SHOULD_EQUAL_ACTUAL = "The assembled data policy DTO should be equal to the expected data policy DTO.";

	private static final String EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL =
			"The assembled data policy domain object should be equal to the expected data policy domain object.";

	private static final String DATA_POLICY_GUID = "data_policy_guid";

	private static final String POLICY_NAME = "policyName";
	private static final Integer RETENTION_PERIOD_IN_DAYS = 100;
	private static final RetentionType RETENTION_TYPE = RetentionType.FROM_CREATION_DATE;
	private static final Date START_DATE = new Date();
	private static final Date END_DATE = new Date();
	private static final DataPolicyState STATE = DataPolicyState.DRAFT;
	private static final String DESCRIPTION = "description";

	private static final String DATA_POINT_GUID = "data_point_guid";
	private static final String DATA_KEY = "dataKey";
	private static final String DESCRIPTION_KEY = "descriptionKey";
	private static final String DESCRIPTION_KEY_UPPER_CASE = DESCRIPTION_KEY.toUpperCase(Locale.US);
	private static final String DATA_LOCATION = "dataLocation";
	public static final String DATA_POINT_NAME = "DATA_POINT_NAME";

	@Mock
	private DataPointService dataPointService;

	@Mock
	private BeanFactory beanFactory;

	@InjectMocks
	private DataPolicyAdapter dataPolicyAdapter;

	/**
	 * Test data policy DTO assembly from domain object.
	 */
	@Test
	public void testPopulateDTO() {
		DataPolicy testDataPolicy = createDataPolicy(null);
		DataPolicyDTO expectedDataPolicyDTO = createDataPolicyDTO(null);

		DataPolicyDTO dataPolicyDTO = new DataPolicyDTO();

		dataPolicyAdapter.populateDTO(testDataPolicy, dataPolicyDTO);

		assertThat(expectedDataPolicyDTO)
				.as(EXPECTED_DTO_SHOULD_EQUAL_ACTUAL)
				.isEqualToComparingFieldByField(dataPolicyDTO);
	}

	/**
	 * Test data policy DTO assembly from domain object is not the same.
	 */
	@Test
	public void testDataPolicyAssembleDtoFromDomainObjectNotEquals() {
		DataPolicy testDataPolicy = createDataPolicy("different Name");
		DataPolicyDTO expectedDataPolicyDTO = createDataPolicyDTO(null);

		DataPolicyDTO dataPolicyDTO = new DataPolicyDTO();

		dataPolicyAdapter.populateDTO(testDataPolicy, dataPolicyDTO);

		assertThat(expectedDataPolicyDTO.getPolicyName())
				.as("Unexpected data policy DTO created by assembler")
				.isNotSameAs(dataPolicyDTO.getPolicyName());
	}

	/**
	 * Tests buildDomain.
	 */
	@Test
	public void testBuildDomain() {
		DataPolicyDTO dataPolicyDTO = createDataPolicyDTO(null);
		DataPolicy expectedDataPolicy = createDataPolicy(null);

		DataPolicy dataPolicy = new DataPolicyImpl();

		dataPolicyAdapter.buildDomain(dataPolicyDTO, dataPolicy);

		assertThat(expectedDataPolicy)
				.as(EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL)
				.isEqualToComparingFieldByField(dataPolicy);
	}

	/**
	 * Tests buildDomain with new data point.
	 */
	@Test
	public void testBuildDomainWithNewDataPoint() {
		DataPolicyDTO dataPolicyDTO = createDataPolicyDTO(null);
		dataPolicyDTO.getDataPoints().add(createDataPointDTO(DATA_POINT_GUID, true, DESCRIPTION_KEY));
		DataPolicy expectedDataPolicy = createDataPolicy(null);
		expectedDataPolicy.getDataPoints().add(createDataPoint(DATA_POINT_GUID, true, DESCRIPTION_KEY));

		when(beanFactory.getBean(ContextIdNames.DATA_POINT)).thenReturn(new DataPointImpl());

		DataPolicy dataPolicy = new DataPolicyImpl();
		dataPolicyAdapter.buildDomain(dataPolicyDTO, dataPolicy);

		assertThat(expectedDataPolicy)
				.as(EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL)
				.isEqualToComparingFieldByField(dataPolicy);
	}

	/**
	 * Tests buildDomain with existing same data point values.
	 */
	@Test
	public void testBuildDomainWithExistingSameDataPointValues() {
		DataPolicyDTO dataPolicyDTO = createDataPolicyDTO(null);
		dataPolicyDTO.getDataPoints().add(createDataPointDTO(DATA_POINT_GUID, true, DESCRIPTION_KEY));
		DataPolicy expectedDataPolicy = createDataPolicy(null);
		DataPoint expectedDataPoint = createDataPoint(DATA_POINT_GUID, true, DESCRIPTION_KEY);
		expectedDataPolicy.getDataPoints().add(expectedDataPoint);

		when(beanFactory.getBean(ContextIdNames.DATA_POINT)).thenReturn(new DataPointImpl());
		when(dataPointService.findByGuids(Arrays.asList(expectedDataPoint.getGuid()))).thenReturn(Arrays.asList(expectedDataPoint));

		DataPolicy dataPolicy = new DataPolicyImpl();
		dataPolicyAdapter.buildDomain(dataPolicyDTO, dataPolicy);

		assertThat(expectedDataPolicy)
				.as(EXPECTED_DOMAIN_OBJECT_SHOULD_EQUAL_ACTUAL)
				.isEqualToComparingFieldByField(dataPolicy);
	}

	/**
	 * Tests buildDomain with existing different data point values.
	 */
	@Test
	public void testBuildDomainWithExistingDifferentDataPointValues() {
		DataPolicyDTO dataPolicyDTO = createDataPolicyDTO(null);
		dataPolicyDTO.getDataPoints().add(createDataPointDTO(DATA_POINT_GUID, true, DESCRIPTION_KEY));

		Message expectedMessage = new Message("IE-31201", DATA_POLICY_GUID, DATA_POINT_GUID);

		when(beanFactory.getBean(ContextIdNames.DATA_POINT)).thenReturn(new DataPointImpl());
		when(dataPointService.findByGuids(Arrays.asList(DATA_POINT_GUID)))
				.thenReturn(Arrays.asList(createDataPoint(DATA_POINT_GUID, false, DESCRIPTION_KEY)));

		DataPolicy dataPolicy = new DataPolicyImpl();
		assertThatThrownBy(() -> dataPolicyAdapter.buildDomain(dataPolicyDTO, dataPolicy))
				.isInstanceOf(PopulationRollbackException.class)
				.extracting("ieMessage")
				.containsExactly(expectedMessage);
	}

	/**
	 * Tests buildDomain with existing different data point values because of case sensitivity.
	 */
	@Test
	public void testBuildDomainWithExistingDifferentCaseSensitivityDataPointValues() {
		DataPolicyDTO dataPolicyDTO = createDataPolicyDTO(null);
		dataPolicyDTO.getDataPoints().add(createDataPointDTO(DATA_POINT_GUID, true, DESCRIPTION_KEY));

		Message expectedMessage = new Message("IE-31201", DATA_POLICY_GUID, DATA_POINT_GUID);

		when(beanFactory.getBean(ContextIdNames.DATA_POINT)).thenReturn(new DataPointImpl());
		when(dataPointService.findByGuids(Arrays.asList(DATA_POINT_GUID)))
				.thenReturn(Arrays.asList(createDataPoint(DATA_POINT_GUID, true, DESCRIPTION_KEY_UPPER_CASE)));

		DataPolicy dataPolicy = new DataPolicyImpl();
		assertThatThrownBy(() -> dataPolicyAdapter.buildDomain(dataPolicyDTO, dataPolicy))
				.isInstanceOf(PopulationRollbackException.class)
				.extracting("ieMessage")
				.containsExactly(expectedMessage);
	}

	/**
	 * Test data policy assembly from DTO object  is not the same.
	 */
	@Test
	public void testDataPolicyAssembleDomainObjectFromDtoNotEquals() {

		DataPolicyDTO dataPolicyDTO = createDataPolicyDTO("different name");
		DataPolicy expectedDataPolicy = createDataPolicy(null);

		DataPolicy dataPolicy = new DataPolicyImpl();

		dataPolicyAdapter.buildDomain(dataPolicyDTO, dataPolicy);

		assertThat(expectedDataPolicy.getPolicyName())
				.as("Unexpected data policy domain created by assembler")
				.isNotSameAs(dataPolicy.getPolicyName());
	}

	/**
	 * Tests createDomainObject.
	 */
	@Test
	public void testCreateDomainObject() {
		dataPolicyAdapter.createDomainObject();

		verify(beanFactory).getBean(ContextIdNames.DATA_POLICY);
	}

	/**
	 * Tests createDtoObject.
	 */
	@Test
	public void testCreateDtoObject() {
		Object dtoObject = dataPolicyAdapter.createDtoObject();

		assertThat(dtoObject)
				.isNotNull();
		assertThat(DataPolicyDTO.class)
				.isEqualTo(dtoObject.getClass());
	}

	private DataPolicyDTO createDataPolicyDTO(final String policyName) {
		DataPolicyDTO dataPolicy = new DataPolicyDTO();
		dataPolicy.setGuid(DATA_POLICY_GUID);
		dataPolicy.setPolicyName(policyName == null ? POLICY_NAME : policyName);
		dataPolicy.setRetentionPeriodInDays(RETENTION_PERIOD_IN_DAYS);
		dataPolicy.setRetentionType(RETENTION_TYPE.getName());
		dataPolicy.setStartDate(START_DATE);
		dataPolicy.setEndDate(END_DATE);
		dataPolicy.setState(STATE.getName());
		dataPolicy.setDescription(DESCRIPTION);
		dataPolicy.setSegments(new HashSet<>());
		dataPolicy.setDataPoints(new ArrayList<>());
		dataPolicy.setActivities(new HashSet<>());
		return dataPolicy;
	}

	private DataPointDTO createDataPointDTO(final String dataPointGuid, final boolean removable, final String descriptionKey) {
		DataPointDTO dataPoint = new DataPointDTO();
		dataPoint.setGuid(dataPointGuid == null ? DATA_POINT_GUID : dataPointGuid);
		dataPoint.setName(DATA_POINT_NAME);
		dataPoint.setDataKey(DATA_KEY);
		dataPoint.setDescriptionKey(descriptionKey);
		dataPoint.setRemovable(removable);
		dataPoint.setDataLocation(DATA_LOCATION);
		return dataPoint;
	}

	private DataPolicy createDataPolicy(final String policyName) {
		DataPolicyImpl dataPolicy = new DataPolicyImpl();
		dataPolicy.setGuid(DATA_POLICY_GUID);
		dataPolicy.setPolicyName(policyName == null ? POLICY_NAME : policyName);
		dataPolicy.setRetentionPeriodInDays(RETENTION_PERIOD_IN_DAYS);
		dataPolicy.setRetentionType(RETENTION_TYPE);
		dataPolicy.setStartDate(START_DATE);
		dataPolicy.setEndDate(END_DATE);
		dataPolicy.setState(STATE);
		dataPolicy.setDescription(DESCRIPTION);
		dataPolicy.setSegments(new HashSet<>());
		dataPolicy.setDataPoints(new ArrayList<>());
		dataPolicy.setActivities(new HashSet<>());
		return dataPolicy;
	}

	private DataPoint createDataPoint(final String dataPointGuid, final boolean removable, final String descriptionKey) {
		DataPoint dataPoint = new DataPointImpl();
		dataPoint.setGuid(dataPointGuid == null ? DATA_POINT_GUID : dataPointGuid);
		dataPoint.setName(DATA_POINT_NAME);
		dataPoint.setDataKey(DATA_KEY);
		dataPoint.setDescriptionKey(descriptionKey);
		dataPoint.setRemovable(removable);
		dataPoint.setDataLocation(DATA_LOCATION);
		return dataPoint;
	}

}
