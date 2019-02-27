/**
 * Copyright (c) Elastic Path Software Inc., 2007
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
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.attribute.AttributeService;

/**
 * The class to display the dual assignment list for product attribute
 * assignment for adding/Editing product type.
 */
public abstract class AbstractAttributesDualList extends AbstractPolicyAwareDualListBox<List<Attribute>> {
	private ViewerFilter availableAttributeFilter;
	private List<Attribute> availableAttributesList;
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
	public AbstractAttributesDualList(final IPolicyTargetLayoutComposite parentComposite,
									  final PolicyActionContainer container,
									  final List<Attribute> model,
									  final String availableTitle,
									  final String selectedTitle,
									  final IEpLayoutData data,
									  final Catalog catalog) {
		super(parentComposite, data, container, model, availableTitle, selectedTitle,
				ALL_BUTTONS | UP_DOWN_BUTTONS | MULTI_SELECTION);
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
				CatalogMessages.get().AttributeDualList_UnAssignAll_ConfirmTitle,
				CatalogMessages.get().AttributeDualList_UnAssignAll_ConfirmMsg);
		if (!answerYes) {
			return false;
		}
		return super.handleRemoveAllEvent();
	}

	@Override
	protected boolean handleRemoveOneEvent() {
		final boolean answerYes = MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
				CatalogMessages.get().AttributeDualList_UnAssignSelected_ConfirmTitle,
				CatalogMessages.get().AttributeDualList_UnAssignSelected_ConfirmMsg + getSelectedAttributeNames());
		if (!answerYes) {
			return false;
		}
		return super.handleRemoveOneEvent();
	}

	private String getSelectedAttributeNames() {
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
		final List<Attribute> attributesList = getModel();
		attributesList.addAll(selection.toList());
		return true;
	}

	@Override
	public Collection<Attribute> getAssigned() {
		return getModel();
	}

	@Override
	public Collection<Attribute> getAvailable() {
		if (availableAttributesList == null) {
			final AttributeService attributeService = ServiceLocator.getService(
					ContextIdNames.ATTRIBUTE_SERVICE);
			availableAttributesList = getAvailableAttributesList(attributeService);
		}
		return availableAttributesList;
	}

	/**
	 * @param attributeService the AttributeService object.
	 * @return the available attribute list in the database.
	 */
	public abstract List<Attribute> getAvailableAttributesList(
			AttributeService attributeService);

	@Override
	public ViewerFilter getAvailableFilter() {
		if (availableAttributeFilter == null) {
			availableAttributeFilter = new AvailableAttributeFilter();
		}
		return availableAttributeFilter;
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new AttributeLabelProvider();
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		final List<Attribute> attributesList = getModel();
		attributesList.removeAll(selection.toList());
		return true;
	}

	/**
	 * The LabelProvider for the product type attribute dual list box.
	 */
	public static class AttributeLabelProvider extends LabelProvider implements
			ILabelProvider {

		@Override
		public String getText(final Object element) {
			final Attribute attr = (Attribute) element;
			return attr.getName();
		}
	}

	/**
	 * The filter class defining the logic to filter the assigned list.
	 */
	public class AvailableAttributeFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement,
							  final Object element) {
			if (getAssigned() == null) {
				return false;
			}
			return !getAssigned().contains(element);
		}
	}
}
