/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.security;


/**
 * Defines a Set of Permissions, and knows how to read the Permissions information from
 * the authorization extension point.
 */
public class Permission  {

	private final String name;

	private final String description;

	private final String key;

	private final String activityId;


	/**
	 *
	 * @param name the permission display name
	 * @param description the permission description
	 * @param key the id of this permission
	 * @param activityId the id of this permission
	 */
	public Permission(final String name, final String description, final String key, final String activityId) {
		super();
		this.name = name;
		this.description = description;
		this.key = key;
		this.activityId = activityId;
	}

	/**
	 *
	 * @return the display name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return the description of this permission
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *
	 * @return the id of this permission
	 */
	public String getKey() {
		return key;
	}

	/**
	 *
	 * @return activity id
	 */
	public String getActivityId() {
		return activityId;
	}

	@Override
	public String toString() {
		StringBuilder sBuffer = new StringBuilder();
		sBuffer.append("\nPermission: { activityId: '"); //$NON-NLS-1$
		sBuffer.append(this.activityId);
		sBuffer.append("', key: '"); //$NON-NLS-1$
		sBuffer.append(this.key);
		sBuffer.append("', name: '"); //$NON-NLS-1$
		sBuffer.append(this.name);
		sBuffer.append("', desc: '"); //$NON-NLS-1$
		sBuffer.append(this.description);
		sBuffer.append("' }");  //$NON-NLS-1$
		return sBuffer.toString();
	}

}
