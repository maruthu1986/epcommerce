/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IMenuService;

/**
 * This class uses menu service to load contribution items from plugin.xml files.
 */
public class ContributionLoader {

	private final IMenuService menuService;

	/**
	 * Constructor.
	 */
	public ContributionLoader() {
		IWorkbench workbench = PlatformUI.getWorkbench();

		menuService = workbench.getService(IMenuService.class);
	}

	/**
	 * Loads contribution elements from plugin.xml files.
	 * Any key can be specified as soon as it matches in xml file.
	 * For standard toolbar URIs reference to MenuUtil class
	 *
	 * @param toolbarURI id of the toolbar found in xml file
	 *
	 * @return loaded list of contribution items
	 */
	public List<IContributionItem> getItems(final String toolbarURI) {
		ToolBarManager contributionHolder = new ToolBarManager(SWT.NONE);

		menuService.populateContributionManager(contributionHolder, toolbarURI);

		IContributionItem[] items = contributionHolder.getItems();

		List<IContributionItem> extractedItems = new ArrayList<IContributionItem>();

		for (IContributionItem item : items) {
			if (item instanceof ToolBarContributionItem) {
				ToolBarContributionItem toolBarContributionItem = (ToolBarContributionItem) item;
				IToolBarManager toolBarManager = toolBarContributionItem.getToolBarManager();

				if (toolBarManager != null) {
					Collections.addAll(extractedItems, toolBarManager.getItems());
				}
			}
		}

		return extractedItems;
	}
}
