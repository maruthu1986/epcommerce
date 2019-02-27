/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.elasticpath.health.monitoring.ServerStatusChecker;
import com.elasticpath.health.monitoring.StatusChecker;
/**
 * Simple Status Servlet which calls system checker bean ({@link StatusChecker}) and outputs in a variety of formats, depending on the URL.
 */
public class StatusCheckerServlet extends HttpServlet {

    private static final long serialVersionUID = -6297009033288967143L;

    private static final String REFRESH_INTERVAL_SECONDS_BEAN_NAME = "healthMonitoringRefreshIntervalSeconds";

    private static final Integer REFRESH_INTERVAL_SECONDS_DEFAULT = 10;

    private ServerStatusChecker serverStatusChecker;

    private StatusChecker statusChecker;

    private int refreshIntervalSeconds;

    @Override
    public void init() throws ServletException {
        super.init();

        final ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        serverStatusChecker = context.getBean("serverStatusChecker", ServerStatusChecker.class);

        statusChecker = context.getBean("statusChecker", StatusChecker.class);

        try {
            refreshIntervalSeconds = context.getBean(REFRESH_INTERVAL_SECONDS_BEAN_NAME, Integer.class);
        } catch (NoSuchBeanDefinitionException e) {
            refreshIntervalSeconds = REFRESH_INTERVAL_SECONDS_DEFAULT;
        }
    }

    /**
     * <p>
     * Retrieves the application status using the status checker.
     * </p>
     * <p>
     * The status page can behave in different ways for different purposes. The URL structure will determine the output:
     * <dl>
     * <dt>/status/lb</dt>
     * <dd>used by load-balancers for their health checks (the default)</dd>
     * <dt>/status/info.json</dt>
     * <dd>used by central status aggregator applications</dd>
     * <dt>/status/info.html</dt>
     * <dd>for human access</dd>
     * </dl>
     *
     * @param request  the request
     * @param response the response
     * @throws ServletException when something bad happens
     * @throws IOException      when something bad happens with the network
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        getServerStatusChecker().getServerStatus(refreshIntervalSeconds, statusChecker, request, response);
    }

    private ServerStatusChecker getServerStatusChecker() {
        return this.serverStatusChecker;
    }
}
