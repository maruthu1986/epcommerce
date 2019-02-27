/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.policy.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * A state policy that makes sure a control is enabled only when a non-empty selection exists.
 */
public abstract class AbstractViewSelectionStatePolicy extends AbstractStatePolicyImpl {

	private final IPartService partService;
	private ISelectionProvider selectionProvider;
	
	/**
	 * Constructor.
	 */
	public AbstractViewSelectionStatePolicy() {
		partService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService();
		partService.addPartListener(new SelectionProviderRetriever());
	}

	/**
	 * Determines the state by checking the selection provider.
	 * 
	 * @param targetContainer the target container
	 * @return the state of the control
	 */
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (selectionProvider == null || selectionProvider.getSelection().isEmpty()) {
			return EpState.READ_ONLY;
		}
		return EpState.EDITABLE;
	}

	@Override
	public void init(final Object dependentObject) {
		// nothing to initialise
	}

	/**
	 * Should provide the ID of the view to be checked.
	 * 
	 * @return the ID of the view
	 */
	protected abstract String getViewId();

	/**
	 * Gets the selection provider of the view.
	 * 
	 * @return the selection provider or null if none has been set yet
	 */
	public ISelectionProvider getSelectionProvider() {
		return selectionProvider;
	}

	/**
	 * A selection provider listener.
	 */
	private final class SelectionProviderRetriever implements IPartListener {
		@Override
		public void partOpened(final IWorkbenchPart part) {
			if (part instanceof IViewPart) {
				IViewPart viewPart = (IViewPart) part;
				if (StringUtils.equals(viewPart.getViewSite().getId(), getViewId())) {
					selectionProvider = viewPart.getViewSite().getSelectionProvider();
				}
			}
		}

		@Override
		public void partDeactivated(final IWorkbenchPart part) {
			// not interested
		}

		@Override
		public void partClosed(final IWorkbenchPart part) {
			if (part instanceof IViewPart) {
				IViewPart viewPart = (IViewPart) part;
				if (StringUtils.equals(viewPart.getViewSite().getId(), getViewId())) {
					selectionProvider = null;
					partService.removePartListener(this);
				}
			}
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {
			// not interested
		}

		@Override
		public void partActivated(final IWorkbenchPart part) {
			// not interested
		}
	}

}
