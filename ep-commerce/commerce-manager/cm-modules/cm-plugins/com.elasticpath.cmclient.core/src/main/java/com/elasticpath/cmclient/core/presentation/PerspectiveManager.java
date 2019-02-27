/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPageService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.menus.CommandContributionItem;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ui.PerspectiveUtil;

/**
 * Perspective Manager calls PerspectiveUtils and triggers Command activation.
 * As a result gets the map that contains perspective and command ids.
 * <p>
 * This class uses PerspectiveService to get the list of Perspective ToolItems,
 * using the map it will find the corresponding ToolItem to the Perspective that got activated
 * and Decorate the image. Images are cached.
 * <p>
 * This manager is also capable of force perspective opening.
 */
public final class PerspectiveManager {

	/**
	 * Variables used to perform Perspective Switching.
	 */
	private final Map<ToolItem, Image> perspectiveImagesDecoratedMap = new HashMap<>();
	private final Map<ToolItem, Image> perspectiveImagesOriginalMap = new HashMap<>();
	private final Map<String, String> perspectivesToCommands;
	private ToolItem oldItem;

	/**
	 * Constructor.
	 */
	private PerspectiveManager() {
		perspectivesToCommands = new PerspectiveUtil().activatePerspectiveHandlers();
	}

	/**
	 * Returns session instance of this class.
	 *
	 * @return perspective manager
	 */
	public static PerspectiveManager getDefault() {
		return CmSingletonUtil.getSessionInstance(PerspectiveManager.class);
	}

	/**
	 * Register perspective listener that will update icons for the perspective given the PerspectiveService.
	 * @param pageService workbench pageService
	 * @param perspectiveService service that is used to get ToolItems
	 */
	public void registerPerspectiveListener(final IPageService pageService, final PerspectiveService perspectiveService) {
		if (perspectiveService != null) {
			pageService.addPerspectiveListener(new PerspectiveAdapter() {
				@Override
				public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
					List<ToolItem> perspectiveToolItems = perspectiveService.getPerspectiveToolItems();
					PerspectiveManager.this.updateIcons(perspective.getId(), perspectiveToolItems);
				}
			});
		}
	}

	/**
	 * Getter for the PerspectiveId.
	 *
	 * @param commandId command id
	 * @return Perspective id
	 */
	public String getPerspectiveByCommandId(final String commandId) {
		for (Map.Entry<String, String> entry : perspectivesToCommands.entrySet()) {
			if (commandId.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Opens preferred Perspective.
	 *
	 * @param service perspective service which returns PerspectiveId
	 */
	public void openPerspective(final PerspectiveService service) {
		if (service != null) {
			String perspectiveId = service.getPreferredPerspectiveId();
			if (perspectiveId == null) {
				return;
			}

			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			try {
				PlatformUI.getWorkbench().showPerspective(perspectiveId, window);
			} catch (WorkbenchException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * Updates the images of the ToolItem.
	 * Get commandId given perspective id, find item that corresponds to the commandId.
	 * Change its icon.
	 *
	 * @param perspectiveId perspective id
	 * @param items         perspective toolItems
	 */
	private void updateIcons(final String perspectiveId, final List<ToolItem> items) {
		String commandId = perspectivesToCommands.get(perspectiveId);
		for (ToolItem toolItem : items) {
			Object data = toolItem.getData();

			CommandContributionItem contributionItem = (CommandContributionItem) data;

			perspectiveImagesOriginalMap.putIfAbsent(toolItem, toolItem.getImage());
			Image image = perspectiveImagesOriginalMap.get(toolItem);

			if (!perspectiveImagesDecoratedMap.containsKey(toolItem)) {
				perspectiveImagesDecoratedMap.put(toolItem, CoreImageRegistry.createPerspectiveDecoratedImage(image));
			}

			if (contributionItem.getCommand().getId().equals(commandId)) {
				toolItem.setImage(perspectiveImagesDecoratedMap.get(toolItem));

				if (oldItem != null) {
					//Put original image back
					oldItem.setImage(perspectiveImagesOriginalMap.get(oldItem));
				}

				//Save this item as old
				oldItem = toolItem;
			}
		}
	}
}
