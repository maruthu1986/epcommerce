/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring;

/**
 * <p>
 * Represents one component of the system that should be checked by the ({@link StatusChecker StatusChecker}).
 * </p>
 * <p>
 * Examples of system target implementations:
 * </p>
 * <ul>
 * <li>{@link com.elasticpath.health.monitoring.impl.DatabaseStatusTargetImpl DatabaseStatusTargetImpl}</li>
 * <li>{@link com.elasticpath.health.monitoring.impl.HttpStatusTargetImpl HttpStatusTargetImpl}</li>
 * </ul>
 */
public interface StatusCheckerTarget {

	/**
	 * @return Name of the target.
	 */
	String getName();

	/**
	 * Hit the status target to test it.
	 * 
	 * @return the status
	 */
	Status check();

}
