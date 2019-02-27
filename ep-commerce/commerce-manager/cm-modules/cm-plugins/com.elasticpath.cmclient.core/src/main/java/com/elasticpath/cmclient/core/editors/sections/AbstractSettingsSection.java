/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.editors.sections;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.sections.SettingsEditingSupport.SettingDialog;
import com.elasticpath.cmclient.core.event.SettingChangeListener;
import com.elasticpath.cmclient.core.helpers.store.SettingModel;
import com.elasticpath.cmclient.core.helpers.store.SettingValidationState;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;

/**
 * Represents abstract settings section.
 */
public abstract class AbstractSettingsSection extends AbstractCmClientEditorPageSectionPart implements ISelectionChangedListener,
		SettingChangeListener {

	private static final int NAME_COLUMN = 0;

	private static final int TYPE_COLUMN = 1;

	private static final int DEFAULTVALUE_COLUMN = 2;

	private static final int ASSIGNEDVALUE_COLUMN = 3;

	private static final int NAME_COLUMN_WIDTH = 450;

	private static final int TYPE_COLUMN_WIDTH = 80;

	private static final int DEFAULTVALUE_COLUMN_WIDTH = 100;

	private static final int ASSIGNEDVALUE_COLUMN_WIDTH = 200;

	private final String tableName;

	private IEpTableViewer settingsTableViewer;

	private IEpLayoutComposite buttonsComposite;

	private Button editButton;

	private EpState rolePermission = EpState.EDITABLE;

	/**
	 * Constructs the abstract settings section.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 * @param tableName name of the table
	 */
	public AbstractSettingsSection(final FormPage formPage, final AbstractCmClientFormEditor editor, final String tableName) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);
		this.tableName = tableName;
	}

	/**
	 * Sets true if settings are editable and false otherwise.
	 *
	 * @param isEditable true if settings are editable and false otherwise
	 */
	protected void setEditable(final boolean isEditable) {
		this.rolePermission = EpState.READ_ONLY;
		if (isEditable) {
			this.rolePermission = EpState.EDITABLE;
		}
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		IEpLayoutComposite controlPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);
		final TableWrapData tableWrapdata = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		tableWrapdata.grabHorizontal = true;
		controlPane.setLayoutData(tableWrapdata);

		final IEpLayoutData tableLayoutData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, true);
		this.settingsTableViewer = controlPane.addTableViewer(false, rolePermission, tableLayoutData, tableName);

		settingsTableViewer.addTableColumn(CoreMessages.get().Store_Marketing_Name, NAME_COLUMN_WIDTH);
		settingsTableViewer.addTableColumn(CoreMessages.get().Store_Marketing_Type, TYPE_COLUMN_WIDTH);
		settingsTableViewer.addTableColumn(CoreMessages.get().Store_Marketing_DefaultValue, DEFAULTVALUE_COLUMN_WIDTH);
		final IEpTableColumn valueColumn = settingsTableViewer.addTableColumn(CoreMessages.get().Store_Marketing_AssignedValue,
				ASSIGNEDVALUE_COLUMN_WIDTH);

		if (rolePermission == EpState.EDITABLE) {
			SettingsEditingSupport editorSupport = new SettingsEditingSupport(settingsTableViewer);
			editorSupport.registerSettingChangeListener(this);
			valueColumn.setEditingSupport(editorSupport);
		}

		buttonsComposite = controlPane.addGridLayoutComposite(1, true, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
				true));

		editButton = buttonsComposite
				.addPushButton(CoreMessages.get().Store_Marketing_EditValue, CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT),
						rolePermission, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		editButton.addSelectionListener(getEditAction());

		setEditSettingsButtonsEnabled(false);

		settingsTableViewer.setContentProvider(new ArrayContentProvider());
		settingsTableViewer.getSwtTableViewer().addSelectionChangedListener(this);
		settingsTableViewer.setLabelProvider(new SettingsLabelProvider());

	}

	//TODO-RAP-M1 Removed mouse hover events
	// See https://eclipse.org/rap/developers-guide/devguide.php?topic=key-and-mouse-events.html

	private SelectionAdapter getEditAction() {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final SettingModel settingModel = getSelectedSetting();
				final SettingDialog editSettingDialog = SettingsEditingSupport.createEditSettingDialog(getEditor().getEditorSite().getShell(),
						settingModel);
				if (editSettingDialog.open() == Window.OK) {
					settingModel.setAssignedValue(editSettingDialog.getValue());
					settingChanged(settingModel);
				}
			}
		};
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {

		final IStructuredSelection selection = (IStructuredSelection) settingsTableViewer.getSwtTableViewer().getSelection();

		boolean enabled = rolePermission == EpState.EDITABLE && !selection.isEmpty();
		setEditSettingsButtonsEnabled(enabled);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		final SettingModel[] input = (SettingModel[]) settingsTableViewer.getSwtTableViewer().getInput();
		binder.bind(bindingContext, editButton, null, null, createValidationStrategy(input), false);
	}

	private ObservableUpdateValueStrategy createValidationStrategy(final SettingModel[] input) {
		return new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				for (SettingModel settingModel : input) {
					if (settingModel.validateSetting() == SettingValidationState.FAILURE) {
						return new Status(IStatus.CANCEL, CorePlugin.PLUGIN_ID,

								NLS.bind(CoreMessages.get().SettingValidationMessage,
								settingModel.getName()));
					}
				}
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	protected void populateControls() {
		final List<SettingModel> input = getInput();
		settingsTableViewer.setInput(input.toArray(new SettingModel[input.size()]));
	}

	@Override
	public void settingChanged(final SettingModel model) {
		settingsTableViewer.getSwtTableViewer().update(model, null);
		settingsTableViewer.getSwtTableViewer().refresh();
		getEditor().controlModified();
	}

	/**
	 * Gets the list of settings to fill the settings table.
	 * 
	 * @return the list of settings
	 */
	protected abstract List<SettingModel> getInput();

	private SettingModel getSelectedSetting() {
		final IStructuredSelection selection = (IStructuredSelection) settingsTableViewer.getSwtTableViewer().getSelection();
		return (SettingModel) selection.getFirstElement();
	}

	/**
	 * Provides labels for the Settings TableViewer.
	 */
	protected class SettingsLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final SettingModel settingModel = (SettingModel) element;
			switch (columnIndex) {
			case NAME_COLUMN:
				return settingModel.getName();
			case TYPE_COLUMN:
				return settingModel.getType();
			case DEFAULTVALUE_COLUMN:
				return getValue(settingModel.getDefaultValue());
			case ASSIGNEDVALUE_COLUMN:
				return getValue(settingModel.getAssignedValue());
			default:
				return ""; //$NON-NLS-1$
			}
		}

		private String getValue(final String value) {
			if (value == null || "".equals(value)) { //$NON-NLS-1$
				return CoreMessages.get().NotDefinedValue;
			}
			return value;
		}
	}

	/**
	 * Enables of disables buttons for editing of marketing settings.
	 * 
	 * @param enabled enable buttons if true, disable if false
	 */
	private void setEditSettingsButtonsEnabled(final boolean enabled) {
		editButton.setEnabled(enabled);
	}
}
