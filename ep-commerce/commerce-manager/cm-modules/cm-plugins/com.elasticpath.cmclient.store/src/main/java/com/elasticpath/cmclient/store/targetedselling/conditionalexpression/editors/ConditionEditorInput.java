/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors;

import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * Class holds the input data for the condition editor.
 * 
 */
public class ConditionEditorInput extends GuidEditorInput {

	private final ConditionalExpression conditionalExpression;
		
	/**
	 * Default constructor for creating new editor input.
	 * Creates a new instance of the handler.
	 * @param conditionalExpression conditional expression object
	 */
	public ConditionEditorInput(final ConditionalExpression conditionalExpression) {
		super(conditionalExpression.getGuid(), ConditionalExpression.class);
		this.conditionalExpression = conditionalExpression;
	}
	
	/**
	 * @return conditional expression object.
	 */
	public ConditionalExpression getConditionalExpression() {
		return this.conditionalExpression;
	}
	
}
