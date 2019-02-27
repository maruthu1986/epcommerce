/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;

/**
 * Abstracts the <code>CTabFolder</code> of Eclipse.
 */
public interface IEpTabFolder {

	/**
	 * Creates new tab item.
	 * 
	 * @param tabName the name of the tab
	 * @param image the image to be displayed next to the name
	 * @param tabIndex sets the number of the tab. Used for selection.
	 * @return EP layout composite
	 */
	IEpLayoutComposite addTabItem(String tabName, Image image, int tabIndex);

	/**
	 * Adds a button or another element to the tool bar manager of the tab folder.<br>
	 * For creating a tool bar item use the Action(String text, int style) where style is one of the constants mentioned below.
	 * 
	 * @param toolbarAction IAction implementation
	 * @see org.eclipse.jface.action.Action
	 * @see IAction#AS_PUSH_BUTTON
	 * @see IAction#AS_DROP_DOWN_MENU
	 * @see IAction#AS_CHECK_BOX
	 * @see IAction#AS_RADIO_BUTTON
	 */
	void addToolBarItem(IAction toolbarAction);

	/**
	 * Selects the tab with the provided index.
	 * 
	 * @param tabIndex the tab index to be visible
	 */
	void setSelection(int tabIndex);

	/**
	 * Adds new tab to the tab folder with a vertical scroll.
	 * 
	 * @param tabName the name of the tab
	 * @param image the image to be displayed for the tab
	 * @param tabIndex the unique tab index
	 * @param numColumns columns used for the layout
	 * @param equalWidthColumns true if columns should be equal width
	 * @return EP layout composite
	 */
	IEpLayoutComposite addTabItem(String tabName, Image image, int tabIndex, int numColumns, boolean equalWidthColumns);

	/**
	 * Adds new tab to the tab folder.
	 * 
	 * @param tabName the name of the tab
	 * @param image the image to be displayed for the tab
	 * @param tabIndex the unique tab index
	 * @param numColumns columns used for the layout
	 * @param equalWidthColumns true if columns should be equal width
	 * @param hasVerticalScroll true if this tab should have vertical scroll 
	 * @return EP layout composite
	 */
	IEpLayoutComposite addTabItem(String tabName, Image image, int tabIndex, int numColumns, boolean equalWidthColumns, boolean hasVerticalScroll);

	
	/**
	 * Gets the original Eclipse tab folder object.
	 * 
	 * @return <code>CTabFolder</code>
	 * @see CTabFolder
	 */
	CTabFolder getSwtTabFolder();

	/**
	 * Returns the selected tab index.
	 * 
	 * @return the tab index the tab was registered in the tab folder
	 */
	int getSelectedTabIndex();

	/**
	 * Returns the original CTabItem object of the selected tab.
	 * 
	 * @return CTabItem
	 */
	CTabItem getSelectedTab();

	/**
	 * Returns the tool bar manager associated with the tab folder.
	 * 
	 * @return IToolbarManager
	 * @see IToolBarManager
	 */
	IToolBarManager getToolBarManager();
	
	/**
	 * Refresh the layout.
	 */
	void layout();

	/**
	 * scroll to its bottom.
	 */
	void scrollToBottom();

}
