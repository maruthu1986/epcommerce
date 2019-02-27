/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter;

import java.util.List;

import com.elasticpath.tags.domain.TagDefinition;

/**
 * ConditionModelAdapter.
 * @param <M> model for adapter
 * @param <O> operator object
 */
public interface ConditionModelAdapter<M, O> extends BaseModelAdapter<M> {

	/** Value for property change events. */
	String TAG_DEFINITION = "tagDefinition"; //$NON-NLS-1$
	/** Value for property change events. */
	String OPERATOR = "operator"; //$NON-NLS-1$
	/** Value for property change events. */
	String TAG_VALUE = "tagValue"; //$NON-NLS-1$

	/**
	 * Set the left operand.
	 * @param leftOperand left operand
	 */
	void setTagDefinition(TagDefinition leftOperand);
	
	/**
	 * Get the left operand.
	 * @return TagDefinition
	 */
	TagDefinition getTagDefinition();
	
	/**
	 * Set the operator.
	 * @param operator operator
	 */
	void setOperator(O operator);
	
	/**
	 * Get the operator.
	 * @return ConditionalOperatorType
	 */
	O getOperator();
	
	/**
	 * Get list of available operators for this tag.
	 * @return operators list
	 */
	List<O> getOperatorsList();
	
	/**
	 * Set right operand.
	 * @param value right operand
	 */
	void setTagValue(Object value);
	
	/**
	 * Get the right operand.
	 * @return Object
	 */
	Object getTagValue();

	/**
	 * @param stringTagValue a string tag value representation
	 * @return tag value in the correct class form (if all attempt fail, for
	 *         example in case of NumberFormatException, returns stringTagValue back)
	 */
	Object getTagValueFromString(String stringTagValue);

	/**
	 * Get resource adapter for tagDefinition.
	 * @return resource adapter
	 */
	ResourceAdapter<TagDefinition> getResourceAdapterForTagDefinition();
	
	/**
	 * Get resource adapter for operator.
	 * @return resource adapter
	 */
	ResourceAdapter<O> getResourceAdapterForOperator();
}
