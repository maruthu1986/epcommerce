/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.progress.IProgressService;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.CatalogSkuOptionChangeSetTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.CatalogSkuOptionTableLabelProviderDecorator;
import com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider.TableLabelProviderAdapter;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.TableItems;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTreeViewer;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractObjectListener;
import com.elasticpath.cmclient.policy.ui.EditorTableSelectionProvider;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * Implements a section of the <code>CatalogCategoryPage</code> providing product the types of
 * products within a catalog.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.ExcessiveClassLength", "PMD.GodClass",
	"PMD.PrematureDeclaration" })
public class CatalogSkuOptionsSection extends AbstractCmClientEditorPageSectionPart implements SelectionListener,
		ISelectionChangedListener, IDoubleClickListener, SkuOptionNameVerifier, StatePolicyTarget {

	private static final int TREE_DROP_DOWN_COLUMN_WIDTH = 20;

	private static final int TABLE_VALUE_COLUMN_WIDTH = 200;

	private static final int TABLE_DISPLAY_NAME_COLUMN_WIDTH = 200;

	private static final int ORDERING_INCREASE_BY = 10;

	private static final Object EMPTY_OBJECT = new Object();

	private IPolicyTargetLayoutComposite controlPane;

	private final FormPage formPage;

	private final ControlModificationListener controlModificationListener;

	private Button addOptionButton;

	private Button addOptionValueButton;

	private Button editButton;

	private Button removeButton;

	private Button moveUpButton;

	private Button moveDownButton;

	private TreeViewer treeViewer;

	private final InternalObjectRegistryListener objectRegistryListener = new InternalObjectRegistryListener();

	private final SkuOptionService skuOptionService = (SkuOptionService) ServiceLocator.getService(
			ContextIdNames.SKU_OPTION_SERVICE);

	private final List<SkuOption> skuOptionList;

	private final CatalogSkuOptionsSectionObservable observable = new CatalogSkuOptionsSectionObservable();

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private static ListenerList listenerList;

	static {
		listenerList = new ListenerList(ListenerList.IDENTITY);
	}

	private final Map<String, PolicyActionContainer> policyTargetContainers  = new HashMap<>();

	private StatePolicy statePolicy;

	private PolicyActionContainer epTableSectionControls;

	private PolicyActionContainer addSkuOptionButtonContainer;
	private PolicyActionContainer addSkuOptionValueButtonContainer;
	private PolicyActionContainer editSelectionButtonContainer;
	private PolicyActionContainer removeSelectionButtonContainer;
	private PolicyActionContainer moveValueUpButtonContainer;
	private PolicyActionContainer moveValueDownButtonContainer;
	private PolicyActionContainer treeSectionContainer;
	private PolicyActionContainer treeSectionSelectedObjectContainer;


	private EditorTableSelectionProvider editorTableSelectionProvider;

	private final SkuOptionNameVerifier skuOptionNameVerifierImpl = new SkuOptionNameVerifierImpl();

	private final ChangeSetColumnDecorator changeSetColumnDecorator = new ChangeSetColumnDecorator();

	/**
	 * Constructor to create a "dirty" CatalogSkuOptionsSection instance
	 * i.e. changes on which have not been saved.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 * @param skuOptionList the list of sku options which were retained from previous session
	 * 		  and haven't been saved
	 */
	public CatalogSkuOptionsSection(final FormPage formPage, final AbstractCmClientFormEditor editor,
			final List<SkuOption> skuOptionList) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		this.formPage = formPage;
		controlModificationListener = editor;
		this.skuOptionList = skuOptionList;

		if (formPage instanceof Observer) {
			observable.addObserver((Observer) formPage);
		}
		initializeSection(editor);
	}


	/**
	 * Default constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CatalogSkuOptionsSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		this.formPage = formPage;
		controlModificationListener = editor;

		final List<SkuOption> readOnlySkuOptionList = skuOptionService.findAllSkuOptionFromCatalog(getModel().getUidPk());
		skuOptionList = new ArrayList<>(readOnlySkuOptionList.size());
		skuOptionList.addAll(readOnlySkuOptionList);

		if (formPage instanceof Observer) {
			observable.addObserver((Observer) formPage);
			observable.setChanged();
			observable.notifyObservers(readOnlySkuOptionList);
		}
		initializeSection(editor);
	}

	private void initializeSection(final AbstractCmClientFormEditor editor) {
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
		fireStatePolicyTargetActivated();

		if (editor instanceof EditorTableSelectionProvider) {
			editorTableSelectionProvider = (EditorTableSelectionProvider) editor;
		}
	}

	/**
	 * Returns the current state of the editor.
	 *
	 * @return the current state of the editor
	 */
	protected EpState getEditorState() {
		return getStatePolicy().determineState(treeSectionContainer);
	}

	private boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(getModel());
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {

		controlPane =  PolicyTargetCompositeFactory.wrapLayoutComposite(CompositeFactory.createGridLayoutComposite(parent, 2, false));
		final IEpLayoutData tableData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		treeSectionContainer = addPolicyActionContainer("treeSectionContainer"); //$NON-NLS-1$
		treeSectionSelectedObjectContainer = addPolicyActionContainer("treeSectionSelectedObjectContainer"); //$NON-NLS-1$


		final IEpTreeViewer tree = controlPane.addTreeViewer(false, tableData, treeSectionContainer);
		treeViewer = tree.getSwtTreeViewer();
		initializeTree(tree);
		treeViewer.addSelectionChangedListener(this);

		if (editorTableSelectionProvider != null) {
			final TableSelectionProvider editorProvider = editorTableSelectionProvider.getEditorTableSelectionProvider();
			treeViewer.addSelectionChangedListener(editorProvider);
		}
		treeViewer.addDoubleClickListener(this);

		createButtons();
		applyStatePolicy(getStatePolicy());
	}

	private void createButtons() {
		epTableSectionControls = addPolicyActionContainer("epTableSectionControls"); //$NON-NLS-1$
		addSkuOptionButtonContainer = addPolicyActionContainer("addSkuOptionButton"); //$NON-NLS-1$
		addSkuOptionValueButtonContainer = addPolicyActionContainer("addSkuOptionValueButton"); //$NON-NLS-1$
		editSelectionButtonContainer = addPolicyActionContainer("editSelectionButton"); //$NON-NLS-1$
		moveValueDownButtonContainer = addPolicyActionContainer("moveValueDownButton"); //$NON-NLS-1$
		moveValueUpButtonContainer = addPolicyActionContainer("moveValueUpButton"); //$NON-NLS-1$
		removeSelectionButtonContainer = addPolicyActionContainer("removeSelectionButton"); //$NON-NLS-1$

		final IPolicyTargetLayoutComposite buttonsComposite = controlPane.addGridLayoutComposite(1, false, null,  epTableSectionControls);
		editButton = buttonsComposite.addPushButton(CatalogMessages.get().CatalogSkuOptionsSection_EditSelectionButton,
				CatalogImageRegistry.getImage(CatalogImageRegistry.EDIT), null, editSelectionButtonContainer);
		editButton.addSelectionListener(this);

		addOptionButton = buttonsComposite.addPushButton(CatalogMessages.get().CatalogSkuOptionsSection_AddSkuOptionButton,
				CatalogImageRegistry.getImage(CatalogImageRegistry.ADD), null, addSkuOptionButtonContainer);
		addOptionButton.addSelectionListener(this);

		addOptionValueButton = buttonsComposite.addPushButton(CatalogMessages.get().CatalogSkuOptionsSection_AddSkuOptionValueButton,
				CatalogImageRegistry.getImage(CatalogImageRegistry.ADD), null, addSkuOptionValueButtonContainer);
		addOptionValueButton.addSelectionListener(this);

		removeButton = buttonsComposite.addPushButton(CatalogMessages.get().CatalogSkuOptionsSection_RemoveSelectionButton,
				CatalogImageRegistry.getImage(CatalogImageRegistry.REMOVE), null, removeSelectionButtonContainer);
		removeButton.addSelectionListener(this);

		moveUpButton = buttonsComposite.addPushButton(CatalogMessages.get().CatalogSkuOptionsSection_MoveValueUpButton,
				CatalogImageRegistry.getImage(CatalogImageRegistry.MOVE_UP), null, moveValueUpButtonContainer);
		moveUpButton.addSelectionListener(this);

		moveDownButton = buttonsComposite.addPushButton(CatalogMessages.get().CatalogSkuOptionsSection_MoveValueDownButton,
				CatalogImageRegistry.getImage(CatalogImageRegistry.MOVE_DOWN), null, moveValueDownButtonContainer);
		moveDownButton.addSelectionListener(this);
	}

	private void initializeTree(final IEpTreeViewer tree) {
		tree.getSwtTreeViewer().setComparator(new TreeComparator());

		addColumns(tree);

		treeViewer.setContentProvider(new TreeContentProvider());

		addLabelProvider();

		treeViewer.setInput(EMPTY_OBJECT);
	}

	private void addColumns(final IEpTreeViewer tree) {
		tree.addColumn(StringUtils.EMPTY, TREE_DROP_DOWN_COLUMN_WIDTH);

		if (changeSetColumnDecorator.isDecoratable()) {
			changeSetColumnDecorator.addLockColumn(tree);
			changeSetColumnDecorator.addActionColumn(tree);
		}

		tree.addColumn(CatalogMessages.get().CatalogSkuOptionsSection_TableValueColumn, TABLE_VALUE_COLUMN_WIDTH);
		tree.addColumn(CatalogMessages.get().CatalogSkuOptionsSection_TableDisplayNameColumn, TABLE_DISPLAY_NAME_COLUMN_WIDTH);
	}

	private void addLabelProvider() {
		if (changeSetColumnDecorator.isDecoratable()) {

			treeViewer.setLabelProvider(new CatalogSkuOptionTableLabelProviderDecorator(
					new CatalogSkuOptionChangeSetTableLabelProviderDecorator(
							new TableLabelProviderAdapter(), getSkuOptionTableItems(), getSkuOptionValueTableItems()),
							((AbstractCmClientEditorPage) formPage).getSelectedLocale()));
		} else {
			treeViewer.setLabelProvider(new CatalogSkuOptionTableLabelProviderDecorator(
					new TableLabelProviderAdapter(), ((AbstractCmClientEditorPage) formPage).getSelectedLocale()));
		}
	}

	@Override
	protected void populateControls() {
		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		controlPane.setControlModificationListener(controlModificationListener);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// empty for now
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
		// not used
	}

	@Override
	public void widgetSelected(final SelectionEvent selectionEvent) {
		if (selectionEvent.getSource() == addOptionButton) {
			final SkuOption addedItem = addSkuOptionAction();
			if (addedItem != null) {
				addSkuOptionAddedItem(addedItem);
				refreshViewerInput();
			}
		} else if (selectionEvent.getSource() == addOptionValueButton) {
			final SkuOption skuOption = (SkuOption) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();

			final SkuOptionValue newSkuOptionValue = addSkuOptionValueAction();
			if (newSkuOptionValue != null) {
				newSkuOptionValue.setSkuOption(skuOption);
				addSkuOptionValueToAddedItems(newSkuOptionValue);
				refreshViewerInput();
			}
		} else if (selectionEvent.getSource() == editButton) {
			final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
			editAction(selection.getFirstElement());
			treeViewer.refresh(selection.getFirstElement());
		} else if (selectionEvent.getSource() == removeButton) {
			final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
			removeWithConfirmationAction((Entity) selection.getFirstElement());
		} else if (selectionEvent.getSource() == moveUpButton || selectionEvent.getSource() == moveDownButton) {
			final SkuOptionValue selectedOption = (SkuOptionValue) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
			moveUpDownAction(selectionEvent, selectedOption);
		}
		applyStatePolicy(getStatePolicy());
	}

	private void removeWithConfirmationAction(final Entity removedItem) {
		final boolean answerYes = MessageDialog.openConfirm(getSection().getShell(),
			CatalogMessages.get().CatalogSkuOptionsSection_RemoveSelectionDialogTitle,
				NLS.bind(CatalogMessages.get().CatalogSkuOptionsSection_RemoveSelectionDialogMessage,
				getItemName(removedItem)));
		if (answerYes && removeSelectionAction(removedItem)) {
			if (removedItem instanceof SkuOption) {
				removeSkuOption((SkuOption) removedItem);
			} else if (removedItem instanceof SkuOptionValue) {
				removeSkuOptionValue((SkuOptionValue) removedItem);
			} else {
				// should never get here
				throw new EpUiException("Not implemented.", null); //$NON-NLS-1$
			}
			updateDependentObjectOnPolicyControlledButtons(removedItem);
			markDirty();
			notifyParent();
			refreshViewerInput();
		}
	}

	private void removeSkuOption(final SkuOption removedItem) {
		if (changeSetHelper.isChangeSetsEnabled()) {
			removeOrMarkSkuOptionAsRemoved(removedItem);
		} else {
			skuOptionList.remove(removedItem);

			getSkuOptionTableItems().addRemovedItem(removedItem);
		}
	}

	private void removeSkuOptionValue(final SkuOptionValue removedItem) {
		final SkuOption parentSkuOption = removedItem.getSkuOption();

		if (changeSetHelper.isChangeSetsEnabled()) {
			removeOrMarkSkuOptionValueAsRemoved(removedItem, parentSkuOption);
		} else {
			parentSkuOption.removeOptionValue(removedItem.getOptionValueKey());
		}
		getSkuOptionTableItems().addModifiedItem(parentSkuOption);
	}

	private void removeOrMarkSkuOptionValueAsRemoved(final SkuOptionValue removedItem, final SkuOption parentSkuOption) {
		if (getSkuOptionValueTableItems().containsAddedItem(removedItem)) {
			parentSkuOption.removeOptionValue(removedItem.getOptionValueKey());
			getSkuOptionValueTableItems().deleteAddedItem(removedItem);
		} else {
			getSkuOptionValueTableItems().addRemovedItem(removedItem);
		}
	}

	private void removeOrMarkSkuOptionAsRemoved(final SkuOption removedItem) {
		if (getSkuOptionTableItems().containsAddedItem(removedItem)) {
			getSkuOptionTableItems().deleteAddedItem(removedItem);
			skuOptionList.remove(removedItem);
		} else {
			getSkuOptionTableItems().addRemovedItem(removedItem);
		}
	}

	private void moveUpDownAction(final SelectionEvent selectionEvent, final SkuOptionValue selectedOption) {
		if (selectionEvent.getSource() == moveUpButton) {
			moveOptionValueUp(selectedOption);

		} else if (selectionEvent.getSource() == moveDownButton) {
			moveOptionValueDown(selectedOption);
		}

		final SkuOption parentOfSelectedOption = selectedOption.getSkuOption();
		getSkuOptionTableItems().addModifiedItem(parentOfSelectedOption);

		// refresh the parent of the moved items
		treeViewer.refresh(parentOfSelectedOption);
		updateDependentObjectOnPolicyControlledButtons(selectedOption);
		applyStatePolicy(getStatePolicy());
		markDirty();
	}

	private void moveOptionValueUp(final SkuOptionValue selectedOption) {
		final SkuOptionValue lowerValue = findPreviousOptionValue(selectedOption);
		swapSkuOptionValueOrdering(selectedOption, lowerValue);
	}

	private void moveOptionValueDown(final SkuOptionValue selectedOption) {
		final SkuOptionValue upperValue = findNextOptionValue(selectedOption);
		swapSkuOptionValueOrdering(selectedOption, upperValue);
	}

	private void swapSkuOptionValueOrdering(final SkuOptionValue firstValue, final SkuOptionValue secondValue) {
		final int secondValueOrder = secondValue.getOrdering();
		secondValue.setOrdering(firstValue.getOrdering());
		firstValue.setOrdering(secondValueOrder);
	}

	private SkuOptionValue findNextOptionValue(final SkuOptionValue selectedOption) {
		final Collection<SkuOptionValue> valuesFromSkuOption = getOtherUnremovedValuesFromParent(selectedOption);
		SkuOptionValue result = null;

		final int limitIndex = selectedOption.getOrdering();
		int nextIndex = selectedOption.getSkuOption().getMaxOrdering();
		for (final SkuOptionValue value : valuesFromSkuOption) {
			final int currentOrderingIndex = value.getOrdering();
			// find the index that is greater than the limitIndex and every one less than the currentOrderingIndex
			if (currentOrderingIndex > limitIndex && currentOrderingIndex <= nextIndex) {
				nextIndex = currentOrderingIndex;
				result = value;
			}
		}
		return result;
	}

	private SkuOptionValue findPreviousOptionValue(final SkuOptionValue selectedOption) {
		final Collection<SkuOptionValue> valuesFromSkuOption = getOtherUnremovedValuesFromParent(selectedOption);
		SkuOptionValue result = null;

		int previousIndex = selectedOption.getSkuOption().getMinOrdering();
		final int limitIndex = selectedOption.getOrdering();
		for (final SkuOptionValue value : valuesFromSkuOption) {
			final int currentOrderingIndex = value.getOrdering();
			// find the index that is less than the limitIndex and every one greater than the currentOrderingIndex
			if (currentOrderingIndex < limitIndex && currentOrderingIndex >= previousIndex) {
				previousIndex = currentOrderingIndex;
				result = value;
			}
		}
		return result;
	}

	private Collection<SkuOptionValue> getOtherUnremovedValuesFromParent(final SkuOptionValue selectedOption) {
		final Collection<SkuOptionValue> valuesFromSkuOption = new ArrayList<>(selectedOption.getSkuOption().getOptionValues());
		valuesFromSkuOption.removeAll(getCatalogModel().getSkuOptionValueTableItems().getRemovedItems());
		return valuesFromSkuOption;
	}

	private void editAction(final Object object) {
		if (object instanceof SkuOption) {
			editSkuOptionAction((SkuOption) object);
		} else if (object instanceof SkuOptionValue) {
			editSkuOptionValueAction((SkuOptionValue) object);
		} else {
			throw new EpUiException("Object type unimplemented: " + object.getClass(), null); //$NON-NLS-1$
		}
	}

	private void editSkuOptionValueAction(final SkuOptionValue skuOptionValue) {
		final CatalogSkuOptionValueAddEditDialog addOptionDialog =
			new CatalogSkuOptionValueAddEditDialog(getSection().getShell(), skuOptionValue, getModel().getSupportedLocales(),
					getModel().getDefaultLocale(), this);

		if (addOptionDialog.open() == Window.OK) {
			getSkuOptionValueTableItems().addModifiedItem(addOptionDialog.getSkuOptionValue());
			getSkuOptionTableItems().addModifiedItem(addOptionDialog.getSkuOptionValue().getSkuOption());
			markDirty();
		}
	}

	private void editSkuOptionAction(final SkuOption skuOption) {
		final CatalogSkuOptionsPage page = (CatalogSkuOptionsPage) formPage;
		final CatalogSkuOptionAddEditDialog addOptionDialog = new CatalogSkuOptionAddEditDialog(getSection().getShell(),
				skuOption, this, getModel().getSupportedLocales(), page.getSelectedLocale(), getModel());

		if (addOptionDialog.open() == Window.OK) {
			getSkuOptionTableItems().addModifiedItem(addOptionDialog.getSkuOption());
			markDirty();
		}
	}

	private SkuOptionValue addSkuOptionValueAction() {
		final CatalogSkuOptionValueAddEditDialog addOptionDialog =
			new CatalogSkuOptionValueAddEditDialog(getSection().getShell(),	null, getModel().getSupportedLocales(), getModel().getDefaultLocale(),
					this);

		if (addOptionDialog.open() == Window.OK) {
			markDirty();
			return addOptionDialog.getSkuOptionValue();
		}
		return null;
	}

	private SkuOption addSkuOptionAction() {
		final CatalogSkuOptionAddEditDialog addOptionDialog = new CatalogSkuOptionAddEditDialog(getSection().getShell(),
				null, this, getModel().getSupportedLocales(), getModel().getDefaultLocale(), getModel());
		if (addOptionDialog.open() == Window.OK) {
			markDirty();
			return addOptionDialog.getSkuOption();
		}
		return null;
	}

	private boolean removeSelectionAction(final Entity object) {
		final ParameterPasser passer = new ParameterPasser();
		passer.canRemove = false;
		final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			// we can use the passer variable here because this is a blocking job
			progressService.busyCursorWhile(monitor -> {
				if (object instanceof SkuOption) {

					if (!skuOptionService.isSkuOptionInUse(object.getUidPk())) {
						passer.canRemove = true;
					}
				} else if (object instanceof SkuOptionValue) {
					if (!skuOptionService.isSkuOptionValueInUse(object.getUidPk())) {
						passer.canRemove = true;
					}
				} else {
					throw new EpUiException("Unknown object type: " + object.getClass(), null); //$NON-NLS-1$
				}
			});
		} catch (final InvocationTargetException | InterruptedException e) {
			throw new EpUiException("Error removing: " + e.getMessage(), e); //$NON-NLS-1$
		}

		if (!passer.canRemove) {
			MessageDialog.openError(getSection().getShell(),
				CatalogMessages.get().CatalogSkuOptionsSection_ErrorDialog_InUse_title,
					NLS.bind(CatalogMessages.get().CatalogSkuOptionsSection_ErrorDialog_InUse_desc,
					getItemName(object)));
		}
		return passer.canRemove;
	}

	private String getItemName(final Object object) {
		final Locale selectedLocale = ((AbstractCmClientEditorPage) formPage).getSelectedLocale();
		if (object instanceof SkuOption) {
			final SkuOption skuOption = (SkuOption) object;
			return String.format("%1$s - %2$s", skuOption.getOptionKey(), skuOption.getDisplayName(selectedLocale, true)); //$NON-NLS-1$
		} else if (object instanceof SkuOptionValue) {
			final SkuOptionValue skuOptionValue = (SkuOptionValue) object;
			return String
					.format("%1$s - %2$s", skuOptionValue.getOptionValueKey(), skuOptionValue.getDisplayName(selectedLocale, true)); //$NON-NLS-1$
		}
		return object.toString();
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		final Object element = selection.getFirstElement();
		updateDependentObjectOnPolicyControlledButtons(element);
		applyStatePolicyWithDependentObject(getStatePolicy(), element);
	}

	private void updateDependentObjectOnPolicyControlledButtons(final Object object) {
		resolveDependentObjectOnSkuOptionValueRelevantButtons(object);
		addSkuOptionValueButtonContainer.setPolicyDependent(object);
	}

	private void resolveDependentObjectOnSkuOptionValueRelevantButtons(final Object object) {
		updateDependentObjectOnSkuOptionValueRelevantButtons(object);

		if (object instanceof SkuOptionValue) {
			final SkuOptionValue skuOptionValue = (SkuOptionValue) object;

			resolveDependentObjectForDeletedSkuOptionValue(skuOptionValue);
			resolveDependentObjectForDownOrderingOfSkuOptionValue(skuOptionValue);
			resolveDependentObjectForUpOrderingOfSkuOptionValue(skuOptionValue);
		}
	}

	private void resolveDependentObjectForDeletedSkuOptionValue(final SkuOptionValue skuOptionValue) {
		if (getSkuOptionValueTableItems().getRemovedItems().contains(skuOptionValue)) {
			updateDependentObjectOnSkuOptionValueRelevantButtons(null);
		}
	}

	private void resolveDependentObjectForUpOrderingOfSkuOptionValue(final SkuOptionValue skuOptionValue) {
		if (findPreviousOptionValue(skuOptionValue) == null) {
			moveValueUpButtonContainer.setPolicyDependent(null);
		}
	}

	private void resolveDependentObjectForDownOrderingOfSkuOptionValue(final SkuOptionValue skuOptionValue) {
		if (findNextOptionValue(skuOptionValue) == null) {
			moveValueDownButtonContainer.setPolicyDependent(null);
		}
	}

	private void updateDependentObjectOnSkuOptionValueRelevantButtons(final Object object) {
		removeSelectionButtonContainer.setPolicyDependent(object);
		editSelectionButtonContainer.setPolicyDependent(object);
		moveValueUpButtonContainer.setPolicyDependent(object);
		moveValueDownButtonContainer.setPolicyDependent(object);
	}

	private void applyStatePolicyWithDependentObject(final StatePolicy statePolicy, final Object object) {
		setStatePolicy(statePolicy);
		statePolicy.init(object);
		applyStatePolicy();
	}

	/**
	 * Internal object registry listener.
	 */
	private class InternalObjectRegistryListener extends AbstractObjectListener {

		@Override
		public void eventFired(final String key) {
			if (ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET.equals(key)) {
				reapplyStatePolicy();
			}
		}
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			updateChangeSetWithSkuOptionValuesFromTableItems();
			clearAllSkuOptionValueTableItems();

			persistAddedItems();
			persistModifiedItems();
			persistRemovedItems();
			clearAllSkuOptionTableItems();

			super.commit(onSave);
			notifyParent();
		}
	}

	private void updateChangeSetWithSkuOptionValuesFromTableItems() {
		for (final SkuOptionValue skuOptionValue : getSkuOptionValueTableItems().getRemovedItems()) {
			changeSetHelper.addObjectToChangeSet(skuOptionValue, ChangeSetMemberAction.DELETE);

			SkuOption parent = skuOptionValue.getSkuOption();
			parent.removeOptionValue(skuOptionValue.getOptionValueKey());
		}
	}

	private void clearAllSkuOptionValueTableItems() {
		getSkuOptionValueTableItems().getAddedItems().clear();
		getSkuOptionValueTableItems().getModifiedItems().clear();
		getSkuOptionValueTableItems().getRemovedItems().clear();
	}

	private void clearAllSkuOptionTableItems() {
		getSkuOptionTableItems().getAddedItems().clear();
		getSkuOptionTableItems().getModifiedItems().clear();
		getSkuOptionTableItems().getRemovedItems().clear();
	}

	private void persistAddedItems() {
		final Set<SkuOption> addedSkuOptions = getSkuOptionTableItems().getAddedItems();
		persistSkuOptionsOnChangeSetAction(addedSkuOptions, ChangeSetMemberAction.ADD);
	}

	private void persistModifiedItems() {
		final Set<SkuOption> modifiedSkuOptions = getSkuOptionTableItems().getModifiedItems();
		persistSkuOptionsOnChangeSetAction(modifiedSkuOptions, ChangeSetMemberAction.EDIT);
	}

	private void persistRemovedItems() {
		final Set<SkuOption> removedSkuOptions = getSkuOptionTableItems().getRemovedItems();
		persistSkuOptionsOnChangeSetAction(removedSkuOptions, ChangeSetMemberAction.DELETE);
	}

	private void persistSkuOptionsOnChangeSetAction(final Set<SkuOption> skuOptions, final ChangeSetMemberAction changeSetMemberAction) {
		for (final SkuOption skuOption : skuOptions) {
			persistSkuOptionOnChangeSetAction(skuOption, changeSetMemberAction);
		}
	}

	private void persistSkuOptionOnChangeSetAction(final SkuOption skuOption, final ChangeSetMemberAction changeSetMemberAction) {
		if (changeSetMemberAction == ChangeSetMemberAction.ADD) {
			skuOptionService.add(skuOption);
			changeSetHelper.addObjectToChangeSet(skuOption, changeSetMemberAction);
		} else if (changeSetMemberAction == ChangeSetMemberAction.EDIT) {
			skuOptionService.saveOrUpdate(skuOption);
			changeSetHelper.addObjectToChangeSet(skuOption, changeSetMemberAction);
		} else if (changeSetMemberAction == ChangeSetMemberAction.DELETE && skuOption.isPersisted()) {
			changeSetHelper.addObjectToChangeSet(skuOption, changeSetMemberAction);
			skuOptionService.remove(skuOption);
		}
	}

	@Override
	public Catalog getModel() {
		return ((CatalogModel) super.getModel()).getCatalog();
	}

	/**
	 * Returns the catalog model.
	 * @return the catalog model
	 */
	public CatalogModel getCatalogModel() {
		return (CatalogModel) super.getModel();
	}

	/**
	 * Adds an item to the set of items added.
	 *
	 * @param skuOption the item to add
	 */
	protected void addSkuOptionAddedItem(final SkuOption skuOption) {

		// need the sku option here because service methods only accept SkuOption
		getSkuOptionTableItems().addAddedItem(skuOption);
		skuOptionList.add(skuOption);
		observable.setChanged();
		notifyParent();
		markDirty();
	}

	/**
	 * Adds a new SkuOptionValue to a skuOption.
	 * @param skuOptionValue the SkuOptionValue to be added.
	 */
	protected void addSkuOptionValueToAddedItems(final SkuOptionValue skuOptionValue) {
		final SkuOption skuOption = skuOptionValue.getSkuOption();

		final int maxOrdering = skuOption.getMaxOrdering();
		skuOptionValue.setOrdering(maxOrdering + ORDERING_INCREASE_BY);

		skuOption.addOptionValue(skuOptionValue);

		// covers case if skuOption is newly created, change set action should be add and not update.
		if (skuOption.isPersisted()) {
			getSkuOptionTableItems().addModifiedItem(skuOption);
		} else {
			getSkuOptionTableItems().addAddedItem(skuOption);
		}

		getSkuOptionValueTableItems().addAddedItem(skuOptionValue);
	}


	/**
	 * Updates the table viewers input. This is done via setting the input to a dummy object (if
	 * previous input is <code>null</code>) or setting it to the previous input.
	 */
	private void refreshViewerInput() {
		if (treeViewer != null && treeViewer.getContentProvider() != null) {
			if (treeViewer.getInput() == null) {
				treeViewer.setInput(EMPTY_OBJECT);
			} else {
				treeViewer.setInput(treeViewer.getInput());
			}
		}
	}

	/**
	 * Content provider for the tree.
	 */
	private class TreeContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getChildren(final Object parentElement) {
			if (parentElement instanceof SkuOption) {
				return ((SkuOption) parentElement).getOptionValues().toArray();
			}
			return new Object[0];
		}

		@Override
		public Object getParent(final Object element) {
			if (element instanceof SkuOptionValue) {
				return ((SkuOptionValue) element).getSkuOption();
			}
			return null;
		}

		@Override
		public boolean hasChildren(final Object element) {
			if (element instanceof SkuOption) {
				return !((SkuOption) element).getOptionValues().isEmpty();
			}
			return false;
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			return skuOptionList.toArray();
		}

		@Override
		public void dispose() {
			// not used
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// not used
		}
	}

	/**
	 * Parameter holder class for passing a parameter between threads.
	 */
	private final class ParameterPasser {
		private boolean canRemove;
	}

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		
		treeSectionSelectedObjectContainer.setPolicyDependent(selection.getFirstElement());
		if (isAuthorized() && getStatePolicy().determineState(treeSectionSelectedObjectContainer) == EpState.EDITABLE) {
			editAction(selection.getFirstElement());
			treeViewer.refresh(selection.getFirstElement());
		}
	}

	/**
	 * SKU option/value specific sorter to sort on the SKU option and SKU option values on key and values on their ordering.
	 */
	private class TreeComparator extends ViewerComparator {
		@Override
		public int compare(final Viewer viewer, final Object object1, final Object object2) {
			// use the super comparer where object1 and object2 don't inherit from the same class
			if (objectsAreDifferentSkuOptionTypes(object1, object2)) {
				return super.compare(viewer, object1, object2);
			}

			if (objectsAreSkuOptionValues(object1, object2)) {
				return compareSkuOptionValues(object1, object2);
			}

			if (objectsAreSkuOptions(object1, object2)) {
				return compareSkuOptions(object1, object2);
			}

			// should never get here
			throw new EpUiException("Unimplemented sorting type.", null); //$NON-NLS-1$

		}

		private int compareSkuOptions(final Object object1, final Object object2) {
			final SkuOption skuOption1 = (SkuOption) object1;
			final SkuOption skuOption2 = (SkuOption) object2;
			return skuOption1.getOptionKey().compareToIgnoreCase(skuOption2.getOptionKey());
		}

		private int compareSkuOptionValues(final Object object1, final Object object2) {
			final SkuOptionValue skuOptionValue1 = (SkuOptionValue) object1;
			final SkuOptionValue skuOptionValue2 = (SkuOptionValue) object2;

			if (getSkuOptionValueTableItems().getRemovedItems().contains(object1)
					&& getSkuOptionValueTableItems().getRemovedItems().contains(object2)) {
				return 0;
			}

			if (getSkuOptionValueTableItems().getRemovedItems().contains(object1)) {
				return 1;
			}

			if (getSkuOptionValueTableItems().getRemovedItems().contains(object2)) {
				return -1;
			}

			return Integer.valueOf(skuOptionValue1.getOrdering()).compareTo(skuOptionValue2.getOrdering());
		}

		private boolean objectsAreDifferentSkuOptionTypes(final Object firstObject, final Object secondObject) {
			return (firstObject instanceof SkuOption && secondObject instanceof SkuOptionValue)
					|| (firstObject instanceof SkuOptionValue && secondObject instanceof SkuOption);

		}

		private boolean objectsAreSkuOptionValues(final Object firstObject, final Object secondObject) {
			return firstObject instanceof SkuOptionValue && secondObject instanceof SkuOptionValue;
		}

		private boolean objectsAreSkuOptions(final Object firstObject, final Object secondObject) {
			return firstObject instanceof SkuOption && secondObject instanceof SkuOption;
		}

	}

	@Override
	public boolean verifySkuOptionKey(final String newValue) {
		for (final SkuOption skuOption : skuOptionList) {
			if (newValue.equals(skuOption.getOptionKey())) {
				return false;
			}
		}

		final Set <SkuOption> skuOptions = getSkuOptionTableItems().getAddedItems();
		for (final SkuOption skuOption : skuOptions) {
			if (newValue.equals(skuOption.getOptionKey())) {
				return false;
			}
		}

		return skuOptionNameVerifierImpl.verifySkuOptionKey(newValue);
	}

	@Override
	public boolean verifySkuOptionValueKey(final String newValue) {
		for (final SkuOption option : skuOptionList) {
			final Collection<SkuOptionValue> skuOptionValues = option.getOptionValues();
			if (skuOptionValues != null) {
				final boolean result = checkSkuOptionValues(skuOptionValues, newValue);
				if (!result) {
					return false;
				}
			}
		}
		final Set <SkuOption> skuOptions = getSkuOptionTableItems().getAddedItems();
		for (final SkuOption skuOption : skuOptions) {
			final boolean result = checkSkuOptionValues(skuOption.getOptionValues(), newValue);
			if (!result) {
				return false;
			}
		}

		return skuOptionNameVerifierImpl.verifySkuOptionValueKey(newValue);
	}

	private boolean checkSkuOptionValues(final Collection<SkuOptionValue> skuOptionValues, final String newValue) {
		for (final SkuOptionValue skuOptionValue : skuOptionValues) {
			if (newValue.equals(skuOptionValue.getOptionValueKey())) {
				return false;
			}
		}
		return !skuOptionService.keyExists(newValue);
	}

	@Override
	public void dispose() {
		super.dispose();
		observable.deleteObservers();
		objectRegistryListener.deRegisterListener();
		PolicyPlugin.getDefault().unregisterStatePolicyTarget(this);

	}

	private void notifyParent() {
		notifyParent(skuOptionList);
	}

	private void notifyParent(final List<SkuOption> skuOptionList) {
		observable.notifyObservers(skuOptionList);
	}

	/**
	 * An observable subclass. Sub-classed to have access to setChanged().
	 *
	 */
	class CatalogSkuOptionsSectionObservable extends Observable {
		@Override
		@SuppressWarnings("PMD.UselessOverridingMethod")
		protected void setChanged() {
			super.setChanged();
		}
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		statePolicy.init(getDependentObject());
		reapplyStatePolicy();
	}

	private void reapplyStatePolicy() {
		applyStatePolicy();
		refreshViewerInput();
	}

	@Override
	public PolicyActionContainer addPolicyActionContainer(final String name) {
		final PolicyActionContainer container = new PolicyActionContainer(name);
		getPolicyActionContainers().put(name, container);
		return container;
		}

	@Override
	public Map<String, PolicyActionContainer> getPolicyActionContainers() {
		return policyTargetContainers;
		}

	@Override
	public void addGovernableListener(final StatePolicyTargetListener governableListener) {
		listenerList.add(governableListener);
	}

	@Override
	public void removeGovernableListener(final StatePolicyTargetListener listener) {
		listenerList.remove(listener);
	}

	@Override
	public String getTargetIdentifier() {
		return "catalogSkuOptionsSection"; //$NON-NLS-1$
	}

	/**
	 * Apply the already stored state policy.
	 */
	public void applyStatePolicy() {
		if (statePolicy != null) {
			for (final PolicyActionContainer container : getPolicyActionContainers().values()) {
				statePolicy.apply(container);
			}
		}
	}

	/**
	 * Fire the activation event to all listeners.
	 */
	private void fireStatePolicyTargetActivated() {
		for (final Object listener : listenerList.getListeners()) {
			((StatePolicyTargetListener) listener).statePolicyTargetActivated(this);
		}
	}

	/**
	 *
	 * @return the statePolicy
	 */
	public StatePolicy getStatePolicy() {
		return statePolicy;
	}

	/**
	 *
	 * @param statePolicy the statePolicy to set
	 */
	public void setStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
	}

	private TableItems<SkuOption> getSkuOptionTableItems() {
		return getCatalogModel().getSkuOptionTableItems();
	}

	private TableItems<SkuOptionValue> getSkuOptionValueTableItems() {
		return getCatalogModel().getSkuOptionValueTableItems();
	}

	@Override
	public void refresh() {
		// do nothing
	}

}
