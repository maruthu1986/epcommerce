/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.valueeditor;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.conditionbuilder.adapter.ConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.service.ConditionModelValidationService;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderPlugin;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.dialog.value.support.DialogValueLabelProvider;
import com.elasticpath.cmclient.core.dialog.value.support.EditingSupportDialogFactory;
import com.elasticpath.cmclient.core.dialog.value.support.SimpleEditingSupportDialogFactory;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.dialog.CategoryFinderDialog;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * Condition value factory used for create SWT control objects based on the
 * {@link TagDefinition}.
 */
@SuppressWarnings({"PMD.ShortClassName", "PMD.GodClass"})
public class ConditionRowValueFactoryImpl implements ConditionRowValueFactory {

	private static final Logger LOG = Logger.getLogger(ConditionRowValueFactoryImpl.class);

	private static final int SPACING_CELLS_IN_LINE_COMPOSITE = 1;
	private static final int EDIT_BUTTON_SIZE = 17;
	private final SelectableValuesProviderFactory selectableValueProviderFactory = SelectableValuesProviderFactory.getInstance();

	private final ConditionModelValidationService validationService;

	private static final EpControlBindingProvider BINDING_PROVIDER = EpControlBindingProvider.getInstance();

	private static final EditingSupportDialogFactory EDITING_SUPPORT_DIALOG_FACTORY = new SimpleEditingSupportDialogFactory();

	private static final DialogValueLabelProvider DIALOG_VALUE_LABEL_PROVIDER = new DialogValueLabelProvider() {
		public String getLabelText() {
			return ConditionBuilderMessages.get().EditConditionalExpressionTagValue;
		}

		public boolean isLabelBold() {
			return false;
		}
	};
	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * Constructor that initialises the underlying core service that will
	 * perform the validation.
	 *
	 * @param validationService the validation service
	 */
	public ConditionRowValueFactoryImpl(final ConditionModelValidationService validationService) {

		this.validationService = validationService;

	}

	@Override
	public Control createControl(
		final Composite parent,
		final int swtStyle,
		final ConditionModelAdapter modelAdapter,
		final DataBindingContext dataBindingContext,
		final DisposeListener disposeListener) {

		TagDefinition tagDefinition = modelAdapter.getTagDefinition();
		Control control;

		if (selectableValueProviderFactory.hasUIPicker(tagDefinition)) {
			control = createControlWithUIPicker(parent, swtStyle, tagDefinition, modelAdapter, dataBindingContext);
		} else if (selectableValueProviderFactory.isEditViaComboBox(tagDefinition)) {
			control = createComboControl(parent, tagDefinition, modelAdapter, dataBindingContext);
		} else {
			control = createTextControl(parent, swtStyle, tagDefinition, modelAdapter, dataBindingContext, true);
		}

		if (disposeListener != null) {
			control.addDisposeListener(disposeListener);
		}

		return control;
	}

	/**
	 * Provides validation trigger for validating values on model adapter.
	 *
	 * @param modelAdapter     the model adapter that holds the values
	 * @param value            the value which to validate for this model adapter
	 * @param localeForMessage the locale to be used for error messages
	 * @return status for validator
	 */
	private IStatus validate(final ConditionModelAdapter modelAdapter, final Object value, final Locale localeForMessage) {

		final ValidationResult result =
			ConditionRowValueFactoryImpl.this.validationService.validate(modelAdapter, value);

		if (!result.isValid()) {
			final String errorMessage = result.getMessage(localeForMessage);
			return new Status(IStatus.ERROR, ConditionBuilderPlugin.PLUGIN_ID, errorMessage);
		}

		return Status.OK_STATUS;
	}

	private Text createTextControl(
		final Composite parent,
		final int swtStyle,
		final TagDefinition tagDefinition,
		final ConditionModelAdapter modelAdapter,
		final DataBindingContext dataBindingContext,
		final boolean noDialogBinding) {

		// instead of NOPMD
		tagDefinition.getGuid();

		final Text text = controlFactory.createTextField(parent, swtStyle | SWT.FLAT | SWT.BORDER, EpControlFactory.EpState.EDITABLE);

		final Locale locale = CorePlugin.getDefault().getDefaultLocale();

		final EpValueBinding valueBinding;

		if (noDialogBinding) {
			if (null != modelAdapter.getTagValue()) {
				text.setText(String.valueOf(modelAdapter.getTagValue()));
			}

			valueBinding = BINDING_PROVIDER.bind(
				dataBindingContext,
				text,
				new CompoundValidator(value -> {
					modelAdapter.setTagValue(modelAdapter.getTagValueFromString(value.toString()));
					return Status.OK_STATUS;
				},
					EpValidatorFactory.REQUIRED,
					value -> ConditionRowValueFactoryImpl.this.validate(
						modelAdapter, modelAdapter.getTagValueFromString(value.toString()), locale)),
				null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						modelAdapter.setTagValue(modelAdapter.getTagValueFromString(value.toString()));
						parent.layout(true);
						return Status.OK_STATUS;
					}
				},
				true
			);

		} else {

			valueBinding = BINDING_PROVIDER.bind(
				dataBindingContext,
				text,
				new CompoundValidator(EpValidatorFactory.REQUIRED,
					value -> ConditionRowValueFactoryImpl.this.validate(
						modelAdapter, modelAdapter.getTagValue(), locale)),
				null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						// update is happening in the dialog selection
						parent.layout(true);
						return Status.OK_STATUS;
					}
				},
				true
			);
		}

		addRemoveBindingListenerToTheControl(dataBindingContext, text, valueBinding);

		return text;
	}

	private Control createComboControl(
		final Composite parent,
		final TagDefinition tagDefinition,
		final ConditionModelAdapter modelAdapter,
		final DataBindingContext dataBindingContext) {

		final CCombo cmb = controlFactory.createComboBox(parent, SWT.FLAT | SWT.BORDER | SWT.READ_ONLY, EpControlFactory.EpState.EDITABLE);

		SelectableValuesProvider valuesProvider = selectableValueProviderFactory.createValueProvider(tagDefinition);
		if (null != valuesProvider) {
			cmb.setData(valuesProvider);
			cmb.setItems(valuesProvider.getNames());
		}

		if (null != modelAdapter.getTagValue() && !StringUtils.isBlank(String.valueOf(modelAdapter.getTagValue()))) {
			Object tagValue = modelAdapter.getTagValue();
			String nameByValue = valuesProvider.getNameByValue(tagValue);
			if (nameByValue == null) {
				String valueOf = String.valueOf(tagValue);
				nameByValue = valuesProvider.getNameByValue(valueOf);
			}
			cmb.setText(nameByValue);
		}

		final Locale locale = CorePlugin.getDefault().getDefaultLocale();

		final EpValueBinding valueBinding = BINDING_PROVIDER.bind(
			dataBindingContext,
			cmb,
			new CompoundValidator(EpValidatorFactory.REQUIRED,
				index -> {
					final int selectionIndex = (Integer) index;
					final SelectableValuesProvider valuesProvider1 = (SelectableValuesProvider) cmb.getData();

					return ConditionRowValueFactoryImpl.this.validate(
						modelAdapter, valuesProvider1.getValueBySelectionIndex(selectionIndex), locale);
				}),
			null,
			new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					final int selectionIndex = (Integer) value;
					final SelectableValuesProvider valuesProvider = (SelectableValuesProvider) cmb.getData();
					modelAdapter.setTagValue(valuesProvider.getValueBySelectionIndex(selectionIndex));
					parent.layout(true);
					return Status.OK_STATUS;
				}
			},
			true
		);

		addRemoveBindingListenerToTheControl(dataBindingContext, cmb, valueBinding);

		return cmb;
	}

	private Control createControlWithUIPicker(
		final Composite parent,
		final int swtStyle,
		final TagDefinition tagDefinition,
		final ConditionModelAdapter modelAdapter,
		final DataBindingContext dataBindingContext) {

		if (SelectableValuesProviderFactory.AUTOCOMPLETE_NON_RESTRICTIVE.equals(tagDefinition.getValueType().getUIPickerKey())) {
			return createNonRestrictiveAutocompleteText(parent, swtStyle, tagDefinition, modelAdapter, dataBindingContext);
		}

		return createTextViaDialogControl(parent, swtStyle, tagDefinition, modelAdapter, dataBindingContext);
	}


	private Control createTextViaDialogControl(
		final Composite parent,
		final int swtStyle,
		final TagDefinition tagDefinition,
		final ConditionModelAdapter modelAdapter,
		final DataBindingContext dataBindingContext) {

		final Composite valueComposite = new Composite(parent, SWT.NONE);
		valueComposite.setBackground(parent.getBackground());
		valueComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
			false));
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = SPACING_CELLS_IN_LINE_COMPOSITE;
		valueComposite.setLayout(layout);

		final Window valueSelectionDialog = EDITING_SUPPORT_DIALOG_FACTORY.getEditorDialog(
			parent.getShell(),
			selectableValueProviderFactory.getValueTypeFromTagDefinition(tagDefinition),
			String.valueOf(modelAdapter.getTagValue()),
			true,
			DIALOG_VALUE_LABEL_PROVIDER,
			true);

		final Text text = createTextControl(valueComposite, swtStyle | SWT.READ_ONLY,
			tagDefinition, modelAdapter, dataBindingContext, false);


		final String valueToShow = getValueForTextbox(valueSelectionDialog, modelAdapter);
		text.setText(valueToShow);

		SelectableValueResolver valueResolver = selectableValueProviderFactory.getValueResolver(tagDefinition);

		if (null != modelAdapter.getTagValue() && StringUtils.isNotBlank(String.valueOf(modelAdapter.getTagValue()))) {
			if (null == valueResolver) {
				text.setText(String.valueOf(modelAdapter.getTagValue()));
			} else {
				text.setText(valueResolver.getNameByValue(String.valueOf(modelAdapter.getTagValue())));
			}
		}

		parent.layout(true);
		valueComposite.layout(true);


		final Button button = controlFactory.createButton(valueComposite, "...", SWT.PUSH | SWT.FLAT, EpControlFactory.EpState.EDITABLE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.heightHint = EDIT_BUTTON_SIZE;
		gridData.widthHint = EDIT_BUTTON_SIZE;
		button.setLayoutData(gridData);


		button.addSelectionListener(
			new SelectionListener() {
				public void widgetDefaultSelected(final SelectionEvent arg0) {
					// nothing TO DO
				}

				public void widgetSelected(final SelectionEvent arg0) {

					if (Window.OK == valueSelectionDialog.open()) {
						Object value = null;
						String valueToShow = null;
						try {
							Pair dataFromDialog = getValuePairFromDialog(valueSelectionDialog);
							value = dataFromDialog.getValue();
							valueToShow = dataFromDialog.getDisplayValue();
						} catch (IllegalArgumentException iae) {
							LOG.error(iae);
						}
						parent.layout(true);
						valueComposite.layout(true);

						modelAdapter.setTagValue(modelAdapter.getTagValueFromString(value.toString()));
						text.setText(valueToShow);

					}
				}
			}
		);


		return text;
	}

	private Control createNonRestrictiveAutocompleteText(final Composite parent, final int swtStyle, final TagDefinition tagDefinition,
		final ConditionModelAdapter modelAdapter, final DataBindingContext dataBindingContext) {

		final Text text = controlFactory.createTextField(parent, swtStyle | SWT.FLAT | SWT.BORDER, EpControlFactory.EpState.EDITABLE);

		final SelectableValuesProvider valuesProvider = selectableValueProviderFactory.createValueProvider(tagDefinition);

		if (null != modelAdapter.getTagValue() && !StringUtils.isBlank(String.valueOf(modelAdapter.getTagValue()))) {
			String name = valuesProvider.getNameByValue(modelAdapter.getTagValue());
			if (null == name) {
				name = String.valueOf(modelAdapter.getTagValue());
			}
			text.setText(name);
		}

		final EpValueBinding valueBinding = BINDING_PROVIDER.bind(
			dataBindingContext, text, EpValidatorFactory.REQUIRED, null,
			new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					Object valueObject = valuesProvider.getValueByName(String.valueOf(value));
					if (null == valueObject) {
						valueObject = value;
					}
					modelAdapter.setTagValue(valueObject);
					parent.layout(true);
					return Status.OK_STATUS;
				}
			},
			true
		);

		addRemoveBindingListenerToTheControl(dataBindingContext, text, valueBinding);

		new AutoCompleteField(text, new TextContentAdapter(), valuesProvider.getNames());

		return text;
	}

	private void addRemoveBindingListenerToTheControl(final DataBindingContext dataBindingContext,
		final Control control, final EpValueBinding valueBinding) {
		control.addListener(ConditionRowValueFactory.EVENT_FOR_UNBIND, new Listener() {
			public void handleEvent(final Event event) {
				EpControlBindingProvider.removeEpValueBinding(dataBindingContext, valueBinding);
			}
		});
	}

	private Pair getValuePairFromDialog(final IValueRetriever dialog) {
		return new Pair(dialog.getValue(), String.valueOf(dialog.getValue()));
	}

	private Pair getValuePairFromDialog(final ProductFinderDialog dialog) {
		final Product product = (Product) dialog.getSelectedObject();
		return new Pair(product.getCode(), product.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
	}

	private Pair getValuePairFromDialog(final CategoryFinderDialog dialog) {
		final Category category = (Category) dialog.getSelectedObject();
		return new Pair(category.getCode(), category.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
	}

	private Pair getValuePairFromDialog(final Window dialog) {
		if (dialog instanceof IValueRetriever) {
			return getValuePairFromDialog((IValueRetriever) dialog);
		} else if (dialog instanceof ProductFinderDialog) {
			return getValuePairFromDialog((ProductFinderDialog) dialog);
		} else if (dialog instanceof CategoryFinderDialog) {
			return getValuePairFromDialog((CategoryFinderDialog) dialog);
		}
		throw new IllegalArgumentException("Unsupported dialog type: " + dialog.getClass().getCanonicalName()); //$NON-NLS-1$
	}

	/**
	 * Holds selection of the dialog.
	 */
	private class Pair {

		private final Object value;
		private final String displayValue;

		/**
		 * @param value        actual selection value.
		 * @param displayValue UI friendly value.
		 */
		Pair(final Object value, final String displayValue) {
			this.value = value;
			this.displayValue = displayValue;
		}

		/**
		 * @return actual selection value.
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * @return UI friendly value.
		 */
		public String getDisplayValue() {
			return displayValue;
		}

	}

	private String getValueForTextboxIValueRetriever(final ConditionModelAdapter modelAdapter) {
		return String.valueOf(modelAdapter.getTagValue());
	}

	private String getValueForTextboxProductFinderDialog(final ConditionModelAdapter modelAdapter) {
		final ProductLookup productLookup = ServiceLocator.getService(ContextIdNames.PRODUCT_LOOKUP);
		final Product product = productLookup.findByGuid(String.valueOf(modelAdapter.getTagValue()));
		if (product == null) {
			return StringUtils.EMPTY;
		}
		return product.getDisplayName(CorePlugin.getDefault().getDefaultLocale());
	}

	private String getValueForTextboxCategoryFinderDialog(final ConditionModelAdapter modelAdapter) {
		final CategoryService categoryService = ServiceLocator.getService(ContextIdNames.CATEGORY_SERVICE);
		final Category category = categoryService.findByCode(String.valueOf(modelAdapter.getTagValue()));
		if (category == null) {
			return StringUtils.EMPTY;
		}
		return category.getDisplayName(CorePlugin.getDefault().getDefaultLocale());
	}

	private String getValueForTextbox(final Window dialog, final ConditionModelAdapter modelAdapter) {
		if (dialog instanceof IValueRetriever) {
			return getValueForTextboxIValueRetriever(modelAdapter);
		} else if (dialog instanceof ProductFinderDialog) {
			return getValueForTextboxProductFinderDialog(modelAdapter);
		} else if (dialog instanceof CategoryFinderDialog) {
			return getValueForTextboxCategoryFinderDialog(modelAdapter);
		}
		throw new IllegalArgumentException("Unsupported dialog type: " + dialog.getClass().getCanonicalName()); //$NON-NLS-1$
	}


}
