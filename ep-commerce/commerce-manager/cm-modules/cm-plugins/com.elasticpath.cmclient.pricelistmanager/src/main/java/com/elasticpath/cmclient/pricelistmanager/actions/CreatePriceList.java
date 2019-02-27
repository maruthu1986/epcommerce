/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.pricelistmanager.editors.PriceListEditor;
import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Create price list action.
 */
public class CreatePriceList extends AbstractPolicyAwareAction {

	private static final Logger LOG = Logger.getLogger(CreatePriceList.class.getName());
	
	/**
	 * The constructor.
	 * 
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */	
	public CreatePriceList(final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		LOG.info("Created " + CreatePriceList.class.getName());  //$NON-NLS-1$ 
	}
	
	@Override
	public void run() {
		final IEditorInput editorInput = new GuidEditorInput(null, PriceListDescriptor.class);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, PriceListEditor.PART_ID);
		} catch (final PartInitException e) {
			LOG.error("Can not open PriceList editor", e); //$NON-NLS-1$
		}	
	}

	@Override
	public String getTargetIdentifier() {
		return "createPriceListAction"; //$NON-NLS-1$
	}

}
