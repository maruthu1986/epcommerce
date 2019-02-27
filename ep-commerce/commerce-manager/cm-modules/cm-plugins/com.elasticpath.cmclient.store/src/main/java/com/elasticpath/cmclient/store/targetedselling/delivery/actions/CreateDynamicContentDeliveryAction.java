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
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.DynamicContentDeliveryWizard;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.commons.constants.ContextIdNames;

/**
 * Create action for DynamicContentDelivery.
 */
public class CreateDynamicContentDeliveryAction extends BaseDynamicContentDeliveryAction {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(CreateDynamicContentDeliveryAction.class);

	/**
	 * The constructor.
	 * 
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public CreateDynamicContentDeliveryAction(final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
	}

	@Override
	public void run() {
		LOG.debug("CreateDynamicContentAssignment Action called."); //$NON-NLS-1$

		DynamicContentDeliveryModelAdapter model =
				new DynamicContentDeliveryModelAdapter(ServiceLocator.getService(
				ContextIdNames.DYNAMIC_CONTENT_DELIVERY));

		final DynamicContentDeliveryWizard wizard = new DynamicContentDeliveryWizard(model);
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setPageSize(DynamicContentDeliveryWizard.DEFAULT_WIDTH, DynamicContentDeliveryWizard.DEFAULT_HEIGHT);
		dialog.addPageChangingListener(wizard);
		wizard.setObjectGuid(null);
		
		if (dialog.open() == Window.OK) {
			this.fireEvent(EventType.CREATE, model.getDynamicContentDelivery());
		}
	}

	@Override
	public String getTargetIdentifier() {	
		return "createDcdAction"; //$NON-NLS-1$
	}	
	
}
