/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.jobs.JobsMessages;

/**
 * Success page.
 * 02.08.2007 12:32:55
 */
public class SuccessPage extends WizardPage {
	/**
	 * A page name.
	 */
	public static final String PAGE_NAME = "SuccessPage"; //$NON-NLS-1$

	private final DataBindingContext dbc;

	/**
	 * Constructor.
	 *
	 * @param title the page title
	 * @param titleImage the image to display in the page
	 * @param description the page description
	 */
	public SuccessPage(final String title, final String description, final ImageDescriptor titleImage) {
		super(PAGE_NAME, title, titleImage);
		setDescription(description);
		this.dbc = new DataBindingContext();
	}

	/**
	 * Get the DataBindingContext.
	 *
	 * @return the DataBindingContext.
	 */
	public DataBindingContext getBindingContext() {
		return this.dbc;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	/**
	 * Create the wizard's page composite.
	 *
	 * @param parent the page's parent
	 */
	public void createControl(final Composite parent) {
		IEpLayoutComposite controlPane = CompositeFactory.createGridLayoutComposite(parent, 1, true);
		controlPane.addLabel(JobsMessages.get().RunWizard_MappingsValidationSuccess, controlPane.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.BEGINNING));
		this.setControl(controlPane.getSwtComposite());
	}

	@Override
	public void setErrorMessage(final String newMessage) {
		// Do nothing
	}
}
