/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.impl;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.elasticpath.tools.sync.client.SyncToolConfiguration;
import com.elasticpath.tools.sync.client.SyncToolControllerType;
import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;

/**
 * The default implementation of a {@link SyncToolConfiguration}.
 */
public class SyncToolConfigurationImpl implements SyncToolConfiguration {

	private SyncToolControllerType controllerType;

	private ConnectionConfiguration sourceConfig;

	private ConnectionConfiguration targetConfig;

	@Override
	public String toString() {
		return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
				.append("Resident Controller Type", getControllerType())
				.append("Source Configuration",
						getControllerType().equals(SyncToolControllerType.LOAD_CONTROLLER) ? "" : getSourceConfig().getUrl())
				.append("Target Configuration",
						getControllerType().equals(SyncToolControllerType.EXPORT_CONTROLLER) ? "" : getTargetConfig().getUrl())
				.build();
	}

	@Override
	public SyncToolControllerType getControllerType() {
		return controllerType;
	}

	public void setControllerType(final SyncToolControllerType controllerType) {
		this.controllerType = controllerType;
	}

	@Override
	public ConnectionConfiguration getSourceConfig() {
		return sourceConfig;
	}

	public void setSourceConfig(final ConnectionConfiguration sourceConfig) {
		this.sourceConfig = sourceConfig;
	}

	@Override
	public ConnectionConfiguration getTargetConfig() {
		return targetConfig;
	}

	public void setTargetConfig(final ConnectionConfiguration targetConfig) {
		this.targetConfig = targetConfig;
	}

}
