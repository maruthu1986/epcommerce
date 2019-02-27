/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.admin.shipping.views;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.shipping.AdminShippingImageRegistry;
import com.elasticpath.cmclient.admin.shipping.AdminShippingMessages;
import com.elasticpath.cmclient.admin.shipping.AdminShippingPlugin;
import com.elasticpath.cmclient.admin.shipping.actions.CreateShippingRegionAction;
import com.elasticpath.cmclient.admin.shipping.actions.DeleteShippingRegionAction;
import com.elasticpath.cmclient.admin.shipping.actions.EditShippingRegionAction;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.service.shipping.ShippingRegionService;

/**
 * Viewer for Shipping Regions.
 */
public class ShippingRegionListView extends AbstractListView {

	private static final int NAME_COLUMN_IDX = 0;

	/** The View's ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.shipping.regions.views.RegionsList"; //$NON-NLS-1$

	private static final String SHIPPING_REGION_TABLE = "Shipping Region";  //$NON-NLS-1$

	private final ShippingRegionService shippingRegionService;

	private CreateShippingRegionAction createRegion;

	private EditShippingRegionAction editRegion;

	private DeleteShippingRegionAction deleteRegion;

	/** Column widths. */
	private static final int[] COLUMN_WIDTHS = new int[]{200};

	/**
	 * Default constructor.
	 */
	public ShippingRegionListView() {
		super(false, SHIPPING_REGION_TABLE);

		shippingRegionService = ServiceLocator.getService(EpShippingContextIdNames.SHIPPING_REGION_SERVICE);
	}

	@Override
	protected Object[] getViewInput() {
		return shippingRegionService.list().toArray();
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new ViewLabelProvider();
	}

	/**
	 * Enables or disables view actions depending on if some item is selected into the view.
	 * 
	 * @param isSelected true if something is selected; false - otherwise.
	 */
	
	// ---- DOCshippingRegionSelected
	void shippingRegionSelected(final boolean isSelected) {
		editRegion.setEnabled(isSelected);
		deleteRegion.setEnabled(isSelected);
	}
	// ---- DOCshippingRegionSelected

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		String[] columnNames = new String[]{AdminShippingMessages.get().ShippingRegionName};

		for (int i = 0; i < columnNames.length; i++) {
			table.addTableColumn(columnNames[i], COLUMN_WIDTHS[i]);
		}

		getViewer().addSelectionChangedListener(event -> {
			ShippingRegion shippingRegion = getSelectedShippingRegion();
			shippingRegionSelected(null != shippingRegion);
		});
	}

	@Override
	protected String getPluginId() {
		return AdminShippingPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {

		createRegion = new CreateShippingRegionAction(this, AdminShippingMessages.get().CreateShippingRegion,
				AdminShippingImageRegistry.IMAGE_SHIPPING_CREATE);
		createRegion.setToolTipText(AdminShippingMessages.get().CreateShippingRegion);
		ActionContributionItem createRegionContributionItem = new ActionContributionItem(createRegion);
		createRegionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		editRegion = new EditShippingRegionAction(this, AdminShippingMessages.get().EditShippingRegion, AdminShippingImageRegistry.
				IMAGE_SHIPPING_EDIT);
		editRegion.setToolTipText(AdminShippingMessages.get().EditShippingRegion);
		editRegion.setEnabled(false);
		ActionContributionItem editRegionContributionItem = new ActionContributionItem(editRegion);
		editRegionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		addDoubleClickAction(editRegion);

		deleteRegion = new DeleteShippingRegionAction(this, AdminShippingMessages.get().DeleteShippingRegion,
				AdminShippingImageRegistry.IMAGE_SHIPPING_DELETE);
		deleteRegion.setToolTipText(AdminShippingMessages.get().DeleteShippingRegion);
		deleteRegion.setEnabled(false);
		ActionContributionItem deleteRegionContributionItem = new ActionContributionItem(deleteRegion);
		deleteRegionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		Separator regionActionGroup = new Separator("regionActionGroup"); //$NON-NLS-1$
		getToolbarManager().add(regionActionGroup);
		getToolbarManager().appendToGroup(regionActionGroup.getGroupName(), editRegionContributionItem);
		getToolbarManager().appendToGroup(regionActionGroup.getGroupName(), createRegionContributionItem);
		getToolbarManager().appendToGroup(regionActionGroup.getGroupName(), deleteRegionContributionItem);
	}

	/**
	 * Returns selected shipping region.
	 * 
	 * @return the Selected ShippingRegion
	 */
	public ShippingRegion getSelectedShippingRegion() {
		IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		ShippingRegion shippingRegion = null;
		if (!selection.isEmpty()) {

			shippingRegion = (ShippingRegion) selection.getFirstElement();
		}
		return shippingRegion;

	}

	/**
	 * Label provider for ShippingRegion.
	 */
	protected static class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public String getColumnText(final Object obj, final int index) {
			if (index == NAME_COLUMN_IDX) {
				return ((ShippingRegion) obj).getName();
			}
			return null;
		}

		@Override
		public Image getColumnImage(final Object obj, final int index) {
			return null;
		}
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}