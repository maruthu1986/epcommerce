/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.price.model.PriceAdjustmentModel;
import com.elasticpath.cmclient.catalog.editors.price.model.PriceAdjustmentModelRoot;
import com.elasticpath.cmclient.catalog.editors.price.model.PriceAdjustmentSummaryCalculator;
import com.elasticpath.cmclient.catalog.editors.product.ProductEditor;
import com.elasticpath.cmclient.catalog.helpers.EventManager;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.service.ChangeSetEventService;
import com.elasticpath.cmclient.core.ui.framework.AutoResizeTreeTableLayout;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTreeColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTreeViewer;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyTargetImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.domain.catalog.Product;

/**
 * A widget for displaying a tree view of price adjustments. Wrapper for a TreeViewer.
 *
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.GodClass" })
public class PriceAdjustmentTree extends AbstractStatePolicyTargetImpl {

	private static final Logger LOG = Logger.getLogger(PriceAdjustmentTree.class);

	private StatePolicy statePolicy;

	private static final int RADIO_COLUMN = 0;
	private static final int PRODUCT_NAME_COLUMN = 1;
	private static final int SKU_CODE_COLUMN = 2;
	private static final int SKU_CONFIGURATION_COLUMN = 3;
	private static final int QUANTITY_COLUMN = 4;
	private static final int ITEM_PRICE_COLUMN = 5;
	private static final int PRICE_ADJUSTMENT_COLUMN = 6;

	private static final int COLUMN_WIDTH_DEFAULT_PIXELS = 0;
	private static final int STRING_COLUMN_WIDTH_DEFAULT_RATIO = 1;
	private static final int PRODUCT_NAME_WIDTH_RATIO = 2;

	private final PriceAdjustmentSelectionController selectionController;

	private static final String[] COLUMN_HEADERS = new String[] {
			"", //$NON-NLS-1$
			CatalogMessages.get().ProductBundlePriceAdjustmentColumnHeader_Constituent,
			CatalogMessages.get().ProductBundlePriceAdjustmentColumnHeader_SkuCode,
			CatalogMessages.get().ProductBundlePriceAdjustmentColumnHeader_SkuConfiguration,
			CatalogMessages.get().ProductBundlePriceAdjustmentColumnHeader_Quantity,
			CatalogMessages.get().ProductBundlePriceAdjustmentColumnHeader_ItemPrices,
			CatalogMessages.get().ProductBundlePriceAdjustmentColumnHeader_PriceAdjustments };

	private IEpTreeColumn[] epColumns = new IEpTreeColumn[COLUMN_HEADERS.length];


	private static final int[] COLUMN_STYLES = new int[] {
			SWT.CENTER,
			SWT.LEFT,
			SWT.RIGHT,
			SWT.RIGHT,
			SWT.RIGHT,
			SWT.RIGHT,
			SWT.RIGHT
	};

	private final Locale locale;

	/** The part is the parent of the tree. */
	private final PriceAdjustmentPart part;

	/** Component being wrapper. Displays the tree. */
	private TreeViewer priceListAdjustmentTreeViewer;

	private IEpTreeViewer epTreeViewer;

	private AutoResizeTreeTableLayout autoTableLayout;

	private PolicyActionContainer priceAdjustmentTreeContainer;

	private IPolicyTargetLayoutComposite controlPane;

	/**
	 * Creates an empty PriceAdjustmentTree wrapper. Initialization takes place in createControls.
	 *
	 * @param priceAdjustmentPart part that contains the tree
	 */
	public PriceAdjustmentTree(final PriceAdjustmentPart priceAdjustmentPart) {
		this.part = priceAdjustmentPart;
		this.locale = priceAdjustmentPart.getPage().getSelectedLocale();
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
		this.selectionController = new PriceAdjustmentSelectionController(priceAdjustmentPart.getProductBundle());
	}

	/**
	 * Create Controls.
	 * @param epLayoutComposite the layout composite
	 */
	public void createControls(final IEpLayoutComposite epLayoutComposite) {
		priceAdjustmentTreeContainer = addPolicyActionContainer("priceAdjustmentTreeContainer"); //$NON-NLS-1$

		controlPane = PolicyTargetCompositeFactory.wrapLayoutComposite(epLayoutComposite);
		IEpLayoutData layoutData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 2, 1);
		controlPane.setLayoutData(layoutData.getSwtLayoutData());

		createTreeViewer(priceAdjustmentTreeContainer, controlPane, layoutData);

		ChangeSetEventService.getInstance().addChangeEventListener(event -> applyStatePolicy());

		fireStatePolicyTargetActivated();
	}

	private void createTreeViewer(final PolicyActionContainer priceAdjustmentTreeContainer,
			final IPolicyTargetLayoutComposite controlPane, final IEpLayoutData layoutData) {
		controlPane.getEpControlFactory().setFormStyle(false);
		epTreeViewer = controlPane.addTreeViewer(false, layoutData, priceAdjustmentTreeContainer);
		priceListAdjustmentTreeViewer = epTreeViewer.getSwtTreeViewer();

		createColumns(epTreeViewer);

		setTreeViewerProperties(priceListAdjustmentTreeViewer);
		setTreeTableLayout(priceListAdjustmentTreeViewer.getTree());

		addCellEditorsTo(priceListAdjustmentTreeViewer);
	}

	private void createColumns(final IEpTreeViewer altTreeViewer) {
		final int defaultWidth = 20;
		for (int i = 0; i < COLUMN_HEADERS.length; ++i) {
			epColumns[i] = altTreeViewer.addColumn(COLUMN_HEADERS[i], defaultWidth, COLUMN_STYLES[i]);
		}
		altTreeViewer.getSwtTreeViewer().setColumnProperties(COLUMN_HEADERS);
	}

	private void setTreeViewerProperties(final TreeViewer treeViewer) {
		treeViewer.setContentProvider(new TreeTableContentProvider());
		treeViewer.setLabelProvider(new TreeTableLabelProvider());
		treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
		treeViewer.getControl().addMouseListener(createMouseListener());
	}

	/**
	 * Update prices according to the price adjustment model.
	 *
	 * @param priceAdjustmentModel priceAdjustmentModel
	 */
	public void updatePrices(final PriceAdjustmentModelRoot priceAdjustmentModel) {
		List<Object> policyDependent = Arrays.asList(
				new Object[] {priceAdjustmentModel.getPriceListDescriptorDto(), priceAdjustmentModel.getProduct()});
		priceAdjustmentTreeContainer.setPolicyDependent(policyDependent);
		reinitStatePolicy();

		selectionController.setInitialStates(priceAdjustmentModel);
		priceListAdjustmentTreeViewer.setInput(priceAdjustmentModel);
		autoTableLayout.controlResized(null);

		applyStatePolicy();
	}

	/**
	 * Gets the price adjustment model root.
	 *
	 * @return {@link PriceAdjustmentModelRoot}.
	 */
	public PriceAdjustmentModelRoot getPriceAdjustmentModelRoot() {
		return (PriceAdjustmentModelRoot) priceListAdjustmentTreeViewer.getInput();
	}

	private void addCellEditorsTo(final TreeViewer priceListAdjustmentTreeViewer) {
		priceListAdjustmentTreeViewer.setCellEditors(createCellEditors(priceListAdjustmentTreeViewer.getTree()));
	}

	private void setTreeTableLayout(final Tree tree) {
		autoTableLayout = new AutoResizeTreeTableLayout(tree);
		autoTableLayout.addColumnData(new ColumnPixelData(COLUMN_WIDTH_DEFAULT_PIXELS));
		autoTableLayout.addColumnData(new ColumnWeightData(PRODUCT_NAME_WIDTH_RATIO));
		autoTableLayout.addColumnData(new ColumnWeightData(STRING_COLUMN_WIDTH_DEFAULT_RATIO));
		autoTableLayout.addColumnData(new ColumnWeightData(STRING_COLUMN_WIDTH_DEFAULT_RATIO));
		autoTableLayout.addColumnData(new ColumnPixelData(COLUMN_WIDTH_DEFAULT_PIXELS));
		autoTableLayout.addColumnData(new ColumnPixelData(COLUMN_WIDTH_DEFAULT_PIXELS));
		autoTableLayout.addColumnData(new ColumnPixelData(COLUMN_WIDTH_DEFAULT_PIXELS));

		tree.setLayout(autoTableLayout);
	}

	private CellEditor[] createCellEditors(final Tree tree) {
		TextCellEditor textEditor = new TextCellEditor(tree);
		return new CellEditor[] { null, null, null, null, textEditor };
	}

	/**
	 * Mouse listener for selection and double clicking on tree. 
	 */
	private MouseListener createMouseListener() {
		return new MouseListener() {
			@Override
			public void mouseUp(final MouseEvent event) {
				if (!isThereAnySelection()) {
					return;
				}

				TreeItem firstTreeItem = getFirstTreeItem();
				Rectangle rect = firstTreeItem.getBounds(0);
				if (rect.contains(event.x, event.y)) {
					selectionController.select(firstTreeItem);
				}
			}

			@Override
			public void mouseDown(final MouseEvent event) {
				// do nothing
			}

			@Override
			public void mouseDoubleClick(final MouseEvent event) {
				if (!isThereAnySelection()) {
					return;
				}

				TreeItem item = getFirstTreeItem();
				Rectangle rect = item.getBounds(1);
				if (rect.contains(event.x, event.y)) {
					openNewEditorFor((PriceAdjustmentModel) item.getData());
				}
			}

			private boolean isThereAnySelection() {
				return priceListAdjustmentTreeViewer.getTree().getSelection().length > 0;
			}

			private TreeItem getFirstTreeItem() {
				return priceListAdjustmentTreeViewer.getTree().getSelection()[0];
			}
		};
	}

	private void openNewEditorFor(final PriceAdjustmentModel priceAdjustmentModel) {
		if (priceAdjustmentModel != null) {
			GuidEditorInput guidEditorInput = new GuidEditorInput(priceAdjustmentModel.getProduct().getCode(), Product.class);
			try {
				part.getPage().getEditorSite().getPage().openEditor(guidEditorInput, ProductEditor.PART_ID);
			} catch (final PartInitException exception) {
				LOG.error("Error opening the Product Editor", exception); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		statePolicy.init(getDependentObject());
		applyStatePolicy();
	}

	/**
	 * Apply the already stored state policy.
	 */
	public void applyStatePolicy() {
		if (statePolicy != null) {
			for (PolicyActionContainer container : getPolicyActionContainers().values()) {
				statePolicy.apply(container);
			}
		}
		refreshLayout();
	}

	@Override
	public void refreshLayout() {
		if (!controlPane.getSwtComposite().isDisposed()) {
			controlPane.getSwtComposite().layout();
		}
	}

	/**
	 * State policy reinit with fresh model (a model after save operation for example).
	 */
	protected void reinitStatePolicy() {
		statePolicy.init(getDependentObject());
	}


	/**
	 * Returns the dependent object. in this case that is the... what?
	 *
	 * @return the dependent object
	 */
	public Object getDependentObject() {
		return priceAdjustmentTreeContainer.getPolicyDependent();
	}

	@Override
	public String getTargetIdentifier() {
		return "priceAdjustmentTree"; //$NON-NLS-1$
	}

	/**
	 * Monitors the model for changes to adjusted price and updates the tree view. 
	 */
	private class TreeTableContentProvider implements ITreeContentProvider, Observer {
		private PriceAdjustmentModelRoot lastRoot;

		@Override
		public void dispose() {
			if (lastRoot == null) {
				return;
			}

			for (PriceAdjustmentModel adjustmentModel : this.lastRoot.getChildren()) {
				adjustmentModel.deleteObserver(this);
			}
		}

		@Override
		public Object[] getChildren(final Object parent) {
			PriceAdjustmentModel constituentPrice = (PriceAdjustmentModel) parent;
			return constituentPrice.getChildren().toArray();
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			PriceAdjustmentModelRoot priceAdjustmentModelRoot = (PriceAdjustmentModelRoot) inputElement;
			Collection<PriceAdjustmentModel> constituentPrices = priceAdjustmentModelRoot.getChildren();

			return constituentPrices.toArray();
		}

		@Override
		public Object getParent(final Object child) {
			return null;
		}

		@Override
		public boolean hasChildren(final Object parent) {
			return getChildren(parent).length > 0;
		}

		@Override
		public void inputChanged(final Viewer view, final Object oldInput, final Object newInput) {
			if (newInput != null) {
				for (PriceAdjustmentModel adjustmentModel : ((PriceAdjustmentModelRoot) newInput).getChildren()) {
					adjustmentModel.addObserver(this);
				}
				this.lastRoot = (PriceAdjustmentModelRoot) newInput;
			}

			if (oldInput != null) {
				for (PriceAdjustmentModel adjustmentModel : ((PriceAdjustmentModelRoot) oldInput).getChildren()) {
					adjustmentModel.deleteObserver(this);
				}
			}
		}

		@Override
		public void update(final Observable observable, final Object value) {
			if (value == null) {
				return;
			}

			priceListAdjustmentTreeViewer.update(value, null);
			EventManager.getInstance().fireEvent(getPriceAdjustmentModelRoot().getProduct(),
					new PropertyChangeEvent(this, PriceAdjustmentSummaryCalculator.PRICE_CHANGED_PROPERTY, null, lastRoot));
		}
	}

	/**
	 * For displaying text in the view. 
	 */
	private class TreeTableLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			PriceAdjustmentModel constituentPriceModel = (PriceAdjustmentModel) element;

			Image image = null;
			switch (columnIndex) {
				case RADIO_COLUMN:
					image = selectionController.getSelectionImage(constituentPriceModel);
					break;
				case PRICE_ADJUSTMENT_COLUMN:
					if (canEditPriceAdjustment(constituentPriceModel)) {
						image = CatalogImageRegistry.getImage(CatalogImageRegistry.EDIT_CELL_SMALL);
					}
					break;
				default:
					break;
			}

			return image;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			PriceAdjustmentModel priceAdjustmentModel = (PriceAdjustmentModel) element;
			String displayString = null;
			switch (columnIndex) {
				case PRODUCT_NAME_COLUMN:
					displayString = priceAdjustmentModel.getProduct().getDisplayName(locale);
					break;
				case SKU_CODE_COLUMN:
					if (priceAdjustmentModel.getBundleConstituent().getConstituent().isProductSku()) {
						displayString = priceAdjustmentModel.getBundleConstituent().getConstituent().getProductSku().getSkuCode();
					}
					break;
				case SKU_CONFIGURATION_COLUMN:
					if (priceAdjustmentModel.getBundleConstituent().getConstituent().isProductSku()) {
						displayString = priceAdjustmentModel.getBundleConstituent().getConstituent().getProductSku()
								.getDisplayName(CorePlugin.getDefault().getDefaultLocale());
					}
					break;
				case QUANTITY_COLUMN:
					displayString = String.valueOf(priceAdjustmentModel.getQuantity());
					break;
				case ITEM_PRICE_COLUMN:
					if (priceAdjustmentModel.getChildren().isEmpty()) {
						displayString = getPriceString(priceAdjustmentModel.getPrice());
					}
					break;
				case PRICE_ADJUSTMENT_COLUMN:
					displayString = getPriceString(priceAdjustmentModel.getPriceAdjustment());
					break;
				default:
					displayString = StringUtils.EMPTY;
					break;
			}

			if (displayString == null) {
				displayString = StringUtils.EMPTY;
			}

			return displayString;
		}

		private String getPriceString(final BigDecimal bigDecimal) {
			if (bigDecimal == null) {
				return StringUtils.EMPTY;
			}

			return String.valueOf(bigDecimal);
		}
	}

	/**
	 * @param bindingContext the binding context
	 */
	public void bindControls(final DataBindingContext bindingContext) {
		epColumns[PRICE_ADJUSTMENT_COLUMN].setEditingSupport(new PriceAdjustmentEditingSupport(
				priceListAdjustmentTreeViewer,
				"priceAdjustment", //name of property in PriceAdjustmentModel that we're changing  //$NON-NLS-1$
				bindingContext,
				this));
	}


	/**
	 * marks the parent part as dirty.
	 */
	public void notifyModification() {
		part.markDirty();
	}

	/**
	 * @param priceAdjustmentModel the model
	 * @return <code>true</code> iff the cell showing the adjustment should be editable
	 */
	public boolean canEditPriceAdjustment(final PriceAdjustmentModel priceAdjustmentModel) {
		// Is the item clicked on a constituent in a bundle?
		boolean isConstituentItem = priceAdjustmentModel.isConstituentItem();
		boolean isCalculatedBundle = priceAdjustmentModel.isProductACalculatedBundle();

		if (isCalculatedBundle || !isConstituentItem) {
			return false;
		}

		return !priceAdjustmentModel.getParent().isProductACalculatedBundle()
				|| priceAdjustmentModel.getPrice() != null;
	}

}
