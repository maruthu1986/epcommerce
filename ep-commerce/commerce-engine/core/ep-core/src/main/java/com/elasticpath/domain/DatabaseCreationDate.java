/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.domain;

import java.util.Date;

/**
 * An object that has a creation date that will be set to database time.
 */
public interface DatabaseCreationDate {

	/**
	 * Get the date that this was created on.
	 *
	 * @return the creation date
	 */
	Date getCreationDate();

	/**
	 * Set the date when the object was created.
	 *
	 * @param creationDate the date when the profile value was created
	 */
	void setCreationDate(Date creationDate);
}
