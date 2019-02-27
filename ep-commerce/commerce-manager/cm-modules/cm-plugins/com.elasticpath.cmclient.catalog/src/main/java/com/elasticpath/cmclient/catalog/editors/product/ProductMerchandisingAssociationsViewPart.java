/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IPropertyListener;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.dialogs.product.ProductMerchandisingAssociationDialog;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.TableItems;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.service.catalog.ProductAssociationService;

/**
 * This class implements the section of the Product editor that displays price information/details.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity",	"PMD.ExcessiveMethodLength", "PMD.TooManyMethods", "PMD.GodClass" })
public class ProductMerchandisingAssociationsViewPart extends DefaultStatePolicyDelegateImpl
	implements	IEpViewPart, IPropertyListener {

	private static final String PRODUCT_MERCHANDISING_TABLE = "Product Merchandising"; //$NON-NLS-1$
	private IEpTabFolder tabFolder;

	private final Map<Integer, TableViewer> tableViewers;

	private static final int TAB_REPLACEMENT = 4;

	private static final int TAB_ACCESSORY = 3;

	private static final int TAB_WARRANTY = 2;

	private static final int TAB_UPSELL = 1;

	private static final int TAB_CROSSSELL = 0;

	private static final int DIRECTION_UP = -1;

	private static final int DIRECTION_DOWN = 1;

	private Button upButton;

	private Button downButton;

	private Button addButton;

	private Button editButton;

	private Button removeButton;

	private IPolicyTargetLayoutComposite mainComposite;

	private static final Map<Integer, ProductAssociationType> TAB_ASSOCIATIONTYPE_MAP = new HashMap<>();

	static {
		TAB_ASSOCIATIONTYPE_MAP.put(TAB_CROSSSELL, ProductAssociationType.CROSS_SELL);
		TAB_ASSOCIATIONTYPE_MAP.put(TAB_UPSELL, ProductAssociationType.UP_SELL);
		TAB_ASSOCIATIONTYPE_MAP.put(TAB_WARRANTY, ProductAssociationType.WARRANTY);
		TAB_ASSOCIATIONTYPE_MAP.put(TAB_ACCESSORY, ProductAssociationType.ACCESSORY);
		TAB_ASSOCIATIONTYPE_MAP.put(TAB_REPLACEMENT, ProductAssociationType.REPLACEMENT);
	}

	private static final int PRODUCT_CODE_WIDTH = 150;

	private static final int NAME_WIDTH = 250;

	private static final int ENABLE_DATE_WIDTH = 100;

	private static final int DISABLE_DATE_WIDTH = 100;

	private static final int COLUMN_INDEX_END_DATE = 3;

	private static final int MAX_ITEM_TO_SCROLL = 10;

	private final Map<Integer, IPolicyTargetLayoutComposite> tabItemComposites = new HashMap<>();

	private final Catalog catalog;

	private final ProductEditor editor;

	private final ProductAssociationService associationService;

	private final TableItems<ProductAssociation> associationItems = new TableItems<>();

	private final Map<ProductAssociationType, Collection<ProductAssociation>> cachedAssociations =
			new HashMap<>();

	private final Map<Long, ProductAssociation> originalAssociations = new HashMap<>();

	private StatePolicy statePolicy;

	private PolicyActionContainer merchContainer;

	/**
	 * Constructor.
	 *
	 * @param editor the editor where the detail section will be placed
	 * @param catalog the Catalog instance
	 */
	public ProductMerchandisingAssociationsViewPart(final Catalog catalog, final ProductEditor editor) {
		this.catalog = catalog;
		this.tableViewers = new HashMap<>();
		this.editor = editor;
		this.associationService = ServiceLocator.getService(ContextIdNames.PRODUCT_ASSOCIATION_SERVICE);
	}

	@Override
	public void createControls(final IEpLayoutComposite parent, final IEpLayoutData data) {

		merchContainer = addPolicyActionContainer("merchAssociationControls"); //$NON-NLS-1$
		merchContainer.setPolicyDependent(catalog);

		mainComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(parent.addGridLayoutComposite(2, false, data));
		final IEpLayoutData tableFolderData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);


		this.tabFolder = mainComposite.getLayoutComposite().addTabFolder(tableFolderData);
		this.tabFolder.getSwtTabFolder().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				updateEditorInput();
				refreshButtons();
			}
		});

		final IEpLayoutComposite crossSellTabItem = this.tabFolder.addTabItem(CatalogMessages.get().ProductMerchandisingAssociationSection_Cross_Sell,
				null, TAB_CROSSSELL, 1, false);

		this.createTable(crossSellTabItem, TAB_CROSSSELL, merchContainer);


		final IEpLayoutComposite upSellTabItem = this.tabFolder.addTabItem(CatalogMessages.get().ProductMerchandisingAssociationSection_Up_Sell,
				null, TAB_UPSELL, 1, false);
		this.createTable(upSellTabItem, TAB_UPSELL, merchContainer);


		final IEpLayoutComposite warrantyTabItem = this.tabFolder.addTabItem(CatalogMessages.get().ProductMerchandisingAssociationSection_Warranty,
				null, TAB_WARRANTY, 1, false);
		this.createTable(warrantyTabItem, TAB_WARRANTY, merchContainer);

		final IEpLayoutComposite accessoryTabItem = this.tabFolder.addTabItem(CatalogMessages.get().ProductMerchandisingAssociationSection_Accessory,
				null, TAB_ACCESSORY, 1, false);

		this.createTable(accessoryTabItem, TAB_ACCESSORY, merchContainer);

		final IEpLayoutComposite replacementTabItem =
				this.tabFolder.addTabItem(CatalogMessages.get().ProductMerchandisingAssociationSection_Replacement,
						null, TAB_REPLACEMENT, 1, false);
		this.createTable(replacementTabItem, TAB_REPLACEMENT, merchContainer);


		final IEpLayoutData buttonCompositeData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IPolicyTargetLayoutComposite buttonComposite = mainComposite.addTableWrapLayoutComposite(1, false, buttonCompositeData, merchContainer);
		this.createButtons(buttonComposite, merchContainer);

		this.tabFolder.setSelection(TAB_CROSSSELL);

	}

	private void createTable(final IEpLayoutComposite epComposite, final int tabIndex, final PolicyActionContainer container) {

		IPolicyTargetLayoutComposite composite = PolicyTargetCompositeFactory.wrapLayoutComposite(epComposite);

		final IEpLayoutData tableViewerData = composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 1);
		final IEpTableViewer epTableViewer = composite.addTableViewer(true, tableViewerData, container, PRODUCT_MERCHANDISING_TABLE);

		epTableViewer.addTableColumn(CatalogMessages.get().ProductMerchandisingAssociationSection_Product_Code, PRODUCT_CODE_WIDTH);
		epTableViewer.addTableColumn(CatalogMessages.get().ProductMerchandisingAssociationSection_Proudct_Name, NAME_WIDTH);
		epTableViewer.addTableColumn(CatalogMessages.get().ProductMerchandisingAssociationSection_Enable_Date, ENABLE_DATE_WIDTH);
		epTableViewer.addTableColumn(CatalogMessages.get().ProductMerchandisingAssociationSection_Disable_Date, DISABLE_DATE_WIDTH);

		epTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> refreshButtons());

		epTableViewer.setContentProvider(new ArrayContentProvider());
		epTableViewer.setLabelProvider(new ViewLabelProvider());
		epTableViewer.getSwtTableViewer().setComparator(new ViewerComparator());
		epTableViewer.getSwtTableViewer().setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object object1, final Object object2) {
				if (object1 instanceof ProductAssociation && object2 instanceof ProductAssociation) {
					ProductAssociation assoc1 = (ProductAssociation) object1;
					ProductAssociation assoc2 = (ProductAssociation) object2;
					return String.valueOf(assoc1.getOrdering()).compareTo(String.valueOf(assoc2.getOrdering()));
				}
				return super.compare(viewer, object1, object2);
			}

		});

		epTableViewer.getSwtTableViewer().addDoubleClickListener(new DoubleClickListener());

		this.tableViewers.put(tabIndex, epTableViewer.getSwtTableViewer());
		this.tabItemComposites.put(tabIndex, composite);
	}

	/**
	 * Gets the product association service.
	 *
	 * @return the association service instance
	 */
	protected ProductAssociationService getProductAssociationService() {
		return associationService;
	}

	/**
	 *
	 * This class is a double click listener for the price table viewer.
	 *
	 */
	private class DoubleClickListener implements IDoubleClickListener {

		@Override
		public void doubleClick(final DoubleClickEvent event) {
			if (isAuthorized()) {
				openAddEditDialog(getSelectedAssociation());
			}
		}
	}

	private void createButtons(final IPolicyTargetLayoutComposite epComposite, final PolicyActionContainer container) {

		this.upButton = epComposite.addPushButton(CatalogMessages.get().ProductMerchandisingAssociationSection_Move_Up,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_UP_ARROW), null, container);
		this.upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				moveSelectedItems(DIRECTION_UP);
			}
		});

		this.downButton = epComposite.addPushButton(CatalogMessages.get().ProductMerchandisingAssociationSection_Move_Down,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_DOWN_ARROW), null, container);
		this.downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				moveSelectedItems(DIRECTION_DOWN);
			}
		});

		// add spacer
		epComposite.addEmptyComponent(null, container);

		this.editButton = epComposite.addPushButton(CatalogMessages.get().ProductMerchandisingAssociationSection_Edit,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT), null, container);
		this.editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				refreshButtons();
				openAddEditDialog(getSelectedAssociation());
			}
		});

		this.addButton = epComposite.addPushButton(CatalogMessages.get().ProductMerchandisingAssociationSection_Add,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD), null, container);
		this.addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				openAddEditDialog(null);
			}
		});

		this.removeButton = epComposite.addPushButton(CatalogMessages.get().ProductMerchandisingAssociationSection_Remove,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE), null, container);
		this.removeButton.addSelectionListener(new RemoveButtonSelectionListener());

		// add spacer
		epComposite.addEmptyComponent(null, container);

		disableButtons();
	}

	private void refreshTableViewer() {
		final TableViewer tableViewer = getSelectedTableViewer();
		tableViewer.refresh();

		if (tableViewer.getTable().getItemCount() < MAX_ITEM_TO_SCROLL) {
			this.tabItemComposites.get(tabFolder.getSelectedTabIndex()).getSwtComposite().layout();
		}

		getEditor().controlModified();
	}

	private ProductAssociation getSelectedAssociation() {
		final TableViewer tableViewer = getSelectedTableViewer();
		final ISelection selection = tableViewer.getSelection();
		return (ProductAssociation) ((IStructuredSelection) selection).getFirstElement();

	}

	private void openAddEditDialog(final ProductAssociation productAssociation) {
		final boolean editMode = productAssociation != null;
		final ProductAssociationType associationType = TAB_ASSOCIATIONTYPE_MAP.get(tabFolder.getSelectedTabIndex());
		final ProductMerchandisingAssociationDialog dialog = new ProductMerchandisingAssociationDialog(
				this.getEditor().getEditorSite().getShell(),
				associationType, getModel(),
				productAssociation, getMaxOrdering(associationType) + 1,
				this.catalog);

		if (dialog.open() == Window.OK) {
			//Get the product association from Dialog for both add and edit mode
			final ProductAssociation updatedProductAssociation = dialog.getProductAssociation();
			if (editMode) {
				associationItems.addModifiedItem(updatedProductAssociation);
			} else {
				associationItems.addAddedItem(updatedProductAssociation);
				getProductAssociationsByType(updatedProductAssociation.getAssociationType()).add(updatedProductAssociation);
			}

			refreshTableViewer();
		}
	}

	/**
	 * Switches the ordering of product associations.
	 * 
	 * @param associationType the associations type
	 * @param association1 the first association
	 * @param association2 the second association
	 */
	protected void switchOrdering(final ProductAssociationType associationType, final ProductAssociation association1,
			final ProductAssociation association2) {
		int orderingAssoc1 = association1.getOrdering();
		int orderingAssoc2 = association2.getOrdering();
		if (orderingAssoc1 == orderingAssoc2) {
			// in case the ordering is corrupted (the ordering numbers of the two associations is the same
			// we need to refresh the ordering of all the associations in the same order as they appear in the UI
			refreshAssociationsOrder(getProductAssociationsByType(associationType));
			// retrieve the newly set 'ordering' numbers
			orderingAssoc1 = association1.getOrdering();
			orderingAssoc2 = association2.getOrdering();
		}

		association1.setOrdering(orderingAssoc2);
		association2.setOrdering(orderingAssoc1);
		associationItems.addModifiedItem(association1);
		associationItems.addModifiedItem(association2);
	}

	/**
	 * Provides the column image for the price table. The image may soon be removed.
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final ProductAssociation productAssociation = (ProductAssociation) element;

			switch (columnIndex) {
			case 0:
				return productAssociation.getTargetProduct().getCode();
			case 1:
				return productAssociation.getTargetProduct().getDisplayName(CorePlugin.getDefault().getDefaultLocale());
			case 2:
				return DateTimeUtilFactory.getDateUtil().formatAsDate(productAssociation.getStartDate());
			case COLUMN_INDEX_END_DATE:
				return DateTimeUtilFactory.getDateUtil().formatAsDate(productAssociation.getEndDate());
			default:
				return CatalogMessages.get().Product_NotAvailable;
			}
		}

		@Override
		public String getText(final Object element) {
			final ProductAssociation productAssociation = (ProductAssociation) element;
			return String.valueOf(productAssociation.getOrdering());
		}
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		//Empty
	}

	@Override
	public void populateControls() {
		updateEditorInput();
	}

	private void updateEditorInput() {
		final TableViewer tableViewer = getSelectedTableViewer();
		final ProductAssociationType associationType = TAB_ASSOCIATIONTYPE_MAP.get(tabFolder.getSelectedTabIndex());

		final Collection<ProductAssociation> associationsByType = getProductAssociationsByType(associationType);
		// set the input as a set
		tableViewer.setInput(associationsByType);
	}


	/**
	 * Gets product associations from the local cache (HashMap). If none exists retrieves them from the database.
	 */
	private Collection<ProductAssociation> getProductAssociationsByType(final ProductAssociationType associationType) {

		Collection<ProductAssociation> associations = cachedAssociations.get(associationType);

		if (associations == null) {
			associations = getProductAssociationService().getAssociationsByType(
					getModel().getCode(),
					associationType,
					this.catalog.getCode(), true);
			cachedAssociations.put(associationType, associations);
			storeOriginalAssociations(associations);
		}
		ensureCorrectOrdering(associations);
		return associations;
	}

	private void refreshButtons() {
		if (getSelectedTableViewer().getTable().isDisposed()) {
			return;
		}
		final TableViewer tableViewer = getSelectedTableViewer();
		final int selectionIndex = tableViewer.getTable().getSelectionIndex();

		disableButtons();
		if (isAuthorized() && isValidSelection(selectionIndex))  {
			enableButtons();
			if (tableViewer.getTable().getSelectionCount() > 1) {
				//Disable the edit and remove buttons for multiple selection since we don't have defined behaviour for them.
				this.editButton.setEnabled(false);
				this.removeButton.setEnabled(false);
			} 
			this.upButton.setEnabled(!isTopRowSelected(tableViewer));
			this.downButton.setEnabled(!isBottomRowSelected(tableViewer));
		}
	}

	private boolean isTopRowSelected(final TableViewer tableViewer) {
		return isRowSelected(tableViewer, 0);
	}
	
	private boolean isBottomRowSelected(final TableViewer tableViewer) {
		return isRowSelected(tableViewer, tableViewer.getTable().getItemCount() - 1);		
	}
	
	private boolean isRowSelected(final TableViewer tableViewer, final int rowIndex) {
		return getSortedSelectionIndices(DIRECTION_UP, tableViewer).contains(rowIndex);
	}
	
	private boolean isValidSelection(final int selectedIndex) {
		return selectedIndex != -1;
	}

	private void disableButtons() {
		this.upButton.setEnabled(false);
		this.downButton.setEnabled(false);
		this.editButton.setEnabled(false);
		this.removeButton.setEnabled(false);
	}

	private void enableButtons() {
		this.upButton.setEnabled(true);
		this.downButton.setEnabled(true);
		this.editButton.setEnabled(true);
		this.removeButton.setEnabled(true);
	}

	/**
	 * Gets the max ordering number existing so far in the database for this association type.
	 *
	 * @param associationType the association type
	 * @return the max ordering number
	 */
	protected int getMaxOrdering(final ProductAssociationType associationType) {
		return getProductAssociationsByType(associationType).size();
	}

	/**
	 * Checks that no two associations in the collection have the same ordering value, if they do refreshes the
	 * ordering values to match the order in the collection.
	 * @param associations collection of <code>ProductAssociations</code>
	 */
	private void ensureCorrectOrdering(final Collection<ProductAssociation> associations) {
		SortedSet<Integer> orderingWithoutDuplicates = getOrderingWithoutDuplicates(associations);
		// if the number of items in the 'ordering' set is less than the associations
		// then there is a discrepancy in the orderings of one or more associations
		// if the last ordering value is greater than the unique count, we must have a gap somewhere in our ordering
		int uniqueOrderingCount = orderingWithoutDuplicates.size();
		if (uniqueOrderingCount < associations.size() || (uniqueOrderingCount > 0 && orderingWithoutDuplicates.last() > uniqueOrderingCount)) {
			refreshAssociationsOrder(associations);
		}
	}
	
	/**
	 * Get a set of the product association orderings ensuring no duplicates. 
	 * @param associations list of product associations to get the ordering from
	 * @return set of orderings in natural order without duplicates
	 */
	protected SortedSet<Integer> getOrderingWithoutDuplicates(final Collection<ProductAssociation> associations) {
		SortedSet<Integer> ordering = new TreeSet<>();
		for (final ProductAssociation productAssociation : associations) {
			ordering.add(productAssociation.getOrdering());
		}
		return ordering;
	}

	/**
	 * Refresh all the items...
	 * @param associations the associations to reorder
	 * @return the next available order number after refreshing the order
	 */
	protected int refreshAssociationsOrder(final Collection<ProductAssociation> associations) {
		List<ProductAssociation> associationsList = new ArrayList<>(associations);
		for (int index = 0; index < associationsList.size(); index++) {
			ProductAssociation association = associationsList.get(index);
			association.setOrdering(index + 1);
			if (association.isPersisted()) {
				associationItems.addModifiedItem(association);
			} else {
				associationItems.addAddedItem(association);
			}
		}
		return associationsList.size() + 1;
	}

	@Override
	public Product getModel() {
		return this.getEditor().getModel().getProduct();
	}

	/**
	 * Gets the editor instance.
	 *
	 * @return the product editor
	 */
	private ProductEditor getEditor() {
		return editor;
	}

	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (propId == ProductEditor.PROP_SAVE_ACTION) {
			updateProductAssociations();
			cachedAssociations.clear();
			originalAssociations.clear();
		}
	}

	/**
	 * Saves or removes product associations accordingly to/from the database.
	 */
	protected void updateProductAssociations() {
		for (ProductAssociation productAssociation : associationItems.getAddedItems()) {
			getProductAssociationService().add(productAssociation);
		}
		associationItems.getAddedItems().clear();

		for (ProductAssociation productAssociation : associationItems.getModifiedItems()) {
			//TODO Possible bug here because the copy contains mutable objects which if you modify 
			//TODO them the ProductAssociation will return unchanged and we won't update. 
			if (!isAssociationUnchanged(productAssociation)) {
				getProductAssociationService().update(productAssociation);
			}
		}
		associationItems.getModifiedItems().clear();

		for (ProductAssociation productAssociation : associationItems.getRemovedItems()) {
			getProductAssociationService().remove(productAssociation);
		}
		associationItems.getRemovedItems().clear();
	}

	// ---- DOCpropertyChanged

	@Override
	public void refreshLayout() {
		if (!mainComposite.getSwtComposite().isDisposed()) {
			mainComposite.getSwtComposite().layout();
		}
	}


	@Override
	public void applyStatePolicy(final StatePolicy policy) {
		this.statePolicy = policy;
		super.applyStatePolicy(policy);
		refreshButtons();
	}
	
	private boolean isAuthorized() {
		return (statePolicy != null && EpState.EDITABLE.equals(statePolicy.determineState(merchContainer)));
	}

	private boolean isAssociationUnchanged(final ProductAssociation productAssociation) {
		return originalAssociations.containsKey(productAssociation.getUidPk()) 
				&& originalAssociations.get(productAssociation.getUidPk()).isSameAs(productAssociation);
	}

	/**
	 * Store the associations as they were most recently loaded from the database. Stored values are
	 * used to make sure we don't attempt to persist things which haven't really been changed, as that
	 * can cause problems with OpenJPA OptimisticLockExceptions. 
	 * @param productAssociations collection of product associations loaded from persistence 
	 */
	protected void storeOriginalAssociations(final Collection<ProductAssociation> productAssociations) {
		for (ProductAssociation association : productAssociations) {
			originalAssociations.put(association.getUidPk(), association.deepCopy());
		}
	}

	/**
	 * Moves the selected associations in the direction specified.
	 * @param direction the direction to move in, either 1 or -1 for down and up respectively
	 */
	protected void moveSelectedItems(final int direction) {
		final TableViewer tableViewer = getSelectedTableViewer();
		List<Integer> sortedSelectionIndices = getSortedSelectionIndices(direction, tableViewer);

		//Keep track of where we've moved associations to so that we don't have selected items switching with each other
		List<Integer> destinationIndices = new ArrayList<>(sortedSelectionIndices.size());

		//Since element positions in the tableViewer don't get updated as we go, we get our own list and maintain the intermediate positions
		List<ProductAssociation> tableElements = getTableElements(tableViewer);

		//for each selected item (in order from farthest in the the specified direction (closest to the top for up, bottom for down))
		//the order is important to not invalidating the selectionIndexes.
		for (int selectedIndex : sortedSelectionIndices) {
			ProductAssociation selectedAssociation = getAssociation(tableElements, selectedIndex);
			//Get the next item which is farther in the specified direction than the selected association
			int targetIndex = selectedIndex + direction;
			ProductAssociation otherAssociation = getAssociation(tableElements, targetIndex);

			//	If the item exists (we're not trying to go off the end) and is not in the selection (we don't want to pass)
			if (associationsExist(selectedAssociation, otherAssociation) && !destinationIndices.contains(targetIndex)) {
				destinationIndices.add(targetIndex);
				Collections.swap(tableElements, selectedIndex, targetIndex);
				switchOrdering(selectedAssociation.getAssociationType(), selectedAssociation, otherAssociation);
			} else {
				destinationIndices.add(selectedIndex);
			}
		}

		refreshTableViewer();
		refreshButtons();
	}

	/**
	 * Creates a local list of all the <code>ProductAssociation</code> in <code>tableViewer</code>. 
	 * The list is not backed by the TableViewer or it's table, assignment, deletion or reordering this
	 * list will have no effect on the table. The ProductAssociations are the same as in the TableViewer, so directly
	 * modifying there properties will change the table viewer. 
	 * @param tableViewer the table view to get the list from
	 * @return list of ProductAssociations from the tableViewer
	 */
	protected List<ProductAssociation> getTableElements(final TableViewer tableViewer) {
		List<ProductAssociation> tableElements = new ArrayList<>();
		int tableItemCount = tableViewer.getTable().getItemCount();
		for (int i = 0; i < tableItemCount; ++i) {
			tableElements.add((ProductAssociation) tableViewer.getElementAt(i));
		}
		return tableElements;
	}

	/**
	 * Get the <code>ProductAssociation</code> at the specified index in the list, returning null if the index is out of bounds.
	 * @param tableElements assumed not null
	 * @param index index to check for validity
	 * @return the association at the specified index, or null if the index is out of bounds
	 */
	protected ProductAssociation getAssociation(final List<ProductAssociation> tableElements, final int index) {
		ProductAssociation productAssociation = null;
		if (index >= 0 && index < tableElements.size()) {
			productAssociation = tableElements.get(index);
		}
		return productAssociation;
	}

	/**
	 * Make sure both selected associations actually exist.
	 */
	private boolean associationsExist(final ProductAssociation associationToMove,
			final ProductAssociation otherAssociation) {
		return associationToMove != null && otherAssociation != null;
	}


	/**
	 * Get the indices of the selected associations and sorts them, either descending (direction = 1) or ascending (direction = -1). This list is not
	 * backed by the table, so if you start rearranging the associations you may make the returned list invalid.
	 */
	private List<Integer> getSortedSelectionIndices(final int direction,
			final TableViewer tableViewer) {
		final int[] selectionIndices = tableViewer.getTable().getSelectionIndices();
		List<Integer> sortedSelectionIndices = new LinkedList<>();
		for (int index : selectionIndices) {
			sortedSelectionIndices.add(index);
		}
		Collections.sort(sortedSelectionIndices);
		//put the selection indices in sorted or (either positive or negative)
		if (direction > 0) {
			Collections.reverse(sortedSelectionIndices);
		}
		return sortedSelectionIndices;
	}


	/**
	 * Gets the currently selected by user table viewer.
	 *
	 * @return the table viewer instance
	 */
	protected TableViewer getSelectedTableViewer() {
		return tableViewers.get(tabFolder.getSelectedTabIndex());
	}

	/**
	 * Remove button selection listener.
	 */
	private class RemoveButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			final ProductAssociation productAssociation = getSelectedAssociation();

			final String removeAssociationMsg =
				NLS.bind(CatalogMessages.get().ProductMerchandisingAssociationDialog_RemoveMsg,
				new Object[]{productAssociation.getTargetProduct().getCode(), productAssociation.getTargetProduct().
				getDisplayName(CorePlugin.getDefault().getDefaultLocale())
			});

			final boolean answerYes = MessageDialog.openConfirm(null,
					CatalogMessages.get().ProductMerchandisingAssociationDialog_RemoveTitle, removeAssociationMsg);
			if (answerYes) {
				associationItems.addRemovedItem(productAssociation);
				Collection<ProductAssociation> associations = getProductAssociationsByType(productAssociation.getAssociationType());
				associations.remove(productAssociation);
				refreshAssociationsOrder(associations);
				refreshTableViewer();
				refreshButtons();
			}
		}
	}
}
