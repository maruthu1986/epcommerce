/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.views;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.JobsPermissions;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportJob;

/**
 * Import job view for Price List import jobs.
 */
public class PriceListImportJobsListView extends AbstractJobList {
	/** The view ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.jobs.views.PriceListImportJobsListView"; //$NON-NLS-1$

	private static final String PRICE_LIST_IMPORT_JOBS_TABLE = "Price List Import Jobs"; //$NON-NLS-1$
	
	private static final int INDEX_IMAGE = 0;

	private static final int INDEX_JOB_NAME = 1;

	private static final int INDEX_PRICE_LIST = 2;

	private static final int INDEX_IMPORT_TYPE = 3;

	private static final int INDEX_DATA_TYPE = 4;

	private final PriceListService plService;

	/**
	 * The constructor.
	 */
	public PriceListImportJobsListView() {
		super(PRICE_LIST_IMPORT_JOBS_TABLE);
		plService = ServiceLocator.getService(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);
	}
	@Override
	protected String[] getTableColumnNames() {
		return new String[] {
			"", //$NON-NLS-1$
			JobsMessages.get().JobListView_TableColumnTitle_JobName, JobsMessages.get().JobListView_TableColumnTitle_PriceList,
			JobsMessages.get().JobListView_TableColumnTitle_ImportType, JobsMessages.get().JobListView_TableColumnTitle_DataType };
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
		case INDEX_PRICE_LIST:
			String plGuid = importJob.getDependentPriceListGuid();
			if (plGuid == null || plGuid.trim().length() == 0) { 
				return ""; //$NON-NLS-1$
			}
			PriceListDescriptorDTO dto = plService.getPriceListDescriptor(plGuid);
			if (dto == null) {
				return ""; //$NON-NLS-1$
			} 
			return dto.getName();
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
	protected int[] getTableColumnWidths() {
		final int imageColumnWidth = 25;
		final int pricelistColumnWidth = 120;
		final int dataTypeColumnWidth = 190;
		final int importTypeColumnWidth = 99;
		final int jobNameColumnWidth = 200;
		
		return new int[] { 
			imageColumnWidth,
			pricelistColumnWidth,
			dataTypeColumnWidth,
			importTypeColumnWidth,
			jobNameColumnWidth };
	}

	@Override
	protected int getType() {
		return PRICE_LIST_IMPORT_JOBS_TYPE;
	}

	@Override
	protected boolean isAccessable() {
		ImportJob selectedImportJob = getSelectedJob();
		if (selectedImportJob == null) {
			return false;
		}
		return AuthorizationService.getInstance().isAuthorizedForPriceList(
				selectedImportJob.getDependentPriceListGuid());
	}

	@Override
	protected List<ImportJob> listJobs(final int start, final int size) {
		return getImportService().listPriceListImportJobs(
				getAccessablePriceListGuids().toArray(new String[getAccessablePriceListGuids().size()])
				);
	}
	
	@Override
	protected long getCount() {
		return getImportService().countPriceListImportJobs(
				getAccessablePriceListGuids().toArray(new String[getAccessablePriceListGuids().size()])
				);
	}
	
	private Collection<String> getAccessablePriceListGuids() {
		if (LoginManager.getCmUser().isAllPriceListsAccess()) {
			return Collections.emptySet();
		}
		return LoginManager.getCmUser().getPriceLists();
	}

	@Override
	protected String getExecutePermission() {
		return JobsPermissions.PRICE_MANAGEMENT_IMPORT_PRICE_LIST_JOB;
	}

	@Override
	protected String getManagePermission() {
		//Now, only one price list related import jobs.
		//The job is generated when database is populated.
		//No need to manage it from the cmclient. 
		return null;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
