/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.reporting;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.CmSingletonUtil;

/**
 * The activator class controls the plug-in life cycle.
 */
public class ReportingPlugin extends AbstractUIPlugin {

	/** The plug-in ID. **/
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.reporting"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public ReportingPlugin() {
		//empty
	}

	/**
	 * Initialize the Birt Engine.
	 * @return IReportEngine the initialized Birt engine object
	 * @throws BirtException if initialization fails
	 */
	public IReportEngine initializeEngine() throws BirtException {
		final EngineConfig config = new EngineConfig();

		Platform.startup(config);

		final IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		final IReportEngine engine = factory.createReportEngine(config);
		engine.changeLogLevel(java.util.logging.Level.WARNING);

		return engine;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			ReportingImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}

		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static ReportingPlugin getInstance() {
		return CmSingletonUtil.getSessionInstance(ReportingPlugin.class);
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
