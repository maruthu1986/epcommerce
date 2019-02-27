/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;

/**
 * Implements the IEpTabFolder abstracting the Eclipse CTabFolder UI component.
 */
public class EpTabFolder implements IEpTabFolder {

	private static final int TAB_EXTRA_HEIGHT = 20;

	private static final int PERCENTAGE = 100;

	/**
	 * Initial size of the map used for storing the tabs.
	 */
	private static final int INITIAL_MAP_SIZE = 5;

	private final CTabFolder tabFolder;

	private final AbstractEpLayoutComposite epLayoutComposite;

	private final Map<Integer, CTabItem> tabsMap;

	private final Map<Integer, Integer> indexMap;

	private ToolBarManager toolbarManager;

	private int nextTabIndex;
	
	private final List<ScrolledComposite> scrollComposites = new ArrayList<ScrolledComposite>();

	/**
	 * Constructs new EP tab folder.
	 * 
	 * @param epLayoutComposite the parent EP composite
	 * @param data EP layout data
	 */
	public EpTabFolder(final AbstractEpLayoutComposite epLayoutComposite, final IEpLayoutData data) {
		this.epLayoutComposite = epLayoutComposite;
		this.tabFolder = new CTabFolder(epLayoutComposite.getSwtComposite(), SWT.BORDER | SWT.V_SCROLL);
		// the next line is required so that a bug under MacOS is avoided.
		// the height must be enough for the images added to the folder tab to be visible.
		// it seems that the max contribution item's height is not calculated properly.
		// it seems that the next line is still needed after conversion to RAP
		this.tabFolder.setTabHeight(this.tabFolder.getFont().getFontData()[0].getHeight() + TAB_EXTRA_HEIGHT);

		this.adaptTabFolder(epLayoutComposite.getFormToolkit());
		this.tabFolder.setLayout(epLayoutComposite.newLayoutInstance(1, false));
		this.tabFolder.setLayoutData(epLayoutComposite.adaptEpLayoutData(data));
		this.tabsMap = new HashMap<Integer, CTabItem>(INITIAL_MAP_SIZE);
		this.indexMap = new HashMap<Integer, Integer>(INITIAL_MAP_SIZE);
	}

	private void adaptTabFolder(final FormToolkit toolkit) {
		toolkit.adapt(this.tabFolder);
		toolkit.getColors().initializeSectionToolBarColors();
		final Color selectedColor = toolkit.getColors().getColor(IFormColors.TB_BG);
		this.tabFolder.setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() }, new int[] { PERCENTAGE }, true);

		// this.tabFolder.setUnselectedImageVisible(false);

		final ToolBar toolbar = new ToolBar(this.tabFolder, SWT.HORIZONTAL | SWT.FLAT);
		toolbar.setBackground(toolkit.getColors().getBackground());

		this.toolbarManager = new ToolBarManager(toolbar);

		final int toolbarHeight = toolbar.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		this.tabFolder.setTabHeight(Math.max(toolbarHeight, this.tabFolder.getTabHeight()));

		this.tabFolder.setTopRight(toolbar);
	}

	/**
	 * Adds new tab item.
	 * 
	 * @param tabName tab's name
	 * @param image tab's image
	 * @param tabIndex index of tab > 0 && unique
	 * @return EP layout composite
	 */
	public IEpLayoutComposite addTabItem(final String tabName, final Image image, final int tabIndex) {
		return this.addTabItem(tabName, image, tabIndex, 1, false);
	}

	/**
	 * Selects a tab by its unique index.
	 * 
	 * @param tabIndex tab's index
	 */
	public void setSelection(final int tabIndex) {
		final CTabItem tabItem = this.tabsMap.get(tabIndex);
		if (tabItem != null) {
			this.tabFolder.setSelection(tabItem);
		}
	}

	@Override
	public IEpLayoutComposite addTabItem(final String tabName, final Image image, final int tabIndex, final int numColumns,
			final boolean equalWidthColumns, final boolean hasVerticalScroll) {
		final CTabItem ordersTabItem = new CTabItem(this.tabFolder, SWT.NONE, this.nextTabIndex);
		ordersTabItem.setImage(image);
		ordersTabItem.setText(tabName);

		// only vertical scroll needed
		
		int style;
		if (hasVerticalScroll) {
			style = SWT.V_SCROLL;
		} else {
			style = SWT.NONE;
		}
		
		final ScrolledComposite scrollComposite = new ScrolledComposite(this.tabFolder, style);
		scrollComposites.add(scrollComposite);
		
		// expand both directions
		scrollComposite.setExpandHorizontal(true);
		scrollComposite.setExpandVertical(true);

		final AbstractEpLayoutComposite epComposite = (AbstractEpLayoutComposite) this.epLayoutComposite.newCompositeInstance(scrollComposite,
				numColumns, equalWidthColumns);
		final Composite swtEpComposite = epComposite.getSwtComposite();
		
		EpEventService.getInstance().addResizeListener(new EpResizeListener() {
			public void resize() {
				final Point size = swtEpComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				scrollComposite.setMinHeight(size.y);
			}
		});
		// adapts continuously to the size of the child composite because
		// the components of the child composite have not been added yet and we do not know the final height of the composite
		scrollComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent event) {
				final Point size = swtEpComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				scrollComposite.setMinHeight(size.y);
			}
		});
		// sets the content of the scroll composite to be newly created EP composite
		scrollComposite.setContent(epComposite.getSwtComposite());
		// sets the control to be the scroll composite
		ordersTabItem.setControl(scrollComposite);
		this.indexMap.put(tabIndex, this.nextTabIndex);
		this.tabsMap.put(this.nextTabIndex++, ordersTabItem);
		return epComposite;
	}
	
	/**
	 * Adds new tab item.
	 * 
	 * @param tabName tab's name
	 * @param image tab's image
	 * @param tabIndex index of tab >= 0 && unique
	 * @param numColumns number of columns to be available in the returned EP layout composite
	 * @param equalWidthColumns true should columns be with equal widths
	 * @return EP layout composite
	 */
	public IEpLayoutComposite addTabItem(final String tabName, final Image image, final int tabIndex, final int numColumns,
			final boolean equalWidthColumns) {
		return addTabItem(tabName, image, tabIndex, numColumns, equalWidthColumns, true);
	}

	/**
	 * Gets the original Eclipse tab folder object.
	 * 
	 * @return <code>CTabFolder</code>
	 * @see CTabFolder
	 */
	public CTabFolder getSwtTabFolder() {
		return this.tabFolder;
	}

	/**
	 * Returns the selected tab index.
	 * Or Integer.MIN_VALUE if index map is empty.
	 * 
	 * @return the tab index the tab was registered in the tab folder
	 */
	public int getSelectedTabIndex() {
		if (this.indexMap.isEmpty()) {
			return Integer.MIN_VALUE;
		}
		return this.indexMap.get(this.tabFolder.getSelectionIndex());
	}

	/**
	 * Returns the original CTabItem object of the selected tab.
	 * 
	 * @return CTabItem
	 */
	public CTabItem getSelectedTab() {
		return this.tabFolder.getSelection();
	}

	/**
	 * Adds an item to the tool bar.
	 * 
	 * @param toolbarAction an action implementation
	 */
	public void addToolBarItem(final IAction toolbarAction) {
		this.toolbarManager.add(toolbarAction);
		this.toolbarManager.update(false);
	}

	/**
	 * Returns the tool bar manager.
	 * 
	 * @return IToolBarManager
	 */
	public IToolBarManager getToolBarManager() {
		return this.toolbarManager;
	}
	
	@Override
	public void layout() {
		//force the scroll bar to refresh
		getSelectedScrollComposite().getVerticalBar().setVisible(false);
		getSelectedScrollComposite().getVerticalBar().setVisible(true);		
		getSelectedScrollComposite().layout(true);
		
		//force the swtTabFolder to refresh
		getSwtTabFolder().setBorderVisible(!getSwtTabFolder().getBorderVisible());
		getSwtTabFolder().layout();
		getSwtTabFolder().setBorderVisible(!getSwtTabFolder().getBorderVisible());
		getSwtTabFolder().layout();
	}

	@Override
	public void scrollToBottom() {
		getSelectedScrollComposite().getVerticalBar().setSelection(
				getSelectedScrollComposite().getVerticalBar().getMaximum());
		layout();
	}

	private ScrolledComposite getSelectedScrollComposite() {
		return scrollComposites.get(this.getSelectedTabIndex());
	}
}


