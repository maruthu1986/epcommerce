/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.wizards.product;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDualListBox;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * The class to display the dual assignment list for product SKU Option
 * assignment for adding/Editing product type.
 */
public class SkuOptionsDualList extends AbstractPolicyAwareDualListBox<List<SkuOption>> {

	private static final String[] AVAIL_ASSIGN_TITLES = {CatalogMessages.get().ProductTypeAddEditWizard_AvailSku,
			CatalogMessages.get().ProductTypeAddEditWizard_SelectSku};

	private AvailableSkuOptionFilter availableOptionFilter;

	private List<SkuOption> availableOptions;

	private ILabelProvider labelProvider;

	private final Catalog catalog;

	/**
	 * @param parentComposite the parent composite of the dual list box.
	 * @param model           the model object.
	 * @param data            the layout data to create the dual list box
	 * @param container       the PolicyActionContainer passed in.
	 * @param catalog         the Catalog whose available sku options will be displayed
	 */
	public SkuOptionsDualList(final IPolicyTargetLayoutComposite parentComposite, final IEpLayoutData data, final PolicyActionContainer container,
							  final List<SkuOption> model, final Catalog catalog) {
		super(parentComposite, data, container, model, AVAIL_ASSIGN_TITLES[0], AVAIL_ASSIGN_TITLES[1],
				ALL_BUTTONS | MULTI_SELECTION | EMPTY_UP_DOWN_BUTTON_PANEL);
		this.catalog = catalog;

	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		final List<SkuOption> skuOptionList = getModel();
		skuOptionList.addAll(selection.toList());
		return true;
	}

	@Override
	public Collection<SkuOption> getAssigned() {
		return getModel();
	}

	@Override
	public Collection<SkuOption> getAvailable() {
		if (availableOptions == null) {
			final SkuOptionService skuOptionService = ServiceLocator.getService(
					ContextIdNames.SKU_OPTION_SERVICE);
			availableOptions = skuOptionService.findAllSkuOptionFromCatalog(this.catalog.getUidPk());
		}
		return availableOptions;
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		if (availableOptionFilter == null) {
			availableOptionFilter = new AvailableSkuOptionFilter();
		}
		return availableOptionFilter;
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		if (labelProvider == null) {
			labelProvider = new SkuOptionLabelProvider();
		}
		return labelProvider;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		final List<SkuOption> skuOptionList = getModel();
		skuOptionList.removeAll(selection.toList());
		return true;
	}

	/**
	 * The filter class defining the logic to filter the assigned list.
	 */
	public class AvailableSkuOptionFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement,
							  final Object element) {
			if (getAssigned() == null) {
				return false;
			}
			for (SkuOption skuOption : getAssigned()) {
				if (skuOption.getUidPk() == ((SkuOption) element).getUidPk()) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * The LabelProvider for the product type SKU option dual list box.
	 */
	public class SkuOptionLabelProvider extends LabelProvider {

		@Override
		public String getText(final Object element) {
			final SkuOption option = (SkuOption) element;
			return option.getOptionKey();
		}
	}
}