/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.BooleanConstraint;

/**
 * Boolean constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class BooleanDef extends AbstractConstraintDef<BooleanConstraint> {

	/**
	 * Default constructor.
	 */
	public BooleanDef() {
		super(BooleanConstraint.class);
	}
}
