/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.taxes.dialogs.TaxCategoryDialog;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;

/**
 * Create new tax category action.
 */
public class CreateTaxCategoryAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(CreateTaxCategoryAction.class);

	/** Tax Categories table view. */
	private final IEpTableViewer tableView;

	/** Tax Jurisdiction. */
	private final TaxJurisdiction taxJurisdiction;

	/**
	 * The constructor.
	 * 
	 * @param tableView the tax category table view.
	 * @param taxJurisdiction Tax Jurisdiction.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public CreateTaxCategoryAction(final IEpTableViewer tableView, final TaxJurisdiction taxJurisdiction, final String text,
			final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.tableView = tableView;
		this.taxJurisdiction = taxJurisdiction;
	}

	@Override
	public void run() {
		LOG.debug("Create Tax Category action called."); //$NON-NLS-1$
		TaxCategory taxCategory = ServiceLocator.getService(ContextIdNames.TAX_CATEGORY);

		boolean dialogOk = TaxCategoryDialog.openCreateDialog(tableView.getSwtTable().getShell(), taxCategory, taxJurisdiction);

		if (dialogOk) {
			
			if (taxCategory.getFieldMatchType() == TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY) {
				TaxRegion taxRegion = ServiceLocator.getService(ContextIdNames.TAX_REGION);
				taxRegion.setRegionName(taxJurisdiction.getRegionCode());
				taxCategory.getTaxRegionSet().add(taxRegion);			
			}
			
			taxJurisdiction.getTaxCategorySet().add(taxCategory);
			tableView.getSwtTableViewer().refresh();
		}
	}
}
