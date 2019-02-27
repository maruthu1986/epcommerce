/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.ServiceHandler;
import org.json.JSONObject;

import com.elasticpath.cmclient.core.helpers.TestIdMapManager;

/**w
 * Service Handler for sending TestIdMaps to client.
 */
public class TestMapServiceHandler implements ServiceHandler {

    /**
     * Constant to identify this service.
     */
    public static final String SERVICE_NAME = "TestMapServiceHandler"; //$NON-NLS-1$

    /**
     * The Service method.
     * The service sends two maps as a response which contain a mapping from <hashedIds, fieldName> and
     * <fieldName, localizedValue>. The field names and localized values for
     * those fields from from .*PluginResources.properties files for each plugin
     *
     * @param request  the request parameter.
     * @param response the response.
     * @throws IOException      on FileInputStream errors.
     * @throws ServletException on Response errors.
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        Map<String, Map> responseMap = new HashMap<>();

        responseMap.put("minified", TestIdMapManager.getMinifiedMap());
        response.setContentType("application/json");
        response.getWriter().write(new JSONObject(responseMap).toString());
    }
}