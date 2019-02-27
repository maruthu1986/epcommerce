/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.event;

import java.util.EventObject;

import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * Event object for the condition change event.
 */
public class ConditionsChangeEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private final ConditionalExpression condition;

	/**
	 * Constructs the event object.
	 * 
	 * @param source the source of the event
	 * @param condition the <code>ConditionalExpression</code> object
	 */
	public ConditionsChangeEvent(final Object source, final ConditionalExpression condition) {
		super(source);
		this.condition = condition;
	}

	/**
	 * Returns the <code>ConditionalExpression</code> object.
	 * 
	 * @return the <code>ConditionalExpression</code> object
	 */
	public ConditionalExpression getCondition() {
		return this.condition;
	}
}
