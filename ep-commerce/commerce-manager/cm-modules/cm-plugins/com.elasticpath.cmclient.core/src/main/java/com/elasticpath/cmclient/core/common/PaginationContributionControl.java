/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.common;

import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.helpers.PaginationSupport;
import com.elasticpath.cmclient.core.helpers.TestIdUtil;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;

/**
 * This is the pagination control class.
 */
public class PaginationContributionControl extends AbstractPaginationControl {

	private final IToolBarManager toolBarManager;

	private PaginationControlResultFirstAction firstAction;
	private PaginationControlResultNextAction nextAction;
	private PaginationControlResultPreviousAction previousAction;
	private PaginationControlResultLastAction lastAction;

	private PaginationControlLabelContribution pageLabelContribution;
	private PaginationControlLabelContribution ofPageLabelContribution;
	private PaginationControlLabelContribution searchResultSummaryLabelContribution;

	private PaginationControlTextContribution pageTextField;

	/**
	 * The constructor.
	 *
	 * @param toolBarManager    the tool bar manager
	 * @param paginationSupport the pagination support container
	 */
	public PaginationContributionControl(final IToolBarManager toolBarManager, final PaginationSupport paginationSupport) {
		super(paginationSupport);
		this.toolBarManager = toolBarManager;
	}

	/**
	 * create view part control.
	 */
	@Override
	public void createViewPartControl() {
		createActions();
		addPaginationItems();

		Display.getCurrent().asyncExec(() -> {
			TestIdUtil epTestUtil = EPTestUtilFactory.getInstance().getTestIdUtil();
			if (toolBarManager instanceof ToolBarManager) {
				ToolBar toolBar = ((ToolBarManager) toolBarManager).getControl();
				epTestUtil.setId(toolBar, TestIdUtil.NO_ID_TO_BE_IMPLEMENTED);
			}
		});
	}

	private void addPaginationItems() {
		Separator paginationSeparator = new Separator("navigation"); //$NON-NLS-1$
		Separator searchResultsSeparator = new Separator("searchResults"); //$NON-NLS-1$

		toolBarManager.add(paginationSeparator);
		toolBarManager.add(searchResultsSeparator);

		// Backward Navigation buttons
		toolBarManager.appendToGroup(paginationSeparator.getGroupName(), firstAction);
		toolBarManager.appendToGroup(paginationSeparator.getGroupName(), previousAction);

		// Status label
		pageLabelContribution = new PaginationControlLabelContribution("com.elasticpath.cmclient.searchResultsNavigationLabel", ""); //$NON-NLS-2$
		toolBarManager.appendToGroup(paginationSeparator.getGroupName(), pageLabelContribution);

		pageTextField = new PaginationControlTextContribution("CurrentPageTextFieldContribution", this); //$NON-NLS-1$
		toolBarManager.appendToGroup(paginationSeparator.getGroupName(), pageTextField);

		ofPageLabelContribution = new PaginationControlLabelContribution("OfPageLabelContribution", ""); //$NON-NLS-2$
		toolBarManager.appendToGroup(paginationSeparator.getGroupName(), ofPageLabelContribution);

		// Forward Navigation buttons
		toolBarManager.appendToGroup(paginationSeparator.getGroupName(), nextAction);
		toolBarManager.appendToGroup(paginationSeparator.getGroupName(), lastAction);

		//Search results label
		searchResultSummaryLabelContribution =
				new PaginationControlLabelContribution("com.elasticpath.cmclient.searchResultsSummaryNavigationLabel", ""); //$NON-NLS-1$
		toolBarManager.appendToGroup(searchResultsSeparator.getGroupName(), searchResultSummaryLabelContribution);

		toolBarManager.update(true);
		pageLabelContribution.setText(getPageLabelText());
	}

	/**
	 * create all page navigation controls.
	 */
	private void createActions() {
		firstAction = new PaginationControlResultFirstAction(getPaginationSupport());
		nextAction = new PaginationControlResultNextAction(getPaginationSupport());
		previousAction = new PaginationControlResultPreviousAction(getPaginationSupport());
		lastAction = new PaginationControlResultLastAction(getPaginationSupport());

		firstAction.setEnabled(false);
		previousAction.setEnabled(false);
		nextAction.setEnabled(false);
		lastAction.setEnabled(false);
	}

	@Override
	protected void updateLayout() {
		//do nothing
	}

	@Override
	protected void setFirstButtonEnabled(final boolean firstButtonEnabled) {
		firstAction.setEnabled(firstButtonEnabled);
	}

	@Override
	protected void setLastButtonEnabled(final boolean lastButtonEnabled) {
		lastAction.setEnabled(lastButtonEnabled);
	}

	@Override
	protected void setPreviousButtonEnabled(final boolean previousButtonEnabled) {
		previousAction.setEnabled(previousButtonEnabled);
	}

	@Override
	protected void setNextButtonEnabled(final boolean nextButtonEnabled) {
		nextAction.setEnabled(nextButtonEnabled);
	}

	@Override
	protected void setPageTextValue(final String pageTextField) {
		this.pageTextField.setText(pageTextField);
	}

	@Override
	protected void setOfPageLabelTextValue(final String ofPageLabelText) {
		ofPageLabelContribution.setText(ofPageLabelText);
	}

	@Override
	protected void setSearchResultSummaryLabelTextValue(final String searchResultSummaryLabelText) {
		searchResultSummaryLabelContribution.setText(searchResultSummaryLabelText);
	}

	@Override
	protected void setOfPageLabelEnabled(final boolean ofPageLabelEnabled) {
		ofPageLabelContribution.setEnabled(ofPageLabelEnabled);
	}

	@Override
	protected void setPageLabelEnabled(final boolean pageLabelEnabled) {
		pageLabelContribution.setEnabled(pageLabelEnabled);
	}

	@Override
	protected void setPageTextFieldEnabled(final boolean pageTextFieldEnabled) {
		pageTextField.setEnabled(pageTextFieldEnabled);
	}

	@Override
	protected void setSearchResultSummaryLabelEnabled(final boolean searchResultSummaryLabelEnabled) {
		searchResultSummaryLabelContribution.setEnabled(searchResultSummaryLabelEnabled);
	}

}
