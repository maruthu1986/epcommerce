/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.domain.misc.LocalizedProperties;

/**
 * Class to control localized text control with combobox.
 */
public final class EpLocalizedPropertyController {

	private static final IStatus ERROR_STATUS = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, CoreMessages.EMPTY_STRING);

	private List<Locale> allLocales;

	private Locale defaultLocale;

	private LocalizedProperties localizedProperties;

	private final Text propertyText;

	private final CCombo localesCombo;

	private final String propertyName;

	private DataBindingContext dataBindingCtx;

	private Locale selectedLocale;

	private Map<Locale, String> prevNames;
	
	private final boolean defaultValueRequired;
	
	private final IValidator validator;

	private ControlModificationListener controlModificationListener;
	
	/**
	 * The constructor.
	 */
	private EpLocalizedPropertyController(final Text propertyText, final CCombo localesCombo, final String propertyName,
			final boolean defaultRequired, final DataBindingContext dataBindingCtx) {
		this.propertyText = propertyText;
		this.localesCombo = localesCombo;
		this.propertyName = propertyName;
		this.dataBindingCtx = dataBindingCtx;
		this.defaultValueRequired = defaultRequired;
		this.validator = null;
	}
	
	/**
	 * The constructor.
	 */
	private EpLocalizedPropertyController(final Text propertyText, final CCombo localesCombo, final String propertyName,
			final boolean defaultRequired, final DataBindingContext dataBindingCtx, final IValidator validator) {
		this.propertyText = propertyText;
		this.localesCombo = localesCombo;
		this.propertyName = propertyName;
		this.dataBindingCtx = dataBindingCtx;
		this.defaultValueRequired = defaultRequired;
		this.validator = validator;
	}
	

	/**
	 * Factory method.
	 * 
	 * @param propertyText the text field
	 * @param localesCombo the combo box
	 * @param propertyName the property name
	 * @param defaultRequired if validation is required on the default locale property 
	 * @param dataBindingCtx the binding context
	 * @return EpLocalizedPropertyController initialized object
	 */
	public static EpLocalizedPropertyController createEpLocalizedPropertyController(final Text propertyText, final CCombo localesCombo,
			final String propertyName, final boolean defaultRequired, final DataBindingContext dataBindingCtx) {
		
		EpLocalizedPropertyController epLocalizedPropertyController = new EpLocalizedPropertyController(propertyText, localesCombo, propertyName,
				defaultRequired, dataBindingCtx);

		epLocalizedPropertyController.create();

		return epLocalizedPropertyController;
	}
	
	/**
	 * Factory method.
	 * 
	 * @param propertyText the text field
	 * @param localesCombo the combo box
	 * @param propertyName the property name
	 * @param defaultRequired if validation is required on the default locale property 
	 * @param dataBindingCtx the binding context
	 * @param validator instance of IValidator. Can be null
	 * @return EpLocalizedPropertyController initialized object
	 */
	public static EpLocalizedPropertyController createEpLocalizedPropertyController(final Text propertyText, final CCombo localesCombo,
			final String propertyName, final boolean defaultRequired, final DataBindingContext dataBindingCtx, final IValidator validator) {
		
		EpLocalizedPropertyController epLocalizedPropertyController = new EpLocalizedPropertyController(propertyText, localesCombo, propertyName,
				defaultRequired, dataBindingCtx, validator);

		epLocalizedPropertyController.create();

		return epLocalizedPropertyController;
	}
	

	private void create() {
		localesCombo.addSelectionListener(new LocaleSelectionListener());
	}

	/**
	 * Populates the control.
	 * 
	 * @param allLocales all the locales
	 * @param defaultLocale the default one
	 * @param localizedProperties properties storage
	 */
	public void populate(final List<Locale> allLocales, final Locale defaultLocale, final LocalizedProperties localizedProperties) {
		this.allLocales = allLocales;
		this.defaultLocale = defaultLocale;
		this.localizedProperties = localizedProperties;

		storeNames();

		if (allLocales.isEmpty()) {
			localesCombo.setEnabled(false);
			propertyText.setEnabled(false);
			return;
		}
		
		// Sort the locales in alphabetical order of display name
		Collections.sort(allLocales, new Comparator<Locale>() {
			public int compare(final Locale locale1, final Locale locale2) {
				return locale1.getDisplayName().compareTo(locale2.getDisplayName());
			}
		});

		//Populate the combo and make sure that the default locale is selected
		localesCombo.select(populateLocalesCombo());
		
		reload(localesCombo.getSelectionIndex());
	}

	private void storeNames() {
		prevNames = new HashMap<Locale, String>();
		for (Locale locale : allLocales) {
			prevNames.put(locale, objectToString(localizedProperties.getValue(propertyName, locale)));
		}
	}

	/**
	 * Binds text control.
	 */
	public void bind() {
		EpValueBinding epValueBinding = 
			EpControlBindingProvider.getInstance().bind(dataBindingCtx, propertyText, 
					new IValidator() {
						/**
						 * Validator wrapper that disables language combo if the 
						 * value is invalid to prevent truncation.
						 */
						public IStatus validate(final Object value) {
							if (validator == null) {
								localesCombo.setEnabled(true);
								return Status.OK_STATUS;
							}
							final IStatus status = validator.validate(value);
							localesCombo.setEnabled(status.isOK());
							return status;
						}
						
				
					}, null, new UpdateLocalizedPropertyValueStrategy(), true);
		epValueBinding.getBinding().updateTargetToModel();
	}
	
	/**
	 * Binds text control in the given context.
	 * 
	 * @param context the context
	 */
	public void bind(final DataBindingContext context) {
		dataBindingCtx = context;
		bind();
	}

	/**
	 * Checks localized names for changes.
	 * 
	 * @return true if localized names were modified
	 */
	public boolean isNameModified() {
		boolean modified = false;
		for (Locale locale : allLocales) {
			if (!prevNames.get(locale).equals(objectToString(localizedProperties.getValue(propertyName, locale)))) {
				modified = true;

				break;
			}
		}

		return modified;
	}

	/**
	 * Populates the locales combo box.
	 * @return the index of the default locale
	 */
	private int populateLocalesCombo() {
		localesCombo.removeAll();

		int defaultLocaleIndex = 0;
		int currentIndex = 0;
		for (Locale locale : allLocales) {
			if (locale.equals(defaultLocale)) {
				defaultLocaleIndex = currentIndex;
			}
			localesCombo.add(locale.getDisplayName());
			currentIndex++;
		}
		return defaultLocaleIndex;
	}

	/**
	 * Synchronizes the value in the textbox that displays the selected locale's displayName
	 * with the displayName of the selected locale.
	 * @param index the index of the combo box value to reload
	 */
	private void reload(final int index) {
		selectedLocale = allLocales.get(index);
		String propertyValue = localizedProperties.getValueWithoutFallBack(propertyName, selectedLocale);
		if (propertyValue == null) {
			propertyText.setText(CoreMessages.EMPTY_STRING);
		} else {
			propertyText.setText(propertyValue);
		}
	}

	private boolean validate() {
		String defaultValue = localizedProperties.getValueWithoutFallBack(propertyName, defaultLocale);
		boolean error = (defaultValue == null) || (defaultValue.trim().length() == 0);

		return !error;
	}

	private String objectToString(final Object object) {
		if (object == null) {
			return CoreMessages.EMPTY_STRING;
		}

		return object.toString();
	}

	/**
	 * Localized Property validator.
	 */
	private class UpdateLocalizedPropertyValueStrategy extends ObservableUpdateValueStrategy {
		@Override
		protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
			String value = (String) newValue;

			localizedProperties.setValue(propertyName, selectedLocale, value);

			IStatus status = Status.OK_STATUS;
			if (defaultValueRequired && !EpLocalizedPropertyController.this.validate()) {
				status = ERROR_STATUS;
			}
			
			if (controlModificationListener != null && isNameModified()) {
				controlModificationListener.controlModified();
			}
			
			return status;
		}
	}

	/**
	 * Locale combo box SelectionListener.
	 */
	private class LocaleSelectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(final SelectionEvent event) {
			// Nothing
		}

		@Override
		public void widgetSelected(final SelectionEvent event) {
			reload(localesCombo.getSelectionIndex());
		}

	}

	/**
	 * Get the localized properties encapsulated in this control.
	 * @return LocalizedProperties object.
	 */
	public LocalizedProperties getLocalizedProperties() {
		return localizedProperties;
	}

	/**
	 * Set a control modification listener for when the text changes.
	 * 
	 * @param controlModificationListener the controlModificationListener to set
	 */
	public void setControlModificationListener(final ControlModificationListener controlModificationListener) {
		this.controlModificationListener = controlModificationListener;
	}

}
