/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors;


import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider.SelectionFilter;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.BaseAmountFilterSection;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.BaseAmountSearchSection;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.BaseAmountSection;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.BaseAmountSectionPart;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.DefaultBaseAmountTableProperties;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Base Amount editor page.
 */
public class BaseAmountEditorPage extends AbstractPolicyAwareEditorPage {

	private static final int HORIZONTAL_SPACING = 15;

	private final PriceListEditorController controller;
		
	private final TableSelectionProvider selectionChangedListener;
	
	private final BaseAmountSearchSection baseAmountSearchSection;		
	private final BaseAmountFilterSection baseAmountFilterSection;		
 
	
	
	

	/**
	 * Constructor.
	 * 
	 * @param editor parent
	 * @param controller for working with the model
	 * @param tableSelectionProvider the base amount table selection changed listener 
	 */
	public BaseAmountEditorPage(final AbstractCmClientFormEditor editor, final PriceListEditorController controller, 
			final TableSelectionProvider tableSelectionProvider) {
		super(editor, BaseAmountEditorPage.class.getName(),	PriceListManagerMessages.get().BaseAmountEditorPage_Title);

		this.controller = controller;
		this.selectionChangedListener = tableSelectionProvider;
		baseAmountSearchSection = new BaseAmountSearchSection(controller);		
		baseAmountFilterSection = new BaseAmountFilterSection(controller);		
		
		
		//TODO - This probably should be removed because of new way of objects handling
		this.selectionChangedListener.setSelectionFilter(new SelectionFilter() {

			public boolean isApplicable(final ISelection selection, final Object source) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				return !controller.isNewlyAdded((BaseAmountDTO) structuredSelection.getFirstElement());
			}
		});
		
		
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer container = addPolicyActionContainer("priceListBaseAmountEditorPage"); //$NON-NLS-1$

		final BaseAmountSection baseAmountSection = new BaseAmountSection(editor, 
				controller, selectionChangedListener, new DefaultBaseAmountTableProperties());		
		
		final BaseAmountSectionPart formPart = new BaseAmountSectionPart(this, 
				editor, 
				baseAmountSection,
				baseAmountSearchSection,
				baseAmountFilterSection);
		
		managedForm.addPart(formPart);

		
		// delegate the call to apply the policy when needed
		container.addTarget(state -> baseAmountSection.reApplyStatePolicy());

		getCustomPageData().put("selectionChangedListener", this.selectionChangedListener);
		addExtensionEditorSections(editor, managedForm, PriceListManagerPlugin.PLUGIN_ID, this.getClass().getSimpleName());
		
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// Empty for now
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return PriceListManagerMessages.get().BaseAmount_Title;
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
