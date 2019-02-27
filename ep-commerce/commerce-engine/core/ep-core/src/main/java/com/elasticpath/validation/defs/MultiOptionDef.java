/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.MultiOptionConstraint;

/**
 * Multi-option constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class MultiOptionDef extends AbstractConstraintDef<MultiOptionConstraint> {

	/**
	 * Default constructor.
	 */
	public MultiOptionDef() {
		super(MultiOptionConstraint.class);
	}
}
