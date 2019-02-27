/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * EP layout composite implementation for the table wrap layout.
 */
public class TableWrapLayoutComposite extends AbstractEpLayoutComposite implements IEpLayoutComposite {

	private static final String NO_DESCRIPTION = "";
	private static final int GRID_COMPOSITE = 0;
	private static final int TABLE_COMPOSITE = 1;

	/**
	 * Constructs a new layout composite object.
	 * 
	 * @param parent the Eclipse parent composite
	 * @param numColumns columns count
	 * @param equalWidthColumns sets equal width columns
	 */
	public TableWrapLayoutComposite(final Composite parent, final int numColumns, final boolean equalWidthColumns) {
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
	public TableWrapLayoutComposite(final Composite composite, final int numColumns, final boolean equalWidthColumns, final IEpLayoutData data) {
		super(composite, numColumns, equalWidthColumns, data);
	}

	@Override
	protected Object adaptEpLayoutData(final IEpLayoutData data) {
		final TableWrapData result;
		if (data == null) {
			result = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		} else {
			final EpLayoutData epData = (EpLayoutData) data;
			final int horizontal = epData.getHorizontalAlignment();
			final int vertical = epData.getVerticalAlignment();
			final int hGridDataAlignment = this.translateAlignment(horizontal, true);
			final int vGridDataAlignment = this.translateAlignment(vertical, false);

			result = new TableWrapData(hGridDataAlignment, vGridDataAlignment, epData.getHorizontalSpan(), epData.getVerticalSpan());
			result.grabHorizontal = epData.isGrabExcessHorizontalSpace();
			result.grabVertical = epData.isGrabExcessVerticalSpace();
			result.colspan = epData.getHorizontalSpan();
			result.rowspan = epData.getVerticalSpan();
		}
		return result;
	}

	private int translateAlignment(final int alignmentConstant, final boolean isHorizontal) {
		int translatedAlignment = TableWrapData.FILL;
		switch (alignmentConstant) {
		case IEpLayoutData.FILL:
			translatedAlignment = TableWrapData.FILL;
			break;
		case IEpLayoutData.BEGINNING:
			if (isHorizontal) {
				translatedAlignment = TableWrapData.LEFT;
			} else {
				translatedAlignment = TableWrapData.TOP;
			}
			break;
		case IEpLayoutData.END:
			if (isHorizontal) {
				translatedAlignment = TableWrapData.RIGHT;
			} else {
				translatedAlignment = TableWrapData.BOTTOM;
			}
			break;
		case IEpLayoutData.CENTER:
			if (isHorizontal) {
				translatedAlignment = TableWrapData.CENTER;
			} else {
				translatedAlignment = TableWrapData.MIDDLE;
			}
			break;
		default: // nothing to do

		}
		return translatedAlignment;
	}

	@Override
	protected IEpLayoutComposite newCompositeInstance(final Composite composite, final int numColumns, final boolean equalWidthColumns) {
		return new TableWrapLayoutComposite(composite, numColumns, equalWidthColumns);
	}

	@Override
	protected Layout newLayoutInstance(final int numColumns, final boolean equalWidthColumns) {
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = numColumns;
		layout.makeColumnsEqualWidth = equalWidthColumns;
		return layout;
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
		// final Composite composite = this.newSwtComposite(this.getSwtComposite());
		final GridLayoutComposite gridLayoutComposite = new GridLayoutComposite(this.getSwtComposite(), numColumns, equalWidthColumns);
		// composite.setLayout(gridLayoutComposite.newLayoutInstance(numColumns, equalWidthColumns));
		gridLayoutComposite.setLayoutData(this.adaptEpLayoutData(data));

		return gridLayoutComposite;
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
		final IEpLayoutComposite epLayoutComposite = this.newCompositeInstance(this.getSwtComposite(), numColumns, equalWidthColumns);
		epLayoutComposite.setLayoutData(this.adaptEpLayoutData(data));
		return epLayoutComposite;
	}

	@Override
	protected IEpLayoutComposite newWrapperCompositeInstance(final Composite composite, final int numColumns, final boolean equalWidthColumns,
			final IEpLayoutData data) {
		return new TableWrapLayoutComposite(composite, numColumns, equalWidthColumns, data);
	}

	@Override
	public IEpLayoutComposite addTableWrapLayoutSection(final int numColumns, final String title,
		final int style, final IEpLayoutData data) {
		return addTableWrapLayoutSection(numColumns, title, NO_DESCRIPTION, style, data);
	}

	@Override
	public IEpLayoutComposite addTableWrapLayoutSection(final int numColumns, final String title,
		final String description, final int style, final IEpLayoutData data) {
		return createLayoutSection(numColumns, title, description, style, data, TABLE_COMPOSITE);
	}

	@Override
	public IEpLayoutComposite addGridLayoutSection(final int numColumns, final String title, final int style, final IEpLayoutData data) {
		return addGridLayoutSection(numColumns, title, NO_DESCRIPTION, style, data);
	}

	@Override
	public IEpLayoutComposite addGridLayoutSection(final int numColumns, final String title, 
			final String description, final int style, final IEpLayoutData data) {
		return createLayoutSection(numColumns, title, description, style, data, GRID_COMPOSITE);
	}


	private IEpLayoutComposite createLayoutSection(final int numColumns, final String title,
		final String description, final int style, final IEpLayoutData data, final int compositeType) {

		boolean needsDescription = !description.equals(NO_DESCRIPTION);
		int styleToSet = style;
		if (needsDescription) {
			styleToSet = style & Section.DESCRIPTION;
		}

		final Section section = getFormToolkit().createSection(getSwtComposite(), styleToSet);
		section.setText(title);
		section.setLayoutData(adaptEpLayoutData(data));

		if (needsDescription) {
			section.setDescription(description);
		}

		final IEpLayoutComposite layoutComposite;

		if (compositeType == GRID_COMPOSITE) {
			layoutComposite = new GridLayoutComposite(section, numColumns, false);
		} else {
			layoutComposite = newCompositeInstance(section, numColumns, false);
		}

		section.setClient(layoutComposite.getSwtComposite());

		return layoutComposite;
	}

}
