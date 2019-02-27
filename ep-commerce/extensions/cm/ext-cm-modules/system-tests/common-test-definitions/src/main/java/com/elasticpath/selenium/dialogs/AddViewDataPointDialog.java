package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

/**
 * Add View Data Point Dialog.
 */
public class AddViewDataPointDialog extends AbstractDialog {

	/**
	 * CSS selector used to identify the dialog.
	 */
	public static final String ADD_VIEW_DATA_POINT_DIALOG_CSS_TEMPLATE = "div[automation-id='com.elasticpath.cmclient.admin.datapolicies"
			+ ".AdminDataPoliciesMessages.DataPolicyEditor_DataPoints_Dialog_%sTitle'][widget-type='Shell'] ";
	private static final String DATA_POINT_NAME_INPUT_CSS = "div[widget-id='Name'] > input";
	private static final String DATA_POINT_LOCATION_COMBO_CSS = "div[widget-id='Data Location'][widget-type='CCombo']";
	private static final String DATA_POINT_KEY_COMBO_CSS = "div[widget-id='Data Key'] input";
	private static final String DATA_POINT_DESCRIPTION_INPUT_CSS = "div[widget-id='Description Key'][widget-type='Text'] input";
	private static final String SAVE_BUTTON_CSS = "div[automation-id='com.elasticpath.cmclient.core.CoreMessages"
			+ ".AbstractEpDialog_ButtonSave'][seeable='true']";
	private final String addViewDataPointDialogCSS;
	private final String dataPointNameInputCSS;

	/**
	 * Constructor.
	 *
	 * @param driver     WebDriver which drives this page.
	 * @param dialogName String for wild card dialog name
	 */
	public AddViewDataPointDialog(final WebDriver driver, final String dialogName) {
		super(driver);
		addViewDataPointDialogCSS = String.format(ADD_VIEW_DATA_POINT_DIALOG_CSS_TEMPLATE, dialogName);
		dataPointNameInputCSS = addViewDataPointDialogCSS + DATA_POINT_NAME_INPUT_CSS;
	}

	/**
	 * Enters Data Point Name.
	 *
	 * @param dataPointName the Data Point Name
	 */
	public void enterDataPointName(final String dataPointName) {
		clearAndType(dataPointNameInputCSS, dataPointName);
	}

	/**
	 * Select Data Location.
	 *
	 * @param dataLocation for Data Point.
	 */
	public void selectDataLocation(final String dataLocation) {
		assertThat(selectComboBoxItem(DATA_POINT_LOCATION_COMBO_CSS, dataLocation))
				.as("Unable to find Data Location - " + dataLocation)
				.isTrue();
	}

	/**
	 * Select Data Key.
	 *
	 * @param dataKey for Data Point.
	 */
	public void selectDataKey(final String dataKey) {
		clearAndType(DATA_POINT_KEY_COMBO_CSS, dataKey);
	}

	/**
	 * Enters Data Point Description.
	 *
	 * @param dataPointDescription the Data Point Description
	 */
	public void enterDataPointDescription(final String dataPointDescription) {
		clearAndType(DATA_POINT_DESCRIPTION_INPUT_CSS, dataPointDescription);
	}

	/**
	 * Clicks save.
	 */
	@Override
	public void clickSave() {
		clickButton(SAVE_BUTTON_CSS, "Save");
	}

}