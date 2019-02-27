/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.HashSet;
import java.util.Set;

import com.elasticpath.cmclient.changeset.helpers.ChangeSetPermissionsHelper;
import com.elasticpath.cmclient.changeset.helpers.impl.ChangeSetPermissionsHelperImpl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.impl.AbstractImportDataTypeImpl;
import com.elasticpath.cmclient.core.ServiceLocator;

/**
 * Policy which determines whether the Run Import Job button can display.
 * The criteria are:
 * 1. Product Association import does not require change set permission.
 * 2. All catalog import jobs require change set permission if change sets enabled.
 * 3. The pricing import job requires change set permission if change sets enabled.
 */
public class ChangeSetRunImportJobPolicy extends AbstractStatePolicyImpl {
	
	private ChangeSetPermissionsHelper changeSetPermissionsHelper;
	
	private boolean changeSetsEnabled;
	
	private final Set<String> jobNamePrefixesRequiringCSPermissions = new HashSet<>();

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
			
		ImportJob selectedImportJob = (ImportJob) targetContainer.getPolicyDependent();
		
		if (selectedImportJob != null) {

			if (!changeSetsEnabled) {
				return EpState.EDITABLE;
			}
			
			return determineStateForSelectedJob(selectedImportJob);
		}

		return EpState.DISABLED;
	}

	private EpState determineStateForSelectedJob(final ImportJob selectedImportJob) {
		// Every import job data type name is created with a datatype prefix. This prefix is set
		// in each subclass of ImportDataType so we can rely on it.
		// However, the Inventory job only has Inventory without the -.
		String importJobName = selectedImportJob.getImportDataTypeName();
		int prefixEnd = importJobName.indexOf(" - "); //$NON-NLS-1$
		if (prefixEnd == -1) {
			return EpState.EDITABLE;
		}
		String importJobPrefix = importJobName.substring(0, prefixEnd); 

		if (jobNamePrefixesRequiringCSPermissions.contains(importJobPrefix)) {
			if (userHasChangeSetPermission()) {
				return EpState.EDITABLE;
			}
			return EpState.DISABLED;
		}
		return EpState.EDITABLE;
	}

	/**
	 * Default access for testing.
	 * @return True if the user has one of the change set permissions.
	 */
	boolean userHasChangeSetPermission() {
		return changeSetPermissionsHelper.userHasChangeSetPermission();
	}
	
	/**
	 * Returns true if the change set feature is enabled.
	 * @return The result
	 */
	boolean isChangeSetFeatureEnabled() {
		return changeSetPermissionsHelper.isChangeSetFeatureEnabled();
	}

	@Override
	public void init(final Object dependentObject) {
		retrieveBeans();
		
		changeSetsEnabled = isChangeSetFeatureEnabled();

		populateJobNamePrefixSet();
	}

	

	/**
	 * Populates the set of import data types to control.
	 * Default access for testing.
	 */
	void populateJobNamePrefixSet() {
		// Determine the ImportJob name prefixes which require change set permissions.

		final AbstractImportDataTypeImpl categoryImportDataType = ServiceLocator.getService(ContextIdNames.IMPORT_DATA_TYPE_CATEGORY);
		jobNamePrefixesRequiringCSPermissions.add(categoryImportDataType.getPrefixOfName());

		final AbstractImportDataTypeImpl productImportDataType = ServiceLocator.getService(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT);
		jobNamePrefixesRequiringCSPermissions.add(productImportDataType.getPrefixOfName());

		final AbstractImportDataTypeImpl productSkuImportDataType = ServiceLocator.getService(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_SKU);
		jobNamePrefixesRequiringCSPermissions.add(productSkuImportDataType.getPrefixOfName());

		final AbstractImportDataTypeImpl productCategoryImportDataType = ServiceLocator.getService(
			ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_CATEGORY_ASSOCIATION);

		jobNamePrefixesRequiringCSPermissions.add(productCategoryImportDataType.getPrefixOfName());

		final AbstractImportDataTypeImpl baseAmountImportDataType = ServiceLocator.getService(ContextIdNames.IMPORT_DATA_TYPE_BASEAMOUNT);
		jobNamePrefixesRequiringCSPermissions.add(baseAmountImportDataType.getPrefixOfName());
	}

	/**
	 * Retrieves required beans.
	 * Default access for testing.
	 */
	void retrieveBeans() {
		// Get services
		changeSetPermissionsHelper = new ChangeSetPermissionsHelperImpl();
		
	}

}
