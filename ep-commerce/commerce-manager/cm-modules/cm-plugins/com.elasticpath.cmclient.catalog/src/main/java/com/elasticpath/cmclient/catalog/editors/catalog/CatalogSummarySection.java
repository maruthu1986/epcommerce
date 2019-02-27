/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Implements a section of the Catalog editor providing general details about a Catalog.
 */
@SuppressWarnings("PMD.AvoidStringBufferField")
public class CatalogSummarySection extends AbstractCmClientEditorPageSectionPart implements SelectionListener {

	private static final int CODE_MAXLENGTH = 64;

	private IEpLayoutComposite controlPane;

	private final ControlModificationListener controlModificationListener;

	private Text catalogCode;

	private Text catalogName;

	private LanguageSelectionDualListBox languageDualList;

	private final StoreService storeService =
		(StoreService) ServiceLocator.getService(ContextIdNames.STORE_SERVICE);

	@SuppressWarnings("PMD.AvoidStringBufferField")
	private StringBuilder bodyString;

	/**
	 * Default constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CatalogSummarySection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		controlModificationListener = editor;
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		EpState epState;
		final boolean isAuthorized = AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(getModel());
		if (isAuthorized) {
			epState = EpState.EDITABLE;
		} else {
			epState = EpState.READ_ONLY;
		}

		controlPane = CompositeFactory.createTableWrapLayoutComposite(parent, 2, false);
		final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER);
		final IEpLayoutData dualListData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, true, 2,
				1);

		controlPane.addLabelBold(CatalogMessages.get().CatalogSummarySection_CatalogCode, labelData);
		catalogCode = controlPane.addTextField(EpState.READ_ONLY, fieldData);
		catalogCode.setTextLimit(CODE_MAXLENGTH);

		controlPane.addLabelBoldRequired(CatalogMessages.get().CatalogSummarySection_CatalogName, epState, labelData);
		catalogName = controlPane.addTextField(epState, fieldData);

		controlPane.addLabelBold(CatalogMessages.get().CatalogSummarySection_DefaultLanguage, labelData);
		controlPane.addLabel(getModel().getDefaultLocale().getDisplayName(), fieldData);

		final IEpLayoutComposite dualListComposite = CompositeFactory.createTableWrapLayoutComposite(controlPane
				.getSwtComposite(), 1, true);
		dualListComposite.setLayoutData(dualListData.getSwtLayoutData());

		languageDualList = LanguageSelectionDualListBox.getInstance(dualListComposite, getModel(),
				CatalogMessages.get().CatalogSummarySection_AvailableLanguages, CatalogMessages.get().CatalogSummarySection_SelectedLanguages, false);
		languageDualList.createControls();
		languageDualList.changeState(epState);

		languageDualList.registerChangeListener(() -> {
			markDirty();
			onLanguageDualListChange();
		});

	}

	@Override
	protected void populateControls() {
		catalogCode.setText(getModel().getCode());
		catalogName.setText(getModel().getName());

		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		controlPane.setControlModificationListener(controlModificationListener);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// catalog code
		bindingProvider.bind(bindingContext, catalogCode, getModel(), "code", new CompoundValidator(new IValidator[] { //$NON-NLS-1$
				EpValidatorFactory.REQUIRED, EpValidatorFactory.MAX_LENGTH_64, EpValidatorFactory.NO_LEADING_TRAILING_SPACES }),
				null, true);

		// catalog name
		bindingProvider.bind(bindingContext, catalogName, getModel(), "name", EpValidatorFactory.STRING_255_REQUIRED, null, true); //$NON-NLS-1$
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			if (completedStoresExistWithUnrepresentedLocalesAndCurrencies()) {
				String messageBody = CatalogMessages.get().CatalogSummaryPage_LocaleOrCurrencyWarningMsg + bodyString;
				createWarningDialog(CatalogMessages.get().CatalogSummaryPage_LocaleOrCurrencyWarningTitle, messageBody);
			}

			saveSelectedLocales();
			super.commit(onSave);
		}
	}

	private void saveSelectedLocales() {
		try {
			getModel().setSupportedLocales(languageDualList.getAssigned());
		} catch (final DefaultValueRemovalForbiddenException ex) {
			throw new EpUiException("Supported locales does not contain default locale", ex); //$NON-NLS-1$
		}
	}

	@Override
	public Catalog getModel() {
		return ((CatalogModel) super.getModel()).getCatalog();
	}

	private void onLanguageDualListChange() {
		if (languageDualList.getAssigned().isEmpty()) {
			getModel().setDefaultLocale(null);
		}
		getEditor().getDataBindingContext().updateModels();
	}


	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		// Combos don't seem to pick up selection change after you remove all from the dual list
		// and then select the same index that was previously selected (before you remove all from
		// the dual list), so lets force an update
		getEditor().getDataBindingContext().updateModels();
	}

	private boolean completedStoresExistWithUnrepresentedLocalesAndCurrencies() {
		bodyString = new StringBuilder();

		final List<Store> stores = storeService.findAllCompleteStores();
		return !localeCheckForStores(stores);
	}

	/**
	 * Helper method that iterates through all stores and ensures that the currently selected
	 * locales contain all of the store default locales.
	 *
	 * @param stores the stores to be checked
	 * @return true if all store default locales are contained, false otherwise
	 */
	private boolean localeCheckForStores(final List<Store> stores) {
		for (final Store store : stores) {
			if (!languageDualList.getAssigned().contains(store.getDefaultLocale())) {
				bodyString.append(
					NLS.bind(CatalogMessages.get().CatalogSummaryPage_WarningDescription,
					new Object[]{store.getName(), store.getDefaultLocale().getDisplayName()}));
				return false;
			}
		}

		return true;
	}


	/**
	 * Create a warning dialog in the CM Client using the provided title and message.
	 *
	 * @param title the title for the warning dialog
	 * @param message the body message to be used in the warning dialog
	 */
	private void createWarningDialog(final String title, final String message) {
		MessageDialog.openWarning(Display.getCurrent().getActiveShell(), title, message);
	}

	@Override
	public void refresh() {
		// do nothing
	}
}
