/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistassignments.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.pricelistassignments.wizard.model.PriceListAssignmentModelAdapter;
import com.elasticpath.cmclient.pricelistassignments.wizard.tableview.CatalogTableViewer;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Class represents second page of {@link DynamicContentDeliveryWizard} wizard.
 * On this page user MUST to select one {@link Catalog}.
 * Only one may be selected. 
 */
public class PriceListAssignmentWizardCatalogSelectPage 
			extends AbstractPolicyAwareWizardPage<PriceListAssignmentModelAdapter> {

	private static final String PRICE_LIST_CATALOG_TABLE = "Price List Catalog"; //$NON-NLS-1$
	private IEpTableViewer modelTableViewer;
	private final CatalogSelectionUpdater catalogSelectionUpdater = new CatalogSelectionUpdater();
	private final CatalogTableViewer catalogTableViewer = new CatalogTableViewer();
	
	/**
	 * Constructor.
	 * 
	 * @param pageName - name of the page
	 * @param title - title of the page
	 * @param description - description of the page
	 */
	public PriceListAssignmentWizardCatalogSelectPage(final String pageName, final String title, final String description) {
		super(2, false, pageName, title, description, new DataBindingContext());
	}

	@Override
	protected void bindControls() {
		// do nothing
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite parent) {
		PolicyActionContainer policyContainer = addPolicyActionContainer("priceListAssignmentWizardCatalogPage"); //$NON-NLS-1$
		final IEpLayoutData tableLayoutData = parent.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false);
		modelTableViewer = parent.addTableViewer(false, tableLayoutData, policyContainer, PRICE_LIST_CATALOG_TABLE);
		
		// add the updater to the policy container so that it triggers the selection listener
		policyContainer.addTarget(catalogSelectionUpdater);

		/* MUST be called */
		setControl(parent.getSwtComposite());
	}

	@Override
	protected void populateControls() {
		catalogTableViewer.initTable(modelTableViewer, getModel().getPriceListAssignment().getCatalog(), this.getContainer());
	}

	@Override
	public boolean isPageComplete() {
		return getModel().getCatalog() != null;
	}
	
	/**
	 * Updates the catalog to the model on user selection.
	 */
	private class CatalogSelectionUpdater implements ISelectionChangedListener, StateChangeTarget {
		
		public void selectionChanged(
				final SelectionChangedEvent event) {
			final IStructuredSelection strSelection = (IStructuredSelection) event
					.getSelection();
			final Catalog firstSelected = (Catalog) strSelection
					.getFirstElement();
			getModel().setCatalog(firstSelected);
			getWizard().getContainer().updateButtons();	
		}

		/**
		 * Adds/removes the selection listener for updating the model depending on the provided control state.
		 * 
		 * @param state the new state to set
		 */
		@Override
		public void setState(final EpState state) {
			if (state == EpState.EDITABLE) {
				modelTableViewer.getSwtTableViewer().addSelectionChangedListener(
						catalogSelectionUpdater);
				catalogTableViewer.setSelectionEnabled(true);
			} else {
				modelTableViewer.getSwtTableViewer().removeSelectionChangedListener(
						catalogSelectionUpdater);
				catalogTableViewer.setSelectionEnabled(false);
			}
		}
	}

	@Override
	protected boolean isCreateCompositeBlock() {
		return false;
	}
}