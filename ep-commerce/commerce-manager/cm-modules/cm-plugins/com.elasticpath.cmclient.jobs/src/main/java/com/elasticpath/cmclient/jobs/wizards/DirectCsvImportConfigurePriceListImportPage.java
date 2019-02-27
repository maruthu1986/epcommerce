/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJobRequest;

/**
 * A configure page for import base amounts.
 *
 */
public class DirectCsvImportConfigurePriceListImportPage extends AbstractConfigureImportJobPageWithDataTypeAndImportType {

	/** A page name. */
	public static final String PAGE_NAME = "ConfigureImportJobPage"; //$NON-NLS-1$
	private Button previewDataButton;
	
	private IWizardPage nextPage = this;

	/**
	 * Default constructor.
	 * @param title a page title
	 * @param description a page description
	 * @param importJobRequest an import job requiest object
	 */
	public DirectCsvImportConfigurePriceListImportPage(
			final String title, final String description,
			final ImportJobRequest importJobRequest) {
		super(PAGE_NAME, title, description, importJobRequest.getImportJob());
		this.setDescription(description);
	}

	@Override
	protected List<ImportDataType> getImportDataTypes() {
		return getImportService().getPriceListImportDataTypes();
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		pageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.createTopComponents(pageComposite);
		
		this.createImportFileComponent(pageComposite);

		this.createBottomComponents(pageComposite);

		this.createPreviewComponent(pageComposite);

		this.setControl(pageComposite.getSwtComposite());
	}

	/**
	 * @param pageComposite a parent composite
	 */
	private void createPreviewComponent(final IEpLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 3, 1);
	
		pageComposite.addLabelBold(JobsMessages.get().JobDetailsPage_PreviewData, labelData);
		previewDataButton = pageComposite.addCheckBoxButton("", EpControlFactory.EpState.EDITABLE, fieldData); //$NON-NLS-1$
	}
	
	/**
	 * Try to check is preview mode is active.
	 * @return true if active
	 */
	public boolean isPreviewModeActivated() {
		return this.previewDataButton.getSelection();
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
