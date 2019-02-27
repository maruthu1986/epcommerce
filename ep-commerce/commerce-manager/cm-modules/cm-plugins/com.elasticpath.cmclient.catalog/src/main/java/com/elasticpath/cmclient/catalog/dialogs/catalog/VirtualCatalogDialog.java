/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.dialogs.catalog;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.list.TreeList;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * This is a main class of the Virtual Catalog dialog box, which is used for both creating and editing virtual catalogs.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.GodClass" })
public class VirtualCatalogDialog extends AbstractPolicyAwareDialog implements ObjectGuidReceiver {	

	private static final int CODE_MAXLENGTH = 64;

	/** The Logger. */
	protected static final Logger LOG = Logger.getLogger(VirtualCatalogDialog.class);

	private static final String CATALOG_NAME_FIELD_NAME = "name"; //$NON-NLS-1$

	private static final int CATALOG_NAME_TEXT_LIMIT = 255;

	private final DataBindingContext dataBindingCtx;

	private Catalog virtualCatalog;
	
	private final CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	
	private Text catalogCode;

	private Text catalogNameText;

	private Text defaultLanguageText;

	private CCombo defaultLanguageCombo;

	private String originalVirtualCatalogName;

	/**
	 * Policy container for the virtual catalog controls.
	 */
	private PolicyActionContainer editableVirtualCatalogPolicyContainer;

	private PolicyActionContainer readOnlyVirtualCatalogPolicyContainer;

	private IPolicyTargetLayoutComposite mainPolicyComposite;

	/**
	 * Virtual Catalog dialog constructor.
	 */
	public VirtualCatalogDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 1, true);
		this.dataBindingCtx = new DataBindingContext();
	}

	/**
	 * Returns true if the virtual catalog object is persistent; false otherwise.
	 *
	 * @return true if the virtual catalog object is persistent; false otherwise.
	 */
	private boolean isVirtualCatalogPersistent() {
		return this.virtualCatalog.isPersisted();
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return virtualCatalog;
	}

	@Override
	protected void populateControls() {
		if (isVirtualCatalogPersistent()) {
			catalogCode.setText(virtualCatalog.getCode());
			
			defaultLanguageText.setText(virtualCatalog.getDefaultLocale().getDisplayName());
			
			// remember the original name for validation purposes
			this.originalVirtualCatalogName = this.virtualCatalog.getName();
			
			this.catalogNameText.setText(this.virtualCatalog.getName());
		} else {
		
			this.defaultLanguageCombo.add(CatalogMessages.get().VirtualCatalogDialog_DefaultLanguage_ComboEntry);


			for (Locale locale : getAllSupportedLocales()) {
				defaultLanguageCombo.setData(locale.getDisplayName(), locale);
				defaultLanguageCombo.add(locale.getDisplayName());
			}

			this.defaultLanguageCombo.select(0);
		}
	}
	
	private List<Locale> getAllSupportedLocales() {
		CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
		List<Catalog> catalogs = catalogService.findAllCatalogs();
		
		Set<Locale> set = new HashSet<>();
		
		for (Catalog catalog : catalogs) {
			for (Locale locale : catalog.getSupportedLocales()) {
				if (locale != null) {
					set.add(locale);
				}
			}
		}
		
		List<Locale> result = new TreeList(set);

		result.sort(Comparator.comparing(Locale::getDisplayName));
		
		return result;
	}

	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;
		
		// catalog code
		EpControlBindingProvider.getInstance().bind(dataBindingCtx,
				catalogCode,
				virtualCatalog,
				"code", //$NON-NLS-1$
				new CompoundValidator(new IValidator[] { EpValidatorFactory.REQUIRED, EpValidatorFactory.MAX_LENGTH_64,
						EpValidatorFactory.NO_LEADING_TRAILING_SPACES }), null, true);

		// catalog name
		EpControlBindingProvider.getInstance().bind(dataBindingCtx, this.catalogNameText, this.virtualCatalog, CATALOG_NAME_FIELD_NAME,
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);
		
		if (!isVirtualCatalogPersistent()) {

			// default locale
			final ObservableUpdateValueStrategy defaultLocaleUpdateStrategy = new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					for (final Locale currLocale : Locale.getAvailableLocales()) {
						if (currLocale.getDisplayName().equalsIgnoreCase(defaultLanguageCombo.getText())) {
							virtualCatalog.setDefaultLocale(currLocale);
						}
					}
					return Status.OK_STATUS;
				}
			};
			EpControlBindingProvider.getInstance().bind(dataBindingCtx, this.defaultLanguageCombo,
					EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null, defaultLocaleUpdateStrategy, hideDecorationOnFirstValidation);
	
		}

		EpDialogSupport.create(this, dataBindingCtx);
	}

	@Override
	// ---- DOCcreateDialogContent
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {
		
		// This name does not need to match anything necessarily
		readOnlyVirtualCatalogPolicyContainer = addPolicyActionContainer("readOnlyVirtualCatalogDialog"); //$NON-NLS-1$
		editableVirtualCatalogPolicyContainer = addPolicyActionContainer("virtualCatalogDialog"); //$NON-NLS-1$
		
		mainPolicyComposite = dialogComposite
				.addGridLayoutComposite(2, false, dialogComposite
						.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false),
										editableVirtualCatalogPolicyContainer);

		final IEpLayoutData mainCompositeFieldData = mainPolicyComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true);
		final IEpLayoutData mainCompositeLabelData = mainPolicyComposite
				.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, true);

		if (isVirtualCatalogPersistent()) {
			mainPolicyComposite.addLabelBold(CatalogMessages.get().CatalogSummarySection_CatalogCode,
												mainCompositeLabelData, readOnlyVirtualCatalogPolicyContainer);
			
			this.catalogCode = mainPolicyComposite.addTextField(mainCompositeFieldData, readOnlyVirtualCatalogPolicyContainer);
		} else {
			mainPolicyComposite.addLabelBoldRequired(CatalogMessages.get().CatalogSummarySection_CatalogCode,
													mainCompositeLabelData, editableVirtualCatalogPolicyContainer);
			this.catalogCode = mainPolicyComposite.addTextField(mainCompositeFieldData, editableVirtualCatalogPolicyContainer);
		}
		// ---- DOCcreateDialogContent
		
		this.catalogCode.setTextLimit(CODE_MAXLENGTH);				

		if (isVirtualCatalogPersistent()) {
						
			mainPolicyComposite.addLabelBold(CatalogMessages.get().VirtualCatalogDialog_CatalogName_Label,
												mainCompositeLabelData, editableVirtualCatalogPolicyContainer);
		} else {
			mainPolicyComposite.addLabelBoldRequired(CatalogMessages.get().VirtualCatalogDialog_CatalogName_Label,
													mainCompositeLabelData, editableVirtualCatalogPolicyContainer);
		}
		this.catalogNameText = mainPolicyComposite.addTextField(mainCompositeFieldData, editableVirtualCatalogPolicyContainer);
		this.catalogNameText.setTextLimit(CATALOG_NAME_TEXT_LIMIT);

		if (isVirtualCatalogPersistent()) {
			mainPolicyComposite.addLabelBold(CatalogMessages.get().VirtualCatalogDialog_DefaultLanguage_Label,
											mainCompositeLabelData, readOnlyVirtualCatalogPolicyContainer);
			this.defaultLanguageText = mainPolicyComposite.addTextField(mainCompositeFieldData, readOnlyVirtualCatalogPolicyContainer);
		} else {
			mainPolicyComposite.addLabelBoldRequired(CatalogMessages.get().VirtualCatalogDialog_DefaultLanguage_Label,
													mainCompositeLabelData, editableVirtualCatalogPolicyContainer);
			this.defaultLanguageCombo = mainPolicyComposite.addComboBox(mainCompositeFieldData, editableVirtualCatalogPolicyContainer);
		}
	}

	private boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.MANAGE_VIRTUAL_CATALOG_LINK_CATEGORY)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(virtualCatalog);
	}

	@Override
	protected void okPressed() {
		this.setErrorMessage(null);
		
		if (!validateName(catalogNameText.getText())) {
			return;
		}
		
		// make sure code is unique
		if (!isVirtualCatalogPersistent() && catalogService.codeExists(virtualCatalog.getCode())) {
			setErrorMessage(CatalogMessages.get().CreateCatalogDialog_CatalogCodeExists_ErrorMessage);
			return;
		}

		// save changes
		virtualCatalog = catalogService.saveOrUpdate(virtualCatalog);

		if (changeSetHelper.isChangeSetsEnabled()) {
			changeSetHelper.addObjectToChangeSet(virtualCatalog, ChangeSetMemberAction.ADD);
		}
		
		// Fire an event to refresh the browse list view
		final ItemChangeEvent<Catalog> event = new ItemChangeEvent<>(this, virtualCatalog, ItemChangeEvent.EventType.ADD);
		CatalogEventService.getInstance().notifyCatalogChanged(event);

		super.okPressed();
	}

	/**
	 * Returns <code>false</code> if the given catalogName already exists; true otherwise.
	 * 
	 * @param catalogName the <code>String</code> catalog name to check
	 * @return <code>false</code> if the given catalogName already exists; true otherwise
	 */
	private boolean validateName(final String catalogName) {
		// allow 'duplicate' name if this is an edit on an existing virtual catalog
		if (this.isVirtualCatalogPersistent() && this.originalVirtualCatalogName.equals(this.virtualCatalog.getName())) {
			return true;
		}
		
		if (catalogService.nameExists(catalogName)) {
			this.setErrorMessage(CatalogMessages.get().VirtualCatalogDialog_CatalogNameExists_ErrMsg);
			return false;
		}

		return true;
	}

	@Override
	protected String getInitialMessage() {
		return StringUtils.EMPTY;
	}

	@Override
	protected String getTitle() {
		if (this.isVirtualCatalogPersistent()) {
			if (isAuthorized()) {
				return CatalogMessages.get().VirtualCatalogDialog_Edit_Title;
			}
			return CatalogMessages.get().VirtualCatalogDialog_Open_Title;
		}
		
		return CatalogMessages.get().VirtualCatalogDialog_Create_Title;
	}

	@Override
	protected String getWindowTitle() {
		if (this.isVirtualCatalogPersistent()) {
			if (isAuthorized()) {
				return CatalogMessages.get().VirtualCatalogDialog_Edit_WindowTitle;
			}
			return CatalogMessages.get().VirtualCatalogDialog_Open_WindowTitle;
		}
		
		return CatalogMessages.get().VirtualCatalogDialog_Create_WindowTitle;
	}

	@Override
	protected Image getWindowImage() {
		if (this.isVirtualCatalogPersistent()) {
			if (isAuthorized()) {
				return CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_VIRTUAL_EDIT);
			}
			return CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_VIRTUAL);
		}
		
		return CatalogImageRegistry.getImage(CatalogImageRegistry.CATALOG_VIRTUAL_CREATE);
	}
	
	/**
	 * Get identity.
	 * @return the identity for the dialog
	 */
	public String getIdentity() {		
		return "virtualCatalogDialog"; //$NON-NLS-1$
	}
	

	@Override
	// ---- DOCrefreshLayout
	protected void refreshLayout() {
		if (mainPolicyComposite != null) {
			mainPolicyComposite.getSwtComposite().layout();
			mainPolicyComposite.getSwtComposite().getParent().layout();
		}
	}
	// ---- DOCrefreshLayout

	@Override
	// ---- DOCgetOkButtonPolicyActionContainer
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return this.editableVirtualCatalogPolicyContainer;
	}
	// ---- DOCgetOkButtonPolicyActionContainer

	// ---- DOCsetObjectGuid
	@Override
	public void setObjectGuid(final String objectGuid) {
		if (objectGuid == null) {
			virtualCatalog = ServiceLocator.getService(ContextIdNames.CATALOG);
			virtualCatalog.setMaster(false);
		} else {
			virtualCatalog = catalogService.findByCode(objectGuid);
			if (virtualCatalog == null) {
				throw new IllegalArgumentException(

						NLS.bind(CoreMessages.get().Given_Object_Not_Exist,
						new String[]{"Virtual Catalog", objectGuid})); //$NON-NLS-1$
			}
		}
	}

	@Override
	// ---- DOCgetTargetIdentifier

	public String getTargetIdentifier() {
		if (this.virtualCatalog.isPersisted()) {
			return "virtualCatalogDialogTarget"; //$NON-NLS-1$
		}
		return "virualCatalogDialogEditableTarget"; //$NON-NLS-1$
	}
	// ---- DOCgetTargetIdentifier

	@Override
	protected Object getDependentObject() {
		return this.virtualCatalog;
	}

	@Override
	public DataBindingContext getDataBindingContext() {
		return dataBindingCtx;
	}

}
