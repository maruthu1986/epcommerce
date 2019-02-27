/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.views;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerImageRegistry;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.event.PriceListSearchEvent;
import com.elasticpath.cmclient.pricelistmanager.event.PricingEventService;

/**
 * Composite that displays the PriceList Search GUI.
 */
public class PriceListSearchTab {
	
	private final PricingEventService eventService;
	
	private Button searchButton;
			
	/**
	 * Constructor.
	 * 
	 * @param tabFolder parent's searchView tab folder.
	 * @param tabIndex index of this tab into tabFolder.
	 */
	public PriceListSearchTab(final IEpTabFolder tabFolder, final int tabIndex) {
		
		eventService = PricingEventService.getInstance();
		
		final IEpLayoutComposite tabComposite = tabFolder.addTabItem(
				PriceListManagerMessages.get().PriceListSearchTab_Name,
				PriceListManagerImageRegistry.getImage(PriceListManagerImageRegistry.IMAGE_PRICE_LIST),
				tabIndex, 
				1, 
				false);
		
		createPriceListSearchItem(tabComposite);		

	}
	
	/**
	 * Creates all sections of the dynamic content tab.
	 * @param tabComposite the Layout Composite 
	 */
	private void createPriceListSearchItem(final IEpLayoutComposite tabComposite) {
		
		final IEpLayoutData layoutData = tabComposite.createLayoutData(
				IEpLayoutData.FILL, 
				IEpLayoutData.FILL, 
				true, 
				false);
		
		// Create the buttons group container
		final IEpLayoutComposite buttonsGroup = tabComposite.addGridLayoutComposite(1, false, layoutData);

		//buttonsGroup.addHorizontalSeparator(buttonsGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		// Create the buttons container
		final IEpLayoutData buttonsCompositeData = tabComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		//final IEpLayoutComposite buttonsComposite = buttonsGroup.addGridLayoutComposite(2, true, buttonsCompositeData);
		final IEpLayoutComposite buttonsComposite = buttonsGroup.addGridLayoutComposite(1, true, buttonsCompositeData);
		
		
		searchButton =  buttonsComposite.addPushButton(
				PriceListManagerMessages.get().PriceListSearchTab_SearchButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE),
				EpState.EDITABLE, 
				null);
		
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				doSearch();
			}
		});		
		
	}
	
	
	/**
	 * TODO: The implementation of this method will change when the Controller is fleshed out and
	 * the available search options are figured out. The controller will likely create an implementation
	 * extending AbstractSearchRequestJob and run it, which should update the SearchResultsView because it
	 * will be registered with the PriceListManagerEventService.
	 */
	private void doSearch() {
		firePriceListSearchEvent();
	}
	
	/**
	 * Fires a PriceListSearch event, populating the event with the
	 * search criteria.
	 */
	private void firePriceListSearchEvent() {
		PriceListSearchEvent plsEvent = new PriceListSearchEvent(StringUtils.EMPTY);
		eventService.fireSearchEvent(plsEvent);
	}
	
	
}
