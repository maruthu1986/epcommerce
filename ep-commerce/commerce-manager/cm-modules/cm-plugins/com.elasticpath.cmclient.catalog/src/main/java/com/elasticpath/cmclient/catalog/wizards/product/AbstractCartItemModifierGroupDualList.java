/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.wizards.product;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDualListBox;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * The class to display the dual assignment list for product cart item modifier group
 * assignment for adding/Editing cart item modifier group.
 */
public abstract class AbstractCartItemModifierGroupDualList extends AbstractPolicyAwareDualListBox<List<CartItemModifierGroup>> {

	private ViewerFilter availableCartItemModifierGroupFilter;

	private List<CartItemModifierGroup> availableCartItemModifierGroupList;

	private final Catalog catalog;

	/**
	 * @param parentComposite the parent composite of the dual list box.
	 * @param model           the model object.
	 * @param data            the layout data to create the dual list box
	 * @param container       the PolicyActionContainer object passed in.
	 * @param availableTitle  the title string text of the available list.
	 * @param selectedTitle   the title string of the selected list.
	 * @param catalog         the catalog
	 */
	public AbstractCartItemModifierGroupDualList(final IPolicyTargetLayoutComposite parentComposite,
			final PolicyActionContainer container,
			final List<CartItemModifierGroup> model,
			final String availableTitle,
			final String selectedTitle,
			final IEpLayoutData data,
			final Catalog catalog) {
		super(parentComposite, data, container, model, availableTitle, selectedTitle,
				ALL_BUTTONS | MULTI_SELECTION | EMPTY_UP_DOWN_BUTTON_PANEL);
		this.catalog = catalog;
	}

	/**
	 * Gets the assigned catalog.
	 *
	 * @return the catalog
	 */
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	protected boolean handleRemoveAllEvent() {
		final boolean answerYes = MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
				CatalogMessages.get().CartItemModifierGroupDualList_UnAssignAll_ConfirmTitle,
				CatalogMessages.get().CartItemModifierGroupDualList_UnAssignAll_ConfirmMsg);
		if (answerYes) {
			return super.handleRemoveAllEvent();
		}
		return false;
	}

	@Override
	protected boolean handleRemoveOneEvent() {
		final boolean answerYes = MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
				CatalogMessages.get().CartItemModifierGroupDualList_UnAssignSelected_ConfirmTitle,
				CatalogMessages.get().CartItemModifierGroupDualList_UnAssignSelected_ConfirmMsg + getSelectedCartItemModifierGroupNames());
		if (answerYes) {
			return super.handleRemoveOneEvent();
		}
		return false;
	}

	private String getSelectedCartItemModifierGroupNames() {
		StringBuilder selectionNames = new StringBuilder();
		Table table = this.getAssignedTableViewer().getTable();
		for (int i = 0; i < table.getSelectionCount(); i++) {
			selectionNames.append(table.getSelection()[i].getText());
			selectionNames.append(CatalogMessages.NEWLINE);
		}
		return selectionNames.toString();
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		final List<CartItemModifierGroup> cartItemModifierGroupList = getModel();
		cartItemModifierGroupList.addAll(selection.toList());
		return true;
	}

	@Override
	public Collection<CartItemModifierGroup> getAssigned() {
		return getModel();
	}

	@Override
	public Collection<CartItemModifierGroup> getAvailable() {
		if (availableCartItemModifierGroupList == null) {
			final CartItemModifierService cartItemModifierService =
					(ServiceLocator.getService(ContextIdNames.CART_ITEM_MODIFIER_SERVICE));
			availableCartItemModifierGroupList = getAvailableCartItemModifierGroupsList(cartItemModifierService);
		}
		return availableCartItemModifierGroupList;
	}

	/**
	 * @param cartItemModifierService the CartItemModifierService object.
	 * @return the available Cart Item Modifier list in the database.
	 */
	public abstract List<CartItemModifierGroup> getAvailableCartItemModifierGroupsList(
			CartItemModifierService cartItemModifierService);

	@Override
	public ViewerFilter getAvailableFilter() {
		if (availableCartItemModifierGroupFilter == null) {
			availableCartItemModifierGroupFilter = new AvailableCartItemModifierGroupFilter();
		}
		return availableCartItemModifierGroupFilter;
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new CartItemModifierGroupLabelProvider();
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		final List<CartItemModifierGroup> cartItemModifierGroupList = getModel();
		cartItemModifierGroupList.removeAll(selection.toList());
		return true;
	}

	/**
	 * The LabelProvider for the product type cart item modifier group dual list box.
	 */
	public static class CartItemModifierGroupLabelProvider extends LabelProvider implements
			ILabelProvider {

		@Override
		public String getText(final Object element) {
			final CartItemModifierGroup cartItemModifierGroup = (CartItemModifierGroup) element;
			return cartItemModifierGroup.getCode();
		}
	}

	/**
	 * The filter class defining the logic to filter the assigned list.
	 */
	public class AvailableCartItemModifierGroupFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			if (getAssigned() == null) {
				return false;
			}
			return !getAssigned().contains(element);
		}
	}
}
