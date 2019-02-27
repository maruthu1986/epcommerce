/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.wizards.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.AttributeComparator;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * The class to create Add/Edit Product type wizard's first window.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.GodClass"})
public class ProductTypeAddEditWizardStep1 extends AbstractPolicyAwareWizardPage<ProductType> implements SelectionListener {

	private static final int DIALOG_NUMBER_OF_COLUMN = 1;

	private static final String EMPTY_STRING = "";

	private ProductTypeAttributesDualList attrDualList;

	private ProductTypeCartItemModifierGroupDualList cartItemModifierGroupDualList;

	private List<Attribute> availableAttributes;

	private List<CartItemModifierGroup> availableCartItemModifierGroups;

	private final boolean editMode;

	private Text productTypeNameText;

	private CCombo productTaxCodeCombo;

	private final ProductType productType;

	private Button productTypeMultipleSku;

	private Button productTypeIsDiscountable;

	private List<Attribute> selectedAttributes;

	private List<CartItemModifierGroup> selectedCartItemModifierGroups;

	private List<TaxCode> taxCodeList;

	private final String originalName;

	private final List<ProductType> productTypes;

	private StatePolicy statePolicy;

	private PolicyActionContainer pageOnePolicyContainer;

	private PolicyActionContainer productTypeMultipleSkuPolicyContainer;

	/**
	 * @param pageName     the page name passed in.
	 * @param title        the window title string passed in.
	 * @param productType  the product type passed in. if null. it is add mode, otherwise
	 *                     it is edit mode.
	 * @param editMode     true for editing and false for adding new.
	 * @param productTypes list of existing productTypes
	 */
	protected ProductTypeAddEditWizardStep1(final String pageName, final String title, final ProductType productType, final boolean editMode,
			final List<ProductType> productTypes) {
		super(DIALOG_NUMBER_OF_COLUMN, false, pageName, title,
				CatalogMessages.get().ProductTypeAddEditWizard_ProductInitMsg, new DataBindingContext());
		setDependentObject(productType);
		this.productTypes = productTypes;
		this.editMode = editMode;
		this.productType = productType;
		if (editMode) { // edit mode
			selectedAttributes = getProductTypeAttributes();
			selectedCartItemModifierGroups = getProductTypeCartItemModifierGroups();
			// remember the original name for validation purposes
			originalName = this.productType.getName();
		} else { // add mode
			selectedAttributes = new ArrayList<>();
			originalName = null;

			selectedCartItemModifierGroups = new ArrayList<>();
		}
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		statePolicy.init(getDependentObject());
		applyStatePolicy();
	}

	/**
	 * Apply the relevant state policy.
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
		return "productTypeWizardPage1";
	}

	@Override
	protected void bindControls() {

		final IValidator nameValidator = new CompoundValidator(new IValidator[]{EpValidatorFactory.STRING_255_REQUIRED, value -> {
			final String input = (String) value;

			if (isInUse(productTypes, input)) {
				return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR,
						CatalogMessages.get().ProductTypeAddEditWizard_NameExists_ErrMsg, null);
			}

			return Status.OK_STATUS;
		}});

		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// name
		bindingProvider.bind(getDataBindingContext(), productTypeNameText, getProductType(), "name",
				nameValidator, null, true);

		// tax code
		bindingProvider.bind(getDataBindingContext(), productTaxCodeCombo, EpValidatorFactory.REQUIRED, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						final int selectionIndex = (Integer) value;
						getProductType().setTaxCode(taxCodeList.get(selectionIndex));
						return Status.OK_STATUS;
					}
				}, true);

		// multi sku checkbox
		bindingProvider.bind(getDataBindingContext(), productTypeIsDiscountable, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				getProductType().setExcludedFromDiscount(!productTypeIsDiscountable.getSelection());
				return Status.OK_STATUS;
			}
		}, true);

		// multi sku checkbox
		bindingProvider.bind(getDataBindingContext(), productTypeMultipleSku, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				getProductType().setMultiSku(productTypeMultipleSku.getSelection());
				return Status.OK_STATUS;
			}
		}, true);

		EpWizardPageSupport.create(this, getDataBindingContext());
	}

	/**
	 * Check if the product type is already in use in the database.
	 *
	 * @param input        the product name string the user input.
	 * @param productTypes the list of product types to check against
	 * @return true if the the name is in use, otherwise return false.
	 */
	protected boolean isInUse(final List<ProductType> productTypes, final String input) {
		// Ignore changes back to original name
		if (editMode && input.equals(originalName)) {
			return false;
		}

		// If any of the existing product types has the same name, its in use
		for (final ProductType type : productTypes) {
			if (getProductType() != type && type.getName().equals(input)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite policyComposite) {
		pageOnePolicyContainer = addPolicyActionContainer("productTypeWizardPageOneControls");
		productTypeMultipleSkuPolicyContainer = addPolicyActionContainer("productTypeWizardPageOneMultiSku");

		setDependentObjectForPolicyContainer();

		final IEpLayoutData mainLayoutData = policyComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		final IPolicyTargetLayoutComposite mainComposite = policyComposite.addScrolledGridLayoutComposite(10, false, mainLayoutData,
				pageOnePolicyContainer);

		final IEpLayoutData emptyLineLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 10, 1);

		//fields setup
		final IEpLayoutData nameLabelLayoutData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, true, 2, 1);
		final IEpLayoutData taxCodeLabelLayoutData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, true, 2, 1);
		final IEpLayoutData multipleSkusLabelLayoutData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, true, 3, 1);
		final IEpLayoutData discountableLabelLayoutData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, true, 3, 1);

		final IEpLayoutData nameFieldLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 8, 1);
		final IEpLayoutData taxCodeFieldLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 8, 1);
		final IEpLayoutData multipleSkusFieldLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 7, 1);
		final IEpLayoutData discountableFieldLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 7, 1);

		//dual list setup
		final IEpLayoutData dualListLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 10, 1);

		//Product name area
		mainComposite.addLabelBoldRequired(CatalogMessages.get().ProductTypeAddEditWizard_Name, nameLabelLayoutData, pageOnePolicyContainer);
		productTypeNameText = mainComposite.addTextField(nameFieldLayoutData, pageOnePolicyContainer);

		//Tax code area
		mainComposite.addLabelBoldRequired(CatalogMessages.get().ProductTypeAddEditWizard_DefaultTaxCode,
				taxCodeLabelLayoutData, pageOnePolicyContainer);

		productTaxCodeCombo = mainComposite.addComboBox(taxCodeFieldLayoutData, pageOnePolicyContainer);
		productTaxCodeCombo.addSelectionListener(this);
		mainComposite.addEmptyComponent(emptyLineLayoutData, pageOnePolicyContainer);

		//Dual list area
		final IPolicyTargetLayoutComposite dualListLayoutComposite = mainComposite.addGridLayoutComposite(4, false, dualListLayoutData,
				pageOnePolicyContainer);
		final IEpLayoutData dualListInternalLayoutData = dualListLayoutComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
				true, 4, 1);
		attrDualList = new ProductTypeAttributesDualList(dualListLayoutComposite, pageOnePolicyContainer, getSelectedAttributes(),
				CatalogMessages.get().ProductTypeAddEditWizard_AvailableAttributes,
				CatalogMessages.get().ProductTypeAddEditWizard_AssignedAttributes, dualListInternalLayoutData, productType.getCatalog());

		attrDualList.createControls();
		mainComposite.addEmptyComponent(emptyLineLayoutData, pageOnePolicyContainer);

		//Dual list area Cart Item Modifier Group
		addCartItemModifierGroupDualList(emptyLineLayoutData, dualListLayoutComposite);

		//Check box
		mainComposite.addLabelBold(CatalogMessages.get().ProductTypeAddEditWizard_ProductMultiSku,
				multipleSkusLabelLayoutData, pageOnePolicyContainer);

		productTypeMultipleSku = mainComposite.addCheckBoxButton(EMPTY_STRING, multipleSkusFieldLayoutData, productTypeMultipleSkuPolicyContainer);
		productTypeMultipleSku.addSelectionListener(this);

		mainComposite.addLabelBold(CatalogMessages.get().ProductTypeAddEditWizard_ProductExcludedFromDiscount, discountableLabelLayoutData,
				pageOnePolicyContainer);


		productTypeIsDiscountable = mainComposite.addCheckBoxButton(EMPTY_STRING, discountableFieldLayoutData, pageOnePolicyContainer);
		productTypeIsDiscountable.addSelectionListener(this);

		//final setup
		setControl(policyComposite.getSwtComposite());
	}

	/**
	 * Add the CartItemModifierGroups Dual List.
	 *
	 * @param emptyLineLayoutData the empty layout.
	 * @param dualListLayoutComposite       the dualList composite layout.
	 */
	public void addCartItemModifierGroupDualList(final IEpLayoutData emptyLineLayoutData,
												 final IPolicyTargetLayoutComposite dualListLayoutComposite) {
		final IEpLayoutData dualListInternalLayoutCartItemModifierGroupData = dualListLayoutComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 4, 1);
		cartItemModifierGroupDualList = new ProductTypeCartItemModifierGroupDualList(
				dualListLayoutComposite, pageOnePolicyContainer,
				getSelectedCartItemModifierGroups(), CatalogMessages.get().ProductTypeAddEditWizard_AvailableCartItemModifierGroups,
				CatalogMessages.get().ProductTypeAddEditWizard_AssignedCartItemModifierGroups, dualListInternalLayoutCartItemModifierGroupData,
				productType.getCatalog());

		cartItemModifierGroupDualList.createControls();
	}

	private void setDependentObjectForPolicyContainer() {
		pageOnePolicyContainer.setPolicyDependent(null);
		productTypeMultipleSkuPolicyContainer.setPolicyDependent(null);

		if (editMode) {
			// make sure state policy is set to editable product type
			pageOnePolicyContainer.setPolicyDependent(productType);
			productTypeMultipleSkuPolicyContainer.setPolicyDependent(productType);
		}
	}

	/**
	 * Get the assigned Attribute list.
	 *
	 * @return the assigned attribute list for the given product type.
	 */
	public Collection<Attribute> getAssignedAttributes() {
		return attrDualList.getAssigned();
	}

	/**
	 * Get the assigned Cart Item Modifier Group list.
	 *
	 * @return the assigned cart item modifier group list for the given product type.
	 */
	public Collection<CartItemModifierGroup> getAssignedCartItemModifierGroups() {
		return cartItemModifierGroupDualList.getAssigned();
	}

	/**
	 * @return the available attribute list in the database.
	 */
	public List<Attribute> getAvailableAttributesList() {
		if (availableAttributes == null) {
			final AttributeService attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);
			availableAttributes = attributeService.getProductAttributes();
		}

		return availableAttributes;
	}

	/**
	 *
	 * @return the available attribute list in the database.
	 */
	public List<CartItemModifierGroup> getAvailableCartItemModifierList() {
		return availableCartItemModifierGroups;
	}

	@Override
	public IWizardPage getNextPage() {
		if (productTypeMultipleSku.getSelection()) {
			return super.getNextPage();
		}
		return null;
	}

	private List<Attribute> getProductTypeAttributes() {
		final AttributeGroup attributeGroup = productType.getProductAttributeGroup();
		final List<AttributeGroupAttribute> groupAttList = new ArrayList<>();

		for (final AttributeGroupAttribute groupAttr : attributeGroup.getAttributeGroupAttributes()) {
			groupAttList.add(groupAttr);
		}
		groupAttList.sort(new AttributeComparator());

		selectedAttributes = new ArrayList<>();

		for (final AttributeGroupAttribute groupAttr : groupAttList) {
			selectedAttributes.add(groupAttr.getAttribute());
		}
		return selectedAttributes;
	}

	private List<CartItemModifierGroup> getProductTypeCartItemModifierGroups() {
		final Set<CartItemModifierGroup> cartItemModifierGroups = productType.getCartItemModifierGroups();
		selectedCartItemModifierGroups = new ArrayList<>(cartItemModifierGroups);
		return selectedCartItemModifierGroups;
	}


	/**
	 * The getter method of the product type.
	 *
	 * @return the given product type.
	 */
	public ProductType getProductType() {
		return productType;
	}

	private List<Attribute> getSelectedAttributes() {
		return selectedAttributes;
	}

	private List<CartItemModifierGroup> getSelectedCartItemModifierGroups() {
		return selectedCartItemModifierGroups;
	}

	@Override
	@SuppressWarnings({"PMD.CyclomaticComplexity"})
	protected void populateControls() {
		// Populate taxCode combo
		final TaxCodeService taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);

		final List<TaxCode> codeList = taxCodeService.list();
		taxCodeList = new ArrayList<>();
		productTaxCodeCombo.removeAll();
		for (final TaxCode currTaxCode : codeList) {
			if (currTaxCode.getCode().equalsIgnoreCase("SHIPPING")) {
				continue;
			}
			productTaxCodeCombo.add(currTaxCode.getCode());
			taxCodeList.add(currTaxCode);
			if (editMode && currTaxCode.getUidPk() == productType.getTaxCode().getUidPk()) {
				productTaxCodeCombo.select(productTaxCodeCombo.getItemCount() - 1);
			}
		}

		if (!editMode) { // add mode
			productTaxCodeCombo.select(0);
			getProductType().setTaxCode(taxCodeList.get(productTaxCodeCombo.getSelectionIndex()));
			productTypeIsDiscountable.setSelection(true);
			return;
		}

		// in edit mode do following:

		productTypeNameText.setText(productType.getName());
		productTaxCodeCombo.setText(productType.getTaxCode().toString());

		productTypeMultipleSku.setSelection(productType.isMultiSku());

		productTypeIsDiscountable.setSelection(!productType.isExcludedFromDiscount());
	}

	/**
	 * The setter of the selectedAttribute field.
	 *
	 * @param selectedAttributes the selected Attributes list to be set.
	 */
	public void setSelectedAttributes(final List<Attribute> selectedAttributes) {
		this.selectedAttributes = selectedAttributes;
	}

	/**
	 * The setter of the selectedCartItemModifierGroups field.
	 *
	 * @param selectedCartItemModifierGroups the selected Cart Item Modifier Groups list to be set.
	 */
	public void setSelectedCartItemModifierGroups(final List<CartItemModifierGroup> selectedCartItemModifierGroups) {
		this.selectedCartItemModifierGroups = selectedCartItemModifierGroups;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// do nothing for now.

	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		getContainer().updateButtons();
	}

}
