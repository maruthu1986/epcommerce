/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.valueeditor;


/**
 * This is interface to helper class that must be used with value provider
 * linking user friendly UI representation of data value with data key used in tag framework.  
 *
 * Resolve object's UI value by key.
 * Returns objects value by index. 
 * 
 * Used for for with single {@link com.elasticpath.tags.domain.SelectableValue}.
 *
 * 
 */
public interface SelectableValueResolver {
	
	/**
	 * Get the value, that will be used in tag framework expression.
	 * 
	 * @param selectionIndex , that passed from UI combobox.
	 *            
	 * @return a value, that will be used in tag framework expression
	 */
	Object getValueBySelectionIndex(int selectionIndex);

	/**
	 * Get the string, that represent object on UI.
	 * 
	 * @param value
	 *            the key, that will be used in tag framework expression
	 * @return string, that represent object on UI.
	 */
	String getNameByValue(Object value);	
	
	
}
