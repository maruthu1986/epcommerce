/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.price.model.CachedPriceAdjustmentModelService;
import com.elasticpath.cmclient.catalog.editors.price.model.PriceAdjustmentModelRoot;
import com.elasticpath.cmclient.catalog.editors.price.model.PriceAdjustmentSummaryCalculator;
import com.elasticpath.cmclient.catalog.helpers.EventManager;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.core.dto.catalog.PriceListSectionModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * UI view for the PriceAdjustmentPage.
 */
public class PriceAdjustmentPart extends AbstractPolicyAwareEditorPageSectionPart implements PriceAdjustmentSummaryUpdater {
	private static final String COLON = ":"; //$NON-NLS-1$

	private static final String EMPTY_PRICE_STRING = "   -"; //$NON-NLS-1$

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private IPolicyTargetLayoutComposite container;

	private final PriceAdjustmentPage page;

	private PriceAdjustmentTree priceAdjustmentTree;

	private Label priceLabel;

	private ComboViewer priceListViewer;

	private Label savingLabel;

	private final PriceAdjustmentSummaryCalculator summaryCaculator;

	private Label totalAfterAdjustmentLabel;

	private Label totalBeforeAdjustmentLabel;

	private CachedPriceAdjustmentModelService cachedModelService;

	private final SelectedElement currentSelectedElement;

	/**
	 * @return the summaryCaculator
	 */
	protected PriceAdjustmentSummaryCalculator getSummaryCaculator() {
		return summaryCaculator;
	}

	private CachedPriceAdjustmentModelService getCachedModelService() {
		if (cachedModelService == null) {
			cachedModelService = new CachedPriceAdjustmentModelService(getProductBundle());
		}

		return cachedModelService;
	}

	/**
	 * Constructor.
	 *
	 * @param editor              editor
	 * @param page                page
	 * @param lastSelectedElement last selection state.
	 */
	public PriceAdjustmentPart(final AbstractCmClientFormEditor editor, final PriceAdjustmentPage page,
							   final SelectedElement lastSelectedElement) {
		super(page, editor, ExpandableComposite.NO_TITLE);
		this.page = page;
		currentSelectedElement = lastSelectedElement;
		summaryCaculator = new PriceAdjustmentSummaryCalculator(this);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		priceListViewer.addSelectionChangedListener(createPriceListChangedListener());
		if (priceListViewer.getCCombo().getItemCount() > 0) {
			Object selectedElement = currentSelectedElement.getElement();
			if (selectedElement == null) {
				selectedElement = priceListViewer.getElementAt(0);
			}
			priceListViewer.setSelection(new StructuredSelection(selectedElement));
		}
		priceAdjustmentTree.bindControls(bindingContext);
	}

	@Override
	public void commit(final boolean onSave) {
		getCachedModelService().saveCachedPriceAdjustmentModels();
		super.commit(onSave);
	}

	private void createBundleInfoGroup(final IPolicyTargetLayoutComposite parentComposite, final ProductBundle bundle, final Locale locale,
									   final PolicyActionContainer defaultControls) {
		IPolicyTargetLayoutComposite bundleNameComposite = parentComposite.addGridLayoutComposite(2, false, createLayoutData(parentComposite, true,
				false), defaultControls);
		bundleNameComposite.addLabelBold(CatalogMessages.get().ProductPricePage_ProductName, createLayoutData(bundleNameComposite, false, false),
				defaultControls);
		String displayName = bundle.getDisplayName(locale);
		if (displayName == null) {
			displayName = StringUtils.EMPTY;
		}
		bundleNameComposite.addLabel(displayName, createLayoutData(bundleNameComposite, true, false), defaultControls);

		IPolicyTargetLayoutComposite priceComposite = parentComposite.addGridLayoutComposite(1, false,
				createLayoutData(parentComposite, true, false), defaultControls);
		IEpLayoutData priceLabelLayoutData = priceComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, true, false);
		priceLabel = priceComposite.addLabel(EMPTY_STRING, priceLabelLayoutData, defaultControls);

		addCompositesToRefresh(priceComposite.getSwtComposite());
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		EventManager.getInstance().addListener(getProductBundle(), summaryCaculator);

		client.addDisposeListener((DisposeListener) event -> dispose());

		PolicyActionContainer navigationControls = addPolicyActionContainer("navigationControls"); //$NON-NLS-1$
		PolicyActionContainer priceAdjustmentEditingControls = addPolicyActionContainer("priceAdjustmentEditingControls"); //$NON-NLS-1$
		container = PolicyTargetCompositeFactory.wrapLayoutComposite(CompositeFactory.createGridLayoutComposite(client, 2, false));
		container.setLayoutData(createLayoutData(container, true, true).getSwtLayoutData());

		createPriceListDropdown(container, navigationControls);

		ProductBundle bundle = getProductBundle();
		Locale locale = page.getSelectedLocale();

		IEpLayoutData bundleInforLayoutData = container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		IPolicyTargetLayoutComposite bundleInfoComposite = container.addGridLayoutComposite(2,
				true, bundleInforLayoutData, priceAdjustmentEditingControls);
		addCompositesToRefresh(bundleInfoComposite.getSwtComposite());
		createBundleInfoGroup(bundleInfoComposite, bundle, locale, priceAdjustmentEditingControls);

		IEpLayoutData priceAdjustmentGroupLayoutData = container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 2, 1);
		IPolicyTargetLayoutComposite priceAdjustmentGroup = container.addGridLayoutComposite(1, false, priceAdjustmentGroupLayoutData, null);
		addCompositesToRefresh(priceAdjustmentGroup.getSwtComposite());
		createPriceAdjustmentTree(this, priceAdjustmentGroup);

		IEpLayoutData priceSummaryGroupLayoutData = container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		IPolicyTargetLayoutComposite priceSummaryGroup = container.addGridLayoutComposite(2, false, priceSummaryGroupLayoutData, null);
		addCompositesToRefresh(priceSummaryGroup.getSwtComposite());
		createPriceSummaryGroup(priceSummaryGroup, priceAdjustmentEditingControls);

		addCompositesToRefresh(container.getSwtComposite());
	}

	private IEpLayoutData createLayoutData(final IPolicyTargetLayoutComposite container, final boolean grabExcessHSpace,
										   final boolean grabExcessVSpace) {
		return container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, grabExcessHSpace, grabExcessVSpace);
	}

	private void createPriceAdjustmentTree(final PriceAdjustmentPart priceAdjustmentPart, final IPolicyTargetLayoutComposite parentComposite) {
		priceAdjustmentTree = new PriceAdjustmentTree(priceAdjustmentPart);
		priceAdjustmentTree.createControls(parentComposite.getLayoutComposite());
	}

	private ISelectionChangedListener createPriceListChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final PriceListDescriptorDTO priceListDescriptor = (PriceListDescriptorDTO) ((StructuredSelection) event.getSelection())
						.getFirstElement();

				currentSelectedElement.setElement(priceListDescriptor);
				priceLabel.setText(EMPTY_STRING);

				Display display = page.getEditorSite().getShell().getDisplay();
				if (display == null || display.isDisposed()) {
					return;
				}

				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						final PriceAdjustmentModelRoot priceAdjustmentModelRoot = getCachedModelService().getPriceAdjustmentModel(
								priceListDescriptor);

						priceAdjustmentTree.updatePrices(priceAdjustmentModelRoot);

						setPrice(priceAdjustmentModelRoot.getPriceListDescriptorDto().getCurrencyCode(), getPriceString(priceAdjustmentModelRoot
								.getPrice()));

						EventManager.getInstance().fireEvent(
								getProductBundle(),
								new PropertyChangeEvent(this, PriceAdjustmentSummaryCalculator.PRICE_CHANGED_PROPERTY, null,
										priceAdjustmentModelRoot));
					}
				});

			}
		};
	}

	private void createPriceListDropdown(final IPolicyTargetLayoutComposite parentComposite, final PolicyActionContainer defaultControls) {
		parentComposite.addEmptyComponent(createLayoutData(parentComposite, true, false), null);
		IEpLayoutData createLayoutData = parentComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, true, false, 1, 1);
		IPolicyTargetLayoutComposite priceListComposite = parentComposite.addGridLayoutComposite(2, false, createLayoutData, null);

		IEpLayoutData priceListViewerLabelLayoutData = priceListComposite.createLayoutData(IEpLayoutData.END,
				IEpLayoutData.CENTER);
		priceListComposite.addLabel(CatalogMessages.get().ProductBundlePriceAdjustmentPriceList + COLON,
				priceListViewerLabelLayoutData, defaultControls);

		IEpLayoutData priceListViewerLayoutData = priceListComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		priceListViewer = new ComboViewer(priceListComposite.addComboBox(priceListViewerLayoutData, defaultControls));
		priceListViewer.setContentProvider(new ArrayContentProvider());
		priceListViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				PriceListDescriptorDTO priceListDescriptor = (PriceListDescriptorDTO) element;
				return priceListDescriptor.getName().trim() + " (" + priceListDescriptor.getCurrencyCode().trim() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	}

	private void createPriceSummaryGroup(final IPolicyTargetLayoutComposite parentComposite, final PolicyActionContainer defaultControls) {
		parentComposite.addEmptyComponent(createLayoutData(parentComposite, true, false), null);
		IEpLayoutData createLayoutData = parentComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, true, false, 1, 1);
		IPolicyTargetLayoutComposite priceAdjustmentGroup = parentComposite.addGridLayoutComposite(2, false, createLayoutData, null);

		IEpLayoutData alignBeginningLayoutData = priceAdjustmentGroup.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, false, 1,
				1);
		IEpLayoutData alignEndingLayoutData = priceAdjustmentGroup.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false, 1, 1);

		priceAdjustmentGroup.addLabel(CatalogMessages.get().ProductBundlePriceAdjustmentListPriceBasedOnSelection + COLON, alignBeginningLayoutData,
				defaultControls);
		totalBeforeAdjustmentLabel = priceAdjustmentGroup.addLabel(EMPTY_STRING, alignEndingLayoutData, defaultControls);

		priceAdjustmentGroup.addLabel(CatalogMessages.get().ProductBundlePriceAdjustmentTotalWithAdjustment + COLON, alignBeginningLayoutData,
				defaultControls);
		totalAfterAdjustmentLabel = priceAdjustmentGroup.addLabel(EMPTY_STRING, alignEndingLayoutData, defaultControls);

		priceAdjustmentGroup.addLabel(CatalogMessages.get().ProductBundlePriceAdjustmentSavings + COLON, alignBeginningLayoutData, defaultControls);
		savingLabel = priceAdjustmentGroup.addLabel(EMPTY_STRING, alignEndingLayoutData, defaultControls);
	}

	@Override
	public void dispose() {
		EventManager.getInstance().removeListener(getProductBundle(), summaryCaculator);
		super.dispose();
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(1, true);
	}

	@Override
	protected Object getLayoutData() {
		return new GridData(GridData.FILL, GridData.FILL, true, true);
	}

	@Override
	public ProductModel getModel() {
		return (ProductModel) super.getModel();
	}

	/**
	 * Gets {@link PriceAdjustmentPage}.
	 *
	 * @return {@link PriceAdjustmentPage}
	 */
	public PriceAdjustmentPage getPage() {
		return page;
	}

	private String getPriceString(final BigDecimal price) {
		if (price == null) {
			return EMPTY_PRICE_STRING;
		}

		return String.valueOf(price);
	}

	/**
	 * Gets the product bundle attached.
	 *
	 * @return {@link ProductBundle}
	 */
	public ProductBundle getProductBundle() {
		return (ProductBundle) getModel().getProduct();
	}

	@Override
	protected void populateControls() {
		List<PriceListDescriptorDTO> priceListDescriptors = new ArrayList<>();
		for (List<PriceListSectionModel> models : getModel().getPriceListSectionModels().values()) {
			for (PriceListSectionModel model : models) {
				priceListDescriptors.add(model.getPriceListDescriptorDTO());
			}
		}

		Collections.sort(priceListDescriptors, Comparator.comparing(PriceListDescriptorDTO::getName));

		priceListViewer.setInput(priceListDescriptors.toArray());
	}

	private void setPrice(final String currency, final String price) {
		priceLabel.setText(CatalogMessages.get().ProductBundlePriceAdjustmentFrom + " " + currency + " " + price); //$NON-NLS-1$ //$NON-NLS-2$
		refreshLayout();
	}

	@Override
	public void updatePriceAdjustmentSummary(final BigDecimal listPricesBasedOnSelections, final BigDecimal totalWithAdjustments,
											 final BigDecimal savings) {
		totalBeforeAdjustmentLabel.setText(getPriceString(listPricesBasedOnSelections));
		totalAfterAdjustmentLabel.setText(getPriceString(totalWithAdjustments));
		savingLabel.setText(getPriceString(savings));

		refreshLayout();
	}
}
