/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.actions;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.actions.ActionDelegate;

import com.elasticpath.cmclient.changeset.ChangeSetImageRegistry;
import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.editors.ChangeSetEditor;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventListener;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.changeset.helpers.ChangeSetsEnabledCondition;
import com.elasticpath.cmclient.changeset.helpers.EditorEventObserver;
import com.elasticpath.cmclient.changeset.helpers.EditorEventObserver.IEditorListener;
import com.elasticpath.cmclient.changeset.helpers.EditorSupport;
import com.elasticpath.cmclient.changeset.helpers.SupportedEditorCondition;
import com.elasticpath.cmclient.changeset.helpers.impl.ChangeSetPermissionsHelperImpl;
import com.elasticpath.cmclient.changeset.support.SupportedComponent;
import com.elasticpath.cmclient.changeset.support.SupportedComponentsExtPoint;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.ui.util.EditorUtil;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * The action delegate for handling the button for adding an object to a change set.
 */
@SuppressWarnings({ "PMD.ExcessiveImports", "PMD.GodClass" })
public class AddToChangeSetActionDelegate extends ActionDelegate implements IWorkbenchWindowActionDelegate, IEditorListener, ChangeSetEventListener {

	private IAction action;
	private AbstractCmClientFormEditor activeEditor;
	private Object changeSetObjectSelection;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private boolean hasActiveEditor;

	private final ChangeSetActionUtil changeSetActionUtil = new ChangeSetActionUtil();
	private final PriceListDescriptorService priceListDescriptorService = ServiceLocator.getService(
			ContextIdNames.PRICE_LIST_DESCRIPTOR_SERVICE);
	private final ChangeSetService changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);

	/**
	 * The listener registered with the selection service.
	 */
	private final ISelectionListener selectionServiceListener = new ISelectionListener() {
		@Override
		public void selectionChanged(final IWorkbenchPart sourcepart, final ISelection selection) {
			if (selection == null || selection.isEmpty()) {
				if (!hasActiveEditor) {
					changeSetObjectSelection = null;
					action.setEnabled(false);
				}
				return;
			}

			if (sourcepart instanceof ChangeSetMemberSelectionProvider) {
				handleSelectionServiceChange((ChangeSetMemberSelectionProvider) sourcepart, selection);
			} else {
				changeSetObjectSelection = null;
				action.setEnabled(false);
			}
		}
	};

	private final ObjectRegistryListener listener = new ObjectRegistryListener() {
		public void objectUpdated(final String key, final Object oldValue, final Object newValue) {
			refresh();
		}
		public void objectRemoved(final String key, final Object object) {
			refresh();
		}
		public void objectAdded(final String key, final Object object) {
			refresh();
		}
	};

	/**
	 * Overridable method which should only handle selections related to Change Sets, such as selecting
	 * Categories or Catalogs in a tree or Products in a table.
	 * <p>
	 * For UI performance keep processing to a minimum.
	 *
	 * @param sourcepart The workbench part where the selection was made, such as a tree or table.
	 * @param selection The selection data, typically structured selections.
	 */
	protected void handleSelectionServiceChange(final ChangeSetMemberSelectionProvider sourcepart, final ISelection selection) {
		// check selection validity
		if (sourcepart == null || selection == null || selection.isEmpty()) {
			return;
		}

		// tables and trees provide structured selections
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			final Object[] selectedItems = structuredSelection.toArray();
			if (selectedItems != null && selectedItems.length > 0) {
				Object candidate = selectedItems[0];

				candidate = sourcepart.resolveObjectMember(candidate);

				// not all objects apply to Change Sets
				if (isApplicableChangeSetObjectSelection(candidate)) {
					setChangeSetObjectSelection(null, candidate);
				} else {
					//reset status of this button action
					setChangeSetObjectSelection(null, null);
				}
			}
		}
	}

	/**
	 * Checks if the selected object is applicable to Change Sets. Not all objects are added to Change Sets
	 * and not all objects apply to this "Add to Change Set" button when selected via the Selection Service.
	 * <table>
	 * <tr>
	 * 		<th>Candidate Object</th><th>Applicable to Change Sets</th><th>Note</th>
	 * </tr>
	 * <tr>
	 * 		<td>Null</td><td>False (not applicable)</td><td>Ignore null selections</td>
	 * </tr>
	 * <tr>
	 * 		<td>Category</td><td>False</td><td>Categories may not be added to change sets from the tree.</td>
	 * </tr>
	 * <tr>
	 * 		<td>ChangeSet</td><td>False</td><td>ChangeSet objects can obviously not be added to change sets.</td>
	 * </tr>
	 * <tr>
	 * 		<td>Catalog.isMaster()</td><td>False</td><td>Master Catalogs do not apply to change sets, only Virtual Catalogs.</td>
	 * </tr>
	 * <tr>
	 * 		<td>All other candidates</td><td>True</td><td>All other selections may apply to change sets.</td>
	 * </tr>
	 * </table>
	 *
	 * @param candidate The selected object which may or may not affect the state of the "Add to Change Set" button.
	 * @return True, if the object may affect the state of this button, false otherwise.
	 */
	protected boolean isApplicableChangeSetObjectSelection(final Object candidate) {
		if (candidate == null) {
			return false;
		}

		if (candidate instanceof Catalog) {
			return !((Catalog) candidate).isMaster();
		}

		if (candidate instanceof BaseAmountDTO) {
			final BigDecimal listValue = ((BaseAmountDTO) candidate).getListValue();
			if (listValue != null && listValue.compareTo(BigDecimal.ZERO) > 0) {
				return true;
			}

			final String priceListDescriptorGuid = ((BaseAmountDTO) candidate).getPriceListDescriptorGuid();
			final PriceListDescriptor priceListDescriptor = priceListDescriptorService.findByGuid(priceListDescriptorGuid);
			final ChangeSet changeSet = changeSetService.findChangeSet(priceListDescriptor);

			return changeSet != null && ObjectUtils.equals(changeSet, changeSetHelper.getActiveChangeSet());
		}

		if (candidate instanceof SkuOptionValue) {
			return false;
		}

		return !(candidate instanceof ChangeSet);
	}

	/**
	 * Set the Change Set object selected and change the Add to Change Set button tooltip to include the object "name".
	 *
	 * @param activeEditor The editor the changeSetObjectSelection is a model of, null if not from editor model.
	 * @param changeSetObjectSelection The newly selected Change Set object.
	 */
	protected void setChangeSetObjectSelection(final AbstractCmClientFormEditor activeEditor, final Object changeSetObjectSelection) {
		if (activeEditor instanceof ChangeSetEditor) {
			// Change Set objects cannot be associated with Change Sets
			// thus no need to process this event
			return;
		}

		// valid Change Set member selection made in the workbench
		this.activeEditor = activeEditor;
		this.changeSetObjectSelection = changeSetObjectSelection;

		setActionToolTip(changeSetObjectSelection);

		// refresh the button state based on the new change set object selected
		refresh();
	}

	/**
	 * Based on the Change Set member object the tooltip of the action should be updated
	 * to include the name of the object, such as category name or product title. This helps
	 * the user understand which object will be added to the Change Set should the button be clicked.
	 * <p>
	 * Default the tooltip to, for example, "Add selected object to Change Set", but attempt
	 * to derive a name or title, for example "Add Category 123 to Change Set" or "Add Canon 321 to Change Set".
	 *
	 * @param changeSetMemberObject The Change Set member object to derive a name or title from.
	 */
	protected void setActionToolTip(final Object changeSetMemberObject) {
		String name = generateActionToolTip(changeSetMemberObject);
		final String message =
			NLS.bind(ChangeSetMessages.get().AddToChangeSetAction_objectName,
			name);
		action.setToolTipText(message);
	}

	private String generateActionToolTip(final Object changeSetMemberObject) {
		String name = ChangeSetMessages.get().AddToChangeSetAction_objectName_default;

		if (changeSetMemberObject != null) {
			if (changeSetObjectSelection instanceof Product) {
				name = ((Product) changeSetObjectSelection).getDisplayName(CorePlugin.getDefault().getDefaultLocale());
			} else if (changeSetObjectSelection instanceof Category) {
				name = ((Category) changeSetObjectSelection).getDisplayName(CorePlugin.getDefault().getDefaultLocale());
			} else if (changeSetObjectSelection instanceof Catalog) {
				name = ((Catalog) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof Rule) {
				name = ((Rule) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof BaseAmountDTO) {
				name = getBaseAmountToolTipName();
			} else if (changeSetObjectSelection instanceof PriceListDescriptorDTO) {
				name = ((PriceListDescriptorDTO) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof PriceListAssignment) {
				name = ((PriceListAssignment) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof DynamicContent) {
				name = ((DynamicContent) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof ConditionalExpression) {
				name = ((ConditionalExpression) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof DynamicContentDelivery) {
				name = ((DynamicContentDelivery) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof Attribute) {
				name = ((Attribute) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof CategoryType) {
				name = ((CategoryType) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof SkuOption) {
				name = ((SkuOption) changeSetObjectSelection).getOptionKey();
			} else if (changeSetObjectSelection instanceof Brand) {
				name = ((Brand) changeSetObjectSelection).getDisplayName(CorePlugin.getDefault().getDefaultLocale(), true);
			} else if (changeSetObjectSelection instanceof ProductType) {
				name = ((ProductType) changeSetObjectSelection).getName();
			} else if (changeSetObjectSelection instanceof CartItemModifierGroup) {
				name = ((CartItemModifierGroup) changeSetObjectSelection).getCode();
			}
		}
		return name;
	}

	/**
	 * Given that the selected object is a {@link BaseAmountDTO}, this method generates a fairly unique "title"
	 * to use in the tool tip message.
	 *
	 * @return the title of the base amount
	 */
	protected String getBaseAmountToolTipName() {
		final BaseAmountDTO dto = (BaseAmountDTO) changeSetObjectSelection;
		final String quantity = dto.getQuantity().toPlainString();

		String listValue = StringUtils.EMPTY;
		if (dto.getListValue() != null) {
			listValue = dto.getListValue().toPlainString();
		}

		// sale value not mandatory, so might be null
		String saleValue;
		if (dto.getSaleValue() == null) {
			saleValue = ChangeSetMessages.get().AddToChangeSetAction_NA;
		} else {
			saleValue = dto.getSaleValue().toPlainString();
		}

		return
			NLS.bind(ChangeSetMessages.get().AddToChangeSetAction_BaseAmountToolTip,
			new Object[] {quantity, listValue, saleValue});
	}

	@Override
	public void init(final IAction action) {
		this.action = action;
		// disable initially
		this.action.setEnabled(false);
		ObjectRegistry.getInstance().addObjectListener(listener);
	}

	@Override
	public void dispose() {
		ObjectRegistry.getInstance().removeObjectListener(listener);
		super.dispose();
	}

	@Override
	public void run(final IAction action) {
		final ChangeSet activeChangeSet = changeSetHelper.getActiveChangeSet();
		if ((activeEditor == null && changeSetObjectSelection == null) || changeSetObjectSelection == null || activeChangeSet == null) {
			// there is no active editor, change set or object selection so skip the execution
			return;
		}

		// this check is required for the key shortcut in which case the key is always active
		// and this action could be executed
		if (!isActionEnabled()) {
			return;
		}

		changeSetHelper.addObjectToChangeSet(changeSetObjectSelection, ChangeSetMemberAction.EDIT);

		// refresh the action after the object was added to the change set
		refresh();

		if (activeEditor == null) {
			// There are cases where nothing is selected.
			if (changeSetObjectSelection != null) {
				// if there is no active editor make sure that if the editor is open we do a refresh
				// this could happen if an object is being added from within a list viewer
				final String objectGuid = changeSetHelper.resolveObjectGuid(changeSetObjectSelection);
				final IEditorReference[] editors = EditorUtil.findOpenEditorsByObjectGuid(objectGuid);
				for (final IEditorReference iEditorReference : editors) {
					final IEditorPart editor = iEditorReference.getEditor(false);
					updateEditor((AbstractCmClientFormEditor) editor);
				}
			}
		} else {
			// decorate and refresh the editor, if the object was added to Change Set from an editor
			updateEditor(activeEditor);
		}

		ChangeSetEventService.getInstance().fireChangeSetModificationEvent(new ItemChangeEvent<ChangeSet>(this, activeChangeSet));
	}

	/**
	 * Updates the editor by updating the editor icon, refreshing the change set info page
	 * and applying the editor's policy.
	 */
	private void updateEditor(final AbstractCmClientFormEditor editor) {
		EditorSupport.decorateEditorImageIfLocked(editor, changeSetService);

		changeSetActionUtil.refreshChangeSetInfoPage(editor);
		applyPolicy(editor);
	}

	/**
	 * Instructs the {@link AbstractPolicyAwareFormEditor} to re-apply it's
	 * state policies after an object was added to a Change Set.
	 *
	 * @param activeEditor the editor to use
	 */
	protected void applyPolicy(final AbstractCmClientFormEditor activeEditor) {
		final AbstractPolicyAwareFormEditor policyAwareEditor = (AbstractPolicyAwareFormEditor) activeEditor;
		policyAwareEditor.applyStatePolicy();
	}

	/**
	 * Handles editor activation.
	 *
	 * @param editor the editor
	 */
	protected void handleEditorActivation(final AbstractCmClientFormEditor editor) {
		EditorSupport.decorateEditorImageIfLocked(editor, changeSetService);

		setChangeSetObjectSelection(editor, editor.getDependentObject());

		hasActiveEditor = true;
		refresh();
	}

	/**
	 * Refreshes the state of the action.
	 */
	protected void refresh() {
		if (action != null && changeSetObjectSelection != null && changeSetHelper.isChangeSetsEnabled()) {
			final ChangeSetObjectStatus status = changeSetHelper.getChangeSetObjectStatus(changeSetObjectSelection);

			if (isObjectInActiveChangeset(status)) {
				action.setEnabled(true);
				action.setImageDescriptor(ChangeSetImageRegistry.CHANGESET_OBJECT_ADDED_LARGE);
				action.setToolTipText(ChangeSetMessages.get().ObjectLockedInCurrentChangeset);
			} else if (isObjectInAnyChangeset(status)) {
				action.setEnabled(true);
				action.setImageDescriptor(ChangeSetImageRegistry.CHANGESET_OBJECT_ADDED_DISABLED_LARGE);
				action.setToolTipText(
					NLS.bind(ChangeSetMessages.get().ObjectLockedInChangeSet,
					changeSetHelper.getChangeSet(changeSetObjectSelection).getName()));
			} else if (isObjectAvailable(status)) {
				action.setEnabled(true);
				action.setImageDescriptor(ChangeSetImageRegistry.CHANGESET_ADD_OBJECT_LARGE);
				action.setToolTipText(
					NLS.bind(ChangeSetMessages.get().AddObjectToChangeset,
					generateActionToolTip(changeSetObjectSelection)));
			} else {
				action.setEnabled(false);
				action.setDisabledImageDescriptor(ChangeSetImageRegistry.CHANGESET_ADD_OBJECT_LARGE);
				action.setToolTipText(StringUtils.EMPTY);
			}
		}
	}

	/**
	 * Checks whether the action is enabled.
	 *
	 * @return true if the action is enabled
	 */
	protected boolean isActionEnabled() {
		final ChangeSetObjectStatus status = changeSetHelper.getChangeSetObjectStatus(changeSetObjectSelection);
		return ChangeSetPermissionsHelperImpl.getDefault().isChangeSetFeatureEnabled() && isObjectAvailable(status);
	}

	/**
	 * Checks whether an object is available for addition.
	 *
	 * @param status the change set status
	 * @return true if the object can be added to a change set
	 */
	protected boolean isObjectAvailable(final ChangeSetObjectStatus status) {
		if (changeSetHelper.isActiveChangeSet()) {
			return status.isAvailable(changeSetHelper.getActiveChangeSet().getGuid())
					&& !status.isMember(changeSetHelper.getActiveChangeSet().getGuid());
		}

		return false;
	}

	/**
	 * Checks whether the object is a active changeset.
	 *
	 * @param status the change set status
	 * @return if the object is in the active changeset
	 */
	protected boolean isObjectInActiveChangeset(final ChangeSetObjectStatus status) {
		if (changeSetHelper.isActiveChangeSet()) {
			return status.isMember(changeSetHelper.getActiveChangeSet().getGuid());
		}
		return false;
	}

	/**
	 * Checks whether the object is in any changeset.
	 *
	 * @param status the change set status
	 * @return if the object is in any changeset
	 */
	protected boolean isObjectInAnyChangeset(final ChangeSetObjectStatus status) {
		return status.isLocked();
	}

	/**
	 * Handles an editor inactivation event.
	 *
	 * @param editor the editor
	 */
	protected void handleEditorDeactivation(final AbstractCmClientFormEditor editor) {
		hasActiveEditor = false;

		// editor is being closed
		action.setEnabled(false);

		// reset change set object
		setChangeSetObjectSelection(null, null);
	}


	/**
	 * Initializes the action delegate.
	 *
	 * @param window the workbench window
	 */
	@Override
	public void init(final IWorkbenchWindow window) {
		final Collection<SupportedComponent> supportedEditors = new SupportedComponentsExtPoint().getSupportedComponents();
		window.getPartService().addPartListener(new EditorEventObserver(this,
				new SupportedEditorCondition(supportedEditors),
				new ChangeSetsEnabledCondition()));

		if (changeSetHelper.isChangeSetsEnabled()) {
			// we use the SelectionService for Change Sets only, so no need to listen if Change Set are disabled
			window.getSelectionService().addSelectionListener(selectionServiceListener);
		}

		window.addPerspectiveListener(new PerspectiveAdapter() {
			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspectiveDescriptor) {
				super.perspectiveActivated(page, perspectiveDescriptor);
				//disable the action when the perspective is changed.
				changeSetObjectSelection = null;
				action.setEnabled(false);
			}
		});

		// if the action gets created through a key binding
		// then it is possible that there already is an active editor
		// the active editor is set by the IPartListener.pageActivated()
		// and this delegate would not be created until the first hit of the shortcut
		if (window.getActivePage() != null) {
			activeEditor = (AbstractCmClientFormEditor) window.getActivePage().getActiveEditor();
		}
	}

	@Override
	public void editorActivated(final AbstractCmClientFormEditor editor) {
		handleEditorActivation(editor);
	}

	@Override
	public void editorClosed(final AbstractCmClientFormEditor editor) {
		handleEditorDeactivation(editor);
	}

	@Override
	public void editorDeactivated(final AbstractCmClientFormEditor editor) {
		handleEditorDeactivation(editor);
	}

	@Override
	public void editorOpened(final AbstractCmClientFormEditor editor) {
		handleEditorActivation(editor);
	}

	/**
	 * Called whenever the active Change Set is changed. The state of this action
	 * depends on the active Change Set, so we refresh.
	 *
	 * @param event the event
	 */
	@Override
	public void changeSetModified(final ItemChangeEvent<ChangeSet> event) {
		refresh();
	}

}
