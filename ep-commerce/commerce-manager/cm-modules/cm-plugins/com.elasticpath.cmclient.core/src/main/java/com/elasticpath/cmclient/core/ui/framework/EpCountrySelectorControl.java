/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Geography;

/**
 * Manager for state and country combo`s.
 */
public class EpCountrySelectorControl {
	
	/** Empty string. */
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private final List<Country> countryList;
	private List<Country> statesList;

	/** The Warehouse country combo box field. */
	private CCombo countryCombo;

	/** The Warehouse state combo box field. */
	private CCombo stateCombo;
	
	/** The state to set combo boxes after editing. */
	private EpState authState;

	/**
	 * Constructor.
	 */
	public EpCountrySelectorControl() {
		countryList = new ArrayList<>();
		for (String countryCode : getGeography().getCountryCodes()) {
			countryList.add(new Country(countryCode, getGeography().getCountryDisplayName(countryCode, CorePlugin.getDefault().getDefaultLocale())));
		}
		Collections.sort(countryList);
	}

	/**
	 * Initialize state and country combo`s.
	 * @param state the state of the combo boxes
	 */
	public void initStateCountryCombo(final EpState state) {
		this.authState = state;
		countryCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(final SelectionEvent event) {
				fillStateCombo();
			}

			public void widgetDefaultSelected(final SelectionEvent event) {
				/* default implementation */
			}

		});

	}

	/**
	 * Populate state and country combo`s.
	 */
	public void populateStateCountryCombo() {
		final String[] items = new String[countryList.size()];

		int index = 0;
		for (final Country country : countryList) {
			items[index++] = country.getCountryName();
		}

		countryCombo.setItems(items);
	}

	/**
	 * Select item country, if country is not found select first item, then notify SelectionChangeListener about selection.
	 * 
	 * @param countryCode Country code
	 */
	public void selectCountryCombo(final String countryCode) {
		final String text = getGeography().getCountryDisplayName(countryCode, CorePlugin.getDefault().getDefaultLocale());
		if (text != null) {
			countryCombo.setText(text);
		}
		countryCombo.notifyListeners(SWT.Selection, null);
	}
	
	/**
	 * Select item[index] then notify SelectionChangeListener about selection.
	 * 
	 * @param index selected index
	 */
	public void selectCountryCombo(final int index) {
		countryCombo.select(index);
		countryCombo.notifyListeners(SWT.Selection, null);
	}

	/**
	 * Select item state, if state is not found select first item.
	 * 
	 * @param stateCode selected state code
	 */
	public void selectStateCombo(final String stateCode) {
		if (EMPTY_STRING.equals(stateCode)) {
			stateCombo.setText(EMPTY_STRING);
		} else {
			final Country selectedCountry = countryList.get(countryCombo.getSelectionIndex());
			final String text = getGeography().getSubCountryDisplayName(selectedCountry.getCountryCode(), stateCode);
			if (text != null) {
				stateCombo.setText(text);
			}
		}
	}

	/**
	 * @return selected country code.
	 */
	public String getCountryComboItem() {
		String result = null;
		if (countryCombo.getSelectionIndex() != -1) {
			final Country selectedCountry = countryList.get(countryCombo.getSelectionIndex());
			result = selectedCountry.getCountryCode();
		}

		return result;
	}

	/**
	 * @return selected state code.
	 */
	public String getStateComboItem() {
		String result = EMPTY_STRING;
		if (stateCombo.getSelectionIndex() != -1) {
			final Country selectedState = statesList.get(stateCombo.getSelectionIndex());
			result = selectedState.getSubCountryCode();
		}

		return result;
	}


	/**
	 * @return the countryCombo
	 */
	public CCombo getCountryCombo() {
		return countryCombo;
	}

	/**
	 * @return the stateCombo
	 */
	public CCombo getStateCombo() {
		return stateCombo;
	}

	/**
	 * @param countryCombo the countryCombo to set
	 */
	public void setCountryCombo(final CCombo countryCombo) {
		this.countryCombo = countryCombo;
	}

	/**
	 * @param stateCombo the stateCombo to set
	 */
	public void setStateCombo(final CCombo stateCombo) {
		this.stateCombo = stateCombo;
	}

	private void fillStateCombo() {
		String selectedCountryCode = null;
		final int selectedIndex = countryCombo.getSelectionIndex();
		if (selectedIndex != -1) {
			selectedCountryCode = countryList.get(selectedIndex).getCountryCode();
		}

		statesList = new ArrayList<>();
		for (String subCountryCode : getGeography().getSubCountryCodes(selectedCountryCode)) {
			statesList.add(new Country(
					selectedCountryCode,
					getGeography().getCountryDisplayName(selectedCountryCode, CorePlugin.getDefault().getDefaultLocale()),
					subCountryCode,
					getGeography().getSubCountryDisplayName(selectedCountryCode, subCountryCode, CorePlugin.getDefault().getDefaultLocale())));
		}
		Collections.sort(statesList);

		if (statesList.isEmpty()) {
			stateCombo.setText(EMPTY_STRING);
			stateCombo.setEnabled(false);
		} else {
			stateCombo.setEnabled(true);

			final String[] items = new String[statesList.size()];

			int index = 0;
			for (final Country subCountry : statesList) {
				items[index++] = subCountry.getSubCountryName();
			}

			stateCombo.setItems(items);
			stateCombo.select(0);
		}
		if (authState != EpState.EDITABLE) {
			stateCombo.setEnabled(false);
			countryCombo.setEnabled(false);
		}
	}

	private static Geography getGeography() {
		return ServiceLocator.getService(ContextIdNames.GEOGRAPHY);
	}
}