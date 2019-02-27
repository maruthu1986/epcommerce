/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import java.util.Date;

import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * Product store rules UI view part.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.GodClass" })
public class StoreRulesViewPart extends DefaultStatePolicyDelegateImpl implements IEpViewPart, SelectionListener {

	private static final int IN_STOCK = 0;

	private static final int PRE_ORDER = 1;

	private static final int BACK_ORDER = 2;

	private static final int ALWAYS = 3;

	private Button storeVisible;

	private Button notSoldSeparatelyCheckBox;

	private IEpDateTimePicker enableDatePicker;

	private IEpDateTimePicker disableDatePicker;

	private Spinner minOrderQtySpinner;

	private IPolicyTargetLayoutComposite controlPane;

	private CCombo availabilityRuleCombo;

	private IEpDateTimePicker expectedReleaseDateComp;

	private Label expectedReleaseDateLabel;

	private final Product model;

	private DataBindingContext bindingContext;

	private Text availabilityText;

	private final boolean wizardMode;

	private static final String[] AVAILABILITY_STRINGS = new String[] { CatalogMessages.get().ProductEditorStoreRuleSection_AvailableWhenInStock,
			CatalogMessages.get().ProductEditorStoreRuleSection_AvailableForPreOrder,
			CatalogMessages.get().ProductEditorStoreRuleSection_AvailableForBackOrder,
			CatalogMessages.get().ProductEditorStoreRuleSection_AlwaysAvailable };

	private static final String[] AVAILABILITY_STRINGS_EDITOR_MODE = new String[] {
			CatalogMessages.get().ProductEditorStoreRuleSection_AvailableWhenInStock,
			CatalogMessages.get().ProductEditorStoreRuleSection_AvailableForPreOrder,
			CatalogMessages.get().ProductEditorStoreRuleSection_AvailableForBackOrder };

	private static final int GRID_COLUMNS = 3;

	private static final int MAX_MINORDER = 100;

	private Button bundleStoreVisible;

	private Text effectiveEnableDatePicker;

	private Text effectiveDisableDatePicker;

	private IPolicyTargetLayoutComposite notSoldSeparateComposite;

	private IPolicyTargetLayoutComposite bundleRulesComposite;

	private IPolicyTargetLayoutComposite effectiveRulesComposite;

	/**
	 * Constructor.
	 * 
	 * @param model the product domain model
	 * @param wizardMode sets it to true if this view part will be used inside a wizard
	 */
	public StoreRulesViewPart(final Product model, final boolean wizardMode) {
		this.model = model;
		this.wizardMode = wizardMode;
	}

	@Override
	public void createControls(final IEpLayoutComposite mainPane, final IEpLayoutData data) {
		PolicyActionContainer productControls = addPolicyActionContainer("productControls"); //$NON-NLS-1$
		PolicyActionContainer relatedControls = addPolicyActionContainer("relatedControls"); //$NON-NLS-1$

		this.controlPane = PolicyTargetCompositeFactory.wrapLayoutComposite(mainPane.addGridLayoutComposite(GRID_COLUMNS, false, data));

		if (isProductBundle()) {
			if (wizardMode) {
				createNotSoldSeparateControls(controlPane, productControls);
				createStoreRulesControls(controlPane, productControls);
			} else {
				final IEpLayoutData compositeLayout = this.controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 3, 1);
				notSoldSeparateComposite = this.controlPane.addGridLayoutComposite(GRID_COLUMNS, false, compositeLayout, productControls);
				createNotSoldSeparateControls(notSoldSeparateComposite, productControls);

				bundleRulesComposite = this.controlPane.addGridLayoutSection(GRID_COLUMNS,
						CatalogMessages.get().ProductEditorStoreRuleSection_BundleRules,
						ExpandableComposite.TITLE_BAR, data, productControls);

				effectiveRulesComposite = this.controlPane.addGridLayoutSection(GRID_COLUMNS,
						CatalogMessages.get().ProductEditorStoreRuleSection_EffectiveRules, ExpandableComposite.TITLE_BAR, data, productControls);

				createStoreRulesControls(bundleRulesComposite, productControls);
				createEffectiveRulesControls(effectiveRulesComposite, relatedControls);
			}
		} else {
			createNotSoldSeparateControls(controlPane, productControls);
			createStoreRulesControls(controlPane, productControls);
			createProductSpecificControls(controlPane, productControls, relatedControls);
		}
	}

	private void createNotSoldSeparateControls(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer productControls) {
		final IEpLayoutData labelLayoutData = parent.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData layoutDataSpan2 = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		
		parent.addLabelBold(CatalogMessages.get().Item_NotSoldSeparately, labelLayoutData, productControls);
		this.notSoldSeparatelyCheckBox = parent.addCheckBoxButton("", layoutDataSpan2, productControls);  //$NON-NLS-1$
	}

	private void createEffectiveRulesControls(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer relatedControls) {
		final IEpLayoutData labelLayoutData = parent.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData layoutDataSpan2 = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);

		parent.addLabelBold(CatalogMessages.get().Item_StoreVisible, labelLayoutData, relatedControls);
		this.bundleStoreVisible = parent.addCheckBoxButton("", layoutDataSpan2, relatedControls);  //$NON-NLS-1$

		parent.addLabelBoldRequired(CatalogMessages.get().Item_EnableDateTime, labelLayoutData, relatedControls);
		this.effectiveEnableDatePicker = parent.addTextField(layoutDataSpan2, relatedControls);

		parent.addLabelBold(CatalogMessages.get().Item_DisableDateTime, labelLayoutData, relatedControls);
		this.effectiveDisableDatePicker = parent.addTextField(layoutDataSpan2, relatedControls);
	}

	private void createStoreRulesControls(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer productControls) {
		final IEpLayoutData labelLayoutData = parent.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData layoutDataSpan2 = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);

		parent.addLabelBold(CatalogMessages.get().Item_StoreVisible, labelLayoutData, productControls);
		this.storeVisible = parent.addCheckBoxButton("", layoutDataSpan2, productControls);  //$NON-NLS-1$

		parent.addLabelBoldRequired(CatalogMessages.get().Item_EnableDateTime, labelLayoutData, productControls);
		this.enableDatePicker = parent.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, layoutDataSpan2, productControls);

		parent.addLabelBold(CatalogMessages.get().Item_DisableDateTime, labelLayoutData, productControls);
		this.disableDatePicker = parent.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, layoutDataSpan2, productControls);
	}

	private void createProductSpecificControls(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer productControls,
			final PolicyActionContainer relatedControls) {
		final IEpLayoutData labelLayoutData = parent.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData layoutDataSpan2 = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);

		parent.addLabelBoldRequired(CatalogMessages.get().ProductEditorStoreRuleSection_MinOrderQty, labelLayoutData, productControls);
		this.minOrderQtySpinner = parent.addSpinnerField(layoutDataSpan2, productControls);
		this.minOrderQtySpinner.setMinimum(1);
		this.minOrderQtySpinner.setMaximum(MAX_MINORDER);

		parent.addLabelBold(CatalogMessages.get().ProductEditorStoreRuleSection_AvailabilityRule, labelLayoutData, productControls);
		if (getModel().getAvailabilityCriteria() == AvailabilityCriteria.ALWAYS_AVAILABLE && !wizardMode) {
			availabilityText = parent.addTextField(layoutDataSpan2, relatedControls);
		} else {
			availabilityRuleCombo = parent.addComboBox(layoutDataSpan2, productControls);
			availabilityRuleCombo.addSelectionListener(this);
		}
		
		this.expectedReleaseDateLabel = parent.addLabelBoldRequired(CatalogMessages.get().ProductEditorStoreRuleSection_ExpReleaseDate,
				labelLayoutData, productControls);
		this.expectedReleaseDateComp = parent.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE, layoutDataSpan2, productControls);
	}

	@Override
	public void populateControls() {
		final Product product = getModel();

		this.notSoldSeparatelyCheckBox.setSelection(product.isNotSoldSeparately());
		if (isProductBundle()) {
			ProductBundle bundle = (ProductBundle) product;

			this.storeVisible.setSelection(!bundle.isBundleHidden());
			this.enableDatePicker.setDate(bundle.getBundleStartDate());
			this.disableDatePicker.setDate(bundle.getBundleEndDate());

			if (!wizardMode) {
				this.bundleStoreVisible.setSelection(!bundle.isHidden());
				final Date effectiveStartDate = bundle.getStartDate();
				if (effectiveStartDate != null) {
					this.effectiveEnableDatePicker.setText(DateTimeUtilFactory.getDateUtil().formatAsDateTime(effectiveStartDate));
				}
				final Date effectiveEndDate = bundle.getEndDate();
				if (effectiveEndDate != null) {
					this.effectiveDisableDatePicker.setText(DateTimeUtilFactory.getDateUtil().formatAsDateTime(effectiveEndDate));
				}
			}
		} else {
			this.storeVisible.setSelection(!product.isHidden());
			this.enableDatePicker.setDate(product.getStartDate());
			this.disableDatePicker.setDate(product.getEndDate());
			this.minOrderQtySpinner.setSelection(product.getMinOrderQty());
			if (product.getAvailabilityCriteria() == AvailabilityCriteria.ALWAYS_AVAILABLE) {
				final int alwaysAvailableIndex = 3;
				availabilityText.setText(AVAILABILITY_STRINGS[alwaysAvailableIndex]);
				setExpReleaseDateControlsEnabled(alwaysAvailableIndex);
			} else {
				this.availabilityRuleCombo.setItems(AVAILABILITY_STRINGS_EDITOR_MODE);
				this.availabilityRuleCombo.setText(getAvailabilityRuleText(product));
				// TODO - move the below to policy
				// this.availabilityRuleCombo.setEnabled(product.getAvailabilityCriteria() != AvailabilityCriteria.ALWAYS_AVAILABLE
				// && epState == EpState.EDITABLE);
				setExpReleaseDateControlsEnabled(availabilityRuleCombo.getSelectionIndex());
			}
			this.expectedReleaseDateComp.setDate(product.getExpectedReleaseDate());
		}

	}

	/**
	 * 
	 */
	public void populateForCreateProductWizard() {
		this.storeVisible.setSelection(false);
		getModel().setHidden(true);

		this.notSoldSeparatelyCheckBox.setSelection(false);
		this.enableDatePicker.setDate(new Date());
		if (!isProductBundle()) {
			this.availabilityRuleCombo.setItems(AVAILABILITY_STRINGS);
			this.availabilityRuleCombo.setText(AVAILABILITY_STRINGS[0]);
			setExpReleaseDateControlsEnabled(availabilityRuleCombo.getSelectionIndex());
		}
		getModel().setAvailabilityCriteria(getAvailabilityCriteriaFromIndex(0)); //select the first item in availability combo
	}

	@Override
	public Product getModel() {
		return model;
	}

	private boolean isProductBundle() {
		return getModel() instanceof ProductBundle;
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		final Product product = getModel();
		this.bindingContext = bindingContext;

		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		bindStoreVisibleCheckBox(bindingContext, product, bindingProvider);

		bindNotSoldSeparatelyCheckBox(bindingContext, product, bindingProvider);

		bindEnableDisableDate(bindingContext, product);

		if (!isProductBundle()) {
			// Min Order Quantity
			bindingProvider.bind(bindingContext, this.minOrderQtySpinner, product, "minOrderQty",  //$NON-NLS-1$
					EpValidatorFactory.POSITIVE_INTEGER, null, true);

			if (product.getAvailabilityCriteria() != AvailabilityCriteria.ALWAYS_AVAILABLE || wizardMode) {

				bindAvailabilityRule(bindingContext, product, bindingProvider);

				bindExpectedReleaseDate(bindingContext, product);
			}
		}
	}

	private void bindExpectedReleaseDate(final DataBindingContext bindingContext, final Product product) {
		this.expectedReleaseDateComp.bind(bindingContext, new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE, value -> {

			int selectedIndex = availabilityRuleCombo.getSelectionIndex();
			if (selectedIndex != PRE_ORDER) {
				return Status.OK_STATUS;
			}

			Date releaseDate = expectedReleaseDateComp.getDate();
			if (releaseDate != null && releaseDate.after(new Date())) {
				return Status.OK_STATUS;
			}
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, CoreMessages.get().EpValidatorFactory_ReleaseDateBeforeToday, null);

		}}), product, "expectedReleaseDate");  //$NON-NLS-1$
	}

	private void bindAvailabilityRule(final DataBindingContext bindingContext, final Product product, 
							final EpControlBindingProvider bindingProvider) {
		bindingProvider.bind(bindingContext, this.availabilityRuleCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final int index = (Integer) value;
				if (index >= 0) {
					product.setAvailabilityCriteria(getAvailabilityCriteriaFromIndex(index));
				}
				return Status.OK_STATUS;
			}
		}, true);
	}

	private void bindEnableDisableDate(final DataBindingContext bindingContext, final Product product) {

		// from-to date interbinding for from before to date validation
		final ModifyListener updateModels = (ModifyListener) event -> {
			bindingContext.updateModels(); // re-validate bound events
		};
		enableDatePicker.getSwtText().addModifyListener(updateModels);
		disableDatePicker.getSwtText().addModifyListener(updateModels);

		// Enable date
		enableDatePicker.bind(bindingContext, EpValidatorFactory.DATE_TIME_REQUIRED, product, "startDate"); //$NON-NLS-1$

		// make sure always disable date > enable date for disable date field
		final IValidator disableDateValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME,
				EpValidatorFactory.createDisableDateValidator(enableDatePicker, disableDatePicker) });
		// Disable date
		disableDatePicker.bind(bindingContext, disableDateValidator, product, "endDate");  //$NON-NLS-1$
	}

	private void bindStoreVisibleCheckBox(final DataBindingContext bindingContext, final Product product,
			final EpControlBindingProvider bindingProvider) {
		bindingProvider.bind(bindingContext, this.storeVisible, product, "hidden",  //$NON-NLS-1$
				null, new Converter(Boolean.class, Boolean.class) {
					@Override
					public Object convert(final Object fromObject) {
						return !((Boolean) fromObject);
					}
				}, true);
	}

	private void bindNotSoldSeparatelyCheckBox(final DataBindingContext bindingContext, final Product product,
			final EpControlBindingProvider bindingProvider) {
		bindingProvider.bind(bindingContext, this.notSoldSeparatelyCheckBox, product, "notSoldSeparately",  //$NON-NLS-1$
				null, null, true);
	}

	private AvailabilityCriteria getAvailabilityCriteriaFromIndex(final int index) {
		AvailabilityCriteria criteria = AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK;
		switch (index) {
		case ALWAYS:
			criteria = AvailabilityCriteria.ALWAYS_AVAILABLE;
			break;
		case IN_STOCK:
			criteria = AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK;
			break;
		case PRE_ORDER:
			criteria = AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER;
			break;
		case BACK_ORDER:
			criteria = AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER;
			break;
		default:
			// do nothing
		}
		return criteria;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == availabilityRuleCombo) {
			final int selectionIndex = availabilityRuleCombo.getSelectionIndex();
			setExpReleaseDateControlsEnabled(selectionIndex);
		}
	}

	/**
	 * Sets dependent expected release date components enabled/disabled.
	 * 
	 * @param index availability combo selection index
	 */
	private void setExpReleaseDateControlsEnabled(final int index) {
		setExpectedReleaseDateControlsEnabled(index);
		if (index != PRE_ORDER) {
			expectedReleaseDateComp.setDate(null);
		}
		if (bindingContext != null) {
			bindingContext.updateModels();
		}
	}

	private String getAvailabilityRuleText(final Product product) {
		String availabilityString;
		switch (product.getAvailabilityCriteria()) {
		case ALWAYS_AVAILABLE:
			availabilityString = AVAILABILITY_STRINGS[ALWAYS];
			break;
		case AVAILABLE_WHEN_IN_STOCK:
			availabilityString = AVAILABILITY_STRINGS[IN_STOCK];
			break;
		case AVAILABLE_FOR_PRE_ORDER:
			availabilityString = AVAILABILITY_STRINGS[PRE_ORDER];
			break;
		case AVAILABLE_FOR_BACK_ORDER:
			availabilityString = AVAILABILITY_STRINGS[BACK_ORDER];
			break;
		default:
			availabilityString = "unknown";  //$NON-NLS-1$
			break;
		}
		return availabilityString;
	}

	/**
	 * Sets the control modification listener.
	 * 
	 * @param controlModificationListener the listener
	 */
	public void setControlModificationListener(final ControlModificationListener controlModificationListener) {
		controlPane.setControlModificationListener(controlModificationListener);

		if (notSoldSeparateComposite != null) {
			notSoldSeparateComposite.setControlModificationListener(controlModificationListener);
		}

		if (bundleRulesComposite != null) {
			bundleRulesComposite.setControlModificationListener(controlModificationListener);
		}

		if (effectiveRulesComposite != null) {
			effectiveRulesComposite.setControlModificationListener(controlModificationListener);
		}
	}
	
	@Override
	public void refreshLayout() {
		if (availabilityRuleCombo != null && !availabilityRuleCombo.isDisposed() && !isProductBundle()) {
			final int selectionIndex = availabilityRuleCombo.getSelectionIndex();
			setExpectedReleaseDateControlsEnabled(selectionIndex);
		}
	}
	
	/**
	 * Sets dependent expected release date components enabled/disabled.
	 * @param index index
	 */
	private void setExpectedReleaseDateControlsEnabled(final int index) {
		expectedReleaseDateLabel.setEnabled(index == PRE_ORDER);
		expectedReleaseDateComp.setEnabled(index == PRE_ORDER);
		expectedReleaseDateComp.setVisible(index == PRE_ORDER);
	}

}
