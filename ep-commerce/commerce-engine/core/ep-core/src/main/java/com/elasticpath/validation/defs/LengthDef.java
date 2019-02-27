/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.LengthConstraint;

/**
 * Length constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class LengthDef extends AbstractConstraintDef<LengthConstraint> {

	/**
	 * Default constructor.
	 */
	public LengthDef() {
		super(LengthConstraint.class);
	}

	/**
	 * Set min size constraint.
	 *
	 * @param min minimum size value.
	 * @return this instance.
	 */
	public LengthDef min(final int min) {
		addParameter("min", min);
		return this;
	}

	/**
	 * Set max size constraint.
	 *
	 * @param max maximum size value.
	 * @return this instance.
	 */
	public LengthDef max(final int max) {
		addParameter("max", max);
		return this;
	}
}
