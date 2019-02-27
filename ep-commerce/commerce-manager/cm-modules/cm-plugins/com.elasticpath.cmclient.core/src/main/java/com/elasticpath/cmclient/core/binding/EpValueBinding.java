/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.binding;

import org.eclipse.core.databinding.Binding;
import org.eclipse.jface.fieldassist.ControlDecoration;

/**
 * Wrapper for retrieving the Binding to ControlDecoration pair.
 * Useful for controlling the validation decoration manually.
 * 
 */
public class EpValueBinding {
	private final Binding binding;

	private final ControlDecoration decoration;

	/**
	 * Construct a EpValueBinding with the binding object and validation decoration.
	 *
	 * @param binding the binding
	 * @param decoration the decoration
	 */
	public EpValueBinding(final Binding binding, final ControlDecoration decoration) {
		this.binding = binding;
		this.decoration = decoration;
	}

	/**
	 * Gets the binding.
	 *
	 * @return the binding
	 */
	public Binding getBinding() {
		return binding;
	}

	/**
	 * Gets the decoration.
	 *
	 * @return the decoration
	 */
	public ControlDecoration getDecoration() {
		return decoration;
	}
}
