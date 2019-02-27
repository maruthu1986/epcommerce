/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.extensions.search.index.status.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;

/**
 * Simple checker for the state of the search server indexes. It will only check the first indexing process.
 */
public class SearchIndexStatusCheckerServlet extends HttpServlet {

	private static final long serialVersionUID = -6297009033288967143L;

	private IndexBuildStatusDao indexBuildStatusDao;
	private Boolean searchIndexCompleted = false;


	@Override
	public void init() throws ServletException {
		super.init();

		final ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

		indexBuildStatusDao = context.getBean("indexBuildStatusDao", IndexBuildStatusDao.class);
	}

	/**
	 * Get the result of the first search indexing process.
	 *
	 * @param request	the request
	 * @param response	the response
	 * @throws ServletException	when something bad happens
	 * @throws IOException		when something bad happens with the network
	 */
	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		response.setHeader("Cache-Control", "no-cache");


		if (searchIndexCompleted) {
			response.sendError(HttpURLConnection.HTTP_OK);
		} else {
			if (SolrStatusCheckerHelper.isSolrIndexingStarted(request.getRequestURL().toString()) && !isSearchCurrentlyIndexing()) {
				response.sendError(HttpURLConnection.HTTP_OK);
				searchIndexCompleted = true;
			} else {
				response.sendError(HttpURLConnection.HTTP_UNAVAILABLE);
			}
		}
	}

	private boolean isSearchCurrentlyIndexing() {
		List<IndexBuildStatus> indexBuildStatusList = indexBuildStatusDao.list();

		return indexBuildStatusList.stream()
				.filter(indexBuildStatus -> "REBUILD_IN_PROGRESS".equalsIgnoreCase(indexBuildStatus.getIndexStatus().toString()))
				.findFirst().isPresent();
	}

}
