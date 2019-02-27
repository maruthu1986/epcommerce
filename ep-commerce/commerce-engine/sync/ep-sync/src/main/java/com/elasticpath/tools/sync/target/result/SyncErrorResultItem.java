/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.target.result;

/**
 * Encapsulates information about error occurred during transaction job unit synchronization.  
 */
public class SyncErrorResultItem extends SyncResultItem {

	private Exception cause;

	/**
	 * @return the cause
	 */
	public Exception getCause() {
		return cause;
	}
	
	/**
	 * @param cause the cause to set
	 */
	public void setCause(final Exception cause) {
		this.cause = cause;
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder(super.toString());
		if (cause != null) {
			stringBuilder.append("\nTransaction job entry failure cause: ").append(cause);
			if (cause.getCause() != null) { //NOPMD
				stringBuilder.append("\nRoot Cause: ").append(cause.getCause());
			}					
		}
		return stringBuilder.toString();
	}

}
