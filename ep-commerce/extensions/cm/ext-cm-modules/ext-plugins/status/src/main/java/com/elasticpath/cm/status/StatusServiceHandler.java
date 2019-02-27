/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cm.status;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.health.monitoring.ServerStatusChecker;
import com.elasticpath.health.monitoring.StatusChecker;
import org.eclipse.rap.rwt.service.ServiceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Status service handler.
 */
public class StatusServiceHandler implements ServiceHandler {

    private static final Integer REFRESH_INTERVAL_SECONDS_DEFAULT = 10;

    /**
     * Executes the service handler.
     * @param request the request.
     * @param response the response.
     * @throws IOException IO Exception if necessary,
     * @throws ServletException Servlet Exception if necessary.
     */
    public void service(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {

        StatusChecker statusChecker = ServiceLocator.getService("statusChecker");
        ServerStatusChecker serverStatusChecker = ServiceLocator.getService("serverStatusChecker");

        serverStatusChecker.getServerStatus(REFRESH_INTERVAL_SECONDS_DEFAULT, statusChecker, request, response);
    }
}
