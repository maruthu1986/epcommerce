/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors.price;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;

/** */
public class PriceAdjustmentPage extends AbstractPolicyAwareEditorPage {
	/** Page id. */
	public static final String PRICE_ADJUSTMENT_PAGE_ID = "com.elasticpath.cmclient.catalog.editors.product.PriceAdjustmentPage"; //$NON-NLS-1$

	private static final int HORIZONTAL_SPACING = 15;
	
	private final SelectedElement lastSelectedState;
	
	/**
	 * Constructor.
	 * @param editor editor
	 */
	public PriceAdjustmentPage(final AbstractCmClientFormEditor editor) {
		super(editor, PRICE_ADJUSTMENT_PAGE_ID, CatalogMessages.get().ProductBundlePriceAdjustmentPage_Title, true);
		this.lastSelectedState = new SelectedElement();
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer container = addPolicyActionContainer(PRICE_ADJUSTMENT_PAGE_ID);

		PriceAdjustmentPart priceAdjustmentPart = new PriceAdjustmentPart(editor, this, lastSelectedState);
		addPart(container, managedForm, priceAdjustmentPart);
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// no toolbar is needed
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}
	
	@Override
	public AbstractCmClientFormEditor getEditor() {
		return (AbstractCmClientFormEditor) super.getEditor();
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().ProductBundlePriceAdjustmentPage_Title;
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
