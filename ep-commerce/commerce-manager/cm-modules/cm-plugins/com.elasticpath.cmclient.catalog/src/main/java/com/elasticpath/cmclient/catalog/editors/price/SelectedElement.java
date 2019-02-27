/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors.price;

/**
 * A model of selected element.
 */
public class SelectedElement {
	private Object element;

	/**
	 * Gets the selected element.
	 * 
	 * @return selection.
	 */
	public Object getElement() {
		return element;
	}

	/**
	 * Sets the selected element.
	 * 
	 * @param element selection.
	 */
	public void setElement(final Object element) {
		this.element = element;
	}
}
