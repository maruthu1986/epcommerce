/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.IntegerConstraint;

/**
 * Integer constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class IntegerDef extends AbstractConstraintDef<IntegerConstraint> {
	/**
	 * Default constructor.
	 */
	public IntegerDef() {
		super(IntegerConstraint.class);
	}
}
