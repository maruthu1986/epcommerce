/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.Optional;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * Composite Layout utility.
 */
public final class CompositeLayoutUtility {

	/**
	 * Private constructor.
	 */
	private CompositeLayoutUtility() {

	}

	/**
	 * Computes the absolute location of the composite based on its parents.
	 *
	 * @param composite the composite.
	 * @return A location point, or null if composite is null.
	 */
	public static Point getAbsoluteLocation(final Composite composite) {

		if (composite == null) {
			return null;
		}

		Point parentPoint = Optional.ofNullable(getAbsoluteLocation(composite.getParent())).orElse(new Point(0, 0));
		Point compositePoint = composite.getLocation();

		parentPoint.x += compositePoint.x;
		parentPoint.y += compositePoint.y;

		return parentPoint;
	}
}
