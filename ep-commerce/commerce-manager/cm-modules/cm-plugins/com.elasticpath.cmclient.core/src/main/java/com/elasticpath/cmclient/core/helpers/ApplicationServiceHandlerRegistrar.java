/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.rap.rwt.service.ServiceManager;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.DownloadServiceHandler;
import com.elasticpath.cmclient.core.TestMapServiceHandler;

/**
 * Registers service handlers for the application.
 * Should only be run once per application instance.
 */
public final class ApplicationServiceHandlerRegistrar {

    /**
     * Private constructor, should only be called once per application instance.
     */
    private ApplicationServiceHandlerRegistrar() {
        register();
    }


    /**
     * Gets the application instance, will create a single instance for the application.
     *
     * @return the single application instance.
     */
    public static ApplicationServiceHandlerRegistrar getApplicationInstance() {
        return CmSingletonUtil.getApplicationInstance(ApplicationServiceHandlerRegistrar.class);

    }

    /**
     * Registers the service handlers with RWT.
     */
    private void register() {
        ServiceManager manager = RWT.getServiceManager();

        manager.registerServiceHandler(DownloadServiceHandler.SERVICE_NAME, new DownloadServiceHandler());

        if (UITestUtil.isEnabled()) {
            manager.registerServiceHandler(TestMapServiceHandler.SERVICE_NAME, new TestMapServiceHandler());
        }
    }
}
