/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.actions;

import org.apache.log4j.Logger;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.editors.PriceListEditor;
import com.elasticpath.cmclient.pricelistmanager.event.PriceListSelectedEvent;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.PriceListSelectedEventListener;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Listens for {@link PriceListSelectedEvent}s and opens
 * a new PriceListEditor for the selected PriceList.
 */
public class OpenPriceListEditorAction extends AbstractPolicyAwareAction implements PriceListSelectedEventListener {
	private static final Logger LOG = Logger.getLogger(OpenPriceListEditorAction.class);
	private PriceListDescriptorDTO pldDto;
	
	/**
	 * Called when a PriceList is selected to be displayed in the PriceListEditor.
	 * @param event the event containing the price list to be displayed
	 */
	@Override
	public void priceListSelected(final PriceListSelectedEvent event) {
		this.pldDto = (PriceListDescriptorDTO) event.getSource();
		run();
	}
	
	@Override
	public void run() {
		LOG.debug("running OpenPriceListEditor action"); //$NON-NLS-1$
		if (this.pldDto != null) {
			showEditor();
		}
	}
	
	/**
	 * Show PriceList Editor.
	 */
	private void showEditor() {
		final IEditorInput editorInput = 
			new GuidEditorInput(pldDto.getGuid(), PriceListDescriptor.class);
		try {
			IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, PriceListEditor.PART_ID);
			if (part instanceof AbstractCmClientFormEditor) {
				AbstractCmClientFormEditor editor = (AbstractCmClientFormEditor) part;
				editor.refreshEditorPages();
			}
		} catch (final PartInitException e) {
			LOG.error("Can not open PriceList editor", e); //$NON-NLS-1$
		}
	}
	
	@Override
	public String getToolTipText() {
		return PriceListManagerMessages.get().OpenPriceListEditorActionToolTip;
	}

	@Override
	public String getTargetIdentifier() {
		return "openPriceListEditor"; //$NON-NLS-1$
	}
}
