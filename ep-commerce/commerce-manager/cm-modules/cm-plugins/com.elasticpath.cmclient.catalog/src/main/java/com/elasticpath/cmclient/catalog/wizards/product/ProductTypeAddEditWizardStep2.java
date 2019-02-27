/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.wizards.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.util.AttributeComparator;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;

/**
 * The class to create Add/Edit Product type wizard's second window.
 */
public class ProductTypeAddEditWizardStep2 extends AbstractPolicyAwareWizardPage<ProductType> {

	private static final int DUAL_LIST_HEIGHT = 300;

	private final ProductType productType;

	private List<Attribute> selectedSkuAttributes;

	private List<SkuOption> selectedSkuOptions;

	private ProductTypeSkuAttributesDualList skuAttributesDualList;

	private SkuOptionsDualList skuOptionsDualList;

	private StatePolicy statePolicy;

	private PolicyActionContainer pageTwoPolicyActionContainer;

	private final boolean editMode;

	/**
	 * @param pageName
	 *            the page name passed in.
	 * @param title
	 *            the window title string passed in.
	 * @param productType
	 *            the product type passed in. if null. it is add mode, otherwise
	 *            it is edit mode.
	 * @param editMode
	 *            true for editing and false for adding new.
	 */
	protected ProductTypeAddEditWizardStep2(final String pageName, final String title, final ProductType productType, final boolean editMode) {
		super(2, false, pageName, title,
				CatalogMessages.get().ProductTypeAddEditWizard_SkuInitMsg, new DataBindingContext());
		this.productType = productType;
		this.editMode = editMode;
		if (editMode) {
			selectedSkuAttributes = getProductTypeSkuAttributes();
			selectedSkuOptions = getProductTypeSkuOptions();
		} else {
			selectedSkuAttributes = new ArrayList<>();
			selectedSkuOptions = new ArrayList<>();
		}
	}

	private List<SkuOption> getProductTypeSkuOptions() {
		final Set<SkuOption> skuOptionSet = productType.getSkuOptions();
		selectedSkuOptions = new ArrayList<>(skuOptionSet);
		return selectedSkuOptions;
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite policyComposite) {
		pageTwoPolicyActionContainer = addPolicyActionContainer("productTypeWizardPageTwoControls"); //$NON-NLS-1$
		setDependentObjectForPolicyContainer();
		final IEpLayoutData layoutData = policyComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		final IPolicyTargetLayoutComposite mainComposite = policyComposite.addGridLayoutComposite(1, false, layoutData,
				pageTwoPolicyActionContainer);
		final IEpLayoutData data = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		final Catalog catalog = productType.getCatalog();

		skuOptionsDualList = new SkuOptionsDualList(mainComposite, data, pageTwoPolicyActionContainer, selectedSkuOptions, catalog);
		skuOptionsDualList.createControls();
		skuOptionsDualList.setPreferredHeight(DUAL_LIST_HEIGHT);

		skuAttributesDualList = new ProductTypeSkuAttributesDualList(mainComposite, pageTwoPolicyActionContainer, selectedSkuAttributes,
				CatalogMessages.get().ProductTypeAddEditWizard_AvailSkuAtt,
				CatalogMessages.get().ProductTypeAddEditWizard_SelectSkuAtt, data, catalog);
		skuAttributesDualList.createControls();
		skuAttributesDualList.setPreferredHeight(DUAL_LIST_HEIGHT);

		setControl(policyComposite.getSwtComposite());

		populateControls();
	}

	private void setDependentObjectForPolicyContainer() {
		pageTwoPolicyActionContainer.setPolicyDependent(null);

		if (editMode) {
			// make sure state policy is set to editable product type
			pageTwoPolicyActionContainer.setPolicyDependent(productType);
		}
	}

	/**
	 * The getter of the attribute selectedSkuOptions.
	 *
	 * @return the selected SkuOption list of the dual list box.
	 */
	public List<SkuOption> getAssignedSkuOptions() {
		return (List<SkuOption>) skuOptionsDualList.getAssigned();
	}

	@Override
	protected void populateControls() {
		// do nothing for now.
	}

	private List<Attribute> getProductTypeSkuAttributes() {
		final AttributeGroup attributeGroup = productType.getSkuAttributeGroup();
		final List<AttributeGroupAttribute> groupAttList = new ArrayList<>();

		for (final AttributeGroupAttribute groupAttr : attributeGroup.getAttributeGroupAttributes()) {
			groupAttList.add(groupAttr);
		}
		Collections.sort(groupAttList, new AttributeComparator());

		selectedSkuAttributes = new ArrayList<>();

		for (final AttributeGroupAttribute groupAttr : groupAttList) {
			selectedSkuAttributes.add(groupAttr.getAttribute());
		}
		return selectedSkuAttributes;
	}

	/**
	 * The getter of the attribute selectedSkuAttribute.
	 *
	 * @return the selected Sku Attribute list of the dual list box.
	 */
	public Collection<Attribute> getAssignedSkuAttributes() {
		return skuAttributesDualList.getAssigned();
	}

	@Override
	protected void bindControls() {
		// do nothing for now.
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		statePolicy.init(getDependentObject());
		applyStatePolicy();
	}

	/**
	 * Apply state policy.
	 */
	public void applyStatePolicy() {
		if (statePolicy != null) {
			for (final PolicyActionContainer container : getPolicyActionContainers().values()) {
				statePolicy.apply(container);
			}
		}
	}

	/**
	 * @return the state policy target
	 */
	public String getTargetIdentifier() {
		return "productTypeWizardPage2"; //$NON-NLS-1$
	}

	@Override
	protected boolean isCreateCompositeBlock() {
		return false;
	}

}