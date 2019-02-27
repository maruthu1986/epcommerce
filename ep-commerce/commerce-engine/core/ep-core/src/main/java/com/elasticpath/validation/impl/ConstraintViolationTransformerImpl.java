/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.validation.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.validation.ConstraintViolationTransformer;
import com.elasticpath.validation.validators.util.DynamicCartItemModifierField;

/**
 * Implementation of {@link ConstraintViolationTransformer}.
 */
public class ConstraintViolationTransformerImpl implements ConstraintViolationTransformer {

	private Map<String, String> idMap;

	private static final String FIELD_NAME = "field-name";

	private static final String EXCLUDED_ATTRIBUTES_REGEX = "payload|groups|message|validFieldOptions";

	private static final String DYNAMIC_VALIDATION_FIELD_NAME_KEY = "fieldName";

	public void setIdMap(final Map<String, String> idMap) {
		this.idMap = idMap;
	}

	@Override
	public <T> List<StructuredErrorMessage> transform(final Set<ConstraintViolation<T>> errors) {
		if (CollectionUtils.isEmpty(errors)) {
			return Collections.emptyList();
		}

		return errors.stream()
				.map(constraintViolation -> transform(constraintViolation))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	private <T> StructuredErrorMessage transform(final ConstraintViolation<T> constraintViolation) {
		String constraintViolationId = constraintViolation.getMessageTemplate();

		if (constraintViolationId.equalsIgnoreCase(constraintViolation.getMessage())) {
			return null;
		}

		String messageId = idMap.getOrDefault(constraintViolationId, constraintViolationId);
		String fieldName = constraintViolation.getPropertyPath().toString();

		Map<String, String> data = new HashMap<>();
		data.put(FIELD_NAME, fieldName);

		// Iterate through all the attributes and grab the ones not on the excluded regex.
		Map<String, Object> attributes = constraintViolation.getConstraintDescriptor().getAttributes();
		attributes.forEach((key, value) -> {
			if (!key.matches(EXCLUDED_ATTRIBUTES_REGEX)) {
				// Grab the fieldname from the attributes and overwrite the existing one.
				// This is for dynamic validation.
				if (DYNAMIC_VALIDATION_FIELD_NAME_KEY.equalsIgnoreCase(key) && !value.toString().isEmpty()) {
					data.put(FIELD_NAME, value.toString());
				} else {
					if (!DYNAMIC_VALIDATION_FIELD_NAME_KEY.equalsIgnoreCase(key)) {
						data.put(key, value.toString());
					}
				}
			}
		});

		String debugMessage;
		if ((attributes.containsKey(DYNAMIC_VALIDATION_FIELD_NAME_KEY)
			&& !attributes.get(DYNAMIC_VALIDATION_FIELD_NAME_KEY).toString().isEmpty())
			|| StringUtils.containsIgnoreCase(constraintViolation.getMessage(), fieldName)) {
			debugMessage = constraintViolation.getMessage();
		} else {
			debugMessage = fieldName + " " + constraintViolation.getMessage();
		}

		// Don't grab class-level validation invalid value because it contains all the fields (valid and invalid).
		// field.required will always return an empty value so no need to show it.
		if (constraintViolation.getLeafBean() != constraintViolation.getInvalidValue()
				&& !"field.required".equalsIgnoreCase(messageId)) {

			getInvalidValuesFromConstraintViolations(constraintViolation, data);
		}

		return new StructuredErrorMessage(messageId, debugMessage, data);
	}

	private <T> void getInvalidValuesFromConstraintViolations(final ConstraintViolation<T> constraintViolation, final Map<String, String> data) {
		String invalidValue = constraintViolation.getInvalidValue().toString();
		String invalidKey = "invalid-value";

		Object rootBeanObject = constraintViolation.getRootBean();
		if (rootBeanObject != null && rootBeanObject.getClass().isAssignableFrom(DynamicCartItemModifierField.class)) {
			DynamicCartItemModifierField rootBean = (DynamicCartItemModifierField) constraintViolation.getRootBean();
			if (rootBean != null) {
				final String[] invalidOptions = rootBean.getInvalidOptions();

				if (ArrayUtils.isNotEmpty(invalidOptions)) {
					invalidValue = showOptionsAsJsonArray(invalidOptions);
					invalidKey = "invalid-options";
				}
			}
		}
		data.put(invalidKey, invalidValue);
	}

	private String showOptionsAsJsonArray(final String[] invalidOptions) {
		String invalidOptionsAsString = ArrayUtils.toString(invalidOptions);

		return StringUtils.replaceEach(invalidOptionsAsString,
				new String[]{"{", "}"},
				new String[]{"[", "]"});
	}
}
