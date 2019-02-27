/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.jobs.JobsMessages;

/**
 * A failed validation results page for header.
 *
 */
public class HeaderValidationPage extends WizardPage {

	/** A page name. */
	public static final String PAGE_NAME = "HEADER_VALIDATION_PAGE"; //$NON-NLS-1$
	
	private Label errorLabel;
	private String validationMessage;
	
	/**
	 * A constructor with page name. 
	 */
	public HeaderValidationPage() {
		super(PAGE_NAME, JobsMessages.get().ConfigurePriceListImportJobPage_ErrorDialog_Title, null);
	}
	
	/**
	 * A constructor with page name, title and image. 
	 * @param title title
	 * @param titleImage image
	 */
	public HeaderValidationPage(final String title, final ImageDescriptor titleImage) {
		super(PAGE_NAME, title, titleImage);
	}


	@Override
	public void createControl(final Composite parent) {

		IEpLayoutComposite controlPane = CompositeFactory.createGridLayoutComposite(parent, 1, true);
		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, false, false);

		this.errorLabel = controlPane.addLabel("", fieldData); //$NON-NLS-1$
		
		this.setControl(controlPane.getSwtComposite());
	}

	private void updateErrorLoabelText() {
		errorLabel.setText(validationMessage);
		errorLabel.getParent().layout();
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void setVisible(final boolean visible) {
		this.updateErrorLoabelText();
		super.setVisible(visible);
	}

	/**
	 * @param validationMessage the validationMessage to set
	 */
	public void setValidationMessage(final String validationMessage) {
		this.validationMessage = validationMessage;
	}

}
