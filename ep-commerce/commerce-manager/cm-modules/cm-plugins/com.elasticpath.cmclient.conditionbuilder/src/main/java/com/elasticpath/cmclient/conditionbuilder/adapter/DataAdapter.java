/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter;

import java.util.List;

/**
 * DataAdapter for object to get children items.
 * @param <MODEL> model object type
 * @param <CHILD> child object type 
 */
public interface DataAdapter<MODEL, CHILD> {

	/**
	 * Get children for current object.
	 * @param model MODEL object type
	 * @return List of CHILD object type
	 */
	List<CHILD> getChildren(MODEL model);
}
