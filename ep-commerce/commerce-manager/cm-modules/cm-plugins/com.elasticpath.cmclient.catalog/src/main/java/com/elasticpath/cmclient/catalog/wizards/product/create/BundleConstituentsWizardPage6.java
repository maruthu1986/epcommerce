/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.dialogs.product.ProductBundleConstituentsDialog;
import com.elasticpath.cmclient.catalog.helpers.BundleConstituentTableContentProvider;
import com.elasticpath.cmclient.catalog.helpers.BundleConstituentTableLabelProvider;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.service.catalog.BundleValidator;

/**
 * The Six page of wizard, for populating bundle constituents.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class BundleConstituentsWizardPage6 extends AbstractEPWizardPage<ProductModel> implements SelectionListener {

	/**
	 * Page ID.
	 */
	protected static final String CREATE_BUNDLE_CONSTITUENTS_WIZARD_PAGE6 = "CreateProductBundleConstituentWizardPage6"; //$NON-NLS-1$

	private static final int COLUMN_WIDTH_QTY = 60;

	private static final int COLUMN_WIDTH_PRODUCT_TYPE = 150;

	private static final int COLUMN_WIDTH_PRODUCT_NAME = 140;

	private static final int COLUMN_WIDTH_PRODUCT_CODE = 120;

	private static final int COLUMN_WIDTH_ICON = 25;

	private static final int COLUMN_WIDTH_SKU_CODE = 120;

	private static final int COLUMN_WIDTH_SKU_CONFIGURATION_NAME = 130;

	private static final int BASE_AMOUNT_TABLE_SIZE = 100;

	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	private static final String CONSTITUENTS_TABLE = "Constituents"; //$NON-NLS-1$

	private IEpTableViewer productConstituentsTableViewer;

	private Button addButton;

	private Button editButton;

	private Button removeButton;

	private Button moveUpButton;

	private Button moveDownButton;

	private static final String[] AVAILABILITY_STRINGS_EDITOR_MODE = new String[]{
			CatalogMessages.get().Bundle_Selection_Rule_All,
			CatalogMessages.get().Bundle_Selection_Rule_One,
			CatalogMessages.get().Bundle_Selection_Rule_X
	};

	private Text selectionRuleParam;

	private CCombo selectRuleCombo;

	private Label selectionRuleParamLabel;

	private EpValueBinding selectionBinding;

	private final BundleValidator bundleValidator;

	/**
	 * Constructor.
	 *
	 * @param pageName    the page name
	 * @param title       the page title
	 * @param description the page description
	 */
	protected BundleConstituentsWizardPage6(final String pageName, final String title, final String description) {
		super(2, false, pageName, new DataBindingContext());

		this.setDescription(description);
		this.setTitle(title);
		bundleValidator = ServiceLocator.getService("bundleValidator"); //$NON-NLS-1$
	}

	@Override
	protected void bindControls() {
		this.productConstituentsTableViewer.getSwtTableViewer().addSelectionChangedListener(createSelectionChangedListener());

		this.moveUpButton.addSelectionListener(createMoveUpAction());
		this.moveDownButton.addSelectionListener(createMoveDownAction());
		this.addButton.addSelectionListener(createAddAction());
		this.editButton.addSelectionListener(createEditAction());
		this.removeButton.addSelectionListener(createRemoveAction());

		EpWizardPageSupport.create(this, getDataBindingContext());
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		this.productConstituentsTableViewer = pageComposite
			.addTableViewer(false, EpState.EDITABLE, createLayoutData(pageComposite, true, true), CONSTITUENTS_TABLE);
		this.productConstituentsTableViewer.addTableColumn("", COLUMN_WIDTH_ICON); //$NON-NLS-1$
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_ProductType, COLUMN_WIDTH_PRODUCT_TYPE);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_ProductCode, COLUMN_WIDTH_PRODUCT_CODE);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_ProductName, COLUMN_WIDTH_PRODUCT_NAME);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_SkuCode, COLUMN_WIDTH_SKU_CODE);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_SkuConfiguration,
				COLUMN_WIDTH_SKU_CONFIGURATION_NAME);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_Qty, COLUMN_WIDTH_QTY);

		this.productConstituentsTableViewer.setContentProvider(new BundleConstituentTableContentProvider());
		this.productConstituentsTableViewer.setLabelProvider(new BundleConstituentTableLabelProvider());

		this.applyTableViewHeightHint();

		final IEpLayoutComposite buttonsComposite = pageComposite.addGridLayoutComposite(1, false, createLayoutData(pageComposite, true, true));

		editButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_EditButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_OPEN), EpState.EDITABLE,
				pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));

		addButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_AddButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD), EpState.EDITABLE,
				pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));

		removeButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_RemoveButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE), EpState.EDITABLE,
				pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));

		buttonsComposite.addEmptyComponent(null);

		moveUpButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_MoveUpButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_UP_ARROW), EpState.EDITABLE,
				pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));

		moveDownButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_MoveDownButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_DOWN_ARROW), EpState.EDITABLE,
				pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));

		createSelectionRuleComposite(pageComposite);

		this.setControl(pageComposite.getSwtComposite());
	}

	private void createSelectionRuleComposite(final IEpLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 1, 1);

		final IEpLayoutComposite selectionRulesComposite = pageComposite.addGridLayoutComposite(2, false,
				createLayoutData(pageComposite, true, true));
		selectionRulesComposite.addLabel(CatalogMessages.get().Bundle_Selection_Rule, labelData);
		selectRuleCombo = selectionRulesComposite.addComboBox(EpState.EDITABLE, fieldData);
		selectRuleCombo.setItems(AVAILABILITY_STRINGS_EDITOR_MODE);
		selectRuleCombo.addSelectionListener(this);

		selectionRuleParamLabel = selectionRulesComposite.addLabel(CatalogMessages.get().Bundle_Selection_Parameter, labelData);
		selectionRuleParam = selectionRulesComposite.addTextField(EpState.EDITABLE, fieldData);
	}


	/**
	 * Sets dependent order limit components enabled/disabled.
	 *
	 * @param index availability combo selection index
	 */
	private void setSelectionParamBoxEnabled(final int index, final Control... controls) {
		final boolean enabled = (index > 1);
		if (selectionBinding == null) {
			ObservableUpdateValueStrategy strategy = getSelectionUpdateStrategy();
			selectionBinding = EpControlBindingProvider.getInstance().bind(getDataBindingContext(), selectionRuleParam,
					new CompoundValidator(EpValidatorFactory.POSITIVE_INTEGER, EpValidatorFactory.REQUIRED), null, strategy, true);
		}
		for (Control control : controls) {
			control.setEnabled(enabled);
			control.setVisible(enabled);
			getDataBindingContext().removeBinding(selectionBinding.getBinding());
			selectionBinding.getDecoration().hide();
		}
		if (enabled) {
			getDataBindingContext().addBinding(selectionBinding.getBinding());
		}
	}

	private ObservableUpdateValueStrategy getSelectionUpdateStrategy() {
		return new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				ProductBundle bundle = getProductBundleModel();

				bundle.setSelectionRule(getSelectionRuleFromRuleInput(bundle));
				return Status.OK_STATUS;
			}

		};
	}

	private SelectionRule getSelectionRuleFromRuleInput(final ProductBundle bundle) {
		SelectionRule selectionRule = bundle.getSelectionRule();
		if (selectionRule == null) {
			selectionRule = ServiceLocator.getService(ContextIdNames.BUNDLE_SELECTION_RULE);
		}

		if (selectRuleCombo.getSelectionIndex() > 1) {
			String text = selectionRuleParam.getText();
			if (StringUtils.isEmpty(text)) {
				selectionRule.setParameter(0);
				return selectionRule;
			}
			int parseInt = Integer.parseInt(text);
			selectionRule.setParameter(parseInt);
		} else {
			selectionRule.setParameter(selectRuleCombo.getSelectionIndex());
		}
		return selectionRule;
	}

	private IEpLayoutData createLayoutData(final IEpLayoutComposite controlPane, final boolean grabExcessHSpace, final boolean grabExcessVSpace) {
		return controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, grabExcessHSpace, grabExcessVSpace);
	}

	private void applyTableViewHeightHint() {
		((GridData) this.productConstituentsTableViewer.getSwtTable().getLayoutData()).heightHint = BASE_AMOUNT_TABLE_SIZE;
	}

	@Override
	protected void populateControls() {
		productConstituentsTableViewer.setInput(getProductBundleModel());

		int modelSelectionIndex = 0;
		selectRuleCombo.select(modelSelectionIndex);
		setSelectionParamBoxEnabled(modelSelectionIndex, selectionRuleParam, selectionRuleParamLabel);
		refreshButtons();
	}

	private ProductBundle getProductBundleModel() {
		return (ProductBundle) getModel().getProduct();
	}

	private void refreshButtons() {
		this.moveUpButton.setEnabled(false);
		this.moveDownButton.setEnabled(false);

		final int selectionIndex = this.productConstituentsTableViewer.getSwtTable().getSelectionIndex();
		final boolean enableEditRemove = selectionIndex >= 0;

		this.editButton.setEnabled(enableEditRemove);
		this.removeButton.setEnabled(enableEditRemove);

		if (this.productConstituentsTableViewer.getSwtTable().getItemCount() > 1) {
			final int lastItemIndex = this.productConstituentsTableViewer.getSwtTable().getItemCount() - 1;

			if (selectionIndex == 0) {
				this.moveDownButton.setEnabled(true);
			} else if (selectionIndex > 0 && selectionIndex < lastItemIndex) {
				this.moveUpButton.setEnabled(true);
				this.moveDownButton.setEnabled(true);
			} else if (selectionIndex == lastItemIndex) {
				this.moveUpButton.setEnabled(true);
			}
		}
	}

	private SelectionListener createMoveUpAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final BundleConstituent selectedBundleConstituent = getSelectedBundleConstituent();
				if (selectedBundleConstituent != null) {
					getProductBundleModel().moveConstituentUp(selectedBundleConstituent);

					refreshWizardPage();
					refreshButtons();
				}
			}
		};
	}

	private SelectionListener createMoveDownAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final BundleConstituent selectedBundleConstituent = getSelectedBundleConstituent();
				if (selectedBundleConstituent != null) {
					getProductBundleModel().moveConstituentDown(selectedBundleConstituent);

					refreshWizardPage();
					refreshButtons();
				}
			}
		};
	}

	private SelectionAdapter createAddAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final BundleConstituent bundleConstituent = ServiceLocator.getService(ContextIdNames.BUNDLE_CONSTITUENT);
				ProductBundleConstituentsDialog dialog = new ProductBundleConstituentsDialog(getShell(), bundleConstituent, false,
						getProductBundleModel());
				if (dialog.open() == Window.OK) {
					getProductBundleModel().addConstituent(bundleConstituent);

					refreshWizardPage();
				}
			}
		};
	}

	private SelectionAdapter createEditAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				ProductBundleConstituentsDialog dialog = new ProductBundleConstituentsDialog(getShell(), getSelectedBundleConstituent(), true,
						getProductBundleModel());
				if (dialog.open() == Window.OK) {
					refreshWizardPage();
				}
			}
		};
	}

	private SelectionAdapter createRemoveAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				BundleConstituent selectedBundleConstituent = getSelectedBundleConstituent();
				if (selectedBundleConstituent != null) {
					getProductBundleModel().removeConstituent(selectedBundleConstituent);

					refreshWizardPage();
				}
			}
		};
	}

	private BundleConstituent getSelectedBundleConstituent() {
		IStructuredSelection selection = (IStructuredSelection) productConstituentsTableViewer.getSwtTableViewer().getSelection();
		if (selection == null || selection.isEmpty()) {
			return null;
		}
		return (BundleConstituent) selection.getFirstElement();
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return event -> refreshButtons();
	}

	private void refreshWizardPage() {
		productConstituentsTableViewer.getSwtTableViewer().refresh();
		this.getWizard().getContainer().updateButtons();
	}


	@Override
	public void widgetDefaultSelected(final SelectionEvent arg0) {
		setSelectionParamBoxEnabled(selectRuleCombo.getSelectionIndex(), selectionRuleParam, selectionRuleParamLabel);
	}

	@Override
	public void widgetSelected(final SelectionEvent arg0) {
		setSelectionParamBoxEnabled(selectRuleCombo.getSelectionIndex(), selectionRuleParam, selectionRuleParamLabel);
		ProductBundle bundle = getProductBundleModel();
		bundle.setSelectionRule(getSelectionRuleFromRuleInput(bundle));
	}

	@Override
	public IWizardPage getNextPage() {
		IWizardPage page = super.getNextPage();
		if (page instanceof PricingWizardPage6) {
			PricingWizardPage6 pricingPage = (PricingWizardPage6) super.getNextPage();
			pricingPage.preloadPricingTable(); // Product code may have changed - show the right prices
		}
		return page;
	}

	@Override
	public boolean canFlipToNextPage() {

		if (bundleValidator.isBundleEmpty(getProductBundleModel())) {
			return false;
		}

		if (!bundleValidator.areAllBundleConstituentsOfTheSamePricingMechanismType(getProductBundleModel())) {
			MessageDialog.openError(this.getShell(), CatalogMessages.get().ProductBundleInvalidPricingDialogTitle,
					createErrorMessageForBundlesWithDifferentPricingType());
			return false;
		}

		if (bundleValidator.doesAssignedBundleContainRecurringCharge(getProductBundleModel())) {
			MessageDialog.openError(this.getShell(), CatalogMessages.get().ProductSaveRecurringChargeOnAssignedBundleErrorTitle,
					createErrorMessageForAssignedBundlesWithRecurringChargeItems());
			return false;
		}

		return super.canFlipToNextPage();
	}

	private String createErrorMessageForBundlesWithDifferentPricingType() {
		StringBuilder incorrectBundleNames = new StringBuilder();
		boolean hasMoreOneBundleInMessage = false;

		ProductBundle bundle = getProductBundleModel();
		String incorrectBundlePricing = StringUtils.EMPTY;

		List<BundleConstituent> constituents = bundle.getConstituents();
		for (BundleConstituent bundleConstituent : constituents) {
			Product constituentProduct = bundleConstituent.getConstituent().getProduct();
			if (constituentProduct instanceof ProductBundle) {
				ProductBundle constituentProductBundle = (ProductBundle) constituentProduct;
				if (constituentProductBundle.isCalculated().booleanValue() != bundle.isCalculated().booleanValue()) {
					if (hasMoreOneBundleInMessage) {
						incorrectBundleNames.append(NEW_LINE);
					}
					incorrectBundleNames.append(constituentProductBundle.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
					hasMoreOneBundleInMessage = true;
					incorrectBundlePricing = CatalogMessages.get().getBundleTypeString(constituentProductBundle);
				}
			}
		}

		return
			NLS.bind(CatalogMessages.get().ProductBundleInvalidPricingDialogMessageWithParams,
			new Object[]{incorrectBundlePricing,
						CatalogMessages.get().getBundleTypeString(getProductBundleModel()), incorrectBundleNames.toString()});
	}

	private String createErrorMessageForAssignedBundlesWithRecurringChargeItems() {
		StringBuilder recurringChargeItems = new StringBuilder();
		boolean hasMoreOneBundleInMessage = false;

		ProductBundle bundle = getProductBundleModel();
		List<BundleConstituent> constituents = bundle.getConstituents();
		for (BundleConstituent bundleConstituent : constituents) {
			ConstituentItem constituentItem = bundleConstituent.getConstituent();
			// check if the constituent item can be added to the bundle
			if (bundleValidator.isRecurringChargeItemOnAssignedBundle(bundle, constituentItem)) {
				if (hasMoreOneBundleInMessage) {
					recurringChargeItems.append(NEW_LINE);
				}
				recurringChargeItems.append(constituentItem.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
				hasMoreOneBundleInMessage = true;
			}
		}

		return
			NLS.bind(CatalogMessages.get().ProductSaveRecurringChargeOnAssignedBundleErrorMsgWithParam,
			new Object[]{recurringChargeItems});
	}
}
