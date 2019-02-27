/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.wizard;

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
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.tableviews.DynamicContentTableViwer;
import com.elasticpath.domain.contentspace.DynamicContent;

/**
 * Class represents second page of {@link DynamicContentDeliveryWizard} wizard.
 * On this page user MUST to select one {@link DynamicContent}.
 * Only one may be selected. 
 */
public class DynamicContentDeliveryWizardDynamicContentSelectPage 
			extends AbstractPolicyAwareWizardPage<DynamicContentDeliveryModelAdapter> implements StateChangeTarget {

	private static final String DYNAMIC_CONTENT_DELIVERY_TABLE = "Dynamic Content Delivery"; //$NON-NLS-1$
	private final DynamicContentTableViwer dynamicContentTableViewer = new DynamicContentTableViwer();
	private final TableSelectionListener tableSelectionListener = new TableSelectionListener();
	
	/**
	 * Constructor.
	 * 
	 * @param pageName - name of the page
	 * @param title - title of the page
	 * @param description - description of the page
	 */
	public DynamicContentDeliveryWizardDynamicContentSelectPage(final String pageName, final String title, final String description) {
		super(2, false, pageName, title, description, new DataBindingContext());
	}

	@Override
	protected void bindControls() {
		// null
	}

	/**
	 * Table selection listener.
	 */
	private class TableSelectionListener implements ISelectionChangedListener {
		@Override
		public void selectionChanged(
				final SelectionChangedEvent event) {
			final IStructuredSelection strSelection = (IStructuredSelection) event
					.getSelection();
			final DynamicContent firstSelectedDC = (DynamicContent) strSelection
					.getFirstElement();
			getModel().setDynamicContent(firstSelectedDC);
			getWizard().getContainer().updateButtons();
		}
	}
	
	@Override
	protected void populateControls() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isPageComplete() {
		return null != getModel().getDynamicContent();
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite parent) {
		PolicyActionContainer dcContainer = addPolicyActionContainer("dcSelectionContainer"); //$NON-NLS-1$
		final IEpLayoutData tableLayoutData = parent.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.CENTER, false, false);
		IEpTableViewer tableViewer = parent.addTableViewer(false, tableLayoutData, dcContainer, DYNAMIC_CONTENT_DELIVERY_TABLE);
		dynamicContentTableViewer.initTable(tableViewer, getModel().getDynamicContent(), this.getContainer());

		/* MUST be called */
		setControl(parent.getSwtComposite());
		dcContainer.addTarget(this);
	}

	@Override
	public void setState(final EpState state) {
		if (state == EpState.EDITABLE) {
			dynamicContentTableViewer.getTableViewer().getSwtTableViewer().addSelectionChangedListener(tableSelectionListener);	
			dynamicContentTableViewer.setSelectionEnabled(true);
		} else {
			dynamicContentTableViewer.getTableViewer().getSwtTableViewer().removeSelectionChangedListener(tableSelectionListener);
			dynamicContentTableViewer.setSelectionEnabled(false);
		}
	}

}