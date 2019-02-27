/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.customers.dialogs;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.customers.AdminCustomersImageRegistry;
import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.admin.customers.AdminCustomersPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.service.attribute.AttributeService;

/**
 * Dialog for adding new address to customer addresses.
 */
public class AttributeDialog extends AbstractEpDialog {

	private final String title;

	private final Image image;

	private final Attribute attribute;

	private final DataBindingContext context;

	private final AttributeService attributeService;

	private Text attributeKeyField;

	private Text attributeNameField;

	private CCombo typeCombo;

	private Button requiredCheckBox;

	/**
	 * Constructs the dialog with fields populated.
	 *
	 * @param parentShell the parent Shell
	 * @param attribute the attribute to edit
	 * @param title the title of the dialog
	 * @param image the image of the dialog
	 */
	public AttributeDialog(final Shell parentShell, final Attribute attribute, final String title, final Image image) {
		super(parentShell, 2, false);
		this.context = new DataBindingContext();
		this.attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);
		this.attribute = attribute;
		this.title = title;
		this.image = image;
	}

	/**
	 * Return the attribute associated with this dialog.
	 *
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * Convenience method to open a create dialog.
	 *
	 * @param parentShell the parent Shell
	 * @param attribute the attribute to edit
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openCreateDialog(final Shell parentShell, final Attribute attribute) {
		final AttributeDialog dialog = new AttributeDialog(parentShell, attribute, AdminCustomersMessages.get().CreateAttribute,
				AdminCustomersImageRegistry.getImage(AdminCustomersImageRegistry.IMAGE_ATTRIBUTE_CREATE));
		return dialog.open() == 0;
	}

	/**
	 * Convenience method to open an edit dialog.
	 * 
	 * @param parentShell the parent Shell
	 * @param attribute the attribute to edit
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openEditDialog(final Shell parentShell, final Attribute attribute) {
		final AttributeDialog dialog = new AttributeDialog(parentShell, attribute, AdminCustomersMessages.get().EditAttribute,
				AdminCustomersImageRegistry.getImage(AdminCustomersImageRegistry.IMAGE_ATTRIBUTE_EDIT));
		return dialog.open() == 0;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		dialogComposite.addLabelBoldRequired(AdminCustomersMessages.get().AttributeKey, EpState.EDITABLE, labelData);
		attributeKeyField = dialogComposite.addTextField(EpState.EDITABLE, fieldData);

		dialogComposite.addLabelBoldRequired(AdminCustomersMessages.get().AttributeName, EpState.EDITABLE, labelData);
		attributeNameField = dialogComposite.addTextField(EpState.EDITABLE, fieldData);

		dialogComposite.addLabelBoldRequired(AdminCustomersMessages.get().AttributeType, EpState.EDITABLE, labelData);
		typeCombo = dialogComposite.addComboBox(EpState.EDITABLE, fieldData);

		dialogComposite.addLabelBold(AdminCustomersMessages.get().Required, labelData);
		requiredCheckBox = dialogComposite.addCheckBoxButton("", EpState.EDITABLE, null); //$NON-NLS-1$
	}

	@Override
	protected String getPluginId() {
		return AdminCustomersPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return attribute;
	}

	@Override
	protected void populateControls() {
		typeCombo.setItems(getAttributeTypeStrings());
		typeCombo.select(getSelectedAttributeTypeIndex());
		if (isEditAttribute()) {
			attributeKeyField.setEnabled(false);
			attributeKeyField.setText(attribute.getKey());
			attributeNameField.setText(attribute.getName());
			typeCombo.setEnabled(false);
			requiredCheckBox.setEnabled(false);
			requiredCheckBox.setSelection(attribute.isRequired());
		}
	}

	@Override
	protected void bindControls() {
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		binder.bind(context, attributeKeyField, attribute, "key", //$NON-NLS-1$
				EpValidatorFactory.ATTRIBUTE_KEY, null, true);
		binder.bind(context, attributeNameField, attribute, "name", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, true);
		final ObservableUpdateValueStrategy attributeTypeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				AttributeType selectedAttributeType = AttributeType.getCustomerAttributeTypes()[typeCombo.getSelectionIndex()];
				attribute.setAttributeType(selectedAttributeType);
				return Status.OK_STATUS;
			}
		};
		binder.bind(context, typeCombo, null, null, attributeTypeUpdateStrategy, true);
		binder.bind(context, requiredCheckBox, attribute, "required"); //$NON-NLS-1$
		EpDialogSupport.create(this, context);
	}

	private boolean isKeyExist() {
		boolean keyExists = attributeService.keyExists(attribute);
		if (keyExists) {
			setErrorMessage(
				NLS.bind(AdminCustomersMessages.get().ProfileAttributeKeyExists,
				attribute.getKey()));
		}
		return keyExists;
	}

	private boolean isNameExist() {
		boolean nameExists = attributeService.nameExistsInAttributeUsage(attribute);
		if (nameExists) {
			setErrorMessage(
				NLS.bind(AdminCustomersMessages.get().ProfileAttributeNameExists,
				attribute.getName()));
		}
		return nameExists;
	}

	@Override
	protected void okPressed() {
		if (!isKeyExist() && !isNameExist()) {
			super.okPressed();
		}
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected String getWindowTitle() {
		return title;
	}

	@Override
	protected Image getWindowImage() {
		return image;
	}

	@Override
	protected String getInitialMessage() {
		return ""; //$NON-NLS-1$
	}

	private boolean isEditAttribute() {
		return attribute.isPersisted();
	}

	private String[] getAttributeTypeStrings() {
		AttributeType[] attributeTypesArray = AttributeType.getCustomerAttributeTypes();
		String[] attributeTypeStrings = new String[attributeTypesArray.length];
		for (int i = 0; i < attributeTypesArray.length; i++) {
			attributeTypeStrings[i] = CoreMessages.get().getMessage(attributeTypesArray[i].getNameMessageKey());
		}
		return attributeTypeStrings;
	}

	private int getSelectedAttributeTypeIndex() {
		List<AttributeType> attributeTypes = Arrays.asList(AttributeType.getCustomerAttributeTypes());
		return attributeTypes.indexOf(attribute.getAttributeType());
	}
}
