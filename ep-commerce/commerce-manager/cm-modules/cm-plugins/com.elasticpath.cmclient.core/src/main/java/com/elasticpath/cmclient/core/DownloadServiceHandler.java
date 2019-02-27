/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.springframework.http.HttpStatus;

import com.elasticpath.cmclient.core.util.FileSystemUtil;

/**
 * Service Handler for file downloads.
 */
public class DownloadServiceHandler implements ServiceHandler {

    /**
     * Constant to identify this service.
     */
    public static final String SERVICE_NAME = "downloadServiceHandler"; //$NON-NLS-1$


    /**
     * The Service method. Returns a file for the given filename in the request parameter.
     *
     * @param request  the request parameter.
     * @param response the response.
     * @throws IOException      on FileInputStream errors.
     * @throws ServletException on Response errors.
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException, ServletException {
        // Which file to download?
        String fileName = request.getParameter("filename"); //$NON-NLS-1$
        String tempDir = FileSystemUtil.getTempDirectory() + File.separator;

        File file = new File(tempDir, FilenameUtils.getName(fileName)); //$NON-NLS-1$
        if (!file.exists()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        response.setContentType("application/octet-stream"); //$NON-NLS-1$
        response.setContentLength(Math.toIntExact(file.length()));
        String contentDisposition = "attachment; filename=\"" + FilenameUtils.getName(fileName) + "\""; //$NON-NLS-1$
        response.setHeader("Content-Disposition", contentDisposition); //$NON-NLS-1$

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            IOUtils.copy(fileInputStream, response.getOutputStream());
        }
    }
}