/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.SingleOptionConstraint;

/**
 * Single-option constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class SingleOptionDef extends AbstractConstraintDef<SingleOptionConstraint> {

	/**
	 * Default constructor.
	 */
	public SingleOptionDef() {
		super(SingleOptionConstraint.class);
	}
}
