/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.editors.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.event.SettingChangeListener;
import com.elasticpath.cmclient.core.helpers.store.SettingModel;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;

/**
 * Represents editing support for settings.
 */
public class SettingsEditingSupport extends EditingSupport {
	
	private final List<SettingChangeListener> settingChangeListeners = new ArrayList<SettingChangeListener>();
	
	private final IEpTableViewer settingsTableViewer;

	/**
	 * Constructs settings editing support.
	 * 
	 * @param attributesTableViewer the attributes table viewer
	 */
	public SettingsEditingSupport(final IEpTableViewer attributesTableViewer) {
		super(attributesTableViewer.getSwtTableViewer());
		this.settingsTableViewer = attributesTableViewer;
	}

	@Override
	protected boolean canEdit(final Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		return new SettingCellEditor(settingsTableViewer.getSwtTable(), (SettingModel) element);
	}

	@Override
	protected Object getValue(final Object element) {
		final SettingModel settingValue = (SettingModel) element;
		return settingValue.getAssignedValue();
	}

	@Override
	protected void setValue(final Object element, final Object value) {
		final SettingModel settingValue = (SettingModel) element;
		settingValue.setAssignedValue((String) value);		
		notifyListeners(settingValue);
	}
	
	/**
	 * Creates the edit setting dialog.
	 * 
	 * @param parentShell the parent shell
	 * @param settingModel the setting model
	 * @return the setting edit dialog
	 */
	public static SettingDialog createEditSettingDialog(final Shell parentShell, final SettingModel settingModel) {
		return new SettingDialog(parentShell, settingModel.getAssignedValue());
	}
	
	/**
	 * Registers the setting change listener.
	 * 
	 * @param listener the setting change listener
	 */
	public void registerSettingChangeListener(final SettingChangeListener listener) {
		settingChangeListeners.add(listener);
	}

	/**
	 * Unregisters the setting change listener.
	 * 
	 * @param listener the setting change listener to remove
	 */
	public void unregisterSettingChangeListener(final SettingChangeListener listener) {
		settingChangeListeners.remove(listener);
	}
	
	private void notifyListeners(final SettingModel model) {
		for (SettingChangeListener changeListener : settingChangeListeners) {
			changeListener.settingChanged(model);
		}
	}
	
	/**
	 * Settings cell editor.
	 */
	private class SettingCellEditor extends DialogCellEditor {
		
		private final SettingModel settingModel;
		
		SettingCellEditor(final Composite parent, final SettingModel settingModel) {
			super(parent);
			this.settingModel = settingModel;
		}

		@Override
		protected Object openDialogBox(final Control cellEditorWindow) {
			SettingDialog dialog = createEditSettingDialog(cellEditorWindow.getShell(), settingModel);
			if (dialog.open() == Window.OK) {
				return dialog.getValue();
			}
			return settingModel.getAssignedValue();
		}
		
	}
	
	/**
	 * Setting dialog.
	 */
	public static class SettingDialog extends AbstractEpDialog {
		
		private String model;
		
		private Text valueTextArea;

		/**
		 * Creates the edit settings dialog.
		 * 
		 * @param parentShell the parent shell
		 * @param model the model
		 */
		public SettingDialog(final Shell parentShell, final String model) {
			super(parentShell, 2, false);
			this.model = ""; //$NON-NLS-1$
			if (model != null) {
				this.model = model;
			}
		}

		@Override
		protected void bindControls() {
			//do nothing
		}

		@Override
		protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
			final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING);
			final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
			
			dialogComposite.addLabelBoldRequired("Value", EpState.EDITABLE, labelData);  //$NON-NLS-1$
			valueTextArea = dialogComposite.addTextArea(false, false, EpState.EDITABLE, fieldData);
			valueTextArea.setText(model);
			
			valueTextArea.addModifyListener(new ModifyListener() {
				public void modifyText(final ModifyEvent event) {
					model = valueTextArea.getText();
				}
			});
		}

		@Override
		protected String getInitialMessage() {
			return null;
		}

		@Override
		protected String getTitle() {
			return getWindowTitle();
		}

		@Override
		protected Image getWindowImage() {
			return null;
		}

		@Override
		protected String getWindowTitle() {
			return CoreMessages.get().EditSetting;
		}

		@Override
		protected String getPluginId() {
			return CorePlugin.PLUGIN_ID;
		}

		@Override
		public Object getModel() {
			return getValue();
		}

		@Override
		protected void populateControls() {
			//do nothing
		}
		
		/**
		 * Gets the result value.
		 * 
		 * @return the result value
		 */
		public String getValue() {
			return model;
		}
		
	}
}
