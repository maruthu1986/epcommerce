/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.reporting;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


/**
 * A Report that can be added to the Reporting navigation view. 
 * A ReportType is represented by a configElement as an extension to the Reporting
 * plugin, so this class knows how to parse such an element.
 */
public class ReportType implements Comparable<ReportType> {

	private static final Logger LOG = Logger.getLogger(ReportType.class);
	
	private static final String TAG_REPORTTYPE = "report"; //$NON-NLS-1$
	
	private static final String DESIGN_FILE = "designFile"; //$NON-NLS-1$

	private static final String ATT_ID = "id"; //$NON-NLS-1$

	private static final String ATT_NAME = "name"; //$NON-NLS-1$

	private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	private final IConfigurationElement configElement;

	private final String sectionId;

	private final String name;
	
	private final URL fileLocation;

	private IReport report;



	/**
	 * Constructs a ReportType from a configurationElement.
	 * 
	 * @param configElement the extension point configuration element that defines a ReportType
	 */
	public ReportType(final IConfigurationElement configElement) {
		this.configElement = configElement;
		sectionId = getAttribute(configElement, ATT_ID);
		name = getAttribute(configElement, ATT_NAME);
		fileLocation = getResource(configElement, DESIGN_FILE);
		// Make sure that the class is defined, but don't load it
		getAttribute(configElement, ATT_CLASS);
	}

	/**
	 * Returns the report's name.
	 * 
	 * @return name the name of the report.
	 */
	public String getName() {
		return name;
	}


	/**
	 * Returns a report or null if initiation of the report failed.
	 *
	 * @return the report
	 */
	public IReport getReport() {
		if (report != null) {
			return report;
		}
		try {
			report = (IReport) configElement.createExecutableExtension(ATT_CLASS);
		} catch (Exception e) {
			LOG.error("Failed to instantiate report: " //$NON-NLS-1$
					+ configElement.getAttribute(ATT_CLASS) + " in type: " //$NON-NLS-1$
					+ sectionId + " in plugin: " //$NON-NLS-1$
					+ configElement.getDeclaringExtension().getNamespaceIdentifier(), e);
		}
		return report;
	}

	private static String getAttribute(final IConfigurationElement configElement, final String name) {
		String value = configElement.getAttribute(name);
		if (value == null) {
			throw new IllegalArgumentException("Missing " + name + " attribute"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return value;
	}
	
	private static URL getResource(final IConfigurationElement configElement, final String name) {
		String resource = configElement.getAttribute(name);
		URL url = null;
		Bundle bundle = null;
		String bundleId = ""; //$NON-NLS-1$

		if (resource != null) {
			bundleId = configElement.getContributor().getName();
			bundle = Platform.getBundle(bundleId);
			if (bundle == null) {
				return null;
			}
			try {
				url = FileLocator.resolve(FileLocator.find(bundle, new Path(resource), null));
			} catch (IOException e) {
				LOG.debug(e.getMessage());
			}		
		}
		return url;
		
	}




	/**
	 * Parse a configuration element representing a ReportType and return an object instance.
	 * 
	 * @param configElement an element representing the ReportType
	 * @return an instance of ReportType
	 */
	public static ReportType parseItem(final IConfigurationElement configElement) {
		if (!configElement.getName().equals(TAG_REPORTTYPE)) {
			LOG.error("Unknown element: " + configElement.getName()); //$NON-NLS-1$
			return null;
		}
		try {
			LOG.debug("Creating new ReportType"); //$NON-NLS-1$
			return new ReportType(configElement);
		} catch (Exception e) {
			String msg = "Failed to load ReportType with name " //$NON-NLS-1$
					+ configElement.getAttribute(ATT_NAME) + " in " //$NON-NLS-1$
					+ configElement.getDeclaringExtension().getNamespaceIdentifier();
			LOG.error(msg, e);
			return null;
		}
	}

	/**
	 * {@inheritDoc}	 
	 */
	public int compareTo(final ReportType reportType) {
		return this.getName().compareTo(reportType.getName());
	}

	/**
	 * Gets the location of the design file.
	 * @return path as URL
	 */
	public URL getFileLocation() {
		return fileLocation;
	}
}
