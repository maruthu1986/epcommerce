/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.policy;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.jobs.views.AbstractJobList;
import com.elasticpath.cmclient.jobs.views.CatalogJobListView;
import com.elasticpath.cmclient.jobs.views.CustomerJobListView;
import com.elasticpath.cmclient.jobs.views.PriceListImportJobsListView;
import com.elasticpath.cmclient.jobs.views.WarehouseJobListView;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.dataimport.ImportJob;

/**
 * Default import job policy for the Run Import Job button.
 */
public class RunImportJobPolicy extends AbstractStatePolicyImpl {

	private String permission;

	private AbstractJobList listView;

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		EpState returnValue = EpState.DISABLED;

		if (AuthorizationService.getInstance().isAuthorizedWithPermission(permission)) {
			returnValue = determineStateByListView(targetContainer);
		} 
		return returnValue;
	}

	private EpState determineStateByListView(final PolicyActionContainer targetContainer) {
		ImportJob selectedImportJob = (ImportJob) targetContainer.getPolicyDependent();

		if (selectedImportJob != null) {
			if (listView instanceof CatalogJobListView
					&& (AuthorizationService.getInstance().isAuthorizedForCatalog(selectedImportJob.getCatalog()))) {
					return EpState.EDITABLE;
			} 
			if (listView instanceof CustomerJobListView
					&& AuthorizationService.getInstance().isAuthorizedForStore(selectedImportJob.getStore())) {
					return EpState.EDITABLE;
			} 
			if (listView instanceof PriceListImportJobsListView 
						&& AuthorizationService.getInstance().isAuthorizedForPriceList(selectedImportJob.getDependentPriceListGuid())) {
					return EpState.EDITABLE;
			}  
			if (listView instanceof WarehouseJobListView
					&& AuthorizationService.getInstance().isAuthorizedForWarehouse(
					selectedImportJob.getWarehouse())) {
					return EpState.EDITABLE;
			}
		}
		return EpState.DISABLED;
	}

	@Override
	public void init(final Object dependentObject) {
		// The AbstractPolicyAwareAction will give us the result of getDependentObject on the RunJobAction.
		Pair<AbstractJobList, String> pair = (Pair<AbstractJobList, String>) dependentObject;
		AbstractJobList view = pair.getFirst();
		this.permission = pair.getSecond();
		this.listView = view;
	}
}
