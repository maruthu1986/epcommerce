/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.wizards.couponcodes;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.layout.GridData;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.jobs.wizards.AbstractConfigureImportJobPage;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJobRequest;

/**
 * A configure page for import base amounts.
 *
 */
public class DirectCsvImportConfigureCouponsImportPage extends AbstractConfigureImportJobPage {

	/** A page name. */
	public static final String PAGE_NAME = "ConfigureImportJobPage"; //$NON-NLS-1$

	private IWizardPage nextPage = this;

	/**
	 * Default constructor.
	 * @param title a page title
	 * @param description a page description
	 * @param importJobRequest an import job request object
	 */
	public DirectCsvImportConfigureCouponsImportPage(
			final String title, final String description,
			final ImportJobRequest importJobRequest) {
		super(PAGE_NAME, title, description, importJobRequest.getImportJob());
		this.setDescription(description);
	}

	@Override
	protected List<ImportDataType> getImportDataTypes() {
		return Collections.emptyList();
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		pageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.createTopComponents(pageComposite);
		
		this.createImportFileComponent(pageComposite);

		this.createBottomComponents(pageComposite);

		this.setControl(pageComposite.getSwtComposite());
	}

	@Override
	public IWizardPage getNextPage() {
		return this.nextPage;
	}
	
	/**
	 * Set the next Page.
	 * @param nextPage set the next page for wizard
	 */
	public void setNextPage(final IWizardPage nextPage) {
		this.nextPage = nextPage;
	}
	
}
