/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.pricelistmanager.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.controller.impl.AbstractBaseControllerImpl;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.PerspectiveDefaults;
import com.elasticpath.cmclient.pricelistassignments.controller.PriceListAssignmentsSearchController;
import com.elasticpath.cmclient.pricelistassignments.views.PriceListAssigmentsSearchView;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;
import com.elasticpath.cmclient.pricelistmanager.actions.OpenPriceListEditorAction;
import com.elasticpath.cmclient.pricelistmanager.actions.OpenPriceListSearchResultsViewAction;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListManagerSearchController;
import com.elasticpath.cmclient.pricelistmanager.controller.impl.PriceListSearchControllerImpl;
import com.elasticpath.cmclient.pricelistmanager.event.PricingEventService;
import com.elasticpath.cmclient.pricelistmanager.views.PriceListSearchResultsView;
import com.elasticpath.cmclient.pricelistmanager.views.PriceListSearchView;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;

/**
 * Factory for specifying the layout of the perspective.
 */
public class PriceListManagerPerspectiveFactory implements IPerspectiveFactory {

	/**
	 * Perspective ID.
	 */
	public static final String PERSPECTIVE_ID = "com.elasticpath.cmclient.pricelistmanager.perspective"; //$NON-NLS-1$
	
	/**
	 * Import jobs view ID.
	 */
	public static final String IMPORT_JOB_VIEW = "com.elasticpath.cmclient.jobs.views.PriceListImportJobsListView"; //$NON-NLS-1$
	
	
	private PriceListManagerSearchController priceListManagerController;
	private final PricingEventService pricingEventService = PricingEventService.getInstance();
	private OpenPriceListEditorAction openPriceListEditorAction;
	private OpenPriceListSearchResultsViewAction openPriceListSearchResultsViewAction;
	
	private AbstractBaseControllerImpl<PriceListAssignmentsDTO> priceListAssignmentsSearchController;
	
	
	/**
	 * Called by Eclipse to layout the perspective.
	 * 
	 * @param layout the page layout
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		final String editorArea = layout.getEditorArea();
		
		//Add the PriceListSelector view on the left and position it left, relative to the editorArea
		if (this.isAuthorized(PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRICE_LISTS)) {
			layout.addView(PriceListSearchView.ID_PRICELISTSEARCH_VIEW, IPageLayout.LEFT, PerspectiveDefaults.LEFT_RATIO, editorArea);
		layout.getViewLayout(PriceListSearchView.ID_PRICELISTSEARCH_VIEW).setCloseable(false);
		
		//Add the search results view on the right and position it top, relative to the editorArea
			IFolderLayout right = layout.createFolder("searchResults", IPageLayout.TOP, PerspectiveDefaults.TOP_RATIO, editorArea); //$NON-NLS-1$
		right.addPlaceholder(PriceListSearchResultsView.VIEW_ID);
		right.addPlaceholder(PriceListAssigmentsSearchView.VIEW_ID);
		
		layout.getViewLayout(PriceListSearchResultsView.VIEW_ID).setMoveable(false);
		layout.getViewLayout(PriceListAssigmentsSearchView.VIEW_ID).setMoveable(false);
		
		//Add a placeholder for the progress view
		layout.addFastView(IPageLayout.ID_PROGRESS_VIEW);

		//Add import job placeholder.
		right.addPlaceholder(IMPORT_JOB_VIEW);
		layout.getViewLayout(IMPORT_JOB_VIEW).setMoveable(false);
		}
		startServices();
		
	}
	
	private void startServices() {
		priceListManagerController =  CmSingletonUtil.getSessionInstance(PriceListSearchControllerImpl.class);
		openPriceListSearchResultsViewAction = CmSingletonUtil.getSessionInstance(OpenPriceListSearchResultsViewAction.class);
		pricingEventService.addPriceListSearchEventListener(openPriceListSearchResultsViewAction);
		pricingEventService.addPriceListSearchEventListener(priceListManagerController);
		//create the action that will open the editor. This could be done declaratively.
		openPriceListEditorAction = CmSingletonUtil.getSessionInstance(OpenPriceListEditorAction.class);
		pricingEventService.addPriceListSelectedEventListener(openPriceListEditorAction);

		priceListAssignmentsSearchController = CmSingletonUtil.getSessionInstance(PriceListAssignmentsSearchController.class);
		
		priceListAssignmentsSearchController.addListener(
			eventObject -> {
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewPart viewPart = page.findView(PriceListAssigmentsSearchView.VIEW_ID);
				if (viewPart == null) {
					try {
						viewPart = page.showView(PriceListAssigmentsSearchView.VIEW_ID);
					} catch (PartInitException e) {
						//TODO-RAP-M2 log if required
					}
				}
				((PriceListAssigmentsSearchView) viewPart).setSearchResultEvent(eventObject);
				page.bringToTop(viewPart);
			}
		);
		
	}
	
	private boolean isAuthorized(final String secureId) {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(secureId);
	}

}
