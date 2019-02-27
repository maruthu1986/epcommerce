/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.ISO8601DateTimeConstraint;

/**
 * ISO8601 Date/Time constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class ISO8601DateTimeDef extends AbstractConstraintDef<ISO8601DateTimeConstraint> {

	/**
	 * Default constructor.
	 */
	public ISO8601DateTimeDef() {
		super(ISO8601DateTimeConstraint.class);
	}
}
