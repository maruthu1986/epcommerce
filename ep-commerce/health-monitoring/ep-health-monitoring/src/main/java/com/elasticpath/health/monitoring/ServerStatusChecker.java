/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Server status checker.
 */
public interface ServerStatusChecker {

    /**
     * Get all the statuses.
     *
     * @param refreshIntervalSeconds the browser refresh interval
     * @param statusChecker the status checker
     * @param request the request
     * @param response the respose
     * @throws ServletException servlet exception
     * @throws IOException IO exception
     */
    void getServerStatus(int refreshIntervalSeconds, StatusChecker statusChecker,
                         HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;
}
