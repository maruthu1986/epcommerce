/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * Abstract superclass for form editor inputs. Extend this to provide a specific input for a form editor.
 * 
 * Note that data models should not be referenced by editor inputs because many editor
 * inputs may be kept by the platform.
 */
public abstract class AbstractFormEditorInput implements IEditorInput {

	private final String name;
	private String toolTip;

	/**
	 * Constructor.
	 * 
	 * @param name the name of the editor input for display
	 */
	public AbstractFormEditorInput(final String name) {
		this.name = name;
		this.toolTip = name; //Set it to the same value with name. 
	}

	/**
	 * Returns whether the editor input exists. This method is primarily used to determine if an editor input should appear in the "File Most
	 * Recently Used" menu. An editor input will appear in the list until the return value of exists becomes false or it drops off the bottom of the
	 * list.
	 * 
	 * @return true
	 */
	public boolean exists() {
		return true;
	}

	/**
	 * Returns the default image descriptor. The default implementation returns null.
	 * Clients can return a specific image descriptor if necessary.
	 * 
	 * @return an <code>ImageDescriptor</code>.
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/**
	 * Get the name of the form.
	 * 
	 * @return the name of the form.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This class is not persistable.
	 * 
	 * @return null
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * Get the text to appear in the editor tooltip.
	 * 
	 * @return the text to appear in the editor tooltip
	 */
	public String getToolTipText() {
		return toolTip;
	}

	/**
	 * Set the text to appear in the editor tooltip.
	 * 
	 * @param toolTip the text to appear in the editor tooltip
	 */
	public void setToolTipText(final String toolTip) {
		this.toolTip = toolTip;
	}

	/**
	 * This abstract form editor input cannot be adapted.
	 * 
	 * @param adapter adapter
	 * @return null
	 */
	public Object getAdapter(final Class adapter) {
		return null;
	}

}