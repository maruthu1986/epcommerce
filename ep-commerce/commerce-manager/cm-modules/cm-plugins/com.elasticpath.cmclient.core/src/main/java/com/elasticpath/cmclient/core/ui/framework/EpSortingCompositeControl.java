/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;

/**
 * Represents Composite Widget for Sorting.
 * 
 * To use it just create it on <code>createControls</code> (as any other control) then on <code>populateControls</code> use methods:
 * <ul>
 *  <li>{@link #addSortTypeItem(String, SortBy, boolean)}, (with defaultItem=true) to create Default item</li>
 * 	<li>{@link #addSortTypeItem(String, SortBy)}, to create common item</li>  
 * </ul>
 * In clear button handler use {@link #clear()}, it is used to clear to default values. 
 * <p>Sorting orders is <b>Acceding</b> and <b>Descending</b>, default is first</p>.
 * 
 */
public class EpSortingCompositeControl {

	private static final String DEFAULT_DATA_INDEX = "defaultDataIndexForSortingComboBox_0";  //$NON-NLS-1$
	
	private CCombo sortByColumnCombo;

	private CCombo sortOrderCombo;

	private final SearchCriteria model;
	
	/**
	 * Constructs <code>SortingComposite</code>.
	 * @param parentComposite the parent composite. 
	 * @param model the <code>SearchCriteria</code> model
	 */
	public EpSortingCompositeControl(final IEpLayoutComposite parentComposite, final SearchCriteria model) {		
		this.model = model;		
		this.createControls(parentComposite);
		this.bindControls();
		this.populateSortOrder();
	}
	
	private void createControls(final IEpLayoutComposite parentComposite) {
		final IEpLayoutData data = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite groupComposite = parentComposite.addGroup(CoreMessages.get().SortingCompositeControl_SortingGroup, 1, false, data);

		// Sort by Column combo box
		groupComposite.addLabelBold(CoreMessages.get().SortingCompositeControl_Label_SortByColumn, null);
		this.sortByColumnCombo = groupComposite.addComboBox(EpState.EDITABLE, data);
		this.sortByColumnCombo.setEnabled(true);

		// Sort Order combo box
		groupComposite.addLabelBold(CoreMessages.get().SortingCompositeControl_Label_SortOrder, null);
		this.sortOrderCombo = groupComposite.addComboBox(EpState.EDITABLE, data);
		this.sortOrderCombo.setEnabled(true);
	}
	
	private void populateSortOrder() {
		populateCombo(sortOrderCombo, CoreMessages.get().SortingCompositeControl_Sort_Order_Ascending, SortOrder.ASCENDING, true);
		populateCombo(sortOrderCombo, CoreMessages.get().SortingCompositeControl_Sort_Order_Descending, SortOrder.DESCENDING, false);
	}
	
	/**
	 * Adds Sort Type Item, (not default).
	 * 
	 * @param name the name of the sorting type
	 * @param sortingType the sorting type instance.
	 */
	public void addSortTypeItem(final String name, final SortBy sortingType) {
		addSortTypeItem(name, sortingType, false);
	}
	
	/**
	 * Clears internals to default state.
	 */
	public void clear() {
		selectDefaultIndex(sortByColumnCombo);
		selectDefaultIndex(sortOrderCombo);
	}

	/**
	 * Adds Sort Type Item.
	 *  
	 * @param name the name of the sorting type
	 * @param sortingType the sorting type instance.
	 * @param defaultItem item is selected ad default.
	 */
	public void addSortTypeItem(final String name, final SortBy sortingType, final boolean defaultItem) {
		populateCombo(sortByColumnCombo, name, sortingType, defaultItem);
	}	

	private void bindControls() {
		sortByColumnCombo.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				getModel().setSortingType((SortBy) getSelectedData(sortByColumnCombo));
			}
		});
		sortOrderCombo.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				getModel().setSortingOrder((SortOrder) getSelectedData(sortOrderCombo));
			};
		});
	}

	/**
	 * @return <code>SearchCriteria</code> model
	 */
	public SearchCriteria getModel() {
		return model;
	}
	
	private static Object getSelectedData(final CCombo combo) {
		return combo.getData(combo.getText());
	}

	private static void populateCombo(final CCombo combo, final String name, final Object data, final boolean defaultItem) {
		Assert.isTrue(!DEFAULT_DATA_INDEX.equals(name), DEFAULT_DATA_INDEX + " could not be used"); //$NON-NLS-1$
		
		combo.add(name);		
		combo.setData(name, data);		
		
		if (defaultItem) {
			final int defaultIndex = combo.getItemCount() - 1;
			
			combo.select(defaultIndex);
			combo.setData(DEFAULT_DATA_INDEX, defaultIndex);
		}
	}
	
	private static void selectDefaultIndex(final CCombo combo) {
		Object data = combo.getData(DEFAULT_DATA_INDEX);
		if (data == null) {
			combo.select(0);
		} else {
			combo.select((Integer) data);
		}
	}

	/**
	 * This method updates search criteria by setting currently selected values.
	 */
	public void updateSearchCriteriaValues() {
		model.setSortingType((SortBy) sortByColumnCombo.getData(sortByColumnCombo.getItems()[sortByColumnCombo.getSelectionIndex()]));
		model.setSortingOrder(((SortOrder) sortOrderCombo.getData(sortOrderCombo.getItems()[sortOrderCombo.getSelectionIndex()])));
	}

	/**
	 * Get the sort by ccombo.
	 *
	 * @return the sort by ccombo
	 */
	public CCombo getSortByColumnCombo() {
		return sortByColumnCombo;
	}

	/**
	 * Get the sort order ccombo.
	 *
	 * @return the sort order ccombo
	 */
	public CCombo getSortOrderCombo() {
		return sortOrderCombo;
	}
}
