/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.manifest;

import com.elasticpath.importexport.common.types.JobType;

/**
 * The ManifestBuilder. Builds The Manifest.
 */
public interface ManifestBuilder {

	/**
	 * Adds a Resource.
	 *
	 * @param jobType to use
	 * @param resource to add
	 */
	void addResource(JobType jobType, String resource);

	/**
	 * Builds the Manifest.
	 *
	 * @return Manifest instance.
	 */
	Manifest build();

}