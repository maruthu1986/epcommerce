/**
 * Copyright (c) Elastic Path Software Inc., 2017
 *
 */
package com.elasticpath.cmclient.reporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * Manages the cached report types.
 */
public class ReportTypeManager {
	private static final Logger LOG = Logger.getLogger(ReportTypeManager.class);

	private  List<ReportType> cachedTypes;

	private static final String EXTENSION_NAME = "reports"; //$NON-NLS-1$

	/**
	 * Get the session instance.
	 * @return the session instance.
	 */
	public static ReportTypeManager getInstance() {
		return CmSingletonUtil.getSessionInstance(ReportTypeManager.class);
	}

	/**
	 * Return all the <code>ReportType</code>s that are plugged into the platform,
	 * sorted alphabetically by report name.
	 *
	 * @return all the <code>ReportType</code>s that are plugged into the platform
	 */
	public List<ReportType> getReportTypes() {
		if (cachedTypes != null) {
			return cachedTypes;
		}
		cachedTypes = new ArrayList<>();
		LOG.debug("Retrieving all plugin extension points for " + EXTENSION_NAME); //$NON-NLS-1$
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(ReportingPlugin.PLUGIN_ID, EXTENSION_NAME).getExtensions();
		LOG.debug("Retrieved " + extensions.length + " extensions"); //$NON-NLS-1$ //$NON-NLS-2$
		for (IExtension extension : extensions) {
			for (IConfigurationElement configElement : extension.getConfigurationElements()) {
				ReportType reportType = ReportType.parseItem(configElement);
				if (reportType != null) {
					cachedTypes.add(reportType);
				}
			}
		}
		Collections.sort(cachedTypes);
		return Collections.unmodifiableList(cachedTypes);
	}

	/**
	 * Finds first report type with the same reportName or null if no such report has been found.
	 *
	 * @param reportName the id of the report
	 * @return report needed
	 */
	public ReportType findReportTypeByName(final String reportName) {
		getReportTypes();
		for (ReportType reportType : cachedTypes) {
			if (reportType.getName().equalsIgnoreCase(reportName)) {
				return reportType;
			}
		}
		return null;
	}
}
