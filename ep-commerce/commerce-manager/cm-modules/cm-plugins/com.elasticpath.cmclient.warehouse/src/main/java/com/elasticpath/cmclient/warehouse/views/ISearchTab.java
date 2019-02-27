/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.events.SelectionListener;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;

/**
 * //TODO: Absolute copy of fulfillment ISearchTab. Need to be abstracted out. Interface for the search tabs.
 */
public interface ISearchTab extends ITab {

	/**
	 * Called when search button is pressed.
	 */
	void search();

	/**
	 * Called when the form should be cleared. Clear button is pressed.
	 */
	void clear();

	/**
	 * @return boolean
	 */
	boolean validateSearchTermEntered();

	/**
	 * The controls of the search tab should be bound here.
	 * 
	 * @param bindingProvider binding provider
	 * @param context binding context used by the binding provider
	 */
	void bindControls(EpControlBindingProvider bindingProvider, DataBindingContext context);

	/**
	 * @param listener control modification listener
	 */
	void setControlModificationListener(ControlModificationListener listener);

	/**
	 * Returns the View ID that should display the results.
	 * 
	 * @return String
	 */
	String getResultViewId();

	/**
	 * @param listener selection listener
	 */
	void setSelectionListener(SelectionListener listener);

}
