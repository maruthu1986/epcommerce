/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.eventlistener.DigitVerifyListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.Paginator;

/**
 * This component constructs a grid of buttons to allow for navigation and display of information.
 * @param <T> the class this pagination works on
 */
public abstract class AbstractPaginationControl<T> {

	private static final int COLUMNS = 8;

	private NavigationButton firstPageButton;

	private NavigationButton previousPageButton;

	private NavigationButton nextPageButton;

	private NavigationButton lastPageButton;

	private Label pageInfoLabel;

	private Label resultsLabel;

	private final Paginator<T> paginator;

	private final IEpLayoutComposite parentComposite;

	private final IEpLayoutData layoutData;

	private EpState epState;

	private Text currentPage;

	private Label pageLabel;


	/**
	 *
	 * @param parentComposite the parent composite
	 * @param layoutData the layout data
	 * @param epState the state of the control
	 * @param paginator the paginator
	 */
	public AbstractPaginationControl(final IEpLayoutComposite parentComposite,
			final IEpLayoutData layoutData, final EpState epState, final Paginator<T> paginator) {
		this.parentComposite = parentComposite;
		this.layoutData = layoutData;
		this.epState = epState;
		this.paginator = paginator;
	}

	/**
	 *
	 */
	public void createControls() {
		IEpLayoutComposite mainComposite = parentComposite.addGridLayoutComposite(COLUMNS, false, layoutData);
		firstPageButton = new NavigationButton(
				mainComposite.addHyperLinkImage(null, EpState.EDITABLE, null),
				CoreImageRegistry.IMAGE_RESULTSET_FIRST,
				CoreImageRegistry.IMAGE_RESULTSET_FIRST
		);
		firstPageButton.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(final HyperlinkEvent event) {
				paginator.first();
				update(paginator.getCurrentPage());
				updateControls();
			}
		});
		previousPageButton = new NavigationButton(mainComposite.addHyperLinkImage(null, EpState.EDITABLE, null),
				CoreImageRegistry.IMAGE_RESULTSET_PREVIOUS,
				CoreImageRegistry.IMAGE_RESULTSET_PREVIOUS);
		previousPageButton.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent event) {
				Page<T> prevPage = paginator.previous();
				if (prevPage != null) {
					update(paginator.getCurrentPage());
					updateControls();
				}
			}
		});
		pageLabel = mainComposite.addLabel(
			NLS.bind(CoreMessages.get().navigation_Page,
			null),
				mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER));

		currentPage = mainComposite.addTextField(EpState.EDITABLE,
				mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER));
		setTextToCurrentPage(1);
		currentPage.addVerifyListener(new DigitVerifyListener());
		currentPage.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				int pageNumberInput = NumberUtils.toInt(currentPage.getText().trim(), 0);
				update(paginator.getPage(pageNumberInput));
				updateControls();
			}
			@Override
			public void widgetSelected(final SelectionEvent event) {
				// nothing to do
			}
		});
		currentPage.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent event) {
				// nothing to do
			}
			@Override
			public void focusLost(final FocusEvent event) {
				setTextToCurrentPage(getCurrentPage().getPageNumber());
			}

		});
		pageInfoLabel = mainComposite.addLabel(constructPageLabelText(),
				mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER));
		nextPageButton = new NavigationButton(mainComposite.addHyperLinkImage(null, EpState.EDITABLE, null),
				CoreImageRegistry.IMAGE_RESULTSET_NEXT,
				CoreImageRegistry.IMAGE_RESULTSET_NEXT);
		nextPageButton.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent event) {
				Page<T> nextPage = paginator.next();
				if (nextPage != null) {
					update(paginator.getCurrentPage());
					updateControls();
				}
			}
		});
		lastPageButton = new NavigationButton(mainComposite.addHyperLinkImage(null, EpState.EDITABLE, null),
				CoreImageRegistry.IMAGE_RESULTSET_LAST,
				CoreImageRegistry.IMAGE_RESULTSET_LAST);
		lastPageButton.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent event) {
				paginator.last();
				update(paginator.getCurrentPage());
				updateControls();
			}
		});
		resultsLabel = mainComposite.addLabel(
			NLS.bind(CoreMessages.get().navigation_Search_Results,
			new Object[]{"", "", ""}), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER));

		// initialise the buttons state
		updateControls();
	}

	private void setTextToCurrentPage(final int pageNumber) {
		//add some space on the end so that the text box will not be too small
		if (!currentPage.isDisposed()) {
			currentPage.setText(pageNumber + "   "); //$NON-NLS-1$
		}
	}

	/**
	 *
	 * @param page the page to be used.
	 */
	protected void setPage(final Page<T> page) {
		updateControls();
	}

	/**
	 * Update the UI controls. This is a callback method on any
	 * @param newPage the new available page.
	 */
	public abstract void update(Page<T> newPage);

	/**
	 *
	 * @return The results string
	 */
	private String constructResultLabelText() {
		return
			NLS.bind(CoreMessages.get().navigation_Search_Results,
			new Object[]{getFirstItemPosition(), getLastItemPosition(), getTotalNumberOfSearchResults()});
	}

	private int getFirstItemPosition() {
		return getPaginator().getCurrentPage().getPageStartIndex();
	}

	private int getLastItemPosition() {
		int lastPosition = getFirstItemPosition() + getPaginator().getPageSize() - 1;

		if (lastPosition > getTotalNumberOfSearchResults()) {
			lastPosition = getTotalNumberOfSearchResults();
		}

		return lastPosition;
	}

	private int getTotalNumberOfSearchResults() {
		return 	(int) this.getPaginator().getTotalItems();
	}

	/**
	 *
	 * @return
	 */
	private String constructPageLabelText() {
		long totalPages = 0;
		if (paginator.getCurrentPage() != null) {
			totalPages = paginator.getCurrentPage().getTotalPages();
		}
		return "  " + //$NON-NLS-1$

				NLS.bind(CoreMessages.get().navigation_Of_Total_Page,
				new Object[]{
				totalPages
			}) + "  "; //$NON-NLS-1$ // adds extra spaces at the end to ensure enough space for the label to expand
	}


	/**
	 *
	 */
	protected void updateControls() {
		boolean isEnabled = epState == EpState.EDITABLE;
		
		if (!pageInfoLabel.isDisposed()) {
			pageInfoLabel.setText(constructPageLabelText());
		}
		
		if (!resultsLabel.isDisposed()) {
			resultsLabel.setText(constructResultLabelText());
		}

		boolean hasPreviousPage = paginator.getCurrentPage().getPageNumber() > 1;
		
		previousPageButton.setEnabled(isEnabled && hasPreviousPage);
		firstPageButton.setEnabled(isEnabled && hasPreviousPage);
		
		boolean hasNextPage = paginator.getCurrentPage().getPageNumber() < paginator.getCurrentPage().getTotalPages();
		
		nextPageButton.setEnabled(isEnabled && hasNextPage);
		lastPageButton.setEnabled(isEnabled && hasNextPage);
		
		setTextToCurrentPage(paginator.getCurrentPage().getPageNumber());
	}

	/**
	 *
	 * @param state the new state to set
	 */
	public void changeState(final EpState state) {
		this.epState = state;
		updateControls();
	}
	
	/**
	 * Sets the control (in)visible.
	 * 
	 * @param value the value
	 */
	public void setVisible(final boolean value) {
		resultsLabel.setVisible(value);
		pageInfoLabel.setVisible(value);
		pageLabel.setVisible(value);
		previousPageButton.setVisible(value);
		firstPageButton.setVisible(value);
		nextPageButton.setVisible(value);
		lastPageButton.setVisible(value);
		currentPage.setVisible(value);
	}

	/**
	 *
	 */
	public void populateControls() {
		update(paginator.getCurrentPage());
	}
	
	/**
	 * Gets the current page.
	 *  
	 * @return the currently selected page
	 */
	public Page<T> getCurrentPage() {
		return paginator.getCurrentPage();
	}

	/**
	 * Disposes this control.
	 */
	public void dispose() {
		// nothing to do by default
	}
	
	/**
	 * 
	 * @return The paginator.
	 */
	public Paginator<T> getPaginator() {
		return paginator;
	}
	
	/**
	 * Navigation button.
	 */
	protected class NavigationButton {
		
		private final ImageHyperlink button;
		private final ImageDescriptor enabledImage;
		private final ImageDescriptor disabledImage;

		/**
		 * Constructs a new navigation button.
		 * 
		 * @param button the image button
		 * @param enabledImage the enabled state image
		 * @param disabledImage the disabled state image
		 */
		public NavigationButton(final ImageHyperlink button, 
				final ImageDescriptor enabledImage, final ImageDescriptor disabledImage) {
			this.button = button;
			this.enabledImage = enabledImage;
			this.disabledImage = disabledImage;
		}
		
		
		/**
		 * Adds a hyperlink listener.
		 * 
		 * @param listener the listener to add
		 */
		public void addHyperlinkListener(final IHyperlinkListener listener) {
			button.addHyperlinkListener(listener);
		}


		/**
		 * Enables/disables this button.
		 * 
		 * @param value the value to set
		 */
		public void setEnabled(final boolean value) {
			if (button.isDisposed()) {
				return;
			}
			button.setEnabled(value);
			if (value) {
				button.setImage(CoreImageRegistry.getImage(enabledImage));
			} else {
				button.setImage(CoreImageRegistry.getImage(disabledImage));
			}
		}
		
		/**
		 * Sets this button visible/invisible.
		 * 
		 * @param value the boolean value
		 */
		public void setVisible(final boolean value) {
			if (!button.isDisposed()) {
				button.setVisible(value);
			}
		}
	}

}
