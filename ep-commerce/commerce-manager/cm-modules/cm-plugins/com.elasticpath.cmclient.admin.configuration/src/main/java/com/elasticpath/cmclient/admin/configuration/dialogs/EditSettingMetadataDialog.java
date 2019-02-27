/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.admin.configuration.dialogs;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationMessages;
import com.elasticpath.cmclient.admin.configuration.AdminConfigurationPlugin;
import com.elasticpath.cmclient.admin.configuration.models.SettingsModel;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;

/**
 * Create a dialog to edit/create a setting metadata.
 */
public class EditSettingMetadataDialog extends AbstractEpDialog {

	/** The logger. */
	protected static final Logger LOG = Logger.getLogger(EditSettingMetadataDialog.class);
	
	private final SettingDefinition settingDefinition;
	
	private Text keyText;
	
	private Text valueText;
	
	private final DataBindingContext bindingContext;

	private final SettingsModel model;
	
	private final SettingMetadata metadata;
	
	private final boolean isEdit;
	
	
	/**
	 * Constructor for the EditSettingMetadataDialog for either editing or creating Setting Metadata.
	 * @param parentShell of the Shell
	 * @param metadata the metadata to edit. null if adding a new one
	 * @param def the definition 
	 * @param model the model to use in operating on setting definitions
	 */
	public EditSettingMetadataDialog(final Shell parentShell, final SettingMetadata metadata,
										final SettingDefinition def, final SettingsModel model) {
		super(parentShell, 2, false);
		this.settingDefinition = def;
		this.model = model;
		if (metadata == null) {
			isEdit = false;
			this.metadata = ServiceLocator.getService(ContextIdNames.SETTING_METADATA);
			this.metadata.setKey(StringUtils.EMPTY);
			this.metadata.setValue(StringUtils.EMPTY);
		} else {
			isEdit = true;
			this.metadata = metadata;
		}

		this.bindingContext = new DataBindingContext();
	}
	
	/**
	 * Return the settingDefinition associated with this dialog.
	 * @return the settingDefinition
	 */
	public SettingDefinition getSettingDefinition() {
		return settingDefinition;
	}

	@Override
	protected void bindControls() {

		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		//update strategy for the default value field control
		final ObservableUpdateValueStrategy keyUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String value = keyText.getText();
				if (LOG.isDebugEnabled()) {
					LOG.debug("Value: " + value); //$NON-NLS-1$
				}
				if (settingDefinition.getMetadata().containsKey(keyText)) {
					return new Status(
							IStatus.ERROR,
							CorePlugin.PLUGIN_ID,
							IStatus.ERROR,
							AdminConfigurationMessages.get().metadataKeyExists,
							null);
				}
				metadata.setKey(keyText.getText());
				return Status.OK_STATUS;
			}
		};
		
		final ObservableUpdateValueStrategy valueUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				metadata.setValue(valueText.getText());
				return Status.OK_STATUS;
			}
		};
		binder.bind(this.bindingContext, this.keyText, EpValidatorFactory.STRING_255_NOSPACES_REQUIRED, null, keyUpdateStrategy, true);
		binder.bind(this.bindingContext, this.valueText, EpValidatorFactory.STRING_255_NOSPACES_REQUIRED, null, valueUpdateStrategy, true);
		EpDialogSupport.create(this, this.bindingContext);
	}

	@Override
	protected String getPluginId() {
		return AdminConfigurationPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return model;
	}

	@Override
	protected void populateControls() {
		this.keyText.setFocus();
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, CoreMessages.get().AbstractEpDialog_ButtonSave, null);
		createEpCancelButton(parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldDataText = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		
		
		dialogComposite.addLabelBoldRequired(AdminConfigurationMessages.get().settingDefMetadataKey, EpState.READ_ONLY, labelData);
		EpState state = EpState.EDITABLE;
		if (isEdit) {
			state = EpState.READ_ONLY;
		}
		this.keyText = dialogComposite.addTextArea(state, fieldDataText);
		
		dialogComposite.addLabelBoldRequired(AdminConfigurationMessages.get().settingDefMetadataValue, EpState.READ_ONLY, labelData);
		this.valueText = dialogComposite.addTextArea(EpState.EDITABLE, fieldDataText);
		
		
		this.keyText.setText(metadata.getKey());
		this.valueText.setText(metadata.getValue());
	}
	

	@Override
	protected void okPressed() {
		if (this.keyText.getText() != null) {
			if (isEdit) {
				model.updateManagedDefinitionMetadata(settingDefinition, metadata);
			} else {
				model.addManagedDefinitionMetadata(settingDefinition, metadata);
			}
			super.okPressed();
		}
	}
	
	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		if (isEdit) {
			return AdminConfigurationMessages.get().editMetadataTitle;
		}
		return AdminConfigurationMessages.get().newDialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		if (isEdit) {
			return AdminConfigurationMessages.get().editMetadataTitle;
		}
		return AdminConfigurationMessages.get().newMetadataTitle;
	}


	@Override
	protected Image getWindowImage() {
		return null;
	}
}
