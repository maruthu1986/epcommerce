/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.admin.configuration.views;

import java.util.Comparator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.configuration.AdminConfigurationImageRegistry;
import com.elasticpath.cmclient.admin.configuration.AdminConfigurationMessages;
import com.elasticpath.cmclient.admin.configuration.dialogs.EditSettingDefinitionDialog;
import com.elasticpath.cmclient.admin.configuration.listener.SettingDefinitionUpdateListener;
import com.elasticpath.cmclient.admin.configuration.models.SettingsModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.settings.domain.SettingDefinition;

/**
 * A composite to display all SettingDefinition objects, using the formtoolkit
 * for a nice white background.
 * NOTE: This could be changed to display a given collection of SettingDefinition objects, 
 * instead of all the SD objects it can get from the service.
 */
public class SettingDefinitionComposite extends Composite implements ISelectionProvider, SettingDefinitionUpdateListener {

	private static final String NULLSTRING = ""; //$NON-NLS-1$
	private TableViewer tableViewer;
	private final SettingsModel definitionModel;
	private final FormToolkit formToolkit;
	private static final int SETTING_KEY_COLUMN_WIDTH = 700;

	private Button editDefButton;

	/**
	 * @param parent the parent composite
	 * @param style the SWT style bits
	 * @param definitionModel the model for manipulating setting definitions
	 */
	public SettingDefinitionComposite(final Composite parent, final int style, final SettingsModel definitionModel) {
		super(parent, style);
		formToolkit = EpControlFactory.getInstance().createFormToolkit();
		formToolkit.adapt(this);
		this.definitionModel = definitionModel;
		createControls();
	}
	
	private void createControls() {
		setupLayout();
		createTableComposite();
		
		editDefButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final EditSettingDefinitionDialog dialog = new EditSettingDefinitionDialog(getShell(), 
						(SettingDefinition) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement(), definitionModel);
				dialog.open(); 
			}
		});
		
		
		this.tableViewer.addSelectionChangedListener(selectionChangedEvent -> {
			if ((selectionChangedEvent.getSelection() instanceof IStructuredSelection)
					&& ((IStructuredSelection) selectionChangedEvent.getSelection()).getFirstElement() instanceof SettingDefinition) {
				editDefButton.setEnabled(true);
			}
		});
	}
	
	private void setupLayout() {
		final int columns = 3;
		this.setLayout(new GridLayout(columns, false));
	}
	
	
	/**
	 * Filter setting definitions against a specified string.
	 */
	private class SettingPathFilter extends ViewerFilter {
		private final String filterText;
		SettingPathFilter(final String filterText) {
			this.filterText = filterText;
		}
		@Override
		public boolean select(final Viewer viewer, final Object parent, final Object element) {
			SettingDefinition definition = (SettingDefinition) element;
			return StringUtils.containsIgnoreCase(definition.getPath(), filterText);
		}
	}
	
	
	private void createTableComposite() {
		editDefButton =  formToolkit.createButton(this, 
				AdminConfigurationMessages.get().editButton, SWT.PUSH);
		editDefButton.setImage(AdminConfigurationImageRegistry.getImage(AdminConfigurationImageRegistry.CONFIGURATION_VALUE_EDIT));
		editDefButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		editDefButton.setEnabled(false);
		
		Label label = formToolkit.createLabel(this, AdminConfigurationMessages.get().filterLabel + ":"); //$NON-NLS-1$
		GridData gridData = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gridData);
		Text settingNameFilter = formToolkit.createText(this, "", SWT.SEARCH | SWT.ICON_CANCEL); //$NON-NLS-1$
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		settingNameFilter.setLayoutData(gridData);

		settingNameFilter.addModifyListener((ModifyListener) event -> {
			final Text source = (Text) event.getSource();
			String filterText = source.getText();
			if (StringUtils.isBlank(filterText)) {
				tableViewer.setFilters(new ViewerFilter [0]);
			} else {
				tableViewer.setFilters(new ViewerFilter [] {new SettingPathFilter(filterText)});
			}
		});
		
		//Table
		final Table table = formToolkit.createTable(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		table.setLayout(new GridLayout(1, true));
		final int horizontalSpan = 3;
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, horizontalSpan, 1));
		table.setHeaderVisible(true);
		final TableColumn column = new TableColumn(table, SWT.BEGINNING);
		column.setText(AdminConfigurationMessages.get().settingDefinitionPath);
		column.setWidth(SETTING_KEY_COLUMN_WIDTH);
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new SettingDefinitionContentProvider());
		tableViewer.setLabelProvider(new SettingDefinitionLabelProvider());
		
		tableViewer.setInput(definitionModel.getAllDefinitions());
		//We want to be able to sort the column in the viewer, so we need a comparator.
		//NOTE: If more than one column is added to the table, the ViewerComparator MUST be changed accordingly!
		tableViewer.setComparator(new SettingDefinitionViewerComparator(tableViewer, column, new Comparator<Object>() {

			/**
			 * Compares the given objects, which must be SettingDefinition objects.
			 * @param settingDefinition1 the first settingDefinition
			 * @param settingDefinition2 the secondSettingDefinition
			 * @return the result of the comparison
			 */
			@Override
			public int compare(final Object settingDefinition1, final Object settingDefinition2) {
				return ((SettingDefinition) settingDefinition2).compareTo((SettingDefinition) settingDefinition1); 
			}
		}));
		
	}	
	
	/**
	 * Add a SelectionChangedListener that will be notified when the table's 
	 * selected settingDefinitionViewer changes.
	 * @param listener the listener to add 
	 */
	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		this.tableViewer.addSelectionChangedListener(listener);
	}

	/**
	 * Get the selected SettingDefinition.
	 * @return the selected SettingDefinition
	 */
	@Override
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}

	

	/**
	 * Remove the given SelectionChangedListener.
	 * @param listener the listener to remove
	 */
	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		this.tableViewer.removeSelectionChangedListener(listener);
	}

	/**
	 * Sets the current SettingDefinition.
	 * @param iselection the new settingDefinition
	 */
	@Override
	public void setSelection(final ISelection iselection) {
		if (iselection instanceof SettingDefinition) {
			this.tableViewer.setSelection(iselection);
		}
	}

	/**
	 * Provides the labels for the setting definition display table.
	 */
	class SettingDefinitionLabelProvider extends LabelProvider implements ITableLabelProvider {

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
			final SettingDefinition definition = (SettingDefinition) element;
			
			if (columnIndex == 0) {
				return definition.getPath();
			}
			return NULLSTRING;
		}
	}
	
	/**
	 * Provides the content for the SettingDefinition display table, given an input.
	 */
	class SettingDefinitionContentProvider implements IStructuredContentProvider {

		/**
		 * Get the elements to display in the table.
		 * @param settingDefinitionSet Set of SettingDefinitions
		 * @return array of elements to pass to the label provider for the table
		 */
		@Override
		public Object[] getElements(final Object settingDefinitionSet) {
			Object[] result = null;
			if (settingDefinitionSet instanceof Set) {
				//check that all objects in the Set are SettingDefinition objects
				for (final Object object : ((Set<Object>) settingDefinitionSet)) {
					if (!(object instanceof SettingDefinition)) {
						return null;
					}
				}
				result = ((Set<Object>) settingDefinitionSet).toArray();
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
	
	/**
	 * Comparator for the SettingDefinition table, which has only one column.
	 * This comparator allows you to click on the column header to toggle the sort order of
	 * the column between ascending and descending sort.
	 */
	class SettingDefinitionViewerComparator extends ViewerComparator {
		
		private final TableViewer viewer;
		private boolean sortAscending;
		private final Comparator<Object> comparator;
		private final TableColumn column;
		
		/**
		 * Constructor.
		 * @param viewer the tableviewer that's using this ViewerComparator; must contain SettingDefinition objects
		 * @param column the column that will be sorted
		 * @param comparator the comparator to use to sort the rows
		 */
		SettingDefinitionViewerComparator(final TableViewer viewer, final TableColumn column, final Comparator<Object> comparator) {
			this.viewer = viewer;
			this.comparator = comparator;
			this.column = column;
			createSelectionListener();
		}
		
		private void createSelectionListener() {
			column.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					sort();
				}
			});
		}
		
		@Override
		public int compare(final Viewer viewer, final Object settingDefinition1, final Object settingDefinition2) {
			final int result = comparator.compare(settingDefinition1, settingDefinition2);
			if (result != 0) {
				if (!sortAscending) {
					return -result;
				}
				return result;
			}
			return 0;
		}
		
		private void sort() {
			sortAscending = (!sortAscending);
			viewer.refresh();
		}
	}

	@Override
	public void settingDefinitionUpdated(final SettingDefinition def) {
		this.tableViewer.setInput(definitionModel.getAllDefinitions());
		this.tableViewer.refresh();
	}
}
