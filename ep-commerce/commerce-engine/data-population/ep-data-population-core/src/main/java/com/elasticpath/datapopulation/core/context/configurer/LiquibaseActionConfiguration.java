/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.context.configurer;

import java.util.Map;

/**
 * Provides a liquibase resource wrapper for {@link com.elasticpath.datapopulation.core.action.impl.RunLiquibaseActionImpl}
 * because it has no knowledge about what changelog or the context to execute.
 */
public class LiquibaseActionConfiguration {

	private String liquibaseChangelog;
	private String liquibaseContexts;
	private boolean dropFirst;
	private boolean useAdminConnections;
	private Map<String, String> liquibaseChangelogParameters;

	public String getLiquibaseChangelog() {
		return liquibaseChangelog;
	}

	public void setLiquibaseChangelog(final String liquibaseChangelog) {
		this.liquibaseChangelog = liquibaseChangelog;
	}

	public String getLiquibaseContexts() {
		return liquibaseContexts;
	}

	public void setLiquibaseContexts(final String liquibaseContexts) {
		this.liquibaseContexts = liquibaseContexts;
	}

	public boolean isDropFirst() {
		return dropFirst;
	}

	public void setDropFirst(final boolean dropFirst) {
		this.dropFirst = dropFirst;
	}

	public boolean isUseAdminConnections() {
		return useAdminConnections;
	}

	public void setUserAdminConnections(final boolean useAdminConnections) {
		this.useAdminConnections = useAdminConnections;
	}

	public Map<String, String> getLiquibaseChangelogParameters() {
		return liquibaseChangelogParameters;
	}

	public void setLiquibaseChangelogParameters(final Map<String, String> liquibaseChangelogParameters) {
		this.liquibaseChangelogParameters = liquibaseChangelogParameters;
	}

}
