/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.dialogs.cartitemmodifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOptionLdf;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;

/**
 * The dialog UI class for Adding/Editing cartItemModifierFieldOption.
 */
@SuppressWarnings({"PMD.GodClass"})
public class AddEditCartItemModifierFieldOptionDialog extends AbstractPolicyAwareDialog
		implements ObjectGuidReceiver, SelectionListener {

	private static final int DIALOG_NUMBER_OF_COLUMN = 1;

	private static final int MAIN_COMPOSITE_NUMBER_OF_COLUMN = 4;

	private static final int BINDING_CALLER = 0;

	private static final int LANGUAGE_CHANGED_CALLER = 1;

	private final DataBindingContext dataBindingContext;

	private CartItemModifierFieldOption cartItemModifierFieldOption;

	private CartItemModifierField cartItemModifierField;

	private Text optionDisplayNameText;

	private Text optionValueText;

	private CCombo optionLanguageCombo;

	private Locale selectedLocale;

	private String originalValue;

	private boolean editMode;

	private List<Locale> allLocales;

	private List<String> allLocalesTags;


	private CartItemModifierGroup cartItemModifierGroup;

	/**
	 * Policy container for the dialog controls.
	 */
	private PolicyActionContainer addEditCartItemModifierFieldOptionContainer;

	/**
	 * Default Constructor.
	 */
	public AddEditCartItemModifierFieldOptionDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), DIALOG_NUMBER_OF_COLUMN, false);
		this.dataBindingContext = new DataBindingContext();
	}

	/**
	 * Constructor.
	 *
	 * @param allLocales                  All Locales available.
	 * @param selectedLocale              the selected locale.
	 * @param cartItemModifierFieldOption the cart item modifier field option to be edited o added
	 *                                    {@link CartItemModifierFieldOption}
	 * @param cartItemModifierField       the cart item modifier field associated {@link CartItemModifierField}
	 * @param cartItemModifierGroup       the cart item modifier group
	 */
	public AddEditCartItemModifierFieldOptionDialog(final List<Locale> allLocales,
			final Locale selectedLocale, final CartItemModifierFieldOption cartItemModifierFieldOption,
			final CartItemModifierField cartItemModifierField, final CartItemModifierGroup cartItemModifierGroup) {

		this();
		this.allLocales = allLocales;
		this.selectedLocale = selectedLocale;
		this.editMode = cartItemModifierFieldOption != null;

		if (editMode) {
			initializeDialog(cartItemModifierFieldOption);
		} else {
			createCartItemModifierFieldOption();
		}
		this.cartItemModifierField = cartItemModifierField;
		this.cartItemModifierGroup = cartItemModifierGroup;
	}

	private void initializeDialog(final CartItemModifierFieldOption newField) {
		this.cartItemModifierFieldOption = newField;

		// remember the original code for validation purposes
		this.originalValue = this.cartItemModifierFieldOption.getValue();
	}

	/**
	 * @return the current CartItemModifierField object edited or added.
	 */
	public CartItemModifierFieldOption getCartItemModifierFieldOption() {
		if (cartItemModifierFieldOption == null) {
			createCartItemModifierFieldOption();
		}
		if (this.cartItemModifierFieldOption.getValue() != null) {
			this.originalValue = this.cartItemModifierFieldOption.getValue();
		}
		return cartItemModifierFieldOption;
	}

	private void createCartItemModifierFieldOption() {
		this.cartItemModifierFieldOption = ServiceLocator.getService(ContextIdNames.CART_ITEM_MODIFIER_FIELD_OPTION);
		this.originalValue = "";
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		// update strategy for the cartItemModifierFieldOption value control
		final ObservableUpdateValueStrategy valueUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final String value = optionValueText.getText();
				getCartItemModifierFieldOption().setValue(value);
				if (!editMode) {
					getCartItemModifierFieldOption().setOrdering(getMaximumFieldOptionOrdering(cartItemModifierField) + 1);
				}
				return Status.OK_STATUS;
			}
		};
		// update strategy for the cartItemModifierFieldOptionLDF
		final ObservableUpdateValueStrategy displayNameUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				checkLDFAndBind(selectedLocale.toString(), optionDisplayNameText.getText(), 0);
				return Status.OK_STATUS;
			}
		};
		// CartItemModifierFieldOption value
		bindingProvider.bind(dataBindingContext, this.optionValueText,
				EpValidatorFactory.CARTITEM_MODIFIER_OPTION_VALUE, null, valueUpdateStrategy, true);
		// CartItemModifierFieldOption displayName
		bindingProvider.bind(dataBindingContext, this.optionDisplayNameText, EpValidatorFactory.STRING_255_REQUIRED, null,
				displayNameUpdateStrategy, true);
		EpDialogSupport.create(this, dataBindingContext);
	}

	@Override
	protected String getTitle() {
		if (!editMode) {
			return CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_Iitle_AddModifierField;
		}
		return CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_Iitle_EditModifierField;
	}

	@Override
	protected String getInitialMessage() {
		if (!editMode) {
			return CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_InitMsg_AddNewModifierField;
		}
		return CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_InitMsg_EditAnModifierField;
	}

	@Override
	protected Image getWindowImage() {
		// no image available for the dialog.
		return null;
	}

	@Override
	protected String getWindowTitle() {
		if (!editMode) {
			return CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_WinIitle_AddModifierField;
		}
		return CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_WinIitle_EditModifierField;
	}

	@Override
	protected void okPressed() {
		setErrorMessage(null);
		if (validate()) {
			super.okPressed();
		}
	}

	/**
	 * Validate the Cart Item Modifier Group Field.
	 *
	 * @return <code>true</code> if the brand fields are valid, false otherwise.
	 */
	private boolean validate() {
		final String optFieldValue = optionValueText.getText();
		final String optFieldName = optionDisplayNameText.getText();
		if (optFieldValue.trim().isEmpty()) {
			setErrorMessage(CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_ErrorDialog_noValue_desc);
			return false;
		} else if (optFieldName.trim().isEmpty()) {
			setErrorMessage(CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_ErrorDialog_noName_desc);
			return false;
		} else {
			//validates the model (newly added items) to not have 2 values with same locale
			if (cartItemModifierField != null) {
				for (CartItemModifierFieldOption itemModifierFieldOption : cartItemModifierField.getCartItemModifierFieldOptions()) {
					if (editMode && itemModifierFieldOption.getValue().equals(originalValue)) {
						return true;
					} else if ((!editMode && itemModifierFieldOption.getValue().equals(optFieldValue))
							&& itemModifierFieldOption.getCartItemModifierFieldOptionsLdfByLocale(this.selectedLocale.toString()) != null) {
						setErrorMessage(CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_ErrorDialog_AddInUse_Langdesc);
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {
		this.addEditCartItemModifierFieldOptionContainer = addPolicyActionContainer("addEditCartItemModifierFieldOptionDialog");

		IPolicyTargetLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(MAIN_COMPOSITE_NUMBER_OF_COLUMN, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true),
				addEditCartItemModifierFieldOptionContainer);

		if (editMode) {
			// force state policy to check if cartItemModifierGroup is in current change set
			this.addEditCartItemModifierFieldOptionContainer.setPolicyDependent(this.cartItemModifierGroup);
		} else {
			// force state policy to *not* check if cartItemModifierGroup in current change set
			this.addEditCartItemModifierFieldOptionContainer.setPolicyDependent(null);
		}

		final IEpLayoutData labelLayoutData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		final IEpLayoutData fieldData1 = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 1, 1);
		final IEpLayoutData fieldData2 = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		final IEpLayoutData textFieldLayoutData3 = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 1, 1);
		final IEpLayoutData textFieldLayoutData2 = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);

		mainComposite.addLabelBoldRequired(
				CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_Value, labelLayoutData,
				this.addEditCartItemModifierFieldOptionContainer);
		optionValueText = mainComposite.addTextField(textFieldLayoutData2, this.addEditCartItemModifierFieldOptionContainer);

		mainComposite.addEmptyComponent(textFieldLayoutData3, this.addEditCartItemModifierFieldOptionContainer);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_DisplayName,
				labelLayoutData, this.addEditCartItemModifierFieldOptionContainer);
		optionLanguageCombo = mainComposite.addComboBox(fieldData1, this.addEditCartItemModifierFieldOptionContainer);
		optionLanguageCombo.addSelectionListener(this);
		optionDisplayNameText = mainComposite.addTextField(fieldData2, this.addEditCartItemModifierFieldOptionContainer);
	}

	@Override
	protected Object getDependentObject() {
		return this.cartItemModifierGroup;
	}

	@Override
	protected void refreshLayout() {
		// Do nothing
	}

	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return this.addEditCartItemModifierFieldOptionContainer;
	}


	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return cartItemModifierFieldOption;
	}

	@Override
	protected void populateControls() {
		this.optionValueText.setText(this.originalValue);
		this.allLocalesTags = new ArrayList<>();
		this.optionLanguageCombo.removeAll();
		for (Locale lang : this.allLocales) {
			allLocalesTags.add(lang.toLanguageTag());
			this.optionLanguageCombo.add(lang.getDisplayName());
		}
		optionLanguageCombo.select(this.allLocales.indexOf(this.selectedLocale));
		optionLanguageCombo.notifyListeners(SWT.Selection, new Event());
	}

	/**
	 * Depending of the Dialog Mode set Ok Button Text.
	 *
	 * @return The OK button text
	 */
	private String setButtonLabel() {
		String bttLabel = CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_Add;
		if (editMode) {
			bttLabel = CoreMessages.get().AbstractEpDialog_ButtonOK;
		}
		return bttLabel;
	}

	@Override
	public String getTargetIdentifier() {
		return "addEditCartItemModifierFieldOptionDialog";
	}

	@Override
	public void setObjectGuid(final String objectGuid) {
		if (objectGuid == null) {
			createCartItemModifierFieldOption();
			editMode = false;
		} else {
			throw new UnsupportedOperationException("Add via ObjectGuidReceiver is not implemented.");
		}
	}

	@Override
	public DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, setButtonLabel(), null);
		createEpCancelButton(parent);
	}

	/**
	 * Check CartItemModifierOption LDF and Bind.
	 *
	 * @param lang    The current locale.
	 * @param display The current display name.
	 */
	private void checkLDFAndBind(final String lang, final String display, final int caller) {
		//If called from binding (ui to model bind)
		if (caller == BINDING_CALLER) {
			if (!display.isEmpty()) {
				CartItemModifierFieldOptionLdf currentOptionLDF =
						this.getCartItemModifierFieldOption().getCartItemModifierFieldOptionsLdfByLocale(lang);
				if (currentOptionLDF == null) {
					currentOptionLDF = ServiceLocator.getService(ContextIdNames.CART_ITEM_MODIFIER_OPTION_LDF);
					currentOptionLDF.setDisplayName(display);
					currentOptionLDF.setLocale(lang);
					this.getCartItemModifierFieldOption().addCartItemModifierFieldOptionLdf(currentOptionLDF);
				} else {
					currentOptionLDF.setDisplayName(display);
					currentOptionLDF.setLocale(lang);
				}
			}
		} else if (caller == LANGUAGE_CHANGED_CALLER) { // If called when language changed (model to ui bind)
			optionDisplayNameText.setText(CatalogMessages.EMPTY_STRING);
			CartItemModifierFieldOptionLdf currentOptionLDF = getCartItemModifierFieldOption().getCartItemModifierFieldOptionsLdfByLocale(lang);
			if (currentOptionLDF != null) {
				optionDisplayNameText.setText(currentOptionLDF.getDisplayName());
			}
		}
	}

	@Override
	public void widgetSelected(final SelectionEvent selectionEvent) {
		if (selectionEvent.getSource().equals(this.optionLanguageCombo)) {
			this.selectedLocale = Locale.forLanguageTag(this.allLocalesTags.get(this.optionLanguageCombo.getSelectionIndex()));
			checkLDFAndBind(selectedLocale.toString(), optionDisplayNameText.getText(), 1);
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
		// Not Used.
	}

	/**
	 * Method to retrieve the maximum field order of a CartItemModifierField.
	 *
	 * @param field CartItemModifierField to get field options  max order.
	 * @return maximum field option index.
	 */
	private int getMaximumFieldOptionOrdering(final CartItemModifierField field) {
		int maximumIndex = 0;
		if (field != null) {
			for (CartItemModifierFieldOption option : field.getCartItemModifierFieldOptions()) {
				if (option.getOrdering() > maximumIndex) {
					maximumIndex = option.getOrdering();
				}
			}
		}
		return maximumIndex;
	}
}
