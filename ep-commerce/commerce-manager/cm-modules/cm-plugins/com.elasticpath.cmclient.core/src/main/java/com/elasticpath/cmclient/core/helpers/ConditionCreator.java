package com.elasticpath.cmclient.core.helpers;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.helpers.extenders.EpModelCreator;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.TagDefinition;

/**
 * Used by components that need to instantiate a new Condition.  This delegates the process to any extension
 * plugin registered to create Conditions, or uses the OOTB Condition if no extensions exist.
 */
public final class ConditionCreator {

	private static final Logger LOG = Logger.getLogger(ConditionCreator.class);
	
	private ConditionCreator() {
		// Dummy constructor to satisfy PMD.
	}
	
	/**
	 * Constructor.
	 * @param tagDefinition the left operand of the expression which is tag definition
	 * @param operator the operator
	 * @param tagValue the right operand of the expression which is value for the expression
	 * @throws UnsupportedOperationException if the right operand is an instance of Arrays, Maps or Collections.
	 * @return the new Condition
	 */
	public static Condition createModel(final TagDefinition tagDefinition, final String operator, final Object tagValue) 
			throws UnsupportedOperationException {
		EpModelCreator<Condition> creatorExtension = getExtendedCreator();
		
		if (creatorExtension == null) {
			LOG.debug("Creating OOTB Condition");
			return new Condition(tagDefinition, operator, tagValue);
		}
		
		LOG.debug("Creating extension Condition");
		Condition condition = creatorExtension.createModel();
		
		condition.setTagDefinition(tagDefinition);
		condition.setOperator(operator);
		condition.setTagValue(tagValue);

		return condition;
	}
	
	private static EpModelCreator<Condition> getExtendedCreator() {
		return PluginHelper.getModelCreator(Condition.class);
	}

}
