/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin.customers.views;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.customers.AdminCustomersImageRegistry;
import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.admin.customers.AdminCustomersPlugin;
import com.elasticpath.cmclient.admin.customers.actions.CreateAttributeAction;
import com.elasticpath.cmclient.admin.customers.actions.DeleteAttributeAction;
import com.elasticpath.cmclient.admin.customers.actions.EditAttributeAction;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.service.attribute.AttributeService;

/**
 * View to show and allow the manipulation of the available customer attributes in CM.
 */
public class AttributeListView extends AbstractListView {
	/** The view ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.customers.views.AttributeListView"; //$NON-NLS-1$	

	private static final String ATTRIBUTE_LIST_TABLE = "Attribute List"; //$NON-NLS-1$

	// Column indices
	private static final int INDEX_ATTRIBUTE_IMAGE = 0;

	private static final int INDEX_ATTRIBUTE_KEY = 1;

	private static final int INDEX_ATTRIBUTE_NAME = 2;

	private static final int INDEX_ATTRIBUTE_TYPE = 3;

	private static final int INDEX_SYSTEM_ATTRIBUTE = 4;
	
	private static final int INDEX_REQUIRED_ATTRIBUTE = 5;

	private Action createAttributeAction;

	private Action editAttributeAction;

	private Action deleteAttributeAction;

	private final AttributeService attributeService;

	/**
	 * The constructor.
	 */
	public AttributeListView() {
		super(false, ATTRIBUTE_LIST_TABLE);
		attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);
	}

	@Override
	protected String getPluginId() {
		return AdminCustomersPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		
		final Separator attributeActionGroup = new Separator("customerActionGroup"); //$NON-NLS-1$
		getToolbarManager().add(attributeActionGroup);

		createAttributeAction = new CreateAttributeAction(this, AdminCustomersMessages.get().CreateAttribute,
				AdminCustomersImageRegistry.IMAGE_ATTRIBUTE_CREATE);
		createAttributeAction.setToolTipText(AdminCustomersMessages.get().CreateAttribute);
		editAttributeAction = new EditAttributeAction(this, AdminCustomersMessages.
				get().EditAttribute, AdminCustomersImageRegistry.IMAGE_ATTRIBUTE_EDIT);
		editAttributeAction.setToolTipText(AdminCustomersMessages.get().EditAttribute);
		editAttributeAction.setEnabled(false);
		addDoubleClickAction(editAttributeAction);
		deleteAttributeAction = new DeleteAttributeAction(this, AdminCustomersMessages.get().DeleteAttribute,
				AdminCustomersImageRegistry.IMAGE_ATTRIBUTE_DELETE);
		deleteAttributeAction.setToolTipText(AdminCustomersMessages.get().DeleteAttribute);
		deleteAttributeAction.setEnabled(false);

		final ActionContributionItem createAttributeActionContributionItem = new ActionContributionItem(createAttributeAction);
		final ActionContributionItem editAttributeActionContributionItem = new ActionContributionItem(editAttributeAction);
		final ActionContributionItem removeAttributeActionContributionItem = new ActionContributionItem(deleteAttributeAction);

		createAttributeActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editAttributeActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		removeAttributeActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(attributeActionGroup.getGroupName(), editAttributeActionContributionItem);
		getToolbarManager().appendToGroup(attributeActionGroup.getGroupName(), createAttributeActionContributionItem);
		getToolbarManager().appendToGroup(attributeActionGroup.getGroupName(), removeAttributeActionContributionItem);
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {

		final String[] columnNames = new String[] {
				"", //$NON-NLS-1$
				AdminCustomersMessages.get().AttributeKey, AdminCustomersMessages.get().AttributeName, AdminCustomersMessages.get().AttributeType,
				AdminCustomersMessages.get().SystemAttribute, AdminCustomersMessages.get().Required };

		final int[] columnWidths = new int[] { 1, 180, 200, 120, 120, 100 };

		for (int i = 0; i < columnNames.length; i++) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}
		// ---- DOCselectionChanged
// ---- DOCselectionChanged
		table.getSwtTableViewer().addSelectionChangedListener(event -> {
			boolean editable = (getSelectedAttribute() != null) && !getSelectedAttribute().isSystem();
			editAttributeAction.setEnabled(editable);
			deleteAttributeAction.setEnabled(editable);
		});
	}

	/**
	 * Return a copy of the table's selected attribute item.
	 * 
	 * @return the copy of the selected attribute
	 */
	public Attribute getSelectedAttribute() {
		final IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		Attribute attribute = null;
		if (!selection.isEmpty()) {

			attribute = (Attribute) selection.getFirstElement();
		}
		return attribute;
	}

	@Override
	protected Object[] getViewInput() {
		final List< ? > attributes = attributeService.getCustomerProfileAttributes();
		Attribute[] attributeArray = attributes.toArray(new Attribute[attributes.size()]);
		Arrays.sort(attributeArray, Comparator.comparing(Attribute::getKey));
		return attributeArray;
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new AttributeListViewLabelProvider();
	}

	/**
	 * Label provider for the view.
	 */
	protected class AttributeListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Get the image to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
//			if (columnIndex == INDEX_ATTRIBUTE_IMAGE) {
//				return CoreImageRegistry.getImage(AdminCustomersImageRegistry.IMAGE_ATTRIBUTE);
//			}
			return null;
		}

		/**
		 * Get the text to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final Attribute attribute = (Attribute) element;
			switch (columnIndex) {
			case AttributeListView.INDEX_ATTRIBUTE_IMAGE:
				return ""; //$NON-NLS-1$;
			case AttributeListView.INDEX_ATTRIBUTE_KEY:
				return attribute.getKey();
			case AttributeListView.INDEX_ATTRIBUTE_NAME:
				return attribute.getName();
			case AttributeListView.INDEX_ATTRIBUTE_TYPE:
				return CoreMessages.get().getMessage(attribute.getAttributeType().getNameMessageKey());
			case AttributeListView.INDEX_SYSTEM_ATTRIBUTE:
				return CoreMessages.get().getMessage(CoreMessages.YES_NO_FOR_BOOLEAN_MSG_PREFIX + attribute.isSystem());
			case AttributeListView.INDEX_REQUIRED_ATTRIBUTE:
				return CoreMessages.get().getMessage(CoreMessages.YES_NO_FOR_BOOLEAN_MSG_PREFIX + attribute.isRequired());
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
