/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.dialogs.product;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.service.catalog.ProductLookup;

/**
 * The class to show the product association add/edit dialog.
 */
public class ProductMerchandisingAssociationDialog extends AbstractEpDialog implements IHyperlinkListener {

	private static final int COLUMN_NUM = 3;

	private final String dialogTitle;

	private ProductAssociation productAssociation;

	private IEpDateTimePicker enableDateComp;

	private IEpDateTimePicker disableDateComp;

	private Spinner quantitySpinner;

	private Text productCodeText;

	/** Helps prevent extra (useless) productService calls when updateModels() occurs. */
	private IStatus cachedValidateProductCodeResult;

	private final ProductAssociationType associationType;

	private final boolean editMode;

	private ImageHyperlink hyperlinkProductSearch;

	private Product selectedProd;

	private DataBindingContext bindingContext;

	private final transient ProductLookup productLookup = (ProductLookup) ServiceLocator.getService(ContextIdNames.PRODUCT_LOOKUP);

	private final int nextOrder;

	private final Product product;

	private final Catalog catalog;

	private static final Map<ProductAssociationType, String> ADD_ASSOCIATIONTYPE_DIALOG_MSG = new HashMap<>();

	static {
		ADD_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.CROSS_SELL,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Add_Cross_Sell);
		ADD_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.UP_SELL,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Add_Up_Sell);
		ADD_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.WARRANTY,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Add_Warranty);
		ADD_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.ACCESSORY,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Add_Accessory);
		ADD_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.REPLACEMENT,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Add_Replacement);
	}

	private static final Map<ProductAssociationType, String> EDIT_ASSOCIATIONTYPE_DIALOG_MSG = new HashMap<>();

	static {
		EDIT_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.CROSS_SELL,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Edit_Cross_Sell);
		EDIT_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.UP_SELL,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Edit_Up_Sell);
		EDIT_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.WARRANTY,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Edit_Warranty);
		EDIT_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.ACCESSORY,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Edit_Accessory);
		EDIT_ASSOCIATIONTYPE_DIALOG_MSG.put(ProductAssociationType.REPLACEMENT,
				CatalogMessages.get().ProductMerchandisingAssociationDialog_Edit_Replacement);
	}

	/**
	 * Constructor.
	 * @param parentShell the parent shell
	 * @param associationType the dialog text for association type
	 * @param product the editing product
	 * @param productAssociation the productAssociation to be edit, if it is null, it means add a new product association
	 * @param nextOrder the next order should be used by the new product association
	 * @param catalog the catalog this association belongs to
	 */
	public ProductMerchandisingAssociationDialog(final Shell parentShell, final ProductAssociationType associationType,
				final Product product, final ProductAssociation productAssociation, final int nextOrder, final Catalog catalog) {
		super(parentShell, COLUMN_NUM, false);

		this.productAssociation = productAssociation;

		this.editMode = this.productAssociation != null;

		this.associationType = associationType;

		if (this.editMode) {
			this.dialogTitle = EDIT_ASSOCIATIONTYPE_DIALOG_MSG.get(associationType);
		} else {
			this.dialogTitle = ADD_ASSOCIATIONTYPE_DIALOG_MSG.get(associationType);
		}

		this.nextOrder = nextOrder;
		this.product = product;
		this.catalog = catalog;
	}

	@Override
	protected void createEpButtonsForButtonsBar(final ButtonsBarType buttonsBarType, final Composite parent) {
		if (editMode) {
			createEpOkButton(parent, CatalogMessages.get().ProductMerchandisingAssociationDialog_Set, null);
		} else {
			createEpOkButton(parent, CatalogMessages.get().ProductMerchandisingAssociationDialog_Add, null);
		}
		createEpCancelButton(parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 2, 1);
		dialogComposite.addLabelBoldRequired(CatalogMessages.get().ProductMerchandisingAssociationDialog_Enable_Date, EpState.EDITABLE, labelData);
		enableDateComp = dialogComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE, EpState.EDITABLE, fieldData);

		dialogComposite.addLabelBold(CatalogMessages.get().ProductMerchandisingAssociationDialog_Disable_Date, labelData);
		disableDateComp = dialogComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE, EpState.EDITABLE, fieldData);

		if (isAccessoryType()) {
			dialogComposite.addLabelBold(CatalogMessages.get().ProductMerchandisingAssociationDialog_Default_Qty, labelData);
			quantitySpinner = dialogComposite.addSpinnerField(EpState.EDITABLE, null);
			quantitySpinner.setMinimum(1);
			dialogComposite.addEmptyComponent(null);
		}
		dialogComposite.addLabelBoldRequired(CatalogMessages.get().ProductMerchandisingAssociationDialog_Product_Code, EpState.EDITABLE, labelData);

		productCodeText = dialogComposite.addTextField(
				EpState.EDITABLE, dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
		cachedValidateProductCodeResult = null;

		hyperlinkProductSearch = dialogComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_SEARCH),
				EpState.EDITABLE, null);
		hyperlinkProductSearch.addHyperlinkListener(this);
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return this.productAssociation;
	}

	@Override
	protected void populateControls() {
		if (editMode) {
			enableDateComp.setDate(productAssociation.getStartDate());
			disableDateComp.setDate(productAssociation.getEndDate());
			if (isAccessoryType()) {
				quantitySpinner.setSelection(productAssociation.getDefaultQuantity());
			}
			productCodeText.setText(productAssociation.getTargetProduct().getCode());

		} else {
			this.productAssociation = ServiceLocator.getService(ContextIdNames.PRODUCT_ASSOCIATION);
			this.productAssociation.setCatalog(this.catalog);
			this.productAssociation.setOrdering(this.nextOrder);
			this.productAssociation.setAssociationType(associationType);
			this.productAssociation.setSourceProduct(this.product);
			if (this.associationType == ProductAssociationType.WARRANTY) {
				this.productAssociation.setSourceProductDependent(true);
			} else {
				this.productAssociation.setSourceProductDependent(false);
			}

			enableDateComp.setDate(new Date());
			disableDateComp.setDate(null);
		}
	}

	private boolean isAccessoryType() {
		return associationType == ProductAssociationType.ACCESSORY;
	}

	@Override
	protected String getInitialMessage() {
		return ""; //$NON-NLS-1$
	}

	@Override
	protected String getTitle() {
		return dialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		return dialogTitle;
	}

	public ProductAssociation getProductAssociation() {
		return productAssociation;
	}

	/**
	 * Set the product association.
	 * @param productAssociation the product association
	 */
	public void setProductAssociation(final ProductAssociation productAssociation) {
		this.productAssociation = productAssociation;
	}

	@Override
	public void linkActivated(final HyperlinkEvent event) {
		final ProductFinderDialog productFinderDialog = new ProductFinderDialog(getShell(), this.catalog, true);
		// Only search for products that are active
		productFinderDialog.setSearchActiveProductsOnly(true);

		final int result = productFinderDialog.open();
		if (result == Window.OK) {
			productCodeText.setText(((Product) productFinderDialog.getSelectedObject()).getCode());
		}
	}

	@Override
	public void linkEntered(final HyperlinkEvent event) {
		// Empty on purpose.
	}

	@Override
	public void linkExited(final HyperlinkEvent event) {
		// Empty on purpose.
	}

	@Override
	protected void bindControls() {
		bindingContext = new DataBindingContext();

		if (isAccessoryType()) {
			EpControlBindingProvider.getInstance().bind(
				bindingContext, quantitySpinner, getProductAssociation(), "defaultQuantity", null, null, true); //$NON-NLS-1$
		}

		bindStartAndEndDate();
		bindProductCode();
		EpDialogSupport.create(this, bindingContext);
	}

	private void bindProductCode() {
		productCodeText.addModifyListener((ModifyListener) event -> cachedValidateProductCodeResult = null);

		IValidator productValidator = value -> {
			if (cachedValidateProductCodeResult == null) {
				String productCode = (String) value;
				cachedValidateProductCodeResult = validateProductCode(productCode);
			}
			return cachedValidateProductCodeResult;
		};

		final IValidator validator = new CompoundValidator(new IValidator[] { EpValidatorFactory.PRODUCT_CODE, productValidator });
		EpControlBindingProvider.getInstance().bind(bindingContext, productCodeText, validator, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				getProductAssociation().setTargetProduct(selectedProd);
				return Status.OK_STATUS;
			} }, true
		);
	}

	private IStatus validateProductCode(final String productCode) {
		IStatus productCodeValidateStatus;

		if (productCode.equals(product.getCode())) { //cannot associate to current product itself
			selectedProd = null;
			productCodeValidateStatus =  new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR,

					NLS.bind(CatalogMessages.get().ProductMerchandisingAssociationDialog_Self_Assocation,
					productCode), null);
			return productCodeValidateStatus;
		}

		selectedProd = productLookup.findByGuid(productCode);
		if (selectedProd == null) {  //product should exist
			productCodeValidateStatus =  new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR,

					NLS.bind(CatalogMessages.get().ProductMerchandisingAssociationDialog_Product_Not_Exist,
					productCode), null);
			return productCodeValidateStatus;
		}

		if (!selectedProd.isInCatalog(catalog)) { //product must be in the catalog
			productCodeValidateStatus =  new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR,

					NLS.bind(CatalogMessages.get().ProductMerchandisingAssociationDialog_Product_Not_In_Catalog,
					productCode, catalog.getCode()), null);
			return productCodeValidateStatus;
		}
		
		return Status.OK_STATUS;
	}
	
	private void bindStartAndEndDate() {
		// This will cause all the validators to fire.
		final ModifyListener updateModels = (ModifyListener) event -> bindingContext.updateModels();
		enableDateComp.getSwtText().addModifyListener(updateModels);
		disableDateComp.getSwtText().addModifyListener(updateModels);

		//Enable Date
		enableDateComp.bind(bindingContext, EpValidatorFactory.DATE_REQUIRED, productAssociation, "startDate"); //$NON-NLS-1$
		
		//Disable Date
		final IValidator disableDateValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE,
				EpValidatorFactory.createDisableDateValidator(enableDateComp, disableDateComp) });
		disableDateComp.bind(bindingContext, disableDateValidator, productAssociation, "endDate"); //$NON-NLS-1$
	}
	
	@Override
	protected Image getWindowImage() {
		return null;
	}
}
