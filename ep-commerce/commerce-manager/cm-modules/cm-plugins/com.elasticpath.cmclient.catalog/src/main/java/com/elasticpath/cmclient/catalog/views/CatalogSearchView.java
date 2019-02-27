/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;

/**
 * 
 * The product search view class.
 *
 */
public class CatalogSearchView extends AbstractCmClientView implements SelectionListener {
	
	/**
	 * SearchView ID specified in the plugin.xml file. It is the same as the class name
	 */
	public static final String VIEW_ID = CatalogSearchView.class.getName();
	
	private static final int PRODUCT_SEARCH_TAB_INDEX = 0;
	private static final int SKU_SEARCH_TAB_INDEX = 1;
	
	private Button clearButton;
	private Button searchButton;
	
	private final List<AbstractCatalogSearchViewTab> tabs = new ArrayList<>();

	private IEpTabFolder tabFolder;
	
	@Override
	protected void createViewPartControl(final Composite parentComposite) {
		// Create the container for the view
		final IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parentComposite, 1, false);

		// Create the tab container
		tabFolder = parentEpComposite.addTabFolder(
				parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		final IEpLayoutComposite productSearchViewTabComposite = tabFolder.addTabItem(
				CatalogMessages.get().ProductBundle_Tab_Title,
				CoreImageRegistry.getImage(CoreImageRegistry.PRODUCT),
				PRODUCT_SEARCH_TAB_INDEX, 
				1, 
				false);
		ProductSearchViewTab productSearchViewTab = new ProductSearchViewTab(productSearchViewTabComposite, this);
		productSearchViewTab.init();
		tabs.add(productSearchViewTab);
		
		final IEpLayoutComposite skuSearchViewTabComposite = tabFolder.addTabItem(
				CatalogMessages.get().SkuBundle_Tab_Title,
				CoreImageRegistry.getImage(CoreImageRegistry.PRODUCT_SKU),
				SKU_SEARCH_TAB_INDEX, 
				1, 
				false);
		SkuSearchViewTab skuSearchViewTab = new SkuSearchViewTab(skuSearchViewTabComposite, this, tabFolder);
		skuSearchViewTab.init();
		tabs.add(skuSearchViewTab);
	
		tabFolder.setSelection(PRODUCT_SEARCH_TAB_INDEX);
		EPTestUtilFactory.getInstance().getTestIdUtil().setTestIdsToTabFolderItems(tabFolder);
		
		createButtonsPane(parentEpComposite);
	}

	@Override
	protected Object getModel() {
		return null;
	}
	
	@Override
	public void setFocus() {
		// nothing to do		
	}
	
	/*
	 * Creates the pane containing the Search and Clear buttons.
	 */
	private void createButtonsPane(final IEpLayoutComposite parentComposite) {
		final IEpLayoutData wrapCompositeData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite wrapComposite = parentComposite.addGridLayoutComposite(1, false, wrapCompositeData);

		wrapComposite.addHorizontalSeparator(wrapComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		final IEpLayoutData buttonsCompositeData = parentComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		final IEpLayoutComposite buttonsComposite = wrapComposite.addGridLayoutComposite(2, true, buttonsCompositeData);

		// search button
		this.searchButton = buttonsComposite.addPushButton(CatalogMessages.get().SearchView_SearchButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE), EpState.EDITABLE, null);
		this.searchButton.addSelectionListener(this);
		
		// clear button
		this.clearButton = buttonsComposite.addPushButton(CatalogMessages.get().SearchView_ClearButton, EpState.EDITABLE, null);
		this.clearButton.addSelectionListener(this);
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
		final AbstractCatalogSearchViewTab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());
		selectedTab.clear();
	}

	private void search() {
		final AbstractCatalogSearchViewTab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());
		selectedTab.search();
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
