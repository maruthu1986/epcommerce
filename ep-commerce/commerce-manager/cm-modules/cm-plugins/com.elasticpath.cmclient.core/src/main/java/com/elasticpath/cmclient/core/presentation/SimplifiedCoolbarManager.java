/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.presentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.internal.provisional.action.CoolBarManager2;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.menus.CommandContributionItem;

import com.elasticpath.cmclient.core.CoreImageRegistry;

/**
 * This class replaces the Workbench-style header from the RCP days allowing
 * direct control over the toolbars created.
 * <p>
 * One benefit: we work around the TRIM_COMMAND2 toolbar bug which caused toolbar truncation.
 * <p>
 * This manager splits the mainBar composite into chunks and adds separators in between them.
 * Then it loads all the items and provides them to the ToolbarManagers.
 * SimplifiedCoolbarManager delegates responsibility of managing ToolItems to the ToolBarManagers
 */
@SuppressWarnings("PMD.GodClass")
public class SimplifiedCoolbarManager extends CoolBarManager2 implements PerspectiveService {

	private static final Logger LOG = Logger.getLogger(SimplifiedCoolbarManager.class);
	private static final int MAX_POSITION_PERCENT = 100; //100% of the parent composite
	private static final int HALF_POSITION_PERCENT = 50; //50% of the parent composite

	private Composite mainBar;

	/**
	 * LinkedHashMap is used because positions of the toolbars depends
	 * on the order specified in plugin.xml with (?after) (?before) tags.
	 * <p>
	 * Contains composites with proper form layouts.
	 * Those composites contain toolbarComposites and separators
	 */
	private final Map<IToolBarContributionItem, Composite> toolbarComposites = new LinkedHashMap<>();
	private final Map<IToolBarContributionItem, ToolBarManager> managers = new LinkedHashMap<>();
	private final Map<IToolBarContributionItem, ReverseConnector> oldConnectionsMap = new HashMap<>();
	private final List<IToolBarContributionItem> keys = new ArrayList<>();
	private ToolBarManager perspectiveToolbarManager;
	private boolean toolbarBlocksCreated;
	private IContributionItem lastKey;

	@Override
	public Control createControl2(final Composite parent) {
		mainBar = new HeaderBuilder(parent).getCoolbarComposite();
		mainBar.setLayout(new FormLayout());
		mainBar.getShell().addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent controlEvent) {
				update(true);
			}
		});
		return mainBar;
	}

	@Override
	public Control getControl2() {
		return mainBar;
	}

	@Override
	public void dispose() {
		toolbarComposites.clear();
		managers.clear();
		perspectiveToolbarManager.dispose();
		oldConnectionsMap.clear();
		keys.clear();
		toolbarBlocksCreated = false;
	}

	@Override
	public void update(final boolean force) {
		if ((isDirty() || force) && getControl2() != null) {
			refresh();

			//Create placeholders for toolbars
			if (!toolbarBlocksCreated) {
				createToolbarBlocks();
			}
			//Put new items to the managers (relies on createdItems map)
			addNewItemsToManagers();

			//Tell every manager to take care of their elements and assign test ids

			for (Map.Entry<IToolBarContributionItem, ToolBarManager> entry : managers.entrySet()) {
				ToolBarManager toolBarManager = entry.getValue();
				toolBarManager.update(true);
				updateToolbarVisibility(entry.getKey(), toolBarManager);
			}

			//Update whole bar composite in order to layout all the composites properly
			mainBar.layout();
			//We are no longer dirty.
			setDirty(false);
		}
	}

	private boolean allInvisible(final ToolBarManager toolBarManager) {
		for (IContributionItem item : toolBarManager.getItems()) {
			if (item.isVisible()) {
				return false;
			}
		}
		return true;
	}

	private void addNewItemsToManagers() {

		for (IContributionItem item : getItems()) {
			if (item instanceof IToolBarContributionItem) {
				addItem((IToolBarContributionItem) item);
			}
		}
	}

	private void addItem(final IToolBarContributionItem item) {
		IToolBarContributionItem key = item;
		ToolBarManager toolBarManager = managers.get(key);

		//If ToolbarManager Container not created
		if (toolBarManager == null) {
			boolean someItemsMustBeAdded = key.getToolBarManager().getItems().length != 0;

			if (someItemsMustBeAdded) {
				LOG.debug("ToolBarManager is null for key, cannot add items: " + key.getId()); //$NON-NLS-1$
			}
			return;
		}

		IToolBarManager itemHolder = key.getToolBarManager();
		if (itemHolder != null && itemHolder.getItems() != null) {
			//Add items to real toolbar manager from item holder
			for (IContributionItem contributionItem : itemHolder.getItems()) {
				if (!contributionItem.isSeparator()) {
					toolBarManager.add(contributionItem);
				}
			}
		}
	}

	/**
	 * This function allocates the spaces on the mainBar composite one for each toolbar.
	 */
	private void createToolbarBlocks() {
		IContributionItem previousKey = null;
		int toolbarAmount = getItems().length;
		for (int count = 0; count < toolbarAmount; count++) {
			IContributionItem iContributionItem = getItems()[count];
			if (!iContributionItem.isVisible()) {
				continue;
			}
			IToolBarContributionItem toolbarItem = (IToolBarContributionItem) iContributionItem;

			if (managers.isEmpty()) {
				//Declare first toolbar attached to left side of the Header
				declareToolbarLeftAttachedTo(new FormAttachment(0), toolbarItem, false);
			} else {
				Composite previousToolBarWrapper = toolbarComposites.get(previousKey);

				//Declare new toolbar just besides previous toolbar
				declareToolbarLeftAttachedTo(new FormAttachment(previousToolBarWrapper), toolbarItem, true);
			}
			previousKey = toolbarItem;
		}
		lastKey = previousKey;

		toolbarBlocksCreated = true;
	}

	private void declareToolbarLeftAttachedTo(final FormAttachment leftFormAttachment,
		final IToolBarContributionItem toolbarItem, final boolean includeSeparator) {

		Composite block = new Composite(mainBar, SWT.NONE);
		block.setLayout(new FormLayout());
		FormData fdToolbar = new FormData();
		block.setLayoutData(fdToolbar);
		fdToolbar.top = new FormAttachment(0);
		fdToolbar.bottom = new FormAttachment(MAX_POSITION_PERCENT);
		fdToolbar.left = leftFormAttachment;

		ToolBarManager toolBarManager;
		if (includeSeparator) {
			Composite separatorContainer = new Composite(block, SWT.NONE);
			separatorContainer.setLayout(new FormLayout());
			FormData fdSeparator = new FormData();
			separatorContainer.setLayoutData(fdSeparator);

			Label separator = new Label(separatorContainer, SWT.NONE);
			separator.setImage(CoreImageRegistry.getImage(CoreImageRegistry.TOOLBAR_SEPARATOR));

			// Use the image height to center the image vertically
			int halfImageHeight = separator.getImage().getBounds().height / 2;
			fdSeparator.top = new FormAttachment(HALF_POSITION_PERCENT, -halfImageHeight);
			fdSeparator.bottom = new FormAttachment(HALF_POSITION_PERCENT, halfImageHeight);
			fdSeparator.left = new FormAttachment(0);


			Composite toolbarContainer = new Composite(block, SWT.NONE);
			toolbarContainer.setLayout(new FormLayout());
			FormData fdToolbarContainer = new FormData();
			toolbarContainer.setLayoutData(fdToolbarContainer);
			fdToolbarContainer.top = new FormAttachment(0);
			fdToolbarContainer.bottom = new FormAttachment(MAX_POSITION_PERCENT);
			fdToolbarContainer.right = new FormAttachment(MAX_POSITION_PERCENT);

			fdSeparator.right = new FormAttachment(toolbarContainer);

			ToolBar toolBar = new ToolBar(toolbarContainer, SWT.FLAT);
			toolBarManager = new EpToolBarManager(toolBar);

		} else {
			ToolBar toolBar = new ToolBar(block, SWT.FLAT);
			toolBarManager = new EpToolBarManager(toolBar);
			perspectiveToolbarManager = toolBarManager;
		}
		keys.add(toolbarItem);
		toolbarComposites.put(toolbarItem, block);
		managers.put(toolbarItem, toolBarManager);
	}

	/**
	 * ToolbarManger that contains unique ContributionItems.
	 */
	private final class EpToolBarManager extends ToolBarManager {

		 EpToolBarManager(final ToolBar toolbar) {
			super(toolbar);
		}

		@Override
		public void add(final IContributionItem item) {
			// `Add` works now as Replace
			super.remove(item.getId());
			super.add(item);
		}
	}

	private void updateToolbarVisibility(final IToolBarContributionItem key, final ToolBarManager toolBarManager) {
		//No need of setting the last container to be invisible
		//It doesn't participate in reconnection process (one in the middle and first one are of importance)
		if (lastKey != null && lastKey.equals(key)) {
			return;
		}
		ToolBar toolBar = toolBarManager.getControl();
		Composite toolbarContainer = toolbarComposites.get(key);

		if (toolBar.getItemCount() == 0 || allInvisible(toolBarManager)) {
			toolbarContainer.setVisible(false);

			IToolBarContributionItem nextKey = getNextKey(key);
			IToolBarContributionItem previousKey = getPreviousKey(key);
			if (nextKey != null && previousKey != null) {
				reconnectOmittingMiddleBlock(previousKey, key, nextKey);
			}
		} else {
			toolbarContainer.setVisible(true);

			ReverseConnector reverseConnector = oldConnectionsMap.get(key);
			if (reverseConnector != null) {
				reverseConnector.restoreInitialConnection();
				oldConnectionsMap.remove(key);
			}
		}
	}

	/**
	 * Connects Next Block to the Previous block omitting the block in between.
	 * Stores the reverse procedure which will be triggered when the MiddleBlock will be visible again.
	 */
	private void reconnectOmittingMiddleBlock(final IToolBarContributionItem previousKey,
		final IToolBarContributionItem middleKey, final IToolBarContributionItem nextKey) {

		Composite nextComposite = toolbarComposites.get(nextKey);
		Composite previousComposite = toolbarComposites.get(previousKey);

		FormAttachment oldConnection = null;
		Object layoutData = nextComposite.getLayoutData();
		if (layoutData instanceof FormData) {
			FormData formData = (FormData) layoutData;

			oldConnection = formData.left;
			formData.left = new FormAttachment(previousComposite);
		}

		if (oldConnection != null) {
			Composite middleComposite = toolbarComposites.get(middleKey);
			oldConnectionsMap.put(middleKey, new ReverseConnector(nextComposite, middleComposite));
		}
	}

	/**
	 * This class saves two composites which will be reattached once restoreInitialConnection is called.
	 * CompositeToConnect will be connected to the current one using it's FormData
	 */
	private class ReverseConnector {

		private final Composite composite;
		private final Composite compositeToConnect;

		ReverseConnector(final Composite composite, final Composite compositeToConnect) {

			this.composite = composite;
			this.compositeToConnect = compositeToConnect;
		}

		void restoreInitialConnection() {
			Object layoutData = composite.getLayoutData();
			FormData formData = (FormData) layoutData;

			formData.left = new FormAttachment(compositeToConnect);
		}
	}

	private IToolBarContributionItem getNextKey(final IToolBarContributionItem key) {
		int next = keys.indexOf(key);
		next++;
		if (keys.size() == next) {
			return null;
		}
		return keys.get(next);
	}

	private IToolBarContributionItem getPreviousKey(final IToolBarContributionItem key) {
		int next = keys.indexOf(key);
		next--;
		if (next < 0) {
			return null;
		}
		return keys.get(next);
	}

	@Override
	public List<ToolItem> getPerspectiveToolItems() {
		ToolItem[] toolItems = perspectiveToolbarManager.getControl().getItems();
		return Arrays.asList(toolItems);
	}

	@Override
	public String getPreferredPerspectiveId() {
		String commandId = getFirstPerspectiveCommandId();
		if (commandId != null) {
			return PerspectiveManager.getDefault().getPerspectiveByCommandId(commandId);
		}
		return null;
	}

	private String getFirstPerspectiveCommandId() {
		if (perspectiveToolbarManager != null) {
			IContributionItem[] items = perspectiveToolbarManager.getItems();
			for (IContributionItem item : items) {
				if (item.isVisible()) {
					CommandContributionItem command = (CommandContributionItem) item;
					return command.getCommand().getId();
				}
			}
		}
		return null;
	}
}
