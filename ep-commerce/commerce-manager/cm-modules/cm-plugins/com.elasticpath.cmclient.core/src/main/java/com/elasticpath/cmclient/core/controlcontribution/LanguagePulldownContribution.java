/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.controlcontribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.helpers.LocaleComparator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.util.EpWidgetUtil;

/**
 * Represents a drop-down box with the available languages.
 */
public class LanguagePulldownContribution extends ControlContribution {

	private static final int COMBO_HEIGHT = 17;

	private CCombo languageSelectorCombo;

	private SelectionListener selectionListener;

	private List<Locale> supportedLocales;
	
	private Locale defaultLocale;
	
	private final Locale selectedLocale;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	private Composite composite;

	/**
	 * Constructs an instance of this class.
	 * 
	 * @param contributionId the unique id of the contribution
	 * @param preSelection the locale that should be selected on display
	 */
	public LanguagePulldownContribution(final String contributionId, final Locale preSelection) {
		super(contributionId);
		this.selectedLocale = preSelection;
	}

	@Override
	protected Control createControl(final Composite parent) {

		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout());

		controlFactory.createLabel(composite, CoreMessages.get().LanguagePulldownLabelText + CoreMessages.SPACE, SWT.NONE, null);

		languageSelectorCombo = controlFactory.createComboBox(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		languageSelectorCombo.setLayout(new RowLayout());
		languageSelectorCombo.setLayoutData(new RowData(SWT.DEFAULT, COMBO_HEIGHT));


		if (selectionListener != null) {
			languageSelectorCombo.addSelectionListener(selectionListener);
		}
		for (Locale locale : supportedLocales) {
			languageSelectorCombo.add(locale.getDisplayName());
		}
		//If a pre selected locale is already set on this pulldown, use it as selection
		if (selectedLocale != null && supportedLocales.contains(selectedLocale)) {
			languageSelectorCombo.select(supportedLocales.indexOf(selectedLocale));
		} else {
			Locale appropriateLocaleFromList = retrieveAppropriateLocaleOrDefault(supportedLocales, CorePlugin.getDefault().getDefaultLocale(), 
					defaultLocale);
			languageSelectorCombo.select(supportedLocales.indexOf(appropriateLocaleFromList));
		}
		return composite;
	}

	/**
	 * This method should return the most appropriate locale from localeList.
	 * First of all it should find appropriate locale from localList using systemLocale.
	 * If it wasn't successful then defaultLocale will be returned
	 * TODO: move it to some where else.
	 * 
	 * @param localeList list of supported locales
	 * @param systemLocale system default locale
	 * @param defaultLocale default locale from localeList
	 * @return appropriate locale from localeList
	 */
	private Locale retrieveAppropriateLocaleOrDefault(final Collection<Locale> localeList, final Locale systemLocale, final Locale defaultLocale) {
		if (localeList.contains(systemLocale)) { // check for real locale
			return systemLocale;
		}
		Locale narrowedLocale = new Locale(systemLocale.getLanguage(), systemLocale.getCountry()); // remove variant
		if (localeList.contains(narrowedLocale)) {
			return narrowedLocale;
		} 
		narrowedLocale = new Locale(systemLocale.getLanguage()); // remove country
		if (localeList.contains(narrowedLocale)) {
			return narrowedLocale;
		} 
		return defaultLocale; //return default
	}
	
	/**
	 * Sets the supported locales.
	 * 
	 * @param supportedLocales the locales to be set to the language combo
	 */
	public void setSupportedLocales(final Collection<Locale> supportedLocales) {
		this.supportedLocales = new ArrayList<Locale>(supportedLocales);
		Collections.sort(this.supportedLocales, new LocaleComparator());
	}
	
	/**
	 * Default locale should be in SupportedLocales.
	 * 
	 * @param defaultLocale the default locale form list of supported locales.
	 */
	public void setDefaultLocale(final Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}
	
	/**
	 * Gets the language selector combo box.
	 * 
	 * @return CCombo
	 */
	public CCombo getLanguageSelectorCombo() {
		return languageSelectorCombo;
	}

	/**
	 * Returns the locale list.
	 * 
	 * @return List
	 */
	public List<Locale> getLocaleList() {
		return supportedLocales;
	}
	
	/**
	 * Returns default locale.
	 * 
	 * @return default locale.
	 */
	public Locale getDefaultLocale() {
		return defaultLocale;
	}
	
	/**
	 * Sets a selection listener for the change language event.
	 * 
	 * @param selectionListener selection listener
	 */
	public void addSelectionListener(final SelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	@Override
	public void dispose() {
		selectionListener = null;
		EpWidgetUtil.safeDispose(composite);
	}
}
