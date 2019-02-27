/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.perspectives;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.views.BrowseProductListView;
import com.elasticpath.cmclient.catalog.views.CatalogBrowseView;
import com.elasticpath.cmclient.catalog.views.CatalogSearchView;
import com.elasticpath.cmclient.catalog.views.SearchProductListView;
import com.elasticpath.cmclient.catalog.views.SearchSkuListView;
import com.elasticpath.cmclient.core.ui.PerspectiveDefaults;
import com.elasticpath.cmclient.jobs.views.CatalogJobListView;

/**
 * This perspective provides the initial layout for working with products and categories.
 */
public class CatalogPerspectiveFactory implements IPerspectiveFactory {

	private static final Logger LOG = Logger.getLogger(CatalogPerspectiveFactory.class);
	
	private static final String BROWSE_SEARCH_FOLDER_ID = "BrowseSearchViewsFolder"; //$NON-NLS-1$

	private static final String LIST_FOLDER_ID = "ProductListViewsFolder"; //$NON-NLS-1$

	private static final String EXTENSION_NAME = "catalogPerspectiveExtension"; //$NON-NLS-1$

	/**
	 * The id of the catalog perspective.
	 */
	public static final String PERSPECTIVE_ID = "com.elasticpath.cmclient.catalog.catalogperspective"; //$NON-NLS-1$

	/**
	 * Creates the initial arrangement of catalog-related views. This will be invoked to set the layout when the user requests the perspective to be
	 * reset.
	 * 
	 * @param layout page layout
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		final IFolderLayout searchFolder = layout.createFolder(BROWSE_SEARCH_FOLDER_ID, IPageLayout.LEFT, PerspectiveDefaults.LEFT_RATIO,
			editorArea);

		searchFolder.addView(CatalogBrowseView.VIEW_ID);
		layout.getViewLayout(CatalogBrowseView.VIEW_ID).setCloseable(false);
		layout.getViewLayout(CatalogBrowseView.VIEW_ID).setMoveable(false);

		searchFolder.addView(CatalogSearchView.VIEW_ID);
		layout.getViewLayout(CatalogSearchView.VIEW_ID).setCloseable(false);
		layout.getViewLayout(CatalogSearchView.VIEW_ID).setMoveable(false);

		final IFolderLayout folder = layout.createFolder(LIST_FOLDER_ID, IPageLayout.TOP, PerspectiveDefaults.TOP_RATIO, editorArea);
		folder.addPlaceholder(CatalogJobListView.VIEW_ID);
		layout.getViewLayout(CatalogJobListView.VIEW_ID).setMoveable(false);

		folder.addPlaceholder(BrowseProductListView.PART_ID);
		layout.getViewLayout(BrowseProductListView.PART_ID).setMoveable(false);

		folder.addPlaceholder(SearchProductListView.PART_ID);
		layout.getViewLayout(SearchProductListView.PART_ID).setMoveable(false);
		
		folder.addPlaceholder(SearchSkuListView.PART_ID);
		layout.getViewLayout(SearchSkuListView.PART_ID).setMoveable(false);

		layout.addFastView(IPageLayout.ID_PROGRESS_VIEW);
		
		registerAdvancedSearchViews(layout, searchFolder, folder);
	}
	
	private void registerAdvancedSearchViews(final IPageLayout layout, final IFolderLayout searchFolder, final IFolderLayout resultFolder) {
		Collection<ICatalogPerspectiveViewExtension> catalogPerspectiveViews = findExtensions();
		
		for (ICatalogPerspectiveViewExtension extension : catalogPerspectiveViews) {
			if (isViewRegistered(extension.getViewId())
					&& extension.isAuthorized()) {
				resultFolder.addPlaceholder(extension.getPlaceholder());
		
				searchFolder.addView(extension.getViewId());
				layout.getViewLayout(extension.getViewId()).setCloseable(extension.isCloseable());
				layout.getViewLayout(extension.getViewId()).setMoveable(extension.isMovable());
			}
		}
	}

	private Collection<ICatalogPerspectiveViewExtension> findExtensions() {
		Collection<ICatalogPerspectiveViewExtension> catalogPerspectiveExtensions = new ArrayList<>();
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(CatalogPlugin.PLUGIN_ID, EXTENSION_NAME).getExtensions();
		for (IExtension extension : extensions) {
			for (IConfigurationElement configElement : extension.getConfigurationElements()) {
				try {
					ICatalogPerspectiveViewExtension adminSectionType = parseItem(configElement);
					catalogPerspectiveExtensions.add(adminSectionType);
				} catch (CoreException e) {
					LOG.error("Exception while reading extensions", e); //$NON-NLS-1$
				}
			}
		}
		return catalogPerspectiveExtensions;
	}

	private ICatalogPerspectiveViewExtension parseItem(final IConfigurationElement configElement) throws CoreException {
		return new CatalogPerspectiveViewExtension(configElement);
	}

	private boolean isViewRegistered(final String viewId) {
		return PlatformUI.getWorkbench().getViewRegistry().find(viewId) != null;
	}
}
