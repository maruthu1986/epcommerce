package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.util.DBConnector;

/**
 * Create Price List Assignment Dialog.
 */
public class CreatePriceListAssignmentWizard extends AbstractWizard {

	private static final String CREATE_PLA_PARENT_CSS = "div[widget-id='Create Price List Assignment'] ";
	private static final String NAME_INPUT = CREATE_PLA_PARENT_CSS + "div[widget-id='Name'] > input";
	private static final String DESCRIPTION_INPUT = "div[automation-id='com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages"
			+ ".Description_Label'] > textarea";
	private static final String PRICE_LIST_PARENT = "div[widget-id='Price List Assignment'][widget-type='Table'] ";
	private static final String PRICE_LIST_LIST = PRICE_LIST_PARENT + "div[parent-widget-id='Price List Assignment'] div[column-id='%s']";
	private static final String CATALOG_PARENT = "div[widget-id='Price List Catalog'][widget-type='Table'] ";
	private static final String CATALOG_LIST = CATALOG_PARENT + "div[parent-widget-id='Price List Catalog'] div[column-id='%s']";
	private static final String STORES_TABLE_PARENT_CSS = "div[automation-id='com.elasticpath.cmclient.conditionbuilder.plugin"
			+ ".ConditionBuilderMessages.SelectedStores_Label'] ";
	private static final String STORES_TABLE_ROW_CSS = STORES_TABLE_PARENT_CSS + "div[widget-type='table_row']";
	private static final String PLA_RADIO_BUTTON_CSS = "div[widget-id='%s'][appearance-id='radio-button']";

	private static final List<String> steps = Arrays.asList("Priority", "Price List", "Catalog", "Shoppers", "Time", "Stores");

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreatePriceListAssignmentWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs price list assignment name.
	 *
	 * @param name String
	 */
	public void enterPriceListAssignmentName(final String name) {
		clearAndType(NAME_INPUT, name);
	}

	/**
	 * Enter Price List Assigment Description.
	 *
	 * @param descriptionText the description
	 */
	public void enterPriceListAssignmentDescription(final String descriptionText) {
		clearAndType(DESCRIPTION_INPUT, descriptionText);
	}

	/**
	 * Get the pricelist assignment description.
	 *
	 * @return the pricelist assignment description.
	 */
	public String getPriceListAssignmentDescription() {
		return getDriver().findElement(By.cssSelector(DESCRIPTION_INPUT)).getAttribute("value");
	}

	/**
	 * Selects a price list from given name.
	 *
	 * @param priceListName String
	 */
	public void selectPriceList(final String priceListName) {
		assertThat(selectItemInDialog(PRICE_LIST_PARENT, PRICE_LIST_LIST, priceListName, "Price List"))
				.as("Given Price List not found - " + priceListName)
				.isTrue();
	}

	/**
	 * Selects a catalog name from given name.
	 *
	 * @param catalogName String
	 */
	public void selectCatalogName(final String catalogName) {
		assertThat(selectItemInDialog(CATALOG_PARENT, CATALOG_LIST, catalogName, "Catalog"))
				.as("Given Catalog not found - " + catalogName)
				.isTrue();
	}

	/**
	 * Skips to to a specific step in Price List Assignment creation wizard.
	 *
	 * @param step the step in the wizard to skip to.
	 */
	public void skipToStep(final String step) {
		int stepNumber = steps.indexOf(step);
		for (int i = 0; i < stepNumber; i++) {
			clickNextInDialog();
		}
	}

	/**
	 * Selects Radio button by display name.
	 *
	 * @param radioButtonName String
	 */
	private void selectRadioButton(final String radioButtonName) {
		clickButton(String.format(PLA_RADIO_BUTTON_CSS, radioButtonName), radioButtonName);
	}

	/**
	 * Verify that the list of actual stores visible in UI matches those expected.
	 *
	 * @param storeList list of stores
	 */
	public void verifyAvailableStores(final List<String> storeList) {
		selectRadioButton("Assign Specific Stores");

		List<WebElement> actualStores = getDriver().findElements(By.cssSelector(STORES_TABLE_ROW_CSS));

		assertThat(actualStores.size())
				.isEqualTo(storeList.size())
				.as("number of expected stores does not match actual");

		for (int i = 0; i < actualStores.size(); i++) {
			assertThat(actualStores.get(i).getText())
					.isEqualTo(storeList.get(i))
					.as("unexpected store found");
		}
	}

	/**
	 * Verify that the list of stores presented in UI matches those stored in the DB.
	 */
	public void verifyAllAvailableStores() {
		DBConnector dbConnector = new DBConnector();
		List<String> allStores = dbConnector.getAllStores();
		dbConnector.closeAll();

		verifyAvailableStores(allStores);
	}

}