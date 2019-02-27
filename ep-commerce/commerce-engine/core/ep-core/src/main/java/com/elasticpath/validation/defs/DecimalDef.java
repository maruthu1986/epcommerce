/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.DecimalConstraint;

/**
 * Decimal constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class DecimalDef extends AbstractConstraintDef<DecimalConstraint> {

	/**
	 * Default constructor.
	 */
	public DecimalDef() {
		super(DecimalConstraint.class);
	}
}
