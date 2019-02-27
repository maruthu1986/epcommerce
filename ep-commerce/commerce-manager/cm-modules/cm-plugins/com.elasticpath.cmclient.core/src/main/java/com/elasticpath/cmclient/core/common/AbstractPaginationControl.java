/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.common;

import org.apache.log4j.Logger;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.event.NavigationEvent.NavigationType;
import com.elasticpath.cmclient.core.helpers.PaginationSupport;
import com.elasticpath.cmclient.core.util.PageUtil;

/**
 * This is the pagination control class.
 * <p>
 * It is responsible for creating managing all the pagination widgets.
 */
public abstract class AbstractPaginationControl implements IPaginationControl {

	/**
	 * The logger.
	 * */
	private static final Logger LOG = Logger.getLogger(AbstractPaginationControl.class);

	private final PaginationSupport paginationSupport;

	/**
	 * The constructor.
	 *
	 * @param paginationSupport the pagination support container
	 */
	public AbstractPaginationControl(final PaginationSupport paginationSupport) {
		this.paginationSupport = paginationSupport;
	}

	/**
	 * update the navigation components.
	 */
	@Override
	public void updateNavigationComponents() {
		if (paginationSupport.getResultsCount() <= 0) {
			setFirstButtonEnabled(false);
			setPreviousButtonEnabled(false);
			setNextButtonEnabled(false);
			setLastButtonEnabled(false);
		} else {
			boolean isFirstPreviousEnabled = paginationSupport.getResultsStartIndex() > 0;
			setFirstButtonEnabled(isFirstPreviousEnabled);
			setPreviousButtonEnabled(isFirstPreviousEnabled);

			boolean isNextLastEnabled = paginationSupport.getResultsCount() > paginationSupport.getResultsStartIndex()
					+ paginationSupport.getResultsPaging();
			setNextButtonEnabled(isNextLastEnabled);
			setLastButtonEnabled(isNextLastEnabled);
		}

		boolean hasResults = paginationSupport.getResultsCount() > 0;
		setSearchResultSummaryLabelEnabled(hasResults);
		setSearchResultSummaryLabelTextValue(getSearchResultsSummaryLabelText());
		setOfPageLabelEnabled(hasResults);
		setPageLabelEnabled(hasResults);
		setPageTextFieldEnabled(hasResults);
		setOfPageLabelTextValue(this.getOfPageLabelText());
		String pageTxtValue = hasResults ? (getCurrentPage() + " ") : "0";
		setPageTextValue(pageTxtValue);

		updateLayout();
	}

	/**
	 * Updates the layout in case there are size changes to controls.
	 * */
	protected abstract void updateLayout();

	/**
	 * Get the search result summary label text.
	 *
	 * @return "Results {0} - {1} of {2}" when english is used
	 */
	protected String getSearchResultsSummaryLabelText() {
		return
			NLS.bind(CoreMessages.get().navigation_Search_Results,
			new Object[]{getPageFirstItemPosition(), getPageLastItemPosition(), getTotalNumberOfSearchResults()});
	}

	/**
	 * Get the page label text.
	 *
	 * @return "Page" when english is used
	 */
	protected String getPageLabelText() {
		return
			NLS.bind(CoreMessages.get().navigation_Page,
			null);
	}

	/**
	 * Get the of page label text.
	 *
	 * @return "of {0}" when english is used
	 */
	protected String getOfPageLabelText() {
		return
			NLS.bind(CoreMessages.get().navigation_Of_Total_Page,
			new String[]{
				String.valueOf(getTotalPage())});
	}

	/**
	 * Get the result count.
	 *
	 * @return result count
	 */
	protected int getTotalNumberOfSearchResults() {
		return paginationSupport.getResultsCount();
	}

	/**
	 * Get the page last item position.
	 *
	 * @return the page last item position
	 */
	protected int getPageLastItemPosition() {
		int startIndex = paginationSupport.getResultsStartIndex();
		int endIndex = startIndex + paginationSupport.getResultsPaging();
		int totalNumberOfItens = paginationSupport.getResultsCount();
		if (endIndex > totalNumberOfItens) {
			endIndex = totalNumberOfItens;
		}
		return endIndex;
	}

	/**
	 * Get the page first item position.
	 *
	 * @return the page first item position.
	 */
	protected int getPageFirstItemPosition() {
		int startIndex = paginationSupport.getResultsStartIndex();
		int totalNumberOfItens = paginationSupport.getResultsCount();
		if (totalNumberOfItens > 0) {
			startIndex++;
		}
		return startIndex;
	}

	@Override
	public int getCurrentPage() {
		return PageUtil.getPage(paginationSupport.getResultsStartIndex() + 1, paginationSupport.getResultsPaging());
	}

	/**
	 * Get the total page.
	 *
	 * @return the total page
	 */
	protected int getTotalPage() {
		return PageUtil.getPage(paginationSupport.getResultsCount(), paginationSupport.getResultsPaging());
	}

	@Override
	public int getValidPage(final int pageNumber) {
		int toPage = pageNumber;
		if (pageNumber < 1) {
			toPage = 1;
		} else if (pageNumber > getTotalPage()) {
			toPage = getTotalPage();
		}
		return toPage;
	}

	@Override
	public void navigateTo(final int toPage) {
		LOG.debug("Firing Specific page navigation event."); //$NON-NLS-1$
		paginationSupport.fireNavigationEvent(NavigationType.TO, new Integer[]{Integer.valueOf(toPage)});
	}

	protected PaginationSupport getPaginationSupport() {
		return paginationSupport;
	}

	/**
	 * Sets the first button enabled/disabled.
	 *
	 * @param firstButtonEnabled the first button enabled/disabled
	 */
	protected abstract void setFirstButtonEnabled(boolean firstButtonEnabled);

	/**
	 * Sets the last button enabled/disabled.
	 *
	 * @param lastButtonEnabled the last button enabled/disabled
	 */
	protected abstract void setLastButtonEnabled(boolean lastButtonEnabled);

	/**
	 * Sets the previous button enabled/disabled.
	 *
	 * @param previousButtonEnabled the previous button enabled/disabled
	 */
	protected abstract void setPreviousButtonEnabled(boolean previousButtonEnabled);

	/**
	 * Sets the next button enabled/disabled.
	 *
	 * @param nextButtonEnabled the next button enabled/disabled
	 */
	protected abstract void setNextButtonEnabled(boolean nextButtonEnabled);

	/**
	 * Sets the page text.
	 *
	 * @param pageTextField the page text
	 */
	protected abstract void setPageTextValue(String pageTextField);

	/**
	 * Sets the of page label text.
	 *
	 * @param ofPageLabelText the of label text
	 */
	protected abstract void setOfPageLabelTextValue(String ofPageLabelText);

	/**
	 * Sets the searchResultSummaryLabel text.
	 *
	 * @param searchResultSummaryLabelText the searchResultSummaryLabel text
	 */
	protected abstract void setSearchResultSummaryLabelTextValue(String searchResultSummaryLabelText);

	/**
	 * Sets the ofPageLabel enabled/disabled.
	 *
	 * @param ofPageLabelEnabled the ofPageLabel enabled/disabled
	 */
	protected abstract void setOfPageLabelEnabled(boolean ofPageLabelEnabled);

	/**
	 * Sets the pageLabel enabled/disabled.
	 *
	 * @param pageLabelEnabled the pageLabel enabled/disabled
	 */
	protected abstract void setPageLabelEnabled(boolean pageLabelEnabled);

	/**
	 * Sets the pageTextFieldEnabled enabled/disabled.
	 *
	 * @param pageTextFieldEnabled the pageTextField enabled/disabled
	 */
	protected abstract void setPageTextFieldEnabled(boolean pageTextFieldEnabled);

	/**
	 * Sets the searchResultSummaryLabel enabled/disabled.
	 *
	 * @param searchResultSummaryLabelEnabled the searchResultSummaryLabel enabled/disabled
	 */
	protected abstract void setSearchResultSummaryLabelEnabled(boolean searchResultSummaryLabelEnabled);
}
