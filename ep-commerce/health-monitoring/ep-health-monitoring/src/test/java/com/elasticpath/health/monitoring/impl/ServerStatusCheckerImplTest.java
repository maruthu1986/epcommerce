/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.health.monitoring.StatusChecker;

@RunWith(MockitoJUnitRunner.class)
public class ServerStatusCheckerImplTest {

	@Mock
	StatusChecker statusChecker;

	@Mock
	HttpServletRequest request;

	@Mock
	HttpServletResponse response;

	@InjectMocks
	ServerStatusCheckerImpl serverStatusCheckerImpl;

	private final StringWriter stringWriter = new StringWriter();
	private final PrintWriter printWriter = new PrintWriter(stringWriter);

	private static final int REFRESH_INTERVAL = 10;

	@Before
	public void setup() throws IOException {
		when(response.getWriter()).thenReturn(printWriter);
	}

	@Test
	public void getServerStatusSimpleShouldReturnOk() throws IOException {
		when(request.getRequestURI()).thenReturn("lb");
		serverStatusCheckerImpl.getServerStatus(REFRESH_INTERVAL, statusChecker, request, response);
		assertEquals("Should return OK.", "OK\n\n", stringWriter.toString());
	}

	@Test
	public void getServerStatusJsonShouldReturnJson() throws IOException {
		when(request.getRequestURI()).thenReturn("info.json");
		serverStatusCheckerImpl.getServerStatus(REFRESH_INTERVAL, statusChecker, request, response);
		assertEquals("Should return {}.", "{}", stringWriter.toString());
	}

	@Test
	public void getServerStatusHtmlShouldReturnHtml() throws IOException {
		when(request.getRequestURI()).thenReturn("info.html");
		serverStatusCheckerImpl.getServerStatus(REFRESH_INTERVAL, statusChecker, request, response);
		assertThat("Should return html", stringWriter.toString(), containsString("<html><head><title>Status</title>"));
	}
}
