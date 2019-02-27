/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.presentation;

import java.util.List;

import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Builds header composite and allocates the space for the right toolbar and the coolbar itself.
 * This works around the Workbench truncation bug for the right hand side toolbar.
 */
public class HeaderBuilder {

	private static final String HEADER_COOLBAR_COMPOSITE = "Header_CoolbarComposite"; //non-nls-id
	private static final int TOP_USER_OFFSET = 8;
	private static final int BOTTOM_USER_OFFSET = 5;
	private static final int MAX_POSITION_PERCENT = 100; //100% of the parent composite
	private static final String HEADER_USER_COMPOSITE = "Header_UserComposite";  //non-nls-id
	private final Composite coolbar;
	private final Composite userComposite;

	/**
	 * Constructor.
	 *
	 * @param parent composite from which the header will be built
	 */
	public HeaderBuilder(final Composite parent) {
		Composite header = new Composite(parent, SWT.NONE);
		header.setLayout(new FormLayout());

		coolbar = new Composite(header, SWT.NONE);
		coolbar.setLayout(new FormLayout());
		EPTestUtilFactory.getInstance().getTestIdUtil().setId(coolbar, HEADER_COOLBAR_COMPOSITE);
		FormData fdCoolbar = new FormData();
		coolbar.setLayoutData(fdCoolbar);
		fdCoolbar.top = new FormAttachment(0);
		fdCoolbar.left = new FormAttachment(0);

		fdCoolbar.bottom = new FormAttachment(MAX_POSITION_PERCENT);

		userComposite = new Composite(header, SWT.NONE);
		userComposite.setLayout(new FormLayout());
		EPTestUtilFactory.getInstance().getTestIdUtil().setId(userComposite, HEADER_USER_COMPOSITE);
		FormData fdUser = new FormData();
		userComposite.setLayoutData(fdUser);
		fdUser.top = new FormAttachment(0, TOP_USER_OFFSET);
		fdUser.bottom = new FormAttachment(MAX_POSITION_PERCENT, -BOTTOM_USER_OFFSET);
		fdUser.right = new FormAttachment(MAX_POSITION_PERCENT, -BOTTOM_USER_OFFSET);

		//Attach right side of the coolbar composite to the user composite
		fdCoolbar.right = new FormAttachment(userComposite);

		createUserMenu();
	}

	private void createUserMenu() {
		ContributionLoader contributionLoader = new ContributionLoader();
		List<IContributionItem> items = contributionLoader.getItems(EpPresentationConstants.TOOLBAR_RIGHT);

		ToolBar toolBar = new ToolBar(userComposite, SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		for (IContributionItem item : items) {
			toolBarManager.add(item);
		}
		toolBarManager.update(true);
	}

	/**
	 * Getter for the coolbar composite.
	 *
	 * @return composite for the coolbar
	 */
	public Composite getCoolbarComposite() {
		return coolbar;
	}
}
