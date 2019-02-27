/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.dialogs.catalog;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.catalog.LanguageSelectionDualListBox;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.service.catalog.CatalogService;

/**
 * This is a main class of the Create catalog dialog box.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class CreateCatalogDialog extends AbstractEpDialog {
	private static final int CODE_MAXLENGTH = 64;

	/** The Logger. */
	protected static final Logger LOG = Logger.getLogger(CreateCatalogDialog.class);

	private static final String CATALOG_NAME_FIELD_NAME = "name"; //$NON-NLS-1$

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final int CATALOG_NAME_TEXT_LIMIT = 255;

	private static final int GROUP_COLUMN_COUNT = 1;

	private final DataBindingContext dataBindingCtx;

	private final Catalog catalog;

	private final CatalogService catalogService;

	private Text catalogCode;

	private Text catalogNameText;

	private CCombo defaultLocaleCombo;

	private String defaultLocaleComboSelection;

	private LanguageSelectionDualListBox languageSelectionDualListBox;

	/**
	 * Create Catalog dialog constructor.
	 * 
	 * @param parentShell parent shell
	 * @param catalog catalog object
	 */
	public CreateCatalogDialog(final Shell parentShell, final Catalog catalog) {
		super(parentShell, 1, true);
		this.catalog = catalog;
		this.dataBindingCtx = new DataBindingContext();
		catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
	}

	/**
	 * Opens a Create Catalog Dialog.
	 * 
	 * @param shell main shell
	 * @param catalog catalog object
	 * @return true if data can be saved
	 */
	public static boolean openCreateDialog(final Shell shell, final Catalog catalog) {
		return new CreateCatalogDialog(shell, catalog).open() == 0;
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return catalog;
	}

	@Override
	protected void populateControls() {
		this.populateDefaultLocaleCombo();
	}

	private void populateDefaultLocaleCombo() {
		// default locale combo box
		this.defaultLocaleCombo.removeAll();
		for (final Locale currLocale : this.languageSelectionDualListBox.getAssigned()) {
			this.defaultLocaleCombo.add(currLocale.getDisplayName());
		}
		// re-populating the combo box on-the-fly un-selects any past selections; need to re-select the combo box if the past selection is still in
		// the list of languages
		if (this.defaultLocaleComboSelection != null) {
			this.defaultLocaleCombo.setText(this.defaultLocaleComboSelection);
		}
	}


	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;

		// catalog code
		EpControlBindingProvider.getInstance().bind(dataBindingCtx,
				catalogCode,
				catalog,
				"code", //$NON-NLS-1$
				EpValidatorFactory.CATALOG_CODE, null, true);

		// catalog name
		EpControlBindingProvider.getInstance().bind(dataBindingCtx, this.catalogNameText, this.catalog, CATALOG_NAME_FIELD_NAME,
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		// default locale
		final ObservableUpdateValueStrategy defaultLocaleUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				for (final Locale currLocale : languageSelectionDualListBox.getAvailable()) {
					if (currLocale.getDisplayName().equalsIgnoreCase(defaultLocaleCombo.getText())) {
						catalog.setDefaultLocale(currLocale);
					}
				}
				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance().bind(dataBindingCtx, this.defaultLocaleCombo, EpValidatorFactory.REQUIRED, null,
				defaultLocaleUpdateStrategy, hideDecorationOnFirstValidation);

		EpDialogSupport.create(this, dataBindingCtx);
	}

	@Override
	@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		// catalog name text
		final IEpLayoutComposite nameComposite = dialogComposite.addGridLayoutComposite(2, false, dialogComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		final IEpLayoutData nameCompositeLabelData = nameComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, true);
		final IEpLayoutData nameCompositeFieldData = nameComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true);

		nameComposite.addLabelBoldRequired(CatalogMessages.get().CatalogSummarySection_CatalogCode, EpState.EDITABLE, nameCompositeLabelData);
		catalogCode = nameComposite.addTextField(EpState.EDITABLE, nameCompositeFieldData);
		catalogCode.setTextLimit(CODE_MAXLENGTH);

		nameComposite.addLabelBoldRequired(CatalogMessages.get().CreateCatalogDialog_CatalogName_Label, EpState.EDITABLE, nameCompositeLabelData);
		this.catalogNameText = nameComposite.addTextField(EpState.EDITABLE, nameCompositeFieldData);
		this.catalogNameText.setTextLimit(CATALOG_NAME_TEXT_LIMIT);

		final IEpLayoutComposite dualListBoxComposite = dialogComposite.addGridLayoutComposite(GROUP_COLUMN_COUNT, false, dialogComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		this.languageSelectionDualListBox = LanguageSelectionDualListBox.getInstance(dualListBoxComposite, this.catalog,
				CatalogMessages.get().CreateCatalogDialog_AvailableLanguage_Label,
				CatalogMessages.get().CreateCatalogDialog_SelectedLanguage_Label, true);
		this.languageSelectionDualListBox.createControls();
		
		// the default language combo box needs to dynamically reflect the selected languages
		this.languageSelectionDualListBox.registerChangeListener(() -> {
			// check if default locale combo box selection is still in assigned list
			if ((defaultLocaleComboSelection != null) && (!isInAssignedLocaleList(defaultLocaleComboSelection))) {
				defaultLocaleComboSelection = null;
			}
			populateDefaultLocaleCombo();

		});

		this.languageSelectionDualListBox.registerRemoveListener(list -> {
			if (list.contains(CreateCatalogDialog.this.catalog.getDefaultLocale())) {
				MessageDialog.openWarning(getShell(), CatalogMessages.get().CreateCatalogDialog_CannotRemoveLocale_Title,
						CatalogMessages.get().CreateCatalogDialog_CannotRemoveLocale_Message);
				return false;
			}
			return true;
		});


		// default language combo box
		final IEpLayoutComposite defaultLanguageComposite = dialogComposite.addGridLayoutComposite(2, false, dialogComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		defaultLanguageComposite.addLabelBoldRequired(CatalogMessages.get().CreateCatalogDialog_DefaultLanguage_Label, EpState.EDITABLE,
				defaultLanguageComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, true));
		this.defaultLocaleCombo = defaultLanguageComposite.addComboBox(EpState.EDITABLE, defaultLanguageComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true));
		this.defaultLocaleComboSelection = null;
		this.defaultLocaleCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// empty
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				defaultLocaleComboSelection = defaultLocaleCombo.getText();
			}
		});

	}

	/**
	 * Returns true if the Locale with the given <code>String</code> locale display text is in the list of selected languages; false otherwise.
	 * 
	 * @param localeDisplayText the <code>String</code> locale display text to check
	 * @return true if the Locale with the given <code>String</code> locale display text is in the list of selected languages; false otherwise
	 */
	private boolean isInAssignedLocaleList(final String localeDisplayText) {
		for (final Locale currLocale : this.languageSelectionDualListBox.getAssigned()) {
			if (currLocale.getDisplayName().equalsIgnoreCase(localeDisplayText)) {
				return true;
			}
		}

		return false;
	}


	@Override
	protected void okPressed() {
		this.setErrorMessage(null);

		if (this.defaultLocaleComboSelection == null) {
			this.setErrorMessage(CatalogMessages.get().CreateCatalogDialog_EmptyDefaultLanguage_ErrorMessage);
			return;
		}

		if (!this.validateName(this.catalogNameText.getText()) || !this.validateSelectedLocales()) {
			return;
		}
		
		this.saveLocales();

		// make sure code is unique
		if (catalogService.codeExists(catalog.getCode())) {
			setErrorMessage(CatalogMessages.get().CreateCatalogDialog_CatalogCodeExists_ErrorMessage);
			return;
		}

		super.okPressed();
	}

	/**
	 * Returns <code>false</code> if the given catalogName already exists; true otherwise.
	 * 
	 * @param catalogName the <code>String</code> catalog name to check
	 * @return <code>false</code> if the given catalogName already exists; true otherwise
	 */
	private boolean validateName(final String catalogName) {
		if (catalogService.nameExists(catalogName)) {
			this.setErrorMessage(CatalogMessages.get().CreateCatalogDialog_CatalogNameExists_ErrorMessage);
			return false;
		}

		return true;
	}

	/**
	 * Returns <code>false</code> if there are no languages selected; true otherwise.
	 * 
	 * @return <code>false</code> if there are no languages selected; true otherwise
	 */
	private boolean validateSelectedLocales() {
		if (this.languageSelectionDualListBox.validate()) {
			return true;
		}

		setErrorMessage(CatalogMessages.get().CreateCatalogDialog_EmptyLanguageSelection_ErrorMessage);
		return false;
	}


	@Override
	protected String getInitialMessage() {
		return EMPTY_STRING;
	}

	@Override
	protected String getTitle() {
		return CatalogMessages.get().CreateCatalogDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		return CatalogMessages.get().CreateCatalogDialog_WindowTitle;
	}

	@Override
	protected Image getWindowImage() {
		return CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_MASTER_CREATE);
	}

	/**
	 * @throws EpUiException if the locales don't contain the default locale
	 */
	private void saveLocales() {
		try {
			this.catalog.setSupportedLocales(this.languageSelectionDualListBox.getAssigned());
		} catch (DefaultValueRemovalForbiddenException ex) {
			throw new EpUiException("Supported Locales does not contain default locale", ex); //$NON-NLS-1$
		}
	}
}
