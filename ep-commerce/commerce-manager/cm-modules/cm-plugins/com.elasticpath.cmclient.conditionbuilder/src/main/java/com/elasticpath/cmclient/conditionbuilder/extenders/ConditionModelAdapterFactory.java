/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.conditionbuilder.extenders;

import com.elasticpath.cmclient.conditionbuilder.adapter.ConditionModelAdapter;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.TagOperator;

/**
 * Interface implemented by extension plugins to instantiate their own ConditionModelAdapter.
 */
public interface ConditionModelAdapterFactory {
	
	/**
	 * Used by extension plugins to instantiate their own extension of ConditionModelAdapter if they have an extension of it.
	 *
	 * @param model the Condition
	 * @return the new model adapter
	 */
	ConditionModelAdapter<Condition, TagOperator> createAdapter(Condition model);

}
