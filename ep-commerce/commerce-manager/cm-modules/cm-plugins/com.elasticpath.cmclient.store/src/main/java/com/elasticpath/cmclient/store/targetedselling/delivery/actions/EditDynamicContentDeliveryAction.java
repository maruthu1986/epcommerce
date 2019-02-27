/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.store.targetedselling.delivery.views.DynamicContentDeliverySearchResultsView;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.DynamicContentDeliveryWizard;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * Create shipping service level action.
 */
public class EditDynamicContentDeliveryAction extends BaseDynamicContentDeliveryAction {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(EditDynamicContentDeliveryAction.class);

	private final DynamicContentDeliverySearchResultsView listView;

	/**
	 * The constructor.
	 *
	 * @param listView the results from search list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public EditDynamicContentDeliveryAction(final DynamicContentDeliverySearchResultsView listView, final String text,
			final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("EditDynamicContent Action called."); //$NON-NLS-1$

		DynamicContentDeliveryService service = ServiceLocator.getService(
				ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);

		DynamicContentDeliveryModelAdapter dcaWrapper = listView.getSelectedItem();
		DynamicContentDelivery dynamicContentDelivery = service.findByGuid(dcaWrapper.getDynamicContentDelivery().getGuid());
		final DynamicContentDeliveryWizard wizard = new DynamicContentDeliveryWizard(new DynamicContentDeliveryModelAdapter(dynamicContentDelivery));
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setPageSize(DynamicContentDeliveryWizard.DEFAULT_WIDTH, DynamicContentDeliveryWizard.DEFAULT_HEIGHT);
		dialog.addPageChangingListener(wizard);

		if (dialog.open() == Window.OK) {
			fireEvent(EventType.UPDATE, dynamicContentDelivery);
		}
	}

	@Override
	public String getTargetIdentifier() {
		return "editDcdAction"; //$NON-NLS-1$
	}

}
