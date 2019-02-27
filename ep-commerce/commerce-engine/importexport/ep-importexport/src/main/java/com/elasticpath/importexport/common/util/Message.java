/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.util;

import java.util.Arrays;
import java.util.Objects;

import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.importexport.common.types.JobType;

/**
 * The <code>Message</code> class contains message code and parameters.
 * Message code corresponds to the message key in the resource bundle.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class Message {

	private String code;

	private String[] params;
	
	private Throwable exception;
	
	private JobType jobType;

	/**
	 * Default constructor.
	 */
	public Message() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param code message code
	 * @param params message parameters
	 */
	public Message(final String code, final String... params) {
		this.code = code;
		this.params = params;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param code message code
	 * @param params message parameters
	 * @param exception exception
	 */
	public Message(final String code, final  Throwable exception, final String... params) {
		this.code = code;
		this.params = params;
		this.exception = exception;
	}
	
	/**
	 * Factory method that constructs the instance of <code>Message</code> with the specified <code>JobType</code> for which the failure occurred.
	 * Use this method only when you need to indicate that the failure occurred during the specified <code>JobType</code> import/export.
	 * 
	 * @param code message code
	 * @param jobType <code>JobType</code> for which the failure occurred
	 * @param params message parameters
	 * @return message with the specified <code>JobType</code>
	 */
	public static Message createJobTypeFailure(final String code, final JobType jobType, final String... params) {
		Message message = new Message(code, params);
		message.setJobType(jobType);
		return message;
	}
	
	/**
	 * Factory method that constructs the instance of <code>Message</code> with the specified <code>JobType</code> for which the failure occurred.
	 * Use this method only when you need to indicate that the failure occurred during the specified <code>JobType</code> import/export.
	 * 
	 * @param code message code
	 * @param jobType <code>JobType</code> for which the failure occurred
	 * @param exception associated exception
	 * @param params message parameters
	 * @return message with the specified <code>JobType</code>
	 */
	public static Message createJobTypeFailure(final String code, final JobType jobType, final Throwable exception, final String... params) {
		Message message = new Message(code, exception, params);
		message.setJobType(jobType);
		return message;
	}
	
	/**
	 * Factory method that constructs the instance of <code>Message</code> with the specified <code>JobType</code> for which the failure occurred.
	 * Use this method only when you need to indicate that the failure occurred during the specified <code>JobType</code> import/export.
	 * 
	 * @param message message
	 * @param jobType <code>JobType</code> for which the failure occurred
	 * @return message with the specified <code>JobType</code>
	 */
	public static Message createJobTypeFailure(final Message message, final JobType jobType) {
		message.setJobType(jobType);
		return message;
	}

	/**
	 * Gets message code.
	 * 
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets message code.
	 * 
	 * @param code message code
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets message parameters.
	 * 
	 * @return params
	 */
	public String[] getParams() {
		return params; // NOPMD
	}

	/**
	 * Sets message parameters.
	 * 
	 * @param params message parameters.
	 */
	public void setParams(final String[] params) { // NOPMD
		this.params = params;
	}

	/**
	 * Sets exception.
	 * 
	 * @param exception the exception to set
	 */
	public void setException(final Throwable exception) {
		this.exception = exception;
	}

	/**
	 * Gets exception.
	 * 
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}
	
	/**
	 * Gets the jobType.
	 * 
	 * @return the jobType
	 */
	public JobType getJobType() {
		return jobType;
	}

	/**
	 * Sets the jobType.
	 * 
	 * @param jobType <code>JobType</code>
	 */
	protected void setJobType(final JobType jobType) {
		this.jobType = jobType;
	}

	/**
	 * Checks whether this message has a <code>JobType</code> associated with it.
	 * 
	 * @return true, in case this is a <code>JobType</code> failure.
	 */
	public boolean isJobTypeFailure() {
		return this.jobType != null;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(code, jobType, params);
	}

	@Override
	public boolean equals(final Object obj) {	// NOPMD
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Message other = (Message) obj;

		return Objects.equals(code, other.code)
			&& Objects.equals(jobType, other.jobType)
			&& Arrays.equals(params, other.params);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(code);
		if (jobType != null) {
			builder.append(' ').append(jobType);
		}
		if (!ArrayUtils.isEmpty(params)) {
			builder.append(' ').append(Arrays.toString(params));
		}
		return builder.toString();
	}
}
