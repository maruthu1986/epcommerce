/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.views;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.eventlistener.UIEventListener;
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
import com.elasticpath.cmclient.store.targetedselling.delivery.model.DynamicContentDeliverySearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.delivery.model.impl.DynamicContentDeliverySearchTabModelImpl;
import com.elasticpath.cmclient.store.views.IStoreMarketingInnerTab;
import com.elasticpath.cmclient.store.views.SearchView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;

/**
 * Provides methods for creating the dynamic content assignment search view.
 */
public class DynamicContentDeliverySearchTab implements IStoreMarketingInnerTab {

	/**
	 * DCA View ID specified in the plugin.xml file. It is the same as the class name.
	 */	
	public static final String ID_DCA_SEARCH_VIEW = DynamicContentDeliverySearchTab.class.getName();

	private DynamicContentDeliverySearchTabModel model; 

	@SuppressWarnings("unused")
	private final SearchView searchView; //NOPMD

	private DataBindingContext dataBindingCtx;

	private Text dynamicContentAssignmentNameText;

	private CCombo dynamicContentCombo;

	private CCombo dynamicContentSpaceCombo;

	private UIEventListener<SearchResultEvent<DynamicContent>> dynamicContentEventListener;

	private UIEventListener<SearchResultEvent<ContentSpace>> assignmentTargetEventListener;

	private final boolean dynamicContentManagmentEnabled;

	private final int tabIndex;

	/**
	 * Constructor.
	 * 
	 * @param tabFolder parent's searchView tab folder.
	 * @param tabIndex index of this tab into tabFolder.
	 * @param searchView parent SearchView.
	 */
	public DynamicContentDeliverySearchTab(final IEpTabFolder tabFolder, final int tabIndex, final SearchView searchView) {

		final Image tabImage = TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_DELIVERY_TAB.createImage();

		final IEpLayoutComposite tabComposite = tabFolder.addTabItem(
				TargetedSellingMessages.get().DynamicContentDeliveryTabTitle,
				tabImage,
				tabIndex, 
				1, 
				false);

		this.searchView = searchView;
		this.tabIndex = tabIndex;

		dynamicContentManagmentEnabled = AuthorizationService.getInstance().isAuthorizedWithPermission(
				TargetedSellingPermissions.DYNAMIC_CONTENT_DELIVERY_MANAGE);

		createListeners();

		createDcaTabItem(tabComposite);

		firePopulateDataEvent();
	}

	/**
	 * Create and fire event for populate date into dynamicContentCombo and dynamicContentSpaceCombo.
	 */
	private void firePopulateDataEvent() {
		StorePlugin.getDefault().getDynamicContentsController().addListener(dynamicContentEventListener);
		StorePlugin.getDefault().getContentSpacesController().addListener(assignmentTargetEventListener);

		UIEvent<DynamicContent> dynamicContentSearchEvent = new UIEvent<>(
				ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT), EventType.SEARCH, true);
		StorePlugin.getDefault().getDynamicContentsController().onEvent(dynamicContentSearchEvent);

		UIEvent<ContentSpace> assignmentTargetEvent = new UIEvent<>(
				ServiceLocator.getService(ContextIdNames.CONTENTSPACE), EventType.SEARCH, true);
		StorePlugin.getDefault().getContentSpacesController().onEvent(assignmentTargetEvent);
	}

	private void createListeners() {
		dynamicContentEventListener = eventObject -> {
			dynamicContentCombo.clearSelection();
			dynamicContentCombo.removeAll();
			if (dynamicContentManagmentEnabled) {
				String[] dcNames = new String[eventObject.getItems().size()];
				String[] dcNamesWithAll = new String[eventObject.getItems().size() + 1];
				dcNamesWithAll[0] = TargetedSellingMessages.get().FilterOptionAll;
				int index = 0;
				for (DynamicContent dynamicContent : eventObject.getItems()) {
					dcNames[index] = dynamicContent.getName();
					index++;
				}
				Arrays.sort(dcNames);
				System.arraycopy(dcNames, 0, dcNamesWithAll, 1, dcNames.length);
				dynamicContentCombo.setItems(dcNamesWithAll);
				dynamicContentCombo.select(0);
			}
		};

		assignmentTargetEventListener = eventObject -> {
			dynamicContentSpaceCombo.clearSelection();
			dynamicContentSpaceCombo.removeAll();
			if (dynamicContentManagmentEnabled) {
				String[] cspaceNames = new String[eventObject.getItems().size()];
				String[] cspaceNamesWithAll = new String[eventObject.getItems().size() + 1];
				cspaceNamesWithAll[0] = TargetedSellingMessages.get().FilterOptionAll;
				int index = 0;
				for (ContentSpace assignmentTarget : eventObject.getItems()) {
					cspaceNames[index] = assignmentTarget.getTargetId();
					index++;
				}
				Arrays.sort(cspaceNames);
				System.arraycopy(cspaceNames, 0, cspaceNamesWithAll, 1, cspaceNames.length);
				dynamicContentSpaceCombo.setItems(cspaceNamesWithAll);
				dynamicContentSpaceCombo.select(0);
			}
		};
	}

	/**
	 * Creates all sections of the dynamic content tab.
	 * @param tabComposite the Layout Composite 
	 */
	private void createDcaTabItem(final IEpLayoutComposite tabComposite) {
		dataBindingCtx = new DataBindingContext();

		final IEpLayoutData layoutData = tabComposite.createLayoutData(
				IEpLayoutData.FILL, 
				IEpLayoutData.FILL, 
				true, 
				false);

		final IEpLayoutComposite epLayoutComposite =  tabComposite.addGroup(
				TargetedSellingMessages.get().FindDynamicContentDelivery,
				1, 
				false, 
				layoutData);

		epLayoutComposite.addLabelBold(TargetedSellingMessages.get().DynamicContentDeliveryName, null);

		dynamicContentAssignmentNameText = epLayoutComposite.addTextField(EpState.EDITABLE, layoutData);

		epLayoutComposite.addLabelBold(TargetedSellingMessages.get().DynamicContent, null);

		dynamicContentCombo = epLayoutComposite.addComboBox(EpState.EDITABLE, layoutData);

		epLayoutComposite.addLabelBold(TargetedSellingMessages.get().ContentSpace, null);

		dynamicContentSpaceCombo = epLayoutComposite.addComboBox(EpState.EDITABLE, layoutData);

		dynamicContentAssignmentNameText.setEnabled(dynamicContentManagmentEnabled);
		dynamicContentCombo.setEnabled(dynamicContentManagmentEnabled);
		dynamicContentSpaceCombo.setEnabled(dynamicContentManagmentEnabled);

		EpControlBindingProvider.getInstance().bind(
				dataBindingCtx, 
				dynamicContentAssignmentNameText, 
				getModel(), 
				"name",   //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_255, 
				null, 
				true);
	}

	/**
	 * Get the text from combo box for model.
	 * 0 selected index mean, that user select all filter option.
	 * @param combo instance of combo box, that contains "All" filter option
	 * @return empty string if selection index is 0, otherwise text from combobox.
	 */
	private String getTextForModel(final CCombo combo) {
		if (combo.getSelectionIndex() == 0) { // All option selected
			return ""; //$NON-NLS-1$
		}
		return combo.getText();
	}

	private DynamicContentDeliverySearchTabModel getModel() {
		if (model == null) {
			model = new DynamicContentDeliverySearchTabModelImpl();
		}
		return model;
	}

	/**
	 * Unregister from Services.
	 */
	public void dispose() {
		StorePlugin.getDefault().getDynamicContentsController().removeListener(dynamicContentEventListener);
		StorePlugin.getDefault().getContentSpacesController().removeListener(assignmentTargetEventListener);
	}

	@Override
	public void search() {
		getModel().setDynamicContentName(getTextForModel(dynamicContentCombo));
		getModel().setContentspaceId(getTextForModel(dynamicContentSpaceCombo));

		UIEvent<DynamicContentDeliverySearchTabModel> searchEvent =
				new UIEvent<>(getModel(), EventType.SEARCH, true);
		StorePlugin.getDefault().getDynamicContentDeliveryListController().onEvent(searchEvent);
	}

	@Override
	public void clear() {
		dynamicContentAssignmentNameText.setText(StringUtils.EMPTY);
		dynamicContentCombo.select(0);
		dynamicContentSpaceCombo.select(0);
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
