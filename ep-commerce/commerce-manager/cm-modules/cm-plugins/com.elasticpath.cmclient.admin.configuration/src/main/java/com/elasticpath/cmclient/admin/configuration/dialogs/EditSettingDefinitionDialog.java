/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.admin.configuration.dialogs;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationMessages;
import com.elasticpath.cmclient.admin.configuration.AdminConfigurationPlugin;
import com.elasticpath.cmclient.admin.configuration.models.SettingsModel;
import com.elasticpath.cmclient.admin.configuration.views.SettingDefinitionDisplayComposite;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.settings.domain.SettingDefinition;

/**
 * Create a dialog to edit/create a setting value.
 */
public class EditSettingDefinitionDialog extends AbstractEpDialog {

	//Global variables
	/** The logger. */
	protected static final Logger LOG = Logger.getLogger(EditSettingDefinitionDialog.class);
	
	private final SettingDefinition settingDefinition;
	
	private final DataBindingContext bindingContext;

	private final SettingsModel model;

	private SettingDefinitionDisplayComposite displayComposite;
	
	/**
	 * Constructor for the SettingValueDialog for either editing or creating settingDefinitions.
	 * @param parentShell of the Shell
	 * @param def the definition 
	 * @param model the model to use in operating on setting definitions
	 */
	public EditSettingDefinitionDialog(final Shell parentShell, final SettingDefinition def,  final SettingsModel model) {
		super(parentShell, 2, false);
		this.settingDefinition = def;
		this.model = model;
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
		displayComposite.bindDefaultValueString(bindingContext, this);
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
			//no controls to populate as we are using a composite to populate them
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, CoreMessages.get().AbstractEpDialog_ButtonSave, null);
		createEpCancelButton(parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		displayComposite = new SettingDefinitionDisplayComposite(dialogComposite.getSwtComposite(), SWT.NONE, true);
		displayComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		displayComposite.setSettingDefinition(settingDefinition);
	}
	
	@Override
	protected void okPressed() {
		
		settingDefinition.setDefaultValue(displayComposite.getDefaultValueStringText());
		model.updateSettingDefinition(settingDefinition);
		super.okPressed();
	}
	
	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return AdminConfigurationMessages.get().editDialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		return AdminConfigurationMessages.get().editDialogTitle;
	}


	@Override
	protected Image getWindowImage() {
		return null;
	}
}
