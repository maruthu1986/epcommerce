/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.dialogs.product;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.cmclient.core.ui.dialog.SkuFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.BundleValidator;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Add/edit Product Constituents to bundle Dialog.
 */
public class ProductBundleConstituentsDialog extends AbstractEpDialog {

	private static final int NUMBER_OF_DIALOG_COLUMNS = 3;

	private static final String DEFAULT_QTY = "1";  //$NON-NLS-1$

	private static final String PRODUCT_COMBO_ITEM = "PRODUCT"; //$NON-NLS-1$

	private static final String SKU_COMBO_ITEM = "SKU"; //$NON-NLS-1$

	private final BundleConstituent bundleConstituent;

	private CCombo typeField;

	private ImageHyperlink guidPickerLink;

	private Text guidField;

	private Text quantityField;

	private final boolean editMode;

	private final ProductBundle rootProductBundle;

	/**
	 * Constructs the {@link ProductBundleConstituentsDialog}.
	 *
	 * @param parentShell the {@link Shell} instance
	 * @param editMode the boolean value that sets the dialog for edit or for create
	 * @param bundleConstituent the {@link BundleConstituent} instance (if it is null then ADD mode, and EDIT mode otherwise)
	 * @param rootProductBundle the {@link ProductBundle} instance
	 */
	public ProductBundleConstituentsDialog(final Shell parentShell, final BundleConstituent bundleConstituent, final boolean editMode,
			final ProductBundle rootProductBundle) {
		super(parentShell, NUMBER_OF_DIALOG_COLUMNS, false);

		this.bundleConstituent = bundleConstituent;
		this.editMode = editMode;
		this.rootProductBundle = rootProductBundle;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.OK, parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false, 2, 1);

		final IEpLayoutData guidFieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false);
		final IEpLayoutData guidPickerLinkData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, false);

		dialogComposite.addLabelBoldRequired("Type", EpState.EDITABLE, labelData); //$NON-NLS-1$
		this.typeField = dialogComposite.addComboBox(EpState.EDITABLE, fieldData);

		dialogComposite.addLabelBoldRequired("Code", EpState.EDITABLE, labelData); //$NON-NLS-1$
		this.guidField = dialogComposite.addTextField(EpState.DISABLED, guidFieldData);
		this.guidPickerLink = dialogComposite.addHyperLinkImage(getGuidPickerLinkIcon(), EpState.EDITABLE, guidPickerLinkData);
		this.guidPickerLink.setEnabled(!editMode);
		this.typeField.setEnabled(!editMode);

		dialogComposite.addLabelBoldRequired("Quantity", EpState.EDITABLE, labelData); //$NON-NLS-1$
		this.quantityField = dialogComposite.addTextField(EpState.EDITABLE, fieldData);
	}

	private Image getGuidPickerLinkIcon() {
		return CatalogImageRegistry.getImage(CatalogImageRegistry.SEARCH);
	}

	@Override
	protected void bindControls() {
		guidPickerLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent event) {
				if (typeField.getText().equals(PRODUCT_COMBO_ITEM)) {
					ProductFinderDialog productFinderDialog = new ProductFinderDialog(getShell(), null, true);
					if (productFinderDialog.open() == Window.OK) {
						guidField.setText(((Product) productFinderDialog.getSelectedObject()).getCode());
						quantityField.setFocus();
					}
				} else {
					SkuFinderDialog skuFinderDialog = new SkuFinderDialog(getShell(), null, true, false);
					if (skuFinderDialog.open() == Window.OK) {
						guidField.setText(((ProductSku) skuFinderDialog.getSelectedObject()).getSkuCode());
						quantityField.setFocus();
					}
				}
			}
		});

		final DataBindingContext dataBindingContext = new DataBindingContext();

		if (!editMode) {
			EpControlBindingProvider.getInstance().bind(dataBindingContext, this.guidField,
					EpValidatorFactory.STRING_255_REQUIRED, null, createSimpleObservableUpdateValueStrategy(), true);
		}


		EpControlBindingProvider.getInstance().bind(dataBindingContext, this.quantityField,
				EpValidatorFactory.POSITIVE_INTEGER_REQUIRED, null, createSimpleObservableUpdateValueStrategy(), true);

		EpDialogSupport.create(this, dataBindingContext);

		EpControlBindingProvider.getInstance().bind(dataBindingContext, this.typeField, EpValidatorFactory.REQUIRED, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
						if (!editMode) {
							guidField.setText(StringUtils.EMPTY);
						}
						return Status.OK_STATUS;
					}
				}, true);
	}

	private ObservableUpdateValueStrategy createSimpleObservableUpdateValueStrategy() {
		return new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return bundleConstituent;
	}

	@Override
	protected void populateControls() { // Read Only
		typeField.add(PRODUCT_COMBO_ITEM);
		typeField.add(SKU_COMBO_ITEM);
		typeField.select(0);
		if (editMode) {
			this.guidField.setText(bundleConstituent.getConstituent().getCode());
			this.quantityField.setText(bundleConstituent.getQuantity().toString());
			if (bundleConstituent.getConstituent().isProductSku()) {
				typeField.select(1);
			}
		} else {
			this.quantityField.setText(DEFAULT_QTY);
		}
		this.quantityField.setFocus();
	}

	@Override
	protected void okPressed() { // actual update of model
		if (typeField.getText().equals(PRODUCT_COMBO_ITEM)) {
			if (getProductConstituent() instanceof ProductBundle && rootProductBundle != null) {
				ProductBundle addedProductBundle = (ProductBundle) getProductConstituent();
				if (addedProductBundle.isCalculated().booleanValue() != rootProductBundle.isCalculated().booleanValue()) {
					MessageDialog.openError(this.getParentShell(), CatalogMessages.get().ProductBundleInvalidPricingDialogTitle,

							NLS.bind(CatalogMessages.get().ProductBundleInvalidPricingDialogMessage,
							new Object[]{CatalogMessages.get().getBundleTypeString(addedProductBundle),
							CatalogMessages.get().getBundleTypeString(rootProductBundle)}));
					return;
				}
			}
			bundleConstituent.setConstituent(getProductConstituent());
		} else {
			bundleConstituent.setConstituent(getProductSkuConstituent());
		}
		bundleConstituent.setQuantity(getQuantity());
		
		// check if the item can be added to the bundle
		if (getBundleValidator().isRecurringChargeItemOnAssignedBundle(rootProductBundle, bundleConstituent.getConstituent())) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), 
				CatalogMessages.get().ProductSaveRecurringChargeOnAssignedBundleErrorTitle,
				CatalogMessages.get().ProductSaveRecurringChargeOnAssignedBundleErrorMsg);
			return;
		}
			
		// should be invoked for proper result;
		super.okPressed();
	}
	
	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return getWindowTitle();
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getWindowTitle() {
		if (editMode) {
			return CatalogMessages.get().ProductBundleEditConstituentsDialog_Title;
		}
		
		return CatalogMessages.get().ProductBundleAddConstituentsDialog_Title;
	}
	
	private Product getProductConstituent() {
		ProductLookup productLookup = ServiceLocator.getService(ContextIdNames.PRODUCT_LOOKUP);
		return productLookup.findByGuid(guidField.getText());
	}

	private ProductSku getProductSkuConstituent() {
		ProductSkuLookup productSkuLookup = ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_LOOKUP);
		return productSkuLookup.findBySkuCode(guidField.getText());
	}

	private Integer getQuantity() {
		return Integer.parseInt(quantityField.getText());
	}

	/**
	 * Get BundleValidator.
	 *  
	 * @return the instance of the price list service
	 */
	protected BundleValidator getBundleValidator() {
		return ServiceLocator.getService("bundleValidator"); //$NON-NLS-1$
	}
}
