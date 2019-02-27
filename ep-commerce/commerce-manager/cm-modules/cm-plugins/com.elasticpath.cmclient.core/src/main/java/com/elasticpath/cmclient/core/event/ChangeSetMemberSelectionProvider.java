/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

/**
 * Interface to indicate to the AddToChangeSetActionDelegate that the SelectionService event source (IWorkbenchPart sourcepart) 
 * is a provider of Change Set object members, such as Products, SKUs, Promotions, Categories and such. Not all SelectService events 
 * apply to Change Sets, and also checking for this marker interface increases UI performance.<br>
 * Allows for sourceparts to provide selection resolution when selection is a wrapper around actual change set object.   
 */
public interface ChangeSetMemberSelectionProvider {
	
	/**
	 * Resolves selection of the sourcepart. Some sourceparts may have model which is different from a change set object, in which case the
	 * resolution is required.
	 * 
	 * @param changeSetObjectSelection current selection of the sourcepart
	 * @return resolved change set object when resolution is required or passed changeSetObjectSelection otherwise
	 */
	Object resolveObjectMember(Object changeSetObjectSelection);

}

