/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.configuration.views;

import java.util.Set;

import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationImageRegistry;
import com.elasticpath.cmclient.admin.configuration.AdminConfigurationMessages;
import com.elasticpath.cmclient.admin.configuration.dialogs.EditSettingValueDialog;
import com.elasticpath.cmclient.admin.configuration.listener.SettingValueUpdateListener;
import com.elasticpath.cmclient.admin.configuration.models.SettingsModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * A composite that displays setting values and allows creation and editing
 * of setting values.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
class SettingValueComposite extends Composite implements SettingValueUpdateListener {

	private final FormToolkit formToolkit;

	private final SettingsModel settingsModel;
	private Button newButton;
	private Button editButton;
	private Button removeButton;
	private SettingDefinition selectedDefinition;
	private Set<SettingValue> settingValues;
	private TableViewer tableViewer;
	private boolean isSingleOverride;

	
	/**
	 * @param parent the parent composite
	 * @param style the style bits
	 * @param definitionModel the settings service
	 */
	SettingValueComposite(final Composite parent, final int style, final SettingsModel definitionModel) {
		super(parent, style);
		formToolkit = EpControlFactory.getInstance().createFormToolkit();
		formToolkit.adapt(this);
		this.settingsModel = definitionModel;
		createControls();
	}
	
	/**
	 * Sets the currently selected SettingDefinition for the SettingValueComposite.
	 * @param definition that is currently selected
	 */
	public void setSettingDefinition(final SettingDefinition definition) {
		this.selectedDefinition = definition;
		settingValues = settingsModel.getAllValues(selectedDefinition);
		tableViewer.setInput(settingValues);
		
		setButtonState();
	}

	/**
	 * Creates the controls for the SettingValueComposite.
	 */
	private void createControls() {
		setupLayout();
		createGroupComposite();
	}
	
	/**
	 * Sets the layout for the SettingValueComposite.
	 */
	private void setupLayout() {
		this.setLayout(new GridLayout(1, true));
	}
	
	/**
	 * Creates the group containing other elements in the SettingValueComposite.
	 */
	private void createGroupComposite() {
		Composite group = new Composite(this, SWT.NONE);
		formToolkit.adapt(group);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		//nest stuff in the group
		createTableComposite(group);
		createButtonComposite(group);
	}
	
	/**
	 * Creates the Table in the group in the SettingsValueComposite.
	 * @param parent to which the table composite will belong
	 */
	private void createTableComposite(final Composite parent) {
		Table table = formToolkit.createTable(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		table.setLayout(new GridLayout(1, true));
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		table.setHeaderVisible(true);
		String[] columnHeaders = { 
				AdminConfigurationMessages.get().settingValueContext,
				AdminConfigurationMessages.get().settingValueValue,
				AdminConfigurationMessages.get().settingValueLastModified };
		final int contextColWidth = 150;
		final int valueColWidth = 150;
		final int lastModifiedColWidth = 100;
		int[] columnWidths = { contextColWidth, valueColWidth, lastModifiedColWidth };
		for (int i = 0; i < columnWidths.length; i++) {
			TableColumn column = new TableColumn(table, SWT.BEGINNING);
			column.setText(columnHeaders[i]);
			column.setWidth(columnWidths[i]);
			
		}
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new SettingValueContentProvider());
		tableViewer.setLabelProvider(new SettingValueLabelProvider());
		tableViewer.setInput(settingValues);
		tableViewer.addSelectionChangedListener(event -> {
			if (event.getSelection().isEmpty()) {
				editButton.setEnabled(false);
				removeButton.setEnabled(false);
			} else {
				editButton.setEnabled(true);
				removeButton.setEnabled(true);
			}

		});
	}
	
	/**
	 * Creates the buttons in the group for the SettingsValueComposite.
	 * @param parent to which the buttons belong.
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	private void createButtonComposite(final Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		formToolkit.adapt(buttonComposite);
		buttonComposite.setLayout(new GridLayout(1, true));
		buttonComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		
		newButton = formToolkit.createButton(buttonComposite, AdminConfigurationMessages.get().addButton, SWT.PUSH);
		newButton.setImage(AdminConfigurationImageRegistry.getImage(AdminConfigurationImageRegistry.CONFIGURATION_VALUE_ADD));
		newButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		editButton = formToolkit.createButton(buttonComposite, AdminConfigurationMessages.get().editButton, SWT.PUSH);
		editButton.setImage(AdminConfigurationImageRegistry.getImage(AdminConfigurationImageRegistry.CONFIGURATION_VALUE_EDIT));
		editButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		removeButton = formToolkit.createButton(buttonComposite, AdminConfigurationMessages.get().removeButton, SWT.PUSH);
		removeButton.setImage(AdminConfigurationImageRegistry.getImage(AdminConfigurationImageRegistry.CONFIGURATION_VALUE_DELETE));
		removeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		//add a listener to the "New..." button to create a new dialog if clicked.
		newButton.addListener(SWT.Selection, (Listener) event -> {
			//create a settingValue and pass to the "New..." dialog

			//Check if the "New..." dialog is for a "single" override for a setting and if it is, then
			//create an edit dialog with the context set to null and value set to an empty string initially
			boolean editDialog = false;
			isSingleOverride = false;
			SettingValue newValue = settingsModel.createSettingValue(selectedDefinition, null);

			if (selectedDefinition.getMaxOverrideValues() == 1) {
				isSingleOverride = true;
				newValue.setValue(""); //$NON-NLS-1$
			}

			boolean dialogOk = EditSettingValueDialog.openEditSettingValueDialog(getShell(),
					newValue, editDialog, settingValues, isSingleOverride, selectedDefinition);

			if (dialogOk) {
				settingsModel.addSettingValue(selectedDefinition, newValue);
			}

			setButtonState();
		});
		
		//add a listener to the "Edit" button to create a new dialog if clicked.
		editButton.addListener(SWT.Selection, (Listener) event -> {
			//open up the edit configuration value dialog box that should have the current context
			//and value present with a save and cancel button - "Edit Configuration Value"
			if ((tableViewer.getSelection() instanceof IStructuredSelection)
					&& ((IStructuredSelection) tableViewer.getSelection()).getFirstElement() instanceof SettingValue) {
				isSingleOverride = false;
				if (selectedDefinition.getMaxOverrideValues() == 1) {
					isSingleOverride = true;
				}

				SettingValue selectedValue = (SettingValue) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				//We work with the managed setting value to ensure that we're always getting and updating values from the same place.
				SettingValue workingValue = settingsModel.getSettingValue(selectedDefinition.getPath(), selectedValue.getContext());
				boolean dialogOk = EditSettingValueDialog.openEditSettingValueDialog(getShell(),
						workingValue, true, settingValues, isSingleOverride, selectedDefinition);

				if (dialogOk) {
					settingsModel.updateSettingValue(selectedDefinition, workingValue);
				}

				setButtonState();
			}


		});
		
		removeButton.addListener(SWT.Selection, (Listener) event -> {
			if ((tableViewer.getSelection() instanceof IStructuredSelection)
				&& ((IStructuredSelection) tableViewer.getSelection()).getFirstElement() instanceof SettingValue) {
				SettingValue selectedValue = (SettingValue) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				settingsModel.deleteSettingValue(selectedDefinition, selectedValue);

				setButtonState();
			}
		});
		
		setButtonState();
	}
	
	/**
	 * Utility method manage buttons states based on several factors such as setting override. 
	 */
	private void setButtonState() {
		boolean settingValueSelected = tableViewer.getSelection() == null;
		removeButton.setEnabled(settingValueSelected);
		editButton.setEnabled(settingValueSelected);
		
		newButton.setEnabled(isDefinitionOverridable());
	}
	
	/**
	 * Helper method to check if the setting definition currently selected
	 * is a global setting definition or store-specific.
	 *
	 * @return a boolean depending whether definition is global or store specific
	 */
	private boolean isDefinitionOverridable() {
		if (selectedDefinition == null) {
			return false;
		}
		
		return selectedDefinition.getMaxOverrideValues() == -1 || settingValues.size() < selectedDefinition.getMaxOverrideValues();
	}
	
	@Override
	public void settingValueUpdated(final SettingValue val) {
		setButtonState();
		
		settingValues = settingsModel.getAllValues(selectedDefinition);
		
		this.tableViewer.setInput(settingValues);
		this.tableViewer.refresh();
	}
	
	/**
	 * Provides the labels for the setting value display table.
	 */
	class SettingValueLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Not implemented.
		 * @param element not used
		 * @param columnIndex not used
		 * @return null
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Get the column text for the given SettingDefiniton and column index.
		 * @param element the setting definition
		 * @param columnIndex the index of the column for which to retrieve the text
		 * @return the column text
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			if (!(element instanceof SettingValue)) {
				return null;
			}
			final SettingValue value = (SettingValue) element;
			
			switch (columnIndex) {
			case 0:
				return value.getContext();
			case 1:
				return value.getValue();
			case 2:
				return DateTimeUtilFactory.getDateUtil().formatAsDateTime(value.getLastModifiedDate());
			default:
				return StringUtils.EMPTY;
			}
		}
	
	}
	
	/**
	 * Provides the content for the SettingValue display table, given an input.
	 */
	class SettingValueContentProvider implements IStructuredContentProvider {

		/**
		 * Get the elements to display in the table.
		 * @param settingValueSet Set of SettingDefinitions
		 * @return array of elements to pass to the label provider for the table
		 */
		@Override
		public Object[] getElements(final Object settingValueSet) {
			Object[] result = null;
			if (settingValueSet instanceof Set) {
				//check that all objects in the Set are SettingDefinition objects
				for (Object object : ((Set<Object>) settingValueSet)) {
					if (!(object instanceof SettingValue)) {
						return new Object[0];
					}
				}
				result = ((Set<Object>) settingValueSet).toArray();
			}
			return result;
		}

		/**
		 * not needed.
		 */
		@Override
		public void dispose() {
			// nothing to dispose
		}

		/**
		 * Not needed.
		 * @param arg0 not used
		 * @param arg1 not used
		 * @param arg2 not used
		 */
		@Override
		public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
			//not needed
		}
	}
}
