/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.EpEmail;

/**
 * Email constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class EpEmailDef extends AbstractConstraintDef<EpEmail> {

	/**
	 * Default constructor.
	 */
	public EpEmailDef() {
		super(EpEmail.class);
		addParameter("message", "{field.invalid.email.format}");
	}


}
