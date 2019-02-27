/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;

/**
 * Target class for testing connectivity to SOAP based Web Services. The test only opens a connection to the URL, which should be to the service's
 * WSDL, checks the response code, and verifies the content type is "text/xml".
 */
@SuppressWarnings({"PMD.GodClass"})
public class WebServiceStatusCheckerTargetImpl extends AbstractStatusCheckerTarget {

	private static final Logger LOG = Logger.getLogger(WebServiceStatusCheckerTargetImpl.class);

	private static final String METHOD_GET = "GET";

	private static final String METHOD_POST = "POST";

	private static final String ENCODING_UTF_8 = "UTF-8";

	private static final String VALUE_NONE = "none";

	private final Map<String, String> connectionHeaderMap = new HashMap<>();

	private final Map<String, String> connectionParameterMap = new HashMap<>();

	private String connectionEndpoint;

	private String connectionEndpointExtras = "";

	private String connectionHeaders;

	private String connectionParameters;

	private String connectionContentType;

	private String connectionMethod = METHOD_GET;

	private String connectionEncoding = ENCODING_UTF_8;

	/**
	 * Attempts to connect to a SOAP based Web service.
	 * 
	 * @return OK if the connection was successful.
	 * @see com.elasticpath.health.monitoring.impl.AbstractStatusCheckerTarget#check()
	 */
	@Override
	public Status check() {
		LOG.debug("Checking Web server connectivity status " + this.getName());
		return this.testWebServiceSoapEndpoint();
	}

	/**
	 * Retrieves the properties {@code webService.endpoint}, {@code webService.endpoint.extras}, and {@code webService.endpoint.contenttype}, then
	 * calls {code testUrl} with the complete endpoint.
	 * 
	 * @return {@link StatusType#OK OK} if the connection was successful
	 */
	private Status testWebServiceSoapEndpoint() {
		return testUrl();
	}

	/**
	 * Calls the {@code testUrlSimple}, catching any exceptions it may throw.
	 * 
	 * @return {@link StatusType#OK OK} if the connection was successful
	 */
	protected Status testUrl() {
		Status status;

		try {
			status = testUrlConnection();
			LOG.debug("Successful Web Service connetivity check " + this.getName());
		} catch (IOException e) {
			LOG.error("Connection connection for " + this.getName(), e);
			status = createStatus(StatusType.CRITICAL, "Connection connection", e.getMessage());
		}

		return status;
	}

	/**
	 * Attempts to open a connection to the given URL and checks its content type.
	 * 
	 * @return the status
	 * @throws IOException for MalformedURLException cases and general connection issues
	 */
	protected Status testUrlConnection() throws IOException {

		HttpURLConnection connection = (HttpURLConnection) new URL(getURL()).openConnection();
		try {
			setConnectionMethod(connection);

			setConnectionHeaders(connection);

			// Can throw an exception that should be caught and handled above.
			verifyResponseCode(connection);

			// Can throw an exception that should be caught and handled above.
			verifyContentType(connection);

			// Can throw an exception that should be caught and handled above.
			verifyConnectionData(connection);

			// Everything must be OK at this point.
			return createStatus(StatusType.OK, null, null);
		} finally {
			// Ensure the connection is closed.
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * Verifies the connection's response code, which must be HTTP_OK.
	 * 
	 * @param connection the connection
	 * @throws IOException when something bad happens with the I/O
	 * @throws UnknownServiceException when the code is invalid
	 */
	private void verifyResponseCode(final HttpURLConnection connection) throws IOException {
		int code = connection.getResponseCode();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new UnknownServiceException("Invalid response code:  " + code);
		}
	}

	/**
	 * Verifies the expected content type of the connection.
	 * 
	 * @param connection the connection
	 * @throws UnknownServiceException when the content type does not match expectations
	 */
	protected void verifyContentType(final HttpURLConnection connection) throws UnknownServiceException {
		String type = connection.getContentType();
		// Allow for an empty expected content type. Don't check the type in this case.
		// This is only allowed for the localhost where the service has been mocked.
		// TODO: Do we really need or want to do this?
		if (StringUtils.isNotEmpty(connectionContentType) && !this.connectionContentType.equals(type)) {
			throw new UnknownServiceException("Invalid content type: " + type);
		}
	}

	/**
	 * Sets the method on the connection.
	 * 
	 * @param connection the connection
	 * @throws ProtocolException when the connection has already been made.
	 */
	protected void setConnectionMethod(final HttpURLConnection connection) throws ProtocolException {
		connection.setRequestMethod(connectionMethod);
		connection.setDoOutput(this.connectionMethod.equals(METHOD_GET));
	}

	/**
	 * Sets any connection headers.
	 * 
	 * @param connection the connection
	 */
	protected void setConnectionHeaders(final HttpURLConnection connection) {
		for (Map.Entry<String, String> entry : this.connectionHeaderMap.entrySet()) {
			connection.addRequestProperty(entry.getKey(), connectionHeaderMap.get(entry.getValue()));
		}
	}

	/**
	 * Verifies the connection data. Can be overridden to verify the actual data returned.
	 * 
	 * @param connection the connection
	 * @throws IOException when any I/O errors occur.
	 */
	protected void verifyConnectionData(final HttpURLConnection connection) throws IOException {
		// Only send output if needed.
		if (this.connectionMethod.equals(METHOD_POST) && this.connectionParameterMap.size() > 0) {
			try (OutputStream output = connection.getOutputStream()) {
				output.write(this.getOutputParameters().getBytes(this.connectionEncoding));
			}
		}

		try (InputStream input = connection.getInputStream()) {
			String inputData = IOUtils.toString(input);
			validateInputData(inputData);
		}
	}

	/**
	 * Validates the data read in from the connection. Meant to be overridden to test data from various Web services.
	 * 
	 * @param data the data
	 * @throws UnknownServiceException when the data doesn't meet expectations.
	 */
	protected void validateInputData(final String data) throws UnknownServiceException {
		if (data.length() == 0) {
			throw new UnknownServiceException("No data read from connection");
		}
	}

	/**
	 * Returns the connection URL with request parameters if the method is "GET".
	 * 
	 * @return URL with any request parameters if applied
	 */
	private String getURL() {
		StringBuilder urlBuilder = new StringBuilder(this.connectionEndpoint);
		if (this.connectionEndpointExtras != null && !this.connectionEndpointExtras.isEmpty()) {
			if (urlBuilder.lastIndexOf("/") != urlBuilder.length() - 1) {
				urlBuilder.append('/');
			}
			urlBuilder.append(this.connectionEndpointExtras);
		}

		// If GET then add the parameters to the URL.
		if (this.connectionMethod.equals(METHOD_GET) && this.connectionParameterMap.size() > 0) {
			// Remove any final slashes.
			if (urlBuilder.lastIndexOf("/") == urlBuilder.length() - 1) {
				urlBuilder.deleteCharAt(urlBuilder.length() - 1);
			}
			urlBuilder.append('?').append(getOutputParameters());
		}

		return urlBuilder.toString();
	}

	private String getOutputParameters() {
		StringBuilder parameters = new StringBuilder();
		int iCount = 0;

		for (Map.Entry<String, String> entry : this.connectionParameterMap.entrySet()) {
			if (iCount++ > 0) {
				parameters.append('&');
			}
			parameters.append(entry.getKey()).append('=').append(entry.getValue());
		}
		return parameters.toString();
	}

	/**
	 * Iterates through the semicolon delimited set of name/value combinations and adds them to the Map.
	 * 
	 * @param objects the String collection of name/value pairs
	 * @param dataMap Map of name/value pairs
	 */
	private void extractMapValues(final String objects, final Map<String, String> dataMap) {

		dataMap.clear();

		if (StringUtils.isEmpty(objects)) {
			return;
		}

		StringTokenizer tokens = new StringTokenizer(objects, ";");
		while (tokens.hasMoreTokens()) {
			String object = tokens.nextToken();
			StringTokenizer objectTokens = new StringTokenizer(object, "=");
			while (objectTokens.hasMoreTokens()) {
				String name = objectTokens.nextToken();
				String value = objectTokens.nextToken();
				dataMap.put(name, value);
			}
		}
	}

	public String getHeaders() {
		return connectionHeaders;
	}

	/**
	 * Sets the connection headers to use when communicating with the remote web service.
	 * 
	 * @param connectionHeaders the connection headers
	 */
	public void setHeaders(final String connectionHeaders) {
		this.connectionHeaders = connectionHeaders;
		extractMapValues(this.connectionHeaders, this.connectionHeaderMap);
	}

	public String getParameters() {
		return connectionParameters;
	}

	/**
	 * Sets the request parameters to use when communicating with the remote web service.
	 * 
	 * @param connectionParameters the connection parameters
	 */
	public void setParameters(final String connectionParameters) {
		this.connectionParameters = connectionParameters;
		extractMapValues(this.connectionParameters, this.connectionParameterMap);
	}

	public String getContentType() {
		return connectionContentType;
	}

	/**
	 * Sets the HTTP content type to use when communicating with the remote web service.
	 * 
	 * @param connectionContentType the content type
	 */
	public void setContentType(final String connectionContentType) {
		this.connectionContentType = connectionContentType;
		if (this.connectionContentType == null) {
			this.connectionContentType = "";
		} else {
			this.connectionContentType = this.connectionContentType.trim();
		}
		if (VALUE_NONE.equalsIgnoreCase(this.connectionContentType)) {
			this.connectionContentType = "";
		}
	}

	public String getMethod() {
		return connectionMethod;
	}

	/**
	 * Sets the HTTP request method to use when communicating with the remote web service.
	 * 
	 * @param connectionMethod the connection method
	 */
	public void setMethod(final String connectionMethod) {
		this.connectionMethod = connectionMethod;
		if (this.connectionMethod == null) {
			this.connectionMethod = METHOD_GET;
		} else {
			this.connectionMethod = this.connectionMethod.trim().toUpperCase();
		}
	}

	public String getEncoding() {
		return connectionEncoding;
	}

	public void setEncoding(final String connectionEncoding) {
		this.connectionEncoding = connectionEncoding;
	}

	public String getEndpoint() {
		return connectionEndpoint;
	}

	public void setEndpoint(final String connectionEndpoint) {
		this.connectionEndpoint = connectionEndpoint;
	}

	public String getEndpointExtras() {
		return connectionEndpointExtras;
	}

	public void setEndpointExtras(final String connectionEndpointExtras) {
		this.connectionEndpointExtras = connectionEndpointExtras;
	}

}
