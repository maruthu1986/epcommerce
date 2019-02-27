/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.rules.impl;

import com.elasticpath.domain.rules.ActiveRule;

/**
 * 
 * Simple rule id wrapper for drools, that indicates applicable rule. 
 *
 */
public class ActiveRuleImpl implements ActiveRule {
	
	private long ruleId;

	/**
	 * Construct simple rule id wrapper for drools.
	 * @param ruleId rule id.
	 */
	public ActiveRuleImpl(final long ruleId) {
		super();
		this.ruleId = ruleId;
	}

	/**
	 * @return rule id
	 */
	@Override
	public long getRuleId() {
		return ruleId;
	}

	/**
	 * Set rule id.
	 * @param ruleId rule id.
	 */
	@Override
	public void setRuleId(final long ruleId) {
		this.ruleId = ruleId;
	}
	
	

}
