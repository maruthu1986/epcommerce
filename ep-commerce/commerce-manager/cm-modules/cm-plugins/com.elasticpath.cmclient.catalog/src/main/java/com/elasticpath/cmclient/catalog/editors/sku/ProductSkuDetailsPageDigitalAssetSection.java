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
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.core.dto.catalog.AbstractProductModel;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * This class implements the section of the Product editor that displays product attribute information.
 */
public class ProductSkuDetailsPageDigitalAssetSection extends AbstractPolicyAwareEditorPageSectionPart {

	private final ProductSku productSku;

	private final DigitalAsset digitalAsset;

	private final AbstractCmClientFormEditor controlModificationListener;

	private final ProductSkuDigitalAssetViewPart digitalAssetViewPart;

	private boolean digital;
	
	private IEpLayoutComposite digAssetComposite;

	/**
	 * Constructor.
	 *
	 * @param formPage parent form page
	 * @param editor the editor where the detail section will be placed
	 */
	public ProductSkuDetailsPageDigitalAssetSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.controlModificationListener = editor;
		productSku = ((AbstractProductModel) getModel()).getProductSku();
		if (productSku.getDigitalAsset() == null) {
			this.digitalAsset = ServiceLocator.getService(ContextIdNames.DIGITAL_ASSET);
		} else {
			this.digitalAsset = productSku.getDigitalAsset();
		}
		digitalAssetViewPart = new ProductSkuDigitalAssetViewPart(productSku, digitalAsset);
	}

	@Override
	protected String getSectionTitle() {
		return CatalogMessages.get().ProductEditorSingleSkuDigAsset_Title;
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		PolicyActionContainer partContainer = addPolicyActionContainer("part"); //$NON-NLS-1$
		partContainer.addDelegate(digitalAssetViewPart);

		final int numColumns = 3;
		digAssetComposite = CompositeFactory.createGridLayoutComposite(parent, numColumns, false);

		digitalAssetViewPart.createControls(digAssetComposite, digAssetComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.FILL, true, false));
		
		addCompositesToRefresh(digAssetComposite.getSwtComposite().getParent());
	}

	@Override
	protected void populateControls() {
		digitalAssetViewPart.populateControls();

	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		digitalAssetViewPart.bindControls(bindingContext);
		digitalAssetViewPart.setControlModificationListener(controlModificationListener);
	}


	@Override
	public void commit(final boolean onSave) {
		super.commit(onSave);
		if (onSave) {
			setProductSkuDigital();
		}
	}

	/**
	 * Sets the productSku's digital section.
	 */
	void setProductSkuDigital() {
		productSku.setDigital(digital);
		if (digital) {				
			productSku.setDigitalAsset(digitalAsset);
		} else {
			productSku.setDigitalAsset(null);
		}
	}
	
	/**
	 * Sets a flag that the checkbox is selected.
	 * Control mark parent editors as dirty when selected. 
	 * No need to fire events here.
	 *
	 * @param isDigital true if selected
	 * @param isDownloadable true if downlodable
	 */
	void setDigitalAndDownlodable(final boolean isDigital, final boolean isDownloadable) {
		this.digital = isDigital;
		digitalAssetViewPart.setTextValidationEnabled(isDownloadable);
		if (getManagedForm() != null) {	//this is an editor
			setProductSkuDigital();
		}
	}
}
