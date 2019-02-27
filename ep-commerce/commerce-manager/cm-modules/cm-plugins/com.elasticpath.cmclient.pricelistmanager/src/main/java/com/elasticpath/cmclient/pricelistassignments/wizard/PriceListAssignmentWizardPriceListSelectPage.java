/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistassignments.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.CCombo;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.pricelistassignments.wizard.model.PriceListAssignmentModelAdapter;
import com.elasticpath.cmclient.pricelistassignments.wizard.tableview.PriceListDescriptiorsTableViewer;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Class represents second page of {@link DynamicContentDeliveryWizard} wizard.
 * On this page user MUST to select one {@link PriceListDescriptor}.
 * Only one may be selected. 
 */
public class PriceListAssignmentWizardPriceListSelectPage 
			extends AbstractPolicyAwareWizardPage<PriceListAssignmentModelAdapter> implements StateChangeTarget {

	private static final String PRICE_LIST_ASSIGNMENT_TABLE = "Price List Assignment"; //$NON-NLS-1$
	private IEpTableViewer modelTableViewer;
	private CCombo currenciesCombo;
	private final PriceListDescriptiorsTableViewer priceListDescriptiorsTableViwer = new PriceListDescriptiorsTableViewer();
	private final PriceListSelectionUpdater priceListSelectionUpdater = new PriceListSelectionUpdater();

	/**
	 * Constructor.
	 *
	 * @param pageName    name of the page
	 * @param title       title of the page
	 * @param description description of the page
	 */
	public PriceListAssignmentWizardPriceListSelectPage(final String pageName, final String title, final String description) {
		super(2, false, pageName, title, description, new DataBindingContext());
	}

	@Override
	protected void bindControls() {
		// nothing to do
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite parent) {
		PolicyActionContainer policyContainer = addPolicyActionContainer("priceListAssignmentWizardPriceListSelectPage"); //$NON-NLS-1$
		PolicyActionContainer editableContainer = addPolicyActionContainer("priceListAssignmentWizardPriceListSelectPageEditable"); //$NON-NLS-1$

		final IEpLayoutData compositLayoutData = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		IPolicyTargetLayoutComposite composite = parent.addGridLayoutComposite(1, true, compositLayoutData, policyContainer);
		final IEpLayoutData comboLayoutData = parent.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, false, false);
		IPolicyTargetLayoutComposite combocomposite = composite.addGridLayoutComposite(2, true, comboLayoutData, policyContainer);
		combocomposite.addLabel(PriceListManagerMessages.get().PLA_Wizard_Currencies_Label, comboLayoutData, editableContainer);
		currenciesCombo = combocomposite.addComboBox(comboLayoutData, editableContainer);

		final IEpLayoutData tableLayoutData = parent.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.CENTER, false, false);
		modelTableViewer = composite.addTableViewer(false, tableLayoutData, policyContainer, PRICE_LIST_ASSIGNMENT_TABLE);

		/* MUST be called */
		setControl(parent.getSwtComposite());

		// adds the current class as a target
		policyContainer.addTarget(this);
	}

	@Override
	protected void populateControls() {
		priceListDescriptiorsTableViwer.initTable(modelTableViewer, currenciesCombo,
				getModel().getPriceListAssignment().getPriceListDescriptor(), this.getContainer());
	}

	@Override
	public boolean isPageComplete() {
		return null != getModel().getPriceListDescriptor();
	}

	/**
	 * Sets the state by enabling/disabling the selection listener on the table.
	 *
	 * @param state the new state
	 */
	@Override
	public void setState(final EpState state) {
		if (state == EpState.EDITABLE) {
			modelTableViewer.getSwtTableViewer().addSelectionChangedListener(priceListSelectionUpdater);
			priceListDescriptiorsTableViwer.setSelectionEnabled(true);
		} else {
			modelTableViewer.getSwtTableViewer().removeSelectionChangedListener(priceListSelectionUpdater);
			priceListDescriptiorsTableViwer.setSelectionEnabled(false);
		}
	}

	/**
	 * Price list selection updater that makes sure the model has the latest selected price list.
	 */
	private class PriceListSelectionUpdater implements ISelectionChangedListener {
		public void selectionChanged(
				final SelectionChangedEvent event) {
			final IStructuredSelection strSelection = (IStructuredSelection) event
					.getSelection();
			final PriceListDescriptor firstSelected = (PriceListDescriptor) strSelection
					.getFirstElement();
			if (firstSelected == null) {
				return;
			}
			getModel().setPriceListDescriptor(firstSelected);
			priceListDescriptiorsTableViwer.setSelectedItem(firstSelected);
			getWizard().getContainer().updateButtons();
		}
	}

	@Override
	protected boolean isCreateCompositeBlock() {
		return false;
	}
}