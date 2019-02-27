/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ui.framework.impl.GridLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.impl.TableWrapLayoutComposite;

/**
 * A factory class for creating new grid or table wrap layout composites.
 */
public final class CompositeFactory {

	private CompositeFactory() {
		super();
	}

	/**
	 * Creates new EP layout composite with a GridLayout.
	 * 
	 * @param parent the Eclipse parent composite
	 * @param numColumns columns count
	 * @param equalWidthColumns true if the columns should be with equal width
	 * @return EP layout composite
	 */
	public static IEpLayoutComposite createGridLayoutComposite(final Composite parent, final int numColumns, final boolean equalWidthColumns) {
		return new GridLayoutComposite(parent, numColumns, equalWidthColumns);

	}

	/**
	 * Creates new pane with table wrap layout. Table wrap layout should be used for forms creation.
	 * 
	 * @param parent the Eclipse composite
	 * @param numColumns sets the number of columns of a grid
	 * @param equalWidthColumns specifies whether the columns should be with an equal width
	 * @return EP layout composite
	 */
	public static IEpLayoutComposite createTableWrapLayoutComposite(final Composite parent, final int numColumns, final boolean equalWidthColumns) {
		return new TableWrapLayoutComposite(parent, numColumns, equalWidthColumns);
	}
}
