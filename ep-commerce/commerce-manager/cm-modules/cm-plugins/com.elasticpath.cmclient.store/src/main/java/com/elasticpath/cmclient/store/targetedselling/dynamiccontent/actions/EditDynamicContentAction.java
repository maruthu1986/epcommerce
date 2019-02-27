/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views.DynamicContentSearchResultsView;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard.NewDynamicContentWizard;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.contentspace.DynamicContentService;

/**
 * Create dynamic content level action.
 */
public class EditDynamicContentAction extends AbstractBaseDynamicContentAction {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(EditDynamicContentAction.class);

	private final DynamicContentSearchResultsView listView;
	/**
	 * The constructor.
	 *
	 * @param listView the results from search list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public EditDynamicContentAction(final DynamicContentSearchResultsView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;

	}

	@Override
	public void run() {
		LOG.debug("EditDynamicContent Action called."); //$NON-NLS-1$

		DynamicContentService dynamicContentService = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_SERVICE);

		DynamicContent dynamicContent = dynamicContentService.findByGuid(listView.getSelectedItem().getGuid());

		final NewDynamicContentWizard wizard = new NewDynamicContentWizard(dynamicContent, true);
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.addPageChangingListener(wizard);

		dialog.open();
	}

	@Override
	public String getTargetIdentifier() {
		return "editDynamicContentAction"; //$NON-NLS-1$
	}
}
