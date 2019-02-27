package com.elasticpath.selenium.toolbars;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.editor.PriceListEditor;
import com.elasticpath.selenium.wizards.CreatePriceListAssignmentWizard;

/**
 * Price List Toolbar.
 */
public class PriceListActionToolbar extends AbstractToolbar {

	private static final String CREATE_PRICE_LIST_ASSIGNMENT_BUTTON_CSS =
			"[widget-id='Create Price List Assignment']";
	private static final String CREATE_PRICE_LIST_BUTTON_CSS =
			"[widget-id='Create Price List']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PriceListActionToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Create Price List Assignment icon.
	 *
	 * @return CreatePriceListAssignmentWizard
	 */
	public CreatePriceListAssignmentWizard clickCreatePriceListAssignment() {
		clickButton(CREATE_PRICE_LIST_ASSIGNMENT_BUTTON_CSS, "Create Price List Assignment");
		return new CreatePriceListAssignmentWizard(getDriver());
	}

	/**
	 * Clicks Create Price List.
	 *
	 * @return PriceListEditor
	 */
	public PriceListEditor clickCreatePriceList() {
		clickButton(CREATE_PRICE_LIST_BUTTON_CSS, "Create Price List");
		return new PriceListEditor(getDriver());
	}

	/**
	 * Verifies Create Price List button is present.
	 */
	public void verifyCreatePriceListButtonIsPresent() {
		assertThat(isElementPresent(By.cssSelector(CREATE_PRICE_LIST_BUTTON_CSS)))
				.as("Unable to find Create Price List button")
				.isTrue();
	}

}
