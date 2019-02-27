/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.dialogs;

import java.math.BigDecimal;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.conversion.EpPriceConverter;
import com.elasticpath.cmclient.core.helpers.BaseAmountDTOCreator;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.cmclient.core.ui.dialog.SkuFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerImageRegistry;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.pricelistmanager.model.impl.BaseAmountType;
import com.elasticpath.cmclient.pricelistmanager.validators.ListPriceValidator;
import com.elasticpath.cmclient.pricelistmanager.validators.SalePriceValidator;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.RandomGuid;
import com.elasticpath.domain.pricing.BaseAmount;

/**
 * Add/edit base amount Dialog.
 */
@SuppressWarnings({"PMD.TooManyFields", "PMD.TooManyMethods", "PMD.GodClass"})
public class BaseAmountDialog extends AbstractPolicyAwareDialog implements ObjectGuidReceiver {

	private static final int MIN_PICKER_ICON_WIDTH = 50;

	private static final int LAYOUT_COLUMN_NUMBER = 1;
	private static final int MAIN_COMPOSITE_WIDTH = 450;
	private static final int MAIN_COMPOSITE_COLUMNS = 3;

	private PriceListEditorController priceListController;

	private BaseAmountDTO baseAmountDto;

	private BaseAmountDTO copyBaseAmountDTO;

	private Text guidField;

	private ImageHyperlink guidPickerLink;

	private CCombo typeField;

	private Spinner qtySpinner;

	private Text listValueField;

	private Text saleValueField;

	/** The data binding context. */
	private final DataBindingContext dataBindingContext;

	private boolean editMode;

	private PolicyActionContainer objectDataContainer;

	private PolicyActionContainer pricesContainer;

	private EpValueBinding guidBinding;

	private EpValueBinding qtyBinding;

	private EpValueBinding listPriceBinding;

	private EpValueBinding salePriceBinding;

	private IStatus guidValidationStatus;

	private IStatus qtyValidationStatus;

	private boolean showProductPane = true;

	private String windowTitle = PriceListManagerMessages.get().BaseAmountDialog_Title;

	private String dialogTitle = PriceListManagerMessages.get().BaseAmountDialog_Title;

	private boolean hideListValue;


	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
	}

	/**
	 * Constructs the Base Amount dialog.
	 */
	public BaseAmountDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), LAYOUT_COLUMN_NUMBER, false);
		this.dataBindingContext = new DataBindingContext();
	}

	/**
	 * Constructs the Base Amount dialog.
	 *
	 * @param baseAmountDTO - the base amount to edit
	 * @param controller - the controller
	 * @param showProductPane - if false, hides product picker section. True by default.
	 * @param editMode - is edit mode activated
	 * @param windowTitle - window title
	 * @param dialogTitle - dialog title
	 * @param hideListValue - if true, list value will not be shown on dialog open
	 */
	public BaseAmountDialog(final boolean editMode, final BaseAmountDTO baseAmountDTO, final PriceListEditorController controller,
			final boolean showProductPane, final String windowTitle, final String dialogTitle, final boolean hideListValue) {
		this(editMode, baseAmountDTO, controller);
		this.showProductPane = showProductPane;
		this.windowTitle = windowTitle;
		this.dialogTitle = dialogTitle;
		this.hideListValue = hideListValue;
	}

	/**
	 * Constructs the Base Amount dialog.
	 *
	 * @param baseAmountDTO the base amount to edit
	 * @param controller the controller
	 * @param showProductPane if false, hides product picker section. True by default.
	 * @param editMode is edit mode activated
	 * @param windowTitle the windowTitle
	 * @param dialogTitle the dialogTitle
	 */
	public BaseAmountDialog(final boolean editMode, final BaseAmountDTO baseAmountDTO, final PriceListEditorController controller,
			final boolean showProductPane, final String windowTitle, final String dialogTitle) {
		this(editMode, baseAmountDTO, controller);
		this.showProductPane = showProductPane;
		this.windowTitle = windowTitle;
		this.dialogTitle = dialogTitle;
	}


	/**
	 * Constructs the Base Amount dialog.
	 *
	 * @param baseAmountDTO the base amount to edit
	 * @param controller the controller
	 * @param showProductPane if false, hides product picker section. True by default.
	 * @param editMode is edit mode activated
	 */
	public BaseAmountDialog(final boolean editMode, final BaseAmountDTO baseAmountDTO, final PriceListEditorController controller,
			final boolean showProductPane) {
		this(editMode, baseAmountDTO, controller);
		this.showProductPane = showProductPane;
	}

	/**
	 * Constructs the Base Amount dialog.
	 *
	 * @param editMode is edit mode activated
	 * @param baseAmountDTO the base amount to edit
	 * @param controller the controller
	 */
	public BaseAmountDialog(final boolean editMode, final BaseAmountDTO baseAmountDTO, final PriceListEditorController controller) {
		this();
		this.editMode = editMode;
		this.priceListController = controller;
		this.baseAmountDto = baseAmountDTO;
		this.copyBaseAmountDTO = BaseAmountDTOCreator.createModel(baseAmountDTO);
	}

	@Override
	protected void populateControls() {
		if (showProductPane) {
			populateBaseAmountTypes();
		}
		if (baseAmountDto.getObjectType() != null && typeField != null) {
			this.typeField.setText(baseAmountDto.getObjectType());
		}

		if (editMode) {
			if (guidField != null) {
				this.guidField.setText(baseAmountDto.getObjectGuid());
			}
			// list price
			if (baseAmountDto.getListValue() != null && !hideListValue) {
				this.listValueField.setText(baseAmountDto.getListValue().toString());
			}
			// sale price
			if (baseAmountDto.getSaleValue() != null) {
				this.saleValueField.setText(baseAmountDto.getSaleValue().toString());
			}
		}
	}

	private void populateBaseAmountTypes() {
		for (BaseAmountType amountType : BaseAmountType.values()) {
			this.typeField.add(amountType.getType());
		}
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.OK, parent);
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {
		objectDataContainer = addPolicyActionContainer("objectDataContainer");

		IPolicyTargetLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(MAIN_COMPOSITE_COLUMNS, false,
				dialogComposite.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.FILL, true, true),
				objectDataContainer);
		((GridData) mainComposite.getSwtComposite().getLayoutData()).widthHint = MAIN_COMPOSITE_WIDTH;

		createDialogContentHook(mainComposite);

		final IEpLayoutData labelData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false, 2, 1);

		final IEpLayoutData guidFieldData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false);
		final IEpLayoutData guidPickerLinkData = mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false);
		((GridData) guidPickerLinkData.getSwtLayoutData()).minimumWidth = MIN_PICKER_ICON_WIDTH;

		objectDataContainer = addPolicyActionContainer("objectDataContainer"); //$NON-NLS-1$

		if (editMode) {
			objectDataContainer.setPolicyDependent(baseAmountDto);
		}

		if (showProductPane) {
			mainComposite.addLabelBoldRequired(PriceListManagerMessages.get().BaseAmount_ObjectType, labelData, objectDataContainer);
			this.typeField = mainComposite.addComboBox(fieldData, objectDataContainer);

			mainComposite.addLabelBoldRequired(PriceListManagerMessages.get().BaseAmount_ObjectCode, labelData, objectDataContainer);

			this.guidField = mainComposite.addTextField(guidFieldData, objectDataContainer);
			this.guidPickerLink = mainComposite.addHyperLinkImage(getGuidPickerLinkIcon(), guidPickerLinkData, objectDataContainer);
		}

		pricesContainer = addPolicyActionContainer("pricesContainer"); //$NON-NLS-1$

		if (editMode) {
			pricesContainer.setPolicyDependent(baseAmountDto);
		}

		createQuantityDialogContent(mainComposite, labelData, fieldData);

		mainComposite.addLabelBoldRequired(PriceListManagerMessages.get().BaseAmount_ListPrice, labelData, pricesContainer);
		this.listValueField = mainComposite.addTextField(fieldData, pricesContainer);

		mainComposite.addLabelBold(PriceListManagerMessages.get().BaseAmount_SalePrice, labelData, pricesContainer);
		this.saleValueField = mainComposite.addTextField(fieldData, pricesContainer);
	}

	/**
	 * Creates the quantity spinner dialog content.
	 *
	 * @param dialogComposite parent EP layout composite
	 * @param labelData the label layout data
	 * @param fieldData the field layout data
	 */
	protected void createQuantityDialogContent(final IPolicyTargetLayoutComposite dialogComposite, final IEpLayoutData labelData,
			final IEpLayoutData fieldData) {
		if (editMode) {
			dialogComposite.addLabelBold(PriceListManagerMessages.get().BaseAmount_Quantity, labelData, pricesContainer);
			dialogComposite.addLabel(String.valueOf(this.baseAmountDto.getQuantity().intValue()), fieldData, pricesContainer);
		} else {
			dialogComposite.addLabelBoldRequired(PriceListManagerMessages.get().BaseAmount_Quantity, labelData, pricesContainer);
			this.qtySpinner = dialogComposite.addSpinnerField(fieldData, pricesContainer);
			qtySpinner.setMinimum(1);
			qtySpinner.setMaximum(BaseAmount.MAX_QTY);
			this.baseAmountDto.setQuantity(BigDecimal.valueOf(qtySpinner.getSelection()));
		}
	}

	/**
	 * Override this method if you want to add more content in your base class.
	 * @param dialogComposite THE dialogComposite
	 * */
	protected void createDialogContentHook(final IPolicyTargetLayoutComposite dialogComposite) {
		//use this method in base classes
	}

	/**
	 * Gets the picker guid icon.
	 * @return the picker guid icon
	 * */
	protected Image getGuidPickerLinkIcon() {
		if (editMode) {
			return PriceListManagerImageRegistry.getImage(PriceListManagerImageRegistry.IMAGE_SEARCH_DISABLED);
		}
		return PriceListManagerImageRegistry.getImage(PriceListManagerImageRegistry.IMAGE_SEARCH);
	}

	@Override
	protected void okPressed() {
		if (editMode && copyBaseAmountDTO.equals(this.baseAmountDto)) {
			super.cancelPressed(); // nothing changed. return cancel to abort.
			return;
		}
		if (performSaveOperation(this.baseAmountDto)) {
			super.okPressed();
		}
	}

	/**
	 * @param baseAmount the base amount to save
	 * @return whether the operation was successful
	 */
	protected boolean performSaveOperation(final BaseAmountDTO baseAmount) {
		if (exists()) {
			MessageDialog.openError(this.getShell(),
					PriceListManagerMessages.get().BaseAmountDialogError_Title, PriceListManagerMessages.get().BaseAmountExists);
			return false;
		}
		return true;
	}

	/**
	 * Check if BaseAmountDTO already in database or already marked for addition.
	 *
	 * @return true if BaseAmountDTO exists.
	 */
	private boolean exists() {
		return !this.priceListController.isUniqueBaseAmountDTO(this.baseAmountDto, this.baseAmountDto);
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return dialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		return windowTitle;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected void bindControls() {
		// guid & quantity share same tier validation algorithm which is synchnonized by triggering manual
		// update on bindings. We need to prevent this synchronization initially, therefore we need to
		// place bindings' references into temp variables until all is initialized.
		EpValueBinding guidTempBinding = null;
		EpValueBinding qtyTempBinding = null;

		if (showProductPane) {

			DuplicateBaseAmountValidator duplicateBaseAmountValidator = new DuplicateBaseAmountValidator() {
				@Override
				public IStatus validate(final Object value) {
					final String stringValue = (String) value;
					BaseAmountDialog.this.baseAmountDto.setObjectGuid(stringValue);
					guidValidationStatus = super.validate(value);
					if (qtyBinding != null && Status.OK_STATUS.equals(guidValidationStatus)
							&& !Status.OK_STATUS.equals(qtyValidationStatus)) {
						qtyBinding.getBinding().updateTargetToModel();
					}
					return guidValidationStatus;
				}
			};

			guidTempBinding = EpControlBindingProvider.getInstance().bind(dataBindingContext, this.guidField, this.baseAmountDto,
					"objectGuid", //$NON-NLS-1$
					new CompoundValidator(
							new IValidator[] {
									EpValidatorFactory.STRING_255_REQUIRED,
									duplicateBaseAmountValidator,
									getAdditionalGuidValidator(),
							}
					), null, true);

			bindPickerLink();

			EpControlBindingProvider.getInstance().bind(dataBindingContext, this.typeField, EpValidatorFactory.REQUIRED, null,
					new ObservableUpdateValueStrategy() {
						@Override
						protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
							if (!editMode) {
								baseAmountDto.setObjectType(typeField.getText());
								guidField.setText(""); //$NON-NLS-1$
							}
							return Status.OK_STATUS;
						}
					}, true);
		}

		// Min Order Quantity
		if (this.qtySpinner != null) {
			qtyTempBinding = EpControlBindingProvider.getInstance().bind(dataBindingContext, this.qtySpinner, this.baseAmountDto,
					"quantity", //$NON-NLS-1$
					new DuplicateBaseAmountValidator() {
						@Override
						public IStatus validate(final Object value) {
							final Integer intValue = (Integer) value;
							BaseAmountDialog.this.baseAmountDto.setQuantity(new BigDecimal(intValue).setScale(2));
							qtyValidationStatus = super.validate(value);
							if (guidBinding != null && Status.OK_STATUS.equals(qtyValidationStatus)
									&& !Status.OK_STATUS.equals(guidValidationStatus)) {
								guidBinding.getBinding().updateTargetToModel();
							}
							return qtyValidationStatus;
						}
					}, null, true);
		}

		SalePriceValidator salePriceValidator = new SalePriceValidator(listValueField);
		ListPriceValidator listPriceValidator = new ListPriceValidator(saleValueField);

		listPriceBinding = EpControlBindingProvider.getInstance().bind(dataBindingContext, this.listValueField,
				this.baseAmountDto, "listValue", listPriceValidator, new EpPriceConverter(), true); //$NON-NLS-1$

		salePriceBinding = EpControlBindingProvider.getInstance().bind(dataBindingContext, this.saleValueField,
				this.baseAmountDto, "saleValue", salePriceValidator, new EpPriceConverter(), true); //$NON-NLS-1$

		salePriceValidator.init(listPriceValidator, listPriceBinding);
		listPriceValidator.init(salePriceValidator, salePriceBinding);

		// see comment in declaration of *TempBinding variables.
		this.guidBinding = guidTempBinding;
		this.qtyBinding = qtyTempBinding;

		EpDialogSupport.create(this, dataBindingContext);
	}

	/**
	 * Adds validator to GUID field.
	 * @return codeValidator the validator
	 */
	protected IValidator getAdditionalGuidValidator() {
		//always returns OK.
		//base classes can override this method to implement a code validator
		//you will use this if you want to make sure the code exists in the system (this is not provided in this class)
		return obj -> Status.OK_STATUS;
	}

	/**
	 * Adds the the picker restriction. This is introduced for the base classes to override and provide additional restrictions.
	 *
	 */
	protected void bindPickerLink() {
		guidPickerLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent event) {
				BaseAmountType baseAmountType = BaseAmountType.findByType(baseAmountDto.getObjectType());
				switch (baseAmountType) {
				case PRODUCT:
					ProductFinderDialog productFinderDialog = new ProductFinderDialog(null, null, false);
					if (productFinderDialog.open() == Window.OK) {
						populateProduct((Product) productFinderDialog.getSelectedObject());
					}
					break;
				case SKU:
					SkuFinderDialog skuFinderDialog = new SkuFinderDialog(null, null, false);
					if (skuFinderDialog.open() == Window.OK) {
						Object selectedObject = skuFinderDialog.getSelectedObject();
						populateSku(selectedObject);
					}
					break;
				default:
				}
			}
		});
	}

	/**
	 * Checks that base amount is not a duplicate.
	 */
	private class DuplicateBaseAmountValidator implements IValidator {

		/**
		 * Validates the base amount.
		 *
		 * @param value the value to validate
		 * @return the status
		 **/
		@Override
		public IStatus validate(final Object value) {
			if (priceListController != null && priceListController.isPriceTierExists(BaseAmountDialog.this.baseAmountDto)) {
				final String duplicatePriceTierMsg =
					NLS.bind(PriceListManagerMessages.get().AddEditPriceTierDialog_Duplicate_PriceTier,
					new Object[]{value});
				return new Status(IStatus.ERROR, PriceListManagerPlugin.PLUGIN_ID, IStatus.ERROR, duplicatePriceTierMsg, null);
			}
			updateButtons();
			return Status.OK_STATUS;
		}
	}

	/**
	 * Populates product.
	 *
	 * @param product the product
	 **/
	protected void populateProduct(final Product product) {
		guidField.setText(product.getCode());
	}

	/**
	 * Populates product sku.
	 *
	 * @param selectedObject the selectedObject
	 **/
	protected void populateSku(final Object selectedObject) {
		if (selectedObject instanceof ProductSku) {
			ProductSku sku = (ProductSku) selectedObject;
			guidField.setText(sku.getSkuCode());
		} else if (selectedObject instanceof Product) {
			ProductSku sku = ((Product) selectedObject).getDefaultSku();
			guidField.setText(sku.getSkuCode());
		}
	}

	@Override
	public boolean isComplete() {
		if (priceListController != null) {
			return super.isComplete() && !exists();
		}
		return super.isComplete();
	}

	@Override
	protected String getPluginId() {
		return PriceListManagerPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return baseAmountDto;
	}

	@Override
	protected Object getDependentObject() {
		return this.baseAmountDto;
	}

	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return pricesContainer;
	}

	@Override
	protected void refreshLayout() {
		// nothing to refresh
	}

	@Override
	public String getTargetIdentifier() {
		return "baseAmountDialog"; //$NON-NLS-1$
	}

	@Override
	public void setObjectGuid(final String objectGuid) {
		if (objectGuid == null) {
			this.baseAmountDto = createNewBaseAmount();
			this.editMode = false;
		} else {
			this.baseAmountDto = getPriceListService().getBaseAmount(objectGuid);
			if (baseAmountDto == null) {
				throw new IllegalArgumentException(
					NLS.bind(CoreMessages.get().Given_Object_Not_Exist,
					new String[]{"Base Amount", objectGuid})); //$NON-NLS-1$
			}
			this.editMode = true;
			this.baseAmountDto = BaseAmountDTOCreator.createModel(this.baseAmountDto);
			this.copyBaseAmountDTO = BaseAmountDTOCreator.createModel(baseAmountDto);
		}
	}

	private BaseAmountDTO createNewBaseAmount() {
		BaseAmountDTO baseAmount = BaseAmountDTOCreator.createModel();
		// set new DTO GUID
		RandomGuid randomGuid = ServiceLocator.getService(ContextIdNames.RANDOM_GUID);
		baseAmount.setGuid(randomGuid.toString());
		baseAmount.setPriceListDescriptorGuid(priceListController.getPriceListDescriptor().getGuid());
		return baseAmount;
	}

	/**
	 * Get price list service.
	 * 
	 * @return the instance of the price list service
	 */
	protected PriceListService getPriceListService() {
		return ServiceLocator.getService(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);
	}

	/**
	 * Get base amount DTO.
	 * 
	 * @return the base amount DTO
	 */
	public BaseAmountDTO getBaseAmountDto() {
		return baseAmountDto;
	}

	/**
	 * Get DataBindingContext.
	 * 
	 * @return the dataBindingContext
	 */
	@Override
	protected DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	/**
	 * Get guidPickerLink.
	 * 
	 * @return the guidPickerLink
	 */
	protected ImageHyperlink getGuidPickerLink() {
		return guidPickerLink;
	}

	/**
	 * Get typeField.
	 * 
	 * @return the typeField
	 */
	protected CCombo getTypeField() {
		return typeField;
	}

	/**
	 * Get guidField.
	 * 
	 * @return the guidField
	 */
	public Text getGuidField() {
		return guidField;
	}

	/**
	 * Get isEditMode.
	 * 
	 * @return the isEditMode
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * Sets the qtdSpinner. 
	 * @param spinner the qtdSpinner to be set
	 * */
	protected void setQtySpinner(final Spinner spinner) {
		this.qtySpinner = spinner;
	}

	/**
	 * Gets the qtdSpinner.
	 * @return the qtdSpinner
	 * */
	protected Spinner getQtySpinner() {
		return qtySpinner;
	}

	/**
	 * Gets the policy prices container.
	 * @return the pricesContainer
	 * */
	protected PolicyActionContainer getPricesContainer() {
		return pricesContainer;
	}

}
