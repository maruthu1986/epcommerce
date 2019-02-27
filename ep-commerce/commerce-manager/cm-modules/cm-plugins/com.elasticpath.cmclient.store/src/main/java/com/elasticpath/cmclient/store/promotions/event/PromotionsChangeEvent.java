/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.event;

import java.util.EventObject;

import com.elasticpath.domain.rules.Rule;

/**
 * Event object for the promotions change event.
 */
public class PromotionsChangeEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private final Rule rule;

	/**
	 * Constructs the event object.
	 * 
	 * @param source the source of the event
	 * @param rule the <code>Rule</code> object
	 */
	public PromotionsChangeEvent(final Object source, final Rule rule) {
		super(source);
		this.rule = rule;
	}

	/**
	 * Returns the <code>Rule</code> object.
	 * 
	 * @return the <code>Rule</code> object
	 */
	public Rule getPromotionRule() {
		return this.rule;
	}
}
