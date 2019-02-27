/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import java.util.Map;
import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusChecker;
import com.elasticpath.health.monitoring.StatusCheckerTarget;

/**
 * <p>
 * Implementation of StatusChecker which goes through the list of status targets ({@link com.elasticpath.health.monitoring.StatusCheckerTarget
 * StatusCheckerTarget}) and checks every target.
 * </p>
 * <p>
 * The targets all return a {@link Status} object. The calling client decides how to use the status objects.
 * </p>
 */
public class StatusCheckerImpl implements StatusChecker {

	private Collection<StatusCheckerTarget> loadBalancerTargets;

	private Collection<StatusCheckerTarget> additionalInfoTargets;

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.elasticpath.health.monitoring.StatusChecker#checkStatus()
	 */
	@Override
	public Map<String, Status> checkStatus() {
		Map<String, Status> results = new LinkedHashMap<>();
		results.putAll(checkStatus(loadBalancerTargets));
		results.putAll(checkStatus(additionalInfoTargets));
		return results;
	}

	@Override
	public Map<String, Status> checkStatusSimple() {
		return checkStatus(loadBalancerTargets);
	}

	private Map<String, Status> checkStatus(final Collection<StatusCheckerTarget> targets) {
		Map<String, Status> results = new LinkedHashMap<>();
		if (CollectionUtils.isNotEmpty(targets)) {
			for (StatusCheckerTarget target : targets) {
				Status status = target.check();
				results.put(target.getName(), status);
			}
		}
		return results;
	}

	/**
	 * Sets the simple targets.
	 * 
	 * @param loadBalancerTargets the targets to set
	 */
	public void setLoadBalancerTargets(final Collection<StatusCheckerTarget> loadBalancerTargets) {
		this.loadBalancerTargets = new ArrayList<>(loadBalancerTargets);
	}

	/**
	 * Sets the additionalInfoTargets.
	 * 
	 * @param additionalInfoTargets additional targets to set for info URLs
	 */
	public void setAdditionalInfoTargets(final Collection<StatusCheckerTarget> additionalInfoTargets) {
		this.additionalInfoTargets = new ArrayList<>(additionalInfoTargets);
	}

}
