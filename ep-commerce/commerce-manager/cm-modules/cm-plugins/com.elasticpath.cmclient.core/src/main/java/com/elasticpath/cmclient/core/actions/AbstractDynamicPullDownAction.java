/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.actions;

import org.eclipse.jface.action.Action;

/**
 * Abstract dynamic pull down action.
 *
 * @param <T> the object this pull down manages
 */
public abstract class AbstractDynamicPullDownAction<T> extends Action {


	/**
	 * Default constructor.
	 *
	 * @param style the style
	 * @param text  the text
	 */
	public AbstractDynamicPullDownAction(final String text, final int style) {
		super(text, style);
	}

	/**
	 * Returns the pull down object.
	 *
	 * @return the pull down object
	 */
	public abstract T getPullDownObject();

	@Override
	public int hashCode() {
		return getPullDownObject().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof AbstractDynamicPullDownAction) {
			return ((AbstractDynamicPullDownAction) obj).getPullDownObject().equals(this.getPullDownObject());
		}
		return super.equals(obj);
	}
}
