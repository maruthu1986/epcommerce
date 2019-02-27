/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingPermissions;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.DynamicContentSearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.impl.AssignedStatus;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.impl.DynamicContentSearchTabModelImpl;
import com.elasticpath.cmclient.store.views.IStoreMarketingInnerTab;
import com.elasticpath.cmclient.store.views.SearchView;

/**
 * Provides methods for creating the dynamic content search view.
 */
public final class DynamicContentSearchTab  implements IStoreMarketingInnerTab {

	/**
	 * CampaignSearchView ID specified in the plugin.xml file. It is the same as the class name.
	 */
	public static final String ID_CAMPAIGN_SEARCH_VIEW = DynamicContentSearchTab.class.getName();

	private static final int INDEX_ALL_DC = 0;
	private static final int INDEX_ASSIGNED_DC = 1;
	private static final int INDEX_NONASSIGNED_DC = 2;

	private DataBindingContext dataBindingCtx;	

	@SuppressWarnings("unused")
	private final SearchView searchView; //NOPMD

	private Text dynamicContentNameText;

	private CCombo dynamicContentStatusCombo;

	private DynamicContentSearchTabModel model;

	private final boolean dynamicContentManagmentEnabled;

	private final int tabIndex;

	/**
	 * Constructor.
	 * 
	 * @param tabFolder parent's searchView tab folder.
	 * @param tabIndex index of this tab into tabFolder.
	 * @param searchView parent SearchView.
	 */
	public DynamicContentSearchTab(final IEpTabFolder tabFolder, final int tabIndex, final SearchView searchView) {
		final Image tabImage = TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_TAB.createImage();
		final IEpLayoutComposite tabComposite = tabFolder.addTabItem(
				TargetedSellingMessages.get().DynamicContentTabTitle,
				tabImage,
				tabIndex, 
				1, 
				false);
		this.searchView = searchView;
		this.tabIndex = tabIndex;

		dynamicContentManagmentEnabled = AuthorizationService.getInstance().isAuthorizedWithPermission(
				TargetedSellingPermissions.DYNAMIC_CONTENT_MANAGE);

		createCampaingTabItem(tabComposite);
	}

	/**
	 * Creates all sections of the dynamic content tab.
	 * @param tabComposite the Layout Composite 
	 */
	private void createCampaingTabItem(final IEpLayoutComposite tabComposite) {
		model = getModel();
		dataBindingCtx = new DataBindingContext();
		final IEpLayoutData layoutData = tabComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite epLayoutComposite =   tabComposite.addGroup(
				TargetedSellingMessages.get().FindDynamicContent,
				1, 
				false, 
				layoutData);
		epLayoutComposite.addLabelBold(TargetedSellingMessages.get().DynamicContentName, null);

		dynamicContentNameText = epLayoutComposite.addTextField(EpState.EDITABLE, layoutData);

		epLayoutComposite.addLabelBold(TargetedSellingMessages.get().DynamicContent_Status, null);

		dynamicContentStatusCombo = epLayoutComposite.addComboBox(EpState.EDITABLE, layoutData);
		dynamicContentStatusCombo.add(TargetedSellingMessages.get().DynamicContent_StatusAll, INDEX_ALL_DC);
		dynamicContentStatusCombo.add(TargetedSellingMessages.get().DynamicContent_StatusAssigned, INDEX_ASSIGNED_DC);
		dynamicContentStatusCombo.add(TargetedSellingMessages.get().DynamicContent_StatusNotAssigned, INDEX_NONASSIGNED_DC);
		dynamicContentStatusCombo.select(INDEX_ALL_DC);

		// Create the buttons group container
		final IEpLayoutComposite buttonsGroup = tabComposite.addGridLayoutComposite(1, false, layoutData);

		buttonsGroup.addHorizontalSeparator(buttonsGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		dynamicContentStatusCombo.setEnabled(dynamicContentManagmentEnabled);
		dynamicContentNameText.setEnabled(dynamicContentManagmentEnabled);

		bind();
	}

	/**
	 * Bind controls to model. Add selection listeners to buttons.
	 */
	private void bind() {
		EpControlBindingProvider.getInstance().bind(
				dataBindingCtx, 
				dynamicContentNameText, 
				getModel(), 
				"name",   //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_255, 
				null, 
				true);

		final ObservableUpdateValueStrategy statusUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				int newIndexValue = (Integer) newValue;
				switch(newIndexValue) {
				case INDEX_ALL_DC:
					getModel().setAssignedStatus(AssignedStatus.ALL);
					break;
				case INDEX_ASSIGNED_DC:
					getModel().setAssignedStatus(AssignedStatus.ASSIGNED);
					break;
				case INDEX_NONASSIGNED_DC:
					getModel().setAssignedStatus(AssignedStatus.NOTASSIGNED);
					break;
				default:
					break;
				}
				return Status.OK_STATUS;
			}
		};

		EpControlBindingProvider.getInstance().bind(
				dataBindingCtx,
				dynamicContentStatusCombo,
				null,
				null,
				statusUpdateStrategy,
				false);
	}

	/**
	 * Get the model.
	 * @return The Search Tab model instance
	 */
	public DynamicContentSearchTabModel getModel() {
		if (model == null) {
			model = new DynamicContentSearchTabModelImpl();
		}
		return model;
	}

	@Override
	public void search() {
		dataBindingCtx.updateModels();
		UIEvent<DynamicContentSearchTabModel> searchEvent =
				new UIEvent<>(model, EventType.SEARCH, true);
		StorePlugin.getDefault().getDynamicContentListController().onEvent(searchEvent);
	}

	@Override
	public void clear() {
		dynamicContentNameText.setText(""); //$NON-NLS-1$
		dynamicContentStatusCombo.select(INDEX_ALL_DC);
		dataBindingCtx.updateModels();
	}

	@Override
	public boolean isDisplaySearchButton() {
		return true;
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}

}
