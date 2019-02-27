/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.core.databinding.DataBindingContext;

/**
 * Represents a view part that is used in multiple UI containers across Eclipse such as wizard pages and editors.
 */
public interface IEpViewPart {

	/**
	 * Creates the controls of the view part.
	 * 
	 * @param mainPane the pane to be used to add the controls to
	 * @param data the layout data
	 */
	void createControls(IEpLayoutComposite mainPane, IEpLayoutData data);
	
	/**
	 * Populates the controls with the default data.
	 */
	void populateControls();
	
	/**
	 * Binds the controls to the binding context.
	 * 
	 * @param bindingContext the context
	 */
	void bindControls(DataBindingContext bindingContext);
	
	/**
	 * Returns the model object.
	 * 
	 * @return the object
	 */
	Object getModel();
}
