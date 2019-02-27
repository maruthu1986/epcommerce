/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.configuration.views;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.settings.domain.SettingDefinition;


/**
 * A composite that displays information about a SettingDefinition. 
 */
public class SettingDefinitionDisplayComposite extends Composite {

	private final FormToolkit formToolkit;

	/**
	 * Possible value types include BigDecimal, boolean, Collection, CSV, Integer, Map, String, url, xml.
	 */
	private String valueType;
	private Label pathString;
	private Label typeString;
	private Label maxOverrideValuesString;
	private Text defaultValueString;
	private Text descriptionString;
	private static final String COLON = ":"; //$NON-NLS-1$
	private final boolean editable;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * @param parent the parent composite
	 * @param style the style bits
	 * @param editable if the composite allows editing of default value
	 */
	public SettingDefinitionDisplayComposite(final Composite parent, final int style, final boolean editable) {
		super(parent, style);
		formToolkit = controlFactory.createFormToolkit();
		formToolkit.adapt(this);
		this.editable = editable;
		this.createControls();
	}

	/**
	 * Creates the SettingDefinition display composite.
	 */
	private void createControls() {
		this.setLayout(new GridLayout(2, false));
		
		//Create grid layout data
		GridData twoColumnLayoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
		
		//Create the path label that will stretch across two columns
		pathString = formToolkit.createLabel(this, StringUtils.EMPTY, SWT.BOLD);
		pathString.setLayoutData(twoColumnLayoutData);
		
		//Create a label for the type of setting definition and another label that will contain the definition's type
		createLabel(AdminConfigurationMessages.get().settingDefType); //Type label
		typeString = createStringLabel();
		
		//Create a label for the overrides and another label that contains the actual number of overrides allowed for the setting
		createLabel(AdminConfigurationMessages.get().settingDefMaxOverrides); //Overrides label
		maxOverrideValuesString = createStringLabel();
		
		//Create a label that spans more rows than a normal label (to match the text box size), 
		//and a text box that contains the description of the setting
		createLabelForTextBox(AdminConfigurationMessages.get().settingDefDescription);
		descriptionString = createTextBox();
		descriptionString.setEditable(false);
		
		//Create a label that spans more rows than a normal label  (to match the text box size),
		//and a text box that contains the default value of the setting
		createLabelForTextBox(AdminConfigurationMessages.get().settingDefDefaultValue); //Default Value label
		defaultValueString = createTextBox();
		defaultValueString.setEditable(editable);
		
		if (editable) {
			//Sets the focus on the default value text box if the composite is in editable mode
			defaultValueString.setFocus();
		}
		
		this.pack();
	}

	private void createLabelForTextBox(final String text) {
		final int height = 4;
		Label label = formToolkit.createLabel(this, text.concat(COLON), SWT.WRAP);
		label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false, 1, height));
	}
	
	/**
	 * Creates a blank text box with default system font. The text box is multi-lined,
	 * vertically scrolls, and wraps text.
	 * @return a text box that is blank with default system font.
	 */
	private Text createTextBox() {
		final int height = 4;
		final int width = 50;
		Text box = formToolkit.createText(this, "", SWT.WRAP | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER); //$NON-NLS-1$
		GC graphics = new GC(box);
		
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, height);
		layoutData.widthHint = graphics.getFontMetrics().getAverageCharWidth() * width;
		layoutData.heightHint = graphics.getFontMetrics().getHeight() * height;
		box.setLayoutData(layoutData);
		graphics.dispose();
		return box;
	}
	
	/**
	 * Creates a label using label font and appends a colon to the label.
	 * @param text to be used for the label
	 */
	private void createLabel(final String text) {
		Label label = formToolkit.createLabel(this, text.concat(COLON), SWT.WRAP);
		label.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));
	}
	
	/**
	 * Creates a blank label with default system font and no colon.
	 * @return Label that was created
	 */
	private Label createStringLabel() {
		Label label = formToolkit.createLabel(this, "", SWT.WRAP); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		return label;
	}

	/**
	 * Set the SettingDefinition that this composite will display.
	 *
	 * @param definition the setting definition
	 */
	public void setSettingDefinition(final SettingDefinition definition) {
		valueType = definition.getValueType();
		pathString.setText(definition.getPath());
		typeString.setText(definition.getValueType());
		maxOverrideValuesString.setText(String.valueOf(definition.getMaxOverrideValues()));
		defaultValueString.setText(StringUtils.defaultString(definition.getDefaultValue()));
		descriptionString.setText(StringUtils.defaultString(definition.getDescription()));
		descriptionString.redraw();
	}
	
	/**
	 * Returns the text in the default value text box.
	 * @return text as a string of the contents of the text box.
	 */
	public String getDefaultValueStringText() {
		return defaultValueString.getText();
	}

	/**
	 * Data bind the default value text box in order to validate the information being entered, should
	 * only be called from a dialog box which contains the SettingDefinitionDisplayComposite.
	 * @param bindingContext context
	 * @param dialog dialog
	 */
	public void bindDefaultValueString(final DataBindingContext bindingContext, final AbstractEpDialog dialog) {
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		//update strategy for the default value field control
		final ObservableUpdateValueStrategy defaultValueUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				//Value is set into object when Save is pressed
				return Status.OK_STATUS;
			}
		};
		
		// BigDecimal, boolean, Collection, CSV, Integer, Map, String, url, xml
		if (valueType == null) {
			binder.bind(bindingContext, defaultValueString, EpValidatorFactory.MAX_LENGTH_65535, null, 
					defaultValueUpdateStrategy, true);
			
		} else if ("BigDecimal".equalsIgnoreCase(valueType)) { //$NON-NLS-1$
			binder.bind(bindingContext, defaultValueString, EpValidatorFactory.BIG_DECIMAL_REQUIRED, null, 
					defaultValueUpdateStrategy, true);
			
		} else if ("boolean".equalsIgnoreCase(valueType)) { //$NON-NLS-1$
			binder.bind(bindingContext, defaultValueString, EpValidatorFactory.BOOLEAN_REQUIRED, null, 
					defaultValueUpdateStrategy, true);
			
		} else if ("Integer".equalsIgnoreCase(valueType)) { //$NON-NLS-1$
			binder.bind(bindingContext, defaultValueString, EpValidatorFactory.INTEGER, null, 
					defaultValueUpdateStrategy, true);
			
		} else {
			binder.bind(bindingContext, defaultValueString, EpValidatorFactory.STRING_65535_REQUIRED, null, 
					defaultValueUpdateStrategy, true);
		}
		
		
		EpDialogSupport.create(dialog, bindingContext);
	}
}
