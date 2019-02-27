/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tags.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.ConditionBuilder;
import com.elasticpath.tags.service.TagDefinitionReader;

/**
 * Builds proper {@link Condition} by looking up the {@link TagDefinition} and its data type.
 */
public class ConditionBuilderImpl implements ConditionBuilder {
	
	private static final Logger LOG = Logger.getLogger(ConditionBuilderImpl.class);
	
	private TagDefinitionReader tagDefinitionReader;
		
	/**
	 * Builds the {@link Condition} object by using given arguments. Downcast the value according to {@link TagDefinition}
	 * data type.
	 * 
	 * @param tagDefinitionName the name of the {@link TagDefinition}
	 * @param operator the operator
	 * @param value the value of the condition - right operand
	 * @return the built Condition (returns null if any of the parameters supplied are null)
	 * @throws IllegalArgumentException if any of the arguments is null or if java type cannot
	 *                                  be looked up for the tag definition or if value provided
	 *                                  does not match the java type in tag definition
	 */
	@Override
	public Condition build(final String tagDefinitionName, final String operator, final Object value)
		throws IllegalArgumentException {
		
		if (tagDefinitionName == null || operator == null || value == null) {
			String message = "Condition [" + tagDefinitionName + "," + operator + ",";
			if (value == null) {
				message += "null";
			} else {
				message += "(" + value.getClass().getName() + ") " + value;
			}
			message += "] cannot be created since all fields are mandatory and cannot be null";
			LOG.error(message);
				
			throw new IllegalArgumentException(message);
		}
		
		final TagDefinition tagDefinition = tagDefinitionReader.findByName(tagDefinitionName);

		if (tagDefinition == null || tagDefinition.getValueType() == null 
				|| StringUtils.isBlank(tagDefinition.getValueType().getJavaType())) {
			final String message = "TagDefinition [" + tagDefinitionName + "] is missing a java type or contains an invalid java type";
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		
		final String javaType = tagDefinition.getValueType().getJavaType();
		if (value.getClass().getName().equals(javaType)) {
			return new Condition(tagDefinition, operator, value);
		}
		
		final String message = "Condition [" + tagDefinitionName + "," + operator 
			+ ",(" + value.getClass().getName() + ") " + value + "] cannot be created since java type of value is wrong (require '"
			+  javaType + "')";
		LOG.error(message);
				
		throw new IllegalArgumentException(message);

	}

	/**
	 * Setter injection.
	 * 
	 * @param tagDefinitionReader the tag definition service
	 */
	public void setTagDefinitionReader(final TagDefinitionReader tagDefinitionReader) {
		this.tagDefinitionReader = tagDefinitionReader;
	}
}
