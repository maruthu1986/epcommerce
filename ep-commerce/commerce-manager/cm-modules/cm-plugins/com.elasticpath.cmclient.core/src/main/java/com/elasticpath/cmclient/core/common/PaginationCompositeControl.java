/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.common;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.event.NavigationEvent.NavigationType;
import com.elasticpath.cmclient.core.helpers.PaginationSupport;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * This is the pagination control class.
 * <p>
 * It is responsible for creating managing all the pagination widgets.
 */
public class PaginationCompositeControl extends AbstractPaginationControl {

	private static final int HYPER_LINK_BUTTON_SIZE = 20;
	private static final int TWO_COLUMNS = 2;
	private static final int THREE_COLUMNS = 3;
	private static final int INNER_BAR_COLUMNS = 8;
	private static final int BUTTON_HORIZONTAL_INDENT = 10;
	private static final int BUTTON_WIDTH = 170;

	private final PaginationControlAlignment alignment;
	private final IEpLayoutComposite resultsComposite;
	private IEpLayoutComposite navigationBarComposite;

	private ImageHyperlink firstButton;
	private ImageHyperlink previousButton;
	private ImageHyperlink nextButton;
	private ImageHyperlink lastButton;

	private Label pageLabel;
	private Label ofPageLabel;
	private Label searchResultSummaryLabel;
	private Text pageTextField;

	/**
	 * Controls the alignment of the pagination control.
	 */
	public enum PaginationControlAlignment {
		/**
		 * LEFT alignment.
		 */
		LEFT,
		/**
		 * CENTER alignment.
		 */
		CENTER,
		/**
		 * RIGHT alignment.
		 */
		RIGHT
	}

	/**
	 * The constructor.
	 *
	 * @param resultsComposite  the results composite
	 * @param paginationSupport the pagination support container
	 * @param alignment         this component alignment
	 */
	public PaginationCompositeControl(final IEpLayoutComposite resultsComposite, final PaginationSupport paginationSupport,
									  final PaginationControlAlignment alignment) {
		super(paginationSupport);
		this.resultsComposite = resultsComposite;
		this.alignment = alignment;
	}

	/**
	 *	Create view part control.
	 *
	 *	This can be aligned to the right or to the left.
	 */
	@Override
	public void createViewPartControl() {
		IEpLayoutData navigationBarLayoutData = resultsComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		int numColumns = calculateNumberOfTableColumns();
		navigationBarComposite = resultsComposite.addGridLayoutComposite(numColumns, false, navigationBarLayoutData);

		//add left spacer when centered or right sided alignment
		if (alignment == PaginationControlAlignment.CENTER || alignment == PaginationControlAlignment.RIGHT) {
			createSpacerComposite(navigationBarComposite);
		}

		//add the page navigation panel
		IEpLayoutComposite navigationPanelComposite = createNavigationPanel(navigationBarComposite);

		//add end composite spacer
		if (alignment == PaginationControlAlignment.CENTER || alignment == PaginationControlAlignment.LEFT) {
			createSpacerComposite(navigationBarComposite);
		}

		//all all widgets to the navigation panel
		buildNavigationPanel(navigationPanelComposite);
	}

	private int calculateNumberOfTableColumns() {
		if (alignment == PaginationControlAlignment.LEFT || alignment == PaginationControlAlignment.RIGHT) {
			return TWO_COLUMNS;
		}
		return THREE_COLUMNS;
	}

	private IEpLayoutComposite createSpacerComposite(final IEpLayoutComposite composite) {
		IEpLayoutData spacerBarLayoutData = composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		IEpLayoutComposite spacerComposite = composite.addGridLayoutComposite(1, false, spacerBarLayoutData);
		((GridData) spacerComposite.getSwtComposite().getLayoutData()).grabExcessVerticalSpace = true;
		((GridData) spacerComposite.getSwtComposite().getLayoutData()).grabExcessHorizontalSpace = true;
		return spacerComposite;
	}

	private IEpLayoutComposite createNavigationPanel(final IEpLayoutComposite navigationBarComposite) {
		IEpLayoutData navigationPanelLayoutData = navigationBarComposite.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.CENTER, false, false);
		IEpLayoutComposite navigationPanelComposite = navigationBarComposite
				.addGridLayoutComposite(INNER_BAR_COLUMNS, false, navigationPanelLayoutData);
		((GridData) navigationPanelComposite.getSwtComposite().getLayoutData()).horizontalIndent = 0;
		((GridData) navigationPanelComposite.getSwtComposite().getLayoutData()).verticalIndent = 0;
		return navigationPanelComposite;
	}

	private void buildNavigationPanel(final IEpLayoutComposite navigationPanelComposite) {
		firstButton = createPaginationImageLink(navigationPanelComposite, CoreImageRegistry.IMAGE_RESULTSET_FIRST,
				CoreMessages.get().navigation_FirstPage);
		previousButton = createPaginationImageLink(navigationPanelComposite, CoreImageRegistry.IMAGE_RESULTSET_PREVIOUS,
				CoreMessages.get().navigation_PreviousPage);
		pageLabel = createPaginationLabel(navigationPanelComposite, getPageLabelText());
		pageTextField = addPaginationTextField(navigationPanelComposite);
		ofPageLabel = createPaginationLabel(navigationPanelComposite, getOfPageLabelText());

		nextButton = createPaginationImageLink(navigationPanelComposite, CoreImageRegistry.IMAGE_RESULTSET_NEXT,
				CoreMessages.get().navigation_NextPage);
		lastButton = createPaginationImageLink(navigationPanelComposite, CoreImageRegistry.IMAGE_RESULTSET_LAST,
				CoreMessages.get().navigation_LastPage);
		searchResultSummaryLabel = createPaginationLabel(navigationPanelComposite, getSearchResultsSummaryLabelText());
		GridData searchResultSummaryLayoutData = (GridData) searchResultSummaryLabel.getLayoutData();
		searchResultSummaryLayoutData.widthHint = BUTTON_WIDTH;
		searchResultSummaryLayoutData.minimumWidth = BUTTON_WIDTH;
		searchResultSummaryLayoutData.horizontalIndent = BUTTON_HORIZONTAL_INDENT;

		pageTextField.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				int toPage = getValidPage(NumberUtils.toInt(pageTextField.getText().trim(), 0));
				pageTextField.setText(String.valueOf(toPage));
				navigateTo(toPage);
				updateNavigationComponents();
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				// nothing to do
			}
		});

		pageTextField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent event) {
				//nothing to do
			}

			@Override
			public void focusLost(final FocusEvent event) {
				pageTextField.setText(String.valueOf(getCurrentPage()));
			}
		});

		firstButton.setEnabled(false);
		previousButton.setEnabled(false);
		nextButton.setEnabled(false);
		lastButton.setEnabled(false);

		final MouseListener hyperlinkListener = addControlMouseListener();

		firstButton.addMouseListener(hyperlinkListener);
		previousButton.addMouseListener(hyperlinkListener);
		nextButton.addMouseListener(hyperlinkListener);
		lastButton.addMouseListener(hyperlinkListener);
	}

	private Text addPaginationTextField(final IEpLayoutComposite composite) {
		IEpLayoutData layoutData = composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false);
		return composite.addTextField(EpControlFactory.EpState.DISABLED, layoutData);
	}

	private Label createPaginationLabel(final IEpLayoutComposite composite, final String text) {
		IEpLayoutData labelBarLayoutData = composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false);

		Label label = composite.addLabel(text, labelBarLayoutData);
		GridData compositeLayoutData = (GridData) label.getLayoutData();
		compositeLayoutData.verticalAlignment = SWT.CENTER;
		compositeLayoutData.horizontalAlignment = SWT.CENTER;
		compositeLayoutData.grabExcessVerticalSpace = true;

		return label;
	}

	private ImageHyperlink createPaginationImageLink(final IEpLayoutComposite composite, final ImageDescriptor imageDescriptor,
													 final String toolTip) {
		IEpLayoutData hyperLinkImageLayoutData = composite.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.CENTER, false, true);
		GridData gridData = (GridData) hyperLinkImageLayoutData.getSwtLayoutData();
		gridData.verticalIndent = 0;
		gridData.horizontalIndent = 0;
		gridData.widthHint = HYPER_LINK_BUTTON_SIZE;
		gridData.heightHint = HYPER_LINK_BUTTON_SIZE;

		ImageHyperlink imageHyperlink = composite.addHyperLinkImage(imageDescriptor.createImage(), EpControlFactory.EpState.DISABLED,
				hyperLinkImageLayoutData);
		imageHyperlink.setToolTipText(toolTip);
		imageHyperlink.textSpacing = 0;

		return imageHyperlink;
	}

	private MouseListener addControlMouseListener() {
		return new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent event) {
				super.mouseDown(event);
				if (event.getSource() == firstButton) {
					getPaginationSupport().fireNavigationEvent(NavigationType.FIRST, null);
				} else if (event.getSource() == lastButton) {
					getPaginationSupport().fireNavigationEvent(NavigationType.LAST, null);
				} else if (event.getSource() == nextButton) {
					getPaginationSupport().fireNavigationEvent(NavigationType.NEXT, null);
				} else if (event.getSource() == previousButton) {
					getPaginationSupport().fireNavigationEvent(NavigationType.PREVIOUS, null);
				}
			}
		};
	}

	@Override
	protected void updateLayout() {
		navigationBarComposite.getSwtComposite().layout();
	}

	@Override
	protected void setFirstButtonEnabled(final boolean firstButtonEnabled) {
		firstButton.setEnabled(firstButtonEnabled);
	}

	@Override
	protected void setLastButtonEnabled(final boolean lastButtonEnabled) {
		lastButton.setEnabled(lastButtonEnabled);
	}

	@Override
	protected void setPreviousButtonEnabled(final boolean previousButtonEnabled) {
		previousButton.setEnabled(previousButtonEnabled);
	}

	@Override
	protected void setNextButtonEnabled(final boolean nextButtonEnabled) {
		nextButton.setEnabled(nextButtonEnabled);
	}

	@Override
	protected void setPageTextValue(final String pageText) {
		pageTextField.setText(pageText);
	}

	@Override
	protected void setOfPageLabelTextValue(final String ofPageLabelText) {
		ofPageLabel.setText(ofPageLabelText);
	}

	@Override
	protected void setSearchResultSummaryLabelTextValue(final String searchResultSummaryLabelText) {
		searchResultSummaryLabel.setText(searchResultSummaryLabelText);
	}

	@Override
	protected void setOfPageLabelEnabled(final boolean ofPageLabelEnabled) {
		ofPageLabel.setEnabled(ofPageLabelEnabled);
	}

	@Override
	protected void setPageLabelEnabled(final boolean pageLabelEnabled) {
		pageLabel.setEnabled(pageLabelEnabled);
	}

	@Override
	protected void setPageTextFieldEnabled(final boolean pageTextFieldEnabled) {
		pageTextField.setEnabled(pageTextFieldEnabled);
	}

	@Override
	protected void setSearchResultSummaryLabelEnabled(final boolean searchResultSummaryLabelEnabled) {
		searchResultSummaryLabel.setEnabled(searchResultSummaryLabelEnabled);
	}
}
