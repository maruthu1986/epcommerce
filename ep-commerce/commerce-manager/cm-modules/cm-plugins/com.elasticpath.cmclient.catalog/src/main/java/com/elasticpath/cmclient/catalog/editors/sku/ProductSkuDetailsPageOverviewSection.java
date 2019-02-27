/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.core.dto.catalog.AbstractProductModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * This class implements the section of the Product editor that displays
 * product attribute information.
 */
public class ProductSkuDetailsPageOverviewSection extends AbstractPolicyAwareEditorPageSectionPart
													implements IProductSkuEventListener {


	private final ControlModificationListener controlModificationListener;
	private IEpSkuOverviewViewPart productSkuOverviewViewPart;
	private final ProductSkuDetailsPage productSkuPage;
	private IEpLayoutComposite overviewSectionComposite;
	
	/**
	 * Constructor.
	 * @param formPage parent form page
	 * @param editor the editor where the detail section will be placed
	 */
	public ProductSkuDetailsPageOverviewSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		productSkuPage = (ProductSkuDetailsPage) formPage;
		this.controlModificationListener = editor;
	}

	@Override
	protected String getSectionTitle() {
		return CatalogMessages.get().ProductEditorSingleSkuOverview_Title;
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		PolicyActionContainer partContainer = addPolicyActionContainer("part"); //$NON-NLS-1$

		overviewSectionComposite = CompositeFactory.createTableWrapLayoutComposite(parent, 2, false);

		if (getModel() instanceof ProductModel 
				&& ((ProductModel) getModel()).getProduct() instanceof ProductBundle) {
			productSkuOverviewViewPart = new ProductBundleSkuOverviewViewPart(((AbstractProductModel) getModel()).getProductSku(), this);
		} else {
			productSkuOverviewViewPart = new ProductSkuOverviewViewPart(((AbstractProductModel) getModel()).getProductSku(), this);
		}
		partContainer.addDelegate(productSkuOverviewViewPart);
		productSkuOverviewViewPart.createControls(overviewSectionComposite, null);
		addCompositesToRefresh(overviewSectionComposite.getSwtComposite().getParent());
	}


	@Override
	protected void populateControls() {
		productSkuOverviewViewPart.populateControls();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		productSkuOverviewViewPart.bindControls(bindingContext);
		productSkuOverviewViewPart.setControlModificationListener(controlModificationListener);
	}

	@Override
	public void commit(final boolean onSave) {
		productSkuOverviewViewPart.setValidateSku(true);
		super.commit(onSave);
	}

	@Override
	public void digitalAssetOptionSelected(final boolean digital, final boolean downloadable) {
		if (productSkuPage.isHasDigitalAssetSection()) {
			productSkuPage.getDigitalAssetSection().setDigitalAndDownlodable(digital, downloadable);
			productSkuPage.setDigitalAssetSectionEnabled(downloadable);			
		}
	}

	@Override
	public void shippableOptionSelected(final boolean selected) {
		if (productSkuPage.isHasShippingSection()) {
			productSkuPage.setShippingSectionEnabled(selected);
		}
	}

	@Override
	public void skuCodeChanged(final String skuCodeString) {
		markDirty();
	}
}
