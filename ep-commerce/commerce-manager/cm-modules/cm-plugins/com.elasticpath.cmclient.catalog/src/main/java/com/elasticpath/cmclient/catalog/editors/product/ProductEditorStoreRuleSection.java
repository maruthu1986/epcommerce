/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.Product;

/**
 * This class implements the section of the Product editor that displays basic product information/details.
 */
public class ProductEditorStoreRuleSection extends AbstractPolicyAwareEditorPageSectionPart {

	private final ControlModificationListener controlModificationListener;
	private StoreRulesViewPart storeRulesViewPart;
	private IEpLayoutComposite mainPane;
	
	// Controls
	/**
	 * Constructor.
	 *
	 * @param formPage the Eclipse form page
	 * @param editor the editor where the detail section will be placed
	 *
	 */
	public ProductEditorStoreRuleSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | Section.DESCRIPTION);
		this.controlModificationListener = editor;

	}

	@Override
	protected String getSectionTitle() {
		return CatalogMessages.get().ProductEditorStoreRuleSection_Title;
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {

		PolicyActionContainer partContainer = addPolicyActionContainer("part"); //$NON-NLS-1$
		
		mainPane = CompositeFactory.createGridLayoutComposite(parent, 1, false);

		storeRulesViewPart = new StoreRulesViewPart(getProduct(), false);
		partContainer.addDelegate(storeRulesViewPart);
		storeRulesViewPart.createControls(mainPane, null);
		
		addCompositesToRefresh(mainPane.getSwtComposite().getParent());
	}

	private Product getProduct() {
		return ((ProductModel) getEditor().getModel()).getProduct();
	}

	@Override
	protected String getSectionDescription() {
		return CatalogMessages.get().ProductEditorStoreRuleSection_AllStoresMessage;
	}

	@Override
	protected void populateControls() {
		storeRulesViewPart.populateControls();
	}


	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		storeRulesViewPart.bindControls(bindingContext);
		storeRulesViewPart.setControlModificationListener(controlModificationListener);
	}
}
