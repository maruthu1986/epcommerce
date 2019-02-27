/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.dialog;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.helpers.extenders.DialogExtension;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;

/**
 * An abstract class for all the input dialogs.
 */
public abstract class AbstractEpDialog extends TitleAreaDialog {

	private static final int VERTICAL_MARGIN = 5;

	private static final int HORIZONTAL_MARGIN = 10;

	private final int numColumns;

	private final boolean equalWidthColumns;

	/**
	 * Indicates whether this dialog is complete.
	 */
	private boolean complete = true;
	private List<DialogExtension> dialogExtensions;

	/**
	 * Constructs the dialog.
	 *
	 * @param parentShell       the parent Eclipse shell
	 * @param numColumns        columns count for the GridLayout
	 * @param equalWidthColumns should the columns be with equal width
	 */
	public AbstractEpDialog(final Shell parentShell, final int numColumns, final boolean equalWidthColumns) {
		super(parentShell);
		this.numColumns = numColumns;
		this.equalWidthColumns = equalWidthColumns;
	}

	/**
	 * Returns the value of an internal state variable set by <code>setComplete</code>.
	 *
	 * @return <code>true</code> if dialog is complete, <code>false</code> otherwise
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Sets whether this dialog is complete.
	 *
	 * @param complete <code>true</code> if this page is complete, and and <code>false</code> otherwise
	 * @see #isComplete()
	 */
	public void setComplete(final boolean complete) {
		this.complete = complete;
		updateButtons();
	}

	/**
	 * Adjusts the enable state of buttons to reflect the state of the dialog. This method is called by the dialog itself when the dialog data
	 * changes and may be called by the dialog at other times to force a button state update.
	 */
	public void updateButtons() {
		getOkButton().setEnabled(isComplete());
	}

	/**
	 * Convenience method to return ok button.
	 *
	 * @return the ok button
	 */
	public Button getOkButton() {
		return getButton(IDialogConstants.OK_ID);
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Control control = super.createContents(parent);

		this.populateControls();
		populateExtensionControls();
		this.bindControls();
		bindExtensionControls();
		this.dialogResizeOverride();
		return control;
	}

	private List<DialogExtension> findDialogExtensions() {
		if (this.dialogExtensions == null) {
			this.dialogExtensions = PluginHelper.findDialogExtensions(getClass().getSimpleName(), getPluginId());
		}
		return this.dialogExtensions;
	}

	private void bindExtensionControls() {
		for (DialogExtension dialogExtension : findDialogExtensions()) {
			dialogExtension.bindControls(this);
		}
	}

	private void populateExtensionControls() {
		for (DialogExtension dialogExtension : findDialogExtensions()) {
			dialogExtension.populateControls(this);
		}
	}

	private void createExtensionEpDialogContent(final IEpLayoutComposite dialogComposite) {
		for (DialogExtension dialogExtension : findDialogExtensions()) {
			dialogExtension.createEpDialogContent(dialogComposite, this);
		}
	}

	/**
	 * gets the associated plugin id.
	 * @return the pluginId.
	 */
	protected abstract String getPluginId();

	/**
	 * gets the (optional) model for the dialog, for extensibility.
	 * @return the model.
	 */
	public abstract Object getModel();

	/**
	 * There is a number of re-sizing calls made during the life cycle of this dialog.
	 *
	 * When we started using RAP many of our dialogs ended up with the wrong size after the final layout was issued.
	 *
	 * This method calls layout() for a final time after the createContents are called.
	 * */
	protected void dialogResizeOverride() {
		this.getShell().layout();
	}

	/**
	 * Populates the controls for the contents.
	 */
	protected abstract void populateControls();

	/**
	 * Binds the controls for the contents.
	 */
	protected abstract void bindControls();

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.SAVE, parent);
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(this.getWindowTitle());
		EPTestUtilFactory.getInstance().getTestIdUtil().setId(newShell, this.getWindowTitle());
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);
		final IEpLayoutComposite dialogComposite = CompositeFactory.createGridLayoutComposite(area, this.numColumns, this.equalWidthColumns);
		dialogComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// alter the layout data margins for the dialog
		final GridLayout gridLayout = (GridLayout) dialogComposite.getSwtComposite().getLayout();
		gridLayout.marginLeft = HORIZONTAL_MARGIN;
		gridLayout.marginRight = HORIZONTAL_MARGIN;
		gridLayout.marginTop = VERTICAL_MARGIN;
		gridLayout.marginBottom = VERTICAL_MARGIN;

		this.createEpDialogContent(dialogComposite);
		this.createExtensionEpDialogContent(dialogComposite);

		// initializes the dialog window texts and images
		if (this.getTitle() != null) {
			this.setTitle(this.getTitle());
		}

		this.setTitleImage(this.getTitleImage());

		if (this.getInitialMessage() != null) {
			this.setMessage(this.getInitialMessage());
		}

		return area;
	}



	@Override
	protected void setButtonLayoutData(final Button button) {
		super.setButtonLayoutData(button);
		((GridData) button.getLayoutData()).verticalAlignment = GridData.FILL;
	}

	/**
	 * Creates the controls that have to be displayed in the client area of the dialog.
	 *
	 * @param dialogComposite the EP layout composite to be used
	 */
	protected abstract void createEpDialogContent(IEpLayoutComposite dialogComposite);

	/**
	 * Gets the title of the dialog displayed in the title area.
	 *
	 * @return String
	 */
	protected abstract String getTitle();

	/**
	 * Gets the description of the dialog displayed in the title area.
	 *
	 * @return String
	 */
	protected abstract String getInitialMessage();

	/**
	 * Gets the title image displayed in the title area.
	 *
	 * @return Image
	 */
	protected Image getTitleImage() {
		return null;
	}

	/**
	 * Should return the dialog window title.
	 *
	 * @return String
	 */
	protected abstract String getWindowTitle();

	/**
	 * Should return the dialog window image.
	 *
	 * @return Image
	 */
	protected abstract Image getWindowImage();

	/**
	 * Convenience method to create EP specific buttons for buttons bar. Buttons creation is based on the <code>ButtonsBarType</code> passed in.
	 *
	 * @param buttonsBarType the buttons bar type
	 * @param parent         the parent composite
	 */
	protected void createEpButtonsForButtonsBar(final ButtonsBarType buttonsBarType, final Composite parent) {
		createEpOkButton(parent, buttonsBarType.getButtonLabel(), buttonsBarType.getImage());
		if (buttonsBarType.isShowCancelButton()) {
			createEpCancelButton(parent);
		}
	}

	@Override
	protected Button createButton(final Composite parent, final int buttonId, final String label, final boolean defaultButton) {
		Button button = super.createButton(parent, buttonId, label, defaultButton);
		EPTestUtilFactory.getInstance().getTestIdUtil().setId(button, label);
		return button;
	}

	/**
	 * Convenience method to create EP specific ok button with a custom button label and image.
	 *
	 * @param parent      the parent composite
	 * @param buttonLabel the label for the button
	 * @param image       the image for the button
	 * @return the button
	 */
	protected Button createEpOkButton(final Composite parent, final String buttonLabel, final Image image) {
		final Button okButton = createButton(parent, IDialogConstants.OK_ID, buttonLabel, true);
		okButton.setImage(image);
		return okButton;
	}

	/**
	 * Convenience method to create EP specific cancel button.
	 *
	 * @param parent the parent composite
	 * @return the button
	 */
	protected Button createEpCancelButton(final Composite parent) {
		return createButton(parent, IDialogConstants.CANCEL_ID, CoreMessages.get().AbstractEpDialog_ButtonCancel, false);
	}

	/**
	 * Show the warning message dialog, the content is from the list of <code>IStatus</code>.
	 *
	 * @param validationStatus the status list
	 */
	public void showValidationDialog(final List<IStatus> validationStatus) {
		final StringBuilder errorListString = new StringBuilder();
		errorListString.append('\n');
		for (final IStatus status : validationStatus) {
			errorListString.append('\n');
			errorListString.append(status.getMessage());
		}
		final String message =
			NLS.bind(CoreMessages.get().AbstractCmClientFormEditor_Error_save,
			errorListString.toString());

		MessageDialog.openWarning(getShell(), CoreMessages.get().AbstractCmClientFormEditor_ErrorTitle_save, message);
	}

	/**
	 * Buttons bar type enumeration.
	 */
	public enum ButtonsBarType {
		/**
		 * Specifies that the buttons bar should contain a save type ok button along with a cancel button.
		 */
		SAVE(CoreMessages.get().AbstractEpDialog_ButtonSave, null, true),
		/**
		 * Specifies that the buttons bar should contain an ok type ok button along with a cancel button.
		 */
		OK(CoreMessages.get().AbstractEpDialog_ButtonOK, null, true);

		private final String buttonLabel;

		private final Image image;

		private final boolean showCancelButton;

		/**
		 * Creates a buttons bar type. The type is used to define how the ok button on the buttons bar should be displayed and whether a cancel
		 * button should also be displayed.
		 *
		 * @param buttonLabel      the button label
		 * @param image            the image for the ok button
		 * @param showCancelButton <code>true</code> if the cancel button is to be displayed and <code>false</code> otherwise
		 */
		ButtonsBarType(final String buttonLabel, final Image image, final boolean showCancelButton) {
			this.buttonLabel = buttonLabel;
			this.image = image;
			this.showCancelButton = showCancelButton;
		}

		/**
		 * Returns the button label.
		 *
		 * @return the button label.
		 */
		public String getButtonLabel() {
			return buttonLabel;
		}

		/**
		 * Returns the image.
		 *
		 * @return the image.
		 */
		public Image getImage() {
			return image;
		}

		/**
		 * Returns whether the cancel button should be displayed in the button bar.
		 *
		 * @return <code>true</code> if the cancel button is to be displayed and <code>false</code> otherwise
		 */
		public boolean isShowCancelButton() {
			return showCancelButton;
		}
	}
}
