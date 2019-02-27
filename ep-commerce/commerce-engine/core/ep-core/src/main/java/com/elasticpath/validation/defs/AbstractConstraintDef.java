/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.defs;

import java.lang.annotation.Annotation;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.cfg.ConstraintDef;

/**
 * Abstract constraint definition that enables addition of valid options for
 * {@link com.elasticpath.domain.cartmodifier.CartItemModifierType#PICK_SINGLE_OPTION} and
 * {@link com.elasticpath.domain.cartmodifier.CartItemModifierType#PICK_MULTI_OPTION} fields.
 *
 * @param <A> Annotation type.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractConstraintDef<A extends Annotation>
		extends ConstraintDef {

	/**
	 * Custom constructor.
	 *
	 * @param annotationClass constraint annotation class.
	 */
	protected AbstractConstraintDef(final Class<A> annotationClass) {
		super(annotationClass);
	}

	/**
	 * Set expected field options (specified during import phase) to the target
	 * annotation via "validFieldOptions" annotation property.
	 * The options will be added only if they are not null/empty
	 *
	 * @param validFieldOptions valid/expected field options to set.
	 * @return an instance of current constraint definition.
	 */
	public AbstractConstraintDef validFieldOptions(final String[] validFieldOptions) {
		if (ArrayUtils.isNotEmpty(validFieldOptions)) {
			super.addParameter("validFieldOptions", validFieldOptions);
		}
		return this;
	}

	/**
	 * Add field name so it can be evaluated in messages.
	 *
	 * @param fieldName the field name
	 * @return an instance of current constraint definition.
	 */
	public AbstractConstraintDef fieldName(final String fieldName) {
		super.addParameter("fieldName", fieldName);
		return this;
	}
}
