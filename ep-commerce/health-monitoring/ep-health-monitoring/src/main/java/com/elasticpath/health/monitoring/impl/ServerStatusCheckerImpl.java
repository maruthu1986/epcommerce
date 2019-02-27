/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;

import com.elasticpath.health.monitoring.ServerStatusChecker;
import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusChecker;
import com.elasticpath.health.monitoring.StatusType;

/**
 * Implementation of ServerStatusChecker.
 */
public class ServerStatusCheckerImpl implements ServerStatusChecker {

    private static final long serialVersionUID = -6297009033288967143L;

    private static final String FORMAT_JSON = "info.json";

    private static final String FORMAT_HTML = "info.html";

    private static final String FORMAT_SIMPLE = "lb";

    private static final String ENCODING = "UTF-8";

    private static final Logger LOG = Logger.getLogger(ServerStatusCheckerImpl.class);

    /**
     * Cross-Origin Resource Sharing (CORS) header name.
     */
    protected static final String CORS_HEADER_NAME = "Access-Control-Allow-Origin";

    private final StatusUtility statusUtility = new StatusUtility();

    @Override
    public void getServerStatus(final int refreshIntervalSeconds, final StatusChecker statusChecker,
                                final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        response.setHeader("Cache-Control", "no-cache");

        // Set simple as default. Used for load balancer health checks.
        String format = FORMAT_SIMPLE;

        if (request.getRequestURI().endsWith(FORMAT_JSON)) {
            format = FORMAT_JSON;
        } else if (request.getRequestURI().endsWith(FORMAT_HTML)) {
            format = FORMAT_HTML;
        }

        Map<String, Status> results;

        // For load balancers, only check the simple statuses.
        if (FORMAT_SIMPLE.equals(format)) {
            results = statusChecker.checkStatusSimple();
        } else {
            results = statusChecker.checkStatus();
        }

        StatusType aggregatedResult;
        try {
            aggregatedResult = getStatusUtility().getAggregatedResult(results);
        } catch (Exception e) {
            LOG.error("Error with StatusChecker:  " + e.getMessage());
            aggregatedResult = StatusType.UNKNOWN;
        }

        // Assuming that when simple we're being called by a load balancer and want the header set.
        if (FORMAT_SIMPLE.equalsIgnoreCase(format) && !aggregatedResult.equals(StatusType.OK)) {
            response.sendError(HttpURLConnection.HTTP_UNAVAILABLE);
            LOG.debug("Setting code to HTTP_UNAVAILABLE.");
        }

        response.getWriter().write(getOutput(refreshIntervalSeconds, response, format, results, aggregatedResult));
    }

    private String getOutput(final int refreshIntervalSeconds, final HttpServletResponse response, final String format,
                             final Map<String, Status> results, final StatusType aggregatedResult) {
        String output;
        try {
            if (FORMAT_HTML.equals(format)) {
                response.setContentType(MediaType.TEXT_HTML_VALUE);
                output = getHTML(results, refreshIntervalSeconds);
            } else {
                response.addHeader(CORS_HEADER_NAME, "*");
                if (FORMAT_JSON.equals(format)) {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    output = getJSON(results);
                } else { // simple
                    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                    output = getPlainText(aggregatedResult, results);
                }
            }
        } catch (Exception e) {
            LOG.error("Error with formatting:  " + e);
            output = "Error Rendering";
        }
        return output;
    }

    /*
     * Generates text/plain content type output.
     */
    private String getPlainText(final StatusType aggregatedResult, final Map<String, Status> results) {
        StringBuilder builder = new StringBuilder();
        builder.append(aggregatedResult.name()).append("\n\n");
        for (Map.Entry<String, Status> result : results.entrySet()) {
            Status status = result.getValue();
            builder.append(String.format("%s: %s %s\n", result.getKey(), status.getStatus().name(), getStatusMessage(status)));
        }
        return builder.toString();
    }

    /*
     * Generates JSON formatted output.
     */
    private String getJSON(final Map<String, Status> results) {
        return getStatusUtility().getJSONResults(results).toString();
    }

    /*
     * Generates pretty HTML formatted output.
     */
    @SuppressWarnings("PMD.ConsecutiveLiteralAppends")
    private String getHTML(final Map<String, Status> results, final int refreshIntervalSeconds) throws IOException {
        try (
            InputStream styleInputStream = this.getClass().getResourceAsStream("/html/status.css");
            InputStream itemRowInputStream = this.getClass().getResourceAsStream("/html/itemRow.html")
        ) {
            StringWriter styleWriter = new StringWriter();
            StringWriter itemRowWriter = new StringWriter();
            try {
                IOUtils.copy(styleInputStream, styleWriter, ENCODING);
                IOUtils.copy(itemRowInputStream, itemRowWriter, ENCODING);
            } catch (IOException e) {
                LOG.error("Error parsing html templates:  " + e.getMessage());
            }

            String stylesheet = styleWriter.toString();
            StringBuilder strBldr = new StringBuilder();
            strBldr.append("<html><head><title>Status</title>")
                    .append("<meta http-equiv=\"cache-control\" content=\"no-cache\">")
                    .append("<meta http-equiv=\"refresh\" content=\"")
                    .append(refreshIntervalSeconds).append("\" >")
                    .append("<style type=\"text/css\">").append("<!--\n")
                    .append(stylesheet)
                    .append("</style></head><body>");


            for (Map.Entry<String, Status> entry : results.entrySet()) {
                Status status = entry.getValue();
                String styleClass = status.getStatus().name();
                String statusMessage = getStatusMessage(status);

                String template = itemRowWriter.toString();
                strBldr.append(String.format(template, styleClass, entry.getKey(), styleClass, status.getStatus().name(), statusMessage));
            }

            strBldr.append("</body></html>");

            return strBldr.toString();
        }
    }

    private String getStatusMessage(final Status status) {
        return StringUtils.defaultIfBlank(status.getMessage(), "") + StringUtils.defaultIfBlank(status.getInfo(), "");
    }

    private StatusUtility getStatusUtility() {
        return statusUtility;
    }
}
