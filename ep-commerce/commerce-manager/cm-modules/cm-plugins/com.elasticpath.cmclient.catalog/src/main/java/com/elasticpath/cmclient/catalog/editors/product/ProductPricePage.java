/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.price.baseamounts.PricingSectionState;
import com.elasticpath.cmclient.catalog.editors.price.baseamounts.ProductEditorPricingSection;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;

/**
 * This page of the product editor displays product details. Pages are responsible for creating the sections that belong in those pages and laying
 * them out.
 */
public class ProductPricePage extends AbstractPolicyAwareEditorPage  {

	private static final String CONTAINER_NAME = "priceListBaseAmountEditorPage";  //$NON-NLS-1$
	private ProductEditorPricingSection pricingSection;
	private final TableSelectionProvider baseAmountTableSelectionProvider;
	private PricingSectionState pricingSectionState;
	
	private static final int HORIZONTAL_SPACING = 15;
	
	/**
	 * Constructor.
	 * 
	 * @param partId the unique part id
	 * @param editor the form editor
	 * @param baseAmountTableSelectionProvider table selection provider
	 */
	public ProductPricePage(final String partId, final AbstractCmClientFormEditor editor, 
			final TableSelectionProvider baseAmountTableSelectionProvider) {
		super(editor, partId, CatalogMessages.get().ProductPricePage_Title, true);
		this.baseAmountTableSelectionProvider = baseAmountTableSelectionProvider;
		this.pricingSectionState = new PricingSectionState();
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer container = addPolicyActionContainer("productPriceBaseAmountEditorPage"); //$NON-NLS-1$
		pricingSection =  new ProductEditorPricingSection(this, editor, container, baseAmountTableSelectionProvider, 
				pricingSectionState, getSelectedLocale());		 
		addPart(container, managedForm, pricingSection);
		getCustomPageData().put("baseAmountTableSelectionProvider", this.baseAmountTableSelectionProvider);
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
	}
	
	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().ProductPricePage_Title;
	}


	public String getTargetIdentifier() {
		return CONTAINER_NAME;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}
	
	/**
	 * Performs model save operation.
	 */
	public void saveModel() {
		if (pricingSection != null) {
			pricingSectionState = pricingSection.getState();
			if (pricingSectionState.getSelectedPL() != null) {
				pricingSection.saveModel();
			}
		}
	}
	
	@Override
	protected Layout getLayout() {
		GridLayout layout = new GridLayout(getFormColumnsCount(), true);
		layout.marginLeft = LEFT_MARGIN;
		layout.marginRight = RIGHT_MARGIN;
		layout.horizontalSpacing = HORIZONTAL_SPACING;
		return layout;
	}
}
