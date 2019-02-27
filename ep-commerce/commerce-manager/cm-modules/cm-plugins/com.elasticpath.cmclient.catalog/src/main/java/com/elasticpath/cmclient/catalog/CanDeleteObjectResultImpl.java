/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog;


/**
 * Object that wraps the result of a "canDelete" check on the service
 * level.
 */
public class CanDeleteObjectResultImpl implements CanDeleteObjectResult {

	private final int reason;
	private final String message;
	
	/**
	 * Default constructor for deletable object.
	 */
	public CanDeleteObjectResultImpl() {
		this.reason = 0;
		this.message = null;
	}
	
	/**
	 * Default constructor.
	 * @param reason the reason that identifies the cause of inability to delete.
	 * @throws IllegalArgumentException if reason is 0.
	 */
	public CanDeleteObjectResultImpl(final int reason) throws IllegalArgumentException {
		this(reason, null);
	}
	
	/**
	 * Default constructor.
	 * @param reason the reason that identifies the cause of inability to delete.
	 * @param message the additional message that can be pushed to the UI level.
	 * @throws IllegalArgumentException if reason is 0.
	 */
	public CanDeleteObjectResultImpl(final int reason, final String message) throws IllegalArgumentException {
		if (reason == 0) {
			throw new IllegalArgumentException("reason must be greater than 0"); //$NON-NLS-1$
		}
		this.reason = reason;
		this.message = message;
	}

	@Override
	public boolean canDelete() {
		return reason == 0;
	}

	@Override
	public int getReason() {
		return reason;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
