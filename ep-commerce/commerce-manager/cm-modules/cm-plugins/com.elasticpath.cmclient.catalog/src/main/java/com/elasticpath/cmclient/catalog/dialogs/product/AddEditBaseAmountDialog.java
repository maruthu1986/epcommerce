/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.dialogs.product;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.conversion.EpPriceConverter;
import com.elasticpath.cmclient.core.helpers.BaseAmountDTOCreator;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory.BigDecimalValidatorForComparator;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.RandomGuid;

/**
 * Dialog for adding new base amount.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class AddEditBaseAmountDialog extends AbstractPolicyAwareDialog implements StatePolicyTarget, ObjectGuidReceiver {

	private BaseAmountDTO baseAmount;

	private Spinner qtySpinner;

	private Text listPriceText;

	private Text salePriceText;

	private final DataBindingContext bindingContext;

	private PriceTierEditMode editMode;

	private Collection<BaseAmountDTO> existingBaseAmounts = Collections.emptyList();

	private static final int MAX_QTY = 10000;

	private final EpPriceConverter converter = new EpPriceConverter();

	/**
	 * Policy container for the dialog controls.
	 */
	private PolicyActionContainer addEditBaseAmountDialogContainer;

	private PriceListService priceListService;

	private int salePriceValidationStatus;
	private int listPriceValidationStatus;
	private EpValueBinding listPriceBinding;
	private EpValueBinding salePriceBinding;

	private BaseAmountDTO copyBaseAmountDTO;

	/**
	 * Price Tier Edit Mode.
	 */
	public enum PriceTierEditMode {

		/**
		 * Add product base amount mode.
		 */
		ADD_BASEAMOUNT,

		/**
		 * Edit product base amount mode.
		 */
		EDIT_BASEAMOUNT,
	}

	/**
	 * Constructs the dialog.
	 *
	 */
	public AddEditBaseAmountDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 2, false);
		this.bindingContext = new DataBindingContext();
	}

	/**
	 * Constructs the dialog.
	 *
	 * @param mode edit or create mode
	 * @param baseAmount the base amount instance
	 * @param existingBaseAmounts a list of existing base amount, used in validation
	 */
	public AddEditBaseAmountDialog(final PriceTierEditMode mode, final BaseAmountDTO baseAmount,
			final Collection<BaseAmountDTO> existingBaseAmounts) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 2, false);

		this.baseAmount = baseAmount;
		this.copyBaseAmountDTO = BaseAmountDTOCreator.createModel(baseAmount);
		this.editMode = mode;
		this.existingBaseAmounts = existingBaseAmounts;

		this.bindingContext = new DataBindingContext();
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.OK, parent);
	}

	private BaseAmountDTO createNewBaseAmount() {
		BaseAmountDTO baseAmount = BaseAmountDTOCreator.createModel();

		// set new DTO GUID
		RandomGuid randomGuid = ServiceLocator.getService(ContextIdNames.RANDOM_GUID);
		baseAmount.setGuid(randomGuid.toString());

		return baseAmount;
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {

		addEditBaseAmountDialogContainer = addPolicyActionContainer("addEditBaseAmountDialog"); //$NON-NLS-1$
		if (editMode == PriceTierEditMode.ADD_BASEAMOUNT) {

			// force state policy to *not* check if base amount in current change set
			addEditBaseAmountDialogContainer.setPolicyDependent(null);
		} else {
			// force state policy to check if base amount in current change set
			addEditBaseAmountDialogContainer.setPolicyDependent(baseAmount);
		}

		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		dialogComposite.addLabelBoldRequired(CatalogMessages.get().AddEditPriceTierDialog_Quantity, labelData, addEditBaseAmountDialogContainer);

		if (this.editMode == PriceTierEditMode.ADD_BASEAMOUNT) {
			this.qtySpinner = dialogComposite.addSpinnerField(fieldData, addEditBaseAmountDialogContainer);
			qtySpinner.setMinimum(1);
			qtySpinner.setMaximum(MAX_QTY);
			baseAmount.setQuantity(BigDecimal.valueOf(qtySpinner.getSelection()));
		} else {
			dialogComposite.addLabel(String.valueOf(this.getBaseAmount().getQuantity().intValue()), fieldData, addEditBaseAmountDialogContainer);
		}

		dialogComposite.addLabelBoldRequired(CatalogMessages.get().AddEditPriceTierDialog_ListPrice, labelData, addEditBaseAmountDialogContainer);
		this.listPriceText = dialogComposite.addTextField(fieldData, addEditBaseAmountDialogContainer);

		dialogComposite.addLabelBold(CatalogMessages.get().AddEditPriceTierDialog_SalePrice, labelData, addEditBaseAmountDialogContainer);
		this.salePriceText = dialogComposite.addTextField(fieldData, addEditBaseAmountDialogContainer);
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return baseAmount;
	}

	@Override
	protected void populateControls() {
		if (this.editMode == PriceTierEditMode.EDIT_BASEAMOUNT) {

			// this.qtySpinner.setSelection(this.getPriceTier().getMinQty());
			if (this.getBaseAmount().getListValue() != null) {
				this.listPriceText.setText(this.getBaseAmount().getListValue().toString());
			}

			if (this.getBaseAmount().getSaleValue() != null) {
				this.salePriceText.setText(this.getBaseAmount().getSaleValue().toString());
			}
		}
	}

	@Override
	@SuppressWarnings("PMD.PrematureDeclaration")
	protected void bindControls() {

		// Min Order Quantity
		if (this.qtySpinner != null) {
			EpControlBindingProvider.getInstance().bind(this.bindingContext, this.qtySpinner, this.getBaseAmount(), "quantity", //$NON-NLS-1$
					value -> {
						// check unique
						final Integer intValue = (Integer) value;
						if (findBaseAmount(intValue) != null) {
							final String duplicatePriceTierMsg =
								NLS.bind(CatalogMessages.get().AddEditPriceTierDialog_Duplicate_PriceTier,
								new Object[]{value});

							return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR, duplicatePriceTierMsg, null);
						}

						return Status.OK_STATUS;
					}, null, true);
		}

		IValidator listPriceValidator = new CompoundValidator(
				new IValidator[] {
						EpValidatorFactory.REQUIRED,
						EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL,
						new BigDecimalValidatorForComparator((first, second) -> {
							listPriceValidationStatus = compareListSalePrices();
							if (listPriceValidationStatus >= 0 && salePriceValidationStatus == -1) {
								salePriceBinding.getBinding().updateTargetToModel();
							}
							return listPriceValidationStatus;
						}, CatalogMessages.get().validator_baseAmount_salePriceIsMoreThenListPrice)
				});

		IValidator salePriceValidator = new CompoundValidator(
				new IValidator[] {
						EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL,
						new BigDecimalValidatorForComparator((first, second) -> {
							if (first == null) {
								return 1;
							}
							salePriceValidationStatus = compareListSalePrices();
							  if (salePriceValidationStatus >= 0 && listPriceValidationStatus == -1) {
								listPriceBinding.getBinding().updateTargetToModel();
							}
							return salePriceValidationStatus;
						}, CatalogMessages.get().validator_baseAmount_salePriceIsMoreThenListPrice)
				});

		listPriceBinding = EpControlBindingProvider.getInstance().bind(this.bindingContext, this.listPriceText,
				this.getBaseAmount(),
				"listValue", //$NON-NLS-1$
				listPriceValidator,
				new EpPriceConverter(), true);

		salePriceBinding = EpControlBindingProvider.getInstance().bind(this.bindingContext, this.salePriceText,
				this.getBaseAmount(),
				"saleValue", //$NON-NLS-1$
				salePriceValidator,
				new EpPriceConverter(), true);

		EpDialogSupport.create(this, this.bindingContext);
	}

	private int compareListSalePrices() {
		BigDecimal saleValue = null; 
		BigDecimal listValue = null;
		try {
			saleValue = (BigDecimal) converter.convert(salePriceText.getText());
			if (saleValue == null) {
				saleValue = BigDecimal.ZERO;
			}
			listValue = (BigDecimal) converter.convert(listPriceText.getText());
			if (listValue == null) {
				listValue = BigDecimal.ZERO;
			}
		} catch (final NumberFormatException exception) {
			return 1;
		}
		return listValue.compareTo(saleValue);
	}

	@Override
	protected String getTitle() {
		if (this.editMode == PriceTierEditMode.ADD_BASEAMOUNT) {
			return CatalogMessages.get().AddPriceTierDialog_Title;
		}

		return CatalogMessages.get().EditPriceTierDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		if (this.editMode == PriceTierEditMode.ADD_BASEAMOUNT) {
			return CatalogMessages.get().AddPriceTierDialog_WindowTitle;
		}

		return CatalogMessages.get().EditPriceTierDialog_WindowTitle;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	/**
	 * Get the base amount.
	 * 
	 * @return base amount
	 */
	public BaseAmountDTO getBaseAmount() {
		return baseAmount;
	}
	
	private BaseAmountDTO findBaseAmount(final int qty) {
		for (BaseAmountDTO amountDTO : existingBaseAmounts) {
			if (amountDTO.getQuantity().intValue() == qty) {
				return amountDTO;
			}
		}
		return null;
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}
	
	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return addEditBaseAmountDialogContainer;
	}

	@Override
	protected void refreshLayout() {
		// Do nothing.
	}

	@Override
	public String getTargetIdentifier() {
		return "addEditBaseAmountDialog"; //$NON-NLS-1$
	}
	
	@Override
	protected Object getDependentObject() {
		return baseAmount;
	}

	@Override
	public void setObjectGuid(final String objectGuid) {
		if (objectGuid == null) {
			this.baseAmount = createNewBaseAmount();
			this.editMode = PriceTierEditMode.ADD_BASEAMOUNT;
		} else {
			this.baseAmount = getPriceListService().getBaseAmount(objectGuid);
			this.editMode = PriceTierEditMode.EDIT_BASEAMOUNT;
			this.copyBaseAmountDTO = BaseAmountDTOCreator.createModel(baseAmount);
		}
	}
	
	/**
	 * Get price list service.
	 *  
	 * @return the instance of the price list service
	 */
	protected PriceListService getPriceListService() {
		if (priceListService != null) {
			return priceListService;
		}
		return ServiceLocator.getService(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);
	}

	@Override
	protected void okPressed() {
		if (PriceTierEditMode.EDIT_BASEAMOUNT == editMode && copyBaseAmountDTO.equals(this.baseAmount)) {
			super.cancelPressed(); // nothing changed. return cancel to abort.
			return;
		}
		super.okPressed();
	}

	@Override
	public DataBindingContext getDataBindingContext() {
		return bindingContext;
	}
	
}
