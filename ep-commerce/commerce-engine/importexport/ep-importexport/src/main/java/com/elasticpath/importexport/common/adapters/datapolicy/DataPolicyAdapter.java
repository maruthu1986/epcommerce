/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.common.adapters.datapolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.elasticpath.common.dto.datapolicy.DataPointDTO;
import com.elasticpath.common.dto.datapolicy.DataPolicyDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.domain.datapolicy.RetentionType;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.service.datapolicy.DataPointService;

/**
 * Helper class for mediating data from DataPolicy entities to DataPolicyDTO.
 */
public class DataPolicyAdapter extends AbstractDomainAdapterImpl<DataPolicy, DataPolicyDTO> {

	private DataPointService dataPointService;

	@Override
	public void populateDTO(final DataPolicy source, final DataPolicyDTO target) {
		target.setGuid(source.getGuid());
		target.setPolicyName(source.getPolicyName());
		target.setRetentionPeriodInDays(source.getRetentionPeriodInDays());
		target.setRetentionType(source.getRetentionType().getName());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setState(source.getState().getName());
		target.setDescription(source.getDescription());
		target.setReferenceKey(source.getReferenceKey());
		target.setSegments(source.getSegments());
		target.setActivities(source.getActivities());

		for (DataPoint sourceDataPoint : source.getDataPoints()) {
			DataPointDTO targetDataPoint = new DataPointDTO();
			targetDataPoint.setGuid(sourceDataPoint.getGuid());
			targetDataPoint.setName(sourceDataPoint.getName());
			targetDataPoint.setDataLocation(sourceDataPoint.getDataLocation());
			targetDataPoint.setDataKey(sourceDataPoint.getDataKey());
			targetDataPoint.setDescriptionKey(sourceDataPoint.getDescriptionKey());
			targetDataPoint.setRemovable(sourceDataPoint.isRemovable());

			target.getDataPoints().add(targetDataPoint);
		}
	}

	@Override
	public DataPolicyDTO createDtoObject() {
		return new DataPolicyDTO();
	}

	@Override
	public DataPolicy createDomainObject() {
		return getBeanFactory().getBean(ContextIdNames.DATA_POLICY);
	}

	@Override
	public void populateDomain(final DataPolicyDTO source, final DataPolicy target) {
		target.setGuid(source.getGuid());
		target.setPolicyName(source.getPolicyName());
		target.setRetentionPeriodInDays(source.getRetentionPeriodInDays());
		target.setRetentionType(RetentionType.valueOf(source.getRetentionType()));
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setState(DataPolicyState.valueOf(source.getState()));
		target.setDescription(source.getDescription());
		target.setReferenceKey(source.getReferenceKey());
		target.setSegments(source.getSegments());
		target.setActivities(source.getActivities());

		populateDataPointsForDomain(source, target);
	}

	private void populateDataPointsForDomain(final DataPolicyDTO sourceDataPolicyDTO, final DataPolicy targetDataPolicy) {
		if (sourceDataPolicyDTO.getDataPointGuids() == null) {
			return;
		}

		final List<DataPoint> existingTargetDataPoints = Optional.ofNullable(getDataPointService()
				.findByGuids(sourceDataPolicyDTO.getDataPointGuids())).orElseGet(ArrayList::new);

		targetDataPolicy.setDataPoints(existingTargetDataPoints);

		for (DataPointDTO sourceDataPoint : sourceDataPolicyDTO.getDataPoints()) {
			final DataPoint targetDataPoint = getBeanFactory().getBean(ContextIdNames.DATA_POINT);
			targetDataPoint.setGuid(sourceDataPoint.getGuid());
			targetDataPoint.setName(sourceDataPoint.getName());
			targetDataPoint.setDataLocation(sourceDataPoint.getDataLocation());
			targetDataPoint.setDataKey(sourceDataPoint.getDataKey());
			targetDataPoint.setDescriptionKey(sourceDataPoint.getDescriptionKey());
			targetDataPoint.setRemovable(sourceDataPoint.isRemovable());

			confirmExistingDataPointAttributeEquality(sourceDataPolicyDTO, existingTargetDataPoints, sourceDataPoint, targetDataPoint);

			if (!existingTargetDataPoints.contains(targetDataPoint)) {
				targetDataPolicy.getDataPoints().add(targetDataPoint);
			}
		}
	}

	private void confirmExistingDataPointAttributeEquality(final DataPolicyDTO sourceDataPolicyDTO,
														   final List<DataPoint> existingTargetDataPoints,
														   final DataPointDTO sourceDataPoint, final DataPoint targetDataPoint) {
		final int indexOfExistingDataPoint = existingTargetDataPoints.indexOf(targetDataPoint);
		if (indexOfExistingDataPoint > -1) {
			final DataPoint existingDataPoint = existingTargetDataPoints.get(indexOfExistingDataPoint);

			if (existingDataPoint != null && !hasTheSameFields(existingDataPoint, targetDataPoint)) {
				throw new PopulationRollbackException("IE-31201", sourceDataPolicyDTO.getGuid(), sourceDataPoint.getGuid());
			}
		}
	}

	/**
	 * Check if the two data points have the same fields.
	 *
	 * @param dataPointOne data point to compare.
	 * @param dataPointTwo data point to compare.
	 * @return if the data point fields are equal.
	 */
	protected boolean hasTheSameFields(final DataPoint dataPointOne, final DataPoint dataPointTwo) {
		return dataPointOne.getGuid().equals(dataPointTwo.getGuid())
				&& dataPointOne.getName().equals(dataPointTwo.getName())
				&& dataPointOne.getDataLocation().equals(dataPointTwo.getDataLocation())
				&& dataPointOne.getDataKey().equals(dataPointTwo.getDataKey())
				&& dataPointOne.getDescriptionKey().equals(dataPointTwo.getDescriptionKey())
				&& dataPointOne.isRemovable() == dataPointTwo.isRemovable();
	}

	public DataPointService getDataPointService() {
		return dataPointService;
	}

	public void setDataPointService(final DataPointService dataPointService) {
		this.dataPointService = dataPointService;
	}

}
