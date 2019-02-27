/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * The class for search sku option filter.
 */
public class SkuSearchSkuOptionFilterSection {

	private static final int FIRST_INDEX = 0;

	private static final String SKU_OPTION_TABLE = "Sku Option"; //$NON-NLS-1$

	private final CCombo skuOptionCombo;

	private IEpTableViewer skuOptionValueListViewer;

	private final IEpLayoutComposite wrapComposite;

	private List<SkuOption> skuOptionList;

	private List<SkuOptionValue> skuOptionValueList;

	private final SkuSearchViewTab skuSearchViewTab;

	private final ImageHyperlink deleteHyperLink;

	/**
	 * The constructor.
	 *
	 * @param parentComposite  the parent composite
	 * @param skuSearchViewTab the sku search view tab
	 */
	public SkuSearchSkuOptionFilterSection(final IEpLayoutComposite parentComposite, final SkuSearchViewTab skuSearchViewTab) {

		this.skuSearchViewTab = skuSearchViewTab;

		final IEpLayoutData layoutData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		wrapComposite = parentComposite.addGroup("", 2, false, layoutData); //$NON-NLS-1$

		final IEpLayoutData oneColumnLayoutData = wrapComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutData oneColumnRightAlignLayoutData = wrapComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, true, false);
		final IEpLayoutData spanTwoColumnLayoutData = wrapComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);

		wrapComposite.addLabelBold(CatalogMessages.get().SearchView_Filter_Label_SkuOptions, oneColumnLayoutData);

		deleteHyperLink = wrapComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_X),
				EpState.EDITABLE, oneColumnRightAlignLayoutData);
		deleteHyperLink.setToolTipText(CatalogMessages.get().SearchView_Filter_Remove_Sku_Option_Filter_Section);
		deleteHyperLink.addHyperlinkListener(new IHyperlinkListener() {
			@Override
			public void linkActivated(final HyperlinkEvent event) {
				//remove itself from the UI
				if (isSkuOptionFilterSectionRemovable()) {
					dispose();
					SkuSearchSkuOptionFilterSection.this.skuSearchViewTab.removeSection(SkuSearchSkuOptionFilterSection.this);
				}
			}

			@Override
			public void linkEntered(final HyperlinkEvent event) {
				deleteHyperLink.setUnderlined(true);
			}

			@Override
			public void linkExited(final HyperlinkEvent event) {
				deleteHyperLink.setUnderlined(false);
			}
		});

		skuOptionCombo = wrapComposite.addComboBox(EpState.EDITABLE, spanTwoColumnLayoutData);
		skuOptionCombo.setEnabled(true);

		skuOptionCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// TODO nothing to do...				
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (skuOptionCombo.getSelectionIndex() == 0) { // ALL
					skuOptionValueListViewer.getSwtTable().removeAll();
				} else {
					final SkuOption skuOption = getSelectedSkuOptionInternal();
					populateSkuOptionValues(skuOption.getOptionValues());
				}
			}

		});

		createSkuOptionListViewer(wrapComposite, spanTwoColumnLayoutData);
	}

	/**
	 * Refresh the UI.
	 */
	protected void refreshUI() {
		skuSearchViewTab.refreshUI();
	}

	/**
	 * determine if the sku option filter section can be removed.
	 *
	 * @return true if the sku option filter section could be removed
	 */
	protected boolean isSkuOptionFilterSectionRemovable() {
		return skuSearchViewTab.isSkuOptionFilterSectionRemovable();
	}

	private void createSkuOptionListViewer(final IEpLayoutComposite wrapComposite, final IEpLayoutData layoutData) {
		wrapComposite.addLabelBold(CatalogMessages.get().SearchView_Filter_Label_SkuOptionValues, layoutData);
		skuOptionValueListViewer = wrapComposite.addTableViewer(true, EpState.READ_ONLY, layoutData, SKU_OPTION_TABLE);
		skuOptionValueListViewer.getSwtTable().setHeaderVisible(false);
	}

	/**
	 * populate controls.
	 *
	 * @param skuOptionList the sku option list to populate
	 */
	public void populateControls(final List<SkuOption> skuOptionList) {
		populateSkuOptionCombo(skuOptionList);
	}

	private void populateSkuOptionValues(final Collection<SkuOptionValue> skuOptionValues) {
		skuOptionValueListViewer.setContentProvider(new SkuOptionValueContentProvider());
		skuOptionValueListViewer.setLabelProvider(new SkuOptionValueLabelProvider());
		skuOptionValueList = sortSkuOptionValues(skuOptionValues);
		skuOptionValueListViewer.setInput(skuOptionValueList);
		skuOptionValueListViewer.getSwtTable().select(FIRST_INDEX);
	}

	private void populateSkuOptionCombo(
			final List<SkuOption> skuOptionList) {
		this.skuOptionList = sortSkuOption(skuOptionList);
		removeAllItemsFromSkuOptionCombo();
		skuOptionCombo.add(CatalogMessages.get().SearchView_Filter_SkuOption_All, FIRST_INDEX);
		for (final SkuOption skuOption : skuOptionList) {
			skuOptionCombo.add(getSkuOptionDisplayName(skuOption));
		}
		skuOptionCombo.select(FIRST_INDEX);
	}

	private List<SkuOption> sortSkuOption(final List<SkuOption> skuOptionList) {
		Collections.sort(skuOptionList, Comparator.comparing(this::getSkuOptionDisplayName));
		return skuOptionList;
	}

	private List<SkuOptionValue> sortSkuOptionValues(final Collection<SkuOptionValue> skuOptionValues) {
		List<SkuOptionValue> skuOptionValueList = new ArrayList<>(skuOptionValues);
		Collections.sort(skuOptionValueList, Comparator.comparing(this::getSkuOptionValueDisplayName));
		return skuOptionValueList;
	}

	private String getSkuOptionDisplayName(final SkuOption targetSkuOption) {
		String skuOptionDisplayName = targetSkuOption.getDisplayName(CorePlugin.getDefault().getDefaultLocale(), true);

		return skuOptionDisplayName + " (" + targetSkuOption.getOptionKey() + ")";    //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String getSkuOptionValueDisplayName(final SkuOptionValue skuOptionValue) {
		return getSkuOptionValueDisplayNameOnly(skuOptionValue) + " (" + skuOptionValue.getOptionValueKey() + ")";    //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String getSkuOptionValueDisplayNameOnly(final SkuOptionValue skuOptionValue) {
		return skuOptionValue.getDisplayName(CorePlugin.getDefault().getDefaultLocale(), true);
	}

	private void removeAllItemsFromSkuOptionCombo() {
		skuOptionCombo.removeAll();
		if (skuOptionValueListViewer != null) {
			skuOptionValueListViewer.getSwtTable().removeAll();
		}
	}

	private SkuOption getSelectedSkuOptionInternal() {
		int index = skuOptionCombo.getSelectionIndex() - 1;
		if (index >= 0) {
			return skuOptionList.get(index);
		}
		return null;
	}

	/**
	 * get selected sku option.
	 *
	 * @return the selected sku option
	 */
	public SkuOption getSelectedSkuOption() {
		if (this.skuOptionCombo.getSelectionIndex() != FIRST_INDEX) {
			return getSelectedSkuOptionInternal();
		}
		return null;
	}

	/**
	 * get selected sku option values.
	 *
	 * @return the set of selected sku option values
	 */
	public Set<String> getSelectedSkuOptionValues() {
		Set<String> selectedSkuOptionValues = new HashSet<>();
		int[] selectedIndex = skuOptionValueListViewer.getSwtTable().getSelectionIndices();
		for (int index : selectedIndex) {
			SkuOptionValue skuOptionValue = skuOptionValueList.get(index);
			selectedSkuOptionValues.add(getSkuOptionValueDisplayNameOnly(skuOptionValue));
		}
		return selectedSkuOptionValues;
	}

	/**
	 * sku option value label provider.
	 */
	protected class SkuOptionValueLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		@SuppressWarnings("PMD.MissingBreakInSwitch")
		public String getColumnText(final Object element, final int columnIndex) {
			switch (columnIndex) {
				case 0:
					if (element instanceof SkuOptionValue) {
						return getSkuOptionValueDisplayName((SkuOptionValue) element);
					}
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}

	/**
	 * Content provider for the list viewers.
	 */
	protected class SkuOptionValueContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			// Do nothing
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// Do nothing
		}

		/**
		 * This implementation takes in a Collection.
		 */
		@Override
		public Object[] getElements(final Object inputElement) {
			return ((Collection<?>) inputElement).toArray();
		}
	}

	/**
	 * Dispose this section.
	 */
	public void dispose() {
		wrapComposite.getSwtComposite().dispose();
		refreshUI();
	}

	/**
	 * hide the remove button.
	 */
	public void hideRemoveButton() {
		deleteHyperLink.setVisible(false);
	}

	/**
	 * display the remove button.
	 */
	public void displayRemoveButton() {
		deleteHyperLink.setVisible(true);
	}

	/**
	 * Get the sku option combo.
	 *
	 * @return the sku option combo
	 * */
	public CCombo getSkuOptionCombo() {
		return skuOptionCombo;
	}

}


