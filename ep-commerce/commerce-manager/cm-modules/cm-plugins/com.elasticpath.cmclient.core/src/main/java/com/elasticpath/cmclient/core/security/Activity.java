/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.security;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the activity in the permissions tree.
 */
public class Activity {

	private final String name;
	
	private final String activityId;
	
	private final String perspectiveId;
	
	private final Collection<Permission> permissions = new ArrayList<Permission>();

	
	/**
	 *
	 * @param activityId the activity activityId
	 * @param name the name of the activity
	 * @param perspectiveId perspective id associated with activity id.
	 */
	public Activity(final String activityId, final String name, final String perspectiveId) {
		super();
		this.activityId = activityId;
		this.name = name;
		this.perspectiveId = perspectiveId;
	}

	/**
	 *
	 * @return the display name of the activity
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return collection of permissions
	 */
	public Collection<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * Gets the activity id.
	 * 
	 * @return activity id
	 */
	public String getActivityId() {
		return activityId;
	}
	
	/**
	 * Gets the perspective id associated with activity.
	 *
	 * @return perspective id
	 */
	public String getPerspectiveId() {
		return perspectiveId;
	}
}
