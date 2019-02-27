/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.settings.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.EntityEditorInput;
import com.elasticpath.cmclient.core.helpers.store.SettingModel;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.store.settings.SettingsImageRegistry;
import com.elasticpath.cmclient.store.settings.SettingsMessages;
import com.elasticpath.cmclient.store.settings.editors.SettingsEditor;
import com.elasticpath.cmclient.store.views.IStoreMarketingInnerTab;
import com.elasticpath.cmclient.store.views.SearchView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Provides methods for creating the settings search view.
 */
public class SettingsSearchTab implements IStoreMarketingInnerTab {

	private static final int COLUMN_STORE_NAME_WIDTH = 200;

	private static final Logger LOG = Logger.getLogger(SettingsSearchTab.class);
	private static final String STORE_TABLE = "Store"; //$NON-NLS-1$

	private final int tabIndex;

	private final StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);

	/**
	 * The constructor.
	 *
	 * @param tabFolder parent's searchView tab folder.
	 * @param tabIndex index of this tab into tabFolder.
	 * @param searchView parent SearchView.
	 */
	@SuppressWarnings("PMD.UnusedFormalParameter")
	public SettingsSearchTab(final IEpTabFolder tabFolder, final int tabIndex, final SearchView searchView) {
		final Image settingsImage = SettingsImageRegistry.getImage(SettingsImageRegistry.IMAGE_SETTING_TAB);
		final IEpLayoutComposite tabComposite = tabFolder.addTabItem(SettingsMessages.get().SearchView_SettingsTab,
				settingsImage, tabIndex, 1, false);
		this.tabIndex = tabIndex;

		createAndPopulateControls(tabComposite);
	}

	private void createAndPopulateControls(final IEpLayoutComposite composite) {
		// Create the filters container
		final IEpLayoutComposite storeSelectGroup = composite.addGroup(SettingsMessages.get().SearchView_SelectStoreGroup, 1, false, composite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		final IEpLayoutData layoutData = storeSelectGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		IEpTableViewer storeTableViewer = storeSelectGroup.addTableViewer(false, EpState.EDITABLE, layoutData, STORE_TABLE);
		storeTableViewer.getSwtTable().setHeaderVisible(false);
		storeTableViewer.getSwtTable().setLinesVisible(false);
		storeTableViewer.addTableColumn("Store Name", COLUMN_STORE_NAME_WIDTH, IEpTableColumn.TYPE_NONE); //$NON-NLS-1$
		storeTableViewer.setLabelProvider(new StoreSelectionViewLabelProvider());
		storeTableViewer.setContentProvider(new ArrayContentProvider());
		storeTableViewer.getSwtTableViewer().addDoubleClickListener(event -> {
			final Store store = (Store) ((IStructuredSelection) event.getSelection()).getFirstElement();

			openSoreSettingsEditor(store);
		});

		storeTableViewer.setInput(getInputStores());
	}

	private void openSoreSettingsEditor(final Store store) {
		final EntityEditorInput editorInput =
			new EntityEditorInput(store.getName(),
				NLS.bind(SettingsMessages.get().StoreEditorTooltip,
				new Object[]{store.getCode(), store.getName()}), store.getUidPk(), SettingModel.class);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, SettingsEditor.ID_EDITOR);
		} catch (PartInitException e) {
			LOG.error(e.getMessage());
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), SettingsMessages.get().NoLongerExistStoreMsgBoxTitle,

					NLS.bind(SettingsMessages.get().NoLongerExistStoreMsgBoxText,
					new String[]{store.getName(), store.getUrl()}));
		}
	}

	private Store[] getInputStores() {
		final List<Store> allStores = storeService.findAllStores();
		AuthorizationService.getInstance().removeUnathorizedStoresFrom(allStores);

		return allStores.toArray(new Store[allStores.size()]);
	}

	/**
	 * Provides labels for the QueryView TableViewer.
	 */
	private static class StoreSelectionViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int STORE_NAME_COLUMN = 0;

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final Store store = (Store) element;
			if (columnIndex == STORE_NAME_COLUMN) {
				return store.getName();
			}
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public void search() {
		//do nothing
	}

	@Override
	public void clear() {
		//do nothing
	}

	@Override
	public boolean isDisplaySearchButton() {
		return false;
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}

}
