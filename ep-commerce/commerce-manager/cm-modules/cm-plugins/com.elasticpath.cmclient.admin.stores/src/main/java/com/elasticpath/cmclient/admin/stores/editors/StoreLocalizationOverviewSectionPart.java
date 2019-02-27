/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;

/**
 * UI representation of the Store Localization Section.
 * 
 * TODO: this class can be refactored using addPart in StoreLocalizationPage
 */
public class StoreLocalizationOverviewSectionPart extends AbstractCmClientEditorPageSectionPart {

	private IEpLayoutComposite controlPane;

	private CCombo defaultLanguageCombo;

	private CCombo defaultCurrencyCombo;

	private StoreLanguageSelectionDualListBox languageSelectionBox;

	private StoreCurrencySelectionDualListBox currencySelectionBox;

	private IEpLayoutComposite defaultComboComposite;
	
	private final boolean authorized;

	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the form page
	 * @param authorized whether the current user is authorized to edit the store
	 */
	public StoreLocalizationOverviewSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final boolean authorized) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);
		this.authorized = authorized;
	}

	@Override
	protected String getSectionTitle() {
		return AdminStoresMessages.get().StoreOverview_Title;
	}

	@Override
	public void initialize(final IManagedForm form) {
		super.initialize(form);
		controlPane.setControlModificationListener(getEditor());
		defaultComboComposite.setControlModificationListener(getEditor());
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		final ObservableUpdateValueStrategy languageUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final Locale locale = (Locale) defaultLanguageCombo.getData(defaultLanguageCombo.getText());
				getStoreEditorModel().setDefaultLocale(locale);
				return Status.OK_STATUS;
			}
		};

		final ObservableUpdateValueStrategy currencyUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final Currency currency = (Currency) defaultCurrencyCombo.getData(defaultCurrencyCombo.getText());
				getStoreEditorModel().setDefaultCurrency(currency);
				return Status.OK_STATUS;
			}
		};

		binder.bind(bindingContext, defaultLanguageCombo, getLanguageAndCurrencyValidator(), null, languageUpdateStrategy, true);
		binder.bind(bindingContext, defaultCurrencyCombo, getLanguageAndCurrencyValidator(), null, currencyUpdateStrategy, true);
	}

	private IValidator getLanguageAndCurrencyValidator() {
		if (getStoreEditorModel().getStoreState().isIncomplete()) {
			return null;
		}
		return EpValidatorFactory.REQUIRED;
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		controlPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, true);

		final IEpLayoutData sectionLayoutData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 2, 1);
		createLanguageSection(toolkit, sectionLayoutData, getEditableState());
		createCurrencySection(toolkit, sectionLayoutData, getEditableState());
		createDefaultsSection(toolkit, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), getEditableState());

	}
	
	private void createDefaultsSection(final FormToolkit toolkit,	final IEpLayoutData sectionLayoutData, final EpState editableState) {
		final Section section = this.createSection(AdminStoresMessages.get().StoreDefaultsSelection, toolkit, sectionLayoutData);
		defaultComboComposite = CompositeFactory.createGridLayoutComposite(section, 2, false);
		
		final IEpLayoutData labelData = defaultComboComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = defaultComboComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true);
		
		defaultComboComposite.addLabelBoldRequired(AdminStoresMessages.get().DefaultLanguage, EpState.EDITABLE, labelData);
		defaultLanguageCombo = defaultComboComposite.addComboBox(editableState, fieldData);
		
		defaultComboComposite.addLabelBoldRequired(AdminStoresMessages.get().DefaultCurrency, EpState.EDITABLE, labelData);
		defaultCurrencyCombo = defaultComboComposite.addComboBox(editableState, fieldData);
		
		section.setClient(defaultComboComposite.getSwtComposite());
	}

	private void createLanguageSection(final FormToolkit toolkit, final IEpLayoutData sectionLayoutData, final EpState editableState) {
		final Section section = this.createSection(AdminStoresMessages.get().StoreLanguageSelection, toolkit, sectionLayoutData);
		final IEpLayoutComposite layoutPane = CompositeFactory.createTableWrapLayoutComposite(section, 1, false);
		languageSelectionBox = new StoreLanguageSelectionDualListBox(layoutPane, getStoreEditorModel(), editableState);
		languageSelectionBox.createControls();
		languageSelectionBox.registerChangeListener(() -> {
			populateLanguages();
			markDirty();
		});

		languageSelectionBox.registerRemoveListener(list -> {
			final StoreEditorModel store = (StoreEditorModel) getModel();
			if (list.contains(store.getDefaultLocale())) {
				MessageDialog.openWarning(getEditor().getEditorSite().getShell(), AdminStoresMessages.get().CannotRemoveLocale_Title,
						AdminStoresMessages.get().CannotRemoveLocale_Message);
				return false;
			}
			return true;
		});
		section.setClient(layoutPane.getSwtComposite());
	}

	private void createCurrencySection(final FormToolkit toolkit, final IEpLayoutData sectionLayoutData, final EpState editableState) {
		final Section section = this.createSection(AdminStoresMessages.get().StoreCurrencySelection, toolkit, sectionLayoutData);
		final IEpLayoutComposite layoutPane = CompositeFactory.createTableWrapLayoutComposite(section, 1, false);
		currencySelectionBox = new StoreCurrencySelectionDualListBox(layoutPane, getStoreEditorModel(), editableState);
		currencySelectionBox.createControls();
		currencySelectionBox.registerChangeListener(() -> {
			populateCurrencies();
			markDirty();
		});

		currencySelectionBox.registerRemoveListener(list -> {
			final StoreEditorModel store = (StoreEditorModel) getModel();
			if (list.contains(store.getDefaultCurrency())) {
				MessageDialog.openWarning(getEditor().getSite().getShell(), AdminStoresMessages.get().CannotRemoveCurrency_Title,
						AdminStoresMessages.get().CannotRemoveCurrency_Message);
				return false;
			}
			return true;
		});
		section.setClient(layoutPane.getSwtComposite());
	}

	private Section createSection(final String localizedMessage, final FormToolkit toolkit, final IEpLayoutData layoutData) {
		final Section section = toolkit.createSection(controlPane.getSwtComposite(), ExpandableComposite.TITLE_BAR);
		section.setLayoutData(layoutData.getSwtLayoutData());
		section.setText(localizedMessage);
		return section;
	}

	@Override
	protected void populateControls() {
		populateLanguages();
		populateCurrencies();
	}

	private StoreEditorModel getStoreEditorModel() {
		return (StoreEditorModel) getEditor().getModel();
	}

	private void populateCurrencies() {
		defaultCurrencyCombo.removeAll();
		for (final Currency currency : sortCurrencies(currencySelectionBox.getAssigned())) {
			defaultCurrencyCombo.setData(currency.getCurrencyCode(), currency);
			defaultCurrencyCombo.add(currency.getCurrencyCode());
		}

		if (getStoreEditorModel().isPersistent()) {
			defaultCurrencyCombo.setText(getDefaultCurrency());
		}
	}

	private String getDefaultCurrency() {
		final Currency defaultCurrency = getStoreEditorModel().getDefaultCurrency();
		if (defaultCurrency != null) {
			for (final Currency currentLocale : currencySelectionBox.getAssigned()) {
				if (currentLocale.equals(defaultCurrency)) {
					return defaultCurrency.getCurrencyCode();
				}
			}
		}
		return ""; //$NON-NLS-1$
	}

	private void populateLanguages() {
		defaultLanguageCombo.removeAll();
		for (final Locale locale : sortLocales(languageSelectionBox.getAssigned())) {
			defaultLanguageCombo.setData(locale.getDisplayName(), locale);
			defaultLanguageCombo.add(locale.getDisplayName());
		}
		
		if (getStoreEditorModel().isPersistent()) {
			defaultLanguageCombo.setText(getDefaultLanguage());
		}
	}

	private String getDefaultLanguage() {
		final Locale defaultLocale = getStoreEditorModel().getDefaultLocale();
		if (defaultLocale != null) {
			for (final Locale currentLocale : languageSelectionBox.getAssigned()) {
				if (currentLocale.equals(defaultLocale)) {
					return defaultLocale.getDisplayName();
				}
			}
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public void commit(final boolean onSave) {
		try {
			getStoreEditorModel().setSupportedLocales(languageSelectionBox.getAssigned());
			getStoreEditorModel().setSupportedCurrencies(currencySelectionBox.getAssigned());
		} catch (DefaultValueRemovalForbiddenException e) {
			throw new EpUiException(e);
		}
		super.commit(onSave);
	}

	private List<Currency> sortCurrencies(final Collection<Currency> collection) {
		final List<Currency> result = new ArrayList<>(collection);
		result.sort(Comparator.comparing(Currency::getCurrencyCode));
		return result;
	}
	
	private List<Locale> sortLocales(final Collection<Locale> collection) {
		final List<Locale> result = new ArrayList<>(collection);
		result.sort(Comparator.comparing(Locale::getDisplayName));
		return result;
	}
	
	/**
	 * @return true if the current user is authorized to edit the current store, false if not.
	 */
	boolean isCurrentUserAuthorized() {
		return authorized;
	}
	
	/**
	 * @return {@code EpState.EDITABLE} if a control should be editable, else {@code EpState.READ_ONLY}.
	 */
	EpState getEditableState() {
		if (isCurrentUserAuthorized()) {
			return EpState.EDITABLE;
		}
		return EpState.READ_ONLY;
	}
}
