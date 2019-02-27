/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * The generic dialog box for editing values.
 * @param <E> class for which the dialog applies
 */
public abstract class AbstractValueDialog<E> extends AbstractEpDialog implements IValueRetriever {

	/**
	 * the value.
	 */
	private E value;
	
	private final boolean editMode;
	
	private String label;
	
	private boolean isLabelBold;
	
	private final boolean valueRequired;
	
	/**
	 * The constructor of the boolean dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the value to modify
	 * 		  upon which the dialog operates.
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 */
	public AbstractValueDialog(final Shell parentShell, 
			final E value, final boolean editMode, final boolean valueRequired) {
		super(parentShell, 2, false);
		this.value = value;
		this.editMode = editMode;
		this.valueRequired = valueRequired;
	}
	
	/**
	 * The constructor of the boolean dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 * @param value the value to modify
	 * 		  upon which the dialog operates.
	 * @param editMode true for edit mode, false for add short text values.
	 * @param valueRequired true if value is required, false is value is not required
	 * @param label the label for this value
	 * @param isLabelBold true if label needs to be bold, false for normal font labels
	 */
	public AbstractValueDialog(final Shell parentShell, 
			final E value, final boolean editMode, final boolean valueRequired,
			final String label, final boolean isLabelBold) {
		super(parentShell, 2, false);
		this.value = value;
		this.editMode = editMode;
		this.label = label;
		this.isLabelBold = isLabelBold;
		this.valueRequired = valueRequired;
	}

	@Override
	public Object getModel() {
		return value;
	}

	@Override
	protected void createEpButtonsForButtonsBar(final ButtonsBarType buttonsBarType, final Composite parent) {
		createEpOkButton(parent, CoreMessages.get().AbstractEpDialog_ButtonOK, null);
		createEpCancelButton(parent);
	}
	
	
	@Override
	protected String getInitialMessage() {
		return null;
	}
	
	@Override
	protected String getTitle() {
		if (isEditMode()) {
			return getEditTitle();
		} 
		return getAddTitle();
	}
	
	/**
	 * Get dialog title for edit mode.
	 * 
	 * @return the dialog title text
	 */
	protected abstract String getEditTitle();
	
	/**
	 * Get dialog title for add mode.
	 * 
	 * @return the dialog title text
	 */
	protected abstract String getAddTitle();

	@Override
	protected String getWindowTitle() {
		if (isEditMode()) {
			return getEditWindowTitle();
		} 
		return getAddWindowTitle();
	}

	/**
	 * Get dialog window title for edit mode.
	 * 
	 * @return the dialog title text
	 */
	protected abstract String getEditWindowTitle();
	
	/**
	 * Get dialog window title for add mode.
	 * 
	 * @return the dialog title text
	 */
	protected abstract String getAddWindowTitle();

	/**
	 * Get the input value via adapter.
	 * @return the value input by the user.
	 */
	public E getValue() {
		return this.value;
	}
	
	/**
	 * Set the input value via adapter.
	 * @param value the value input by the user.
	 */
	protected void setValue(final E value) {
		this.value = value;
	}

	@Override
	protected Image getWindowImage() {
		// do nothing here
		return null;
	}

	@Override
	protected String getPluginId() {
		return CorePlugin.PLUGIN_ID;
	}

	@Override
	protected void bindControls() {
		// no binding is needed for the dialog because dialog does not bind to any object.
	}
	
	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		if (label != null) {
			if (isLabelBold) {
				dialogComposite.addLabelBold(label, dialogComposite.createLayoutData(IEpLayoutData.FILL,
						IEpLayoutData.CENTER));
			} else {
				dialogComposite.addLabel(label, dialogComposite.createLayoutData(IEpLayoutData.FILL,
						IEpLayoutData.CENTER));
			}
		}
	}

	/**
	 * get dialog model mode.
	 * @return true if dialog is in edit mode, false if in add mode.
	 */
	protected boolean isEditMode() {
		return editMode;
	}
	
	/**
	 * Gets flag that indicates if value in dialog is required.
	 * 
	 * @return true if required, false if not required
	 */
	protected boolean isValueRequired() {
		return valueRequired;
	}
	
}
