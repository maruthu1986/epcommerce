/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.ui.framework.AbstractEpCheckLabelProvider;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;

/**
 * Wrapper for getting the text, color and image of the table items.
 */
class EpTableLabelProviderWrapper extends ColumnLabelProvider {

	private static final String CHECKED_RADIO_KEY = "checked_radio"; //$NON-NLS-1$

	private static final String UNCHECK_RADIO_KEY = "unchecked_radio"; //$NON-NLS-1$

	private static final String CHECKED_CHECKBOX_KEY = "checked_checkbox"; //$NON-NLS-1$

	private static final String UNCHECK_CHECKBOX_KEY = "unchecked_checkbox"; //$NON-NLS-1$

	private static final String CHECKED_RADIO_KEY_DISABLED = "checked_radio_disabled"; //$NON-NLS-1$

	private static final String UNCHECK_RADIO_KEY_DISABLED = "unchecked_radio_disabled"; //$NON-NLS-1$

	private static final String CHECKED_CHECKBOX_KEY_DISABLED = "checked_checkbox_disabled"; //$NON-NLS-1$

	private static final String UNCHECK_CHECKBOX_KEY_DISABLED = "unchecked_checkbox_disabled"; //$NON-NLS-1$

	private final ColumnLabelProvider labelProvider;

	private final int type;

	private final Shell shell;

	private final boolean enabled;

	/**
	 * Constructs the wrapper.
	 * 
	 * @param shell the parent shell
	 * @param type the type of the column - radio or checkbox style
	 * @param editMode if in read-only mode
	 * @param labelProvider the real label provider
	 */
	EpTableLabelProviderWrapper(final Shell shell, final int type, final boolean editMode, final ColumnLabelProvider labelProvider) {
		this.type = type;
		this.labelProvider = labelProvider;
		this.shell = shell;
		this.enabled = editMode;
		this.initImages();
	}

	/**
	 *
	 */
	private void initImages() {
		if (this.type == IEpTableColumn.TYPE_RADIO && JFaceResources.getImageRegistry().getDescriptor(CHECKED_RADIO_KEY) == null) {
			if (this.enabled) {
				JFaceResources.getImageRegistry().put(UNCHECK_RADIO_KEY, this.makeShot(false));
				JFaceResources.getImageRegistry().put(CHECKED_RADIO_KEY, this.makeShot(true));
			} else {
				JFaceResources.getImageRegistry().put(UNCHECK_RADIO_KEY_DISABLED, this.makeShot(false));
				JFaceResources.getImageRegistry().put(CHECKED_RADIO_KEY_DISABLED, this.makeShot(true));
			}
		}
		if (this.type == IEpTableColumn.TYPE_CHECKBOX && JFaceResources.getImageRegistry().getDescriptor(CHECKED_CHECKBOX_KEY) == null) {
			if (this.enabled) {
				JFaceResources.getImageRegistry().put(UNCHECK_CHECKBOX_KEY, this.makeShot(false));
				JFaceResources.getImageRegistry().put(CHECKED_CHECKBOX_KEY, this.makeShot(true));
			} else {
				JFaceResources.getImageRegistry().put(UNCHECK_CHECKBOX_KEY_DISABLED, this.makeShot(false));
				JFaceResources.getImageRegistry().put(CHECKED_CHECKBOX_KEY_DISABLED, this.makeShot(true));
			}
		}
	}

	@Override
	public Color getBackground(final Object element) {
		return this.labelProvider.getBackground(element);
	}

	@Override
	public Font getFont(final Object element) {
		return this.labelProvider.getFont(element);
	}

	@Override
	public Color getForeground(final Object element) {
		return this.labelProvider.getForeground(element);
	}

	private Image makeShot(final boolean isSelected) {
		final Shell shellChild = new Shell(this.shell, SWT.NO_TRIM);
		final Button button = new Button(shellChild, this.type);
		button.setBackground(CmClientResources.getBackgroundColor());
		button.setSelection(isSelected);
		button.setEnabled(this.enabled);

		final Point bsize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		button.setSize(bsize);
		button.setLocation(0, 0);

		shellChild.setSize(bsize);
		shellChild.open();

		final Image image = new Image(shellChild.getDisplay(), bsize.x, bsize.y);


		//TODO-RAP-M1 No copyArea. Removed graphic objed
		shellChild.close();

		return image;
	}

	@Override
	public Image getImage(final Object element) {
		if (this.labelProvider instanceof AbstractEpCheckLabelProvider && this.type == IEpTableColumn.TYPE_RADIO) {
			final AbstractEpCheckLabelProvider checkLabelProvider = (AbstractEpCheckLabelProvider) this.labelProvider;
			return this.getCheckboxImage(element, checkLabelProvider);
		} else if (this.labelProvider instanceof AbstractEpCheckLabelProvider && this.type == IEpTableColumn.TYPE_CHECKBOX) {
			final AbstractEpCheckLabelProvider checkLabelProvider = (AbstractEpCheckLabelProvider) this.labelProvider;
			return this.getRadioButtonImage(element, checkLabelProvider);
		}
		return this.labelProvider.getImage(element);
	}

	/**
	 * @param element
	 * @param checkLabelProvider
	 * @return
	 */
	private Image getRadioButtonImage(final Object element, final AbstractEpCheckLabelProvider checkLabelProvider) {
		Image image = null;
		if (checkLabelProvider.isChecked(element)) {
			if (this.enabled) {
				image = JFaceResources.getImageRegistry().get(CHECKED_CHECKBOX_KEY);
			} else {
				image = JFaceResources.getImageRegistry().get(CHECKED_CHECKBOX_KEY_DISABLED);
			}
		} else {
			if (this.enabled) {
				image = JFaceResources.getImageRegistry().get(UNCHECK_CHECKBOX_KEY);
			} else {
				image = JFaceResources.getImageRegistry().get(UNCHECK_CHECKBOX_KEY_DISABLED);
			}
		}
		return image;
	}

	/**
	 * @param element
	 * @param checkLabelProvider
	 * @return
	 */
	private Image getCheckboxImage(final Object element, final AbstractEpCheckLabelProvider checkLabelProvider) {
		Image image = null;
		if (checkLabelProvider.isChecked(element)) {
			if (this.enabled) {
				image = JFaceResources.getImageRegistry().get(CHECKED_RADIO_KEY_DISABLED);
			} else {
				image = JFaceResources.getImageRegistry().get(CHECKED_RADIO_KEY);
			}
		} else {
			if (this.enabled) {
				image = JFaceResources.getImageRegistry().get(UNCHECK_RADIO_KEY_DISABLED);
			} else {
				image = JFaceResources.getImageRegistry().get(UNCHECK_RADIO_KEY);
			}
		}
		return image;
	}

	@Override
	public String getText(final Object element) {
		return this.labelProvider.getText(element);
	}

}
