/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.admin.configuration.dialogs;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationMessages;
import com.elasticpath.cmclient.admin.configuration.AdminConfigurationPlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Create a dialog to edit/create a setting value.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class EditSettingValueDialog extends AbstractEpDialog {

	//Global variables
	/** The logger. */
	protected static final Logger LOG = Logger.getLogger(EditSettingValueDialog.class);
	
	private final SettingValue settingValue;
	
	private int numberContextMatches;
	
	private Text contextField;
	
	private Text valueField;
	
	private final DataBindingContext bindingContext;
	
	private final boolean isEditDialog;
	
	private final Set<SettingValue> values;
	
	private Shell shell;
	
	private final boolean isSingleOverride;

	private final FormToolkit formToolkit;
	
	private String valueType;
	
	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		this.shell = newShell;
	}
	
	/**
	 * Convenience method for creating an SettingValue dialog to edit/create settingValues.
	 * @param parentShell the parent shell
	 * @param value who's settingValue you are editing
	 * @param editDialog boolean depending on whether it is a edit dialog or a new dialog
	 * @param values all settingValues for the specified settingDefinition
	 * @param isSingleOverride boolean depending on whether the setting definition may only have one override
	 * @param settingDefinition is used to manage the type validation
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openEditSettingValueDialog(final Shell parentShell, final SettingValue value,  
			final boolean editDialog, final Set<SettingValue> values, final boolean isSingleOverride,
			final SettingDefinition settingDefinition) {
		
		final EditSettingValueDialog dialog = new EditSettingValueDialog(parentShell, value, editDialog, values, isSingleOverride);
		dialog.setValueType(settingDefinition.getValueType());
		return (dialog.open() == 0);
	}
	
	/**
	 * Constructor for the SettingValueDialog for either editing or creating settingValues.
	 * @param parentShell of the Shell
	 * @param value to which the data will belong
	 * @param editDialog boolean depending on whether it is a edit dialog or a new dialog
	 * @param values all settingValues for the specified settingDefinition
	 * @param isSingleOverride boolean depending on whether the setting definition may only have one override
	 */
	public EditSettingValueDialog(final Shell parentShell, final SettingValue value,  
			final boolean editDialog, final Set<SettingValue> values, final boolean isSingleOverride) {
		
		super(parentShell, 2, false);
		this.settingValue = value;
		this.isEditDialog = editDialog;
		this.bindingContext = new DataBindingContext();
		this.values = values;
		this.isSingleOverride = isSingleOverride;
		formToolkit = EpControlFactory.getInstance().createFormToolkit();
	}
	
	/**
	 * Return the settingDefinition associated with this dialog.
	 * @return the settingDefinition
	 */
	public SettingValue getSettingValue() {
		return settingValue;
	}

	/**
	 * set the ValueType for validation purposes.
	 * @param valueType the editor type
	 */
	public void setValueType(final String valueType) {
		this.valueType = valueType;
	}
	
	@Override
	protected void bindControls() {
		bindValueStrings(bindingContext);
	}

	/**
	 * bind value Strings.
	 * This method also sets up validators for the appropriate value type.
	 * @param bindingContext the data binding context.
	 */
	private void bindValueStrings(final DataBindingContext bindingContext) {
		
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		//If the setting value is not a single override then the context field needs to be displayed
		//and validated
		if (!isSingleOverride) {
			
			//update strategy for the context field control
			final ObservableUpdateValueStrategy contextUpdateStrategy = new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					String context = contextField.getText();
					if (LOG.isDebugEnabled()) {
						LOG.debug("Context: " + context); //$NON-NLS-1$
					}
					getSettingValue().setContext(context);
					return Status.OK_STATUS;
				}
			};
		
			binder.bind(this.bindingContext, this.contextField, EpValidatorFactory.STRING_255_NOSPACES_REQUIRED, 
					null, contextUpdateStrategy, true);

		}

		//update strategy for the value field control
		final ObservableUpdateValueStrategy valueUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				return Status.OK_STATUS;
			}
		};

		// BigDecimal, boolean, Collection, CSV, Integer, Map, String, url, xml
		if (valueType == null) {
			binder.bind(bindingContext, valueField, EpValidatorFactory.MAX_LENGTH_65535, null, 
					valueUpdateStrategy, true);
			
		} else if ("BigDecimal".equalsIgnoreCase(valueType)) { //$NON-NLS-1$
			binder.bind(bindingContext, valueField, EpValidatorFactory.BIG_DECIMAL_REQUIRED, null, 
					valueUpdateStrategy, true);
			
		} else if ("boolean".equalsIgnoreCase(valueType)) { //$NON-NLS-1$
			binder.bind(bindingContext, valueField, EpValidatorFactory.BOOLEAN_REQUIRED, null, 
					valueUpdateStrategy, true);
			
		} else if ("Integer".equalsIgnoreCase(valueType)) { //$NON-NLS-1$
			binder.bind(bindingContext, valueField, EpValidatorFactory.INTEGER, null, 
					valueUpdateStrategy, true);
			
		} else if ("xml".equalsIgnoreCase(valueType)) { //$NON-NLS-1$
			binder.bind(bindingContext, valueField, EpValidatorFactory.STRING_65535X5_REQUIRED, null, 
					valueUpdateStrategy, true);
			
		} else {
			binder.bind(bindingContext, valueField, EpValidatorFactory.STRING_65535_REQUIRED, null, 
					valueUpdateStrategy, true);
		}
		
		EpDialogSupport.create(this, this.bindingContext);

	}


	@Override
	protected String getPluginId() {
		return AdminConfigurationPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return settingValue;
	}

	@Override
	protected void populateControls() {
		
		//If the dialog box is a "edit..." dialog and a single setting override then the 
		//value should be the focus initially
		if (isEditDialog || isSingleOverride) {
			this.valueField.setFocus();
		} else {
			this.contextField.setFocus();
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		
		createEpOkButton(parent, "Save", null); //$NON-NLS-1$
		createEpCancelButton(parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData labelDataValue = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false, 1, 4);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		final IEpLayoutData fieldDataText = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
				
		createContextFields(dialogComposite, labelData, fieldData);
		createValueFields(dialogComposite, labelDataValue, fieldDataText);
	}

	/**
	 * Creates the context part of the dialog content, and populates with controls depending upon
	 * what type of dialog box was opened.
	 *
	 * @param dialogComposite the dialog composite
	 * @param labelData the layout of the label data
	 * @param fieldData the layout of the field data
	 */
	protected void createContextFields(final IEpLayoutComposite dialogComposite, 
			final IEpLayoutData labelData, final IEpLayoutData fieldData) {
		
		//If the setting value is not a single override then the context field needs to be displayed
		//and validated
		if (!isSingleOverride) {	
			final EpState state;
		
			if (isEditDialog) {
				state = EpState.READ_ONLY;
			} else {
				state = EpState.EDITABLE;
			}
		
			//depending on what type of dialog has been populated, make the context box able to edit accordingly
			dialogComposite.addLabelBoldRequired(AdminConfigurationMessages.get().contextLabel, state, labelData);
			this.contextField = dialogComposite.addTextField(state, fieldData);
		
			if (isEditDialog) {
				contextField.setText(settingValue.getContext());
			}
		}
	}
	
	/**
	 * Creates the value part of the dialog content, and populates with controls depending upon 
	 * what type of dialog box was opened.
	 *
	 * @param dialogComposite the dialog composite
	 * @param labelData the layout of the label data
	 * @param fieldDataText the layout of the field data text
	 */
	protected void createValueFields(final IEpLayoutComposite dialogComposite, 
			final IEpLayoutData labelData, final IEpLayoutData fieldDataText) {
		
		dialogComposite.addLabelBoldRequired(AdminConfigurationMessages.get().valueLabel, EpState.EDITABLE, labelData);
		createValueText(dialogComposite);
		
		//depending on what type of dialog has been opened populate the fields accordingly if edit dialog
		if (isEditDialog) {
			valueField.setText(settingValue.getValue());
		}
	}
	
	/**
	 * Create the value text box for the dialog pop-up and also sets layout data for the text box that will allow
	 * for the text box to fit 50 characters and to be vertically scrollable.
	 * @param dialogComposite the dialog composite
	 */
	private void createValueText(final IEpLayoutComposite dialogComposite) {
		final int height = 4;
		final int width = 50;
		valueField = formToolkit.createText(dialogComposite.getSwtComposite(), "", SWT.WRAP | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER); //$NON-NLS-1$
		GC graphics = new GC(valueField);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, height);
		layoutData.widthHint = graphics.getFontMetrics().getAverageCharWidth() * width;
		layoutData.heightHint = graphics.getFontMetrics().getHeight() * height;
		valueField.setLayoutData(layoutData);
		graphics.dispose();
	}

	@Override
	protected void okPressed() {
		
		if (fieldCheck()) {
			
			//case 1: edit dialog changing the value
			if (isEditDialog) {
				//update value only on save
				getSettingValue().setValue(valueField.getText());
				super.okPressed();
				
			//case 2: new dialog and then implement a check if the context already exists
			//if so then create an error telling the user to use a different context.
			} else {
	
				if (isContextExists()) {
					MessageDialog.openError(shell, AdminConfigurationMessages.get().errorDialogTitle,
							AdminConfigurationMessages.get().errorDialogText);
					//Pop-up error message saying the context is already being used if count > 0
					//Then let the user fix and try to resubmit
					numberContextMatches = 0;
					return;
				}
				//update value only on save
				getSettingValue().setValue(valueField.getText());
				super.okPressed();
			}
			
		}
	}
	
	/**
	 * Helper method that returns a boolean when it has determined that the appropriate fields contain
	 * non-null values when the OK button has been clicked. If the setting can only have a single override,
	 * then only the value field needs to be checked for a non-null value, otherwise if multiple overrides are
	 * available for use both the context and value fields must have non-null values otherwise returns false.
	 * 
	 * @return boolean depending on whether appropriate fields contain non-null values.
	 */
	private boolean fieldCheck() {
		
		//If the setting is able to have multiple overrides need to check if both the contextField 
		//and valueField are both non-null before trying to save
		if (!isSingleOverride) {
			return this.contextField.getText() != null && this.valueField.getText() != null;
		} 
		
		//If the setting is a single override then just need to verify that the valueField is non-null
		return this.valueField.getText() != null;
	}
	
	/**
	 * Checks if the context already exists in the setting value list from the SettingValueInformationComposite
	 * screen.
	 *
	 * @return true if the context already exists, false if the context does not exist in the 
	 * setting value list in the SettingValueInformationComposite.
	 */
	private boolean isContextExists() {
		
		//If the setting is able to have a single override then the context should not 
		//already exist because there should be no other setting with the specified path
		//already present
		if (isSingleOverride) {
			return false;
		}
		
		//If the setting is able to have multiple overrides then there is a need to check
		//to see if the context already exists in previous setting values
		numberContextMatches = 0;
		for (SettingValue value : values) {
			if (value.getContext() != null && value.getContext().equals(contextField.getText())) {
				numberContextMatches++;
				break;
			}
		}
		return numberContextMatches > 0;
	}
	
	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		if (isEditDialog) {
			return AdminConfigurationMessages.get().editDialogTitle;
		}
		
		return AdminConfigurationMessages.get().newDialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		if (isEditDialog) {
			return AdminConfigurationMessages.get().editDialogTitle;
		}
		return AdminConfigurationMessages.get().newDialogTitle;
	}


	@Override
	protected Image getWindowImage() {
		return null;
	}
}
