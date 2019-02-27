/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */

package com.elasticpath.cmclient.admin.shipping.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.shipping.AdminShippingImageRegistry;
import com.elasticpath.cmclient.admin.shipping.AdminShippingMessages;
import com.elasticpath.cmclient.admin.shipping.AdminShippingPlugin;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.Country;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.service.shipping.ShippingRegionService;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * This is a main class of the Create/Edit Shipping Region dialog box.
 */
public class ShippingRegionDialog extends AbstractEpDialog {

	private static final String NAME_FIELD_NAME = "name"; //$NON-NLS-1$

	private static final String COUNTRIES_GROUP_LABEL = ""; //$NON-NLS-1$

	private static final int SHIPPING_REGION_NAME_TEXT_LIMIT = 255;

	private static final int GROUP_COLUMN_COUNT = 3;

	private final DataBindingContext dataBindingCtx;

	private final ShippingRegion shippingRegion;

	private final String dialogInitialMessage;

	private final String dialogTitle;

	private final String windowTitle;

	private final Image dialogImage;

	private Text shippingRegionNameText;

	private CountrySelectionDualListBox countrySelectionDualListBox;

	private String previousShippingRegionName;

	/**
	 * Shipping Region dialog constructor.
	 * 
	 * @param parentShell parent shell
	 * @param shippingRegion newly created domain object or an object for edition
	 * @param title dialog title
	 * @param image dialog image
	 */
	public ShippingRegionDialog(final Shell parentShell, final ShippingRegion shippingRegion, final String title, final Image image) {
		super(parentShell, 1, true);

		dataBindingCtx = new DataBindingContext();

		this.shippingRegion = shippingRegion;

		dialogInitialMessage = AdminShippingMessages.get().InitialMessage;
		dialogTitle = title;
		windowTitle = dialogTitle;
		dialogImage = image;
	}

	/**
	 * Opens an Edit Shipping Region Dialog.
	 * 
	 * @param shell main shell
	 * @param shippingRegion ShippingRegion entity
	 * @return true if data can be saved
	 */
	public static boolean openEditDialog(final Shell shell, final ShippingRegion shippingRegion) {
		return new ShippingRegionDialog(shell, shippingRegion, AdminShippingMessages.get().EditShippingRegionTitle, AdminShippingImageRegistry
				.getImage(AdminShippingImageRegistry.IMAGE_SHIPPING_EDIT)).open() == 0;
	}

	/**
	 * Opens a Create Shipping Region Dialog.
	 * 
	 * @param shell main shell
	 * @param shippingRegion ShippingRegion entity
	 * @return true if data can be saved
	 */
	public static boolean openCreateDialog(final Shell shell, final ShippingRegion shippingRegion) {
		return new ShippingRegionDialog(shell, shippingRegion, AdminShippingMessages.get().CreateShippingRegionTitle, AdminShippingImageRegistry
				.getImage(AdminShippingImageRegistry.IMAGE_SHIPPING_CREATE)).open() == 0;
	}

	/**
	 * Getter method for the ShippingRegion entity.
	 * 
	 * @return ShippingRegion.
	 */
	protected ShippingRegion getShippingRegion() {
		return shippingRegion;
	}

	private boolean isShippingRegionPersistent() {
		return shippingRegion.isPersisted();
	}

	@Override
	protected String getPluginId() {
		return AdminShippingPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return shippingRegion;
	}

	@Override
	protected void populateControls() {
		if (isShippingRegionPersistent()) {
			previousShippingRegionName = getShippingRegion().getName();

			shippingRegionNameText.setText(previousShippingRegionName);
		}
	}

	@Override
	protected void bindControls() {
		EpControlBindingProvider.getInstance().bind(dataBindingCtx, shippingRegionNameText, getShippingRegion(), NAME_FIELD_NAME,
				EpValidatorFactory.STRING_255_REQUIRED, null, true);

		// FIXME Don't know how to enable list box validation via standard validation

		EpDialogSupport.create(this, dataBindingCtx);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutComposite nameComposite = dialogComposite.addGridLayoutComposite(2, false, dialogComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));
		nameComposite.addLabelBoldRequired(AdminShippingMessages.get().RegionNameLabel, EpState.EDITABLE, nameComposite.createLayoutData(
				IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, true));

		EpState state = EpState.EDITABLE;
		if (((ShippingServiceLevelService) ServiceLocator.getService(EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE))
				.getShippingRegionInUseUidList().contains(shippingRegion.getUidPk())) {
			state = EpState.READ_ONLY;
		}
		shippingRegionNameText = nameComposite.addTextField(state, nameComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true,
				true));
		shippingRegionNameText.setTextLimit(SHIPPING_REGION_NAME_TEXT_LIMIT);

		final IEpLayoutComposite group = dialogComposite.addGroup(COUNTRIES_GROUP_LABEL, 1, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		final IEpLayoutComposite container = group.addGridLayoutComposite(GROUP_COLUMN_COUNT, false, group.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.FILL, true, true));

		List<Country> shippingRegionCountries = findShippingRegionCountries();
		List<Country> allCountries = findAllCountries();

		countrySelectionDualListBox = new CountrySelectionDualListBox(container, shippingRegionCountries, allCountries,
				AdminShippingMessages.get().AvailableCountriesLabel, AdminShippingMessages.get().SelectedCountriesLabel);
		countrySelectionDualListBox.createControls();

		shippingRegionNameText.addModifyListener((ModifyListener) event -> setErrorMessage(null));
	}

	@Override
	protected void okPressed() {
		setErrorMessage(null);
		if (validateName() && validateSelectedCountries()) {
			saveRegions();
			super.okPressed();
		}
	}

	private boolean validateName() {
		if (isShippingRegionPersistent() && previousShippingRegionName.equals(getShippingRegion().getName())) {
			return true;
		}

		if (!((ShippingRegionService) ServiceLocator.getService(EpShippingContextIdNames.SHIPPING_REGION_SERVICE))
				.nameExists(getShippingRegion())) {
			return true;
		}

		setErrorMessage(AdminShippingMessages.get().NameExistsErrorMessage);

		return false;
	}

	private boolean validateSelectedCountries() {
		if (countrySelectionDualListBox.validate()) {
			return true;
		}

		setErrorMessage(AdminShippingMessages.get().EmptySelectedCountryListBoxErrorMessage);

		return false;
	}

	@Override
	protected String getInitialMessage() {
		return dialogInitialMessage;
	}

	@Override
	protected String getTitle() {
		return dialogTitle;
	}

	@Override
	protected String getWindowTitle() {
		return windowTitle;
	}

	@Override
	protected Image getWindowImage() {
		return dialogImage;
	}

	private void saveRegions() {
		HashMap<String, Region> regionMap = new HashMap<>();

		for (Country country : countrySelectionDualListBox.getAssigned()) {
			Region region = regionMap.computeIfAbsent(country.getCountryCode(), key -> ServiceLocator.getService(ContextIdNames.REGION));

			region.setCountryCode(country.getCountryCode());
			if (country.getSubCountryCode() != null) {
				region.getSubCountryCodeList().add(country.getSubCountryCode());
			}
		}

		shippingRegion.setRegionMap(regionMap);
	}

	private List<Country> findShippingRegionCountries() {
		final ArrayList<Region> regionList = new ArrayList<>();
		if (isShippingRegionPersistent()) {
			regionList.addAll(shippingRegion.getRegionMap().values());
		}

		// countries for regionList
		List<Country> countries = new ArrayList<>();
		for (Region region : regionList) {
			final String countryCode = region.getCountryCode();
			Country country = new Country(countryCode, getGeography().getCountryDisplayName(countryCode, CorePlugin.getDefault().getDefaultLocale()));
			countries.add(country);
			for (String subCountryCode : region.getSubCountryCodeList()) {
				Country subCountry = new Country(country, subCountryCode, getGeography().getSubCountryDisplayName(countryCode,
						subCountryCode, CorePlugin.getDefault().getDefaultLocale()));
				countries.add(subCountry);
			}
		}
		Collections.sort(countries);
		return countries;
	}

	private List<Country> findAllCountries() {
		List<Country> allCountries = new ArrayList<>();
		for (String countryCode : getGeography().getCountryCodes()) {
			Country country = new Country(countryCode, getGeography().getCountryDisplayName(countryCode, CorePlugin.getDefault().getDefaultLocale()));
			allCountries.add(country);
			for (String subCountryCode : getGeography().getSubCountryCodes(countryCode)) {
				Country subCountry = new Country(country, subCountryCode, getGeography().getSubCountryDisplayName(countryCode,
						subCountryCode, CorePlugin.getDefault().getDefaultLocale()));
				allCountries.add(subCountry);
			}
		}
		Collections.sort(allCountries);
		return allCountries;
	}

	protected Geography getGeography() {
		return (Geography) ServiceLocator.getService(ContextIdNames.GEOGRAPHY);
	}

}
