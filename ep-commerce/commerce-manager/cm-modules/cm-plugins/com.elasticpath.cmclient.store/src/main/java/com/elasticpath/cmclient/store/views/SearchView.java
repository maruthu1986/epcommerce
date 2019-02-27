/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.views;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.perspective.StorePerspectiveFactory;
import com.elasticpath.cmclient.store.promotions.PromotionsPermissions;
import com.elasticpath.cmclient.store.promotions.views.PromotionsSearchTab;
import com.elasticpath.cmclient.store.settings.StoreMarketingPermissions;
import com.elasticpath.cmclient.store.settings.views.SettingsSearchTab;
import com.elasticpath.cmclient.store.shipping.ShippingLevelsPermissions;
import com.elasticpath.cmclient.store.shipping.views.ShippingLevelsSearchTab;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingPermissions;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views.ConditionalExpressionSearchTab;
import com.elasticpath.cmclient.store.targetedselling.delivery.views.DynamicContentDeliverySearchTab;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views.DynamicContentSearchTab;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for creating the search view.
 */
public class SearchView extends AbstractCmClientView implements IPerspectiveListener, SelectionListener {

	/**
	 * SearchView ID specified in the plugin.xml file. It is the same as the class name.
	 */
	public static final String ID_SEARCH_VIEW = SearchView.class.getName();

	private final List<IStoreMarketingInnerTab> tabs = new ArrayList<>();

	private PromotionsSearchTab promotionsSearchTab;

	private ShippingLevelsSearchTab shippingLevelsSearchTab;
	
	@SuppressWarnings("unused")
	private DynamicContentSearchTab dynamicContentSearchTab; //NOPMD
	
	private DynamicContentDeliverySearchTab dynamicContentDeliverySearchTab; //NOPMD
	
	@SuppressWarnings("unused")
	private SettingsSearchTab settingsSearchTab; //NOPMD
	
	private ConditionalExpressionSearchTab conditionalExpressionSearchTab; //NOPMD

	private IEpTabFolder tabFolder;

	private Button clearButton;

	private Button searchButton;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		site.getWorkbenchWindow().addPerspectiveListener(this);
	}

	@Override
	protected void createViewPartControl(final Composite parentComposite) {

		// Create the container for the view
		final IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parentComposite, 1, false);

		// Create the tab container
		tabFolder = parentEpComposite.addTabFolder(parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		
		int index = 0;
		if (this.isAuthorized(PromotionsPermissions.PROMOTION_MANAGE)) {
			promotionsSearchTab = new PromotionsSearchTab(tabFolder, index++, this);
			tabs.add(promotionsSearchTab);
		}
		if (this.isAuthorized(ShippingLevelsPermissions.SHIPPING_SERVICE_LEVELS_MANAGE)) {
			shippingLevelsSearchTab = new ShippingLevelsSearchTab(tabFolder, index++, this);
			tabs.add(shippingLevelsSearchTab);
		}
		if (this.isAuthorized(StoreMarketingPermissions.MANAGE_STORE_SETTINGS)) {
			settingsSearchTab = new SettingsSearchTab(tabFolder, index++, this);
			tabs.add(settingsSearchTab);
		}
		if (StorePlugin.ENABLE_DYNAMIC_CONTENT_IN_UI) {
			if (this.isAuthorized(TargetedSellingPermissions.DYNAMIC_CONTENT_MANAGE)) {
				dynamicContentSearchTab = new DynamicContentSearchTab(tabFolder, index++, this);
				tabs.add(dynamicContentSearchTab);
			}
			if (this.isAuthorized(TargetedSellingPermissions.DYNAMIC_CONTENT_DELIVERY_MANAGE)) {
				dynamicContentDeliverySearchTab = new DynamicContentDeliverySearchTab(tabFolder, index++, this);
				tabs.add(dynamicContentDeliverySearchTab);
			}
			if (this.isAuthorized(TargetedSellingPermissions.CONDITIONAL_EXPRESSION_MANAGE)) {
				conditionalExpressionSearchTab = new ConditionalExpressionSearchTab(tabFolder, index++, this);
				tabs.add(conditionalExpressionSearchTab);
			}
		}

		createButtonsPane(parentEpComposite);

		if (index > 0) {
			tabFolder.setSelection(0);
		}
	}
	
	private boolean isAuthorized(final String secureId) {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(secureId);
	}

	@Override
	protected Object getModel() {
		if (tabFolder.getSelectedTabIndex() == promotionsSearchTab.getTabIndex()) {
			return promotionsSearchTab.getModel();
		}
		return null;
	}

	@Override
	public void setFocus() {
		if (tabFolder.getSelectedTabIndex() == promotionsSearchTab.getTabIndex()) {
			promotionsSearchTab.setFocus();
		}
	}

	@Override
	public void dispose() {
		if (conditionalExpressionSearchTab != null) {
			conditionalExpressionSearchTab.dispose();
		}
		if (shippingLevelsSearchTab != null) {
			shippingLevelsSearchTab.dispose();
		}
		if (dynamicContentDeliverySearchTab != null) {
			dynamicContentDeliverySearchTab.dispose();
		}
		super.dispose();
	}

	@Override
	public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
		if (perspective.getId().equals(StorePerspectiveFactory.PERSPECTIVE_ID)) {
			if (shippingLevelsSearchTab != null) {
				shippingLevelsSearchTab.reinitFilterLists();
			}
			if (promotionsSearchTab != null) {
				promotionsSearchTab.reinitStoreFilter();
			}
		}
	}

	@Override
	public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective, final String changeId) {
		//do nothing
	}

	/**
	 * Creates the pane containing the Search and Clear buttons.
	 */
	private IEpLayoutComposite createButtonsPane(final IEpLayoutComposite parentComposite) {
		final IEpLayoutData wrapCompositeData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite wrapComposite = parentComposite.addGridLayoutComposite(1, false, wrapCompositeData);

		wrapComposite.addHorizontalSeparator(wrapComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		final IEpLayoutData buttonsCompositeData = parentComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		final IEpLayoutComposite buttonsComposite = wrapComposite.addGridLayoutComposite(2, true, buttonsCompositeData);

		// search button
		this.searchButton = buttonsComposite.addPushButton(CatalogMessages.get().SearchView_SearchButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE), EpControlFactory.EpState.EDITABLE, null);
		this.searchButton.addSelectionListener(this);

		// clear button
		this.clearButton = buttonsComposite.addPushButton(CatalogMessages.get().SearchView_ClearButton, EpControlFactory.EpState.EDITABLE, null);
		this.clearButton.addSelectionListener(this);

		tabFolder.getSwtTabFolder().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {

				final IStoreMarketingInnerTab selectedTab = findSelectedTab();
				if (selectedTab.isDisplaySearchButton()) {
					buttonsComposite.getSwtComposite().setVisible(true);
				} else {
					buttonsComposite.getSwtComposite().setVisible(false);
				}
			}
		});
		return wrapComposite;
	}

	private IStoreMarketingInnerTab findSelectedTab() {
		for (IStoreMarketingInnerTab iStoreMarketingInnerTab : tabs) {
			if (iStoreMarketingInnerTab.getTabIndex() == tabFolder.getSelectedTabIndex()) {
				return iStoreMarketingInnerTab;
			}
		}
		return null;
	}

	/**
	 * Called when a widget is default selected. For text fields hitting ENTER calls this method.
	 *
	 * @param event the event.
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		if (event.getSource() instanceof Text) {
			search();
		}
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == this.searchButton) {
			search();

		} else if (event.getSource() == this.clearButton) {
			this.clear();
		}
	}

	private void clear() {
		final IStoreMarketingInnerTab selectedTab = findSelectedTab();
		selectedTab.clear();
	}

	private void search() {
		final IStoreMarketingInnerTab selectedTab = findSelectedTab();
		selectedTab.search();
	}

	@Override
	protected String getPartId() {
		return ID_SEARCH_VIEW;
	}
}