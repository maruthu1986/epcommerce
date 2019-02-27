/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.editors;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.changeset.ChangeSetImageRegistry;
import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.actions.ChangeSetActionUtil;
import com.elasticpath.cmclient.changeset.dialogs.ChangeSetDialog;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.changeset.helpers.ComponentHelper;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.pagination.PaginationInfo;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.framework.impl.TableColumnSorterControl;
import com.elasticpath.cmclient.core.ui.framework.impl.TableColumnSorterSupporterListener;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwarePaginationControl;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.PaginationConfig;
import com.elasticpath.commons.pagination.Paginator;
import com.elasticpath.commons.pagination.PaginatorFactory;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.commons.pagination.SortingField;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.changeset.ChangeSetMemberSortingField;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.service.changeset.impl.SkuOptionValueMetadataResolver;

/**
 * The objects section UI.
 */
@SuppressWarnings({ "PMD.ExcessiveImports", "PMD.GodClass", "PMD.PrematureDeclaration" })
public class ChangeSetEditorObjectsSection extends AbstractPolicyAwareEditorPageSectionPart implements ICheckStateListener,
		TableColumnSorterSupporterListener {

	private static final String OBJECT_TABLE = "Object Table"; //$NON-NLS-1$
	private static final int TABLE_HEIGHT = 265;

	private ComponentHelper componentHelper = new ComponentHelper();

	private CheckboxTableViewer checkboxTableViewer;

	private IPolicyTargetLayoutComposite mainComposite;

	private IEpTableViewer objectsTableViewer;

	private Button moveButton;

	private PolicyActionContainer container;

	private StatePolicy statePolicy;

	private Button openButton;

	private AbstractPolicyAwarePaginationControl<ChangeSetMember> paginationControl;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	/**
	 * Constructs a new section.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public ChangeSetEditorObjectsSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);

	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		mainComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(CompositeFactory.createGridLayoutComposite(client, 2, false));
		mainComposite.getSwtComposite().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
		container = addPolicyActionContainer("objectsSection"); //$NON-NLS-1$
		final PolicyActionContainer displayContainer = addPolicyActionContainer("objectsDisplaySection"); //$NON-NLS-1$

		final IEpLayoutData buttonData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING);

		final IEpLayoutData paginationData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);

		final SortingField defaultSortingField = ChangeSetMemberSortingField.OBJECT_NAME;
		final SortingDirection defaultSortingDirection = SortingDirection.ASCENDING;
		final Paginator<ChangeSetMember> paginator = createPaginator(defaultSortingField, defaultSortingDirection);

		paginationControl = new AbstractPolicyAwarePaginationControl<ChangeSetMember>(mainComposite, paginationData, displayContainer, paginator) {
			@Override
			public void update(final Page<ChangeSetMember> newPage) {
				updateTable(newPage);
			}
		};
		paginationControl.createControls();

		// empty control to fill the second cell in the row
		mainComposite.addEmptyComponent(null, container);

		final IEpLayoutData tableData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 2);
		objectsTableViewer = mainComposite.addCheckboxTableViewer(true, tableData, container, OBJECT_TABLE);
		((GridData) objectsTableViewer.getSwtTable().getLayoutData()).heightHint = TABLE_HEIGHT;

		setFiltersOnObjectTableViewer(objectsTableViewer);

		TableColumnSorterControl tableColumnSorterControl = new TableColumnSorterControl(objectsTableViewer.getSwtTable(), this);

		final int[] widths = new int[] { 60, 80, 160, 220, 115, 125, 100 };
		final String[] columnNames = new String[] {
				ChangeSetMessages.get().ChangeSetEditor_ObjectsTable_SelectedColumn,
				ChangeSetMessages.get().ChangeSetEditor_ObjectsTable_Action,
				ChangeSetMessages.get().ChangeSetEditor_ObjectsTable_IdColumn,
				ChangeSetMessages.get().ChangeSetEditor_ObjectsTable_NameColumn,
				ChangeSetMessages.get().ChangeSetEditor_ObjectsTable_TypeColumn,
				ChangeSetMessages.get().ChangeSetEditor_ObjectsTable_DateAddedColumn,
				ChangeSetMessages.get().ChangeSetEditor_ObjectsTable_AddedByColumn };

		final SortingField[] sortingFields = new SortingField[] {
				null,
				ChangeSetMemberSortingField.CHANGE_TYPE,
				ChangeSetMemberSortingField.OBJECT_ID,
				defaultSortingField,
				ChangeSetMemberSortingField.OBJECT_TYPE,
				ChangeSetMemberSortingField.DATE_ADDED,
				ChangeSetMemberSortingField.ADDED_BY };

		for (int columnIndex = 0; columnIndex < widths.length; columnIndex++) {
			final IEpTableColumn tableColumn = objectsTableViewer.addTableColumn(columnNames[columnIndex], widths[columnIndex]);
			tableColumnSorterControl.registerColumnListener(tableColumn.getSwtTableColumn(), sortingFields[columnIndex]);
		}

		final int sortColumnIndex = ArrayUtils.indexOf(sortingFields, defaultSortingField);
		final TableColumn defaultSortingColumn = objectsTableViewer.getSwtTable().getColumn(sortColumnIndex);
		tableColumnSorterControl.updateTableSortDirection(defaultSortingColumn, defaultSortingDirection);

		objectsTableViewer.setContentProvider(new ObjectsChangeSetProvider());
		objectsTableViewer.setLabelProvider(new ObjectsChangeSetLabelProvider());

		objectsTableViewer.getSwtTableViewer().addDoubleClickListener((IDoubleClickListener) event -> openComponentForSelectedObject());

		// Create new reference to underlying swt viewer
		checkboxTableViewer = (CheckboxTableViewer) objectsTableViewer.getSwtTableViewer();
		checkboxTableViewer.setAllChecked(false);
		checkboxTableViewer.addCheckStateListener(this);

		moveButton = mainComposite.addPushButton(ChangeSetMessages.get().ChangeSetEditor_Objects_MoveObjects,
				ChangeSetImageRegistry.getImage(ChangeSetImageRegistry.CHANGESET_MOVE_OBJECT), buttonData, container);

		moveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {

				moveObjectsToChangeSet();
			}
		});

		openButton = mainComposite.addPushButton(ChangeSetMessages.get().ChangeSetEditor_Objects_OpenObjectEditor,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_OPEN), buttonData, displayContainer);
		openButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				openComponentForSelectedObject();
			}
		});
	}

	private void setFiltersOnObjectTableViewer(final IEpTableViewer objectsTableViewer) {
		final ChangeSetObjectFilterFactory filterFactory = new ChangeSetObjectFilterFactory();
		objectsTableViewer.getSwtTableViewer().setFilters(filterFactory.getFilters());

	}

	private Paginator<ChangeSetMember> createPaginator(final SortingField defaultSortingField, final SortingDirection defaultSortingDirection) {
		final PaginatorFactory factory = getBean(ContextIdNames.PAGINATOR_FACTORY);
		final PaginationConfig config = getBean(ContextIdNames.PAGINATION_CONFIG);
		config.setObjectId(((ChangeSet) getModel()).getGuid());
		config.setPageSize(getPageSize());
		config.setSortingFields(new DirectedSortingField(defaultSortingField, defaultSortingDirection));

		return factory.createPaginator(ChangeSetMember.class, config);
	}

	private void updateTable(final Page<ChangeSetMember> newPage) {
		objectsTableViewer.setInput(newPage);
		objectsTableViewer.getSwtTable().setFocus();
		if (statePolicy != null) {
			applyStatePolicy(statePolicy);
		}
	}

	/**
	 * @param beanName the bean name
	 * @param <T> the bean type
	 * @return the bean instance
	 */
	<T> T getBean(final String beanName) {
		return ServiceLocator.getService(beanName);
	}

	private int getPageSize() {
		return PaginationInfo.getInstance().getPagination();
	}

	/*
	 * Get the selected/checked business object descriptors.
	 *	 */
	private Collection<BusinessObjectDescriptor> getSelectedBusinessObjectDescriptors(final ChangeSet originChangeSet,
			final Collection<ChangeSetMember> checkedElements) {

		final Collection<BusinessObjectDescriptor> checkedBods = new HashSet<>(checkedElements.size());

		for (final ChangeSetMember changeSetMember : checkedElements) {
			checkedBods.add(changeSetMember.getBusinessObjectDescriptor());
			checkedBods.addAll(addSupportingChangeSetElements(originChangeSet, changeSetMember));
		}

		return checkedBods;
	}

	/*
	 * Get the change lists available for moving objects to (if any).
	 *
	 * @return a collection of valid change sets
	 */
	private Collection<ChangeSet> getMoveChangeSetList() {
		final ChangeSetSearchCriteria changeSetSearchCriteria = new ChangeSetSearchCriteria();
		changeSetSearchCriteria.setChangeSetStateCode(ChangeSetStateCode.OPEN);
		changeSetSearchCriteria.setUserGuid(LoginManager.getCmUserGuid());

		final ChangeSetLoadTuner noMembersLoadTuner = getChangeSetLoadTuner();
		noMembersLoadTuner.setLoadingMemberObjects(false);

		return ((ChangeSetManagementService) getBean(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE)).findByCriteria(
				changeSetSearchCriteria, noMembersLoadTuner);
	}

	private void moveObjectsToChangeSet() {
		final Collection<ChangeSet> changeSets = getMoveChangeSetList();

		// Get all selected change set members and display a dialog window
		final Collection<ChangeSetMember> checkedElementsCollection = new HashSet<>(0);
		CollectionUtils.addAll(checkedElementsCollection, checkboxTableViewer.getCheckedElements());

		// Create dialog if some are selected
		if (!checkedElementsCollection.isEmpty()) {

			if (changeSets.size() <= 1) {

				MessageDialog.openInformation(objectsTableViewer.getSwtTable().getShell(),
						ChangeSetMessages.get().ChangeSetDialog_MoveObjects_WindowTitle, ChangeSetMessages.get().ChangeSetDialog_NotEnoughChangeSets);

			} else {

				// Filter out the current change set in view
				changeSets.remove(getModel());

				// Display dialog
				final ChangeSetDialog dialog = new ChangeSetDialog(objectsTableViewer.getSwtTable().getShell(), changeSets);

				if (dialog.open() != Window.OK) {
					return;
				}

				final ChangeSet originChangeSet = (ChangeSet) getModel();
				final ChangeSet targetChangeSet = dialog.getSelectedChangeSetForMove();

				final ChangeSetActionUtil changeSetActionUtil = new ChangeSetActionUtil();
				final IWorkbenchWindow workbenchWindow = getEditor().getSite().getWorkbenchWindow();

				// reload all the editors part of the previously active change set
				if (!changeSetActionUtil.saveAndReloadEditors(originChangeSet, workbenchWindow)) {
					// abort switch change set
					return;
				}

				if (!changeSetActionUtil.saveAndReloadEditors(targetChangeSet, workbenchWindow)) {
					// abort switch change set
					return;
				}

				// refresh the action delegate
				ChangeSetEventService.getInstance().fireChangeSetModificationEvent(new ItemChangeEvent<ChangeSet>(this, targetChangeSet));
				final ChangeSetManagementService changeSetManagementService = ServiceLocator.getService(
						ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);

				final ChangeSetLoadTuner allMembersLoadTuner = getChangeSetLoadTuner();
				allMembersLoadTuner.setLoadingMemberObjects(true);
				allMembersLoadTuner.setLoadingMemberObjectsMetadata(true);

				final Pair<ChangeSet, ChangeSet> changeSetPair = changeSetManagementService.updateAndMoveObjects(originChangeSet.getGuid(),
						targetChangeSet.getGuid(), getSelectedBusinessObjectDescriptors(originChangeSet, checkedElementsCollection), 
						allMembersLoadTuner);

				// apply policies to all components part of either the origin change set or the target change set
				changeSetActionUtil.applyStatePolicyToComponents(changeSetPair.getFirst(), changeSetPair.getSecond(),
						workbenchWindow.getActivePage());

				changeSetActionUtil.refreshEditorChangeSetInfoPages(workbenchWindow.getActivePage());

				ChangeSetEventService.getInstance().fireChangeSetModificationEvent(new ItemChangeEvent<ChangeSet>(this, changeSetPair.getFirst()));

				ChangeSetEventService.getInstance().fireChangeSetModificationEvent(new ItemChangeEvent<ChangeSet>(this, changeSetPair.getSecond()));

				// Refresh the active changeset
				changeSetActionUtil.refreshActiveChangeSet();

			}
		}
	}

	private Collection<BusinessObjectDescriptor> addSupportingChangeSetElements(final ChangeSet originChangeSet, 
			final ChangeSetMember changeSetMember) {
		final Collection<BusinessObjectDescriptor> businessObjectDescriptors = new HashSet<>();

		addSkuOptionValues(originChangeSet, changeSetMember, businessObjectDescriptors);

		return businessObjectDescriptors;
	}

	private void addSkuOptionValues(final ChangeSet originChangeSet, final ChangeSetMember changeSetMember, 
			final Collection<BusinessObjectDescriptor> businessObjectDescriptors) {
		//if you are moving sku options, make sure you move its related sku option values
		//this implementation relies on the SkuOptionValue metadata to because when the object is removed, we can't find it in the system
		if ("Sku Option".equals(changeSetMember.getBusinessObjectDescriptor().getObjectType())) { //$NON-NLS-1$
			
			final ChangeSetLoadTuner allMembersLoadTuner = getChangeSetLoadTuner();
			allMembersLoadTuner.setLoadingMemberObjects(true);
			allMembersLoadTuner.setLoadingMemberObjectsMetadata(true);

			final ChangeSetManagementService changeSetManagementService = 
				ServiceLocator.getService(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
			ChangeSet changeSet = changeSetManagementService.get(originChangeSet.getGuid(), allMembersLoadTuner);
			
			for (ChangeSetMember changeSetMembers : changeSet.getChangeSetMembers()) {
				if (changeSetMembers.getBusinessObjectDescriptor().getObjectType().equals("Sku Option Value")) { //$NON-NLS-1$
					String skuOptionValueGuid = changeSetMembers.getMetadata().get(SkuOptionValueMetadataResolver.SKU_OPTION_GUID_KEY);
					if (skuOptionValueGuid.equals(changeSetMember.getBusinessObjectDescriptor().getObjectIdentifier())) {
						businessObjectDescriptors.add(changeSetMembers.getBusinessObjectDescriptor());
					}
				}
			}
		}
	}

	private ChangeSetLoadTuner getChangeSetLoadTuner() {
		return getBean(ContextIdNames.CHANGESET_LOAD_TUNER);
	}

	@Override
	protected void populateControls() {
		paginationControl.populateControls();

		if (paginationControl.getCurrentPage().getTotalItems() <= 0) {
			paginationControl.setVisible(false);
			objectsTableViewer.getSwtTable().setVisible(false);
			moveButton.setVisible(false);
			openButton.setVisible(false);

			final IEpLayoutData labelData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, false, false, 1, 1);

			mainComposite.addLabel(ChangeSetMessages.get().ChangeSetDialog_NoObjectsAssigned, labelData, container);
		}

		// enable the open editor button on selected item from the table
		checkboxTableViewer.addSelectionChangedListener(event -> updateButtonsState());

		updateButtonsState();
	}

	/*
	 * Set move button state.
	 */
	private void updateButtonsState() {
		if (checkboxTableViewer.getTable().isDisposed()) {
			return;
		}
		if (ArrayUtils.isEmpty(checkboxTableViewer.getCheckedElements())) {
			moveButton.setEnabled(false);
		} else {
			moveButton.setEnabled(!isReadOnly());
		}
		boolean isMemberDeleted = false;
		final boolean isSelectionEmpty = checkboxTableViewer.getSelection().isEmpty();
		if (!isSelectionEmpty) {
			final ChangeSetMember selectedElement = (ChangeSetMember) ((IStructuredSelection) checkboxTableViewer.getSelection()).getFirstElement();
			isMemberDeleted = isMemberDeleted(selectedElement);
		}
		openButton.setEnabled(!isSelectionEmpty && !isMemberDeleted);
	}

	private boolean isMemberDeleted(final ChangeSetMember selectedElement) {
		boolean isMemberDeleted = false;
		if (selectedElement != null) {
			final String action = selectedElement.getMetadata().get("action"); //$NON-NLS-1$
			isMemberDeleted = ChangeSetMemberAction.DELETE == ChangeSetMemberAction.getChangeSetMemberAction(action);
		}
		return isMemberDeleted;
	}

	@Override
	public void refreshLayout() {
		if (!mainComposite.getSwtComposite().isDisposed()) {
			mainComposite.getSwtComposite().layout();
		}

		updateButtonsState();
	}

	/**
	 * Check state changed.
	 *
	 * @param event is the check state event
	 */
	@Override
	public void checkStateChanged(final CheckStateChangedEvent event) {
		if (isReadOnly()) {
			checkboxTableViewer.setChecked(event.getElement(), false);
			return;
		}

		updateButtonsState();
	}

	private boolean isReadOnly() {
		return statePolicy != null && statePolicy.determineState(container) != EpState.EDITABLE;
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		this.statePolicy = statePolicy;
		super.applyStatePolicy(statePolicy);
	}

	/**
	 * @return the editorHelper
	 */
	public ComponentHelper getComponentHelper() {
		return componentHelper;
	}

	/**
	 * @param componentHelper the editorHelper to set
	 */
	public void setComponentHelper(final ComponentHelper componentHelper) {
		this.componentHelper = componentHelper;
	}

	@Override
	public void setFocus() {
		checkboxTableViewer.getTable().setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		paginationControl.dispose();
	}

	private void openComponentForSelectedObject() {
		final IStructuredSelection selection = (IStructuredSelection) objectsTableViewer.getSwtTableViewer().getSelection();
		if (!selection.isEmpty()) {
			final ChangeSetMember member = (ChangeSetMember) selection.getFirstElement();
			if (!isMemberDeleted(member)) {

				final ChangeSetActionUtil changeSetActionUtil = new ChangeSetActionUtil();
				final IWorkbenchWindow workbenchWindow = getEditor().getSite().getWorkbenchWindow();

				final boolean saveOrReloadSuccessful = changeSetActionUtil.saveAndReloadEditors(changeSetHelper.getActiveChangeSet(),
						workbenchWindow);

				if (!saveOrReloadSuccessful) {
					return;
				}
				getComponentHelper().openComponent(((ChangeSet) getModel()).getGuid(), member.getBusinessObjectDescriptor());
				refreshEditors(workbenchWindow);
			}
		}
	}

	private void refreshEditors(final IWorkbenchWindow workbenchWindow) {
		final IWorkbenchPage activePage = workbenchWindow.getActivePage();
		final IEditorReference[] openEditorReferences = activePage.getEditorReferences();
		for (final IEditorReference editorReference : openEditorReferences) {
			final AbstractCmClientFormEditor cmClientFormEditor = (AbstractCmClientFormEditor) editorReference.getEditor(false);
			cmClientFormEditor.refreshEditorPages();
		}
	}

	/**
	 * Select column header.
	 *
	 * @param directedSortingField the field.
	 */
	public void columnHeaderSelected(final DirectedSortingField directedSortingField) {
		paginationControl.getPaginator().setSortingFields(directedSortingField);
		updateTable(paginationControl.getPaginator().first());
	}

	public DirectedSortingField getCurrentSortingField() {
		return paginationControl.getPaginator().getSortingFields()[0];
	}

}
