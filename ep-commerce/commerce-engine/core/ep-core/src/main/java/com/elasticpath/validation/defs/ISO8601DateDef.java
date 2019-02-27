/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.ISO8601DateConstraint;

/**
 * ISO8601 Date constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class ISO8601DateDef extends AbstractConstraintDef<ISO8601DateConstraint> {
	/**
	 * Default constructor.
	 */
	public ISO8601DateDef() {
		super(ISO8601DateConstraint.class);
	}
}
