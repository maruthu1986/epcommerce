/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientInfo;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.PropertyBased;
import com.elasticpath.service.remote.ResourceRetrievalService;

/**
 * The activator class controls the plug-in life cycle.
 */
public class CorePlugin extends AbstractUIPlugin {

	private static final Logger LOG = Logger.getLogger(CorePlugin.class);
	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.core"; //$NON-NLS-1$

	private ConfigurableApplicationContext appContext;

	private static final Set<Runnable> PRE_STARTUP_CALLBACKS = new HashSet<>();
	private static final Set<Runnable> POST_STARTUP_CALLBACKS = new HashSet<>();

	/**
	 * The constructor.
	 */
	public CorePlugin() {
		// Do nothing
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		createApplicationContext();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			// dispose of the created images
			CoreImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}

		super.stop(context);

		//Close Spring context
		if (this.appContext != null) {
			this.appContext.close();
		}
		PRE_STARTUP_CALLBACKS.clear();
		POST_STARTUP_CALLBACKS.clear();
	}

	/**
	 * Returns the session instance.
	 *
	 * @return the session instance
	 */
	public static CorePlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(CorePlugin.class);
	}

	/**
	 * Retrieves the current value of the default locale for this instance of the JVM.
	 *
	 * @return the current value of the default locale for this instance of the JVM.
	 */
	public Locale getDefaultLocale() {
		Locale locale = RWT.getClient().getService(ClientInfo.class).getLocale();
		if (locale == null) {
			return Locale.getDefault();
		}
		return locale;
	}


	private void createApplicationContext() {
		LOG.debug("Initializing Spring Application Context"); //$NON-NLS-1$


		appContext = new ClassPathXmlApplicationContext("spring/application-context.xml"); //$NON-NLS-1$

		ServiceLocator.setBeanFactory((BeanFactory) appContext.getBean("coreBeanFactory"));

		initializeAdditionalEpServices();
	}

	private void initializeAdditionalEpServices() {
		final List<String> propertyBasedServices = new ArrayList<>();
		propertyBasedServices.add(ContextIdNames.ORDER_RETURN_RECEIVED_STATE);
		propertyBasedServices.add(ContextIdNames.ORDER_RETURN_SKU_REASON);
		propertyBasedServices.add(ContextIdNames.ADJUSTMENT_QUANTITY_ON_HAND_REASON);

		loadPropertyBasedServices(propertyBasedServices);

	}

	private void loadPropertyBasedServices(final List<String> propertyBasedServices) {
		Map<String, Properties> propertiesMap = getPropertiesMap();

		for (final String service : propertyBasedServices) {
			final PropertyBased propertyBased = ServiceLocator.getService(service);
			propertyBased.setPropertiesMap(propertiesMap);
		}
	}

	private Map<String, Properties> getPropertiesMap() {
		LOG.debug("Retrieving properties from remote ResourceRetrievalService"); //$NON-NLS-1$

		final ResourceRetrievalService resourceRetrievalService = ServiceLocator.getService("remoteResourceRetrievalService"); //$NON-NLS-1$

		Map<String, Properties> propertiesMap = resourceRetrievalService.getProperties();
		if (propertiesMap == null || propertiesMap.isEmpty()) {
			throw new EpServiceException("Could not retrieve the properties from server.");
		}

		return propertiesMap;
	}

	/**
	 * Registers runnable that will be triggered during Application Workbench PRE startup.
	 *
	 * @param runnable runnable to execute
	 */
	public static void registerPreStartupCallback(final Runnable runnable) {
		PRE_STARTUP_CALLBACKS.add(runnable);
	}

	/**
	 * Registers runnable that will be triggered during Application Workbench POST startup.
	 *
	 * @param runnable runnable to execute
	 */
	public static void registerPostStartupCallback(final Runnable runnable) {
		POST_STARTUP_CALLBACKS.add(runnable);
	}

	/**
	 * Runs the startup callbacks synchronously.
	 */
	static void runPreStartupCallbacks() {
		executeRunnablesFromList(PRE_STARTUP_CALLBACKS);
	}

	/**
	 * Runs the startup callbacks synchronously.
	 */
	static void runPostStartupCallbacks() {
		executeRunnablesFromList(POST_STARTUP_CALLBACKS);
	}

	private static void executeRunnablesFromList(final Set<Runnable> runnables) {
		final Display display = Display.getCurrent();

		for (Runnable runnable : runnables) {
			UISession uiSession = RWT.getUISession(display);
			uiSession.exec(runnable);

		}

	}
}