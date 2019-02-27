/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.shipping;


import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;

/**
 * Messages class for the shipping levels module.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.VariableNamingConventions" })
public final class ShippingLevelsMessages {

	/** Property file binding. */
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.store.shipping.ShippingLevelsResources"; //$NON-NLS-1$

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
	 * Empty constructor.
	 */
	private ShippingLevelsMessages() {
	}

	/** Search tab messages. */
	public String ShippingLevelsSearchTabTitle;

	public String ShippingLevelsStoreLabel;

	public String ShippingLevelsShippingRegionLabel;

	public String ShippingLevelsFiltersGroupTitle;

	public String ShippingLevelsAllShippingRegionsComboboxItem;
	
	public String ShippingLevelsAllStoresComboboxItem;

	public String ShippingLevelsFilterButton;

	/** Search results view. */
	public String ShippingLevelStoreColumnLabel;

	public String ShippingLevelRegionColumnLabel;

	public String ShippingLevelCarierColumnLabel;
	
	public String ShippingLevelCode;

	public String ShippingLevelNameColumnLabel;

	public String ShippingLevelCalcMethodColumnLabel;
	
	public String ShippingLevelActiveColumnLabel;


	/** Actions. */
	public String CreateShippingLevelAction;

	public String EditShippingLevelAction;

	public String DeleteShippingLevelAction;

	/** Errors. */
	public String ConfirmDeleteShippingLevelMsgBoxTitle;

	public String ConfirmDeleteShippingLevelMsgBoxText;

	public String NoLongerExistShippingLevelMsgBoxTitle;

	public String NoLongerExistShippingLevelMsgBoxText;

	public String PropertiesAreRequiredMsg;
	
	public String WrongNumberFormatMsg;
	
	public String ShippingServiceLeve_Duplicate_Code;

	/** Dialog. */
	public String CreateShippingLevelDialogTitle;

	public String EditShippingLevelDialogTitle;

	public String ShippingLevelDialogInitialMessage;

	public String ShippingLevelDialogStoreTitle;

	public String ShippingLevelDialogCarrierTitle;
	
	public String ShippingLevelDialogCodeTitle;

	public String ShippingLevelDialogNameTitle;

	public String ShippingLevelDialogCalculationMethodTitle;

	public String ShippingLevelDialogPropertiesTitle;

	public String ShippingLevelDialogPropertiesKeyColumn;

	public String ShippingLevelDialogPropertiesValueColumn;

	public String ShippingLevelDialogShippingRegionTitle;

	public String ShippingLevelPropertiesModifiedTitle;

	public String ShippingLevelPropertiesModifiedText;

	public String ShippingLevelNameAndPropertiesModifiedTitle;

	public String ShippingLevelNameAndPropertiesModifiedText;

	public String UsedShippingServiceLevelDialogTitle;

	public String UsedShippingServiceLevelDialogText;

	public String Active;

	public String AllShippingServiceLevels;

	public String InActive;

	public String ShippingLevelState;

	public String Yes;

	public String ActiveNo;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static ShippingLevelsMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, ShippingLevelsMessages.class);
	}

	/**
	 * Helps display localized calculation method name.
	 * 
	 * @param shippingCostCalculationMethod calculation method to process
	 * @return converted displayable name
	 */
	public String localizeCalcParam(final ShippingCostCalculationMethod shippingCostCalculationMethod) {
		return CoreMessages.get().getMessage(shippingCostCalculationMethod.getDisplayText());
	}

	/**
	 * Convert object to string.
	 * 
	 * @param object to convert to string
	 * @return object converted to string or empty string if the object is null
	 */
	public String objectToString(final Object object) {
		if (object == null) {
			return EMPTY_STRING;
		}

		return object.toString();
	}

}