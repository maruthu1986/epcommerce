/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.validators.util;

import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.bootstrap.ProviderSpecificBootstrap;
import javax.validation.spi.ValidationProvider;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.validation.defs.AbstractConstraintDef;
import com.elasticpath.validation.defs.LengthDef;
import com.elasticpath.validation.defs.NotBlankDef;

/**
 * Validator used for validation of dynamic fields, like {@link CartItemModifierField}.
 * It uses Hibernate implementation because of the ability to add constraints programmatically.
 */
public class DynamicCartItemModifierFieldValidator {

	private static final ProviderSpecificBootstrap<HibernateValidatorConfiguration> HIBERNATE_BOOTSTRAP =
			Validation.byProvider(HibernateValidator.class);

	private static final String VALIDATION_MESSAGES_PREFIX = "ValidationMessages";

	private final HibernateValidatorConfiguration configuration;
	private final ConstraintMapping constraintMapping;
	private final PropertyConstraintMappingContext constraintMappingContext;
	private final boolean isRequiredSuppression;

	/**
	 * Default constructor.
	 * Initializes required Hibernate structures.
	 *
	 * @param isRequiredSuppression flag indicates whether required constraint should be applied or not.
	 */
	public DynamicCartItemModifierFieldValidator(final boolean isRequiredSuppression) {
		this.configuration = HIBERNATE_BOOTSTRAP
				.providerResolver(createValidationProviderResolver(new HibernateValidator()))
				.configure()
				.messageInterpolator(
						new ResourceBundleMessageInterpolator(
								new PlatformResourceBundleLocator(VALIDATION_MESSAGES_PREFIX,
										this.getClass().getClassLoader())
						)
				);
		this.constraintMapping = configuration.createConstraintMapping();

		//tell validator what type and field need to be validated
		this.constraintMappingContext = constraintMapping
				.type(DynamicCartItemModifierField.class)
				.property("valueToValidate", ElementType.FIELD);
		this.isRequiredSuppression = isRequiredSuppression;
	}

	/**
	 * Validate dynamic field.
	 *
	 * @param fieldToValidate a field to validate.
	 * @return a set of {@link ConstraintViolation} instances or empty set.
	 */
	public Set<ConstraintViolation<DynamicCartItemModifierField>> validate(final DynamicCartItemModifierField fieldToValidate) {

		final CartItemModifierField referentField = fieldToValidate.getReferentField();

		addDefaultConstraints(fieldToValidate.getFieldName(), referentField);

		addAdditionalConstraints(referentField.getFieldType().getConstraintDefs()
						.orElse(new AbstractConstraintDef<?>[]{}),
				fieldToValidate);

		return configuration.addMapping(constraintMapping)
				.buildValidatorFactory()
				.getValidator()
				.validate(fieldToValidate);
	}

	//All dynamic fields are verified against default constraints (required and max size [optional])
	private void addDefaultConstraints(final String fieldName, final CartItemModifierField referentField) {

		if (referentField.isRequired() && !isRequiredSuppression) {
			constraintMappingContext
					.constraint(new NotBlankDef()
							.fieldName(fieldName));
		}

		if (referentField.getMaxSize() != null) {
			constraintMappingContext
					.constraint(new LengthDef()
							.max(referentField.getMaxSize())
							.fieldName(fieldName));
		}
	}

	//Additional constraints are added based on field type. Each field may contain one or more constraint definitions
	private void addAdditionalConstraints(final AbstractConstraintDef<?>[] typeConstraintDefs,
			final DynamicCartItemModifierField fieldToValidate) {

		final String[] validFieldOptions = fieldToValidate.getValidOptions().orElse(new String[]{});
		for (AbstractConstraintDef<?> constraintDef : typeConstraintDefs) {
			constraintDef.validFieldOptions(validFieldOptions);
			constraintDef.fieldName(fieldToValidate.getFieldName());
			constraintMappingContext
					.constraint(constraintDef);
		}
	}

	private ValidationProviderResolver createValidationProviderResolver(final ValidationProvider<?> provider) {
		return () -> {
			return Arrays.asList(provider);
		};
	}
}
