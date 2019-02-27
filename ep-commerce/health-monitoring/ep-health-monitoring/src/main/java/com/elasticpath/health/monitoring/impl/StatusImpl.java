/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;

/**
 * Default implementation of {@link Status}.
 */
public class StatusImpl implements Status {

	private StatusType status;

	private String info;

	private String message;

	@Override
	public StatusType getStatus() {
		return status;
	}

	@Override
	public void setStatus(final StatusType status) {
		this.status = status;
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public void setInfo(final String info) {
		this.info = info;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(final String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getStatus(), this.getInfo(), this.getMessage());

	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof StatusImpl)) {
			return false;
		}

		if (EqualsBuilder.reflectionEquals(this, obj)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
