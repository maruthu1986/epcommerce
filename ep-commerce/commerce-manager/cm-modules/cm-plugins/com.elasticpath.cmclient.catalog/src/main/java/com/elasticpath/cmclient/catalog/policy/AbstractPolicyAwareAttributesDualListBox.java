/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.policy;

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
import com.elasticpath.service.attribute.AttributeService;

/**
 * Presents a dual list box control for assigning attributes for specified parent model.
 */
public abstract class AbstractPolicyAwareAttributesDualListBox extends AbstractPolicyAwareDualListBox<List<Attribute>> {

	private List<Attribute> availableAttributesList;

	private ViewerFilter availableAttributeFilter;

	private final AttributeService attributeService;

	/**
	 * Constructs a new policy aware dual list box.
	 *
	 * @param parentComposite the parent composite
	 * @param data the layout data
	 * @param container the container to use
	 * @param model the model
	 * @param availableTitle the available box title
	 * @param assignedTitle the assigned box title
	 */
	public AbstractPolicyAwareAttributesDualListBox(final IPolicyTargetLayoutComposite parentComposite,
			final IEpLayoutData data,
			final PolicyActionContainer container,
			final List<Attribute> model,
			final String availableTitle,
			final String assignedTitle) {

		super(parentComposite, data, container, model, availableTitle, assignedTitle, ALL_BUTTONS | UP_DOWN_BUTTONS | MULTI_SELECTION);

		attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);
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
		final StringBuilder selectionNames = new StringBuilder();
		final Table table = getAssignedTableViewer().getTable();
		for (int i = 0; i < table.getSelectionCount(); i++) {
			selectionNames.append(table.getSelection()[i].getText());
			selectionNames.append(CatalogMessages.NEWLINE);
		}
		return selectionNames.toString();
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new AttributeLabelProvider();
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
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		final List<Attribute> attributesList = getModel();
		attributesList.removeAll(selection.toList());
		return true;
	}

	@Override
	public Collection<Attribute> getAvailable() {
		if (availableAttributesList == null) {
			availableAttributesList = getAvailableAttributesList(attributeService);
		}
		return availableAttributesList;
	}

	/**
	 * Get the available attributes based on the given {@link AttributeService}.
	 *
	 * @param attributeService the attribute service
	 * @return the available attribute list
	 */
	public abstract List<Attribute> getAvailableAttributesList(final AttributeService attributeService);

	@Override
	public Collection<Attribute> getAssigned() {
		return getModel();
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		if (availableAttributeFilter == null) {
			availableAttributeFilter = new AvailableAttributeFilter();
		}
		return availableAttributeFilter;
	}

	/**
	 * The LabelProvider for the product type attribute dual list box.
	 */
	private static class AttributeLabelProvider extends LabelProvider implements ILabelProvider {

		@Override
		public String getText(final Object element) {
			final Attribute attr = (Attribute) element;
			return attr.getName();
		}
	}

	/**
	 * The filter class defining the logic to filter the assigned list.
	 */
	private class AvailableAttributeFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			if (getAssigned() == null) {
				return false;
			}
			return !getAssigned().contains(element);
		}
	}

}
