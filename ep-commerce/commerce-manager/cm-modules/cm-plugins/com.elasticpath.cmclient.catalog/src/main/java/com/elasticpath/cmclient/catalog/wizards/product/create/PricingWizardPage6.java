/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.pricelistmanager.controller.listeners.PriceListModelChangedListener;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.BaseAmountSection;
import com.elasticpath.cmclient.pricelistmanager.model.impl.BaseAmountType;
import com.elasticpath.cmclient.core.dto.catalog.PriceListEditorModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;

/**
 * Wizard page for editing pricing information for product/bundle.
 */
public class PricingWizardPage6 extends AbstractPolicyAwareWizardPage<ProductModel> implements PriceListModelChangedListener {


	/** Page ID. **/
	protected static final String PRICING_WIZARD_PAGE6 = "PricingWizardPage6"; //$NON-NLS-1$

	private static final int PAGE_LAYOUT_NUM_COLUMNS = 1;

	private Label priceListNameLabel;
	private Label priceListCurrencyLabel;
	private Label priceListPriorityLabel;
	private CCombo priceListCombo;

	private final BaseAmountSection baseAmountSection;

	private final NewProductPriceListEditorControllerImpl priceListController;
	private final TableSelectionProvider baseAmountTableSelectionProvider = new TableSelectionProvider();

	private final ComboModel comboModel = new ComboModel();

	private final int initialComboSelectionIndex;
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	// Allow us to detect if the user changes the product code
	private String productCodeSnapshot;

	private boolean resetData;

	/**
	 * Constructor.
	 *
	 * @param plas price list assignments for the catalog
	 * @param pageName page name
	 * @param title title
	 * @param description description
	 */
	protected PricingWizardPage6(final List<PriceListAssignmentsDTO> plas, final String pageName, final String title, final String description) {
		super(PAGE_LAYOUT_NUM_COLUMNS, false, pageName, new DataBindingContext());
		this.setDescription(description);
		this.setTitle(title);
		setPageComplete(false);
		priceListController = new NewProductPriceListEditorControllerImpl();
		priceListController.addModelChangedListener(this);

		final List<PriceListAssignmentsDTO> uniquePlas = PlaSortingPolicy.sortAlpha(PlaSortingPolicy.findLowestPriorityPlas(plas));
		this.comboModel.setPlas(uniquePlas);

		initialComboSelectionIndex = PlaSortingPolicy.findLowestPriorityPlaIndex(uniquePlas);
		baseAmountSection = new BaseAmountSection(null, priceListController, baseAmountTableSelectionProvider, null, false, true, true);
	}

	/** Switches the pricelists to repopulate the table. */
	private void switchControllersPricelist(final String priceListDescriptorGuid) {
		final BaseAmountFilter filter = priceListController.getBaseAmountsFilter();
		filter.setObjectType(BaseAmountType.PRODUCT.getType());
		filter.setPriceListDescriptorGuid(priceListDescriptorGuid);
		filter.setObjectGuid(StringUtils.EMPTY);
		final PriceListEditorModel model = getModel().getPriceEditorModel(priceListDescriptorGuid);
		priceListController.setModel(model);
		priceListController.setPriceListDescriptorGuid(priceListDescriptorGuid);
		priceListController.reloadBaseAmountsForManagedProduct(resetData);
		setDescriptionIfChangeSetEnabled();
	}

	private void setDescriptionIfChangeSetEnabled() {
		if (changeSetHelper.isChangeSetsEnabled()) {
			if (changeSetHelper.isMemberOfActiveChangeset(priceListController.getPriceListDescriptor())) {
				this.setDescription(StringUtils.EMPTY);
			} else {
				this.setDescription(CatalogMessages.get().PriceListShouldBeInTheChangeSet);
			}
		}
	}

	private void populatePriceListDescriptorCombo(final CCombo combo) {
		priceListCombo.removeAll();
		comboModel.populate(combo);
	}


	private void populatePriceListLabels(final PriceListAssignmentsDTO dto) {
		priceListNameLabel.setText(dto.getPriceListName());
		priceListCurrencyLabel.setText(dto.getPriceListCurrency().getCurrencyCode());
		priceListPriorityLabel.setText(String.valueOf(dto.getPriority()));
	}

	private void addSelectionListeners() {
		priceListCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final int selectionIndex = ((CCombo) event.getSource()).getSelectionIndex();
				populatePriceListLabels(comboModel.get(selectionIndex));
				switchTableViewerToSelectedPriceList(comboModel.get(selectionIndex).getPriceListGuid());
			}
		});
	}

	private void switchTableViewerToSelectedPriceList(final String priceListGuid) {
		switchControllersPricelist(priceListGuid);
		baseAmountSection.reApplyStatePolicy();
		baseAmountSection.refreshTableViewer();
	}

	@Override
	protected void bindControls() {
		addSelectionListeners();
		baseAmountSection.bindControls(getDataBindingContext());
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		// create container
		final PolicyActionContainer policyActionContainer = addPolicyActionContainer("productPriceBaseAmountWizardPage");  //$NON-NLS-1$

		final IEpLayoutComposite composite = pageComposite.addGridLayoutComposite(1, true, pageComposite.createLayoutData());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		switchControllersPricelist(comboModel.get(initialComboSelectionIndex).getPriceListGuid());

		createPricingTableSection(composite);
		createPricingInfoSection(composite);
		this.setControl(pageComposite.getSwtComposite());
		policyActionContainer.addTarget(state -> baseAmountSection.reApplyStatePolicy());
	}

	private void createPricingTableSection(final IEpLayoutComposite container) {
		// layout for the table area
		final IEpLayoutData tableLayoutData = container.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		final IEpLayoutComposite mainComposite = container.addGridLayoutComposite(2, false, tableLayoutData);
		final IEpLayoutData comboLayoutData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.END, false,
				false, 1, 1);
		priceListCombo = mainComposite.addComboBox(EpState.EDITABLE, comboLayoutData);
		mainComposite.addEmptyComponent(comboLayoutData);
		baseAmountSection.createControls(mainComposite, null);

		priceListController.setManagedProduct(getModel().getProduct());
	}

	private void createPricingInfoSection(final IEpLayoutComposite container) {
		final IEpLayoutComposite pricingInfoComposite = container.addGridLayoutSection(3, CatalogMessages.get().CreateProductWizard_PriceListDetails,
				ExpandableComposite.TITLE_BAR, container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
		final IEpLayoutData labelData = pricingInfoComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);
		final IEpLayoutData labelData2 = pricingInfoComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		pricingInfoComposite.addLabelBold(CatalogMessages.get().CreateProductWizard_PriceListName, labelData);
		priceListNameLabel = pricingInfoComposite.addLabel(StringUtils.EMPTY, labelData2);

		pricingInfoComposite.addLabelBold(CatalogMessages.get().CreateProductWizard_PriceListCurrency, labelData);
		priceListCurrencyLabel = pricingInfoComposite.addLabel(StringUtils.EMPTY, labelData2);

		pricingInfoComposite.addLabelBold(CatalogMessages.get().CreateProductWizard_PriceListPriority, labelData);
		priceListPriorityLabel = pricingInfoComposite.addLabel(StringUtils.EMPTY, labelData2);
	}

	@Override
	protected void populateControls() {
		populatePriceListDescriptorCombo(priceListCombo);
		priceListCombo.select(initialComboSelectionIndex);
		final int selectedItemIndex = priceListCombo.getSelectionIndex();
		populatePriceListLabels(comboModel.get(selectedItemIndex));
		baseAmountSection.populateControls();
		baseAmountSection.refreshTableViewer();
	}

	@Override
	public boolean isPageComplete() {
		return true; //optional page, so return true
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite policyComposite) {
		// do nothing
	}

	/**
	 * Marks the price list name in the combo with a star if the user modified the pricing information in the table.
	 */
	@Override
	public void notifyPriceListModelChanged() {
		comboModel.setDirty(priceListController.getModel().getPriceListDescriptor().getGuid());
		final int selectedItemIndex = priceListCombo.getSelectionIndex();
		populatePriceListDescriptorCombo(priceListCombo);
		priceListCombo.select(selectedItemIndex);
	}


	/**
	 * Updates product code shapshot.
	 */
	protected void updateProductCodeSnapshot() {
		this.productCodeSnapshot = getModel().getProduct().getCode();
	}

	/**
	 * Preloads existing base amount to the table and checks if the product code was changed.
	 */
	public void preloadPricingTable() {
		resetData = productCodeSnapshot != null && !productCodeSnapshot.equals(getModel().getProduct().getCode());
		switchTableViewerToSelectedPriceList(comboModel.get(priceListCombo.getSelectionIndex()).getPriceListGuid());
		updateProductCodeSnapshot();
	}

	@Override
	protected boolean isCreateCompositeBlock() {
		return false;
	}

}
