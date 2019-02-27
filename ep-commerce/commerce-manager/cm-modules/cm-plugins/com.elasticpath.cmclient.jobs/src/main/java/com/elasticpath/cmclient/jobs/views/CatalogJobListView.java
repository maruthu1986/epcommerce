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
 * This view displays list of catalog jobs in a table format.
 */
public class CatalogJobListView extends AbstractJobList {
	/** The view ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.jobs.views.CatalogJobListView"; //$NON-NLS-1$

	private static final String CATALOG_JOB_TABLE = "Catalog job"; //$NON-NLS-1$
	
	private static final int INDEX_IMAGE = 0;

	private static final int INDEX_CATALOG = 1;

	private static final int INDEX_DATA_TYPE = 2;

	private static final int INDEX_IMPORT_TYPE = 3;

	private static final int INDEX_JOB_NAME = 4;

	/**
	 * Constructor.
	 */
	public CatalogJobListView() {
		super(CATALOG_JOB_TABLE);
	}

	@Override
	protected int getType() {
		return CATALOG_IMPORT_JOBS_TYPE;
	}

	@Override
	protected long getCount() {
		return getImportService().countCatalogJobs(accessibleCatalogUids().toArray(new Long [] {}));
	}

	@Override
	protected List<ImportJob> listJobs(final int start, final int size) {
		return getImportService().listCatalogImportJobs(start, size, accessibleCatalogUids().toArray(new Long [] {}));
	}

	private Set<Long> accessibleCatalogUids() {
		Set<Long> catalogCodes = new HashSet<Long>();
		if (!LoginManager.getCmUser().isAllCatalogsAccess()) {
			SafeSearchUids uids = new SafeSearchUidsImpl();
			uids.extractAndAdd(LoginManager.getCmUser().getCatalogs());
			catalogCodes.addAll(uids.asSet());
		}
		return catalogCodes;
	}
	
	@Override
	protected String[] getTableColumnNames() {
		return new String[] {
			"", //$NON-NLS-1$
			JobsMessages.get().JobListView_TableColumnTitle_Catalog, JobsMessages.get().JobListView_TableColumnTitle_DataType,
			JobsMessages.get().JobListView_TableColumnTitle_ImportType, JobsMessages.get().JobListView_TableColumnTitle_JobName, };
	}

	@Override
	protected int[] getTableColumnWidths() {
		final int imageColumnWidth = 25;
		final int catalogColumnWidth = 120;
		final int dataTypeColumnWidth = 190;
		final int importTypeColumnWidth = 99;
		final int jobNameColumnWidth = 200;
		
		return new int[] { 
			imageColumnWidth,
			catalogColumnWidth,
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
		return AuthorizationService.getInstance().isAuthorizedForCatalog(selectedImportJob.getCatalog());
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
		case INDEX_CATALOG:
			return importJob.getCatalog().getName();
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
		return JobsPermissions.CATALOG_IMPORTS_EXECUTE;
	}

	@Override
	protected String getManagePermission() {		
		return JobsPermissions.CATALOG_IMPORTS_MANAGE;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
