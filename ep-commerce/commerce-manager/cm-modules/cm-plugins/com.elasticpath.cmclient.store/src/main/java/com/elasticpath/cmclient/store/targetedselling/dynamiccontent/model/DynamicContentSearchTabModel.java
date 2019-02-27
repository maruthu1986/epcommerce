/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model;

import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.impl.AssignedStatus;

/**
 * View model, represent search criteria for dynamic content list. 
 */
public interface DynamicContentSearchTabModel {
	
	/**
	 * Get the part of name for search.
	 * @return part of name for search.
	 */
	String getName();
	
	/**
	 * Set the search string.
	 * @param name name or part of name for search.
	 */
	void setName(String name);
	
	/**
	 * Set assigned status  for dynamic content.
	 * @param assignedStatus the status of dynamic content.
	 */
	void setAssignedStatus(AssignedStatus assignedStatus);
	
	/**
	 * Assigned or not dynamic content.
	 * @return  assigned dynamic content status.
	 */
	AssignedStatus getAssignedStatus();
	

}
