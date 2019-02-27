/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.elasticpath.domain.rules.RuleAction;

/**
 * Abstract base class for Rule Actions.
 *
 */
@MappedSuperclass
public abstract class AbstractRuleActionImpl extends AbstractRuleElementImpl implements RuleAction {

	/** Specifies the salience of the action where higher salience means higher execution priority. */
	private static final int DEFAULT_SALIENCE = 0;
	private static final long serialVersionUID = 1985486352799155423L;

	private int salience = DEFAULT_SALIENCE;
	private String agendaGroup = RuleAction.DEFAULT_AGENDA_GROUP;
	
	/**
	 * Get the salience value for this rule. The higher the salience, the earlier
	 * the actions of the rule will be executed relative to other rules.
	 * @return the salience value
	 */
	@Override
	@Transient
	public int getSalience() {
		return this.salience;
	}
	
	/**
	 * Set the salience value.
	 * @see getSalience
	 * @param salience the new salience value
	 */
	@Override
	public void setSalience(final int salience) {
		this.salience = salience;
	}
	
	/**
	 * Get the agenda group for this action. The agenda group is used to determine which
	 * rules will be fired together. <br>
	 * <b>Only one action in a given rule can specify an agenda group because the group
	 * will become the agenda group of the rule</b>
	 * @return the agenda group or <code>null</code> if no agenda group is required
	 */
	@Override
	@Transient
	public String getAgendaGroup() {
		return this.agendaGroup;
	}
	
	/**
	 * Set the agenda group.
	 * @see getAgendaGroup
	 * @param agendaGroup the agenda group name
	 */
	@Override
	public void setAgendaGroup(final String agendaGroup) {
		this.agendaGroup = agendaGroup;
	}


	@Override
	@Transient
	public int getDiscountQuantityPerCoupon() {
		return 1;
	}
}
