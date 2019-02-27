/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.cmclient.jobs.JobsImageRegistry;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.JobsPlugin;
import com.elasticpath.cmclient.jobs.actions.CreateJobAction;
import com.elasticpath.cmclient.jobs.actions.DeleteJobAction;
import com.elasticpath.cmclient.jobs.actions.EditJobAction;
import com.elasticpath.cmclient.jobs.actions.RunJobAction;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.service.dataimport.ImportService;

/**
 * This view displays lists of jobs in a table format.
 */
public abstract class AbstractJobList extends AbstractListView {

	private final ImportService importService;

	private Action createJobAction;

	private Action editJobAction;

	private Action deleteJobAction;

	private RunJobAction runJobAction;
	
	private List<ImportDataType> importDataTypes;

	/**
	 * If ImportJobWizard.type == CUSTOMER_IMPORT_JOBS_TYPE in addPages()
	 * ConfigureCustomerImportJobPage will be created for configuring import job.
	 */
	public static final int CUSTOMER_IMPORT_JOBS_TYPE = 0;

	/**
	 * If ImportJobWizard.type == CATALOG_IMPORT_JOBS_TYPE in addPages()
	 * ConfigureCatalogImportJobPage will be created for configuring import job.
	 */
	public static final int CATALOG_IMPORT_JOBS_TYPE = 1;

	/**
	 * If ImportJobWizard.type == WAREHOUSE_IMPORT_JOBS_TYPE in addPages()
	 * ConfigureWarehouseImportJobPage will be created for configuring import job.
	 */
	public static final int WAREHOUSE_IMPORT_JOBS_TYPE = 2;

	/**
	 * If ImportJobWizard.type == PRICE_LIST_IMPORT_JOBS_TYPE in addPages()
	 * ConfigurePriceListImportJobPage will be created for configuring import job.
	 */
	public static final int PRICE_LIST_IMPORT_JOBS_TYPE = 3;

	
	/**
	 * The constructor.
	 * @param tableName name of the table
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public AbstractJobList(final String tableName) {
		super(true, tableName);
		importService = getBean(ContextIdNames.IMPORT_SERVICE);
	}

	/**
	 * Find a bean from the factory by name.
	 * 
	 * @param <T> the type of the bean
	 * @param beanName the name of the bean
	 * @return the bean.
	 */
	protected <T> T getBean(final String beanName) {
		return ServiceLocator.getService(beanName);
	}
	
	@Override
	protected void initializeViewToolbar() {
		final Separator jobsActionGroup = new Separator("JobActionGroup"); //$NON-NLS-1$

		getToolbarManager().add(jobsActionGroup);

		createJobAction = new CreateJobAction(this, JobsMessages.get().CreateJobAction,
				JobsImageRegistry.JOB_CREATE, getManagePermission(), getType());
		createJobAction.setToolTipText(JobsMessages.get().CreateJobActionToolTip);
		//createJobAction.setEnabled(createJobAction.is);

		runJobAction = new RunJobAction(this, JobsMessages.get().RunJobAction, JobsImageRegistry.JOB_RUN, getExecutePermission(), getType());
		runJobAction.setToolTipText(JobsMessages.get().RunJobActionToolTip);
		runJobAction.setEnabled(false);

		editJobAction = new EditJobAction(this, JobsMessages.get().EditJobAction, JobsImageRegistry.JOB_EDIT, getManagePermission(), getType());
		editJobAction.setToolTipText(JobsMessages.get().EditJobActionToolTip);
		editJobAction.setEnabled(false);
		addDoubleClickAction(editJobAction);

		deleteJobAction = new DeleteJobAction(this, JobsMessages.get().DeleteJobAction, JobsImageRegistry.JOB_DELETE, getManagePermission());
		deleteJobAction.setToolTipText(JobsMessages.get().DeleteJobActionToolTip);
		deleteJobAction.setEnabled(false);

		final ActionContributionItem createJobActionContributionItem = new ActionContributionItem(createJobAction);
		final ActionContributionItem runJobActionContributionItem = new ActionContributionItem(runJobAction);
		final ActionContributionItem editJobActionContributionItem = new ActionContributionItem(editJobAction);
		final ActionContributionItem removeJobActionContributionItem = new ActionContributionItem(deleteJobAction);

		runJobActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editJobActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		createJobActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		removeJobActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(jobsActionGroup.getGroupName(), runJobActionContributionItem);
		getToolbarManager().appendToGroup(jobsActionGroup.getGroupName(), editJobActionContributionItem);
		getToolbarManager().appendToGroup(jobsActionGroup.getGroupName(), createJobActionContributionItem);
		getToolbarManager().appendToGroup(jobsActionGroup.getGroupName(), removeJobActionContributionItem);
	}

	
	/**
	 * Returns type.
	 * 
	 * @return type
	 */
	protected abstract int getType();
	
	/**
	 * Gets a manage permission.
	 *
	 * @return Permission Name
	 */
	protected abstract String getManagePermission(); 

	/**
	 * Gets a execute permission.
	 *
	 * @return Permission Name
	 */
	protected abstract String getExecutePermission(); 

	@Override
	protected Object[] getViewInput() {
		long longResultsCount = getCount();
		setResultsCount((int) longResultsCount);
		final List< ? > importJobs = listJobs(getResultsStartIndex(), getResultsPaging());
		return importJobs.toArray(new ImportJob[importJobs.size()]);
	}

	/**
	 * @return total jobs amount.
	 */
	protected abstract long getCount();

	/**
	 * Return list of jobs to display.
	 * 
	 * @param start lower border of list.
	 * @param size maximum size of list to return.
	 * @return list of jobs to display.
	 */
	protected abstract List<ImportJob> listJobs(final int start, final int size);

	/**
	 * Gets the currently-selected job.
	 * 
	 * @return the currently-selected ImportJob
	 */
	public ImportJob getSelectedJob() {
		final IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		ImportJob importJob = null;
		if (!selection.isEmpty()) {
			importJob = (ImportJob) selection.getFirstElement();
		}
		return importJob;
	}

	/**
	 * @return ImportService instance.
	 */
	protected ImportService getImportService() {
		return importService;
	}
	
	/**
	 * gets Data Type Name and caching import DataTypes.
	 * @param importDataTypeName the name of DataType.
	 * @return Localized MessageKey + dataTypeName.
	 */
	protected String getDataTypeName(final String importDataTypeName) {
		// Caching of Import DataTypes
		if (importDataTypes == null) {
			importDataTypes = getImportService().listImportDataTypes();
		}
		
		// Finding dataType by name
		for (ImportDataType importDataType : importDataTypes) {
			if (importDataType.getName().equals(importDataTypeName)) {
				return JobsMessages.get().getMessage(importDataType.getNameMessageKey(), importDataType.getTypeName());
			}					
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	protected void initializeTable(final IEpTableViewer viewerTable) {
		final String[] columnNames = getTableColumnNames();
		
		final int[] columnWidths = getTableColumnWidths();

		for (int i = 0; i < columnNames.length; i++) {
			viewerTable.addTableColumn(columnNames[i], columnWidths[i]);
		}
		viewerTable.getSwtTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				final boolean editable = (getSelectedJob() != null) && isAccessable();
				editJobAction.setEnabled(editable);
				deleteJobAction.setEnabled(editable);
				runJobAction.jobSelectionChanged(event, getSelectedJob());
			}
		});
	}
	
	/**
	 * Create column names specific for concrete view.
	 *
	 * @return names of table columns
	 */
	protected abstract String[] getTableColumnNames();
	
	/**
	 * Create column widths specific for concrete view.
	 *
	 * @return widths of table columns
	 */
	protected abstract int[] getTableColumnWidths();

	/**
	 * Whether are actions for selected job accessible by current cmuser.  
	 *
	 * @return boolean 
	 */
	protected abstract boolean isAccessable();
	
	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new ListViewLabelProvider();
	}
	
	/**
	 * Label provider for the page.
	 */
	protected class ListViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * Get the image to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			return getTableColumnImage(element, columnIndex);
		}

		/**
		 * Get the text to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			return getTableColumnText(element, columnIndex);
		}
	}
	
	/**
	 * Get the image to put in each column for concrete view.
	 * 
	 * @param element the row object
	 * @param columnIndex the column index in concrete view's table
	 * @return the Image to put in the column
	 */
	public abstract Image getTableColumnImage(final Object element, final int columnIndex);
	
	/**
	 * Get the text to put in each column for concrete view.
	 * 
	 * @param element the row object
	 * @param columnIndex the column index in concrete view's table
	 * @return the String to put in the column
	 */
	public abstract String getTableColumnText(final Object element, final int columnIndex);

	@Override
	protected String getPluginId() {
		return JobsPlugin.PLUGIN_ID;
	}
}
