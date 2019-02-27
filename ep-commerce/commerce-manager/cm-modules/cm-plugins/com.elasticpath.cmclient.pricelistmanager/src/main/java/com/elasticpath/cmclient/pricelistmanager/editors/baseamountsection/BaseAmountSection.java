/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.dto.catalog.PriceListEditorModel;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.BaseAmountDTOCreator;
import com.elasticpath.cmclient.core.helpers.ChangeEventListener;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.helpers.ProductListener;
import com.elasticpath.cmclient.core.helpers.ProductSkuListener;
import com.elasticpath.cmclient.core.helpers.extenders.EPTableColumnCreator;
import com.elasticpath.cmclient.core.helpers.extenders.EPTableColumnNameCreator;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.ChangeSetEventService;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.IEpViewPart;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyTargetImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.pricelistmanager.dialogs.BaseAmountDialog;
import com.elasticpath.cmclient.pricelistmanager.dialogs.BaseAmountRemoveDialog;
import com.elasticpath.cmclient.pricelistmanager.event.BaseAmountSearchEvent;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.BaseAmountSearchEventListener;
import com.elasticpath.cmclient.pricelistmanager.model.impl.BaseAmountType;
import com.elasticpath.cmclient.pricelistmanager.validators.ListPriceValidator;
import com.elasticpath.cmclient.pricelistmanager.validators.SalePriceValidator;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.misc.RandomGuid;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.service.catalog.ProductSkuLookup;


/**
 * Represents Base Amount section.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyFields", "PMD.ExcessiveImports", "PMD.GodClass", "PMD.PrematureDeclaration" })
public class BaseAmountSection extends AbstractStatePolicyTargetImpl  implements IEpViewPart, BaseAmountSearchEventListener,
					ProductListener, ProductSkuListener {

	private static final Logger LOG = Logger.getLogger(BaseAmountSection.class);

	private static final int INITIAL_WIDTH_LARGE = 275;
	private static final int INITIAL_WIDTH_MED = 100;

	private static final String BASE_AMOUNT_TABLE = "Base Amount"; //$NON-NLS-1$

	private final PriceListEditorController controller;

	private IEpTableViewer baseAmountTableViewer;

	private Button editButton;

	private Button addButton;

	private Button deleteButton;

	private Button openItemButton;

	private final TableSelectionProvider selectionChangedListener;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private final boolean isChangeSetsEnabled;

	private ColumnClickListener columnClickListener;

	private IPolicyTargetLayoutComposite buttonsComposite;

	private final BaseAmountTableContentProvider baseAmountTableContentProvider;

	private final Map<TableColumn, Comparator<BaseAmountDTO>> columnComparatorMap =
			new HashMap<>();

	private Map<Object, String> mapOfEditableBaseAmounts;

	private final ControlModificationListener controlModificationListener;

	private StatePolicy statePolicy;

	private IEpTableColumn listPriceColumn;

	private IEpTableColumn salePriceColumn;

	private IPolicyTargetLayoutComposite controlPane;

	/* required for create product wizard, no need to select the product while prices setup */
	private final boolean showProductPane;

	/* required for create product wizard, only list, sale and qty need to be shown */
	private final boolean showPricingOnly;

	private final boolean hideOpenItemButton;

	private final BaseAmountTableProperties tableProperties;

	private final List<BaseAmountDTO> emptyObjects = new ArrayList<>();
	
	private int baseColumnCount;
	
	// Keeps the list of extension column names, if any, for later use.
	private List<Pair> extensionColumnNames = new ArrayList<Pair>();

	/**
	 * Constructs the BaseAmount editing section.
	 *
	 * @param editor the editor
	 * @param controller the controller
	 * @param selectionChangedListener selection change listener
	 * @param properties - base amount table properties holder
	 * @param showProductPane true, if the AddBaseAmount dialog should contain the product/sku picker
	 * @param showPricingOnly true if only qty, sale and list prices should be displayed in the base amount table
	 * @param showOpenItemButton true if the Open Item button is to be visible, otherwise false.
	 */
	public BaseAmountSection(final ControlModificationListener editor, final PriceListEditorController controller,
											final TableSelectionProvider selectionChangedListener,
											final BaseAmountTableProperties properties,
											final boolean showProductPane,
											final boolean showPricingOnly,
											final boolean showOpenItemButton) {
		controlModificationListener = editor;
		this.controller = controller;
		this.selectionChangedListener = selectionChangedListener;
		this.showProductPane = showProductPane;
		this.showPricingOnly = showPricingOnly;
		this.hideOpenItemButton = showOpenItemButton;

		if (properties == null) {
			tableProperties = new DefaultBaseAmountTableProperties();
		} else {
			tableProperties = properties;
		}
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
		isChangeSetsEnabled = changeSetHelper.isChangeSetsEnabled();
		if (isChangeSetsEnabled) {
			mapOfEditableBaseAmounts = changeSetHelper.getObjectsLocked(controller.getAllBaseAmounts().toArray());
		} else {
			getTableProperties().setIsLockedWidth(0);
		}

		baseAmountTableContentProvider = new BaseAmountTableContentProvider(controller, this);

		ObjectRegistry.getInstance().addObjectListener(objectRegistryListener);

		if (showPricingOnly) {
			getTableProperties().setListPriceWidth(INITIAL_WIDTH_LARGE); //resizing the columns to match a new table size
			getTableProperties().setSalePriceWidth(INITIAL_WIDTH_LARGE);
			getTableProperties().setEditButtonEnabled(false);
			baseAmountTableContentProvider.setDefaultComparator(
					baseAmountTableContentProvider.getQuantityComparator()
				);
		}
		
		// Initialize the count of base columns.
		baseColumnCount = 0;
	}

	/**
	 * Constructs the BaseAmount editing section.
	 *
	 * @param editor the editor
	 * @param controller the controller
	 * @param selectionChangedListener selection change listener
	 * @param properties - base amount table properties holder
	 */
	public BaseAmountSection(final ControlModificationListener editor,
											final PriceListEditorController controller,
											final TableSelectionProvider selectionChangedListener,
											final BaseAmountTableProperties properties) {
		this(editor, controller, selectionChangedListener, properties, true, false, false);
	}

	/**
	 * Caches objects that are currently in change set.
	 *
	 * @param collection - collection of the base amounts to check.
	 */
	protected void populateLockedObjects(final Collection<BaseAmountDTO> collection) {
		if (isChangeSetsEnabled) {
			mapOfEditableBaseAmounts = changeSetHelper.getObjectsLocked(collection.toArray());
		}
	}

	//force table refresh on change set is selected or changed
	private final ObjectRegistryListener objectRegistryListener = new ObjectRegistryListener() {

		@Override
		public void objectUpdated(final String key, final Object oldValue, final Object newValue) {
			if (newValue instanceof ChangeSet) {
				refreshTableViewer();
			}
		}

		@Override
		public void objectRemoved(final String key, final Object object) {
			//
		}

		@Override
		public void objectAdded(final String key, final Object object) {
			if (object instanceof ChangeSet) {
				refreshTableViewer();
			}
		}
	};

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		final IValidator listPriceValidator = new ListPriceValidator(() -> getSelection().getSaleValue());
		final IValidator salePriceValidator = new SalePriceValidator(() -> getSelection().getListValue());

		final TableViewer tableViewer = baseAmountTableViewer.getSwtTableViewer();
		listPriceColumn.setEditingSupport(new BaseAmountEditingSupport(tableViewer,
				BaseAmountEditingSupport.LIST_VALUE_FIELD, bindingContext, this, controller, listPriceValidator));

		salePriceColumn.setEditingSupport(new BaseAmountEditingSupport(tableViewer,
				BaseAmountEditingSupport.SALE_VALUE_FIELD, bindingContext, this, controller, salePriceValidator));

	}

	@Override
	public void createControls(final IEpLayoutComposite client, final IEpLayoutData data) {

		final PolicyActionContainer priceListBaseAmountEditorPageContainer = addPolicyActionContainer(getTargetIdentifier());

		controlPane = PolicyTargetCompositeFactory.wrapLayoutComposite(client);
		controlPane.setLayoutData(controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true).getSwtLayoutData());

		createBaseAmountTableStructure(priceListBaseAmountEditorPageContainer, controlPane);

		buttonsComposite = controlPane.addGridLayoutComposite(1, true, controlPane.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.FILL, true, true), priceListBaseAmountEditorPageContainer);

		final PolicyActionContainer editButtonContainer;
		// we do not create the edit button in the product create wizard due to changeset usage restrictions
		if (getTableProperties().isEditButtonEnabled()) {
			editButtonContainer = createEditButton(controlPane, buttonsComposite);
		} else {
			editButtonContainer = null;
		}

		createAddButton(controlPane, buttonsComposite);

		final PolicyActionContainer deleteButtonContainer = createDeleteButton(controlPane, buttonsComposite);

		createOpenItemButton(controlPane);

		// viewer
		baseAmountTableViewer.setContentProvider(baseAmountTableContentProvider);
		baseAmountTableViewer.setLabelProvider(
					getLabelProvider()
				);
		baseAmountTableViewer.getSwtTableViewer().addDoubleClickListener(getDoubleClickViewAction(editButton));
		baseAmountTableViewer.getSwtTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			/**
			 * Listen for changes to the base amount table and update edit and delete buttons based on row selection state.
			 *
			 * @param event the selection event
			 */
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (editButtonContainer != null) {
					editButtonContainer.setPolicyDependent(selection.getFirstElement());
				}
				deleteButtonContainer.setPolicyDependent(selection.getFirstElement());
				reApplyStatePolicy();
			}
		});

		// base amount row selection events must be sent to the SelectionService in order to be
		// added to a change set, using the Add to Change Set button
		baseAmountTableViewer.getSwtTableViewer().addSelectionChangedListener(selectionChangedListener);

		baseAmountTableViewer.setInput(controller);

		fireStatePolicyTargetActivated();
	}

	/**
	 * Allows extensions to include their own label providers.
	 *
	 * @return the label provider
	 */
	protected ITableLabelProvider getLabelProvider() {
		if (showPricingOnly) {
			return new SimpleBaseAmountTableLabelProvider(this, controller, baseColumnCount);
		}
		return new BaseAmountTableLabelProvider(
			this, controller, baseColumnCount
		);
	}

	@Override
	public void refreshLayout() {
		if (!controlPane.getSwtComposite().isDisposed()) {
			controlPane.getSwtComposite().layout();
		}
	}

	private PolicyActionContainer createDeleteButton(final IPolicyTargetLayoutComposite controlPane,
			final IPolicyTargetLayoutComposite buttonsComposite) {
		// delete button
		final PolicyActionContainer deleteButtonContainer = addPolicyActionContainer("priceListBaseAmountRemoveButton"); //$NON-NLS-1$

		deleteButton = buttonsComposite.addPushButton(getTableProperties().getDeleteButtonCaption(), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_REMOVE), controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL),
				deleteButtonContainer);
		deleteButton.addSelectionListener(getDeleteAction());

		deleteButtonContainer.addTarget(state -> setButtonState(deleteButton, state));
		return deleteButtonContainer;
	}

	private PolicyActionContainer createEditButton(final IPolicyTargetLayoutComposite controlPane,
												   final IPolicyTargetLayoutComposite buttonsComposite) {
		// edit button
		final PolicyActionContainer editButtonContainer = addPolicyActionContainer("priceListBaseAmountEditButton"); //$NON-NLS-1$

		editButton = buttonsComposite.addPushButton(getTableProperties().getEditButtonCaption(), CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT), controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL),
				editButtonContainer);
		editButton.addSelectionListener(getEditAction());

		editButtonContainer.addTarget(state -> setButtonState(editButton, state));
		return editButtonContainer;
	}

	private void setButtonState(final Button button, final EpState state) {
		if (button != null && !button.isDisposed()) {
			final boolean policyEditable = state == EpState.EDITABLE;
			final boolean hasSelection = !baseAmountTableViewer.getSwtTableViewer().getSelection().isEmpty();
			final boolean enabledState = (hasSelection && !controller.isDeleted(getSelection()) && policyEditable
					|| controller.isNewlyAdded(getSelection())) && StringUtils.isNotEmpty(getSelection().getGuid());
			button.setEnabled(enabledState && hasManageProductPricingPermissions());
		}
	}

	private PolicyActionContainer createAddButton(final IPolicyTargetLayoutComposite controlPane,
														final IPolicyTargetLayoutComposite buttonsComposite) {
		// add button
		final PolicyActionContainer addButtonContainer = addPolicyActionContainer("priceListBaseAmountAddButton"); //$NON-NLS-1$
		addButton = buttonsComposite.addPushButton(getTableProperties().getAddButtonCaption(),
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), addButtonContainer);
		addButton.addSelectionListener(getAddAction());
		if (!hasManageProductPricingPermissions()) {
			addButton.setEnabled(false);
		}
		return addButtonContainer;
	}

	private void createOpenItemButton(final IPolicyTargetLayoutComposite controlPane) {
		final PolicyActionContainer openItemButtonContainer = addPolicyActionContainer("priceListOpenItemButtonContainer"); //$NON-NLS-1$

		openItemButton = buttonsComposite.addPushButton(PriceListManagerMessages.get().BaseAmount_OpenItemButton, CoreImageRegistry
				.getImage(CoreImageRegistry.PRODUCT), controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), openItemButtonContainer);
		openItemButton.addSelectionListener(getOpenItemListener());

		if (hideOpenItemButton) {
			openItemButton.setVisible(false);
		}

		openItemButtonContainer.addTarget(new StateChangeTarget() {

			@Override
			public void setState(final EpState state) {

				final boolean hasManageProductSkuPermission = AuthorizationService.getInstance().isAuthorizedWithPermission(
						"MANAGE_PRODUCT_SKU" //$NON-NLS-1$
				);
				// can not refer to CatalogPermissions.MANAGE_PRODUCT_SKU, because of cycle dep
				if (!hasManageProductSkuPermission) {
					setState(false);
					return;
				}
				final BaseAmountDTO baseAmountDTO = getSelection();
				final boolean hasSelection = baseAmountDTO != null;
				if (!hasSelection) {
					setState(false);
					return;
				}
				final boolean hasProductCode = StringUtils.isNotEmpty(baseAmountDTO.getProductCode());
				if (!hasProductCode) {
					setState(false);
					return;
				}
				final Collection<Catalog> catalogs = controller.getCatalogsFor(baseAmountDTO);
				if (CollectionUtils.isEmpty(catalogs)) {
					setState(false);
					return;
				}

				final boolean enabledState = isAuthorized(catalogs) && !controller.isDeleted(baseAmountDTO) || controller.isNewlyAdded(baseAmountDTO);
				setState(enabledState);
			}

			private boolean isAuthorized(final Collection<Catalog> catalogs) {
				for (final Catalog catalog : catalogs) {
					if (AuthorizationService.getInstance().isAuthorizedForCatalog(catalog)) {
						return true;
					}
				}
				return false;
			}

			private void setState(final boolean state) {
				if (openItemButton != null && !openItemButton.isDisposed()) {
					openItemButton.setEnabled(state);
				}
			}
		});
	}

	private SelectionAdapter getOpenItemListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) baseAmountTableViewer.getSwtTableViewer().getSelection();
				final BaseAmountDTO baseAmountDTO = (BaseAmountDTO) selection.getFirstElement();
				String editorId;
				try {
					final BaseAmountType baseAmountType = BaseAmountType.findByType(baseAmountDTO.getObjectType());
					Class<?> clazz = null;
					if (baseAmountType == null) {
						return;
					}
					String objectGuid = null;
					switch (baseAmountType) {
					case PRODUCT:
						clazz = Product.class;
						editorId = "com.elasticpath.cmclient.catalog.editors.product.ProductEditor"; //$NON-NLS-1$
						// can not refer to ProductEditor.PART_ID; , because cycle dep
						objectGuid = baseAmountDTO.getObjectGuid();
						break;
					case SKU:
						clazz = ProductSku.class;
						editorId = "com.elasticpath.cmclient.catalog.editors.sku.ProductSkuEditor"; //$NON-NLS-1$
						// can not refer to ProductSkuEditor.PART_ID;
						objectGuid = getSkuGuidForSkuCode(baseAmountDTO.getObjectGuid());
						break;
					default:
						return;
					}
					final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					workbenchPage.openEditor(new GuidEditorInput(objectGuid, clazz), editorId);
				} catch (final PartInitException exc) {
					LOG.error("Error opening the Product SKU Editor", exc); //$NON-NLS-1$
				}

			}
		};
	}

	private IEpTableColumn registerSortableColumn(final IEpTableColumn column, final Comparator<BaseAmountDTO> comparator) {
		final TableColumn swtColumn = column.getSwtTableColumn();
		swtColumn.addListener(SWT.Selection, columnClickListener);
		columnComparatorMap.put(swtColumn, comparator);
		return column;
	}

	private void createBaseAmountTableStructure(final PolicyActionContainer priceListBaseAmountEditorPageContainer,
			final IPolicyTargetLayoutComposite controlPane) {
		final IEpLayoutData tableLayoutData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, true);
		baseAmountTableViewer = controlPane.addTableViewer(false, tableLayoutData, priceListBaseAmountEditorPageContainer, BASE_AMOUNT_TABLE);
		final GridData data = new GridData();
		data.heightHint = getTableProperties().getTableHeight();
		data.grabExcessHorizontalSpace = true;
		baseAmountTableViewer.getSwtTable().setData(data);
		baseAmountTableViewer.setEnableEditMode(true);
		createTableColumns();

		if (isChangeSetsEnabled) {
			ChangeSetEventService.getInstance().addChangeEventListener(new ChangeEventListener() {
				public void changeSetChanged(final ItemChangeEvent< ? > event) {
					if (event.getItem() instanceof BaseAmountDTO) {
						mapOfEditableBaseAmounts.put(event.getItem(), changeSetHelper.getActiveChangeSet().getGuid());
					}
					refreshTableViewer();
				}
			});
		}

		boolean addMoveNextPrevRow = true;
		if (isChangeSetsEnabled) {
			// Prevent moving to the next/prev row when changesets are enabled - the navigation implementation
			// uses recursion to find the next editable cell which can blow the stack when editable base amounts
			// are far apart in the table.
			addMoveNextPrevRow = false;
		}
		TableViewerEnhancer.addTraversalTabbing(baseAmountTableViewer.getSwtTableViewer(), false, addMoveNextPrevRow);
	}

	/**
	 * Creates the standard BaseAmount table columns.
	 */
	protected void createStandardBaseAmountTableColumns() {
		columnClickListener = new ColumnClickListener(
				baseAmountTableViewer,
				columnComparatorMap,
				baseAmountTableContentProvider,
				controller);

		if (!showPricingOnly) {
			final IEpTableColumn isLockedColumn = registerSortableColumn(
					baseAmountTableViewer.addTableColumn(StringUtils.EMPTY, getTableProperties().getIsLockedWidth()),
					baseAmountTableContentProvider.getLockedComparator());

			isLockedColumn.getSwtTableColumn().setToolTipText(PriceListManagerMessages.get().BaseAmount_ChangeSet);

			registerUnsavedChangesColumn();

			final IEpTableColumn typeColumn = registerSortableColumn(
					baseAmountTableViewer.addTableColumn(StringUtils.EMPTY, getTableProperties().getTypeWidth()),
					baseAmountTableContentProvider.getObjectTypeComparator());

			typeColumn.getSwtTableColumn().setToolTipText(PriceListManagerMessages.get().BaseAmount_ObjectType);

			registerSortableColumn(
					baseAmountTableViewer.addTableColumn(PriceListManagerMessages.get().BaseAmount_ProductName, getTableProperties().getNameWidth()),
					baseAmountTableContentProvider.getProductNameComparator());

			registerSortableColumn(
					baseAmountTableViewer.addTableColumn(PriceListManagerMessages.get().BaseAmount_ProductCode, getTableProperties().getCodeWidth()),
					baseAmountTableContentProvider.getProductCodeComparator());
			registerSortableColumn(
					baseAmountTableViewer.addTableColumn(PriceListManagerMessages.get().BaseAmount_SkuCode, getTableProperties().getSkuCodeWidth()),
					baseAmountTableContentProvider.getSkuCodeComparator());
			registerSortableColumn(
					baseAmountTableViewer.addTableColumn(PriceListManagerMessages.get().BaseAmount_SkuConfiguration,
							getTableProperties().getSkuConfigurationWidth()),
					baseAmountTableContentProvider.getSkuConfigComparator());
		}

		if (showPricingOnly) {
			registerUnsavedChangesColumn();
		}

		registerSortableColumn(
				baseAmountTableViewer.addTableColumn(PriceListManagerMessages.get().BaseAmount_Quantity, getTableProperties().getQuantityWidth()),
				baseAmountTableContentProvider.getQuantityComparator());

		listPriceColumn = registerSortableColumn(
				baseAmountTableViewer.addTableColumn(PriceListManagerMessages.get().BaseAmount_ListPrice,
				getTableProperties().getListPriceWidth()),
				baseAmountTableContentProvider.getListValueComparator());

		salePriceColumn = registerSortableColumn(
				baseAmountTableViewer.addTableColumn(PriceListManagerMessages.get().BaseAmount_SalePrice,
				getTableProperties().getSalePriceWidth()),
				baseAmountTableContentProvider.getSaleValueComparator());
	}
	
	/**
	 * Creates the columns for extension plugins, if any.
	 */
	protected void createExtensionColumns() {
		// Set the number of base columns.
		this.baseColumnCount = baseAmountTableViewer.getColumnsCount();
		
		// Preserve the column names for later use.
		extensionColumnNames = getExtensionColumnNames();
		
		for (Pair extensionColumnName : extensionColumnNames) {
			// Create the column with the second part of the Pair, which should be the localized label.
			registerSortableColumn(baseAmountTableViewer.addTableColumn(extensionColumnName.getSecond().toString(), INITIAL_WIDTH_MED),
					baseAmountTableContentProvider.getDefaultComparator());
		}		
	}

	/**
	 * Creates the table columns.
	 */
	protected void createTableColumns() {
		createStandardBaseAmountTableColumns();
		createExtensionColumns();
	}
	
	private List<Pair> getExtensionColumnNames() {
		List<Pair> columnNames = new ArrayList<>();
		PluginHelper.findTables(getClass().getSimpleName(), getPluginId())
				.forEach(column -> 
					columnNames.addAll(((EPTableColumnNameCreator) column).visitColumnNameLabels()));
		return columnNames;
	}

	/**
	 * Gets the pluginId associated with the section.
	 * @return the pluginId.
	 */
	protected String getPluginId() {
		return PriceListManagerPlugin.PLUGIN_ID;
	}

	private void registerUnsavedChangesColumn() {
		final IEpTableColumn unsavedChangesColumn = registerSortableColumn(
				baseAmountTableViewer.addTableColumn(StringUtils.EMPTY, getTableProperties().getUnsavedChangesWidth()),
				baseAmountTableContentProvider.getChangesComparator());

		unsavedChangesColumn.getSwtTableColumn().setToolTipText(PriceListManagerMessages.get().BaseAmount_UnsavedChanges);
	}

	/**
	 * Re-applies the state policy.
	 */
	public void reApplyStatePolicy() {
		if (statePolicy != null) {
			applyStatePolicy(statePolicy);
		}
	}

	/**
	 * Gets the currently selected base amount if any selected.
	 *
	 * @return the base amount selected or null
	 */
	protected BaseAmountDTO getSelection() {
		final IStructuredSelection selection = (IStructuredSelection) baseAmountTableViewer.getSwtTableViewer().getSelection();
		return (BaseAmountDTO) selection.getFirstElement();
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		statePolicy.init(getModel());
		super.applyStatePolicy(statePolicy);
	}

	/**
	 * Refreshes base amounts table when required.
	 */
	public void refreshTableViewer() {
		if (baseAmountTableViewer != null && baseAmountTableViewer.getSwtTable() != null && !baseAmountTableViewer.getSwtTable().isDisposed()) {
			baseAmountTableViewer.getSwtTableViewer().refresh();
		}
	}

	private IDoubleClickListener getDoubleClickViewAction(final Button button) {
		return new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				if (getTableProperties().isEditButtonEnabled() &&  button.isEnabled()) {
					final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					processEditBaseAmount((BaseAmountDTO) selection.getFirstElement());
				}
			}
		};
	}

	private SelectionAdapter getEditAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) baseAmountTableViewer.getSwtTableViewer().getSelection();
				processEditBaseAmount((BaseAmountDTO) selection.getFirstElement());
			}
		};
	}

	private SelectionAdapter getDeleteAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) baseAmountTableViewer.getSwtTableViewer().getSelection();
				final BaseAmountDTO baseAmountDto = (BaseAmountDTO) selection.getFirstElement();
				final int status = new BaseAmountRemoveDialog(null, baseAmountDto).open();
				if (status == Window.OK) {
					controller.deleteBaseAmountDTO(baseAmountDto);
					notifyTableModified(baseAmountDto);
				}
			}
		};
	}

	private SelectionAdapter getAddAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				// clear the current selection in base amounts list
				baseAmountTableViewer.getSwtTableViewer().setSelection(null);
				processEditBaseAmount(null);
			}
		};
	}

	/**
	 * Handles edit base amount action.
	 *
	 * @param baseAmountDto - base amount object.
	 */
	private void processEditBaseAmount(final BaseAmountDTO baseAmountDto) {
		boolean editMode = true;
		BaseAmountDTO dto = baseAmountDto;
		if (dto == null) {
			dto = BaseAmountDTOCreator.createModel();
					
			// set new DTO defaults
			final RandomGuid randomGuid = ServiceLocator.getService(ContextIdNames.RANDOM_GUID);
			dto.setGuid(randomGuid.toString());
			editMode = false;
			dto.setObjectGuid(null);
			dto.setObjectType(BaseAmountObjectType.PRODUCT.getName());
			dto.setPriceListDescriptorGuid(getModel().getGuid());
		} else {
			dto = BaseAmountDTOCreator.createModel(baseAmountDto);
		}
		final BaseAmountDialog dialog = createDialog(editMode, dto);
		if (dialog.open() == Window.OK) {
			if (editMode && getEmptyObjects().contains(baseAmountDto)) {
				final PriceListEditorModel model = controller.getModel();
				model.getRawBaseAmounts().remove(baseAmountDto);
				controller.addBaseAmountDTO(dto);
				emptyObjects.remove(baseAmountDto);
			} else  if (editMode) {
				controller.updateBaseAmountDTO(baseAmountDto, dto);
			} else {
				controller.addBaseAmountDTO(dto);
			}
			notifyTableModified(dto);
		}
	}

	/**
	 * Creates dialog used to add/edit base amount.
	 *
	 * @param editMode - defines edit mode
	 * @param dto - base amount dto
	 * @return dialog for base amount editing
	 */
	protected BaseAmountDialog createDialog(final boolean editMode, final BaseAmountDTO dto) {
		return new BaseAmountDialog(editMode, dto, controller, showProductPane);
	}

	/**
	 * Triggers table modified event.
	 *
	 * @param baseAmountDto - object whose changes should be notified.
	 */
	protected void notifyTableModified(final BaseAmountDTO baseAmountDto) {
		if (isChangeSetsEnabled) {
			mapOfEditableBaseAmounts.put(baseAmountDto, changeSetHelper.getActiveChangeSet().getGuid());
		}
		if (controlModificationListener != null) {
			controlModificationListener.controlModified();
		}
		reApplyStatePolicy();
		refreshTableViewer();
	}

	@Override
	public void populateControls() {
		baseAmountTableViewer.getSwtTableViewer().refresh();
	}

	@Override
	public String getTargetIdentifier() {
		return "priceListBaseAmountEditorPageContainer"; //$NON-NLS-1$
	}

	/**
	 * Checks if base amount can be edited.
	 *
	 * @param baDTO - base amount object.
	 * @return - true if can be edited, false otherwise.
	 */
	boolean canEdit(final BaseAmountDTO baDTO) {
		if (!hasManageProductPricingPermissions()) {
			return false;
		}
		if (null == baDTO) {
			return false;
		}
		if (!isChangeSetsEnabled && !controller.isDeleted(baDTO)) {
			return true;
		}
		if (!changeSetHelper.isActiveChangeSet()) {
			return false;
		}
		if (controller.isDeleted(baDTO)) {
			return false;
		}
		final String changeSetGuid = mapOfEditableBaseAmounts.get(baDTO);
		final boolean isInSavedChangeSet = changeSetHelper.isActiveChangeSet() && changeSetGuid != null
		&& changeSetGuid.equals(changeSetHelper.getActiveChangeSet().getGuid());
		return isInSavedChangeSet;
	}

	/**
	 * @return true, if the user has permissions to manage product pricing
	 */
	private boolean hasManageProductPricingPermissions() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(
				PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRODUCT_PRICING);
	}

	/**
	 * Checks whether base amount object is locked.
	 * Method makes sense only if change sets are enabled.
	 *
	 * @param baDto - base amount object.
	 * @return - true if object is locked, false otherwise
	 */
	boolean isObjectLocked(final BaseAmountDTO baDto) {
		boolean isLocked = false;
		if (isChangeSetsEnabled) {
			isLocked = mapOfEditableBaseAmounts.containsKey(baDto);
		}
		return isLocked;
	}

	@Override
	public PriceListDescriptorDTO getModel() {
		return controller.getModel().getPriceListDescriptor();
	}

	/**
	 *
	 * @return base amount table viewer.
	 */
	public IEpTableViewer getBaseAmountTableViewer() {
		return baseAmountTableViewer;
	}

	/**
	 * Returns default base amount table content provider.
	 *
	 * @return default base amount table content provider.
	 */
	protected BaseAmountTableContentProvider getBaseAmountTableContentProvider() {
		return baseAmountTableContentProvider;
	}

	@Override
	public void searchBaseAmounts(final BaseAmountSearchEvent event) {
		if (isChangeSetsEnabled) {
			mapOfEditableBaseAmounts = changeSetHelper.getObjectsLocked(controller.getAllBaseAmounts().toArray());
			refreshTableViewer();
		}
	}

	/**
	 * Return the list of faked empty objects used for UI purposes.
	 *
	 * @return - list of faked empty objects used for UI purposes
	 */
	public List<BaseAmountDTO> getEmptyObjects() {
		return emptyObjects;
	}

	/**
	 * Adds new object to the list of faked empty objects used for UI purposes.
	 *
	 * @param emptyObject - new faked empty object.
	 */
	public void addEmptyObject(final BaseAmountDTO emptyObject) {
		emptyObjects.add(emptyObject);
	}

	/**
	 * Remove object from the list of faked empty objects used for UI purposes.
	 *
	 * @param emptyObject - new faked empty object.
	 */
	public void removeEmptyObject(final BaseAmountDTO emptyObject) {
		emptyObjects.remove(emptyObject);
	}

	@Override
	public void productChanged(final ItemChangeEvent<Product> event) {
		refreshPricingTable();
	}

	private void refreshPricingTable() {
		controller.reloadModel();
		refreshTableViewer();
	}

	@Override
	public void productSearchResultReturned(final SearchResultEvent<Product> event) {
		//do nothing
	}

	@Override
	public void productSkuChanged(final ItemChangeEvent<ProductSku> event) {
		refreshPricingTable();
	}

	@Override
	public void productSkuSearchResultReturned(final SearchResultEvent<ProductSku> event) {
		//do nothing
	}

	/**
	 * Updates the dto object in the model. If the dto is empty, it will be removed from the emptyObjects collection.
	 *
	 * @param oldDto the old dto
	 * @param newDto the new dto
	 */
	public void updateDto(final BaseAmountDTO oldDto, final BaseAmountDTO newDto) {
		if (oldDto.equals(newDto)) {
			return;
		}
		if (getEmptyObjects().contains(newDto)) {
			final PriceListEditorModel model = controller.getModel();
			model.getRawBaseAmounts().remove(newDto);
			controller.addBaseAmountDTO(newDto);
			removeEmptyObject(newDto);
		} else {
			controller.updateBaseAmountDTO(oldDto, newDto);
		}
		controller.updateBaseAmountDTO(oldDto, newDto);
		notifyTableModified(newDto);
	}

	/**
	 * @return the controlPane
	 */
	public IPolicyTargetLayoutComposite getControlPane() {
		return controlPane;
	}

	/**
	 * @return the buttonsComposite
	 */
	public IPolicyTargetLayoutComposite getButtonsComposite() {
		return buttonsComposite;
	}

	/**
	 * @return the controller
	 */
	public PriceListEditorController getController() {
		return controller;
	}

	/**
	 * @return the table properties object
	 */
	protected BaseAmountTableProperties getTableProperties() {
		return tableProperties;
	}

	private String getSkuGuidForSkuCode(final String skuCode) { 
		ProductSkuLookup productSkuLookup = ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_LOOKUP);
		ProductSku productSku = productSkuLookup.findBySkuCode(skuCode);
		if (productSku == null) {
			return null;
		}
		return productSku.getGuid();
	}

	/**
	 * Gets the extension column text.
	 * Used by various base amount label providers.
	 * @param element the model element.
	 * @param columnIndex the column index.
	 * @return the cell text or empty string.
	 */
	public String getExtensionColumnText(final Object element, final int columnIndex) {
		int extensionIndex = columnIndex - this.baseColumnCount;
		
		if (extensionIndex < 0) {
			LOG.warn("Invalid columnIndex: " + columnIndex);
			// This shouldn't happen, but just in case...
			return ""; //$NON-NLS-1$
		}
		
		List<EPTableColumnCreator> tables = PluginHelper
				.findTables(this.getClass().getSimpleName(), getPluginId());
		String result = StringUtils.EMPTY;
		for (EPTableColumnCreator table : tables) {
			result = ((EPTableColumnNameCreator) table).visitColumnText(
					element, this.extensionColumnNames.get(extensionIndex).getFirst().toString());
		}
		return result;
	}

	/**
	 * @return the showPricingOnly flag
	 */
	protected boolean isShowPricingOnly() {
		return showPricingOnly;
	}

	/**
	 * @return the baseColumnCount
	 */
	protected int getBaseColumnCount() {
		return baseColumnCount;
	}

}
