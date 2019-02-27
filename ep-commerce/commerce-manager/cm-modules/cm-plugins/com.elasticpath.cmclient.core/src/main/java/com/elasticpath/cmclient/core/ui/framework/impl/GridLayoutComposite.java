/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.Section;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * Implementation of <code>IEpLayoutComposite</code> with a <code>GridLayout</code> set.
 */
public class GridLayoutComposite extends AbstractEpLayoutComposite implements IEpLayoutComposite {

	/**
	 * Constructs new grid layout composite.
	 * 
	 * @param parent the parent Eclipse composite
	 * @param numColumns the number of columns in the grid
	 * @param equalWidthColumns true if the columns should be with equal width
	 */
	public GridLayoutComposite(final Composite parent, final int numColumns, final boolean equalWidthColumns) {
		super(parent, numColumns, equalWidthColumns);

	}

	/**
	 * Constructor for wrapping an existing composite.
	 * 
	 * @param composite the SWT composite
	 * @param numColumns columns count
	 * @param equalWidthColumns sets whether the columns should be equal width
	 * @param data the EP layout data
	 */
	public GridLayoutComposite(final Composite composite, final int numColumns, final boolean equalWidthColumns, final IEpLayoutData data) {
		super(composite, numColumns, equalWidthColumns, data);
	}

	@Override
	protected Object adaptEpLayoutData(final IEpLayoutData data) {
		final GridData result;
		if (data == null) {
			result = new GridData(GridData.FILL, GridData.FILL, false, false);
		} else {
			final EpLayoutData epData = (EpLayoutData) data;
			final int horizontal = epData.getHorizontalAlignment();
			final int vertical = epData.getVerticalAlignment();
			final int hGridDataAlignment = this.translateAlignment(horizontal);
			final int vGridDataAlignment = this.translateAlignment(vertical);

			result = new GridData(hGridDataAlignment, vGridDataAlignment, epData.isGrabExcessHorizontalSpace(), epData.isGrabExcessVerticalSpace(),
					epData.getHorizontalSpan(), epData.getVerticalSpan());
		}
		return result;
	}

	private int translateAlignment(final int alignmentConstant) {
		int translatedAlignment = GridData.FILL;
		switch (alignmentConstant) {
		case IEpLayoutData.FILL:
			translatedAlignment = GridData.FILL;
			break;
		case IEpLayoutData.BEGINNING:
			translatedAlignment = GridData.BEGINNING;
			break;
		case IEpLayoutData.END:
			translatedAlignment = GridData.END;
			break;
		case IEpLayoutData.CENTER:
			translatedAlignment = GridData.CENTER;
			break;
		default:
			// does nothing
		}
		return translatedAlignment;
	}

	/**
	 * Adds new composite with a grid layout.
	 * 
	 * @param numColumns the number of columns the grid should have
	 * @param equalWidthColumns true should the columns be equal width
	 * @param data EP layout data
	 * @return EP layout composite
	 */
	public IEpLayoutComposite addGridLayoutComposite(final int numColumns, final boolean equalWidthColumns, final IEpLayoutData data) {
		final IEpLayoutComposite epGridLayoutComposite = new GridLayoutComposite(this.getSwtComposite(), numColumns, equalWidthColumns);
		epGridLayoutComposite.setLayoutData(this.adaptEpLayoutData(data));
		return epGridLayoutComposite;
	}

	@Override
	public IEpLayoutComposite addGridLayoutSection(final int numColumns, final String title, final int style, final IEpLayoutData data) {
		final Section section = getFormToolkit().createSection(getSwtComposite(), style);
		section.setText(title);
		final IEpLayoutComposite layoutComposite = newCompositeInstance(section, numColumns, false);
		section.setLayoutData(adaptEpLayoutData(data));
		section.setClient(layoutComposite.getSwtComposite());
		return layoutComposite;
	}

	@Override
	public IEpLayoutComposite addGridLayoutSection(final int numColumns, final String title, 
			final String description, final int style, final IEpLayoutData data) {
		final Section section = getFormToolkit().createSection(getSwtComposite(), style | Section.DESCRIPTION);
		section.setText(title);
		section.setDescription(description);
		final IEpLayoutComposite layoutComposite = newCompositeInstance(section, numColumns, false);
		section.setLayoutData(adaptEpLayoutData(data));
		section.setClient(layoutComposite.getSwtComposite());
		return layoutComposite;
	}

	@Override
	public IEpLayoutComposite addTableWrapLayoutSection(final int numColumns, final String title, final int style, final IEpLayoutData data) {
		final Section section = getFormToolkit().createSection(getSwtComposite(), style);
		section.setText(title);
		final IEpLayoutComposite layoutComposite = new TableWrapLayoutComposite(section, numColumns, false);
		section.setLayoutData(adaptEpLayoutData(data));
		section.setClient(layoutComposite.getSwtComposite());
		return layoutComposite;
	}

	@Override
	public IEpLayoutComposite addTableWrapLayoutSection(final int numColumns, final String title, 
			final String description, final int style, final IEpLayoutData data) {
		final Section section = getFormToolkit().createSection(getSwtComposite(), style | Section.DESCRIPTION);
		section.setText(title);
		section.setDescription(description);
		final IEpLayoutComposite layoutComposite = new TableWrapLayoutComposite(section, numColumns, false);
		section.setLayoutData(adaptEpLayoutData(data));
		section.setClient(layoutComposite.getSwtComposite());
		return layoutComposite;
	}

	@Override
	protected Layout newLayoutInstance(final int numColumns, final boolean equalWidthColumns) {
		return new GridLayout(numColumns, equalWidthColumns);
	}

	@Override
	protected IEpLayoutComposite newCompositeInstance(final Composite composite, final int numColumns, final boolean equalWidthColumns) {
		return new GridLayoutComposite(composite, numColumns, equalWidthColumns);
	}

	@Override
	protected IEpLayoutComposite newWrapperCompositeInstance(final Composite composite, final int numColumns, final boolean equalWidthColumns,
			final IEpLayoutData data) {
		return new GridLayoutComposite(composite, numColumns, equalWidthColumns, data);
	}

	/**
	 * Adds new composite with a table wrap layout.
	 * 
	 * @param numColumns the number of columns the grid should have
	 * @param equalWidthColumns true should the columns be equal width
	 * @param data EP layout data
	 * @return EP layout composite
	 */
	public IEpLayoutComposite addTableWrapLayoutComposite(final int numColumns, final boolean equalWidthColumns, final IEpLayoutData data) {
		final TableWrapLayoutComposite tableWrapComposite = new TableWrapLayoutComposite(this.getSwtComposite(), numColumns, equalWidthColumns);
		tableWrapComposite.setLayoutData(this.adaptEpLayoutData(data));
		return tableWrapComposite;
	}

	/**
	 * Returns the Grid Data.
	 *
	 * @return the GridData
	 */
	public GridData getGridData() {
		return (GridData) this.getSwtComposite().getLayoutData();
	}

	/**
	 * Returns the Grid Layout.
	 *
	 * @return the GridLayout
	 */
	public GridLayout getGridLayout() {
		return (GridLayout) this.getSwtComposite().getLayout();
	}

}
