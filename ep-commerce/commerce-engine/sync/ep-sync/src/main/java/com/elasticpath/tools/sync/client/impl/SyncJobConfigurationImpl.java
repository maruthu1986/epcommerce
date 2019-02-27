/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.impl;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Date;
import java.util.Objects;

import com.google.common.base.Strings;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;

/**
 * The default implementation of a {@link SyncJobConfiguration}.
 */
public class SyncJobConfigurationImpl implements SyncJobConfiguration {

	private static final FastDateFormat TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");
	
	private String adapterParameter;

	private String rootPath;
	
	private String subDir;

	private String executionId;

	/**
	 * Default conmstructor. Initializes the executionId.
	 */
	public SyncJobConfigurationImpl() {
		executionId = TIMESTAMP_FORMAT.format(new Date());
	}

	/**
	 * Constructor that takes the configuration details.
	 *
	 * @param rootPath the root path
	 * @param subDir the subdirectory
	 * @param adapterParameter the adapter parameter
	 */
	public SyncJobConfigurationImpl(final String rootPath, final String subDir, final String adapterParameter) {
		this();
		this.rootPath = rootPath;
		this.subDir = subDir;
		this.adapterParameter = adapterParameter;
	}

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this, SHORT_PREFIX_STYLE);

		if (!Strings.isNullOrEmpty(adapterParameter)) {
			builder.append("Job parameter", getAdapterParameter());
		}

		if (!Strings.isNullOrEmpty(rootPath)) {
			builder.append("Job Root Directory", getRootPath());
		}

		if (!Strings.isNullOrEmpty(subDir)) {
			builder.append("Job Sub Directory", getSubDir());
		}

		return builder.build();
	}

	@Override
	public String getAdapterParameter() {
		return adapterParameter;
	}

	public void setAdapterParameter(final String adapterParameter) {
		this.adapterParameter = adapterParameter;
	}

	@Override
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(final String rootPath) {
		this.rootPath = rootPath;
	}

	@Override
	public String getSubDir() {
		return subDir;
	}

	@Override
	public String getExecutionId() {
		return executionId;
	}

	public void setSubDir(final String subDir) {
		this.subDir = subDir;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}

		final SyncJobConfigurationImpl that = (SyncJobConfigurationImpl) other;

		return Objects.equals(this.adapterParameter, that.adapterParameter)
			&& Objects.equals(this.rootPath, that.rootPath)
			&& Objects.equals(this.subDir, that.subDir);
	}

	@Override
	public int hashCode() {
		return Objects.hash(adapterParameter, rootPath, subDir);
	}

}
