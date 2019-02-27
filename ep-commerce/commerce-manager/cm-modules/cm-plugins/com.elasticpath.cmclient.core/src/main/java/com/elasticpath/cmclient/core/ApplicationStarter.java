/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;

import com.elasticpath.cmclient.core.helpers.ApplicationServiceHandlerRegistrar;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.helpers.TestIdMapManager;
import com.elasticpath.cmclient.core.helpers.TestIdUtil;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;

/**
 * This is an entry point class used for initialization of service handlers, widget utils etc
 * as well opening a login dialog.
 */
public class ApplicationStarter implements IApplication {

	private static final Logger LOG = Logger.getLogger(ApplicationStarter.class);

	/**
	 * Initializes service handlers, widget util etc. i.e. everything that requires UI thread.
	 *
	 * @return IApplication.EXIT_OK (0) if everything is ok; otherwise 1 as required by
	 * {@link IApplication#start(IApplicationContext)} method.
	 */
	@Override
	public Object start(final IApplicationContext iApplicationContext) throws Exception {

		init();

		return LoginManager.getInstance().showLoginDialog();
	}

	@Override
	public void stop() {
		//do nothing
	}

	private void init() {
		try {
			//register service handlers on application level
			ApplicationServiceHandlerRegistrar.getApplicationInstance();
			ApplicationLockManager.getInstance().start();
			DateTimeUtilFactory.getDateUtil().initializeTimezone();

			//TODO: Is this still necessary?
			if (UITestUtil.isEnabled()) {
				TestIdUtil testIdUtil = EPTestUtilFactory.getInstance().getTestIdUtil();

				testIdUtil.initialize();
				testIdUtil.sendTestIdMapsToClient(TestIdMapManager.getMinifiedMap());
			}
		} catch (final IOException ioe) {
			LOG.error("Startup failed due to exception", ioe); //$NON-NLS-1$
			MessageDialog.openError(null, "Error", "Commerce Manager startup failed due to:\n\n" + ioe.getMessage()); //$NON-NLS-2$
		}
	}
}
