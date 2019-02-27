/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.conditionbuilder.extenders;

import com.elasticpath.tags.domain.TagDefinition;

/**
 * Factory interface used by extensions to return a creator object for the TagDefinition and/or type.
 * 
 * Extension plugins "register" interest in a TagDefinition by returning an instance of ConditionRowCompositeCreator
 * that creates the composite.  It's up to the extension plugins to determine what that interest is based on. 
 */
public interface ConditionRowCompositeCreatorFactory {
	
	/**
	 * Implemented by the extension plugin to return a creator object for the TagDefinition.  If an extension
	 * isn't interested in it then it can simply return null to allow the OOTB ConditionRowComposite to be used.
	 *
	 * @param <M>  model type
	 * @param <OP>  operator type
	 * @param <M2> parent model adapter type
	 * @param <O2> parent operator type
	 * @param tagDefinition the tag definition
	 * @return a creator instance if the extension wants to handle it.  null to use OOTB processing.
	 */
	<M, OP, M2, O2> ConditionRowCompositeCreator<M, OP, M2, O2> getCreator(TagDefinition tagDefinition);
}
