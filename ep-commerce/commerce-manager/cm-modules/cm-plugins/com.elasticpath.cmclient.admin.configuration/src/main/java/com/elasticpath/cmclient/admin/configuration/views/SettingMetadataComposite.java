/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.configuration.views;

import java.util.Set;

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
import com.elasticpath.cmclient.admin.configuration.dialogs.EditSettingMetadataDialog;
import com.elasticpath.cmclient.admin.configuration.listener.SettingDefinitionUpdateListener;
import com.elasticpath.cmclient.admin.configuration.models.SettingsModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;

/**
 * A composite that displays setting values and allows creation and editing
 * of setting values.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
class SettingMetadataComposite extends Composite implements SettingDefinitionUpdateListener {

	private final FormToolkit formToolkit;

	private final SettingsModel settingsModel;
	private Button editButton;
	private Button removeButton;
	private SettingDefinition selectedDefinition;
	private Set<SettingMetadata> settingMetadataMap;
	private TableViewer tableViewer;


	/**
	 * @param parent          the parent composite
	 * @param style           the style bits
	 * @param definitionModel the settings service
	 */
	SettingMetadataComposite(final Composite parent, final int style, final SettingsModel definitionModel) {
		super(parent, style);
		formToolkit = EpControlFactory.getInstance().createFormToolkit();
		formToolkit.adapt(this);
		this.settingsModel = definitionModel;
		createControls();
	}

	/**
	 * Sets the currently selected SettingDefinition for the SettingValueComposite.
	 *
	 * @param definition that is currently selected
	 */
	public void setSettingDefinition(final SettingDefinition definition) {
		this.selectedDefinition = definition;
		settingMetadataMap = settingsModel.getManagedMetadataForDefinition(selectedDefinition);
		tableViewer.setInput(settingMetadataMap);
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
	 *
	 * @param parent to which the table composite will belong
	 */
	private void createTableComposite(final Composite parent) {
		//Table
		Table table = formToolkit.createTable(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		table.setLayout(new GridLayout(1, true));
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		table.setHeaderVisible(true);
		String[] columnHeaders = {
				AdminConfigurationMessages.get().settingDefMetadataKey,
				AdminConfigurationMessages.get().settingDefMetadataValue
		};
		final int keyColWidth = 100;
		final int valueColWidth = 200;

		int[] columnWidths = {keyColWidth, valueColWidth};
		for (int i = 0; i < columnWidths.length; i++) {
			TableColumn column = new TableColumn(table, SWT.BEGINNING);
			column.setText(columnHeaders[i]);
			column.setWidth(columnWidths[i]);

		}
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new SettingValueContentProvider());
		tableViewer.setLabelProvider(new SettingValueLabelProvider());
		tableViewer.setInput(settingMetadataMap);
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
	 *
	 * @param parent to which the buttons belong.
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	private void createButtonComposite(final Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		formToolkit.adapt(buttonComposite);
		buttonComposite.setLayout(new GridLayout(1, true));
		buttonComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

		Button newButton = formToolkit.createButton(buttonComposite, AdminConfigurationMessages.get().addButton, SWT.PUSH);
		newButton.setImage(AdminConfigurationImageRegistry.getImage(AdminConfigurationImageRegistry.CONFIGURATION_VALUE_ADD));
		newButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		editButton = formToolkit.createButton(buttonComposite, AdminConfigurationMessages.get().editButton, SWT.PUSH);
		editButton.setImage(AdminConfigurationImageRegistry.getImage(AdminConfigurationImageRegistry.CONFIGURATION_VALUE_EDIT));
		editButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		removeButton = formToolkit.createButton(buttonComposite, AdminConfigurationMessages.get().removeButton, SWT.PUSH);
		removeButton.setImage(AdminConfigurationImageRegistry.getImage(AdminConfigurationImageRegistry.CONFIGURATION_VALUE_DELETE));
		removeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		newButton.addListener(SWT.Selection, (Listener) event ->
				new EditSettingMetadataDialog(getShell(), null, selectedDefinition, settingsModel).open());

		editButton.addListener(SWT.Selection, (Listener) event -> {
			if ((tableViewer.getSelection() instanceof IStructuredSelection)
					&& ((IStructuredSelection) tableViewer.getSelection()).getFirstElement() instanceof SettingMetadata) {

				SettingMetadata selectedMetadata = (SettingMetadata) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				new EditSettingMetadataDialog(getShell(), selectedMetadata, selectedDefinition, settingsModel).open();
			}


		});

		removeButton.addListener(SWT.Selection, (Listener) event -> {
			if ((tableViewer.getSelection() instanceof IStructuredSelection)
					&& ((IStructuredSelection) tableViewer.getSelection()).getFirstElement() instanceof SettingMetadata) {
				SettingMetadata selectedMetadata = (SettingMetadata) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				settingsModel.deleteManagedSettingMetadata(selectedDefinition, selectedMetadata);
				removeButton.setEnabled(false);
				editButton.setEnabled(false);
			}
		});

		newButton.setEnabled(true);
		editButton.setEnabled(false);
		removeButton.setEnabled(false);
	}


	@Override
	public void settingDefinitionUpdated(final SettingDefinition definition) {
		settingMetadataMap = settingsModel.getManagedMetadataForDefinition(definition);
		this.tableViewer.setInput(settingMetadataMap);
		this.tableViewer.refresh();
	}


	/**
	 * Provides the labels for the setting value display table.
	 */
	class SettingValueLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Not implemented.
		 *
		 * @param element     not used
		 * @param columnIndex not used
		 * @return null
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Get the column text for the given SettingMetadata and column index.
		 *
		 * @param element     the setting definition
		 * @param columnIndex the index of the column for which to retrieve the text
		 * @return the column text
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			if (!(element instanceof SettingMetadata)) {
				return null;
			}
			final SettingMetadata value = (SettingMetadata) element;

			switch (columnIndex) {
				case 0:
					return value.getKey();
				case 1:
					return value.getValue();
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
		 *
		 * @param settingMetadataSet Map of SettingDefinitions
		 * @return array of elements to pass to the label provider for the table
		 */
		@Override
		public Object[] getElements(final Object settingMetadataSet) {
			Object[] result = null;
			if (settingMetadataSet instanceof Set) {
				//check that all objects in the Set are SettingDefinition objects
				for (Object object : ((Set<SettingMetadata>) settingMetadataSet)) {
					if (!(object instanceof SettingMetadata)) {
						return new Object[0];
					}
				}
				result = ((Set<Object>) settingMetadataSet).toArray();
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
		 *
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
