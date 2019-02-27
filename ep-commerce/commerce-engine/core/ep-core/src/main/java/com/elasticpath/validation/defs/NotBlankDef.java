/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import com.elasticpath.validation.constraints.NotBlank;

/**
 * Not-blank constraint definition used with
 * {@link org.hibernate.validator.HibernateValidator}.
 */
public class NotBlankDef extends AbstractConstraintDef<NotBlank> {

	/**
	 * Default constructor.
	 */
	public NotBlankDef() {
		super(NotBlank.class);
		super.addParameter("message", "{field.required}");
	}
}
