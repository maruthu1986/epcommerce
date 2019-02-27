/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.views;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.search.SafeSearchUids;
import com.elasticpath.cmclient.core.search.impl.SafeSearchUidsImpl;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.JobsPermissions;
import com.elasticpath.domain.dataimport.ImportJob;

/**
 * This view displays list of warehouse import jobs in a table format.
 */
public class WarehouseJobListView extends AbstractJobList {

	/** The view ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.jobs.views.WarehouseJobListView"; //$NON-NLS-1$

	private static final String WAREHOUSE_JOB_TABLE = "Warehouse Job"; //$NON-NLS-1$
	
	private static final int INDEX_IMAGE = 0;
	
	private static final int INDEX_DATA_TYPE = 1;

	private static final int INDEX_IMPORT_TYPE = 2;
	
	private static final int INDEX_JOB_NAME = 3;

	/**
	 * Constructor.
	 */
	public WarehouseJobListView() {
		super(WAREHOUSE_JOB_TABLE);
	}

	@Override
	protected int getType() {
		return WAREHOUSE_IMPORT_JOBS_TYPE;
	}

	@Override
	protected long getCount() {
		return getImportService().countWarehouseJobs(accessibleWarehouseUids().toArray(new Long [] {}));
	}

	@Override
	protected List<ImportJob> listJobs(final int start, final int size) {
		return getImportService().listWarehouseImportJobs(start, size, accessibleWarehouseUids().toArray(new Long [] {}));
	}

	private Set<Long> accessibleWarehouseUids() {
		Set<Long> warehouseUids = new HashSet<Long>();
		if (!LoginManager.getCmUser().isAllWarehousesAccess()) {
			SafeSearchUids uids = new SafeSearchUidsImpl();
			uids.extractAndAdd(LoginManager.getCmUser().getWarehouses());
			warehouseUids.addAll(uids.asSet());
		}
		return warehouseUids;
	}
	
	@Override
	protected String[] getTableColumnNames() {
		return new String[] {
			"", //$NON-NLS-1$
			JobsMessages.get().JobListView_TableColumnTitle_DataType,
			JobsMessages.get().JobListView_TableColumnTitle_ImportType,
			JobsMessages.get().JobListView_TableColumnTitle_JobName };
	}

	@Override
	protected int[] getTableColumnWidths() {
		final int imageColumnWidth = 25;
		final int dataTypeColumnWidth = 190;
		final int importTypeColumnWidth = 99;
		final int jobNameColumnWidth = 200;
		
		return new int[] {
			imageColumnWidth,
			dataTypeColumnWidth,
			importTypeColumnWidth,
			jobNameColumnWidth };
	}
	
	@Override
	protected boolean isAccessable() {
		ImportJob selectedImportJob = getSelectedJob();
		if (selectedImportJob == null) {
			return false;
		}
		return AuthorizationService.getInstance().isAuthorizedForWarehouse(selectedImportJob.getWarehouse());
	}
	
	@Override
	public Image getTableColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	@Override
	public String getTableColumnText(final Object element, final int columnIndex) {
		final ImportJob importJob = (ImportJob) element;

		switch (columnIndex) {

		case INDEX_IMAGE:
			return ""; //$NON-NLS-1$
		case INDEX_DATA_TYPE:
			return getDataTypeName(importJob.getImportDataTypeName());
		case INDEX_IMPORT_TYPE:
			return JobsMessages.get().getMessage(importJob.getImportType().getNameMessageKey());
		case INDEX_JOB_NAME:
			return importJob.getName();
		default:
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	protected String getExecutePermission() {
		return JobsPermissions.WAREHOUSE_IMPORT_INVENTORY;
	}

	@Override
	protected String getManagePermission() {
		return JobsPermissions.WAREHOUSE_IMPORT_INVENTORY;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
